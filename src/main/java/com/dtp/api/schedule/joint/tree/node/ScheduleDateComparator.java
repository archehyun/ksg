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

 * @�ۼ��� : pch

 * @�����̷� :

 * @���α׷� ���� : ������ ��¥ ����
 *  ������� ���� ���ڰ� ������..
 *  �������� ���� ���ڰ� ������..
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
				
				// ����� ���� ��� ������ �������� ����
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
