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
package com.ksg.workbench.shippertable.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.admin.KSGViewParameter;
import com.ksg.workbench.shippertable.ShipperTableAbstractMgtUI;
import com.ksg.workbench.shippertable.dialog.ManageVesselDialog;
import com.ksg.workbench.shippertable.dialog.ShowScheduleDialog;

/**
 * �������� ǥ��
 * @����Command SearchADVCommand
 * @author archehyun
 *
 */
public class KSGADVTablePanel extends KSGPanel implements ActionListener,KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AdvertiseTable				tblADVTable;

	private ShipperTableAbstractMgtUI base;

	private JTextField txfImportDate;

	public JTable _tblVesselTable;	

	private ManageVesselDialog vesseldialog;

	private SimpleDateFormat sorceDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat targetDateFormat = new SimpleDateFormat("yyyyMMdd");

	private ShippersTable selectedTable;

	public KSGADVTablePanel(ShipperTableAbstractMgtUI base) {

		this.base=base;

		this.createAndUpdateUI();
	}
	
	public String getTable_id()
	{
		return tblADVTable.getTableID();
	}

	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();

		if(command.equals("���� ����"))
		{
			vesseldialog = new ManageVesselDialog(tblADVTable.getTableID(),base,tblADVTable.getVesselModel());

			vesseldialog.createAndUpdateUI();
		}
		else if(command.equals("��ü ����"))
		{
			tblADVTable.delete(); 
		}
		else if(command.equals("������ ǥ��"))
		{
			ShowScheduleDialog dialog = new ShowScheduleDialog(this.selectedTable.getTable_id());
			dialog.createAndUpdateUI();
		}
	}

	public void createAndUpdateUI() {

		this.setName("KSGADVTablePanel");

		tblADVTable = new AdvertiseTable();

		this.setLayout(new BorderLayout());

		JScrollPane jScrollPane = new JScrollPane(tblADVTable);

		KSGPanel pnCenter = new KSGPanel();

		pnCenter.setLayout(new BorderLayout());

		pnCenter.add(jScrollPane);

		KSGPanel pnADVControl = new KSGPanel();

		pnADVControl.setLayout(new BorderLayout());

		JButton butPortList = new KSGGradientButton("�ױ� ����(P)");

		butPortList.setActionCommand("�ױ� ����");

		butPortList.setMnemonic(KeyEvent.VK_P);

		butPortList.addActionListener(base);

		JButton butVesselList = new KSGGradientButton("���� ����");

		butVesselList.setActionCommand("���� ����");

		butVesselList.addActionListener(this);

		JButton butDel = new KSGGradientButton("��ü ����(D)");

		butDel.setActionCommand("��ü ����");

		butDel.setMnemonic(KeyEvent.VK_D);

		butDel.addActionListener(this);

		JSlider slider = new JSlider(JSlider.HORIZONTAL, 50, 150, 75);

		slider.setBackground(Color.white);

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
		
		JButton butShowSchedule = new KSGGradientButton("������ ǥ��");
		
		butShowSchedule.addActionListener(this);

		KSGPanel pn1 = new KSGPanel();
		
		pn1.add(butPortList);
		
		pn1.add(butVesselList);
		
		pn1.add(butDel);
		
		pn1.add(slider);
		
		pn1.add(butShowSchedule);


		pnADVControl.add(pn1,BorderLayout.WEST);

		KSGPanel pn2 = new KSGPanel();

		JLabel lblDate = new JLabel(" �Է³�¥ : ");

		txfImportDate = new JTextField(8);

		txfImportDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));

		JCheckBox cbxImportDate = new JCheckBox("������",true);

		cbxImportDate.setBackground(Color.white);

		cbxImportDate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				JCheckBox bo =(JCheckBox) e.getSource();

				if(!bo.isSelected()) return;
				
				txfImportDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
			}});

		JButton butSave = new KSGGradientButton("����(S)");

		butSave.setMnemonic(KeyEvent.VK_S);
		
		butSave.setActionCommand(base.SAVE_ADV_DATA);
		
		butSave.addActionListener(base);

		JButton butCancel = new KSGGradientButton("���(C)");

		butCancel.setMnemonic(KeyEvent.VK_C);
		
		butCancel.setActionCommand(base.ADV_CANCEL);
		
		butCancel.addActionListener(base);

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
	
	public ADVData getADVData() throws ParseException
	{
		String inputDate 	= txfImportDate.getText();

		ADVData insertParam	= tblADVTable.getADVData();

		insertParam.setDate_isusse(KSGDateUtil.toDate2(inputDate));

		insertParam.setTable_date(targetDateFormat.format(sorceDateFormat.parse(inputDate)));
		
		return insertParam;
	}

	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
		JTable table = (JTable) e.getSource();

		if(!table.equals(tblADVTable)) return;

		int row = table.getSelectedRow();

		int col = table.getSelectedColumn();

		if(row==-1 ||col==-1) return;

		if(e.getKeyCode()==KeyEvent.VK_TAB)
		{
			int cl 		 = table.getSelectedColumn();
			int ro 		 = table.getSelectedRow();
			int colCount = table.getColumnCount();
			if(cl==colCount-1)
			{
				table.changeSelection(ro+1, 0, false, false);
			}
		}

		if(e.getKeyCode()==KeyEvent.VK_ENTER)
		{
			row = row-1;
			
			if(row==-1) row=0;
			
		}else
		{
			col=col-1; 
			
			if(col==-1) col=0;
		}

	}

	public void keyTyped(KeyEvent e) {}

	public void setSelectedTable(ShippersTable selectedTable) {
		
		this.selectedTable = selectedTable;

		tblADVTable.setShippersTableInfo(selectedTable);
	}

	public void retrive() throws Exception{
		
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
