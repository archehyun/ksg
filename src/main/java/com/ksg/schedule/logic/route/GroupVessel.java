package com.ksg.schedule.logic.route;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;


import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.exception.VesselNullException;
import com.ksg.common.util.KSGDateUtil;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.joint.RouteScheduleJoint;

public class GroupVessel extends ArrayList<ScheduleData> implements Comparable<GroupVessel>{
	
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int SORT_BY_LAST	= 1;
	
	public static final int SORT_BY_FIRST 	= 2;
	
	private String voyage_num;
	
	private int intVoyate_num;
	
	private ArrayList<ScheduleData> scheduleList;
	
	private String majorCompany;
	
	private int orderByType;
	
	private GroupPort groupPort;	
	
	ScheduleManager scheduleManager = ScheduleManager.getInstance();
	
	private String vessel_name;
	
	public String getVessel_name() {
		return vessel_name;
	}
	public void setVessel_name(String vessel_name) {
		this.vessel_name = vessel_name;
	}
	public String getVoyage_num() {
		return voyage_num;
	}
	public void setVoyage_num(String voyage_num) {
		this.voyage_num = voyage_num;
	}
	public int getIntVoyate_num() {
		return intVoyate_num;
	}
	public void setIntVoyate_num(int intVoyate_num) {
		this.intVoyate_num = intVoyate_num;
	}
	
	public ArrayList<ScheduleData> getScheduleList() {
		return scheduleList;
	}
	public void setScheduleList(ArrayList<ScheduleData> scheduleList) {
		this.scheduleList = scheduleList;
	}
	public String getMajorCompany() {
		return majorCompany;
	}
	public void setMajorCompany(String majorCompany) {
		this.majorCompany = majorCompany;
	}

	
	public GroupPort getGroupPort()
	{
		return groupPort;
	}
	public GroupVessel()
	{
		this.scheduleList = new ArrayList<ScheduleData>();
		groupPort = new GroupPort();
	}
	public GroupVessel(ScheduleData data,int orderByType) throws SQLException, ParseException, VesselNullException 
	{
		this();
		this.vessel_name = data.getVessel();
		this.voyage_num = data.getVoyage_num();
		this.initMayorVesselName(data);
		this.orderByType = orderByType;
		this.addSchedule(data);
	}
	public GroupVessel(GroupVessel clone) throws SQLException
	{
		this();
		this.vessel_name = clone.getVessel_name();
		this.voyage_num= clone.getVoyage_num();
		this.majorCompany = clone.getMajorCompany();
		this.orderByType = clone.getOrderByType();
	}
	private int getOrderByType() {
		return orderByType;
	}
	public void addSchedule(ScheduleData data) throws ParseException
	{
		this.add(data);		
		groupPort.addPort(data);

	}
	public String toString()
	{
		return "["+this.getVessel_name()+"," +this.getVoyage_num()+"]";
	}

	/**
	 * 대표 선사 설정
	 * 
	 * @param scheduleData
	 * @throws SQLException
	 * @throws VesselNullException
	 */
	private void initMayorVesselName(ScheduleData scheduleData) throws SQLException, VesselNullException
	{
		
		Vessel searchedVessel = scheduleManager.searchVessel(scheduleData.getVessel());
		
		if(searchedVessel.getVessel_company()==null&&searchedVessel.getVessel_company().equals("")) return;
		
		if(searchedVessel.getVessel_company().contains("/"))
		{
			majorCompany=(String) searchedVessel.getVessel_company().subSequence(0, searchedVessel.getVessel_company().indexOf("/"));
		}else
		{
			majorCompany=searchedVessel.getVessel_company();
		}
	}


	public PortScheduleInfo[] getPortArray()
	{	
		return groupPort.toPortArray();
	}
	
	/**
	 * 중복 제거 후 문자 형태로 반환
	 * @param companyList
	 * @return
	 */
	public String getArrangedCompanyList(List<String> companyList) {
		
		List<String> result = companyList.stream().distinct().collect(Collectors.toList());		
		
		Collections.sort(result);	
		
		return StringUtils.join(result, ',');

	}
	
