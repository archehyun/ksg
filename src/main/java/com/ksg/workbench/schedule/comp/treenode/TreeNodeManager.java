package com.ksg.workbench.schedule.comp.treenode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.InboundGroupTreeNode;
import com.ksg.workbench.common.comp.treetable.node.JointOutboundScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;


public class TreeNodeManager {

	//TODO TEST

	private ObjectMapper objectMapper;

	public TreeNodeManager()
	{
		objectMapper = new ObjectMapper();
	}

	/**
	 * 
	 * 스케줄 리스트를 기준으로 outbound 트리 노드 생성
	 * 도착항 -> 출발항 -> 스케줄그룹 -> 스케줄 or 도착항 -> 출발항 -> 스케줄   
	 * 
	 * @param result
	 * @return
	 */
	public DefaultMutableTreeNode getOutboundTreeNode(HashMap<String, Object> areaList) {


		DefaultMutableTreeNode root = new AreaTreeNode("AREA");


		for(String strArea:areaList.keySet())
		{

			HashMap<String, Object> toPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			DefaultMutableTreeNode area = new AreaTreeNode(strArea);

			// 도착항 정렬
			Object[] mapkey = toPortItems.keySet().toArray();

			Arrays.sort(mapkey);


			for (Object toPortKey : mapkey)
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

		// 항차 번호, 선박명이 같은 목록 조회
		
		// 공동배선 대상 목록 생성, 선박명-항차번호가 같은 목록을 그룹화
		
		// TODO 공동배선으로 묶을 시 3일 이내로
		for(ScheduleData scheduleItem:schedule)
		{	
			//convert hashMap
			SortedSchedule scheduleMap=new SortedSchedule(scheduleItem);

			String scheduleKey = getScheduleKey(scheduleMap);

			if(scheduleList.containsKey(scheduleKey))
			{	
				SortedScheduleGroup jointScheduleItemList = (SortedScheduleGroup) scheduleList.get(scheduleKey);
				jointScheduleItemList.add(scheduleMap);
			}
			else
			{
				SortedScheduleGroup jointScheduleItemList = new SortedScheduleGroup();

				jointScheduleItemList.add(scheduleMap);

				scheduleList.put(scheduleKey, jointScheduleItemList);
			}
		}


		ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();

		Collection<SortedScheduleGroup> values = scheduleList.values();
		
		List<SortedScheduleGroup> list = new ArrayList<SortedScheduleGroup>();
		
		list.addAll(values);

		Collections.sort(list);

		for(SortedScheduleGroup jointScheduleItemList:list)
		{				

			if(jointScheduleItemList.scheduleList.size()==1)
			{	
				CommandMap item = objectMapper.convertValue(jointScheduleItemList.scheduleList.get(0).getData(), CommandMap.class);
				
				nodeList.add(new OutbondScheduleTreeNode(new TreeTableNode(item)));
			}
			else if(jointScheduleItemList.scheduleList.size()>1)
			{
				
				
				//TODO 공동배선 묶인 스케줄중 날짜 선정
				
				
				Collections.sort(jointScheduleItemList.scheduleList);
				
				ScheduleData firstSchedule=jointScheduleItemList.scheduleList.get(0).getData();
				
				ScheduleData lastSchedule=jointScheduleItemList.scheduleList.get(jointScheduleItemList.scheduleList.size()-1).getData();
				
				String dateF =KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(firstSchedule.getDateF());
				
				String vessel = firstSchedule.getVessel();
				
				String company_abbr = firstSchedule.getCompany_abbr();
				
				String dateT = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(lastSchedule.getDateT());
				
				DefaultMutableTreeNode node = new JointOutboundScheduleTreeNode(String.format("%s %s (%s) %s " , dateF, vessel, company_abbr, dateT));

				jointScheduleItemList.scheduleList.forEach(item -> node.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(item.getData(), CommandMap.class)))));

				nodeList.add(node);
			}
		}

		return nodeList;
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

		private  SimpleDateFormat formatYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");

		public ArrayList<SortedSchedule> scheduleList;

		public SortedScheduleGroup()
		{
			scheduleList = new ArrayList<SortedSchedule>();
		}

		public void add(SortedSchedule item)
		{
			this.scheduleList.add(item);
		}

		@Override
		public int compareTo(SortedScheduleGroup o) {
			// TODO Auto-generated method stub
			try {
				
				return formatYYYYMMDD.parse(scheduleList.get(0).getData().getDateF()).compareTo(formatYYYYMMDD.parse(o.scheduleList.get(0).getData().getDateF()));
			} catch (ParseException e) {
				return 0;
			}
		}

	}

	private String getScheduleKey(SortedSchedule map)
	{
		String vessel 	= map.getData().getVessel();

		int n_voyage 	= ScheduleBuildUtil.getNumericVoyage( map.getData().getVoyage_num());

		return vessel+"-"+n_voyage;
	}


	/**=======================================================================**/
	/** inbounds tree node**/

	/**
	 * 지역
	 * ----출발항(외국항)
	 * --------선박
	 * ------------도착항(국내항)
	 * 
	 * 
	 * @param areaList
	 * @return
	 */
	public DefaultMutableTreeNode getInboundTreeNode(CommandMap areaList) {

		//inbound port 약어 목록 조회

		DefaultMutableTreeNode root = new AreaTreeNode("AREA");

		for(String strArea: areaList.keySet())		
		{	

			DefaultMutableTreeNode area = new PortTreeNode(strArea);

			//출발항
			CommandMap fromPortItems =  (CommandMap) areaList.get(strArea);

			// 출발항 정렬
			Object[] mapkey = fromPortItems.keySet().toArray();

			for (Object fromPortKey : mapkey)
			{	
				CommandMap vesselitems =  (CommandMap) fromPortItems.get(fromPortKey);
				//tree 노드 생성
				DefaultMutableTreeNode fromPort = new PortTreeNode((String)fromPortKey);

				// 선박 목록

				//makeSortedKey

				String[][] sortedKey = getSortedKey(vesselitems.keySet().toArray());


				for(String arr[]: sortedKey)
				{
					String str = (String) arr[0];

					String vesselName =str.substring(0,str.indexOf("$$"));

					// 스케줄 목록
					ArrayList<CommandMap> scheduleList =  (ArrayList<CommandMap>) vesselitems.get(str);


					fromPort.add(new InboundGroupTreeNode(vesselName, scheduleList ));	
				}

				area.add(fromPort);

			}
			root.add(area);
		}


		return root;

	}
	/**
	 * 
	 * @param vesselAndDatekey
	 * @return inbound sorted keys
	 */
	private String[][] getSortedKey(Object[] vesselAndDatekey) {
		
		String[][] sortedKey = new String[vesselAndDatekey.length][2];

		for(int i =0;i<vesselAndDatekey.length;i++)
		{
			String str = (String) vesselAndDatekey[i];
			sortedKey[i][0] =str; //vesselAndDateF
			sortedKey[i][1] =str.substring(str.indexOf("$$")+2, str.length()); // dateF

		}

		// key 출발일 기준 정렬
		Arrays.sort(sortedKey, new Comparator<String[]>() { 
			@Override 
			public int compare(String[] o1, String[] o2) {

				String fromDateOne = o1[1];
				String fromDateTwo = o2[1];;

				return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

			}
		});
		return sortedKey;
	}


}
