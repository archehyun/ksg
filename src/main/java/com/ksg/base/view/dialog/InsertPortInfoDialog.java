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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.ksg.base.view.BaseInfoUI;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.PortInfo;
import com.ksg.view.comp.dialog.KSGDialog;

/**
 * @since v1.0
 * @author 박창현
 * @설명 항구정보 입력 폼
 */
public class InsertPortInfoDialog extends BaseInfoDialog implements ActionListener
{
	
	private static final long serialVersionUID = 1L;


	private JTextField txfPort_name;

	private JTextField txfPort_nationailty;

	private JTextField txfPort_area;

	private JTextField txfArea_code;

	public InsertPortInfoDialog(BaseInfoUI baseInfoUI) {
		super(baseInfoUI);
		initComp();
	}
	public InsertPortInfoDialog(BaseInfoUI baseInfoUI,String port_name) {
		this(baseInfoUI);
		
		initComp();
		try {
			PortInfo port=baseService.getPortInfo(port_name);
			if(port!=null){
			txfArea_code.setText(port.getArea_code());
			txfPort_area.setText(port.getPort_area());
			txfPort_name.setText(port.getPort_name());
			txfPort_nationailty.setText(port.getPort_nationality());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public void createAndUpdateUI() {
		
		this.setModal(true);
		this.setTitle("항구 정보 추가");
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
	
		txfArea_code.setEditable(false);
		txfPort_area.setEditable(false);
		JPanel pnPort_name = new JPanel();
		pnPort_name.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany_name = new JLabel("Port Name");
		lblCompany_name.setPreferredSize(new Dimension(100,25));
		pnPort_name.add(lblCompany_name);
		pnPort_name.add(txfPort_name);


		JPanel pnPort_nationailty = new JPanel();
		pnPort_nationailty.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany_abbr = new JLabel("Port Nationality");
		lblCompany_abbr.setPreferredSize(new Dimension(100,25));
		pnPort_nationailty.add(lblCompany_abbr);	
		pnPort_nationailty.add(txfPort_nationailty);


		JPanel pnPort_area = new JPanel();
		pnPort_area.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAgentCode = new JLabel("Port Area");
		lblAgentCode.setPreferredSize(new Dimension(100,25));
		pnPort_area.add(lblAgentCode);	
		pnPort_area.add(txfPort_area);

		JPanel pnArea_code = new JPanel();
		pnArea_code.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAgent_abbr = new JLabel("Area Code");

		lblAgent_abbr.setPreferredSize(new Dimension(100,25));
		JButton butSearchCode = new JButton("코드 검색");
		butSearchCode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				final JDialog dialog = new JDialog(InsertPortInfoDialog.this);
				dialog.setModal(true);
				JPanel pnMain = new JPanel();

				JTable tblAreaList = new JTable();
				pnMain.setLayout(new BorderLayout());

				JPanel pnControl = new JPanel();
				pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
				final JButton butOk = new JButton("확인");
				butOk.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						dialog.setVisible(false);
						dialog.dispose();
						
					}});
				butOk.setEnabled(false);
				JButton butCancel = new JButton("취소");
				butCancel.addActionListener(new ActionListener(){


					public void actionPerformed(ActionEvent e) {
						txfPort_area.setText("");
						txfArea_code.setText("");
						dialog.setVisible(false);
						dialog.dispose();

					}});
				pnControl.add(butOk);
				pnControl.add(butCancel);
				try 
				{
					List li=baseService.getAreaInfoList();

					DefaultMutableTreeNode root = new DefaultMutableTreeNode("전체지역");
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
				dialog.setLocation(InsertPortInfoDialog.this.getX()+InsertPortInfoDialog.this.getWidth(), InsertPortInfoDialog.this.getY());
				dialog.setVisible(true);


			}});


		pnArea_code.add(lblAgent_abbr);	
		pnArea_code.add(txfArea_code);

		pnArea_code.add(butSearchCode);




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
		JLabel label = new JLabel("Port Info");
		label.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(label);

		pnCenter.add(pnPort_name);
		pnCenter.add(pnPort_nationailty);
		pnCenter.add(pnArea_code);
		pnCenter.add(pnPort_area);

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
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		this.setVisible(true);
	
	}
	private void initComp() {
		txfPort_name = new JTextField(20);
		txfPort_nationailty = new JTextField(20);
		txfPort_area = new JTextField(20);
		txfArea_code = new JTextField(5);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			
			baseService = new BaseServiceImpl();
			if(txfPort_name.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "항구명이 없습니다.");
				return;
			}
			if(txfPort_nationailty.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "국가명이 없습니다.");
				return;
			}
			if(txfArea_code.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(this, "코드번호를 선택하세요");
				return;
			}
			
			
			PortInfo info = new PortInfo();
			info.setArea_code(txfArea_code.getText());
			info.setPort_name(txfPort_name.getText());
			info.setPort_nationality(txfPort_nationailty.getText());
			info.setPort_area(txfPort_area.getText());

			try {

				baseService.insertPortInfo(info);
				this.setVisible(false);
				this.dispose();
				this.result=KSGDialog.SUCCESS;
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "항구를 추가했습니다.");
				
			} catch (SQLException e1) {
				
				if(e1.getErrorCode()==2627)
				{
					JOptionPane.showMessageDialog(this, "항구명이 존재합니다.");
					
					
				}else
				{
					JOptionPane.showMessageDialog(this, e1.getErrorCode()+","+e1.getMessage());
					e1.printStackTrace();
				}
			}
		
		}else if(command.equals("취소"))
		{
			this.result=KSGDialog.FAILE;
			this.setVisible(false);
			this.dispose();
		}

	}




}
