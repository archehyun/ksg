package com.dtp.api.schedule.joint.print;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.dtp.api.schedule.comparator.DateComparator;
import com.dtp.api.schedule.comparator.IFComparator;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;

public abstract class ScheduleGroup implements Comparable<ScheduleGroup>, IFComparator{
	
	protected ScheduleGroup parent;
	
	protected DateComparator dateComparator 			= new DateComparator(new SimpleDateFormat("yyyy/MM/dd"));
	
	protected List<ScheduleData> scheduleList;
	
	protected String dateF, dateT;
	
	protected Vessel vessel;
	
	public ScheduleGroup() {}
	
	public ScheduleGroup(Vessel vessel, List<ScheduleData> scheduleList)
	{
		this.vessel = vessel;
		
		this.scheduleList = scheduleList;
	}
	public ScheduleGroup(List<ScheduleData> scheduleList)
	{
		this.scheduleList = scheduleList;
	}
	
	public Vessel getVessel()
	{
		return vessel;
	}
	public String getDateF()
	{
		return dateF;
	}
	public String getDateT()
	{
		return dateT;
	}

	public List<ScheduleData> getScheduleList() {
		return this.scheduleList;
	}
	
	public String getVesselName() {
		return null;
	}

	public String getForrmatedDate(String date) {
		try {
			return KSGDateUtil.dateFormat5.format(KSGDateUtil.inputDateFormat.parse(String.valueOf(date)));
		} catch (ParseException e) {
			return date;
		}
	}

	public ScheduleGroup getParent() {
		return parent;
	};
}
