package com.dtp.api.schedule.joint.print.route;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.schedule.comparator.IFComparator;
import com.dtp.api.schedule.joint.tree.node.ScheduleDateComparator;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.route.RouteScheduleUtil;
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
	
	SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
	
	List<PortAndDay> inboundPort;
	
	List<PortAndDay> outboundPort;
	
	
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
		
		fromPortCount = fromPorts.keySet().size();
		
		
		List<PortAndDay> fromPortlist = joint.makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE);
		
		this.date = fromPortlist.get(0).getDate();
		
		// 첫번째 출발일 보다 늦은 항구 제외
		toPorts 	= scheduleList.stream()
									.filter(item -> compareDate(item.getDateT(), date))
									.collect(Collectors.groupingBy(ScheduleData::getPort));
		
		toPortCount = toPorts.keySet().size();
		
		inboundPort = joint.makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE);
		
		outboundPort = joint.makeDayList(toPorts, ScheduleDateComparator.TO_DATE);
		
	}
	
	private boolean compareDate(String one, String two)
	{
		try {
			return (dateformat.parse(one).compareTo(dateformat.parse(two)))>0;
		} catch (ParseException e) {
			return false;
		}
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
		return StringUtils.join(inboundPort," - ");
	}
	public String toToPortString()
	{
		return StringUtils.join(outboundPort," - ");
	}
	public String getVoyage()
	{
		return  (!scheduleList.isEmpty())?scheduleList.get(0).getVoyage_num():"";
	}
	
	/**
	 * 스케줄 제외 조건
	 * 1. 외국항 수
	 * 2. 외국항 첫번째 도착일이 국내항 마지막 출발일 보다 늦은 경우
	 * @param strArea
	 * @return
	 */
	public boolean isRouteScheduleValidation(String strArea)
	{	
		return RouteScheduleUtil.checkOutPort(strArea, toPortCount)&&isDateValidate();
	}
	/**
	 * 외국항 첫번째 도착일이 국내항 마지막 출발일 비교 
	 * @return
	 */
	private boolean isDateValidate()
	{
		if(inboundPort.size()==0||outboundPort.size()==0) return false;
		
		// 국내항 마지막
		PortAndDay inboundLastPortAndDate=inboundPort.get(inboundPort.size()-1);
		
		// 외국항 첫번째		
		PortAndDay outboundFirstPortAndDate=outboundPort.get(0);
		
		return KSGDateUtil.dayDiff(inboundLastPortAndDate.getDate(), outboundFirstPortAndDate.getDate())>0;
	}

}
