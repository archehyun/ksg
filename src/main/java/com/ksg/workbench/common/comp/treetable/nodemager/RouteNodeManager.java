package com.ksg.workbench.common.comp.treetable.nodemager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.ksg.domain.ScheduleData;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;
import com.ksg.workbench.common.comp.treetable.node.ScheduleDateComparator;

public class RouteNodeManager extends AbstractNodeManager{
	
	
	DateComparator dateComparator = new DateComparator();
	
	public RouteNodeManager()
	{
		super();
	}
	/**
	 * 지역 - 선박
	 * @param areaList
	 * @return
	 */
	public DefaultMutableTreeNode getTreeNode(CommandMap areaList) {

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("area");

		// 지역
		for(String strArea:areaList.keySet())
		{
			HashMap<String, Object> vesselList =  (HashMap<String, Object>) areaList.get(strArea);
			// 선박
			Object[] vesselArray = vesselList.keySet().toArray();
			
			
			DefaultMutableTreeNode area = new AreaTreeNode(strArea);

			for (Object vesselKey : vesselArray)
			{
				// 요소조회
				List<ScheduleData> scheduleList = (List<ScheduleData>) vesselList.get(vesselKey);

				Map<String, List<ScheduleData>> fromPorts =scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getFromPort));

				Map<String, List<ScheduleData>> toPorts =scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getPort));

				int toPortCount = toPorts.keySet().size();

				int fromPortCount = fromPorts.keySet().size();
				
				//TODO 스케줄 밸리데이션
				
				
				DefaultMutableTreeNode schedule = new DefaultMutableTreeNode(vesselKey+", ");
				
				
				
				DefaultMutableTreeNode toPort =  new PortTreeNode( StringUtils.join(makeDayList(toPorts, ScheduleDateComparator.TO_DATE)," - "));				
				
				scheduleList.stream().forEach(o -> toPort.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(o, CommandMap.class)))));
				
				// 출발항 목록
				schedule.add(new PortTreeNode( StringUtils.join(makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE)," - ")));
				
				// 도착항 목록
				schedule.add(toPort);
				
				
				
				area.add(schedule);
				
			}
			root.add(area);

		}

		return root;
	}
	
	/**
	 * 출발일, 도착일 별 그룹 생성
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

}
