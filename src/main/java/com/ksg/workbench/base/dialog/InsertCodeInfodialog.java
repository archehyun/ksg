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
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Code;
import com.ksg.service.BaseServiceImpl;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.base.comp.PnCode;

/**
 * �ڵ� ���� �߰� ���̾�׷�
 * 
 * @author ��â��
 *
 */
public class InsertCodeInfodialog extends BaseInfoDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblInfo
	;

	private JTextField txfCodeName;
	private JTextField txtKorCode;
	
	private PnCode baseCode;
	
	private JTextField txfField;
	
	String code_type;
	String title;
	
	
	public InsertCodeInfodialog(BaseInfoUI baseInfoUI) 
	{
		super(baseInfoUI);
		baseService = new BaseServiceImpl();
		this.baseInfoUI=baseInfoUI;
		
	}

	public InsertCodeInfodialog(BaseInfoUI base, String type) {
		this(base);
		this.code_type=type;
		title =type;
		if(type.equals("Inbound ��� �ױ�"))
		{
			code_type="fromPort";
		}
		if(type.equals("�����̳� Ÿ��"))
		{
			code_type="conType";
		}
		if(type.equals("IN�ױ�"))
		{
			code_type="inPort";
		}
		
	}

	public InsertCodeInfodialog(BaseInfoUI base, String string,PnCode code) {
		this(base,string);
		this.baseCode = code;
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle("Code ���� �߰�");
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfCodeName = new JTextField(20);
		txtKorCode = new JTextField(20);
		txfField = new JTextField(20);
		lblInfo = new JLabel("",JLabel.RIGHT);
		lblInfo.setFont(defaultFont);
		
		JPanel pnName = new JPanel();
		pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCodeName = new JLabel("Name");
		lblCodeName.setPreferredSize(new Dimension(80,25));
		pnName.add(lblCodeName);
		pnName.add(txfCodeName);
		
		JPanel pnCode = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblKorName = new JLabel("�ѱ�");
		lblKorName.setPreferredSize(new Dimension(80,25));
		pnCode.add(lblKorName);	
		pnCode.add(txtKorCode);
		
		
		JPanel pnField = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblField = new JLabel("Field");
		lblField.setPreferredSize(new Dimension(80,25));
		pnField.add(lblField);	
		pnField.add(txfField);


		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		JPanel pnControl =  new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("Ȯ��");
		butOK.setFont(defaultFont);
		
		butOK.addActionListener(this);
		JButton butCancel = new JButton("���");
		butCancel.addActionListener(this);
		butCancel.setFont(defaultFont);

		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		JLabel label = new JLabel(title+" �ڵ� �߰�");
		label.setFont(new Font("����",0,16));
		pnTitle.add(label);
		JPanel pnInfo = new JPanel();
		pnInfo.setLayout( new FlowLayout(FlowLayout.LEFT));
		pnInfo.add(lblInfo);

		pnCenter.add(pnName);
		pnCenter.add(pnCode);
		pnCenter.add(pnField);
		pnCenter.add(pnInfo);
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
		ViewUtil.center(this, true);
		this.setResizable(false);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Ȯ��"))
		{
			if(txfCodeName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "Name�� ��������");
				return;
			}
			if(txtKorCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ѱ۸��� ��������");
				return;
			}
			try{
				String codeField = txfField.getText();
				
				Code op = new Code();
				op.setCode_field(codeField	);
				op.setCode_type(code_type);
				op.setCode_name(txfCodeName.getText());
				op.setCode_name_kor(txtKorCode.getText());
				
				baseService.insertCode(op);
				baseCode.updateCodeTable(title);
				JOptionPane.showMessageDialog(this, "�߰� �߽��ϴ�.");
				close();
				
				
			}catch(SQLException sqle)
			{
				if(sqle.getErrorCode()==2627)//mssql �ߺ�Ű �����ڵ�
				{	
					JOptionPane.showMessageDialog(InsertCodeInfodialog.this, "�ڵ尡 �����մϴ�.");
					/*int option=JOptionPane.showConfirmDialog(this, "�ڵ尡 �����մϴ�. ������Ʈ �Ͻðڽ��ϱ�?", "�ڵ� ����", JOptionPane.YES_NO_OPTION);
					if(option==JOptionPane.YES_OPTION)
					{
						AreaInfo insert = new AreaInfo();
						insert.setArea_name(txfCodeName.getText());
						insert.setArea_code(txtKorCode.getText());
						try {
							baseService.updateAreaInfo(insert);
							baseInfoUI.updateTable();
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(this,sqle.getErrorCode()+","+ sqle.getMessage());	
						}
						JOptionPane.showMessageDialog(this, "���� �߽��ϴ�.");
						close();
					}*/
				}else
				{
					JOptionPane.showMessageDialog(this,sqle.getErrorCode()+","+ sqle.getMessage());	
				}
			}
		}
		else
		{
			close();
		}
	}

}
