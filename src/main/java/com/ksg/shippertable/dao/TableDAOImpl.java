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
package com.ksg.shippertable.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Table_Property;
public class TableDAOImpl implements TableDAO{
	SqlMapClient sqlMap;
	public TableDAOImpl() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public int deleteTableInfo(ShippersTable table) throws SQLException {
		return sqlMap.delete("Table.deleteTable",table);

	}


	public ShippersTable insertTableInfo(ShippersTable table) throws SQLException {
		return (ShippersTable) sqlMap.insert("Table.insertTableInfo",table);

	}

	public int updateTableInfo(ShippersTable table) throws SQLException {
		return sqlMap.update("Table.updateTable",table);
	}


	public int updateTableDate(ADVData data) throws SQLException {
		return sqlMap.update("Table.updateTableDate",data);
	}

	public List selectCompanyList()throws SQLException
	{
		return sqlMap.queryForList("Table.selectCompanyList");
	}

	public List selectCompanyListGroupByCompany_abbr() throws SQLException {
		return sqlMap.queryForList("Table.selectCompanyListGroupByCompany_abbr");
	}


	public List selectTableInfoByCompany(String company_abbr)
	throws SQLException {
		return sqlMap.queryForList("Table.selectTableInfoByCompany",company_abbr);
	}


	public List selectCompanyListGroupByCompany_abbr(String data_isusse)
	throws SQLException {
		return sqlMap.queryForList("Table.selectCompanyListGroupByCompany_abbr",data_isusse);
	}


	public List selectTableListByTableCount() throws SQLException {
		return sqlMap.queryForList("Table.selectTableListByTableCount");
	}


	public List getTableByCompany(ShippersTable shippersTable)throws SQLException {
		return sqlMap.queryForList("Table.getTableListByCompany",shippersTable);
	}


	public List getTableListByPage(int start, int end) throws SQLException {

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("start", start);
		map.put("end", end);
		return sqlMap.queryForList("Table.getTableListByPage",map);
	}


	public int getLastTableIndex(String company_abbr) throws SQLException {
		if(company_abbr==null)
			return 0;
		return (Integer) sqlMap.queryForObject("Table.selectLastTableIndex",company_abbr);
	}


	public List<ShippersTable> getTableListBycompany(ShippersTable table) {
		// TODO Auto-generated method stub
		return null;
	}


	public List getTotalTableList() throws SQLException {
		return sqlMap.queryForList("Table.selectTotalTableList");
	}


