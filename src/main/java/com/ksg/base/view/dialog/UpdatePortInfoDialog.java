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
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
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

import com.ksg.base.service.PortService;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.AreaInfo;
import com.ksg.view.comp.KSGDialog;

/**
 * 항구 정보 수정 다이어그램
 * 
 * @author 박창현
 *
 */
public class UpdatePortInfoDialog extends KSGDialog implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<String, Object> param;

	private JTextField txfPort_name;

	private JTextField txfPort_nationailty;

	private JTextField txfPort_area;

	private JTextField txfArea_code;
	
	protected BaseDAOManager baseServices;
	private JButton butOK;
	private JButton butCancel;
	private JLabel lblTitleInfo;
	
	PortService portService;

	public UpdatePortInfoDialog(int type)
	{
		super();
		portService = new PortService();
		this.type = type;
	}
	
	public UpdatePortInfoDialog(int type, HashMap<String, Object> param) {
		this(type);
		this.param = param;
		
	}
	public void createAndUpdateUI() {
		
		this.addComponentListener(this);
		txfPort_name = new JTextField(20);
		txfPort_nationailty = new JTextField(20);
		txfPort_area = new JTextField(20);
		txfArea_code = new JTextField(5);
		
		
		this.setModal(true);
		this.setTitle("항구 정보 관리");
		Box pnCenter = new Box(BoxLayout.Y_AXIS);
	
		txfArea_code.setEditable(false);
		txfPort_area.setEditable(false);
		JPanel pnPort_name = new JPanel();
		pnPort_name.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany_name = new JLabel("항구명");
		lblCompany_name.setPreferredSize(new Dimension(100,25));
		pnPort_name.add(lblCompany_name);
		pnPort_name.add(txfPort_name);


		JPanel pnPort_nationailty = new JPanel();
		pnPort_nationailty.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany_abbr = new JLabel("나라");
		lblCompany_abbr.setPreferredSize(new Dimension(100,25));
		pnPort_nationailty.add(lblCompany_abbr);	
		pnPort_nationailty.add(txfPort_nationailty);


		JPanel pnPort_area = new JPanel();
		pnPort_area.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAgentCode = new JLabel("지역");
		lblAgentCode.setPreferredSize(new Dimension(100,25));
		pnPort_area.add(lblAgentCode);	
		pnPort_area.add(txfPort_area);

		JPanel pnArea_code = new JPanel();
		pnArea_code.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAgent_abbr = new JLabel("지역 코드");

		lblAgent_abbr.setPreferredSize(new Dimension(100,25));
		JButton butSearchCode = new JButton("코드 검색");
		butSearchCode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				final JDialog dialog = new JDialog(UpdatePortInfoDialog.this);
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
				dialog.setLocation(UpdatePortInfoDialog.this.getX()+UpdatePortInfoDialog.this.getWidth(), UpdatePortInfoDialog.this.getY());
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
		butOK = new JButton("저장");
		butCancel = new JButton("취소");
		butOK.addActionListener(this);
		butCancel.addActionListener(this);
		pnControl.add(butOK);
		pnControl.add(butCancel);

		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnTitle.setBackground(Color.white);
		lblTitleInfo = new JLabel("항구 정보 수정");
		lblTitleInfo.setFont(new Font("area",Font.BOLD,16));
		pnTitle.add(lblTitleInfo);
		
		
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
	
		
	

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("수정"))
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
			
			
			HashMap<String, Object> param = new HashMap<String, Object>();
			
			
			param.put("area_code", txfArea_code.getText());
			
			param.put("port_name", txfPort_name.getText());
			
			param.put("port_nationality", txfPort_nationailty.getText());
			
			param.put("port_area", txfPort_area.getText());
			
			param.put("base_port_name", this.param.get("port_name"));

			try {
				portService.updatePort(param);
				this.setVisible(false);
				this.dispose();
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "항구를 수정했습니다.");

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
		
		}
		else if(command.equals("추가"))
		{
			
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
			
			try {
				HashMap<String, Object> param = new HashMap<String, Object>();
				
				
				param.put("area_code", txfArea_code.getText());
				
				param.put("port_name", txfPort_name.getText());
				
				param.put("port_nationality", txfPort_nationailty.getText());
				
				param.put("port_area", txfPort_area.getText());
				
				portService.insertPort(param);
				
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
			
			
			
		}
		else if(command.equals("취소"))
		{
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

			lblTitleInfo.setText("항구정보 수정");
			butOK.setActionCommand("수정");

			break;
		case INSERT:

			lblTitleInfo.setText("지역정보 추가");
			butOK.setActionCommand("추가");

			break;

		}

	}	




}
