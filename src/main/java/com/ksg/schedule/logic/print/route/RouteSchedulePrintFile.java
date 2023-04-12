package com.ksg.schedule.logic.print.route;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.dtp.api.schedule.comparator.DateComparator;
import com.dtp.api.schedule.comparator.VesselComparator;
import com.dtp.api.schedule.joint.RouteJoint;
import com.dtp.api.schedule.joint.RouteJointSubject;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.SchedulePrint;
import com.ksg.schedule.logic.joint.route.RouteScheduleGroup;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;
import com.ksg.schedule.logic.print.SchedulePrintParam;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;

/**
 * 

  * @FileName : RouteScheduleJointV4.java

  * @Project : KSG2

  * @Date : 2023. 1. 29. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 라우트 스케줄 출력
 */
public class RouteSchedulePrintFile extends AbstractRouteSchedulePrint implements RouteJointSubject{
	
	private RouteJoint routeJoint;
	
	private DateComparator dateComparator 			= new DateComparator(new SimpleDateFormat("yyyy/MM/dd"));

	private VesselComparator vesselComparator 		= new VesselComparator();
	
	private VesselServiceV2 vesselService 			= new VesselServiceImpl();
	
	private String date_isusse;
	
	private int orderBy;
	
	private List<Vessel>allVesselList;

	public RouteSchedulePrintFile(ShippersTable op, int orderBy) throws Exception {
		
		super(op);
	}
	
	public RouteSchedulePrintFile(String date_isusse, int orderBy) throws Exception {
		
		super();
		
		this.date_isusse = date_isusse;
		
		this.orderBy = orderBy;
		
		routeJoint = new RouteJoint(this);
		
		CommandMap param = new CommandMap();
		
		param.put("date_issue", date_isusse);

		param.put("inOutType", OUTBOUND);
		
//		param.put("gubun", "Normal");
		
		// 스케줄 목록 조회
		scheduleList= scheduleService.selecteScheduleListByCondition(param);
		
		logger.info("schedule size:{}", scheduleList.size());		
		
	}

	public RouteSchedulePrintFile(List<ScheduleData> scheduleList, int orderBy) throws Exception {
		super();
		
		routeJoint = new RouteJoint(this);
		
		this.scheduleList = scheduleList; 
		
		this.orderBy = orderBy;
	}
	
	public RouteSchedulePrintFile(SchedulePrintParam param) throws Exception {
		
		this(param.getScheduleList(), param.getOrderBy());
	}
	
	public RouteSchedulePrintFile(CommandMap param) throws Exception {
		
		this( (List) param.get("scheduleList"), (int) param.get("orderBy"));
	}

	public RouteSchedulePrintFile() throws Exception {
		super();
	}

	@Override
	public int execute() throws Exception {
		
		
		if(scheduleList== null || scheduleList.isEmpty()) return SchedulePrint.FAILURE;
		
		message = "항로별 스케줄 출력..";		
		
		// vessel map 생성
		allVesselList= vesselService.selectAllList();
		
		
		Map<String, Vessel> vesselNameMap = allVesselList.stream().distinct().collect(Collectors.toMap(Vessel::getVessel_name, Function.identity()));		
		
		logger.info("row schedule size:{}",scheduleList.size());
		
		//  선박명 유효성 체크
		List<ScheduleData> schedulelist = scheduleList.stream().filter(schedule ->vesselNameMap.containsKey(schedule.getVessel())).collect(Collectors.toList());
		
		logger.info("schedule size:{}",scheduleList.size());

		schedulelist.forEach(item -> item.setArea_name(item.getArea_name().toUpperCase()));

		// 지역명으로 그룹화
		Map<String, Map<String, List<ScheduleData>>> areaList =  schedulelist.stream().collect(
				Collectors.groupingBy(ScheduleData::getArea_name,
						// 지역
						Collectors.groupingBy(ScheduleData::getVessel))
						
						); // 선박
		
		try
		{
			executeJointSchedule(areaList);
			
		}catch(Exception e)
		{
			throw new Exception(e);
		}
		
		finally
		{
			close();
		}

		return SchedulePrint.SUCCESS;
	}
	
	
	/**
	 * 지역 목록 출력
	 * @param areakeyList
	 */
	private void printAreaList(Object[] areakeyList) {

		StringJoiner joiner = new StringJoiner("\n");
		
		// 지역 정보 로그 출력
		for(Object area:areakeyList)
		{
			joiner.add((String)area);
		}

		logger.debug("\n- 지역목록- \n{}",joiner.toString());
	}



