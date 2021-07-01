/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.shippertable.service.impl;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Table_Property;
import com.ksg.shippertable.service.TableDAO;
import com.ksg.shippertable.service.TableDAOImpl;
import com.ksg.shippertable.service.TableService;
@SuppressWarnings("unchecked")
public class TableServiceImpl implements TableService{
	
	protected Logger 			logger = Logger.getLogger(getClass());
	
	private TableDAO tableDAO;
	public TableServiceImpl() {
		tableDAO = new TableDAOImpl();
	}
	
	public int deleteTable(ShippersTable table) throws SQLException {
		return tableDAO.deleteTableInfo(table);
	}

	
	public ShippersTable insertTable(ShippersTable table) throws SQLException {
		return tableDAO.insertTableInfo(table);
	}

	
	public int updateTable(ShippersTable table) throws SQLException {
		return tableDAO.updateTableInfo(table);
	}
	
	public int updateTableDate(ADVData data)throws SQLException  {
		return tableDAO.updateTableDate(data);
		
	}
	
	public List selectCompanyListGroupByCompany_abbr() throws SQLException 
	{
		return tableDAO.selectCompanyListGroupByCompany_abbr();
	}
	
	public List selectTableInfoByCompany(String company_name) throws SQLException{
		return tableDAO.selectTableInfoByCompany(company_name);
	}
	
	public List selectCompanyList() throws SQLException {
		return tableDAO.selectCompanyList();
	}
	
	public List selectTableListByTableCount() throws SQLException {
		return tableDAO.selectTableListByTableCount();
	}
	
	public List selectCompanyListGroupByCompany_abbr(String date)
			throws SQLException {
		return tableDAO.selectCompanyListGroupByCompany_abbr(date);
	}
	
	public List<ShippersTable> getTableListByCompany(String company_abbr,
			String table_id, String date, int p_index) throws SQLException {
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setCompany_abbr(company_abbr);
		shippersTable.setTable_id(table_id);
		shippersTable.setTable_index(p_index);
		shippersTable.setDate_isusse(date);
		return tableDAO.getTableByCompany(shippersTable);
	}
	
	
	public List<ShippersTable> getTableListBycompany(String company_abbr,
			String table_id, String date_isusse) throws SQLException {
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setCompany_abbr(company_abbr);
		shippersTable.setTable_id(table_id);
		shippersTable.setDate_isusse(date_isusse);
		return tableDAO.getTableByCompany(shippersTable);
	}
	
	public List<ShippersTable> getTableListByCompany(String company_abbr)
			throws SQLException {
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setCompany_abbr(company_abbr);
		
		return (List<ShippersTable>) tableDAO.getTableByCompany(shippersTable);
	}
	
	public List<ShippersTable> getTableListByCompany(String company_abbr, String date)
			throws SQLException {
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setCompany_abbr(company_abbr);

		shippersTable.setDate_isusse(date);
		return (List<ShippersTable>) tableDAO.getTableByCompany(shippersTable);
	}
	
	public List getTableListByPage(int start, int end) throws SQLException {
		return  tableDAO.getTableListByPage(start,end);
	}

	public int getLastTableIndex(String company_abbr) throws SQLException {
		return tableDAO.getLastTableIndex(company_abbr);
	}

	public List<ShippersTable> getTableListBycompany(ShippersTable table)
			throws SQLException {
		return tableDAO.getTableListBycompany(table);
	}

	public List<ShippersTable> getTableListOrderByCompany(ShippersTable table)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ShippersTable> getTableListOrderByPage(ShippersTable table)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public List getTotalTableList() throws SQLException {
		return tableDAO.getTotalTableList();
	}

	public List selectCompanyListGroupByPage() throws SQLException {
		return tableDAO.selectCompanyListGroupByPage();
	}
	public ShippersTable getTableById(Object tableId) throws SQLException {
		
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setTable_id(tableId.toString());
		return tableDAO.getTableById(shippersTable);
	}

	public List<ShippersTable> getTableListByCompany(String company_abbr,
			int page) throws SQLException {
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setCompany_abbr(company_abbr);
		shippersTable.setPage(page);
		
		return (List<ShippersTable>) tableDAO.getTableByCompany(shippersTable);
	}

	public ShippersTable getTableInfoByIndex(String companyAbbr, int tableIndex,int page)
			throws SQLException {
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setCompany_abbr(companyAbbr);
		shippersTable.setTable_index(tableIndex);
		shippersTable.setPage(page);
		return tableDAO.getTableInfoByIndex(shippersTable);
	}

	public List selectTableInfoByPage(int page) throws SQLException {
		return tableDAO.selectTableInfoByPage(page);
	}

	public List getTablePageListByCompany(String company_abbr)
			throws SQLException {
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setCompany_abbr(company_abbr);
		return tableDAO.selectTablePageListByCompany(shippersTable);
	}

	public List getTableCompanyListByPage(int page) throws SQLException {
		ShippersTable shippersTable = new ShippersTable();
		shippersTable.setPage(page);
		return tableDAO.selectTableCompanyListByPage(shippersTable);
	}

