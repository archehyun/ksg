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
import java.util.List;

import javax.swing.JComboBox;

import org.apache.log4j.Logger;

import com.ksg.dao.impl.ADVService;
import com.ksg.dao.impl.ADVServiceImpl;
import com.ksg.domain.Company;
import com.ksg.model.KSGModelManager;
import com.ksg.model.KSGObserver;

public class KSGCompboBox extends JComboBox implements KSGObserver
{
	private ADVService service = new ADVServiceImpl();
	String name;
	KSGModelManager model =KSGModelManager.getInstance();
	public static final int TYPE1=1;
	public static final int TYPE2=2;
	int callSign;
	protected Logger logger = Logger.getLogger(this.getClass());
	public KSGCompboBox(String name,int callSign) {
		this.setName(name);
		model.addObservers(this);
		this.callSign=callSign;
	}
	
	
	public void update(KSGModelManager manager) 
	{
		this.removeAllItems();
		switch (callSign) {		
		case TYPE1:
		
			for(int i=0;i<manager.vesselCount;i++)
				this.addItem(i);
			break;
		case TYPE2:
			try {
				List<Company> li = service.getShippers(KSGTree1.GroupByCompany);
				for(Company company: li)
				{
					this.addItem(company.getCompany_abbr());
				}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;

		default:
			break;
		}
	
	}
}
