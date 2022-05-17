package com.ksg.workbench.schedule.comp;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import com.ksg.service.ScheduleService;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;

public abstract class PnSchedule extends KSGPanel implements ActionListener, ComponentListener{
	
	
	protected String input_date="";
	
	protected String gubun;
	
	public void setGubun(String gubun)
	{
		this.gubun = gubun;
	}
	
	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}
	
	protected CodeServiceImpl codeService;

	protected ScheduleService scheduleService;
	
	public PnSchedule()
	{
		super();
		scheduleService = new ScheduleServiceImpl();
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


}
