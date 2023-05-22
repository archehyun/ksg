package com.dtp.api.schedule.joint.tree;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dtp.api.schedule.comparator.DateComparator;
import com.dtp.api.schedule.comparator.VesselComparator;
import com.dtp.api.schedule.joint.print.route.RouteJoint;
import com.dtp.api.schedule.joint.print.route.RouteJointSubject;
import com.dtp.api.schedule.joint.print.route.RouteScheduleGroup;
import com.dtp.api.schedule.joint.tree.node.AreaTreeNode;
import com.dtp.api.schedule.joint.tree.node.NodeType;
import com.dtp.api.schedule.joint.tree.node.OutbondScheduleTreeNode;
import com.dtp.api.schedule.joint.tree.node.PortTreeNode;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.treetable.TreeTableNode;

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
public class RouteNodeManager extends AbstractNodeManager implements RouteJointSubject{
	
	private RouteJoint routeJoint;

	private DateComparator dateComparator 			= new DateComparator(new SimpleDateFormat("yyyy/MM/dd"));

	private VesselComparator vesselComparator 		= new VesselComparator();
	
	private boolean diviedByAreaGap = true;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	boolean isAddValidate = false;
	
	public RouteNodeManager()
	{
		super();
		routeJoint = new RouteJoint(this);
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
		
		log.info("param:{}",param);
		logger.info("param:{}",param);

		Map<String, Map<String, List<ScheduleData>>> areaList= (Map<String, Map<String, List<ScheduleData>>>) param.get("data");
		
		vesselMap 					= (HashMap<String, Vessel>) param.get("vesselMap");

		String sortType = (String) param.get("sortType");
		
		isAddValidate = (boolean) param.get("isAddValidate");
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("area");
		
		areaList.keySet().stream()
						.sorted()
						.forEach(strArea ->{
							Map<String, List<ScheduleData>> vesselList =  (Map<String, List<ScheduleData>>) areaList.get(strArea);
							
							// ����
							List<OutbondScheduleTreeNode> areaScheduleNodeList = routeJoint.createRouteScheduleGroupList(	vesselList , strArea,this);
							// ����� �������� ����
							Collections.sort(areaScheduleNodeList, sortType.equals("date")?dateComparator:vesselComparator);
							
							// ���			
							int count =(int) areaScheduleNodeList.stream().filter(o ->isAddValidate?true:!o.getType().equals(NodeType.JOINT_SCHEDULE)).count();
							
							if(count>0) {			
								DefaultMutableTreeNode area = new AreaTreeNode(String.format("%s(%d)", strArea, count));
					
								areaScheduleNodeList.stream()
													.filter(o -> isAddValidate?true:!o.getType().equals(NodeType.JOINT_SCHEDULE))
													.forEach(o ->area.add(o));
								
								root.add(area);
							}
						});
		
		return root;
	}


	
	/**
	 * 
	 * @param strArea
	 * @param group
	 * @param scheduleList
	 * @return
	 */
	private DefaultMutableTreeNode makeScheduleNode(String strArea, RouteScheduleGroup group, List<ScheduleData> scheduleList)
	{
		String strCompanys 	= group.toCompanyString();
		
		String strVoyage 	= group.getVoyage();
		
		String vesselName 	= group.getVesselName();
		
		String strFromPorts = group.toFromPortString();
		
		String strToPorts 	= group.toToPortString();
		
		String nodeName = String.format("%s - %s (%s)", vesselName, strVoyage, strCompanys);
		
		NodeType nodeType = group.isRouteScheduleValidation(strArea)?NodeType.SCHEDULE:NodeType.JOINT_SCHEDULE;
		
		OutbondScheduleTreeNode schedule 	= new OutbondScheduleTreeNode(nodeName, nodeType  );

		DefaultMutableTreeNode toPort 		=  new PortTreeNode( strToPorts);				

		scheduleList.stream().forEach(o -> toPort.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(o, CommandMap.class)))));
		
		// ����� ���
		schedule.add(new PortTreeNode( strFromPorts));

		// ������� ���� ��¥
		schedule.date = group.getDateF();

		schedule.vessel = group.getVesselName();

		// ������ ���
		schedule.add(toPort);

		// TODO ����� ���� ��¥ �������� ����

		return schedule;

	}


	@Override
	public void createScheduleAndAddGroup(List group, List scheduleList, String areaName, String vesselName) {
		// ���� ���� ����� ��ĳ�� �׷� ����
		List<RouteScheduleGroup> validScheduleGroupList = routeJoint.getValidatedScheduleGroupList(areaName,vesselMap.get(vesselName), scheduleList, isAddValidate);

		// ������ ��忡 �߰� 
		validScheduleGroupList.stream().forEach(o -> group.add( (OutbondScheduleTreeNode) makeScheduleNode(areaName,o, scheduleList)));
		
	}
}
