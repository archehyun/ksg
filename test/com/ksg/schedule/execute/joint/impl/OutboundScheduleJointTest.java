package com.ksg.schedule.execute.joint.impl;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ksg.common.model.CommandMap;
import com.ksg.service.ScheduleServiceV2;
import com.ksg.service.impl.ScheduleServiceImpl;

public class OutboundScheduleJointTest {
	
	
	ScheduleServiceV2 scheduleService;
	OutboundScheduleJoint joint;
	@Before
	public void setUp()
	{
		scheduleService = new ScheduleServiceImpl();
		joint = new OutboundScheduleJoint("2021-12-06");
	}

	@Test
	public void testExecute() {
		CommandMap param = new CommandMap();
		param.put("inOutType", "O");
		param.put("date_issue", "2021-12-06");
		
		try {
			List area= scheduleService.selecteScheduleListByCondition(param);
			joint.buildSortedAndJointedScheduleList2(joint.buildOutboundScheduleGroup(area));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
