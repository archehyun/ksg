package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;

/**

  * @FileName : AreaDAO.java

  * @Date : 2021. 2. 24. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� : ��������(Area) ���� DAO Ŭ����

  */
public class AreaDAOImpl extends AbstractDAO{
	public AreaDAOImpl() {
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

	public int updateArea(HashMap<String, Object> param) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) update("area.updateArea", param);
	}

	public void insertArea(HashMap<String, Object> param) throws SQLException {
		insert("area.insertArea",param);
		
	}

}
