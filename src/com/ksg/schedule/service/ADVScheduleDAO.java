package com.ksg.schedule.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

public class ADVScheduleDAO extends AbstractDAO{
	

	public ADVScheduleDAO() {
		super();
	}

	public List<Map<String, Object>> selectScheduleList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return selectList("schedule.selectScheduleList",commandMap);
	}

	public int selectScheduleCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("schedule.selectCount", commandMap);
	}
	

}
