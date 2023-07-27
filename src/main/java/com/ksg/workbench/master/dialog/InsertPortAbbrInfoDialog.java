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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dtp.api.control.PortController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.dialog.MainTypeDialog;
import com.ksg.workbench.master.BaseInfoUI;

/**
 * �ױ� ��� ���� �߰� ���̾�׷�
 * 
 * @author ��â��
 *
 */
public class InsertPortAbbrInfoDialog extends MainTypeDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField txfPortName;

	private JTextField txfPortAbbr;

	private String port_name,port_abbr="";

	public InsertPortAbbrInfoDialog(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);

		this.setController(new PortController());

		this.baseInfoUI=baseInfoUI;

		this.addComponentListener(this);
	}

	public InsertPortAbbrInfoDialog(BaseInfoUI baseInfoUI,String port_name) {

		this(baseInfoUI);

		this.port_name =port_name;
	}

	public void createAndUpdateUI() {
		
		this.titleInfo ="�ױ��� ��� �߰�";
		
		initComp();
		
		this.setModal(true);

		this.getContentPane().add(buildHeader(titleInfo),BorderLayout.NORTH);

		this.addComp(buildCenter(),BorderLayout.CENTER);

		this.addComp(buildControl(),BorderLayout.SOUTH);
		
		ViewUtil.center(this, true);

		this.setResizable(false);

		this.setVisible(true);
	}

	private void initComp() {
		
		txfPortAbbr = new JTextField(20);
		
		txfPortName = new JTextField(20);
		
		txfPortName.setEditable(false);
	}

	public KSGPanel buildCenter() {

		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		pnCenter.add(createFormItem(txfPortName, "�ױ���"));

		pnCenter.add(createFormItem(txfPortAbbr, "�ױ��� ���"));

		pnMain.add(pnCenter);

		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if(command.equals("����"))
		{
			if(txfPortAbbr.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "�ױ��� �� �Է��Ͻʽÿ�");
				return;
			}
			String port_abbr = txfPortAbbr.getText(); 

			CommandMap param = new CommandMap();

			param.put("port_name", port_name);

			param.put("port_abbr", port_abbr);

			callApi("insertPortDetail", param);

		}else
		{
			close();
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		this.txfPortName.setText(port_name);
		this.txfPortAbbr.setText(port_abbr);
	}

	@Override
	public void updateView() {

		CommandMap resultMap= this.getModel();

		String serviceId=(String) resultMap.get("serviceId");

		if("insertPortDetail".equals(serviceId))
		{	
			result = SUCCESS;

			NotificationManager.showNotification("�߰��߽��ϴ�.");

			close();
		}
	}
}
