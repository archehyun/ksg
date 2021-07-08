package com.ksg.base.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ksg.base.dao.VesselDAO;

/**

  * @FileName : VesselService.java

  * @Project : KSG2

  * @Date : 2021. 7. 8. 

  * @작성자 : pch

  * @변경이력 : Vessel 서비스

  * @프로그램 설명 :

  */
public class VesselService {
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
	VesselDAO vesselDAO;
	
	public VesselService() {
		
		vesselDAO = new VesselDAO();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectVesselList(Map<String, Object> param) throws SQLException {
		
		logger.info("param:{}",param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", vesselDAO.selectVesselCount(param));
		
		resultMap.put("master",vesselDAO.selectVesselList(param));
		
		return resultMap;

	}

	public Map<String, Object> selectVesselAbbrList(HashMap<String, Object> param) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		logger.info("param:{}",param);		
		resultMap.put("master",vesselDAO.selectVesselAbbrList(param));
		return resultMap;
	}

	public Object updateVesselAbbr(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}",param);
		return vesselDAO.updateVesselAbbr(param);
		
	}

	public Object deleteVessel(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}",param);
		return vesselDAO.deleteVessel(param);
		
	}

	public void insertVessel(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}",param);
		vesselDAO.insertVessel(param);
		
	}

	public void deleteVesselAbbr(HashMap<String, Object> param) throws SQLException {
		logger.info("param:{}",param);
		vesselDAO.deleteVesselAbbr(param);
		
	}

}
