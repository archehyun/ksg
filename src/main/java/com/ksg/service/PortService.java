package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;

public interface PortService extends PageService{
	
	//--select--//
	
	public PortInfo select(Map<String, Object> commandMap) throws SQLException;
	
	public Map<String, Object> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public Map<String, String> selectAll() throws SQLException;
	
	public Map<String, Object> selectListByCondition(Map<String, Object> commandMap) throws SQLException;
	
	public List<Map<String, Object>> selectPortAbbrList(HashMap<String, Object> commandMap) throws SQLException;
	
	public Object selectPortAbbr(HashMap<String, Object> param) throws SQLException;
	
	public Object selectPort(HashMap<String, Object> param) throws SQLException;
	
	
	//--update--//
	public int update(CommandMap param) throws SQLException;
	
	public int updateDetail(CommandMap param) throws SQLException;
	
	//--delete--//
	public int delete(CommandMap param) throws SQLException;
	
	public int deleteDetail(CommandMap param) throws SQLException;
	
	
	//--insert--//

	public Object insert(CommandMap param) throws RuntimeException;
	
	public Object insertDetail(CommandMap param) throws Exception;

	public Object insert(PortInfo t) throws Exception;

	public List<PortInfo> selectPortListByNameList(List<String> portNames) throws SQLException;


}
