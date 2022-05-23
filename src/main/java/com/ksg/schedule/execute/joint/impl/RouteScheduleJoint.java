package com.ksg.schedule.execute.joint.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.domain.ScheduleData;
import com.ksg.schedule.execute.joint.JointSchedule;

import lombok.extern.slf4j.Slf4j;

/**
 * 그룹핑
 * 지역 -> 선박
 */
@Slf4j
public class RouteScheduleJoint extends JointSchedule{
	
	
	public RouteScheduleJoint(String date)
	{
		super(date);
	}
	

	@Override
	public int execute() {
		
		log.info("inbound joint start");
		HashMap<String, Object> param = new HashMap<String, Object>();

		
		param.put("inOutType", "O");
		param.put("date_issue", this.date);

	
		
		try {
			
			List<ScheduleData> result= scheduleService.selecteScheduleListByCondition(param);
			
			result.stream().collect(Collectors.groupingBy(ScheduleData::getArea_name))
					.forEach((key, value)->{
						
						 
						Map<String, List<ScheduleData>> schduleList= value.stream().collect(Collectors.groupingBy(ScheduleData::getVessel));
						
						
								 
						 for (Object key2 : schduleList.keySet()) {

							    List<ScheduleData> areaList = schduleList.get(key2);
							    
					    
							    printSchedule((String) key2,areaList);
						
						 }		
			
			
					
			}	);
	
			log.info("inbound joint end:{}",result.size());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public void printSchedule(String vesselName, List<ScheduleData> data)
	{
		// 출발항 리스트
		Map<String, String> fromPortSchedule = data.stream().collect(Collectors.toMap(ScheduleData::getFromPort, ScheduleData::getDateF,
				(oldValue,newValue)->{return oldValue;}));
//		log.info("data:{}",data);
		
		StringBuilder strFromPorts = new StringBuilder();
		for(String key:fromPortSchedule.keySet())
		{
			String date= fromPortSchedule.get(key);
			strFromPorts.append(key+" "+getFormatedDate(date)+"-");
		}		
		
		// 도착항 리스트
		StringBuilder strToPorts = new StringBuilder();
		List<ScheduleData> toPortSchedule = data.stream().sorted().collect(Collectors.toList());
				
		
		
//		log.info("toPort:{}",toPortSchedule);
		
		
		
		
		for(ScheduleData item:toPortSchedule )
		{
			strToPorts.append(item.getPort()+" "+ getFormatedDate(item.getDateF())+" - ");	
		}
		
		
		
		
		System.out.println(
					vesselName+
					data.get(0).getVoyage_num()
					+"("+data.get(0).getCompany_abbr()+")"+"\r\n"
					+strFromPorts.toString()+"\r\n"
					+strToPorts.toString() +"\r\n\r\n");
	}
}