	public List getTableListByPage(ShippersTable table) throws SQLException {
		return tableDAO.selectTableListByPage(table);
	}

	public List getParentPortList(String tableId) throws SQLException {
		return tableDAO.selectParentPortList(tableId);
	}

	public List getSubPortList(String tableId) {
		return null;
	}

	public void insertPortList(TablePort tablePort) throws SQLException {
		tableDAO.insertPortList(tablePort);
	}

	public List getTablePortList(TablePort tablePort) throws SQLException {
		return tableDAO.getTablePortList(tablePort);
	}

	public void deleteTablePort(TablePort tablePort) throws SQLException {
		tableDAO.deleteTablePort(tablePort);
	}

	public TablePort getTablePort(TablePort port) throws SQLException {
		return tableDAO.getTablePort(port);
	}

	public void updateTablePort(TablePort port) throws SQLException {
		tableDAO.updateTablePort(port);
	}

	public TablePort getTablePortByIndex(TablePort port) throws SQLException {
		return tableDAO.getTablePortByIndex(port);
	}

	/**
	 * 
	 * 스케줄 처리용
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public List getTableListByDate(ShippersTable data) throws SQLException {
		return tableDAO.getTableListByDate(data);
	}

	public List selectTableInfoList(ShippersTable stable) throws SQLException {
		return tableDAO.selectTableInfoList(stable);
	}

	public int getMaxPortIndex(String table_id) throws SQLException {
		return tableDAO.selectMaxPortIndex(table_id);
	}

	public void updateTablePortIndex(TablePort port) throws SQLException {
		tableDAO.updateTablePortIndex(port);
		
	}

	public int updateTableDate(String table_id, String date)
			throws SQLException {
		return tableDAO.updateTableDate(table_id,date);
	}

	public boolean isPageHave(int page, String company) throws SQLException {
		
		return tableDAO.isPageHave(page, company);
	}

	public int getTableCount(String date) throws SQLException {
		return tableDAO.getTableCount(date);
	}

	public List getTableProperty() throws SQLException {
		return tableDAO.getTableProperty();
	}

	public List getTableProperty(String company_abbr, int page)
			throws SQLException {
		return tableDAO.getTableProperty(company_abbr,page);
	}

	public void insertTableProperty(Table_Property propertis) throws SQLException {
		tableDAO.insertTableProperty(propertis);
	}

	public void updateTableProperty(Table_Property property)
			throws SQLException {
		tableDAO.updateTableProperty(property);
		
	}

	public void updateTablePortIndex2(TablePort port) throws SQLException {
		tableDAO.updateTablePortIndex2(port);
	}

	public List getScheduleTableListByDate(ShippersTable data) throws SQLException {
		return tableDAO.getScheduleTableListByDate(data);
	}

	public void updateTablePortCount(String table_id,int portcount) throws SQLException {
		tableDAO.updateTablePortCount(table_id,portcount);
	}

	public void updateTableVesselCount(String table_id,int vesselcount) throws SQLException {
		tableDAO.updateTableVesselCount(table_id,vesselcount);
		
	}

	
	public List getTableList(ShippersTable table) throws SQLException {
		return tableDAO.getTableList(table);
		
	}

	public int updateTableDateAll(ShippersTable table) throws SQLException {
		
		return tableDAO.updateTableDateAll(table);
	}

	public Table_Property getTableProperty(String table_id) throws SQLException {
		
		Table_Property param = new Table_Property();
		param.setTable_id(table_id);
		return tableDAO.getTableProperty(param);
	}

	public List getTableListByAgent(ShippersTable table) throws SQLException {
		return tableDAO.getTableListByAgent(table);
	}

	public String getTableAgentByPage(int page) throws SQLException {
		return tableDAO.getTableAgentByPage(page);
	}

	public int getPortCount(String tableId) throws SQLException {
		return tableDAO.getPortCount(tableId);
	}

	public int updateTablePortName(TablePort port) throws SQLException {
		
		return tableDAO.updateTablePortName(port);
	}

	public List getTableDateList() throws SQLException {
		return tableDAO.getTableDateList();
	}
	public List selectSystemTableList() throws SQLException
	{
		return tableDAO.selectSystemTableList();
	}
	public List selectTableColumnList(String table) throws SQLException {
		return tableDAO.selectTableColumnList(table);
	}
	public List selectSystemDataList(String table_name) throws SQLException
	{
		return tableDAO.selectSystemDataList(table_name);
	}

	@Override
	public int updateTableDateByTableIDs(List table,String updateDate) throws SQLException {
		logger.info("update by "+updateDate+", "+table);
		Iterator<ShippersTable> iter = table.iterator();
		int count=0;
		while(iter.hasNext())
		{
			ShippersTable item = iter.next();
			item.setDate_isusse(updateDate);
			tableDAO.updateTableDateByTableIDs(item);
			count++;
		}
		
		return count;
	}


	
}
