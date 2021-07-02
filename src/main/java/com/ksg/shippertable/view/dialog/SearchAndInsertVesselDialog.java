package com.ksg.shippertable.view.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.ksg.adv.view.comp.VesselInfo;
import com.ksg.adv.view.dialog.AddVesselDialog;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.shippertable.view.comp.AdvertiseTable;
import com.ksg.view.comp.dialog.KSGDialog;

/**
 * @설명 
 * @author archehyun
 *
 */
public class SearchAndInsertVesselDialog extends KSGDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txfSearch;
	private JList liVessel;
	private String vesselName;
	public VesselInfo info;

	public static final int OK_OPTION=1;
	public static final int CANCEL_OPTION=2;
	public int OPTION;
	private JPanel pnCenter;
	private boolean isSelected=false;
	private JButton butAdd;
	private JButton pnAddVesselAbbr;
	private JDialog addDialog;

	private int row,col;
	private String value;
	private DefaultTableModel vesselModel;
	private AdvertiseTable advTable;
	public SearchAndInsertVesselDialog(String vesselName) {
		super();
		this.vesselName=vesselName;
		baseService = DAOManager.getInstance().createBaseService();
	}
	public SearchAndInsertVesselDialog(AdvertiseTable advTable,
			int selectedVesselrow, int col, String value,
			DefaultTableModel vesselModel) {
		this.row =selectedVesselrow;
		this.col = col;
		this.value=value;
		this.vesselModel = vesselModel;
		this.advTable = advTable;
		this.vesselName= value;
		baseService = DAOManager.getInstance().createBaseService();
		
		
	}
	public void createAndUpdateUI() {
		setTitle("선박명 검색");
		setModal(true);

		JPanel pnTitle = new JPanel(new BorderLayout());

		JLabel lblTitle = new JLabel("검색된 선박명: "+vesselName);

		JPanel pnMargin = new JPanel();
		pnMargin.setPreferredSize(new Dimension(15,0));
		pnTitle.add(pnMargin,BorderLayout.WEST);
		pnTitle.add(lblTitle);

		JPanel pnSearchRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		butAdd = new JButton("선박명 추가");
		butAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{

				if(isSelected)
				{
					if(liVessel.getSelectedIndex()==-1)
					{
						JOptionPane.showMessageDialog(null, "선택된 선박명이 없습니다.");
						return;
					}

					addVesselAction(liVessel.getSelectedValue().toString());
				}else
				{
					addVesselAction(vesselName);
				}
			}});


		JToggleButton butSearch = new JToggleButton("펼치기");
		butSearch.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {

				JToggleButton button = (JToggleButton) e.getSource();


				isSelected = button.isSelected();
				pnCenter.setVisible(button.isSelected());

				if(isSelected)
				{
					button.setText("접기");
					butAdd.setEnabled(false);
					pnAddVesselAbbr.setVisible(true);
				}else
				{
					button.setText("펼치기");
					butAdd.setEnabled(true);
					pnAddVesselAbbr.setVisible(false);
				}
				pack();

			}});

		pnSearchRight.add(butAdd);
		pnSearchRight.add(butSearch);

		pnTitle.add(pnSearchRight,BorderLayout.EAST);

		pnCenter = new JPanel();
		pnCenter.setVisible(false);
		pnCenter.setLayout(new BorderLayout());
		liVessel = new JList();


		JPanel pnSearch = new JPanel();
		JLabel lblSearch = new JLabel("선박명 검색");			

		txfSearch = new JTextField(25);
		txfSearch.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) 
			{
			}

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					try 
					{
						List li=baseService.getVesselInfoByPattenGroupByName(txfSearch.getText()+"%");
						DefaultListModel limodel = new DefaultListModel();
						for(int i=0;i<li.size();i++)
						{
							limodel.addElement(li.get(i));
						}
						liVessel.setModel(limodel);
						liVessel.updateUI();
					}
					catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}

			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}});


		pnSearch.add(lblSearch);
		pnSearch.add(txfSearch);

		pnCenter.add(pnSearch,BorderLayout.NORTH);
		pnCenter.add(new JScrollPane(liVessel));
		pnCenter.add(KSGDialog.createMargin(0,15),BorderLayout.SOUTH);

		JPanel pnControl = new JPanel();
		pnControl.setLayout(new BorderLayout());
		
		JButton butCancel = new JButton("닫기");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				setVisible(false);
				dispose();

			}});
		
		JButton butApply = new JButton("적용");
		butApply.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				int index=liVessel.getSelectedIndex();
				if(index<0)return;
				Vessel vesselName=(Vessel) liVessel.getSelectedValue();
				
				
				
				if(vesselModel.getRowCount()==row)
					vesselModel.setRowCount(vesselModel.getRowCount()+1);
				vesselModel.setValueAt(vesselName.getVessel_name(), row, 0);
				vesselModel.setValueAt(vesselName.getVessel_abbr(), row, 1);
				vesselModel.fireTableCellUpdated(row, 0);
				advTable.setValue(vesselName.getVessel_name(), row, 0);
				setVisible(false);
				dispose();
				

			}});

		JPanel pnRightControl = new JPanel();
		pnRightControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		pnRightControl.add(butApply);
		pnRightControl.add(butCancel);
		
		JPanel pnLeftContorl = new JPanel();
		pnAddVesselAbbr = new JButton("선박명 약어추가");
		pnAddVesselAbbr.setVisible(false);
		pnAddVesselAbbr.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				int row = liVessel.getSelectedIndex();
				if(row==-1)
					return;
				addVesselAbbrAction(vesselName, liVessel.getSelectedValue().toString());

			}
		});
		
		pnLeftContorl.add(pnAddVesselAbbr);

		pnControl.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnControl.add(pnLeftContorl,BorderLayout.WEST);
		pnControl.add(pnRightControl,BorderLayout.EAST);

		getContentPane().add(pnTitle,BorderLayout.NORTH);
		getContentPane().add(pnControl,BorderLayout.SOUTH);
		getContentPane().add(KSGDialog.createMargin(),BorderLayout.WEST);
		getContentPane().add(KSGDialog.createMargin(),BorderLayout.EAST);
		getContentPane().add(pnCenter);

		pack();

		ViewUtil.center(this, false);
		setVisible(true);

	}
	private void addVesselAbbrAction(String vessel_abbr,String vessel_name) {
		
		int result=JOptionPane.showConfirmDialog(null,vessel_name+":"+vessel_abbr+"약어를 추가하시겠습니까?","선박명 약어 추가",JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.YES_OPTION)
		{
			try {
				Vessel vessel = new Vessel();
				vessel.setVessel_name(vessel_name);
				vessel.setVessel_abbr(vessel_abbr);
				
				Vessel info = new Vessel();
				info.setVessel_name(vessel_name);
				try {
					List li=baseService.getVesselListGroupByName(info);
					
					
					if(li.size()>0)
					{	
						Vessel item = (Vessel) li.get(0);
						vessel.setVessel_type(item.getVessel_type());
						
						//20121025 수정 및 추가
						vessel.setVessel_use(item.getVessel_use());
						if(item.getVessel_type()==null)
							vessel.setVessel_type("");
						
					}else
					{
						vessel.setVessel_type("");
						vessel.setVessel_use(Vessel.USE);
					}
					vesselModel.setRowCount(vesselModel.getRowCount()+1);
					vesselModel.setValueAt(vessel_name, row, 0);
					vesselModel.setValueAt(vessel_abbr, row, 1);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				baseService.insertVessel(vessel);
				advTable.setValue( vessel.getVessel_abbr().toUpperCase(), row, 0);
				JOptionPane.showMessageDialog(null, vessel_abbr+" 추가했습니다.");
				txfSearch.setText("");
			} catch (SQLException e1) 
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
		AddVesselDialog addVesselDialog = new AddVesselDialog(advTable,row,col,vesselName,vesselModel);
		addVesselDialog.createAndUpdateUI();
	}
	private void addVesselAction2(final String te) {
		
		JFrame frame = new JFrame();
	    JOptionPane optionPane = new JOptionPane();
	    optionPane.setMessage("선박명 추가");
	    optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
	    JButton butUse = new JButton("사용함");
	    butUse.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				Vessel vessel = new Vessel();
				vessel.setVessel_name(te);
				vessel.setVessel_abbr(te);
//				vessel.setVessel_use(Vessel.USE);
				try {
					baseService.insertVessel(vessel);
//					_baseService.insertVessel_Abbr(vessel);
					JOptionPane.showMessageDialog(null, te+" 추가했습니다.");
					txfSearch.setText("");
				} catch (SQLException e1) 
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
				
				addDialog.setVisible(false);
				addDialog.dispose();
			}
		});

	    JButton butNonUse = new JButton("사용안함");
	    butNonUse.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {Vessel vessel = new Vessel();
			vessel.setVessel_name(te);
			vessel.setVessel_abbr(te);
//			vessel.setVessel_use(Vessel.NON_USE);
			try {
				baseService.insertVessel(vessel);
				baseService.insertVessel_Abbr(vessel);
				JOptionPane.showMessageDialog(null, te+" 추가했습니다.");
				txfSearch.setText("");
			} catch (SQLException e1) 
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
				addDialog.setVisible(false);
				addDialog.dispose();
			}
		});

	    JButton butCancel = new JButton("취소");
	    butCancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				addDialog.setVisible(false);
				addDialog.dispose();
			}
		});
	    optionPane.setOptions(new Object[] {butUse,butNonUse,butCancel  });
	    addDialog = optionPane.createDialog(frame, "선박명");
	    addDialog.setVisible(true);


		/*int result=JOptionPane.showConfirmDialog(null,te+"을 추가하시겠습니까?","선박명 추가",JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.YES_OPTION)
		{
			Vessel vessel = new Vessel();
			vessel.setVessel_name(te);
			vessel.setVessel_abbr(te);
			try {
				_baseService.insertVessel(vessel);
				_baseService.insertVessel_Abbr(vessel);
				JOptionPane.showMessageDialog(null, te+" 추가했습니다.");
				txfSearch.setText("");
			} catch (SQLException e1) 
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
		}*/
	}
}
