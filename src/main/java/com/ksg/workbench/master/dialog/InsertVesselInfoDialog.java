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
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;

import com.dtp.api.control.VesselController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.checkbox.KSGCheckBox;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.dialog.MainTypeDialog;
import com.ksg.workbench.common.dialog.SearchCompanyDialog2;

@SuppressWarnings("serial")
public class InsertVesselInfoDialog extends MainTypeDialog{

	private String LABEL = "선박명 추가";

	private JTextField txfVesselName;

	private JTextField txfMMSI;

	private JTextField txfCompanyName;	

	private JCheckBox chbUse;

	private KSGComboBox cbxConType;

	private JCheckBox cbxMMSICheck;
	
	private String vessel_name;

	public InsertVesselInfoDialog()
	{
		super();

//		this.setTitle("신규 선박 정보 추가");
		
		this.titleInfo="신규 선박 정보 추가";

		this.setController(new VesselController());

		this.addComponentListener(this);
	}
	
	public InsertVesselInfoDialog(String vessel_name)
	{
		this();
		
		this.vessel_name = vessel_name;
	}

	public void createAndUpdateUI() 
	{	
		this.setModal(true);

		this.getContentPane().add(buildHeader(titleInfo),BorderLayout.NORTH);

		this.addComp(buildCenter(),BorderLayout.CENTER);

		this.addComp(buildControl(),BorderLayout.SOUTH);

		ViewUtil.center(this,true);

		this.setResizable(false);

		this.setVisible(true);

	}

	private KSGPanel buildCenter()
	{	
		txfVesselName = new JTextField(20);

		txfCompanyName = new JTextField(20);
		
		txfCompanyName.setEditable(false);

		chbUse= new KSGCheckBox("사용안함");

		cbxConType = new KSGComboBox("conType");

		txfMMSI = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#")));


		txfMMSI.setEnabled(false);

		txfMMSI.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if(((JFormattedTextField)e.getSource()).getText().length()>8)
					e.consume();
			}
		});

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

		KSGGradientButton butSearchCompany = new KSGGradientButton("조회");
		butSearchCompany.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		butSearchCompany.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				
				SearchCompanyDialog2 searchDialog = new SearchCompanyDialog2();
				
				searchDialog.createAndUpdateUI();
				if(searchDialog.resultObj==null) return;
				
				CommandMap result = (CommandMap) searchDialog.resultObj;
				
				String company_name = (String) result.get("company_name");
				
				txfCompanyName.setText(company_name);
				
			}
		});

		JButton butCancelCompany = new JButton("취소");
		
		butCancelCompany.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				txfCompanyName.setText("");
			}});

		Box pnCenter = new Box(BoxLayout.Y_AXIS);

		pnCenter.add(createFormItem(txfVesselName, "선박명(*)"));

		pnCenter.add(createFormItem(cbxConType, "선박 타입"));
		
		Box mmsi = Box.createHorizontalBox();
		mmsi.add(txfMMSI);
		mmsi.add(Box.createHorizontalStrut(25));
		mmsi.add(cbxMMSICheck);
		mmsi.add(Box.createHorizontalStrut(25));

		pnCenter.add(createFormItem(mmsi, "MMSI"));
		
		Box company = Box.createHorizontalBox();
		company.add(txfCompanyName);
		company.add(Box.createHorizontalStrut(20));
		company.add(butSearchCompany);
		company.add(Box.createHorizontalStrut(25));

		pnCenter.add(createFormItem(company,"대표 선사"));

		pnCenter.add(createFormItem(chbUse,"사용 유무"));

		KSGPanel pnMain = new KSGPanel();
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
		
		pnMain.add(pnCenter);
		
		return pnMain;
	}



	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command .equals("저장"))
		{	
			if(txfVesselName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "선박명이 없습니다.");
				return;				
			}

			String vesselName = txfVesselName.getText();

			String vesselAbbr = vesselName.toUpperCase();

			KSGTableColumn con =(KSGTableColumn) cbxConType.getSelectedItem();

			CommandMap param = new CommandMap();

			param.put("vessel_name",vesselName);
			
			param.put("vessel_abbr",(vesselAbbr==null||vesselAbbr.equals("")?vesselName:vesselAbbr));
			
			param.put("vessel_type", con.columnField);
			
			param.put("vessel_use",chbUse.isSelected()?Vessel.NON_USE:Vessel.USE);
			
			param.put("vessel_company", txfCompanyName.getText());

			if(!cbxMMSICheck.isSelected())
			{
				int mmsi_size=txfMMSI.getText().length();
				if(mmsi_size<9)					
				{
					JOptionPane.showMessageDialog(this, "MMSI는 9자리 숫자입니다.");
					return;
				}
				param.put("vessel_mmsi", txfMMSI.getText());
			}
			else
			{
				param.put("vessel_mmsi", "");
			}

			callApi("insertVessel", param);
		}else
		{
			result = KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
		cbxConType.initComp();
		
		if(vessel_name!=null&&!vessel_name.isEmpty()) txfVesselName.setText(vessel_name);

	}
	class MMISLimit extends PlainDocument
	{
		private int limit;
		
		public MMISLimit(int limit) {
			super();
			this.limit = limit;
		}		

		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			
			if (str == null) return;

			if ((getLength() + str.length()) <= limit) {
				
				super.insertString(offset, str, attr);
			}
		}
	}
	
	@Override
	public void updateView() {
		CommandMap resultMap= this.getModel();

		String serviceId=(String) resultMap.get("serviceId");

		if("insertVessel".equals(serviceId))
		{	
			result = KSGDialog.SUCCESS;

			JOptionPane.showMessageDialog(this, "추가했습니다.");	

			close();
		}
	}
}