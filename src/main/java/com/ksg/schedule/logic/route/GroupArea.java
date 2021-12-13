package com.ksg.schedule.logic.route;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ksg.common.exception.VesselNullException;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;

 /**
 * @설명 최상위 정렬 기준, 지역이름을 기준으로 선박을 그룹화 하는 클래스 
 * @author 박창현
 *
 */
public class GroupArea extends HashMap<String, GroupVessel> implements Comparable
{
	/**
	 * 
	 */
	protected Logger 			logger = Logger.getLogger(getClass());
	private ShippersTable op;
	private static final long serialVersionUID = 1L;
	private String area_name;
	private int orderByType;
	ArrayList<GroupVessel> commonVesselList;
	public GroupArea(ScheduleData data,ShippersTable op, int orderByType) throws SQLException, ParseException, VesselNullException {
		this.area_name = data.getArea_name().toUpperCase();
		this.op = op;
		this.orderByType = orderByType; 
		commonVesselList = new ArrayList<GroupVessel>();
		logger.info("new area:"+this.area_name);
		addSchedule(data);
	}
	public GroupArea(String area_name,ShippersTable op, int orderByType) throws SQLException, ParseException {
		this.area_name = area_name.toUpperCase();
		this.op = op;
		this.orderByType = orderByType; 
		commonVesselList = new ArrayList<GroupVessel>();
		logger.info("new area:"+this.area_name);
	}

	public void addSchedule(ScheduleData data) throws SQLException, ParseException, VesselNullException 
	{

		String key =data.getVessel()+"\r\n"+data.getIntVoyage_num();
		if(this.containsKey(key))
		{
			// 기존 그룹 추가
			GroupVessel group = this.get(key);
			// 키 : 선박명
			group.addSchedule(data);
			

		}else
		{
			// 신규 그룹 생성
			// 키 :선막명	+ 항차 번호
			this.put(key, new GroupVessel(data,this.orderByType));
		}
		
	}

	// 지역 이름을 기준으로 정렬
	public int compareTo(Object arg0) {
		GroupArea one = (GroupArea) arg0;
		if(this.area_name.compareTo(one.area_name)>0)
		{
			return 1;
		}else
		{
			return -1;
		}
	}
	public ArrayList<GroupVessel> getCommonVessel()
	{
		return commonVesselList;
	}

	public String getArea_name() {
		return area_name;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}
	
	public GroupVessel[] toSortedArray() throws ParseException, SQLException
	{
		Set<String>keylist=keySet();
		ArrayList<GroupVessel> newList = new ArrayList<GroupVessel>();

		Iterator<String> iter=keylist.iterator();
		while(iter.hasNext())	
		{
			String key =iter.next();
			GroupVessel item  = this.get(key);
			
			
			// 국내항 기준으로 공동배선
			
			int index=-1;
			
			if((index=item.getSeperateIndex(this.getArea_name()))>-1)
			{
				
				this.commonVesselList.add(item);
				GroupVessel newGroup1 = new GroupVessel(item);
				GroupVessel newGroup2 = new GroupVessel(item);
				
				newGroup1.setInPortList(item.getInPortList(0, index));
				newGroup2.setInPortList(item.getInPortList(index));
				newGroup1.setOutPortList(item.getOutPortList(0, index));
				newGroup2.setOutPortList(item.getOutPortList(index));				

				newList.add(newGroup1);
				newList.add(newGroup2);
			}
			else
			{
				newList.add(item);
			}
		}
		GroupVessel lit[] = new GroupVessel[newList.size()];
		lit = newList.toArray(lit);
		Arrays.sort(lit);

		return lit;
	}
	
	
}

