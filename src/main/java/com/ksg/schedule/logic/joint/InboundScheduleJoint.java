package com.ksg.schedule.logic.joint;

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
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.StringCompare;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.logic.PortNullException;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.VesselNullException;

/**
 * ScheduelDateFResult
 * ----ScheduelDateTResult
 * ---------MySortElement
 * 
 * 
 * 국내항 인바운드 스케줄 공동배선 생성 클래스
 * 
 * 
 * @author 박창현
 *
 */
public class InboundScheduleJoint extends DefaultScheduleJoint{


	private static final String INBOUND_PRINT_TXT = "inbound_print.txt";

	private XTGManager xtgmanager = new XTGManager();

	private Element schedule_row;

	private String 	BOLD_TAG_F;
	private String 	BOLD_TAG_B;
	private String 	TAG_VERSION0;	
	private String 	TAG_VERSION2;
	private String 	TAG_VERSION3;
	private String 	TAG_VERSION6;

	private HashMap<String, String> portMap;	

	// 항구 리스트
	private List portlist;

	// 항구 약어
	private PortInfo port_abbr;

	private String inbound_source_filename;

	public InboundScheduleJoint() throws SQLException {
		super();
		
		inbound_source_filename = "inbound_source.xml";
		
		initInPort();

		portlist = scheduleService.getInboundPortList();

		lengthOfTask = portlist.size();

		message = "Inbound 생성중...";

		logger.debug("searched inbound port list size:"+lengthOfTask);

	}

