package com.ksg.schedule.logic.joint;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.SortUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.PortNullException;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.schedule.logic.VesselNullException;
import com.ksg.schedule.logic.joint.outbound.FromPortGroup;
import com.ksg.schedule.logic.joint.outbound.PrintItem;
import com.ksg.schedule.logic.joint.outbound.ToPortGroup;
import com.ksg.schedule.logic.joint.outbound.VesselGroup;

/**
 * 
 * @설명 아웃바운드 생성 모듈
 * @author archehyun
 *
 */
public class OutboundScheduleJointV2 extends DefaultScheduleJoint{

	private static final String PORT_NAME = "outbound_port.txt";

	private static final String FILE_NAME = "outbound_new.txt";

	private static final String ERROR_NAME = "outbound_error.txt";	

	private ScheduleData op;

	private ScheduleData data;

	private ArrayList<PrintItem> printList;

	private String 	BOLD_TAG_F="",
			BOLD_TAG_B="",
			TAG_VERSION0="",
			TAG_VERSION2="",
			TAG_VERSION3="",
			TAG_VERSION4="",
			TAG_VERSION5="";

	private boolean isApplyTag=true;// 태그 적용 여부

	private String errorFileName;

	private String portName;

	private List<String> outbounSchedulePortList;

	private FileWriter fw;

	private FileWriter errorfw;

	private FileWriter portfw;

	public OutboundScheduleJointV2() throws SQLException, IOException {

		super();

		logger.info("outbound build");

		fileName = fileLocation+"/"+FILE_NAME;

		errorFileName = fileLocation+"/"+ERROR_NAME;

		portName = fileLocation+"/"+PORT_NAME;

		fw = new FileWriter(fileName);

		errorfw = new FileWriter(errorFileName);

		portfw = new FileWriter(portName);

		printList = new ArrayList<PrintItem>();

		message = "Outbound 생성중...";

		logger.info("outbound 스케줄 생성 및 초기화:"+lengthOfTask);

	}

	public void initTag() {
		logger.info("태그정보 초기화");


		if(isApplyTag)
		{
			BOLD_TAG_B="<ct:>";

			BOLD_TAG_F="<ct:Bold Condensed>";

			TAG_VERSION0="<KSC5601-WIN>";

			TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<검정:COLOR:CMYK:Process:0,0,0,1>>";

			TAG_VERSION3="<dps:정규=<Nextstyle:정규><cc:검정><cs:8.000000><clig:0><cbs:-0.000000><phll:0><pli:53.858291><pfli:-53.858292><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";		

			TAG_VERSION4="<dps:Body Text=<BasedOn:정규><Nextstyle:Body Text><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

			TAG_VERSION5="<pstyle:Body Text><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cs:8.000000><cl:5.479995><cf:Helvetica LT Std><ct:Roman>\r\n";			
		}
	}
	/**
	 * @author archehyun
	 *
	 */
	class ScheduleGroup extends HashMap<String, ToPortGroup>
	{
		private static final long serialVersionUID = 1L;

