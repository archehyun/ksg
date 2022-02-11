package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.dao.impl.VesselDAOImpl;
import com.ksg.service.VesselService;

/**

  * @FileName : VesselServiceImpl.java

  * @Project : KSG2

  * @Date : 2021. 11. 25. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� :

  */
public class VesselServiceImpl implements VesselService{
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	VesselDAOImpl vesselDAO;
	
	public VesselServiceImpl() {
		
		vesselDAO = new VesselDAOImpl();
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> selectList(Map<String, Object> commandMap) throws SQLException {
		
		logger.debug("param:"+commandMap);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", vesselDAO.selectCount(commandMap));
		
		resultMap.put("master",vesselDAO.selectList(commandMap));
		
		return resultMap;

	}

	public Map<String, Object> selectDetailList(HashMap<String, Object> commandMap) throws SQLException {
		
		logger.debug("param:"+commandMap);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		//resultMap.put("total", vesselDAO.selectVesselCount(commandMap));
		
		resultMap.put("master",vesselDAO.selectDetailList(commandMap));
		return resultMap;
	}

	public Object updateDetail(HashMap<String, Object> param) throws SQLException {
		return vesselDAO.updateDetail(param);
		
	}

	public int delete(HashMap<String, Object> pram) throws SQLException {
		return vesselDAO.delete(pram);
		
	}

	public void insert(HashMap<String, Object> param) throws SQLException {
		
		logger.debug("param:"+param);
		vesselDAO.insert(param);
		
	}

	public Object deleteDetail(HashMap<String, Object> param) throws SQLException {
		return  vesselDAO.deleteDetail(param);
		
	}

	@Override
	public HashMap<String, Object> selectDetailList(Map<String, Object> commandMap) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("master",vesselDAO.selectDetailList((HashMap<String, Object>) commandMap));
		return resultMap;
	}

	@Override
	public int update(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void insertDetail(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
