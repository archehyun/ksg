package com.ksg.common.dao;

import com.ksg.adv.service.ADVService;
import com.ksg.adv.service.ADVServiceImpl;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.schedule.ScheduleService;
import com.ksg.schedule.ScheduleServiceImpl;
import com.ksg.shippertable.service.TableService;
import com.ksg.shippertable.service.TableServiceImpl;

public class DAOManager {
	private static DAOManager manager;
	private TableService tableservice;
	private BaseService baseservice;
	private ADVService advservice;
	private ScheduleService scheduleService;
	public static DAOManager getInstance()
	{
		if(manager==null)
			manager= new DAOManager();
		return manager;
	}
	public TableService createTableService()
	{
		if(tableservice ==null)
			tableservice = new TableServiceImpl();
		return tableservice;
	}
	public BaseService createBaseService()
	{
		if(baseservice ==null)
			baseservice = new BaseServiceImpl();
		return baseservice;
	}
	public ADVService createADVService()
	{
		if(advservice ==null)
			advservice = new ADVServiceImpl();
		return advservice;
	}
	public ScheduleService createScheduleService() 
	{
		if(scheduleService==null)
			scheduleService = new ScheduleServiceImpl();
		return scheduleService;
	}

}
