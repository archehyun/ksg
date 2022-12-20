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
package com.ksg.workbench.base.vessel.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.util.ViewUtil;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.dialog.BaseInfoDialog;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**���� ��� ���� �߰� ���̾�׷�
 * @author ��â��
 *
 */
public class InsertVesselAbbrInfoDialog extends BaseInfoDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txfVesselAbbr = new JTextField(20);
	BaseInfoUI baseInfoUI;
	private String vessel_name;
	private String vessel_abbr="";
	HashMap<String, Object> info;
	
	VesselServiceImpl vesselService;

	public InsertVesselAbbrInfoDialog(String vessel_name) {
		super();
		this.vessel_name = vessel_name;
		this.title="���ڸ� ��� �߰�";
	}

	public InsertVesselAbbrInfoDialog(HashMap<String, Object> info) {
		super();
		this.info = info;
		this.title="���ڸ� ��� �߰�";
		vesselService = new VesselServiceImpl();
	}

	public void createAndUpdateUI() {

		this.setModal(true);
		this.addComponentListener(this);
		this.setTitle(title);

		JPanel pnCode = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblVesselAbbr = new JLabel("���ڸ� ���");
		
		lblVesselAbbr.setPreferredSize(new Dimension(80,25));
		
		pnCode.add(lblVesselAbbr);
		
		pnCode.add(txfVesselAbbr);

		this.getContentPane().add(buildTitle("���ڸ�: "+info.get("vessel_name")),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		ViewUtil.center(this, true);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public KSGPanel buildCenter()
	{
		KSGPanel pnMain= new KSGPanel(new BorderLayout());
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.add(createFormItem(txfVesselAbbr, "���ڸ� ���"));
		
		pnMain.add(pnCenter);
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("����"))
		{

			if(txfVesselAbbr.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "���ڸ� �� �Է� �Ͻʽÿ�");
				return;
			}
			
			
			info.put("vessel_abbr", txfVesselAbbr.getText());

			try {
				vesselService.insertDetail(info);
				
				result = KSGDialog.SUCCESS;
				this.setVisible(false);
				this.dispose();
			}
			catch (NullPointerException e1)
			{
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, e1.getMessage());
			}
			
			catch(AlreadyExistException e1)
			{
				JOptionPane.showMessageDialog(this, "�����ϴ� ���ڸ��Դϴ�.");
			}
		}else
		{
			result = KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}

	}
	class ConType
	{
		private String typeName;
		private String typeField;
		public String getTypeName() {
			return typeName;
		}
		public String toString()
		{
			return typeField+" : "+typeName;
		}
		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
		public String getTypeField() {
			return typeField;
		}
		public void setTypeField(String typeField) {
			this.typeField = typeField;
		}


	}
	

}
