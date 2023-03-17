package com.ksg.commands.schedule.task;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.jdom.JDOMException;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ScheduleType;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;

import com.ksg.domain.Vessel;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

/**
 * 2014-11-20 업데이트
 * @author 박창현
 *
 */
public class ConsoleTask extends SimpleTask{
	/**
	 * @설명 도착항 그룹 클래스
	 * 키: 스케줄 그룹(출발항-출발일-선박명-도착일)
	 * @author archehyun
	 *
	 */
	class FromPortGroup extends HashMap<String,ScheduleGroup>
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public FromPortGroup(ScheduleData data) {

			addSchedule(data);

		}
		public void addSchedule(ScheduleData data)
		{	
			// 스케줄이 존재할 경우 추가
			if(this.containsKey(data.getFromPort()+"\r\n"+data.getDateF()+"\r\n"+data.getVessel()+"\r\n"+data.getDateT()))
			{
				ScheduleGroup group = this.get(data.getFromPort()+"\r\n"+data.getDateF()+"\r\n"+data.getVessel()+"\r\n"+data.getDateT());
				// 키 : 출발항-출발일-선박명-도착일
				group.addSchedule(data);

			}else// 신규 그룹 생성
			{	

				// 키 : 출발항-출발일-선박명-도착일				
				ScheduleGroup group = new ScheduleGroup(data);				
				this.put(group.getID(), group);
			}
		}
	}
	/**
	 * @설명 출발항 기준 에이전트 리스트 그룹화 클래스
	 * @author archehyun
	 *
	 */
	class ScheduleGroup extends ArrayList<SortScheduleData> 
	{

		private static final long serialVersionUID = 1L;

		private String dateF,dateT,fromPort,vessel;	// 출발일, 도착일, 출발항,  선박명

		/**초기화 생성자, 스케줄 데이터를 인자로 출발항, 출발일, 도착일, 선박명을 초기화한 후 에이전트를 추가함
		 * @param data
		 */
		public ScheduleGroup(ScheduleData data) {
			this.setFromPort(data.getFromPort());
			this.setDateF(data.getDateF());
			this.setVessel(data.getVessel());
			this.setDateT(data.getDateT());
			this.addSchedule(data);
			
			
		}
		public void addSchedule(ScheduleData data)
		{
			this.add(new SortScheduleData(data));
		}
		/**@설명 클로징 시간 정보중 월/일 정보만 표시 
		 * @param time 클로징 타임
		 * @return
		 */
		private String getClosingTime(String time) throws ArrayIndexOutOfBoundsException,NumberFormatException
		{	
			logger.debug("closing time parsing:"+time);


			// TBN 확인		
			String newTime = time.trim();
			String tbnList[]={"TBN", "T.B.N", "T.B.N.", "T B N", "-TBN-","Tbn","tbn"};
			for(int i=0;i<tbnList.length;i++)
			{
				if(newTime.equals(tbnList[i]))
					return "TBN";
			}

			// '/'이 없는 경우 그냥 출력
			if(!time.contains("/"))
			{
				return time;
			}

			String[] timeGroup=time.split("/");
			String month =timeGroup[0];
			String dayString=timeGroup[1];
			char dayGroup[] = dayString.toCharArray();
			try{
				int  first= Integer.parseInt(String.valueOf(dayGroup[0]));
				int second= Integer.parseInt(String.valueOf(dayGroup[1]));
			}
			catch(NumberFormatException ee)
			{
				return Integer.parseInt(month)+"/"+ String.valueOf(dayGroup[0]);
			}
			catch(ArrayIndexOutOfBoundsException eee)
			{
				return Integer.parseInt(month)+"/"+ String.valueOf(dayGroup[0]);
			}

			return 	Integer.parseInt(month)+"/"+
			((String.valueOf(dayGroup[0]).equals("0"))?"":String.valueOf(dayGroup[0]))
			+dayGroup[1];

		}

		public String getDateF() {
			return dateF;
		}
		public String getDateT() {
			return dateT;
		}
		public String getFromPort() {
			return fromPort;
		}
		// 그룹화 키 지정 생성: 출발항-출발일-선박-도착일
		public String getID()
		{
			return getFromPort()+"\r\n"+getDateF()+"\r\n"+getVessel()+"\r\n"+getDateT();
		}
		public String getVessel() {
			return vessel;
		}
		
		public void setDateF(String dateF) {
			this.dateF = dateF;
		}
		
		public void setDateT(String dateT) {
			this.dateT = dateT;
		}

		public void setFromPort(String fromPort) {
			this.fromPort = fromPort;
		}
		public void setVessel(String vessel) {
			this.vessel = vessel;
		}
		/**출력 값 출발항, 출발인, 선박명, 도착일
		 * @return
		 * @throws SQLException 
		 */
		public String toScheduleInfo() throws SQLException
		{
			return toStringSchedueInfoByTag(portMap.get(getFromPort()), dateFormat.format(new Date(getDateF())), getVessel(), dateFormat.format(new Date(getDateT())));			
		}
		/**
		 * @설명 에이전트 리스트 문자 구성
		 * @param comapny_abbr 			선사 약어
		 * @param d_time 				Document 클로징
		 * @param c_time 				Cargo 클로징 
		 * @param console_info 			콘솔정보(Page, CFS)
		 * @param console_print_type 	콘솔 출력 타입 0:Page, 1:CFS
		 * @출력형식 선사 약어 	도큐먼트 클로징	카고 클로징 	[Page] 콘솔정보
		 * @return
		 */
		private String toStringAgentInfoByTag(String comapny_abbr, String d_time, String c_time,String console_info, int console_print_type) throws ArrayIndexOutOfBoundsException,NumberFormatException		
		{
			return TAG_BODY_AGENT+"\t"+comapny_abbr+"\t"+getClosingTime(d_time)+"\t"+getClosingTime(c_time)+"\t"+(console_print_type==ScheduleType.CONSOLE_PAGE?"[Page] ":"")+console_info+TAG_BODY_AGENT_CLOSE;
		}

		/**
		 * @설명 에이전트 리스트를 스트링 형태로 생성 하는 메소드
		 * @return
		 */
		public String toStringAgentList()
		{
			StringBuffer buffer = new StringBuffer();
			Collections.sort(this);
			for(int i=0;i<this.size();i++)
			{
				ScheduleData data = this.get(i).getData();
				try{
					buffer.append(this.toStringAgentInfoByTag(	data.getCompany_abbr(),
							data.getD_time(),
							data.getC_time(),
							op.getConsole_print_type()==ScheduleType.CONSOLE_PAGE?data.getConsole_page():data.getConsole_cfs(),
									op.getConsole_print_type())+"\r\n");
				}catch(ArrayIndexOutOfBoundsException e)
				{
					logger.error("closing time index error:id:"+data.getTable_id()+","+data.getD_time()+","+data.getC_time());
				}	
				catch(NumberFormatException e)
				{
					logger.error("closing time number format error:id:"+data.getTable_id()+","+data.getD_time()+","+data.getC_time());
				}	
			}	
			return buffer.toString();
		}
		/**
		 * @설명 태그를 적용하여 출력
		 * @param fromPort 		출발항
		 * @param dateF 		출발일자
		 * @param vessel_abbr 	선박명 약어
		 * @param dateT 		도착일
		 * @출력형식 [출발항] 출발일	[VSL] 선박명약어 [선종]	[ETA] 도착일
		 * @return
		 * @throws SQLException 
		 */
		private String toStringSchedueInfoByTag(String fromPort, String dateF, String vessel_abbr, String dateT) throws SQLException
		{
			Vessel op = new Vessel();
			op.setVessel_name(vessel_abbr);
			Vessel  vesselInfo = baseService.getVesselInfo(op);

			String vesseltype=vesselInfo!=null?vesselInfo.getVessel_type():"error";
			// 공백일 경우 괄로 제거
			String vessel = vesseltype.equals("")?"":"["+vesseltype+"]";

			return TAG_BODY_FROM_PORT+"["+fromPort+"] "+dateF+"\t"+vessel_abbr+" "+vessel+"\t[ETA] "+dateT;
		}		
	}
	class SortScheduleData implements Comparable<SortScheduleData>
	{
		ScheduleData data;
		public ScheduleData getData() {
			return data;
		}
		public void setData(ScheduleData data) {
			this.data = data;
		}
		public SortScheduleData(ScheduleData data) {
			this.data=data;
		}
		public String toString()
		{
			return data.getAgent();
		}
		public int compareTo(SortScheduleData o) {
			return this.getData().getAgent().toUpperCase().compareTo(o.getData().getAgent().toUpperCase());
		}
	}
	/**
	 * 도착항 그룹
	 * 키 : toPort
	 * @author 박창현
	 * @설명 도착항 그룹 클래스, 도착항을 key로 하는 해쉬맵 형태 객체 value 값음 출발항 그룹 객체를 저장함
	 *
	 */
	class ToPortGroup extends HashMap<String, FromPortGroup>
	{

		private static final long serialVersionUID = 1L;

		public void add(ScheduleData data)
		{
			if(this.containsKey(data.getPort()))
			{
				FromPortGroup group = this.get(data.getPort());
				// 키 : 출발항-출발일-선박명-도착일
				group.addSchedule(data);

			}else
			{
				// 신규 그룹 생성
				// 키 : 출발항-출발일-선박명-도착일
				
				this.put(data.getPort(), new FromPortGroup(data));
			}
		}
	}


	private Calendar dateIssue;
	
	private SimpleDateFormat dateIssueformat 	= new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat inputDateformat 	= new SimpleDateFormat("yyyy/MM/dd");
	private SimpleDateFormat dateFormat 		= new SimpleDateFormat("M/d");
	private ScheduleBuildMessageDialog di;
	private String fileName;
	private DAOManager manager =DAOManager.getInstance();
	private ScheduleData op; // 콘솔 스케줄 조회 옵션 저장
	private String TAG_BODY_AGENT;
	private String TAG_BODY_AGENT_CLOSE;
	private String TAG_BODY_FROM_PORT;
	private String TAG_BODY_TO_PORT;
	private String TAG_DOCUMENT_INFO_1;
	private String TAG_DOCUMENT_INFO_2;
	private String TAG_HEAD_TO_PORT;
	private final String TAG_VERSION="<KSC5601-WIN>";

	public ConsoleTask(ScheduleData op) {
		super();
		this.op=op;
		try {

			// 생성 파일 이름 지정
			fileName = KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+"console.txt";

			dateIssue = Calendar.getInstance();
			dateIssue.setTime(dateIssueformat.parse(op.getDate_issue()));

			initTag();
			makeSchedule();
			JOptionPane.showMessageDialog(null, "생성 완료");

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "생성 실패");

		}
		finally
		{
			done=true;
			di.setVisible(false);
			di.dispose();
		}
	}


	protected void initTag() {
		logger.debug("디자인 태그 초기화");
		TAG_BODY_AGENT ="<ct:><cs:><chs:><cl:><cf:><ptr:><ptr:8\\,Left\\,.\\,0\\,\\;63\\,Left\\,.\\,0\\,\\;86\\,Left\\,.\\,0\\,\\;107\\,Left\\,.\\,0\\,\\;><cc:C\\=0 M\\=0 Y\\=0 K\\=60><ct:30><cs:6.000000><chs:0.900000><cl:8.000000><cf:Yoon가변 윤고딕100\\_TT>";
		TAG_BODY_AGENT_CLOSE="<cc:>";
		TAG_BODY_FROM_PORT ="<ct:><cs:><chs:><cl:><cf:><ptr:><ptr:44\\,Left\\,.\\,0\\,\\;107\\,Left\\,.\\,0\\,\\;><ct:Regular><cs:6.000000><chs:0.900000><cl:8.000000><cf:Rix정굴림 B>";		
		TAG_BODY_TO_PORT ="<ct:><cs:><chs:><cl:><cf:><ptr:><ptr:8\\,Left\\,.\\,0\\,\\;63\\,Left\\,.\\,0\\,\\;86\\,Left\\,.\\,0\\,\\;107\\,Left\\,.\\,0\\,\\;><ct:Bold><cs:8.000000><cl:8.000000><cf:Helvetica LT Std>";
		TAG_DOCUMENT_INFO_1="<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1><C\\=0 M\\=0 Y\\=0 K\\=60:COLOR:CMYK:Process:0,0,0,0.6>>";
		TAG_DOCUMENT_INFO_2="<dps:NormalParagraphStyle=<Nextstyle:NormalParagraphStyle>>";		
		TAG_HEAD_TO_PORT ="<pstyle:NormalParagraphStyle><ptr:8\\,Left\\,.\\,0\\,\\;63\\,Left\\,.\\,0\\,\\;86\\,Left\\,.\\,0\\,\\;107\\,Left\\,.\\,0\\,\\;><ct:Bold><cs:8.000000><cl:8.000000><cf:Helvetica LT Std>";
	}
	/**
	 * @설명 날짜 비교  주어진 인자가 기준 일자 보다 이전 인지 비교 
	 * (ex1: dateIssue:3.1, dateF:3.2=>false)
	 * (ex2: dateIssue:3.1, dateF:3.1=>false)
	 * (ex3: dateIssue:3.2, dateF:3.1=>true)
	 * @param dateF 기준 일자
	 * @return
	 * @throws ParseException
	 */
	private boolean isAfterThenDateIssue(String dateF) throws ParseException
	{		
		Calendar dateFCal = Calendar.getInstance();
		dateFCal.setTime(inputDateformat.parse(dateF));

		return dateIssue.after(dateFCal);

	}
	/*	public static void main(String[] args) throws ParseException 
	{
		SimpleDateFormat inputDateformat 	= new SimpleDateFormat("yyyy/MM/dd");
		Calendar dateFCal = Calendar.getInstance();
		dateFCal.setTime(inputDateformat.parse("2015/03/02"));

		Calendar dateIssue;
		dateIssue = Calendar.getInstance();
		SimpleDateFormat dateIssueformat 	= new SimpleDateFormat("yyyy-MM-dd");
		dateIssue.setTime(dateIssueformat.parse("2015-03-02"));

		System.out.println(dateIssue.after(dateFCal));
	}*/



	/* (non-Javadoc)
	 * @see com.ksg.commands.schedule.task.ScheduleTask#makeSchedule()
	 * @ 스케줄 생성 메소드
	 */
	public void makeSchedule() throws SQLException, JDOMException, IOException {

		// 아웃 바운드 항구 목록 조회
		logger.debug("콘솔 스케줄 생성 시작");

		// 콜솔 스케줄 조회
		
		
		//List<ScheduleData> scheduleli = scheduleService.getConsoleScheduleList(op);
		
		op.setGubun(ScheduleType.GUBUN_CONSOLE);
		
		op.setOrderby("port, fromPort, DateF,DateT");
		
		List<ScheduleData> scheduleli = scheduleService.getScheduleList(op);
		
		logger.debug("콘솔 스케줄 조회:"+scheduleli.size()+"건");

		lengthOfTask = scheduleli.size();
		di = new ScheduleBuildMessageDialog (this);
		di.setMessage("Console");
		di.createAndUpdateUI();
		logger.debug("date_issue:"+op.getDate_issue());


		for(int i=0;i<scheduleli.size();i++)
		{

			ScheduleData data = scheduleli.get(i);
			try {
				logger.debug(data.getDateF()+","+dateIssueformat.format(inputDateformat.parse(data.getDateF()))+","+isAfterThenDateIssue(data.getDateF()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}		

		ToPortGroup toPortGroupList = new ToPortGroup();
		Iterator<ScheduleData> iter = scheduleli.iterator();

		/* 스케줄 조회 및 정렬
		 * 조회된 스케줄을  ToPort 그룹에 추가 
		 */
		while(iter.hasNext())
		{
			ScheduleData item = iter.next();
			try {
				// 기준 일자 이후 스케줄 만 스케줄 처리
				if(!isAfterThenDateIssue(item.getDateF()))
				{
					toPortGroupList.add(item);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}	

		FileWriter fw = new FileWriter(fileName);


		// 출력 프로세스
		// toPort 그룹에서 키 셋(도착항) 조회


		// 태그 정보 출력
		fw.write(TAG_VERSION+"\r\n");
		fw.write(TAG_DOCUMENT_INFO_1+"\r\n");
		fw.write(TAG_DOCUMENT_INFO_2+"\r\n");

		String[] toPortkeyArray = toPortGroupList.keySet().toArray(new String[toPortGroupList.keySet().size()]);

		// 도착항 키값 정렬
		bubbleSort(toPortkeyArray);
		for(int toPortIndex=0;toPortIndex<toPortkeyArray.length;toPortIndex++)
		{
			logger.debug("toPort:"+toPortkeyArray[toPortIndex]);
			PortInfo info=baseService.getPortInfo(toPortkeyArray[toPortIndex]);
			// 오류 체크
			if(info==null)// 존재하지 않는 항구 일 경우 패스
			{
				logger.error(toPortkeyArray[toPortIndex]+":"+info);
				continue;
			}
			fw.write("\r\n"+(toPortIndex==0?TAG_HEAD_TO_PORT:TAG_BODY_TO_PORT)+toPortkeyArray[toPortIndex]+" , "+info.getPort_nationality() + "\r\n\r\n");

			// 도착항 기준으로 출발항 그룹 조회
			FromPortGroup group = toPortGroupList.get(toPortkeyArray[toPortIndex]);

			// 날짜값을 이용하여 정렬 수행
			String[] keyArray = group.keySet().toArray(new String[group.keySet().size()]);
			bubbleSortByDate(keyArray);			
			current++;
			for(int i=0;i<keyArray.length;i++)
			{
				//각 에이전트 출력
				logger.debug("fromPort Key:"+keyArray[i]);
				ScheduleGroup subGroup =group.get(keyArray[i]);
				logger.debug(subGroup.toScheduleInfo());
				fw.write(subGroup.toScheduleInfo()+"\r\n");
				logger.debug(subGroup.toStringAgentList());
				fw.write(subGroup.toStringAgentList());
			}
		}		

		// 파일 닫기
		fw.close();
	}
	private void bubbleSortByDate(String[] intArray) {
		int out, in;
		for (out = intArray.length - 1; out > 0; out--) {
			for (in = 0; in < out; in++) {
				try{
					String[] oneDateList = intArray[in].split("\r\n");
					String[] twoDateList = intArray[in+1].split("\r\n");

					String onePort = oneDateList[0];
					String twoPort = twoDateList[0];
					String oneDateF =oneDateList[1];
					String twoDateF =twoDateList[1];

					String oneDateT =oneDateList[3];
					String twoDateT =twoDateList[3];


					// 출발항 비교
					if (onePort.compareTo(twoPort)>0) {
						swap(intArray, in, in + 1);
					}
					else if (onePort.compareTo(twoPort)==0)
					{				
						// 출발일 비교
						if(KSGDateUtil.biggerDate(oneDateF, twoDateF).equals(oneDateF)&&oneDateF.compareTo(twoDateF)!=0)
						{

							swap(intArray, in, in + 1);
						}
						else if(KSGDateUtil.biggerDate(oneDateF, twoDateF).equals(twoDateF))
						{
							if(oneDateF.compareTo(twoDateF)==0)
							{
								try{
									//도착일 비교
									if(KSGDateUtil.biggerDate(oneDateT, twoDateT).equals(oneDateT))
									{

										swap(intArray, in, in + 1);
									}
								}
								catch(Exception e)
								{

									e.printStackTrace();
									System.err.println("error one:"+intArray[in]+",two:"+ intArray[in+1]);
								}
							}
						}
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.err.println("error one:"+intArray[in]+",two:"+ intArray[in+1]);
				}
			}
		}
	}
}

