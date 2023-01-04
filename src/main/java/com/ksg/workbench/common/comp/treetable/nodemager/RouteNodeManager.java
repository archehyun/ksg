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
 */
@Slf4j
public class RouteNodeManager extends AbstractNodeManager{
	

	
	
	DateComparator dateComparator = new DateComparator();
	
	public RouteNodeManager()
	{
		super();
	}
	/**
	 * ���� - ����
	 * @param areaList
	 * @return
	 */
	public DefaultMutableTreeNode getTreeNode(CommandMap areaList) {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("area");

		// ����
		for(String strArea:areaList.keySet())
		{
			HashMap<String, Object> vesselList =  (HashMap<String, Object>) areaList.get(strArea);
			// ����
			Object[] vesselArray = vesselList.keySet().toArray();
			
			
			DefaultMutableTreeNode area = new AreaTreeNode(strArea);
			
			Arrays.sort(vesselArray);

			for (Object vesselKey : vesselArray)
			{
				
				// �����ȸ
				List<ScheduleData> scheduleList = (List<ScheduleData>) vesselList.get(vesselKey);
				
				// ������ȣ(����)�� �׷�ȭ
				Map<Integer, List<ScheduleData>> testList =  scheduleList.stream().collect(
						Collectors.groupingBy(o -> getNumericVoyage(o.getVoyage_num()) ));// ����
				
				
				for(Object voyagekey:testList.keySet())
				{
					List<ScheduleData> subscheduleList = testList.get(voyagekey);
					area.add(makeScheduleNode(strArea, vesselKey, voyagekey, subscheduleList));
				}
				
			}
			root.add(area);

		}

		return root;
	}
	
	private DefaultMutableTreeNode makeScheduleNode(String strArea, Object vesselKey, Object voyagekey, List<ScheduleData> scheduleList) {
		

		Map<String, List<ScheduleData>> fromPorts =scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getFromPort));

		Map<String, List<ScheduleData>> toPorts =scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getPort));

		int toPortCount = toPorts.keySet().size();

		int fromPortCount = fromPorts.keySet().size();
		
		
		
		//TODO ������ - Route ������ �븮���̼�
		
		log.info("area:{}, fromPortCount:{}, {}",strArea, fromPortCount, checkOutPort(strArea, toPortCount));
		
		// TODO ������-���� ��ȣ ǥ��
		DefaultMutableTreeNode schedule = new OutbondScheduleTreeNode(vesselKey+", "+ voyagekey, (checkOutPort(strArea, toPortCount)?NodeType.SCHEDULE:NodeType.JOINT_SCHEDULE)  );
		
		
		
		DefaultMutableTreeNode toPort =  new PortTreeNode( StringUtils.join(makeDayList(toPorts, ScheduleDateComparator.TO_DATE)," - "));				
		
		scheduleList.stream().forEach(o -> toPort.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(o, CommandMap.class)))));
		
		// ����� ���
		schedule.add(new PortTreeNode( StringUtils.join(makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE)," - ")));
		
		// ������ ���
		schedule.add(toPort);
		
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

			ScheduleData fristSchedule = ll.get(0);

			list.add(new PortAndDay(entry.getKey(), dateType==ScheduleDateComparator.FROM_DATE?fristSchedule.getDateF():fristSchedule.getDateT()));

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
	
	class DateComparator implements Comparator<PortAndDay> {


		private  SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");
		@Override
		public int compare(PortAndDay f1, PortAndDay f2) {

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

}
