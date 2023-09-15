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
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;

public class PreferenceDialog extends KSGDialog implements ActionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Vector<PnOption> preferenceList;

	private KSGPropertis propertis = KSGPropertis.getIntance();

	private CardLayout cardLayout;
	
	private KSGPanel pnCardMain;
	
	private JLabel lblTitle;
	
	private String startTab;
	
	public PreferenceDialog(String title, boolean modal) {
		
		this.setTitle(title);
		
		this.setModal(modal);
		
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
	
	private void initComp()
	{
		preferenceList = new Vector<PnOption>();	
		
		lblTitle = new JLabel();
		
		lblTitle.setFont(new Font("돋음",Font.BOLD,14));
		
		pnCardMain = new KSGPanel();
		
		cardLayout = new CardLayout();
		
		pnCardMain.setLayout(cardLayout);
		
		addPnOption(new PnXLS(this));
		
		addPnOption(new PnKeyWord(this));
		
		addPnOption(new PnPath(this));
		
		addPnOption(new PnPortExcpetion2(this));
		
		addPnOption(new PnCheckPort(this));
		
		addPnOption(new PnApperance(this));
		
		if(startTab==null)
		{
			lblTitle.setText(preferenceList.get(0).getName());	
		}else
		{
			changeTab(startTab);
		}
		
	}
	private KSGPanel buildCenter() {
		
		KSGPanel pnCenter = new KSGPanel(new BorderLayout());
		
		pnCenter.add(buildContentsPn(),BorderLayout.CENTER);
		
		pnCenter.add(buildLeftMenu(),BorderLayout.WEST);
		
		pnCenter.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));

		return pnCenter;
	}
	
	private KSGPanel buildLeftMenu()
	{
		KSGPanel pnLeftMenu = new KSGPanel( new BorderLayout());
		
		pnLeftMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.lightGray));
		
		JTree jTree = createTree();
		
		pnLeftMenu.add(jTree);
		
		return pnLeftMenu;
	}
	
	private KSGPanel buildContentsPn()
	{
		KSGPanel pn1 =new KSGPanel(new BorderLayout());
		
		pn1.add(pnCardMain);
		
		pn1.add(buildTitle(),BorderLayout.NORTH);		
		
		return pn1;
	}
	private KSGPanel buildTitle() {
		
		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnTitle.setPreferredSize(new Dimension(0,45));
		
		pnTitle.add(lblTitle);
		
		pnTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
		
		return pnTitle;
	}

	private void addPnOption(PnOption pn)
	{
		pnCardMain.add((Component) pn, pn.getName());
		
		preferenceList.add(pn);
	}
	
	private KSGPanel buildControl() {
		
		KSGPanel pnButtom = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		pnButtom.setPreferredSize(new Dimension(0,45));
		
		JButton butOK 		= new KSGGradientButton("확인");
		
		JButton butCancel 	= new KSGGradientButton("취소");
		
		butOK.addActionListener(this);
		
		butCancel.addActionListener(this);
		
		pnButtom.add(butOK);
		
		pnButtom.add(butCancel);
		
		pnButtom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray));
		
		return pnButtom;
	}
	public void createAndUpdateUI()
	{
		this.initComp();
		
		this.getContentPane().add( buildCenter(),BorderLayout.CENTER);
		
		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		ViewUtil.center(this, true);
		
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
