package com.ksg.workbench.common.comp.treetable.nodemager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang3.StringUtils;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.JointOutboundScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;
import com.ksg.workbench.schedule.comp.treenode.SortedSchedule;


/**
 * 

  * @FileName : OutboundNodeManager.java

  * @Project : KSG2

  * @Date : 2022. 12. 17. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :
 */
public class OutboundNodeManager extends AbstractNodeManager{
	
	public OutboundNodeManager()
	{
		super();
	}
	
	/**
	 * 
	 * 스케줄 리스트를 기준으로 outbound 트리 노드 생성
	 * 도착항 -> 출발항 -> 스케줄그룹 -> 스케줄 or 도착항 -> 출발항 -> 스케줄   
	 * 
	 * @param result
	 * @return
	 */
	public DefaultMutableTreeNode getTreeNode(CommandMap areaList) {


		DefaultMutableTreeNode root = new AreaTreeNode("AREA");


		for(String strArea:areaList.keySet())
		{
			HashMap<String, Object> toPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			DefaultMutableTreeNode area = new AreaTreeNode(strArea);

			// 도착항 정렬
			Object[] toPortArray = toPortItems.keySet().toArray();

			Arrays.sort(toPortArray);

			for (Object toPortKey : toPortArray)
			{
				// 요소조회
				HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) toPortItems.get(toPortKey);

				//tree 노드 생성
				DefaultMutableTreeNode toPort = new PortTreeNode((String)toPortKey);

				// 출발항 정렬
				List<Entry<String, Object>> list_entries = new ArrayList<Entry<String, Object>>(fromPortitems.entrySet());

				// 출발항 목록

				for(String fromPortKey:fromPortitems.keySet())
				{
					DefaultMutableTreeNode fromPort = new PortTreeNode(fromPortKey);

					List<ScheduleData> schedule = (List) fromPortitems.get(fromPortKey);

					ArrayList<DefaultMutableTreeNode> jointSchedule = createOutboundJoinedScheduleNode(schedule);

					jointSchedule.forEach(scheduleNode ->fromPort.add(scheduleNode ));

					// 도착항 그룹에 추가
					toPort.add(fromPort);
				}

				area.add(toPort);				  
			}

			root.add(area);

		}

