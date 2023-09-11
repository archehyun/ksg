package com.dtp.api.schedule.joint.print.route;

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
	public String getPort()
	{
		return port;
	}
	public String toString()
	{
		return  port+" "+KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(date);
	}
	public String getDateF()
	{
		return date;
	}
	public String getVesselName()
	{
		return vessel;
	}

}
