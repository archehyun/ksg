package com.dtp.api.control;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ksg.common.model.CommandMap;

public class ScheduleControllerTest {

	ScheduleController controller;
	@Before
	public void setUp()
	{
		controller = new ScheduleController();
	}
	@Ignore
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
	public void testCreateSchedule() {
		
		CommandMap param = new CommandMap();
		
		param.put("table_date", "20230628");
		
		controller.call("createSchedule2", param,null);
	}
	@Ignore
	@Test
	public void test()
	{
		int index[]= {8,9,10};
		
		List strList = Arrays.stream(index)        // IntStream
                .boxed()          // Stream<Integer>
                .collect(Collectors.toList());
		
		strList.forEach(obj->System.out.println(obj));
		
	}
	@Ignore
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
