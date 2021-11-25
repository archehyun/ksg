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
package com.ksg.common.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.workbench.adv.dialog.AddAdvDialog;
import com.ksg.workbench.adv.xls.XLSManagerImpl;

public class SelectOptionDialog extends JDialog implements ActionListener{
	
	protected Logger logger = Logger.getLogger(getClass());
	KSGModelManager manager = KSGModelManager.getInstance();
	private XLSManagerImpl managerImpl;
	String filename;
	AddAdvDialog addAdvDialog;
	public SelectOptionDialog(AddAdvDialog addAdvDialog) {
		managerImpl = XLSManagerImpl.getInstance();
		
		this.addAdvDialog=addAdvDialog; 
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildButtom(),BorderLayout.SOUTH);
		this.setTitle("�ɼ� �����ϱ�");
		this.setSize(400,180);
		this.setLocation(addAdvDialog.getX(),addAdvDialog.getY());
		
		this.setVisible(true);

	}

	private Component buildButtom() {
		JPanel pnButtom = new JPanel();
		pnButtom.setLayout(new GridLayout(1,0));
		JPanel pnPass = new JPanel();
		GridLayout grid = new GridLayout(1,0);
		grid.setHgap(15);
		pnPass.setLayout(grid);
		JButton btnNext = new JButton("����");
		btnNext.addActionListener(this);
		pnPass.add(btnNext);
		JButton btnCancel = new JButton("����");
		btnCancel.addActionListener(this);
		pnPass.add(btnCancel);
		
		pnButtom.add(new JPanel());
		pnButtom.add(pnPass);
		return pnButtom;
	}

	private Component buildCenter() {
		JPanel panel = new JPanel();
		
		JRadioButton button = new JRadioButton("�ϰ����");
		JRadioButton button2 = new JRadioButton("�������");
		panel.add(button);
		panel.add(button2);
		
		
		return panel;
	}

	
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("����"))
		{
			//managerImpl.readFile(addAdvDialog.selectXLSFilePath);
			String data = managerImpl.getData();			
			
/*			manager.ADVStringData= managerImpl.getXLSData();
			logger.debug("size;"+manager.ADVStringData.size());*/
			manager.execute("adv");
			manager.data=data;
			
			manager.vesselCount = managerImpl.getSearchedTableCount();
			manager.execute("vessel");
			addAdvDialog.dispose();
			this.dispose();
			
			
		}else if(e.getActionCommand().equals("����"))			
		{
			this.dispose();
			addAdvDialog.setVisible(true);
			
		}
		
	}


}
