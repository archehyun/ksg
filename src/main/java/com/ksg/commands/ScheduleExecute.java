package com.ksg.commands;

public interface ScheduleExecute extends LongTask{
	public int execute() throws Exception;
	public final int SUCCESS=1;
	public final int FAILURE=2;

}
