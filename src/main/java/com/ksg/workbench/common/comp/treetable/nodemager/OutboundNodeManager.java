package com.ksg.workbench.common.comp.treetable.nodemager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang3.StringUtils;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.NodeType;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;
import com.ksg.workbench.common.comp.treetable.node.ScheduleTreeNode;
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
	
	private static final int DIVIED_SCHEDULE = 3;
	
	private static SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMdd");


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

		Set<String> areaKeySet=areaList.keySet();

		areaKeySet.stream().sorted();

		Object[] mapkey = areaList.keySet().toArray();

		Arrays.sort(mapkey);


		for(Object strArea:mapkey)
		{
			HashMap<String, Object> toPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			DefaultMutableTreeNode scheduleAreaTreeNode = new AreaTreeNode((String) strArea);

			// 도착항 정렬
			Object[] toPortArray = toPortItems.keySet().toArray();

			Arrays.sort(toPortArray);

			for (Object toPortKey : toPortArray)
			{
				DefaultMutableTreeNode toPort = createToPortScheduleGroup(toPortKey, (HashMap<String, Object>) toPortItems.get(toPortKey));

				scheduleAreaTreeNode.add(toPort);				  
			}

			root.add(scheduleAreaTreeNode);

		}

		return root;
	}

	private DefaultMutableTreeNode createToPortScheduleGroup(Object toPortKey, HashMap<String, Object> fromPortitems) {
		//tree 노드 생성
		DefaultMutableTreeNode toPort = new PortTreeNode((String)toPortKey);

		for(String fromPortKey:fromPortitems.keySet())
		{
			List<ScheduleData> schedule = (List) fromPortitems.get(fromPortKey);

			DefaultMutableTreeNode fromPort = new PortTreeNode(fromPortKey);

			ArrayList<ScheduleGroup> list = (ArrayList<ScheduleGroup>) createFromPortScheduleGroup(schedule);

			ArrayList<DefaultMutableTreeNode> nodeList = createTreeNode(list);

			nodeList.forEach(scheduleNode ->fromPort.add(scheduleNode ));

			// 도착항 그룹에 추가
			toPort.add(fromPort);
		}
		return toPort;
	}

	/**
	 * outbound공동배선 적용 
	 * @param schedule
	 * @return
	 */
	private List<ScheduleGroup> createFromPortScheduleGroup(List<ScheduleData> schedule)
	{
		HashMap<String, ScheduleGroup> scheduleList = new HashMap<String,ScheduleGroup>();


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
				addExitScheduleGroup(scheduleList, scheduleMap, scheduleKey);
			}
			else
			{
				addNewScheduleGroup(scheduleList, scheduleMap, scheduleKey);
			}
		}

		//====================================
		//날짜 기준으로 정렬 수행
		//====================================

		List<ScheduleGroup> list = new ArrayList<ScheduleGroup>(scheduleList.values());

