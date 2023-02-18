package com.ksg.commands.schedule.task;

import com.ksg.commands.LongTask;
import com.ksg.commands.schedule.route.RouteTaskNewVessel;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.joint.InboundScheduleJoint;
import com.ksg.schedule.logic.joint.OutboundScheduleJointV2;
import com.ksg.schedule.logic.joint.RouteAbstractScheduleJoint;
import com.ksg.schedule.logic.joint.RouteScheduleJoint;
import com.ksg.schedule.logic.joint.RouteScheduleJointV4;
import com.ksg.schedule.logic.joint.route.RouteSchedulePrint;

public class SortAllTask implements LongTask {

	int orderBy;

	boolean isRouteNew;

	boolean isPrintNewRoute;
	
	private ShippersTable op;
	
	private String date_isusse;

	public SortAllTask(ShippersTable op,int orderBy,boolean isNew,boolean isPrintInbound, boolean isPrintOutbound, boolean isPrintRoute) throws Exception {
		this.op = op;
		
		this.orderBy = orderBy;
		
		this.isPrintNewRoute = false;
		
		initPrintSchedule(isPrintOutbound, isPrintInbound, isPrintRoute);		

	}
	public SortAllTask(CommandMap param) throws Exception
	{	
		date_isusse = (String) param.get("date_isusse");
		
		boolean isPrintInbound = (boolean) param.get("isPrintInbound");

		boolean isPrintOutbound = (boolean) param.get("isPrintOutbound");

		boolean isPrintRoute = (boolean) param.get("isPrintRoute");

		orderBy = (int) param.get("orderBy");

		isPrintNewRoute = (boolean) param.get("isPrintNewRoute");

		isRouteNew = (boolean) param.get("isNew");
		
		initPrintSchedule(isPrintOutbound, isPrintInbound, isPrintRoute);

	}

	private void initPrintSchedule(boolean isPrintOutbound, boolean isPrintInbound, boolean isPrintRoute) throws Exception
	{
		if(isPrintInbound) ScheduleManager.getInstance().addBulid(new InboundScheduleJoint());
		
		if(isPrintOutbound) ScheduleManager.getInstance().addBulid(new OutboundScheduleJointV2());
		
		if(isPrintRoute)
		{
			if(isRouteNew)
			{
				new RouteTaskNewVessel(op,orderBy).start();
			}
			else
			{
				RouteAbstractScheduleJoint joint = isPrintNewRoute?new RouteScheduleJointV4(date_isusse, orderBy):new RouteScheduleJoint(date_isusse, orderBy);

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
