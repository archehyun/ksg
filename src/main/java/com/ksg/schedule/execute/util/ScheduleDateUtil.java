package com.ksg.schedule.execute.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ksg.commands.schedule.NotSupportedDateTypeException;


/**

  * @FileName : ScheduleDateUtil.java

  * @Project : KSG2

  * @Date : 2022. 5. 18. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 스케줄 날짜 지정

  */
public class ScheduleDateUtil {
	
	
	private static String datePattern = "(\\d{1,2})/(\\d{1,2})";
	private static String datePattern1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})";
	private static String datePattern1_1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})/(\\d{1,2})\\w+";
	private static String datePattern1_2 = "(\\d{1,2}).(\\d{1,2})/(\\d{1,2}).(\\d{1,2})";
	private static String datePattern2 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})";
	private static String datePattern2_1 = "(\\d{1,2})/(\\d{1,2})-(\\d{1,2})\\w+";
	private static int yearF;
	private static int yearT;
	
	
	public static String[] getScheduleDate(String dateF, String dateT, String inOutBoundType) throws NotSupportedDateTypeException{
		
		SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");

		String currentYear = sdfYear.format(new Date());
		int currentMonth = Integer.valueOf(sdfMonth.format(new Date()));


		dateT = dateT.replace(" ", "");

		dateF = dateF.replace(" ", "");

		int monthAndDayF[]=getETD(dateF);
		int monthAndDayT[]=getETA(dateT);

		// 날짜 유효성 체크 2020-01-28
		if(monthAndDayF[0]==0||monthAndDayF[1]==0)
			throw new NotSupportedDateTypeException(dateF, 0);
		if(monthAndDayT[0]==0||monthAndDayT[1]==0)
			throw new NotSupportedDateTypeException(dateT, 0);


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

		yearF =selectYear(currentMonth, monthAndDayF[0], Integer.valueOf(currentYear));
		yearT =selectYear(currentMonth, monthAndDayT[0], Integer.valueOf(currentYear));


		String newDate[] = new String[2];

		newDate[0] = yearF+"/"+monthAndDayF[0]+"/"+monthAndDayF[1];
		newDate[1] = yearT+"/"+monthAndDayT[0]+"/"+monthAndDayT[1];


		return newDate;
	}
	
	/**연도 선택
	 * 
	 * @param currentMonth
	 * @param targetMonth
	 * @param currentYear
	 * @return
	 */
	private static int selectYear(int currentMonth, int targetMonth,int currentYear )
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
	
	/**
	 * @see 뒷날짜 선택
	 * @param date
	 * @return
	 * @throws NotSupportedDateTypeException
	 */
	private static int[] getETD(String date)
			throws NotSupportedDateTypeException {
		
		SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
		SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");

		String currentYear = sdfYear.format(new Date());


		
		yearF = Integer.valueOf(currentYear);
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
			/*if(!date.equals("-"))
			{
				logger.error(date);	
			}		*/	

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
	private static int[] getETA(String date)
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

}