		public void add(ScheduleData data) throws SQLException, VesselNullException
		{
			//try {
			if(this.containsKey(data.getPort()))
			{
				ToPortGroup group = this.get(data.getPort());
				// 키 : 출발항-출발일-선박명-도착일

				group.addSchedule(data);


			}else
			{
				// 신규 그룹 생성
				// 키 : 출발항-출발일-선박명-도착일
				this.put(data.getPort(), new ToPortGroup(data));
			}
			/*} catch (VesselNullException e) {
				logger.error("table-id:"+data.getTable_id()+",null vessel:"+e.getVesselName());
				e.printStackTrace();
			}*/
		}
	}

/*	*//**
	 * @author archehyun
	 *
	 *//*
	class ToPortGroup extends HashMap<String,FromPortGroup>
	{
		private String toPort;
		private String port_nationality;
		public String getPort_nationality() {
			return port_nationality;
		}

		public ToPortGroup(String toPortName) throws SQLException, PortNullException {
			this.toPort =toPortName;
			PortInfo info=scheduleManager.searchPort(toPortName);
			this.port_nationality = info.getPort_nationality();
		}

		public ToPortGroup(ScheduleData data) throws SQLException, VesselNullException {
			this.toPort =data.getPort();
			this.addSchedule(data);
		}
		public void addSchedule(ScheduleData data) throws SQLException, VesselNullException {

			
			 * 항구명이 부산 신항일 경우에는 부산항으로 이름 변경
			 
			PortInfo info=scheduleManager.searchPort(data.getFromPort());

			if(info.getPort_name().equals(DefaultScheduleJoint.BUSAN_NEW_PORT))
			{
				data.setFromPort(DefaultScheduleJoint.BUSAN);
			}				

			// 스케줄이 존재할 경우 추가
			if(this.containsKey(data.getFromPort()))
			{
				FromPortGroup group = this.get(data.getFromPort());
				// 키 : 출발항-출발일-선박명-도착일
				group.addSchedule(data);

			}else// 신규 그룹 생성
			{
				// 키 : 출발항-출발일-선박명-도착일				
				FromPortGroup group = new FromPortGroup(data);				
				this.put(group.getID(), group);
			}

		}
		public String toString()
		{
			return toPort;
		}

	}*/
	/**
	 * @설명 선박명이 같은 스케줄들의 그룹, 조건에 따라 공동배선 실시
	 * @author archehyun
	 *
	 *//*
	class VesselGroup extends  ArrayList<ScheduleData> implements Comparable<Object>
	{
		*//**
		 * 
		 *//*
		private static final long serialVersionUID = 1L;
		private int use;// 선박 사용 유무
		private ArrayList<String> companyList;
		private ArrayList<CompanyString> companyStringList;
		private String vessel;// 선박명
		private String dateF; // 출발일
		private String dateT; // 도착일
		private String company;  //선사
		private String commonDateT;  // 공동배선 도착일
		private String commonDateF;  // 공동배선 출발일
		private String agent; // 에이전트
		private boolean isGroupedFormerSchedule;


		public int isUse() {
			return use;
		}

		public void setUse(int use) {
			this.use = use;
		}
		private String vessel_type;
		public String getVessel_type() {
			return vessel_type;
		}

		public void setVessel_type(String vessel_type) {
			this.vessel_type = vessel_type;
		}

		*//**
		 * @설명 공동배선 생성여부를 결정
		 * @param itemFirst
		 * @param itemSecond
		 * @return
		 *//*
		private boolean isMakeCommonShipping(ScheduleData itemFirst,ScheduleData itemSecond)
		{
			if(itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // 선사명이 같고
					ScheduleBuildUtil.getNumericVoyage(itemFirst.getVoyage_num())==ScheduleBuildUtil.getNumericVoyage(itemSecond.getVoyage_num())&& //항차명이 같고
					KSGDateUtil.isThreeDayUnder(itemFirst.getDateF(),itemSecond.getDateF())) //3일 이내
			{
				return true;
			}
			else if(!itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // 선사명이 다르고
					ScheduleBuildUtil.getNumericVoyage(itemFirst.getVoyage_num())==ScheduleBuildUtil.getNumericVoyage(itemSecond.getVoyage_num())&& //항차명이 같고
					KSGDateUtil.isThreeDayUnder(itemFirst.getDateF(),itemSecond.getDateF())||//3일 이내 이거나
					KSGDateUtil.isSame(itemFirst.getDateF(),itemSecond.getDateF())// 출발일이 같다면
					) //3일 이내
			{
				return true;
			}
			else
			{
				return false;
			}
		}


		*//**
		 * @param commpany
		 *//*
		private void addCompany(String commpany)
		{
			boolean isAdd=true;

			for(int i=0;i<companyList.size();i++)
			{
				if(companyList.get(i).equals(commpany))
					isAdd=false;
			}

			if(isAdd)
			{
				companyList.add(commpany);
			}
		}
		*//**		 * 
		 * 대표 선사 지정
		 * @param company
		 * @param agent
		 * @throws Exception 
		 *//*
		private void addCompany(String company,String agent) throws CompanyNameNullExcption
		{
			boolean isAdd=true;

			for(int i=0;i<companyStringList.size();i++)
			{
				if(companyStringList.get(i).getCompanyName().equals(company))
					isAdd=false;
			}

			if(isAdd)
			{
				companyStringList.add(new CompanyString(company, agent));
			}
		}

		*//**
		 * @return
		 * @throws CompanyNameNullExcption 
		 *//*
		public ArrayList<PrintItem> getVesselList() throws CompanyNameNullExcption
		{
			HashMap<String, ArrayList<ScheduleData>> sulist = new HashMap<String, ArrayList<ScheduleData>>();

			ArrayList<PrintItem> list = new ArrayList<PrintItem>();
			//선박명 항차번호가 같은 스케줄끼리 그룹화
			for(int i=0;i<this.size();i++)
			{
				ScheduleData data = this.get(i);
				String vesselName = data.getVessel();
				int voyageNum = ScheduleBuildUtil.getNumericVoyage(data.getVoyage_num());

				if(sulist.containsKey(vesselName+"-"+voyageNum))
				{
					ArrayList<ScheduleData> sub = sulist.get(vesselName+"-"+voyageNum);
					sub.add(data);
				}
				else
				{
					ArrayList<ScheduleData> sub = new ArrayList<ScheduleData>();
					sub.add(data);					
					sulist.put(vesselName+"-"+voyageNum, sub);	
				}				
			}
			Set<String> keySet = sulist.keySet();
			Iterator<String> keyIter = keySet.iterator();
			while(keyIter.hasNext())
			{
				String key = keyIter.next();
				ArrayList<ScheduleData> datas=sulist.get(key);
				if(datas.size()==1)
				{
					ScheduleData data =datas.get(0);

					dateF = data.getDateF();
					dateT = data.getDateT();
					logger.debug("일반 스케줄:"+vessel+",datef:"+dateF+",dateT:"+dateT+","+data.getVoyage_num());
					company = data.getCompany_abbr();
					agent = data.getAgent();
					PrintItem item = new PrintItem(dateF, vessel, vessel_type, new CompanyString(company, agent), dateT);
					list.add(item);
				}
				else if(datas.size()>1)
				{
					logger.info("선박명:"+vessel+", 항차:"+datas.get(0).getVoyage_num()+ " 공동배선 적용:"+datas.size());
					// 전체 목록 출력
					logger.info(datas);
					Collections.sort(datas);


					isGroupedFormerSchedule=false;

					ScheduleData commonSchedule=null;					


					for(int i=0;i<datas.size()-1;i++)
					{
						// 비교 하기 위한 스케줄 조회
						ScheduleData itemFirst = datas.get(i);
						ScheduleData itemSecond = datas.get(i+1);


						// 공동배선 생성 여부 확인
						if(isMakeCommonShipping((isGroupedFormerSchedule?commonSchedule:itemFirst),itemSecond))					
						{	
							commonSchedule = itemSecond; // 공동배선으로 묶일 경우 임시 스케줄 클래스에 할당

							// 이전에 공동배선으로 묶였다면 공동배선 날짜와 비교
							if(isGroupedFormerSchedule)
							{
								commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), commonDateT);
								commonDateF =KSGDateUtil.upperDate(itemFirst.getDateF(), commonDateF);	
							}
							// 아니라면 항목간 비교
							else
							{
								commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), itemSecond.getDateT());								

								commonDateF =KSGDateUtil.upperDate(itemFirst.getDateF(), itemSecond.getDateF());
							}

							commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), commonDateT);
							commonDateF =KSGDateUtil.upperDate(itemFirst.getDateF(), commonDateF);
							
							this.addCompany(itemFirst.getCompany_abbr());							
							this.addCompany(itemSecond.getCompany_abbr());
							try {
													
							
							this.addCompany(itemFirst.getCompany_abbr(), itemFirst.getAgent());
							this.addCompany(itemSecond.getCompany_abbr(), itemSecond.getAgent());
							}catch(CompanyNameNullExcption excption)
							{
								this.addCompany(itemFirst.getCompany_abbr(), itemFirst.getCompany_abbr());
								this.addCompany(itemSecond.getCompany_abbr(), itemSecond.getCompany_abbr());
								logger.error("f:"+itemFirst.getCompany_abbr()+", "+itemFirst.getAgent());
								logger.error("s:"+itemFirst.getCompany_abbr()+", "+itemFirst.getAgent());
							}
							isGroupedFormerSchedule=true;// 공동배선으로 묶였는지 여부 저장

						}else
						{	
							logger.info("공동배선 아님");
							//이전에 공동배선으로 묶인 결과가 있는 경우
							if(isGroupedFormerSchedule)
							{
								commonSchedule.setDateT(commonDateT);
								commonSchedule.setDateF(commonDateF);
								logger.info("!공동배선:"+commonDateT);

								String company=null;
								if(this.getVessel_company()!=null)
								{								
									company=toCompanyString(new CompanyString(getVessel_company(), agent),companyStringList);
								}else
								{
									company=toCompanyString(companyStringList);
								}

								logger.info("공동배선:"+this.vessel+","+company);
								PrintItem item = new PrintItem(commonSchedule.getDateF(), commonSchedule.getVessel(), vessel_type, company,  commonSchedule.getDateT());
								list.add(item);
								companyStringList.clear();
								companyList.clear();

							}else //이전에 공동배선으로 묶인 결과가 없는 경우
							{
								logger.info("단독 스케줄 추가:" +itemFirst.getVessel() +", "+itemFirst.getDateF()+", "+itemFirst.getDateT());
								PrintItem item = new PrintItem(itemFirst.getDateF(), itemFirst.getVessel(), vessel_type, new CompanyString( itemFirst.getCompany_abbr(), itemFirst.getAgent()), itemFirst.getDateT());
								list.add(item);

							}
							isGroupedFormerSchedule=false;
						}

						// 마지막 스케줄 출력
						if(i==datas.size()-2)
						{
							PrintItem item;
							if(isGroupedFormerSchedule)
							{	
								String lastDateT = datas.get(datas.size()-1).getDateT();
								String lastDateF = datas.get(datas.size()-1).getDateF();

								commonDateT = KSGDateUtil.rowerDate(lastDateT, commonDateT);
								commonDateF =KSGDateUtil.upperDate(lastDateF, commonDateF);

								logger.info("마지막 공동배선 스케줄 출력:"+commonDateF+" - "+commonDateT);

								commonSchedule.setDateT(commonDateT);
								commonSchedule.setDateF(commonDateF);

								String company=null;;
								if(this.getVessel_company()!=null)
								{								
									company=toCompanyString(new CompanyString(getVessel_company(), agent),companyStringList);
								}else
								{
									company=toCompanyString(companyStringList);
								}
								item = new PrintItem(commonSchedule.getDateF(), commonSchedule.getVessel(), vessel_type, company, commonSchedule.getDateT());
								list.add(item);
								companyStringList.clear();
								companyList.clear();
							}else
							{
								item = new PrintItem(itemSecond.getDateF(), itemSecond.getVessel(), vessel_type, new CompanyString( itemSecond.getCompany_abbr(), itemSecond.getAgent()), itemSecond.getDateT());
								list.add(item);
								logger.info("마지막 스케줄 추가:"+item.dateF+", "+item.vessel_name +", "+item.dateT);
							}

						}
					}
					logger.info("공동배선 적용 끝\n\n");
				}
			}

			return list;
		}

		private String vessel_company;// 선박회사


		*//**
		 * @return
		 *//*
		public String getVessel_company() {
			return vessel_company;
		}

		*//**대표 선사 할당
		 * @param vessel_company
		 *//*
		public void setVessel_company(String vessel_company) {
			this.vessel_company = vessel_company;
		}

		public VesselGroup(ScheduleData data) throws SQLException, VesselNullException {
			this.vessel = data.getVessel();
			//대표 선사 검색

			Vessel searchedVessel =scheduleManager.searchVessel(data.getVessel());

			this.vessel_type = searchedVessel.getVessel_type()!=null?searchedVessel.getVessel_type():"";
			if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
			{
				this.setVessel_company(searchedVessel.getVessel_company());
			}
			companyList = new ArrayList<String>();
			companyStringList = new ArrayList<CompanyString>();
			this.use = searchedVessel.getVessel_use();
			this.add(data);

		}

		public String getID() {
			return vessel;
		}

		public int compareTo(Object o) {
			VesselGroup table1 =(VesselGroup) o;

			Date one = new Date(table1.getDateF());
			Date two = new Date(this.getDateF());

			return KSGDateUtil.daysDiff( one,two);

		}
		public String getDateF()
		{
			Collections.sort(this);
			ScheduleData data = this.get(this.size()-1);
			dateF = data.getDateF();

			return dateF;
		}
		*//**
		 * @param major_company
		 * @param companyStrings
		 * @return
		 *//*
		private String toCompanyString(CompanyString major_company,ArrayList<CompanyString> companyStrings)
		{
			ArrayList<CompanyString> new_company = new ArrayList<CompanyString>();
			for(int i=0;i<companyList.size();i++)
			{
				if(major_company.getCompanyName().compareToIgnoreCase(companyStrings.get(i).getCompanyName())!=0)
				{
					new_company.add(companyStrings.get(i));
				}
			}

			return major_company+","+toCompanyString(new_company);

		}		

		private String toCompanyString(ArrayList<CompanyString> companyStrings)
		{
			CompanyString[] companyArray=new CompanyString[companyStrings.size()];
			for(int i=0;i<companyStrings.size();i++)
			{
				companyArray[i] = companyStrings.get(i);
			}
			Arrays.sort(companyArray);
			String companyStringList ="";

			for(int i=0;i<companyArray.length;i++)
			{
				companyStringList+=companyArray[i];
				if(i!=companyArray.length-1)
					companyStringList+=",";
			}

			return companyStringList;
		}

		*//**
		 * 
		 * 대표 선사 있을 경우 대표 선사를 맨 처음 표시
		 * @param major_company
		 * @param companyList
		 * @return
		 *//*
		private String arrangedCompanyList(String major_company,
				ArrayList<String> companyList) {

			ArrayList<String> new_company = new ArrayList<String>();
			for(int i=0;i<companyList.size();i++)
			{
				if(major_company.compareToIgnoreCase(companyList.get(i))!=0)
				{
					new_company.add(companyList.get(i));
				}
			}
			logger.debug("major:"+major_company);

			return major_company+","+this.arrangedCompanyList(new_company);
		}

		*//**
		 * 
		 * @설명 선사 목록을 정리
		 * @param companyList
		 * @return
		 *//*
		private String arrangedCompanyList(ArrayList<String> companyList) {
			String[] companyArray=new String[companyList.size()];		

			companyList.toArray(companyArray);
			//알파벳 정렬
			Arrays.sort(companyArray, new StringCompare());

			String companyStringList ="";

			for(int i=0;i<companyArray.length;i++)
			{
				companyStringList+=companyArray[i];
				if(i!=companyArray.length-1)
					companyStringList+=",";
			}

			return companyStringList;
		}

	}*/

