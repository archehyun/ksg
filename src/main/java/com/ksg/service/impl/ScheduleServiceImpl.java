/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.dtp.api.mapper.VesselMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.dao.SchduleDAO;
import com.ksg.dao.impl.ADVScheduleDAO;
import com.ksg.dao.impl.ScheduleDAOImpl;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Schedule;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.KSGHashMap;
import com.ksg.service.ScheduleServiceV2;
import com.ksg.service.ScheduleSubService;

import lombok.extern.slf4j.Slf4j;

/**

 * @FileName : ScheduleServiceImpl.java

 * @Project : KSG2

 * @Date : 2022. 3. 8. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 :

 */
@Slf4j
@SuppressWarnings("unchecked")
public class ScheduleServiceImpl extends AbstractServiceImpl implements ScheduleSubService, ScheduleServiceV2{

	private SchduleDAO schduleDAO;

	private Vector<KSGHashMap> ksgHashMapList;

	private ADVScheduleDAO advScheduleDAO;	

	KSGModelManager manager = KSGModelManager.getInstance();
	
	VesselMapper vesselMapper;

	public ScheduleServiceImpl() {

		super();
		schduleDAO = new ScheduleDAOImpl();
		advScheduleDAO = new ADVScheduleDAO();
	}

	@Override
	public List getInboundPortList() throws SQLException{
		return schduleDAO.getInboundPortList();
	}

	public int deleteSchedule() throws SQLException {
		return schduleDAO.deleteSchedule();

	}

	public int deleteInlnadSchedule() throws SQLException {
		return schduleDAO.deleteInlandSchedule();

	}


	public List getScheduleList() throws SQLException
	{
		return schduleDAO.getScheduleList();
	}
	public List getScheduleList2(String inOutType) throws SQLException
	{
		return schduleDAO.getScheduleListOrderBy(inOutType);
	}

	public List getTableAndADVList() throws SQLException {
		return schduleDAO.getTableAndADVList();
	}


	public int updateScheduleData(ScheduleData data) throws SQLException {
		return schduleDAO.updateScheduleData(data);

	}

	public List getOutboundFromPortList(String port)
			throws SQLException {
		return schduleDAO.getOutboundFromPortList(port);
	}

	public List getOutboundPortList() throws SQLException {
		return schduleDAO.getOutboundPortList();
	}

	public List getOutboundScheduleList(String port,String fromPort) throws SQLException {
		return schduleDAO.getOutboundScheduleList(port, fromPort);
	}

	public List getOutboundScheduleList() throws SQLException {
		return schduleDAO.getOutboundScheduleList();
	}

	public List getOutboundScheduleList(ScheduleData data) throws SQLException {
		return schduleDAO.getOutboundScheduleList(data);
	}

	public ScheduleData insertScheduleData(ScheduleData data) 
			throws SQLException {
		return schduleDAO.insertScheduleData(data);
	}
	public ScheduleData insertInlandScheduleData(ScheduleData data) 
			throws SQLException {
		return schduleDAO.insertInlandScheduleData(data);
	}

	public List getInboundtoPortList(String port) throws SQLException {
		return schduleDAO.getInboundtoPortList();
	}

	public PortInfo getPortInfoByPortAbbr(String port) throws SQLException {
		return schduleDAO.getPortInfoByPortAbbr(port);
	}

	public boolean isInPort(String port_name) throws SQLException {
		return schduleDAO.isInPort(port_name);
	}

	public List getInboundScheduleList(String port) throws SQLException {
		return schduleDAO.getInboundScheduleList(port );
	}

	public List getInboundScheduleListByVessel(String port, String vessel)
			throws SQLException {
		return schduleDAO.getInboundScheduleListByVessel(port,vessel );

	}

	public List getScheduleListByArea(ScheduleData data)
			throws SQLException {
		return schduleDAO.getScheduleListByArea(data);
	}

