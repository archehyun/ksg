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
package com.ksg.adv.logic.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Sheet;

import com.ksg.domain.ShippersTable;

/**
 * @author 박창현
 *
 */
public class TableLocation extends ShippersTable{
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public static final int NONE=0;
	
	public static final int VESSEL=1;
	
	public static final int VOYAGE=2;
	
	public static final int BOTH=3;
	
	protected int vesselvoycount;
	
	protected String vesselvoydivider;
	
	protected int tableType;
	
	public int getTableType() {
		return tableType;
	}
	public void setTableType(int tableType) {
		this.tableType = tableType;
	}
	public TableLocation() {
		
	}
	public TableLocation(Sheet sheet) {
		this.sheet=sheet;
	}
	
	private Sheet sheet;  // 쉬트 정보
	
	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	private int col;
	
	private int row;
	
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	
	protected boolean hasVoy=true;
	
	private HSSFCell keyWordCell;
	
	private HSSFCell titleCell;
	
	private boolean isUnderPort=false;
	
	public void setKeyWordCell(HSSFCell keyWordCell) {
		
		this.keyWordCell =keyWordCell;
		HSSFRow rows = keyWordCell.getSheet().getRow(this.keyWordCell.getRowIndex()-1);
		if(rows==null)
			return;
		this.setTitleCell(rows.getCell(this.keyWordCell.getColumnIndex()));
		
	}
	public HSSFCell getKeyWordCell() {
		return keyWordCell;
	}
	public HSSFCell getTitleCell() {
			
		return titleCell;
	}
	public HSSFCell getTitleCell(int i) {
		
		HSSFRow rows = keyWordCell.getSheet().getRow(this.keyWordCell.getRowIndex()-i);
		if(rows==null)
			return null;
		
		return rows.getCell(this.keyWordCell.getColumnIndex());		
	}
	public void setTitleCell(HSSFCell titleCell) {
		this.titleCell = titleCell;
	};
	public String toString()
	{
		return "[table:"+row+","+col+","+keyWordCell+"]";
	}
	public void setUnderPort(boolean isUnderPort) {
		this.isUnderPort = isUnderPort;
	}
	public boolean isUnderPort() {
		return isUnderPort;
	}
	public int getVesselvoycount() {
		return vesselvoycount;
	}

	public String getVesselvoydivider() {
		return vesselvoydivider;
	}
	public boolean isHasVoy() {
		return hasVoy;
	}
	public void setHasVoy(boolean hasVoy) {
		this.hasVoy = hasVoy;

	}
	public void setVesselvoycount(int vesselvoycount) {
		this.vesselvoycount = vesselvoycount;
	}
	public void setVesselvoydivider(String vesselvoydivider) {
		this.vesselvoydivider = vesselvoydivider;
		
		if(vesselvoydivider.equals("공백"))
		{
			vesselvoydivider=" ";
		}
		if(vesselvoydivider.equals("Enter"))
		{
			vesselvoydivider="\n";
		}

	}
	
}
