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
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.base.dialog.SearchCompanyInfoDialog;
import com.ksg.workbench.base.vessel.comp.PnVessel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

@SuppressWarnings("serial")
public class InsertVesselInfoDialog extends KSGDialog implements ActionListener{
	
	private String LABEL = "선박명 추가";
	private JTextField txfVesselName;
	private JTextField txfMMSI;
	private JTextField txfCompany;	

	private JCheckBox chbUse;
	
	private KSGComboBox cbxType;
	
	
	VesselService service = new VesselServiceImpl();
	
	Vessel dataInfo;
	
	private JCheckBox cbxMMSICheck;
	
	public InsertVesselInfoDialog()
	{
		super();
		this.setTitle("신규 선박 정보 추가");
		this.addComponentListener(this);
	 
		
	}
	PnVessel pnVessel;
	public InsertVesselInfoDialog(PnVessel pnVessel) {
		this();
		this.pnVessel = pnVessel;
	}

	public void createAndUpdateUI() 
	{
		this.setModal(true);
		cbxType = new KSGComboBox("conType");
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.setBackground(Color.white);
		txfVesselName = new JTextField(20);

		txfCompany = new JTextField(20);
		

		chbUse= new JCheckBox("사용안함");
		chbUse.setBackground(Color.white);	
		
		txfMMSI = new JTextField(9);
		txfMMSI.setEnabled(false);
		
		
		KSGPanel pnName = new KSGPanel();
		pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaName = new JLabel("선박명(*)");
		lblAreaName.setPreferredSize(new Dimension(100,25));
		pnName.add(lblAreaName);
		pnName.add(txfVesselName);

		KSGPanel pnType = new KSGPanel();
		pnType.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblType = new JLabel("선박 타입");
		lblType.setPreferredSize(new Dimension(100,25));
		pnType.add(lblType);
		pnType.add(cbxType);
		
		KSGPanel pnMMSI = new KSGPanel();
		pnMMSI.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblMMSI = new JLabel("MMSI");
		lblMMSI.setPreferredSize(new Dimension(100,25));
		cbxMMSICheck = new JCheckBox("없음",true);
		cbxMMSICheck.setBackground(Color.white);
		cbxMMSICheck.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				txfMMSI.setEnabled(!cbxMMSICheck.isSelected());
				
				if(cbxMMSICheck.isSelected())
				{
					txfMMSI.setText("");
				}
			}
		});
		
		pnMMSI.add(lblMMSI);
		pnMMSI.add(txfMMSI);
		pnMMSI.add(cbxMMSICheck);
		
		
		KSGPanel pnCompany = new KSGPanel();
		pnCompany.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany = new JLabel("대표 선사");
		lblCompany.setPreferredSize(new Dimension(100,25));
		JButton butSearchCompany = new JButton("조회");
		JButton butCancelCompany = new JButton("취소");
		txfCompany.setEditable(false);
		butSearchCompany.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				KSGDialog dialog = new SearchCompanyInfoDialog(InsertVesselInfoDialog.this,txfCompany);
				dialog.createAndUpdateUI();
			}});
		butCancelCompany.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				txfCompany.setText("");
						
				
			}});
		pnCompany.add(lblCompany);
		pnCompany.add(txfCompany);
		pnCompany.add(butSearchCompany);
		pnCompany.add(butCancelCompany);

		KSGPanel pnS = new KSGPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		KSGPanel pnS1 = new KSGPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		KSGPanel pnControl =  new KSGPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("확인");
		JButton butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);

		KSGPanel pnTitle = new KSGPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		JLabel label = new JLabel(LABEL);
		label.setFont(new Font("돋움",0,16));
		pnTitle.add(label);
		
		KSGPanel pnUse = new KSGPanel();
		pnUse.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblUse = new JLabel("사용유무");
		lblUse.setPreferredSize(new Dimension(100,25));
		pnUse.add(lblUse);
		pnUse.add(chbUse);
		pnCenter.add(pnName);

		pnCenter.add(pnType);
		pnCenter.add(pnMMSI);
		pnCenter.add(pnCompany);
		pnCenter.add(pnUse);
		
		pnCenter.add(pnS);
		pnCenter.add(pnControl);


		
		
		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(pnCenter,BorderLayout.CENTER);
		
		ViewUtil.center(this, true);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command .equals("확인"))
		{
			
			if(txfVesselName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "선박명이 없습니다.");
				return;				
			}

			String vesselName = txfVesselName.getText();
			String vesselAbbr = vesselName.toUpperCase();
			
			
			
			KSGTableColumn con =(KSGTableColumn) cbxType.getSelectedItem();
			
			
			try {
				
				
				HashMap<String, Object> param = new HashMap<String, Object>();
				param.put("vessel_name",vesselName);
				param.put("vessel_abbr",(vesselAbbr==null||vesselAbbr.equals("")?vesselName:vesselAbbr));
				param.put("vessel_type", con.columnField);
				
				
				param.put("vessel_use",chbUse.isSelected()?Vessel.NON_USE:Vessel.USE);
				param.put("vessel_company", txfCompany.getText());
				
				if(!cbxMMSICheck.isSelected())
				{
					int mmsi_size=txfMMSI.getText().length();
					if(mmsi_size<9)					
					{
						JOptionPane.showMessageDialog(this, "MMSI는 9자리입니다.");
						return;
					}
					param.put("vessel_mmsi", txfMMSI.getText());
				}
				else
				{
					param.put("vessel_mmsi", "");
				}
				
				
				service.insert(param);
				
				pnVessel.fnSearch();
				
				this.setVisible(false);
				
				this.dispose();
				
				result = KSGDialog.SUCCESS;
				
				JOptionPane.showMessageDialog(this, "추가했습니다.");				

			} catch (SQLException e1) 
			{
				if(e1.getErrorCode()==2627)
				{					

					JOptionPane.showMessageDialog(this, "동일한 선박명이 있습니다.");
					
					
				}
				else if(e1.getErrorCode()==8152)
				{
					JOptionPane.showMessageDialog(this, "9자리까지 입력 할수 있습니다.");	
				}
				else
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
	
	@Override
	public void componentShown(ComponentEvent e) {
		cbxType.initComp();
		

		
		
	}
	


}
