package com.ksg.schedule.logic.print.route;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.SchedulePrint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.route.GroupArea;
import com.ksg.schedule.logic.route.GroupVessel;
import com.ksg.schedule.logic.route.PortDateUtil;
import com.ksg.schedule.logic.route.PortScheduleInfo;
import com.ksg.schedule.logic.route.RouteScheduleUtil;

import lombok.extern.slf4j.Slf4j;


/**

 * @FileName : RouteScheduleJoint.java

 * @Date : 2021. 4. 29. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :
 *  적용 룰 목록
	    1. 외국항이 3개 이상인 경우에만 스케줄 출력
	    2. 마지막 국내항 일자가 옵션 날짜 보다 큰 경우
	    3. 정렬 기준
	       - ORDER_BY_VESSEL:	지역-> 선박명	->	날짜		->	국내항	-> 외국항
	       - ORDER_BY_DATE:	지역-> 날짜	->	선박명	->	국내항	-> 외국항
	    4. 항구 그룹시
	       - 국내항 : 늦은 날짜 기준
	       - 국제항 : 

	    GroupArea
	    GroupVessel
	    GroupInOutPort

 */
@Slf4j
public class RouteScheduleJointV1 extends AbstractRouteSchedulePrint{

	/**
	 * @설명 최상위 그룹
	 * @author 박창현
	 *
	 */
	class GroupSchedule extends HashMap<String, GroupArea>
	{		
		private static final long serialVersionUID = 1L;

		public void addScheule(ScheduleData data) throws Exception
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
				this.put(key, new GroupArea(data,orderByType));
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

	
	private List<ScheduleData> scheduleList;
	
	private int orderByType=1;
	
	private String date_issue;
	
	private int totalCount;
	
	public RouteScheduleJointV1(int orderBy) throws Exception {
		super();
		
		this.orderByType =orderBy;
		
		message = "항로별 생성중...";
		
	}
	
	public RouteScheduleJointV1(String date_issue, int orderBy) throws Exception {
		this(orderBy);
		
		this.date_issue = date_issue;
	}
	
	public RouteScheduleJointV1(List<ScheduleData> scheduleList, int orderBy) throws Exception {

		this(orderBy);
		
		this.scheduleList = scheduleList;
	}
	

	public RouteScheduleJointV1(ShippersTable op, int orderBy) throws Exception {

		super(op);
		
		this.date_issue  = KSGDateUtil.format(KSGDateUtil.toDate2(op.getDate_isusse()));
		
		this.orderByType =orderBy;
		
		CommandMap param = new CommandMap();

		param.put("date_issue", this.date_issue);

		param.put("inOutType", OUTBOUND);

		// 스케줄 목록 조회
		this.scheduleList= scheduleService.selecteScheduleListByCondition(param);

		message = "항로별 생성중...";

		logger.info("정렬기준:"+orderByType);
	}
	

	

	public void init() {

		WORLD_F="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon가변 윤고딕100\\_TT>▲<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		WORLD_B="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon가변 윤고딕100\\_TT>▲<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		WORLD_VERSION1="<KSC5601-WIN>\r\n<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1><60.100.0.0.:COLOR:CMYK:Process:0.6,1,0,0><30.60.0.0.:COLOR:CMYK:Process:0.3,0.6,0,0>>";
		WORLD_INPORT="<cc:><ct:><cs:><cf:><cc:30.60.0.0.><ct:Roman><cs:6.000000><cbs:-1.000000><cf:Helvetica LT Std>";
		WORLD_OUTPORT="<cc:><ct:><cs:><cbs:><cf:><ct:Roman><cs:6.000000><cf:Helvetica LT Std>";
		WORLD_VERSION2="<pstyle:><ct:Bold><chs:0.900000><cl:8.000000><cf:Helvetica LT Std>";
		WORLD_VERSION3="<pstyle:정규><pli:182.500000><pfli:-182.000000><psa:0.566894><ptr:96.37789916992188\\,Left\\,.\\,0\\,\\;201\\,Left\\,.\\,0\\,\\;><chs:0.800003><cl:20.000000><cs:18.000000><cf:Helvetica LT Std>\r\n<cl:><cl:20.099990>\r\n<cs:><ct:Bold><cs:18.000000>";
		WORLD_E="<ct:><cs:><cf:><ct:Bold><cf:Helvetica LT Std>";
	}

