package com.ksg.dao.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.domain.Vessel;


@Deprecated
public class VesselDAOImpl2 {
	private SqlMapClient sqlMap;
	public VesselDAOImpl2() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public int getCount() throws SQLException {
//		return (Integer) sqlMap.queryForObject("BASE_VESSEL.selectVesselCount");
//	}
//	public int getVesselAbbrCount() throws SQLException {
//		return (Integer) sqlMap.queryForObject("BASE_VESSEL.selectVesselAbbrCount");
//	}
	
//	public List selectList(Vessel info) throws SQLException {
//		return sqlMap.queryForList("BASE_VESSEL.selectVesselList", info);
//	}

//	public List getVesselListByPatten(String patten) throws SQLException {
//		return sqlMap.queryForList("Base.selectVesselListByPatten",patten);
//	}
//	public int deleteVessel(String data) throws SQLException {
//		return sqlMap.delete("BASE_VESSEL.deleteVessel",data);
//	}
//	public List getSearchedVesselList(String searchKeyword) throws SQLException {
//		return sqlMap.queryForList("BASE_VESSEL.selectSearchedVesselList",searchKeyword);
//	}
//	public Object insert(Vessel vessel) throws SQLException {
//		return sqlMap.insert("BASE_VESSEL.insertVessel",vessel);
//	}
//	public Object insertNew(Vessel vessel) throws SQLException {
//		return sqlMap.insert("BASE_VESSEL.insertNewVessel",vessel);
//	}
//	public int update(Vessel info) throws SQLException {
//		return sqlMap.update("BASE_VESSEL.updateVessel",info);
//	}
//	public Object insertVesselAbbr(Vessel vesselAbbr) throws SQLException {
//		return sqlMap.insert("BASE_VESSEL.insertVesselAbbr",vesselAbbr);
//	}
//	public Vessel select(Vessel vessel) throws SQLException {
//		return (Vessel) sqlMap.queryForObject("BASE_VESSEL.selectVesselInfo",vessel);
//	}
//	public List getArrangedVesselList(Object orderBy) throws SQLException {
//		return sqlMap.queryForList("BASE_VESSEL.selectArrangedVesselAbbrList",orderBy);
//	}
//	public List getVesselListGroupByName(Vessel info) throws SQLException {
//		return sqlMap.queryForList("BASE_VESSEL.selectVesselListGroupByName", info);
//	}
//	public List getVesselListByPattenGroupByName(String string)throws SQLException {
//		return sqlMap.queryForList("BASE_VESSEL.selectVesselListByPattenGroupBynName",string);
//	}
//	public Vessel getVesselInfo(Vessel vessel) throws SQLException {
//		return (Vessel) sqlMap.queryForObject("BASE_VESSEL.selectVesselInfoItem",vessel);
//	}
//	public int deleteVesselAll() throws SQLException {
//		return sqlMap.delete("BASE_VESSEL.deleteVesselAll");
//	}
//	public int updateVesselAbbr(Vessel vessel) throws SQLException {
//		return sqlMap.update("BASE_VESSEL.updateVesselAbbr",vessel);
//	}
	
//	public List getSearchedVesselList(Vessel op) throws SQLException {
//		return sqlMap.queryForList("BASE_VESSEL.selectSearchedVesselList",op);
//	}
	
//	public List getVesselAbbrList(String vesselName) throws SQLException {
//		return sqlMap.queryForList("BASE_VESSEL.selectVesselAbbrInfo",vesselName);
//	}

//	public List getVesselAbbrList(Vessel info) throws SQLException {
//		return sqlMap.queryForList("BASE_VESSEL.selectVesselAbbrList", info);
//	}


}
