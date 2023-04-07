package com.ksg.schedule.logic.joint.route;

import java.sql.SQLException;

import com.ksg.schedule.logic.print.DefaultSchedulePrint;
import com.ksg.schedule.logic.print.route.RouteAbstractSchedulePrint;

public class RouteSchedulePrint extends DefaultSchedulePrint{
	
	RouteAbstractSchedulePrint joint;
	
	public RouteSchedulePrint(RouteAbstractSchedulePrint joint) throws SQLException
	{
		super();
		this.joint = joint;
	}

	@Override
	public int execute() throws Exception {
		// TODO Auto-generated method stub
		return joint.execute();
	}

	@Override
	public void initTag() {
		joint.initTag();
	}
	
	@Override
	public int getLengthOfTask() {
		return joint.getLengthOfTask();
	}
	@Override
	public int getCurrent() {
		return joint.getCurrent();
	}
	@Override
	public boolean isDone() {
		return joint.isDone();
	}
	
	@Override
	public void setDone(boolean done) {
		joint.setDone(done);
	}
	@Override
	public void stop() {

	}

	@Override
	public String getMessage() {
		return joint.getMessage();
	}
	
	
	

}
