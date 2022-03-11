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
package com.ksg.workbench.base.port.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.PortInfo;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

public class UpdatePortAbbrInfoDialog extends KSGDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txfPortName = new JTextField(20);
	private JTextField txfPortAbbr = new JTextField(20);
	String selectedPortCode;
	private BaseServiceImpl baseService;
	


	public UpdatePortAbbrInfoDialog(Dialog dialog,String portCode,String portName) {
		super(dialog);

		txfPortAbbr.setText(portCode);
		txfPortName.setText(portName);
		selectedPortCode = portCode;
		baseService = new BaseServiceImpl(); 
		
	}
	public UpdatePortAbbrInfoDialog(String portName,String portAbbr) {
		super();
		txfPortAbbr.setText(portAbbr);
		txfPortName.setText(portName);
		selectedPortCode = portAbbr;
		baseService = new BaseServiceImpl(); 
		
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle("항구 정보 관리");
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		JPanel pnName = new JPanel();
		pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblPortName = new JLabel("항구명");
		lblPortName.setPreferredSize(new Dimension(80,25));
		txfPortName.setEditable(false);
		pnName.add(lblPortName);
		pnName.add(txfPortName);
		JPanel pnCode = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblPortAbbr = new JLabel("항구명 약어");
		lblPortAbbr.setPreferredSize(new Dimension(80,25));
		pnCode.add(lblPortAbbr);	
		pnCode.add(txfPortAbbr);




		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		JPanel pnControl =  new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("확인");
		JButton butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);

		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		JLabel label = new JLabel("항구 약어 수정");
		label.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(label);

		pnCenter.add(pnName);
		pnCenter.add(pnCode);
		pnCenter.add(pnS);
		pnCenter.add(pnControl);

		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(15,0));
		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension(15,0));


		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(pnCenter,BorderLayout.CENTER);
		this.getContentPane().add(left,BorderLayout.WEST);
		this.getContentPane().add(right,BorderLayout.EAST);
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setVisible(true);
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

}
