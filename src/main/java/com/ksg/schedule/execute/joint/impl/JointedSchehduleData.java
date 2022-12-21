package com.ksg.schedule.execute.joint.impl;

import java.util.ArrayList;
import java.util.List;

import com.ksg.domain.ScheduleData;

public class JointedSchehduleData {
	List<ScheduleData> list;
	String vesselName;
	private String toDate;
	public String getToDate() {
		return toDate;
	}
	private String fromDate;
	public String getFromDate() {
		return fromDate;
	}
	private String company;
	private String vesselType;

	public String getVesselType() {
		return vesselType;
	}
	public JointedSchehduleData()
	{
		list = new ArrayList<ScheduleData>();
	}
	public void addScheduleData(ScheduleData data)
	{
		list.add(data);
		vesselName = data.getVessel();
		toDate = data.getDateT();
		fromDate = data.getDateT();
		company = data.getCompany_abbr();
		vesselType = data.getVessel_type();
	}

	public String fromPort()
	{
		return "";
	}
	public String toPort()
	{
		return "";
	}
	public String toDate()
	{
		return toDate;
	}

	public String fromDate()
	{
		return fromDate;
	}
	public String vesselName()
	{
		return vesselName;		
	}
	public int size()
	{
		return list.size();
	}
	public String company() {
		return company;
	}
	public String vesselType() {
		return vesselType;
	}
	public List<ScheduleData> getList() {
		return list;
	}
}
