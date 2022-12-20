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
package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Schedule;
import com.ksg.domain.ScheduleData;
@SuppressWarnings("unchecked")
public interface SchduleDAO {
	
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getInboundPortList()										throws SQLException;
	/**
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	public List getInboundScheduleList(String port)							throws SQLException;
	/**
	 * @param port
	 * @param toPort
	 * @return
	 * @throws SQLException
	 */
	public List getInboundScheduleList(String port, String toPort)			throws SQLException;
	/**
	 * @param port
	 * @param vessel
	 * @return
	 * @throws SQLException
	 */
	public List getInboundScheduleListByVessel(String port, String vessel)	throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getInboundtoPortList()										throws SQLException;
	/**
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	public List getOutboundFromPortList(String port) 						throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getOutboundPortList()										throws SQLException;
	/**
	 * @param port
	 * @param fromPort
	 * @return
	 * @throws SQLException
	 */
	public List getOutboundScheduleList(String port, String fromPort) 		throws SQLException;
	/**
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	public PortInfo getPortInfoByPortAbbr(String port) 						throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */

	/**
	 * @param inOutType
	 * @return
	 * @throws SQLException
	 */

	/**
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleList() 											throws SQLException;
	/**
	 * @param area_code
	 * @param e_i
	 * @param forSch
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleListByArea(ScheduleData data)throws SQLException;
	/**
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleListByFromPort(String port) 						throws SQLException;
	/**
	 * @param port2
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleListByPort(String port2)							throws SQLException;
	/**
	 * @param inOutType
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleListOrderBy(String inOutType)					throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getTableAndADVList() 										throws SQLException;
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	
	
	/**
	 * @param port_name
	 * @return
	 * @throws SQLException
	 */
	public boolean isInPort(String port_name)								throws SQLException;
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public int updateScheduleData(ScheduleData data)						throws SQLException;
	@Deprecated
	public List getScheduleListByVesselVoy(String vessel, String voy)		throws SQLException;
	@Deprecated
	public List getScheduleListByToFrom(String areaCode, String areaCode2,
			String inOut, int forSch)										throws SQLException;
	public List getScheduleList(String date)throws SQLException;
	@Deprecated
	public List getScheduleListGroupByCompany(String searchDate)throws SQLException;
	@Deprecated
	public List getScheduleList(ScheduleData data)throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List selectScheduleDateList()throws SQLException;
	@Deprecated
	public int getScheduleTotalCount()throws SQLException;
	@Deprecated
	public List getScheduleListNTop(ScheduleData data) throws SQLException;
	@Deprecated
	public int getScheduleNTopCount(ScheduleData data)throws SQLException;
	
//	public List getOutboundFromPortTSList(String port)throws SQLException;
	@Deprecated
	public List<ScheduleData> getConsoleScheduleList(String port,
			String fromPort) throws SQLException;
	@Deprecated
	public List getConsoleScheduleList()throws SQLException;
	public List<ScheduleData> getConsoleScheduleList(ScheduleData data)		throws SQLException;
	@Deprecated
	public List<ScheduleData> getInlandScheduleList(ScheduleData data)		throws SQLException;
	
	public List getOutboundScheduleList()throws SQLException;
	@Deprecated
	public List getInlandScheduleDateList()throws SQLException;
	@Deprecated
	public int deleteInlandSchedule()throws SQLException;
	@Deprecated
	public List getOutboundScheduleList(ScheduleData data)throws SQLException;
	@Deprecated
	public List<String> getOutboundAreaList()throws SQLException;
	
	
	public List<CommandMap> selectList(CommandMap param) throws SQLException;
	
	public List<Map<String, Object>> selectInlnadList(HashMap<String, Object> param) throws SQLException;
	
	public int selectCount(Map<String, Object> commandMap) throws SQLException;
	
	public Object selectListByPage(HashMap<String, Object> param)throws SQLException;
	
	public Object insertSchedule(HashMap<String, Object> param) throws SQLException;
	
	public ScheduleData insertInlandScheduleData(ScheduleData data)				throws SQLException;
	
	public ScheduleData insertScheduleData(ScheduleData data)				throws SQLException;
	
	public int deleteSchedule()												throws SQLException;
	
	public List selectInlandScheduleDateList()throws SQLException;
	
	public List<ScheduleData> selectScheduleLisByCondition(ScheduleData schedule) throws SQLException;
	
	public List<Schedule> selectScheduleLisByCondition(Schedule schedule) throws SQLException;
	
	public List<Schedule> selectAll(Schedule schedule) throws SQLException;
	
	public List<Schedule> selectAll() throws  Exception;
	
	
	
}
