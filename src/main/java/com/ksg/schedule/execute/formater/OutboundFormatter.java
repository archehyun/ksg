package com.ksg.schedule.execute.formater;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;

public class OutboundFormatter extends JointFormatter{

	private String fromDate;
	private String vessel;
	private String vessel_type;
	private String company;
	private String agent;
	private String toDate;
	private Object formatedVesselType;
	
	public void setParam(String fromDate, String vessel, String vessel_type, String comapny, String toDate)	
	{
		this.fromDate 	= fromDate;
		this.vessel 	= vessel;
		this.vessel_type = vessel_type;
		this.company 	= comapny;
		this.toDate 	= toDate;
	}
	
	@Override
	public void setParam(CommandMap param)
	{
		super.setParam(param);
		
		this.fromDate = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(String.valueOf(param.get("dateF")));
		this.vessel = String.valueOf(param.get("vessel"));
		this.vessel_type = String.valueOf(param.get("vessel_type"));
		
		this.formatedVesselType =  (vessel_type.equals("")||vessel_type.equals(" "))?"   ":String.format("   [%s]   ", vessel_type);   
		
		this.company = String.valueOf(param.get("company_abbr"));
		this.agent = String.valueOf(param.get("agent"));
		this.toDate = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(String.valueOf(param.get("dateT")));
	}

	@Override
	public String getFormattedString() {
		
		return String.format("%-8s%-15s%s(%s)   %s", fromDate,vessel,formatedVesselType, company.equals(agent)?company:company+"/"+agent, toDate);
	}

}
