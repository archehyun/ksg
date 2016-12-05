package com.ksg.view.search.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.ksg.dao.DAOManager;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.search.SearchUI;
import com.ksg.view.util.ViewUtil;

public class ManageVesselDialog extends KSGDialog implements ActionListener {
	String table_id;
	SearchUI base;
	JTable _tblVesselList;
	DefaultTableModel model;
	public ManageVesselDialog(String selectedTableId, SearchUI base) {
		this.base =base;
		this.table_id=selectedTableId;
		DAOManager manager = DAOManager.getInstance();
		tableService = manager.createTableService();
		baseService = manager.createBaseService();
	}

	public ManageVesselDialog(String selectedTableId, SearchUI base2,
			DefaultTableModel vesselModel) {
		this(selectedTableId,base2);
		model = vesselModel;	
	}

	public void createAndUpdateUI() {
		setTitle(this.table_id+"테이블 선박 관리");
		setModal(true);
		
		
		getContentPane().add(buildCenter());
		getContentPane().add(createLine(),BorderLayout.WEST);
		getContentPane().add(createLine(),BorderLayout.EAST);
		getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		setSize(520,550);
		this.setMinimumSize(new Dimension(400,400));
		ViewUtil.center(this, false);
		setVisible(true);

	}

	private Component buildControl() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JPanel pnRight = new JPanel();
		pnRight.setLayout( new FlowLayout(FlowLayout.RIGHT));


		JPanel pnLeft = new JPanel();

	/*	JButton butArrange = new JButton("정렬");
		butArrange.setFont(defaultfont);
		butArrange.addActionListener(this);*/
		
		
		pnLeft.setLayout( new FlowLayout(FlowLayout.LEFT));
		JButton butDel = new JButton("수정");

		butDel.addActionListener(this);

		JButton butCancel = new JButton("닫기(S)");
		butCancel.setActionCommand("닫기");
		butCancel.setMnemonic(KeyEvent.VK_S);
		butCancel.addActionListener(this);
		
		pnRight.add(butDel);
		pnRight.add(butCancel);

		pnMain.add(pnLeft,BorderLayout.WEST);
		pnMain.add(pnRight,BorderLayout.EAST);
		return pnMain;
	}

	public Component createLine()
	{
		JPanel pn = new JPanel();
		pn.setPreferredSize(new Dimension(15,0));
		return pn;
	}

	private Component buildCenter() {
		JPanel pnMain = new JPanel();
		_tblVesselList = new JTable();
		_tblVesselList.setModel(model);
		_tblVesselList.updateUI();
		pnMain.add(new JScrollPane(_tblVesselList));
		return pnMain;
	}

	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		if(command.equals("닫기"))
		{
			this.setVisible(false);
			dispose();
		}
		else if(command.equals("수정"))
		{
			int row = _tblVesselList.getSelectedRow();
			if(row==-1)
				return;
			String vesselAbbr = (String) _tblVesselList.getValueAt(row, 1);
			try {
				/*Vessel oriVessel=_baseService.getVesselInfo(String.valueOf(value));
				if(oriVessel!=null)
				{
					String obj = oriVessel.getVessel_name().toUpperCase();

//					setValue(table,  obj,selectedVesselrow,col);

					return;
				}
				
				if(li.size()==1)
				{
					Vessel vessel = (Vessel) li.get(0);
					String obj = vessel.getVessel_name().toUpperCase();
					

//					setValue(table,obj, selectedVesselrow, col)	;
//					vesselModel.setRowCount(vesselModel.getRowCount()+1);
//					model.setValueAt(vessel.getVessel_abbr(), selectedVesselrow, 0);
					model.setValueAt(vessel.getVessel_name(), row, 1);

				}else if(li.size()>1)*/
				Vessel info = new Vessel();
				info.setVessel_abbr(vesselAbbr);
				List li=baseService.getVesselAbbrList(info);
				{
					SearchVesselDialog searchVesselDialog = new SearchVesselDialog(li);
					searchVesselDialog.createAndUpdateUI();


					if(searchVesselDialog.result!=null)
					{
//						setValue(table,searchVesselDialog.result.toUpperCase(), selectedVesselrow, col);
						
//						model.setRowCount(vesselModel.getRowCount()+1);
//						model.setValueAt(searchVesselDialog.resultAbbr, row, 0);
						model.setValueAt(searchVesselDialog.result, row, 0);
					}

				}/*else
				{
					int result=JOptionPane.showConfirmDialog(null, "해당 선박명이 없습니다. 추가 하시겠습니까?", "선박명 추가", JOptionPane.YES_NO_OPTION);
					if(result == JOptionPane.YES_OPTION)
					{
						AddVesselDialog addVesselDialog = new AddVesselDialog(this,table,selectedVesselrow,col,value);
						addVesselDialog.createAndUpdateUI();
					}else
					{
						
					}
				}*/
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void updateTable()
	{
		
	}

	/*public void setVesselModel(DefaultTableModel vesselmodel2) {
		model =vesselmodel2;
		System.out.println("set model");
		
	}*/

}
