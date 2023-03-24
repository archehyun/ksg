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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;
import com.ksg.service.TableService;
import com.ksg.workbench.admin.KSGViewParameter;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public abstract class KSGTable extends JTable implements KSGObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int TABLE_TYPE_ADV=0;
	
	public static final int TABLE_TYPE_ERROR=1;
	
	public static final int ADV_TYPE = 0;
	
	public static final int TABLE_TYPE = 1;
	
	protected int tableindex;
	
	protected TableService tableService;
	
	protected Font defaultFont = new Font("µ¸À½",0,12);
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public KSGTable() {
		initStyle();
	}
	public void setTableIndex(int index) {
		tableindex = index;
	}
	public int getTableIndex() {
		return tableindex;
	}
	public void update(KSGModelManager manager)
	{
		
	}
	
	public void initColumModel()
	{
		TableColumnModel colmodel =getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new KSGTableCellRenderer();

			namecol.setCellRenderer(renderer);
			namecol.setHeaderRenderer(new IconHeaderRenderer());

		}
	}
	
	public void initStyle()
	{
		this.setGridColor(Color.LIGHT_GRAY);

		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		this.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		
		this.setFont(defaultFont);
	}
	public abstract void retrive() throws SQLException;
	
	class IconHeaderRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(new Color(47,47,47));

					setBackground(new Color(249,250,250));
					
					Font font = new Font("Malgun Gothic",Font.BOLD,12);
					
					setFont(font);
				}
			}

			setText((value == null) ? "" : value.toString());

			setBorder(BorderFactory.createLineBorder(new Color(220,220,220),1));

			setHorizontalAlignment(JLabel.CENTER);
			
			this.setPreferredSize(new Dimension(this.getSize().width,30));


			return this;
		}
	}

}

