package com.dtp.api.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.ShippersTable;

public interface ShipperTableService {
	public List selectTableListByCondition(ShippersTable param) throws SQLException;
	public List selectShipperTablePortListByID(String id) throws SQLException;

}
