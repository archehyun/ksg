package com.ksg.base.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ksg.base.dao.VesselDAO;

public class VesselService {
	VesselDAO vesselDAO;
	public VesselService() {
		
		vesselDAO = new VesselDAO();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectVesselList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", vesselDAO.selectVesselCount(commandMap));
		
		resultMap.put("master",vesselDAO.selectVesselList(commandMap));
		
		return resultMap;

	}

}
