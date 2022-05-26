package com.ksg.schedule.execute.formater;

import java.util.ArrayList;
import java.util.HashMap;

public abstract  class JointFormatter {
	
	String nodeName;
	
	ArrayList<HashMap<String, Object>> scheduleList;
	
	public void setNodeName(String nodeName)
	{
		this.nodeName=nodeName;
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
