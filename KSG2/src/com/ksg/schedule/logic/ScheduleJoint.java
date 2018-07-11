package com.ksg.schedule.logic;

import java.io.IOException;

import com.ksg.commands.LongTask;


public interface ScheduleJoint extends LongTask{
	public int execute() throws IOException;
	public final int SUCCESS=1;
	public final int FAILURE=2;
}

