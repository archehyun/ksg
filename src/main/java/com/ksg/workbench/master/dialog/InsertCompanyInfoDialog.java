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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.service.CompanyService;
import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;


/**

  * @FileName : InsertCompanyInfoDialog.java

  * @Project : KSG2

  * @Date : 2022. 3. 18. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 선박정보 추가 다이어그램

  */
public class InsertCompanyInfoDialog extends BaseInfoDialog{
	/**
	 * 
	 */
	
	
	private static final long serialVersionUID = 1L;
	public static final int UPDATE=1;
	public static  final int INSERT=0;
	private int type;
	private JTextField txfCompany_name;
	private JTextField txfCompany_abbr;
	private JTextField txfAgent_name;
	private JTextField txfAgent_abbr;
	private JTextArea txaContents;
	
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
		
		this.addComponentListener(this);
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			
			String company_name = txfCompany_name.getText();
			String company_abbr = txfCompany_abbr.getText();
			String agent_name = txfAgent_name.getText();
			String agent_abbr = txfAgent_abbr.getText();
			String contents = txaContents.getText();
			
			CommandMap param = new CommandMap();
			param.put("company_name",company_name);
			param.put("company_abbr",company_abbr);
			param.put("agent_name",agent_name);
			param.put("agent_abbr",agent_abbr);
			param.put("contents",contents);
			
			

			try {

				service.insert(param);
				
				JOptionPane.showMessageDialog(this,"추가했습니다.");
				this.setVisible(false);
				this.dispose();
				this.result=KSGDialog.SUCCESS;
			} catch (Exception e1) {
				
				e1.printStackTrace();
				
				JOptionPane.showMessageDialog(this, e1.getMessage()+","+e1.getMessage());
				
				
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
		this.setTitle(title);

		this.getContentPane().add(buildTitle("선사정보수정"),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter());
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);

	}
	
	public KSGPanel buildCenter()
	{
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.setBackground(Color.white);
		txfCompany_name = new JTextField(20);
		txfCompany_abbr = new JTextField(20);
		txfAgent_name = new JTextField(20);
		txfAgent_abbr = new JTextField(20);
		txaContents = new JTextArea(8,32);
		KSGPanel pnCompany_name = new KSGPanel();
		pnCompany_name.setLayout(new FlowLayout(FlowLayout.LEFT));



		pnCenter.add( createFormItem(txfCompany_name,"선사명"));
		pnCenter.add( createFormItem(txfCompany_abbr,"선사명 약어(*)"));
		pnCenter.add(createFormItem(txfAgent_name,"에이전트"));
		pnCenter.add(createFormItem(txfAgent_abbr,"에이전트 약어"));
		pnCenter.add(createFormItem(new JScrollPane(txaContents),"비고"));


		

		
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
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createEmptyBorder(4, 45, 45, 45));
		
		pnMain.add(pnCenter);
		
		return pnMain;
	}
	@Override
	public void componentShown(ComponentEvent e) {
		
		this.lblTitle.setText(titleInfo);
		this.txfCompany_abbr.setText(company_abbr);
		this.txfCompany_name.setText(company_name);
		this.txfAgent_abbr.setText(agent_abbr);
		this.txfAgent_name.setText(agent_name);
		

	}
	
}
