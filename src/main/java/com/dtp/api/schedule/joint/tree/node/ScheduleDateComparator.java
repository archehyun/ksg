package com.dtp.api.schedule.joint.tree.node;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import com.ksg.domain.ScheduleData;

/**
 * 

 * @FileName : ScheduleDateComparator.java

 * @Project : KSG2

 * @Date : 2023. 1. 16. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 스케줄 날짜 정렬
 *  출발항은 늦은 날자가 앞으로..
 *  도착항은 빠른 날자가 앞으로..
 */
public class ScheduleDateComparator implements Comparator<ScheduleData> {
	public static final int FROM_DATE=0;
	public static final int TO_DATE=1;
	private int dateType;

	public ScheduleDateComparator(int dateType)
	{
		this.dateType =dateType;
	}

	private  SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");
	
	@Override
	public int compare(ScheduleData f1, ScheduleData f2) {

		try {

			switch (dateType) {
			case FROM_DATE:

				int result = formatYYYYMMDD.parse(f1.getDateF()).compareTo(formatYYYYMMDD.parse(f2.getDateF()));
				
				if(result!=0)return result;
				
				// 출발일 같은 경우 도착일 기준으로 정렬
				return formatYYYYMMDD.parse(f1.getDateT()).compareTo(formatYYYYMMDD.parse(f2.getDateT()));



			case TO_DATE:
				return formatYYYYMMDD.parse(f1.getDateT()).compareTo(formatYYYYMMDD.parse(f2.getDateT()))*-1;


			default:
				return 0;
			}

		} catch (ParseException e) {
			return 0;
		}
	}


}
