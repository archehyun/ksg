package com.ksg.base.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ksg.base.dao.PortDAO;
import com.ksg.domain.PortInfo;
import com.sun.org.apache.bcel.internal.generic.DALOAD;

public class PortService {
	
	PortDAO portDAO;
	public PortService() {
		portDAO = new PortDAO();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectPortList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", portDAO.selectPortCount(commandMap));
		
		resultMap.put("master", portDAO.selectPortList(commandMap));
		
		return resultMap;

	}

	public int deletePortAbbr(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return portDAO.deletePortAbbr(param);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> selectPort(HashMap<String, Object> param) throws SQLException {
		
		HashMap<String,Object> resultMap=(HashMap<String, Object>) portDAO.selectPort( param);
		
		if(resultMap==null)
		{
			resultMap = (HashMap<String, Object>) portDAO.selectPortAbbr(param);
		}		
		
		return resultMap;
	}

	public int updatePort(HashMap<String, Object> param) throws SQLException {
		return portDAO.updatePort(param);
		
	}

	public void insertPort(HashMap<String, Object> param) throws SQLException {
		portDAO.isnertPort(param);
		
	}

	public int deletePort(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return portDAO.deletePort(param);
	}
	
	

}
