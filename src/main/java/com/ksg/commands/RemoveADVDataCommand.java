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

import javax.swing.JOptionPane;

import com.ksg.service.ADVService;
import com.ksg.service.impl.ADVServiceImpl;

public class RemoveADVDataCommand implements KSGCommand {
	String company;
	String date;
	public int result;
	private ADVService 		service;
	public RemoveADVDataCommand(String company,String date) {
		this.company = company;
		this.date =date;
		service = new ADVServiceImpl();
	}

	
	public int execute() {
		try {
			int result=-1;
			if((result=service.removeADVData(company,date))>1)
			{
				JOptionPane.showMessageDialog(null, "삭제 되었습니다.");
				return RESULT_SUCCESS;
				
			}else
			{
				JOptionPane.showMessageDialog(null, "result : "+result);
				return RESULT_FAILE;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return RESULT_FAILE;
		}

	}

}
