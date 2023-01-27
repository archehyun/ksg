package com.ksg.schedule.logic.joint;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ksg.domain.AreaEnum;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.route.RouteScheduleUtil;

public class RouteScheduleJointTest {

	
	@Test
	public void testCheckOutPort() throws Exception {
		
		
		
		 assertTrue(RouteScheduleUtil.checkOutPort(AreaEnum.ASIA.getName(), 3));
		 
		 assertFalse(RouteScheduleUtil.checkOutPort(AreaEnum.CHINA.getName(), 1));
		
	}
	@Test
	public void testCreateSche() throws Exception
	{
		ScheduleManager.getInstance().init();
		ShippersTable op = new ShippersTable();
		op.setDate_isusse("2023-01-30");
		RouteScheduleJoint schedule= new RouteScheduleJoint(op, 1);
		schedule.execute();
	}
	

}