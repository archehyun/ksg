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
package com.ksg.commands;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.PortInfo;
import com.ksg.service.BaseService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.dialog.InsertPortAbbrInfoDialog;
import com.ksg.workbench.master.dialog.InsertPortInfoDialog;

public class SearchPortCommand extends AbstractCommand implements ActionListener {
	
	private BaseService baseService;
	
	private KSGModelManager manager = KSGModelManager.getInstance();
	
	private JDialog dialog;
	
	private JTextField txfPortName;
	
	public PortInfo info;
	
	private JRadioButton rbtPort, rbtPortAbbr;	
	
	private JCheckBox cbkAddPort;
	
	private String selectedport;
	
	private JLabel lblSearchedPort;
	
	private JButton butAdd;
	
	ComboBoxActionLister actionLister = new ComboBoxActionLister();
	
	private JComboBox box;
	
	public SearchPortCommand() {
		baseService = new BaseServiceImpl();
	}

	public SearchPortCommand(String selectedPort) {
		this();
		this.selectedport=selectedPort;
	}

	public int execute() {
		dialog = new JDialog(manager.frame);
		dialog.setTitle(selectedport+" 항구 검색");
		box = new JComboBox();
		box.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					JComboBox bo =(JComboBox) e.getSource();
					
					if(bo.getSelectedItem()!=null)
					{
						txfPortName.setText(bo.getSelectedItem().toString());
					}else
					{
						txfPortName.setText("");
					}
				}
				else if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
				{
					txfPortName.setText("");
				}
			}
		});
	
		box.addActionListener(actionLister);
			 
		dialog.setModal(true);

		txfPortName = new JTextField(25);
		txfPortName.addKeyListener(new KeyAdapter(){

			public void keyTyped(KeyEvent e)
			{
				box.setSelectedItem(null);
			
			}
			 public void keyPressed(KeyEvent e) {
					logger.debug("typed : "+txfPortName.getText());
					try {
						actionLister.stop();
						List li=	baseService.getPortListByPatten(txfPortName.getText());
						if(li.size()==0)
						{
							box.setPopupVisible(false);
						}else
						{
							box.removeAllItems();
							Iterator iter = li.iterator();
							while(iter.hasNext())
							{
								box.addItem(iter.next());
							}
							box.setSelectedItem(null);
							box.setPopupVisible(false);
							box.setPopupVisible(true);
						}
						

					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					actionLister.start();
			 }

		});

	
		box.setPreferredSize(new Dimension(40,0));
		Box panel = new Box(BoxLayout.Y_AXIS);
		JPanel pn0 =  new JPanel();
		pn0.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lbl0 = new JLabel("검색된 항구명 : ");
		pn0.add(lbl0);
		lblSearchedPort = new JLabel();
		lblSearchedPort.setText(selectedport);
		pn0.add(lblSearchedPort);


		JPanel pn1 =  new JPanel();
		pn1.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lbl1 = new JLabel("항구 검색");

		pn1.add(lbl1);
		pn1.add(txfPortName);
		panel.add(pn0);
		panel.add(pn1);
		KSGPanel pnBox = new KSGPanel(new BorderLayout());		
		pnBox.add(box,BorderLayout.CENTER);
		panel.add(pnBox);

		KSGPanel pnControl = new KSGPanel();
		
		pnControl.setLayout(new BorderLayout());

		KSGPanel pnControlright = new KSGPanel();
		JButton butOK = new JButton("확인");
		butOK.addActionListener(this);
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(this);
		pnControlright.add(butOK);
		pnControlright.add(butCancel);
		pnControl.add(pnControlright,BorderLayout.EAST);
		KSGPanel pnLeft =  new KSGPanel();
		cbkAddPort = new JCheckBox("항구등록");
		cbkAddPort.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.SELECTED:
					rbtPort.setEnabled(true);
					rbtPortAbbr.setEnabled(true);
					butAdd.setEnabled(true);
					
					break;
				case ItemEvent.DESELECTED:
					rbtPort.setEnabled(false);
					rbtPortAbbr.setEnabled(false);
					butAdd.setEnabled(false);
					break;

				default:
					break;
				}

			}
		});
		ButtonGroup bg = new ButtonGroup();
		rbtPort = new JRadioButton("항구");
		rbtPort.setEnabled(false);
		rbtPort.setBackground(Color.white);
		rbtPortAbbr = new JRadioButton("약어",true);
		rbtPortAbbr.setBackground(Color.white);
		rbtPortAbbr.setEnabled(false);

		butAdd = new JButton("추가");
		butAdd.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				if(cbkAddPort.isSelected())
				{
					if(rbtPort.isSelected())
					{
						KSGDialog dialog = new InsertPortInfoDialog(null, txfPortName.getText());
						
						dialog.createAndUpdateUI();
					}
					else if(rbtPortAbbr.isSelected())
					{
						String port_name = txfPortName.getText();
						String port_abbr = lblSearchedPort.getText();
						KSGDialog dialog1 = new InsertPortAbbrInfoDialog(port_abbr,port_name );
						dialog1.createAndUpdateUI();
					}
				}
			}
		});

		bg.add(rbtPort);
		bg.add(rbtPortAbbr);
		pnLeft.add(cbkAddPort);

		pnLeft.add(rbtPort);
		pnLeft.add(rbtPortAbbr);
		pnLeft.add(butAdd);

		pnControl.add(pnLeft,BorderLayout.WEST);
		dialog.getContentPane().add(panel,BorderLayout.NORTH);
		dialog.getContentPane().add(pnControl,BorderLayout.SOUTH);
		txfPortName.setComponentPopupMenu(createTypePopup());

		dialog.setSize(500,300);
		ViewUtil.center(dialog, false);
		dialog.setLocation(dialog.getX()+150, dialog.getY());
		dialog.setResizable(false);
		dialog.setVisible(true);
		return RESULT_SUCCESS;

	}
	private JPopupMenu createTypePopup() {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem item = new JMenuItem("test");
		menu.add(item);
		return menu;
	}

	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		if(command.equals("확인"))
		{
			try {
				info=baseService.getPortInfo(txfPortName.getText());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dialog.setVisible(false);
			dialog.dispose();
		}
		else
		{
			dialog.setVisible(false);
			dialog.dispose();
		}

	}
	class ComboBoxActionLister implements ActionListener
	{
		boolean flag;
		public void stop(){
			flag = false;
		}
		public void start(){
			flag = true;
		}
	
		public void actionPerformed(ActionEvent e) {
			JComboBox bo =(JComboBox) e.getSource();
			
			if(bo.getSelectedItem()!=null&&flag)
			{
				txfPortName.setText(bo.getSelectedItem().toString());
			}
			
		}
	}
	
	
	
	/**
	 * 
	
	  * @FileName : SearchPortCommand.java
	
	  * @Project : KSG2
	
	  * @Date : 2022. 12. 5. 
	
	  * @작성자 : pch
	
	  * @변경이력 :
	
	  * @프로그램 설명 : 추가 예정
	 */
	class PortSearchDialog extends KSGDialog implements ActionListener
	{
		private String portName;
		
		private JComboBox cbxSelectOption;
		
		private JTextField txfPortName;
		
		public PortSearchDialog(String portName )
		{
			super();
			this.portName = portName;
		}
		
		public void buildCenter()
		{	
			cbxSelectOption.addKeyListener(new KeyAdapter(){
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()==KeyEvent.VK_ENTER)
					{
						JComboBox bo =(JComboBox) e.getSource();
						
						if(bo.getSelectedItem()!=null)
						{
							txfPortName.setText(bo.getSelectedItem().toString());
						}else
						{
							txfPortName.setText("");
						}
					}
					else if(e.getKeyCode()==KeyEvent.VK_ESCAPE)
					{
						txfPortName.setText("");
					}
				}
			});
			
			txfPortName = new JTextField(25);
			
			txfPortName.addKeyListener(new KeyAdapter(){

				public void keyTyped(KeyEvent e)
				{
					box.setSelectedItem(null);
				
				}
				 public void keyPressed(KeyEvent e) {
						logger.debug("typed : "+txfPortName.getText());
						try {
							actionLister.stop();
							List li=	baseService.getPortListByPatten(txfPortName.getText());
							if(li.size()==0)
							{
								box.setPopupVisible(false);
							}else
							{
								box.removeAllItems();
								Iterator iter = li.iterator();
								while(iter.hasNext())
								{
									box.addItem(iter.next());
								}
								box.setSelectedItem(null);
								box.setPopupVisible(false);
								box.setPopupVisible(true);
							}
							

						} catch (SQLException e1) {
							e1.printStackTrace();
						}
						actionLister.start();
				 }

			});
		}
		
		private KSGPanel buildControl()
		{
			
			KSGPanel pnControl = new KSGPanel();
			
			pnControl.setLayout(new BorderLayout());
			KSGPanel pnControlright = new KSGPanel();			
			
			JButton butOK = new JButton("확인");
			JButton butCancel = new JButton("취소");
			
			butOK.addActionListener(this);
			butCancel.addActionListener(this);
			
			pnControlright.add(butOK);
			pnControlright.add(butCancel);
			
			pnControl.add(pnControlright,BorderLayout.EAST);
			KSGPanel pnLeft =  new KSGPanel();
			cbkAddPort = new JCheckBox("항구등록");
			cbkAddPort.setBackground(Color.white);
			cbkAddPort.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					switch (e.getStateChange()) {
					case ItemEvent.SELECTED:
						rbtPort.setEnabled(true);
						rbtPortAbbr.setEnabled(true);
						butAdd.setEnabled(true);
						
						break;
					case ItemEvent.DESELECTED:
						rbtPort.setEnabled(false);
						rbtPortAbbr.setEnabled(false);
						butAdd.setEnabled(false);
						break;

					default:
						break;
					}

				}
			});
			ButtonGroup bg = new ButtonGroup();
			rbtPort = new JRadioButton("항구");
			rbtPort.setEnabled(false);
			rbtPortAbbr = new JRadioButton("약어",true);
			rbtPortAbbr.setEnabled(false);

			butAdd = new JButton("추가");
			butAdd.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {

					if(cbkAddPort.isSelected())
					{
						if(rbtPort.isSelected())
						{
							KSGDialog dialog = new InsertPortInfoDialog(null, txfPortName.getText());
							
							dialog.createAndUpdateUI();
						}
						else if(rbtPortAbbr.isSelected())
						{
							KSGDialog dialog1 = new InsertPortAbbrInfoDialog(lblSearchedPort.getText(),txfPortName.getText());
							dialog1.createAndUpdateUI();
						}
					}
				}
			});

			bg.add(rbtPort);
			bg.add(rbtPortAbbr);
			pnLeft.add(cbkAddPort);

			pnLeft.add(rbtPort);
			pnLeft.add(rbtPortAbbr);
			pnLeft.add(butAdd);

			return null;
		}

		@Override
		public void createAndUpdateUI() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void componentShown(ComponentEvent e) {
			
		}
		
	}

}
