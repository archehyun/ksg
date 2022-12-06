package com.ksg.schedule.execute.formater;

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
	public String getFormattedString() {
		
		return String.format("%s   %s   (%s)   %s", fromDate,vessel, company, toDate);
	}

}
