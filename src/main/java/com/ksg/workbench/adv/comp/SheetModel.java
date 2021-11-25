package com.ksg.workbench.adv.comp;

import javax.swing.table.AbstractTableModel;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class SheetModel extends AbstractTableModel {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String columnNames[] = {"FilPath","FileName","SheetName","Use"};

	Object rowData[][];
	public SheetModel(Object rowData[][])
	{
		this.rowData=rowData;
	}

	public void downRow(int row)
	{
		if(row>rowData.length-2)
			return;
		Object[] temp=rowData[row+1];
		rowData[row+1]=rowData[row];
		rowData[row]=temp;

	}

	public Class getColumnClass(int column) {
		return (getValueAt(0, column).getClass());
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public int getRowCount() {
		return rowData.length;
	}

	public Object getValueAt(int row, int column) {
		return rowData[row][column];
	}

	public boolean isCellEditable(int row, int column) {
		return (column != 0);
	}
	public void setValueAt(Object value, int row, int column) {
		rowData[row][column] = value;
	}
	/**
	 * @param row
	 */
	public void upRow(int row)
	{
		if(row<1)
			return;
		Object[] temp=rowData[row-1];
		rowData[row-1]=rowData[row];
		rowData[row]=temp;
	}

}
