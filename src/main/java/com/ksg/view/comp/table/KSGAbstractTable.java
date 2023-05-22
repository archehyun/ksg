package com.ksg.view.comp.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.TableRowSorter;

import com.ksg.view.comp.KSGViewUtil;
import com.ksg.view.comp.table.model.TableModel;

import mycomp.comp.MyTable;
import mycomp.comp.table.MyTableModel;

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
public class KSGAbstractTable extends MyTable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private KSGViewUtil propeties = KSGViewUtil.getInstance();

	private int HEADER_HEIGHT;

	private int ROW_HEIGHT;

	private int FONT_SIZE;

	private static Color GRID_COLOR;


	public KSGAbstractTable() {

		super();

		HEADER_HEIGHT=Integer.parseInt(propeties.getProperty("table.header.height"));

		ROW_HEIGHT=Integer.parseInt(propeties.getProperty("table.row.height"));

		GRID_COLOR = Color.decode(propeties.getProperty("table.girdcolor"));

		ODD_COLOR = Color.decode(propeties.getProperty("grid.body.row.cell.odd"));

		FONT_SIZE = Integer.parseInt(propeties.getProperty("table.font.size"));

		selectedBackgroundColor = Color.decode(propeties.getProperty("grid.body.row.cell.selected"));

		isOdd =Boolean.parseBoolean(propeties.getProperty("table.row.odd"));

		this.setGridColor(GRID_COLOR);

		this.setRowHeight(ROW_HEIGHT);
		
		this.getFont().deriveFont((float)FONT_SIZE);

		// 컬럼 리사이징 오프
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// 정렬 기능 추가
		setRowSorter(new TableRowSorter<MyTableModel>(model));

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	
	public KSGAbstractTable(TableModel model) {

		super(model);

		// 컬럼 리사이징 오프
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// 정렬 기능 추가
		setRowSorter(new TableRowSorter<TableModel>(model));

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	}

}