	/**
	 * @author 박창현
	 *
	 *//*
	class FromPortGroup extends HashMap<String,VesselGroup>
	{
		*//**
		 * 
		 *//*
		private static final long serialVersionUID = 1L;
		// 스케줄이 존재할 경우 추가
		private String fromPortName;

		public FromPortGroup(ScheduleData data) throws SQLException, VesselNullException {
			this.fromPortName =data.getFromPort();
			this.addSchedule(data);
		}
		public String getID() {
			// TODO Auto-generated method stub
			return fromPortName;
		}
		public void addSchedule(ScheduleData data) throws SQLException, VesselNullException {
			// 스케줄이 존재할 경우 추가
			if(this.containsKey(data.getVessel()))
			{
				VesselGroup group = this.get(data.getVessel());
				// 키 : 선박명
				group.add(data);

			}else// 신규 그룹 생성
			{
				// 키 : 출발항-출발일-선박명-도착일				
				VesselGroup group = new VesselGroup(data);				
				this.put(group.getID(), group);
			}

		}
		public String toString()
		{
			return "- "+fromPortName+" -";
		}

	}*/
/*	*//**
	 * 선사명 표시
	 * 규칙1 : 선사명과 agent명이 동일 할시 선사명 만 표시
	 * 
	 * @author 박창현
	 *
	 *//*
	class CompanyString implements Comparable<CompanyString>
	{
		private String companyName;
		private String agent;
		public CompanyString(String companyName, String agent) {
			this.companyName = companyName;
			this.agent = agent;			
		}

		public String getCompanyName() {
			// TODO Auto-generated method stub
			return companyName;
		}
		

		 
		 * 2020-04-07
		 * 대표 선사 표시 기능
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 
		public String toString()
		{
			
			return companyName.equals(agent)?companyName:companyName+agent!=null?("/"+agent):"";
		}

		@Override
		public int compareTo(CompanyString o) {

			return this.getCompanyName().compareTo(o.getCompanyName());
		}


	}*/

