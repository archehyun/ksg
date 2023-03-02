package com.dtp.api.dao;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
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
	
	public List selectShipperTablePortListByID(String id) throws SQLException
	{
		return selectList(namespace+".selectShipperTablePortListByID", id);
	}

	public List<ShippersTable> selectShipperTableListAll() throws SQLException {
		// TODO Auto-generated method stub
		return selectList(namespace+".selectShipperTableListAll");
	}

}
