package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.Notification;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.adv.comp.VesselInfo;
import com.ksg.workbench.adv.dialog.AddVesselDialog;
import com.ksg.workbench.master.dialog.BaseInfoDialog;
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
	public SearchAndInsertVesselDialog(AdvertiseTable advTable, int selectedVesselrow, int col, String value, DefaultTableModel vesselModel) {

		this(value);

		this.row =selectedVesselrow;

		this.col = col;

		this.vesselModel = vesselModel;

		this.advTable = advTable;


	}
	public void createAndUpdateUI() {

		setTitle("선박명 검색");

		setModal(true);

		KSGPanel pnTitle = new KSGPanel();

		JLabel lblTitle = new JLabel("검색된 선박명: "+vesselName);	

		pnTitle.add(lblTitle);

		KSGPanel pnSearchRight = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		butAdd = new JButton("선박명 추가");

		butAdd.addActionListener(this);

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

		pnSearchRight.add(butAdd);

		pnSearchRight.add(butSearch);

		pnTitle.add(pnSearchRight,BorderLayout.EAST);

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

		KSGPanel pnControl = new KSGPanel();

		JButton butCancel = new JButton("닫기");

		JButton butApply = new JButton("적용");

		butCancel.addActionListener(this);

		butApply.addActionListener(this);

		KSGPanel pnRightControl = new KSGPanel();
		
		pnRightControl.setLayout(new FlowLayout(FlowLayout.RIGHT));

		pnRightControl.add(butApply);
		
		pnRightControl.add(butCancel);

		KSGPanel pnLeftContorl = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnAddVesselAbbr = new JButton("선박명 약어추가");
		
		pnAddVesselAbbr.setVisible(false);
		
		pnAddVesselAbbr.addActionListener(this);

		pnLeftContorl.add(pnAddVesselAbbr);

		pnControl.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		
		pnControl.add(pnLeftContorl,BorderLayout.WEST);
		
		pnControl.add(pnRightControl,BorderLayout.EAST);

		getContentPane().add(pnTitle,BorderLayout.NORTH);
		
		getContentPane().add(pnControl,BorderLayout.SOUTH);

		getContentPane().add(pnCenter);

		pack();

		ViewUtil.center(this, false);
		
		setVisible(true);

	}

	public KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel();
		return pnMain;
	}
	private void addVesselAbbrAction(String vessel_abbr,String vessel_name) {

		int result=JOptionPane.showConfirmDialog(null,vessel_name+":"+vessel_abbr+"약어를 추가하시겠습니까?","선박명 약어 추가",JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.YES_OPTION)
		{
			try {
				HashMap<String,Object> vesselParam = new HashMap<String, Object>();

				vesselParam.put("vessel_name", vessel_name);
				
				vesselParam.put("vessel_abbr", vessel_abbr);

				vesselService.insertDetail(vesselParam);
				
				vesselModel.setRowCount(vesselModel.getRowCount()+1);
				
				vesselModel.setValueAt(vessel_name, row, 0);
				
				vesselModel.setValueAt(vessel_abbr, row, 1);
				
				Vessel vessel = Vessel.builder()
										.vessel_name(vessel_name)
										.vessel_abbr(vessel_abbr).build();

				vesselService.insertVessel(vessel);
				
				advTable.setValue( vessel.getVessel_abbr().toUpperCase(), row, 0);
				
				txfSearch.setText("");
				
				NotificationManager.showNotification(Notification.Type.WARNING,vessel_abbr+" 추가했습니다.");
			}

			catch (AlreadyExistException e1)
			{
				e1.printStackTrace();
				
				NotificationManager.showNotification(Notification.Type.WARNING,"선박명이 존재합니다.");
			}
			catch (SQLException e1) 
			{
				if(e1.getErrorCode()==2627)// 중복키
				{
					JOptionPane.showMessageDialog(null, "선박명이 존재합니다.");
				}else
				{
					JOptionPane.showMessageDialog(null, e1.getMessage());	
				}
				e1.printStackTrace();
			}
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
