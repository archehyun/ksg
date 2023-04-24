package com.ksg.schedule.logic.joint.route;

import java.io.IOException;
import java.sql.SQLException;

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;

public class RouteSchedulePrint extends AbstractSchedulePrint{
	
	AbstractSchedulePrint joint;
	
	public RouteSchedulePrint(AbstractSchedulePrint joint) throws SQLException
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
	public void init() throws Exception {
		joint.init();
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

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
