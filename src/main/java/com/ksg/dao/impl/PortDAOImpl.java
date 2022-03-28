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
		
		this.namespace ="port";
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		return selectList(this.namespace+".selectPortList", commandMap);

	}
	
	public List<Map<String, Object>> selectDetailList(HashMap<String, Object> commandMap) throws SQLException{
		return selectList(this.namespace+".selectPortAbbrList", commandMap);
		
	}
	
	public Object select(HashMap<String, Object> param) throws SQLException
	{
		return selectOne("port.selectPort", param);
	}
	public Object selectDetail(HashMap<String, Object> param) throws SQLException
	{
		return selectOne(this.namespace+".selectPortAbbr", param);
	}

	public int delete(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete(this.namespace+".deletePort", param);
	}
	public int deleteDetail(HashMap<String, Object> param) throws SQLException{
		return (Integer) delete(this.namespace+".deletePortAbbr", param);
	}
	

	public int selectCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne(this.namespace+".selectCount", commandMap);
	}

	public int update(HashMap<String, Object> param) throws SQLException{
		// TODO Auto-generated method stub
		return (Integer) update(this.namespace+".updatePort",param);
	}

	public Object isnert(HashMap<String, Object> param) throws SQLException{
		return insert(this.namespace+".insertPort",param);
	}
	
	public Object selectListByPage(HashMap<String, Object> param)throws SQLException
	{
		return selectList(this.namespace+".selectPortListByPage", param);
	}
	

	@Override
	public List<Map<String, Object>> selectListByLike(Map<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return selectList(this.namespace+".selectPortListByLike", param);
	}
	

}
