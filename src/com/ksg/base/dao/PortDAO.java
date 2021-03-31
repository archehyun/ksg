package com.ksg.base.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

public class PortDAO extends AbstractDAO{
	public PortDAO() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectPortList(Map<String, Object> commandMap) throws SQLException {
		return selectList("port.selectPortList", commandMap);

	}
	
	public Object selectPort(HashMap<String, Object> param) throws SQLException
	{
		return selectOne("port.selectPort", param);
	}
	public Object selectPortAbbr(HashMap<String, Object> param) throws SQLException
	{
		return selectOne("port.selectPortAbbr", param);
	}

	public int deletePort(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete("port.deletePort", param);
	}

	public List<Map<String, Object>> selectPortAbbrList(HashMap<String, Object> commandMap) throws SQLException{
		return selectList("port.selectPortAbbrList", commandMap);
		
	}

	public int selectPortCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("port.selectCount", commandMap);
	}

	public int deletePortAbbr(HashMap<String, Object> param) throws SQLException{
		return (Integer) delete("port.deletePortAbbr", param);
	}

}
