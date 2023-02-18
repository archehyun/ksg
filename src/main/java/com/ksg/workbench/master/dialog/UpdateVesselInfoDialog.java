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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.KSGCheckBox;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;


/**

  * @FileName : UpdateVesselInfoDialog.java

  * @Project : KSG2

  * @Date : 2022. 3. 12. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 선박 정보 수정 다이어그램

  */
public class UpdateVesselInfoDialog extends BaseInfoDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private  String LABEL,vesselName;
	
	private JTextField txfCompanyName;
	
	private JTextField txfMMSI;	

	private JList listVesselAbbr;

	private KSGComboBox cbxConType;

	private JCheckBox chbUse;
	
	HashMap<String, Object> info;
	
	VesselService service;
	
	private JCheckBox cbxMMSICheck;
	
	public UpdateVesselInfoDialog(HashMap<String, Object> item) {
		super();
		service = new VesselServiceImpl();
		this.info = item;
		this.addComponentListener(this);
	}
	public void createAndUpdateUI() 
	{
		this.setModal(true);			
		
		this.getContentPane().add(buildTitle(),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		

		this.pack();
		
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	private KSGPanel buildTitle() {
		KSGPanel pnTitle = new KSGPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		LABEL ="선박 명: ";
		

		
		LABEL +=info.get("vessel_name");
		vesselName = (String) info.get("vessel_name");
		JLabel label = new JLabel(LABEL);
		label.setFont(new Font("돋움",0,16));
		pnTitle.add(label);
		
		return pnTitle;
	}
	
	private KSGPanel buildCenter()
	{	
		// initcomp
		txfCompanyName = new JTextField(20);
		txfCompanyName.setEditable(false);
		txfCompanyName.setEnabled(false);
		
		chbUse= new KSGCheckBox("사용안함");
		
		
		txfMMSI = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#")));
		txfMMSI.setPreferredSize(new Dimension(75,20));
		
		txfMMSI.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(((JFormattedTextField)e.getSource()).getText().length()>8)
					e.consume();
				
			}
		});
		
		
		
		
		cbxConType = new KSGComboBox("conType");
		
		cbxMMSICheck = new KSGCheckBox("없음");
		cbxMMSICheck.setSelected(false);
		cbxMMSICheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				txfMMSI.setEnabled(!cbxMMSICheck.isSelected());				

				String mmsi = (String) info.get("vessel_mmsi");
				if(cbxMMSICheck.isSelected())
				{	
					if(mmsi!=null)
					{	
						txfMMSI.setText("");						
					}
				}
				else
				{
					txfMMSI.setText(mmsi.equals("         ")?"":mmsi);		
				}
			}
		});
	

		JButton butSearchCompany = new JButton("조회");
		butSearchCompany.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				KSGDialog dialog = new SearchCompanyInfoDialog(UpdateVesselInfoDialog.this,txfCompanyName);
				dialog.createAndUpdateUI();
			}

		});


		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.add(createFormItem(cbxConType, "선박 타입"));
		pnCenter.add(createFormItem(txfMMSI,cbxMMSICheck, "MMSI"));
		pnCenter.add(createFormItem(txfCompanyName,butSearchCompany,"대표 선사"));
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
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			

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
			
			KSGTableColumn conType = (KSGTableColumn) cbxConType.getSelectedItem(); 


			param.put("vessel_name", this.vesselName);			
			param.put("vessel_type", conType.columnField);
			param.put("vessel_use", chbUse.isSelected()?Vessel.NON_USE:Vessel.USE);
			param.put("vessel_company", txfCompanyName.getText());			
			
			try {
				
				service.update(param);
				


				this.setVisible(false);
				this.dispose();
				JOptionPane.showMessageDialog(this, "수정했습니다.");

				result=KSGDialog.SUCCESS;

			} catch (SQLException e1) 
			{	
				
				e1.printStackTrace();
				if(e1.getErrorCode()==8152)
				{
					JOptionPane.showMessageDialog(this, "9자리까지 입력 할수 있습니다.");	
				}
				else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());	
				}

			}catch(Exception ee)
			{
				JOptionPane.showMessageDialog(UpdateVesselInfoDialog.this,ee.getMessage());
				ee.printStackTrace();
				
					
			}
		}else
		{
			result=KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}

	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		
		cbxConType.initComp();
		
		
		String vessel_type = (String) info.get("vessel_type");
		String vessel_company = (String) info.get("vessel_company");
		String vessel_mmsi = (String) info.get("vessel_mmsi");

		int use=(Short)info.get("vessel_use");
		if(use==Vessel.NON_USE)
		{
			chbUse.setSelected(true);
		}
		
		
		txfCompanyName.setText(vessel_company);

		if(vessel_mmsi!=null&&!vessel_mmsi.equals("         "))
		{
			cbxMMSICheck.setSelected(false);
			txfMMSI.setEnabled(true);
			txfMMSI.setText(vessel_mmsi);
		}
		else
		{
			cbxMMSICheck.setSelected(true);
			txfMMSI.setEnabled(false);	
		}
		
		for(int i=0;i<cbxConType.getItemCount();i++)
		{
			KSGTableColumn type=(KSGTableColumn) cbxConType.getItemAt(i);

			if(type.columnField.equals(vessel_type))
			{
				cbxConType.setSelectedIndex(i);
				break;
			}
		}
		
	}



}
