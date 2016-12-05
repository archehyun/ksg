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
package com.ksg.dao.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.dao.SqlMapManager;
import com.ksg.dao.schedule.SchduleDAO;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;

public class ScheduleDAOImpl implements SchduleDAO {

	private SqlMapClient sqlMap;
	public ScheduleDAOImpl() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		public List getScheduleList() throws SQLException {
		return sqlMap.queryForList("Schedule.selectScheduleList");
	}

	public List getTableAndADVList() throws SQLException {
		return sqlMap.queryForList("Schedule.selectTableAndADVList");
	}

	public List getPortList() throws SQLException {
		return sqlMap.queryForList("Base.selectPort_nameList");
	}


	public ScheduleData insertScheduleData(ScheduleData data) throws SQLException {
		return (ScheduleData) sqlMap.insert("Schedule.insertSchedule", data);
	}
	public ScheduleData insertInlandScheduleData(ScheduleData data) throws SQLException {
		return (ScheduleData) sqlMap.insert("Schedule.insertInlandSchedule", data);
	}
	public int deleteSchedule() throws SQLException {
		return sqlMap.delete("Schedule.deleteSchedule");
	}

	public List getScheduleListOrderBy(String inOutType) throws SQLException {
		return sqlMap.queryForList("Schedule.selectScheduleListOrderBy",inOutType);
	}

	public int updateScheduleData(ScheduleData data) throws SQLException {
		return 0;
	}

	public List getPortListBySchedule(String inOutType) throws SQLException {
		if(inOutType.equals("I"))
		{
			return sqlMap.queryForList("Schedule.selectPort_nameListByInbound");
		}else
		{
			return sqlMap.queryForList("Schedule.selectPort_nameListByOutbound");
		}
		
	}
	
	

	public List getScheduleListByPort(String port) throws SQLException {
		return sqlMap.queryForList("Schedule.selectScheduleList",port);
	}

	public List getScheduleListByFromPort(String fromPort) throws SQLException{
		return sqlMap.queryForList("Schedule.selectScheduleListByFromPort",fromPort);
	}

	public List getOutboundFromPortList(String port) throws SQLException {
		return sqlMap.queryForList("Schedule.selectOutboundFromPortList",port);
	}

	public List getOutboundScheduleList(String port, String fromPort)
			throws SQLException {
		ScheduleData data = new ScheduleData();
		data.setPort(port);
		data.setFromPort(fromPort);
		return sqlMap.queryForList("Schedule.selectOutboundScheduleList",data);
	}
	public List getOutboundScheduleList()
			throws SQLException {
		
		return sqlMap.queryForList("Schedule.selectOutboundScheduleList");
	}
	public List getOutboundScheduleList(ScheduleData data) 
			throws SQLException {
		
		return sqlMap.queryForList("Schedule.selectOutboundScheduleList",data);
	}

	public List getOutboundPortList() throws SQLException {
		return sqlMap.queryForList("Schedule.selectOutboundPortList");
	}

	public List getInboundPortList() throws SQLException {
		return sqlMap.queryForList("Schedule.selectInboundPortList");
	}

	public List getInboundtoPortList() throws SQLException {
		return sqlMap.queryForList("Schedule.selectnboundtoPortList");
	}

	public List getInboundScheduleList(String port, String toPort)
			throws SQLException {
		ScheduleData data = new ScheduleData();
		data.setPort(toPort);
		data.setFromPort(port);
		return sqlMap.queryForList("Schedule.selectInboundScheduleList",data);
	}

	public PortInfo getPortInfoByPortAbbr(String port) throws SQLException {
		return (PortInfo) sqlMap.queryForObject("Schedule.selectPortInfoByPortAbbr",port);
	}

	public boolean isInPort(String port_name) throws SQLException {
		 
		PortInfo info=(PortInfo) sqlMap.queryForObject("Schedule.selectInPort",port_name);
		if(info==null)
		
			return false;
		
		if(info.getPort_nationality().equals("South Korea"))
		{
			System.out.println("in:"+port_name);
			return true;
		}else
			
		{
			return false;
		}
	}

