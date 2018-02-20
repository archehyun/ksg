package com.ksg.view.util;

public class DateFormattException extends Exception{
	
	String inputDate;
	public DateFormattException(String date) {
		super(date);
		this.inputDate = date;
	}

}
