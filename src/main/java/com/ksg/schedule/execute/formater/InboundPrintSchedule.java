package com.ksg.schedule.execute.formater;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;


import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.execute.joint.impl.JointedSchehduleData;
import com.ksg.schedule.execute.joint.impl.ScheduleList;

/**

 * @FileName : PnNormalByTree.java

 * @Project : KSG2

 * @Date : 2022. 5. 25. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 :

 */
public class InboundPrintSchedule<E> extends PrintSchedule
{
	
	
	SimpleDateFormat inputDateFormat 	= KSGDateUtil.inputDateFormat;

	SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();
	
	ScheduleList<E> scheduelList;

	public InboundPrintSchedule(ScheduleList<E> scheduelList) throws IOException  {
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
					ScheduleList<JointedSchehduleData> toPort = toPortIter.next();

					printToPort(toPort.getName());

					
					// 정렬
					for(JointedSchehduleData temp:toPort)
					{
						printSchedule(temp);
					}

//					//정렬
//					while(fromPortIter.hasNext())
//					{
//						ScheduleList<JointedSchehduleData> scheduleList = fromPortIter.next();
//
//						printFromPort(scheduleList.getName());						
//
//						
//					}
				}
			}
		}
		catch(Exception e)
		{	
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
		StringBuffer buffer = new StringBuffer(); 
		for(ScheduleData item:schedule.getList())
		{
			buffer.append("["+item.getPort()+"]"+getFormatedDate(dateT));
		}
		String ports = buffer.toString();

		return getFormatedDate(dateF)+"\t"+vessel_name+"["+vessel_type+"]   ("+company+")\t" + ports+"\r\n";

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
//		return date;
		
			
			try {
				return outputDateFormat.format(inputDateFormat.parse(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "error";
			}
		
	}
}