	public List getInboundScheduleList(String port) throws SQLException {
		ScheduleData data = new ScheduleData();
		data.setFromPort(port);
		return sqlMap.queryForList("Schedule.selectInboundScheduleList",data);
	}

	public List getInboundScheduleListByVessel(String port, String vessel)
			throws SQLException {
		ScheduleData data = new ScheduleData();
		data.setFromPort(port);
		data.setVessel(vessel);
		return sqlMap.queryForList("Schedule.selectInboundScheduleListByVessel",data);
	}

	public List getScheduleListByArea(ScheduleData data)
			throws SQLException {
		
		//data.se
		return sqlMap.queryForList("Schedule.selectScheduleListByArea",data);
	}

	public List getScheduleListByVesselVoy(String vessel, String voy)
			throws SQLException {
		ScheduleData data = new ScheduleData();
		data.setVessel(vessel);
		data.setVoyage_num(voy);
		data.setInOutType("O");
		return sqlMap.queryForList("Schedule.selectScheduleListByVesselVoy",data);
	}

	public List getScheduleListByToFrom(String areaCode, String areaCode2,
			String inOut, int forSch) throws SQLException {
		ScheduleData data = new ScheduleData();
		
		data.setPort(areaCode);
		data.setFromPort(areaCode2);
		data.setInOutType(inOut);
		return sqlMap.queryForList("Schedule.selectScheduleListByFromTo",data);
	}

	public List getScheduleList(String date) throws SQLException {
		ScheduleData data= new ScheduleData();
		data.setDate_issue(date);
		return sqlMap.queryForList("Schedule.selectScheduleList",data);
	}

	public List getScheduleListGroupByCompany(String searchDate)
			throws SQLException {
		ScheduleData data= new ScheduleData();
		data.setDate_issue(searchDate);
		return sqlMap.queryForList("Schedule.selectScheduleListGroupByCompany",searchDate);
	}

	public List getScheduleList(ScheduleData data) throws SQLException {
		return sqlMap.queryForList("Schedule.selectScheduleList",data);
	}
	public List getScheduleListNTop(ScheduleData data) throws SQLException {
		return sqlMap.queryForList("Schedule.selectScheduleListNTop",data);
	}

	public List getScheduleDateList() throws SQLException {
		return sqlMap.queryForList("Schedule.selectScheduleDateList");
	}

	public int getScheduleTotalCount() throws SQLException {
		return (Integer) sqlMap.queryForObject("Schedule.selectScheduleTotalCount");
	}

	public int getScheduleNTopCount(ScheduleData data) throws SQLException 
	{	
		return (Integer) sqlMap.queryForObject("Schedule.getScheduleNTopCount",data);
	}

	public List getOutboundFromPortTSList(String port) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Schedule.selectOutboundFromPortTSList",port);
	}

	public List<ScheduleData> getConsoleScheduleList(String port,
			String fromPort) throws SQLException {
		// TODO Auto-generated method stub
		ScheduleData data = new ScheduleData();
		data.setPort(port);
		data.setFromPort(fromPort);
		return sqlMap.queryForList("Schedule.selectConsoleList",data);
	}
	public List<ScheduleData> getConsoleScheduleList() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Schedule.selectConsoleList");
	}
	public List<ScheduleData> getConsoleScheduleList(ScheduleData data) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Schedule.selectConsoleList",data);
	}
	public List<ScheduleData> getInlandScheduleList(ScheduleData data) throws SQLException{
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Schedule.selectInlandList",data);
	}
	public List getInlandScheduleDateList() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Schedule.selectInlandScheduleDateList");
	}
	public int deleteInlandSchedule() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.delete("Schedule.deleteInlnadSchedule");
	}
	@Override
	public List<String> getOutboundAreaList() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Schedule.selectOutboundAreaList");
	}

	

	
}
