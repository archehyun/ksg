package com.ksg.schedule.execute.formater;

import java.io.IOException;

import com.ksg.schedule.execute.joint.impl.ScheduleList;

public class TagedPrintSchedule extends NormalPrintSchedule
{
	public TagedPrintSchedule(ScheduleList scheduelList) throws IOException  {
		super(scheduelList);
	}

	public void printArea(String area) throws IOException
	{
		System.out.println("Area1:"+area);
		fw.write("Area1:"+area);
	}

	public void printToPort(String port) throws IOException
	{
		System.out.println(" \r\n"+port);
		fw.write(" \r\n"+port);
	}
	public void printFromPort(String name) throws IOException {
		System.out.println(" \r\n- "+name+" -\r\n");
		fw.write(" \r\n- "+name+" -\r\n");

	}

}