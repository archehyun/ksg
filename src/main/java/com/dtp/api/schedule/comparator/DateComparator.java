package com.dtp.api.schedule.comparator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateComparator implements Comparator<IFComparator>{
	
	protected SimpleDateFormat dateformat;
	
	public DateComparator(SimpleDateFormat dateformat)
	{
		this.dateformat =dateformat; 
	}
	
	@Override
	public int compare(IFComparator f1, IFComparator f2) {

		try {

			return dateformat.parse(f1.getDateF()).compareTo(dateformat.parse(f2.getDateF()));

		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

}
