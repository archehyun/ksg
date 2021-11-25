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
package com.ksg.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Table_Property;
public interface TableService {
	/**
	 * @param table
	 * @return 삭제 결과
	 * @throws SQLException
	 */
	public int deleteTable(ShippersTable table) throws SQLException;
	/**
	 * @param company_abbr
	 * @return 테이블 인덱스
	 * @throws SQLException
	 */
	public int getLastTableIndex(String company_abbr)throws SQLException;
	/**
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> getTableListBycompany(ShippersTable table)throws SQLException;
	/**
	 * @param shipper_name
	 * @param table_id
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> getTableListBycompany(String shipper_name, String table_id, String date)throws SQLException;
	/**
	 * @param string
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> getTableListByCompany(String company_abbr)throws SQLException;
	
	public List<ShippersTable> getTableListByCompany(String company_abbr,int page)throws SQLException;
	/**
	 *  
	 * @설명 테이블 정보 조회
	 * @param company_abbr:선사명
	 * @param date:날짜
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> getTableListByCompany(String company_abbr,String date)throws SQLException;
	/**
	 * 
	 * @param company_abbr 선사명
	 * @param table_id 테이블 아읻
	 * @param date 날짜
	 * @param p_index
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> getTableListByCompany(String company_abbr,String table_id, String date, int p_index)throws SQLException;
	
	/**
	 * @param start
	 * @param end
	 * @return
	 * @throws SQLException
	 */
	public List getTableListByPage(int start, int end)throws SQLException;
	/**
	 * @param table
	 * @throws SQLException
	 */
	public ShippersTable insertTable(ShippersTable table) throws SQLException;
	public List selectCompanyList()throws SQLException;
	public List selectCompanyListGroupByCompany_abbr()throws SQLException;

	public List selectCompanyListGroupByCompany_abbr(String date)throws SQLException;


	public List selectTableInfoByCompany(String company_name) throws SQLException;

	public List selectTableListByTableCount() throws SQLException;
	/**
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public int updateTable(ShippersTable table) throws SQLException;
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public int updateTableDate(ADVData data)throws SQLException ;
	public int updateTableDate(String table_id, String date)throws SQLException ;

	public List<ShippersTable> getTableListOrderByCompany(ShippersTable table)	throws SQLException;
	public List<ShippersTable> getTableListOrderByPage(ShippersTable table)	throws SQLException;
	public List getTotalTableList()throws SQLException;
	public List selectCompanyListGroupByPage()throws SQLException;
	public ShippersTable getTableById(Object tableId) throws SQLException;
	public ShippersTable getTableInfoByIndex(String company_abbr,int table_index, int page)throws SQLException;
	public List selectTableInfoByPage(int page)throws SQLException;
	public List getTablePageListByCompany(String company_abbr)throws SQLException;
	public List getTableListByPage(ShippersTable table)throws SQLException;
	public List getTableCompanyListByPage(int parseInt) throws SQLException;
	public List getSubPortList(String tableId);
	public List<TablePort> getParentPortList(String tableId) throws SQLException;
	public void insertPortList(TablePort tablePort)throws SQLException;
	public List<TablePort> getTablePortList(TablePort tablePort) throws SQLException;
	
	public void deleteTablePort(TablePort tablePort) throws SQLException;
	public TablePort getTablePort(TablePort port) throws SQLException;
	public void updateTablePort(TablePort port)throws SQLException;
	public TablePort getTablePortByIndex(TablePort port)throws SQLException;
	public List selectTableInfoList(ShippersTable stable)throws SQLException;
	public int getMaxPortIndex(String table_id)throws SQLException;
	public void updateTablePortIndex(TablePort port)throws SQLException;
	public boolean isPageHave(int parseInt, String text)throws SQLException;
	public int getTableCount(String date)throws SQLException;
	public List getTableProperty()throws SQLException;
	public List getTableProperty(String company_abbr, int page)throws SQLException;
	public void insertTableProperty(Table_Property p)throws SQLException;
	public void updateTableProperty(Table_Property property)throws SQLException;
	public void updateTablePortIndex2(TablePort port)throws SQLException;
	public List<ShippersTable> getTableListByDate(ShippersTable data)throws SQLException;
	public List getScheduleTableListByDate(ShippersTable searchOption)throws SQLException;
	public void updateTablePortCount(String table_id,int portcount)throws SQLException;
	public void updateTableVesselCount(String table_id,int vesselcount)throws SQLException;
	public List getTableList(ShippersTable table)throws SQLException;
	public int updateTableDateAll(ShippersTable table) throws SQLException;
	public int updateTableDateByTableIDs(List table, String updateDate) throws SQLException;
	public Table_Property getTableProperty(String table_id)throws SQLException;
	public List getTableListByAgent(ShippersTable table)throws SQLException;
	public String getTableAgentByPage(int page)throws SQLException;
	public int getPortCount(String table_id)throws SQLException;
	public int updateTablePortName(TablePort port) throws SQLException;
	/** 테이블 업데이트 날짜 목록 조회
	 * @return
	 * @throws SQLException
	 */
	public List getTableDateList() throws SQLException;
	public List selectSystemTableList() throws SQLException;
	public List<String> selectTableColumnList(String table) throws SQLException;
	public List selectSystemDataList(String table_name) throws SQLException;


}
