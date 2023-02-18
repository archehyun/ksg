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
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.PortInfo;
import com.ksg.service.PortService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;

/**
 * 항구 약어 정보 추가 다이어그램
 * 
 * @author 박창현
 *
 */
public class InsertPortAbbrInfoDialog extends BaseInfoDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txfPortName = new JTextField(20);
	private JTextField txfPortAbbr = new JTextField(20);
	private String port_name,port_abbr="";
	private BaseServiceImpl baseService;
	private PortService portService;

	public InsertPortAbbrInfoDialog(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		portService = new PortServiceImpl();
		this.baseInfoUI=baseInfoUI;
		this.addComponentListener(this);
	}
	
	public InsertPortAbbrInfoDialog(BaseInfoUI baseInfoUI,String port_name) {
		this(baseInfoUI);
		this.port_name =port_name;
		
	}

	public InsertPortAbbrInfoDialog(String port_name) {
		baseService = new BaseServiceImpl();
		this.port_name =port_name;
		this.addComponentListener(this);
	}

	public InsertPortAbbrInfoDialog(String port_abbr,String port_name) {
		this(port_name);

		this.port_abbr=port_abbr;
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle("항구명 약어 추가");

		this.getContentPane().add(buildTitle("항구명: "+port_name),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setVisible(true);
	}
	
	public KSGPanel buildCenter() {
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		pnCenter.add(createFormItem(txfPortAbbr, "항구명 약어"));
		
		pnMain.add(pnCenter);
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("저장"))
		{
			if(txfPortAbbr.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "항구명 약어를 입력하십시요");
				return;
			}
			PortInfo info = new PortInfo();
			info.setPort_name(port_name);
			info.setPort_abbr(txfPortAbbr.getText());
			try {
				PortInfo info_one=baseService.getPortInfo(info.getPort_name());
				if(info_one!=null)
				{
					baseService.insertPort_Abbr(info);
					JOptionPane.showMessageDialog(this, "추가 했습니다.");
					this.setVisible(false);
					this.dispose();

				}else
					
				{
					JOptionPane.showMessageDialog(this, info.getPort_name()+" 기존 항구가 존재하지 않습니다.");
				}
				
			} catch (SQLException e1) {
				e1.printStackTrace();
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
			this.setVisible(false);
//			this.dispose();
		}
		
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		this.txfPortName.setText(port_name);
		this.txfPortAbbr.setText(port_abbr);
		
	}

}