	/**출발항구 정렬
	 * @param outboundFromPortList
	 * @return
	 */
	private String[] arrangeFromPort(String[] outboundFromPortList) 
	{	
		logger.debug("from-port arrange");

		List<String> arragedFromPort = new LinkedList<String>();
		for(int i=0;i<fromPort.length;i++)
		{
			arragedFromPort.add(fromPort[i]);
		}

		List<String> fromPorts = new LinkedList<String>();
		for(int i=0;i<outboundFromPortList.length;i++)
		{
			fromPorts.add(outboundFromPortList[i]);
		}

		for(int i=0;i<fromPort.length;i++)
		{
			// 국내항 항목이 없으면
			if(!fromPorts.contains(fromPort[i]))
			{
				//정렬된 항국 목록 에서 제외
				arragedFromPort.remove(fromPort[i]);
			}
		}
		String[] newArray = new String[arragedFromPort.size()];
		for(int i=0;i<newArray.length;i++)
		{
			newArray[i] = arragedFromPort.get(i);
		}
		return newArray;
	}

	private boolean checkFromPort(String toPort)
	{
		for(int i=0;i<fromPort.length;i++)
		{
			if(fromPort[i].equals(toPort))
				return true;
		}
		return false;
	}
	@Override
	public int execute() throws IOException, ScheduleJointError {

		long startTime = System.currentTimeMillis();


		message = "아웃바운드 스케줄 그룹화...";


		/*정렬 기준
		 * 1. 도착항
		 * 2. 출발항
		 * 3. 출발일
		 * 4. 도착일 
		 */

		try{

			//도착항구명 목록 조회
			outbounSchedulePortList = scheduleService.getOutboundPortList();

			lengthOfTask = outbounSchedulePortList.size();

			Iterator<String> toPortIter = outbounSchedulePortList.iterator();

			fw.write(buildVersionXTG());

			int tagIndex=0;// 태그 적용을 위한 인덱스

			String toPort=null;


			//도창항구명 기준으로 스케줄 생성
			while(toPortIter.hasNext())
			{
				try{
					toPort = toPortIter.next();
					logger.info("");
					logger.info("도착항: "+toPort);

					if(checkFromPort(toPort))
						continue;

					portfw.write(toPort+"\n");
					op = new ScheduleData();
					op.setPort(toPort);
					op.setInOutType("O");

					// 도착항구명 기준으로 스케줄 조회
					List<ScheduleData> outboundScheduleListByToPort =scheduleService.getScheduleList(op);

					// 도착항구명 기준으로 그룹 생성
					ToPortGroup toPortgroup = new ToPortGroup(toPort);
					Iterator<ScheduleData> scheduleList = outboundScheduleListByToPort.iterator();
					while(scheduleList.hasNext())
					{
						try{
							data =scheduleList.next();
							toPortgroup.addSchedule(data);
						}catch(VesselNullException e)
						{
							logger.error("vessel null error table-id:"+data.getTable_id()+",vessel-name:"+data.getVessel());
							errorfw.write("vessel null error table-id:"+data.getTable_id()+",vessel-name:"+data.getVessel()+"\n");
							continue;
						}
					}

					fw.write(buildToPortXTG(tagIndex, toPortgroup.getToPort(), toPortgroup.getPort_nationality()));

					tagIndex++;

					// 도착항 기준 출발항 목록 정렬
					String[] fromPortArray = arrangeFromPort(toPortgroup.keySet().toArray(new String[toPortgroup.keySet().size()]));

					/*
					 * 출발항 기준으로 출력 시작
					 */

					for(int i=0;i<fromPortArray.length;i++)
					{
						FromPortGroup fromPortGroup =toPortgroup.get(fromPortArray[i]);						

						fw.write(buildFromXTG(i, fromPortGroup.getFromPortName()));

						String[] vesselArray = fromPortGroup.keySet().toArray(new String[fromPortGroup.keySet().size()]);

						//여기서 정렬하는 이유?
						SortUtil.bubbleSort(vesselArray);

						VesselGroup[] vesselArrays =new VesselGroup[vesselArray.length];

						for(int y=0;y<vesselArray.length;y++)
						{
							vesselArrays[y] =fromPortGroup.get(vesselArray[y]);
						}

						printList.clear();

						/*
						 * vesselgroup에서 공동배선 생성 및 정렬을 위한 목록 생성
						 */

						for(int y=0;y<vesselArrays.length;y++)
						{
							ArrayList<PrintItem> li = vesselArrays[y].getVesselList();

							Iterator<PrintItem> iterator = li.iterator();
							while(iterator.hasNext())
							{
								printList.add(iterator.next());
							}
						}

						// 출발일로 정렬
						PrintItem pr[]= new PrintItem[printList.size()];

						pr=printList.toArray(pr);

						Arrays.sort(pr);

						for(int pr_i=0;pr_i<pr.length;pr_i++)
						{
							fw.write(pr[pr_i].toString());
						}
					}
				}catch(PortNullException e)
				{
					logger.error("port null:"+toPort);
					errorfw.write("port null:"+toPort);
				}
				current++;

			}

			logger.info("outbound 스케줄 생성 종료("+(System.currentTimeMillis()-startTime)+")");

			return ScheduleBuild.SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			throw new ScheduleJointError(e,data);
			
		}
		finally
		{
			// 파일 닫기
			fw.close();
			errorfw.close();
			portfw.close();
		}
	}

