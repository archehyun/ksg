package com.ksg.schedule.logic.joint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;

import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.route.RouteScheduleJointV1;
import com.ksg.schedule.logic.print.route.RouteSchedulePrintFile;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;

public class RouteScheduleJoint4Test {
	
	@Ignore
	@Test
	public void testDate() throws Exception {
		SimpleDateFormat dateFormat =new SimpleDateFormat("yyyyMMddHHmmss");
		
		System.out.println("date:"+dateFormat.format(new Date()));
	}
//	@Ignore
	@Test
	public void testPrintRoute() throws Exception {
		
		ShippersTable op = new ShippersTable();
		
		op.setDate_isusse("2023-03-06");
		
		RouteSchedulePrintFile jointV4 = new RouteSchedulePrintFile("2023-03-06",RouteScheduleJointV1.ORDER_BY_DATE);
		
//		jointV4.initTag();
		
		jointV4 .execute();
		
	}
	@Ignore
	@Test
	public void testCheckOutPort() throws Exception {
		
		ShippersTable op = new ShippersTable();
		
		op.setDate_isusse("2023-02-13");
		
		RouteSchedulePrintFile jointV4 = new RouteSchedulePrintFile("2023-02-27",RouteScheduleJointV1.ORDER_BY_DATE);
		
		jointV4.init();
		
		jointV4 .execute();
		
	}
	@Ignore
	@Test
	public void test()
	{
		
		VesselService service = new VesselServiceImpl();
		
		List<Vessel> allVesselList = new ArrayList<Vessel>();
		
		allVesselList.add(Vessel.builder().vessel_name("test").build());
		allVesselList.add(Vessel.builder().vessel_name("test1").vessel_abbr("test11").build());
		allVesselList.add(Vessel.builder().vessel_name("test2").vessel_abbr("test12").build());
		allVesselList.add(Vessel.builder().vessel_name("test1").build());
		Map<String, Vessel> vesselNameMap = allVesselList.stream()
														.distinct()
														
														.collect(Collectors.toMap(Vessel::getVessel_name, Function.identity()));
		for(String name:vesselNameMap.keySet())
		{
			System.out.println(name);
		}
		
	}
	
	
}
