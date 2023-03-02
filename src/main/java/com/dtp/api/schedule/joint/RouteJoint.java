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
	 * 1. ���� ��¥ �������� ������ �и�
	 * 2. �ܱ��� ���ڿ� ���� ������ �и�
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

			// ������ ���� ���� �Ǵ�
			if(routeScheduleGroup.isRouteScheduleValidation(areaName))
			{	
				
				scheduleGroupList.add(routeScheduleGroup);
			}
		}
		return scheduleGroupList;  
	}
	
	/**
	 * �����, ������ �� �׷� ����
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
			
			// �������� ���� ��¥�� ����
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
