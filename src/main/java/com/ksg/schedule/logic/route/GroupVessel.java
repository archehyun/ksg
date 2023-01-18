package com.ksg.schedule.logic.route;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.exception.VesselNullException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.StringCompare;
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
	private static final String AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC = "AUSTRALIA, NEW ZEALAND & SOUTH PACIFIC";
	private static final String PERSIAN_GULF = "PERSIAN GULF";
	private static final String RUSSIA = "RUSSIA";
	private static final String ASIA = "ASIA";
	private static final String JAPAN = "JAPAN";
	private static final String CHINA = "CHINA";
	
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

	public GroupVessel(ScheduleData data,int orderByType) throws SQLException, ParseException, VesselNullException 
	{
		this.vessel_name = data.getVessel();
		this.voyage_num = data.getVoyage_num();
		this.scheduleList = new ArrayList<ScheduleData>();
		this.initMayorVesselName(data);
		this.orderByType = orderByType;
		this.groupPort = new GroupPort();
		this.addSchedule(data);

	}
	public GroupVessel(GroupVessel clone) throws SQLException
	{
		this.vessel_name = clone.getVessel_name();
		this.voyage_num= clone.getVoyage_num();
		this.majorCompany = clone.getMajorCompany();
		this.orderByType = clone.getOrderByType();
		groupPort = new GroupPort();
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

	private void initMayorVesselName(ScheduleData data) throws SQLException, VesselNullException
	{
		
		Vessel searchedVessel = scheduleManager.searchVessel(data.getVessel());
		if(searchedVessel.getVessel_company()!=null&&!searchedVessel.getVessel_company().equals(""))
		{
			if(searchedVessel.getVessel_company().contains("/"))
			{
				majorCompany=(String) searchedVessel.getVessel_company().subSequence(0, searchedVessel.getVessel_company().indexOf("/"));
			}else
			{
				majorCompany=searchedVessel.getVessel_company();
			}
		}
	}


	public PortScheduleInfo[] getPortArray()
	{	
		return groupPort.toPortArray();
	}
	private String arrangedCompanyList(ArrayList<String> companyList) {
		Vector<String> temp = new Vector<String>();
		for(int i=0;i<companyList.size();i++)
		{
			boolean flag=true;
			for(int j=0;j<temp.size();j++)
			{
				if(temp.get(j).equals(companyList.get(i)))
				{
					flag=false;
				}
			}
			if(flag)
				temp.add(companyList.get(i));
		}

		String[] array2=new String[temp.size()];

		for(int i=0;i<temp.size();i++)
		{
			array2[i]=temp.get(i);
		}

		companyList.toArray(array2);
		//알파벳 정렬
		Arrays.sort(array2, new StringCompare());


		String companyStringList ="";
		for(int i=0;i<array2.length;i++)
		{
			companyStringList+=array2[i]+(i<array2.length-1?",":"");

		}
	
		
		return companyStringList;

	}
	/**
	 * @param major_company
	 * @param companyList
	 * @return
	 */
	private String arrangedCompanyLists(String major_company,
			ArrayList<String> companyList) {

		ArrayList<String> new_company = new ArrayList<String>();
		for(int i=0;i<companyList.size();i++)
		{
			if(major_company.compareToIgnoreCase(companyList.get(i))!=0)
			{
				new_company.add(companyList.get(i));
			}
		}
		
		if(new_company.size()>0)
		{
			
			return major_company+","+arrangedCompanyList(new_company);
		}else
		{
			return major_company;
		}
	}
	public String getCompany()
	{
		String company;

		ArrayList<String> companyList = new ArrayList<String>();

		PortScheduleInfo[] inPortList = groupPort.createInPortArray();

		for(int i=0;i<inPortList.length;i++)
		{
			companyList.add(inPortList[i].getCompany());
		}


		if(majorCompany==null)
		{
			company =arrangedCompanyList(companyList);
		}
		else
		{
			company=arrangedCompanyLists(majorCompany,companyList);
		}

		return company;
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
	private String getVoyageInfo(String vessel) {

		int result=getNumericVoyage(vessel);
		if(result!=0)
		{
			return " - "+result;
		}else
		{
			return "";
		}
	}
	private int getNumericVoyage(String voyage_num)
	{

		int result=0;

		String temp="";
		if(voyage_num==null)
			return 0;
		for(int i=0;i<voyage_num.length();i++)
		{
			try{
				temp+=Integer.parseInt(String.valueOf(voyage_num.charAt(i)));
			}catch(NumberFormatException e)
			{
				//				return 0;
			}
		}
		try{
			result=Integer.valueOf(temp);
		}catch(Exception e)
		{
			return 0;
		}

		return result;
	}
	public String getVoyageInfo()
	{
		return this.getVoyageInfo(voyage_num);
	}
	/**
	 * @설명 국내항 기준 공동배선 여부 결정  
	 *      중국, 일본, 러시아: 4일 
	 *      동남아: 6일
	 *      구주, 북미, 중남미, 아프리카 : 8일
	 *      기타 : 10일
	 *      
	 * @param area_name
	 * @return
	 * @throws ParseException
	 */
	public int getSeperateIndex(String area_name) throws ParseException
	{

		int index=-1;

		PortScheduleInfo[] sortedInPortList = groupPort.createInPortArray();
		if(sortedInPortList.length<2)
			return index;

		int base=0; // 공동배선 기준 일자
		String upCaseAreaName = area_name.toUpperCase();
		if(upCaseAreaName.equals(CHINA)||
				upCaseAreaName.equals(JAPAN)||
				upCaseAreaName.equals(RUSSIA)) // 중국, 일본, 러시아
		{
			base=4;
		}else if(upCaseAreaName.equals(ASIA)) // 동남아
		{
			base=6;

		}else if(upCaseAreaName.equals(PERSIAN_GULF)||
				upCaseAreaName.equals(AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC))// 중동, 호주
		{
			base=8;
		}		
		else // 구주, 북미, 중남미, 아프리카
		{
			base=10;
		}

		PortScheduleInfo firstInPort=sortedInPortList[0];
		
		for(int i=1;i<sortedInPortList.length;i++)
		{
			int differ = KSGDateUtil.daysDiff(PortDateUtil.parse(firstInPort.getDate()), PortDateUtil.parse(sortedInPortList[i].getDate()));
			if(differ>=base)
				return i;
		}
		return index;
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
