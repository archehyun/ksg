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

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� :
 */
public class OutboundNodeManager extends AbstractNodeManager{
	
	public OutboundNodeManager()
	{
		super();
	}
	
	/**
	 * 
	 * ������ ����Ʈ�� �������� outbound Ʈ�� ��� ����
	 * ������ -> ����� -> �����ٱ׷� -> ������ or ������ -> ����� -> ������   
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

			// ������ ����
			Object[] toPortArray = toPortItems.keySet().toArray();

			Arrays.sort(toPortArray);

			for (Object toPortKey : toPortArray)
			{
				// �����ȸ
				HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) toPortItems.get(toPortKey);

				//tree ��� ����
				DefaultMutableTreeNode toPort = new PortTreeNode((String)toPortKey);

				// ����� ����
				List<Entry<String, Object>> list_entries = new ArrayList<Entry<String, Object>>(fromPortitems.entrySet());

				// ����� ���

				for(String fromPortKey:fromPortitems.keySet())
				{
					DefaultMutableTreeNode fromPort = new PortTreeNode(fromPortKey);

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
		HashMap<String, SortedScheduleGroup> scheduleList = new HashMap<String,SortedScheduleGroup>();


		//====================================
		// ���ڸ�-������ȣ �׷� ����
		//
		// TODO �����輱���� ���� �� 3�� �̳���
		//====================================
		for(ScheduleData scheduleItem:schedule)
		{	
			//convert hashMap
			SortedSchedule scheduleMap=new SortedSchedule(scheduleItem);

			// ���ڸ�, ���� ��ȣ�� Ű ����
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
		//��¥ �������� ���� ����
		//====================================
		Collection<SortedScheduleGroup> values = scheduleList.values();

		List<SortedScheduleGroup> list = new ArrayList<SortedScheduleGroup>();

		list.addAll(values);

		Collections.sort(list);


		//=====================================
		//Ʈ�� ��� ����
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

				//treetable ��� �߰�
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


		// ����� ���
		scheduleList.stream().forEach(o -> list1.add(o.getData()));

		List<String> dateFList=list1.stream().map(ScheduleData::getDateF)

									.collect(Collectors.toList());

		List<String> dateTList=list1.stream().map(ScheduleData::getDateT)

									.collect(Collectors.toList());

		List<String> companyAbbrList=list1.stream().map(ScheduleData::getCompany_abbr).distinct()

										  .collect(Collectors.toList());

		// ����� ����(���� ����)
		Collections.sort(dateFList);

		// ������ ����(���� ����)
		Collections.sort(dateTList, Collections.reverseOrder());

		// ����� ����(���� ����)
		Collections.sort(companyAbbrList);		

		// �����
		String dateF =KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(dateFList.get(0));

		// ������
		String dateT = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(dateTList.get(0));		

		// TODO ������ - ��ǥ ����� ����
		String re_company_abbr = "";

		// ����� ��� (�����1, �����2, ...)
		String company_abbr = StringUtils.join(companyAbbrList.toArray(new String[companyAbbrList.size()]),",");



		return String.format("%s %s (%s) %s " , dateF, vessel, company_abbr, dateT);
	}
	

	/**
	 * 

	 * @FileName : TreeNodeManager.java

	 * @Project : KSG2

	 * @Date : 2022. 12. 7. 

	 * @�ۼ��� : pch

	 * @�����̷� :

	 * @���α׷� ���� :
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
