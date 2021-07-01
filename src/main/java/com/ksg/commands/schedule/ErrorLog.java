package com.ksg.commands.schedule;

public class ErrorLog {
	public static final String DATE_ERROR_F = "날짜오류F ";
	public final static String DATE_ERROR_F_NULL = "날짜오류F Null";
	private String currentTime;
	private String type;
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	String id;
	String date;
	public void setTableID(String tableId) {
		this.id = tableId;

	}

	public void setDate(String date) {
		this.date = date;
	}

	public String toString()
	{
		return this.getCurrentTime()+"-["+type+":"+id+","+date+"]";
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
}
