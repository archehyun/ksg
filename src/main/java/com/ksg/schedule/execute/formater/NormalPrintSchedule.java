package com.ksg.schedule.execute.formater;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import com.ksg.schedule.execute.joint.impl.JointedSchehduleData;
import com.ksg.schedule.execute.joint.impl.ScheduleList;

public class NormalPrintSchedule<E> extends PrintSchedule
{
	ScheduleList<E> scheduelList;

	public NormalPrintSchedule(ScheduleList<E> scheduelList) throws IOException  {
		this.scheduelList =scheduelList;
	}

	public void printArea(String area) throws IOException
	{
		System.out.println("Area:"+area);
		fw.write("Area:"+area);
	}

	public void printToPort(String port) throws IOException
	{
		System.out.println(" \r\n"+port);
		fw.write(" \r\n"+port);
	}

	public void print() throws IOException
	{	
		Iterator<ScheduleList> areaIter= (Iterator<ScheduleList>) scheduelList.iterator();

		try {
			fw = new FileWriter(fileLocation+"/"+"test.txt");

			while(areaIter.hasNext())
			{
				ScheduleList area = areaIter.next();

				printArea(area.getName());

				Iterator<ScheduleList> toPortIter = area.iterator();
				// 정렬
				while(toPortIter.hasNext())
				{
					ScheduleList toPort = toPortIter.next();

					printToPort(toPort.getName());

					Iterator<ScheduleList> fromPortIter= toPort.iterator();

					//정렬
					while(fromPortIter.hasNext())
					{
						ScheduleList<JointedSchehduleData> scheduleList = fromPortIter.next();

						printFromPort(scheduleList.getName());						

						// 정렬
						for(JointedSchehduleData temp:scheduleList)
						{
							printSchedule(temp);
						}
					}
				}
			}
		}
		catch(Exception e)
		{	
			e.printStackTrace();
			throw new RuntimeException();
		}
		finally
		{
			fw.close();
		}
	}

	private String getScheduleInfo(JointedSchehduleData schedule)	
	{
		String vessel_name 	= schedule.vesselName();
		String vessel_type = schedule.vesselType();
		String company 		= schedule.company();
		String dateF 		= schedule.fromDate();
		String dateT 		= schedule.toDate();

		return getFormatedDate(dateF)+"\t"+vessel_name+"["+vessel_type+"]   ("+company+")\t"+getFormatedDate(dateT)+ "\r\n";

	}

	public void printSchedule(JointedSchehduleData temp) throws IOException {
		System.out.println("\t\t\t"+getScheduleInfo(temp));
		fw.write("\t\t\t"+getScheduleInfo(temp));
	}

	public void printFromPort(String name) throws IOException {
		System.out.println(" \r\n- "+name+" -\r\n");
		fw.write(" \r\n- "+name+" -\r\n");
	}
	
	
	public String getFormatedDate(String date)
	{
		return date;
//		try {
//			
//			return outputDateFormat.format(inputDateFormat.parse(date));
//		} catch (ParseException e) {
//			
//			return "error";
//		}
	}
}