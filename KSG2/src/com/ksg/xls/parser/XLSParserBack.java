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
package com.ksg.xls.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Sheet;

import com.ksg.domain.ShippersTable;
import com.ksg.xls.ADVTableNotMatchException;
import com.ksg.xls.model.TableLocation;

public class XLSParserBack extends XLSParser{

	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getSearchedTableCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Vector getXLSData() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPreData(List li) {
		// TODO Auto-generated method stub
		
	}

	public Vector getErrorList() {
		// TODO Auto-generated method stub
		return null;
	}

	public void readFile(String sheetName, String xlsFile, ShippersTable table)
			throws FileNotFoundException, ADVTableNotMatchException {
		// TODO Auto-generated method stub
		
	}

	public void readFile(String xlsFile, ShippersTable table)
			throws FileNotFoundException, ADVTableNotMatchException {
		// TODO Auto-generated method stub
		
	}

	public Vector<TableLocation> readFile(Vector sheetList, String xlsFile, ShippersTable table)
			throws FileNotFoundException, ADVTableNotMatchException {
	return null;
		
	}

	public Vector<TableLocation> readFile(Vector pageList, Vector sheetList, String xlsFile,
			ShippersTable table) throws FileNotFoundException,
			ADVTableNotMatchException, IOException {
				return sheetList;
		// TODO Auto-generated method stub
		
	}

	public Vector getXLSTableInfoList() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector extractData(Sheet sheet, Vector<TableLocation> tableLocation)
			throws ADVTableNotMatchException {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector<TableLocation> readFile(Vector sheetNameList)
			throws ADVTableNotMatchException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
