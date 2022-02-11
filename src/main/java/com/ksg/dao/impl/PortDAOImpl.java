package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.dao.PortDAO;

public class PortDAOImpl extends AbstractDAO implements PortDAO{
	public PortDAOImpl() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		return selectList("port.selectPortList", commandMap);

	}
	
	public List<Map<String, Object>> selectDetailList(HashMap<String, Object> commandMap) throws SQLException{
		return selectList("port.selectPortAbbrList", commandMap);
		
	}
	
	public Object select(HashMap<String, Object> param) throws SQLException
	{
		return selectOne("port.selectPort", param);
	}
	public Object selectDetail(HashMap<String, Object> param) throws SQLException
	{
		return selectOne("port.selectPortAbbr", param);
	}

	public int delete(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete("port.deletePort", param);
	}
	public int deleteDetail(HashMap<String, Object> param) throws SQLException{
		return (Integer) delete("port.deletePortAbbr", param);
	}
	

	public int selectCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("port.selectCount", commandMap);
	}

	public int update(HashMap<String, Object> param) throws SQLException{
		// TODO Auto-generated method stub
		return (Integer) update("port.updatePort",param);
	}

	public void isnert(HashMap<String, Object> param) throws SQLException{
		insert("port.insertPort",param);
	}

}
