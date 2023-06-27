package com.dtp.api.control;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.schedule.create.CreateScheduleData;
import com.dtp.api.schedule.create.CreateScheduleDataGroup;
import com.dtp.api.schedule.joint.tree.TreeNodeManager;
import com.dtp.api.service.DTPScheduleService;
import com.dtp.api.service.ShipperTableService;
import com.dtp.api.service.impl.CodeServiceImpl;
import com.dtp.api.service.impl.DTPScheduleServiceImpl;
import com.dtp.api.service.impl.ShipperTableServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.commands.ScheduleExecute;
import com.ksg.commands.schedule.SortAllCommand;
import com.ksg.commands.schedule.SortInlandCommnad;
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Schedule;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ScheduleEnum;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.ScheduleServiceManager;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.service.AreaService;
import com.ksg.service.PortService;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("schedule")
public class ScheduleController extends AbstractController{
	
	
	private DTPScheduleService dtpScheduleService;

	private AreaService areaService = new AreaServiceImpl();
	
	protected CodeServiceImpl codeService = 		 new CodeServiceImpl();;
	
	private ObjectMapper objectMapper;

	private ScheduleSubService service;

	private VesselServiceV2 vesselService;
	
	private PortService portService;

	private ShipperTableService tableService = new ShipperTableServiceImpl();
	
	private TreeNodeManager nodeManager = new TreeNodeManager();
	
	public ScheduleController()
	{
		
		dtpScheduleService = new DTPScheduleServiceImpl();
		
		service = new ScheduleServiceImpl();
		
		vesselService = new VesselServiceImpl();
		
		portService = new PortServiceImpl();
		
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

		result.put("data", li);

		return result;
	}
	public CommandMap selectScheduleMapList(CommandMap param) throws Exception
	{
		log.info("param:{}", param);
		
		CommandMap result = new CommandMap();

		List li  = service.selecteScheduleListMapByCondition(param);
		result.put("success", true);
		result.put("master", li);

		return result;
	}

	public CommandMap createSchedule(String scheduleType, String date) throws Exception
	{
		log.info("param:{},{}", scheduleType, date);
		CommandMap result = new CommandMap();
		result.put("data", null);

		return result;
	}

	public CommandMap createScheduleFile(String scheduleType, String date) throws Exception
	{
		log.info("param:{},{}", scheduleType, date);
		CommandMap result = new CommandMap();
		result.put("success", true);
		result.put("data", null);

		return result;
	}

