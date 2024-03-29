package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface VesselDAO {
	
	
	
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException;
	public Object selectListByPage(Map<String, Object> commandMap) throws SQLException;
	
	public List<Map<String, Object>> selectDetailList(HashMap<String, Object> commandMap) throws SQLException;
	
	public List<Map<String, Object>> selectDetailListByLike(HashMap<String, Object> commandMap) throws SQLException;
	
	public int selectCount(Map<String, Object> commandMap) throws SQLException;
	
	public int delete(HashMap<String, Object> param) throws SQLException ;
	
	public int deleteDetail(HashMap<String, Object> param) throws SQLException ;
	
	public Object insert(HashMap<String, Object> param) throws SQLException;
	
	public Object insertDetail( Object params) throws SQLException;
	
	public Object update(HashMap<String, Object> param) throws SQLException ;

	public Object updateDetail(HashMap<String, Object> param) throws SQLException ;
	
	

}
