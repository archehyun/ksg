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
package com.ksg.view.comp.combobox;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JComboBox;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;
import com.ksg.domain.Company;
import com.ksg.service.ADVService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.view.comp.tree.KSGTreeImpl;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class KSGCompboBox2 extends JComboBox implements KSGObserver
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ADVService service = new ADVServiceImpl();
	private String name;
	private KSGModelManager model =KSGModelManager.getInstance();
	public static final int TYPE1=1;
	public static final int TYPE2=2;
	int callSign;
	protected Logger logger = LogManager.getLogger(this.getClass());
	public KSGCompboBox2(String name,int callSign) {
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
				List<Company> li = service.getShippers(KSGTreeImpl.GroupByCompany);
				for(Company company: li)
				{
					this.addItem(company.getCompany_abbr());
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			break;

		default:
			break;
		}
	
	}
}
