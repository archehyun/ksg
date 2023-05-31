package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.domain.Vessel;

public interface VesselServiceV2 extends VesselService{
	
	public List selectListByLike(HashMap<String, Object> param) throws SQLException;
	
	public Map<String, String> selectAll()throws SQLException;
	
	public HashMap<String, Object> selectTotalList() throws SQLException;
	
	public Vessel selectDetail(String vessel_abbr) throws SQLException;
	
	public Object update(HashMap<String, Object> param) throws SQLException;
	
	public Object updateDetail(HashMap<String, Object> param) throws SQLException;
	
	public void insert(HashMap<String, Object> param) throws RuntimeException;	
	
	public void insertDetail(HashMap<String, Object> param) throws RuntimeException;
	
	
	
	
	public HashMap<String, Object> selectDetailList(Map<String, Object> commandMap) throws SQLException;
	
	public HashMap<String, Object> selectDetailListByLike(Map<String, Object> commandMap) throws SQLException;
	
	
	
	
	

	

}
