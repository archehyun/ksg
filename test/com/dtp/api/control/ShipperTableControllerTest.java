package com.dtp.api.control;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ShipperTableControllerTest {
	
	
	private ShipperTableController controller;

	@Before
	public void setUp()
	{
		controller = new ShipperTableController();
	}
	
	@Test
	public void selectTest() throws SQLException
	{
		controller.select(null);
	}
	@Ignore
	@Test
	public void tt()
	{
		
		int a =0;
				
		System.out.println(a/10);
	}

}
