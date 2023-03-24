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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.dtp.api.control.PortController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.treetable.node.AreaTreeNode;
import com.ksg.workbench.common.comp.treetable.node.NodeType;
import com.ksg.workbench.common.comp.treetable.node.ScheduleTreeNode;

/**
 * 항구 정보 수정 다이어그램
 * 
 * @author 박창현
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

	SearchAreaDialog searchAreaDailog;

	CommandMap areaMap;

	public UpdatePortInfoDialog(int type)
	{
		super();
		
		this.setController(new PortController());
		
		this.type = type;
		
		areaMap = new CommandMap();
	}

	public UpdatePortInfoDialog(int type, HashMap<String, Object> param) {
		
		this(type);
		
		this.param = param;
	}
	public void createAndUpdateUI() {

		this.addComponentListener(this);

		this.setModal(true);

		this.setTitle("항구 정보 관리");

		this.getContentPane().add(buildTitle("항구 정보 수정"),BorderLayout.NORTH);

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
		
		txfArea_code.setEditable(false);
		
		txfPort_area.setEditable(false);

		JButton butSearchCode = new JButton("코드 검색");
		
		butSearchCode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				searchAreaDailog = new SearchAreaDialog(UpdatePortInfoDialog.this);
				
				searchAreaDailog.createAndUpdateUI();

			}});

	
		KSGPanel pnCenter = new KSGPanel(new GridLayout(0,1,0,-3));
		
		pnCenter.add(createFormItem(txfPort_name, "항구명"));
		
		pnCenter.add(createFormItem(txfPort_nationailty, "나라"));
		
		pnCenter.add(createFormItem(txfArea_code, butSearchCode, "지역코드"));
		
		pnCenter.add(createFormItem(txfPort_area, "지역명"));	

		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		pnMain.add(pnCenter);
		
		return pnMain;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("수정"))
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


			CommandMap param = new CommandMap();


			param.put("area_code", txfArea_code.getText());

			param.put("port_name", txfPort_name.getText());

			param.put("port_nationality", txfPort_nationailty.getText());

			param.put("port_area", txfPort_area.getText());

			param.put("base_port_name", this.param.get("port_name"));
			
			callApi("updatePort", param);


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

			CommandMap param = new CommandMap();

			param.put("area_code", txfArea_code.getText());

			param.put("port_name", txfPort_name.getText());

			param.put("port_nationality", txfPort_nationailty.getText());

			param.put("port_area", txfPort_area.getText());
			
			callApi("insertPort", param);

		}
		else if(command.equals("취소"))
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

			lblTitle.setText("항구정보 수정");
			butOK.setActionCommand("수정");

			break;
		case INSERT:

			lblTitle.setText("항구정보 추가");
			butOK.setActionCommand("추가");

			break;

		}
		
		callApi("portDialog.init");

	}

	class SearchAreaDialog extends KSGDialog
	{
		private JButton butOk;
		
		private JButton butCancel;
		
		public SearchAreaDialog(JDialog dialog)		
		{
			super(dialog);
			this.setTitle("지역 선택");
		}

		private KSGPanel buildCenter()
		{
			KSGPanel pnMain = new KSGPanel();

			DefaultMutableTreeNode root = new DefaultMutableTreeNode("전체지역");
			
			for(String areaName: areaMap.keySet())
			{	
				root.add(new AreaTreeNode(areaName));
			}
			
			final JTree tree = new JTree(root);
			
			tree.setRowHeight(25);
			
			tree.setCellRenderer(new ScheduleCellRenderer());
			
			tree.setRootVisible(false);
			
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

							CommandMap areaInfo = (CommandMap) areaMap.get(area1);
							
							txfArea_code.setText(String.valueOf(areaInfo.get("area_code")));
							
							txfPort_area.setText(String.valueOf(areaInfo.get("area_name")));

							butOk.setEnabled(true);
							
							break;

						case 3:
							
							String area2=path.getLastPathComponent().toString();

							CommandMap areaInfo2 = (CommandMap) areaMap.get(area2);
							
							txfArea_code.setText(String.valueOf(areaInfo2.get("area_code")));
							
							txfPort_area.setText(String.valueOf(areaInfo2.get("area_name")));
							
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
			
			return pnMain;
		}

		private KSGPanel buildControl()
		{
			KSGPanel pnControl = new KSGPanel();
			
			pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			butOk = new JButton("확인");
			
			butOk.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					close();

				}});
			butOk.setEnabled(false);
			butCancel = new JButton("취소");
			butCancel.addActionListener(new ActionListener(){


				public void actionPerformed(ActionEvent e) {
					
					txfPort_area.setText((String) param.get("port_area"));
					
					txfArea_code.setText((String) param.get("area_code"));
					
					close();

				}});
			
			pnControl.add(butOk);
			
			pnControl.add(butCancel);


			return pnControl;
		}

		@Override
		public void createAndUpdateUI() {
			
			getContentPane().add(buildControl(),BorderLayout.SOUTH);
			
			getContentPane().add(buildCenter(),BorderLayout.CENTER);
			
			setSize(400,350);
			
			setLocation(UpdatePortInfoDialog.this.getX()+UpdatePortInfoDialog.this.getWidth(), UpdatePortInfoDialog.this.getY());
			
			setVisible(true);
		}

	}
	
	@Override
	public void updateView() {
		
		CommandMap resultMap= this.getModel();

		boolean success = (boolean) resultMap.get("success");

		if(success)
		{
			String serviceId=(String) resultMap.get("serviceId");

			if("insertPort".equals(serviceId))
			{	
				result = SUCCESS;

				JOptionPane.showMessageDialog(UpdatePortInfoDialog.this,"추가했습니다.");

				this.setVisible(false);

				this.dispose();

			}

			else if("updatePort".equals(serviceId))
			{	
				result = SUCCESS;

				JOptionPane.showMessageDialog(UpdatePortInfoDialog.this,"수정했습니다.");

				this.setVisible(false);

				this.dispose();

			}
			else if("portDialog.init".equals(serviceId))
			{	
				List<CommandMap> li = (List<CommandMap>) resultMap.get("areaMap");
				
				for(CommandMap item:li)
				{
					areaMap.put(String.valueOf(item.get("area_name")), item);
				}
			}
		}
		else{  
			String error = (String) resultMap.get("error");

			JOptionPane.showMessageDialog(this, error);
		}

	}
	
	
	
	class ScheduleCellRenderer extends JLabel implements TreeCellRenderer
	{
		Image changeAreaImg;
		
		public ScheduleCellRenderer() {
			Image img4 = new ImageIcon("images/internet.png").getImage();
			changeAreaImg = img4.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

			TreeNode t = node.getParent();
			
			if(t== null) return this;
			
			if(node instanceof ScheduleTreeNode)
			{
				ScheduleTreeNode treeNode = (ScheduleTreeNode) node;
				
				
				if(treeNode.getType() == NodeType.AREA)
				{
					setIcon(new ImageIcon(changeAreaImg));	
				}
				
			}
			
			if(selected)
			{
				this.setForeground(Color.red);
				this.setBackground(Color.blue);
					
			}
			else
			{
				this.setForeground(Color.black);
			}
			

			setText(String.valueOf(value));
			
			return this;
		}

	}




}
