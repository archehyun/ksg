package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.ksg.domain.PortInfo;


public interface PortDAO {
	
	
	// -- select List -- //
	
	public List<PortInfo> selectAll() throws SQLException ;
	
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException ;
	
	public PortInfo select(PortInfo param) throws SQLException;
	
	public Object selectDetail(HashMap<String, Object> param) throws SQLException;	

	public List<Map<String, Object>> selectDetailList(HashMap<String, Object> commandMap) throws SQLException;
	
	public int selectCount(Map<String, Object> commandMap) throws SQLException;
	
	public Object selectListByPage(HashMap<String, Object> param)throws SQLException;
	
	public List<Map<String, Object>> selectListByLike(Map<String, Object> param)throws SQLException;
	
	
	// -- update -- //
	
	public int update(HashMap<String, Object> param) throws SQLException;
	
	public int update(PortInfo param) throws SQLException;

	public int updateDetail(PortInfo param) throws SQLException;
	
	// -- insert -- //
	
	public Object isnert(HashMap<String, Object> param) throws SQLException;
	
	public Object insert(PortInfo param) throws SQLException;
	
	public Object insertDetail(PortInfo param) throws SQLException;
	
	
	// -- delete -- //
	
	public int delete(PortInfo param) throws SQLException;
	
	public int deleteDetail(PortInfo param) throws SQLException;
	
	public int delete(HashMap<String, Object> param) throws SQLException;
	
	public int deleteDetail(HashMap<String, Object> param) throws SQLException;

	public PortInfo selectDetail(PortInfo item) throws SQLException;

}
