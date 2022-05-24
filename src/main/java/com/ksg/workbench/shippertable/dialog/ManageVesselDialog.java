package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.shippertable.ShipperTableAbstractMgtUI;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 이름 : 선박 관리 다이얼 로그
 * @author 박창현
 *
 */
@Slf4j
public class ManageVesselDialog extends KSGDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String table_id;

	private ShipperTableAbstractMgtUI base;

	private JTable _tblVesselList;

	private DefaultTableModel model;

	//	private BaseService baseService;

	VesselService vesselService;
	public ManageVesselDialog(String selectedTableId, ShipperTableAbstractMgtUI base) {
		this.base =base;
		this.table_id=selectedTableId;
		DAOManager manager = DAOManager.getInstance();
		tableService = manager.createTableService();
		//		baseService = manager.createBaseService();

		vesselService = new VesselServiceImpl();
	}


	public ManageVesselDialog(String table_id, ShipperTableAbstractMgtUI base, DefaultTableModel vesselModel) {
		this(table_id, base);
		this.model =vesselModel;
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

	/**
	 * view
	 * 
	 * @return
	 */
	private Component buildControl() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JPanel pnRight = new JPanel();
		pnRight.setLayout( new FlowLayout(FlowLayout.RIGHT));

		JPanel pnLeft = new JPanel();


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



	/**
	 * view
	 * @return
	 */
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

//				Vessel info = new Vessel();
//				info.setVessel_abbr(vesselAbbr);


				//List li=baseService.getVesselAbbrList(info);
				
				HashMap<String, Object> param = new HashMap<String, Object>();
				
				param.put("vessel_name", vesselAbbr);

				log.info("Param:{}",param);
				
				HashMap<String, Object> result =  vesselService.selectDetailListByLike(param);
				
				SearchVesselDialog searchVesselDialog = new SearchVesselDialog((List) result.get("master"));
				
				searchVesselDialog.createAndUpdateUI();


				if(searchVesselDialog.result!=null)
				{

					model.setValueAt(searchVesselDialog.result, row, 0);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
