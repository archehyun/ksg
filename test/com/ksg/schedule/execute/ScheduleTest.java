package com.ksg.schedule.execute;

import org.junit.Test;

import com.ksg.schedule.execute.proxy.ScheduleFactory;

public class ScheduleTest {

	@Test
	public void test() {
		Executeable schedule= ScheduleFactory.createNormalScheduleBuild("2021-12-01");
		schedule.execute();
	}

}
