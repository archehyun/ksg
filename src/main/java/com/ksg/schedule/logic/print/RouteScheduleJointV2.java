package com.ksg.schedule.logic.print;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.dtp.api.schedule.joint.print.route.AbstractRouteSchedulePrint;
import com.ksg.commands.schedule.ScheduleSortData;
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.StringCompare;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.Code;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.service.BaseService;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

/**

  * @FileName : RouteScheduleJointV2.java

  * @Date : 2021. 4. 29. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :

  */
public class RouteScheduleJointV2 extends AbstractRouteSchedulePrint {
	
	protected ScheduleSubService scheduleService	= new ScheduleServiceImpl();
	
	final int FORWARD =0;
	
	final int BACK =1;
	
	BaseService baseService=baseService 	= DAOManager.getInstance().createBaseService();

	private XTGManager xtgmanager = new XTGManager();
	
	private static final String WORLD_SOURCE_XML = "world_source.xml";
	
	private ScheduleBuildMessageDialog di;

	private int UP_SIZE,DOWN_SIZE;

	public RouteScheduleJointV2(ShippersTable op) throws Exception {
		super(op);
				
		map = new HashMap<String, MiniSchedule>();
		logger.info("op date:"+op.getDate_isusse());

	}

	public void init(){
		logger.debug("start");
		try {
			Code code_info = new Code();
			code_info.setCode_name("WORLD_F");

			Code b=baseService.getCodeInfo(code_info);


			//		WORLD_F=b.getCode_field();
			//		WORLD_F="◆11◆";
			WORLD_F="<ct:><cf:><cs:><cf:Helvetica Neue LT Std><ct:77 Bold Condensed><cs:7.000000>";
			WORLD_F="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon가변 윤고딕100\\_TT>▲<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
			code_info.setCode_name("WORLD_B");

			Code f=baseService.getCodeInfo(code_info);

			//		WORLD_B=f.getCode_field();
			//		WORLD_B="◆22◆";
			WORLD_B="<ct:><cf:><cf:Helvetica LT Std><ct:Roman><cs:><cs:6.000000>";
			WORLD_B="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon가변 윤고딕100\\_TT>▲<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
			code_info.setCode_name("WORLD_VERSION1");
			Code f0=baseService.getCodeInfo(code_info);
			//		WORLD_VERSION1=f0.getCode_field();
			//		WORLD_VERSION1="◆33◆";
			WORLD_VERSION1="<KSC5601-WIN>\r\n<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1><60.100.0.0.:COLOR:CMYK:Process:0.6,1,0,0><30.60.0.0.:COLOR:CMYK:Process:0.3,0.6,0,0>>";
			WORLD_INPORT="<cc:><ct:><cs:><cf:><cc:30.60.0.0.><ct:Roman><cs:6.000000><cbs:-1.000000><cf:Helvetica LT Std>";
			WORLD_OUTPORT="<cc:><ct:><cs:><cbs:><cf:><ct:Roman><cs:6.000000><cf:Helvetica LT Std>";
			code_info.setCode_name("WORLD_VERSION2");
			Code f2=baseService.getCodeInfo(code_info);
			//		WORLD_VERSION2=f2.getCode_field();
			//		WORLD_VERSION2="◆44◆";
			WORLD_VERSION2="<dps:정규=<Nextstyle:정규><cc:검정><clig:0><cbs:-0.000000><phll:0><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><cf:JCsm><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><pta:JustifyLeft><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";
			WORLD_VERSION2="<pstyle:><ct:Bold><chs:0.900000><cl:8.000000><cf:Helvetica LT Std>";

			code_info.setCode_name("WORLD_VERSION3");
			Code f3=baseService.getCodeInfo(code_info);
			//		WORLD_VERSION3=f3.getCode_field();
			//		WORLD_VERSION3="◆55◆";
			WORLD_VERSION3="<pstyle:정규><pli:182.500000><pfli:-182.000000><psa:0.566894><ptr:96.37789916992188\\,Left\\,.\\,0\\,\\;201\\,Left\\,.\\,0\\,\\;><chs:0.800003><cl:20.000000><cs:18.000000><cf:Helvetica LT Std>\r\n<cl:><cl:20.099990>\r\n<cs:><ct:Bold><cs:18.000000>";

			code_info.setCode_name("WORLD_E");
			Code f4=baseService.getCodeInfo(code_info);
			//		WORLD_E=f4.getCode_field();
			//		WORLD_E="◆66◆";
			WORLD_E=" \r\n<cl:><cl:20.099990><cs:><ct:><cf:><cf:Helvetica LT Std><ct:Bold><cs:18.000000>";
			WORLD_E="<ct:><cs:><cf:><ct:Bold><cf:Helvetica LT Std>";

			code_info.setCode_name("WORLD_U_SIZE");
			Code USIZE=baseService.getCodeInfo(code_info);
			UP_SIZE=Integer.parseInt(USIZE.getCode_field());

			code_info.setCode_name("WORLD_L_SIZE");
			Code LSIZE=baseService.getCodeInfo(code_info);
			DOWN_SIZE=Integer.parseInt(LSIZE.getCode_field());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.debug("end");

	}
	
	private List<ScheduleData> getOutboundScheduleList(AreaInfo portInfo,
			ScheduleData data) throws SQLException {
		data.setInOutType("O");
		data.setVoyage_num(null);
		data.setArea_name( portInfo.getArea_name());
		List<ScheduleData> li=scheduleService.getScheduleList(data);
		return li;
	}

	private List getScheduleListByAraa(AreaInfo portInfo) throws SQLException {
		ScheduleData param = new ScheduleData();
		param.setArea_name(portInfo.getArea_name());
		param.setInOutType("O");
		List scheduleList=scheduleService.getScheduleListByArea(param);
		return scheduleList;
	}

	private MiniSchedule[] arrangeOutPort(Vector<MiniSchedule> outList, MiniSchedule lastInSchedule) {
		map = new HashMap<String, MiniSchedule>();
		for(int i=0;i<outList.size();i++)
		{
			MiniSchedule temp = (MiniSchedule) outList.get(i);
			try {

				// 인바운드 마지막 날자 보다 크면 추가
				if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(temp.date), KSGDateUtil.toDate4(lastInSchedule.date))<0)
				{
					addOutSchedule((MiniSchedule) outList.get(i));
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Set<String>keylist=map.keySet();
		MiniSchedule lit[] = new MiniSchedule[keylist.size()];
		Iterator<String> iter=keylist.iterator();

		for(int i=0;iter.hasNext();i++)	
		{
			String key =iter.next();
			lit[i]=map.get(key);
		}

		Arrays.sort(lit);
		return lit;
	}
	/**
	 * @param companyList
	 * @return
	 */
	private MiniSchedule[] arrangeMiniScheduleByCompany(Vector companyList) {
		map = new HashMap<String, MiniSchedule>();

		for(int i=0;i<companyList.size();i++)
		{
			MiniSchedule data= (MiniSchedule) companyList.get(i);
			map.put(data.company_abbr, data);
		}

		Set<String>keylist=map.keySet();
		MiniSchedule lit[] = new MiniSchedule[keylist.size()];
		Iterator<String> iter=keylist.iterator();

		for(int i=0;iter.hasNext();i++)	
		{
			String key =iter.next();
			lit[i]=map.get(key);
		}
		return lit;
	}

	HashMap<String, MiniSchedule> map;

	/**
	 * @param data
	 */
	private void addInSchedule(MiniSchedule data)
	{
		if(map.containsKey(data.port))
		{
			MiniSchedule one=map.get(data.port);
			try {
				if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(data.date), KSGDateUtil.toDate4(one.date))<=0)
				{
					map.put(data.port, data);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{	
			map.put(data.port, data);
		}
	}
	/**
	 * @param data
	 */
	private void addOutSchedule(MiniSchedule data)
	{	

		if(map.containsKey(data.port))
		{
			MiniSchedule one=map.get(data.port);
			try {
				if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(data.date), KSGDateUtil.toDate4(one.date))>=0)
				{
					map.put(data.port, data);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{	
			map.put(data.port, data);
		}
	}
	/**
	 * @param inList
	 * @return
	 */
	private MiniSchedule[] arrangeInPort(Vector inList) {
		map = new HashMap<String, MiniSchedule>();
		for(int i=0;i<inList.size();i++)
		{
			addInSchedule((MiniSchedule) inList.get(i));
		}

		Set<String>keylist=map.keySet();
		MiniSchedule lit[] = new MiniSchedule[keylist.size()];
		Iterator<String> iter=keylist.iterator();

		for(int i=0;iter.hasNext();i++)	
		{
			String key =iter.next();
			lit[i]=map.get(key);
		}

		Arrays.sort(lit);
		return lit;
	}
	private void createXMLFile(Document document) throws JDOMException,
	IOException, ParseException {
		try {
			Format format = Format.getPrettyFormat();

			format.setEncoding("EUC-KR");
			//			format.setIndent("\n");
			format.setIndent("\r\n");
			format.setIndent("\t");


			XMLOutputter outputter = new XMLOutputter(format);

			// 양이 많을 경우에 오류 발생
			//			String strXml=outputter.outputString(document);
			//			logger.debug("strXml  ; "+strXml);


			FileWriter writer = new FileWriter(WORLD_SOURCE_XML);
			outputter.output(document, writer);
			writer.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(WORLD_SOURCE_XML);

		for (Iterator it = doc.getRootElement().getContent().iterator(); it.hasNext();) {
			Content content = (Content) it.next();
			if(content instanceof Element)
			{
				Element schedule = (Element)content;
			}
		}
		printXTG(document);
	}

	private void bubbleSortByDate(Element[] array) {

		int out, in;
		for (out = array.length - 1; out > 0; out--) {
			for (in = 0; in < out; in++) {
				Element inPort1=array[in].getChild("inport");
				Element inPort2=array[in+1].getChild("inport");

				List inport_list1 = inPort1.getChildren("port");
				List inport_list2 = inPort2.getChildren("port");

				Element port1 = (Element) inport_list1.get(0);				
				String date1=port1.getAttributeValue("port-date");

				Element port2 = (Element) inport_list2.get(0);

				String date2=port2.getAttributeValue("port-date");
				if(isBigger(date2, date1))
				{
					swapE(array, in, in + 1);
				}
			}
		}
	}
	/**
	 * @param intArray
	 * @param one
	 * @param two
	 */
	protected void swapE(Element[] intArray, int one, int two) {
		Element temp = intArray[one];
		intArray[one] = intArray[two];
		intArray[two] = temp;
	}

	/**
	 * @param document
	 * @throws ParseException
	 * @throws IOException
	 */
	private void printXTG(Document document) throws ParseException, IOException {

		StringBuffer version = new StringBuffer();
		version.append(WORLD_VERSION1+"\r\n"+WORLD_VERSION2);
		Element root = document.getRootElement();
		List area_list=root.getChildren("area");
		Vector<StringBuffer> bufferList = new Vector<StringBuffer>();
		bufferList.add(version);
		for(int i=0;i<area_list.size();i++)
		{
			Element area = (Element) area_list.get(i);

			StringBuffer buffer = new StringBuffer();

			buffer.append((i>0?"\r\n\r\n\r\n\r\n":"")+area.getAttributeValue("name").toUpperCase()+"\r\n\r\n");

			List<Element> vessel_list=area.getChildren("vessel");


			Element[] arrays = new Element[vessel_list.size()];
			arrays= vessel_list.toArray(arrays);

			bubbleSortByDate(arrays);

			for(int vessel_index=0;vessel_index<arrays.length;vessel_index++)
			{
				Element vessel = arrays[vessel_index];
				Element company=vessel.getChild("company");
				Element inport=vessel.getChild("inport");

				Element outport=vessel.getChild("outport");

				String temp_company="";

				List company_list = company.getChildren("sub-company");
				List inport_list = inport.getChildren("port");
				List outport_list = outport.getChildren("port");



				/*외국항이 3개 미만인 경우에서 스케줄 생성(취소)
				 * if(outport_list.size()<DOWN_SIZE)
					continue;*/
				/*
				if(outport_list.size()>UP_SIZE)
					continue;*/


				/*
				 * 2015-05-06 변경사항
				 * 기존: 외국한 3개 미만인 스케줄 제외
				 * 변경
				 * - 일본, 중국 : 2개 미만 스케줄 제외
				 * - 러시아	 : 1개 이상 스케줄 포함
				 * - 기타		 : 3개 미만 스케줄 제외
				 */
				logger.info("code:"+area);

				if(skipCheck(area,outport_list))
					continue;


				//선사명 정렬

				Vector<String> new_companys = new Vector<String>();


				for(int j=0;j<company_list.size();j++)
				{
					Element sub_company = (Element) company_list.get(j);

					new_companys.add(sub_company.getAttributeValue("company-name"));
				}



				if(company.getAttributeValue(XML_INFO.XML_TAG_MAJOR_COMPANY)!=null)
				{					
					temp_company = this.arrangeCompany(company.getAttributeValue(XML_INFO.XML_TAG_MAJOR_COMPANY),new_companys);
				}else
				{
					temp_company = this.arrangeCompany(new_companys);
				}


				String voayge = getVoyageInfo(vessel);
				buffer.append(WORLD_F + vessel.getAttributeValue("vessel-name") +voayge + " (" + temp_company + ")");
				buffer.append("\r\n"+WORLD_INPORT);				

				for(int j=0;j<inport_list.size();j++)
				{
					Element port = (Element) inport_list.get(j);
					buffer.append((port.getAttributeValue("port-name").equals("PUSAN")?"BUSAN":port.getAttributeValue("port-name"))+" "+
							KSGDateUtil.format2(KSGDateUtil.toDate4(port.getAttributeValue("port-date")))+(j<(inport_list.size()-1)?" - ":""));
				}

				buffer.append("\r\n"+WORLD_OUTPORT);
				for(int j=0;j<outport_list.size();j++)
				{
					Element port = (Element) outport_list.get(j);
					buffer.append(port.getAttributeValue("port-name")+" "+
							KSGDateUtil.format2(KSGDateUtil.toDate4(port.getAttributeValue("port-date")))+
							(j<(outport_list.size()-1)?" - ":""));
				}

				buffer.append("\r\n");
				buffer.append("\r\n");

			}

			buffer.append(WORLD_E);
			bufferList.add(buffer);
		}


		StringBuffer total = new StringBuffer();
		for(int i=0;i<bufferList.size();i++)
		{
			total.append(bufferList.get(i).toString());	
		}
		xtgmanager.createXTGFile(total.toString(),"world_print_old_date.txt");
	}

	/**
	 * @작성일 2015-05-06
	 * @설명 항로별 스케줄 생성 여부 결정
	 * 		- 일본, 중국 : 2개 미만 스케줄 제외
	 * 		- 러시아	  : 1개 이상 스케줄 포함
	 *		- 기타	  : 3개 미만 스케줄 제외
	 * 
	 * @param code
	 * @param outport_list
	 * @return
	 */
	private boolean skipCheck(Element area, List outport_list) {
		boolean flag = false;

		String area_code = area.getAttributeValue("area_code");
		int area_book_code = Integer.parseInt(area.getAttributeValue("area_book_code"));

		if(area_code.equals("02"))
		{
			switch (area_book_code) {
			case 1: // 아시아(Asia)
				if(outport_list.size()<3)
				{
					flag = true;
				}
				break;

			case 16: //  (CIS)
				if(outport_list.size()<3)
				{
					flag = true;
				}
				break;
			case 9: // PERSIAN GULF
				if(outport_list.size()<3)
				{
					flag = true;
				}
				break;



			case 4: // China
				if(outport_list.size()<2)
				{
					flag = true;
				}
				break;
			case 7: // Japan
				if(outport_list.size()<2)
				{
					flag = true;
				}
				break;	

			default:// 러시아는 1개라도 있으면 출력
				break;
			}
		}
		else // 다른 지역은 3개 미만인 스케줄은 제외
		{
			if(outport_list.size()<3)
				flag = true;
		}

		return flag;
	}

	private String arrangeCompany(Vector<String> companyList) {
		Vector<String> temp = new Vector<String>();
		for(int i=0;i<companyList.size();i++)
		{
			boolean flag=true;
			for(int j=0;j<temp.size();j++)
			{
				if(temp.get(j).equals(companyList.get(i)))
				{
					flag=false;
				}
			}
			if(flag)
				temp.add(companyList.get(i));
		}

		//=========추가
		String[] array2=new String[temp.size()];

		for(int i=0;i<temp.size();i++)
		{
			array2[i]=temp.get(i);
		}

		companyList.toArray(array2);
		//알파벳 정렬
		Arrays.sort(array2, new StringCompare());
		//


		String companyStringList ="";
		for(int i=0;i<array2.length;i++)
		{
			companyStringList+=array2[i]+(i<array2.length-1?",":"");

		}



		return companyStringList;

	}
	private String arrangeCompany(String major_company,
			Vector<String> companyList) {

		Vector<String> new_company = new Vector<String>();
		for(int i=0;i<companyList.size();i++)
		{
			if(major_company.compareToIgnoreCase(companyList.get(i))!=0)
			{
				new_company.add(companyList.get(i));
			}
		}

		return major_company+","+arrangeCompany(new_company);
	}	

	private String getVoyageInfo(Element vessel) {

		int result=ScheduleBuildUtil.getNumericVoyage(vessel.getAttributeValue("voyage"));
		if(result!=0)
		{
			return " - "+vessel.getAttributeValue("voyage");
		}else
		{
			return "";
		}

	}
	class ScheduleResult
	{
		private static final String VESSEL = "vessel";
		private static final String COMPANY_ABBR = "company_abbr";
		HashMap<String, Vector> map;
		public ScheduleResult(List li) {
			map = new HashMap<String, Vector>();

			for(int i=0;i<li.size();i++)
			{
				this.addSchedule((ScheduleData) li.get(i));
				logger.debug("add Schedule:"+(ScheduleData) li.get(i));
			}
		}
		private void addSchedule(ScheduleData data)
		{
			String key = createKey(data);
			if(map.containsKey(key))
			{
				Vector d=map.get(key);
				d.add(data);
			}else
			{
				Vector d = new Vector();
				d.add(data);
				map.put(key, d);
			}
		}
		private String createKey(ScheduleData data) {
			String vessel =data.getVessel();
			String voy = data.getVoyage_num();

			return vessel+":"+ScheduleBuildUtil.getNumericVoyage(voy);
		}
		public List getXMLScheduleList() throws ParseException
		{
			logger.debug("start");
			List li =new LinkedList();
			Set<String> v=map.keySet();
			Iterator iter=v.iterator();
			while(iter.hasNext())
			{
				String key = (String) iter.next();
				Vector scheduleList = map.get(key);
				Element datas =new Element("data");

				StringTokenizer st =new StringTokenizer(key,":");

				try{
					logger.debug("key:"+key);
					datas.setAttribute(VESSEL,st.nextToken());
					datas.setAttribute("voy",st.nextToken());
				}catch(NoSuchElementException e)
				{
					e.printStackTrace();
				}

				if(scheduleList.size()>1)
				{
					HashMap<String, String> ports = new HashMap<String, String>();
					String portList="";// portlist의 쓰임새는?
					for(int i=0;i<scheduleList.size();i++)
					{
						ScheduleData ss = (ScheduleData) scheduleList.get(i);

						Element company = new Element(COMPANY_ABBR);

						KSGDateUtil.toDate4(ss.getDateF());
						logger.debug("put:"+ss.getFromPort()+","+ss.getDateF());
						ports.put(ss.getFromPort(), ss.getDateF());

						String company_abbr = ss.getCompany_abbr();
						company.setText(company_abbr);
						notifyMessage("company:"+company_abbr);

						List companyLi =datas.getChildren();
						boolean check = true;
						for(int j=0;j<companyLi.size();j++)
						{
							Element element = (Element) companyLi.get(j);
							if(element.getText().equals(company_abbr))
								check=false;
						}

						if(check)datas.addContent(company);

						Element port = new Element("port");
						port.setAttribute("name",ss.getPort());
						try
						{
							port.setAttribute("date",ss.getDateT().substring(5));
						}
						catch (Exception e) 
						{
							logger.error("error dateT:"+ss.getDateT());
							port.setAttribute("date",ss.getDateT());
						}
						try
						{
							portList+=ss.getPort()+" "+ss.getDateT().substring(5);
						}
						catch (Exception e) 
						{
							logger.error("error dateT:"+ss.getDateT());
							portList+=ss.getPort()+" "+ss.getDateT();	
						}

						try
						{
							ports.put(ss.getPort(), ss.getDateT().substring(5));
						}
						catch(Exception e)
						{
							ports.put(ss.getPort(), ss.getDateT());
						}

						if(i<scheduleList.size()-1)
							portList+="*";
					}
					datas.setAttribute("portlist",portList);	

					ScheduleSortData dd[] = sortKeySet(ports);
					Set<String> v1=ports.keySet();
					Iterator iter1=v1.iterator();

					for(int i=0;i<dd.length;i++)
					{
						Element element = new Element("port");
						element.setAttribute("name",(String) dd[i].key);
						element.setAttribute("date",(String) dd[i].sortData);
						datas.addContent(element);

					}

				}else if(scheduleList.size()==1)
				{
					ScheduleData d=(ScheduleData) scheduleList.get(0);

					datas.setAttribute(COMPANY_ABBR,d.getCompany_abbr());
					try{
						datas.setAttribute("portlist",d.getPort()+" "+d.getDateT().substring(5));
					}catch(Exception e)
					{
						datas.setAttribute("portlist",d.getPort()+" "+d.getDateT());
					}
					Element company = new Element(COMPANY_ABBR);
					company.setText(d.getCompany_abbr());
					datas.addContent(company);
				}

				li.add(datas);
			}
			logger.info("end:"+li);
			return li;
		}


	}
	private ScheduleSortData[] sortKeySet(HashMap<String, String> map) throws ParseException {

		Set<String> keyset = map.keySet();

		Iterator iter=keyset.iterator();

		ScheduleSortData data[] = new ScheduleSortData[map.size()];
		int i=0;
		while(iter.hasNext())
		{
			String key = (String) iter.next();

			data[i] = new ScheduleSortData(key,map.get(key));
			i++;

		}
		Arrays.sort(data);

		return data;


	}
	private void notifyMessage(String message)
	{
		KSGModelManager.getInstance().workProcessText=message;
		KSGModelManager.getInstance().execute(di.getName());
	}

	class MiniSchedule implements Comparable
	{
		public String company_abbr;
		public MiniSchedule(String port, String date) {
			this.port=port;
			this.date= date;
		}
		public MiniSchedule(String company_abbr) {

			this.company_abbr=company_abbr;
		}

		public String port;
		public String date;
		public String toString()
		{
			return port+" "+date+" "+company_abbr;
		}

		public int compareTo(Object o) {

			MiniSchedule d =(MiniSchedule) o;
			try {
				return KSGDateUtil.daysDiff(KSGDateUtil.toDate4(d.date), KSGDateUtil.toDate4(this.date));
			} catch (ParseException e) {
				return -1;
			}
		}

	}

	public boolean isBigger(String fdate, String tdate) {

		try {


			return KSGDateUtil.daysDiff(KSGDateUtil.toDate4(fdate), KSGDateUtil.toDate4(tdate))>0;
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"oneDate:"+fdate+", twoDate:"+tdate);
		}
	}
	public boolean isBigger2(String fdate, String tdate) {

		try {
			return KSGDateUtil.daysDiff(KSGDateUtil.toDate4(fdate), KSGDateUtil.toDate4(tdate))>0;
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"oneDate:"+fdate+", twoDate:"+tdate);
		}
	}


	class MyElement extends Element implements Comparable
	{

		public MyElement(String string) {
			super(string);
		}

		public int compareTo(Object o)
		{
			Element inList=(Element) this.getChild("inport");
			List inportList= inList.getChildren("port");
			Element firstPort1 = (Element) inportList.get (0);

			Element target =(Element) o;
			Element inList2=(Element) target.getChild("inport");
			List inportList2= inList2.getChildren("port");
			Element firstPort2 = (Element) inportList2.get(0);

			try {
				if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(firstPort1.getAttributeValue("port-date")), KSGDateUtil.toDate4(firstPort2.getAttributeValue("port-date")))>0)
				{
					return 0;
				}else
				{
					return 1;
				}

			} catch (ParseException e) {
				return -1;
			}
		}

	}
	@Override
	public int execute() {
		logger.info("start");
		try{

			Element rootElement = new Element("root");
			Document document = new Document(rootElement);

			// 1_0 지역 정보 조회
			AreaInfo info = new AreaInfo();
			info.setOrderBy("area_name");
			List area_li = baseService.getAreaInfoList(info);


			/*lengthOfTask = area_li.size();
		di = new ScheduleBuildMessageDialog(this);
		di.setMessage("Route ");
		di.createAndUpdateUI();*/


			Iterator<AreaInfo> areaIter = area_li.iterator();

			// 1_1 지역별로 출발 하는 스케줄 조회
			while(areaIter.hasNext())

			{
				AreaInfo portInfo= areaIter.next();
				Element area =new Element("area");
				area.setAttribute("name",portInfo.getArea_name());
				area.setAttribute("area_code",portInfo.getArea_code());
				area.setAttribute("area_book_code",String.valueOf(portInfo.getArea_book_code()));
				rootElement.addContent(area);

				List scheduleList = getScheduleListByAraa(portInfo);

				if(scheduleList.size()>0)
				{
					logger.info("area name:"+portInfo.getArea_name());
				}

				Iterator<ScheduleData> scheduleIter = scheduleList.iterator();

				logger.info("port:"+portInfo.getArea_name()+",size:"+scheduleList.size());

				Vector vesselList = new Vector();

				while(scheduleIter.hasNext())
				{
					ScheduleData data = scheduleIter.next();
					
					Vessel vesselInfo = ScheduleManager.getInstance().searchVessel(data.getVessel());
					
					// 사용하지 않는 선박이면 스케줄에서 제외
					if(vesselInfo.getVessel_use()==Vessel.NON_USE)
						continue;

					List<ScheduleData> outboundList = getOutboundScheduleList(portInfo, data);

					Iterator<ScheduleData> scheduleVessel = outboundList.iterator();


					MyElement vessel =new MyElement("vessel");
					vessel.setAttribute("vessel-name",data.getVessel());
					logger.debug("vessel name:"+data.getVessel());

					Vector<MiniSchedule> inList = new Vector<MiniSchedule>();
					Vector<MiniSchedule> outList = new Vector<MiniSchedule>();
					Vector<MiniSchedule> companyList = new Vector<MiniSchedule>();

					while(scheduleVessel.hasNext())
					{
						ScheduleData dataVessel = scheduleVessel.next();
						vessel.setAttribute("voyage",dataVessel.getVoyage_num());

						inList.add(new MiniSchedule(dataVessel.getFromPort(), dataVessel.getDateF()));
						outList.add(new MiniSchedule(dataVessel.getPort(), dataVessel.getDateT()));
						companyList.add(new MiniSchedule(dataVessel.getCompany_abbr()));
					}
					MiniSchedule inArray[]=arrangeInPort(inList);
					if(inArray.length==0)
						continue;

					MiniSchedule lastInSchedule = inArray[inArray.length-1];
					logger.debug("inArray size:"+inArray.length);
					MiniSchedule outArray[]=arrangeOutPort(outList,lastInSchedule);


					MiniSchedule companyArray[]=arrangeMiniScheduleByCompany(companyList);

					Element inport =new Element("inport");
					vessel.addContent(inport);
					Element outport =new Element("outport");
					vessel.addContent(outport);
					Element company =new Element("company");
					vessel.addContent(company);


					try {
						//대표 선사 검색
						Vessel op = new Vessel();
						op.setVessel_name(data.getVessel());
						Vessel searchedVessel = baseService.getVesselInfo(op);

						if(searchedVessel==null)
							continue;

						if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
						{						
							if(searchedVessel.getVessel_company().contains("/"))
							{
								company.setAttribute(XML_INFO.XML_TAG_MAJOR_COMPANY,
										(String) searchedVessel.getVessel_company().subSequence(0, searchedVessel.getVessel_company().indexOf("/")));
							}else
							{
								company.setAttribute(XML_INFO.XML_TAG_MAJOR_COMPANY,searchedVessel.getVessel_company());
							}

							logger.info("set major-company:"+searchedVessel.getVessel_company());

						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					logger.debug("last In Schedule:"+lastInSchedule+","+isBigger(op.getDate_isusse(),lastInSchedule.date ));

					if(isBigger(op.getDate_isusse(),lastInSchedule.date ))
					{
						for(int i=0;i<inArray.length;i++)
						{
							Element port =new Element("port");
							port.setAttribute("port-name",inArray[i].port);
							port.setAttribute("port-date",inArray[i].date);
							inport.addContent(port);
						}

						for(int i=0;i<outArray.length;i++)
						{
							if(isBigger2(lastInSchedule.date, outArray[i].date))
							{
								Element port =new Element("port");
								outport.addContent(port);
								port.setAttribute("port-name",outArray[i].port);
								port.setAttribute("port-date",outArray[i].date);
							}
						}


						for(int i=0;i<companyArray.length;i++)
						{
							Element sub_company =new Element("sub-company");
							company.addContent(sub_company);
							sub_company.setAttribute("company-name",companyArray[i].company_abbr);
						}


						if(outport.getChildren("port").size()!=0)
						{
							vesselList.add(vessel);
						}

					}
				}

				MyElement li[] = new MyElement[vesselList.size()];

				for(int i=0;i<vesselList.size();i++)
				{
					li[i]= (MyElement) vesselList.get(i);
				}

				for(int i=0;i<li.length;i++)
				{
					area.addContent(li[i]);
				}
				
				notifyMessage("area name:"+portInfo.getArea_name());
				
				logger.debug("end");
			}
			createXMLFile(document);
			return ScheduleBuild.SUCCESS;

		}catch(Exception e)
		{
			e.printStackTrace();
			return ScheduleBuild.FAILURE;
		}


	}

	@Override
	public void writeFile(ArrayList<String> printList) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
