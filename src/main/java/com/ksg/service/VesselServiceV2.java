package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ksg.domain.Vessel;

public interface VesselServiceV2 extends VesselService{
	
	public HashMap<String, Object> selectList(Vessel commandMap) throws SQLException;
	
	public List selectListByLike(HashMap<String, Object> param) throws SQLException;
	
	public List<Vessel> selectAllList() throws SQLException;
	
	public HashMap<String, Object> selectDetailList(Vessel commandMap) throws SQLException;
	
	public HashMap<String, Object> selectTotalList() throws SQLException;
	
	public Vessel select(String vessel_name) throws SQLException;
	
	public Vessel selectDetail(String vessel_abbr) throws SQLException;	
	
	public void insert(Vessel param) throws SQLException;
	
	public Object update(Vessel op)throws SQLException;

	

}
