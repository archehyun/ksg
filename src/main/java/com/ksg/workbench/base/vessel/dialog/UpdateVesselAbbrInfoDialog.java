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

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Vessel;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * 선박 약어 정보 수정 다이어그램
 * 
 * 
 * @author 박창현
 *
 */
public class UpdateVesselAbbrInfoDialog extends KSGDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  String LABEL = "";
	private JTextField txfVessel_Abbr;
	private String UPDATE_TITLE = "선박 약어 수정";
	private Vessel dataInfo;
	private BaseServiceImpl baseService;

	/**
	 * @deprecated
	 * @param baseInfoUI
	 * @param vessel
	 */
	public UpdateVesselAbbrInfoDialog(BaseInfoUI baseInfoUI, Vessel vessel) {
		super();
		this.baseInfoUI=baseInfoUI;
		this.setTitle(UPDATE_TITLE);
		LABEL = "선박명: "+vessel.getVessel_name();
		this.dataInfo = vessel;
		
		baseService = new BaseServiceImpl(); 

	}
	public UpdateVesselAbbrInfoDialog(Vessel vessel) {
		super();
		this.setTitle(UPDATE_TITLE);
		LABEL = "선박명: "+vessel.getVessel_name();
		this.dataInfo = vessel;
	}
	public void createAndUpdateUI() 
	{
		this.setModal(true);
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfVessel_Abbr = new JTextField(20);
		JPanel pnRequest = new JPanel();
		pnRequest.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblRequest = new JLabel("선박명 약자");
		lblRequest.setPreferredSize(new Dimension(100,25));
		pnRequest.add(lblRequest);
		pnRequest.add(txfVessel_Abbr);

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
		JLabel label = new JLabel(LABEL);
		label.setFont(new Font("돋움",0,16));
		pnTitle.add(label);
		pnCenter.add(pnRequest);

		pnCenter.add(pnS);
		pnCenter.add(pnControl);

		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(15,0));
		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension(15,0));

		if(dataInfo!=null)
		{
			txfVessel_Abbr.setText(dataInfo.getVessel_abbr());
		}		

		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(pnCenter,BorderLayout.CENTER);
		this.getContentPane().add(left,BorderLayout.WEST);
		this.getContentPane().add(right,BorderLayout.EAST);
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command .equals("확인"))
		{			

			Vessel op = new Vessel();
			op.setVessel_name(dataInfo.getVessel_name());
			op.setVessel_abbr(dataInfo.getVessel_abbr());
			op.setVessel_abbr2(txfVessel_Abbr.getText());


			try {
				baseService.updateVesselAbbr(op);

				this.result = KSGDialog.SUCCESS;
				this.setVisible(false);
				this.dispose();
				JOptionPane.showMessageDialog(this, "수정했습니다.");

				

			} catch (SQLException e1) 
			{
				JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
				e1.printStackTrace();
			}
		}else
		{
			this.result = KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}

	}
}
