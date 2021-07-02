package com.ksg.schedule.logic.joint.outbound;

import java.sql.SQLException;
import java.util.HashMap;

import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.PortNullException;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.VesselNullException;
import com.ksg.schedule.logic.joint.DefaultScheduleJoint;

/**
 * @author 박창현
 *
 */
public class ToPortGroup extends HashMap<String,FromPortGroup>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScheduleManager scheduleManager = ScheduleManager.getInstance();
	
	// 항구 국적
	private String port_nationality;
	
	// 출발항구 명
	private String toPort;
	
	public String getToPort() {
		return toPort;
	}
	
	
	public String getPort_nationality() {
		return port_nationality;
	}

	public ToPortGroup(String toPortName) throws SQLException, PortNullException {
		this.toPort =toPortName;
		PortInfo info=scheduleManager.searchPort(toPortName);
		this.port_nationality = info.getPort_nationality();
	}

	public ToPortGroup(ScheduleData scheduleData) throws SQLException, VesselNullException {
		this.toPort =scheduleData.getPort();
		this.addSchedule(scheduleData);
	}
	public void addSchedule(ScheduleData data) throws SQLException, VesselNullException {

		/*
		 * 항구명이 부산 신항일 경우에는 부산항으로 이름 변경
		 */
		PortInfo info=scheduleManager.searchPort(data.getFromPort());

		if(info.getPort_name().equals(DefaultScheduleJoint.BUSAN_NEW_PORT))
		{
			data.setFromPort(DefaultScheduleJoint.BUSAN);
		}				

		// 스케줄이 존재할 경우 추가
		if(this.containsKey(data.getFromPort()))
		{
			FromPortGroup group = this.get(data.getFromPort());
			// 키 : 출발항-출발일-선박명-도착일
			group.addSchedule(data);

		}else// 신규 그룹 생성
		{
			// 키 : 출발항-출발일-선박명-도착일				
			FromPortGroup group = new FromPortGroup(data);				
			this.put(group.getID(), group);
		}

	}
	public String toString()
	{
		return toPort;
	}

}
