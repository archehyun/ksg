package com.dtp.api.control;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ksg.domain.TablePort;

public class ShipperTableControllerTest {
	
	
	private ShipperTableController controller;

	@Before
	public void setUp()
	{
//		controller = new ShipperTableController();
	}
	@Ignore
	@Test
	public void selectTest() throws SQLException
	{
		controller.select(null);
	}
	@Ignore
	@Test
	public void createSchedule() throws SQLException
	{
//		controlle
	}
	@Ignore
	@Test
	public void tt()
	{
		
		int a =0;
				
		System.out.println(a/10);
	}
	
	
	@Test
	public void tt1()
	{
		
		ArrayList<TablePort> list = new ArrayList<TablePort>();
		
		list.add(TablePort.builder().port_name("BUSAN NEW")
				.port_index(1).build());
		list.add(TablePort.builder().port_name("BUSAN")
				.port_index(3).build());

		int index[]= {1,2};
		
		List strList = Arrays.stream(index)        // IntStream
				.boxed()          // Stream<Integer>
				.collect(Collectors.toList());

		List list2=list.stream()
				.distinct()
				.filter(o ->strList.contains(o.getPort_index()))
				.filter(o -> o.getPort_name().equals("BUSAN")||o.getPort_name().equals("BUSAN NEW"))
				.collect(Collectors.toList());

		System.out.println(list2.size());
	}

}
