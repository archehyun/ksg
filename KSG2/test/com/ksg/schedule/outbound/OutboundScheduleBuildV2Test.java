package com.ksg.schedule.outbound;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import com.ksg.schedule.logic.joint.DefaultScheduleJoint;
import com.ksg.schedule.logic.joint.OutboundScheduleJointV2;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;

public class OutboundScheduleBuildV2Test {

	
	OutboundScheduleJointV2 outbound;
	
	
	@Test
	public void testOutboundScheduleBuildV2() throws SQLException {
		try {
			outbound = new OutboundScheduleJointV2();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetNumericVoyage() throws SQLException {
		System.out.println(ScheduleBuildUtil.getNumericVoyage("0030W"));
		System.out.println(ScheduleBuildUtil.getNumericVoyage("029QHWCNC"));
		System.out.println(ScheduleBuildUtil.getNumericVoyage("0030W"));
		System.out.println(ScheduleBuildUtil.getNumericVoyage("003 W"));
		System.out.println(ScheduleBuildUtil.getNumericVoyage("0009/0010E"));
		System.out.println(ScheduleBuildUtil.getNumericVoyage("009W/009E"));
		
		fail("Not yet implemented");
	}

}
