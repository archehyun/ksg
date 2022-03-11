package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PortService extends PageService{
	
	public Map<String, Object> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public List<Map<String, Object>> selectPortAbbrList(HashMap<String, Object> commandMap) throws SQLException;
	
	public Object selectPortAbbr(HashMap<String, Object> param) throws SQLException;
	
	public int update(HashMap<String, Object> param) throws SQLException;	

	public int delete(HashMap<String, Object> param) throws SQLException;

	public Object insert(HashMap<String, Object> param) throws Exception;

}
