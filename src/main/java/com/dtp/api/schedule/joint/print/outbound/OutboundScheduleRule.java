package com.dtp.api.schedule.joint.print.outbound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;
import com.ksg.workbench.schedule.comp.treenode.SortedSchedule;

/**

  * @FileName : OutboundSchedule.java

  * @Project : KSG2

  * @Date : 2023. 4. 10. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 아웃 바운드 룰 정의

  */
public class OutboundScheduleRule {
	
	private Map<String, Vessel> vesselMap;
	
	private static final int DIVIED_SCHEDULE = 3;
	
	public OutboundScheduleRule(Map<String, Vessel> vesselMap) {
		this.vesselMap =vesselMap;
	}

	/**
	 * 스케줄 분리
	 * @param jointScheduleItemList
	 * @return
	 */
	public List<OutboundScheduleGroup> getSplitedOutboundScheduleGroupNode(OutboundScheduleGroup jointScheduleItemList)
	{
		ArrayList<Integer> indexList = getSplitedScheduleIndex(jointScheduleItemList);
		
		List<OutboundScheduleGroup> sortedOutboundScheduleGroupList = new ArrayList<>();
		// 인덱스 리스트 기준으로 새로운 리스트 생성
		List<ArrayList<SortedSchedule>> partitions 	= new ArrayList<>();
		
		for(int index =0,startIndex=0;index<indexList.size();index++)
		{
			int splitIndex 	= indexList.get(index);

			partitions.add(new ArrayList<>(jointScheduleItemList.scheduleList.subList(startIndex,splitIndex)));

			startIndex = splitIndex;

			if(index==indexList.size()-1) partitions.add(new ArrayList<>(jointScheduleItemList.scheduleList.subList(startIndex,jointScheduleItemList.scheduleList.size())));
		}
		
		partitions.forEach(o -> {
			try {
				sortedOutboundScheduleGroupList.add(new OutboundScheduleGroup(jointScheduleItemList, (ArrayList) o));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		if(partitions.isEmpty()) sortedOutboundScheduleGroupList.add(jointScheduleItemList);
		
		return sortedOutboundScheduleGroupList;
	}
	
	/**
	 * @param jointScheduleItemList
	 * @return
	 */
	private ArrayList<Integer> getSplitedScheduleIndex(OutboundScheduleGroup jointScheduleItemList) {
		// 공동배선 3이상 스케줄 분할
		ArrayList<Integer> indexList = new ArrayList<Integer>();

		// 3일 차이 나는 인덱스 조회
		for(int i=0;i<jointScheduleItemList.scheduleList.size()-1;i++)
		{
			SortedSchedule sc1 = jointScheduleItemList.scheduleList.get(i);
			
			SortedSchedule sc2 = jointScheduleItemList.scheduleList.get(i+1);
			
			String firstFromDate 	= sc1.getData().getDateF();
			
			String secondFromDate 	= sc2.getData().getDateF();
			
			//3일 이상
			if(!KSGDateUtil.isDayUnder(DIVIED_SCHEDULE,firstFromDate,secondFromDate))
			{
				indexList.add(i+1);
			}
		}
		return indexList;
	}
	
	/**
	 * @param scheduleList
	 * @param scheduleMap
	 * @param scheduleKey
	 */
	public void addExitOutboundScheduleGroup(HashMap<String, OutboundScheduleGroup> scheduleList, SortedSchedule scheduleMap,
			String scheduleKey) {
		OutboundScheduleGroup jointScheduleItemList = (OutboundScheduleGroup) scheduleList.get(scheduleKey);

		jointScheduleItemList.add(scheduleMap);
	}

	/**
	 * @param scheduleList
	 * @param scheduleMap
	 * @param scheduleKey
	 * @throws Exception 
	 */
	public void addNewOutboundScheduleGroup(HashMap<String, OutboundScheduleGroup> scheduleList, SortedSchedule scheduleMap,
			String scheduleKey)  {
		
		try {
		OutboundScheduleGroup jointScheduleItemList = new OutboundScheduleGroup(this.vesselMap.get(scheduleMap.getData().getVessel()));
		
		jointScheduleItemList.add(scheduleMap);

		scheduleList.put(scheduleKey, jointScheduleItemList);
		}catch(Exception e)
		{
			System.out.println(scheduleMap.getData().getVessel());
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public String getOutboundScheduleKey(SortedSchedule map)
	{
		String vessel 	= map.getData().getVessel();

		int n_voyage 	= ScheduleBuildUtil.getNumericVoyage( map.getData().getVoyage_num());

		return String.format("%s-%s", vessel, n_voyage);
	}
	
	/**
	 * outbound공동배선 적용 
	 * @param schedule
	 * @return
	 * @throws Exception 
	 */
	public List<OutboundScheduleGroup> createFromPortOutboundScheduleGroup(List<ScheduleData> schedule)
	{
		HashMap<String, OutboundScheduleGroup> scheduleList = new HashMap<String,OutboundScheduleGroup>();
		
		//====================================
		// 선박명-항차번호 그룹 생성
		//
		//
		//====================================
		for(ScheduleData scheduleItem:schedule)
		{	
			//convert hashMap
			SortedSchedule scheduleMap=new SortedSchedule(scheduleItem);

			// 선박명, 항차 번호로 키 생성
			String scheduleKey = getOutboundScheduleKey(scheduleMap);

			if(scheduleList.containsKey(scheduleKey))
			{	
				addExitOutboundScheduleGroup(scheduleList, scheduleMap, scheduleKey);
			}
			else
			{
				addNewOutboundScheduleGroup(scheduleList, scheduleMap, scheduleKey);
			}
		}

		//====================================
		//날짜 기준으로 정렬 수행
		//====================================

		List<OutboundScheduleGroup> list 			= new ArrayList<OutboundScheduleGroup>(scheduleList.values());

		List<OutboundScheduleGroup> fillteredList 	= list.stream().filter(o -> o.scheduleList.size()>0)
																	.collect(Collectors.toList());
		
		ArrayList<OutboundScheduleGroup> outboundScheduleGroupList 	= new ArrayList<OutboundScheduleGroup>();

		fillteredList.forEach(scheduleGroup -> outboundScheduleGroupList.addAll( getSplitedOutboundScheduleGroupNode(scheduleGroup)));
		
		
		outboundScheduleGroupList.forEach(o -> o.joinnted());
		
		return outboundScheduleGroupList;
	}
	
	/**출발항구 정렬
	 * @param outboundFromPortArray
	 * @return
	 */
	private String[] arrangeFromPort(String[]arrangeFromPortArray, String[] outboundFromPortArray) 
	{	
		List<String> arragedFromPortList =Arrays.asList(arrangeFromPortArray);
		
		List<String> fromPortList =Arrays.asList(outboundFromPortArray);
		
		for(int i=0;i<arrangeFromPortArray.length;i++)
		{
			// 국내항 항목이 없으면
			if(!fromPortList.contains(arrangeFromPortArray[i]))
			{
				//정렬된 항국 목록 에서 제외
				arragedFromPortList.remove(arrangeFromPortArray[i]);
			}
		}
		String[] newArray = arragedFromPortList.toArray(new String[arragedFromPortList.size()]);
		
		
		return newArray;
	}
	
	public Map<String, Map<String, List<ScheduleData>>> groupedOutboundSchedule(List<ScheduleData> scheduleList) {
		
		return this.groupedOutboundSchedule(false, scheduleList);
	}
	
	/**
	 * 
	 * @param newBusanMerge 부산 신항 공동 배선 유무
	 * @param scheduleList
	 * @return
	 */
	public Map<String, Map<String, List<ScheduleData>>> groupedOutboundSchedule(boolean newBusanMerge, List<ScheduleData> scheduleList) {
		
		scheduleList.stream()
					.forEach(schedule1 -> schedule1.setConvertedFromPort(schedule1.getFromPort()));
		
		scheduleList.stream()
					.filter(schedule-> "BUSAN NEW".equals(schedule.getFromPort()))
					.forEach(schedule -> {if(!newBusanMerge) schedule.setConvertedFromPort("BUSAN");});
		
		Map<String, Map<String, List<ScheduleData>>> toPortList =  scheduleList.stream().collect(
				Collectors.groupingBy(ScheduleData::getPort, // 도착항
						Collectors.groupingBy(ScheduleData::getConvertedFromPort)));// 출발항
		

		return toPortList;
	}


}
