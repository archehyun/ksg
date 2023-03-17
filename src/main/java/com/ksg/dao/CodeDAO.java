package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.domain.Code;

public interface CodeDAO {
	
	
	// -- select List -- //
	
	public Object select(Map<String, Object> commandMap) throws SQLException ;
	
	public Object selectDetail(Map<String, Object> param) throws SQLException;
	
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public List<Map<String, Object>> selectDetailList(Map<String, Object> commandMap) throws SQLException ;
	
	public int selectDetailCount(Map<String, Object> commandMap) throws SQLException;
	
	public int selectCount(Map<String, Object> commandMap)throws SQLException ;
	
	// -- insert -- //

	public Object insert(HashMap<String, Object> param) throws SQLException;
	
	public Object insertDetail(HashMap<String, Object> param) throws SQLException ;
	
	// -- update -- //

	public Object update(HashMap<String, Object> param) throws SQLException ;
	
	// -- delete -- //

	public Object delete(HashMap<String, Object> param) throws SQLException;

	public Object deleteDetail(HashMap<String, Object> param) throws SQLException ;

	Object deleteCode(String code_field) throws SQLException;

	Object deleteCodeDetail(Code param) throws SQLException;
	

}