	/**
	 * express port code
	 * @param attributeValue
	 * @return
	 */
	private String getTagedPortCode(String attributeValue) {

		String fport=portMap.get(attributeValue);

		return fport!=null?"[<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+fport+BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>]":"[<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+attributeValue+BOLD_TAG_B+"<cf:><cf:Helvetica LT Std><ct:Roman>]";
	}

	public void initTag() {

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

		/**
		 * 
		 * 스케줄 리스트(XML) 반환
		 * @return 스케줄 리스트
		 * @throws ParseException
		 * @throws VesselNullException 
		 * @throws SQLException 
		 */
		public List<Element> getXMLScheduleList() throws ParseException, SQLException, VesselNullException
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
					//대표 선사 검색
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

		/**
		 * 
		 * express Company and Agent
		 * 
		 * EX) Company/Agent
		 * @param company
		 * @param agent
		 * @return
		 */
		private String getCompanyAndAgent(String company,String agent)
		{
			return company.equals(agent)?company:company+"/"+agent;
		}
		/**
		 * 
		 * arrange company list and return text
		 * @param companyList
		 * @return 정렬된 회사 목록
		 */
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
				if(flag) temp.add(companyList.get(i));
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


			String companyStringList ="";
			for(int i=0;i<array2.length;i++)
			{
				companyStringList+=array2[i]+(i<array2.length-1?",":"");
			}

			return companyStringList;

		}
		
		/**
		 * 대표 선사 표시 후, 선사 목록 정렬
		 * 
		 * @param major_company
		 * @param companyList
		 * @return
		 */
		private String arrangeCompany(String major_company,
				Vector<String> companyList) {

			Vector<String> new_companys = new Vector<String>();
			for(int i=0;i<companyList.size();i++)
			{
				if(major_company.compareToIgnoreCase(companyList.get(i))!=0)
				{
					new_companys.add(companyList.get(i));
				}
			}
			
			/*
			 * 일자 : 20210603
			 * 수정내용 '대표선사,' 표시 수정
			 * 
			 */
			if(new_companys.size()>0)
			{
				return major_company+","+arrangeCompany(new_companys);	
			}
			else
			{
				return major_company;
			}
			
		}		


		/**
		 * 공동배선 정렬 메소드
		 * 
		 * @param companyList
		 * @return
		 * @throws ParseException 
		 */
		private String[] arrangeCommonList(Vector<MySortElement> companyList,String baseDate) throws ParseException
		{
			HashMap<String, MySortElement> map = new HashMap<String, MySortElement>();

			for(int i=0;i<companyList.size();i++)
			{
				if(map.containsKey(companyList.get(i).getPortCode()))
				{	


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

					Date baseDate1 =KSGDateUtil.toDateBySearch(baseDate);

					Date portDate1 =KSGDateUtil.toDateBySearch(portDate.date);

					Date companyDate = KSGDateUtil.toDateBySearch(companyList.get(i).date);

					if(	KSGDateUtil.daysDiff(baseDate1,portDate1)<=0&&
							KSGDateUtil.daysDiff(baseDate1,companyDate)>0)							
					{
						map.put(companyList.get(i).getPortCode(), companyList.get(i));
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

			String item[] = new String[2];
			item[0]=commom;
			item[1]=tagcommom;
			return item;
		}


		/** 
		 * 
		 * 날짜 기준 선박 정렬
		 * sort vessel list
		 * 
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
		 * @throws ParseException 
		 */
		private MySortElement[] sortCommonList(HashMap<String, MySortElement> vesselList,String date) throws ParseException 
		{
			Set<String> v=vesselList.keySet();
			Iterator<String> iter=v.iterator();


			Vector<MySortElement> temp = new Vector<MySortElement>();
			for(int i=0;iter.hasNext();i++)
			{
				String key = iter.next();

				//출발일 날짜보다 느린날 추가 안함
				if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(date), KSGDateUtil.toDate4(vesselList.get(key).date))>=0)
				{
					temp.add(vesselList.get(key));
				}else
				{
					String errorLog ="출발인 날짜 보다 느린날 제거:"+KSGDateUtil.toDate4(date)+","+KSGDateUtil.toDate4(vesselList.get(key).date); 
					logger.error(errorLog);
					//di.addErrorMessage(errorLog);
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
		/**
		 * @param keyList
		 * @return
		 * @throws ParseException
		 */
		private ScheduleSortData[] sortKeySet(Set<String> keyList) throws ParseException {

			Iterator<String> iter=keyList.iterator();


			ScheduleSortData data[] = new ScheduleSortData[keyList.size()];
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
		/**
		 * add schedule
		 * check 3일 이내
		 * @param data
		 */
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

						if(ScheduleBuildUtil.getNumericVoyage(addVoyage)==Onedata.getNumericVoayge())
						{
							logger.debug("3일 이내 추가"+OneVessel+","+OnedateF+","+AddDateF);
							logger.debug("3일 이내 추가 Voyage"+addVoyage+","+Onedata.getVoyage());

							VoaygeVector voyageVector = new VoaygeVector();
							voyageVector.setVoyage(data.getAttributeValue(XML_INFO.XML_TAG_VOYAGE));
							for(int i=0;i<Onedata.size();i++)
							{
								voyageVector.add(Onedata.get(i));
							}

							voyageVector.add(data);
							voyageVectorMap.remove(keyItem);
							logger.debug("key 삭제:"+keyItem+","+addVoyage+","+Onedata.getNumericVoayge());


							String newKey = createKey(AddVessel,KSGDateUtil.biggerDate(OnedateF, AddDateF), ScheduleBuildUtil.getNumericVoyage(addVoyage));

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
		/**
		 * 
		 * 키 생성
		 * @param xml Element
		 * @return
		 */
		private String createKey(Element data) {
			String vessel =data.getAttributeValue(XML_INFO.XML_TAG_VESSEL);
			String dateF = data.getAttributeValue(XML_INFO.XML_TAG_DATE_F);
			int voyage_n = ScheduleBuildUtil.getNumericVoyage(data.getAttributeValue(XML_INFO.XML_TAG_VOYAGE));
			return createKey(vessel,dateF,voyage_n);
		}
		/**
		 * 키 생성
		 * 
		 * @param vessel
		 * @param dateF
		 * @param voyage_n
		 * @return
		 */
		private String createKey(String vessel, String dateF,int voyage_n) {

			return vessel+":"+dateF+":"+voyage_n;
		}
	}

	/**
	 * 
	 * 출발일자 정렬을 위한 스케줄 데이터 
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
		/**
		 * 
		 *  키 생성 메소드
		 * 
		 * vessel name : port : dateT
		 * 
		 * 
		 * @param data
		 * @return
		 */
		private String createKey(ScheduleData data)
		{
			return data.getVessel()+":"+data.getPort()+":"+data.getDateT();
		}
		/**
		 * 
		 * 스케줄 정보 추가
		 * 
		 * @param data
		 */
		private void addSchedule(ScheduleData data)
		{
			String key = createKey(data);

			if(map.containsKey(key))
			{
				Vector<ScheduleData> item=map.get(key);
				item.add(data);
				logger.debug("take key:"+data);
			}else
			{
				Vector<ScheduleData> item = new Vector<ScheduleData>();
				item.add(data);
				map.put(key, item);
				logger.debug("put key:"+data);
			}
		}
		/**
		 * 
		 * xml 스케줄 리스트 반환
		 * 
		 * @return scheduelList(xml)
		 */
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

	/**
	 * 
	 * 정렬을 위한 클래스
	 * 정렬 기준 : 날짜
	 * 
	 * @author 박창현
	 *
	 */
	class MySortElement implements Comparable<Object>
	{
		public String tagPort;
		// 날짜
		public String date; 
		// 회사
		public String company; 
		// 항구 
		public String port;

		/**
		 * 항구 코드 반환
		 * 
		 * @return
		 */
		public String getPortCode()
		{
			return getPortCodeInfo(port);
		}
		/**
		 * 항구 코드 반환
		 * @return
		 */
		public String getTagedPort()
		{
			return getTagedPortCode(port);
		}
		
		public int compareTo(Object o) {
			/*
			 * 날짜 비교 정렬
			 */
			
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
	/**
	 * 
	 * 항구 코드 출력
	 * 
	 * @param port
	 * @return
	 */
	private String getPortCodeInfo(String port)
	{
		String fport=portMap.get(port);

		//*****		return fport!=null?"["+fport+"]":null;
		return fport!=null?"[====="+fport+"=====]":null;

	}
	/**
	 * 
	 * xml 파일 생성
	 * 
	 * @param document
	 * @throws IOException 
	 * @throws ParseException 
	 */
	private void createXMLFile(Document document) throws ParseException, IOException {


		Format format = Format.getPrettyFormat();

		format.setEncoding("EUC-KR");			
		format.setIndent("\r\n");
		format.setIndent("\t");


		XMLOutputter outputter = new XMLOutputter(format);		
		
		FileWriter writer = new FileWriter(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+inbound_source_filename);
		outputter.output(document, writer);
		writer.close();

		printXTG(document);



	}

	/**
	 * 
	 * xtg 파일 출력
	 * 
	 * @param document
	 * @throws ParseException
	 * @throws IOException
	 */
	private void printXTG(Document document) throws ParseException, IOException {

		logger.info("파일 출력");

		StringBuffer buffer = new StringBuffer();

		buffer.append(TAG_VERSION0+"\r\n"+TAG_VERSION2+"\r\n"+TAG_VERSION3+"\r\n"+TAG_VERSION6+"\r\n");

		Element root = document.getRootElement();

		List schedule_list=root.getChildren("schedule-row");

		this.lengthOfTask=schedule_list.size();

		current=0;

		/*
		 * 결과 생성
		 */
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

		// 파일 출력
		xtgmanager.createXTGFile(buffer.toString(),INBOUND_PRINT_TXT);
	}
	
	/**
	 * 
	 * xtg 파일 출력
	 * 
	 * @param document
	 * @throws ParseException
	 * @throws IOException
	 */
	private void printXTG2(Document document) throws ParseException, IOException {

		logger.info("파일 출력");

		StringBuffer buffer = new StringBuffer();

		buffer.append(TAG_VERSION0+"\r\n"+TAG_VERSION2+"\r\n"+TAG_VERSION3+"\r\n"+TAG_VERSION6+"\r\n");

		Element root = document.getRootElement();

		List schedule_list=root.getChildren("schedule-row");

		this.lengthOfTask=schedule_list.size();

		current=0;


		/*
		 * 결과 생성
		 */
		for(int i=0;i<schedule_list.size();i++)
		{
			Element schedule_row = (Element) schedule_list.get(i);

			buffer.append((i!=0?"<ct:><cf:><cs:><cs:8.000000><cf:Helvetica LT Std><ct:Bold>":"<ct:><cf:><cs:><cs:8.000000><cf:Helvetica LT Std><ct:Bold>")+schedule_row.getAttributeValue("name")+" , "+schedule_row.getAttributeValue("nationality"));		
			buffer.append("\r\n\r\n");
			List dataLi=schedule_row.getChildren("data");
			for(int j=0;j<dataLi.size();j++)
			{
				Element data = (Element) dataLi.get(j);

				//buffer.append("<ct:><cf:><cs:><cs:6.500000><cf:Helvetica LT Std><ct:Roman>"+KSGDateUtil.format2(KSGDateUtil.toDate4(data.getAttributeValue("dateF")))+"\t<ct:><cf:><cf:Helvetica Neue LT Std>"+BOLD_TAG_F+ data.getAttributeValue("vessel")+BOLD_TAG_B+"<cf:>");
				
				String strDateF = KSGDateUtil.format2(KSGDateUtil.toDate4(data.getAttributeValue("dateF")));
				String strVessel = data.getAttributeValue("vessel");
				String company =data.getAttributeValue("company");

				buffer.append(String.format("<ct:><cf:><cs:><cs:6.500000><cf:Helvetica LT Std><ct:Roman>%s\t<ct:><cf:><cf:Helvetica Neue LT Std><ct:77 Bold Condensed>%s<ct:><cf:>", strDateF, strVessel));				
				buffer.append(String.format("<cf:Helvetica LT Std><ct:Roman>  (%s)", company));

				buffer.append(data.getAttributeValue("tag-common")+"\r\n");
			}

			buffer.append((i<schedule_list.size()-1?"\r\n":""));
			current++;
		}		

		// 파일 출력
		xtgmanager.createXTGFile(buffer.toString(),INBOUND_PRINT_TXT);
	}
	
	public static void xtgFormat()
	{	
		System.out.println(String.format("<ct:><cf:><cs:><cs:8.000000><cf:Helvetica LT Std><ct:Bold>%s , %s", "c","b"));
		System.out.println(String.format("<ct:><cf:><cs:><cs:6.500000><cf:Helvetica LT Std><ct:Roman>%s \t<ct:><cf:><cf:Helvetica Neue LT Std>, %s", "c","b"));
		System.out.println(String.format("<cf:Helvetica LT Std><ct:Roman>  (%s)", "c"));
		
	}
	
	public static void main(String[] args) {
		InboundScheduleJoint.xtgFormat();

	}


	/**
	 * 
	 * 항차 정보 표현 클래스
	 * 
	 * @author 박창현
	 *
	 */
	class VoaygeVector extends Vector<Element>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// 항차번호
		private String voyage;

		// 항차번호(숫자)
		private int voyage_n;

		/**
		 * @return 항차번호
		 */
		public String getVoyage() {
			return voyage;
		}

		/**
		 * 항차 번호 할당
		 * @param voyage
		 */
		public void setVoyage(String voyage) {
			this.voyage = voyage;
			voyage_n = ScheduleBuildUtil.getNumericVoyage(voyage);
		}
		/**
		 * @return 항차번호(숫자)
		 */
		public int getNumericVoayge()
		{
			return voyage_n;
		}
	}
	public int execute() throws Exception {
		logger.info("START");

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

					// 항구명 검색
					logger.debug(" from port 항구 검색: "+fromPort);
					
					if(fromPort.equals("BUSAN"))
					{
						System.exit(1);
					}
					port_abbr = scheduleManager.searchPort(fromPort);
					schedule_row = new Element(XML_INFO.XML_TAG_SCHEDULE_ROW);
					schedule_row.setAttribute(XML_INFO.XML_TAG_NAME,fromPort);
					schedule_row.setAttribute(XML_INFO.XML_TAG_NATIONALITY,port_abbr.getPort_nationality());
					rootElement.addContent(schedule_row);
				}

				// 항구명이 없을 경우
				catch(PortNullException e)
				{

					//TODO 인바운드 출력시 국내항 표시 오류
					try{
						// 항구 약어 검색
						port_abbr=scheduleManager.searchPortAbbr(fromPort);


						if(!scheduleService.isInPort(port_abbr.getPort_name()))
						{
							PortInfo portInfo2=baseService.getPortInfoByPortName(port_abbr.getPort_name());

							schedule_row = new Element(XML_INFO.XML_TAG_SCHEDULE_ROW);
							schedule_row.setAttribute(XML_INFO.XML_TAG_NAME,portInfo2.getPort_name());
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
						//항구 약어도 없을 경우 예외 반환
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
			logger.info("END("+(System.currentTimeMillis()-startTime)+"s)");
			

		}
		catch(PortNullException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "등록된 항구가 없습니다.-"+e.getPortName());
		}
		
		return ScheduleJoint.SUCCESS;

	}
}
