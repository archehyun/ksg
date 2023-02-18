package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.domain.Vessel;

public interface VesselService extends PageService{
	
	public HashMap<String, Object> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public HashMap<String, Object> selectDetailList(Map<String, Object> commandMap) throws SQLException;
	
	public HashMap<String, Object> selectDetailListByLike(Map<String, Object> commandMap) throws SQLException;
	
	public Map<String, String> selectAll()throws SQLException;
	
	
	//--insert--//
	public Object update(HashMap<String, Object> param) throws SQLException;
	
	public Object updateDetail(HashMap<String, Object> param) throws SQLException;	

	//--delete--//
	public Object delete(HashMap<String, Object> param) throws SQLException;
	
	public Object deleteDetail(HashMap<String, Object> param) throws RuntimeException;

	//--insert--//
	public void insert(HashMap<String, Object> param) throws RuntimeException;	
	
	public void insertDetail(HashMap<String, Object> param) throws RuntimeException;

	List<Vessel> selectListByCondition(HashMap<String, Object> param) throws SQLException;
	
	List<Vessel> selectVesselListByNameList(List<String>nameList)throws SQLException;

	

	



}
