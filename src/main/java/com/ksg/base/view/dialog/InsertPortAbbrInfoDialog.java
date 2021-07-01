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
package com.ksg.base.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
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

import com.ksg.base.view.BaseInfoUI;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.PortInfo;

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
	private JTextField txfPortCode = new JTextField(20);
	private String port_name,port_abbr="";

	public InsertPortAbbrInfoDialog(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		this.baseInfoUI=baseInfoUI;
		
	}
	
	public InsertPortAbbrInfoDialog(BaseInfoUI baseInfoUI,String port_name) {
		this(baseInfoUI);
		this.port_name =port_name;
		
	}

	public InsertPortAbbrInfoDialog(String portName) {
		this.port_name =portName;
	}

	public InsertPortAbbrInfoDialog(String port_abbr,String port_name) {
		baseService = new BaseServiceImpl();
		this.port_abbr=port_abbr;
		this.port_name=port_name;
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle("항구명 약어 추가");
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		

		JPanel pnCode = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaCode = new JLabel("항구명 약어");
		lblAreaCode.setPreferredSize(new Dimension(80,25));
		pnCode.add(lblAreaCode);	
		pnCode.add(txfPortCode);
		txfPortCode.setText(port_abbr);
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
		JLabel label = new JLabel("항구명: "+port_name);
		label.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(label);

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
			if(txfPortCode.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "항구명 약어를 입력하십시요");
				return;
			}
			PortInfo info = new PortInfo();
			info.setPort_name(port_name);
			info.setPort_abbr(txfPortCode.getText());
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

}
