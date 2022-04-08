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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.AreaInfo;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * �������� ���� ���̾�׷�
 * 
 * @author ��â��
 *
 */
@SuppressWarnings("serial")
public class UpdateAreaInfodialog extends BaseInfoDialog implements ActionListener{
	
	
//	Area
	private JLabel lblInfo;
	HashMap<String, Object> param;
	private JTextField txfAreaName;
	private JTextField txfAreaCode;
	private JTextField txfAreaBookCode;
	
	BaseInfoUI baseInfoUI;
	
	String title;
	
	AreaInfo selectedInfo;

	AreaServiceImpl areaService = new AreaServiceImpl();
	private JLabel lblTitleInfo;
	public UpdateAreaInfodialog(int type) {
		super();
		title = "���� ���� ����";
		this.type = type;
		this.addComponentListener(this);
	}

	public UpdateAreaInfodialog(int type,HashMap<String, Object> param) {
		this(type);
		this.param = param;
	}

	private KSGPanel buildCenter()
	{	

		Box pnCenter = new Box(BoxLayout.Y_AXIS);
		pnCenter.setBackground(Color.white);
		txfAreaName = new JTextField(21);
		txfAreaCode = new JTextField(5);
		txfAreaBookCode = new JTextField(5);
		lblInfo = new JLabel("",JLabel.RIGHT);
		KSGPanel pnName = new KSGPanel();
		pnName.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaName = new JLabel("������");
		lblAreaName.setPreferredSize(new Dimension(80,25));
		pnName.add(lblAreaName);
		pnName.add(txfAreaName);
		KSGPanel pnCode = new KSGPanel();
		pnCode.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAreaCode = new JLabel("�����ڵ�");
		lblAreaCode.setPreferredSize(new Dimension(80,25));
		pnCode.add(lblAreaCode);	
		pnCode.add(txfAreaCode);

		JLabel lblAreaBookCode = new JLabel("���� å �ڵ�");
		lblAreaBookCode.setPreferredSize(new Dimension(100,25));
		pnCode.add(lblAreaBookCode);
		pnCode.add(txfAreaBookCode);
		KSGPanel pnInfo = new KSGPanel();
		pnInfo.setLayout( new FlowLayout(FlowLayout.LEFT));
		pnInfo.add(lblInfo);

		pnCenter.add(pnName);
		pnCenter.add(pnCode);
		pnCenter.add(pnInfo);
	
	
		
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		pnMain.add(pnCenter);
		return pnMain;
	}
	public void createAndUpdateUI() {
		this.setModal(true);
		
		
		
		this.setTitle(title);


		this.getContentPane().add(buildTitle("������������"),BorderLayout.NORTH);
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);		
		this.setResizable(false);
		this.setVisible(true);



	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("����"))
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
			
			if(txfAreaBookCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ڵ��ȣ�� ��������");
				return;
			}
			try{
				
				
				HashMap<String, Object> param = new HashMap<String, Object>();
				
				param.put("area_name", txfAreaName.getText());
				
				param.put("area_code", txfAreaCode.getText());
				
				param.put("area_book_code", Integer.parseInt(txfAreaBookCode.getText()));
				
				param.put("base_area_name", this.param.get("area_name"));
				
				areaService.updateArea(param);
				
				result=KSGDialog.SUCCESS;
				
				JOptionPane.showMessageDialog(this, "���� �߽��ϴ�.");
				
				close();

			}catch (NumberFormatException nume) {
				JOptionPane.showMessageDialog(this, "���� ������ �߸� �Ǿ����ϴ�.");
				nume.printStackTrace();
			}catch(SQLException sqle)
			{
				/*if(sqle.getErrorCode()==2627)//mssql �ߺ�Ű �����ڵ�
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
				}*/
			}

		}
		else if(command.equals("�߰�"))
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
			
			if(txfAreaBookCode.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ڵ��ȣ�� ��������");
				return;
			}
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			
			param.put("area_name", txfAreaName.getText());
			
			param.put("area_code", txfAreaCode.getText());
			
			param.put("area_book_code", Integer.parseInt(txfAreaBookCode.getText()));
			
			try {
				areaService.insertArea(param);
				result=KSGDialog.SUCCESS;
				JOptionPane.showMessageDialog(this, "���� �߽��ϴ�.");
				close();
			} catch (Exception e1) {
				
				e1.printStackTrace();
				
				JOptionPane.showMessageDialog(this, e1.getMessage());
				result=KSGDialog.FAILE;
				close();
			}
			
		}
		else
		{
			result=KSGDialog.FAILE;
			close();
		}
	}



	@Override
	public void componentShown(ComponentEvent e) {

		if(param!=null)
		{
			txfAreaCode.setText((String) param.get("area_code"));
			txfAreaName.setText((String) param.get("area_name"));
			txfAreaBookCode.setText(String.valueOf(param.get("area_book_code")));
		}
		
		switch (type) {
		case UPDATE:

			
			butOK.setActionCommand("����");

			break;
		case INSERT:

			
			butOK.setActionCommand("�߰�");

			break;

		}

	}	

}
