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
package com.ksg.service;

import java.sql.SQLException;

import java.util.HashMap;
import java.util.List;

import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
public interface ScheduleService {

	public static final String INBOUND = "I";
	
	
	public static final String OUTBOUND = "O";
	
	
	public List selectScheduleList(HashMap<String, Object> param) throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException
	 */
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
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	public List getInboundtoPortList(String port)							throws SQLException;
	/**
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	public List getOutboundFromPortList(String port)						throws SQLException;
	/**
	 * 
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List getOutboundPortList()										throws SQLException;
	
	
	public List<ScheduleData> getConsoleScheduleList(String port, String fromPort)		throws SQLException;
	/**
	 * @param port
	 * @param fromPort
	 * @return
	 * @throws SQLException
	 */
	public List<ScheduleData> getOutboundScheduleList()		throws SQLException;
	
	public List<ScheduleData> getOutboundScheduleList(String port, String fromPort)		throws SQLException;
	/**
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	public PortInfo getPortInfoByPortAbbr(String port)						throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleList() 											throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getTableAndADVList()										throws SQLException;
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
	public boolean isInPort(String port_name) 								throws SQLException;
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public int updateScheduleData(ScheduleData data)						throws SQLException;
	
	/**
	 * @param area_code
	 * @param e_i
	 * @param forSch
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleListByArea(ScheduleData param)throws SQLException;
	public List getScheduleListByVesselVoy(String vessel, String voy)throws SQLException;
	public List getScheduleListByToFrom(String areaCode, String areaCode2,
			String InOut, int forSch)throws SQLException;
	public List getScheduleList(String date)throws SQLException;
	public List<ScheduleData> getScheduleList(ScheduleData data)throws SQLException;
	public List getScheduleListGroupByCompany(String searchDate)throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getScheduleDateList()throws SQLException;
	public int getScheduleTotalCount()throws SQLException;
	public int getScheduleNTopCount(ScheduleData data)throws SQLException;
	public List getScheduleListNTop(ScheduleData data) throws SQLException;
	public List getOutboundFromPortTSList(String port)throws SQLException;
	public List<ScheduleData> getConsoleScheduleList()throws SQLException;
	
	/**콘솔 스케줄 조회 
	 * @param data ScheduleData 클래스
	 * @return
	 * @throws SQLException
	 */
	public List<ScheduleData> getConsoleScheduleList(ScheduleData data) throws SQLException;
	public List<ScheduleData> getInlandScheduleList(ScheduleData data) throws SQLException;
	public List getInlandScheduleDateList()throws SQLException;
	public int deleteInlnadSchedule()throws SQLException;
	public List<ScheduleData> getOutboundScheduleList(ScheduleData op)throws SQLException;
	public List<String> getOutboundAreaList()throws SQLException;
	


}
