package com.ksg.schedule.logic.joint.route;

import com.dtp.api.schedule.comparator.IFComparator;
import com.ksg.common.util.KSGDateUtil;

public class PortAndDay implements IFComparator{
	
	private String port;
	private String date;
	private String vessel;
	public PortAndDay(String port, String date)
	{
		this.port = port;
		this.date = date;
	}
	public String toString()
	{
		return  port+" "+KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(date);
	}
	public String getDate()
	{
		return date;
	}
	public String getVessel()
	{
		return vessel;
	}

}
