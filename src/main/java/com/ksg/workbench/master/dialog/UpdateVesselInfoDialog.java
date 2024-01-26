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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import com.dtp.api.control.VesselController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.checkbox.KSGCheckBox;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.Notification;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.dialog.MainTypeDialog;


/**

 * @FileName : UpdateVesselInfoDialog.java

 * @Project : KSG2

 * @Date : 2022. 3. 12. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 선박 정보 수정 다이어그램

 */
public class UpdateVesselInfoDialog extends MainTypeDialog{

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
	private HashMap<String, Object> info;

	private JCheckBox cbxMMSICheck;

	private KSGGradientButton butSearchCompany;

	public UpdateVesselInfoDialog(HashMap<String, Object> item) {
		super();

		this.setController( new VesselController());
		this.info = item;		
		this.titleInfo="신규 선박 정보 수정";
		this.addComponentListener(this);
	}
	public void createAndUpdateUI() 
	{
		this.initComp();
		
		this.setModal(true);

		this.getContentPane().add(buildHeader(titleInfo),BorderLayout.NORTH);

		this.addComp(buildCenter(),BorderLayout.CENTER);

		this.addComp(buildControl(),BorderLayout.SOUTH);

		ViewUtil.center(this,true);

		this.setResizable(false);

		this.setVisible(true);
	}

	private void initComp() {
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
		
		butSearchCompany = new KSGGradientButton("조회");

		butSearchCompany.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butSearchCompany.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				KSGDialog dialog = new SearchCompanyInfoDialog(UpdateVesselInfoDialog.this, txfCompanyName);
				dialog.createAndUpdateUI();
			}

		});
		
	}

	private KSGPanel buildCenter()
	{	
		KSGPanel pnMain = new KSGPanel();
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));	
		
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.add(createFormItem(cbxConType, "선박 타입"));
		pnCenter.add(createFormItem(txfMMSI,cbxMMSICheck, "MMSI"));
		pnCenter.add(createFormItem(txfCompanyName,butSearchCompany,"대표 선사"));
		pnCenter.add(createFormItem(chbUse,"사용 유무"));
		
		pnMain.add(pnCenter);

		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command .equals("저장"))
		{
			CommandMap param = new CommandMap();			

			if(!cbxMMSICheck.isSelected())			
			{
				int mmsi_size=txfMMSI.getText().length();

				if(mmsi_size<9)
				{
					NotificationManager.showNotification(Notification.Type.WARNING, "MMSI는 9자리 숫자입니다.");
					return;
				}
				param.put("vessel_mmsi", txfMMSI.getText());
			}

			KSGTableColumn conType = (KSGTableColumn) cbxConType.getSelectedItem(); 
			param.put("vessel_name", this.vesselName);			
			param.put("vessel_type", conType.columnField);
			param.put("vessel_use", chbUse.isSelected()?Vessel.NON_USE:Vessel.USE);
			param.put("vessel_company", txfCompanyName.getText());

			callApi("updateVessel",param);


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

		String vessel_type 		= (String) info.get("vessel_type");
		String vessel_company 	= (String) info.get("vessel_company");
		String vessel_mmsi 		= (String) info.get("vessel_mmsi");

		int use=(Integer)info.get("vessel_use");

		chbUse.setSelected(use==Vessel.NON_USE);
		
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

	@Override
	public void updateView() {
		
		CommandMap resultMap= this.getModel();

		String serviceId=(String) resultMap.get("serviceId");

		if("updateVessel".equals(serviceId))
		{
			
			NotificationManager.showNotification("수정했습니다.");

			result=KSGDialog.SUCCESS;
			
			close();

		}
	}
}
