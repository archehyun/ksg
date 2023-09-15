package com.dtp.api.schedule.joint.print.route;

import java.util.List;

public interface RouteJointSubject {
	
	public void createScheduleAndAddGroup(List group, List scheduleList, String areaName, String vesselName);
}
