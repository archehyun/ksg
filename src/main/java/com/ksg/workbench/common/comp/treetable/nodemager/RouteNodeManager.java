package com.ksg.workbench.common.comp.treetable.nodemager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang3.StringUtils;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.AreaEnum;
import com.ksg.domain.ScheduleData;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.NodeType;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;
import com.ksg.workbench.common.comp.treetable.node.ScheduleDateComparator;

import lombok.extern.slf4j.Slf4j;
/**
 * 

 * @FileName : RouteNodeManager.java

 * @Project : KSG2

 * @Date : 2022. 12. 20. 

 * @�ۼ��� : pch

 * @�����̷� :

 * @���α׷� ���� : ���Ʈ ������ Ʈ�� ��� ����
 * 
 * ���� ���� 1, ����, �����
 */
@Slf4j
public class RouteNodeManager extends AbstractNodeManager{


	PortAndDayDateComparator dateComparator = new PortAndDayDateComparator();

	FromDateComparator fromDateComparator 	= new FromDateComparator();

	VesselComparator vesselComparator 		= new VesselComparator();

	SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");


	private static final String AUSTRALIA_NEW_ZEALAND_SOUTH_PACIFIC = "AUSTRALIA, NEW ZEALAND & SOUTH PACIFIC";
	private static final String PERSIAN_GULF = "PERSIAN GULF";
	private static final String RUSSIA = "RUSSIA";
	private static final String ASIA = "ASIA";
	private static final String JAPAN = "JAPAN";
	private static final String CHINA = "CHINA";



	public RouteNodeManager()
	{
		super();
	}

	/**
	 * TODO ������ ���� ��¥�� ����
	 * TODO
	 */
	/**
	 * ���� - ����
	 * @param param
	 * @return
	 */
	public DefaultMutableTreeNode getTreeNode(CommandMap param) {


		DefaultMutableTreeNode root = new DefaultMutableTreeNode("area");

		CommandMap areaList=(CommandMap) param.get("data");

		String sortType = (String) param.get("sortType");


		Set<String> areaKeySet=areaList.keySet();

		areaKeySet.stream().sorted();

		Object[] mapkey = areaList.keySet().toArray();
		Arrays.sort(mapkey);

		// ����
		for(Object strArea:mapkey)
		{
			HashMap<String, Object> vesselList =  (HashMap<String, Object>) areaList.get(strArea);
			// ����
			Object[] vesselArray = vesselList.keySet().toArray();


			DefaultMutableTreeNode area = new AreaTreeNode((String) strArea);

			//Arrays.sort(vesselArray);

			List<OutbondScheduleTreeNode> areaScheduleList = new ArrayList<OutbondScheduleTreeNode>();

			for (Object vesselKey : vesselArray)
			{	
				// �����ȸ
				List<ScheduleData> scheduleList = (List<ScheduleData>) vesselList.get(vesselKey);

				// ������ȣ(����)�� �׷�ȭ
				Map<Integer, List<ScheduleData>> testList =  scheduleList.stream().collect(
						Collectors.groupingBy(o -> getNumericVoyage(o.getVoyage_num()) ));// ����

				// ���� ��ȣ�� ����
				Object[] voyageArray = testList.keySet().toArray();

				Arrays.sort(voyageArray);

				for(Object voyagekey:voyageArray)
				{
					List<ScheduleData> subscheduleList = testList.get(voyagekey);

					//TODO ������ ������ �Ⱓ�� ������ ����


					Collections.sort(subscheduleList);					

					List[] li=divideScheduleByArea(subscheduleList, (String)strArea);
					for(List item:li)
					{
						areaScheduleList.add((OutbondScheduleTreeNode) makeScheduleNode((String) strArea, (String) vesselKey, voyagekey, item));	
					}					
				}	

			}

			// ����� �������� ����
			Collections.sort(areaScheduleList, sortType.equals("date")?fromDateComparator:vesselComparator);			

			areaScheduleList.forEach(o ->area.add(o));

			root.add(area);

		}

		return root;
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
			
			int gap = getGap(area_name);
			
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
	private DefaultMutableTreeNode makeScheduleNode(String strArea, String vesselKey, Object voyagekey, List<ScheduleData> scheduleList) {


		Map<String, List<ScheduleData>> fromPorts 	= scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getFromPort));

