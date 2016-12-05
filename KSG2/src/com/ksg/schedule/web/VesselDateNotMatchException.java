package com.ksg.schedule.web;

public class VesselDateNotMatchException extends Exception{
	private String date;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public VesselDateNotMatchException(String message, String dateF) {
		super(message);
		this.setDate(dateF);
	}

}
