package com.dtp.api.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.dtp.api.dao.ShipperTableDAO;
import com.dtp.api.exception.ResourceNotFoundException;
import com.dtp.api.service.ShipperTableService;
import com.ksg.common.exception.AlreadyExistException;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShipperTableServiceImpl implements ShipperTableService{

	private ShipperTableDAO shipperTableDAO;

	public ShipperTableServiceImpl()
	{
		shipperTableDAO = new ShipperTableDAO();
	}

	@Override
	public List selectTableAll() throws SQLException {

		List<ShippersTable> li = shipperTableDAO.selectShipperTableListAll();

		return li;
	}


	@Override
	public List selectTableListAndPortListByCondition(ShippersTable param) throws SQLException {

		List<ShippersTable> li = shipperTableDAO.selectShipperTableListByCondition(param);
		
		for(ShippersTable table:li)
		{
			table.setTablePortList(shipperTableDAO.selectShipperTablePortListByID(table.getTable_id()));
		}
		
		return li;
	}

	public List selectShipperTablePortListByID(String id) throws SQLException
	{
		return shipperTableDAO.selectShipperTablePortListByID(id);
	}

	@Override
	public List selectTableListByCondition(ShippersTable param) throws SQLException {

		return shipperTableDAO.selectShipperTableListByCondition(param);
	}

	@Override
	public ShippersTable selectShipperTableById(String table_id) throws SQLException {

		return shipperTableDAO.selectShipperTableById(table_id);
	}
	@Override
	public ADVData selectADVDataById(String table_id) throws SQLException {

		return shipperTableDAO.selectADVDataById(table_id);
	}
	
	@Override
	public void insert(ShippersTable param) throws Exception {

		String table_id = param.getTable_id();

		ShippersTable target = selectShipperTableById(table_id);

		if(target !=null) throw new AlreadyExistException("exist ");

		shipperTableDAO.insert("shippertable.insert", param);
	}

	@Override
	public void delete(String table_id) throws Exception {

		ShippersTable target = selectShipperTableById(table_id);

		if(target ==null) throw new ResourceNotFoundException(table_id);

		shipperTableDAO.delete("shippertable.delete", table_id);

		shipperTableDAO.delete("shippertable.deleteADV", table_id);
	}

	@Override
	public void update(ShippersTable param) throws Exception {

		String table_id = param.getTable_id();

		ShippersTable target = selectShipperTableById(table_id);

		if(target ==null) throw new ResourceNotFoundException(table_id);

		shipperTableDAO.update("shippertable.update", param);
	}
	@Override
	public void updateTableData(ADVData param) throws SQLException
	{
		shipperTableDAO.updateTableDate(param);
	}

	@Override
	public int updateTableDateByTableIDs(List<String> table, String updateDate) throws SQLException {

		log.info("update by "+updateDate+", "+table);
		
		return (int) shipperTableDAO.updateShipperTableDateByList(updateDate, table);
	}

	@Override
	public void save(ADVData param) throws Exception {
		
		ADVData data = shipperTableDAO.selectADVDataById(param.getTable_id());
		
		if(data== null) throw new ResourceNotFoundException(param.getTable_id());
		
		shipperTableDAO.deleteADV(param.getTable_id());
		
		shipperTableDAO.insertADV(param);
		
		updateTableData(param);
	}

	@Override
	public List selectShipperTableListAllByCondition(ShippersTable param) throws SQLException {
		return shipperTableDAO.selectShipperTableListAllByCondition(param);
	}

	@Override
	public ShippersTable selectShipperTableAllById(String table_id) throws SQLException {
		return shipperTableDAO.selectShipperTableAllById(table_id);
	}

}
