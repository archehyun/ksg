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
package com.ksg.service.impl;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.StringTokenizer;



import com.ksg.common.util.KSGDateUtil;
import com.ksg.dao.AdvDAO;
import com.ksg.dao.impl.AdvDAOImpl;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.service.ADVService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unchecked")
public class ADVServiceImpl extends AbstractServiceImpl implements ADVService{

	
	private AdvDAO advDAO;
	public ADVServiceImpl() {
		super();
		advDAO = new AdvDAOImpl();
	}
	
	public List getShippers(String gropBy) throws SQLException {
		
		return advDAO.getShippers(gropBy);
	}
	
	public ADVData getADVData(String table_id, String date_isusse,String company) throws SQLException
	{
		ADVData data = new ADVData();
		data.setTable_id(table_id);
		try {
			data.setDate_isusse(KSGDateUtil.toDate2(date_isusse));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		data.setCompany_abbr(company);
		
		return advDAO.getADVData(data);
	}
	
	public String[][] createADVTableModel(String data) 
	{
		log.debug("data:\n"+data);
		
//		StringTokenizer st = new StringTokenizer(data,"\n\n");
		String words[] = data.split(ADVData.ROW_DIVIDER);
//		log.debug("data row:"+st.countTokens());
		String[][] a = new String[words.length][];
		for(int i=0;i<a.length;i++)
		{
			StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);
			a[i]=new String[st2.countTokens()];
			for(int j=0;j<a[i].length;j++)
			{
				a[i][j]=st2.nextToken();
			}
		}
		
		return a;
	}
	
	public ADVData insertADVData(ADVData data) throws SQLException {
		return advDAO.insertADVData(data);
	}
	
	public void updateADVData(ADVData data) throws SQLException {
		advDAO.updateADVData(data);
		
	}

	
	public List getADVDataList(String shipper, String date) throws SQLException {
		ADVData data = new ADVData();
		data.setCompany_abbr(shipper);
		try {
			data.setDate_isusse(KSGDateUtil.toDate2(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return advDAO. getADVDataList(data);
	}

	
	public String getQuarkFormat(int tableId) throws SQLException {
		return advDAO.getQuarkFormat(tableId);
		
	}
	
	public List getQuarkFormatList(String company, String date)
			throws SQLException {
		
		return advDAO.getQuarkFormatList(company,date);
		
	}
	
	public List<ShippersTable> getPageList(String gropBy) throws SQLException {
		
		return advDAO.getPageList(gropBy);
	}
	
	public List<ShippersTable> getTableByShipperByDate(String shipper_name) throws SQLException {
		ShippersTable table = new ShippersTable();
		table.setCompany_abbr(shipper_name);

		return advDAO.getTableByShipperByDate(table);
	}
	

	public List getADVDataListAddOrder(String company_abbr, String date) throws SQLException {
		ADVData data = new ADVData();
		data.setCompany_abbr(company_abbr);
		try {
			data.setDate_isusse(KSGDateUtil.toDate2(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return advDAO. getADVDataListAddOrder(data);
	}
	
	public int removeADVData(String table_id, String date) throws SQLException {
		return advDAO.removeADVData(table_id,date);
		
	}
	
	public List getADVDataListByDate(String date) throws SQLException {
		
		return advDAO.getADVDataListByDate(date);
	}

	public List getCompanyList() throws SQLException {
		
		return advDAO.getCompanyList();
	}

	public List getADVDataList() throws SQLException {
		
		return advDAO. getADVDataList();
	}

	public ADVData getADVData(String table_id, String date_isusse)
			throws SQLException {
		ADVData data = new ADVData();
		data.setTable_id(table_id);
		try {
			data.setDate_isusse(KSGDateUtil.toDate2(date_isusse));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return advDAO.getADVData(data);
	}

	public List<ADVData> getADVDataList(String selectedCompany, int selectedPage)
			throws SQLException {
		
		ADVData data = new ADVData();
		data.setCompany_abbr(selectedCompany);
		data.setPage(selectedPage);
		return advDAO. getADVDataList(data);
	}

	public List<ADVData> getADVDataList(String selectedCompany,
			int selectedPage, String selectDate) throws SQLException {
		ADVData data = new ADVData();
		data.setCompany_abbr(selectedCompany);
		data.setPage(selectedPage);
		try {
			data.setDate_isusse(KSGDateUtil.toDate2(selectDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return advDAO. getADVDataList(data);
	}

	
	public List<ADVData> getADVDataListByPage(String selectedCompany,
			int selectedPage, String selectDate) throws SQLException {
		ADVData data = new ADVData();
		data.setCompany_abbr(selectedCompany);
		data.setPage(selectedPage);
		try {
			data.setDate_isusse(KSGDateUtil.toDate2(selectDate));
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		return advDAO. getADVDataListByPage(data);
	}

	public List getADVDataListByTableID(String tableId) throws SQLException {
		ADVData data = new ADVData();
		data.setTable_id(tableId);
		return advDAO. getADVDataList(data);
	}

	public int getADVCount(String tableId) throws SQLException {
		
		return advDAO. getADVCount(tableId);
	}

	public ADVData getADVTopOne(String tableId) throws SQLException {
		
		return advDAO. getADVTopOne(tableId);
	}

	public void removeADVDataTopOne(String tableId) throws SQLException {
		advDAO.removeADVDataTopOne(tableId);
		
	}

	public void removeADVData(String table_id) throws SQLException {
		advDAO.removeADVData(table_id);
	}

	public void updateDateADVData(String table_id, String date)
			throws SQLException {
		advDAO.updateDateADVData(table_id,date);
	}

	public ADVData getADVData(String table_id) throws SQLException {
		
		
		return advDAO.getADVData(table_id);
	}
	public ADVData getADVData(ADVData data) throws SQLException {
		
		return advDAO.getADVData(data);
	}

	public void updateDataADVData(ADVData data) throws SQLException {
		advDAO.updateDataADVData(data);
		
	}

	

	

	

}
