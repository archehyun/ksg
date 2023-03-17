package com.ksg.schedule.execute.joint.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.execute.formater.InboundPrintSchedule;
import com.ksg.schedule.execute.formater.TagedPrintSchedule;
import com.ksg.schedule.execute.joint.JointSchedule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InboundScheduleJoint extends JointSchedule{

	
	public InboundScheduleJoint(String date_isusse) {
		super(date_isusse);
	}
	
	
	/**
	 * 스케줄을 지역, 출발항(외국항) 기준으로 그룹화
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public Map<String, Map<String, List<ScheduleData>>> buildOutboundScheduleGroup(List<ScheduleData> result) throws Exception
	{

		Map<String, Map<String, List<ScheduleData>>> areaList =  result.stream().collect(
				Collectors.groupingBy(ScheduleData::getArea_name, // 지역
						Collectors.groupingBy(ScheduleData::getFromPort)));// 출발항


		return areaList;
	}
	
	/**그룹화된 스케줄을 리스트로 변환, 공동배선 적용
	 * 
	 * 생성 기준 : 
	 * 출발항
	 * 		도착항
	 * 			선박
	 * 
	 * @param areaMap
	 * @return
	 */
	public ScheduleList buildSortedAndJointedScheduleList2(Map map)  throws Exception	
	{
		ScheduleList areaLists = new ScheduleList(date);

		Iterator<String> areaIter= (Iterator<String>) map.keySet().iterator();

		while(areaIter.hasNext())
		{
			String area_name = areaIter.next();

			HashMap<String, Object> toitem= (HashMap<String, Object>) map.get(area_name);

			Iterator<String> toPortite= (Iterator<String>) toitem.keySet().iterator();
			
			log.debug("area_name:{}",area_name);
			ScheduleList toPortList = new ScheduleList(area_name);

			//TODO 정렬 : 출발항(외국항) 정렬 추가
			while(toPortite.hasNext())
			{
				String toPort = toPortite.next();

				log.debug("to_port:{}",toPort);				
				
				// 공동 배선이 적용된 스케줄 리스트 생성
				List<JointedSchehduleData> scheduleItems = getJointScheduleList((List)  toitem.get(toPort));

				ScheduleList scheduleList = new ScheduleList(toPort);

				for(JointedSchehduleData temp:scheduleItems)
				{	
					scheduleList.add(temp);
				}
				
				// 출발일 정렬 추가
				Collections.sort(scheduleList, new AscendingFromDate() );

				toPortList.add(scheduleList);
				
			}
			areaLists.add(toPortList);
		}
		return areaLists;
	}



	/**공동배선 적용 리스트
	 * 선박명, 항차명이 같은 경우
	 * 동배선으로 묶였을 경우 출발일은 빠른 날짜, 도착일은 늦은 날짜로 지정
	 * @param list
	 * @return
	 * @throws SQLException 
	 */
	private List getJointScheduleList(List<ScheduleData> list) throws SQLException
	{
		// 선박명으로 group by
		Map<String, List<ScheduleData>> result= list.stream().collect(Collectors.groupingBy(ScheduleData::getVessel));

		Iterator<String> keys= result.keySet().iterator();

		ArrayList<JointedSchehduleData> list2 = new ArrayList<JointedSchehduleData>();

		while(keys.hasNext())
		{
			String vesselName = keys.next();
			try {
				//TODO NULL VESSEL 처리 방법
				Vessel vesselItem=getVesselInfo(vesselName);

				List<ScheduleData> items=result.get(vesselName);

				JointedSchehduleData group = new JointedSchehduleData();

				//group.vesselType = vesselItem.getVessel_type();

				for(ScheduleData sc:items)
				{
					group.addScheduleData(sc);
				}

				list2.add(group);

//				if(group.size()>1)
//
//					System.out.println("\t\t\tvesselName:"+vesselName+","+group.size());
			}catch(ResourceNotFoundException e)
			{
				System.err.println("null vessel:"+vesselName+", e:"+e.getMessage());
			}
		}

		return list2;
	}


	@Override
	public int execute() {
		log.info("inbound joint start");
		CommandMap param = new CommandMap();
		
		param.put("inOutType", "I");
		
		param.put("date_issue", this.date);
		try {
		List<ScheduleData> result= scheduleService.selecteScheduleListByCondition(param);
		
		Map<String, Map<String, List<ScheduleData>>> areaMap = buildOutboundScheduleGroup(result);
		
		ScheduleList areaLists =  buildSortedAndJointedScheduleList2(areaMap);
		
		new InboundPrintSchedule(areaLists).print();
		
		log.info("inbound joint end:{}",areaLists.size());
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		return 0;
	}
	/** 날짜 기준 정렬
	
	  * @FileName : OutboundScheduleJoint.java
	
	  * @Project : KSG2
	
	  * @Date : 2022. 6. 8. 
	
	  * @작성자 : pch
	
	  * @변경이력 :
	
	  * @프로그램 설명 :
	
	  */
	class AscendingFromDate implements Comparator<JointedSchehduleData> 
	{ 
		@Override 
		public int compare(JointedSchehduleData one, JointedSchehduleData two) {

			String fromDateOne = one.getFromDate();
			String fromDateTwo = two.getFromDate();

			return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

		} 
	}

}
