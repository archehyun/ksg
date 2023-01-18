package com.ksg.schedule.logic.route;

import java.text.ParseException;

import com.ksg.common.util.KSGDateUtil;

public class RouteScheduleUtil {
	
	private static final String AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC = "AUSTRALIA, NEW ZEALAND & SOUTH PACIFIC";
	private static final String PERSIAN_GULF = "PERSIAN GULF";
	private static final String RUSSIA = "RUSSIA";
	private static final String ASIA = "ASIA";
	private static final String JAPAN = "JAPAN";
	private static final String CHINA = "CHINA";
	
	/**
	 * @설명 국내항 기준 공동배선 여부 결정  
	 *      중국, 일본, 러시아: 4일 
	 *      동남아: 6일
	 *      구주, 북미, 중남미, 아프리카 : 8일
	 *      기타 : 10일
	 *      
	 * @param area_name
	 * @return
	 * @throws ParseException
	 */
	public static int getSeperateIndex(String area_name, PortScheduleInfo[] sortedInPortList) throws ParseException
	{

		int index=-1;

		//PortScheduleInfo[] sortedInPortList = groupPort.createInPortArray();
		if(sortedInPortList.length<2)
			return index;

		
		
		
		int base=getGap(area_name.toUpperCase()); // 공동배선 기준 일자
		

		PortScheduleInfo firstInPort=sortedInPortList[0];
		
		for(int i=1;i<sortedInPortList.length;i++)
		{
			int differ = KSGDateUtil.daysDiff(PortDateUtil.parse(firstInPort.getDate()), PortDateUtil.parse(sortedInPortList[i].getDate()));
			if(differ>=base)
				return i;
		}
		return index;
	}
	
	public static int getGap(String area_name)
	{
		int base=0; // 공동배선 기준 일자
		String upCaseAreaName = area_name.toUpperCase();
		if(upCaseAreaName.equals(CHINA)||
				upCaseAreaName.equals(JAPAN)||
				upCaseAreaName.equals(RUSSIA)) // 중국, 일본, 러시아
		{
			base=4;
		}else if(upCaseAreaName.equals(ASIA)) // 동남아
		{
			base=6;

		}else if(upCaseAreaName.equals(PERSIAN_GULF)||
				upCaseAreaName.equals(AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC))// 중동, 호주
		{
			base=8;
		}		
		else // 구주, 북미, 중남미, 아프리카
		{
			base=10;
		}
		return base;
	}
	public static int getNumericVoyage(String voyage_num)
	{	
		try{
			return Integer.valueOf(voyage_num.replaceAll("[^0-9]", ""));
		}catch(Exception e)
		{
			return 0;
		}
	}

}
