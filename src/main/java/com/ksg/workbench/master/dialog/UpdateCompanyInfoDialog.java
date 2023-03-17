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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.dtp.api.control.CompanyController;
import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;

public class UpdateCompanyInfoDialog extends BaseInfoDialog  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CompanyServiceImpl companyService;

	private JTextField txfCompany_name; // �����

	private JTextField txfCompany_abbr; // ���� ���

	private JTextField txfAgent_name;// ������Ʈ��

	private JTextField txfAgent_abbr;// ������Ʈ ���

	private JTextArea txaContents;// ���

	private int type;

	private HashMap<String, Object> company;

	public UpdateCompanyInfoDialog(int type)
	{
		super();

		this.setController(new CompanyController());

		companyService = new CompanyServiceImpl();

		this.addComponentListener(this);

		this.type = type;

		title = "���� ���� ����";

		switch (type) {
		case UPDATE:

			titleInfo="���� ���� ����";
			break;
		case INSERT:

			titleInfo="���� ���� �߰�";
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


	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("����"))
		{
			CommandMap param = new CommandMap();

			param.put("company_name", txfCompany_name.getText());

			param.put("company_abbr", txfCompany_abbr.getText());

			param.put("agent_name", txfAgent_name.getText());

			param.put("agent_abbr", txfAgent_abbr.getText());

			param.put("contents", txaContents.getText());

			param.put("base_company_abbr", txfCompany_abbr.getText());

			callApi("updateCompany", param);



		}else if(command.equals("���"))
		{
			result = FAILE;

			this.setVisible(false);
			this.dispose();

		}
		else if(command.equals("�߰�"))
		{
			CommandMap param = new CommandMap();

			param.put("company_name", txfCompany_name.getText());

			param.put("company_abbr", txfCompany_abbr.getText());

			param.put("agent_name", txfAgent_name.getText());

			param.put("agent_abbr", txfAgent_abbr.getText());

			param.put("contents", txaContents.getText());

			param.put("base_company_abbr", txfCompany_abbr.getText());

			callApi("insertCompany", param);

		}

	}

	public void createAndUpdateUI() {
		
		this.setModal(true);

		this.getContentPane().add(buildTitle("Add a Company Field"),BorderLayout.NORTH);

		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);

		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);

		this.setSize(400, 350);

		//		this.pack

		ViewUtil.center(this);

		this.setResizable(false);

		this.setVisible(true);

	}




	public KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());


		txfCompany_name = new JTextField(20);

		txfCompany_abbr = new JTextField(20);

		txfAgent_name = new JTextField(20);

		txfAgent_abbr = new JTextField(20);

		txaContents = new JTextArea(8,32);

		txaContents.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		KSGPanel pnCenter = new KSGPanel(new GridLayout(0,1,0,-3));

		pnCenter.add( createFormItem(txfCompany_name,"�����"));

		pnCenter.add( createFormItem(txfCompany_abbr,"����� ���"));

		pnCenter.add(createFormItem(txfAgent_name,"������Ʈ"));

		pnCenter.add(createFormItem(txfAgent_abbr,"������Ʈ ���"));

		pnCenter.add(createFormItem(txaContents,"���"));

		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		pnMain.add(pnCenter);

		return pnMain;
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

			butOK.setActionCommand("����");

			break;
		case INSERT:

			butOK.setActionCommand("�߰�");

			break;

		}

	}

	@Override
	public void updateView() {
		CommandMap resultMap= this.getModel();

		boolean success = (boolean) resultMap.get("success");

		if(success)
		{

			String serviceId=(String) resultMap.get("serviceId");

			if("insertCompany".equals(serviceId))
			{	
				result = SUCCESS;

				JOptionPane.showMessageDialog(UpdateCompanyInfoDialog.this,"�߰��߽��ϴ�.");

				this.setVisible(false);

				this.dispose();

			}

			if("updateCompany".equals(serviceId))
			{	
				result = SUCCESS;

				JOptionPane.showMessageDialog(UpdateCompanyInfoDialog.this,"�����߽��ϴ�.");

				this.setVisible(false);

				this.dispose();

			}

		}
		else{  
			String error = (String) resultMap.get("error");

			JOptionPane.showMessageDialog(this, error);
		}

	}


}
