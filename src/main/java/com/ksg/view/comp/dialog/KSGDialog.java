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
package com.ksg.view.comp.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.ksg.adv.service.ADVService;
import com.ksg.base.view.BaseInfoUI;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.ShippersTable;
import com.ksg.shippertable.service.TableService;

/**
 * @author ��â��
 *
 */
public abstract class KSGDialog extends JDialog implements ComponentListener{
	/**
	 * 
	 */
	protected String title;
	
	private static final long serialVersionUID = 1L;
	
	protected BaseInfoUI baseInfoUI;
	
	public int OPTION = 0;
	
	public int result;
	
	public static final int SUCCESS=0;
	
	public static final int FAILE=1;
	
	protected BaseService baseService;
	
	protected ADVService 	advservice;
	
	protected TableService tableService;
	
	protected ShippersTable selectedTable;
	
	public static final int UPDATE=1;
	public static final int INSERT=0;
	
	protected int type;
	
	protected Font defaultFont = new Font("����",0,10);
	
	protected Logger 			logger = Logger.getLogger(getClass());
	
	public KSGDialog() {
		super(KSGModelManager.getInstance().frame);
		
		baseService = new BaseServiceImpl();
	}
	public KSGDialog(Dialog arg0)
	{
		super(arg0);
	}
	public abstract void createAndUpdateUI();
	public void createAndUpdateUI(Component compo)
	{
		
	}
	public static Component createMargin()
	{
		JPanel pn =  new JPanel();
		pn.setPreferredSize(new Dimension(15,0));
		return pn;
	}
	public static Component createMargin(int w, int h)
	{
		JPanel pn =  new JPanel();
		pn.setPreferredSize(new Dimension(w,h));
		return pn;
	}
	public void close() {
		this.setVisible(false);
		this.dispose();
		
	}
	public Component createLine()
	{
		JPanel pn = new JPanel();
		pn.setPreferredSize(new Dimension(15,0));
		return pn;
	}
	
	@Override
	public void componentResized(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	

}
