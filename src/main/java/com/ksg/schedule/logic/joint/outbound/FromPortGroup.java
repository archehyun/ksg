package com.ksg.schedule.logic.joint.outbound;

import java.sql.SQLException;
import java.util.HashMap;

import com.ksg.common.exception.VesselNullException;
import com.ksg.domain.ScheduleData;

/**
 * ��
 * @author ��â��
 *
 */
public class FromPortGroup extends HashMap<String,VesselGroup>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// �������� ������ ��� �߰�
	private String fromPortName;// ������ �ױ���

	public String getFromPortName() {
		return fromPortName;
	}
	public FromPortGroup(ScheduleData scheduleData) throws SQLException, VesselNullException {
		this.fromPortName =scheduleData.getFromPort();
		this.addSchedule(scheduleData);
	}
	public String getID() {
		return fromPortName;
	}
	/**
	 * @param scheduleData
	 * @throws SQLException
	 * @throws VesselNullException
	 */
	public void addSchedule(ScheduleData scheduleData) throws SQLException, VesselNullException {
		// �������� ������ ��� �߰�
		if(this.containsKey(scheduleData.getVessel()))
		{
			VesselGroup group = this.get(scheduleData.getVessel());
			// Ű : ���ڸ�
			group.add(scheduleData);

		}else// �ű� �׷� ����
		{
			// Ű : �����-�����-���ڸ�-������				
			VesselGroup group = new VesselGroup(scheduleData);				
			this.put(group.getID(), group);
		}
	}
	public String toString()
	{
		return "- "+fromPortName+" -";
	}

}
