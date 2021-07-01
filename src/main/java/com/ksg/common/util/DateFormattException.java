package com.ksg.common.util;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class DateFormattException extends Exception{
	
	String inputDate;
	public DateFormattException(String date) {
		super(date);
		this.inputDate = date;
	}

}
