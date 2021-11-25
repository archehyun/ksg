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
import java.util.List;

import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
@SuppressWarnings("unchecked")
public interface SchduleDAO {
	
	public int deleteSchedule()												throws SQLException;
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
	public List getPortList()												throws SQLException;
	/**
	 * @param inOutType
	 * @return
	 * @throws SQLException
	 */
	public List getPortListBySchedule(String inOutType)						throws SQLException;
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
	public ScheduleData insertScheduleData(ScheduleData data)				throws SQLException;
	public ScheduleData insertInlandScheduleData(ScheduleData data)				throws SQLException;
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
	public List getScheduleListByVesselVoy(String vessel, String voy)		throws SQLException;
	public List getScheduleListByToFrom(String areaCode, String areaCode2,
			String inOut, int forSch)										throws SQLException;
	public List getScheduleList(String date)throws SQLException;
	public List getScheduleListGroupByCompany(String searchDate)throws SQLException;
	public List getScheduleList(ScheduleData data)throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleDateList()throws SQLException;
	public int getScheduleTotalCount()throws SQLException;
	public List getScheduleListNTop(ScheduleData data) throws SQLException;
	public int getScheduleNTopCount(ScheduleData data)throws SQLException;
	public List getOutboundFromPortTSList(String port)throws SQLException;
	public List<ScheduleData> getConsoleScheduleList(String port,
			String fromPort) throws SQLException;
	public List getConsoleScheduleList()throws SQLException;
	public List<ScheduleData> getConsoleScheduleList(ScheduleData data)		throws SQLException;
	public List<ScheduleData> getInlandScheduleList(ScheduleData data)		throws SQLException;
	public List getOutboundScheduleList()throws SQLException;
	public List getInlandScheduleDateList()throws SQLException;
	public int deleteInlandSchedule()throws SQLException;
	public List getOutboundScheduleList(ScheduleData data)throws SQLException;
	public List<String> getOutboundAreaList()throws SQLException;
}
