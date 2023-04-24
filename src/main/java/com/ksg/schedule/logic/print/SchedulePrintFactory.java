package com.ksg.schedule.logic.print;

import com.ksg.common.model.CommandMap;
import com.ksg.schedule.logic.print.inbound.InboundSchedulePrintFile;
import com.ksg.schedule.logic.print.outbound.OutboundSchedulePrintFile;
import com.ksg.schedule.logic.print.outbound.OutboundSchedulePrintV2;
import com.ksg.schedule.logic.print.route.RouteScheduleJointV1;
import com.ksg.schedule.logic.print.route.RouteSchedulePrintFile;

public class SchedulePrintFactory {
	public static AbstractSchedulePrint createSchedulePrint(String scheduleType,SchedulePrintParam param) throws Exception
	{
		switch (scheduleType) {
		case "Outbound": return new OutboundSchedulePrintFile(param);
		case "Inbound": return new InboundSchedulePrintFile(param);
		case "Route": return new RouteSchedulePrintFile(param);
		default: throw new IllegalArgumentException(param.getScheduleType());
		}
	}
	
	public static AbstractSchedulePrint createSchedulePrint(String scheduleType, CommandMap param) throws Exception
	{
		switch (scheduleType) {
		case "Outbound": return (boolean) (param.get("isPrintNewOutbound"))? new OutboundSchedulePrintFile(param):new OutboundSchedulePrintV2();
		case "Inbound": return new InboundSchedulePrintFile(param);
		case "Route": return (boolean) (param.get("isPrintNewRoute"))? new RouteSchedulePrintFile(param):new RouteScheduleJointV1(param);
		default: throw new IllegalArgumentException(scheduleType);
		}
	}

}
