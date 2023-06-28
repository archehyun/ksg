package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.dtp.api.control.VesselController;
import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.Notification;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.adv.comp.VesselInfo;
import com.ksg.workbench.master.dialog.BaseInfoDialog;
import com.ksg.workbench.master.dialog.InsertVesselAbbrInfoDialog;
import com.ksg.workbench.master.dialog.InsertVesselInfoDialog;
import com.ksg.workbench.shippertable.comp.AdvertiseTable;

/**
 * @설명 
 * @author archehyun
 *
 */
@SuppressWarnings("serial")
public class SearchAndInsertVesselDialog extends BaseInfoDialog{
	/**
	 * 
	 */
	private JTextField txfSearch;

	private JList liVessel;

	private String vesselName;

	public VesselInfo info;

	public static final int OK_OPTION=1;

	public static final int CANCEL_OPTION=2;

	public int OPTION;

	private KSGPanel pnCenter;

	private boolean isSelected=false;

	private JButton butAdd;

	private JButton pnAddVesselAbbr;

	private int row,col;

	private DefaultTableModel vesselModel;

	private AdvertiseTable advTable;

	private VesselServiceV2 vesselService;

	public SearchAndInsertVesselDialog(String vesselName) {
		
		super();
		
		this.setController(new VesselController());
		
		this.vesselName=vesselName;

		vesselService = new VesselServiceImpl();

	}
	public SearchAndInsertVesselDialog(AdvertiseTable advTable, int selectedVesselrow, int col, String vesselName, DefaultTableModel vesselModel) {

		this(vesselName);

		this.row 			= selectedVesselrow;

		this.col 			= col;

		this.vesselModel 	= vesselModel;

		this.advTable 		= advTable;
	}
	
