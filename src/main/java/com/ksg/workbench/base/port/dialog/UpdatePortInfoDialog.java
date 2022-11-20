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

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.service.PortService;
import com.ksg.service.impl.AreaService;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.service.impl.PortServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.base.dialog.BaseInfoDialog;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

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

	private PortService portService;

	private AreaService areaService;

	CommandMap areaMap;

	public UpdatePortInfoDialog(int type)
	{
		super();
		portService = new PortServiceImpl();
		areaService = new AreaServiceImpl();
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



		Box pnCenter = new Box(BoxLayout.Y_AXIS);

		txfArea_code.setEditable(false);
		txfPort_area.setEditable(false);




		KSGPanel pnArea_code = new KSGPanel();
		pnArea_code.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblAgent_abbr = new JLabel("지역 코드");

		lblAgent_abbr.setPreferredSize(new Dimension(100,25));
		JButton butSearchCode = new JButton("코드 검색");
		butSearchCode.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

//				final JDialog dialog = new JDialog(UpdatePortInfoDialog.this);
//				dialog.setModal(true);
//				KSGPanel pnMain = new KSGPanel();
//
//				JTable tblAreaList = new JTable();
//				pnMain.setLayout(new BorderLayout());
//
//				KSGPanel pnControl = new KSGPanel();
//				pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
//				final JButton butOk = new JButton("확인");
//				butOk.addActionListener(new ActionListener(){
//
//					public void actionPerformed(ActionEvent e) {
//						dialog.setVisible(false);
//						dialog.dispose();
//
//					}});
//				butOk.setEnabled(false);
//				JButton butCancel = new JButton("취소");
//				butCancel.addActionListener(new ActionListener(){
//
//
//					public void actionPerformed(ActionEvent e) {
//						txfPort_area.setText((String) param.get("port_area"));
//						txfArea_code.setText((String) param.get("area_code"));
//						dialog.setVisible(false);
//						dialog.dispose();
//
//					}});
//				pnControl.add(butOk);
//				pnControl.add(butCancel);
//				try 
//				{
//					DefaultMutableTreeNode root = new DefaultMutableTreeNode("전체지역");
//					Iterator<String> iter =areaMap.keySet().iterator();
//					while(iter.hasNext())
//					{
//						String area_name = (String) iter.next();
//						DefaultMutableTreeNode areaGroup = new DefaultMutableTreeNode(area_name);
//						root.add(areaGroup);
//
//					}
//					final JTree tree = new JTree(root);
//					tree.addMouseListener(new MouseAdapter() {
//
//						public void mouseClicked(MouseEvent arg0) {
//							if(arg0.getClickCount()>=0)
//							{
//								TreePath path=tree.getSelectionPath();
//								if(path==null)
//									return;
//								switch (path.getPathCount()) {
//
//								case 2:
//									String area1=path.getLastPathComponent().toString();
//
//
//									CommandMap areaInfo = (CommandMap) areaMap.get(area1);
//									txfArea_code.setText(String.valueOf(areaInfo.get("area_code")));
//									txfPort_area.setText(String.valueOf(areaInfo.get("area_name")));
//
//									butOk.setEnabled(true);
//									break;
//
//								case 3:
//									String area2=path.getLastPathComponent().toString();
//
//									CommandMap areaInfo2 = (CommandMap) areaMap.get(area2);
//									txfArea_code.setText(String.valueOf(areaInfo2.get("area_code")));
//									txfPort_area.setText(String.valueOf(areaInfo2.get("area_name")));
//
//
//
//									butOk.setEnabled(true);
//									break;
//
//								default:
//									butOk.setEnabled(false);
//									txfArea_code.setText("");
//									txfPort_area.setText("");
//
//									break;
//								}
//
//							}
//
//						}
//					});
//					pnMain.add(new JScrollPane(tree),BorderLayout.CENTER);
//
//				} catch (Exception e1) {
//					e1.printStackTrace();
//					JOptionPane.showMessageDialog(UpdatePortInfoDialog.this, e1.getMessage());
//
//				}
//
//				dialog.getContentPane().add(pnControl,BorderLayout.SOUTH);
//				dialog.getContentPane().add(pnMain,BorderLayout.CENTER);
//				dialog.setSize(400,400);
//				dialog.setLocation(UpdatePortInfoDialog.this.getX()+UpdatePortInfoDialog.this.getWidth(), UpdatePortInfoDialog.this.getY());
//				dialog.setVisible(true);
				
				searchAreaDailog = new SearchAreaDialog(UpdatePortInfoDialog.this);
				searchAreaDailog.createAndUpdateUI();

			}});

		pnArea_code.add(txfArea_code);
		pnArea_code.add(butSearchCode);		

		pnCenter.add(createFormItem(txfPort_name, "항구명"));
		pnCenter.add(createFormItem(txfPort_nationailty, "나라"));
		pnCenter.add(createFormItem(txfPort_area, "지역"));
		pnCenter.add(createFormItem(pnArea_code, "지역코드"));	


		KSGPanel pnMain = new KSGPanel(new BorderLayout());
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

			try {
				portService.update(param);
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
				CommandMap param = new CommandMap();


				param.put("area_code", txfArea_code.getText());

				param.put("port_name", txfPort_name.getText());

				param.put("port_nationality", txfPort_nationailty.getText());

				param.put("port_area", txfPort_area.getText());

				portService.insert(param);

				this.setVisible(false);

				this.dispose();

				this.result=KSGDialog.SUCCESS;
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "항구를 추가했습니다.");

			} 
			catch (AlreadyExistException e1)
			{
				JOptionPane.showMessageDialog(this, "항구명이 존재합니다.");
			}
			catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, e1.getMessage());
			}

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

		try {
			List<CommandMap> li = areaService.selectAreaInfoList();

			for(CommandMap item:li)
			{
				areaMap.put(String.valueOf(item.get("area_name")), item);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

	}

	class SearchAreaDialog extends KSGDialog
	{

		private JButton butOk;
		private JButton butCancel;
		
		public SearchAreaDialog(JDialog dialog)		
		{
			super(dialog);
		}

		private KSGPanel buildCenter()
		{

			KSGPanel pnMain = new KSGPanel();

			JTable tblAreaList = new JTable();

			DefaultMutableTreeNode root = new DefaultMutableTreeNode("전체지역");
			Iterator<String> iter =areaMap.keySet().iterator();
			while(iter.hasNext())
			{
				String area_name = (String) iter.next();
				DefaultMutableTreeNode areaGroup = new DefaultMutableTreeNode(area_name);
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
					setVisible(false);
					dispose();

				}});
			butOk.setEnabled(false);
			butCancel = new JButton("취소");
			butCancel.addActionListener(new ActionListener(){


				public void actionPerformed(ActionEvent e) {
					txfPort_area.setText((String) param.get("port_area"));
					txfArea_code.setText((String) param.get("area_code"));
					setVisible(false);
					dispose();

				}});
			pnControl.add(butOk);
			pnControl.add(butCancel);


			return pnControl;
		}

		@Override
		public void createAndUpdateUI() {
			getContentPane().add(buildControl(),BorderLayout.SOUTH);
			getContentPane().add(buildCenter(),BorderLayout.CENTER);
			setSize(400,400);
			setLocation(UpdatePortInfoDialog.this.getX()+UpdatePortInfoDialog.this.getWidth(), UpdatePortInfoDialog.this.getY());
			setVisible(true);
		}

	}




}
