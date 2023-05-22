package com.dtp.api.schedule.joint.print.route;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dtp.api.schedule.comparator.DateComparator;
import com.dtp.api.schedule.joint.tree.node.ScheduleDateComparator;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;
import com.ksg.schedule.logic.route.RouteScheduleUtil;

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
	public List getValidatedScheduleGroupList(String areaName,Vessel vessel, List<ScheduleData> subscheduleList, boolean isValid)
	{
		Collections.sort(subscheduleList, new ScheduleDateComparator(ScheduleDateComparator.FROM_DATE));

		List[] li=RouteScheduleUtil.divideScheduleByArea(subscheduleList, areaName);
		
		List<RouteScheduleGroup> scheduleGroupList = new ArrayList<RouteScheduleGroup>();

		for(List listItem:li)
		{
			RouteScheduleGroup routeScheduleGroup = new RouteScheduleGroup(vessel,listItem);

			// 도착항 수에 따라 판단
			if(routeScheduleGroup.isRouteScheduleValidation(areaName))
			{	
				scheduleGroupList.add(routeScheduleGroup);
			}
			else
			{
				if(isValid) scheduleGroupList.add(routeScheduleGroup);
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
	public List<PortAndDay> makeDayList(Map<String, List<ScheduleData>> ports, Comparator<ScheduleData> comparator , int DateType)
	{
		
		
		ArrayList<PortAndDay> list = new ArrayList<PortAndDay>();

		ports.entrySet().stream()
		.forEach(entry -> {

			List<ScheduleData> ll =entry.getValue();

			Collections.sort(ll, comparator);

			ScheduleData lastSchedule = ll.get(ll.size()-1);
			
			// 도착항은 빠른 날짜로 정렬
			list.add(new PortAndDay(entry.getKey(), DateType ==ScheduleDateComparator.FROM_DATE?lastSchedule.getDateF():lastSchedule.getDateT()));

		});

		Collections.sort(list, dateComparator);

		return list;

	}
	public void createScheduleAndAddGroup(List group, List scheduleList, String areaName, String vesselName)
	{
		subject.createScheduleAndAddGroup(group, scheduleList, areaName, vesselName);
	}
	
	public List createRouteScheduleGroupList(Map<String, List<ScheduleData>> vesselList,
			String strArea, RouteJointSubject delegate) {
		
		List scheduleGroupList = new ArrayList();
		
		Object[] vesselArray = vesselList.keySet().toArray();
		
		for (Object vesselKey : vesselArray)
		{	
			List<ScheduleData> scheduleList = (List<ScheduleData>) vesselList.get(vesselKey);
			
			// 항차번호(숫자)로 그룹화
			Map<Integer, List<ScheduleData>> voyageList =  scheduleList.stream().collect(
					Collectors.groupingBy(o -> ScheduleBuildUtil. getNumericVoyage(o.getVoyage_num()) ));// 항차
			
			// 항차번호로 정렬(오름차순)								
			voyageList.keySet().stream()
								.forEach(voyagekey ->delegate.createScheduleAndAddGroup(scheduleGroupList, voyageList.get(voyagekey), (String)strArea, (String)vesselKey));
			
		}
		return scheduleGroupList;
	}
	
	


}
