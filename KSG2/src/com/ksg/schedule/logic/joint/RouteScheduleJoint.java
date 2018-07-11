package com.ksg.schedule.logic.joint;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.VesselNullException;
import com.ksg.schedule.logic.route.GroupArea;
import com.ksg.schedule.logic.route.GroupPort;
import com.ksg.schedule.logic.route.GroupVessel;
import com.ksg.schedule.logic.route.PortDateUtil;
import com.ksg.schedule.logic.route.PortScheduleInfo;

/**
 * @author 박창현
 * @적용 룰 목록
 *   1. 외국항이 3개 이상인 경우에만 스케줄 출력
 *   2. 마지막 국내항 일자가 옵션 날짜 보다 큰 경우
 *   3. 정렬 기준
 *      - ORDER_BY_VESSEL:	지역-> 선박명	->	날짜		->	국내항	-> 외국항
 *      - ORDER_BY_DATE:	지역-> 날짜	->	선박명	->	국내항	-> 외국항
 *   4. 항구 그룹시
 *      - 국내항 : 늦은 날짜 기준
 *      - 국제항 : 
 *       
 *   GroupArea
 *   GroupVessel
 *   GroupInOutPort    
 *        
 */
public class RouteScheduleJoint extends DefaultScheduleJoint{


	private static final String RUSSIA = "Russia";
	private static final String JAPAN = "Japan";
	private static final String CHINA = "China";
	ScheduleManager scheduleManager = ScheduleManager.getInstance();
	/**
	 * @설명 최상위 그룹
	 * @author 박창현
	 *
	 */
	class GroupSchedule extends HashMap<String, GroupArea>
	{		
		private static final long serialVersionUID = 1L;

		public void addScheule(ScheduleData data) throws SQLException, ParseException, VesselNullException
		{
			String key =data.getArea_name().toLowerCase();

			if(this.containsKey(key))
			{
				// 기존 지역 추가
				GroupArea group = this.get(key);
				// 키 : 지역명
				group.addSchedule(data);

			}else
			{
				// 신규 그룹 생성
				// 키 : 지역명
				this.put(key, new GroupArea(data,op,orderByType));
			}
		}

		public GroupArea[] toSortedArray() {
			Set<String>keylist=keySet();

			ArrayList<GroupArea> newList = new ArrayList<GroupArea>();

			Iterator<String> iter=keylist.iterator();

			while(iter.hasNext())	
			{
				newList.add(get(iter.next()));
			}

			GroupArea lit[] = new GroupArea[newList.size()];
			lit = newList.toArray(lit);
			Arrays.sort(lit);

			return lit;
		}

	}
	public static final int ORDER_BY_DATE=1;

	public static final int ORDER_BY_VESSEL=2;

	private String fileName="world_print_new_date2.txt";

	private String errorOutPortfileName="world_print_error_outport.txt";

	private ShippersTable op;

	private int orderByType=1;

	private String WORLD_B="";

	private String WORLD_E="";

	private String WORLD_F="";

	private String WORLD_INPORT="";

	private String WORLD_OUTPORT="";

	private String WORLD_VERSION1="";

	private String WORLD_VERSION2="";

	private String WORLD_VERSION3="";

	private String commonInPortfileName="world_print_common_inport.txt";;

	List<ScheduleData> li;

	private List<String> areaList;

	public RouteScheduleJoint(ShippersTable op) throws SQLException {
		super();
		this.op = op;
	}

	public RouteScheduleJoint(ShippersTable op, int orderBy) throws SQLException {

		this(op);

		this.orderByType =orderBy;

		areaList=scheduleService.getOutboundAreaList();		

		lengthOfTask = areaList.size();

		message = "항로별 생성중...";

		logger.info("정렬기준:"+orderByType);
	}
	public void initTag() {

		WORLD_F="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon가변 윤고딕100\\_TT>▲<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		WORLD_B="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon가변 윤고딕100\\_TT>▲<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		WORLD_VERSION1="<KSC5601-WIN>\r\n<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1><60.100.0.0.:COLOR:CMYK:Process:0.6,1,0,0><30.60.0.0.:COLOR:CMYK:Process:0.3,0.6,0,0>>";
		WORLD_INPORT="<cc:><ct:><cs:><cf:><cc:30.60.0.0.><ct:Roman><cs:6.000000><cbs:-1.000000><cf:Helvetica LT Std>";
		WORLD_OUTPORT="<cc:><ct:><cs:><cbs:><cf:><ct:Roman><cs:6.000000><cf:Helvetica LT Std>";
		WORLD_VERSION2="<pstyle:><ct:Bold><chs:0.900000><cl:8.000000><cf:Helvetica LT Std>";
		WORLD_VERSION3="<pstyle:정규><pli:182.500000><pfli:-182.000000><psa:0.566894><ptr:96.37789916992188\\,Left\\,.\\,0\\,\\;201\\,Left\\,.\\,0\\,\\;><chs:0.800003><cl:20.000000><cs:18.000000><cf:Helvetica LT Std>\r\n<cl:><cl:20.099990>\r\n<cs:><ct:Bold><cs:18.000000>";
		WORLD_E="<ct:><cs:><cf:><ct:Bold><cf:Helvetica LT Std>";
	}



