package com.ksg.schedule.logic.route;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.exception.VesselNullException;
import com.ksg.domain.AreaEnum;
import com.ksg.domain.ScheduleData;

import lombok.extern.slf4j.Slf4j;

 /**
 * @���� �ֻ��� ���� ����, �����̸��� �������� ������ �׷�ȭ �ϴ� Ŭ���� 
 * @author ��â��
 *
 */
@SuppressWarnings("serial")
@Slf4j
public class GroupArea extends HashMap<String, GroupVessel> implements Comparable
{
	/**
	 * 
	 */
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");
	
	private String area_name;
	
	private int orderByType;
	
	private ArrayList<GroupVessel> commonVesselList;
	
	public GroupArea(ScheduleData data, int orderByType) throws Exception {
		
		this(data.getArea_name().toUpperCase(),orderByType);
		
		addSchedule(data);
	}
	public GroupArea(String area_name, int orderByType) throws Exception {
		
		this.area_name = area_name.toUpperCase();
		
		this.orderByType = orderByType; 
		
		commonVesselList = new ArrayList<GroupVessel>();
		
		logger.debug("new area:"+this.area_name);
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
	/**
	 * ������ �����輱 ����
	 * @deprecated
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	public GroupVessel[] toSortedArray_temp() throws ParseException, SQLException
	{
	
		
		ArrayList<GroupVessel> newList = new ArrayList<GroupVessel>();	
		
		for(String key:this.keySet())
		{
			GroupVessel item  = this.get(key);
			// ������ �������� �����輱
			
			int index=-1;
			
			if((index=RouteScheduleUtil.getSeperateIndex(this.getArea_name(), item.getGroupPort().createInPortArray()))>-1)
			{
				logger.debug("sperate====="+item.getVessel_name());
				
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
	
	/**
	 * ������ �����輱 ����
	 * 
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	public GroupVessel[] toSortedArray() throws ParseException, SQLException
	{	
		ArrayList<GroupVessel> newList = new ArrayList<GroupVessel>();	
		
		for(String key:this.keySet())
		{
			GroupVessel item  = this.get(key);
			// ������ �������� �����輱
			
			int index=-1;
			
			if((index=RouteScheduleUtil.getSeperateIndex(this.getArea_name(), item.getGroupPort().createInPortArray()))>-1)
			{
				
				//item.getGroupPort().getInPortList().stream().forEach(o-> logger.debug(o.getPort()+","+o.getDate()));
				
				
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
	private List[] divideScheduleByArea(List<ScheduleData> list, String area_name)
	{		

		// ù��° ����� �����
		String firstInPortDateF = list.get(0).getDateF();

		int index =-1;
		
		
		for(int i=1;i< list.size();i++)
		{
			ScheduleData secondOutPortData = list.get(i);
			String outPortDateF = secondOutPortData.getDateF();
			
			int differ = differDay(firstInPortDateF, outPortDateF);
			
			int gap = RouteScheduleUtil. getGap(area_name);
			
			AreaEnum area = AreaEnum.findGapByAreaName(area_name);
			
			if(differ>area.getGap())
			{	
				List<ScheduleData> first = new ArrayList<>(list.subList(0, i));
				
			    List<ScheduleData> second = new ArrayList<>(list.subList(i, list.size()));
			    
			    return new List[] {first, second};
			}
		}
		return new List[] {list};
	}
	
	/**
	 * �� ��¥�� ����
	 * @param firstDate yyyy/MM/dd
	 * @param secondDate yyyy/MM/dd
	 * @return 
	 */
	private int differDay(String firstDate, String secondDate)
	{
		try {

			long diffInMillies = Math.abs(formatYYYYMMDD.parse(firstDate).getTime() - formatYYYYMMDD.parse(secondDate).getTime());
			return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		}catch(Exception e)
		{
			return 0;
		}

	}
	
	
	
}

