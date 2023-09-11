package com.ksg.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ShipperTableDAO {
	
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public Object select(Map<String, Object> commandMap) throws SQLException;
	
	public int selectCount(Map<String, Object> param) throws SQLException;
	
	public int update(Map<String, Object> param) throws SQLException;
	
	public int delete(Map<String, Object> param) throws SQLException;
	
	public Object insert(Map<String, Object> param) throws SQLException;

	int updateDate(Map<String, Object> param) throws SQLException;
}