//		list.addAll(values);
		
		List<ScheduleGroup> fillteredList = list.stream().filter(o -> o.scheduleList.size()>0).collect(Collectors.toList());
		
		ArrayList<ScheduleGroup> newlist = new ArrayList<ScheduleGroup>();
		
		for(ScheduleGroup jointScheduleItemList:list)
		{	
			List<ScheduleGroup>  li = getSplitedScheduleGroupNode(jointScheduleItemList);
			
			newlist.addAll(li);
		}

		Collections.sort(newlist);

		return newlist;
	}

	private void addExitScheduleGroup(HashMap<String, ScheduleGroup> scheduleList, SortedSchedule scheduleMap,
			String scheduleKey) {
		ScheduleGroup jointScheduleItemList = (ScheduleGroup) scheduleList.get(scheduleKey);

		jointScheduleItemList.add(scheduleMap);
	}

	private void addNewScheduleGroup(HashMap<String, ScheduleGroup> scheduleList, SortedSchedule scheduleMap,
			String scheduleKey) {
		ScheduleGroup jointScheduleItemList = new ScheduleGroup();

		jointScheduleItemList.setVesselName(scheduleMap.getData().getVessel());

		jointScheduleItemList.add(scheduleMap);

		scheduleList.put(scheduleKey, jointScheduleItemList);
	}

	private ArrayList<DefaultMutableTreeNode> createTreeNode(List<ScheduleGroup> list) {

		ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();

		list.forEach(o -> nodeList.add(o.isMulti()? createJointScheduleGroupNode(o):createScheduleNode(o)));
		
		return nodeList;
	}

	private OutbondScheduleTreeNode createScheduleNode(ScheduleGroup jointScheduleItemList) {

		CommandMap item = objectMapper.convertValue(jointScheduleItemList.scheduleList.get(0).getData(), CommandMap.class);

		return new OutbondScheduleTreeNode(new TreeTableNode(item), jointScheduleItemList.parent!=null?NodeType.SPLITED_SCHEDULE: NodeType.SCHEDULE);
	}
	private List<ScheduleGroup> getSplitedScheduleGroupNode(ScheduleGroup jointScheduleItemList)
	{
		// 공동배선 3이상 스케줄 분할
		ArrayList<Integer> indexList = new ArrayList<Integer>();

		// 3일 차이 나는 인덱스 조회
		for(int i=0;i<jointScheduleItemList.scheduleList.size()-1;i++)
		{
			SortedSchedule sc1 = jointScheduleItemList.scheduleList.get(i);
			SortedSchedule sc2 = jointScheduleItemList.scheduleList.get(i+1);
			String firstFromDate = sc1.getData().getDateF();
			String secondFromDate = sc2.getData().getDateF();
			//3일 이상
			if(!KSGDateUtil.isDayUnder(DIVIED_SCHEDULE,firstFromDate,secondFromDate))
			{
				indexList.add(i+1);
			}
		}
		List<ScheduleGroup> sortedScheduleGroupList = new ArrayList<>();
		// 인덱스 리스트 기준으로 새로운 리스트 생성
		List<ArrayList<SortedSchedule>> partitions = new ArrayList<>();
		
		for(int index =0,startIndex=0;index<indexList.size();index++)
		{
			int splitIndex 	= indexList.get(index);

			partitions.add(new ArrayList<>(jointScheduleItemList.scheduleList.subList(startIndex,splitIndex)));

			startIndex = splitIndex;

			if(index==indexList.size()-1) partitions.add(new ArrayList<>(jointScheduleItemList.scheduleList.subList(startIndex,jointScheduleItemList.scheduleList.size())));
		}
		
		partitions.forEach(o -> sortedScheduleGroupList.add(new ScheduleGroup(jointScheduleItemList, (ArrayList) o)));
		
		if(partitions.isEmpty()) sortedScheduleGroupList.add(jointScheduleItemList);
		
		sortedScheduleGroupList.forEach(o -> o.joinnted());
		
		
		return sortedScheduleGroupList;
	}
	private DefaultMutableTreeNode createJointScheduleGroupNode(ScheduleGroup jointScheduleItemList) {

		DefaultMutableTreeNode node = new ScheduleTreeNode(jointScheduleItemList.toJointedOutboundScheduleString(), jointScheduleItemList.parent==null?NodeType.JOINT_SCHEDULE:NodeType.SPLITED_SCHEDULE);

		jointScheduleItemList.scheduleList.forEach(item -> node.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(item.getData(), CommandMap.class)))));

		return node;
	}
	
	private DefaultMutableTreeNode createSplitedScheduleGroupNode(ScheduleGroup jointScheduleItemList) {

		DefaultMutableTreeNode node = new ScheduleTreeNode(jointScheduleItemList.toJointedOutboundScheduleString(),NodeType.SPLITED_SCHEDULE);

		jointScheduleItemList.scheduleList.forEach(item -> node.add(new OutbondScheduleTreeNode(new TreeTableNode(objectMapper.convertValue(item.getData(), CommandMap.class)))));

		return node;
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
	public String toJointedOutboundScheduleString(ScheduleGroup jointScheduleItemList)	{

		String vesselName = jointScheduleItemList.getVesselName();

		ArrayList<ScheduleData> list1 = new ArrayList<ScheduleData>();
		
		List<SortedSchedule> scheduleList= jointScheduleItemList.scheduleList;

		// 출발일 목록
		scheduleList.stream().forEach(o -> list1.add(o.getData()));

		List<String> dateFList=list1.stream().map(ScheduleData::getDateF)

				.collect(Collectors.toList());

		List<String> dateTList=list1.stream().map(ScheduleData::getDateT)

				.collect(Collectors.toList());

		List<String> companyAbbrList=list1.stream().map(ScheduleData::getCompany_abbr).distinct()

				.collect(Collectors.toList());

		// 출발일 정렬(늦은 날짜)
		Collections.sort(dateFList, Collections.reverseOrder());

		// 도착일 정렬(빠른 날짜)
		Collections.sort(dateTList);

		// 선사명 정렬(오름 차순)
		Collections.sort(companyAbbrList);		

		// 출발일
		String dateF =KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(dateFList.get(0));

		// 도착일
		String dateT = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(dateTList.get(0));		

		// TODO 스케줄 - 대표 선사명 지정
		String re_company_abbr = "";

		// 선사명 양식 (선사명1, 선사명2, ...)
		String company_abbr = StringUtils.join(companyAbbrList.toArray(new String[companyAbbrList.size()]),",");



		return String.format("%s %s (%s) %s " , dateF, vesselName, company_abbr, dateT);
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

	class ScheduleGroup implements Comparable<ScheduleGroup>
	{
		
		
		
		private ScheduleGroup parent;
		
		private boolean isJointted=false;
		
		
		private String jointedDateF;
		private String jointedDateT;
		
		private String jointed_company_abbr;
		
		public void setParent(ScheduleGroup parent)
		
		{
			this.parent = parent;
		}
		
		
		
		public String getDateF()
		{
			return isJointted?jointedDateF:scheduleList.get(0).getData().getDateF();
		}
		public String getForrmatedDate(String date)
		{
			try {
				return outputDateFormat.format(inputDateFormat.parse(String.valueOf(date)));
			} catch (ParseException e) {
				return date;
			}
		}
		private void joinnted()
		{
			ArrayList<ScheduleData> list1 = new ArrayList<ScheduleData>();
			// 출발일 목록
			scheduleList.stream().forEach(o -> list1.add(o.getData()));

			Optional<String> dateF=list1.stream().map( o -> getForrmatedDate(o.getDateF()))
					// 출발일 정렬(늦은 날짜)
					.sorted(Collections.reverseOrder()) 
					.findFirst();

			Optional<String> dateT=list1.stream().map(o -> getForrmatedDate(o.getDateT()))
					 // 도착일 정렬(빠른 날짜)
					.sorted()
					.findFirst();


			List<String> companyAbbrList=list1.stream().map(ScheduleData::getCompany_abbr).distinct()
					.sorted()// 오름 차순
					.collect(Collectors.toList());

			


			// 선사명 정렬(오름 차순)
//			Collections.sort(companyAbbrList);		


			
			// 출발일
			this.jointedDateF=KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD(dateF.get());

			// 도착일
			this.jointedDateT = KSGDateUtil.convertDateFormatYYYY_MM_DDToMMDD(dateT.get());		

			// TODO 스케줄 - 대표 선사명 지정
			String re_company_abbr = "";

			// 선사명 양식 (선사명1, 선사명2, ...)
			jointed_company_abbr = StringUtils.join(companyAbbrList.toArray(new String[companyAbbrList.size()]),",");
		}
		
		
		private String vessel;

		private  SimpleDateFormat formatYYYYMMDD = KSGDateUtil.createInputDateFormat();

		public ArrayList<SortedSchedule> scheduleList;

		public ScheduleGroup()
		{
			scheduleList = new ArrayList<SortedSchedule>();
		}
		
		
		public boolean isMulti()
		{
			return scheduleList.size()>1;
		}
		public ScheduleGroup(ScheduleGroup parent, ArrayList scheduleList)
		{
			this.vessel = parent.getVesselName();
			
			this.scheduleList  = scheduleList;
			this.parent = parent;
			
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
		public int compareTo(ScheduleGroup o) {
			try {

				return formatYYYYMMDD.parse(getDateF()).compareTo(formatYYYYMMDD.parse(o.getDateF()));
			} catch (ParseException e) {
				return 0;
			}
		}
		
		/**
		 * 
		 * @param scheduleList
		 * @return
		 */
		public String toJointedOutboundScheduleString()	{

			return String.format("%s %s (%s) %s " , this.jointedDateF, getVesselName(), jointed_company_abbr, jointedDateT);
		}
	}
	



}