	/**XTG 출력
	 * @return
	 */
	private String buildVersionXTG() {
		String buffer = TAG_VERSION0+"\r\n"+
				TAG_VERSION2+"\r\n"+
				TAG_VERSION3+"\r\n"+
				TAG_VERSION4+"\r\n"+
				TAG_VERSION5;
		return buffer;
	}

	/**
	 * @param i
	 * @param portName
	 * @param portNationality
	 * @return
	 */
	private String buildToPortXTG(int i, String portName,String portNationality) {

		if(isApplyTag)
		{
			return (i!=0?" \r\n<ct:><cs:><ct:Bold><cs:8.000000>":"<ct:><cs:><ct:Bold><cs:8.000000>")

					+portName +" , "+portNationality+" ";	
		}
		else
		{
			return (i!=0?" \r\n":"")+portName +" , "+portNationality+" ";	
		}

	}
	private String buildFromXTG(int j, String fromPort) {
		if(isApplyTag)
		{
			return (j==0?" \r\n \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ":" \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ")+fromPort+" -\r\n";
		}
		else
		{
			return (j==0?" \r\n \r\n- ":" \r\n- ")+fromPort+" -\r\n";
		}
	}

	
	/**
	 * @설명 최하위 그룹 요소.. 날짜로 정렬
	 * @author archehyun
	 *
	 *//*
	class PrintItem implements Comparable<PrintItem>
	{	
		private String vessel_name;
		private String vessel_type;

		private String dateF;
		private String dateT;
		private String company;
		private CompanyString companyString;
		public PrintItem(String dateF, String vessel_name,String vessel_type, CompanyString companyString, String dateT) {
			this.vessel_name = vessel_name;			
			this.vessel_type = vessel_type;
			this.dateF = dateF;
			this.dateT= dateT;
			this.companyString = companyString;

		}
		public PrintItem(String dateF, String vessel_name,String vessel_type, String company,String dateT) {
			this.vessel_name = vessel_name;			
			this.vessel_type = vessel_type;
			this.dateF = dateF;
			this.dateT= dateT;
			this.company= company;
		}




		@Override
		public int compareTo(PrintItem o) {

			PrintItem table1 = (PrintItem) o;
			Date fromOne = new Date(table1.dateF);
			Date fromTwo = new Date(this.dateF);

			// 정렬 기준 : 출발일->도착일

			if(KSGDateUtil.isSame(table1.dateF, this.dateF))
			{
				//출발일이 같은 경우 도착일 비교 
				Date toOne = new Date(table1.dateT);
				Date toTwo = new Date(this.dateT);

				return KSGDateUtil.daysDiff( toOne,toTwo);
			}
			else
			{
				return KSGDateUtil.daysDiff( fromOne,fromTwo);	
			}

		}	


		public String toString()
		{
			// 
			try {

				String formatedDateF=outputDateFormat.format(inputDateFormat.parse(dateF));
				String formatedDateT=outputDateFormat.format(inputDateFormat.parse(dateT));
				return buildVesselXTG(formatedDateF, vessel_name, companyString==null?company:companyString.toString(), vessel_type, formatedDateT);
			} catch (ParseException e)
			{

				e.printStackTrace();
				return "date error";
			}

		}

		private String buildVesselXTG(String dateF, String vesselname, String company,	String vessel_type, String dateT) {
			if(isApplyTag)
			{
				return "<ct:><cs:><ct:Roman><cs:6.000000>"+dateF+"<ct:>"+BOLD_TAG_F+"\t"+vesselname+BOLD_TAG_B+"<ct:Roman>"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n";
			}
			else
			{
				return dateF+"\t"+vesselname+"\t"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n";


			}
		}
	
	}*/
	
}