	private FileWriter fw,errorOutfw,commonInfw;


	public int execute() {
		logger.info("항로별 스케줄 생성 시작");		



		message = "항로별 스케줄 그룹화..";

		logger.info("스케줄 그룹화 시작");




		logger.info("스케줄 그룹화 종료");

		try{
			fw = new FileWriter(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName);

			errorOutfw = new FileWriter(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+errorOutPortfileName);

			commonInfw = new FileWriter(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+commonInPortfileName);


			// 출력 프로세스
			// toPort 그룹에서 키 셋(도착항) 조회


			// 태그 정보 출력

			fw.write(WORLD_VERSION1+"\r\n"+WORLD_VERSION2);



			Iterator<String> areaIter = areaList.iterator();
			int i=0;
			while(areaIter.hasNext())
			{
				String area = areaIter.next();
				ScheduleData searchOp = new ScheduleData();
				searchOp.setInOutType("O");
				searchOp.setArea_name(area);
				List<ScheduleData> outboundScheduleListByArea =scheduleService.getScheduleList(searchOp);
				GroupArea group = new GroupArea(area, op, orderByType);
				Iterator<ScheduleData> scheduleIter =outboundScheduleListByArea.iterator();
				while(scheduleIter.hasNext())
				{
					ScheduleData data=scheduleIter.next();
					try {
					group.addSchedule(data);
					}catch(VesselNullException e)
					{
						logger.error("vessel null "+e.getMessage());
					}
				}

				String areaName = (i>0?"\r\n\r\n\r\n\r\n":"")+group.getArea_name()+"\r\n\r\n";
				i++;
				fw.write(areaName);

				errorOutfw.write(areaName);

				logger.info("Area: "+areaName+ ", 스케줄 사이즈:"+group.size());

				GroupVessel[] vesselList=group.toSortedArray();


				for(int j=0;j<vesselList.length;j++)
				{	
					/* 스케줄 제외 구분
					 * 중국, 일본; 2개 미만
					 * 러시아 1개미만
					 * 기타 3개 미만 */

					// 국내항 목록
					PortScheduleInfo[] inPortList = vesselList[j].getCompressInPortList();

					// 외국항 목목
					PortScheduleInfo[] outPortList = vesselList[j].getCompressOutPortList();

					PortScheduleInfo lastInPort = inPortList[inPortList.length-1];

					GroupPort temp = new GroupPort();

					for(int newIndex=0;newIndex<outPortList.length;newIndex++)
					{
						if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(outPortList[newIndex].getDate()), KSGDateUtil.toDate4(lastInPort.getDate()))<0)
						{
							temp.add(outPortList[newIndex]);
						}
					}

					PortScheduleInfo[] newOutPortList =temp.toPortArray();

					if(checkOutPort(group.getArea_name(),newOutPortList.length))
					{
						fw.write(toStringSchedule(vesselList[j],newOutPortList));

						logger.debug(toStringSchedule(vesselList[j],newOutPortList));
					}
					else
					{
						// 로그 저장
						logger.error("외국항 항구수 부족 제외:"+vesselList[j].getVessel_name()+","+vesselList[j].getCompany());

						PortScheduleInfo list[]=newOutPortList;

						StringBuffer buffer = new StringBuffer();

						for(int index=0;index<list.length;index++)
						{
							buffer.append(list[index].getPort()+" "+PortDateUtil.toPrintDate(list[index].getDate())+(index<list.length-1?" - ":""));

							buffer.append(list[index].getPort()+" "+PortDateUtil.toPrintDate(list[index].getDate())+(index<list.length-1?" - ":""));

						}
						errorOutfw.write(group.getArea_name()+",\t"+vesselList[j].getVessel_name()+",\t"+vesselList[j].getVoyage_num()+",\t"+vesselList[j].getCompany()+",\t"+buffer.toString()+"\r\n");
					}
				}
				ArrayList<GroupVessel> commonVesselList = group.getCommonVessel();

				commonInfw.write("\r\n\r\nArea:"+group.getArea_name()+", 공동배선스케줄 수: "+commonVesselList.size()+"\r\n\r\n");

				logger.error("area:"+group.getArea_name()+", 공동배선스케줄 수: "+commonVesselList.size());

				for(int commonIndex=0;commonIndex<commonVesselList.size();commonIndex++)
				{
					GroupVessel commonVessel = commonVesselList.get(commonIndex);

					PortScheduleInfo list[]=commonVessel.getOutPortList();

					StringBuffer buffer = new StringBuffer();

					for(int index=0;index<list.length;index++)
					{
						buffer.append(list[index].getPort()+" "+PortDateUtil.toPrintDate(list[index].getDate())+(index<list.length-1?" - ":""));
					}
					commonInfw.write("   "+commonVessel.getVessel_name()+",\t"+commonVessel.getVoyage_num()+",\t"+commonVessel.getCompany()+",\t"+buffer.toString()+"\r\n");
				}

				current++;
				fw.write(WORLD_E);

			}
			// 파일 닫기
			close();
			logger.info("항로별 스케줄 생성 종료");
			return ScheduleJoint.SUCCESS;
		}catch(Exception e)
		{

			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
			return ScheduleJoint.FAILURE;

		}
	}
	private void close() throws IOException
	{
		fw.close();
		errorOutfw.close();
		commonInfw.close();	
	}
	/**
	 * @param areaName
	 * @param outportCount
	 * @return
	 */
	private boolean checkOutPort(String areaName,int outportCount)
	{
		if(areaName.equals(CHINA.toUpperCase())||areaName.equals(JAPAN.toUpperCase()))
		{
			if(outportCount>=2)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		// 러시아
		else if(areaName.equals(RUSSIA.toUpperCase()))
		{
			if(outportCount>0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}					
		// 기타 지역
		else
		{
			if(outportCount>=3)
			{
				return true;
			}else
			{
				return false;
			}
		}
	}
	/**
	 * @param vessel
	 * @param outPortList
	 * @return
	 * @throws ParseException
	 */
	private String toStringSchedule(GroupVessel vessel,PortScheduleInfo[] outPortList) throws ParseException
	{		
		PortScheduleInfo[] inPortList = vessel.getCompressInPortList();
		return 	WORLD_F+vessel.getVessel_name()+" - "+vessel.getVoyage_num()+" (" + vessel.getCompany() + ")"+"\r\n"+
		WORLD_INPORT+toStringInPortInfo(inPortList)+"\r\n"+
		WORLD_OUTPORT+toStringOutPortInfo(inPortList[inPortList.length-1],outPortList)+"\r\n\r\n";
	}

	/**
	 * @param portList
	 * @return
	 * @throws ParseException
	 */
	private String toStringInPortInfo(PortScheduleInfo[] portList) throws ParseException
	{
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<portList.length;i++)
		{
			String printDate = PortDateUtil.toPrintDate(portList[i].getDate());
			buffer.append(portList[i].getPort().equals("PUSAN")?"BUSAN":portList[i].getPort()+" "+printDate+(i<(portList.length-1)?" - ":""));
		}

		return buffer.toString();
	}

	/**
	 * @param inPortLast
	 * @param portList
	 * @return
	 * @throws ParseException
	 */
	private String toStringOutPortInfo(PortScheduleInfo inPortLast,PortScheduleInfo[] portList) throws ParseException
	{
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<portList.length;i++)
		{
			// 인바운드 마지막 날짜보다 크면 스케줄 표시
			if(KSGDateUtil.daysDiff(KSGDateUtil.toDate4(portList[i].getDate()), KSGDateUtil.toDate4(inPortLast.getDate()))<0)
			{
				String printDate = PortDateUtil.toPrintDate(portList[i].getDate());

				buffer.append(portList[i].getPort().equals("PUSAN")?"BUSAN":portList[i].getPort()+" "+printDate+(i<(portList.length-1)?" - ":""));
			}
		}

		return buffer.toString();
	}


	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
