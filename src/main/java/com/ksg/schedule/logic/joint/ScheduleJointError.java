package com.ksg.schedule.logic.joint;

import com.ksg.domain.ScheduleData;

public class ScheduleJointError extends Exception{
	Exception ee;
	ScheduleData data;
	public ScheduleData getData() {
		return data;
	}

	/*public ScheduleJointError(Exception e) {
		this.ee =e;
	}*/
	
	public ScheduleJointError(Exception e, ScheduleData data) {
		this.ee = e;
		this.data= data;
	}
	
	public String getMessage() {
		return ee.getMessage();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
