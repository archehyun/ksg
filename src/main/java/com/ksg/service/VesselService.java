package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ksg.domain.Vessel;

public interface VesselService extends PageService{
	
	public List<Vessel> selectAllList() throws SQLException;
	
	public Vessel selectVesselById(String vessel_name) throws SQLException;
	
	public List<Vessel> selectVesselListByCondition(Vessel param) throws SQLException;
	
	public List<Vessel> selectVesselListByNameList(List<String>nameList)throws SQLException;
	
	//--update--//	
	public Object update(Vessel vessel) throws SQLException;

	//--delete--//
	public Object delete(HashMap<String, Object> param) throws SQLException;
	
	public Object deleteDetail(HashMap<String, Object> param) throws SQLException;

	//--insert--//
	public void insertVessel(Vessel vessel) throws SQLException;
	
	


	
	

	



}
