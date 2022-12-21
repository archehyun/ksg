/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.schedule.logic.web.VesselDateNotMatchException;

/**날짜 형식 관리
 * 날짜 비교
 * @설명 날짜 비교 유틸
 * @author archehyun
 *
 */
public class KSGDateUtil {


	private static String datePattern = "(\\d{1,2})/(\\d{1,2})";
	private static String datePattern1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
	private static String datePattern1_1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})\\w+";
	private static String datePattern1_2 = "(\\d{1,2}).(\\d{1,2})/(\\d{1,2}).(\\d{1,2})";
	private static String datePattern2 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
	private static String datePattern2_1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})\\w+";

	public final static long SECOND_MILLIS = 1000;
	public final static long MINUTE_MILLIS = SECOND_MILLIS*60;
	public final static long HOUR_MILLIS = MINUTE_MILLIS*60;
	public final static long DAY_MILLIS = HOUR_MILLIS*24;
	public final static long YEAR_MILLIS = DAY_MILLIS*365;
	public static DateFormat outputDateFormat = new SimpleDateFormat("M/d");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat dashdateFormat = new SimpleDateFormat("yyyy.M.d");
	private static SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyyMMdd");

	public final static int TIME_TYPE_1=1;
	public final static int TIME_TYPE_2=2;
	public final static int TIME_TYPE_3=3;
	public final static int TIME_TYPE_4=4;
	public final static int TIME_TYPE_ERROR=0;

	public final static int FORWARD =0;
	public final static int BACK =1;


	private static String pattenList[][]=
		{{"\\d{4}/\\d{1,2}/\\d{1,2}","yyyy/MM/dd"},
				{"\\d{4}.\\d{1,2}.\\d{1,2}","yyyy.M.d"},
				{"\\d{1,2}/\\d{1,2}","M/d"},
				{"\\d{4}-\\d{1,2}-\\d{1,2}","yyyy-MM-dd"}};
	private static String strYear;

	public static final int TYPE=0;

	public static String format(Date date)
	{
		return dateFormat.format(date);
	}
	public static String format2(Date date)
	{
		return outputDateFormat.format(date);
	}
	public static String format4(Date date)
	{
		return inputDateFormat.format(date);
	}
	public static String format5(Date date)
	{
		return dateFormat5.format(date);
	}
	public static String dashformat(Date date)
	{
		return dashdateFormat.format(date);
	}
	public static Date nextMonday(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		switch (c.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			c.add(Calendar.DAY_OF_MONTH, 1);
			break;
		case Calendar.MONDAY:
			c.add(Calendar.DAY_OF_MONTH, 7);
			break;
		case Calendar.TUESDAY:
			c.add(Calendar.DAY_OF_MONTH, 6);
			break;
		case Calendar.WEDNESDAY:
			c.add(Calendar.DAY_OF_MONTH, 5);
			break;
		case Calendar.THURSDAY:
			c.add(Calendar.DAY_OF_MONTH, 4);
			break;
		case Calendar.FRIDAY:
			c.add(Calendar.DAY_OF_MONTH, 3);
			break;
		case Calendar.SATURDAY:
			c.add(Calendar.DAY_OF_MONTH, 2);
			break;
		}
		return c.getTime();
	}

	/**
	 * @param value
	 * @param type
	 * @return
	 * @throws ParseException
	 */
	public static java.sql.Date toDate( Object value ,int type) throws ParseException
	{
		if( value == null ) return null;        
		if( value instanceof java.sql.Date ) return (java.sql.Date)value;
		if( value instanceof String )
		{
			if( "".equals( (String)value ) ) return null;

			return new java.sql.Date( outputDateFormat.parse( (String)value ).getTime() );
		}

		return new java.sql.Date( outputDateFormat.parse( value.toString() ).getTime() );
	}

	/**
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static java.sql.Date toDate( Object value ) throws ParseException
	{
		if( value == null ) return null;        
		if( value instanceof java.sql.Date ) return (java.sql.Date)value;
		if( value instanceof String )
		{
			if( "".equals( (String)value ) ) return null;
			return new java.sql.Date( outputDateFormat.parse( (String)value ).getTime() );
		}

		return new java.sql.Date( outputDateFormat.parse( value.toString() ).getTime() );
	}


	public static int daysDiffABS( Date earlierDate, Date laterDate )
	{
		return Math.abs(daysDiff(earlierDate, laterDate));
	}
	public static int daysDiff( Date earlierDate, Date laterDate )
	{
		if( earlierDate == null || laterDate == null ) return 0;


		return (int)((laterDate.getTime()/DAY_MILLIS) - (earlierDate.getTime()/DAY_MILLIS));
	}

	//"yyyy-MM-dd"
	public static java.sql.Date toDate2( Object value ) throws ParseException
	{
		if( value == null ) return null;        
		if( value instanceof java.sql.Date ) return (java.sql.Date)value;
		if( value instanceof String )
		{
			if( "".equals( (String)value ) ) return null;
			return new java.sql.Date( dateFormat.parse( (String)value ).getTime() );
		}

		return new java.sql.Date( dateFormat.parse( value.toString() ).getTime() );
	}



	/**
	 * @param value
	 * @return
	 * @throws ParseException
	 * @throws NoSuchElementException
	 */
	public static java.sql.Date toDate3( Object value ) throws  DateFormattException
	{

		try
		{
			if(!KSGDateUtil.isDashFomatt((String) value)) throw  new DateFormattException((String) value);
			if( value == null ) return null;        
			if( value instanceof java.sql.Date ) return (java.sql.Date)value;
			if( value instanceof String )
			{
				if( "".equals( (String)value ) ) return null;

				StringTokenizer st = new StringTokenizer(value.toString(),".");
				String year=st.nextToken();
				String month = st.nextToken();
				String day = st.nextToken();
				int monthc = Integer.parseInt(month);
				int dayc = Integer.parseInt(day);

				return new java.sql.Date( dateFormat.parse( year+"-"+(monthc>9?monthc:"0"+monthc)+"-"+(dayc>9?dayc:"0"+dayc) ).getTime() );
			}

			return new java.sql.Date( dateFormat.parse( value.toString() ).getTime() );	
		}
		catch(Exception e)
		{
			throw new DateFormattException((String) value);
		}

	}

	//"yyyy/MM/dd"
	public static java.sql.Date toDate4( Object value ) throws ParseException
	{
		if( value == null ) return null;        
		if( value instanceof java.sql.Date ) return (java.sql.Date)value;
		if( value instanceof String )
		{

			try
			{
				return new java.sql.Date( inputDateFormat.parse( (String)value ).getTime() );
			}catch(ParseException ee)
			{
				return new java.sql.Date( outputDateFormat.parse( (String)value ).getTime() );
			}
		}

		return new java.sql.Date( outputDateFormat.parse( value.toString() ).getTime() );
	}
	public static java.sql.Date toDate5( Object value ) throws ParseException
	{
		if( value == null ) return null;        
		if( value instanceof java.sql.Date ) return (java.sql.Date)value;
		if( value instanceof String )
		{

			try
			{
				return new java.sql.Date( dateFormat5.parse( (String)value ).getTime() );
			}catch(ParseException ee)
			{
				return new java.sql.Date( outputDateFormat.parse( (String)value ).getTime() );
			}
		}

		return new java.sql.Date( outputDateFormat.parse( value.toString() ).getTime() );
	}
	/**
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static java.sql.Date toDateBySearch( String value ) throws ParseException
	{

		if( value == null ) return null; 


		int TYPE=KSGDateUtil.searchMatchType(value);
		SimpleDateFormat dashdateFormat;

		if(TYPE!=TIME_TYPE_ERROR)
		{

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

			strYear = sdf.format(new Date());
			dashdateFormat = new SimpleDateFormat(pattenList[TYPE-1][1]);

			dashdateFormat.getCalendar().set(SimpleDateFormat.YEAR_FIELD, Integer.valueOf(strYear));


			if( TYPE==TIME_TYPE_3)
			{
				try{
					Calendar calendar =Calendar.getInstance();
					int year =Integer.parseInt(strYear);
					calendar.set(Calendar.YEAR,year );

					SimpleDateFormat ormat = new SimpleDateFormat("MM");
					String month = ormat.format(dashdateFormat.parse( value).getTime());
					ormat.applyPattern("dd");
					String day = ormat.format(dashdateFormat.parse( value).getTime());


					return  new java.sql.Date(dateFormat.parse(strYear+"-"+month+"-"+day).getTime() );
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}else if( TYPE==TIME_TYPE_1)
			{


			}

			return  new java.sql.Date(dashdateFormat.parse( value).getTime() );	

		}else
		{	
			throw new  ParseException(value, 0);
		}	
	}



	/**
	 * @param value
	 * @return
	 * @throws ParseException
	 */
	public static java.sql.Date toDateBySearch2( String value ) throws ParseException
	{

		if( value == null ) return null; 


		int TYPE=KSGDateUtil.searchMatchType(value);
		SimpleDateFormat dashdateFormat;

		if(TYPE!=TIME_TYPE_ERROR)
		{

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

			strYear = sdf.format(new Date());
			dashdateFormat = new SimpleDateFormat(pattenList[TYPE-1][1]);
			dashdateFormat.getCalendar().set(SimpleDateFormat.YEAR_FIELD, Integer.valueOf(strYear));

			if( TYPE==TIME_TYPE_1)
			{
				try{
					Calendar calendar =Calendar.getInstance();
					int year =Integer.parseInt(strYear);
					calendar.set(Calendar.YEAR,year );

					java.sql.Date t = new java.sql.Date( dashdateFormat.parse( value).getTime() );
					SimpleDateFormat ormat = new SimpleDateFormat("M");

					String month = ormat.format(t);
					ormat.applyPattern("dd");
					String day = ormat.format(t);
					return  new java.sql.Date(outputDateFormat.parse(month+"/"+day).getTime() );
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else if( TYPE==TIME_TYPE_4)
			{
				try{
					Calendar calendar =Calendar.getInstance();
					int year =Integer.parseInt(strYear);
					calendar.set(Calendar.YEAR,year );

					java.sql.Date t = new java.sql.Date( dashdateFormat.parse( value).getTime() );
					SimpleDateFormat ormat = new SimpleDateFormat("M");

					String month = ormat.format(t);
					ormat.applyPattern("dd");
					String day = ormat.format(t);
					return  new java.sql.Date(outputDateFormat.parse(month+"/"+day).getTime() );
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			return  new java.sql.Date(dashdateFormat.parse( value).getTime() );

		}else
		{	
			throw new  ParseException(value, 0);
		}	
	}


	public static String getDate()
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime( new Date() );

		int year = cal.get( Calendar.YEAR );
		int month = cal.get( Calendar.MONTH ) + 1;
		int day = cal.get( Calendar.DAY_OF_MONTH );

		return "" + year
				+ "-" + month
				+ "-" + day;
	}
	public static int searchMatchType(String date)
	{

		for(int i=0;i<pattenList.length;i++)
		{
			if(date.matches(pattenList[i][0]))
				return i+1;
		}
		return TIME_TYPE_ERROR;
	}
	public static boolean isDashFomatt(String date)
	{
		String datePattern = "\\d{4}.\\d{1,2}.\\d{1,2}";
		return date.matches(datePattern);
	}
	public static boolean isThreeDayUnder(String onedate, String twodate) {

		return isDayUnder(3, onedate, twodate);
	}
	public static int dayDiff(String earlierDate, String laterDate){
		try {
			return daysDiff(KSGDateUtil.toDate4(earlierDate), KSGDateUtil.toDate4(laterDate));
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"earlierDate:"+earlierDate+", laterDate:"+laterDate);
		}
	}
	public static boolean isDayUnder(int day,String onedate, String twodate) {

		try {
			return KSGDateUtil.daysDiffABS(KSGDateUtil.toDate4(onedate), KSGDateUtil.toDate4(twodate))<=day;
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"oneDate:"+onedate+", twoDate:"+twodate);
		}
	}
	public static boolean isSame(String onedate, String twodate) {

		try {
			return KSGDateUtil.daysDiffABS(KSGDateUtil.toDate4(onedate), KSGDateUtil.toDate4(twodate))==0;
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"oneDate:"+onedate+", twoDate:"+twodate);
		}
	}
	/**
	 * 
	 * 
	 * 날짜 비교후 큰 날짜 반환
	 * yyyy/mm/dd
	 * @param onedate
	 * @param twodate
	 * @return
	 */
	public static String biggerDate(String onedate, String twodate) {

		int oneYear = getYear(onedate);
		int twoYear = getYear(twodate);
		int oneTMonth =getMonth(onedate);
		int twoTMonth =getMonth(twodate);
		int oneTDay =getDay(onedate);
		int twoTDay =getDay(twodate);
		if(oneYear>twoYear)
		{
			return onedate;
		}
		else if(oneYear<twoYear)
		{
			return twodate;
		}
		else
		{
			if(oneTMonth>twoTMonth)
			{
				return onedate;
			}
			else if(oneTMonth==twoTMonth)
			{
				if(oneTDay>twoTDay)
				{
					return onedate;

				}else
				{
					return twodate;
				}
			}else
			{
				return twodate;
			}	
		}
	}
	private static int getYear(String dateF) {
		StringTokenizer st = new StringTokenizer(dateF,"/");
		return Integer.parseInt(st.nextToken());
	}
	private static int getDay(String dateF) {
		StringTokenizer st = new StringTokenizer(dateF,"/");
		st.nextToken();
		st.nextToken();
		return Integer.parseInt(st.nextToken());
	}
	private static int getMonth(String dateF) {
		StringTokenizer st = new StringTokenizer(dateF,"/");
		st.nextToken();
		return Integer.parseInt(st.nextToken());
	}
	public static String getDate(String date, int direction)
	{

		StringTokenizer st = new StringTokenizer(date,"-");

		if(st.countTokens()==2)
		{
			String dateFoward = st.nextToken();
			String dateBack = st.nextToken();
			String returnDate="";
			switch (direction) {
			case FORWARD:


				returnDate = dateFoward;
				break;

			case BACK:

				returnDate = dateBack;
				break;
			}

			return returnDate;

		}else 
		{
			return date;
		}
	}

	public static int selectYear(int currentMonth, int targetMonth,int currentYear )
	{
		int gapMonth = currentMonth -targetMonth;

		if(currentMonth<4)
		{
			if(Math.abs(gapMonth)>=9)
			{
				return --currentYear;
			}else
			{
				return currentYear;
			}

		}else if(currentMonth==4)
		{
			return currentYear;
		}
		else
		{
			if(gapMonth<=3)
			{
				return currentYear;
			}else
			{
				return ++currentYear;
			}
		}
	}

	public static String rowerDate(String onedate, String twodate) {
		int oneTMonth =getMonth(onedate);
		int twoTMonth =getMonth(twodate);
		int oneTDay =getDay(onedate);
		int twoTDay =getDay(twodate);
		if(oneTMonth>twoTMonth)
		{
			return twodate;
		}
		else if(oneTMonth==twoTMonth)
		{
			if(oneTDay>twoTDay)
			{
				return twodate;

			}else
			{
				return onedate;
			}
		}else
		{
			return onedate;
		}
	}
	public static String upperDate(String onedate, String twodate) {
		int oneTMonth =getMonth(onedate);
		int twoTMonth =getMonth(twodate);
		int oneTDay =getDay(onedate);
		int twoTDay =getDay(twodate);

		if(oneTMonth>twoTMonth)
		{
			return onedate;
		}
		else if(oneTMonth==twoTMonth)
		{
			if(oneTDay>twoTDay)
			{
				return onedate;

			}else
			{
				return twodate;
			}
		}else
		{
			return twodate;
		}
	}	
	public static String getRouteDate(String date)
	{
		try {
			return outputDateFormat.format(inputDateFormat.parse(date));
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"date:"+date);
		}
	}

	/**
	 * @see 뒷날짜 선택
	 * @param date
	 * @return
	 * @throws NotSupportedDateTypeException
	 */
	public static int[] getETD(String date)
			throws NotSupportedDateTypeException {
		int monthAndDay[] = new int[2];


		//"(\\d{1,2})/(\\d{1,2})";
		if(date.matches(datePattern))
		{
			Pattern patt = Pattern.compile(datePattern);
			Matcher matcher = patt.matcher(date);
			matcher.lookingAt();

			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
			/*monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));*/		


		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
		else if(date.matches(datePattern1))
		{
			Pattern patt = Pattern.compile(datePattern1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();


			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(3));
			monthAndDay[1]=Integer.valueOf(matcher.group(4));


		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})\\w+";
		else if(date.matches(datePattern1_1))
		{
			Pattern patt = Pattern.compile(datePattern1_1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(3));
			monthAndDay[1]=Integer.valueOf(matcher.group(4));
		}
		//"(\\d{1,2}).(\\d{1,2})/(\\d{1,2}).(\\d{1,2})";
		else if(date.matches(datePattern1_2))
		{
			Pattern patt = Pattern.compile(datePattern1_2);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();


			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(3));
			monthAndDay[1]=Integer.valueOf(matcher.group(4));
		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
		else if(date.matches(datePattern2))
		{
			Pattern patt = Pattern.compile(datePattern2);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(3));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(3));

		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})\\w+";
		else if(date.matches(datePattern2_1))
		{
			Pattern patt = Pattern.compile(datePattern2_1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(3));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(3));
		}else
		{
			throw new NotSupportedDateTypeException(date, 0);
		}
		return monthAndDay;
	}
	/**
	 * @see 앞날짜 선택
	 * @param date
	 * @return
	 * @throws NotSupportedDateTypeException
	 */
	public static int[] getETA(String date)
			throws NotSupportedDateTypeException {
		int monthAndDay[] = new int[2];


		//"(\\d{1,2})/(\\d{1,2})";
		if(date.matches(datePattern))
		{
			Pattern patt = Pattern.compile(datePattern);
			Matcher matcher = patt.matcher(date);
			matcher.lookingAt();

			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
			/*monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));*/		


		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
		else if(date.matches(datePattern1))
		{
			Pattern patt = Pattern.compile(datePattern1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();


			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));


		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})\\w+";
		else if(date.matches(datePattern1_1))
		{
			Pattern patt = Pattern.compile(datePattern1_1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
		}
		//"(\\d{1,2}).(\\d{1,2})/(\\d{1,2}).(\\d{1,2})";
		else if(date.matches(datePattern1_2))
		{
			Pattern patt = Pattern.compile(datePattern1_2);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();


			/*			monthF=Integer.valueOf(matcher.group(3));
			dayF=Integer.valueOf(matcher.group(4));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
		else if(date.matches(datePattern2))
		{
			Pattern patt = Pattern.compile(datePattern2);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(3));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));

		}
		//"(\\d{1,2})/(\\d{1,2})-(\\d{1,2})\\w+";
		else if(date.matches(datePattern2_1))
		{
			Pattern patt = Pattern.compile(datePattern2_1);
			Matcher matcher = patt.matcher(date);			
			matcher.lookingAt();
			/*			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(3));*/
			monthAndDay[0]=Integer.valueOf(matcher.group(1));
			monthAndDay[1]=Integer.valueOf(matcher.group(2));
		}else
		{
			throw new NotSupportedDateTypeException(date, 0);
		}
		return monthAndDay;
	}

	/**
	 * @param dateF
	 * @return
	 * @throws VesselDateNotMatchException 
	 */
	public static String[] getDates(String dateF, int currentMonth,String strYear) throws VesselDateNotMatchException
	{
		String datePattern = "(\\d{1,2})/(\\d{1,2})";
		String datePattern1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
		String datePattern2 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
		int monthF = 0;
		int dayF = 0;


		int monthT = 0;
		int dayT = 0;
		int yearT = 0;
		int yearF = 0;
		yearF = Integer.valueOf(strYear);
		yearT = Integer.valueOf(strYear);
		if(dateF.matches(datePattern))
		{
			Pattern patt = Pattern.compile(datePattern);
			Matcher matcher = patt.matcher(dateF);
			matcher.lookingAt();

			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));		
			monthT=monthF;
			dayT=dayF;	

		}else if(dateF.matches(datePattern1))
		{
			Pattern patt = Pattern.compile(datePattern1);
			Matcher matcher = patt.matcher(dateF);			
			matcher.lookingAt();
			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));

			monthT=Integer.valueOf(matcher.group(3));
			dayT=Integer.valueOf(matcher.group(4));
		}
		else if(dateF.matches(datePattern2))
		{
			Pattern patt = Pattern.compile(datePattern2);
			Matcher matcher = patt.matcher(dateF);			
			matcher.lookingAt();
			monthF=Integer.valueOf(matcher.group(1));
			dayF=Integer.valueOf(matcher.group(2));
			monthT=Integer.valueOf(matcher.group(1));
			dayT=Integer.valueOf(matcher.group(3));
		}
		else
		{
			throw new VesselDateNotMatchException("vessel date not match:",dateF);
		}


		if(Math.abs(currentMonth-monthF)>=6)
		{
			if(currentMonth<monthF)
				yearF--;
			if(currentMonth>monthF)
				yearF++;
		}
		if(Math.abs(currentMonth-monthT)>=6)
		{
			if(currentMonth<monthT)
				yearT--;
			if(currentMonth>monthT)
				yearT++;
		}

		String dd[] = new String[2]; 


		{
			dd[0] = yearF+"/"+monthF+"/"+dayF;
			dd[1] = yearT+"/"+monthT+"/"+dayT;
		}
		return dd;

	}
	public static String[] adjestDateYear(String dateF, String dateT, String inOutBoundType, int currentMonth, String currentYear) throws NotSupportedDateTypeException{

		dateT = dateT.replace(" ", "");

		dateF = dateF.replace(" ", "");

		int monthAndDayF[]=KSGDateUtil.getETD(dateF);
		int monthAndDayT[]=KSGDateUtil.getETA(dateT);

		//수정 : 2011-12-08
		/*
		전년도	당해년도	 차년도
		10,11,12	1,2,3,4,5,6,7,8,9	-
		11,12	1,2,3,4,5,6,7,8,9,10	-
		12	1,2,3,4,5,6,7,8,9,10,11	-
		-	1,2,3,4,5,6,7,8,9,10,11,12	-
		-	2,3,4,5,6,7,8,9,10,11,12	1
		-	3,4,5,6,7,8,9,10,11,12	1,2
		-	4,5,6,7,8,9,10,11,12	1,2,3
		-	5,6,7,8,9,10,11,12	1,2,3,4
		-	6,7,8,9,10,11,12	1,2,3,4,5
		-	7,8,9,10,11,12	1,2,3,4,5,6
		-	8,9,10,11,12	1,2,3,4,5,6,7
		-	9,10,11,12	1,2,3,4,5,6,7,8

		 */

		int yearF =KSGDateUtil.selectYear(currentMonth, monthAndDayF[0], Integer.valueOf(currentYear));
		int  yearT =KSGDateUtil.selectYear(currentMonth, monthAndDayT[0], Integer.valueOf(currentYear));


		String dd[] = new String[2];

		dd[0] = yearF+"/"+monthAndDayF[0]+"/"+monthAndDayF[1];
		dd[1] = yearT+"/"+monthAndDayT[0]+"/"+monthAndDayT[1];


		return dd;
	}
	public static String[] adjestDateYear2(String dateF, String dateT, String inOutBoundType, int currentMonth, String currentYear) throws NotSupportedDateTypeException{

		dateT = dateT.replace(" ", "");

		dateF = dateF.replace(" ", "");

		int monthAndDayF[]=KSGDateUtil.getETA(dateF);
		int monthAndDayT[]=KSGDateUtil.getETD(dateT);

		//수정 : 2011-12-08
		/*
		전년도	당해년도	 차년도
		10,11,12	1,2,3,4,5,6,7,8,9	-
		11,12	1,2,3,4,5,6,7,8,9,10	-
		12	1,2,3,4,5,6,7,8,9,10,11	-
		-	1,2,3,4,5,6,7,8,9,10,11,12	-
		-	2,3,4,5,6,7,8,9,10,11,12	1
		-	3,4,5,6,7,8,9,10,11,12	1,2
		-	4,5,6,7,8,9,10,11,12	1,2,3
		-	5,6,7,8,9,10,11,12	1,2,3,4
		-	6,7,8,9,10,11,12	1,2,3,4,5
		-	7,8,9,10,11,12	1,2,3,4,5,6
		-	8,9,10,11,12	1,2,3,4,5,6,7
		-	9,10,11,12	1,2,3,4,5,6,7,8

		 */

		int yearF =KSGDateUtil.selectYear(currentMonth, monthAndDayF[0], Integer.valueOf(currentYear));
		int yearT =KSGDateUtil.selectYear(currentMonth, monthAndDayT[0], Integer.valueOf(currentYear));


		String dd[] = new String[2];

		dd[0] = yearF+"/"+monthAndDayF[0]+"/"+monthAndDayF[1];
		dd[1] = yearT+"/"+monthAndDayT[0]+"/"+monthAndDayT[1];


		return dd;
	}
	
	public static String convertDateFormatYYYYMMDDToMMDD(String date)
	{
		try {
			return outputDateFormat.format(inputDateFormat.parse(String.valueOf(date)));
		} catch (ParseException e) {
			return date;
		}
	}
	public static SimpleDateFormat createInputDateFormat() {
		return new SimpleDateFormat("yyyy/MM/dd");
	}
	public static SimpleDateFormat createOutputDateFormat() {
		return new SimpleDateFormat("M/d");
	}
	


}
