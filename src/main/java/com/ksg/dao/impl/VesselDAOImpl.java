package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.dao.VesselDAO;

/**

  * @FileName : VesselDAO.java

  * @Date : 2021. 2. 26. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 선박 정보 DAO

  */
public class VesselDAOImpl extends AbstractDAO implements VesselDAO{
	/**
	 * 
	 */
	public VesselDAOImpl() {
		super();
	}
	
	@Override
	public List<Map<String, Object>> selectList(Map<String, Object> commandMap) throws SQLException {
		return selectList("vessel.selectVesselList", commandMap);

	}
	@Override
	public List<Map<String, Object>> selectDetailList(HashMap<String, Object> commandMap) throws SQLException{
		
		return selectList("vessel.selectVesselAbbrList", commandMap);
	}
	@Override
	public int delete(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete("vessel.deleteVessel", param);
	}
	
	@Override
	public int deleteDetail(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete("vessel.deleteVesselAbbr", param);
	}
	
	@Override
	public Object insert(HashMap<String, Object> param) throws SQLException{		
		return insert("vessel.insertVessel", param);
	}
	
	@Override
	public Object insertDetail( Object param) throws SQLException {		
		return insert("vessel.insertVesselAbbr", param);
	}

	@Override
	public int selectCount(Map<String, Object> commandMap) throws SQLException{
		return  (Integer) selectOne("vessel.selectCount", commandMap);
	}
	
	@Override
	public Object update(HashMap<String, Object> param) throws SQLException {		
		return update("vessel.updateVessel", param);
	}

	public Object updateDetail(HashMap<String, Object> param) throws SQLException {		
		return update("vessel.updateVesselAbbr", param);
	}

	@Override
	public Object selectListByPage(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return selectList("vessel.selectVesselListByPage", commandMap);
	}

	@Override
	public List<Map<String, Object>> selectDetailListByLike(HashMap<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return selectList("vessel.selectVesselAbbrListByLike", commandMap);
	}







}
