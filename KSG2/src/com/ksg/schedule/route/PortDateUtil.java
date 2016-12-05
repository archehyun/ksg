package com.ksg.schedule.route;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PortDateUtil {
	
	private static SimpleDateFormat inputDateType_yyyy_MM_dd = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat outputDateType = new SimpleDateFormat("M/d");
	
	public static Date parse(String date) throws ParseException
	{
		return inputDateType_yyyy_MM_dd.parse(date);
	}
	public static String toPrintDate(String date) throws ParseException
	{
		return outputDateType.format(PortDateUtil.parse(date));
	}
}
