package com.ksg.view.ui;

public class ErrorLogManager {
	
	private static ErrorLogManager instance;
	
	private String logger;
	
	public String getLogger() {
		return logger;
	}
	private ErrorLogManager()
	{
		
	}

	public void setLogger(String logger) {
		this.logger = logger;
	}

	public static ErrorLogManager getInstance()
	{
		
		if(instance==null)
		{
			instance = new ErrorLogManager();
		}
		return instance;
	}

}
