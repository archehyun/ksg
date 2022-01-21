package com.ksg.schedule.joint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.service.ScheduleService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.ScheduleServiceLogic;

/**

  * @FileName : KSGScheduleJoint.java

  * @Date : 2021. 5. 18. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :
  * 공동배선 추상 클래스

  */
public abstract class KSGScheduleJoint {
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	protected ScheduleService scheduleService;
	
	protected HashMap<String, Object> scheduleList;
	
	public KSGScheduleJoint() {
		
		scheduleService = new ScheduleServiceImpl();
	}
	
	public abstract int execute() throws Exception;
	
	public abstract void init();
	
	public final int SUCCESS=1;
	public final int FAILURE=2;
	
	
	private SimpleDateFormat inputDateFormat 	= new SimpleDateFormat("yyyy/MM/dd");

	private SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/d");
	
	public String convertDateFormat(String dateType)
	{
		try {
			return outputDateFormat.format(inputDateFormat.parse(dateType));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return "error";
		}
	}

}
