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
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ksg.base.service.CompanyService;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.view.comp.KSGDialog;

public class UpdateCompanyInfoDialog extends KSGDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CompanyService companyService;
	private JTextField txfCompany_name; // 선사명
	private JTextField txfCompany_abbr; // 선사 약어
	private JTextField txfAgent_name;// 에이전트명
	private JTextField txfAgent_abbr;// 에이전트 약어
	private JTextArea txaContents;// 비고
	
	private int type;
	
	private JLabel lblTitle;

	private String titleInfo;


	private HashMap<String, Object> company;


	public UpdateCompanyInfoDialog(int type)
	{
		super();

		companyService = new CompanyService();

		this.type = type;
		title = "선사 정보 관리";
		switch (type) {
		case UPDATE:

			titleInfo="선사 정보 수정";
			break;
		case INSERT:

			titleInfo="선사 정보 추가";
			break;

		default:
			break;
		}

	}

	

	public UpdateCompanyInfoDialog(int type, HashMap<String, Object> company)
	{
		this(type);
		this.company = company;
	}

	private JButton butOK;
	private JButton butCancel;
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("수정"))
		{
			HashMap<String, Object> param = new HashMap<String, Object>();

			param.put("company_name", txfCompany_name.getText());
			param.put("company_abbr", txfCompany_abbr.getText());
			param.put("agent_name", txfAgent_name.getText());
			param.put("agent_abbr", txfAgent_abbr.getText());
			param.put("contents", txaContents.getText());
			param.put("base_company_abbr", txfCompany_abbr.getText());


			try {
				int obj=companyService.updateComapny(param);

				if(obj>0)
				{	
					result = SUCCESS;

					JOptionPane.showMessageDialog(null,"수정 했습니다.");
					this.setVisible(false);
					this.dispose();
				}

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}else if(command.equals("취소"))
		{
			result = FAILE;

			this.setVisible(false);
			this.dispose();

		}
		else if(command.equals("추가"))
		{
			HashMap<String, Object> param = new HashMap<String, Object>();

			param.put("company_name", txfCompany_name.getText());
			param.put("company_abbr", txfCompany_abbr.getText());
			param.put("agent_name", txfAgent_name.getText());
			param.put("agent_abbr", txfAgent_abbr.getText());
			param.put("contents", txaContents.getText());
			param.put("base_company_abbr", txfCompany_abbr.getText());


			try {
				companyService.insertComapny(param);


				result = SUCCESS;

				JOptionPane.showMessageDialog(null,"추가했습니다.");

				this.setVisible(false);
				this.dispose();

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	public void createAndUpdateUI() {
		this.setModal(true);
		this.addComponentListener(this);
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfCompany_name = new JTextField(20);
		txfCompany_abbr = new JTextField(20);
		txfAgent_name = new JTextField(20);
		txfAgent_abbr = new JTextField(20);
		txaContents = new JTextArea(8,32);
		JPanel pnCompany_name = new JPanel();
		pnCompany_name.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		JPanel pnControl =  new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));

		butOK = new JButton("저장");

		butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		lblTitle = new JLabel("Add a Company Field");
		lblTitle.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(lblTitle);

		pnCenter.add( addFormItem(txfCompany_name,"선사명"));
		pnCenter.add( addFormItem(txfCompany_abbr,"선사명 약어"));
		pnCenter.add(addFormItem(txfAgent_name,"에이전트"));
		pnCenter.add(addFormItem(txfAgent_abbr,"에이전트 약어"));
		pnCenter.add(addFormItem(txaContents,"비고"));
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
		this.setResizable(false);
		this.setVisible(true);

	}
	private JPanel addFormItem(JComponent comp, String title) {
		JPanel pnCompany_abbr = new JPanel();
		pnCompany_abbr.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany_abbr = new JLabel(title);
		lblCompany_abbr.setPreferredSize(new Dimension(100,25));
		pnCompany_abbr.add(lblCompany_abbr);	
		pnCompany_abbr.add(comp);
		return pnCompany_abbr;
	}

	

	@Override
	public void componentShown(ComponentEvent e) {

		this.setTitle(title);
		this.lblTitle.setText(titleInfo);

		if(company!=null)
		{
			this.txfCompany_abbr.setText((String) company.get("company_abbr"));
			this.txfCompany_name.setText((String) company.get("company_name"));
			this.txfAgent_abbr.setText((String) company.get("agent_abbr"));
			this.txfAgent_name.setText((String) company.get("agent_name"));
			this.txaContents.setText((String) company.get("contents"));
		}
		switch (type) {
		case UPDATE:

			butOK.setActionCommand("수정");

			break;
		case INSERT:

			butOK.setActionCommand("추가");

			break;

		}

	}

	
}
