package com.ksg.workbench.schedule.comp;

import java.awt.event.ActionListener;

import com.ksg.service.ScheduleService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;

public abstract class PnSchedule extends KSGPanel implements ActionListener{
	
	
	protected String input_date="";
	
	protected String gubun;
	
	public void setGubun(String gubun)
	{
		this.gubun = gubun;
	}
	
	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}

	protected ScheduleService scheduleService;
	
	public PnSchedule()
	{
		super();
		scheduleService = new ScheduleServiceImpl();
	}

}
