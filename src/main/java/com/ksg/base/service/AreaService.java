package com.ksg.base.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.base.dao.AreaDAO;

public class AreaService {
	
	AreaDAO areaDAO;
	
	public AreaService() {
		areaDAO = new AreaDAO();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectAreaList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return areaDAO.selectAreaList(commandMap);

	}

	public int deleteArea(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) areaDAO.deleteArea(param);
	}
	
	public int updateArea(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) areaDAO.updateArea(param);
	}

	public void insertArea(HashMap<String, Object> param) throws SQLException{
		areaDAO.insertArea(param);
		
	}
	
	

}
