package com.dtp.api.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.AreaEnum;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.route.RouteScheduleUtil;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.workbench.common.comp.treetable.nodemager.RouteNodeManager;
import com.ksg.workbench.common.comp.treetable.nodemager.TreeNodeManager;
import com.ksg.workbench.schedule.comp.treenode.SortedSchedule;

public class TreeNodeTest {

	TreeNodeManager manager;
	@Before
	public void setUp()
	{
		manager = new TreeNodeManager();
	}
	@Test
	public void test()
	{
		

		ArrayList<SortedSchedule> list = new ArrayList<SortedSchedule>();

		SortedSchedule one = new SortedSchedule(ScheduleData.builder().DateF("2022/5/12")
				.DateT("2022/5/12")
				.company_abbr("test3")
				.build());
		SortedSchedule two = new SortedSchedule(ScheduleData.builder().DateF("2022/5/13")
				.DateT("2022/5/15")
				.company_abbr("test1")
				.build());
		SortedSchedule three = new SortedSchedule(ScheduleData.builder().DateF("2022/5/11")
				.DateT("2022/5/14")
				.company_abbr("test1")
				.build());

		list.add(one);
		list.add(two);
		list.add(three);
		

	}
	@Test
	public void test2()
	{
		
	

		// 중복항구 제고

		// 출발일 정렬

		// 도착일 정렬
		ArrayList<ScheduleData> scheduleList = new ArrayList<ScheduleData>();
		scheduleList.add(ScheduleData.builder().fromPort("BUSAN")
				.DateF("2022/5/13")
				.port("TEST")
				.DateT("2022/5/13")
				.build());

		scheduleList.add(ScheduleData.builder().fromPort("ULSAN")
				.DateF("2022/5/14")
				.port("TEST1")
				.DateT("2022/5/13")
				.build());

		scheduleList.add(ScheduleData.builder().fromPort("BUSAN")
				.DateF("2022/5/12")
				.port("TEST2")
				.DateT("2022/5/14")
				.build());	
		

		// 출발항 목록
		
		Map<String, List<ScheduleData>> fromPorts =scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getFromPort));

		Map<String, List<ScheduleData>> toPorts =scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getPort));


		//System.out.println( manager.toJointedRouteScheduleString(fromPorts, toPorts));		

		
	}
	
	@Test
	public void selectRouteScheduleGroupListTest3() throws SQLException
	{
		ScheduleSubService  scheduleService = new ScheduleServiceImpl();
		CommandMap param = new CommandMap();
		param.put("inOutType","O");
		//CommandMap result = (CommandMap) scheduleService.selectRouteScheduleGroupList(param);
		
		
		//manager.getRouteTreeNode(result);
		
	}
	
	@Test
	public void checkRouteTest3() throws SQLException
	{
		
		assertFalse(RouteScheduleUtil.checkOutPort(AreaEnum.JAPAN.toString(), 1));
		assertFalse(RouteScheduleUtil.checkOutPort(AreaEnum.CHINA.toString(), 1));
		assertFalse(RouteScheduleUtil.checkOutPort("test", 2));
		
	}
	
	
	

}
