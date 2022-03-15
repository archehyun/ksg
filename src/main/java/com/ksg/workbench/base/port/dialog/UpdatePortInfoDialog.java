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
package com.ksg.workbench.base.port.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.AreaInfo;
import com.ksg.service.PortService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.base.dialog.BaseInfoDialog;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

/**
 * �ױ� ���� ���� ���̾�׷�
 * 
 * @author ��â��
 *
 */
public class UpdatePortInfoDialog extends BaseInfoDialog
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Object> param;

	private JTextField txfPort_name;

	private JTextField txfPort_nationailty;

	private JTextField txfPort_area;

	private JTextField txfArea_code;	
	

	
	private JLabel lblTitleInfo;
	
	private PortService portService;

	private BaseServiceImpl baseService;

	public UpdatePortInfoDialog(int type)
	{
		super();
		portService = new PortServiceImpl();
		baseService = new BaseServiceImpl(); 
		this.type = type;
	}
	
	public UpdatePortInfoDialog(int type, HashMap<String, Object> param) {
		this(type);
		this.param = param;
		
		
	}
	public void createAndUpdateUI() {
		
		this.addComponentListener(this);
		
		this.setModal(true);
		
		this.setTitle("�ױ� ���� ����");


		this.getContentPane().add(buildTitle("�ױ� ���� ����"),BorderLayout.NORTH);
		
		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setVisible(true);
	
	}
	
	public KSGPanel buildCenter()
	{
		txfPort_name = new JTextField(20);
		txfPort_nationailty = new JTextField(20);
		txfPort_area = new JTextField(20);
		txfArea_code = new JTextField(5);
		
		

		Box pnCenter = new Box(BoxLayout.Y_AXIS);
	
		txfArea_code.setEditable(false);
		txfPort_area.setEditable(false);
		
		
		

		KSGPanel pnArea_code = new KSGPanel();
		pnArea_code.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAgent_abbr = new JLabel("���� �ڵ�");

		lblAgent_abbr.setPreferredSize(new Dimension(100,25));
		JButton butSearchCode = new JButton("�ڵ� �˻�");
		butSearchCode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				final JDialog dialog = new JDialog(UpdatePortInfoDialog.this);
				dialog.setModal(true);
				KSGPanel pnMain = new KSGPanel();

				JTable tblAreaList = new JTable();
				pnMain.setLayout(new BorderLayout());

				KSGPanel pnControl = new KSGPanel();
				pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
				final JButton butOk = new JButton("Ȯ��");
				butOk.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						dialog.setVisible(false);
						dialog.dispose();
						
					}});
				butOk.setEnabled(false);
				JButton butCancel = new JButton("���");
				butCancel.addActionListener(new ActionListener(){


					public void actionPerformed(ActionEvent e) {
						txfPort_area.setText((String) param.get("port_area"));
						txfArea_code.setText((String) param.get("area_code"));
						dialog.setVisible(false);
						dialog.dispose();

					}});
				pnControl.add(butOk);
				pnControl.add(butCancel);
				try 
				{
					List li=baseService.getAreaInfoList();

					DefaultMutableTreeNode root = new DefaultMutableTreeNode("��ü����");
					Iterator iter =li.iterator();
					while(iter.hasNext())
					{
						AreaInfo area = (AreaInfo) iter.next();
						DefaultMutableTreeNode areaGroup = new DefaultMutableTreeNode(area.getArea_name());
						root.add(areaGroup);
						
					}
					final JTree tree = new JTree(root);
					tree.addMouseListener(new MouseAdapter() {

						public void mouseClicked(MouseEvent arg0) {
							if(arg0.getClickCount()>=0)
							{
								TreePath path=tree.getSelectionPath();
								if(path==null)
									return;
								switch (path.getPathCount()) {
								
								case 2:
									String area1=path.getLastPathComponent().toString();

									try {
										AreaInfo areaInfo = baseService.getAreaInfo(area1);
										txfArea_code.setText(areaInfo.getArea_code());
										txfPort_area.setText(areaInfo.getArea_name());
									} catch (SQLException e) {

										e.printStackTrace();
									}

									butOk.setEnabled(true);
									break;

								case 3:
									String area2=path.getLastPathComponent().toString();

									try {
										AreaInfo areaInfo = baseService.getAreaInfo(area2);
										txfArea_code.setText(areaInfo.getArea_code());
										txfPort_area.setText(areaInfo.getArea_name());
									} catch (SQLException e) {

										e.printStackTrace();
									}

									butOk.setEnabled(true);
									break;

								default:
								butOk.setEnabled(false);
								txfArea_code.setText("");
								txfPort_area.setText("");

								break;
								}

							}

						}
					});
					pnMain.add(new JScrollPane(tree),BorderLayout.CENTER);

				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}

				dialog.getContentPane().add(pnControl,BorderLayout.SOUTH);
				dialog.getContentPane().add(pnMain,BorderLayout.CENTER);
				dialog.setSize(400,400);
				dialog.setLocation(UpdatePortInfoDialog.this.getX()+UpdatePortInfoDialog.this.getWidth(), UpdatePortInfoDialog.this.getY());
				dialog.setVisible(true);


			}});
		
		pnArea_code.add(txfArea_code);
		pnArea_code.add(butSearchCode);		
		
		pnCenter.add(createFormItem(txfPort_name, "�ױ���"));
		pnCenter.add(createFormItem(txfPort_nationailty, "����"));
		pnCenter.add(createFormItem(txfPort_area, "����"));
		pnCenter.add(createFormItem(pnArea_code, "�����ڵ�"));	

		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		pnMain.add(pnCenter);
		return pnMain;
	}
	
		
	

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("����"))
		{
			
			if(txfPort_name.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ױ����� �����ϴ�.");
				return;
			}
			if(txfPort_nationailty.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�������� �����ϴ�.");
				return;
			}
			if(txfArea_code.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ڵ��ȣ�� �����ϼ���");
				return;
			}
			
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			
			
			param.put("area_code", txfArea_code.getText());
			
			param.put("port_name", txfPort_name.getText());
			
			param.put("port_nationality", txfPort_nationailty.getText());
			
			param.put("port_area", txfPort_area.getText());
			
			param.put("base_port_name", this.param.get("port_name"));

			try {
				portService.update(param);
				this.setVisible(false);
				this.dispose();
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�ױ��� �����߽��ϴ�.");

			} catch (SQLException e1) {
				
				
				if(e1.getErrorCode()==2627)
				{
					JOptionPane.showMessageDialog(this, "�ױ����� �����մϴ�.");
					
					
				}else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
					e1.printStackTrace();
				}
			}
		
		}
		else if(command.equals("�߰�"))
		{
			
			if(txfPort_name.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ױ����� �����ϴ�.");
				return;
			}
			if(txfPort_nationailty.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�������� �����ϴ�.");
				return;
			}
			if(txfArea_code.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "�ڵ��ȣ�� �����ϼ���");
				return;
			}
			
			try {
				HashMap<String, Object> param = new HashMap<String, Object>();
				
				
				param.put("area_code", txfArea_code.getText());
				
				param.put("port_name", txfPort_name.getText());
				
				param.put("port_nationality", txfPort_nationailty.getText());
				
				param.put("port_area", txfPort_area.getText());
				
				portService.insert(param);
				
				this.setVisible(false);
				
				this.dispose();
				
				this.result=KSGDialog.SUCCESS;
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�ױ��� �߰��߽��ϴ�.");
				
			} catch (SQLException e1) {
				
				if(e1.getErrorCode()==2627)
				{
					JOptionPane.showMessageDialog(this, "�ױ����� �����մϴ�.");
					
					
				}else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
					e1.printStackTrace();
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
		}
		else if(command.equals("���"))
		{
			this.result=KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}

	}
	
	@Override
	public void componentShown(ComponentEvent e) {

		if(param!=null)
		{	
			txfArea_code.setText((String) param.get("area_code"));
			txfPort_area.setText((String) param.get("port_area"));
			txfPort_name.setText((String) param.get("port_name"));
			txfPort_nationailty.setText((String) param.get("port_nationality"));
		}
		
		switch (type) {
		case UPDATE:

			lblTitleInfo.setText("�ױ����� ����");
			butOK.setActionCommand("����");

			break;
		case INSERT:

			lblTitleInfo.setText("�������� �߰�");
			butOK.setActionCommand("�߰�");

			break;

		}

	}	




}
