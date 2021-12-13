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
 * @���� �ֻ��� ���� ����, �����̸��� �������� ������ �׷�ȭ �ϴ� Ŭ���� 
 * @author ��â��
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
			// ���� �׷� �߰�
			GroupVessel group = this.get(key);
			// Ű : ���ڸ�
			group.addSchedule(data);
			

		}else
		{
			// �ű� �׷� ����
			// Ű :������	+ ���� ��ȣ
			this.put(key, new GroupVessel(data,this.orderByType));
		}
		
	}

	// ���� �̸��� �������� ����
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
			
			
			// ������ �������� �����輱
			
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

