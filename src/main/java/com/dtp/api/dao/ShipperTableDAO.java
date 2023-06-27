package com.dtp.api.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;

public class ShipperTableDAO extends AbstractDAO{
	
	public ShipperTableDAO()
	{
		super();
		
		this.namespace = "shippertable";
	}
	
	public List selectShipperTableListByCondition(ShippersTable param) throws SQLException
	{
		return selectList(namespace+".selectShipperTableListByCondition", param);
	}
	
	public List selectShipperTableListAllByCondition(ShippersTable param) throws SQLException
	{
		return selectList(namespace+".selectShipperTableListAllByCondition", param);
	}
	
	public List selectShipperTablePortListByID(String id) throws SQLException
	{
		return selectList(namespace+".selectShipperTablePortListByID", id);
	}

	public List<ShippersTable> selectShipperTableListAll() throws SQLException {
		
		return selectList(namespace+".selectShipperTableListAll");
	}
	
	public ADVData selectADVDataById(String table_id) throws SQLException {
		
		return (ADVData) selectOne(namespace+".selectADVDataById", table_id);
	}
	public ShippersTable selectShipperTableById(String table_id) throws SQLException {
		
		return (ShippersTable) selectOne(namespace+".selectShipperTableByID", table_id);
	}
	
	public Object updateTableDate(ADVData param ) throws SQLException {
		
		return update(namespace+".updateTableDate", param);
	}
	
	public Object updateShipperTableDate(HashMap<String, Object> param ) throws SQLException {
		
		return update(namespace+".updateDate", param);
	}
	
	public Object updateShipperTableDateByList(String inputDate, List tableIdList) throws SQLException {
		
		HashMap<Object, Object>param  = new HashMap<Object, Object>();
		
		param.put("date_isusse", inputDate);
		
		param.put("tableIDList", tableIdList);
		
		return update(namespace+".updateShipperTableDate", param);
	}

	public Object deleteADV(String table_id) throws SQLException {
		return delete(namespace+".deleteADV" , table_id);
		
	}

	public Object insertADV(ADVData param) throws SQLException {
		
		return insert(namespace+".insertADV", param);
	}

	public ShippersTable selectShipperTableAllById(String table_id) throws SQLException {
		return (ShippersTable) selectOne(namespace+".selectShipperTableAllById", table_id);
	}
}
