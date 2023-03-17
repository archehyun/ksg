package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ksg.common.model.CommandMap;
import com.ksg.service.ScheduleSubService;

public class ScheduleServiceImplTest {
	
	
	ScheduleSubService service;
	
	@Before
	public void setUp()
	{
		service = new ScheduleServiceImpl();
	}

	@Test
	public void testSelectOutboundScheduleGroupList() {
		
		CommandMap map = new CommandMap();
		map.put("inOutType", "O");
//		try {
//			Map<String, Object> li= service.selectOutboundScheduleGroupList(map);
//			
//		System.out.println("size:"+li.size());
//			
//			li.forEach((strKey, strValue)->{
//				System.out.println( strKey +":"+ strValue );
//			});
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
