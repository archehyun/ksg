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

import org.apache.poi.ss.usermodel.Sheet;

import com.ksg.adv.logic.model.TableLocation;
import com.ksg.domain.ShippersTable;
import com.ksg.workbench.adv.comp.ADVTableNotMatchException;

public class XLSParserBack extends XLSParser{

	public String getData() {
		return null;
	}

	public int getSearchedTableCount() {
		return 0;
	}

	public Vector getXLSData() {
		return null;
	}

	public void setPreData(List li) {
		
	}

	public Vector getErrorList() {
		return null;
	}

	public void readFile(String sheetName, String xlsFile, ShippersTable table)
			throws FileNotFoundException, ADVTableNotMatchException {
		
	}

	public void readFile(String xlsFile, ShippersTable table)
			throws FileNotFoundException, ADVTableNotMatchException {
		
	}

	public Vector<TableLocation> readFile(Vector sheetList, String xlsFile, ShippersTable table)
			throws FileNotFoundException, ADVTableNotMatchException {
	return null;
		
	}

	public Vector<TableLocation> readFile(Vector pageList, Vector sheetList, String xlsFile,
			ShippersTable table) throws FileNotFoundException,
			ADVTableNotMatchException, IOException {
				return sheetList;
		
	}

	public Vector getXLSTableInfoList() {
		return null;
	}

	public Vector extractData(Sheet sheet, Vector<TableLocation> tableLocation)
			throws ADVTableNotMatchException {
		return null;
	}

	public Vector<TableLocation> readFile(Vector sheetNameList)
			throws ADVTableNotMatchException, IOException {
		return null;
	}

}
