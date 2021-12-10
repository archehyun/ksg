package com.ksg.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ShipperTableDAO {
	
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException;

}
