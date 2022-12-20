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
package com.ksg.workbench.master.dialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.PortInfo;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

public class UpdatePortAbbrInfoDialog extends BaseInfoDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txfPortName = new JTextField(20);
	private JTextField txfPortAbbr = new JTextField(20);
	String selectedPortCode;
	private BaseServiceImpl baseService;
	


	String portName;
	String portAbbr;
	public UpdatePortAbbrInfoDialog(String portName,String portAbbr) {
		super();
		this.addComponentListener(this);

		selectedPortCode = portAbbr;
		
		this.portAbbr=  portAbbr;
		this.portName = portName;
		baseService = new BaseServiceImpl(); 
		
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		
		this.setTitle("항구 정보 관리");
		
		this.getContentPane().add(buildTitle("항구 약어 수정"),BorderLayout.NORTH);
		
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		
		
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		
		this.setVisible(true);
	}
	
	private KSGPanel buildCenter()
	{
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		
		pnCenter.add(createFormItem(txfPortName, "항구명"));
		
		pnCenter.add(createFormItem(txfPortAbbr, "항구명약어"));	

		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		pnMain.add(pnCenter);
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			PortInfo info = new PortInfo();
			info.setPort_name(txfPortName.getText());
			info.setPort_abbr(txfPortAbbr.getText());
			info.setBase_port_abbr(selectedPortCode);
			try {

					baseService.updatePortAbbr(info);
					JOptionPane.showMessageDialog(this, "수정했습니다.");
					this.setVisible(false);
					this.dispose();
					this.result = KSGDialog.SUCCESS;
				
				
			} catch (SQLException e1) {
				if(e1.getErrorCode()==2627)
				{
					JOptionPane.showMessageDialog(this, "항구명이 존재합니다.");
				}else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
					e1.printStackTrace();
				}
			}
		}else
		{
			this.result = KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}
		
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		txfPortAbbr.setText(portAbbr);
		txfPortName.setText(portName);
	}

}
