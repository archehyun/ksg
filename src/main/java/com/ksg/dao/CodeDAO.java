package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CodeDAO {
	
	public Object select(Map<String, Object> commandMap) throws SQLException ;
	
	public Object selectDetail(Map<String, Object> param) throws SQLException;
	
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public List<Map<String, Object>> selectDetailList(Map<String, Object> commandMap) throws SQLException ;
	
	public int selectDetailCount(Map<String, Object> commandMap) throws SQLException;
	
	public int selectCount(Map<String, Object> commandMap)throws SQLException ;

	public Object insert(HashMap<String, Object> param) throws SQLException;
	
	public Object insertDetail(HashMap<String, Object> param) throws SQLException ;

	public Object update(HashMap<String, Object> param) throws SQLException ;	

	public Object delete(HashMap<String, Object> param) throws SQLException;

	public Object deleteDetail(HashMap<String, Object> param) throws SQLException ;
	

}