	public int execute() throws IOException {

		log.info("항로별 스케줄 생성 시작");		

		long startTime = System.currentTimeMillis();

		message = "항로별 스케줄 그룹화..";

		logger.info("스케줄 그룹화 시작");
		
		totalCount =0;

		// 출력 프로세스
		// toPort 그룹에서 키 셋(도착항) 조회

		try{
			// 태그 정보 출력

			fw.write(WORLD_VERSION1+"\r\n"+WORLD_VERSION2);			

			int firstIndex=0;

			scheduleList.forEach(item -> item.setArea_name(item.getArea_name().toUpperCase()));

			logger.info("schedule size:{}",scheduleList.size());
			
			// 지역명으로 그룹화
			Map<String, List<ScheduleData>> areaList =  scheduleList.stream().collect(
					Collectors.groupingBy(ScheduleData::getArea_name)); // 지역


			Object[] areakeyList = areaList.keySet().toArray();

			lengthOfTask = areakeyList.length;

			// 지역명 정렬
			Arrays.sort(areakeyList);

			printAreaList(areakeyList);
			

			for(Object strArea: areakeyList)
			{
				firstIndex++;
				
				// 지역별 조회
				List<ScheduleData> outboundScheduleListByArea = areaList.get(strArea);
				
				logger.info("Area: "+strArea+ ", 스케줄 그룹 사이즈:"+outboundScheduleListByArea.size());

				// 지역 그룹 생성
				GroupArea group = crateGroupArea((String)strArea, orderByType, outboundScheduleListByArea);
				
				
				
				// 지역명 출력
				writeArea(group.getArea_name(), firstIndex);				
				
				// 스케줄 출력
				List<String> scList= getValidatedRouteScheduleString(group.getArea_name(), group.toSortedArray());
				
				logger.info("route schedule :{}", scList.size());
				
				for(String schedule:scList)
				{
					totalCount++;
					
					fw.write(schedule);
				}
				

				// 공동배선 정보 출력
				writeCommonInPort(group.getArea_name(), group.getCommonVessel());

				current++;
				
				

				fw.write(WORLD_E);

			}
			// 파일 닫기

			close();

			long endTime = System.currentTimeMillis();

			logger.info("항로별 스케줄 생성 종료({}s)",(endTime-startTime));
			
			logger.info("항로별 스케줄 수 :({})",totalCount);
			return SchedulePrint.SUCCESS;

		}catch(Exception e)
		{
			e.printStackTrace();

			JOptionPane.showMessageDialog(null, e.getMessage());

			return SchedulePrint.FAILURE;

		}
	}
	
	private void printAreaList(Object[] areakeyList) {
		
		StringBuffer areaBuffer = new StringBuffer();

		// 지역 정보 로그 출력
		for(Object area:areakeyList)
		{
			areaBuffer.append(area+"\n");
		}

		logger.info("\n- 지역목록- \n{}",areaBuffer.toString());
	}
	
	private void writeArea(String areaName, int index) throws IOException
	{	
		String strAreaName = (index>0?"\r\n\r\n\r\n\r\n":"")+areaName+"\r\n\r\n";

		fw.write(strAreaName);

		errorOutfw.write(strAreaName);
	}
	

	private void writeCommonInPort(String areaName, ArrayList<GroupVessel> commonVesselList) throws IOException, ParseException
	{
		commonInfw.write("\r\n\r\nArea:"+areaName+", 공동배선스케줄 수: "+commonVesselList.size()+"\r\n\r\n");

		logger.debug("area:"+areaName+", 공동배선스케줄 수: "+commonVesselList.size());

		for(GroupVessel commonVessel:commonVesselList)
		{
			PortScheduleInfo portArray[]=commonVessel.getOutPortList();	

			List<PortScheduleInfo> list = Arrays.asList(portArray);

			String vesselName 	= commonVessel.getVessel_name();

			String voyage 		= commonVessel.getVoyage_num();

			String company 		= commonVessel.getCompanyString();

			String strPortAndDate =list.stream().map(item ->  item.toPortAndDateString())
					.collect( Collectors.joining(" - "));

			commonInfw.write(String.format("   %s,\t%s,\t%s,\t%s\r\n", vesselName, voyage, company, strPortAndDate));

		}

	}
	// 항구수 조건에 따른 제외 스케줄 로그 저장
	private void writeErrorSchedule(String areaName, GroupVessel vesselGroup, PortScheduleInfo[] newOutPortArray ) throws IOException {
		
		List<PortScheduleInfo> list = Arrays.asList(newOutPortArray);

		String vesselName 	= vesselGroup.getVessel_name();

		String voyage 		= vesselGroup.getVoyage_num();

		String company 		= vesselGroup.getCompanyString();

		String strPortAndDate =list.stream().map(item ->  item.toPortAndDateString())
				.collect( Collectors.joining(" - "));

		errorOutfw.write(String.format("E1:%s,\t%s,\t%s,\t%s,\t%s\r\n",areaName, vesselName, voyage, company, strPortAndDate));
		
	}


