package com.dtp.api.control;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.service.ShipperTableService;
import com.dtp.api.service.impl.ShipperTableServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.commands.schedule.SortAllCommand;
import com.ksg.commands.schedule.SortInlandCommnad;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.Schedule;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("schedule")
public class ScheduleController extends AbstractController{

	private ObjectMapper objectMapper;

	private ScheduleSubService service;

	private VesselServiceV2 vesselService;

	private ShipperTableService tableService = new ShipperTableServiceImpl();
	
	protected Logger logger = LogManager.getLogger(this.getClass());

	public ScheduleController()
	{
		service = new ScheduleServiceImpl();
		
		vesselService = new VesselServiceImpl();
		
		objectMapper = new ObjectMapper();

	}

	public CommandMap selectScheduleList(String inOoutType, String date) throws Exception
	{
		log.info("param:{},{}", inOoutType, date);
		CommandMap result = new CommandMap();

		Schedule param = new Schedule();
		param .setDate_issue(date);
		param.setInOutType(inOoutType);

		List li=service.selecteScheduleListByCondition(param);

		result.put("success", true);
		result.put("data", li);

		return result;
	}
	public CommandMap selectScheduleMapList(CommandMap param) throws Exception
	{
		CommandMap result = new CommandMap();

		List li  = service.selecteScheduleListMapByCondition(param);
		result.put("success", true);
		result.put("master", li);

		return result;
	}



	public CommandMap createSchedule(String scheduleType, String date) throws Exception
	{
		CommandMap result = new CommandMap();
		result.put("success", true);
		result.put("data", null);

		return result;
	}

	public CommandMap createScheduleFile(String scheduleType, String date) throws Exception
	{
		CommandMap result = new CommandMap();
		result.put("success", true);
		result.put("data", null);

		return result;
	}

	public CommandMap createWebScheduleFile(String scheduleType, String date) throws Exception
	{
		CommandMap result = new CommandMap();
		result.put("success", true);
		result.put("data", null);

		return result;
	}

	public CommandMap deleteSchedule(String date) throws Exception
	{
		CommandMap result = new CommandMap();

		result.put("data", null);

		return result;
	}

	/**
	 *지역
	 *----도착항
	 *--------출발항
	 *------------선박
	 */

	public CommandMap selectOutboundScheduleGroupList(CommandMap param) throws SQLException {

		List<ScheduleData>  li = service.selecteScheduleListByCondition(param);

		if(li.isEmpty()) return new CommandMap();


		List<String> vesselNames=li.stream().map(ScheduleData::getVessel)
				.distinct()
				.collect(Collectors.toList());

		CommandMap vesselParam = new CommandMap();
		vesselParam.put("vesselNameList", vesselNames);
		List<Vessel> vesselList = vesselService.selectListByCondition(vesselParam);
		Map<String, Vessel> vesselMap = vesselList.stream().collect(Collectors.toMap(Vessel::getVessel_name, Function.identity()));
		li.forEach(o -> o.setVesselInfo(vesselMap.get(o.getVessel())));


		Map<String, Map<String, Map<String, List<ScheduleData>>>> areaList =  li.stream().collect(
				Collectors.groupingBy(ScheduleData::getArea_name, // 지역
						Collectors.groupingBy(ScheduleData::getFromPort, // 출발항
								Collectors.groupingBy(ScheduleData::getPort))));// 도착항

		CommandMap returnValue = new CommandMap();
		for(String area : areaList.keySet() ) {
			returnValue.put(area, areaList.get(area));
		}

		return returnValue;

	}


	/** 지역
	 *  ----출발항(외국항)
	 *  --------선박
	 *  -----------도착항(국내항)
	 */

	public CommandMap selectInboundScheduleGroupList(CommandMap param) throws SQLException {

		log.info("param:{}",param);

		CommandMap result = (CommandMap) service.selectListMap(param);

		List<CommandMap> master = (List) result.get("master");

		CommandMap areaList = new CommandMap();

		Iterator<CommandMap>iter = master.iterator();		

		while(iter.hasNext())
		{
			CommandMap item = iter.next();

			String area_name=(String) item.get("area_name");

			String fromPort = (String) item.get("fromPort");

			String toPort = (String) item.get("port");

			String dateF = (String) item.get("DateF");

			String vessel = (String) item.get("vessel");

			if(areaList.containsKey(area_name))
			{	
				//출발항 목록
				CommandMap fromPorts =(CommandMap) areaList.get(area_name);

				//출발항 있을 경우
				if(fromPorts.containsKey(fromPort))					  
				{
					//스케줄 목록
					CommandMap vessels =(CommandMap) fromPorts.get(fromPort);


					//vessel 있을 경우
					if(vessels.containsKey(vessel+"$$"+dateF))					  
					{
						ArrayList<CommandMap> scheduleList =(ArrayList<CommandMap>) vessels.get(vessel+"$$"+dateF);
						scheduleList.add(item);
					}
					else
					{
						ArrayList<CommandMap> scheduleList  = new ArrayList<CommandMap>();
						scheduleList.add(item);
						vessels.put(vessel+"$$"+dateF, scheduleList);
					}


				}
				//출발항 없을 경우
				else
				{
					CommandMap vessels = new CommandMap();

					ArrayList<CommandMap> scheduleList  = new ArrayList<CommandMap>();

					scheduleList.add(item);

					/*
					 * 
					 *  스케줄 그룹 키 : vessel$$fromDate
					 */

					vessels.put(vessel+"$$"+dateF, scheduleList);

					fromPorts.put(fromPort, vessels);
				}



			}
			else
			{
				//스케줄 그룹
				CommandMap vessels = new CommandMap();

				ArrayList<CommandMap> scheduleList  = new ArrayList<CommandMap>();

				scheduleList.add(item);

				/*
				 * 
				 *  스케줄 그룹 키 : vessel$$fromDate
				 */

				vessels.put(vessel+"$$"+dateF, scheduleList);

				// 출발항 그룹
				CommandMap newFromPorts = new CommandMap();

				newFromPorts.put(fromPort, vessels);	

				areaList.put(area_name, newFromPorts);
			}

		}

		//정렬 및 공동 배선

		return areaList;



	}

