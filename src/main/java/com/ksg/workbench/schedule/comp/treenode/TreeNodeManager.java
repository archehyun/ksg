package com.ksg.workbench.schedule.comp.treenode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.execute.formater.InboundJointedFormatter;
import com.ksg.schedule.execute.formater.JointFormatter;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.treetable.node.InboundGroupTreeNode;
import com.ksg.workbench.common.comp.treetable.node.InboundPortTreeNode;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;


public class TreeNodeManager {
	
	
	
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


		DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");
		
		
		for(String strArea:areaList.keySet())
		{
		
			HashMap<String, Object> toPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			DefaultMutableTreeNode area = new DefaultMutableTreeNode(strArea);

			// 도착항 정렬
			Object[] mapkey = toPortItems.keySet().toArray();
			
			Arrays.sort(mapkey);


			for (Object toPortKey : mapkey)
			{
				// 요소조회
				HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) toPortItems.get(toPortKey);

				//tree 노드 생성
				DefaultMutableTreeNode toPort = new InboundPortTreeNode(toPortKey);

				// 출발항 정렬
				List<Entry<String, Object>> list_entries = new ArrayList<Entry<String, Object>>(fromPortitems.entrySet());

				// 출발항 목록
				
				for(String fromPortKey:fromPortitems.keySet())
				{

					DefaultMutableTreeNode fromPort = new DefaultMutableTreeNode(fromPortKey);

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
		HashMap<String, Object> scheduleList = new HashMap<String, Object>();
		// 항차 번호, 선박명이 같은 목록 조회

		for(ScheduleData scheduleItem:schedule)
		{	
			//convert hashMap
			CommandMap scheduleMap=(CommandMap) objectMapper.convertValue(scheduleItem, CommandMap.class);
			
			String scheduleKey = getScheduleKey(scheduleMap);
			
			if(scheduleList.containsKey(scheduleKey))
			{	
				ArrayList<HashMap<String, Object>> jointScheduleItemList = (ArrayList<HashMap<String, Object>>) scheduleList.get(scheduleKey);
				jointScheduleItemList.add(scheduleMap);
			}
			else
			{
				ArrayList<HashMap<String, Object>> jointScheduleItemList = new ArrayList<HashMap<String, Object>>();
				
				jointScheduleItemList.add(scheduleMap);
				
				scheduleList.put(scheduleKey, jointScheduleItemList);
			}
		}

		ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();

		for(String strKey:scheduleList.keySet())
		{
			ArrayList<CommandMap> jointScheduleItemList=(ArrayList<CommandMap>) scheduleList.get(strKey);			
			
			if(jointScheduleItemList.size()==1)
			{
				DefaultMutableTreeNode node = new OutbondScheduleTreeNode(new TreeTableNode(jointScheduleItemList.get(0)));
				nodeList.add(node);
			}
			else if(jointScheduleItemList.size()>1)
			{
				DefaultMutableTreeNode node = new OutbondScheduleTreeNode("joint");
				
				jointScheduleItemList.forEach(item -> node.add(new OutbondScheduleTreeNode(new TreeTableNode(item))));
				
				nodeList.add(node);
			}

		}

		// 스케줄 추가
		// 정렬
		return nodeList;
	}
	
	private String getScheduleKey(CommandMap map)
	{
		String vessel 	= String.valueOf( map.get("vessel"));
		
		int n_voyage 	= ScheduleBuildUtil.getNumericVoyage( String.valueOf(map.get("voyage_num")));
		
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
	public DefaultMutableTreeNode getInboundTreeNode(HashMap<String, Object> areaList) {
		
		//inbound port 약어 목록 조회
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");
		
		for(String strArea: areaList.keySet())		
		{	

			DefaultMutableTreeNode area = new DefaultMutableTreeNode(strArea);

			//출발항
			HashMap<String, Object> fromPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			// 출발항 정렬
			Object[] mapkey = fromPortItems.keySet().toArray();

			for (Object fromPortKey : mapkey)
			{	
				HashMap<String, Object> vesselitems =  (HashMap<String, Object>) fromPortItems.get(fromPortKey);
				//tree 노드 생성
				DefaultMutableTreeNode fromPort = new InboundPortTreeNode((String)fromPortKey, "");

				// 선박 목록
				Object[] vesselAndDatekey = vesselitems.keySet().toArray();
				
				
				//makeSortedKey
				
				String[][] key = new String[vesselAndDatekey.length][2];
				
				for(int i =0;i<vesselAndDatekey.length;i++)
				{
					String str = (String) vesselAndDatekey[i];
					key[i][0] =str; //vesselAndDateF
					key[i][1] =str.substring(str.indexOf("$$")+2, str.length()); // dateF
					
				}
				
				// key 출발일 기준 정렬
				Arrays.sort(key, new Comparator<String[]>() { 
					@Override 
					public int compare(String[] o1, String[] o2) {
						
						String fromDateOne = o1[1];
						String fromDateTwo = o2[1];;

						return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;
						
					}
				});

				
				
				for (int i=0;i<key.length;i++)
				{
					String str = (String) key[i][0];
					
					String vesselName =str.substring(0,str.indexOf("$$"));

					// 스케줄 목록
					ArrayList<HashMap<String, Object>> scheduleList =  (ArrayList<HashMap<String, Object>>) vesselitems.get(str);


					fromPort.add(new InboundGroupTreeNode(vesselName,new InboundJointedFormatter(), scheduleList ));
					
				}
				area.add(fromPort);

			}
			root.add(area);
		}


		return root;

	}
	

}