	public CommandMap createWebScheduleFile(String scheduleType, String date) throws Exception
	{
		log.info("param:{},{}", scheduleType, date);
		CommandMap result = new CommandMap();
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

	public CommandMap selectOutboundScheduleGroupList(List<ScheduleData>  scheduleList) throws Exception {
		
		Code codeParam = new Code();

		codeParam.setCode_type(XML_INFO.XML_TAG_FROM_PORT);

		List<Code> li = codeService.selectCodeDetailListByCondition(codeParam);

		String[] fromPort = new String[li.size()];

		for(int i=0;i<li.size();i++)
		{
			Code info = li.get(i);
			fromPort[i] =info.getCode_name();
		}

		if(scheduleList.isEmpty()) return new CommandMap();

		Map<String, Vessel> vesselMap = extractedVesselMap(scheduleList);
		
		Map<String, PortInfo> portMap = extractedPortMap(scheduleList);
		
		
		scheduleList.forEach(o -> o.setVesselInfo(vesselMap.get(o.getVessel())));

		CommandMap result = new CommandMap();
		
		result.put("scheduleList", scheduleList);
		
		result.put("portMap", portMap);
		
		result.put("vesselMap", vesselMap);
		
		result.put("fromPort", fromPort);

		return result;

	}

	private Map<String, Vessel> extractedVesselMap(List<ScheduleData> scheduleList) throws SQLException {
		List<String> vesselNames=scheduleList.stream().map(ScheduleData::getVessel)
				.distinct()
				.collect(Collectors.toList());
		
		
		if(vesselNames.isEmpty()) return new HashMap();
		
		CommandMap vesselParam = new CommandMap();
		
		vesselParam.put("vesselNameList", vesselNames);
		
		List<Vessel> vesselList = vesselService.selectVesselListByNameList(vesselNames);
		
		Map<String, Vessel> vesselMap = vesselList.stream().collect(Collectors.toMap(Vessel::getVessel_name, Function.identity()));
		
		return vesselMap;
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

			String area_name 	= (String) item.get("area_name");

			String fromPort 	= (String) item.get("fromPort");

			String toPort 		= (String) item.get("port");

			String dateF 		= (String) item.get("DateF");

			String vessel 		= (String) item.get("vessel");

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

	public Map<String, Map<String, List<ScheduleData>>> selectRouteScheduleGroupList(List<ScheduleData> li) throws SQLException {
		
		log.info("schedule size:{}", li.size());
		
		li.stream().forEach(schedule -> schedule.setArea_name(schedule.getArea_name().toUpperCase()));
		
		List allVesselList= vesselService.selectAllList();
		
		Map<String, Vessel> vesselNameMap = (Map<String, Vessel>) allVesselList.stream().distinct().collect(Collectors.toMap(Vessel::getVessel_name, Function.identity()));
		
		List<ScheduleData> schedulelist = li.stream().filter(schedule ->vesselNameMap.containsKey(schedule.getVessel())).collect(Collectors.toList());
		
		Map<String, Map<String, List<ScheduleData>>> areaList =  schedulelist.stream().collect(
				Collectors.groupingBy(ScheduleData::getArea_name, // 지역
						Collectors.groupingBy(ScheduleData::getVessel))
				);// 선박
		
		return areaList;

	}
	
	@ControlMethod(serviceId = "deleteSchedule")
	public CommandMap deleteSchedule(CommandMap param) throws Exception
	{
		log.info("param:{}", param);
		
		CommandMap returnMap = new CommandMap();
		
		int result=service.deleteSchedule();
		
		int b=service.deleteInlnadSchedule();
		
		returnMap.put("deleteCount",(result+b));
		
		return returnMap;
	}

	@ControlMethod(serviceId = "scheduleViewUpdate")
	public CommandMap updateView(CommandMap param) throws Exception
	{	
		log.info("param:{}", param);
		
		List<ShippersTable> tableDateTarget = tableService.selectTableAll();
		
		List<String> tableDatelist =  tableDateTarget.stream()
															.filter(ShippersTable ->ShippersTable.getDate_isusse()!=null)
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
	
	@ControlMethod(serviceId = "createSchedule")
	public CommandMap scheduleCreate(CommandMap param) throws Exception
	{	
		log.info("param:{}", param);
		
		ScheduleServiceManager serviceManager =ScheduleServiceManager.getInstance();
		
		String inputDate= (String) param.get("inputDate");
		
		serviceManager.buildSchedule(inputDate);
		
		CommandMap returnMap = new CommandMap();
		
		return returnMap;
	}
	
	
	@ControlMethod(serviceId = "createSchedule2")
	public CommandMap scheduleCreate2(CommandMap param) throws Exception
	{	
		log.info("param:{}", param);
		
		String table_date = (String) param.get("table_date");
		
		List<ShippersTable> tableList = tableService.selectTableAll();
		
		log.info("size:{}", tableList.size());
		
		List fillteredTableList = tableList.stream()
										.filter(table -> table.getTable_date()!=null)
										.filter(table -> table.getTable_date().equals(table_date))
										.collect(Collectors.toList());
		log.info("fillterd size:{}", fillteredTableList.size());
		
		CreateScheduleDataGroup create = new  CreateScheduleDataGroup(fillteredTableList);
		
		create.execute();
		
		CommandMap returnMap = new CommandMap();
		
		return returnMap;
	}
	
	@ControlMethod(serviceId = "schedulePrint")
	public CommandMap schedulePrint(CommandMap param) throws Exception
	{	
		log.info("param:{}", param);
		
		ScheduleManager scheduleManager = ScheduleManager.getInstance();
		
		ScheduleData op = (ScheduleData) param.get("op");
		
		String gubun = (String) param.get("gubun");
		
		String date_issue = (String) param.get("date_issue");
		
		// 콘솔 스케줄 생성
		if(gubun.equals(ShippersTable.GUBUN_CONSOLE))
		{	
			ScheduleExecute console=scheduleManager.getConsoleSchedudle(op);

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
			CommandMap searchParam = new CommandMap();
			
			searchParam.put("gubun", gubun);
			
			searchParam.put("date_issue", date_issue);
			
			List<ScheduleData> scheduleList=service.selecteScheduleListByCondition(searchParam);
			
			Map<String, PortInfo> portMap = extractedPortMap(scheduleList);
			
			Map<String, Vessel> vesselMap = extractedVesselMap(scheduleList);
			
			List<ScheduleData>outboundScheduleList= scheduleList.stream()
																.filter(o->o.getInOutType().equals("O"))
																.collect(Collectors.toList());
			
			List<ScheduleData>inboundScheduleList= scheduleList.stream()
																.filter(o->o.getInOutType().equals("I"))
																.collect(Collectors.toList());
			
			
			Map<String, Map<String, List<ScheduleData>>> inboundScheduleMap = inboundScheduleList.stream().collect(
							Collectors.groupingBy(ScheduleData::getFromPort,
									Collectors.groupingBy(ScheduleData::getVessel
											)));
			
			
			Map<String, Map<String, List<ScheduleData>>> routeScheduleMap =  scheduleList.stream().collect(
							Collectors.groupingBy(ScheduleData::getArea_name, // 지역
									Collectors.groupingBy(ScheduleData::getVessel))
							);// 선박
			
			param.put("outboundScheduleList", outboundScheduleList);
			
			param.put("inboundScheduleList", inboundScheduleList);
			
			param.put("inboundScheduleMap", inboundScheduleMap);
			
			param.put("scheduleList", scheduleList);
			
			param.put("routeScheduleMap", routeScheduleMap);
			
			param.put("portMap", portMap);
			
			param.put("vesselMap", vesselMap);
			
			new SortAllCommand(param).execute();
		}
		
		CommandMap returnMap = new CommandMap();
		
		return returnMap;
	}

	private Map<String, PortInfo>  extractedPortMap(List<ScheduleData> scheduleList) throws SQLException {
		List<String> portNames=scheduleList.stream().map(ScheduleData::getPort)
				.distinct()
				.collect(Collectors.toList());
		
		List<String> fromportNames=scheduleList.stream()
												.map(ScheduleData::getFromPort)
												.distinct()
												.collect(Collectors.toList());
		
		portNames.addAll(fromportNames);
		
		if(portNames.isEmpty()) return new HashMap();
		
		List<PortInfo> portList = portService.selectPortListByNameList(portNames);
		
		return  portList.stream().collect(Collectors.toMap(PortInfo::getPort_name, Function.identity()));
	}
	
	@ControlMethod(serviceId = "pnNormalByTree.init")
	public CommandMap initView(CommandMap param) throws Exception
	{	
		log.info("param:{}", param);
		
		List<AreaInfo> trgetList=areaService.selectAll();
		
		List<String>areaList= trgetList.stream()
										.map(AreaInfo::getArea_name)
										.sorted()
										.collect(Collectors.toList());
		
		HashMap<String, Object> inboundCodeMap = (HashMap<String, Object>) codeService.selectInboundPortMap();
		
		CommandMap returnMap = new CommandMap();
		
		returnMap.put("areaList", areaList);
		
		returnMap.put("inboundCodeMap", inboundCodeMap);
		
		return returnMap;
	}
	
	@ControlMethod(serviceId = "pnNormal2.fnSearch")
	public CommandMap pnNormalfnSearch(CommandMap param) throws Exception
	{
		log.info("param:{}", param);
		
		CommandMap returnMap = new CommandMap();
		
		ScheduleData scheduleParam = ScheduleData.builder()
				.date_issue((String) param.get("date_issue"))
				.table_id((String) param.get("table_id"))
				.InOutType((String) param.get("inOutType"))
				.gubun((String) param.get("gubun"))
				.vessel((String) param.get("vessel"))
				.company_abbr((String) param.get("company_abbr"))
				.agent((String) param.get("agent"))
				.area_name((String) param.get("area_name"))
				.port((String) param.get("port"))
				.fromPort((String) param.get("fromPort"))
				.build();
		
		List<ScheduleData>  rawScheduleList = dtpScheduleService.selecteScheduleListByCondition(scheduleParam);
		
		
		List<CommandMap> result=rawScheduleList.stream()
				.map(o -> objectMapper.convertValue(o, CommandMap.class))
				.collect(Collectors.toList());
		
		returnMap.put("data", result);
		
		return returnMap;
	}
	@ControlMethod(serviceId = "pnNormalByTree.fnSearch")
	public CommandMap fnSearch(CommandMap param) throws Exception
	{
		log.info("param:{}", param);
		
		CommandMap returnMap = new CommandMap();
		
		String inOutType = (String)param.get("inOutType");
		
		returnMap.put("inOutType", inOutType);
		
		returnMap.put("depth", param.get("depth"));
		
		ScheduleData scheduleParam = ScheduleData.builder()
												.date_issue((String) param.get("date_issue"))
												.table_id((String) param.get("table_id"))
												.InOutType("I".equals( param.get("inOutType"))?"I":"O")
												.gubun((String) param.get("gubun"))
												.vessel((String) param.get("vessel"))
												.company_abbr((String) param.get("company_abbr"))
												.agent((String) param.get("agent"))
												.area_name((String) param.get("area_name"))
												.port((String) param.get("port"))
												.fromPort((String) param.get("fromPort"))
												.build();
		
		List<ScheduleData>  rawScheduleList = service.selecteScheduleListByCondition(scheduleParam);
		
		Map<String, Vessel> vesselMap 		= extractedVesselMap(rawScheduleList);
		
		Map<String, PortInfo> portMap 		= extractedPortMap(rawScheduleList);
		
		if(inOutType.equals(ScheduleEnum.INBOUND.getSymbol()))			
		{	
			Map<String, Map<String, List<ScheduleData>>> inboundScheduleMap =  rawScheduleList.stream()
					
					.filter(schedule -> "I".equals(schedule.getInOutType()))
					.collect(
					Collectors.groupingBy(ScheduleData::getFromPort, // 출발항
							Collectors.groupingBy(ScheduleData::getVessel))
					);// 선박
			
			CommandMap result = new CommandMap();
			
			result.put("inboundScheduleMap", inboundScheduleMap);
			result.put("isAddValidate", param.get("isAddValidate"));
			result.put("vesselMap", vesselMap);
			result.put("portMap", portMap);
			returnMap.put("count", rawScheduleList.size());
			
			
			returnMap.put("treeNode", nodeManager.getInboundTreeNode(result));
			
		}
		else
		{
			if(inOutType.equals(ScheduleEnum.OUTBOUND.getSymbol()))
			{	
				CommandMap result = (CommandMap) selectOutboundScheduleGroupList(rawScheduleList);
				
				result.put("isAddValidate", param.get("isAddValidate"));
				
				result.put("mergeBusan", param.get("mergeBusan"));
				
				returnMap.put("count", rawScheduleList.size());
				
				returnMap.put("treeNode", nodeManager.getOutboundTreeNode(result));
			}
			else
			{	
				Map<String, Map<String, List<ScheduleData>>> result =  selectRouteScheduleGroupList(rawScheduleList);

				CommandMap routeparam = new CommandMap();

				routeparam.put("data", result);
				
				routeparam.put("sortType", param.get("sortType"));
				
				routeparam.put("isAddValidate", param.get("isAddValidate"));
				
				routeparam.put("vesselMap", vesselMap);
				
				routeparam.put("portMap", portMap);
				
				//==============				
				returnMap.put("count", rawScheduleList.size());
				
				returnMap.put("treeNode", nodeManager.getRouteTreeNode(routeparam));
				
				returnMap.put("inOutType","O");
			}
		}
		return returnMap;
	}
}

