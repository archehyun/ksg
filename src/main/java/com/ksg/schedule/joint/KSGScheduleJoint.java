package com.ksg.schedule.joint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ksg.schedule.service.ScheduleServiceLogic;

/**

  * @FileName : KSGScheduleJoint.java

  * @Date : 2021. 5. 18. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� :
  * �����輱 �߻� Ŭ����

  */
public abstract class KSGScheduleJoint {
	
	protected Logger 			logger = Logger.getLogger(getClass());
	
	protected ScheduleServiceLogic scheduleService;
	
	protected HashMap<String, Object> scheduleList;
	
	public KSGScheduleJoint() {
		
		scheduleService = new ScheduleServiceLogic();
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
