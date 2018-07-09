package com.ksg.schedule.outbound;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import com.ksg.schedule.logic.joint.DefaultScheduleJoint;
import com.ksg.schedule.logic.joint.OutboundScheduleJointV2;

public class OutboundScheduleBuildV2Test {

	
	OutboundScheduleJointV2 outbound;
	
	
	@Test
	public void testOutboundScheduleBuildV2() throws SQLException {
		outbound = new OutboundScheduleJointV2();
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetNumericVoyage() throws SQLException {
		System.out.println(DefaultScheduleJoint.getNumericVoyage("0030W"));
		System.out.println(DefaultScheduleJoint.getNumericVoyage("029QHWCNC"));
		System.out.println(DefaultScheduleJoint.getNumericVoyage("0030W"));
		System.out.println(DefaultScheduleJoint.getNumericVoyage("003 W"));
		System.out.println(DefaultScheduleJoint.getNumericVoyage("0009/0010E"));
		System.out.println(DefaultScheduleJoint.getNumericVoyage("009W/009E"));
		
		fail("Not yet implemented");
	}

}
