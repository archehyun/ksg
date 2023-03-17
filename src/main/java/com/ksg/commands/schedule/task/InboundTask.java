package com.ksg.commands.schedule.task;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ksg.commands.schedule.ScheduleSortData;
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.StringCompare;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

public class InboundTask extends SimpleTask{
	private XTGManager xtgmanager = new XTGManager();
	private Element schedule_row;
	private String 	BOLD_TAG_F,
					BOLD_TAG_B,
					TAG_VERSION0,	
					TAG_VERSION2,
					TAG_VERSION3,
					TAG_VERSION6;
	private HashMap<String, String> portMap;
	protected Logger logger = LogManager.getLogger(this.getClass());
	private ScheduleBuildMessageDialog di;
	public InboundTask() {

		try {

			initTag();
			initInPort();
			di = new ScheduleBuildMessageDialog (this);
			di.setMessage("inbound");

			makeSchedule();
		} catch (Exception e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e.getMessage());
		} 
		finally
		{
			done=true;
			di.setVisible(false);
			di.dispose();
		}

	}
	/**
	 * @param attributeValue
	 * @return
	 */
	private String getTagedPortCode(String attributeValue) {
		String fport=portMap.get(attributeValue);


		return fport!=null?"[<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+fport+BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>]":"[<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+attributeValue+BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>]";
	}

	protected void initTag() {

		BOLD_TAG_B="<ct:>";

		BOLD_TAG_F="<ct:77 Bold Condensed>";

		TAG_VERSION0="<KSC5601-WIN>";

		TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<검정:COLOR:CMYK:Process:0,0,0,1>>";

		TAG_VERSION3="<dps:정규=<Nextstyle:정규><cc:검정><cs:7.500000><clig:0><cbs:-0.000000><phll:0><pli:21.543304><pfli:-21.543305><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:21.543304443359375\\,Left\\,.\\,0\\,\\;138.89764404296875\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

		TAG_VERSION6="<pstyle:정규><pli:21.543289><pfli:-21.543290><ptr:158\\,Char\\,\\[\\,0\\,\\;><cl:6.750000><chs:0.800003><cs:8.000000><cf:Helvetica LT Std><ct:Roman>";

	}	
	/** IN 항구 초기화
	 *  CODE 필드에서 IN항구를 조회하여 할당
	 * 
	 * @throws SQLException
	 */
	private void initInPort() throws SQLException
	{

		Code code_info = new Code();
		code_info.setCode_name_kor("IN항구");
		List<Code> li=baseService.getSubCodeInfo(code_info);

		Iterator<Code> iter = li.iterator();

		portMap = new HashMap<String, String>();
		while(iter.hasNext())
		{
			Code info = iter.next();
			portMap.put(info.getCode_field(), info.getCode_name());
		}
	}
	/**
	 * @throws SQLException
	 * @throws JDOMException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void makeSchedule() throws SQLException, JDOMException, IOException, ParseException {
		logger.info("make inbound source start");

		
		Element rootElement = new Element("root");
		
		Document document = new Document(rootElement);
		
		List portlist = scheduleService.getInboundPortList();
		
		logger.debug("schedule size:"+portlist.size());
		
		lengthOfTask = portlist.size();

		di.createAndUpdateUI();		

		Iterator portiter = portlist.iterator();

		while(portiter.hasNext())
		{
			String fromPort = (String) portiter.next();

			//항구 및 지역 표시(볼드체)
			if(fromPort!=null)
			{
				logger.info("from port: "+fromPort);

				PortInfo port_abbr=baseService.getPortInfo(fromPort);
				try{
					if(port_abbr==null)
						continue;
					schedule_row = new Element(XML_INFO.XML_TAG_SCHEDULE_ROW);
					schedule_row.setAttribute(XML_INFO.XML_TAG_NAME,fromPort);
					schedule_row.setAttribute(XML_INFO.XML_TAG_NATIONALITY,port_abbr.getPort_nationality());
					rootElement.addContent(schedule_row);
				}catch(NullPointerException e)
				{
					e.printStackTrace();
				}
			}else
			{
				PortInfo port_abbr=scheduleService.getPortInfoByPortAbbr(fromPort);

				if(port_abbr==null)
					continue;

				if(!scheduleService.isInPort(port_abbr.getPort_name()))
				{
					PortInfo portInfo2=baseService.getPortInfoByPortName(port_abbr.getPort_name());
					schedule_row = new Element(XML_INFO.XML_TAG_SCHEDULE_ROW);
					schedule_row.setAttribute(XML_INFO.XML_TAG_NATIONALITY,portInfo2.getPort_nationality());
					rootElement.addContent(schedule_row);
					Element fromPorts =new Element(XML_INFO.XML_TAG_FROM_PORT);
					schedule_row.addContent(fromPorts);
				}else
				{
					continue;
				}
			}
			
			
			List inScheduleList = scheduleService.getInboundScheduleList(fromPort);

			List<Element> xmlScheduel = new ScheduleDateFResult(new ScheduleDateTResult(inScheduleList)).getXMLScheduleList();


			for(int i=0;i<xmlScheduel.size();i++)
			{
				schedule_row.addContent(xmlScheduel.get(i));
			}
			current++;

		}
		createXMLFile(document);

	}

	/**@설명 스케줄 정보 저장 객체
	 * @author archehyun
	 *
	 */
	class ScheduleDateFResult
	{
		HashMap<String, VoaygeVector> voyageVectorMap;
		public ScheduleDateFResult(ScheduleDateTResult result) {
			voyageVectorMap = new HashMap<String, VoaygeVector>();
			List<Element> li = result.getXMLScheduleList();
			for(int i=0;i<li.size();i++)
			{
				this.addSchedule(li.get(i));
			}
		}

		public List<Element> getXMLScheduleList() throws ParseException
		{
			List<Element> li =new LinkedList<Element>();
			Set<String> v=voyageVectorMap.keySet();

			ScheduleSortData[] data=sortKeySet(v);


			for(int i=0;i<data.length;i++)
			{
				ScheduleSortData sortItem = data[i];
				String key = (String) sortItem.key;
				VoaygeVector vesselList = voyageVectorMap.get(key);
				Element datas =new Element("data");
				StringTokenizer st =new StringTokenizer(key,":");
				String vessel = st.nextToken();
				String dateF = st.nextToken();
				String company="";


				String common="";
				String tagCommon="";
				String company2="";				
				

				if(vesselList.size()>1)
				{
					ScheduleSortData[] s = sortVesselList(vesselList);


					Vector<String> companyList = new Vector<String>();
					Vector inDateList = new Vector();
					Vector tagDateList = new Vector();
					Vector<MySortElement> commonList = new Vector<MySortElement>();
					for(int j=0;j<s.length;j++)
					{
						Element e = (Element) s[j].key;

						String sub_company=e.getAttributeValue(XML_INFO.XML_TAG_COMPANY_ABBR);
						String sub_agent = e.getAttributeValue(XML_INFO.XML_TAG_AGENT);

						try{
							if(sub_agent==null)
							{
								logger.error("agent null");
							}
							String port=getPortCodeInfo(e.getAttributeValue(XML_INFO.XML_TAG_PORT));
							if(port==null)
							{
								logger.error("in port error:"+e.getAttributeValue(XML_INFO.XML_TAG_PORT));
								continue;
							}

							String dateT =e.getAttributeValue(XML_INFO.XML_TAG_DATE_T);
							common +=port+dateT;


//*****							tagCommon +="\t"+getTagedPortCode(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+e.getAttributeValue("dateT");
							tagCommon +="\t"+getTagedPortCode(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+e.getAttributeValue("dateT");

							MySortElement element = new MySortElement();
							element.port = e.getAttributeValue(XML_INFO.XML_TAG_PORT);
							element.date=e.getAttributeValue(XML_INFO.XML_TAG_DATE_T);

							element.tagPort = getTagedPortCode(e.getAttributeValue(XML_INFO.XML_TAG_PORT));

							commonList.add(element);
							inDateList.add(getPortCodeInfo(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+e.getAttributeValue("dateT"));
							tagDateList.add(getTagedPortCode(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+e.getAttributeValue("dateT"));
							companyList.add(this.getCompanyAndAgent(sub_company, sub_agent));

						}catch(Exception ee)
						{
							logger.error("error:"+e.getAttributeValue(XML_INFO.XML_TAG_DATE_T));
							ee.printStackTrace();
							String port=getPortCodeInfo(e.getAttributeValue(XML_INFO.XML_TAG_PORT));
							if(port==null)
								continue;
							common +=getPortCodeInfo(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+e.getAttributeValue("dateT");

							company2+=(sub_company.equals(sub_agent)?sub_company:sub_company+"/"+sub_agent)+(j<s.length-1?",":"");

							companyList.add(this.getCompanyAndAgent(sub_company, sub_agent));
							tagCommon +="\t"+getTagedPortCode(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+e.getAttributeValue("dateT");
						}

					}
					try {
						//대표 선사 검색
						Vessel op = new Vessel();
						op.setVessel_name(vessel);
						Vessel searchedVessel = baseService.getVesselInfo(op);
						if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
						{
							datas.setAttribute(XML_INFO.XML_TAG_MAJOR_COMPANY,searchedVessel.getVessel_company());
							logger.info("set major-company:"+searchedVessel.getVessel_company());
							company=arrangeCompany(searchedVessel.getVessel_company(),companyList);
							
						}else
						{
							company=arrangeCompany(companyList);
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					String d[]=arrangeCommonList(commonList,dateF);
					if(d==null)
					{
						logger.error("국내항 항구가 없어서 생략");
						continue;
					}
					common="\t"+d[0];
					tagCommon="\t"+d[1];


				}else if(vesselList.size()==1)
				{
					Element e = vesselList.get(0);
					String sub_company=e.getAttributeValue(XML_INFO.XML_TAG_COMPANY_ABBR);
					String sub_agent = e.getAttributeValue(XML_INFO.XML_TAG_AGENT);
					try
					{
						String port=getPortCodeInfo(e.getAttributeValue(XML_INFO.XML_TAG_PORT));
						String dateT =KSGDateUtil.format2(KSGDateUtil.toDate4(e.getAttributeValue(XML_INFO.XML_TAG_DATE_T)));


						if(port==null)
							continue;
						common =port+dateT;
						tagCommon ="\t"+getTagedPortCode(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+dateT;
						company2=sub_company.equals(sub_agent)?sub_company:sub_company+"/"+sub_agent;

					}catch(Exception ee)
					{
						ee.printStackTrace();
						String port=getPortCodeInfo(e.getAttributeValue(XML_INFO.XML_TAG_PORT));
						if(port==null)
							continue;
						common +=getPortCodeInfo(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+e.getAttributeValue(XML_INFO.XML_TAG_DATE_T);
						tagCommon +="\t"+getTagedPortCode(e.getAttributeValue(XML_INFO.XML_TAG_PORT))+e.getAttributeValue(XML_INFO.XML_TAG_DATE_T);
						company2=(sub_company.equals(sub_agent)?sub_company:sub_company+"/"+sub_agent);
					}
					company=company2;
					
					try {
						//대표 선사 검색
						Vessel op = new Vessel();
						op.setVessel_name(vessel);
						Vessel searchedVessel = baseService.getVesselInfo(op);
						if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
						{
							datas.setAttribute(XML_INFO.XML_TAG_MAJOR_COMPANY,searchedVessel.getVessel_company());
							logger.info("set major-company:"+searchedVessel.getVessel_company());
							company=searchedVessel.getVessel_company();
							
						}else
						{
							company=company2;
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					catch (NullPointerException e1) {
						throw new NullPointerException("vessel name null:"+vessel+", agent:"+sub_agent);
					}
				}

				datas.setAttribute(XML_INFO.XML_TAG_DATE_F,dateF);
				datas.setAttribute(XML_INFO.XML_TAG_VESSEL,vessel);
				datas.setAttribute(XML_INFO.XML_TAG_COMPANY,company);
				datas.setAttribute(XML_INFO.XML_TAG_COMMON,common);
				datas.setAttribute(XML_INFO.XML_TAG_TAG_COMMON,tagCommon);
				datas.setAttribute(XML_INFO.XML_TAG_VOYAGE,vesselList.getVoyage());
				li.add(datas);
			}


			return li;
		}



		private String getCompanyAndAgent(String company,String agent)
		{
			return company.equals(agent)?company:company+"/"+agent;
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
		

		/**
		 * @설명
		 * @param companyList 
		 * @return
		 */
		private String[] arrangeCommonList(Vector<MySortElement> companyList,String baseDate)
		{
			HashMap<String, MySortElement> map = new HashMap<String, MySortElement>();

			for(int i=0;i<companyList.size();i++)
			{
				if(map.containsKey(companyList.get(i).getPortCode()))
				{	

					try {

						MySortElement portDate=map.get(companyList.get(i).getPortCode());

						// 빠른 날짜 적용
						// 기준일(date)보다 크고 해당 항구일자(dd)


						if(KSGDateUtil.daysDiff(KSGDateUtil.toDateBySearch(baseDate),KSGDateUtil.toDateBySearch(companyList.get(i).date) )>0
								// 기존 portDate 일자 보다 신규가 작을 때 
								&&KSGDateUtil.daysDiff(KSGDateUtil.toDateBySearch(companyList.get(i).date),KSGDateUtil.toDateBySearch(portDate.date) )>0)
						{
							// 교체
							map.put(companyList.get(i).getPortCode(), companyList.get(i));
						}

						// 이미 작은 날짜가 할당 된 경우 할당

						if(KSGDateUtil.daysDiff(KSGDateUtil.toDateBySearch(baseDate),KSGDateUtil.toDateBySearch(portDate.date) )<=0&&
								KSGDateUtil.daysDiff(KSGDateUtil.toDateBySearch(baseDate),KSGDateUtil.toDateBySearch(companyList.get(i).date) )>0
						)							
						{
							map.put(companyList.get(i).getPortCode(), companyList.get(i));
						}

					} catch (ParseException e) {
						e.printStackTrace();
					}

				}else
				{
					map.put(companyList.get(i).getPortCode(), companyList.get(i));
				}
			}

			String commom="";
			String tagcommom="";

			MySortElement[] sotedData = sortCommonList(map,baseDate);

			if(sotedData.length==0)
				return null;

			for(int i=0;i<sotedData.length;i++)
			{
				try {

					String dateT=KSGDateUtil.format2(KSGDateUtil.toDate4(sotedData[i].date));

					commom += sotedData[i].getPortCode()+dateT;
					tagcommom += sotedData[i].getTagedPort()+dateT+(i<sotedData.length-1?"\t":"");

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}

			String d[] = new String[2];
			d[0]=commom;
			d[1]=tagcommom;
			return d;

		}


		/**
		 * @param vesselList
		 * @return
		 */
		private ScheduleSortData[] sortVesselList(Vector<Element> vesselList) 
		{
			Iterator<Element> iter=vesselList.iterator();
			ScheduleSortData data[] = new ScheduleSortData[vesselList.size()];

			for(int i=0;iter.hasNext();i++)
			{
				Element key = iter.next();
				data[i] = new ScheduleSortData(key,key.getAttributeValue(XML_INFO.XML_TAG_DATE_T));

			}

			Arrays.sort(data);



			return data;
		}
		/**
		 * @param vesselList
		 * @param date 출발일
		 * @return
		 */
		private MySortElement[] sortCommonList(HashMap<String, MySortElement> vesselList,String date) 
		{
			Set<String> v=vesselList.keySet();
			Iterator<String> iter=v.iterator();


			Vector<MySortElement> temp = new Vector<MySortElement>();
			for(int i=0;iter.hasNext();i++)
			{
				String key = iter.next();

				try {
					//출발일 날짜보다 느린날 추가 안함
					if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(date), KSGDateUtil.toDate4(vesselList.get(key).date))>=0)
					{
						temp.add(vesselList.get(key));
					}else
					{
						logger.error("출발일 날짜보다 느린날 추가 안함:"+KSGDateUtil.toDate4(date)+","+KSGDateUtil.toDate4(vesselList.get(key).date));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
			MySortElement data[] = new MySortElement[temp.size()];
			for(int i=0;i<temp.size();i++)
			{
				data[i]=temp.get(i);
			}
			Arrays.sort(data);


			return data;
		}
		private ScheduleSortData[] sortKeySet(Set<String> v) throws ParseException {

			Iterator<String> iter=v.iterator();


			ScheduleSortData data[] = new ScheduleSortData[v.size()];
			int i=0;
			while(iter.hasNext())
			{
				String key = iter.next();
				StringTokenizer st = new StringTokenizer(key,":");
				st.nextToken();
				data[i] = new ScheduleSortData(key,st.nextToken());
				i++;

			}
			Arrays.sort(data);

			return data;
		}
		private void addSchedule(Element data)
		{
			String key = createKey(data);

			String AddVessel =data.getAttributeValue(XML_INFO.XML_TAG_VESSEL);
			String AddDateF = data.getAttributeValue(XML_INFO.XML_TAG_DATE_F);
			String addVoyage = data.getAttributeValue(XML_INFO.XML_TAG_VOYAGE);


			if(voyageVectorMap.containsKey(key))
			{	
				VoaygeVector d=voyageVectorMap.get(key);
				d.add(data);
			}else
			{
				Set<String> keyList=voyageVectorMap.keySet();
				Iterator<String> iter = keyList.iterator();

				while(iter.hasNext())
				{
					String keyItem = iter.next();
					StringTokenizer st = new StringTokenizer(keyItem,":");
					String OneVessel = st.nextToken();
					String OnedateF = st.nextToken();

					VoaygeVector Onedata=voyageVectorMap.get(keyItem);
					if(OneVessel.equals(AddVessel)&&KSGDateUtil.isThreeDayUnder(AddDateF, OnedateF))
					{
						// voyage 비교 해야 함

						if(getNumericVoyage(addVoyage)==Onedata.getNumericVoayge())
						{
							logger.info("3일 이내 추가"+OneVessel+","+OnedateF+","+AddDateF);
							logger.info("3일 이내 추가 Voyage"+addVoyage+","+Onedata.getVoyage());

							VoaygeVector voyageVector = new VoaygeVector();
							voyageVector.setVoyage(data.getAttributeValue(XML_INFO.XML_TAG_VOYAGE));
							for(int i=0;i<Onedata.size();i++)
							{
								voyageVector.add(Onedata.get(i));
							}

							voyageVector.add(data);
							voyageVectorMap.remove(keyItem);
							logger.info("key 삭제:"+keyItem+","+addVoyage+","+Onedata.getNumericVoayge());


							String newKey = createKey(AddVessel,KSGDateUtil.biggerDate(OnedateF, AddDateF));

							voyageVectorMap.put(newKey, voyageVector);
							return;
						}
					}

				}
				VoaygeVector newVoageVector = new VoaygeVector();
				newVoageVector.setVoyage(addVoyage);
				newVoageVector.add(data);
				voyageVectorMap.put(key, newVoageVector);
			}


		}
		private String createKey(Element data) {
			String vessel =data.getAttributeValue(XML_INFO.XML_TAG_VESSEL);
			String dateF = data.getAttributeValue(XML_INFO.XML_TAG_DATE_F);
			return vessel+":"+dateF;
		}
		private String createKey(String vessel, String dateF) {

			return vessel+":"+dateF;
		}
	}

	class ScheduleDateTResult 
	{

		HashMap<String, Vector> map;
		public ScheduleDateTResult(List scheduleList) 
		{
			map = new HashMap<String, Vector>();


			for(int i=0;i<scheduleList.size();i++)
			{
				this.addSchedule((ScheduleData) scheduleList.get(i));
			}

		}
		private String createKey(ScheduleData data)
		{
			return data.getVessel()+":"+data.getPort()+":"+data.getDateT();
		}
		private void addSchedule(ScheduleData data)
		{
			String key = createKey(data);

			if(map.containsKey(key))
			{
				Vector<ScheduleData> d=map.get(key);
				d.add(data);
				logger.debug("take key:"+data);
			}else
			{
				Vector<ScheduleData> d = new Vector<ScheduleData>();
				d.add(data);
				map.put(key, d);
				logger.debug("put key:"+data);
			}
		}
		private List<Element> getXMLScheduleList()
		{
			List<Element> li =new LinkedList<Element>();
			Set<String> v=map.keySet();
			Iterator<String> iter=v.iterator();
			while(iter.hasNext())
			{
				String key = iter.next();

				Vector vesselList = map.get(key);


				StringTokenizer st =new StringTokenizer(key,":");
				String vessel = st.nextToken();
				String port = st.nextToken();
				String dataT = st.nextToken();

				if(vesselList.size()>1)
				{
					for(int i=0;i<vesselList.size();i++)
					{

						ScheduleData ss = (ScheduleData) vesselList.get(i);
						Element datas =new Element("data");
						datas.setAttribute(XML_INFO.XML_TAG_VOYAGE,ss.getVoyage_num());						
						datas.setAttribute(XML_INFO.XML_TAG_AGENT,ss.getAgent());
						datas.setAttribute(XML_INFO.XML_TAG_COMPANY_ABBR,ss.getCompany_abbr());
						datas.setAttribute(XML_INFO.XML_TAG_DATE_F,ss.getDateF());
						datas.setAttribute(XML_INFO.XML_TAG_DATE_T,KSGDateUtil.getDate(dataT,KSGDateUtil.BACK));
						datas.setAttribute(XML_INFO.XML_TAG_VESSEL,vessel);
						datas.setAttribute(XML_INFO.XML_TAG_PORT,port);
						li.add(datas);


					}
				}
				else if(vesselList.size()==1)
				{
					ScheduleData d=(ScheduleData) vesselList.get(0);
					Element datas =new Element("data");
					datas.setAttribute(XML_INFO.XML_TAG_VOYAGE,d.getVoyage_num());
					datas.setAttribute(XML_INFO.XML_TAG_AGENT,d.getAgent());
					datas.setAttribute(XML_INFO.XML_TAG_COMPANY_ABBR,d.getCompany_abbr());
					datas.setAttribute(XML_INFO.XML_TAG_DATE_F,KSGDateUtil.getDate(d.getDateF(), KSGDateUtil.FORWARD));
					datas.setAttribute(XML_INFO.XML_TAG_DATE_T,KSGDateUtil.getDate(dataT,KSGDateUtil.BACK));
					datas.setAttribute(XML_INFO.XML_TAG_VESSEL,vessel);
					datas.setAttribute(XML_INFO.XML_TAG_PORT,port);
					li.add(datas);
				}
			}

			return li;
		}
	}
	class MySortElement implements Comparable
	{
		public String tagPort;
		public String date;
		public String company;
		public String port;

		public String getPortCode()
		{
			return getPortCodeInfo(port);
		}
		public String getTagedPort()
		{
			return getTagedPortCode(port);
		}
		public int compareTo(Object o) {
			MySortElement table1 = (MySortElement) o;

			try 
			{
				Date one = null;
				Date two = null;


				one = KSGDateUtil.toDate4(table1.date);
				two = KSGDateUtil.toDate4(this.date);


				return KSGDateUtil.daysDiff( one,two);
			} catch (ParseException e) 
			{
				return -1;
			}catch (Exception e)
			{
				return-1;
			}
		}
	}
	private String getPortCodeInfo(String port)
	{
		String fport=portMap.get(port);

		return fport!=null?"[====="+fport+"=====]":null;

	}
	private void createXMLFile(Document document) {
		try 
		{

			Format format = Format.getPrettyFormat();

			format.setEncoding("EUC-KR");
			format.setIndent("\r\n");
			format.setIndent("\t");


			XMLOutputter outputter = new XMLOutputter(format);


			FileWriter writer = new FileWriter("inbound_source.xml");
			outputter.output(document, writer);
			writer.close();

			try {
				printXTG(document);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	private void printXTG(Document document) throws ParseException, IOException {

		StringBuffer buffer = new StringBuffer();
		buffer.append(TAG_VERSION0+"\r\n"+TAG_VERSION2+"\r\n"+TAG_VERSION3+"\r\n"+TAG_VERSION6+"\r\n");
		Element root = document.getRootElement();
		List schedule_list=root.getChildren("schedule-row");
		for(int i=0;i<schedule_list.size();i++)
		{
			Element schedule_row = (Element) schedule_list.get(i);
			buffer.append((i!=0?"<ct:><cf:><cs:><cs:8.000000><cf:Helvetica LT Std><ct:Bold>":"<ct:><cf:><cs:><cs:8.000000><cf:Helvetica LT Std><ct:Bold>")+schedule_row.getAttributeValue("name")+" , "+schedule_row.getAttributeValue("nationality"));

			buffer.append("\r\n\r\n");
			List dataLi=schedule_row.getChildren("data");
			for(int j=0;j<dataLi.size();j++)
			{
				Element data = (Element) dataLi.get(j);

				buffer.append("<ct:><cf:><cs:><cs:6.500000><cf:Helvetica LT Std><ct:Roman>"+KSGDateUtil.format2(KSGDateUtil.toDate4(data.getAttributeValue("dateF")))+"\t<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+ data.getAttributeValue("vessel")+BOLD_TAG_B+"<cf:>");
				buffer.append("<cf:Helvetica LT Std><ct:Roman>  ("+data.getAttributeValue("company")+")");
				buffer.append(data.getAttributeValue("tag-common")+"\r\n");
			}

			buffer.append((i<schedule_list.size()-1?"\r\n":""));
		}		

		xtgmanager.createXTGFile(buffer.toString(),"inbound_print.txt");
	}




	/**보이지 넘버 생성
	 * @param voyage_num
	 * @return
	 */
	private int getNumericVoyage(String voyage_num)
	{
		
		int result=0;

		String temp="";
		if(voyage_num==null)
			return 0;
		for(int i=0;i<voyage_num.length();i++)
		{
			try{
				// 숫자만 처리
				temp+=Integer.parseInt(String.valueOf(voyage_num.charAt(i)));
			}catch(NumberFormatException e)
			{
				//숫자가 아니면 처리 안함
				//logger.error(e.getMessage());
			}
		}
		try{
			result=Integer.valueOf(temp);
		}catch(Exception e)
		{
			return 0;
		}

		return result;
	}
	class VoaygeVector extends Vector<Element>
	{
		private String voyage;

		private int voyage_n;

		public String getVoyage() {
			return voyage;
		}

		public void setVoyage(String voyage) {
			this.voyage = voyage;
			voyage_n = getNumericVoyage(voyage);
		}
		public int getNumericVoayge()
		{
			return voyage_n;
		}
	}

}
