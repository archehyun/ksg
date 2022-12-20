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
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.KSGCheckBox;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.dialog.KSGDialog;


/**

  * @FileName : UpdateVesselInfoDialog.java

  * @Project : KSG2

  * @Date : 2022. 3. 12. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 선박 정보 수정 다이어그램

  */
public class UpdateVesselInfoDialog extends KSGDialog implements ActionListener{

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

		cbxConType = new KSGComboBox("conType");
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfCompanyName = new JTextField(20);
		txfCompanyName.setEditable(false);
		txfCompanyName.setEnabled(false);
		
		chbUse= new KSGCheckBox("사용안함");
		txfMMSI = new JTextField(5);

		
		
		int use=(Short)info.get("vessel_use");
		if(use==Vessel.NON_USE)
		{
			chbUse.setSelected(true);
		}

		KSGPanel pnRequest = new KSGPanel();
		pnRequest.setLayout(new FlowLayout(FlowLayout.LEFT));


		KSGPanel pnType = new KSGPanel();
		pnType.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblType = new JLabel("선박 타입");
		lblType.setPreferredSize(new Dimension(100,25));
		pnType.add(lblType);
		pnType.add(cbxConType);

		KSGPanel pnMMSI = new KSGPanel();
		pnMMSI.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblMMSI = new JLabel("MMSI");
		lblMMSI.setPreferredSize(new Dimension(100,25));

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
		pnMMSI.add(lblMMSI);
		pnMMSI.add(txfMMSI);
		pnMMSI.add(cbxMMSICheck);



		KSGPanel pnCompany = new KSGPanel();
		pnCompany.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany = new JLabel("대표 선사");
		lblCompany.setPreferredSize(new Dimension(100,25));
		pnCompany.add(lblCompany);
		pnCompany.add(txfCompanyName);
		JButton butSearchCompany = new JButton("조회");
		butSearchCompany.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				KSGDialog dialog = new SearchCompanyInfoDialog(UpdateVesselInfoDialog.this,txfCompanyName);
				dialog.createAndUpdateUI();
			}

		});

		JButton butCancelCompany = new JButton("취소");
		butCancelCompany.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				txfCompanyName.setText("");			

			}});
		pnCompany.add(butSearchCompany);
		pnCompany.add(butCancelCompany);


		KSGPanel pnUse = new KSGPanel();
		pnUse.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblUse = new JLabel("사용유무");
		lblUse.setPreferredSize(new Dimension(100,25));
		pnUse.add(lblUse);
		pnUse.add(chbUse);

		KSGPanel pnS = new KSGPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		KSGPanel pnS1 = new KSGPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		KSGPanel pnControl =  new KSGPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("저장");
		JButton butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);

		KSGPanel pnTitle = new KSGPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		LABEL ="선박 명: ";

		
		LABEL +=info.get("vessel_name");
		vesselName = (String) info.get("vessel_name");
		JLabel label = new JLabel(LABEL);
		label.setFont(new Font("돋움",0,16));
		pnTitle.add(label);

		


		KSGPanel pnVesselAbbrList = new KSGPanel(new BorderLayout());

		JLabel lblVesselAbbrList = new JLabel("선박 약어");
		lblVesselAbbrList.setPreferredSize(new Dimension(110,25));

		pnVesselAbbrList.add(lblVesselAbbrList,BorderLayout.WEST);
		pnVesselAbbrList.add(new JScrollPane(listVesselAbbr));


		pnCenter.add(pnRequest);
		pnCenter.add(pnType);
		pnCenter.add(pnMMSI);
		pnCenter.add(pnCompany);
		pnCenter.add(pnUse);		
		pnCenter.add(pnS);
		pnCenter.add(pnControl);


			

		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(pnCenter,BorderLayout.CENTER);

		this.pack();
		
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);
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
					JOptionPane.showMessageDialog(this, "MMSI는 9자리입니다.");
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
				ee.printStackTrace();
				
				//JOptionPane.showMessageDialog(this, "error:"+ee.getMessage());	
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
