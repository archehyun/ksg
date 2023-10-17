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
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.control.PortController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.Notification;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.dialog.MainTypeDialog;

/**
 * �������� update/insert ���̾�׷�
 * 
 * @author ��â��
 *
 */
@SuppressWarnings("serial")
public class UpdateAreaInfodialog extends MainTypeDialog {
	
	
//	Area
	private HashMap<String, Object> param;
	
	private JTextField txfAreaName;
	
	private JTextField txfAreaCode;
	
	private JTextField txfAreaBookCode;
	
	
	public UpdateAreaInfodialog(int type) {
		super();
		setController(new PortController());
		title = "���� ���� ����";
		
		this.type = type;
		this.addComponentListener(this);
	}

	public UpdateAreaInfodialog(int type,HashMap<String, Object> param) {
		this(type);
		this.param = param;
		
	}

	private KSGPanel buildCenter()
	{	
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		pnCenter.add(createFormItem(txfAreaName,"������"));
		
		pnCenter.add(createFormItem(txfAreaCode,"�����ڵ�"));
		
		pnCenter.add(createFormItem(txfAreaBookCode, "���� å �ڵ�"));

		KSGPanel pnMain = new KSGPanel();
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		pnMain.add(pnCenter);
		
		return pnMain;
	}
	public void createAndUpdateUI() {
		initComp();
		
		titleInfo = "������������";
		
		this.setModal(true);

		this.getContentPane().add(buildHeader(titleInfo),BorderLayout.NORTH);

		this.addComp(buildCenter(),BorderLayout.CENTER);

		this.addComp(buildControl(),BorderLayout.SOUTH);

		ViewUtil.center(this,true);

		this.setResizable(false);

		this.setVisible(true);
	}

	private void initComp() {
		
		txfAreaName = new JTextField(21);
		
		txfAreaCode = new JTextField(5);
		
		txfAreaBookCode = new JTextField(5);
	}

	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if(command.equals("����")||command.equals("�߰�"))
		{
			if(txfAreaName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�������� ��������");
				return;
			}
			if(txfAreaCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ڵ��ȣ�� ��������");
				return;
			}
			
			if(txfAreaBookCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "���ڵ��ȣ�� ��������");
				return;
			}
			else
			{
				String str = txfAreaBookCode.getText();
				
				boolean isNumeric = StringUtils.isNumeric(str);
				
				if(!isNumeric)
				{
					NotificationManager.showNotification(Notification.Type.WARNING,"���ڵ��ȣ�� ���ڸ� �Է��ؾ� �մϴ�.");
					return;
				}
			}
		}
		
		if(command.equals("����"))
		{	
			fnUpdate();
		}
		else if(command.equals("�߰�"))
		{
			fnInsert();
		}
		else
		{
			result=KSGDialog.FAILE;
			close();
		}
	}
	
	private void fnInsert()
	{	
		CommandMap param = new CommandMap();
		
		param.put("area_name", txfAreaName.getText());
		
		param.put("area_code", txfAreaCode.getText());
		
		param.put("area_book_code", Integer.parseInt(txfAreaBookCode.getText()));
		
		callApi("insertArea", param);
	}
	
	private void fnUpdate() {
		
		CommandMap param = new CommandMap();
		
		param.put("area_name", txfAreaName.getText());
		
		param.put("area_code", txfAreaCode.getText());
		
		param.put("area_book_code", Integer.parseInt(txfAreaBookCode.getText()));
		
		param.put("base_area_name", this.param.get("area_name"));
		
		callApi("updateArea", param);
	}

	@Override
	public void componentShown(ComponentEvent e) {

		if(param!=null)
		{
			txfAreaCode.setText((String) param.get("area_code"));
			txfAreaName.setText((String) param.get("area_name"));
			txfAreaBookCode.setText(String.valueOf(param.get("area_book_code")));
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
		
		CommandMap resultObj= this.getModel();

		String serviceId=(String) resultObj.get("serviceId");

		if("insertArea".equals(serviceId))
		{
			result=KSGDialog.SUCCESS;
			NotificationManager.showNotification("�߰� �߽��ϴ�.");
		}
		else if("updateArea".equals(serviceId))
		{
			result=KSGDialog.SUCCESS;
			NotificationManager.showNotification("���� �߽��ϴ�.");
			
		}
		
		close();
	}
}
