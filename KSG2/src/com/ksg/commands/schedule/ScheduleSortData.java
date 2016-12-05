package com.ksg.commands.schedule;

import java.sql.Date;
import java.text.ParseException;

import org.jdom.Element;

import com.ksg.domain.ScheduleData;
import com.ksg.view.util.KSGDateUtil;

public class ScheduleSortData extends ScheduleData
{
	public Object sortData;
	public Object key;
	public Object value;
	public ScheduleSortData(String key, Object sortData) throws ParseException
	{
		this.sortData =sortData;
		KSGDateUtil.toDate4(sortData);
		this.key = key;

	}
	public ScheduleSortData(Element data, Object sortData) 
	{
		this.sortData =sortData;
		this.key = data;
	}
	public int compareTo(Object o) {
		try 
		{


			Date l1 = KSGDateUtil.toDate4(sortData);
			Date e1 = KSGDateUtil.toDate4(o.toString());


			return KSGDateUtil.daysDiff(e1,l1);

		}
		catch (ParseException e) 
		{
			e.printStackTrace();
			return -1;
		}
	}
	public String toString()
	{
		return (String) sortData;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) 
	{
		this.value = value;
	}
}