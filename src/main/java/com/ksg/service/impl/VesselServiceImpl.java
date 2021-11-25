package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ksg.dao.impl.VesselDAOImpl;
import com.ksg.service.VesselService;

/**

  * @FileName : VesselServiceImpl.java

  * @Project : KSG2

  * @Date : 2021. 11. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class VesselServiceImpl implements VesselService{
	
	VesselDAOImpl vesselDAO;
	
	public VesselServiceImpl() {
		
		vesselDAO = new VesselDAOImpl();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", vesselDAO.selectVesselCount(commandMap));
		
		resultMap.put("master",vesselDAO.selectVesselList(commandMap));
		
		return resultMap;

	}

	public Map<String, Object> selectDetailList(HashMap<String, Object> commandMap) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		//resultMap.put("total", vesselDAO.selectVesselCount(commandMap));
		
		resultMap.put("master",vesselDAO.selectVesselAbbrList(commandMap));
		return resultMap;
	}

	public Object updateDetail(HashMap<String, Object> param) throws SQLException {
		return vesselDAO.updateVesselAbbr(param);
		
	}

	public int delete(HashMap<String, Object> pram) throws SQLException {
		return vesselDAO.deleteVessel(pram);
		
	}

	public void insert(HashMap<String, Object> info) throws SQLException {
		vesselDAO.insertVessel(info);
		
	}

	public Object deleteDetail(HashMap<String, Object> param) throws SQLException {
		return  vesselDAO.deleteVesselAbbr(param);
		
	}

	@Override
	public Map<String, Object> selectDetailList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return null;
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
