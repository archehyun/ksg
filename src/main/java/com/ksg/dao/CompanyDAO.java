package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CompanyDAO {
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public int update(HashMap<String, Object> param) throws SQLException;
	
	public int delete(Map<String, Object> commandMap) throws SQLException;
	
	public Object insert(HashMap<String, Object> param) throws SQLException;
	
	public int selectCount(Map<String, Object> commandMap) throws SQLException;

}
