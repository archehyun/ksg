package com.ksg.shippertable.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

public class ShipperTableDAO extends AbstractDAO{
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectPortList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return selectList("shippertable.selectPortList", commandMap);

	}

	public void insertShipperPort(HashMap<String, Object> commandMap) throws SQLException {
		insert("shippertable.insertShipperPort", commandMap);
		
	}
	
	public void deleteShipperPortList(HashMap<String, Object> commandMap) throws SQLException {
		delete("shippertable.deleteShipperPortList", commandMap);
		
	}

}
