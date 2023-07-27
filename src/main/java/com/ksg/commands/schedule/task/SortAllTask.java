package com.ksg.commands.schedule.task;

import com.ksg.commands.LongTask;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.joint.route.RouteSchedulePrint;
import com.ksg.schedule.logic.print.InboundScheduleJoint;
import com.ksg.schedule.logic.print.SchedulePrintFactory;

public class SortAllTask implements LongTask {

	private int orderBy;

	private boolean isRouteNew;

	private boolean isPrintNewRoute;
	
	private ShippersTable op;
	
	private String date_isusse;

	private boolean isPrintNewOutbound;
	
	CommandMap param =new CommandMap();

	private boolean isPrintNewInbound;

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
		
		isPrintNewInbound 		= (boolean) param.get("isPrintNewInbound");

		isRouteNew 				= (boolean) param.get("isNew");
		
		initPrintSchedule(isPrintOutbound, isPrintInbound, isPrintRoute);

	}

	private void initPrintSchedule(boolean isPrintOutbound, boolean isPrintInbound, boolean isPrintRoute) throws Exception
	{
//		if(isPrintInbound) ScheduleManager.getInstance().addBulid(new InboundScheduleJoint());
		
		if(isPrintInbound) ScheduleManager.getInstance().addBulid(SchedulePrintFactory.createSchedulePrint("Inbound", param));
		
		if(isPrintOutbound) ScheduleManager.getInstance().addBulid(SchedulePrintFactory.createSchedulePrint("Outbound", param));
		
		if(isPrintRoute) ScheduleManager.getInstance().addBulid(new RouteSchedulePrint(SchedulePrintFactory.createSchedulePrint("Route", param)));

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
