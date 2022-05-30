package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ksg.domain.Vessel;

public interface VesselService extends PageService{
	
	public HashMap<String, Object> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public Vessel select(String vessel_name) throws SQLException;
	
	public Vessel selectDetail(String vessel_abbr) throws SQLException;
	
	public HashMap<String, Object> selectDetailList(Map<String, Object> commandMap) throws SQLException;
	
	public HashMap<String, Object> selectDetailListByLike(Map<String, Object> commandMap) throws SQLException;
	
	public Object update(HashMap<String, Object> param) throws SQLException;
	
	public Object update(Vessel op)throws SQLException;
	
	public Object updateDetail(HashMap<String, Object> param) throws SQLException;

	public int delete(HashMap<String, Object> param) throws SQLException;
	
	public Object deleteDetail(HashMap<String, Object> param) throws RuntimeException;

	public void insert(HashMap<String, Object> param) throws RuntimeException;
	
	public void insert(Vessel param) throws SQLException;
	
	public void insertDetail(HashMap<String, Object> param) throws RuntimeException;
	
	public void insertDetail(Vessel param) throws RuntimeException;

	

	

	

	public Map<String, String> selectAll()throws SQLException;

	public Vessel selectDetailInfo(String vesselName) throws SQLException;



}
