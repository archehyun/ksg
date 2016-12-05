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
package com.ksg.view.comp;

import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.table.AbstractTableModel;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.Vessel;

public class KSGADVTableData extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector m_vector;
	ColumData colums[];
	private BaseService baseService;
	DAOManager manager;
	
	public KSGADVTableData() {
		m_vector = new Vector();
		manager = DAOManager.getInstance();
		baseService = manager.createBaseService();
	}
	public int getColumnCount() {
		return colums.length;
	}
	public String getColumnName(int column)
	{
		return colums[column].m_title;
	}

	public int getRowCount() {
		return m_vector==null?0:m_vector.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex<0||rowIndex>=getRowCount())
			return "";
		
		
		ADVTableRowData data =(ADVTableRowData) m_vector.get(rowIndex);
		if(columnIndex>1)
		{
			return data.getData(columnIndex);
		}else
		{
			return data.getData(columnIndex);
		}
		
	}
	public boolean isCellEditable(int nRow, int nCol)
	{
		return true;
	}

	public void addColum(String[] strings) {

		this.colums = new ColumData[strings.length];
		for(int i=0;i<colums.length;i++)
		{
			colums[i] = new ColumData(strings[i],100,JLabel.CENTER);
		}
	}

	public void addData(String[] strings) 
	{
		try {
			m_vector.add(new ADVTableRowData(strings));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class ColumData
	{
		public String m_title;
		public  int m_width;
		public int m_alignment;
		public ColumData(String title, int width, int alignment) {
			this.m_title=title;
			this.m_width=width;
			this.m_alignment=alignment;

		}
	}
	class ADVTableRowData
	{
		ADVTableData data[];
		private Vessel vesselinfo=null;
		public ADVTableRowData(String datas[]) throws SQLException {
			
			if(datas!=null)
			{
				this.data = new ADVTableData[datas.length];
				for(int i=0;i<datas.length;i++)
				{
					if(i==0)
					{
						
						Vessel op = new Vessel();
						op.setVessel_name(datas[i]);
						vesselinfo = baseService.getVesselInfo(op);
						if(vesselinfo==null)
						{
							this.data[i] = new ADVTableData(datas[i],false);
						}else
						{
							this.data[i] = new ADVTableData(datas[i],true);	
						}
						
					}else
					{
						this.data[i] =  new ADVTableData(datas[i]);
					}
				}
			}
			
			
			
			
		}

		public Object getData(int columnIndex) 
		{
			if(data.length<=columnIndex)
				return "";
			
			
			return data[columnIndex];
		
		}

	}


}
