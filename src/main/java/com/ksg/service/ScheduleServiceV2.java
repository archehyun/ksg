package com.ksg.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.Schedule;
import com.ksg.domain.ScheduleData;

@Deprecated
public interface ScheduleServiceV2 {
	
	public List<CommandMap> selecteScheduleListMapByCondition(CommandMap param);
	
	public List<ScheduleData> selecteScheduleListByCondition(CommandMap param) throws SQLException;
	
	public List<Schedule> selecteAll(CommandMap param) throws SQLException;

}
