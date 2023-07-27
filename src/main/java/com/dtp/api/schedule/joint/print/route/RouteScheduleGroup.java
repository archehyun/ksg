package com.dtp.api.schedule.joint.print.route;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.schedule.comparator.IFComparator;
import com.dtp.api.schedule.joint.print.ScheduleGroup;
import com.dtp.api.schedule.joint.tree.comparator.ScheduleDateComparator;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.route.RouteScheduleUtil;
/**
 * 

  * @FileName : RouteScheduleGroup.java

  * @Project : KSG2

  * @Date : 2023. 2. 13. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� :
 */
public class RouteScheduleGroup extends ScheduleGroup implements IFComparator{
	
	private String date;	
	
	private Map<String, List<ScheduleData>> fromPorts;
	
	private Map<String, List<ScheduleData>> toPorts;
	
	private int toPortCount;
	
	private int fromPortCount;
	
	private RouteJoint joint = new RouteJoint();
	
	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd");
	
	private List<PortAndDay> inboundPort;
	
	private List<PortAndDay> outboundPort;
	
	public RouteScheduleGroup() {}
	
	public void setDate(String date)
	{
		this.date = date;
	}
	

	public RouteScheduleGroup(Vessel vessel, List<ScheduleData> scheduleList)		
	{
		super(vessel, scheduleList);

		fromPorts 	= scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getFromPort));
		
		fromPortCount = fromPorts.keySet().size();
		
		
		List<PortAndDay> fromPortlist = joint.makeDayList(fromPorts, new ScheduleDateComparator(ScheduleDateComparator.FROM_DATE), ScheduleDateComparator.FROM_DATE);
		
		this.date = fromPortlist.get(0).getDateF();
		
		// ù��° ����� ���� ���� �ױ� ����
		toPorts 	= scheduleList.stream()
									.filter(item -> compareDate(item.getDateT(), date))
									.collect(Collectors.groupingBy(ScheduleData::getPort));
		
		toPortCount = toPorts.keySet().size();
		
		inboundPort = joint.makeDayList(fromPorts,new ScheduleDateComparator(ScheduleDateComparator.FROM_DATE), ScheduleDateComparator.FROM_DATE);
		
		outboundPort = joint.makeDayList(toPorts, new ScheduleDateComparator(ScheduleDateComparator.TO_DATE).reversed(), ScheduleDateComparator.TO_DATE);
		
	}
	
	private boolean compareDate(String one, String two)
	{
		try {
			return (dateformat.parse(one).compareTo(dateformat.parse(two)))>0;
		} catch (ParseException e) {
			return false;
		}
	}
	
	public String getDateF()
	{
		return date;
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
	 * ������ ���� ����
	 * 1. �ܱ��� ��
	 * 2. �ܱ��� ù��° �������� ������ ������ ����� ���� ���� ���
	 * @param strArea
	 * @return
	 */
	public boolean isRouteScheduleValidation(String strArea)
	{	
		return RouteScheduleUtil.checkOutPort(strArea, toPortCount)&&isDateValidate();
	}
	/**
	 * �ܱ��� ù��° �������� ������ ������ ����� �� 
	 * @return
	 */
	private boolean isDateValidate()
	{
		if(inboundPort.size()==0||outboundPort.size()==0) return false;
		
		// ������ ������
		PortAndDay inboundLastPortAndDate=inboundPort.get(inboundPort.size()-1);
		
		// �ܱ��� ù��°		
		PortAndDay outboundFirstPortAndDate=outboundPort.get(0);
		
		return KSGDateUtil.dayDiff(inboundLastPortAndDate.getDateF(), outboundFirstPortAndDate.getDateF())>0;
	}

	@Override
	public int compareTo(ScheduleGroup o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getVesselName() {
		return vessel.getVessel_name();
	}

}
