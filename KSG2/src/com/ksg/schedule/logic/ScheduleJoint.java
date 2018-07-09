package com.ksg.schedule.logic;

import com.ksg.commands.LongTask;


public interface ScheduleJoint extends LongTask{
	public int execute();
	public final int SUCCESS=1;
	public final int FAILURE=2;
}

