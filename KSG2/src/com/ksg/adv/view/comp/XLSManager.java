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
package com.ksg.adv.view.comp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Sheet;

import com.ksg.adv.logic.model.TableLocation;
import com.ksg.domain.ShippersTable;

/**
 * @author Administrator
 *
 */
public interface XLSManager {
	/**
	 * @param sheetName
	 * @param xlsFile
	 * @param table
	 * @throws FileNotFoundException
	 * @throws ADVTableNotMatchException
	 */
	public void readFile(String sheetName,String xlsFile,ShippersTable table) throws FileNotFoundException, ADVTableNotMatchException;
	/**
	 * @param xlsFile
	 * @param table
	 * @throws FileNotFoundException
	 * @throws ADVTableNotMatchException
	 */
	public void readFile(String xlsFile,ShippersTable table) throws FileNotFoundException, ADVTableNotMatchException;
	
	/**
	 * @param sheetList
	 * @param xlsFile
	 * @param table
	 * @return
	 * @throws FileNotFoundException
	 * @throws ADVTableNotMatchException
	 * @throws IOException
	 */
	public Vector<TableLocation> readFile(Vector sheetList, String xlsFile, ShippersTable table)
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
	public Vector<TableLocation> readFile(Vector pageList, Vector sheetList, String xlsFile, ShippersTable table)
	
	throws FileNotFoundException, ADVTableNotMatchException, IOException;
	/**
	 * @return
	 */
	public String getData();

	/**
	 * @return
	 */
	public Vector getXLSData();

	/**
	 * @return
	 */
	/**
	 * @return
	 */
	public int getSearchedTableCount();
	/**
	 * @param sheet
	 * @param tableLocation
	 * @return
	 * @throws ADVTableNotMatchException
	 */
	public Vector extractData(Sheet sheet,Vector<TableLocation> tableLocation)
	throws ADVTableNotMatchException;
	
	
	public abstract  void setPreData(List li);

	public abstract  Vector<XLSTableInfo> getXLSTableInfoList();
	public Vector<TableLocation> readFile(Vector sheetNameList)
	throws ADVTableNotMatchException, IOException; 
	

}
