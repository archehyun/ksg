package com.ksg.workbench.common.comp.treetable.nodemager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dtp.api.schedule.comparator.DateComparator;
import com.dtp.api.schedule.comparator.VesselComparator;
import com.dtp.api.schedule.joint.RouteJoint;
import com.dtp.api.schedule.joint.RouteJointSubject;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.joint.route.RouteScheduleGroup;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.NodeType;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;

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
	
	private RouteJoint joint;

	private DateComparator dateComparator 			= new DateComparator(new SimpleDateFormat("yyyy/MM/dd"));

	private VesselComparator vesselComparator 		= new VesselComparator();
	
	private boolean diviedByAreaGap = true;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	boolean isAddValidate = false;
	
	public RouteNodeManager()
	{
		super();
		joint = new RouteJoint(this);
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

		CommandMap areaList=(CommandMap) param.get("data"); 

		String sortType = (String) param.get("sortType");
		
		isAddValidate = (boolean) param.get("isAddValidate");

		Set<String> areaKeySet=areaList.keySet();

		areaKeySet.stream().sorted();

		Object[] areaKeyList = areaList.keySet().toArray();
		
		Arrays.sort(areaKeyList);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("area");
		
		// ����
		for(Object strArea:areaKeyList)
		{
			HashMap<String, Object> vesselList =  (HashMap<String, Object>) areaList.get(strArea);
			
			logger.info("Area: "+strArea+ ", ������ �׷� ������:"+vesselList.keySet().size());
			
			// ����
			Object[] vesselArray = vesselList.keySet().toArray();
			
			List<OutbondScheduleTreeNode> areaScheduleNodeList = new ArrayList<OutbondScheduleTreeNode>();

			for (Object vesselKey : vesselArray)
			{	
				// �����ȸ
				List<ScheduleData> scheduleList = (List<ScheduleData>) vesselList.get(vesselKey);
				
				// ������ȣ(����)�� �׷�ȭ
				Map<Integer, List<ScheduleData>> voyageList =  scheduleList.stream().collect(
						Collectors.groupingBy(o -> ScheduleBuildUtil. getNumericVoyage(o.getVoyage_num()) ));// ����

				// ���� ��ȣ�� ����
				Object[] voyageArray = voyageList.keySet().toArray();

				Arrays.sort(voyageArray);
				
				for(Object voyagekey:voyageArray)
				{
					List<ScheduleData> subscheduleList = voyageList.get(voyagekey);
					
					joint.createScheduleAndAddGroup(areaScheduleNodeList, subscheduleList, (String)strArea, (String)vesselKey);

				}
			}
			// ����� �������� ����
			Collections.sort(areaScheduleNodeList, sortType.equals("date")?dateComparator:vesselComparator);
			
			// ���			
			int count =(int) areaScheduleNodeList.stream().filter(o ->isAddValidate?true:!o.getType().equals(NodeType.JOINT_SCHEDULE)).count();
			
			if(count==0) continue;
			
			DefaultMutableTreeNode area = new AreaTreeNode(String.format("%s(%d)", strArea, count));

			areaScheduleNodeList.stream()
								.filter(o -> isAddValidate?true:!o.getType().equals(NodeType.JOINT_SCHEDULE))
								.forEach(o ->area.add(o));
			
			root.add(area);
		}

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
		
		String vesselName 	= group.getVessel();
		
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
		schedule.date = group.getDate();

		schedule.vessel = group.getVessel();

		// ������ ���
		schedule.add(toPort);

		// TODO ����� ���� ��¥ �������� ����

		return schedule;

	}


	@Override
	public void createScheduleAndAddGroup(List group, List scheduleList, String areaName, String vesselName) {
		// ���� ���� ����� ��ĳ�� �׷� ����
		List<RouteScheduleGroup> validScheduleGroupList = joint.getValidatedScheduleGroupList(areaName,vesselName, scheduleList, isAddValidate);

		// ������ ��忡 �߰� 
		validScheduleGroupList.stream().forEach(o -> group.add( (OutbondScheduleTreeNode) makeScheduleNode(areaName,o, scheduleList)));
		
	}



	
}
