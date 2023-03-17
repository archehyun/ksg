package com.ksg.view.comp.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.ksg.view.comp.KSGViewUtil;
import com.ksg.view.comp.table.model.TableModel;

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
	
	private DefaultTableCellRenderer renderer;
	
	private boolean isOdd= true;
	
	private KSGViewUtil propeties = KSGViewUtil.getInstance();
	
	private int HEADER_HEIGHT;
	
	private int ROW_HEIGHT;
	
	private int FONT_SIZE;
	
	private static Color GRID_COLOR;
	
	private static Color ODD_COLOR;

	public KSGAbstractTable() {

		model = new TableModel();
		
        HEADER_HEIGHT=Integer.parseInt(propeties.getProperty("table.header.height"));

		ROW_HEIGHT=Integer.parseInt(propeties.getProperty("table.row.height"));
		
		GRID_COLOR = getColor(propeties.getProperty("table.girdcolor"));
		
		ODD_COLOR = getColor(propeties.getProperty("table.oddcolor"));
		
		FONT_SIZE = Integer.parseInt(propeties.getProperty("table.font.size"));
		
		isOdd =Boolean.parseBoolean(propeties.getProperty("table.row.odd"));
		
		this.setGridColor(GRID_COLOR);
		
		this.setRowHeight(ROW_HEIGHT);
		
		this.setFontSize(FONT_SIZE);

		// 컬럼 리사이징 오프
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// 정렬 기능 추가
		setRowSorter(new TableRowSorter<TableModel>(model));
		
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}
	
	
	
	private void setFontSize(int size)
	{
		Font currentFont=this.getFont();
		this.setFont(new Font(currentFont.getName(),currentFont.getStyle(), size));
		
	}
	
	public void setOdd(boolean isOdd)
	{
		this.isOdd = isOdd;
	}
	
	public KSGAbstractTable(TableModel model) {


		this.model = model;
		

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
	
    private Color getColor(String param)
	{
		String index[] = param.split(",");
		return new Color(Integer.parseInt(index[0].trim()),Integer.parseInt(index[1].trim()), Integer.parseInt(index[2].trim()));
	}

	public void initComp() {
		super.setModel(model);

		TableColumnModel colmodel = getColumnModel();


		for (int i = 0; i < colmodel.getColumnCount(); i++) {
			
			
			DefaultTableCellRenderer cellRenderer = getCellRenderer();
			
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
			
			
			
			if(isOdd)
			{
				if (isSelected) {
					foreground = Color.WHITE;
					background = new Color(51, 153, 255);
				} else {

					if (row % 2 == 0) {
						foreground = Color.black;
						background = Color.WHITE;
					} else {
						background = ODD_COLOR;
						foreground = Color.black;
					}
				}
				setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
				renderer.setForeground(foreground);
				renderer.setBackground(background);
			}
			else
			{
				if (isSelected) {
					foreground = Color.WHITE;
					background = new Color(51, 153, 255);
				} else {

					foreground = Color.black;
					background = Color.WHITE;
				}
				setBorder(BorderFactory.createCompoundBorder(getBorder(), padding));
				renderer.setForeground(foreground);
				renderer.setBackground(background);
			}
			
			
			

			return renderer;
		}

	}

	public Object getValueAt(int rowIndex, String columnField) {
		return model.getValueAt(rowIndex, columnField);

	}

}
