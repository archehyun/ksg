package com.ksg.commands.schedule.outbound;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.JDOMException;

import com.ksg.commands.schedule.StringCompare;
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.commands.schedule.task.SimpleTask;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.Code;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;

/**
 * 
 * @설명 아웃바운드 생성 모듈
 * @author archehyun
 *
 */
public class OutboundTaskV2 extends SimpleTask{

	// 날짜 형식 yyyy-mm-dd;
	private String 	BOLD_TAG_F="",
			BOLD_TAG_B="",
			TAG_VERSION0="",
			TAG_VERSION2="",
			TAG_VERSION3="",
			TAG_VERSION4="",
			TAG_VERSION5="";

	private boolean isApplyTag=false;
	private String[] fromPort;
	private String fileName;
	public OutboundTaskV2() {
		fileName = KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+"outbound_new.txt";
	}

	/* (non-Javadoc)
	 * @see com.ksg.commands.schedule.task.SimpleTask#makeSchedule()
	 */
	public void makeSchedule() throws SQLException, JDOMException, IOException,
	ParseException {

		List<ScheduleData> outbounScheduleList=scheduleService.getOutboundScheduleList();
		ScheduleGroup scheduleGroup = new ScheduleGroup();
		Iterator<ScheduleData> iter = outbounScheduleList.iterator();

		// 스케줄 그룹화
		while(iter.hasNext())
		{
			ScheduleData item = iter.next();
			scheduleGroup.add(item);
		}
		
		/*정렬 기준
		 * 1. 도착항
		 * 2. 출발항
		 * 3. 출발일
		 * 4. 도착일 
		 */



		String[] toPortkeyArray = scheduleGroup.keySet().toArray(new String[scheduleGroup.keySet().size()]);

		// 도착항 기준으로 정렬
		bubbleSort(toPortkeyArray);

		FileWriter fw = new FileWriter(fileName);
		for(int toPortIndex=0;toPortIndex<toPortkeyArray.length;toPortIndex++)
		{
			ToPortGroup toPortgroup = scheduleGroup.get(toPortkeyArray[toPortIndex]);
			logger.info("\r\n"+toPortgroup+"\r\n");
			fw.write("\r\n"+toPortgroup+"\r\n\r\n");
			String[] fromPortArray = toPortgroup.keySet().toArray(new String[toPortgroup.keySet().size()]);

			// 출발항 기준으로 정렬
			bubbleSort(fromPortArray);			
			for(int i=0;i<fromPortArray.length;i++)
			{
				FromPortGroup fromPortGroup =toPortgroup.get(fromPortArray[i]);
				logger.info("\t"+fromPortGroup);
				fw.write("\t"+fromPortGroup+"\r\n\r\n");
				String[] vesselArray = fromPortGroup.keySet().toArray(new String[fromPortGroup.keySet().size()]);
				bubbleSort(vesselArray);

				VesselGroup[] vesselArrays =new VesselGroup[vesselArray.length];
				for(int y=0;y<vesselArray.length;y++)
				{
					vesselArrays[y] =fromPortGroup.get(vesselArray[y]);
				}
				
				// 선박 그룹 정렬
				// 출발일로 정렬
				Arrays.sort(vesselArrays);

				for(int y=0;y<vesselArrays.length;y++)
				{
					logger.debug(vesselArrays[y].toCommonShippingString());
					fw.write(vesselArrays[y].toCommonShippingString());
				}
			}
		}
		// 파일 닫기
		fw.close();
	}

