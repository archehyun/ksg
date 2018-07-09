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
package com.ksg.adv.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.ADVData;
import com.ksg.domain.Company;
import com.ksg.domain.ShippersTable;

public interface ADVService {
	/**
	 * @param data
	 * @return
	 */
	public String[][] createADVTableModel(String data);
	
	/**
	 * @param table_id
	 * @param date_isusse
	 * @param company
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public ADVData getADVData(String table_id, String date_isusse, String company) throws SQLException;
	@Deprecated
	public ADVData getADVData(String table_id, String date_isusse) throws SQLException;
	/**
	 * @param shipper
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public List getADVDataList(String shipper, String date) throws SQLException;
	
	/**
	 * @param company_abbr
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public List getADVDataListAddOrder(String company_abbr, String date) throws SQLException;
	
	/**
	 * @param gropBy
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> getPageList(String gropBy) throws SQLException;

	/**
	 * @param tableId
	 * @return
	 * @throws SQLException
	 */
	public String getQuarkFormat(int tableId)throws SQLException;
	
	/**
	 * @param company
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public List getQuarkFormatList(String company, String date)throws SQLException;

	/**
	 * @param gropBy
	 * @return
	 * @throws SQLException
	 */
	public List<Company> getShippers(String gropBy) throws SQLException;
		
	
	/**
	 * @param string
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> getTableByShipperByDate(String string)throws SQLException;
	
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public ADVData insertADVData(ADVData data) throws SQLException;

	/**
	 * @param data
	 * @throws SQLException
	 */
	public void updateADVData(ADVData data) throws SQLException;

	/**
	 * @param company
	 * @param date
	 * @throws SQLException
	 */
	public int removeADVData(String company, String date) throws SQLException;
	
	public List getADVDataListByDate(String date) throws SQLException;
	public List<Company> getCompanyList() throws SQLException;

	public List getADVDataList()throws SQLException;

	public List<ADVData> getADVDataList(String selectedCompany, int selectedPage)throws SQLException;

	public List<ADVData> getADVDataList(String selectedCompany,
			int selectedPage, String selectDate)throws SQLException;

	public List<ADVData> getADVDataListByPage(String selectedCompany,
			int selectedPage, String selectDate)throws SQLException;

	public List getADVDataListByTableID(String tableId)throws SQLException;

	public int getADVCount(String tableId)throws SQLException;
	@Deprecated
	public ADVData getADVTopOne(String tableId)throws SQLException;
	@Deprecated
	public void removeADVDataTopOne(String tableId)throws SQLException;
	
	public void removeADVData(String table_id)throws SQLException;

	public void updateDateADVData(String table_id, String date)throws SQLException;

	public ADVData getADVData(String table_id) throws SQLException;
	public ADVData getADVData(ADVData data) throws SQLException;
	public void updateDataADVData(ADVData data)throws SQLException;


}