	/**
	 * 
	 * @param major_company 대표 선사
	 * @param companyList 선사 리스트
	 * @return major_company, major_company를 제외한 선사 리스트 
	 */
	public String getMajorCompanyAndCompanyListString(String major_company, List<String> companyList) {

		List<String> newList=companyList.stream().filter(company -> !company.toUpperCase().equals(major_company.toUpperCase()))
				                                 .collect(Collectors.toList());

		return major_company+(!newList.isEmpty()?","+getArrangedCompanyList(newList):"");
	}
	/**
	 * 대표선사가 있을 경우에는 대표선사를 맨 앞으로 위치한 선사 목록 반환
	 * @return 선박목록
	 */
	public String getCompanyString()
	{	
		List<PortScheduleInfo> inPortList =groupPort.getInPortList();
		
		List<String> companyList=inPortList.stream().map(PortScheduleInfo::getCompany).collect(Collectors.toList());

		return majorCompany==null||majorCompany.isEmpty()?getArrangedCompanyList(companyList):getMajorCompanyAndCompanyListString(majorCompany,companyList);
	}	


	public boolean isSeperate()
	{
		return true;
	}
	
	public int compareTo(GroupVessel one) {

		int result;
		switch (orderByType) {
		case RouteScheduleJoint.ORDER_BY_DATE:// 날짜 정렬
			try {
				if(KSGDateUtil.daysDiff(PortDateUtil.parse(this.getFirstInScheduleDate()), PortDateUtil.parse(one.getFirstInScheduleDate()))<=0)
				{
					result= 1;
				}else
				{
					result= -1;
				}

			} catch (ParseException e) {
				e.printStackTrace();
				result= -1;
			}

			break;
		case RouteScheduleJoint.ORDER_BY_VESSEL:// 선박명 정렬
			if(this.vessel_name.compareTo(one.getVessel_name())>0)
			{
				result= 1;
			}
			else if(this.vessel_name.compareTo(one.getVessel_name())==0)
			{
				try{
					// 선박명이 같은 경우 날짜 정렬
					if(KSGDateUtil.daysDiff(PortDateUtil.parse(this.getFirstInScheduleDate()), PortDateUtil.parse(one.getFirstInScheduleDate()))<0)
					{
						result= 1;
					}else
					{
						result= -1;
					}
				} catch (ParseException e) {
					e.printStackTrace();
					result= -1;
				}
			}
			else
			{
				result= -1;
			}
			break;	

		default:
			result=-1;
			break;
		}
		return result;			

	}
	public String getFirstInScheduleDate() {
		return groupPort.getFirstInScheduleDate();
	}

	public String getID()
	{
		return vessel_name+"\r\n"+this.getVoyageInfo();
	}
	private String getVoyageInfo(String voyageString) {

		int result=RouteScheduleUtil.getNumericVoyage(voyageString);
		
		return result!=0? " - "+result:"";
		
	}
	
	public String getVoyageInfo()
	{
		return this.getVoyageInfo(voyage_num);
	}

	public void setOutPortList(PortScheduleInfo[] outPortList) {

		groupPort.setOutPortArray(outPortList);

	}
	public void setInPortList(PortScheduleInfo[] inPortList) {
		groupPort.setInPortArray(inPortList);
	}
	public int getOutPortSize() throws ParseException {
		return groupPort.getOutPortSize();
	}


	public PortScheduleInfo[] getInPortList() throws ParseException {
		return groupPort.createInPortArray();
	}
	public PortScheduleInfo[] getCompressInPortList() throws ParseException {

		return groupPort.createCompressedArray(groupPort.createInPortArray(),SORT_BY_LAST);
	}

	public PortScheduleInfo[] getCompressOutPortList() throws ParseException {

		return groupPort.createCompressedArray(groupPort.createOutPortArray(),SORT_BY_FIRST);
	}

	public PortScheduleInfo[] getInPortList(int startIndex, int endIndex) throws ParseException {

		return groupPort.selectArray(groupPort.createInPortArray(), startIndex,endIndex);

	}

	public PortScheduleInfo[] getInPortList(int startIndex) throws ParseException {

		return groupPort.selectArray(groupPort.createInPortArray(), startIndex);

	}

	public PortScheduleInfo[] getOutPortList() throws ParseException {

		return groupPort.createOutPortArray();
	}

	public PortScheduleInfo[] getOutPortList(int startIndex, int endIndex) throws ParseException {

		return groupPort.selectArray(groupPort.createOutPortArray(), startIndex,endIndex);

	}

	public PortScheduleInfo[] getOutPortList(int startIndex) throws ParseException {

		return groupPort.selectArray(groupPort.createOutPortArray(), startIndex);

	}


}
