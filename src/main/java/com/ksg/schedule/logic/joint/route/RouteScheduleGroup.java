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
//	public String toString()
//	{
//		String strVoyage = (!scheduleList.isEmpty())?scheduleList.get(0).getVoyage_num():"";		
//		
//		String strFromPorts = StringUtils.join(makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE)," - ");
//
//		String strToPorts = 
//
//		return String.format("%s%s - %s (%s)\r\n%s%s\r\n%s%s\r\n\r\n",WORLD_F,vesselName, strVoyage, strCompanys,WORLD_INPORT, strFromPorts,WORLD_OUTPORT, strToPorts);
//	}
	
	public boolean isRouteScheduleValidation(String strArea)
	{
		return RouteScheduleUtil.checkOutPort(strArea, toPortCount);
	}
	
//	/**
//	 * 출발일, 도착일 별 그룹 생성
//	 * @param ports
//	 * @param dateType
//	 * @return
//	 */
//	private List<PortAndDay> makeDayList(Map<String, List<ScheduleData>> ports,  int dateType)
//	{
//		ArrayList<PortAndDay> list = new ArrayList<PortAndDay>();
//
//		ports.entrySet().stream()
//		.forEach(entry -> {
//
//			List<ScheduleData> ll =entry.getValue();
//
//			Collections.sort(ll, new ScheduleDateComparator(dateType));
//
//			ScheduleData lastSchedule = ll.get(ll.size()-1);
//			// 도착항은 빠른 날짜로 정렬
//			list.add(new PortAndDay(entry.getKey(), dateType==ScheduleDateComparator.FROM_DATE?lastSchedule.getDateF():lastSchedule.getDateT()));
//
//		});
//
//		Collections.sort(list, dateComparator);
//
//		return list;
//
//	}


}
