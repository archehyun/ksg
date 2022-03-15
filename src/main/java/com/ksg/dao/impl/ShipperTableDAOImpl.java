package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.dao.ShipperTableDAO;

public class ShipperTableDAOImpl extends AbstractDAO implements ShipperTableDAO{
	
	public ShipperTableDAOImpl() {
		super();
	}
	
	
	public List<Map<String, Object>> selectPortList(Map<String, Object> commandMap) throws SQLException {
		// 
		return selectList("shippertable.selectPortList", commandMap);

	}

	public void insertShipperPort(HashMap<String, Object> commandMap) throws SQLException {
		insert("shippertable.insertShipperPort", commandMap);
		
	}
	
	public void deleteShipperPortList(HashMap<String, Object> commandMap) throws SQLException {
		delete("shippertable.deleteShipperPortList", commandMap);
		
	}


	@Override
	public Object select(Map<String, Object> param) throws SQLException {
		
		return selectOne("shippertable.select", param);
	}
	
	@Override
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {

		return selectList("shippertable.selectList", commandMap);
	}
	
	public int selectCount(Map<String, Object> param) throws SQLException{
		return  (Integer) selectOne("shippertable.selectCount", param);
	}

	@Override
	public int update(Map<String, Object> param) throws SQLException {
		
		return (Integer) update("shippertable.update", param);
	}
	
	@Override
	public int updateDate(Map<String, Object> param) throws SQLException {
		
		return (Integer) update("shippertable.updateDate", param);
	}

	@Override
	public int delete(Map<String, Object> param) throws SQLException {
		
		return (Integer) delete("shippertable.delete", param);
	}

	@Override
	public Object insert(Map<String, Object> param) throws SQLException {
		
		return (Integer) insert("shippertable.insert", param);
	}

	public Object selectListByPage(HashMap<String, Object> param) throws SQLException {
		return selectList("shippertable.selectShipperTableListByPage", param);
	}

}
