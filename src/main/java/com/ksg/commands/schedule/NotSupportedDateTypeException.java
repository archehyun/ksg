package com.ksg.commands.schedule;

import java.text.ParseException;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class NotSupportedDateTypeException extends ParseException
{
	public String date;
	public NotSupportedDateTypeException(String s, int errorOffset) {
		super(s, errorOffset);
		date = s;
	}
	
	

	

}
