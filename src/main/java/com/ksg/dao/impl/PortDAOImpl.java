package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.dao.PortDAO;
import com.ksg.domain.PortInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	public PortInfo select(PortInfo param) throws SQLException
	{
		return (PortInfo) selectOne("port.selectPort", param);
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
	@Override
	public List<PortInfo> selectAll() throws SQLException {
		// TODO Auto-generated method stub
		return selectList(this.namespace+".selectAll");
	}

	@Override
	public Object insert(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return insert(this.namespace+".insertPortV1",param);
	}
	
	@Override
	public Object insertDetail(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return insert(this.namespace+".insertPortDetailV1",param);
	}

	@Override
	public int update(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return (int) update(this.namespace+".updatePortV1",param);
	}

	@Override
	public int updateDetail(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return (int) update(this.namespace+".updatePortDetailV1",param);
	}

	@Override
	public int delete(PortInfo param) throws SQLException {
		
		log.debug("delete param:{}", param);
		return (Integer) delete(this.namespace+".deletePort2", param);
	}

	@Override
	public int deleteDetail(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) delete(this.namespace+".deletePortAbbr2", param);
	}

	@Override
	public PortInfo selectDetail(PortInfo param) throws SQLException {
		// TODO Auto-generated method stub
		return (PortInfo) selectOne("port.selectPortDetail", param);
	}
	

}
