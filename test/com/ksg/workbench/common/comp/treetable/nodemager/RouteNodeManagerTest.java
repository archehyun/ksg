package com.ksg.workbench.common.comp.treetable.nodemager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.ksg.domain.ScheduleData;
import com.ksg.workbench.common.comp.treetable.node.ScheduleDateComparator;

public class RouteNodeManagerTest {

	@Test
	public void test() {
		List<ScheduleData> li = new ArrayList<ScheduleData>();
		
		
		li.add(ScheduleData.builder().DateF("2023/2/11").DateT("2023/3/2").build());
		li.add(ScheduleData.builder().DateF("2023/2/12").DateT("2023/3/2").build());
		li.add(ScheduleData.builder().DateF("2023/2/12").DateT("2023/3/1").build());
		li.add(ScheduleData.builder().DateF("2023/2/12").DateT("2023/3/3").build());
		li.add(ScheduleData.builder().DateF("2023/2/10").DateT("2023/3/3").build());
		
		Collections.sort(li, new ScheduleDateComparator(ScheduleDateComparator.FROM_DATE));
		
		li.stream().forEach(o->System.out.println(String.format("%s-%s",o.getDateF(), o.getDateT())));
	}

}
