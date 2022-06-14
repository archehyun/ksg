package com.ksg.schedule.execute;

import org.junit.Test;

import com.ksg.schedule.execute.proxy.ScheduleFactory;

public class ScheduleTest {

	//@Test
	public void test() {
		Executeable schedule= ScheduleFactory.createNormalScheduleBuild("2021-12-01");
		schedule.execute();
	}
	
//	@Test
	public void test2() {
		Executeable schedule= ScheduleFactory.createOutboundScheduleJoint("2021-12-06");
		schedule.execute();
	}
	
	@Test
	public void test3() {
		Executeable schedule= ScheduleFactory.createInboundScheduleJoint("2021-12-06");
		schedule.execute();
	}
	
//	@Test
	public void test4() {
		Executeable schedule= ScheduleFactory.createRoputeScheduleJoint("2021-12-06");
		schedule.execute();
	}

}