	public KSGPanel buildTitle()
	{
		KSGPanel pnTitle = new KSGPanel();

		JLabel lblTitle = new JLabel("검색된 선박명: "+vesselName);	
		
		KSGPanel pnSearchRight = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JToggleButton butSearch = new JToggleButton("펼치기");

		butSearch.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {

				JToggleButton button = (JToggleButton) e.getSource();

				isSelected = button.isSelected();

				pnCenter.setVisible(button.isSelected());

				butAdd.setEnabled(!isSelected);

				pnAddVesselAbbr.setVisible(isSelected);

				button.setText(isSelected?"접기":"펼치기");

				pack();

			}});
		
		butAdd = new KSGGradientButton("선박명 추가");

		butAdd.addActionListener(this);

		pnSearchRight.add(butAdd);

		pnSearchRight.add(butSearch);
		
		pnTitle.add(lblTitle);
		
		pnTitle.add(pnSearchRight,BorderLayout.EAST);
		
		return pnTitle;
		
	}
	
	public KSGPanel buildControl()
	{
		KSGPanel pnControl = new KSGPanel();

		JButton butCancel = new KSGGradientButton("닫기");

		JButton butApply = new KSGGradientButton("적용");

		butCancel.addActionListener(this);

		butApply.addActionListener(this);

		KSGPanel pnRightControl = new KSGPanel();
		
		pnRightControl.setLayout(new FlowLayout(FlowLayout.RIGHT));

		pnRightControl.add(butApply);
		
		pnRightControl.add(butCancel);

		KSGPanel pnLeftContorl = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnAddVesselAbbr = new KSGGradientButton("선박명 약어추가");
		
		pnAddVesselAbbr.setVisible(false);
		
		pnAddVesselAbbr.addActionListener(this);

		pnLeftContorl.add(pnAddVesselAbbr);

		pnControl.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		
		pnControl.add(pnLeftContorl,BorderLayout.WEST);
		
		pnControl.add(pnRightControl,BorderLayout.EAST);
		
		return pnControl;
		
	}

	public void createAndUpdateUI() {

		setTitle("선박명 검색");

		setModal(true);

		getContentPane().add(buildTitle(),BorderLayout.NORTH);
		
		getContentPane().add(buildControl(),BorderLayout.SOUTH);

		getContentPane().add(buildCenter());

		ViewUtil.center(this, true);
		
		setVisible(true);
	}

	public KSGPanel buildCenter()
	{
		

		pnCenter = new KSGPanel(new BorderLayout());

		pnCenter.setVisible(false);

		pnCenter.setBorder(BorderFactory.createEmptyBorder(5,7,5,7));

		liVessel = new JList();

		JLabel lblSearch = new JLabel("선박명 검색");			

		txfSearch = new JTextField(25);

		txfSearch.addKeyListener(new KeyAdapter(){

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					try 
					{
						HashMap<String, Object> param = new HashMap<String, Object>();

						param.put("vessel_name", txfSearch.getText());

						List<HashMap<String, Object>> li=vesselService.selectListByLike(param);

						DefaultListModel limodel = new DefaultListModel();

						for(HashMap vesseName:li)
						{
							Vessel newItem = new Vessel();
							newItem.setVessel_name((String)vesseName.get("vessel_name"));
							newItem.setVessel_abbr((String)vesseName.get("vessel_abbr"));
							limodel.addElement(newItem);
						}

						liVessel.setModel(limodel);
						liVessel.updateUI();
					}
					catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		KSGPanel pnSearch = new KSGPanel();

		pnSearch.add(lblSearch);

		pnSearch.add(txfSearch);

		pnCenter.add(pnSearch,BorderLayout.NORTH);

		pnCenter.add(new JScrollPane(liVessel));
		
		return pnCenter;
	}
	private void addVesselAbbrAction(String vessel_abbr,String vessel_name) {
		
		InsertVesselAbbrInfoDialog insertVesselAbbrInfoDialog = new InsertVesselAbbrInfoDialog(vessel_name, vessel_abbr);
		
		insertVesselAbbrInfoDialog.createAndUpdateUI();
		
		if(insertVesselAbbrInfoDialog.result == KSGDialog.SUCCESS)
		{
			NotificationManager.showNotification(vessel_abbr+" 추가했습니다.");
			
			advTable.setValue( vessel_abbr.toUpperCase(), row, 0);
			
			txfSearch.setText("");
		}
	}
	private void addVesselAction(final String te)
	{
		InsertVesselInfoDialog insertVesselDaiDialog = new InsertVesselInfoDialog(vesselName);
		
		insertVesselDaiDialog.createAndUpdateUI();
		
		//TODO 선박명이 추가 되었을 때 광고 정보 갱신
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if("선박명 약어추가".equals(command))
		{
			int row = liVessel.getSelectedIndex();

			if(row==-1) return;

			addVesselAbbrAction(vesselName, liVessel.getSelectedValue().toString());
		}
		else if("적용".equals(command))
		{
			int index=liVessel.getSelectedIndex();

			if(index<0)return;

			Vessel vesselName=(Vessel) liVessel.getSelectedValue();

			if(vesselModel.getRowCount()==row) vesselModel.setRowCount(vesselModel.getRowCount()+1);

			vesselModel.setValueAt(vesselName.getVessel_name(), row, 0);

			vesselModel.setValueAt(vesselName.getVessel_abbr(), row, 1);

			vesselModel.fireTableCellUpdated(row, 0);

			advTable.setValue(vesselName.getVessel_name(), row, 0);

			setVisible(false);

			dispose();
		}
		else if("닫기".equals(command))
		{
			setVisible(false);
			dispose();

		}
		else if("선박명 추가".equals(command))
		{
			if(isSelected)
			{
				if(liVessel.getSelectedIndex()==-1)
				{
					NotificationManager.showNotification(Notification.Type.WARNING,"선택된 선박명이 없습니다.");
					return;
				}

				addVesselAction(liVessel.getSelectedValue().toString());
			}else
			{
				addVesselAction(vesselName);
			}
		}
	}
}