package com.ksg.dao.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.domain.AreaInfo;


@Deprecated
public class AreaDAOImpl2 {
	private SqlMapClient sqlMap;

	public AreaDAOImpl2() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	
//	/**
//	 * @param data
//	 * @return
//	 * @throws SQLException
//	 */
//	public int delete(String data) throws SQLException {
//		return sqlMap.delete("BASE_AREA.deleteArea",data);
//		
//	}
//	/**
//	 * @return
//	 * @throws SQLException
//	 */
//	public int getCount() throws SQLException {
//		return (Integer) sqlMap.queryForObject("BASE_AREA.selectAreaCount");
//	}
//	/**
//	 * @return
//	 * @throws SQLException
//	 */
//	public List getAreaGroupList() throws SQLException {
//		return sqlMap.queryForList("BASE_AREA.selectAreaGroupList");
//	}
//	/**
//	 * @param area
//	 * @return
//	 * @throws SQLException
//	 */
//	public AreaInfo select(AreaInfo  area) throws SQLException {
//		return (AreaInfo) sqlMap.queryForObject("BASE_AREA.selectAreaInfo",area);
//	}
//	/**
//	 * @return
//	 * @throws SQLException
//	 */
//	public List selectAreaList() throws SQLException {
//		return sqlMap.queryForList("BASE_AREA.selectAreaInfoList");
//	}
//	/**
//	 * @param info
//	 * @return
//	 * @throws SQLException
//	 */
//	public List getAreaSubList(AreaInfo info) throws SQLException {
//		return sqlMap.queryForList("BASE_AREA.selectAreaSubList",info);
//	}
//	/**
//	 * @param orderBy
//	 * @return
//	 * @throws SQLException
//	 */
//	public List getArrangedAreaInfoList(String orderBy) throws SQLException {
//		AreaInfo areaInfo = new AreaInfo();
//		areaInfo.setOrderBy(orderBy);
//		return sqlMap.queryForList("BASE_AREA.selectArrangedAreaList",areaInfo);
//	}
//
//	/**
//	 * @param orderBy
//	 * @param type
//	 * @return
//	 * @throws SQLException
//	 */
//	public List getArrangedAreaInfoList(String orderBy, String type)
//			throws SQLException {
//		AreaInfo areaInfo = new AreaInfo();
//		areaInfo.setOrderBy(orderBy);
//		areaInfo.setArea_type(type);
//		return sqlMap.queryForList("BASE_AREA.selectArrangedAreaList",areaInfo);
//	}
//	
//	public Object insert(AreaInfo info) throws SQLException {
//		
//		return sqlMap.insert("BASE_AREA.insertArea",info);
//	}
//	
//	/**
//	 * @설명 지역 정보 업데이트
//	 * @param update
//	 * @throws SQLException
//	 */
//	public int update(AreaInfo update) throws SQLException {
//		return sqlMap.update("BASE_AREA.updateArea",update);
//		
//	}
//	
//	public List getAreaInfoList(AreaInfo info) throws SQLException {
//		
//		return sqlMap.queryForList("BASE_AREA.selectAreaInfoListOrderBy",info);
//	}
//	


}
