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
package com.ksg.adv.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class AdvDAOImpl implements AdvDAO{
	private SqlMapClient sqlMap;

	public AdvDAOImpl() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#insertADVData(com.ksg.domain.ADVData)
	 */
	public ADVData insertADVData(ADVData data) throws SQLException {

		return (ADVData) sqlMap.insert("Adv.insertADV",data);

	}


	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getADVData(com.ksg.domain.ADVData)
	 */
	public ADVData getADVData(ADVData data) throws SQLException
	{
		return (ADVData) sqlMap.queryForObject("Adv.selectADVdata",data);
	}
	
	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getADVDataList(com.ksg.domain.ADVData)
	 */
	public List getADVDataList(ADVData data) throws SQLException {
		return sqlMap.queryForList("Adv.selectADVdataList",data);
	}
	
	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getShippers(java.lang.String)
	 */
	public List getShippers(String gropBy) throws SQLException
	{
		return sqlMap.queryForList("Adv.selectShipperList",gropBy);
	}
	
	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getCompanyList()
	 */
	public List getCompanyList()throws SQLException
	{
		return sqlMap.queryForList("Adv.selectCompanyList");
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#updateADVData(com.ksg.domain.ADVData)
	 */
	public void updateADVData(ADVData data) throws SQLException {
		sqlMap.update("Adv.updateADVData",data);

	}


	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getQuarkFormat(int)
	 */
	public String getQuarkFormat(int tableId) throws SQLException {
		return (String) sqlMap.queryForObject("Table.deleteTables",tableId);

	}

	/**
	 * @param table_id
	 * @return
	 * @throws SQLException
	 */
	public ShippersTable  getTableById(int table_id) throws SQLException {
		return (ShippersTable) sqlMap.queryForObject("Table.selectTableByID",table_id);
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getQuarkFormatList(java.lang.String, java.lang.String)
	 */
	public List getQuarkFormatList(String company, String date)
	throws SQLException {
		return null;
	}

	public List<ShippersTable> getPageList(String gropBy) throws SQLException {
		return sqlMap.queryForList("Adv.selectPageList",gropBy);
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getTableByShipperByDate(com.ksg.domain.ShippersTable)
	 */
	public List<ShippersTable> getTableByShipperByDate(ShippersTable table)
	throws SQLException {
		return sqlMap.queryForList("Table.selectTableByShipperListByDate",table);
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getADVDataListAddOrder(com.ksg.domain.ADVData)
	 */
	public List getADVDataListAddOrder(ADVData data) throws SQLException {
		return sqlMap.queryForList("Adv.selectADVdataListAddOrder",data);
	}

	public int removeADVData(String table_id, String date) throws SQLException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("table_id", table_id);
		map.put("date_isusse", date);

		return sqlMap.delete("Adv.deleteADVData", map);

	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getADVDataListByDate(java.lang.String)
	 */
	public List getADVDataListByDate(String date) throws SQLException {
		return sqlMap.queryForList("Adv.selectADVdataByDate",date);
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getADVDataList()
	 */
	public List getADVDataList() throws SQLException {
		return sqlMap.queryForList("Adv.selectTotalADVdataList");
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getTableById(com.ksg.domain.ShippersTable)
	 */
	public ShippersTable getTableById(ShippersTable table) throws SQLException {
		return (ShippersTable) sqlMap.queryForObject("Table.selectTableByID",table);
	}


	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getADVDataListByPage(com.ksg.domain.ADVData)
	 */
	public List<ADVData> getADVDataListByPage(ADVData data) throws SQLException {
		return sqlMap.queryForList("Adv.selectADVdataListByPage",data);
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getADVCount(java.lang.String)
	 */
	public int getADVCount(String table_id) throws SQLException {
		return (Integer)sqlMap.queryForObject("Adv.selectADVCount",table_id);
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#getADVTopOne(java.lang.String)
	 */
	public ADVData getADVTopOne(String table_id) throws SQLException {
		return (ADVData) sqlMap.queryForObject("Adv.selectADVTopOne",table_id);
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#removeADVDataTopOne(java.lang.String)
	 */
	public void removeADVDataTopOne(String table_id) throws SQLException {
		sqlMap.delete("Adv.deleteADVDataTopOne",table_id);

	}
	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#removeADVData(java.lang.String)
	 */
	public void removeADVData(String table_id) throws SQLException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("table_id", table_id);
		sqlMap.delete("Adv.deleteADVData",map);
	}

	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#updateDateADVData(java.lang.String, java.lang.String)
	 */
	public void updateDateADVData(String table_id, String date)
	throws SQLException {
		ADVData data = new ADVData();
		data.setTable_id(table_id);
		try {
			data.setDate_isusse(KSGDateUtil.toDate2(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sqlMap.update("Adv.updateADVDate",data);

	}
	/* (non-Javadoc)
	 * @see com.ksg.adv.service.AdvDAO#updateDataADVData(com.ksg.domain.ADVData)
	 */
	public void updateDataADVData(ADVData data) throws SQLException {

		sqlMap.update("Adv.updateADVData",data);

	}

	public ADVData getADVData(String table_id) throws SQLException {
		// TODO Auto-generated method stub
		return (ADVData) sqlMap.queryForObject("Adv.selectADVdataByTableID",table_id);
	}
}
