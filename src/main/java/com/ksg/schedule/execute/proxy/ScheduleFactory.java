package com.ksg.schedule.execute.proxy;

import com.ksg.schedule.execute.Executeable;
import com.ksg.schedule.execute.build.impl.ConsoleScheduleBuild;
import com.ksg.schedule.execute.build.impl.InlnadScheduleBuild;
import com.ksg.schedule.execute.build.impl.NormalScheduleBuild;
import com.ksg.schedule.execute.joint.impl.InboundScheduleJoint;
import com.ksg.schedule.execute.joint.impl.OutboundScheduleJoint;
import com.ksg.schedule.execute.joint.impl.RouteScheduleJoint;

public class ScheduleFactory {
	
	public static Executeable createNormalScheduleBuild(String date_isusse)
	{
		return new ExecuteableSchedule(new NormalScheduleBuild(date_isusse));
	}
	
	public static Executeable createInlnadScheduleBuild()
	{
		return new ExecuteableSchedule(new InlnadScheduleBuild());
	}
	
	public static Executeable createConsoleScheduleBuild()
	{
		return new ExecuteableSchedule(new ConsoleScheduleBuild());
	}
	
	public static Executeable createInboundScheduleJoint(String date_isusse)
	{
		return new ExecuteableSchedule(new InboundScheduleJoint(date_isusse));
	}
	
	public static Executeable createOutboundScheduleJoint(String date_isusse)
	{
		return new ExecuteableSchedule(new OutboundScheduleJoint(date_isusse));
	}

	public static Executeable createRoputeScheduleJoint(String date_isusse) {
		return new ExecuteableSchedule(new RouteScheduleJoint(date_isusse));
	}

	
	

}
