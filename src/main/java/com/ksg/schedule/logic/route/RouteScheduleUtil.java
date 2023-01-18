package com.ksg.schedule.logic.route;

import java.text.ParseException;

import com.ksg.common.util.KSGDateUtil;

public class RouteScheduleUtil {
	
	private static final String AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC = "AUSTRALIA, NEW ZEALAND & SOUTH PACIFIC";
	private static final String PERSIAN_GULF = "PERSIAN GULF";
	private static final String RUSSIA = "RUSSIA";
	private static final String ASIA = "ASIA";
	private static final String JAPAN = "JAPAN";
	private static final String CHINA = "CHINA";
	
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
	
	public static int getGap(String area_name)
	{
		int base=0; // �����輱 ���� ����
		String upCaseAreaName = area_name.toUpperCase();
		if(upCaseAreaName.equals(CHINA)||
				upCaseAreaName.equals(JAPAN)||
				upCaseAreaName.equals(RUSSIA)) // �߱�, �Ϻ�, ���þ�
		{
			base=4;
		}else if(upCaseAreaName.equals(ASIA)) // ������
		{
			base=6;

		}else if(upCaseAreaName.equals(PERSIAN_GULF)||
				upCaseAreaName.equals(AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC))// �ߵ�, ȣ��
		{
			base=8;
		}		
		else // ����, �Ϲ�, �߳���, ������ī
		{
			base=10;
		}
		return base;
	}
	public static int getNumericVoyage(String voyage_num)
	{	
		try{
			return Integer.valueOf(voyage_num.replaceAll("[^0-9]", ""));
		}catch(Exception e)
		{
			return 0;
		}
	}

}
