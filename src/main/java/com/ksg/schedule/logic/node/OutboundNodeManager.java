package com.ksg.schedule.logic.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.dtp.api.schedule.joint.outbound.OutboundScheduleGroup;
import com.dtp.api.schedule.joint.outbound.OutboundScheduleRule;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	private OutboundScheduleRule outboundSchedule;
	
	private HashMap<String, PortInfo> portMap;
	
	private HashMap<String, Vessel> vesselMap;

	private String[] fromPort;

	private boolean isAddValidate;
	
	public OutboundNodeManager()
	{
		super();
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
	public DefaultMutableTreeNode getTreeNode(CommandMap result) {
		
		List<ScheduleData> scheduleList =(List<ScheduleData>) result.get("scheduleList");

		portMap 					= (HashMap<String, PortInfo>) result.get("portMap");

		vesselMap 					= (HashMap<String, Vessel>) result.get("vesselMap");
		
		fromPort 					= (String[]) result.get("fromPort");
		
		isAddValidate = 			(boolean) result.get("isAddValidate");

		outboundSchedule 			= new OutboundScheduleRule(vesselMap);
		
		boolean mergeBusan 			= (boolean) result.get("mergeBusan");
		
		
		if(scheduleList==null) return new DefaultMutableTreeNode();
		
		Map<String, Map<String, List<ScheduleData>>> scheduleGroupMap  = outboundSchedule.groupedOutboundSchedule(mergeBusan,scheduleList);
		
		DefaultMutableTreeNode root = new AreaTreeNode("AREA");
		
		if(scheduleGroupMap== null) return  root;

		scheduleGroupMap.keySet().stream()
								.sorted()
								.forEach(toPortKey ->
								{
									PortInfo port =(PortInfo) portMap.get(toPortKey);
						
									DefaultMutableTreeNode toPort = new PortTreeNode(String.format("%s, %s", toPortKey, port.getPort_nationality()) );
									root.add(toPort);
									
									Map<String, List<ScheduleData>> fromPortItems = (Map<String, List<ScheduleData>>) scheduleGroupMap.get(toPortKey);
									
									for(String fromPortKey:fromPort)
									{
										if(fromPortItems.containsKey(fromPortKey)) toPort.add(createFromPortNodeItem( fromPortKey, fromPortItems.get(fromPortKey)));	
									}
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

		ArrayList<OutboundScheduleGroup> outboundScheduleGroupList = (ArrayList<OutboundScheduleGroup>) outboundSchedule. createFromPortOutboundScheduleGroup(scheduleList);
		
		outboundScheduleGroupList.forEach(o -> o.joinnted());

		ArrayList<DefaultMutableTreeNode> vesselNodeList = new ArrayList<DefaultMutableTreeNode>();
		
		outboundScheduleGroupList.stream()
								.filter(o -> isAddValidate?true:!o.isDateValidate())
								.sorted()
								.forEach(o -> vesselNodeList.add(o.isMulti()? createJointOutboundScheduleGroupNode(o):createScheduleNode(o)));

		vesselNodeList.forEach(scheduleNode ->fromPort.add(scheduleNode ));

		// 도착항 그룹에 추가
		return fromPort;
	}

	/**
	 * @param jointScheduleItemList
	 * @return
	 */
	private OutbondScheduleTreeNode createScheduleNode(OutboundScheduleGroup jointScheduleItemList) {

		CommandMap item = objectMapper.convertValue(jointScheduleItemList.scheduleList.get(0).getData(), CommandMap.class);

		item.put("vessel_type", jointScheduleItemList.getVesselType());

		return new OutbondScheduleTreeNode(new TreeTableNode(item), jointScheduleItemList.parent!=null?NodeType.SPLITED_SCHEDULE: NodeType.SCHEDULE);
	}

	/**
	 * @param jointScheduleItemList
	 * @return
	 */
	private DefaultMutableTreeNode createJointOutboundScheduleGroupNode(OutboundScheduleGroup jointScheduleItemList) {

		DefaultMutableTreeNode node = new ScheduleTreeNode(jointScheduleItemList.toJointedOutboundScheduleString(), jointScheduleItemList.parent==null?NodeType.JOINT_SCHEDULE:NodeType.SPLITED_SCHEDULE);

		jointScheduleItemList.scheduleList.forEach(item -> {
			
			CommandMap param = objectMapper.convertValue(item.getData(), CommandMap.class);
			param.put("vessel_type", jointScheduleItemList.getVesselType());
			
			node.add(new OutbondScheduleTreeNode(new TreeTableNode(param), NodeType.SCHEDULE));
			});

		return node;
	}
}
