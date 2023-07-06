package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.dialog.SearchVesselDialog;
import com.ksg.workbench.master.dialog.BaseInfoDialog;
import com.ksg.workbench.shippertable.ShipperTableAbstractMgtUI;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 이름 : 선박 관리 다이얼 로그
 * @author 박창현
 *
 */
@Slf4j
public class ManageVesselDialog extends BaseInfoDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String table_id;

	private ShipperTableAbstractMgtUI base;

	private JTable _tblVesselList;

	private DefaultTableModel model;

	public ManageVesselDialog(String selectedTableId, ShipperTableAbstractMgtUI base) {
		
		this.base =base;

		this.table_id=selectedTableId;
	}


	public ManageVesselDialog(String table_id, ShipperTableAbstractMgtUI base, DefaultTableModel vesselModel) {
		
		this(table_id, base);
		
		this.model =vesselModel;
	}


	public void createAndUpdateUI() {

		setTitle(this.table_id+"테이블 선박 관리");

		setModal(true);

		getContentPane().add(buildCenter());	

		getContentPane().add(buildControl(),BorderLayout.SOUTH);

		setSize(520,550);

		this.setMinimumSize(new Dimension(400,400));

		ViewUtil.center(this, false);

		setVisible(true);

	}

	/**
	 * view
	 * @return
	 */
	private Component buildCenter() {

		KSGPanel pnMain = new KSGPanel();

		_tblVesselList = new JTable();

		_tblVesselList.setRowHeight(25);

		_tblVesselList.setModel(model);

		_tblVesselList.updateUI();

		pnMain.add(new JScrollPane(_tblVesselList));

		_tblVesselList.getParent().setBackground(Color.white);

		return pnMain;
	}

	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		if(command.equals("취소"))
		{
			this.setVisible(false);
			dispose();
		}
		else if(command.equals("수정"))
		{
			int row = _tblVesselList.getSelectedRow();
			
			if(row==-1) return;
			
			String vesselAbbr = (String) _tblVesselList.getValueAt(row, 1);

			HashMap<String, Object> param = new HashMap<String, Object>();

			param.put("vessel_name", vesselAbbr);

			//TODO 
			SearchVesselDialog searchVesselDialog = new SearchVesselDialog("",new ArrayList<>());

			searchVesselDialog.createAndUpdateUI();


			if(searchVesselDialog.result!=null)
			{
				model.setValueAt(searchVesselDialog.result, row, 0);
			}
		}
	}


	@Override
	public void updateView() {
		// TODO Auto-generated method stub
		
	}
}
