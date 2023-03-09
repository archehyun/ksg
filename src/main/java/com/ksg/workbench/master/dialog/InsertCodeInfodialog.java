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
import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Code;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;

/**
 * �ڵ� ���� �߰� ���̾�׷�
 * 
 * @author ��â��
 *
 */
public class InsertCodeInfodialog extends BaseInfoDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField txfCodeName;
	
	private JTextField txtKorCode;
	
	private JTextField txfField;
	
	private String code_type;
	
	private String title;
	
	private BaseServiceImpl baseService;
	
	public InsertCodeInfodialog(BaseInfoUI baseInfoUI) 
	{
		super(baseInfoUI);
		this.addComponentListener(this);
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



	public void createAndUpdateUI() {
		this.setModal(true);
		this.setTitle("Code ���� �߰�");

		this.getContentPane().add(buildTitle(title+" �ڵ� �߰�"),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		ViewUtil.center(this, true);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public KSGPanel buildCenter()
	{
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		
		txfCodeName = new JTextField(20);
		txtKorCode = new JTextField(20);
		txfField = new JTextField(20);	
		


		pnCenter.add(createFormItem(txfCodeName, "Name"));
		pnCenter.add(createFormItem(txtKorCode, "�ѱ�"));
		pnCenter.add(createFormItem(txfField, "Field"));
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		return pnMain;
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
