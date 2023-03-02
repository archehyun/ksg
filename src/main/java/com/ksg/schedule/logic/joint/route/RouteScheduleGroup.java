package com.ksg.schedule.logic.joint.route;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.schedule.comparator.IFComparator;
import com.dtp.api.schedule.joint.RouteJoint;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.route.RouteScheduleUtil;
import com.ksg.workbench.common.comp.treetable.node.ScheduleDateComparator;
/**
 * 

  * @FileName : RouteScheduleGroup.java

  * @Project : KSG2

  * @Date : 2023. 2. 13. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :
 */
public class RouteScheduleGroup implements IFComparator{
	
	private List<ScheduleData> scheduleList;

	private String date;

	private String vesselName;
	
	private Map<String, List<ScheduleData>> fromPorts;
	
	private Map<String, List<ScheduleData>> toPorts;
	
	private int toPortCount;
	
	private int fromPortCount;
	
	RouteJoint joint = new RouteJoint();
	
	public RouteScheduleGroup() {}
	
	public void setDate(String date)
	{
		this.date = date;
	}
	

	public RouteScheduleGroup(String vesselName, List<ScheduleData> scheduleList)		
	{
		this.vesselName = vesselName;
		
		this.scheduleList = scheduleList;

		fromPorts 	= scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getFromPort));

		toPorts 	= scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getPort));
		
		toPortCount = toPorts.keySet().size();

		fromPortCount = fromPorts.keySet().size();

		List<PortAndDay> fromPortlist = joint.makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE);

		this.date = fromPortlist.get(0).getDate();
	}
	
	public String getDate()
	{
		return date;
	}
	public String getVessel()
	{
		return vesselName;
	}
	
	public String toCompanyString()
	{
		List<String> companyList = this.scheduleList.stream()
				.map(ScheduleData::getCompany_abbr)
				.distinct()
				.sorted()
				.collect(Collectors.toList());
		
		return StringUtils.join(companyList,",");
	}
	public String toFromPortString()
	{
		return StringUtils.join(joint.makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE)," - ");
	}
	public String toToPortString()
	{
		return StringUtils.join(joint.makeDayList(toPorts, ScheduleDateComparator.TO_DATE)," - ");
	}
	public String getVoyage()
	{
		return  (!scheduleList.isEmpty())?scheduleList.get(0).getVoyage_num():"";
	}

	public boolean isRouteScheduleValidation(String strArea)
	{
		return RouteScheduleUtil.checkOutPort(strArea, toPortCount);
	}

}
