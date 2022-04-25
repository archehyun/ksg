package com.ksg.workbench.adv.dialog;

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

import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.BaseService;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.adv.comp.VesselInfo;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

public class SearchVesselDialog extends KSGDialog {

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
	
	private KSGPanel pnCenter;
	
	private boolean isSelected=false;
	
	private JButton butAdd;
	
	private JButton pnAddVesselAbbr;
	
	private JDialog addDialog;
	
	private BaseService baseService;
	
	private VesselService service;

	public SearchVesselDialog(String vesselName) {
		super();
		this.vesselName=vesselName;
		baseService = DAOManager.getInstance().createBaseService();
		
		service = new VesselServiceImpl();
	}
	public void createAndUpdateUI() {
		setTitle("선박명 검색");
		
		setModal(true);	
	
		getContentPane().add(buildTitle(),BorderLayout.NORTH);
		
		getContentPane().add(buildCenter());
		
		getContentPane().add(buildControl(),BorderLayout.SOUTH);

		pack();

		ViewUtil.center(this, false);
		
		setVisible(true);

	}
	
	private KSGPanel buildControl()
	{
		KSGPanel pnControl = new KSGPanel(new BorderLayout());
		
		JButton butOK = new JButton("적용");
		butOK.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				if(isSelected)
				{
					if(liVessel.getSelectedIndex()==-1)
					{
						JOptionPane.showMessageDialog(null, "선택된 선박명이 없습니다.");
						return;
					}
					info = new VesselInfo();

					info.setExist(true);
					Vessel v = (Vessel) liVessel.getSelectedValue();
					info.vesselName= v.getVessel_name();

					OPTION = SearchVesselDialog.OK_OPTION;	
				}else
				{
					info = new VesselInfo();

					info.setExist(true);

					info.vesselName= vesselName;

					OPTION = SearchVesselDialog.OK_OPTION;
				}


				setVisible(false);
				dispose();




			}});
		JButton butCancel = new JButton("닫기");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				setVisible(false);
				dispose();

			}});

		KSGPanel pnRightControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));		
		pnRightControl.add(butOK);
		pnRightControl.add(butCancel);
		
		KSGPanel pnLeftContorl = new KSGPanel();
		pnAddVesselAbbr = new JButton("선박명 약어추가");
		pnAddVesselAbbr.setVisible(false);
		pnAddVesselAbbr.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				int row = liVessel.getSelectedIndex();
				if(row==-1)
				{
					JOptionPane.showMessageDialog(SearchVesselDialog.this, "선택된 선박명이 없습니다.");
					return;
				}
				addVesselAbbrAction(vesselName, liVessel.getSelectedValue().toString());

			}
		});
		
		pnLeftContorl.add(pnAddVesselAbbr);
		
		pnControl.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnControl.add(pnLeftContorl,BorderLayout.WEST);
		pnControl.add(pnRightControl,BorderLayout.EAST);

		
		return pnControl;
	}
	
	private KSGPanel buildTitle()
	{
		KSGPanel pnTitle = new KSGPanel(new BorderLayout());
		pnTitle.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		
		JLabel lblTitle = new JLabel("검색된 선박명: "+vesselName);		
		
		pnTitle.add(lblTitle);
		
		
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
		
		
		KSGPanel pnSearchRight = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
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
		pnSearchRight.add(butAdd);
		pnSearchRight.add(butSearch);

		pnTitle.add(pnSearchRight,BorderLayout.EAST);
		return pnTitle;
	}
	
	private KSGPanel buildCenter()
	{
		pnCenter = new KSGPanel(new BorderLayout());
		pnCenter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		pnCenter.setVisible(false);
		
		liVessel = new JList();


		KSGPanel pnSearch = new KSGPanel();
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
		return pnCenter;
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
						vessel.setVessel_use(item.getVessel_use());
						if(item.getVessel_type()==null)
							vessel.setVessel_type("");
						
					}else
					{
						vessel.setVessel_type("");
						vessel.setVessel_use(0);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				baseService.insertVessel(vessel);
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
		AddVesselDialog addVesselDialog = new AddVesselDialog(te);
		addVesselDialog.createAndUpdateUI();
	}


}
