package com.ksg.commands.schedule.task;

import java.util.List;

import com.ksg.commands.LongTask;
import com.ksg.commands.schedule.route.RouteTaskNewVessel;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.joint.route.RouteSchedulePrint;
import com.ksg.schedule.logic.print.InboundScheduleJoint;
import com.ksg.schedule.logic.print.outbound.OutboundSchedulePrintV2;
import com.ksg.schedule.logic.print.outbound.OutboundSchedulePrintV3;
import com.ksg.schedule.logic.print.route.RouteAbstractSchedulePrint;
import com.ksg.schedule.logic.print.route.RouteScheduleJoint;
import com.ksg.schedule.logic.print.route.RouteSchedulePrintV4;

public class SortAllTask implements LongTask {

	private int orderBy;

	private boolean isRouteNew;

	private boolean isPrintNewRoute;
	
	private ShippersTable op;
	
	private String date_isusse;

	private boolean isPrintNewOutbound;
	
	CommandMap param =new CommandMap();

	public SortAllTask(ShippersTable op,int orderBy,boolean isNew,boolean isPrintInbound, boolean isPrintOutbound, boolean isPrintRoute) throws Exception {
		this.op = op;
		
		this.orderBy = orderBy;
		
		this.isPrintNewRoute = false;
		
		initPrintSchedule(isPrintOutbound, isPrintInbound, isPrintRoute);		

	}
	public SortAllTask(CommandMap param) throws Exception
	{	
		
		this.param = param;
		
		date_isusse 			= (String) param.get("date_isusse");
		
		boolean isPrintInbound 	= (boolean) param.get("isPrintInbound");

		boolean isPrintOutbound = (boolean) param.get("isPrintOutbound");

		boolean isPrintRoute 	= (boolean) param.get("isPrintRoute");

		orderBy 				= (int) param.get("orderBy");

		isPrintNewRoute 		= (boolean) param.get("isPrintNewRoute");
		
		isPrintNewOutbound 		= (boolean) param.get("isPrintNewOutbound");

		isRouteNew 				= (boolean) param.get("isNew");
		
		initPrintSchedule(isPrintOutbound, isPrintInbound, isPrintRoute);

	}

	private void initPrintSchedule(boolean isPrintOutbound, boolean isPrintInbound, boolean isPrintRoute) throws Exception
	{
		if(isPrintInbound) ScheduleManager.getInstance().addBulid(new InboundScheduleJoint());
		
		if(isPrintOutbound)
		{
//			List<ScheduleData> scheduleList = (List<ScheduleData>) param.get("scheduleList");
			
			ScheduleManager.getInstance().addBulid(isPrintNewOutbound?new OutboundSchedulePrintV3(param):new OutboundSchedulePrintV2());
		}
		
		if(isPrintRoute)
		{
			if(isRouteNew)
			{
				new RouteTaskNewVessel(op,orderBy).start();
			}
			else
			{
				List<ScheduleData> scheduleList = (List<ScheduleData>) param.get("scheduleList");
				
				
				RouteAbstractSchedulePrint joint = isPrintNewRoute?new RouteSchedulePrintV4(scheduleList, orderBy):new RouteScheduleJoint(scheduleList, orderBy);

				ScheduleManager.getInstance().addBulid(new RouteSchedulePrint(joint));
			}
		}

	}
	
	public Object startBuild()
	{
		ScheduleManager.getInstance().startBuild();
		
		return this;
	}



	public int getCurrent() {
		return 0;
	}

	public int getLengthOfTask() {
		return 0;
	}

	public String getMessage() {
		return null;
	}

	public boolean isDone() {
		return false;
	}

	public void stop() {

	}

}
