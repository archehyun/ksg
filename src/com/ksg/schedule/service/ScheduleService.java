package com.ksg.schedule.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.base.dao.AreaDAO;

public class ScheduleService {
	
	
	ADVScheduleDAO advScheduleDAO;
	
	public ScheduleService() {
		advScheduleDAO = new ADVScheduleDAO();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectSceduleList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return advScheduleDAO.selectScheduleList(commandMap);

	}


	public HashMap<String, Object> selectScheduleList(HashMap<String, Object> commandMap) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", advScheduleDAO.selectScheduleCount(commandMap));
		
		resultMap.put("master", advScheduleDAO.selectScheduleList(commandMap));
		
		return resultMap;

	}

}