	/**
	 * 라우트 스케줄 그룹 조회
	 * @param param
	 * @return
	 * @throws SQLException
	 */

	public Map<String, Object> selectRouteScheduleGroupList(CommandMap param) throws SQLException {

		List<ScheduleData>  li = service.selecteScheduleListByCondition(param);
		
		logger.info("schedule size:{}", li.size());
		
		System.out.println("size:"+li.size());

		li.stream().forEach(schedule -> schedule.setArea_name(schedule.getArea_name().toUpperCase()));
		
		List allVesselList= vesselService.selectAllList();
		
		Map<String, Vessel> vesselNameMap = (Map<String, Vessel>) allVesselList.stream().distinct().collect(Collectors.toMap(Vessel::getVessel_name, Function.identity()));
		
		List<ScheduleData> schedulelist = li.stream().filter(schedule ->vesselNameMap.containsKey(schedule.getVessel())).collect(Collectors.toList());

		Map<String, Map<String, List<ScheduleData>>> areaList =  schedulelist.stream().collect(
				Collectors.groupingBy(ScheduleData::getArea_name, // 지역
						Collectors.groupingBy(ScheduleData::getVessel))
				);// 선박
		
		

		CommandMap returnValue = new CommandMap();
		for(String area : areaList.keySet() ) {
			returnValue.put(area, areaList.get(area));
		}

		return returnValue;

	}
	
	@ControlMethod(serviceId = "deleteSchedule")
	public CommandMap deleteSchedule(CommandMap param) throws Exception
	{
		CommandMap returnMap = new CommandMap();
		
		int result=service.deleteSchedule();
		
		int b=service.deleteInlnadSchedule();
		
		returnMap.put("deleteCount",(result+b));
		
		return returnMap;
	}

	@ControlMethod(serviceId = "scheduleViewUpdate")
	public CommandMap updateView(CommandMap param) throws Exception
	{	
		
		List<ShippersTable> tableDateTarget = tableService.selectTableAll();
		
		List<String> tableDatelist =  tableDateTarget.stream()
															.map(ShippersTable ->
															{
																try {
																	return KSGDateUtil.format(KSGDateUtil.toDate2(ShippersTable.getDate_isusse()));
																	
																} catch (ParseException e) {
																	e.printStackTrace();
																	return "error";
																}
															})
															
															.distinct()
															.sorted(Comparator.reverseOrder())
															.collect(Collectors.toList());
		
		List scheduleDateLists = service.selectScheduleDateList();

		CommandMap returnMap = new CommandMap();
		
		returnMap.put("tableDatelist", tableDatelist);

		returnMap.put("scheduleDateLists", scheduleDateLists);

		return returnMap;

	}
	@ControlMethod(serviceId = "schedulePrint")
	public CommandMap schedulePrint(CommandMap param) throws Exception
	{	
		ScheduleManager scheduleManager = ScheduleManager.getInstance();
		
		ScheduleData op = (ScheduleData) param.get("op");
		
		String gubun = (String) param.get("gubun");
		
		// 콘솔 스케줄 생성
		if(gubun.equals(ShippersTable.GUBUN_CONSOLE))
		{	
			ScheduleJoint console=scheduleManager.getConsoleSchedudle(op);

			scheduleManager.addBulid(console);

			scheduleManager.startBuild();

		}

		// 인랜드 스케줄
		else if(gubun.equals(ShippersTable.GUBUN_INLAND))
		{	

			new SortInlandCommnad(op).execute();
		}

		else //normal: 아웃바운드, 인바운드, 항로별
		{
			new SortAllCommand(param).execute();
		}
		
		
		
		CommandMap returnMap = new CommandMap();
		
		return returnMap;
	}




}
