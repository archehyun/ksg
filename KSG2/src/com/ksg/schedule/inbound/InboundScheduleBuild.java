package com.ksg.schedule.inbound;

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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ksg.commands.schedule.ScheduleSortData;
import com.ksg.commands.schedule.StringCompare;
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.quark.XTGManager;
import com.ksg.schedule.build.DefaultScheduleBuild;
import com.ksg.schedule.build.PortNullException;
import com.ksg.schedule.build.ScheduleManager;
import com.ksg.schedule.build.VesselNullException;
import com.ksg.view.util.KSGDateUtil;

public class InboundScheduleBuild extends DefaultScheduleBuild{


	private XTGManager xtgmanager = new XTGManager();

	private Element schedule_row;

	private String 	BOLD_TAG_F,
	BOLD_TAG_B,
	TAG_VERSION0,	
	TAG_VERSION2,
	TAG_VERSION3,
	TAG_VERSION6;

	private HashMap<String, String> portMap;	

	private List portlist;

	ScheduleManager scheduleManager = ScheduleManager.getInstance();

	private PortInfo port_abbr;

	public InboundScheduleBuild() throws SQLException {
		super();

		initTag();

		initInPort();

		portlist = scheduleService.getInboundPortList();


		lengthOfTask = portlist.size();

		message = "Inbound ������...";

		logger.debug("searched inbound port list size:"+lengthOfTask);

	}

	private String getTagedPortCode(String attributeValue) {

		String fport=portMap.get(attributeValue);

		return fport!=null?"[<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+fport+BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>]":"[<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+attributeValue+BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>]";
	}

	protected void initTag() {

		BOLD_TAG_B="<ct:>";

		BOLD_TAG_F="<ct:77 Bold Condensed>";

		TAG_VERSION0="<KSC5601-WIN>";

		TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<����:COLOR:CMYK:Process:0,0,0,1>>";

		TAG_VERSION3="<dps:����=<Nextstyle:����><cc:����><cs:7.500000><clig:0><cbs:-0.000000><phll:0><pli:21.543304><pfli:-21.543305><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:21.543304443359375\\,Left\\,.\\,0\\,\\;138.89764404296875\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:����><prat:100.000000><prbc:����><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

		TAG_VERSION6="<pstyle:����><pli:21.543289><pfli:-21.543290><ptr:158\\,Char\\,\\[\\,0\\,\\;><cl:6.750000><chs:0.800003><cs:8.000000><cf:Helvetica LT Std><ct:Roman>";

	}	
	/** IN �ױ� �ʱ�ȭ
	 *  CODE �ʵ忡�� IN�ױ��� ��ȸ�Ͽ� �Ҵ�
	 * 
	 * @throws SQLException
	 */
	private void initInPort() throws SQLException
	{
		Code code_info = new Code();

		code_info.setCode_name_kor("IN�ױ�");

		List<Code> li=baseService.getSubCodeInfo(code_info);

		Iterator<Code> iter = li.iterator();

		portMap = new HashMap<String, String>();

		while(iter.hasNext())
		{
			Code info = iter.next();
			portMap.put(info.getCode_field(), info.getCode_name());
		}
	}




