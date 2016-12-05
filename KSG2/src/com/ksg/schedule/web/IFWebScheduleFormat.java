package com.ksg.schedule.web;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;

public interface IFWebScheduleFormat {
	public String toWebScheduleString(ScheduleData data);
	public ScheduleData createWebScheduleData(ShippersTable table,String inOutType,int vslIndex,
			Vessel vesselInfo,int fromPortIndex,int toPortIndex, String fromPort, String toPort);
	public String getFileName();
	public String getErrorFileName();

}
