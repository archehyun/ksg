package com.ksg.schedule.logic.print;

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;
import com.dtp.api.schedule.joint.print.SchedulePrintParam;
import com.dtp.api.schedule.joint.print.inbound.InboundScheduleJoint;
import com.dtp.api.schedule.joint.print.inbound.InboundSchedulePrintFile;
import com.dtp.api.schedule.joint.print.outbound.OutboundSchedulePrintFile;
import com.dtp.api.schedule.joint.print.outbound.OutboundSchedulePrintV2;
import com.dtp.api.schedule.joint.print.route.RouteScheduleJointV1;
import com.dtp.api.schedule.joint.print.route.RouteSchedulePrintFile;
import com.ksg.common.model.CommandMap;


public class SchedulePrintFactory {
//	public static AbstractSchedulePrint createSchedulePrint(String scheduleType,SchedulePrintParam param) throws Exception
//	{
//		switch (scheduleType) {
//		case "Outbound": return new OutboundSchedulePrintFile(param);
//		case "Inbound": return new InboundSchedulePrintFile(param);
//		case "Route": return new RouteSchedulePrintFile(param);
//		default: throw new IllegalArgumentException(param.getScheduleType());
//		}
//	}
//	
	public static AbstractSchedulePrint createSchedulePrint(String scheduleType, CommandMap param) throws Exception
	{
		switch (scheduleType) {
		case "Outbound"	: return (boolean) (param.get("isPrintNewOutbound"))? 	new OutboundSchedulePrintFile(param):new OutboundSchedulePrintV2();
		case "Inbound"	: return (boolean) (param.get("isPrintNewInbound"))?  	new InboundSchedulePrintFile(param):new InboundScheduleJoint();
		case "Route"	: return (boolean) (param.get("isPrintNewRoute"))? 		new RouteSchedulePrintFile(param):new RouteScheduleJointV1(param);
		default: throw new IllegalArgumentException(scheduleType);
		}
	}

}
