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
package com.ksg.shippertable.view.comp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.shippertable.view.ShipperTableMgtUI;
import com.ksg.shippertable.view.dialog.ManageTablePortPop;
import com.ksg.shippertable.view.dialog.ManageVesselDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.KSGViewParameter;

/**
 * 광고정보 표시
 * @관련Command SearchADVCommand
 * @author archehyun
 *
 */
public class KSGADVTablePanel extends KSGPanel implements ActionListener,KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdvertiseTable				tblADVTable;

	private DAOManager daoManager;

	public boolean motherVessel=false;

	private ShippersTable selectedTable;

	private ShipperTableMgtUI base;

	private JTextField txfImportDate;

	public JTable _tblVesselTable;	

	private ManageVesselDialog vesseldialog;

	public KSGADVTablePanel(ShipperTableMgtUI base) {
		this.base=base;

		daoManager =DAOManager.getInstance();

		_advService= daoManager.createADVService();

		_baseSearvice = daoManager.createBaseService();

		tableService = daoManager.createTableService();

		this.createAndUpdateUI();
	}


	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if(command.equals("항구 관리"))
		{
			/*KSGDialog dialog = new ManagePortDialog(KSGModelManager.getInstance().selectedTable_id,base);
			dialog.createAndUpdateUI();
			int result = dialog.OPTION;
			if(result == ManagePortDialog.UPDATE_OPTION)
			{
				SearchADVCommand advCommand = new SearchADVCommand(this);
				advCommand.execute();
				this.updateUI();
			}*/


			ManageTablePortPop pop = new ManageTablePortPop(KSGModelManager.getInstance().selectedTable_id);

			pop.showPop();

			if(pop.RESLULT==ManageTablePortPop.OK)
			{
				base.searchADVTable();
			}

		}
		if(command.equals("선박 관리"))
		{
			vesseldialog = new ManageVesselDialog(selectedTable.getTable_id(),base,tblADVTable.getVesselModel());

			vesseldialog.createAndUpdateUI();
		}
		else if(command.equals("전체 삭제"))
		{
			tblADVTable.delete(); 

		}
	}




	public void createAndUpdateUI() {
		this.setName("KSGADVTablePanel");

		tblADVTable = new AdvertiseTable();

		this.setLayout(new BorderLayout());

		JScrollPane jScrollPane = new JScrollPane(tblADVTable);

		JPanel pnCenter = new JPanel();
		pnCenter.setLayout(new BorderLayout());
		pnCenter.add(jScrollPane);

		JPanel pnADVControl = new JPanel();
		pnADVControl.setLayout(new BorderLayout());

		JButton butPortList = new JButton("항구 관리(P)");
		butPortList.setActionCommand("항구 관리");
		butPortList.setMnemonic(KeyEvent.VK_P);
		butPortList.addActionListener(this);
		JButton butVesselList = new JButton("선박 관리");
		butVesselList.setActionCommand("선박 관리");
		butVesselList.addActionListener(this);

		JButton butDel = new JButton("전체 삭제(D)");
		butDel.setActionCommand("전체 삭제");
		butDel.setMnemonic(KeyEvent.VK_D);
		butDel.addActionListener(this);

		JSlider slider = new JSlider(JSlider.HORIZONTAL, 50, 150, 75);
		slider.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent item) {
				JSlider op = (JSlider) item.getSource();
				int value=op.getValue();
				KSGViewParameter.TABLE_COLUM_WIDTH= value;
				tblADVTable.updateTableView();

			}});
		slider.setMinorTickSpacing(15);
		slider.setMajorTickSpacing(30);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);

		slider.setLabelTable(slider.createStandardLabels(15));

		JPanel pn1 = new JPanel();
		pn1.add(butPortList);
		pn1.add(butVesselList);
		pn1.add(butDel);
		pn1.add(slider);


		pnADVControl.add(pn1,BorderLayout.WEST);

		JPanel pn2 = new JPanel();
		JLabel lblDate = new JLabel(" 입력날짜 : ");

		txfImportDate = new JTextField(8);
		txfImportDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
		JCheckBox cbxImportDate = new JCheckBox("월요일",true);
		cbxImportDate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					txfImportDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
				}
			}});

		JButton butSave = new JButton("저장(S)");


		butSave.setMnemonic(KeyEvent.VK_S);
		butSave.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				try
				{					
					String importDate = txfImportDate.getText();
					tblADVTable.save(importDate);
					base.showTableList();

				}catch(Exception ee)
				{
					ee.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, ee.getMessage());
				}

			}});

		JButton butCancel = new JButton("취소(C)");
		butCancel.setMnemonic(KeyEvent.VK_C);
		butCancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try 
				{
					base.showTableList();
				} catch (SQLException e1)
				{

					e1.printStackTrace();
					JOptionPane.showMessageDialog(KSGADVTablePanel.this, e1.getMessage());
				}
			}
		});
		pn2.add(lblDate);
		pn2.add(txfImportDate);
		pn2.add(cbxImportDate);
		pn2.add(butSave);
		pn2.add(butCancel);

		pnADVControl.add(pn2,BorderLayout.EAST);
		pnCenter.add(pnADVControl,BorderLayout.SOUTH);

		this.add(pnCenter,BorderLayout.CENTER);
		this.add(pnADVControl,BorderLayout.SOUTH);
	}

	public ShippersTable getSelectedTable() {
		return selectedTable;
	}


	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
		JTable table = (JTable) e.getSource();


		if(table.equals(tblADVTable))
		{
			int row=table.getSelectedRow();
			if(row==-1)
				return;

			int col = table.getSelectedColumn();
			if(col==-1)
				return;


			if(e.getKeyCode()==KeyEvent.VK_TAB)
			{
				int cl=table.getSelectedColumn();
				int ro = table.getSelectedRow();
				int colCount=table.getColumnCount();
				if(cl==colCount-1)
				{
					table.changeSelection(ro+1, 0, false, false);
				}
			}

			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				row=row-1;
				if(row==-1)
					row=0;
			}else
			{
				col=col-1;
				if(col==-1)
					col=0;
			}
		}
	}

	public void keyTyped(KeyEvent e) {}

	public void setSelectedTable(ShippersTable selectedTable) {

		this.selectedTable =selectedTable;

		tblADVTable.setShippersTableInfo(selectedTable);


	}

	public void update(KSGModelManager manager){}

	public void retrive() {
		tblADVTable.retrive();
	}

	public void setValue(String upperCase, int row, int col) {
		tblADVTable.setValue(upperCase, row, col);

	}


	public void autoVesselWrite( int selectedVesselrow) {
		tblADVTable.autoVesselWrite( selectedVesselrow);

	}


	public void autoVesselWrite( int selectedVesselrow, int col) {
		tblADVTable.autoVesselWrite( selectedVesselrow,col);

	}

}
