package com.ksg.view.comp.table;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
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
 * 
 * 기본
 * 1. row 사이즈 : 30
 * 2. 컬럼 리사이즈 : Off

 */
@SuppressWarnings("serial")
public class KSGAbstractTable extends JTable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private TableModel model;
	
	DefaultTableCellRenderer renderer;

	public KSGAbstractTable() {


		model = new TableModel();

		// 컬럼 리사이징 오프
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// 정렬 기능 추가
		setRowSorter(new TableRowSorter<TableModel>(model));
		
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}

	private DefaultTableCellRenderer getCellRenderer()
	{
		
		if(renderer== null)
		{
			renderer = new KSGTableCellRenderer();
		}
		
		return renderer;
	}
	public void setCellRenderer(DefaultTableCellRenderer renderer)
	{
		this.renderer = renderer;
	}

	public void setColumnName(KSGTableColumn columnNames[]) {
		this.model.setColumns(columnNames);
	}

	public void initComp() {
		super.setModel(model);

		TableColumnModel colmodel = getColumnModel();


		for (int i = 0; i < colmodel.getColumnCount(); i++) {
			
			
			DefaultTableCellRenderer cellRenderer = new KSGTableCellRenderer();
			TableColumn namecol = colmodel.getColumn(i);

			namecol.setCellRenderer(cellRenderer);
			
			KSGTableColumn col = model.getColumn(i);

			cellRenderer.setHorizontalAlignment(col.ALIGNMENT);
			

			if(col.maxSize!=0)
			{
				namecol.setMaxWidth(col.size);
			}

			if(col.minSize!=0)
			{
				namecol.setMinWidth(col.minSize);
			}

			if(col.size!=0)
			{
				namecol.setPreferredWidth(col.size);
			}
		}
		this.setRowHeight(30);
		
		

		DefaultTableCellRenderer renderer =  
				(DefaultTableCellRenderer)this.getTableHeader().getDefaultRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);
		this.getTableHeader().setDefaultRenderer(renderer);



	}

	public void addColumn(KSGTableColumn column) {

		model.addColumn(column);

	}

	/**
	 * 결과 저장
	 * @param resultData
	 */
	@SuppressWarnings("rawtypes")
	public void setResultData(List resultData) {

		this.model.setData(resultData);

		model.fireTableDataChanged();
		// 결과 저장후 화면 갱신
		this.updateUI();
	}

	public Object getValueAt(int rowIndex) {
		return model.getValueAt(rowIndex);
	}

	/**
	 * 데이터 초기화
	 */
	public void clearReslult() {
		model.clearResult();
	}

	/**

	 * @FileName : KSGTable.java

	 * @Date : 2021. 2. 26. 

	 * @작성자 : 박창현

	 * @변경이력 :

	 * @프로그램 설명 : 사용자 정의 테이블 모델

	 */
	class TableModel extends AbstractTableModel {


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
				updateUI();
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

	/**

	 * @FileName : KSGTable.java

	 * @Date : 2021. 2. 26. 

	 * @작성자 : 박창현

	 * @변경이력 :

	 * @프로그램 설명 : 테이블 셀 렌더러
	 * 
	 * 홀수 반복 색 표시

	 */
	class KSGTableCellRenderer extends DefaultTableCellRenderer {
		
		int leftPadding = 10;
		int rightPadding = 10;
		
		Border padding = BorderFactory.createEmptyBorder(0, leftPadding, 0, rightPadding);

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
			setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
			renderer.setForeground(foreground);
			renderer.setBackground(background);

			return renderer;
		}

	}

	public Object getValueAt(int rowIndex, String columnField) {
		return model.getValueAt(rowIndex, columnField);

	}

}
