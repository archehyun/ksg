package com.dtp.api.schedule.joint.tree;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

import com.dtp.api.schedule.comparator.DateComparator;
import com.dtp.api.schedule.joint.print.inbound.InboundScheduleGroup;
import com.dtp.api.schedule.joint.print.inbound.InboundScheduleRule;
import com.dtp.api.schedule.joint.print.route.PortAndDay;
import com.dtp.api.schedule.joint.tree.node.AreaTreeNode;
import com.dtp.api.schedule.joint.tree.node.InboundGroupTreeNode;
import com.dtp.api.schedule.joint.tree.node.NodeType;
import com.dtp.api.schedule.joint.tree.node.PortTreeNode;
import com.dtp.api.schedule.joint.tree.node.ScheduleTreeNode;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.schedule.comp.treenode.InboundCodeMap;

public class InboundNodeManager extends AbstractNodeManager{
	
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
	
	protected DateComparator dateComparator 			= new DateComparator(new SimpleDateFormat("yyyy/MM/dd"));
	
	protected  Map<String, Vessel> vesselMap;
	
	protected  Map<String, PortInfo> portMap;
	
	Map<String, Map<String, List<ScheduleData>>> inboundScheduleMap;
	
	protected SimpleDateFormat inputDateFormat 	= KSGDateUtil.inputDateFormat;

	protected SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();
	
	private InboundScheduleRule inboundScheduleRule;
	
	private InboundCodeMap inboundCodeMap = InboundCodeMap.getInstance();
	
	public DefaultMutableTreeNode getTreeNode2(CommandMap areaList) {

		//inbound port 약어 목록 조회
		
		//=====================================
		//트리 노드 생성
		//=====================================

		DefaultMutableTreeNode root = new AreaTreeNode("AREA");

		for(String strArea: areaList.keySet())		
		{	
			DefaultMutableTreeNode area = new AreaTreeNode(strArea);

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

				String[][] sortedKey = getInboundScheduleKey(vesselitems.keySet().toArray());


				for(String arr[]: sortedKey)
				{
					String str = (String) arr[0];

					String vesselName =str.substring(0,str.indexOf("$$"));

					// 스케줄 목록
					//TODO 스케줄 INBOUND 공동배선 적용 룰 검토
					ArrayList<CommandMap> scheduleList =  (ArrayList<CommandMap>) vesselitems.get(str);

					fromPort.add(new InboundGroupTreeNode(vesselName,scheduleList ));	
				}

				area.add(fromPort);

			}
			
			root.add(area);
		}
		
		return root;

	}
	
	
	public DefaultMutableTreeNode getTreeNode(CommandMap areaList) 
	{	
		DefaultMutableTreeNode root = new AreaTreeNode("AREA");
		
		inboundScheduleMap=(Map<String, Map<String, List<ScheduleData>>>) areaList.get("inboundScheduleMap");
		
		vesselMap=(Map<String, Vessel>) areaList.get("vesselMap");
		
		portMap=(Map<String, PortInfo>) areaList.get("portMap");
		
		inboundScheduleRule = new InboundScheduleRule(vesselMap);
		
		Object[] mapkey = inboundScheduleMap.keySet().toArray();
		
		Arrays.sort(mapkey);
		
		for(Object strFromPort: mapkey)		
		{
			Map<String, List<ScheduleData>> vesselList = inboundScheduleMap.get(strFromPort);
			
			PortInfo portInfo = portMap.get(strFromPort);
			
			DefaultMutableTreeNode port = new PortTreeNode(String.valueOf(strFromPort)+" - "+portInfo.getPort_nationality());
			
			List<InboundScheduleGroup> groupList = inboundScheduleRule.createScheduleGroup(vesselList,false);
			
			groupList.stream()
					.sorted()
					.forEach(group ->{
						
						ScheduleTreeNode child = new ScheduleTreeNode(toPrintString(group),group.getParent()!=null?NodeType.SPLITED_SCHEDULE: NodeType.SCHEDULE);
						
						port.add(child); 
						
						group.getScheduleList() .forEach(item -> {
							
							CommandMap param = objectMapper.convertValue(item, CommandMap.class);
							
							String fromDate = formatedDate(item.getDateF());							
							
							String vessel = item.getVessel();
							
							String company = item.getCompany_abbr();
							
							String portAndDAy = String.format("%s%s",String.format("[%s]", inboundCodeMap.get( item.getPort())), formatedDate(item.getDateF()));
							
							child.add(new ScheduleTreeNode(toFormattedString(fromDate, vessel, company, portAndDAy, false),new TreeTableNode(param), NodeType.SCHEDULE));
						});
					});
			// 항차 번호로 그룹 화
			
			

			root.add(port);
		}
		return root;
	}
	
	
	public String toPrintString(InboundScheduleGroup group) {
		
		String dateF 			=  formatedDate(group.getDateF());
		
		String sortedCompanyist = group.toJointedCompanyString();
		
		List<PortAndDay> list 	= group.getJointedInboundPortList();
		
		StringBuffer jointedInboundPort = new StringBuffer();
		
		list.stream().sorted(dateComparator)
		
		.forEach(o -> jointedInboundPort.append(String.format("%s%s",String.format("[%s]", inboundCodeMap.get( o.getPort())), formatedDate(o.getDateF()))));		
		
		String vesselName = group.getVessel().getVessel_name();

		return toFormattedString(dateF, vesselName, sortedCompanyist, jointedInboundPort.toString(), true);
	}
	
	private String formatedDate(String date)
	{
		try {
			return  outputDateFormat.format(inputDateFormat.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}
	
	public String toFormattedString(String dateF,String vesselName, String company, String ports, boolean taged)
	{	
			return String.format("%-8s %s (%s) %-15s", dateF, vesselName , company, ports);
	}
	

	/**
	 * 
	 * @param vesselAndDatekey
	 * @return inbound sorted keys
	 */
	private String[][] getInboundScheduleKey(Object[] vesselAndDatekey) {

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

				int dayDiff = KSGDateUtil.dayDiff(fromDateOne, fromDateTwo);
				return dayDiff>0?-1:dayDiff==0?-1:1;

			}
		});
		return sortedKey;
	}

}