		return root;
	}
	
	/**
	 * outbound공동배선 적용 
	 * @param schedule
	 * @return
	 */
	private ArrayList<DefaultMutableTreeNode> createOutboundJoinedScheduleNode(List<ScheduleData> schedule)
	{
		HashMap<String, SortedScheduleGroup> scheduleList = new HashMap<String,SortedScheduleGroup>();


		//====================================
		// 선박명-항차번호 그룹 생성
		//
		// TODO 공동배선으로 묶을 시 3일 이내로
		//====================================
		for(ScheduleData scheduleItem:schedule)
		{	
			//convert hashMap
			SortedSchedule scheduleMap=new SortedSchedule(scheduleItem);

			// 선박명, 항차 번호로 키 생성
			String scheduleKey = getOutboundScheduleKey(scheduleMap);

			if(scheduleList.containsKey(scheduleKey))
			{	
				SortedScheduleGroup jointScheduleItemList = (SortedScheduleGroup) scheduleList.get(scheduleKey);

				jointScheduleItemList.add(scheduleMap);
			}
			else
			{
				SortedScheduleGroup jointScheduleItemList = new SortedScheduleGroup();

				jointScheduleItemList.setVesselName(scheduleMap.getData().getVessel());

				jointScheduleItemList.add(scheduleMap);

				scheduleList.put(scheduleKey, jointScheduleItemList);
			}
		}

		//====================================
		//날짜 기준으로 정렬 수행
		//====================================
		Collection<SortedScheduleGroup> values = scheduleList.values();

		List<SortedScheduleGroup> list = new ArrayList<SortedScheduleGroup>();

		list.addAll(values);

		Collections.sort(list);


		//=====================================
		//트리 노드 생성
		//=====================================
		ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();

		for(SortedScheduleGroup jointScheduleItemList:list)
		{
			if(jointScheduleItemList.scheduleList.size()==1)
			{	
				CommandMap item = objectMapper.convertValue(jointScheduleItemList.scheduleList.get(0).getData(), CommandMap.class);

				nodeList.add(new OutbondScheduleTreeNode(new TreeTableNode(item)));
			}
			else if(jointScheduleItemList.scheduleList.size()>1)
			{	
				String vesselname = jointScheduleItemList.getVesselName();

				DefaultMutableTreeNode node = new JointOutboundScheduleTreeNode(toJointedOutboundScheduleString(vesselname, jointScheduleItemList.scheduleList));

				//treetable 노드 추가
				jointScheduleItemList.scheduleList.forEach(item -> node.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(item.getData(), CommandMap.class)))));

				nodeList.add(node);
			}
		}

		return nodeList;
	}
	
	private String getOutboundScheduleKey(SortedSchedule map)
	{
		String vessel 	= map.getData().getVessel();

		int n_voyage 	= ScheduleBuildUtil.getNumericVoyage( map.getData().getVoyage_num());

		return vessel+"-"+n_voyage;
	}
	
	/**
	 * 
	 * @param scheduleList
	 * @return
	 */
	public String toJointedOutboundScheduleString(String vessel, ArrayList<SortedSchedule> scheduleList)	{


		ArrayList<ScheduleData> list1 = new ArrayList<ScheduleData>();


		// 출발일 목록
		scheduleList.stream().forEach(o -> list1.add(o.getData()));

		List<String> dateFList=list1.stream().map(ScheduleData::getDateF)

									.collect(Collectors.toList());

		List<String> dateTList=list1.stream().map(ScheduleData::getDateT)

									.collect(Collectors.toList());

		List<String> companyAbbrList=list1.stream().map(ScheduleData::getCompany_abbr).distinct()

										  .collect(Collectors.toList());

		// 출발일 정렬(오른 차순)
		Collections.sort(dateFList);

		// 도착일 정렬(내림 차순)
		Collections.sort(dateTList, Collections.reverseOrder());

		// 선사명 정렬(오른 차순)
		Collections.sort(companyAbbrList);		

		// 출발일
		String dateF =KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(dateFList.get(0));

		// 도착일
		String dateT = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(dateTList.get(0));		

		// TODO 스케줄 - 대표 선사명 지정
		String re_company_abbr = "";

		// 선사명 양식 (선사명1, 선사명2, ...)
		String company_abbr = StringUtils.join(companyAbbrList.toArray(new String[companyAbbrList.size()]),",");



		return String.format("%s %s (%s) %s " , dateF, vessel, company_abbr, dateT);
	}
	

	/**
	 * 

	 * @FileName : TreeNodeManager.java

	 * @Project : KSG2

	 * @Date : 2022. 12. 7. 

	 * @작성자 : pch

	 * @변경이력 :

	 * @프로그램 설명 :
	 */

	class SortedScheduleGroup implements Comparable<SortedScheduleGroup>
	{
		private String vessel;

		private  SimpleDateFormat formatYYYYMMDD = KSGDateUtil.createInputDateFormat();

		public ArrayList<SortedSchedule> scheduleList;

		public SortedScheduleGroup()
		{
			scheduleList = new ArrayList<SortedSchedule>();
		}

		public void setVesselName(String vessel) {
			this.vessel=  vessel;

		}
		public String getVesselName()
		{
			return vessel;
		}

		public void add(SortedSchedule item)
		{
			this.scheduleList.add(item);
		}

		@Override
		public int compareTo(SortedScheduleGroup o) {
			try {

				return formatYYYYMMDD.parse(scheduleList.get(0).getData().getDateF()).compareTo(formatYYYYMMDD.parse(o.scheduleList.get(0).getData().getDateF()));
			} catch (ParseException e) {
				return 0;
			}
		}

	}



}
