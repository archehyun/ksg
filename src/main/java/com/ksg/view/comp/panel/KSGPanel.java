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
package com.ksg.view.comp.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import com.ksg.adv.service.ADVService;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;
import com.ksg.common.util.KSGPropertis;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.dao.impl.BaseService;

/**
 * @author 박창현
 *
 */
/**

  * @FileName : KSGPanel.java

  * @Project : KSG2

  * @Date : 2021. 7. 7. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 화면 패널 클래스

  */
public class KSGPanel extends JPanel implements KSGObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected KSGModelManager 	manager = KSGModelManager.getInstance();	
	
	protected ADVService	 		_advService;
	
	protected BaseService _baseSearvice;
	
	protected BaseDAOManager baseDaoService;
	
	protected KSGPropertis 	propertis = KSGPropertis.getIntance();
	
	public void createAndUpdateUI()
	{
		
	}
	
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
	
	public KSGPanel()
	{
		super();
		this.setBackground(Color.white);
	}
	public KSGPanel(LayoutManager layout) {
		super(layout);
		this.setBackground(Color.white);
	}

	@Override
	public void update(KSGModelManager manager) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
}
