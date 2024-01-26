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
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.dtp.api.control.CompanyController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.dialog.MainTypeDialog;

@SuppressWarnings("serial")
public class ManageCompanyInfoDialog extends MainTypeDialog  {

	private JTextField txfCompany_name; // 선사명
	private JTextField txfCompany_abbr; // 선사 약어
	private JTextField txfAgent_name;// 에이전트명
	private JTextField txfAgent_abbr;// 에이전트 약어
	private JTextArea txaContents;// 비고

	private int type;

	private HashMap<String, Object> company;

	public ManageCompanyInfoDialog(int type)
	{
		super();

		this.setController(new CompanyController());

		this.addComponentListener(this);

		this.type = type;
		
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

	public ManageCompanyInfoDialog(int type, HashMap<String, Object> company)
	{
		this(type);
		
		this.company = company;
	}
	private void initComp()
	{
		txfCompany_name = new JTextField(20);
		txfCompany_abbr = new JTextField(20);
		txfAgent_name = new JTextField(20);
		txfAgent_abbr = new JTextField(20);
		txaContents = new JTextArea(8,32);
	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		if(command.equals("수정")||(command.equals("추가")))
		{
			CommandMap param = new CommandMap();

			param.put("company_name", txfCompany_name.getText());
			param.put("company_abbr", txfCompany_abbr.getText());
			param.put("agent_name", txfAgent_name.getText());
			param.put("agent_abbr", txfAgent_abbr.getText());
			param.put("contents", txaContents.getText());
			param.put("base_company_abbr", txfCompany_abbr.getText());
			
			callApi(command.equals("수정")? "updateCompany":"insertCompany", param);
			
			
		}else if(command.equals("취소"))
		{
			result = FAILE;

			this.setVisible(false);

			this.dispose();
		}		
	}

	public void createAndUpdateUI() 
	{
		initComp();
		
		this.setModal(true);

		this.getContentPane().add(buildHeader(titleInfo),BorderLayout.NORTH);
		this.addComp(buildCenter(),BorderLayout.CENTER);
		this.addComp(buildControl(),BorderLayout.SOUTH);
		ViewUtil.center(this,true);
		this.setResizable(false);
		this.setVisible(true);

	}

	public KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		txaContents.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.add( createFormItem(txfCompany_name,"선사명"));
		pnCenter.add( createFormItem(txfCompany_abbr,"선사명 약어"));
		pnCenter.add(createFormItem(txfAgent_name,"에이전트"));
		pnCenter.add(createFormItem(txfAgent_abbr,"에이전트 약어"));
		pnCenter.add(createFormItem(txaContents,"비고"));
		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		pnMain.add(pnCenter);

		return pnMain;
	}

	@Override
	public void componentShown(ComponentEvent e) {

		title = "선사 정보 관리";

		switch (type) {
		case UPDATE:

			butOK.setActionCommand("수정");
			this.txfCompany_abbr.setText((String) company.get("company_abbr"));
			this.txfCompany_name.setText((String) company.get("company_name"));
			this.txfAgent_abbr.setText((String) company.get("agent_abbr"));
			this.txfAgent_name.setText((String) company.get("agent_name"));
			this.txaContents.setText((String) company.get("contents"));

			break;
		case INSERT:

			butOK.setActionCommand("추가");

			break;
		}
	}

	@Override
	public void updateView() {
		
		CommandMap resultMap= this.getModel();

		String serviceId=(String) resultMap.get("serviceId");

		if("insertCompany".equals(serviceId))
		{	
			result = SUCCESS;

			NotificationManager.showNotification("추가했습니다.");

			this.setVisible(false);

			this.dispose();
		}

		if("updateCompany".equals(serviceId))
		{	
			result = SUCCESS;

			NotificationManager.showNotification("수정했습니다.");

			this.setVisible(false);

			this.dispose();
		}
	}
}
