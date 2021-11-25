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
package com.ksg.dao;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public interface AdvDAO {
	
	/**
	 * @param gropBy
	 * @return
	 * @throws SQLException
	 */
	public List getShippers(String gropBy) throws SQLException;	
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public ADVData getADVData(ADVData data) throws SQLException;
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
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public List getADVDataList(ADVData data) throws SQLException;
	
	/**
	 * @param tableId
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
	public List<ShippersTable> getPageList(String gropBy)throws SQLException;
	/**
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public List<ShippersTable> getTableByShipperByDate(ShippersTable table)throws SQLException;
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public List getADVDataListAddOrder(ADVData data)throws SQLException;
	/**
	 * @param company
	 * @param date
	 * @throws SQLException 
	 */
	public int removeADVData(String company, String date) throws SQLException;
	
	/**
	 * @param date
	 * @return
	 * @throws SQLException
	 */
	public List getADVDataListByDate(String date) throws SQLException;
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getCompanyList() throws SQLException;
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getADVDataList()throws SQLException;
	/**
	 * @param tableId
	 * @return
	 * @throws SQLException
	 */
	public ShippersTable getTableById(ShippersTable tableId)throws SQLException;
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public List<ADVData> getADVDataListByPage(ADVData data)throws SQLException;
	/**
	 * @param tableId
	 * @return
	 * @throws SQLException
	 */
	public int getADVCount(String tableId)throws SQLException;
	/**
	 * @param tableId
	 * @return
	 * @throws SQLException
	 */
	@Deprecated
	public ADVData getADVTopOne(String tableId)throws SQLException;
	/**
	 * @param tableId
	 * @throws SQLException
	 */
	@Deprecated
	public void removeADVDataTopOne(String tableId)throws SQLException;
	/**
	 * @param table_id
	 * @throws SQLException
	 */
	public void removeADVData(String table_id)throws SQLException;
	/**
	 * @param table_id
	 * @param date
	 * @throws SQLException
	 */
	public void updateDateADVData(String table_id, String date)throws SQLException;
	/**
	 * @param data
	 * @throws SQLException
	 */
	public void updateDataADVData(ADVData data)throws SQLException;
	/**
	 * @param table_id
	 * @return
	 * @throws SQLException
	 */
	public ADVData getADVData(String table_id)throws SQLException;
}
