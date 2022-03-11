package com.ksg.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ShipperTableService extends PageService{
	
	public Map<String, Object> selectList(Map<String, Object> param);
	
	public void delete(Map<String, Object> param);
	
	public void update(Map<String, Object> param);
	
	public void insert(Map<String, Object> param);

	int updateTableDateByTableIDs(List table, String updateDate) throws SQLException;

}