	private List<String> getValidatedRouteScheduleString(String areaName,GroupVessel[] vesselList) throws Exception
	{
		
		List<String> list = new ArrayList<String>();
		
		for(GroupVessel vesselGroup:vesselList)
		{
			/* 스케줄 제외 구분
			 * 중국, 일본; 2개 미만
			 * 러시아 1개미만
			 * 기타 3개 미만 */

			logger.debug("vesselName:"+vesselGroup.getVessel_name());

			// 국내항 목록
			PortScheduleInfo[] inPortArray = vesselGroup.getCompressInPortList();

			// 외국항 목목
			PortScheduleInfo[] outPortArray = vesselGroup.getCompressOutPortList();

			// 마지막 국내항 정보
			PortScheduleInfo lastInPort = inPortArray[inPortArray.length-1];

			List<PortScheduleInfo> outPortList = Arrays.asList(outPortArray);

			// 국내항 마지막 일자 보다 늦은 외국항 제외
			List<PortScheduleInfo> filteredOutPortList = outPortList.stream().filter(item ->isValidateDate(item.getDate(), lastInPort.getDate()))
					                                                         
																	.collect(Collectors.toList());

			PortScheduleInfo[] newOutPortArray =filteredOutPortList.toArray(new PortScheduleInfo[filteredOutPortList.size()]);

			// 국내항과 외국항 수 체크
			if(RouteScheduleUtil.checkOutPort(areaName,newOutPortArray.length))
			{
				// 정상 스케줄
				
				list.add(toStringSchedule(vesselGroup,newOutPortArray));
				
			}
			else
			{
				// 항구수 조건에 따른 제외 스케줄 로그 저장
				writeErrorSchedule(areaName, vesselGroup, newOutPortArray );
			}
		}
		return list;
	}
	
	// 국내항 마지막 일자 보다 늦은 외국항 제외
	private boolean isValidateDate(String oneDate, String twoDate)
	{
		try {
			return KSGDateUtil.daysDiff(KSGDateUtil.toDate4(oneDate), KSGDateUtil.toDate4(twoDate))<0;
		} catch (ParseException e) {
			return false;
		}
	}

	/**
	 * 스케줄 그룹 생성
	 * @param areaName 지역명
	 * @param orderByType 정렬 타입
	 * @return
	 * @throws Exception
	 */
	public GroupArea crateGroupArea(String areaName, int orderByType, List<ScheduleData> outboundScheduleListByArea) throws Exception
	{
		logger.debug("AREA:{}, scheduleSize:{}",areaName,outboundScheduleListByArea.size());

		GroupArea group = new GroupArea(areaName,  orderByType);

		Iterator<ScheduleData> scheduleIter =outboundScheduleListByArea.iterator();

		while(scheduleIter.hasNext())
		{
			ScheduleData data=scheduleIter.next();

			// 선박명 validateion
			Vessel vesselInfo = ScheduleManager.getInstance().searchVessel(data.getVessel());

			// 동록된 선박명이 없으면 스케줄 제외
			if(vesselInfo == null) continue;

			// 사용하지 않는 선박이면 스케줄에서 제외
			if(vesselInfo.getVessel_use()==Vessel.NON_USE)
				continue;

			group.addSchedule(data);

		}
		return group;

	}


	/**
	 * @deprecated
	 * @param vessel
	 * @param outPortList
	 * @return
	 * @throws ParseException
	 */
	private String toStringSchedule2(GroupVessel vessel,PortScheduleInfo[] outPortList) throws ParseException
	{		
		PortScheduleInfo[] inPortList = vessel.getCompressInPortList();

		return 	WORLD_F+vessel.getVessel_name()+" - "+vessel.getVoyage_num()+" (" + vessel.getCompanyString() + ")"+"\r\n"+

		WORLD_INPORT+toStringInPortInfo(inPortList)+"\r\n"+

		WORLD_OUTPORT+toStringOutPortInfo(inPortList[inPortList.length-1],outPortList)+"\r\n\r\n";
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

		String vessel_name = vessel.getVessel_name();

		String voyage      = vessel.getVoyage_num();

		String company = vessel.getCompanyString();

		String inPorts = toStringInPortInfo(inPortList);

		String outPorts = toStringOutPortInfo(inPortList[inPortList.length-1],outPortList);

		return String.format("%s%s - %s (%s)\r\n%s%s\r\n%s%s\r\n\r\n", WORLD_F, vessel_name, voyage, company, WORLD_INPORT, inPorts, WORLD_OUTPORT, outPorts);

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


}
