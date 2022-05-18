package com.ksg.schedule.execute.proxy;

import com.ksg.schedule.execute.Executeable;
import com.ksg.schedule.execute.build.impl.ConsoleScheduleBuild;
import com.ksg.schedule.execute.build.impl.InlnadScheduleBuild;
import com.ksg.schedule.execute.build.impl.NormalScheduleBuild;
import com.ksg.schedule.execute.joint.impl.InboundScheduleJoint;
import com.ksg.schedule.execute.joint.impl.OutboundScheduleJoint;

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
	
	public static Executeable createInboundScheduleJoint()
	{
		return new ExecuteableSchedule(new InboundScheduleJoint());
	}
	
	public static Executeable createOutboundScheduleJoint()
	{
		return new ExecuteableSchedule(new OutboundScheduleJoint());
	}
	

}
