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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.ksg.base.view.BaseInfoUI;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.Company;

public class UpdateCompanyInfoDialog extends KSGDialog implements ActionListener  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BaseService baseService;
	private JTextField txfCompany_name; // �����
	private JTextField txfCompany_abbr; // ���� ���
	private JTextField txfAgent_name;// ������Ʈ��
	private JTextField txfAgent_abbr;// ������Ʈ ���
	private JTextArea txaContents;// ���

	public static final int UPDATE=1;
	public static final int INSERT=0;
	private int type;
	private JLabel lblTitle;

	private String titleInfo;
	private String title;
	private String base_company_abbr;

	private Company companyInfo;


	public UpdateCompanyInfoDialog(int type, String company_abbr)
	{
		super();
		try {
			baseService = DAOManager.getInstance().createBaseService();

			switch (type) {
			case UPDATE:
				title = "���� ���� ����";
				titleInfo="���� ���� ����";
				break;
			case INSERT:
				title = "Company ���� �߰�";
				titleInfo="Add a Company Field";
				break;

			default:
				break;
			}
			base_company_abbr=company_abbr;

			companyInfo= baseService.getCompanyInfo(company_abbr);

		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int result=0;
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Ȯ��"))
		{
			Company info = new Company();
			info.setCompany_name(txfCompany_name.getText());
			info.setCompany_abbr(txfCompany_abbr.getText());
			info.setAgent_name(txfAgent_name.getText());
			info.setAgent_abbr(txfAgent_abbr.getText());
			info.setBase_company_abbr(base_company_abbr);
			info.setContents(txaContents.getText());

			try {

				baseService.updateCompany(info);
				JOptionPane.showMessageDialog(null,"���� �߽��ϴ�.");
				this.setVisible(false);
				this.dispose();	

				result=1;
			} catch (SQLException e1) {
				if(e1.getErrorCode()==2627)
				{					

				}else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
					e1.printStackTrace();
				}
			}			

		}else if(command.equals("���"))
		{
			this.setVisible(false);
			this.dispose();

		}

	}

	public void createAndUpdateUI() {
		this.setModal(true);

		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfCompany_name = new JTextField(20);
		txfCompany_abbr = new JTextField(20);
		txfAgent_name = new JTextField(20);
		txfAgent_abbr = new JTextField(20);
		txaContents = new JTextArea(8,32);
		JPanel pnCompany_name = new JPanel();
		pnCompany_name.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		JPanel pnControl =  new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("Ȯ��");

		JButton butCancel = new JButton("���");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		lblTitle = new JLabel("Add a Company Field");
		lblTitle.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(lblTitle);

		pnCenter.add( addFormItem(txfCompany_name,"�����"));
		pnCenter.add( addFormItem(txfCompany_abbr,"����� ���"));
		pnCenter.add(addFormItem(txfAgent_name,"������Ʈ"));
		pnCenter.add(addFormItem(txfAgent_abbr,"������Ʈ ���"));
		pnCenter.add(addFormItem(txaContents,"���"));
		pnCenter.add(pnS);
		pnCenter.add(pnControl);

		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(15,0));
		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension(15,0));

		this.setTitle(title);
		this.lblTitle.setText(titleInfo);
		this.txfCompany_abbr.setText(companyInfo.getCompany_abbr());
		this.txfCompany_name.setText(companyInfo.getCompany_name());
		this.txfAgent_abbr.setText(companyInfo.getAgent_abbr());
		this.txfAgent_name.setText(companyInfo.getAgent_name());
		this.txaContents.setText(companyInfo.getContents());
		switch (type) {
		case UPDATE:

			butOK.setText("����");
			butOK.setActionCommand("Ȯ��");
			break;
		case INSERT:

			break;

		}


		this.getContentPane().add(pnTitle,BorderLayout.NORTH);
		this.getContentPane().add(pnCenter,BorderLayout.CENTER);
		this.getContentPane().add(left,BorderLayout.WEST);
		this.getContentPane().add(right,BorderLayout.EAST);
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setResizable(false);
		this.setVisible(true);

	}
	private JPanel addFormItem(JComponent comp, String title) {
		JPanel pnCompany_abbr = new JPanel();
		pnCompany_abbr.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany_abbr = new JLabel(title);
		lblCompany_abbr.setPreferredSize(new Dimension(100,25));
		pnCompany_abbr.add(lblCompany_abbr);	
		pnCompany_abbr.add(comp);
		return pnCompany_abbr;
	}
}