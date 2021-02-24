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
 * @author ��â��
 *
 */
public class ToPortGroup extends HashMap<String,FromPortGroup>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScheduleManager scheduleManager = ScheduleManager.getInstance();
	
	// �ױ� ����
	private String port_nationality;
	
	// ����ױ� ��
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
		 * �ױ����� �λ� ������ ��쿡�� �λ������� �̸� ����
		 */
		PortInfo info=scheduleManager.searchPort(data.getFromPort());

		if(info.getPort_name().equals(DefaultScheduleJoint.BUSAN_NEW_PORT))
		{
			data.setFromPort(DefaultScheduleJoint.BUSAN);
		}				

		// �������� ������ ��� �߰�
		if(this.containsKey(data.getFromPort()))
		{
			FromPortGroup group = this.get(data.getFromPort());
			// Ű : �����-�����-���ڸ�-������
			group.addSchedule(data);

		}else// �ű� �׷� ����
		{
			// Ű : �����-�����-���ڸ�-������				
			FromPortGroup group = new FromPortGroup(data);				
			this.put(group.getID(), group);
		}

	}
	public String toString()
	{
		return toPort;
	}

}
