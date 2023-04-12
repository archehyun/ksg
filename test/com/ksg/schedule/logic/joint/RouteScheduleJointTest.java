package com.ksg.schedule.logic.joint;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ksg.domain.AreaEnum;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.print.route.RouteScheduleJointV1;
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
		ScheduleManager.getInstance().initMasterData();
		ShippersTable op = new ShippersTable();
		op.setDate_isusse("2023-01-30");
		RouteScheduleJointV1 schedule= new RouteScheduleJointV1(op, 1);
		schedule.init();
		schedule.execute();
	}
	

}
