package com.ksg.schedule.logic.route;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.AreaEnum;
import com.ksg.domain.ScheduleData;

public class RouteScheduleUtil {
	
	private static final String AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC = "AUSTRALIA, NEW ZEALAND & SOUTH PACIFIC";
	private static final String PERSIAN_GULF = "PERSIAN GULF";
	private static final String RUSSIA = "RUSSIA";
	private static final String ASIA = "ASIA";
	private static final String JAPAN = "JAPAN";
	private static final String CHINA = "CHINA";
	
	private static SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");
	
	/**
	 * @���� ������ ���� �����輱 ���� ����  
	 *      �߱�, �Ϻ�, ���þ�: 4�� 
	 *      ������: 6��
	 *      ����, �Ϲ�, �߳���, ������ī : 8��
	 *      ��Ÿ : 10��
	 *      
	 * @param area_name
	 * @return
	 * @throws ParseException
	 */
	public static int getSeperateIndex(String area_name, PortScheduleInfo[] sortedInPortList) throws ParseException
	{

		int index=-1;

		//PortScheduleInfo[] sortedInPortList = groupPort.createInPortArray();
		
		if(sortedInPortList.length<2)
			return index;
		
		
		int base=getGap(area_name.toUpperCase()); // �����輱 ���� ����
		

		PortScheduleInfo firstInPort=sortedInPortList[0];
		
		for(int i=1;i<sortedInPortList.length;i++)
		{
			int differ = KSGDateUtil.daysDiff(PortDateUtil.parse(firstInPort.getDate()), PortDateUtil.parse(sortedInPortList[i].getDate()));
			if(differ>=base)
				return i;
		}
		return index;
	}
	
	
	/**
	 * 
	 * ����
	 * �߱�, �Ϻ�, ���þ� : 4��(D+3)
	 * ������ : 6��(D+5)
	 * �ߵ�, ȣ�� : 8��(D+7)
	 * ��Ÿ : 10��(D+9)
	 * @param area_name
	 * @return
	 */
	public static int getGap(String area_name)
	{
		int base=0; // �����輱 ���� ����
		
		String upCaseAreaName = area_name.toUpperCase();
		
		if(upCaseAreaName.equals(CHINA)||
				upCaseAreaName.equals(JAPAN)||
				upCaseAreaName.equals(RUSSIA)) // �߱�, �Ϻ�, ���þ�
		{
			//4��(D+3)
			base=4;
		}else if(upCaseAreaName.equals(ASIA)) // ������
		{
			//6��(D+5)
			base=6;

		}else if(upCaseAreaName.equals(PERSIAN_GULF)||
				upCaseAreaName.equals(AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC))// �ߵ�, ȣ��
		{
			//8��(D+7)
			base=7;
		}		
		else // ����, �Ϲ�, �߳���, ������ī
		{
			//10��(D+9)
			base=10;
		}
		return base;
	}

	
	/**
	 * �߱�, �Ϻ� : 2�� �̻�
	 * ���þ� : 1�� �̻�
	 * ��Ÿ : 3�� �̻�
	 * ���� ������ ǥ�� Ȯ��
	 * @param areaName
	 * @param outportCount
	 * @return
	 */
	public static boolean checkOutPort(String areaName,int outportCount)
	{
		if(areaName.equals(AreaEnum.CHINA.toUpperCase())||areaName.equals(AreaEnum.JAPAN.toUpperCase()))
		{
			return outportCount>=2;
		}
		// ���þ�
		else if(areaName.equals(AreaEnum.RUSSIA.toUpperCase()))
		{
			return outportCount>0;
		}					
		// ��Ÿ ����
		else
		{
			return outportCount>=3;
		}

	}
	
	/**
	 *  ������ ���� ������ �и�
	 *  
	 * @param list
	 * @param area_name
	 * @return
	 */
	public static List[] divideScheduleByArea(List<ScheduleData> list, String area_name)
	{		

		// ù��° ����� �����
		
		String firstInPortDateF = list.get(0).getDateF();
		
		// ������ ù�糯 , �ܱ��� ù°�� ��
		
		for(int i=1;i< list.size();i++)
		{
			ScheduleData secondOutPortData = list.get(i);
			
			String outPortDateF = secondOutPortData.getDateF();
			
			int differ = differDay(firstInPortDateF, outPortDateF);
			
			AreaEnum area = AreaEnum.findGapByAreaName(area_name.toUpperCase());
			   
			if(differ>=(area.getGap()-1))
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
	private static int differDay(String firstDate, String secondDate)
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
