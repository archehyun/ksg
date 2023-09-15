package com.ksg.commands;

public interface LongTask {
	

	public int getLengthOfTask();
	public int getCurrent();
	public void stop();
	public boolean isDone();
	public String getMessage();

}