	public List getScheduleListByVesselVoy(String vessel, String voy)
			throws SQLException {
		return schduleDAO.getScheduleListByVesselVoy(vessel,voy );
	}

	public List getScheduleListByToFrom(String areaCode, String areaCode2,
			String InOut, int forSch) throws SQLException {
		return schduleDAO.getScheduleListByToFrom(areaCode, areaCode2,
				InOut, forSch);
	}

	public List getScheduleListGroupByCompany(String searchDate)
			throws SQLException {
		return schduleDAO.getScheduleListGroupByCompany(searchDate);
	}

	public List getScheduleList(ScheduleData param) throws SQLException {

		log.info("param:{}",param.getDate_issue());

		return schduleDAO.getScheduleList(param);
	}

	public List selectScheduleDateList() throws SQLException {

		List li1 = schduleDAO.selectScheduleDateList();

		List li2 = schduleDAO.selectInlandScheduleDateList();

		li1.addAll(li2);
		return li1;
	}


	public int getScheduleNTopCount(ScheduleData data) throws SQLException {
		return schduleDAO.getScheduleNTopCount(data);
	}

	public List getScheduleListNTop(ScheduleData data) throws SQLException
	{
		return schduleDAO.getScheduleListNTop(data);
	}

	public List<ScheduleData> getConsoleScheduleList(String port,
			String fromPort) throws SQLException {
		return schduleDAO.getConsoleScheduleList(port, fromPort);
	}

	public List getConsoleScheduleList() throws SQLException {
		return schduleDAO.getConsoleScheduleList();
	}

	@Override
	public List getConsoleScheduleList(ScheduleData data) throws SQLException {

		return schduleDAO.getConsoleScheduleList(data);
	}

	@Deprecated
	@Override
	public List<ScheduleData> getInlandScheduleList(ScheduleData data) 
			throws SQLException {

		return schduleDAO.getInlandScheduleList(data);
	}

	public List getInlandScheduleDateList() throws SQLException {
		return schduleDAO.getInlandScheduleDateList();
	}



	@Override
	public List<String> getOutboundAreaList() throws SQLException {
		return schduleDAO.getOutboundAreaList();

	}
	public CommandMap selectScheduleList(CommandMap commandMap) throws SQLException {

		log.debug("param:{}", commandMap);

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", schduleDAO.selectCount(commandMap));

		resultMap.put("master", schduleDAO.selectList(commandMap));

		return resultMap;

	}

