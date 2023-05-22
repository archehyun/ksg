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

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 라우트 스케줄 트리 노드 생성
 * 
 * 정렬 기준 1, 선박, 출발일
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
	 * TODO 도착일 늦은 날짜로 정렬
	 * TODO
	 */
	/**
	 * 지역 - 선박
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
							
							// 선박
							List<OutbondScheduleTreeNode> areaScheduleNodeList = routeJoint.createRouteScheduleGroupList(	vesselList , strArea,this);
							// 출발일 기준으로 정렬
							Collections.sort(areaScheduleNodeList, sortType.equals("date")?dateComparator:vesselComparator);
							
							// 출력			
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
		
		// 출발항 목록
		schedule.add(new PortTreeNode( strFromPorts));

		// 출발항은 늦은 날짜
		schedule.date = group.getDateF();

		schedule.vessel = group.getVesselName();

		// 도착항 목록
		schedule.add(toPort);

		// TODO 출발항 빠른 날짜 기준으로 정렬

		return schedule;

	}


	@Override
	public void createScheduleAndAddGroup(List group, List scheduleList, String areaName, String vesselName) {
		// 예외 사항 적용된 스캐줄 그룹 생성
		List<RouteScheduleGroup> validScheduleGroupList = routeJoint.getValidatedScheduleGroupList(areaName,vesselMap.get(vesselName), scheduleList, isAddValidate);

		// 스케줄 노드에 추가 
		validScheduleGroupList.stream().forEach(o -> group.add( (OutbondScheduleTreeNode) makeScheduleNode(areaName,o, scheduleList)));
		
	}
}
