package com.ksg.schedule.web;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;

import com.ksg.domain.ShippersTable;

public class DefaultWebScheduleV2Test {
	
	DefaultWebScheduleV2 testCase;
	
	public void setUp()
	{
		try {
			testCase = new DefaultWebScheduleV2(DefaultWebScheduleV2.FORMAT_NOMAL, new ShippersTable());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			fail("fail init");
		}
	}

	@Test
	public void testIsTableDataValidation() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPortInfoByPortName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPortInfoAbbrByPortName() {
		fail("Not yet implemented");
	}

	@Test
	public void testExecute() {
		
		try {

			try {
				ShippersTable parameter = new ShippersTable();
				parameter.setDate_isusse("2016.3.28");
				//parameter.setGubun("Normal");
				//parameter.setGubun("Console");
				parameter.setGubun("Inland");
				testCase = new DefaultWebScheduleV2(DefaultWebScheduleV2.FORMAT_INLNAD, parameter);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				fail("fail init");
			}
			
			
			testCase.execute();
		} catch (Exception e) {
			
			e.printStackTrace();
			fail("test fail:"+e.getMessage());
			System.exit(-1);
		}
		fail("Not yet implemented");
	}

	@Test
	public void testGetPortAreaCode() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPortList() {
		fail("Not yet implemented");
	}

	@Test
	public void testMakePortArrayWebIndexMap() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetXMLFullVesselArray() {
		fail("Not yet implemented");
	}

}
