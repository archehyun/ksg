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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.base.view.BaseInfoUI;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.domain.AreaInfo;

public class UpdateAreaInfodialog extends KSGDialog implements ActionListener {
	private Font defaultFont = new Font("����",0,10);
	private JLabel lblInfo;

	private JTextField txfAreaName;
	private JTextField txfAreaCode;
	private JTextField txfAreaBookCode;
	BaseInfoUI baseInfoUI;
	String title;
	AreaInfo selectedInfo;

	/**
	 * @deprecated
	 * @param baseInfoUI
	 */
	public UpdateAreaInfodialog(BaseInfoUI baseInfoUI) {
		super();
		this.baseInfoUI=baseInfoUI;
		title = "���� ���� �߰�";
		
	}

	/**
	 * @deprecated
	 * @param infoUI
	 * @param info
	 */
	public UpdateAreaInfodialog(BaseInfoUI infoUI, AreaInfo info) {
		this(infoUI);
		title = "���� ���� ����";
		selectedInfo= info;
	}

	public UpdateAreaInfodialog(AreaInfo info) {
		super();
		title = "���� ���� ����";
		selectedInfo= info;
	}

	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle(title);
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		txfAreaName = new JTextField(21);
		txfAreaCode = new JTextField(5);
		txfAreaBookCode = new JTextField(5);
		lblInfo = new JLabel("",JLabel.RIGHT);
		JPanel pnName = new JPanel();
		pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaName = new JLabel("������");
		lblAreaName.setPreferredSize(new Dimension(80,25));
		pnName.add(lblAreaName);
		pnName.add(txfAreaName);
		JPanel pnCode = new JPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaCode = new JLabel("�����ڵ�");
		lblAreaCode.setPreferredSize(new Dimension(80,25));
		pnCode.add(lblAreaCode);	
		pnCode.add(txfAreaCode);
		
		JLabel lblAreaBookCode = new JLabel("���� å �ڵ�");
		lblAreaBookCode.setPreferredSize(new Dimension(100,25));
		pnCode.add(lblAreaBookCode);
		pnCode.add(txfAreaBookCode);
		


		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));

		JPanel pnControl =  new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOK = new JButton("Ȯ��");
		
		butOK.addActionListener(this);
		JButton butCancel = new JButton("���");
		butCancel.addActionListener(this);

		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		JLabel label = new JLabel("���� ���� ����");
		label.setFont(new Font("����",0,16));
		pnTitle.add(label);


		JPanel pnInfo = new JPanel();
		pnInfo.setLayout( new FlowLayout(FlowLayout.LEFT));
		pnInfo.add(lblInfo);

		pnCenter.add(pnName);
		pnCenter.add(pnCode);
		pnCenter.add(pnInfo);
		pnCenter.add(pnS);
		pnCenter.add(pnControl);

		JPanel left = new JPanel();
		left.setPreferredSize(new Dimension(15,0));
		JPanel right = new JPanel();
		right.setPreferredSize(new Dimension(15,0));
		if(selectedInfo!=null)
		{
			txfAreaCode.setText(selectedInfo.getArea_code());
			txfAreaName.setText(selectedInfo.getArea_name());
			txfAreaBookCode.setText(String.valueOf(selectedInfo.getArea_book_code()));
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

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Ȯ��"))
		{
			if(txfAreaName.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�������� ��������");
				return;
			}
			if(txfAreaCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ڵ��ȣ�� ��������");
				return;
			}
			try{
				int codeNum =Integer.parseInt(txfAreaCode.getText());
				
				
				AreaInfo insert = new AreaInfo();
				insert.setArea_name(txfAreaName.getText());
				insert.setArea_code(txfAreaCode.getText());
				insert.setArea_book_code(Integer.parseInt(txfAreaBookCode.getText()));
				
				insert.setBase_area_name(selectedInfo.getArea_name());
				baseService.updateAreaInfo(insert);
				result=KSGDialog.SUCCESS;
				JOptionPane.showMessageDialog(this, "���� �߽��ϴ�.");
				close();
				//baseInfoUI.updateTable();
				
			}catch (NumberFormatException nume) {
				JOptionPane.showMessageDialog(this, "���� ������ �߸� �Ǿ����ϴ�.");
				nume.printStackTrace();
			}catch(SQLException sqle)
			{
				if(sqle.getErrorCode()==2627)//mssql �ߺ�Ű �����ڵ�
				{
					int option=JOptionPane.showConfirmDialog(this, "�ڵ尡 �����մϴ�. ������Ʈ �Ͻðڽ��ϱ�?", "�ڵ� ����", JOptionPane.YES_NO_OPTION);
					if(option==JOptionPane.YES_OPTION)
					{
						AreaInfo insert = new AreaInfo();
						insert.setArea_name(txfAreaName.getText());
						insert.setArea_code(txfAreaCode.getText());
						insert.setBase_area_name(selectedInfo.getArea_name());
						try {
							baseService.updateAreaInfo(insert);
							result=KSGDialog.SUCCESS;
							//baseInfoUI.updateTable();
						} catch (SQLException e1) {
							JOptionPane.showMessageDialog(this,sqle.getErrorCode()+","+ sqle.getMessage());	
						}
						JOptionPane.showMessageDialog(this, "���� �߽��ϴ�.");
						close();
					}
				}else
				{
					JOptionPane.showMessageDialog(this,sqle.getErrorCode()+","+ sqle.getMessage());	
				}
			}

		}
		else
		{
			result=KSGDialog.FAILE;
			close();
		}
	}

}