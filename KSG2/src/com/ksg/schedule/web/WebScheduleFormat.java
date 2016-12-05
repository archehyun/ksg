package com.ksg.schedule.web;

import org.apache.log4j.Logger;

public abstract class WebScheduleFormat implements IFWebScheduleFormat{
	
	protected DefaultWebSchedule webSchedule;
	
	protected Logger 			logger = Logger.getLogger(getClass());	
	
	protected String fileName="";
	
	public String getFileName() {
		return fileName;
	}

	protected String errorFileName="";
	
	public String getErrorFileName() {
		// TODO Auto-generated method stub
		return fileName;
	}
}
