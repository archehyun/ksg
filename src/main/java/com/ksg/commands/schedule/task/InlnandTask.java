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

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

/**
 * 2014-11-20 업데이트
 * @author 박창현
 *
 */
public class InlnandTask extends SimpleTask{
	private String TAG_VERSION;
	private String TAG_DOCUMENT_INFO_1;
	private String TAG_DOCUMENT_INFO_2;
	private String TAG_HEAD_TO_PORT;
	private String TAG_BODY_TO_PORT;
	private String TAG_BODY_FROM_PORT;
	private String TAG_BODY_AGENT_HEAD;
	private String TAG_BODY_AGENT_DATE;
	private String TAG_BODY_AGENT_BODY;

	private ScheduleData option; // 콘솔 스케줄 조회 옵션 저장

	private ScheduleBuildMessageDialog di;

	/**
	 *날짜 형식 월/일 
	 */
	private SimpleDateFormat dateFormat = new SimpleDateFormat("M/d");

	private String fileName,portFileName;

	public InlnandTask(ScheduleData op) {
		super();
		this.option=op;
		try {
			// 생성 파일 이름 지정
			fileName = KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+"inland.txt";

			portFileName= KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+"inland_port.txt";
			
			initTag();
			
			di = new ScheduleBuildMessageDialog (this);
			
			di.setMessage("Inland");
			
			di.createAndUpdateUI();

			makeSchedule();
			
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "생성 완료");
			
		} catch (Exception e) {
			
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "생성 실패");
			
