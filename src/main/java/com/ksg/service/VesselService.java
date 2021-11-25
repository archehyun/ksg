package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.dao.impl.VesselDAOImpl;

public class VesselService {
	VesselDAOImpl vesselDAO;
	public VesselService() {
		
		vesselDAO = new VesselDAOImpl();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectVesselList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", vesselDAO.selectVesselCount(commandMap));
		
		resultMap.put("master",vesselDAO.selectVesselList(commandMap));
		
		return resultMap;

	}

	public Map<String, Object> selectVesselAbbrList(HashMap<String, Object> commandMap) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		//resultMap.put("total", vesselDAO.selectVesselCount(commandMap));
		
		resultMap.put("master",vesselDAO.selectVesselAbbrList(commandMap));
		return resultMap;
	}

	public Object updateVesselAbbr(HashMap<String, Object> param) throws SQLException {
		return vesselDAO.updateVesselAbbr(param);
		
	}

	public Object deleteVessel(HashMap<String, Object> pram) throws SQLException {
		return vesselDAO.deleteVessel(pram);
		
	}

	public void insertVessel(HashMap<String, Object> info) throws SQLException {
		vesselDAO.insertVessel(info);
		
	}

	public void deleteVesselAbbr(HashMap<String, Object> param) throws SQLException {
		vesselDAO.deleteVesselAbbr(param);
		
	}

}
