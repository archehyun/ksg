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
package com.ksg.workbench.base.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import com.ksg.common.util.ViewUtil;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.domain.Code;
import com.ksg.domain.Vessel;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.workbench.base.BaseInfoUI;

@SuppressWarnings("serial")
public class InsertVesselInfoDialog extends KSGDialog implements ActionListener{
	protected Logger logger = Logger.getLogger(getClass());
	private  String LABEL = "선박명 추가";
	private JTextField txfVesselName;
	private JTextField txfMMSI;
	private JTextField txfCompany;
	private BaseInfoUI baseInfoUI;
	private String UPDATE_TITLE = "선박 정보 수정";
	private String INSERT_TITLE = "선박 정보 추가";
	private String data;
	private JComboBox cbxType;
	private JCheckBox chbUse;
	
	private BaseDAOManager baseDAOManager;

	Vessel dataInfo;
	private JCheckBox cbxMMSICheck;
	public InsertVesselInfoDialog()
	{
		super();
		this.setTitle("신규 선박 정보 추가");
		baseDAOManager = new BaseDAOManager();
		
	}
	public InsertVesselInfoDialog(BaseInfoUI baseInfoUI) {
		super();
		this.baseInfoUI=baseInfoUI;
		this.setTitle(INSERT_TITLE);
	}
	public InsertVesselInfoDialog(BaseInfoUI baseInfoUI,String data) {
		super();
		this.baseInfoUI=baseInfoUI;
		this.setTitle(UPDATE_TITLE);
		LABEL = "Update a Vessel Field";

		this.data=data;

		logger.debug("data:"+data);


	}

	public InsertVesselInfoDialog(BaseInfoUI baseInfoUI, Vessel vessel) {
		super();
		baseService = new BaseServiceImpl();
		this.baseInfoUI=baseInfoUI;
		this.setTitle(UPDATE_TITLE);
		LABEL = "Update a Vessel Field";
		this.dataInfo = vessel;
	}
	public void createAndUpdateUI() 
	{
		this.setModal(true);
		cbxType = new JComboBox();
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfVesselName = new JTextField(20);
		txfVesselName.addFocusListener(new FocusListener() {
			
			public void focusLost(FocusEvent arg0) {
				String vesselName=txfVesselName.getText();
				
				Vessel info = new Vessel();
				info.setVessel_name(vesselName);
				try {
					List li=baseService.getVesselListGroupByName(info);
					if(li.size()>0)
					{	
						Vessel item = (Vessel) li.get(0);
						int count=cbxType.getItemCount();
						for(int i=0;i<count;i++)
						{
							ConType type=(ConType) cbxType.getItemAt(i);
							
							if(type.getTypeField().equals(item.getVessel_type()))
							{
								cbxType.setSelectedIndex(i);
								cbxType.setEnabled(false);
								break;
							}
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			public void focusGained(FocusEvent arg0) {
				cbxType.setEnabled(true);
				
			}
		});
		txfCompany = new JTextField(20);
		Code code = new Code();
		code.setCode_name_kor("컨테이너 타입");
		try {
			List li=	baseService.getSubCodeInfo(code);
			
			DefaultComboBoxModel boxModel = new DefaultComboBoxModel();
			
			Iterator iter = li.iterator();
			
			while(iter.hasNext())
			{
				Code code2=(Code) iter.next();
				
				ConType conType = new ConType();
				conType.setTypeField(code2.getCode_field());
				conType.setTypeName(code2.getCode_name());
				boxModel.addElement(conType);
			}
			
			
			cbxType.setModel(boxModel);


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		chbUse= new JCheckBox("사용안함");
		
		txfMMSI = new JTextField(9);
		txfMMSI.setEnabled(false);
		
		
		JPanel pnName = new JPanel();
		pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaName = new JLabel("선박명(*)");
		lblAreaName.setPreferredSize(new Dimension(100,25));
		pnName.add(lblAreaName);
		pnName.add(txfVesselName);

		JPanel pnType = new JPanel();
		pnType.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblType = new JLabel("선박 타입");
		lblType.setPreferredSize(new Dimension(100,25));
		pnType.add(lblType);
		pnType.add(cbxType);
		
		JPanel pnMMSI = new JPanel();
		pnMMSI.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblMMSI = new JLabel("MMSI");
		lblMMSI.setPreferredSize(new Dimension(100,25));
		cbxMMSICheck = new JCheckBox("없음",true);
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
		
		
		JPanel pnCompany = new JPanel();
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
		
		JPanel pnUse = new JPanel();
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

		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(15,0));
		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension(15,0));
		
		if(dataInfo!=null)
		{
			txfVesselName.setText(dataInfo.getVessel_name());
			txfVesselName.setEditable(false);
			
			int count=cbxType.getItemCount();
			for(int i=0;i<count;i++)
			{
				ConType type=(ConType) cbxType.getItemAt(i);
				
				if(type.getTypeField().equals(dataInfo.getVessel_type()))
				{
					cbxType.setSelectedIndex(i);
					
					break;
				}
			}
		}		
		
		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(pnCenter,BorderLayout.CENTER);
		this.getContentPane().add(left,BorderLayout.WEST);
		this.getContentPane().add(right,BorderLayout.EAST);
		ViewUtil.center(this, true);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command .equals("확인"))
		{
			Vessel vessel = new Vessel();
			if(txfVesselName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "선박명이 없습니다.");
				return;				
			}
			
			if(!cbxMMSICheck.isSelected())
			{
				int mmsi_size=txfMMSI.getText().length();
				if(mmsi_size<9)					
				{
					JOptionPane.showMessageDialog(this, "MMSI는 9자리입니다.");
					return;
				}
				vessel.setVessel_mmsi(txfMMSI.getText());
			}
			

			String vesselName = txfVesselName.getText();
			String vesselAbbr = vesselName.toUpperCase();
			
			vessel.setVessel_name(vesselName);
			if(vesselAbbr==null||vesselAbbr.equals(""))
			{
				vessel.setVessel_abbr(vesselName);
			}else
			{
				vessel.setVessel_abbr(vesselAbbr);	
			}
			
			ConType con =(ConType) cbxType.getSelectedItem();
			vessel.setVessel_type(con.getTypeField());
			
			vessel.setVessel_use(chbUse.isSelected()?Vessel.NON_USE:Vessel.USE);
			vessel.setVessel_company(txfCompany.getText());
			
			try {
				baseDAOManager.insertNewVessel(vessel);
				
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
