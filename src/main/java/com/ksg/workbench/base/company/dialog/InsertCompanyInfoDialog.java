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
package com.ksg.workbench.base.company.dialog;

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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Company;
import com.ksg.service.CompanyService;
import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.dialog.BaseInfoDialog;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * 선박정보 추가 다이어그램
 * @author 박창현
 *
 */
public class InsertCompanyInfoDialog extends BaseInfoDialog implements ActionListener  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txfCompany_name;
	private JTextField txfCompany_abbr;
	private JTextField txfAgent_name;
	private JTextField txfAgent_abbr;
	private JTextArea txaContents;
	public static final int UPDATE=1;
	public static  final int INSERT=0;
	private int type;
	private JLabel lblTitle;
	private String titleInfo;
	private String title;
	private String company_name;
	private String company_abbr;
	private String agent_name;
	private String agent_abbr;
	
	CompanyService service;

	public InsertCompanyInfoDialog(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		
		service = new CompanyServiceImpl();
		title = "선사 정보 추가";
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			Company info = new Company();
			info.setCompany_name(txfCompany_name.getText());
			info.setCompany_abbr(txfCompany_abbr.getText());
			info.setAgent_name(txfAgent_name.getText());
			info.setAgent_abbr(txfAgent_abbr.getText());
			info.setContents(txaContents.getText());
			try {

				baseService.insertCompany(info);
				JOptionPane.showMessageDialog(this,"추가했습니다.");
				this.setVisible(false);
				this.dispose();
				this.result=KSGDialog.SUCCESS;
			} catch (SQLException e1) {
				if(e1.getErrorCode()==2627)
				{
					
					JOptionPane.showMessageDialog(this, "선사명이 존재합니다.");
					

				}else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
					e1.printStackTrace();
				}
			}

		}else if(command.equals("취소"))
		{
			this.result=KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}

	}

	public void createAndUpdateUI() {
		this.setModal(true);
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.setBackground(Color.white);
		txfCompany_name = new JTextField(20);
		txfCompany_abbr = new JTextField(20);
		txfAgent_name = new JTextField(20);
		txfAgent_abbr = new JTextField(20);
		txaContents = new JTextArea(8,32);
		KSGPanel pnCompany_name = new KSGPanel();
		pnCompany_name.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		KSGPanel pnS = new KSGPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		KSGPanel pnS1 = new KSGPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		KSGPanel pnControl =  new KSGPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("확인");
	
		JButton butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);

		KSGPanel pnTitle = new KSGPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		lblTitle = new JLabel("Add a Company Field");
		lblTitle.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(lblTitle);

		pnCenter.add( addFormItem(txfCompany_name,"선사명"));
		pnCenter.add( addFormItem(txfCompany_abbr,"선사명 약어(*)"));
		pnCenter.add(addFormItem(txfAgent_name,"에이전트"));
		pnCenter.add(addFormItem(txfAgent_abbr,"에이전트 약어"));
		pnCenter.add(addFormItem(new JScrollPane(txaContents),"비고"));
		pnCenter.add(pnS);
		pnCenter.add(pnControl);

		KSGPanel left = new KSGPanel();
		left.setPreferredSize(new Dimension(15,0));
		KSGPanel right = new KSGPanel();
		right.setPreferredSize(new Dimension(15,0));

		this.setTitle(title);
		this.lblTitle.setText(titleInfo);
		this.txfCompany_abbr.setText(company_abbr);
		this.txfCompany_name.setText(company_name);
		this.txfAgent_abbr.setText(agent_abbr);
		this.txfAgent_name.setText(agent_name);
		
		switch (type) {
		case UPDATE:
			
			//this.txfAgent_abbr.setEditable(false);
			//this.txfAgent_name.setEditable(false);
//			this.txfCompany_name.setEditable(false);
			this.txfCompany_abbr.setEditable(false);
			butOK.setText("수정");
			butOK.setActionCommand("확인");
			break;
		case INSERT:
			
			break;
		
		}


		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(pnCenter,BorderLayout.CENTER);
		this.getContentPane().add(left,BorderLayout.WEST);
		this.getContentPane().add(right,BorderLayout.EAST);
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);

	}
	private KSGPanel addFormItem(JComponent comp, String title) {
		KSGPanel pnCompany_abbr = new KSGPanel();
		pnCompany_abbr.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany_abbr = new JLabel(title);
		lblCompany_abbr.setPreferredSize(new Dimension(100,25));
		pnCompany_abbr.add(lblCompany_abbr);	
		pnCompany_abbr.add(comp);
		return pnCompany_abbr;
	}
}