	public void writeArea(int index, String areaName) throws IOException
	{	
		fw.write((index>0?"\r\n\r\n\r\n\r\n":"")+areaName+"\r\n\r\n");
	}

	@Override
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
	private void executeJointSchedule(Map<String, Map<String, List<ScheduleData>>> areaList) throws IOException
	{	
		
		Object[] areakeyList = areaList.keySet().toArray();

		lengthOfTask = areakeyList.length;

		// 지역명 정렬
		Arrays.sort(areakeyList);
		
		// 지역명 출력
		printAreaList(areakeyList);		

		int firstIndex=0;
		
		int totalCount = 0;

		fw.write(WORLD_VERSION1+"\r\n"+WORLD_VERSION2);
		
		// 지역 순회
		for(Object strArea:areakeyList)
		{	
			writeArea(firstIndex, (String) strArea);

			Map<String, List<ScheduleData>> vesselList = areaList.get(strArea);
			
			logger.debug("Area: "+strArea+ ", 스케줄 그룹 사이즈:"+vesselList.keySet().size());
		
			Object[] vesselArray = vesselList.keySet().toArray();
			
			List<RouteScheduleGroup> scheduleGroupList = new ArrayList<RouteScheduleGroup>();			
			
			for (Object vesselKey : vesselArray)
			{	
				List<ScheduleData> scheduleList = (List<ScheduleData>) vesselList.get(vesselKey);
				
				// 항차번호(숫자)로 그룹화
				Map<Integer, List<ScheduleData>> voyageList =  scheduleList.stream().collect(
						Collectors.groupingBy(o -> ScheduleBuildUtil. getNumericVoyage(o.getVoyage_num()) ));// 항차
				
				List<Integer> keySet = new ArrayList<>(voyageList.keySet());
				
				// 항차번호로 정렬(오름차순)
				Collections.sort(keySet);

				//항차명 정렬
				
				// 
				keySet.stream().forEach(voyage -> routeJoint.createScheduleAndAddGroup(scheduleGroupList, voyageList.get(voyage), (String)strArea, (String)vesselKey));
				
			}
			
			// 출발일, 선박명 기준으로 정렬
			Collections.sort(scheduleGroupList, orderBy==AbstractRouteSchedulePrint.ORDER_BY_DATE ?dateComparator:vesselComparator);
			
			// 출력
			for(RouteScheduleGroup group:scheduleGroupList ){
				
				String strCompanys 	= group.toCompanyString();
				
				String strVoyage 	= group.getVoyage();
				
				String vesselName 	= group.getVessel();
				
				String strFromPorts = group.toFromPortString();
				
				String strToPorts 	= group.toToPortString();
				
				fw.write(String.format("%s%s - %s (%s)\r\n%s%s\r\n%s%s\r\n\r\n",WORLD_F,vesselName, strVoyage, strCompanys,WORLD_INPORT, strFromPorts,WORLD_OUTPORT, strToPorts));
				
			}
			fw.write(WORLD_E);
			current++;
			
			firstIndex++;
		}
	}

	/**
	 * 
	 */
	@Override
	public void createScheduleAndAddGroup(List scheduleGroupList, List scheduleList, String areaName, String vesselName) {
		
		List<RouteScheduleGroup> validScheduleGroupList = routeJoint.getValidatedScheduleGroupList(areaName,vesselName, scheduleList, false);
		
		scheduleGroupList.addAll(validScheduleGroupList);
	}
}
