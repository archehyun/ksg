package com.dtp.api.schedule.joint.print.inbound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.KSGViewUtil;

/**
 * 
 * @FileName : InboundScheduleRule.java
 * @Project : KSG2
 * @Date : 2023. 5. 16
 * @작성자 : ch.park
 * @변경이력 :
 * @프로그램 설명 :
 */
public class InboundScheduleRule {
	
	private KSGViewUtil propeties = KSGViewUtil.getInstance();
	
	private int DIVIED_SCHEDULE = 3;
	
	private Map<String, Vessel> vesselMap;
	
	public InboundScheduleRule(Map<String, Vessel> vesselMap)
	{	
		DIVIED_SCHEDULE=Integer.parseInt(propeties.getProperty("schedule.inbound.dividedate"));
		
		this.vesselMap = vesselMap;
	}
	
	/**
	 * 
	 * @param list
	 * @param printTag
	 * @return
	 */
	public List<InboundScheduleGroup> createScheduleGroup(Map<String,List<ScheduleData>> list, boolean printTag)
	{	
		ArrayList<InboundScheduleGroup> groupList = new ArrayList<InboundScheduleGroup>();
		
		for(Object vessel: list.keySet())		
		{
			List<ScheduleData> li =list.get(vessel);
					
			Map<Integer, Map<String, List<ScheduleData>>> vesselScheduleMap = li.stream().collect(
					Collectors.groupingBy(ScheduleData::getIntVoyage_num, // 지역
							Collectors.groupingBy(ScheduleData::getVessel)));
			
			for(Object key:vesselScheduleMap.keySet())
			{
				Map<String, List<ScheduleData>> scheduleList = vesselScheduleMap.get(key);
				
				groupList.addAll(
				scheduleList.entrySet().stream()
										.map(o -> new InboundScheduleGroup(vesselMap.get( o.getKey()), o.getValue(),printTag))
										.collect(Collectors.toList()));
			}
		}		
		
		// 스케줄 필터
		List<InboundScheduleGroup> fillteredList 	= groupList.stream()
																.filter(o -> o.getScheduleList().size()>0)
																.collect(Collectors.toList());
		
		ArrayList<InboundScheduleGroup> inboundScheduleGroupList 	= new ArrayList<InboundScheduleGroup>();

		fillteredList.forEach(scheduleGroup -> inboundScheduleGroupList.addAll( getSplitedInboundScheduleGroupNode(scheduleGroup)));
		
		return inboundScheduleGroupList;
	}
	
	/**
	 * 스케줄 분리
	 * @param jointScheduleItemList
	 * @return
	 */
	public List<InboundScheduleGroup> getSplitedInboundScheduleGroupNode(InboundScheduleGroup jointScheduleItemList)
	{
		ArrayList<Integer> indexList = getSplitedScheduleIndex(jointScheduleItemList);

		List<InboundScheduleGroup> sortedInboundScheduleGroupList = new ArrayList<>();
		// 인덱스 리스트 기준으로 새로운 리스트 생성
		List<ArrayList<ScheduleData>> partitions 	= new ArrayList<>();

		for(int index =0,startIndex=0;index<indexList.size();index++)
		{
			int splitIndex 	= indexList.get(index);
			
			List<ScheduleData> scheduleList = jointScheduleItemList.getScheduleList();

			partitions.add(new ArrayList<>(scheduleList.subList(startIndex,splitIndex)));

			startIndex = splitIndex;

			if(index==indexList.size()-1) partitions.add(new ArrayList<>(scheduleList.subList(startIndex,scheduleList.size())));
		}

		partitions.forEach(o -> {
			try {
				sortedInboundScheduleGroupList.add(new InboundScheduleGroup(jointScheduleItemList, (ArrayList) o));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		if(partitions.isEmpty()) sortedInboundScheduleGroupList.add(jointScheduleItemList);

		return sortedInboundScheduleGroupList;
	}
	
	/**
	 * 
	 * 공동배선 3이상 스케줄 분할
	 * @param jointScheduleItemList
	 * @return
	 */
	private ArrayList<Integer> getSplitedScheduleIndex(InboundScheduleGroup jointScheduleItemList) {
		// 
		ArrayList<Integer> indexList 		= new ArrayList<Integer>();
		
		List<ScheduleData> scheduleList  	= jointScheduleItemList.getScheduleList();

		// 3일 차이 나는 인덱스 조회
		for(int i=0;i<scheduleList.size()-1;i++)
		{
			ScheduleData sc1 		= scheduleList.get(i);

			ScheduleData sc2 		= scheduleList.get(i+1);

			String firstFromDate 	= sc1.getDateF();

			String secondFromDate 	= sc2.getDateF();

			//3일 이상, 2일까지 허용
			if(!KSGDateUtil.isDayUnder(DIVIED_SCHEDULE,firstFromDate,secondFromDate))
			{
				indexList.add(i+1);
			}
		}
		return indexList;
	}
}
