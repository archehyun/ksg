package com.ksg.base.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

/**

  * @FileName : VesselDAO.java

  * @Date : 2021. 2. 26. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 :

  */
public class VesselDAO extends AbstractDAO{
	/**
	 * 
	 */
	public VesselDAO() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectVesselList(Map<String, Object> commandMap) throws SQLException {
		return selectList("vessel.selectVesselList", commandMap);

	}
	
	public int deleteVessel(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete("vessel.deleteVessel", param);
	}


}
