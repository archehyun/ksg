package com.dtp.api.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.dtp.api.dao.ShipperTableDAO;
import com.dtp.api.service.ShipperTableService;
import com.ksg.domain.ShippersTable;

public class ShipperTableServiceImpl implements ShipperTableService{
	
	ShipperTableDAO shipperTableDAO;
	
	public ShipperTableServiceImpl()
	{
		shipperTableDAO = new ShipperTableDAO();
	}

	@Override
	public List selectTableListByCondition(ShippersTable param) throws SQLException {
		List<ShippersTable> li = shipperTableDAO.selectShipperTableListByCondition(param);
		
		li.stream().forEach(o -> {
			try {
				o.setTablePortList(shipperTableDAO.selectShipperTablePortListByID(o.getTable_id()));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		return li;
	}
	
	public List selectShipperTablePortListByID(String id) throws SQLException
	{
		return shipperTableDAO.selectShipperTablePortListByID(id);
	}

}
