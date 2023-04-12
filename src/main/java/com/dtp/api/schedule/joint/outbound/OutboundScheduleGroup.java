package com.dtp.api.schedule.joint.outbound;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.workbench.schedule.comp.treenode.SortedSchedule;

public class OutboundScheduleGroup implements Comparable<OutboundScheduleGroup>
{
	public OutboundScheduleGroup parent;
	
	private boolean isJointted=false;		
	
	private String dateF;
	private String dateT;
	
	private String jointedDateF;
	
	public String getJointedDateF()
	{
		return jointedDateF;
	}
	private Vessel vessel;
	
	public void setVessel(Vessel vessel)
	{
		this.vessel = vessel;
	}
	
	private String jointedDateT;
	
	public String getJointedDateT()
	{
		return jointedDateT;
	}
	
	public String getJointedCompanyName()
	{
		return jointed_company_abbr;
	}
	
	private String jointed_company_abbr;
	/* YYYYMMdd */
	private  SimpleDateFormat formatYYYYMMDD = KSGDateUtil.dateFormat5;
	
	public ArrayList<SortedSchedule> scheduleList;
	
	public void setParent(OutboundScheduleGroup parent)
	{
		this.parent = parent;
	}
	
	public String getDateF()
	{
		return dateF;
	}
	public String getForrmatedDate(String date)
	{
		try {
			return KSGDateUtil.dateFormat5.format(KSGDateUtil.inputDateFormat.parse(String.valueOf(date)));
		} catch (ParseException e) {
			return date;
		}
	}
	public void joinnted() 
	{
		ArrayList<ScheduleData> list1 = new ArrayList<ScheduleData>();
		// 출발일 목록
		scheduleList.stream().forEach(o -> list1.add(o.getData()));
		
		String re_company_abbr = vessel.getVessel_company();

		// 출발일 정렬(늦은 날짜)
		Optional<String> lastDateF=list1.stream().map( o -> getForrmatedDate(o.getDateF()))
				.sorted(Collections.reverseOrder()) 
				.findFirst();

		// 도착일 정렬(빠른 날짜)
		Optional<String> firstDateT=list1.stream().map(o -> getForrmatedDate(o.getDateT()))
				.sorted()
				.findFirst();

		// 선사명 정렬(오름 차순)
		List<String> companyAbbrList=list1.stream()
											.filter(o -> !re_company_abbr.equals(o))
											.map( data-> (data.getCompany_abbr().equals(data.getAgent())?data.getCompany_abbr():data.getCompany_abbr()+"/"+data.getAgent()))
											.distinct()
											.sorted()
											.collect(Collectors.toList());
		
		dateF = lastDateF.get();
		// 출발일
		this.jointedDateF = KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD(dateF);

		// 도착일
		this.jointedDateT = KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD(firstDateT.get());		

		String sortedCompanyist =StringUtils.join(companyAbbrList.toArray(new String[companyAbbrList.size()]),",");
		
		//TODO 선사명 양식1 (선사명/에이전트)
		
		// 선사명 양식2 (선사명1, 선사명2, ...)
		
		// 선사명 양식2 (대표선사명, 선사명1, 선사명2, ...)
		
		if(scheduleList.size()==1)
		{
			jointed_company_abbr  = scheduleList.get(0).getData().getCompany_abbr()+"/"+scheduleList.get(0).getData().getAgent(); 
		}
		else if(scheduleList.size()>1)
		{
			jointed_company_abbr = re_company_abbr.isEmpty()?sortedCompanyist:re_company_abbr+","+sortedCompanyist;	
		}
		
		
		if(!re_company_abbr.isEmpty())
		{
			System.out.println(String.format("%s %s - %s",this.getVesselName(), sortedCompanyist, jointed_company_abbr));
		}
	}

	public OutboundScheduleGroup(Vessel vessel)
	{
		scheduleList = new ArrayList<SortedSchedule>();
		this.vessel =vessel;
	}
	
	
	public boolean isMulti()
	{
		return scheduleList.size()>1;
	}
	public OutboundScheduleGroup(OutboundScheduleGroup parent, ArrayList<SortedSchedule> scheduleList)
	{
		this.vessel = parent.getVessel();
		
		this.scheduleList  = scheduleList;
		
		this.parent = parent;
		
	}

	public Vessel getVessel() {
		return this.vessel;
	}
	public String getVesselName()
	{
		return vessel.getVessel_name();
	}
	
	public String getVesselType()
	{
		return vessel.getVessel_type();
	}

	public void add(SortedSchedule item)
	{
		this.scheduleList.add(item);
	}

	@Override
	public int compareTo(OutboundScheduleGroup o) {
		try {

			return formatYYYYMMDD.parse(getDateF()).compareTo(formatYYYYMMDD.parse(o.getDateF()))>=0?1:-1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * 
	 * @param scheduleList
	 * @return
	 */
	public String toJointedOutboundScheduleString()	{
		
		String vessel_type = getVesselType();
		
		String formatedVesselType =  (vessel_type.equals("")||vessel_type.equals(" "))?"   ":String.format("   [%s]   ", vessel_type);   
		
		return String.format("%-8s%-15s%s(%s)   %s", this.jointedDateF,getVesselName(),formatedVesselType, jointed_company_abbr, jointedDateT);

//		return String.format("%s %s%s(%s) %s " , this.jointedDateF, getVesselName(), formatedVesselType , jointed_company_abbr, jointedDateT);
	}

	public boolean isDateValidate() {
		
		try {
			return (KSGDateUtil.outputDateFormat.parse(getJointedDateF()).compareTo(KSGDateUtil.outputDateFormat.parse(getJointedDateT()))>0);
		} catch (ParseException e) {
			
			e.printStackTrace();
			return false;
		}
	}
	public String toString()
	{
		return  toJointedOutboundScheduleString();
	}

	
}