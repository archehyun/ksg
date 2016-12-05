package com.ksg.schedule.outbound;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

import com.ksg.schedule.build.DefaultScheduleBuild;

public class OutboundScheduleBuildV2Test {

	
	OutboundScheduleBuildV2 outbound;
	
	
	@Test
	public void testOutboundScheduleBuildV2() throws SQLException {
		outbound = new OutboundScheduleBuildV2();
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetNumericVoyage() throws SQLException {
		System.out.println(DefaultScheduleBuild.getNumericVoyage("0030W"));
		System.out.println(DefaultScheduleBuild.getNumericVoyage("029QHWCNC"));
		System.out.println(DefaultScheduleBuild.getNumericVoyage("0030W"));
		System.out.println(DefaultScheduleBuild.getNumericVoyage("003 W"));
		System.out.println(DefaultScheduleBuild.getNumericVoyage("0009/0010E"));
		System.out.println(DefaultScheduleBuild.getNumericVoyage("009W/009E"));
		
		fail("Not yet implemented");
	}

}
