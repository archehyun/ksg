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
package com.ksg.dao.adv;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.dao.SqlMapManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.view.util.KSGDateUtil;

public class AdvDAOImpl implements AdvDAO{
	SqlMapClient sqlMap;

	public AdvDAOImpl() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ADVData insertADVData(ADVData data) throws SQLException {

		return (ADVData) sqlMap.insert("Adv.insertADV",data);

	}


	public ADVData getADVData(ADVData data) throws SQLException
	{
		return (ADVData) sqlMap.queryForObject("Adv.selectADVdata",data);
	}
	public List getADVDataList(ADVData data) throws SQLException {
		return sqlMap.queryForList("Adv.selectADVdataList",data);
	}
	public List getShippers(String gropBy) throws SQLException
	{
		return sqlMap.queryForList("Adv.selectShipperList",gropBy);
	}
	public List getCompanyList()throws SQLException
	{
		return sqlMap.queryForList("Adv.selectCompanyList");
	}

	public void updateADVData(ADVData data) throws SQLException {
		sqlMap.update("Adv.updateADVData",data);

	}


	public String getQuarkFormat(int tableId) throws SQLException {
		return (String) sqlMap.queryForObject("Table.deleteTables",tableId);

	}

	public ShippersTable  getTableById(int table_id) throws SQLException {
		return (ShippersTable) sqlMap.queryForObject("Table.selectTableByID",table_id);
	}

	public List getQuarkFormatList(String company, String date)
	throws SQLException {
		return null;
	}

	public List<ShippersTable> getPageList(String gropBy) throws SQLException {
		return sqlMap.queryForList("Adv.selectPageList",gropBy);
	}

	public List<ShippersTable> getTableByShipperByDate(ShippersTable table)
	throws SQLException {
		return sqlMap.queryForList("Table.selectTableByShipperListByDate",table);
	}

	public List getADVDataListAddOrder(ADVData data) throws SQLException {
		return sqlMap.queryForList("Adv.selectADVdataListAddOrder",data);
	}

	public int removeADVData(String table_id, String date) throws SQLException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("table_id", table_id);
		map.put("date_isusse", date);

		return sqlMap.delete("Adv.deleteADVData", map);

	}

	public List getADVDataListByDate(String date) throws SQLException {
		return sqlMap.queryForList("Adv.selectADVdataByDate",date);
	}

	public List getADVDataList() throws SQLException {
		return sqlMap.queryForList("Adv.selectTotalADVdataList");
	}

	public ShippersTable getTableById(ShippersTable table) throws SQLException {
		return (ShippersTable) sqlMap.queryForObject("Table.selectTableByID",table);
	}


	public List<ADVData> getADVDataListByPage(ADVData data) throws SQLException {
		// TODO Auto-generated method stub
		return sqlMap.queryForList("Adv.selectADVdataListByPage",data);
	}

	public int getADVCount(String table_id) throws SQLException {
		// TODO Auto-generated method stub
		return (Integer)sqlMap.queryForObject("Adv.selectADVCount",table_id);
	}

	public ADVData getADVTopOne(String table_id) throws SQLException {
		// TODO Auto-generated method stub
		return (ADVData) sqlMap.queryForObject("Adv.selectADVTopOne",table_id);
	}

	public void removeADVDataTopOne(String table_id) throws SQLException {
		sqlMap.delete("Adv.deleteADVDataTopOne",table_id);

	}
	public void removeADVData(String table_id) throws SQLException {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("table_id", table_id);
		sqlMap.delete("Adv.deleteADVData",map);
	}

	public void updateDateADVData(String table_id, String date)
	throws SQLException {
		ADVData data = new ADVData();
		data.setTable_id(table_id);
		try {
			data.setDate_isusse(KSGDateUtil.toDate2(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sqlMap.update("Adv.updateADVDate",data);

	}
	public void updateDataADVData(ADVData data)
	throws SQLException {


		sqlMap.update("Adv.updateADVData",data);

	}

	public ADVData getADVData(String table_id) throws SQLException {
		// TODO Auto-generated method stub
		return (ADVData) sqlMap.queryForObject("Adv.selectADVdataByTableID",table_id);
	}
}
