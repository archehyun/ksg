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
package com.ksg.commands;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ShippersTable;
import com.ksg.model.KSGModelManager;
import com.ksg.view.comp.ColorData;
import com.ksg.view.comp.EvenOddRenderer;
import com.ksg.view.comp.KSGTableModel;

public class SearchSubTableCommand implements KSGCommand {

	protected Logger 		logger = Logger.getLogger(this.getClass());
	private List searchedList;
	private JTable _tblSubTotalTable;
	private String[] colums1s;
	private TableService _tableService;
	public SearchSubTableCommand( JTable table,List searchedList) {
		this.searchedList=searchedList;
		_tblSubTotalTable=table;
		_tableService = DAOManager.getInstance().createTableService();

		colums1s= KSGModelManager.getInstance().colums1;
	}

	public int execute() {
		//;

		DefaultTableModel model = new KSGTableModel(){
			public boolean isCellEditable(int row, int column) 
			{
				return column==2?true:false;
			}};

			model.setColumnCount(0);

			for(int i=0;i<colums1s.length;i++)
			{
				model.addColumn(colums1s[i]);
			}

			Iterator iter = searchedList.iterator();
			while(iter.hasNext())
			{
				ShippersTable cell= (ShippersTable) iter.next();
			
				
				model.addRow(new Object[]{
						cell.getPage(),
						cell.getTable_index(),
						cell.getDate_isusse(),
						cell.getTable_id(),
//						cell.getPort_col()==portli.size()?cell.getPort_col():new ColorData(ColorData.RED,cell.getPort_col()),
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
			_tblSubTotalTable.setModel(model);

			TableColumnModel colmodel =_tblSubTotalTable.getColumnModel();

			for(int i=0;i<colmodel.getColumnCount();i++)
			{

				TableColumn namecol = colmodel.getColumn(i);

				DefaultTableCellRenderer renderer = new EvenOddRenderer();
				if(i==0||i==1||i==2||i==4||i==5)
					renderer.setHorizontalAlignment(SwingConstants.CENTER);



				namecol.setCellRenderer(renderer);
				namecol.setHeaderRenderer(new IconHeaderRenderer());
				
			}

			TableColumnModel colmodel_width =_tblSubTotalTable.getColumnModel();

			colmodel_width.getColumn(0).setMaxWidth(65);		
			colmodel_width.getColumn(1).setMaxWidth(55);
			colmodel_width.getColumn(2).setPreferredWidth(100);
			colmodel_width.getColumn(4).setMaxWidth(60);
			colmodel_width.getColumn(5).setMaxWidth(60);
			colmodel_width.getColumn(6).setPreferredWidth(150);
			colmodel_width.getColumn(7).setPreferredWidth(250);
			colmodel_width.getColumn(8).setMaxWidth(55);
			
			
			_tblSubTotalTable.updateUI();
			return 0;
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

}
