package com.ksg.workbench.schedule.comp;

import com.ksg.service.ScheduleSubService;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.workbench.common.comp.KSGView;

/**

  * @FileName : PnSchedule.java

  * @Project : KSG2

  * @Date : 2022. 5. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
@SuppressWarnings("serial")
public abstract class PnSchedule extends KSGView{
	
	
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

	protected ScheduleSubService scheduleService;
	
	public PnSchedule()
	{
		super();
		scheduleService = new ScheduleServiceImpl();
	}
	
	

}
