package com.ksg.workbench.schedule.comp;

import com.dtp.api.control.AbstractController;
import com.dtp.api.service.impl.CodeServiceImpl;
import com.ksg.common.model.CommandMap;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.workbench.common.comp.KSGView;
import com.ksg.workbench.common.comp.View;

/**

  * @FileName : PnSchedule.java

  * @Project : KSG2

  * @Date : 2022. 5. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
@SuppressWarnings("serial")
public abstract class PnSchedule extends KSGView implements View{
	
	
	private CommandMap model;
	
	protected String input_date="";
	
	protected String gubun;
	
	private AbstractController controller;
	
	public void setGubun(String gubun)
	{
		this.gubun = gubun;
	}
	
	public void setInput_date(String input_date) {
		this.input_date = input_date;
	}
	
	protected CodeServiceImpl codeService;

	protected ScheduleSubService scheduleService;
	
	public PnSchedule()
	{
		super();
		scheduleService = new ScheduleServiceImpl();
	}
	
	@Override
	public void setModel(CommandMap model) {
		this. model = model;

	}
	public CommandMap getModel() {

		return model;
	}

	public void callApi(String serviceId, CommandMap param)
	{
		if(this.controller!=null)
			this.controller.call(serviceId, param, this);
	}
	
	public void callApi(String serviceId)
	{
		if(this.controller!=null)
			this.controller.call(serviceId, new CommandMap(),this);
	}
	
	public void updateView() {};
	
	public void setController(AbstractController constroller)
	{
		this.controller =constroller;
	}
	
	

}
