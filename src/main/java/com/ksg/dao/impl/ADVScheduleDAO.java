package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

public class ADVScheduleDAO extends AbstractDAO{
	

	public ADVScheduleDAO() {
		super();
	}
	
	public List<Map<String, Object>> selectScheduleJointList(Map<String, Object> commandMap) throws SQLException {
		return selectList("schedule.selectScheduleJointList",commandMap);
	}

	public List<Map<String, Object>> selectScheduleList(Map<String, Object> commandMap) throws SQLException {
		return selectList("schedule.selectScheduleList",commandMap);
	}

	public int selectScheduleCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("schedule.selectCount", commandMap);
	}

	public List<Map<String, Object>> selectScheduledAreaList(Map<String, Object> commandMap) throws SQLException {
		return selectList("schedule.selectScheduledAreaList",commandMap);
	}
	
	public List<Map<String, Object>> selectScheduledToPortList(Map<String, Object> commandMap) throws SQLException {
		return selectList("schedule.selectScheduledPortList",commandMap);
	}
	
	public List<Map<String, Object>> selectScheduledFromPortList(Map<String, Object> commandMap) throws SQLException {
		return selectList("schedule.selectScheduledFromPortList",commandMap);
	}

	

}
