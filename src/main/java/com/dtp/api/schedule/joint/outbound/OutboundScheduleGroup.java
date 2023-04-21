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
	
	private String dateF;
	
	private String dateT;
	
	private String jointedDateF;
	
	private Vessel vessel;
	
	private String jointedDateT;
	
	private String jointed_company_abbr;
	
	/* YYYYMMdd */
	private  SimpleDateFormat formatYYYYMMDD = KSGDateUtil.dateFormat5;
	
	public ArrayList<SortedSchedule> scheduleList;
	
	public OutboundScheduleGroup(OutboundScheduleGroup parent, ArrayList<SortedSchedule> scheduleList) throws Exception
	{
		this.vessel = parent.getVessel();
		
		this.scheduleList  = scheduleList;
		
		this.parent = parent;
		
		if(vessel == null) throw new Exception("vessel is null");
	}
	
	public OutboundScheduleGroup(Vessel vessel) throws Exception
	{
		scheduleList = new ArrayList<SortedSchedule>();
		
		this.vessel =vessel;
		
		if(vessel == null) throw new Exception("vessel is null");
	}
	
	
	public String getJointedDateF()
	{
		return jointedDateF;
	}
	
	public String getJointedDateT()
	{
		return jointedDateT;
	}
	
	public String getJointedCompanyName()
	{
		return jointed_company_abbr;
	}
	
	
	public void setParent(OutboundScheduleGroup parent)
	{
		this.parent = parent;
	}
	
	public String getDateF()
	{
		return dateF;
	}
	public String getDateT()
	{
		return dateT;
	}
	public String getForrmatedDate(String date)
	{
		try {
			return KSGDateUtil.dateFormat5.format(KSGDateUtil.inputDateFormat.parse(String.valueOf(date)));
		} catch (ParseException e) {
			return date;
		}
	}
	
	/**
	 *   부산항과 부산항 신항이 같이 있을 경우
	 *   
	 * @return
	 */
	private boolean bothBusanAndBusanNew(List<ScheduleData> originScheduleList)
	{	
		Optional<ScheduleData> busanList= originScheduleList.stream().filter(o -> o.getFromPort().equals("BUSAN")).findFirst();
		
		Optional<ScheduleData> busanNewList= originScheduleList.stream().filter(o -> o.getFromPort().equals("BUSAN NEW")).findFirst();
		
		return busanList.isPresent()&&busanNewList.isPresent();
	}
	
	public void joinnted() 
	{
		ArrayList<ScheduleData> originScheduleList = new ArrayList<ScheduleData>();
		// 출발일 목록
		scheduleList.stream().forEach(o -> originScheduleList.add(o.getData()));
		
		boolean bothBusan = bothBusanAndBusanNew(originScheduleList);		
		
		if(vessel == null) 
			System.out.println();
		String re_company_abbr = vessel.getVessel_company();

		// 출발일 정렬(늦은 날짜)
		// 부산항과 부산항이 같이 존재 할 경우 부산 신항 스케줄로 표시
		Optional<String> lastDateF=originScheduleList.stream()
				.filter(o->bothBusan?o.getFromPort().equals("BUSAN NEW"):true)
				.map( o -> getForrmatedDate(o.getDateF()))
				.sorted(Collections.reverseOrder()) 
				.findFirst();

		// 도착일 정렬(빠른 날짜)
		// 부산항과 부산항이 같이 존재 할 경우 부산 신항 스케줄로 표시
		Optional<String> firstDateT=originScheduleList.stream()
				.filter(o->bothBusan?o.getFromPort().equals("BUSAN NEW"):true)
				.map(o -> getForrmatedDate(o.getDateT()))
				.sorted()
				.findFirst();

		// 선사명 정렬(오름 차순)
		List<String> companyAbbrList=originScheduleList.stream()
											.filter(o -> !re_company_abbr.equals(o))
											.map( data-> (data.getCompany_abbr().equals(data.getAgent())?data.getCompany_abbr():data.getCompany_abbr()+"/"+data.getAgent()))
											.distinct()
											.sorted()
											.collect(Collectors.toList());
		
		dateF = lastDateF.get();
		
		dateT = firstDateT.get();
		
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

	
	public boolean isMulti()
	{
		return scheduleList.size()>1;
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
			int fromDateGap = formatYYYYMMDD.parse(getDateF()).compareTo(formatYYYYMMDD.parse(o.getDateF()));
			
			int toDateGap = formatYYYYMMDD.parse(getDateT()).compareTo(formatYYYYMMDD.parse(o.getDateT()));
			
			int vessel = this.getVesselName().compareTo(o.getVesselName());
			// 정렬 기준
			//출발일 -> 도착일 -> 선박명
			return fromDateGap>0?1:(fromDateGap==0)?(toDateGap>0?1:(toDateGap==0?(vessel>0?1:-1):-1)):-1;
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