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
import com.ksg.common.util.ViewUtil;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.domain.Vessel;

/**선박 양어 정보 추가 다이어그램
 * @author 박창현
 *
 */
public class InsertVesselAbbrInfoDialog extends KSGDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txfVesselAbbr = new JTextField(20);
	BaseInfoUI baseInfoUI;
	private String vessel_name;
	private String vessel_abbr="";


	public InsertVesselAbbrInfoDialog(String vessel_name) {
		super();
		this.vessel_name = vessel_name;
		this.title="선박명 약어 추가";
	}

	public void createAndUpdateUI() {


		this.setModal(true);

		this.setTitle(title);
		Box pnCenter = new Box(BoxLayout.Y_AXIS);


		JPanel pnCode = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblVesselAbbr = new JLabel("선박명 약어");
		lblVesselAbbr.setPreferredSize(new Dimension(80,25));
		pnCode.add(lblVesselAbbr);	
		pnCode.add(txfVesselAbbr);
		this.txfVesselAbbr.setText(vessel_abbr);

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
		JLabel label = new JLabel("선박명: "+vessel_name);
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
		ViewUtil.center(this, true);
		this.setResizable(false);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{

			if(txfVesselAbbr.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "선박명 약어를 입력 하십시요");
				return;
			}


			try {
				Vessel vessel_abbr = new Vessel();
				vessel_abbr.setVessel_name(vessel_name);

				Vessel vessel_info = baseService.getVesselInfo(vessel_abbr);

				if(vessel_info!=null)
				{
					vessel_info.setVessel_abbr(txfVesselAbbr.getText());
					if(vessel_info.getVessel_mmsi()==null)
					{
						vessel_info.setVessel_mmsi("");
					}
					baseService.insertVessel(vessel_info);
					JOptionPane.showMessageDialog(this, "추가 했습니다.");
					this.setVisible(false);
					this.dispose();
					result = KSGDialog.SUCCESS;

				}else

				{
					JOptionPane.showMessageDialog(this, vessel_info.getVessel_name()+" 기존 선박이 존재하지 않습니다.");

				}

			} catch (SQLException e1) {
				if(e1.getErrorCode()==2627)
				{
					JOptionPane.showMessageDialog(this, "선박명이 존재합니다.");
					e1.printStackTrace();
				}else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
					e1.printStackTrace();
				}
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
