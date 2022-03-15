package com.ksg.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ShipperTableService extends PageService{
	
	public Map<String, Object> selectList(Map<String, Object> param);
	
	public int delete(Map<String, Object> param) throws Exception;
	
	public void update(Map<String, Object> param);
	
	public void insert(Map<String, Object> param) throws Exception;

	int updateTableDateByTableIDs(List table, String updateDate) throws SQLException;

}
