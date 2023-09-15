package com.ksg.schedule.logic.joint;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ksg.schedule.logic.print.ScheduleBuildUtil;

public class ScheduleBuildUtilTest {

	@Test
	public void test() {
		
		
		assertNotEquals(ScheduleBuildUtil.getNumericVoyage("0079S"), ScheduleBuildUtil.getNumericVoyage("0080S"));
		
	}

}
