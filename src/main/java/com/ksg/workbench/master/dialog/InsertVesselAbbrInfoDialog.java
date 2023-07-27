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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JTextField;

import com.dtp.api.control.VesselController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.Notification;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.dialog.MainTypeDialog;

/**선박 양어 정보 추가 다이어그램
 * @author 박창현
 *
 */
@SuppressWarnings("serial")
public class InsertVesselAbbrInfoDialog extends MainTypeDialog{

	private JTextField txfVesselAbbr;
	
	private JTextField txfVesselName;

	private String vessel_name;
	
	private String vessel_abbr;

	public InsertVesselAbbrInfoDialog() {

		super();
		
		this.addComponentListener(this);

		this.setController(new VesselController());

		this.title="선박명 약어 추가";
	}

	public InsertVesselAbbrInfoDialog(String vessel_name) {

		this();

		this.vessel_name = vessel_name;
	}
	
	public InsertVesselAbbrInfoDialog(String vessel_name, String vessel_abbr) {

		this(vessel_name);

		this.vessel_abbr = vessel_abbr;
	}

	public void createAndUpdateUI() {

		this.titleInfo ="항구명 약어 추가";
		
		initComp();
		
		this.addComponentListener(this);
		
		this.setModal(true);

		this.getContentPane().add(buildHeader(titleInfo),BorderLayout.NORTH);

		this.addComp(buildCenter(),BorderLayout.CENTER);

		this.addComp(buildControl(),BorderLayout.SOUTH);
		
		ViewUtil.center(this, true);

		this.setResizable(false);

		this.setVisible(true);
	}

	private void initComp() {
		
		txfVesselAbbr = new JTextField(20);
		
		txfVesselName = new JTextField(20);
		
		txfVesselName.setEditable(false);
	}

	public KSGPanel buildCenter()
	{
		KSGPanel pnMain= new KSGPanel(new BorderLayout());

		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		pnCenter.add(createFormItem(txfVesselName, "선박명"));

		pnCenter.add(createFormItem(txfVesselAbbr, "선박명 약어"));

		pnMain.add(pnCenter);
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
		
		return pnMain;
	}

	private void fnInsertDetail()
	{
		CommandMap param = new CommandMap();

		if(txfVesselAbbr.getText().equals(""))
		{
			NotificationManager.showNotification(Notification.Type.WARNING, "선박명 약어를 입력 하십시요");
			
			return;
		}

		param.put("vessel_name", vessel_name);
		
		param.put("vessel_abbr", txfVesselAbbr.getText());

		callApi("insertVesselDetail", param);
	}

	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if(command.equals("저장"))
		{
			fnInsertDetail();

		}else
		{
			result = KSGDialog.FAILE;
			
			this.setVisible(false);
			
			this.dispose();
		}
	}
	@Override
	public void componentShown(ComponentEvent e) {
		
		this.txfVesselName.setText(vessel_name);
		
		this.txfVesselAbbr.setText(vessel_abbr);
	}
	@Override
	public void updateView() {
		
		CommandMap resultMap= this.getModel();

		String serviceId=(String) resultMap.get("serviceId");

		if("insertVesselDetail".equals(serviceId))
		{	
			result = KSGDialog.SUCCESS;
			
			this.setVisible(false);
			
			this.dispose();
		}
	}
}