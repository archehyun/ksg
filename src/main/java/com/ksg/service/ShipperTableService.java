package com.ksg.service;

import java.util.Map;

public interface ShipperTableService {
	
	public Map<String, Object> selectList(Map<String, Object> param);
	
	public void delete(Map<String, Object> param);
	
	public void update(Map<String, Object> param);
	
	public void insert(Map<String, Object> param);

}
