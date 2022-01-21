package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.dao.impl.AreaDAOImpl;

public class AreaServiceImpl {
	
	AreaDAOImpl areaDAO;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public AreaServiceImpl() {
		areaDAO = new AreaDAOImpl();
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
	
	public List getAreaListGroupByAreaName() throws SQLException {
		return areaDAO.getAreaListGroupBy("area");
	}


	public List getAreaListGroupByAreaCode() throws SQLException{
		// TODO Auto-generated method stub
		return areaDAO.getAreaListGroupBy("code");
	}


	
	

}
