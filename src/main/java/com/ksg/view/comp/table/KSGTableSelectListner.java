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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import com.ksg.adv.service.ADVService;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ADVData;

public class KSGTableSelectListner  extends MouseAdapter{
	private ADVService _service;
	private JTable table;
	KSGModelManager manager = KSGModelManager.getInstance();
	JPopupMenu menu;
	public KSGTableSelectListner(ADVService service,JTable table,JPopupMenu menu) {
		_service=service;
		this.table=table;
		this.menu = menu;
	}
	
	public void mouseClicked(MouseEvent e) 
	{
		if(e.getClickCount()==2)
		{
			JTable es = (JTable) e.getSource();
			int row=es.getSelectedRow();
			if(row<0)
			return;
			
			Object table_id= es.getValueAt(row, 0);//테이블 아이디
			
			try {
				ADVData data=_service.getADVData((String)table_id, manager.selectedDate,((KSGSearchTable)this.table).company);
				if(data!=null)
				{
					String[][]d = _service.createADVTableModel(data.getData());
					KSGTableModel model = new KSGTableModel(d[0],d.length-1);
					KSGTableImpl.updateColum(KSGTable.ADV_TYPE,table);
					for(int i=1;i<d.length;i++)
					{
						for(int j=0;j<d[i].length;j++)
							model.setValueAt(d[i][j], i-1, j);
					}
					table.setModel(model);
					
					
					table.removeAll();
					//table.removeMouseListener(table.listner);
					
					table.remove(menu);
				}else
				{
					JOptionPane.showMessageDialog(null, "광고정보가 없습니다.");
				}

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
