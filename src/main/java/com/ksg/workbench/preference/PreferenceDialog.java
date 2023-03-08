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
package com.ksg.workbench.preference;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.ksg.common.util.KSGPropertis;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.panel.KSGPanel;

public class PreferenceDialog extends KSGDialog implements ActionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<PnOption> preferenceList;

	private KSGPropertis propertis = KSGPropertis.getIntance();

	String selectedKeyword="vessel";
	
	private CardLayout cardLayout;
	
	private KSGPanel pnCardMain;
	
	private PnPath pnPath;
	
	private PnXLS pnXLS;
	
	private PnKeyWord pnKeyWord;
	
	private PnPortExcpetion pnOtherPort;
	
	private PnCheckPort pnCheckPort;
	
	private JLabel lblTitle;
	
	String startTab;
	private PnApperance pnApperance;
	
	private BaseServiceImpl baseService;
	
	public PreferenceDialog(String title, boolean modal) {
		this.setTitle(title);
		this.setModal(modal);
		baseService =  new BaseServiceImpl();
	}
	public PreferenceDialog(String title, boolean modal,String startTab) {
		this(title,modal);
		this.startTab=startTab;
	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("확인"))
		{
			saveAction();			
		}		
		this.setVisible(false);
		this.dispose();

	}
	public KSGPanel addComp(JComponent comp,String label)
	{
		return this.addComp(comp, label,100);
	}
	public KSGPanel addComp(JComponent comp,String label,int width)
	{
		KSGPanel pnSavefolder =  new KSGPanel();
		
		pnSavefolder.setLayout(new BorderLayout());
		
		JLabel label2 = new JLabel(label,JLabel.RIGHT);
		
		label2.setPreferredSize(new Dimension(width,24));

		pnSavefolder.add(label2,BorderLayout.WEST);
		
		if(comp instanceof JList)
		{
			pnSavefolder.add(new JScrollPane(comp),BorderLayout.CENTER);
		}else
		{
			pnSavefolder.add(comp,BorderLayout.CENTER);	
		}

		return pnSavefolder;
	}
	private KSGPanel buildCenter() {
		
		KSGPanel pnCenter = new KSGPanel();
		
		KSGPanel pnNorth = new KSGPanel();
		
		pnNorth.setPreferredSize(new Dimension(0,15));
		
		pnNorth.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnCenter.setLayout(new BorderLayout());
		

		pnCenter.setBorder(BorderFactory.createEmptyBorder());

		KSGPanel pnSearch =  new KSGPanel();
		pnSearch.setBackground(Color.white);
		pnSearch.setLayout(new FlowLayout(FlowLayout.LEFT));
		JTextField field = new JTextField(10);
		pnSearch.add(field);

		
		KSGPanel pn1 =new KSGPanel();
		pn1.setLayout(new BorderLayout());
		KSGPanel pnTitle = new KSGPanel();
		pnTitle.setPreferredSize(new Dimension(0,45));
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
		lblTitle = new JLabel();
		Font defaultFont = new Font("돋음",Font.BOLD,14);
		lblTitle.setFont(defaultFont);
		
		pnTitle.add(lblTitle);
		pnTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
		pn1.add(createCardPn());		
		pn1.add(pnTitle,BorderLayout.NORTH);
		
		pnCenter.add(pn1,BorderLayout.CENTER);
		KSGPanel pnLeftMenu = new KSGPanel();
		pnLeftMenu.setLayout( new BorderLayout());

		pnLeftMenu.setBorder(BorderFactory.createEtchedBorder());
		pnLeftMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.lightGray));
		JTree jTree = createTree();
		pnLeftMenu.add(jTree);
		
		pnCenter.add(pnLeftMenu,BorderLayout.WEST);
		pnCenter.setBorder(BorderFactory.createEtchedBorder());
		
		pnCenter.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

		return pnCenter;
	}
	

	private Component createCardPn() {
		preferenceList = new Vector<PnOption>();
		pnCardMain = new KSGPanel();
		cardLayout = new CardLayout();
		pnCardMain.setLayout(cardLayout);
		
		pnPath = new PnPath(this);
		pnXLS = new PnXLS(this);
		pnKeyWord = new PnKeyWord(this);
		pnOtherPort = new PnPortExcpetion(this);
		pnCheckPort = new PnCheckPort(this);
		
		pnApperance = new PnApperance(this);
		
		
		
		addPnOption(pnXLS);
		
		addPnOption(pnKeyWord);
		
		addPnOption(pnPath);
		
		addPnOption(pnOtherPort);
		
		addPnOption(pnCheckPort);
		
		addPnOption(pnApperance);
		

		if(startTab==null)
		{
			lblTitle.setText(preferenceList.get(0).getName());	
		}else
		{
			changeTab(startTab);
		}
		
		
		return pnCardMain;
	}
	private void addPnOption(PnOption pn)
	{
		pnCardMain.add((Component) pn, pn.getName());
		preferenceList.add(pn);
	}
	
	private KSGPanel buildControl() {
		KSGPanel pnButtom = new KSGPanel();
		pnButtom.setPreferredSize(new Dimension(0,45));
		pnButtom.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton butOK = new JButton("확인");
		butOK.addActionListener(this);
		pnButtom.add(butOK);
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(this);
		pnButtom.add(butCancel);
		return pnButtom;
	}
	public void createAndUpdateUI()
	{
		
	}
	public void createAndUpdateUI(Component component) {
		
		this.getContentPane().add( buildCenter(),BorderLayout.CENTER);
		
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		this.pack();
		
		this.setLocationRelativeTo(component);
		
		this.setVisible(true);
		
	}
	private JTree createTree() {
		
		JTree jTree = new JTree();
		
		jTree.setPreferredSize( new Dimension(180,0));

		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		
		Iterator<PnOption> iterator = preferenceList.iterator();
		while(iterator.hasNext())
		{
			DefaultMutableTreeNode rtGeneral = new DefaultMutableTreeNode(iterator.next().getName());
			root.add(rtGeneral);
		}
	
		DefaultTreeModel model = new DefaultTreeModel(root);
		
		jTree.setModel(model);
		
		jTree.setRootVisible(false);
		
		jTree.setRowHeight(25);
		
		jTree.addTreeSelectionListener(new TreeSelectionListener(){

			public void valueChanged(TreeSelectionEvent e) {
				TreePath path=e.getNewLeadSelectionPath();
				cardLayout.show(pnCardMain, path.getLastPathComponent().toString());
				lblTitle.setText(path.getLastPathComponent().toString());
				
			}
		});

		return jTree;
	}
	public void saveAction()
	{
		Iterator<PnOption> iterator = preferenceList.iterator();
		while(iterator.hasNext())
		{
			iterator.next().saveAction();
		}
		propertis.store();
	}
	public void changeTab(String string) 
	{
		logger.debug("change tab:"+string);
		cardLayout.show(pnCardMain, string);
		lblTitle.setText(string);
	}
	

}
