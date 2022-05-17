package com.ksg.view.comp.table.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.ksg.view.comp.table.KSGTableColumn;

public class TableModel extends AbstractTableModel {


	@SuppressWarnings("rawtypes")
	private List data;

	private List<KSGTableColumn> columnNames;


	public TableModel() {
		columnNames = new LinkedList<KSGTableColumn>();
	}

	public List getData() {
		return data;
	}

	public void addColumn(KSGTableColumn column) {

		columnNames.add(column);

	}

	public KSGTableColumn getColumn(int col) {
		return columnNames.get(col);
	}

	public void setData(List data) {
		this.data = data;
	}

	@Override
	public String getColumnName(int index) {

		KSGTableColumn column = columnNames.get(index);


		return column.columnName;
	}

	public void setColumns(KSGTableColumn columns[]) {

		columnNames = Arrays.asList(columns);
	}

	/**
	 * 데이터 초기화
	 */
	public void clearResult() {

		if (data != null) {
			data.clear();
			fireTableDataChanged();
		}
	}

	@Override
	public int getRowCount() {
		if (data == null)
			return 0;

		return data.size();
	}

	@Override
	public int getColumnCount() {

		if (columnNames == null)
			return 0;

		return columnNames.size();
	}

	public Object getValueAt(int rowIndex) {

		try {

			return data.get(rowIndex);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		try {
			HashMap<String, Object> item = (HashMap<String, Object>) data.get(rowIndex);

			KSGTableColumn colum =columnNames.get(columnIndex);

			Object obj = item.get(colum.columnField);

			return colum.getValue(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getValueAt(int rowIndex, String columnField) {

		try {

			HashMap<String, Object> item = (HashMap<String, Object>) data.get(rowIndex);

			return item.get(columnField);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}