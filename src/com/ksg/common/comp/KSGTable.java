package com.ksg.common.comp;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

/**

  * @FileName : KSGTable.java

  * @Date : 2021. 2. 24. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 사용자 정의 JTable

  */
@SuppressWarnings("serial")
public class KSGTable extends JTable{
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private TableModel model;

	public KSGTable() {


		model = new TableModel();

		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// 정렬 기능 추가
		setRowSorter(new TableRowSorter<TableModel>(model));
		
		
		
		
		

	}
	


	public void setColumnName(KSGTableColumn columnNames[]) {
		this.model.setColumns(columnNames);
	}

	public void initComp() {
		super.setModel(model);

		TableColumnModel colmodel = getColumnModel();


		for (int i = 0; i < colmodel.getColumnCount(); i++) {
			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new KSGTableCellRenderer();

			namecol.setCellRenderer(renderer);
			KSGTableColumn col = model.getColumn(i);

			renderer.setHorizontalAlignment(SwingConstants.CENTER);

			//namecol.setHeaderRenderer(new IconHeaderRenderer());
			namecol.setPreferredWidth(col.size);

		}
		this.setRowHeight(30);

	}

	public void addColumn(KSGTableColumn column) {

		model.addColumn(column);

	}

	public void setResultData(List resultData) {

		this.model.setData(resultData);
		this.updateUI();
	}

	public Object getValueAt(int rowIndex) {
		return model.getValueAt(rowIndex);
	}

	public void clearReslult() {
		model.clearResult();
	}

	class TableModel extends AbstractTableModel {


		@SuppressWarnings("rawtypes")
		private List data;

		private List<KSGTableColumn> columnNames;

		//private KSGTableColumn columnNames[];

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

		public void clearResult() {

			if (data != null) {
				data.clear();
				updateUI();
			}
		}

		@Override
		public int getRowCount() {
			// TODO Auto-generated method stub
			if (data == null)
				return 0;

			return data.size();
		}

		@Override
		public int getColumnCount() {
			// TODO Auto-generated method stub

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

				return item.get(columnNames.get(columnIndex).columnField);
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

	class KSGTableCellRenderer extends DefaultTableCellRenderer {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			((JLabel) renderer).setOpaque(true);
			Color foreground, background;
			if (isSelected) {
				foreground = Color.WHITE;
				background = new Color(51, 153, 255);
			} else {

				if (row % 2 == 0) {
					foreground = Color.black;
					background = Color.WHITE;
				} else {
					background = new Color(225, 235, 255);
					foreground = Color.black;
				}
			}

			renderer.setForeground(foreground);
			renderer.setBackground(background);

			return renderer;
		}

	}

	public Object getValueAt(int rowIndex, String columnField) {
		return model.getValueAt(rowIndex, columnField);

	}

}