	public HashMap<String, Object> selectInlandScheduleList(HashMap<String, Object> commandMap) throws SQLException {

		log.debug("param:{}", commandMap);

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("total", schduleDAO.selectCount(commandMap));

		resultMap.put("master", schduleDAO.selectInlnadList(commandMap));

		return resultMap;

	}



	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectScheduledAreaList(Map<String, Object> commandMap) throws SQLException {

		log.debug("param:{}", commandMap);

		return advScheduleDAO.selectScheduledAreaList(commandMap);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectScheduleJointList(Map<String, Object> commandMap) throws SQLException {

		log.debug("param:{}", commandMap);

		List<Map<String, Object>> areaList = selectScheduledAreaList(commandMap);

		List<Map<String, Object>> returnList = new LinkedList<Map<String,Object>>();

		for(Map<String, Object> areaItem: areaList)
		{
			HashMap<String, Object> param = new HashMap<String, Object>();

			String area_name = (String) areaItem.get("area_name");			

			commandMap.put("area_name", area_name);			

			List<Map<String, Object>> portList = selectScheduledPortList("toPort",commandMap);

			param.put("area_name", area_name);

			param.put("port_list", portList);

			for(Map<String, Object> portItem: portList)
			{
				String port =(String) portItem.get("port");

				commandMap.put("port", port);

				param.put(port, advScheduleDAO.selectScheduleJointList(commandMap));	
			}

			returnList.add(param);
		}

		return returnList;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectInboundScheduleJointList(Map<String, Object> commandMap) throws SQLException {

		List<Map<String, Object>> areaList = selectScheduledPortList("fromPort",commandMap);

		List<Map<String, Object>> returnList = new LinkedList<Map<String,Object>>();

		for(Map<String, Object> areaItem: areaList)
		{
			String port =(String) areaItem.get("fromPort");

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("fromPort", port);
			commandMap.put("fromPort", port);
			param.put(port, advScheduleDAO.selectScheduleJointList(commandMap));

			returnList.add(param);
		}



		return returnList;
	}

	private List<Map<String, Object>> selectScheduledPortList(String portType,Map<String, Object> commandMap) throws SQLException {
		if(portType.equals("toPort"))
		{
			return advScheduleDAO.selectScheduledToPortList(commandMap);
		}else
		{
			return advScheduleDAO.selectScheduledFromPortList(commandMap);
		}
	}


	@Override
	public int getTotalCount(HashMap<String, Object> commandMap) throws SQLException
	{
		return advScheduleDAO.selectScheduleCount(commandMap);
	}

	@Override
	public CommandMap selectListMap(CommandMap param) throws SQLException {

		log.debug("param:{}", param);

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", schduleDAO.selectCount(param));

		resultMap.put("master", schduleDAO.selectList(param));

		return resultMap;
	}

	@Override
	public HashMap<String, Object> selectListByPage(HashMap<String, Object> param) throws SQLException {

		log.debug("param:{}", param);

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("total", schduleDAO.selectCount(param));

		resultMap.put("master", schduleDAO.selectListByPage(param));

		return resultMap;
	}

	@Override
	public CommandMap selectGroupList(CommandMap param) throws SQLException {

		log.debug("param:{}", param);

		CommandMap resultMap = new CommandMap();

		List list=schduleDAO.selectList(param);

		return null;
	}
//	@Override
//	public Map<String, Object> selectOutboundScheduleGroupList(CommandMap param) throws SQLException {
//
//		log.debug("param:{}", param);
//
//		return selectOutboundScheduleGroupList2(param);
//	}


//
//
//	/**
//	 *지역
//	 *----도착항
//	 *--------출발항
//	 *------------선박
//	 */
//
//	private Map<String, Object> selectOutboundScheduleGroupList2(CommandMap param) throws SQLException {
//
//		List<ScheduleData>  li = selecteScheduleListByCondition(param);
//		
//		
//		List<String> vesselNames=li.stream().map(ScheduleData::getVessel)
//											.distinct()
//											.collect(Collectors.toList());
//		
//		
//		
//		
//
//		Map<String, Map<String, Map<String, List<ScheduleData>>>> areaList =  li.stream().collect(
//				Collectors.groupingBy(ScheduleData::getArea_name, // 지역
//						Collectors.groupingBy(ScheduleData::getFromPort, // 출발항
//								Collectors.groupingBy(ScheduleData::getPort))));// 도착항
//
//		CommandMap returnValue = new CommandMap();
//		for(String area : areaList.keySet() ) {
//			returnValue.put(area, areaList.get(area));
//		}
//
//		return returnValue;
//
//	}

//	public Map<String, Object> selectRouteScheduleGroupList(CommandMap param) throws SQLException {
//
//		List<ScheduleData>  li = selecteScheduleListByCondition(param);
//
//		Map<String, Map<String, List<ScheduleData>>> areaList =  li.stream().collect(
//				Collectors.groupingBy(ScheduleData::getArea_name, // 지역
//						Collectors.groupingBy(ScheduleData::getVessel))
//				);// 선박
//
//		CommandMap returnValue = new CommandMap();
//		for(String area : areaList.keySet() ) {
//			returnValue.put(area, areaList.get(area));
//		}
//
//		return returnValue;
//
//	}

	/**
	 *지역
	 *----도착항
	 *--------출발항
	 *------------선박
	 */

	private HashMap<String, Object> selectOutboundScheduleGroupList1(CommandMap param) throws SQLException {



		String inOutType  = (String) param.get("inOutType");

		CommandMap result = (CommandMap) selectListMap(param);

		List<HashMap<String, Object>> master = (List) result.get("master");

		HashMap<String, Object> areaList = new HashMap<String, Object>();

		Iterator<HashMap<String, Object>>iter = master.iterator();

		while(iter.hasNext())
		{
			HashMap<String, Object> item = iter.next();

			String area_name=(String) item.get("area_name");

			String toPort = (String) item.get(inOutType.equals("O")?"port":"fromPort");

			String fromPort = (String) item.get(inOutType.equals("O")?"fromPort":"port");

			//String vesselName = (String) item.get("vessel_name");

			if(areaList.containsKey(area_name))
			{  
				//해당 지역의 도착항 목록
				HashMap<String, Object> toPorts =(HashMap<String, Object>) areaList.get(area_name);

				//출발항 있을 경우
				if(toPorts.containsKey(toPort))					  
				{   
					//도착항 목록
					HashMap<String, Object> fromPorts =(HashMap<String, Object>) toPorts.get(toPort);

					// 도착항 있을 경우
					if(fromPorts.containsKey(fromPort))
					{
						List list =(List) fromPorts.get(fromPort); 
						list.add(item);

						// 출발일 기준으로 정렬
						Collections.sort(list, new AscendingFromDate() );
					}
					// 도착항 없을 경우
					else
					{
						ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
						scheduleList.add(item);

						fromPorts.put(fromPort, scheduleList);
					}
					// 스케줄 목록


					// 해당 도착항의 선박 목록


				}
				// 출발항 없을 경우
				else
				{
					// 신규 스케줄 리스트 생성
					ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
					scheduleList.add(item);

					HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
					newFromPorts.put(fromPort, scheduleList);

					// 신규 도착항 정보 생성

					toPorts.put(toPort, newFromPorts);

				}

			}
			else
			{
				ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
				scheduleList.add(item);


				HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
				newFromPorts.put(fromPort, scheduleList);

				HashMap<String, Object> newToPorts = new HashMap<String, Object>();

				newToPorts.put(toPort, newFromPorts);

				areaList.put(area_name, newToPorts);
			}
		}
		return areaList;		

	}
	class AscendingFromDate implements Comparator<HashMap<String,Object>> 
	{ 
		@Override 
		public int compare(HashMap<String,Object> one, HashMap<String,Object> two) {

			String fromDateOne = String.valueOf(one.get("DateF"));
			String fromDateTwo = String.valueOf(two.get("DateF"));

			return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

		} 
	}

	class AscendingFromDate2 implements Comparator<HashMap<String,Object>> 
	{ 
		@Override 
		public int compare(HashMap<String,Object> one, HashMap<String,Object> two) {

			String fromDateOne = String.valueOf(one.get("DateF"));
			String fromDateTwo = String.valueOf(two.get("DateF"));

			return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

		} 
	}
//
//	/** 지역
//	 *  ----출발항(외국항)
//	 *  --------선박
//	 *  -----------도착항(국내항)
//	 */
//	@Override
//	public CommandMap selectInboundScheduleGroupList(CommandMap param) throws SQLException {
//
//		return selectInboundScheduleGroupList3(param);
//
//	}


//
//	/** 지역
//	 *  ----출발항(외국항)
//	 *  --------선박
//	 *  -----------도착항(국내항)
//	 */
//	private CommandMap selectInboundScheduleGroupList3(CommandMap param) throws SQLException {
//
//		log.info("param:{}",param);
//
//		CommandMap result = (CommandMap) selectListMap(param);
//
//		List<CommandMap> master = (List) result.get("master");
//
//		CommandMap areaList = new CommandMap();
//
//		Iterator<CommandMap>iter = master.iterator();		
//
//		while(iter.hasNext())
//		{
//			CommandMap item = iter.next();
//
//			String area_name=(String) item.get("area_name");
//
//			String fromPort = (String) item.get("fromPort");
//
//			String toPort = (String) item.get("port");
//
//			String dateF = (String) item.get("DateF");
//
//			String vessel = (String) item.get("vessel");
//
//			if(areaList.containsKey(area_name))
//			{	
//				//출발항 목록
//				CommandMap fromPorts =(CommandMap) areaList.get(area_name);
//
//				//출발항 있을 경우
//				if(fromPorts.containsKey(fromPort))					  
//				{
//					//스케줄 목록
//					CommandMap vessels =(CommandMap) fromPorts.get(fromPort);
//
//
//					//vessel 있을 경우
//					if(vessels.containsKey(vessel+"$$"+dateF))					  
//					{
//						ArrayList<CommandMap> scheduleList =(ArrayList<CommandMap>) vessels.get(vessel+"$$"+dateF);
//						scheduleList.add(item);
//					}
//					else
//					{
//						ArrayList<CommandMap> scheduleList  = new ArrayList<CommandMap>();
//						scheduleList.add(item);
//						vessels.put(vessel+"$$"+dateF, scheduleList);
//					}
//
//
//				}
//				//출발항 없을 경우
//				else
//				{
//					CommandMap vessels = new CommandMap();
//
//					ArrayList<CommandMap> scheduleList  = new ArrayList<CommandMap>();
//
//					scheduleList.add(item);
//
//					/*
//					 * 
//					 *  스케줄 그룹 키 : vessel$$fromDate
//					 */
//
//					vessels.put(vessel+"$$"+dateF, scheduleList);
//
//					fromPorts.put(fromPort, vessels);
//				}
//
//
//
//			}
//			else
//			{
//				//스케줄 그룹
//				CommandMap vessels = new CommandMap();
//
//				ArrayList<CommandMap> scheduleList  = new ArrayList<CommandMap>();
//
//				scheduleList.add(item);
//
//				/*
//				 * 
//				 *  스케줄 그룹 키 : vessel$$fromDate
//				 */
//
//				vessels.put(vessel+"$$"+dateF, scheduleList);
//
//				// 출발항 그룹
//				CommandMap newFromPorts = new CommandMap();
//
//				newFromPorts.put(fromPort, vessels);	
//
//				areaList.put(area_name, newFromPorts);
//			}
//
//		}
//
//		//정렬 및 공동 배선
//
//		return areaList;
//
//
//
//	}
//
//	/*
//	 * map
//	 *    key(vessel)
//	 *    scheduleList
//	 *
//	 * list
//	 * 	
//	 * 
//	 * 
//	 * list
//	 * 	 map
//	 *      key:fromPort
//	 *      value:map 	 			
//	 *     			key:vesselName-date     			
//	 *     			value:scheduleList
//	 */
//	@Override
//	public CommandMap selectInboundScheduleGroupList1(CommandMap param) throws SQLException {
//
//		CommandMap result = (CommandMap) selectListMap(param);
//
//		List<CommandMap> master = (List) result.get("master");
//
//		CommandMap areaList = new CommandMap();
//
//		Iterator<CommandMap>iter = master.iterator();
//
//		int scheduleKey=0;
//
//		while(iter.hasNext())
//		{
//			HashMap<String, Object> item = iter.next();
//
//			String area_name	= (String) item.get("area_name");
//
//			String fromPort 	= (String) item.get("fromPort");
//
//			String toPort 		= (String) item.get("port");
//
//			String vessel 		= (String) item.get("vessel");
//
//			if(areaList.containsKey(area_name))
//			{
//				//출발항 목록
//				HashMap<String, Object> fromPorts =(HashMap<String, Object>) areaList.get(area_name);
//
//				//출발항 있을 경우
//				if(fromPorts.containsKey(fromPort))					  
//				{
//					//스케줄 목록
//					HashMap<String, Object> vessels =(HashMap<String, Object>) fromPorts.get(fromPort);
//
//
//					//vessel 있을 경우
//					if(vessels.containsKey(vessel))					  
//					{
//						ArrayList<HashMap<String, Object>> scheduleList =(ArrayList<HashMap<String, Object>>) vessels.get(vessel);
//						scheduleList.add(item);
//					}
//					else
//					{
//						ArrayList<HashMap<String, Object>> scheduleList  = new ArrayList<HashMap<String,Object>>();
//						scheduleList.add(item);
//						vessels.put(vessel+"-"+scheduleKey, scheduleList);
//					}
//
//
//				}
//				//출발항 없을 경우
//				else
//				{
//					HashMap<String, Object> vessels = new HashMap<String, Object>();
//
//					ArrayList<HashMap<String, Object>> scheduleList  = new ArrayList<HashMap<String,Object>>();
//
//					scheduleList.add(item);
//
//					/*
//					 * 
//					 *  스케줄 그룹 키 : vessel-fromDate
//					 */
//
//					vessels.put(vessel+"-"+scheduleKey, scheduleList);
//
//					fromPorts.put(fromPort, vessels);
//				}
//				// 
//			}
//			else
//			{	
//
//				//스케줄(선박) 목록
//				ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
//
//				scheduleList.add(item);
//
//				HashMap<String, Object> vessels = new HashMap<String, Object>();
//
//				vessels.put(vessel, scheduleList);
//
//				// 출발항 그룹
//				HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
//
//				newFromPorts.put(fromPort, vessels);	
//
//				areaList.put(area_name, newFromPorts);
//
//			}
//		}
//
//		return areaList;
//	}



	@Override
	public List<CommandMap> selecteScheduleListMapByCondition(CommandMap param) {

		log.debug("param:{}", param);

		ScheduleData schedule = ScheduleData.builder()
				.date_issue((String) param.get("date_issue"))
				.InOutType((String) param.get("inOutType"))
				.gubun((String) param.get("gubun"))
				.vessel((String) param.get("vessel"))
				.company_abbr((String) param.get("company_abbr"))
				.agent((String) param.get("agent"))
				.area_name((String) param.get("area_name"))
				.port((String) param.get("port"))
				.fromPort((String) param.get("fromPort"))
				.build();

		ArrayList<CommandMap> map = new ArrayList<CommandMap>();
		try {
			List<ScheduleData> li = schduleDAO.selectScheduleLisByCondition(schedule);


			for(ScheduleData item:li)
			{	
				map.add((CommandMap) objectMapper.convertValue(item, CommandMap.class));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}


	@Override
	public List<ScheduleData> selecteScheduleListByCondition(CommandMap param) throws SQLException {

		log.debug("param:{}", param);

		ScheduleData schedule = ScheduleData.builder()
				.date_issue((String) param.get("date_issue"))
				.InOutType((String) param.get("inOutType"))
				.fromPort((String) param.get("fromPort"))
				.port((String) param.get("port"))
				.gubun((String) param.get("gubun"))
				.vessel((String) param.get("vessel"))
				.table_id((String) param.get("table_id"))
				.company_abbr((String) param.get("company_abbr"))
				.agent((String) param.get("agent"))
				.area_name((String) param.get("area_name"))
				
				.build();

		return  schduleDAO.selectScheduleLisByCondition(schedule);
	}


	@Override
	public List<Schedule> selecteAll(CommandMap param) throws SQLException {

		log.debug("param:{}", param);

		Schedule schedule = Schedule.builder()
				.date_issue((String) param.get("date_issue"))
				.InOutType((String) param.get("inOutType"))
				.gubun((String) param.get("gubun"))
				.build();


		return  schduleDAO.selectAll(schedule);
	}

	@Override
	public List<Schedule> selecteScheduleListByCondition(Schedule param) throws SQLException {

		log.debug("param:{}", param);

		return  schduleDAO.selectScheduleLisByCondition(param);
	}



}
