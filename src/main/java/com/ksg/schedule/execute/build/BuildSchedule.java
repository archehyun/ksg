package com.ksg.schedule.execute.build;

import java.util.HashMap;

import com.ksg.schedule.execute.Executeable;
import com.ksg.service.ADVService;
import com.ksg.service.ScheduleService;
import com.ksg.service.ShipperTableService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.ShipperTableServiceImpl;

public abstract class BuildSchedule implements Executeable{
	
	private HashMap<String, String> portMap;
	
	protected ScheduleService 	scheduleService;
	
	protected ADVService advService;
	
	protected ShipperTableService shipperTableService;
	
	public BuildSchedule()
	{
		scheduleService 	= new ScheduleServiceImpl();
		shipperTableService = new ShipperTableServiceImpl();
		advService 			= new ADVServiceImpl();
	}
	
	

}