		Map<String, List<ScheduleData>> toPorts 	= scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getPort));

		int toPortCount = toPorts.keySet().size();

		int fromPortCount = fromPorts.keySet().size();


		//TODO ������ - Route ������ �븮���̼�

		log.info("area:{}, fromPortCount:{}, {}",strArea, fromPortCount, checkOutPort(strArea, toPortCount));

		// TODO ������-���� ��ȣ ǥ��
		OutbondScheduleTreeNode schedule = new OutbondScheduleTreeNode(vesselKey+", "+ voyagekey, (checkOutPort(strArea, toPortCount)?NodeType.SCHEDULE:NodeType.JOINT_SCHEDULE)  );

		DefaultMutableTreeNode toPort =  new PortTreeNode( StringUtils.join(makeDayList(toPorts, ScheduleDateComparator.TO_DATE)," - "));				

		scheduleList.stream().forEach(o -> toPort.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(o, CommandMap.class)))));


		List<PortAndDay> fromPortlist = makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE);
		// ����� ���
		schedule.add(new PortTreeNode( StringUtils.join( fromPortlist," - ")));

		// ������� ���� ��¥
		schedule.date = fromPortlist.get(0).date;

		schedule.vessel = vesselKey;


		// ������ ���
		schedule.add(toPort);

		// TODO ����� ���� ��¥ �������� ����

		return schedule;




	}

	/**
	 * �����, ������ �� �׷� ����
	 * @param ports
	 * @param dateType
	 * @return
	 */
	private List<PortAndDay> makeDayList(Map<String, List<ScheduleData>> ports,  int dateType)
	{
		ArrayList<PortAndDay> list = new ArrayList<PortAndDay>();

		ports.entrySet().stream()
		.forEach(entry -> {

			List<ScheduleData> ll =entry.getValue();

			Collections.sort(ll, new ScheduleDateComparator(dateType));

			ScheduleData lastSchedule = ll.get(ll.size()-1);
			// �������� ���� ��¥�� ����
			list.add(new PortAndDay(entry.getKey(), dateType==ScheduleDateComparator.FROM_DATE?lastSchedule.getDateF():lastSchedule.getDateT()));

		});

		Collections.sort(list, dateComparator);

		return list;

	}

	class PortAndDay
	{

		String port;
		String date;
		public PortAndDay(String port, String date)
		{
			this.port = port;
			this.date = date;
		}
		public String toString()
		{
			return  port+" "+KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(date);
		}

	}

	abstract class DateComparator<T> implements Comparator<T>
	{
		protected SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");

	}


	class VesselComparator implements Comparator<OutbondScheduleTreeNode> {

		@Override
		public int compare(OutbondScheduleTreeNode f1, OutbondScheduleTreeNode f2) {

			return f1.vessel.compareTo(f2.vessel);

		}
	}

	class PortAndDayDateComparator extends DateComparator<PortAndDay> {

		@Override
		public int compare(PortAndDay f1, PortAndDay f2) {

			try {

				return formatYYYYMMDD.parse(f1.date).compareTo(formatYYYYMMDD.parse(f2.date));

			} catch (ParseException e) {
				return 0;
			}
		}
	}

	class FromDateComparator extends DateComparator<OutbondScheduleTreeNode>{

		@Override
		public int compare(OutbondScheduleTreeNode f1, OutbondScheduleTreeNode f2) {

			try {

				return formatYYYYMMDD.parse(f1.date).compareTo(formatYYYYMMDD.parse(f2.date));

			} catch (ParseException e) {
				return 0;
			}
		}
	}

	/**
	 * ������ ������ȣ �� ���ڸ� ��ȯ
	 * @param voyage_num
	 * @return
	 */
	public int getNumericVoyage(String voyage_num)
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

	/**
	 * �߱�, �Ϻ� : 2�� �̻�
	 * ���þ� : 1�� �̻�
	 * ��Ÿ : 3�� �̻�
	 * ���� ������ ǥ�� Ȯ��
	 * @param areaName
	 * @param outportCount
	 * @return
	 */
	public boolean checkOutPort(String areaName,int outportCount)
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
	private int getGap(String area_name)
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
}
