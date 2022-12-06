package com.ksg.schedule.execute.formater;

import java.util.ArrayList;
import java.util.HashMap;

import com.ksg.common.model.CommandMap;

public abstract  class JointFormatter {
	
	String nodeName;
	
	ArrayList<HashMap<String, Object>> scheduleList;
	
	CommandMap param;
	
	
	public void setNodeName(String nodeName)
	{
		this.nodeName=nodeName;
	}
	
	public void setParam(CommandMap param)
	{
		this.param = param;
	}
	
	public HashMap<String, Object> getParam()
	{
		return param;
	}
	public String getNodeName()
	{
		return nodeName;
	}
	public  void setSchedule(ArrayList<HashMap<String, Object>> scheduleList)
	{
		this. scheduleList = scheduleList;
	}
	
	public abstract String getFormattedString();

}
