package com.ksg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.AreaInfo;

/**

  * @FileName : AreaDAO.java

  * @Date : 2021. 2. 24. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 지역정보(Area) 관리 DAO 클래스

  */
public class AreaDAOImpl extends AbstractDAO{
	public AreaDAOImpl() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> selectAreaList(Map<String, Object> commandMap) throws SQLException {
		return selectList("area.selectAreaList", commandMap);

	}
	
	@SuppressWarnings("unchecked")
	public List<AreaInfo> selectAreaList2(Map<String, Object> commandMap) throws SQLException {
		return selectList("area.selectAreaList2", commandMap);

	}
	
	@SuppressWarnings("unchecked")
	public List<AreaInfo> selectAll() throws SQLException {
		return selectList("area.selectAll");

	}

	public int deleteArea(HashMap<String, Object> param) throws SQLException {
		return (Integer) delete("area.deleteArea", param);
	}
	
	public int deleteArea(AreaInfo param) throws SQLException {
		return (Integer) delete("area.deleteArea", param);
	}

	public int updateArea(HashMap<String, Object> param) throws SQLException {
	
		return (Integer) update("area.updateArea", param);
	}
	
	public int updateArea(AreaInfo param) throws SQLException {
		
		return (Integer) update("area.updateArea", param);
	}

	public void insertArea(HashMap<String, Object> param) throws SQLException {
		insert("area.insertArea",param);
		
	}
	
	public List getAreaListGroupBy(String groupBy) throws SQLException {
		
		if(groupBy.equals("area"))
		{
			return sqlMap.queryForList("area.selectAreaListGroupByAreaName");	
		}
		else
			
		{
			return sqlMap.queryForList("area.selectAreaCodeListGroupByAreaCode");
		}
		
	}

	public Object insertArea(AreaInfo info) throws SQLException {
		// TODO Auto-generated method stub
		return insert("area.insertArea",info);
	}

}
