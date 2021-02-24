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
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.ksg.base.view.BaseInfoUI;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.domain.Code;
import com.ksg.domain.Vessel;

/**
 * 선박 정보 수정 다이어그램
 * 
 * @author 박창현
 *
 */
public class UpdateVesselInfoDialog extends KSGDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  String LABEL,vesselName;
	private JTextField txfCompanyName;
	private JTextField txfMMSI;
	private String UPDATE_TITLE = "선박 정보 수정";
	private String INSERT_TITLE = "선박 정보 추가";
	private String data;

	private JList listVesselAbbr;

	private JComboBox cbxType;

	private JCheckBox chbUse;

	private Vessel dataInfo;
	private JCheckBox cbxMMSICheck;
	/**
	 * @deprecated
	 * @param baseInfoUI
	 */
	public UpdateVesselInfoDialog(BaseInfoUI baseInfoUI) {
		super();

		logger.info("생성");
		this.baseInfoUI=baseInfoUI;
		this.setTitle(INSERT_TITLE);
	}
	/**
	 * @deprecated
	 * @param baseInfoUI
	 * @param data
	 */
	public UpdateVesselInfoDialog(BaseInfoUI baseInfoUI,String data) {
		super();

		this.baseInfoUI=baseInfoUI;
		this.setTitle(UPDATE_TITLE);

		this.data=data;

		logger.info("data:"+data);


	}

	public UpdateVesselInfoDialog(Vessel vessel)
	{
		super();
		this.setTitle(UPDATE_TITLE);	
		this.dataInfo = vessel;
	}


	/**
	 * @deprecated
	 * @param baseInfoUI
	 * @param vessel
	 */
	public UpdateVesselInfoDialog(BaseInfoUI baseInfoUI, Vessel vessel) {
		super();
		this.baseInfoUI=baseInfoUI;
		this.setTitle(UPDATE_TITLE);
		this.dataInfo = vessel;
	}
	public void createAndUpdateUI() 
	{
		this.setModal(true);

		cbxType = new JComboBox();
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfCompanyName = new JTextField(20);
		txfCompanyName.setEditable(false);
		txfCompanyName.setEnabled(false);
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
		txfMMSI = new JTextField(5);

		Vessel info = new Vessel();
		info.setVessel_name(dataInfo.getVessel_name());
		try {
			List li=baseService.getVesselListGroupByName(info);
			if(li.size()>0)
			{	
				Vessel item = (Vessel) li.get(0);
				if(item.getVessel_use()==Vessel.NON_USE)
				{
					chbUse.setSelected(true);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		JPanel pnRequest = new JPanel();
		pnRequest.setLayout(new FlowLayout(FlowLayout.LEFT));


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

		cbxMMSICheck = new JCheckBox("없음");
		cbxMMSICheck.setSelected(false);
		cbxMMSICheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				txfMMSI.setEnabled(!cbxMMSICheck.isSelected());				


				if(cbxMMSICheck.isSelected())
				{
					if(dataInfo.getVessel_mmsi()!=null)
					{	
						txfMMSI.setText("");						
					}
				}
				else
				{
					txfMMSI.setText(dataInfo.getVessel_mmsi().equals("         ")?"":dataInfo.getVessel_mmsi());		
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


		JPanel pnUse = new JPanel();
		pnUse.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblUse = new JLabel("사용유무");
		lblUse.setPreferredSize(new Dimension(100,25));
		pnUse.add(lblUse);
		pnUse.add(chbUse);

		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		JPanel pnControl =  new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("저장");
		JButton butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		LABEL ="선박 명: ";
		if(dataInfo!=null)
		{		
			LABEL +=dataInfo.getVessel_name();
			vesselName =dataInfo.getVessel_name();
		}

		JLabel label = new JLabel(LABEL);
		label.setFont(new Font("돋움",0,16));
		pnTitle.add(label);

		listVesselAbbr = new JList();


		try {
			List li=baseService.getVessel_AbbrList(dataInfo.getVessel_name());

			Iterator iter = li.iterator();
			DefaultListModel model = new DefaultListModel();

			while(iter.hasNext())
			{
				Vessel portInfo = (Vessel) iter.next();
				model.addElement(portInfo.getVessel_abbr());
			}

			listVesselAbbr.setModel(model);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}


		JPanel pnVesselAbbrList = new JPanel(new BorderLayout());

		JLabel lblVesselAbbrList = new JLabel("선박 약어");
		lblVesselAbbrList.setPreferredSize(new Dimension(110,25));

		pnVesselAbbrList.add(lblVesselAbbrList,BorderLayout.WEST);
		pnVesselAbbrList.add(new JScrollPane(listVesselAbbr));


		pnCenter.add(pnRequest);
		pnCenter.add(pnType);
		pnCenter.add(pnMMSI);
		pnCenter.add(pnCompany);
		pnCenter.add(pnUse);
		pnCenter.add(pnVesselAbbrList);
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
		this.pack();
		initView();	
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);
	}
	private void initView() {
		
		logger.info("init vesselInfo:"+dataInfo.getVessel_mmsi());
		if(dataInfo!=null)
		{		
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
			txfCompanyName.setText(dataInfo.getVessel_company());

			if(dataInfo.getVessel_mmsi()!=null&&!dataInfo.getVessel_mmsi().equals("         "))
			{
				cbxMMSICheck.setSelected(false);
				txfMMSI.setEnabled(true);
				txfMMSI.setText(dataInfo.getVessel_mmsi());
			}
			else
			{
				cbxMMSICheck.setSelected(true);
				txfMMSI.setEnabled(false);	
			}

		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command .equals("저장"))
		{
			Vessel vessel = new Vessel();

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


			vessel.setVessel_name(this.vesselName);
			ConType con =(ConType) cbxType.getSelectedItem();
			vessel.setVessel_type(con.getTypeField());
			vessel.setVessel_use(chbUse.isSelected()?Vessel.NON_USE:Vessel.USE);
			vessel.setVessel_company(txfCompanyName.getText());

			try {
				baseService.updateVessel(vessel);


				this.setVisible(false);
				this.dispose();
				JOptionPane.showMessageDialog(this, "수정했습니다.");

				result=KSGDialog.SUCCESS;

			} catch (SQLException e1) 
			{	
				if(e1.getErrorCode()==8152)
				{
					JOptionPane.showMessageDialog(this, "9자리까지 입력 할수 있습니다.");	
				}
				else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());	
				}

			}
		}else
		{
			result=KSGDialog.FAILE;
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
