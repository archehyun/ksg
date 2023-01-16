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

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 라우트 스케줄 트리 노드 생성
  * 
  * 정렬 기준 1, 선박, 출발일
 */
@Slf4j
public class RouteNodeManager extends AbstractNodeManager{
	
	
	PortAndDayDateComparator dateComparator = new PortAndDayDateComparator();
	
	FromDateComparator fromDateComparator 	= new FromDateComparator();
	
	VesselComparator vesselComparator 		= new VesselComparator();
	

	
	public RouteNodeManager()
	{
		super();
	}
	
	/**
	 * TODO 도착일 늦은 날짜로 정렬
	 * TODO
	 */
	/**
	 * 지역 - 선박
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
		
		// 지역
		for(Object strArea:mapkey)
		{
			HashMap<String, Object> vesselList =  (HashMap<String, Object>) areaList.get(strArea);
			// 선박
			Object[] vesselArray = vesselList.keySet().toArray();
			
			
			DefaultMutableTreeNode area = new AreaTreeNode((String) strArea);
			
			//Arrays.sort(vesselArray);
			
			List<OutbondScheduleTreeNode> areaScheduleList = new ArrayList<OutbondScheduleTreeNode>();
			
			for (Object vesselKey : vesselArray)
			{	
				// 요소조회
				List<ScheduleData> scheduleList = (List<ScheduleData>) vesselList.get(vesselKey);
				
				// 항차번호(숫자)로 그룹화
				Map<Integer, List<ScheduleData>> testList =  scheduleList.stream().collect(
						Collectors.groupingBy(o -> getNumericVoyage(o.getVoyage_num()) ));// 항차
				
				
				//TODO 항차 번호로 정렬
				
				Object[] voyageArray = testList.keySet().toArray();
				
				Arrays.sort(voyageArray);
				
				for(Object voyagekey:voyageArray)
				{
					List<ScheduleData> subscheduleList = testList.get(voyagekey);
					areaScheduleList.add((OutbondScheduleTreeNode) makeScheduleNode((String) strArea, (String) vesselKey, voyagekey, subscheduleList));
				}	
				
			}
			
			// 출발일 기준으로 정렬
			Collections.sort(areaScheduleList, sortType.equals("date")?fromDateComparator:vesselComparator);			
			
			areaScheduleList.forEach(o ->area.add(o));
			
			root.add(area);

		}

		return root;
	}
	
	private DefaultMutableTreeNode makeScheduleNode(String strArea, String vesselKey, Object voyagekey, List<ScheduleData> scheduleList) {
		

		Map<String, List<ScheduleData>> fromPorts 	= scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getFromPort));

		Map<String, List<ScheduleData>> toPorts 	= scheduleList.stream().collect(Collectors.groupingBy(ScheduleData::getPort));

		int toPortCount = toPorts.keySet().size();

		int fromPortCount = fromPorts.keySet().size();
		
		
		//TODO 스케줄 - Route 스케줄 밸리데이션
		
		log.info("area:{}, fromPortCount:{}, {}",strArea, fromPortCount, checkOutPort(strArea, toPortCount));
		
		// TODO 스케줄-항차 번호 표시
		OutbondScheduleTreeNode schedule = new OutbondScheduleTreeNode(vesselKey+", "+ voyagekey, (checkOutPort(strArea, toPortCount)?NodeType.SCHEDULE:NodeType.JOINT_SCHEDULE)  );
		
		DefaultMutableTreeNode toPort =  new PortTreeNode( StringUtils.join(makeDayList(toPorts, ScheduleDateComparator.TO_DATE)," - "));				
		
		scheduleList.stream().forEach(o -> toPort.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(o, CommandMap.class)))));
		
		
		List<PortAndDay> fromPortlist = makeDayList(fromPorts, ScheduleDateComparator.FROM_DATE);
		// 출발항 목록
		schedule.add(new PortTreeNode( StringUtils.join( fromPortlist," - ")));
		
		// 출발항은 늦은 날짜
		schedule.date = fromPortlist.get(0).date;
		
		schedule.vessel = vesselKey;
		
		
		// 도착항 목록
		schedule.add(toPort);
		
		// TODO 출발항 빠른 날짜 기준으로 정렬
		
		return schedule;
		
		
		
		
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

			ScheduleData lastSchedule = ll.get(ll.size()-1);
			// 도착항은 빠른 날짜로 정렬
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
	 * 문자형 항차번호 중 숫자만 반환
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
	 * 중국, 일본 : 2곳 이상
	 * 러시아 : 1곳 이상
	 * 기타 : 3곳 이상
	 * 항차 스케줄 표시 확인
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
		// 러시아
		else if(areaName.equals(AreaEnum.RUSSIA.toUpperCase()))
		{
			return outportCount>0;
		}					
		// 기타 지역
		else
		{
			return outportCount>=3;
		}
		
	}
}