			e.printStackTrace();
		}
		finally
		{
			
//			di.end();
			done=true;
			di.setVisible(false);
			di.dispose();
		}


	}
	/* (non-Javadoc)
	 * @see com.ksg.commands.schedule.task.SimpleTask#initTag()
	 */
	protected void initTag() {
		logger.debug("디자인 태그 초기화");
		TAG_VERSION="<KSC5601-WIN>";
		TAG_DOCUMENT_INFO_1="<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1>>";
		TAG_DOCUMENT_INFO_2="<dps:NormalParagraphStyle=<Nextstyle:NormalParagraphStyle>>";
		TAG_HEAD_TO_PORT ="<pstyle:NormalParagraphStyle><ptr:19.84251968503937\\,Left\\,.\\,0\\,\\;115.99937007874017\\,Right\\,.\\,0\\,\\;126.00000000000001\\,Left\\,.\\,0\\,\\;211.33417322834646\\,Right\\,.\\,0\\,\\;><cl:6.500000><ct:Bold><cs:8.000000><cf:Helvetica LT Std>";
		TAG_BODY_TO_PORT ="<ct:><cs:><cf:><ct:Bold><cs:8.000000><cf:Helvetica LT Std>";
		TAG_BODY_FROM_PORT 	= "<ct:><cs:><cf:><ct:Bold><cs:6.300000><cf:Helvetica LT Std>";
		TAG_BODY_AGENT_HEAD = "<ct:><cs:><cf:><cs:6.000000><cf:Helvetica LT Std><ct:Roman>";
		TAG_BODY_AGENT_DATE = "<ct:><ct:Roman>";
		TAG_BODY_AGENT_BODY = "<ct:><ct:Bold Condensed>";
	}




	public void makeSchedule() throws SQLException, JDOMException, IOException {

		// 아웃 바운드 항구 목록 조회
		logger.info("내륙운송 스케줄 생성 시작");
		// 콜솔 스케줄 조회
		List<ScheduleData> scheduleli = scheduleService.getInlandScheduleList(option);


		/*
		 * 스케줄 그룹화
		 */
		lengthOfTask = scheduleli.size();

		logger.info("내류운송 스케줄 그룹화 : {}건",lengthOfTask);

		for(int i=0;i<scheduleli.size();i++)
		{
			ScheduleData data = scheduleli.get(i);
			logger.debug(data);
		}

		ToPortGroup toPortGroupList = new ToPortGroup();

		Iterator<ScheduleData> iter = scheduleli.iterator();

		while(iter.hasNext())
		{
			ScheduleData item = iter.next();
			toPortGroupList.add(item);
		}	
		/*
		 * 파일 출력 
		 */

		FileWriter fw = new FileWriter(fileName);

		FileWriter portFw = new FileWriter(portFileName);
		// 출력 프로세스
		// toPort 그룹에서 키 셋(도착항) 조회


		// 태그 정보 출력
		fw.write(TAG_VERSION+"\r\n"+TAG_DOCUMENT_INFO_1+"\r\n"+TAG_DOCUMENT_INFO_2+"\r\n");

		String[] toPortkeyArray = toPortGroupList.keySet().toArray(new String[toPortGroupList.keySet().size()]);

		// 도착항 키값 정렬
		bubbleSort(toPortkeyArray);	

		for(int toPortIndex=0;toPortIndex<toPortkeyArray.length;toPortIndex++)
		{
			logger.debug("toPort:"+toPortkeyArray[toPortIndex]);
			portFw.write(toPortkeyArray[toPortIndex]+"\r\n");
			PortInfo info=baseService.getPortInfo(toPortkeyArray[toPortIndex]);
			// 오류 체크
			if(info==null)// 존재하지 않는 항구 일 경우 패스
			{
				logger.error(toPortkeyArray[toPortIndex]+":"+info);
				continue;
			}
			fw.write("\r\n"+(toPortIndex==0?TAG_HEAD_TO_PORT:TAG_BODY_TO_PORT)+toPortkeyArray[toPortIndex]+" , "+info.getPort_nationality() + "\r\n\r\n");

			// 도착항 기분으로 출발항 그룹 조회
			FromPortGroup group = toPortGroupList.get(toPortkeyArray[toPortIndex]);

			// 키값을 이용하여 정렬 수행
			String[] keyArray = group.keySet().toArray(new String[group.keySet().size()]);

			bubbleSort(keyArray);

			current++;

			for(int i=0;i<keyArray.length;i++)
			{
				//각 에이전트 출력

				ScheduleGroup subGroup =group.get(keyArray[i]);				

				fw.write(subGroup.toScheduleInfo()+"\r\n"+subGroup.toStringAgentList());			

				logger.debug("fromPort Key:"+keyArray[i]);
				logger.debug(subGroup.toScheduleInfo());
				logger.debug(subGroup.toStringAgentList());
			}
		}		
		// 파일 닫기
		fw.close();
		portFw.close();
		
		logger.info("내륙운송 스케줄 생성 종료");
	}

	/**
	 * 내륙 도시 그룹
	 * 키 : 내륙 도시
	 * @author 박창현
	 * @설명 
	 *
	 */
	class ToPortGroup extends HashMap<String, FromPortGroup>
	{
		private static final long serialVersionUID = 1L;
		public ToPortGroup() {
			logger.debug("toPort group 생성");
		}
		public void add(ScheduleData data)
		{
			if(this.containsKey(data.getPort()))
			{
				FromPortGroup group = this.get(data.getPort());
				// 키 : 도착항
				group.addSchedule(data);

			}else
			{
				// 신규 그룹 생성
				// 키 : 도착항
				this.put(data.getPort(), new FromPortGroup(data));
			}
		}
	}

	/**
	 * @설명 도착항 그룹 클래스
	 * 키: 스케줄 그룹(출발항-중간기항지)
	 * @author archehyun
	 *
	 */
	class FromPortGroup extends HashMap<String,ScheduleGroup>
	{
		private static final long serialVersionUID = 1L;
		public FromPortGroup(ScheduleData data) {

			addSchedule(data);

		}
		public void addSchedule(ScheduleData data)
		{	
			// 스케줄이 존재할 경우 추가
			if(this.containsKey(data.getFromPort()+"\r\n"+data.getInland_port()))
			{
				ScheduleGroup group = this.get(data.getFromPort()+"\r\n"+data.getInland_port());
				// 키 : 출발항-출발일-선박명-도착일
				group.addSchedule(data);

			}else// 신규 그룹 생성
			{	
				// 키 : 국내항-중간기항지				
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
	class ScheduleGroup extends ArrayList<InlandSchedule>
	{
		private static final long serialVersionUID = 1L;

		/**초기화 생성자, 스케줄 데이터를 인자로 출발항, 출발일, 도착일, 선박명을 초기화한 후 에이전트를 추가함
		 * @param data
		 */
		public ScheduleGroup(ScheduleData data) {
			this.setFromPort(data.getFromPort());			
			this.setInlnad_port(data.getInland_port());
			this.addSchedule(data);

		}

		/**
		 * @param data
		 */
		public void addSchedule(ScheduleData data) {
			InlandSchedule inlandData = new InlandSchedule();
			inlandData.setDateF(data.getDateF());
			inlandData.setDateT(data.getDateT());
			inlandData.setVessel(data.getVessel());
			inlandData.setAgent(data.getAgent());
			inlandData.setInland_date(data.getInland_date());
			this.add(inlandData);

		}

		// 그룹화 키 지정 생성: 출발항-중간 기항지
		public String getID()
		{
			return getFromPort()+"\r\n"+getInlnad_port();
		}
		private String fromPort;// 출발항		
		private String inlnad_port;// 중간 기항지



		public String getInlnad_port() {
			return inlnad_port;
		}

		public void setInlnad_port(String inlnad_port) {
			this.inlnad_port = inlnad_port;
		}

		public String getFromPort() {
			return fromPort;
		}

		public void setFromPort(String fromPort) {
			this.fromPort = fromPort;
		}	

		/**
		 * @설명 에이전트 리스트를 스트링 형태로 생성 하는 메소드
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public String toStringAgentList()
		{
			StringBuffer buffer = new StringBuffer();
			Collections.sort(this);

			for(int i=0;i<this.size();i++)
			{
				ScheduleData data = (ScheduleData) this.get(i);
				buffer.append(this.toStringAgentInfoByTag(	data.getDateF(),
						data.getVessel(),
						data.getInland_date(),
						data.getAgent(),
						data.getDateT())+"\r\n");
			}	
			return buffer.toString();
		}

		/**
		 * @param dateF 		출발일
		 * @param vessel 		선박명
		 * @param inland_date 	기항일자
		 * @param agent 		에이전트
		 * @param dateT			도착일
		 * @return
		 */
		private String toStringAgentInfoByTag(String dateF, String vessel, String inland_date,String agent, String dateT)		
		{
			return TAG_BODY_AGENT_HEAD+dateFormat.format(new Date(dateF))+"\t"+TAG_BODY_AGENT_BODY+vessel+"\t"+TAG_BODY_AGENT_DATE+dateFormat.format(new Date(inland_date))+"\t"+TAG_BODY_AGENT_BODY+agent+"\t"+TAG_BODY_AGENT_DATE+dateFormat.format(new Date(dateT));
		}
		/**출력 값 출발항, 출발인, 선박명, 도착일
		 * @return
		 * @throws SQLException 
		 */
		public String toScheduleInfo() throws SQLException
		{
			return toStringSchedueInfoByTag(this.getFromPort(),this.getInlnad_port());			
		}

		/**
		 * @param fromPort	출발항
		 * @param inlnadPort	기항지
		 * @return
		 * @throws SQLException
		 */
		private String toStringSchedueInfoByTag(String fromPort,String inlnadPort) throws SQLException
		{
			return TAG_BODY_FROM_PORT+"["+fromPort+" - "+inlnadPort+"]";

		}

	}


	/**
	 * 정렬 객체
	 * @author archehyun
	 *
	 */
	class InlandSchedule extends ScheduleData
	{

		Calendar nowF = Calendar.getInstance();
		Calendar oldF = Calendar.getInstance();
		Calendar nowT = Calendar.getInstance();
		Calendar oldT = Calendar.getInstance();
		Calendar nowInland = Calendar.getInstance();
		Calendar oldInland = Calendar.getInstance();

		SimpleDateFormat inputformat = new SimpleDateFormat("yyyy/MM/dd");


		public int compareTo(Object o) {
			ScheduleData table1 =(ScheduleData) o;


			String dateF1 =this.getDateF();
			String dateF2 =table1.getDateF();

			String dateT1 =this.getDateT();
			String dateT2 =table1.getDateT();

			String inland1 = this.getInland_date();
			String inland2 = table1.getInland_date();

			try {
				nowF.setTime(inputformat.parse(dateF1));
				oldF.setTime(inputformat.parse(dateF2));
				nowT.setTime(inputformat.parse(dateT1));
				oldT.setTime(inputformat.parse(dateT2));
				nowInland.setTime(inputformat.parse(inland1));
				oldInland.setTime(inputformat.parse(inland2));

				//출발일 비교
				if(oldF.compareTo(nowF)>0)
				{	
					return -1;
				}
				else if(oldF.compareTo(nowF)==0)
				{

					// 도착일 비교
					if(oldT.compareTo(nowT)>0)
					{
						return -1;	
					}					
					else if(oldT.compareTo(nowT)==0)
					{
						// 기항일 비교
						if(nowInland.compareTo(oldInland)>0)
						{
							return -1;
						}
						else
						{
							return 1;
						}		
					}
					else
					{
						return 1;
					}

				}
				else
				{
					return 1;
				}



			} catch (ParseException e) {

				e.printStackTrace();
				return -1;
			}
		}
	}
}

