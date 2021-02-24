package com.ksg.schedule.logic.web;


public class WebScheduleManager {
	
	private static WebScheduleManager instance;
	private WebScheduleManager() {
		
	}
	public static WebScheduleManager getInstance()
	{
		if(instance== null)
			instance = new WebScheduleManager();
		return instance;
		
	}


}
