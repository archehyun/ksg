package com.dtp.api.dao;


import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.Vessel;

/**
 * 

  * @FileName : VesselDAO.java

  * @Project : KSG2

  * @Date : 2023. 6. 27. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :
 */
public class VesselDAO extends AbstractDAO{
	
	/**
	 * 
	 */
	public VesselDAO() {
		super();
		this.namespace = "vessel";
	}
	
	// -- select List -- //

	public List<Vessel> selectAll() throws SQLException {
		return selectList("vessel.selectAll");
	}
	

	public List selectListByCondition(Vessel param) throws SQLException {
		return selectList("vessel.selectVesselListByCondition", param);
	}
	
	public List selectDetailList(String vessel_name) throws SQLException {
		Vessel param = Vessel.builder().vessel_name(vessel_name).build();
		return selectList("vessel.selectVesselDetailList", param);
	}
	
	public List selectByVesselNames(List names) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// -- select -- //
	public Vessel selectById(String vessel_name) throws SQLException {
		
		return (Vessel) selectOne("vessel.selectVesselById", vessel_name);
	}
	
	public Vessel selectVesselDetailByKey(Vessel param) throws SQLException {
		return (Vessel) selectOne("vessel.selectVesselDetailByKey", param);
	}


	// -- update -- //

	public Object updateVessel(Vessel param) throws SQLException {
		return update("vessel.updateVesselInfo",param);
	}
	
	// -- insert -- //
	
	public Object insertVessel(Vessel param) throws SQLException {
		return insert("vessel.insertVessel", param);
	}
	
	// -- delete -- //
	public Object deleteVesselDetail(Vessel param) throws SQLException {
			return delete("vessel.deleteVesselDetail",param);
	}
	
	public Object deleteVessel(String vessel_name) throws SQLException {
		return delete("vessel.deleteVesselInfo",vessel_name);
	}

	public Object insertVesselDetail(Vessel param) throws SQLException {
		return insert("vessel.insertVesselDetail", param);		
	}

	public List<Vessel> selectDetailAll() throws SQLException {
		// TODO Auto-generated method stub
		return selectList("vessel.selectDetailAll");
	}


}