	protected void initTag() {
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

		try {
			Code param = new Code();
			param.setCode_type(XML_INFO.XML_TAG_FROM_PORT);
			List<Code> li = baseService.getCodeInfoList(param);

			fromPort = new String[li.size()];
			for(int i=0;i<li.size();i++)
			{
				Code info = li.get(i);
				fromPort[i] =info.getCode_name();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	class ScheduleGroup extends HashMap<String, ToPortGroup>
	{
		private static final long serialVersionUID = 1L;

		public void add(ScheduleData data) throws SQLException
		{
			if(this.containsKey(data.getPort()))
			{
				ToPortGroup group = this.get(data.getPort());
				// 키 : 출발항-출발일-선박명-도착일
				group.addSchedule(data);

			}else
			{
				// 신규 그룹 생성
				// 키 : 출발항-출발일-선박명-도착일
				//logger.debug("toPort create:"+data.getPort());
				this.put(data.getPort(), new ToPortGroup(data));
			}
		}
	}

	class ToPortGroup extends HashMap<String,FromPortGroup>
	{
		private String toPort;

		public ToPortGroup(ScheduleData data) throws SQLException {
			this.toPort =data.getPort();
			this.addSchedule(data);
		}
		public void addSchedule(ScheduleData data) throws SQLException {
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

	}
	/**
	 * @설명 선박명이 같은 스케줄들의 그룹, 조건에 따라 공동배선 실시
	 * @author archehyun
	 *
	 */
	class VesselGroup extends  ArrayList<ScheduleData> implements Comparable<Object>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int use;// 선박 사용 유무
		ArrayList<String> companyList;
		private String vessel;// 선박명
		private String dateF; // 출발일
		private String dateT; // 도착일
		private String commpany;  //선사
		private String commonDateT;  // 공동배선 출발일
		private String agent; // 에이전트
		boolean isGroupedFormerSchedule;
		private SimpleDateFormat outputFormat = new SimpleDateFormat("M/d");
		private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
		
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
		
		/**
		 * @rule1 선사명과 에이전트 명이 같을 경우에는 선사명만 출력
		 * @rule2 공동배선으로 묶였을 경우 태표 선사
		 * @param dateF
		 * @param vessel
		 * @param company
		 * @param agent
		 * @param dateT
		 * @return
		 */
		private String toShippingString(String dateF, String vessel, String company, String agent, String dateT)
		{
			// 
			try {

				String formatedDateF=outputFormat.format(inputFormat.parse(dateF));
				String formatedDateT=outputFormat.format(inputFormat.parse(dateT));
				String formatedCompany = company.equals(agent)?company:company+"/"+agent;
				return  "\t"+formatedDateF+
						"\t"+vessel+
						"\t"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+
						"("+formatedCompany+")"+
						"\t"+formatedDateT+"\r\n";
			} catch (ParseException e) {

				e.printStackTrace();
				return "date error";
			}

		}
		private String toShippingString(ScheduleData data,ArrayList<String> companyList)
		{
			String company=null;
			if(this.getVessel_company()!=null)
			{

				company=arrangedCompanyList(this.getVessel_company(),companyList);
			}else
			{
				company=arrangedCompanyList(companyList);
			}

			return this.toShippingString(data.getDateF(), data.getVessel(), company, data.getAgent(), data.getDateT());
		}
		private String toShippingString(ScheduleData data)
		{
			return this.toShippingString(data.getDateF(), data.getVessel(), data.getCompany_abbr(), data.getAgent(), data.getDateT());
		}

		/**@설명 문자로된 항차번호에서 숫자만 추출하는 메소드
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
					temp+=Integer.parseInt(String.valueOf(voyage_num.charAt(i)));
				}catch(NumberFormatException e)
				{

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

		/**
		 * @설명 공동배선 생성여부를 결정
		 * @param itemFirst
		 * @param itemSecond
		 * @return
		 */
		private boolean isMakeCommonShipping(ScheduleData itemFirst,ScheduleData itemSecond)
		{
			if(itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // 선사명이 같고
					this.getNumericVoyage(itemFirst.getVoyage_num())==this.getNumericVoyage(itemSecond.getVoyage_num())&& //항차명이 같고
					KSGDateUtil.isThreeDayUnder(itemFirst.getDateF(),itemSecond.getDateF())) //3일 이내
			{
				return true;
			}
			else if(!itemFirst.getCompany_abbr().equals(itemSecond.getCompany_abbr())&& // 선사명이 다르고
					this.getNumericVoyage(itemFirst.getVoyage_num())==this.getNumericVoyage(itemSecond.getVoyage_num())&& //항차명이 같고
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
		@SuppressWarnings("unchecked")
		private String toCommonShippingString()
		{
			if(this.size()==1)
			{
				ScheduleData data =this.get(0);
				dateF = data.getDateF();
				dateT = data.getDateT();
				commpany = data.getCompany_abbr();
				agent = data.getAgent();
				return toShippingString(dateF,vessel,commpany,agent,dateT);
			}
			else
			{				
				Collections.sort(this);
				String list="";
				boolean isCommonThree=false;

				isGroupedFormerSchedule=false;
				ScheduleData commonSchedule=null;

				ArrayList<String> lists = new ArrayList<String>();
				for(int i=0;i<this.size();i++)
				{
					ScheduleData itemFirst = this.get(i);
					logger.info("-"+this.toShippingString(itemFirst));
				}

				for(int i=0;i<this.size()-1;i++)
				{

					// 비교 하기 위한 스케줄 조회
					ScheduleData itemFirst = this.get(i);
					ScheduleData itemSecond = this.get(i+1);

					// 공동배선 생성 여부 확인
					if(isMakeCommonShipping((isGroupedFormerSchedule?commonSchedule:itemFirst),itemSecond))					
					{	
						commonSchedule = itemSecond; // 공동배선으로 묶일 경우 임시 스케줄 클래스에 할당
						isGroupedFormerSchedule=true;// 공동배선으로 묶였는지 여부 저장
						isCommonThree = true;// 공동배선 식별용
						commonDateT = KSGDateUtil.rowerDate(itemFirst.getDateT(), commonSchedule.getDateT());
						this.addCompany(itemSecond.getCompany_abbr());

					}else
					{	
						//이전에 공동배선으로 묶인 결과가 있는 경우
						if(isGroupedFormerSchedule)
						{
							commonSchedule.setDateT(commonDateT);

							lists.add((isCommonThree?"*":"")+this.toShippingString(commonSchedule,companyList));
							companyList.clear();

						}else //이전에 공동배선으로 묶인 결과가 없는 경우
						{
							lists.add(this.toShippingString(itemFirst));
						}
						isGroupedFormerSchedule=false;
					}

					// 마지막 스케줄 출력
					if(i==this.size()-2)
					{
						if(isGroupedFormerSchedule)
						{
							commonSchedule.setDateT(commonDateT);
							lists.add((isCommonThree?"*":"")+this.toShippingString(commonSchedule,companyList));
							companyList.clear();
						}else
						{
							lists.add(this.toShippingString(itemSecond));
						}
					}
				}
				for(int i=0;i<lists.size();i++)
				{
					if(lists.get(i).startsWith("*"))
						logger.info(lists.get(i));
					list+=lists.get(i);
				}

				return list;
			}
		}
		private String vessel_company;// 선박회사


		public String getVessel_company() {
			return vessel_company;
		}

		public void setVessel_company(String vessel_company) {
			this.vessel_company = vessel_company;
		}

		public VesselGroup(ScheduleData data) throws SQLException {
			this.vessel = data.getVessel();
			//대표 선사 검색
			Vessel op = new Vessel();
			op.setVessel_name(data.getVessel());

			Vessel searchedVessel = baseService.getVesselInfo(op);
			this.vessel_type = searchedVessel.getVessel_type()!=null?searchedVessel.getVessel_type():"";
			if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
			{
				this.setVessel_company(searchedVessel.getVessel_company());
			}
			companyList = new ArrayList<String>();
			this.use = searchedVessel.getVessel_use();
			this.add(data);
		}

		public String getID() {
			return vessel;
		}
		public String toString()
		{	
			return toCommonShippingString();
		}

		public int compareTo(Object o) {
			VesselGroup table1 =(VesselGroup) o;

			try 
			{
				return KSGDateUtil.daysDiff( KSGDateUtil.toDate4(table1.getDateF()),KSGDateUtil.toDate4(this.getDateF()));
			} catch (ParseException e) 
			{

				return -1;
			}
		}
		public String getDateF()
		{
			Collections.sort(this);
			ScheduleData data = this.get(this.size()-1);
			dateF = data.getDateF();

			return dateF;
		}
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

			return major_company+","+this.arrangedCompanyList(new_company);
		}

		private String arrangedCompanyList(ArrayList<String> companyList) {
			String[] array2=new String[companyList.size()];		

			companyList.toArray(array2);
			//알파벳 정렬
			Arrays.sort(array2, new StringCompare());

			String companyStringList ="";
			for(int i=0;i<array2.length;i++)
			{
				companyStringList+=array2[i];
				if(i!=array2.length-1)
					companyStringList+=",";
			}

			return companyStringList;
		}

	}
	class FromPortGroup extends HashMap<String,VesselGroup>
	{
		// 스케줄이 존재할 경우 추가
		private String fromPort;

		public FromPortGroup(ScheduleData data) throws SQLException {
			this.fromPort =data.getFromPort();
			this.addSchedule(data);
		}
		public String getID() {
			// TODO Auto-generated method stub
			return fromPort;
		}
		public void addSchedule(ScheduleData data) throws SQLException {
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
			return "- "+fromPort+" -";
		}

	}



	public static void main(String[] args) throws SQLException, JDOMException, IOException, ParseException {
		new OutboundTaskV2().makeSchedule();

	}



}
