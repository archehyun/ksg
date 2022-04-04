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
import java.util.Map;

import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
public interface ScheduleService extends PageService{	
	

	public static final String INBOUND = "I";
	
	
	public static final String OUTBOUND = "O";	
	
	public HashMap<String, Object> selectScheduleList(HashMap<String, Object> commandMap) throws SQLException;
	
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
	//public List getInboundScheduleList(String port, String toPort)			throws SQLException;
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
	//public List getInboundtoPortList(String port)							throws SQLException;
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
	@Deprecated
	public List getOutboundPortList()										throws SQLException;
	
	
	@Deprecated
	public List<ScheduleData> getConsoleScheduleList(String port, String fromPort)		throws SQLException;
	/**
	 * @param port
	 * @param fromPort
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public List<ScheduleData> getOutboundScheduleList()		throws SQLException;
	
	@Deprecated
	public List<ScheduleData> getOutboundScheduleList(String port, String fromPort)		throws SQLException;
	/**
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public PortInfo getPortInfoByPortAbbr(String port)						throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public List getScheduleList() 											throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
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
	@Deprecated
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
	//public List getScheduleList(String date)throws SQLException;
	public List<ScheduleData> getScheduleList(ScheduleData data)throws SQLException;
	public List getScheduleListGroupByCompany(String searchDate)throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List selectScheduleDateList()throws SQLException;
	
	public int getScheduleTotalCount()throws SQLException;
	
	public int getScheduleNTopCount(ScheduleData data)throws SQLException;
	
	public List getScheduleListNTop(ScheduleData data) throws SQLException;

	
	/**�ܼ� ������ ��ȸ 
	 * @param data ScheduleData Ŭ����
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public List<ScheduleData> getConsoleScheduleList(ScheduleData data) throws SQLException;
	
	@Deprecated
	public List<ScheduleData> getInlandScheduleList(ScheduleData data) throws SQLException;
	
	@Deprecated
	public List getInlandScheduleDateList()throws SQLException;
	
	
	
	
	@Deprecated
	public List<ScheduleData> getOutboundScheduleList(ScheduleData op)throws SQLException;
	
	@Deprecated
	public List<String> getOutboundAreaList()throws SQLException;

	public int getTotalCount(HashMap<String, Object> commandMap) throws SQLException;

	public List<Map<String, Object>> selectInboundScheduleJointList(Map<String, Object> commandMap) throws SQLException;

	List<Map<String, Object>> selectScheduleJointList(Map<String, Object> commandMap) throws SQLException;

	public HashMap<String, Object> selectList(HashMap<String, Object> param) throws SQLException;
	
	public HashMap<String, Object> selectGroupList(HashMap<String, Object> param) throws SQLException;
	
	
	public int deleteInlnadSchedule()throws SQLException;
	
	
	
	


}


