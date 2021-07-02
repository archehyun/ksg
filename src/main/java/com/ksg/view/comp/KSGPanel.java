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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.ksg.adv.service.ADVService;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;
import com.ksg.common.util.KSGPropertis;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.shippertable.service.TableService;

/**
 * @author ��â��
 *
 */
public abstract class KSGPanel extends JPanel implements KSGObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected KSGModelManager 	manager = KSGModelManager.getInstance();
	
	protected Logger 			logger = Logger.getLogger(getClass());
	
	protected ADVService	 		_advService;
	
	protected TableService 		tableService;  // ���̺� ���� ��ü
	
	protected BaseService _baseSearvice;
	
	protected BaseDAOManager baseDaoService;
	
	protected KSGPropertis 	propertis = KSGPropertis.getIntance();
	
	public abstract void createAndUpdateUI();
	
	public Component createMargin()
	{
		return createMargin(15);
	}
	public Component createMargin(int w)
	{
		JPanel pn =  new JPanel();
		pn.setPreferredSize(new Dimension(w,0));
		return pn;
	}
}