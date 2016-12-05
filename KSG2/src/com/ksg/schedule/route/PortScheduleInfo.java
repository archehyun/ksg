package com.ksg.schedule.route;

import java.text.ParseException;

import com.ksg.view.util.KSGDateUtil;

public class PortScheduleInfo implements Comparable<Object>
{	

	public Object getPrintDate() {
		return printDate;
	}
	public void setPrintDate(Object printDate) {
		this.printDate = printDate;
	}

	private String date;
	private String port;
	private Object printDate;
	private String company;

	public PortScheduleInfo(String port, String date, String company)
	{
		this.port =port;
		this.date = date;
		this.company=company;
	}

	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public PortScheduleInfo(String port, String date) {
		this.port = port;
		this.date = date;
	}
	public int compareTo(Object o) {

		try {
			PortScheduleInfo d =(PortScheduleInfo) o;
			return KSGDateUtil.daysDiff(KSGDateUtil.toDate4(d.getDate()), KSGDateUtil.toDate4(this.getDate()));

		} catch (ParseException e) {
			return -1;
		}
	}
	public String getDate() {
		return date;
	}
	public String getPort() {
		return port;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String toString()
	{		
		return this.getPort()+" "+KSGDateUtil.getRouteDate(this.getDate());
	}


}
