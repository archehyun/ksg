package com.dtp.api.dao;


import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.Vessel;

public class VesselDAO extends AbstractDAO{
	
	/**
	 * 
	 */
	public VesselDAO() {
		super();
		this.namespace = "vessel";
	}

	public Vessel selectById(String vessel_name) throws SQLException {
		
		return (Vessel) selectOne("vessel.selectVesselById", vessel_name);
	}

	public List<Vessel> selectAll() {
		return null;
	}

	public int deleteVessel(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Object insertVessel(Vessel param) throws SQLException {
		// TODO Auto-generated method stub
		return insert("vessel.insertVessel", param);
	}

	public int updateVessel(Vessel param) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List selectListByCondition(Vessel param) throws SQLException {
		// TODO Auto-generated method stub
		return selectList("vessel.selectVesselListByCondition", param);
	}

	public List selectDetailList(String vessel_name) throws SQLException {
		Vessel param = Vessel.builder().vessel_name(vessel_name).build();
		return selectList("vessel.selectVesselDetailList", param);
	}

	public int deleteVessel(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List selectByVesselNames(List names) {
		// TODO Auto-generated method stub
		return null;
	}

}
