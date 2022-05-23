package com.ksg.schedule.execute.joint.impl;

import java.util.HashMap;
import java.util.List;

import com.ksg.schedule.execute.joint.JointSchedule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InboundScheduleJoint extends JointSchedule{

	
	public InboundScheduleJoint(String date_isusse) {
		super(date_isusse);
	}

	@Override
	public int execute() {
		log.info("inbound joint start");
		HashMap<String, Object> param = new HashMap<String, Object>();

		
		param.put("inOutType", "I");
		param.put("date_issue", this.date);


		List<HashMap<String, Object>> result= scheduleService.selecteScheduleListMapByCondition(param);
		
		log.info("inbound joint end:{}",result.size());
		
		return 0;
	}

}
