package com.dtp.api.control;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ksg.common.model.CommandMap;

public class ScheduleControllerTest {

	ScheduleController controller;
	@Before
	public void setUp()
	{
		controller = new ScheduleController();
	}
	@Test
	public void testSelectScheduleList() {
		try {
			CommandMap result =controller.selectScheduleList("O", "2022-06-20");
			List data = (List) result.get("data");
			
			
			
			assertNotNull(data);
			assertTrue(data.size()>0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSelectRouteScheduleList() {
//		try {
//			
//			CommandMap param = new CommandMap();
//			param.put("inOutType", "O");
//			CommandMap result =(CommandMap) controller.selectRouteScheduleGroupList(param);
//				
//			TreeNodeManager node = new TreeNodeManager();
//			
//			DefaultMutableTreeNode tt = node.getRouteTreeNode(result);
//			
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
//	@Test
//	public void testSelectScheduleGroupList() {
//		try {
//			CommandMap result =controller.selectScheduleGroupMap("O", "2022-06-20");
//			CommandMap data = (CommandMap) result.get("data");
//			assertNotNull(data);
//			assertTrue(data.size()>0);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
