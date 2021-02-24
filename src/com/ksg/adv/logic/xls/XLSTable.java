package com.ksg.adv.logic.xls;

import org.apache.poi.ss.usermodel.Sheet;

import com.ksg.domain.ShippersTable;

public class XLSTable {
	public XLSTable(Sheet sheet, int row, int col) {
		this.sheet = sheet;
		this.setRow(row);
		this.setCol(col);
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	private Sheet sheet;
	private int row;
	private int col;
	private int tableIndex;
	private int tablePage;
	private int keyWordType;
	
	ShippersTable shippersTable;
	
	public ShippersTable getShippersTable() {
		return shippersTable;
	}
	public void setShippersTable(ShippersTable shippersTable) {
		this.shippersTable = shippersTable;
	}
	public void setKeyWordType(int keyWordType) {
		this.keyWordType=keyWordType;		
	}

}
