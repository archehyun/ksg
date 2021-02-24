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
package com.ksg.schedule;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.KSGHashMap;
import com.ksg.schedule.service.SchduleDAO;
import com.ksg.schedule.service.ScheduleDAOImpl;
@SuppressWarnings("unchecked")
public class ScheduleServiceImpl implements ScheduleService{

	private SchduleDAO schduleDAO;

	private Vector<KSGHashMap> ksgHashMapList;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	KSGModelManager manager = KSGModelManager.getInstance();

	public ScheduleServiceImpl() {
		schduleDAO = new ScheduleDAOImpl();
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

	public List getScheduleList(String date) throws SQLException {
		return schduleDAO.getScheduleList(date);
	}

	public List getScheduleListGroupByCompany(String searchDate)
			throws SQLException {
		return schduleDAO.getScheduleListGroupByCompany(searchDate);
	}

	public List getScheduleList(ScheduleData data) throws SQLException {
		logger.debug("");
		return schduleDAO.getScheduleList(data);
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
}
