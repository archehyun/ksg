package com.ksg.base.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

public class VesselDAO extends AbstractDAO{
	/**
	 * 
	 */
	public VesselDAO() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectVesselList(Map<String, Object> commandMap) throws SQLException {
		return selectList("vessel.selectVesselList", commandMap);

	}

}
