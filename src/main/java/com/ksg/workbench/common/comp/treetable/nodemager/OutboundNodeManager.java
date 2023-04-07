package com.ksg.workbench.common.comp.treetable.nodemager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

import com.dtp.api.schedule.joint.outbound.OutboundSchedule;
import com.dtp.api.schedule.joint.outbound.OutboundScheduleGroup;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.NodeType;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;
import com.ksg.workbench.common.comp.treetable.node.ScheduleTreeNode;


/**
 * 

 * @FileName : OutboundNodeManager.java

 * @Project : KSG2

 * @Date : 2022. 12. 17. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 :
 */
/**

 * @FileName : OutboundNodeManager.java

 * @Project : KSG2

 * @Date : 2023. 4. 6. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 :

 */
public class OutboundNodeManager extends AbstractNodeManager{

	private OutboundSchedule outboundSchedule;
	
	private HashMap<String, Object> portMap;
	
	private HashMap<String, Vessel> vesselMap;
	
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
	public DefaultMutableTreeNode getTreeNode(CommandMap result) {

		CommandMap scheduleGroupMap 		= (CommandMap) result.get("areaList");

		portMap 					= (HashMap<String, Object>) result.get("portMap");

		vesselMap 					= (HashMap<String, Vessel>) result.get("vesselMap");

		outboundSchedule 			= new OutboundSchedule(vesselMap);

		DefaultMutableTreeNode root = new AreaTreeNode("AREA");

		Set<String> areaKeySet=scheduleGroupMap.keySet();

		areaKeySet.stream()
					.sorted()
					.forEach(toPortKey ->
					{
						PortInfo port =(PortInfo) portMap.get(toPortKey);
			
						DefaultMutableTreeNode toPort = new PortTreeNode(String.format("%s, %s", toPortKey, port.getPort_nationality()) );
			
						Map<String, List<ScheduleData>> fromPortItems = (Map<String, List<ScheduleData>>) scheduleGroupMap.get(toPortKey);
			
						fromPortItems.keySet().stream()
												.sorted()
												.forEach(fromPortKey -> toPort.add(createFromPortNodeItem( fromPortKey, fromPortItems.get(fromPortKey))));
						
						root.add(toPort);
			
					});

		return root;
	}

	/**
	 * @param fromPortName
	 * @param scheduleList
	 * @return
	 */
	private DefaultMutableTreeNode createFromPortNodeItem(String fromPortName , List<ScheduleData> scheduleList) {
		//tree 노드 생성

		DefaultMutableTreeNode fromPort = new PortTreeNode(fromPortName);

		ArrayList<OutboundScheduleGroup> list = (ArrayList<OutboundScheduleGroup>) outboundSchedule. createFromPortOutboundScheduleGroup(scheduleList);

		ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();

		list.forEach(o -> nodeList.add(o.isMulti()? createJointOutboundScheduleGroupNode(o):createScheduleNode(o)));

		nodeList.forEach(scheduleNode ->fromPort.add(scheduleNode ));

		// 도착항 그룹에 추가
		return fromPort;
	}

	/**
	 * @param jointScheduleItemList
	 * @return
	 */
	private OutbondScheduleTreeNode createScheduleNode(OutboundScheduleGroup jointScheduleItemList) {

		CommandMap item = objectMapper.convertValue(jointScheduleItemList.scheduleList.get(0).getData(), CommandMap.class);

		item.put("vessel_type", jointScheduleItemList.getVesel_type());

		return new OutbondScheduleTreeNode(new TreeTableNode(item), jointScheduleItemList.parent!=null?NodeType.SPLITED_SCHEDULE: NodeType.SCHEDULE);
	}

	/**
	 * @param jointScheduleItemList
	 * @return
	 */
	private DefaultMutableTreeNode createJointOutboundScheduleGroupNode(OutboundScheduleGroup jointScheduleItemList) {

		DefaultMutableTreeNode node = new ScheduleTreeNode(jointScheduleItemList.toJointedOutboundScheduleString(), jointScheduleItemList.parent==null?NodeType.JOINT_SCHEDULE:NodeType.SPLITED_SCHEDULE);

		jointScheduleItemList.scheduleList.forEach(item -> node.add(new OutbondScheduleTreeNode(jointScheduleItemList.toJointedOutboundScheduleString(),new TreeTableNode(objectMapper.convertValue(item.getData(), CommandMap.class)), NodeType.SCHEDULE)));

		return node;
	}
}
