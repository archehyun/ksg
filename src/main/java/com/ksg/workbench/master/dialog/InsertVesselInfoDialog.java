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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.NumberFormatter;
import javax.swing.text.PlainDocument;

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.HintTextField;
import com.ksg.view.comp.KSGCheckBox;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.master.comp.PnVessel;

@SuppressWarnings("serial")
public class InsertVesselInfoDialog extends BaseInfoDialog{

	private String LABEL = "���ڸ� �߰�";
	private JTextField txfVesselName;
	private JTextField txfMMSI;
	private JTextField txfCompanyName;	

	private JCheckBox chbUse;

	private KSGComboBox cbxConType;


	VesselService service = new VesselServiceImpl();

	Vessel dataInfo;

	private JCheckBox cbxMMSICheck;

	public InsertVesselInfoDialog()
	{
		super();
		this.setTitle("�ű� ���� ���� �߰�");
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

		this.getContentPane().add(buildTitle(),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);



		this.pack();

		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);
	}

	private KSGPanel buildCenter()
	{	
		// initcomp
		txfVesselName = new JTextField(20);
		txfCompanyName = new JTextField(20);
		chbUse= new KSGCheckBox("������");


		cbxConType = new KSGComboBox("conType");
		
		txfMMSI = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#")));
		txfMMSI.setPreferredSize(new Dimension(75,20));
		txfMMSI.setEnabled(false);
		txfMMSI.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(((JFormattedTextField)e.getSource()).getText().length()>8)
					e.consume();
				
			}
		});
		
		cbxMMSICheck = new JCheckBox("����",true);
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


		JButton butSearchCompany = new JButton("��ȸ");
		butSearchCompany.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				KSGDialog dialog = new SearchCompanyInfoDialog(InsertVesselInfoDialog.this,txfCompanyName);
				dialog.createAndUpdateUI();
			}

		});

		JButton butCancelCompany = new JButton("���");
		butCancelCompany.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				txfCompanyName.setText("");


			}});

		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.add(createFormItem(txfVesselName, "���ڸ�(*)"));
		pnCenter.add(createFormItem(cbxConType, "���� Ÿ��"));
		pnCenter.add(createFormItem(txfMMSI,cbxMMSICheck, "MMSI"));
		pnCenter.add(createFormItem(txfCompanyName,butSearchCompany,"��ǥ ����"));
		pnCenter.add(createFormItem(chbUse,"��� ����"));


		KSGPanel pnMain = new KSGPanel();
		pnMain.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));		
		pnMain.add(pnCenter);
		return pnMain;
	}

	private KSGPanel buildTitle() {
		KSGPanel pnTitle = new KSGPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		JLabel label = new JLabel(LABEL);
		label.setFont(new Font("����",0,16));
		pnTitle.add(label);

		return pnTitle;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command .equals("����"))
		{	
			if(txfVesselName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "���ڸ��� �����ϴ�.");
				return;				
			}

			String vesselName = txfVesselName.getText();

			String vesselAbbr = vesselName.toUpperCase();

			KSGTableColumn con =(KSGTableColumn) cbxConType.getSelectedItem();
			
			


			try {


				HashMap<String, Object> param = new HashMap<String, Object>();
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
						JOptionPane.showMessageDialog(this, "MMSI�� 9�ڸ� �����Դϴ�.");
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

				JOptionPane.showMessageDialog(this, "�߰��߽��ϴ�.");				

			} 
			catch(AlreadyExistException ee)
			{
				JOptionPane.showMessageDialog(this, "�����ϴ� ���ڸ��Դϴ�");

			}

			catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(this, ee.getMessage());

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
		cbxConType.initComp();		

	}
	class MMISLimit extends PlainDocument
	{
		private int limit;
		MMISLimit(int limit) {
			super();
			this.limit = limit;
		}

		MMISLimit(int limit, boolean upper) {
			super();
			this.limit = limit;
		}

		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null)
				return;

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}

	}



}