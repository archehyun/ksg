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
package com.ksg.adv.logic.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;

import com.ksg.adv.logic.model.TableLocation;
import com.ksg.adv.service.ADVService;
import com.ksg.adv.view.comp.ADVTableNotMatchException;
import com.ksg.adv.view.comp.XLSTableInfo;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.ShippersTable;
import com.ksg.shippertable.service.TableService;

/**
 * @author Administrator
 *
 */
public abstract class XLSReader {
	
	protected List preData;
	
	protected Sheet sheet;
	
	protected String vesselKeyWord[];
	
	protected String voyKeyWord[];
	
	protected String bothKeyWord[];	
	
	protected String xlsFile;
	
	protected FormulaEvaluator evaluator;
	
	
	protected String company;
	
	protected String data="";
	
	protected Vector datas = new Vector();
	
	protected TableService tableService;
	
	protected ADVService service;
	
	protected BaseService baseService;
	
	DAOManager manager = DAOManager.getInstance();
	
	protected KSGPropertis propertis = KSGPropertis.getIntance();
	
	protected Vector<TableLocation> tableLocationList;
	
	protected Vector xlsTableInfoList;
	
	public XLSReader()
	{
		tableService = manager.createTableService();
		baseService =manager.createBaseService();
		service = manager.createADVService();
	}
	protected Logger logger = Logger.getLogger(this.getClass());
	/**
	 * @param sheetName
	 * @param xlsFile
	 * @param table
	 * @throws FileNotFoundException
	 * @throws ADVTableNotMatchException
	 */
	public abstract void readFile(String sheetName,String xlsFile,ShippersTable table) throws FileNotFoundException, ADVTableNotMatchException;
	/**
	 * @param xlsFile
	 * @param table
	 * @throws FileNotFoundException
	 * @throws ADVTableNotMatchException
	 */
	public abstract void readFile(String xlsFile,ShippersTable table) throws FileNotFoundException, ADVTableNotMatchException;
	
	/**
	 * @param sheetList
	 * @param xlsFile
	 * @param table
	 * @return
	 * @throws FileNotFoundException
	 * @throws ADVTableNotMatchException
	 * @throws IOException
	 */
	public abstract Vector<TableLocation> readFile(Vector sheetList, String xlsFile, ShippersTable table)
	throws FileNotFoundException, ADVTableNotMatchException, IOException;
	
	/**
	 * @param pageList
	 * @param sheetList
	 * @param xlsFile
	 * @param table
	 * @return
	 * @throws FileNotFoundException
	 * @throws ADVTableNotMatchException
	 * @throws IOException
	 */
	public abstract Vector<TableLocation> readFile(Vector pageList, Vector sheetList, String xlsFile, ShippersTable table)
	
	throws FileNotFoundException, ADVTableNotMatchException, IOException;
	/**
	 * @return
	 */
	public String getData() {
		return data;
	}
	
	public Vector getXLSData() {
		// TODO Auto-generated method stub
		return datas;
	}
	
	/**
	 * @param cell
	 * @return
	 */
	public String getColumString(HSSFCell cell)
	{
		if(cell==null)
			return "";

		switch (cell.getCellType()) 
		{
		case HSSFCell.CELL_TYPE_STRING:

			return cell.getRichStringCellValue().toString();
		default:

			break;
		}
		return "";
	}

	
	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public abstract int getSearchedTableCount();
	/**
	 * @param sheet
	 * @param tableLocation
	 * @return
	 * @throws ADVTableNotMatchException
	 */

	public abstract Vector<TableLocation> readFile(Vector sheetNameList)
	throws ADVTableNotMatchException, IOException;
	
	public Vector extractData(Sheet sheet,Vector<TableLocation> tableLocation)
			throws ADVTableNotMatchException 
	{
		logger.info("<===="+xlsFile+": data extract start ====>");
		
		xlsTableInfoList = new Vector();
		
		Vector datas2 = new Vector();
		
		long start= System.currentTimeMillis();
		
		for(int i=0;i<tableLocation.size();i++)
		{
			// 기존 테이블 정보
			ShippersTable tableInfo = (ShippersTable) preData.get(i);
			// row의 위치 정보
			TableLocation location =(TableLocation) tableLocation.get(i);// 테이블의 시작 위치

			XLSTableInfo info = new XLSTableInfo(tableInfo, location);
			info.setTableInfo(tableInfo);
			info.setTable_id(tableInfo.getTable_id());
			//			datas2.add(info.getTableStringInfo());
			xlsTableInfoList.add(info);
			//KSGModelManager.getInstance().processBar.setValues(i);
		}
		
		long end= System.currentTimeMillis();
		logger.debug("search data time:"+(end-start));

		return datas2;

	}
	public void setPreData(List li) {
		this.preData=li;

	}
	public Vector getXLSTableInfoList() {
		// TODO Auto-generated method stub
		return xlsTableInfoList;
	}
	

}
