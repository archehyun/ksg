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
package com.ksg.view.comp.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ShippersTable;
import com.ksg.shippertable.service.TableService;
import com.ksg.shippertable.service.impl.TableServiceImpl;
import com.ksg.shippertable.view.ShipperTableMgtUI;
import com.ksg.view.comp.ColorData;
import com.ksg.workbench.KSGViewParameter;
/**
 * @author archehyun
 *
 */
public class KSGSearchTable extends KSGTable{

	/**
	 * 
	 */
	private Font defaultFont = new Font("돋음",0,10);
	private static final long serialVersionUID = 1L;
	public static final int ADV_TYPE = 0;
	public static final int TABLE_TYPE = 1;
	private TableService 	_tableService = new TableServiceImpl();
	public String date;
	public String company;
	private DefaultTableModel model;
	private int tableCount;
	private KSGModelManager manager = KSGModelManager.getInstance();
	private KSGTableSelectListner listener;
	private String[] colums1;
	private ShipperTableMgtUI base;
	public KSGSearchTable(ShipperTableMgtUI base) {
		logger.info("search Table init");
		this.setName("KSGSearchTable");
		manager.addObservers(this);
		colums1 = KSGModelManager.getInstance().colums1;
		this.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setShowGrid(true);		
		this.setFont(defaultFont);		
		this.base=base;
		logger.debug("search Table init end ");
	}


	public void setLisnter(KSGTableSelectListner listner)
	{
		this.listener = listner;
	}


	private List<ShippersTable> initTableModel(String company_abbr,String date) throws SQLException
	{
		logger.info("init table model");
		List<ShippersTable> li =null;
		if(date!=null)
		{
			li=_tableService.getTableListByCompany(company_abbr,date);

		}else
		{
			li=_tableService.getTableListByCompany(company_abbr);
		}

		manager.searchedData=li;
		
		tableCount=li.size();	
		
		model = new KSGTableModel();
		
		logger.debug("init table model:"+li.size());
		return li;
	}

	// 전체 테이블 조회



	private void updateTableAction(List<ShippersTable> li) throws SQLException
	{
		model.setColumnCount(0);

		for(int i=0;i<colums1.length;i++)
		{
			model.addColumn(colums1[i]);
		}

		Iterator iter = li.iterator();
		while(iter.hasNext())
		{
			ShippersTable cell= (ShippersTable) iter.next();

			model.addRow(new Object[]{
					cell.getPage(),
					cell.getTable_index(),
					cell.getDate_isusse(),
					cell.getTable_id(),
					cell.getPort_col()== cell.getR_port_col()?cell.getPort_col():
						cell.getPort_col()+cell.getOthercell()==cell.getR_port_col()?
								new ColorData(ColorData.Black,cell.getPort_col()+"("+cell.getR_port_col()+")"):
						new ColorData(ColorData.RED,cell.getPort_col()+"("+cell.getR_port_col()+")"),
					cell.getVsl_row(),
					cell.getCompany_abbr(),
					cell.getTitle(),
					cell.getGubun(),
					cell.getTs_port(),
					cell.getIn_port(),
					cell.getIn_to_port(),
					cell.getOut_port(),
					cell.getOut_to_port(),
					cell.getAgent()
			});
			
		}
		this.setModel(model);
		
		
	}

	

	public void update(KSGModelManager manager) {
		try {
			
			logger.debug("start");
			date=manager.selectedDate;
			company = manager.selectedCompany;

			List<ShippersTable> li=initTableModel(null, null);
			updateTableAction(li);


			manager.tableCount=tableCount;


			this.removeMouseListener(listener);
			this.addMouseListener(listener);

			TableColumnModel colmodel =this.getColumnModel();

			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new KSGTableCellRenderer();
				if(i==0||i==1||i==4||i==5||i==2||i==8)
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);

				namecol.setHeaderRenderer(new IconHeaderRenderer());
			}

			TableColumnModel colmodel_width =this.getColumnModel();

			colmodel_width.getColumn(0).setMaxWidth(65); 		// 페이지
			colmodel_width.getColumn(1).setMaxWidth(55); 		// 인덱스
			colmodel_width.getColumn(2).setPreferredWidth(100); // 입력일자
			colmodel_width.getColumn(4).setMaxWidth(60); 		// 항구 수
			colmodel_width.getColumn(5).setMaxWidth(60);		// 선박 수
			colmodel_width.getColumn(6).setPreferredWidth(150); // 선사명
			colmodel_width.getColumn(7).setPreferredWidth(250); // 제목
			colmodel_width.getColumn(8).setMaxWidth(60);  		// 구분

				this.base._searchedList = li;
		} catch (SQLException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end");

	}
	class IconHeaderRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(new Color(61,86,113));
					
					setBackground(new Color(214,226,242));
					Font f = header.getFont();
					Font font = new Font(f.getFontName(),Font.BOLD,f.getSize());
					
					setFont(font);
					
				}
			}

			setText((value == null) ? "" : value.toString());

			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			
			setHorizontalAlignment(JLabel.CENTER);
			this.setPreferredSize(new Dimension(this.getSize().width,30));
			
			
			return this;
		}
	}
	@Override
	public void retrive() throws SQLException {
		// TODO Auto-generated method stub
		
	}


}
