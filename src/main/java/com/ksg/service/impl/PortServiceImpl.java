package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ksg.dao.impl.PortDAOImpl;
import com.ksg.service.PortService;

/**

  * @FileName : PortServiceImpl.java

  * @Project : KSG2

  * @Date : 2021. 12. 7. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class PortServiceImpl implements PortService{
	
	PortDAOImpl portDAO;
	
	Logger logger = Logger.getLogger(this.getClass());
	
	public PortServiceImpl() {
		portDAO = new PortDAOImpl();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", portDAO.selectPortCount(commandMap));
		
		resultMap.put("master", portDAO.selectPortList(commandMap));
		
		return resultMap;

	}

	public int deletePortAbbr(HashMap<String, Object> param) throws SQLException {
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

	public int update(HashMap<String, Object> param) throws SQLException {
		return portDAO.updatePort(param);
		
	}

	public void insert(HashMap<String, Object> param) throws SQLException {
		portDAO.isnertPort(param);
		
	}

	public int delete(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return portDAO.deletePort(param);
	}

	@Override
	public Object selectPortAbbr(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return portDAO.selectPortAbbr(param);
	}

	@Override
	public List<Map<String, Object>> selectPortAbbrList(HashMap<String, Object> commandMap) throws SQLException {
		
		return portDAO.selectPortAbbrList(commandMap);
	}


	
	

}
