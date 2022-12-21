package com.ksg.schedule.logic.joint.outbound;

import java.sql.SQLException;
import java.util.HashMap;

import com.ksg.common.exception.VesselNullException;
import com.ksg.domain.ScheduleData;

/**
 * ㅊ
 * @author 박창현
 *
 */
public class FromPortGroup extends HashMap<String,VesselGroup>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 스케줄이 존재할 경우 추가
	private String fromPortName;// 도착항 항구명

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
		// 스케줄이 존재할 경우 추가
		if(this.containsKey(scheduleData.getVessel()))
		{
			VesselGroup group = this.get(scheduleData.getVessel());
			// 키 : 선박명
			group.add(scheduleData);

		}else// 신규 그룹 생성
		{
			// 키 : 출발항-출발일-선박명-도착일				
			VesselGroup group = new VesselGroup(scheduleData);				
			this.put(group.getID(), group);
		}
	}
	public String toString()
	{
		return "- "+fromPortName+" -";
	}

}
