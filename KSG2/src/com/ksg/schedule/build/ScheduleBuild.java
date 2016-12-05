package com.ksg.schedule.build;

import com.ksg.commands.LongTask;


public interface ScheduleBuild extends LongTask{
	public int execute();
	public final int SUCCESS=1;
	public final int FAILURE=2;
}

