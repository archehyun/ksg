package com.ksg.base.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

/**

  * @FileName : AreaDAO.java

  * @Date : 2021. 2. 24. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 지역정보(Area) 관리 DAO 클래스

  */
public class AreaDAO extends AbstractDAO{
	public AreaDAO() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectAreaList(Map<String, Object> commandMap) throws SQLException {
		// TODO Auto-generated method stub
		return selectList("area.selectAreaList", commandMap);

	}

	public int deleteArea(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) delete("area.deleteArea", param);
	}

}
