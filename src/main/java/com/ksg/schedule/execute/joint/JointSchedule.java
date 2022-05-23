package com.ksg.schedule.execute.joint;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.schedule.execute.Executeable;
import com.ksg.service.ScheduleServiceV2;
import com.ksg.service.impl.ScheduleServiceImpl;

public abstract class JointSchedule implements Executeable{
	
	SimpleDateFormat inputDateFormat 	= KSGDateUtil.createInputDateFormat();

	SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();
	
	protected ScheduleServiceV2 	scheduleService;
	protected String date;
	public JointSchedule(String date)
	{
		scheduleService 	= new ScheduleServiceImpl();
		this.date = date;
//		shipperTableService = new ShipperTableServiceImpl();
//		advService 			= new ADVServiceImpl();
	}
	
	public String getFormatedDate(String date)
	{
		try {
			return outputDateFormat.format(inputDateFormat.parse(date));
		} catch (ParseException e) {
			
			return "error";
		}
	}

}
