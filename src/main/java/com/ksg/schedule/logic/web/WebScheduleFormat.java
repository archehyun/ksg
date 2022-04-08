package com.ksg.schedule.logic.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class WebScheduleFormat implements IFWebScheduleFormat{
	
	protected DefaultWebSchedule webSchedule;
	
	protected Logger logger = LogManager.getLogger(this.getClass());	
	
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