	public List selectCompanyListGroupByPage() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.selectCompanyListGroupByPage");
	}


	public ShippersTable getTableById(ShippersTable shippersTable)
	throws SQLException {
		// TODO Auto-generated method stub
		return (ShippersTable) sqlMap.queryForObject("Table.selectTableByID",shippersTable);
	}


	public ShippersTable getTableInfoByIndex(ShippersTable shippersTable)
	throws SQLException {
		return (ShippersTable) sqlMap.queryForObject("Table.selectTableInfoByIndex",shippersTable);
	}
	
	public List selectSystemTableList() throws SQLException
	{
		return sqlMap.queryForList("Table.selectSystemTableList");
	}


	public List selectTableInfoByPage(int page) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.selectTableInfoByPage",page);
	}


	public List selectTablePageListByCompany(ShippersTable shippersTable)
	throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.getTablePageListByCompany",shippersTable);
	}


	public List selectTableCompanyListByPage(ShippersTable shippersTable) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.getTableCompanyListByPage",shippersTable);
	}


	public List selectParentPortList(String table_id) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.getParentPortList",table_id);
	}


	public void insertPortList(TablePort tablePort) throws SQLException {
		sqlMap.insert("Table.insertPortList",tablePort);

	}


	public List selectTableListByPage(ShippersTable table) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.selectTableListByPage",table);
	}
	public List getTablePortList(TablePort tablePort) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.getTablePortList",tablePort);
	}


	public void deleteTablePort(TablePort tablePort) throws SQLException {
		// TODO Auto-generated method stub
		sqlMap.delete("Table.deleteTablePort",tablePort);
	}


	public TablePort getTablePort(TablePort tablePort) throws SQLException {
		return (TablePort) sqlMap.queryForObject("Table.getTablePortList",tablePort);
	}


	public TablePort getTablePortByIndex(TablePort port) throws SQLException {
		return (TablePort) sqlMap.queryForObject("Table.getTablePortByIndex",port);
	}


	public void updateTablePort(TablePort port) throws SQLException {
		sqlMap.update("Table.updateTablePort",port);
	}


	public List getTablePortByDate(String date) throws SQLException {
		// TODO Auto-generated method stub
		return (List) sqlMap.queryForList("Table.getTableListByDate",date);
	}


	public List selectTableInfoList(ShippersTable stable) throws SQLException {
		// TODO Auto-generated method stub
		return (List) sqlMap.queryForList("Table.selectTableInfoList",stable);
	}


	public int selectMaxPortIndex(String table_id) throws SQLException 
	{
		Object obj = sqlMap.queryForObject("Table.selectMaxPortIndex",table_id);
		if(obj==null)
			return 0;		
			
		return (Integer) obj;
	}


	public void updateTablePortIndex(TablePort port) throws SQLException {
		sqlMap.update("Table.updateTablePortIndex",port);
		
	}


	public int updateTableDate(String table_id, String date)
			throws SQLException {
		ShippersTable shippersTable  = new ShippersTable();
		shippersTable.setTable_id(table_id);
		shippersTable.setDate_isusse(date);
		return sqlMap.update("Table.updateTableDate_issuse",shippersTable);
	}


	public boolean isPageHave(int page, String company) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


	public List getTableListByDate(ShippersTable data) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.getTableListByDate",data);
	}

	public int getTableCount(String date_isusse) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer)sqlMap.queryForObject("Table.getTableCountByDate",date_isusse);
	}


	public List getTableProperty() throws SQLException 
	{		
		return sqlMap.queryForList("Table.selectTablePropetyList");
	}

	public List getTableProperty(String company_abbr, int page)
			throws SQLException {
		Table_Property property = new Table_Property();
		property.setCompany_abbr(company_abbr);
		property.setPage(page);
		return sqlMap.queryForList("Table.selectTablePropetyListByPro",property);
	}


	public void insertTableProperty(Table_Property propertis)
			throws SQLException {
		sqlMap.insert("Table.insertTableProperty", propertis);
		
	}


	public void updateTableProperty(Table_Property property)
			throws SQLException {
		sqlMap.update("Table.updateTableProperty",property);
		
	}


	public void updateTablePortIndex2(TablePort port) throws SQLException {
		sqlMap.update("Table.updateTablePortIndex2",port);
		
	}

	public List getScheduleTableListByDate(ShippersTable data)
			throws SQLException {
		return sqlMap.queryForList("Table.getScheduleTableListByDate",data);
	}


	public void updateTablePortCount(String table_id,int portcount) throws SQLException {
		
		ShippersTable table = new ShippersTable();
		table.setPort_col(portcount);
		table.setTable_id(table_id);
		
		sqlMap.update("Table.updateTablePortCount",table);
	}


	public void updateTableVesselCount(String table_id,int vesselcount) throws SQLException {
		ShippersTable table = new ShippersTable();
		table.setVsl_row(vesselcount);
		table.setTable_id(table_id);
		sqlMap.update("Table.updateTableVesselCount",table);
	}


	public List getTableList(ShippersTable table) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.selectTableList",table);
	}


	public int updateTableDateAll(ShippersTable table) throws SQLException {
		
		return sqlMap.update("Table.updateTableDateAll", table);
	}
	
	public Table_Property getTableProperty(Table_Property param) throws SQLException {
		// TODO Auto-generated method stub
		return (Table_Property) sqlMap.queryForObject("TABLEProperty.selectTABLEProperty",param);
	}


	public List getTableListByAgent(ShippersTable table) throws SQLException {
		
		return sqlMap.queryForList("Table.selectTableByAgent",table);
	}


	public String getTableAgentByPage(int page) throws SQLException {
		ShippersTable table = new ShippersTable();
		table.setPage(page);
		return (String) sqlMap.queryForObject("Table.selectTableAgentByPage",table);
	}


	public int getPortCount(String tableId) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) sqlMap.queryForObject("Table.selectTablePortCount",tableId);
	}


	public int updateTablePortName(TablePort port) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.update("Table.updateTablePortName",port);
	}


	public List getTableDateList() throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Table.selectTableDateList");
	}


	public List selectTableInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}


	public List selectTableColumnList(String table) throws SQLException {
		
		return sqlMap.queryForList("Table.selectTableColumnList",table);
	}
	
	public List selectSystemDataList(String table_name)  throws SQLException 
	{
		List li = sqlMap.queryForList("Table.selectSystemData"+table_name);
		
		
		return li;
	}


	@Override
	public int updateTableDateByTableIDs(ShippersTable table) throws SQLException {
		
		return sqlMap.update("Table.updateTableDateByTableIDs", table);
	}





}
