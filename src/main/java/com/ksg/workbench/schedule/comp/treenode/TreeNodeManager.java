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
	 * ������ ����Ʈ�� �������� outbound Ʈ�� ��� ����
	 * ������ -> ����� -> �����ٱ׷� -> ������ or ������ -> ����� -> ������   
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

			// ������ ����
			Object[] mapkey = toPortItems.keySet().toArray();
			
			Arrays.sort(mapkey);


			for (Object toPortKey : mapkey)
			{
				// �����ȸ
				HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) toPortItems.get(toPortKey);

				//tree ��� ����
				DefaultMutableTreeNode toPort = new InboundPortTreeNode(toPortKey);

				// ����� ����
				List<Entry<String, Object>> list_entries = new ArrayList<Entry<String, Object>>(fromPortitems.entrySet());

				// ����� ���
				
				for(String fromPortKey:fromPortitems.keySet())
				{

					DefaultMutableTreeNode fromPort = new DefaultMutableTreeNode(fromPortKey);

					List<ScheduleData> schedule = (List) fromPortitems.get(fromPortKey);

					ArrayList<DefaultMutableTreeNode> jointSchedule = createOutboundJoinedScheduleNode(schedule);

					jointSchedule.forEach(scheduleNode ->fromPort.add(scheduleNode ));

					// ������ �׷쿡 �߰�
					toPort.add(fromPort);
				}

				area.add(toPort);				  
			}

			root.add(area);

		}

		return root;
	}
	
	/**
	 * outbound�����輱 ���� 
	 * @param schedule
	 * @return
	 */
	private ArrayList<DefaultMutableTreeNode> createOutboundJoinedScheduleNode(List<ScheduleData> schedule)
	{
		HashMap<String, Object> scheduleList = new HashMap<String, Object>();
		// ���� ��ȣ, ���ڸ��� ���� ��� ��ȸ

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

		// ������ �߰�
		// ����
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
	 * ����
	 * ----�����(�ܱ���)
	 * --------����
	 * ------------������(������)
	 * 
	 * 
	 * @param areaList
	 * @return
	 */
	public DefaultMutableTreeNode getInboundTreeNode(HashMap<String, Object> areaList) {
		
		//inbound port ��� ��� ��ȸ
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");
		
		for(String strArea: areaList.keySet())		
		{	

			DefaultMutableTreeNode area = new DefaultMutableTreeNode(strArea);

			//�����
			HashMap<String, Object> fromPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			// ����� ����
			Object[] mapkey = fromPortItems.keySet().toArray();

			for (Object fromPortKey : mapkey)
			{	
				HashMap<String, Object> vesselitems =  (HashMap<String, Object>) fromPortItems.get(fromPortKey);
				//tree ��� ����
				DefaultMutableTreeNode fromPort = new InboundPortTreeNode((String)fromPortKey, "");

				// ���� ���
				Object[] vesselAndDatekey = vesselitems.keySet().toArray();
				
				
				//makeSortedKey
				
				String[][] key = new String[vesselAndDatekey.length][2];
				
				for(int i =0;i<vesselAndDatekey.length;i++)
				{
					String str = (String) vesselAndDatekey[i];
					key[i][0] =str; //vesselAndDateF
					key[i][1] =str.substring(str.indexOf("$$")+2, str.length()); // dateF
					
				}
				
				// key ����� ���� ����
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

					// ������ ���
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
