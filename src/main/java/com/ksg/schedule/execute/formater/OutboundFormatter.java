package com.ksg.schedule.execute.formater;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;

public class OutboundFormatter extends JointFormatter{

	private String fromDate;
	private String vessel;
	private String company;
	private String toDate;
	
	public void setParam(String fromDate, String vessel, String comapny, String toDate)	
	{
		this.fromDate 	= fromDate;
		this.vessel 	= vessel;
		this.company 	= comapny;
		this.toDate 	= toDate;
	}
	
	@Override
	public void setParam(CommandMap param)
	{
		super.setParam(param);
		
		this.fromDate = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(String.valueOf(param.get("dateF")));
		this.vessel = String.valueOf(param.get("vessel"));
		this.company = String.valueOf(param.get("company_abbr"));
		this.toDate = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(String.valueOf(param.get("dateT")));
	}

	@Override
	public String getFormattedString() {
		
		return String.format("%-8s%-15s(%s)   %s", fromDate,vessel, company, toDate);
	}

}