	/**@���� ������ ���� ���� ��ü
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
						//��ǥ ���� �˻�
						Vessel searchedVessel = scheduleManager.searchVessel(vessel);
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
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					catch (VesselNullException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					String d[]=arrangeCommonList(commonList,dateF);
					if(d==null)
					{
						logger.error("������ �ױ��� ��� ����");
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
						Vessel searchedVessel = scheduleManager.searchVessel(vessel);
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
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					catch(VesselNullException e1 )
					{
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

			//=========�߰�
			String[] array2=new String[temp.size()];

			for(int i=0;i<temp.size();i++)
			{
				array2[i]=temp.get(i);
			}

			companyList.toArray(array2);
			//���ĺ� ����
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

						// ���� ��¥ ����
						// ������(date)���� ũ�� �ش� �ױ�����(dd)


						if(KSGDateUtil.daysDiff(KSGDateUtil.toDateBySearch(baseDate),KSGDateUtil.toDateBySearch(companyList.get(i).date) )>0
								// ���� portDate ���� ���� �ű԰� ���� �� 
								&&KSGDateUtil.daysDiff(KSGDateUtil.toDateBySearch(companyList.get(i).date),KSGDateUtil.toDateBySearch(portDate.date) )>0)
						{
							// ��ü
							map.put(companyList.get(i).getPortCode(), companyList.get(i));
						}

						// �̹� ���� ��¥�� �Ҵ� �� ��� �Ҵ�

						Date baseDate1 =KSGDateUtil.toDateBySearch(baseDate);

						Date portDate1 =KSGDateUtil.toDateBySearch(portDate.date);

						Date companyDate = KSGDateUtil.toDateBySearch(companyList.get(i).date);

						if(	KSGDateUtil.daysDiff(baseDate1,portDate1)<=0&&
								KSGDateUtil.daysDiff(baseDate1,companyDate)>0)							
						{
							map.put(companyList.get(i).getPortCode(), companyList.get(i));
						}

					} catch (ParseException e) {
						// TODO Auto-generated catch block
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
					// TODO Auto-generated catch block
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
		 * @param date �����
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
					//����� ��¥���� ������ �߰� ����
					if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(date), KSGDateUtil.toDate4(vesselList.get(key).date))>=0)
					{
						temp.add(vesselList.get(key));
					}else
					{
						String errorLog ="����� ��¥ ���� ������ ����:"+KSGDateUtil.toDate4(date)+","+KSGDateUtil.toDate4(vesselList.get(key).date); 
						logger.error(errorLog);
						//di.addErrorMessage(errorLog);
					}
				} catch (ParseException e) {

					//di.addErrorMessage("��¥ ����");
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
						// voyage �� �ؾ� ��

						if(getNumericVoyage(addVoyage)==Onedata.getNumericVoayge())
						{
							logger.info("3�� �̳� �߰�"+OneVessel+","+OnedateF+","+AddDateF);
							logger.info("3�� �̳� �߰� Voyage"+addVoyage+","+Onedata.getVoyage());

							VoaygeVector voyageVector = new VoaygeVector();
							voyageVector.setVoyage(data.getAttributeValue(XML_INFO.XML_TAG_VOYAGE));
							for(int i=0;i<Onedata.size();i++)
							{
								voyageVector.add(Onedata.get(i));
							}

							voyageVector.add(data);
							voyageVectorMap.remove(keyItem);
							logger.info("key ����:"+keyItem+","+addVoyage+","+Onedata.getNumericVoayge());


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

	/**
	 * @author archehyun
	 *
	 */
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

		//*****		return fport!=null?"["+fport+"]":null;
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

		logger.info("���� ���");

		StringBuffer buffer = new StringBuffer();

		buffer.append(TAG_VERSION0+"\r\n"+TAG_VERSION2+"\r\n"+TAG_VERSION3+"\r\n"+TAG_VERSION6+"\r\n");
		Element root = document.getRootElement();
		List schedule_list=root.getChildren("schedule-row");

		this.lengthOfTask=schedule_list.size();
		current=0;
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
			current++;
		}		

		xtgmanager.createXTGFile(buffer.toString(),"inbound_print.txt");
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
	public int execute() {
		logger.info("make inbound source start");

		try
		{

			long startTime=System.currentTimeMillis();

			Element rootElement = new Element("root");

			Document document = new Document(rootElement);

			Iterator portiter = portlist.iterator();

			while(portiter.hasNext())
			{
				String fromPort = (String) portiter.next();

				try{

					// �ױ��� �˻�
					logger.info(" from port �ױ� �˻�: "+fromPort);
					port_abbr = scheduleManager.searchPort(fromPort);
					schedule_row = new Element(XML_INFO.XML_TAG_SCHEDULE_ROW);
					schedule_row.setAttribute(XML_INFO.XML_TAG_NAME,fromPort);
					schedule_row.setAttribute(XML_INFO.XML_TAG_NATIONALITY,port_abbr.getPort_nationality());
					rootElement.addContent(schedule_row);
				}

				// �ױ����� ���� ���
				catch(PortNullException e)
				{
					try{
						// �ױ� ��� �˻�
						port_abbr=scheduleManager.searchPortAbbr(fromPort);
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
					catch(PortNullException e2)
					{
						//�ױ� �� ���� ��� ���� ��ȯ
						throw new PortNullException(fromPort);
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

			logger.info("inbound build time : "+(System.currentTimeMillis()-startTime));

		}
		catch(PortNullException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "��ϵ� �ױ��� �����ϴ�.-"+e.getPortName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;

	}



}
