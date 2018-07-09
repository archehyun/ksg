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

import java.sql.SQLException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ShippersTable;
import com.ksg.shippertable.service.TableService;
import com.ksg.shippertable.service.TableServiceImpl;

public class DelTableCommand implements KSGCommand {
	
	private KSGModelManager 	manager = KSGModelManager.getInstance();
	private TableService 		_tableService;
	ShippersTable table;
	JComponent comp;
	public DelTableCommand(ShippersTable table,JComponent comp) {
		this.table = table;
		this.comp = comp;
		_tableService = new TableServiceImpl();
		
	}

	public int execute() {
		if(table==null||table.getCompany_abbr()==null)
		{
			JOptionPane.showMessageDialog(null, "선택하십시요");
			return RESULT_FAILE;
		}


		int j=JOptionPane.showConfirmDialog(null ,table.getCompany_abbr()+"의"+ table.getTable_id()+
				"번 테이블을 삭제하시겠습니까?.","테이블 정보 삭제",JOptionPane.OK_CANCEL_OPTION);
		if(j==JOptionPane.YES_OPTION)
		{
			try {
				_tableService.deleteTable(table);
				manager.execute(comp.getName());
				return RESULT_SUCCESS;
			} catch (SQLException e) {

				if(e.getErrorCode()==1451)
					JOptionPane.showMessageDialog(null, e.getErrorCode()+","+"기존 광고까지 삭제하시겠습니까?");
				return RESULT_FAILE;
			}
		}else
		{
			return RESULT_FAILE;
		}

	}

}
