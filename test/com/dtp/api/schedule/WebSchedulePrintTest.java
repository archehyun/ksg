package com.dtp.api.schedule;

import org.junit.Test;

import com.dtp.api.schedule.joint.print.webschedule.WebSchedulePrint;

public class WebSchedulePrintTest {
	
	@Test
	public void printTest()
	{
		WebSchedulePrint print = new WebSchedulePrint("");
		print.execute();
		
	}

}
