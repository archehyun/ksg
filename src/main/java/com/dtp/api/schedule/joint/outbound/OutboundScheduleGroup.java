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

import com.ksg.workbench.schedule.comp.treenode.SortedSchedule;

public class OutboundScheduleGroup implements Comparable<OutboundScheduleGroup>
{
	
	// yyyy/MM/dd
	private static SimpleDateFormat inputDateFormat = KSGDateUtil.inputDateFormat;
		//	private static SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMdd");
		
		// yyyyMMdd
	private static SimpleDateFormat outputDateFormat =KSGDateUtil.dateFormat5;

	public OutboundScheduleGroup parent;
	
	private boolean isJointted=false;		
	
	private String jointedDateF;
	
	public String getJointedDateF()
	{
		return jointedDateF;
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
	
	private  SimpleDateFormat formatYYYYMMDD = KSGDateUtil.inputDateFormat;
	
	public ArrayList<SortedSchedule> scheduleList;
	
	private String vessel;
	
	private String vessel_type;
	
	public void setVessel_type(String vessel_type)
	
	{
		this.vessel_type =vessel_type; 
	}
	
	public void setParent(OutboundScheduleGroup parent)
	{
		this.parent = parent;
	}
	
	public String getDateF()
	{
		return isJointted?jointedDateF:scheduleList.get(0).getData().getDateF();
	}
	public String getForrmatedDate(String date)
	{
		try {
			return outputDateFormat.format(inputDateFormat.parse(String.valueOf(date)));
		} catch (ParseException e) {
			return date;
		}
	}
	public void joinnted()
	{
		ArrayList<ScheduleData> list1 = new ArrayList<ScheduleData>();
		// ����� ���
		scheduleList.stream().forEach(o -> list1.add(o.getData()));

		// ����� ����(���� ��¥)
		Optional<String> lastDateF=list1.stream().map( o -> getForrmatedDate(o.getDateF()))
				.sorted(Collections.reverseOrder()) 
				.findFirst();

		// ������ ����(���� ��¥)
		Optional<String> firstDateT=list1.stream().map(o -> getForrmatedDate(o.getDateT()))
				.sorted()
				.findFirst();

		// ����� ����(���� ����)
		List<String> companyAbbrList=list1.stream().map(ScheduleData::getCompany_abbr).distinct()
				.sorted()
				.collect(Collectors.toList());
		
		// �����
		this.jointedDateF=KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD(lastDateF.get());

		// ������
		this.jointedDateT = KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD(firstDateT.get());		

		// TODO ������ - ��ǥ ����� ����
		String re_company_abbr = "";

		// ����� ��� (�����1, �����2, ...)
		jointed_company_abbr = StringUtils.join(companyAbbrList.toArray(new String[companyAbbrList.size()]),",");
	}

	public OutboundScheduleGroup()
	{
		scheduleList = new ArrayList<SortedSchedule>();
	}
	
	
	public boolean isMulti()
	{
		return scheduleList.size()>1;
	}
	public OutboundScheduleGroup(OutboundScheduleGroup parent, ArrayList scheduleList)
	{
		this.vessel = parent.getVesselName();
		
		this.vessel_type =parent.getVesel_type();
		
		this.scheduleList  = scheduleList;
		
		this.parent = parent;
		
	}
	

	public String getVesel_type() {
		// TODO Auto-generated method stub
		return vessel_type;
	}

	public void setVesselName(String vessel) {
		this.vessel=  vessel;

	}
	public String getVesselName()
	{
		return vessel;
	}

	public void add(SortedSchedule item)
	{
		this.scheduleList.add(item);
	}

	@Override
	public int compareTo(OutboundScheduleGroup o) {
		try {

			return formatYYYYMMDD.parse(getDateF()).compareTo(formatYYYYMMDD.parse(o.getDateF()));
		} catch (ParseException e) {
			return 0;
		}
	}
	
	/**
	 * 
	 * @param scheduleList
	 * @return
	 */
	public String toJointedOutboundScheduleString()	{
		
		String formatedVesselType =  (vessel_type.equals("")||vessel_type.equals(" "))?"   ":String.format("   [%s]   ", vessel_type);   

		return String.format("%s %s%s(%s) %s " , this.jointedDateF, getVesselName(), formatedVesselType , jointed_company_abbr, jointedDateT);
	}

	
}