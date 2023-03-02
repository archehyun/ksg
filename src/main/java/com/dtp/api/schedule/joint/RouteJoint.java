package com.dtp.api.schedule.joint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.dtp.api.schedule.comparator.DateComparator;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.joint.route.PortAndDay;
import com.ksg.schedule.logic.joint.route.RouteScheduleGroup;
import com.ksg.schedule.logic.route.RouteScheduleUtil;
import com.ksg.workbench.common.comp.treetable.node.ScheduleDateComparator;

public class RouteJoint {
	
	public RouteJoint()
	{}
	RouteJointSubject subject;
	public RouteJoint(RouteJointSubject subject) {
		this.subject = subject;
	}
	
	private DateComparator dateComparator 	= new DateComparator(new SimpleDateFormat("yyyy/MM/dd"));
	
	/**
	 * 1. 기존 날짜 기준으로 스케줄 분리
	 * 2. 외국항 숫자에 따라 스케줄 분리
	 * 
	 * @param areaName
	 * @param vesselName
	 * @param subscheduleList
	 * @see RouteScheduleUtil#divideScheduleByArea
	 * @see RouteScheduleUtil#isRouteScheduleValidation
	 * @return
	 */
	public List getValidatedScheduleGroupList(String areaName,String vesselName, List<ScheduleData> subscheduleList)
	{
		Collections.sort(subscheduleList, new ScheduleDateComparator(ScheduleDateComparator.FROM_DATE));

		List[] li=RouteScheduleUtil.divideScheduleByArea(subscheduleList, areaName);
		
		List<RouteScheduleGroup> scheduleGroupList = new ArrayList<RouteScheduleGroup>();

		for(List item:li)
		{
			RouteScheduleGroup routeScheduleGroup = new RouteScheduleGroup(vesselName,item);

			// 도착항 수에 따라 판단
			if(routeScheduleGroup.isRouteScheduleValidation(areaName))
			{	
				
				scheduleGroupList.add(routeScheduleGroup);
			}
		}
		return scheduleGroupList;  
	}
	
	/**
	 * 출발일, 도착일 별 그룹 생성
	 * @param ports
	 * @param dateType
	 * @return
	 */
	public List<PortAndDay> makeDayList(Map<String, List<ScheduleData>> ports,  int dateType)
	{
		ArrayList<PortAndDay> list = new ArrayList<PortAndDay>();

		ports.entrySet().stream()
		.forEach(entry -> {

			List<ScheduleData> ll =entry.getValue();

			Collections.sort(ll, new ScheduleDateComparator(dateType));

			ScheduleData lastSchedule = ll.get(ll.size()-1);
			
			// 도착항은 빠른 날짜로 정렬
			list.add(new PortAndDay(entry.getKey(), dateType==ScheduleDateComparator.FROM_DATE?lastSchedule.getDateF():lastSchedule.getDateT()));

		});

		Collections.sort(list, dateComparator);

		return list;

	}
	public void createScheduleAndAddGroup(List group, List scheduleList, String areaName, String vesselName)
	{
		subject.createScheduleAndAddGroup(group, scheduleList, areaName, vesselName);
	}
	
	


}
