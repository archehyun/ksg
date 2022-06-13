package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ksg.domain.Schedule;
import com.ksg.domain.ScheduleData;

public interface ScheduleServiceV2 {
	
	public List<HashMap<String, Object>> selecteScheduleListMapByCondition(HashMap<String, Object> param);
	
	public List<ScheduleData> selecteScheduleListByCondition(HashMap<String, Object> param) throws SQLException;
	
	public List<Schedule> selecteAll(HashMap<String, Object> param) throws SQLException;

}
