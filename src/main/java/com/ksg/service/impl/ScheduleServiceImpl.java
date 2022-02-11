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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.SchduleDAO;
import com.ksg.dao.impl.ADVScheduleDAO;
import com.ksg.dao.impl.ScheduleDAOImpl;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.KSGHashMap;
import com.ksg.service.ScheduleService;
@SuppressWarnings("unchecked")

public class ScheduleServiceImpl implements ScheduleService{

	private SchduleDAO schduleDAO;

	private Vector<KSGHashMap> ksgHashMapList;
	
	private ADVScheduleDAO advScheduleDAO;
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	KSGModelManager manager = KSGModelManager.getInstance();
	

	public ScheduleServiceImpl() {
		schduleDAO = new ScheduleDAOImpl();
		advScheduleDAO = new ADVScheduleDAO();
	}

	public List getInboundPortList() throws SQLException{
		return schduleDAO.getInboundPortList();
	}

	public int deleteSchedule() throws SQLException {
		return schduleDAO.deleteSchedule();

	}

	public List getPortlistBySchedule(String inOutType) throws SQLException
	{
		return schduleDAO.getPortListBySchedule(inOutType);

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

	public List getInboundScheduleList(String port, String toPort)
	throws SQLException {
		return schduleDAO.getInboundScheduleList(port, toPort);
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
		// TODO Auto-generated method stub
		return schduleDAO.getScheduleListByArea(data);
	}

	public List getScheduleListByVesselVoy(String vessel, String voy)
			throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getScheduleListByVesselVoy(vessel,voy );
	}

	public List getScheduleListByToFrom(String areaCode, String areaCode2,
			String InOut, int forSch) throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getScheduleListByToFrom(areaCode, areaCode2,
				InOut, forSch);
	}

	public List getScheduleListGroupByCompany(String searchDate)
			throws SQLException {
		return schduleDAO.getScheduleListGroupByCompany(searchDate);
	}

	public List getScheduleList(ScheduleData param) throws SQLException {
		logger.info("param:{}",param);
		return schduleDAO.getScheduleList(param);
	}

	public List getScheduleDateList() throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getScheduleDateList();
	}


	public int getScheduleTotalCount() throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getScheduleTotalCount();
	}

	public int getScheduleNTopCount(ScheduleData data) throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getScheduleNTopCount(data);
	}

	public List getScheduleListNTop(ScheduleData data) throws SQLException
	{
		return schduleDAO.getScheduleListNTop(data);
	}

	public List getOutboundFromPortTSList(String port) throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getOutboundFromPortTSList(port);
	}

	public List<ScheduleData> getConsoleScheduleList(String port,
			String fromPort) throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getConsoleScheduleList(port, fromPort);
	}

	public List getConsoleScheduleList() throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getConsoleScheduleList();
	}
	
	@Override
	public List getConsoleScheduleList(ScheduleData data) throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getConsoleScheduleList(data);
	}
	
	public List<ScheduleData> getInlandScheduleList(ScheduleData data) 
			throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getInlandScheduleList(data);
	}

	public List getInlandScheduleDateList() throws SQLException {
		// TODO Auto-generated method stub
		return schduleDAO.getInlandScheduleDateList();
	}

	public int deleteInlnadSchedule() throws SQLException {
		return schduleDAO.deleteInlandSchedule();
		
	}

	@Override
	public List<String> getOutboundAreaList() throws SQLException {
		return schduleDAO.getOutboundAreaList();
		
	}
	public HashMap<String, Object> selectScheduleList(HashMap<String, Object> commandMap) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", schduleDAO.selectCount(commandMap));
		
		resultMap.put("master", schduleDAO.selectList(commandMap));
		
		return resultMap;

	}
	

	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectScheduledAreaList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return advScheduleDAO.selectScheduledAreaList(commandMap);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectScheduleJointList(Map<String, Object> commandMap) throws SQLException {
		
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
	public HashMap<String, Object> selectList(HashMap<String, Object> param) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", schduleDAO.selectCount(param));
		
		resultMap.put("master", schduleDAO.selectList(param));
		
		return resultMap;
	}

}
