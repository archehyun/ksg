package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PortDAO {
	
	
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException ;
	
	public Object select(HashMap<String, Object> param) throws SQLException;
	
	public Object selectDetail(HashMap<String, Object> param) throws SQLException;

	public int delete(HashMap<String, Object> param) throws SQLException;

	public List<Map<String, Object>> selectDetailList(HashMap<String, Object> commandMap) throws SQLException;
	
	public int selectCount(Map<String, Object> commandMap) throws SQLException;

	public int deleteDetail(HashMap<String, Object> param) throws SQLException;

	public int update(HashMap<String, Object> param) throws SQLException;

	public Object isnert(HashMap<String, Object> param) throws SQLException;
	
	public Object selectListByPage(HashMap<String, Object> param)throws SQLException;
	public List<Map<String, Object>> selectListByLike(Map<String, Object> param)throws SQLException;
}
