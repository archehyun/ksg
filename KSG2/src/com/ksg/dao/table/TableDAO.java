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
package com.ksg.dao.table;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Table_Property;
@SuppressWarnings("unchecked")
public interface TableDAO {
	
	/**
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public int updateTableInfo(ShippersTable table)throws SQLException;
	public int updateTableDateAll(ShippersTable table)throws SQLException;
	
	/**
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public int deleteTableInfo(ShippersTable table)throws SQLException;
	
	/**
	 * @param talbe
	 * @return
	 * @throws SQLException
	 */
	public ShippersTable insertTableInfo(ShippersTable talbe) throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException
	 */
	
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public int updateTableDate(ADVData data)throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List selectCompanyListGroupByCompany_abbr()throws SQLException;
	
	/**
	 * @param company_name
	 * @return
	 * @throws SQLException
	 */
	public List selectTableInfoByCompany(String company_name)throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List selectCompanyList()throws SQLException;

	/**
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public List selectCompanyListGroupByCompany_abbr(String date)throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List selectTableListByTableCount()throws SQLException;
	
	/**
	 * @param shippersTable
	 * @return
	 * @throws SQLException
	 */
	public List getTableByCompany(ShippersTable shippersTable) throws SQLException;
	
	/**
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public List getTableListByPage(int start, int end)throws SQLException;
	
	/**
	 * @param company_abbr
	 * @return
	 * @throws SQLException
	 */
	public int getLastTableIndex(String company_abbr)throws SQLException;
	
	/**
	 * @param table
	 * @return
	 */
	public List<ShippersTable> getTableListBycompany(ShippersTable table);
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getTotalTableList()throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List selectCompanyListGroupByPage()throws SQLException;
	
	/**
	 * @param shippersTable
	 * @return
	 * @throws SQLException
	 */
	public ShippersTable getTableById(ShippersTable shippersTable)throws SQLException;
	
	/**
	 * @param shippersTable
	 * @return
	 * @throws SQLException
	 */
	public ShippersTable getTableInfoByIndex(ShippersTable shippersTable)throws SQLException;
	
	/**
	 * @param page
	 * @return
	 * @throws SQLException
	 */
	public List selectTableInfoByPage(int page)throws SQLException;
	
	/**
	 * @param shippersTable
	 * @return
	 * @throws SQLException
	 */
	public List selectTablePageListByCompany(ShippersTable shippersTable)throws SQLException;
	
	/**
	 * @param shippersTable
	 * @return
	 * @throws SQLException
	 */
	public List selectTableCompanyListByPage(ShippersTable shippersTable) throws SQLException;
	
	/**
	 * @param tableId
	 * @return
	 * @throws SQLException
	 */
	public List selectParentPortList(String tableId) throws SQLException;
	
	/**
	 * @param tablePort
	 * @throws SQLException
	 */
	public void insertPortList(TablePort tablePort)throws SQLException;
	
	/**
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public List selectTableListByPage(ShippersTable table)throws SQLException;
	
	/**
	 * @param tablePort
	 * @return
	 * @throws SQLException
	 */
	public List getTablePortList(TablePort tablePort) throws SQLException;
	
	/**
	 * @param tablePort
	 * @return
	 * @throws SQLException
	 */
	public TablePort getTablePort(TablePort tablePort) throws SQLException;
	
	/**
	 * @param tablePort
	 * @throws SQLException
	 */
	public void deleteTablePort(TablePort tablePort) throws SQLException;
	
	/**
	 * @param port
	 * @return
	 * @throws SQLException
	 */
	public TablePort getTablePortByIndex(TablePort port)throws SQLException;
	
	/**
	 * @param port
	 * @throws SQLException
	 */
	public void updateTablePort(TablePort port) throws SQLException;
	
	/**
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public List getTablePortByDate(String date)throws SQLException;
	
	/**
	 * @param stable
	 * @return
	 * @throws SQLException
	 */
	public List selectTableInfoList(ShippersTable stable)throws SQLException;

	public int selectMaxPortIndex(String table_id)throws SQLException;

	public void updateTablePortIndex(TablePort port)throws SQLException;

	public int updateTableDate(String table_id, String date)throws SQLException;

	public boolean isPageHave(int page, String company)throws SQLException;

	public List getTableListByDate(ShippersTable data)throws SQLException;

	public int getTableCount(String date)throws SQLException;

	public List getTableProperty()throws SQLException;

	public List getTableProperty(String company_abbr, int page)throws SQLException;

	public void insertTableProperty(Table_Property propertis)throws SQLException;

	public void updateTableProperty(Table_Property property)throws SQLException;

	public void updateTablePortIndex2(TablePort port)throws SQLException;


	public List getScheduleTableListByDate(ShippersTable data)throws SQLException;

	public void updateTablePortCount(String table_id,int portcount)throws SQLException;

	public void updateTableVesselCount(String table_id,int vesselcount)throws SQLException;

	public List getTableList(ShippersTable table)throws SQLException;
	public Table_Property getTableProperty(String table_id)throws SQLException;
	public List getTableListByAgent(ShippersTable table)throws SQLException;
	public String getTableAgentByPage(int psge)throws SQLException;
	public int getPortCount(String tableId)throws SQLException;
	public int updateTablePortName(TablePort port)throws SQLException;
	
	public List getTableDateList()throws SQLException;
	
	public List selectSystemTableList() throws SQLException;
	public List selectTableColumnList(String table) throws SQLException;
	public List selectSystemDataList(String table_name) throws SQLException;
	
	public int updateTableDateByTableIDs(ShippersTable table) throws SQLException;
	

}
