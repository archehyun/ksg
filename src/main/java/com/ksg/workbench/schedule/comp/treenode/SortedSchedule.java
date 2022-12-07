package com.ksg.workbench.schedule.comp.treenode;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.ksg.domain.ScheduleData;




@SuppressWarnings("rawtypes")
public class SortedSchedule implements Comparable<SortedSchedule>
{
	ScheduleData data;
	
	 private  SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");
	
	public SortedSchedule(ScheduleData data) {
		this.data = data;
	}
	public ScheduleData getData()
	{
		return data;
	}

	@Override
	public int compareTo(SortedSchedule o) {
		// TODO Auto-generated method stub
		 try {
			return formatYYYYMMDD.parse(data.getDateF()).compareTo(formatYYYYMMDD.parse(o.getData().getDateF()));
		} catch (ParseException e) {
			return 0;
		}
	}
	
}
