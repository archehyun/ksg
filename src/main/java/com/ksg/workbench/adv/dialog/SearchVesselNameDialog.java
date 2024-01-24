package com.ksg.workbench.adv.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.JobAttributes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.ArrayList;
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

import com.ksg.common.dao.DAOManager;
import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Vessel;
import com.ksg.service.BaseService;
import com.ksg.service.VesselService;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.adv.comp.VesselInfo;

public class SearchVesselNameDialog extends KSGDialog {

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

//	private BaseService baseService;

	private VesselServiceV2 vesselService;

	public SearchVesselNameDialog(String vesselName) {
		super();
		this.vesselName=vesselName;

		vesselService = new VesselServiceImpl();
	}
	public void createAndUpdateUI() {
		setTitle("���ڸ� �˻�");

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

		JButton butOK = new JButton("����");
		butOK.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				if(isSelected)
				{
					if(liVessel.getSelectedIndex()==-1)
					{
						JOptionPane.showMessageDialog(null, "���õ� ���ڸ��� �����ϴ�.");
						return;
					}
					info = new VesselInfo();

					info.setExist(true);
					
					info.vesselName= (String) liVessel.getSelectedValue();

					OPTION = SearchVesselNameDialog.OK_OPTION;	
				}else
				{
					info = new VesselInfo();

					info.setExist(true);

					info.vesselName= vesselName;

					OPTION = SearchVesselNameDialog.OK_OPTION;
				}


				setVisible(false);
				dispose();




			}});
		JButton butCancel = new JButton("�ݱ�");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				setVisible(false);
				dispose();

			}});

		KSGPanel pnRightControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));		
		pnRightControl.add(butOK);
		pnRightControl.add(butCancel);

		KSGPanel pnLeftContorl = new KSGPanel();
		pnAddVesselAbbr = new JButton("���ڸ� ����߰�");
		pnAddVesselAbbr.setVisible(false);
		pnAddVesselAbbr.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				int row = liVessel.getSelectedIndex();
				if(row==-1)
				{
					JOptionPane.showMessageDialog(SearchVesselNameDialog.this, "���õ� ���ڸ��� �����ϴ�.");
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

		JLabel lblTitle = new JLabel("�˻��� ���ڸ�: "+vesselName);		

		pnTitle.add(lblTitle);


		JToggleButton butSearch = new JToggleButton("��ġ��");
		butSearch.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {

				JToggleButton button = (JToggleButton) e.getSource();


				isSelected = button.isSelected();
				pnCenter.setVisible(button.isSelected());

				if(isSelected)
				{
					button.setText("����");
					butAdd.setEnabled(false);
					pnAddVesselAbbr.setVisible(true);
				}else
				{
					button.setText("��ġ��");
					butAdd.setEnabled(true);
					pnAddVesselAbbr.setVisible(false);
				}
				pack();

			}});


		KSGPanel pnSearchRight = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		butAdd = new JButton("���ڸ� �߰�");
		butAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{

				if(isSelected)
				{
					if(liVessel.getSelectedIndex()==-1)
					{
						JOptionPane.showMessageDialog(null, "���õ� ���ڸ��� �����ϴ�.");
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
		JLabel lblSearch = new JLabel("���ڸ� �˻�");			

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
						HashMap<String, Object> param = new HashMap<String, Object>();
						
						param.put("vessel_name", txfSearch.getText());
						
						List<HashMap<String, Object>> li=vesselService.selectListByLike(param);
//						List li1=baseService.getVesselInfoByPattenGroupByName(txfSearch.getText()+"%");
						
						DefaultListModel limodel = new DefaultListModel();
						for(HashMap vesseName:li)
						{
							limodel.addElement((String)vesseName.get("vessel_name"));
						}
						liVessel.setModel(limodel);
						liVessel.updateUI();
					}
					catch (SQLException e1) {
						
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, e1.getMessage());
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
		int result=JOptionPane.showConfirmDialog(null,vessel_name+":"+vessel_abbr+"�� �߰��Ͻðڽ��ϱ�?","���ڸ� ��� �߰�",JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.YES_OPTION)
		{
			try {
				
				HashMap<String,Object> vessel = new HashMap<String, Object>();
				vessel.put("vessel_name", vessel_name);
				vessel.put("vessel_abbr", vessel_abbr);

				vesselService.insertDetail(vessel);
				JOptionPane.showMessageDialog(SearchVesselNameDialog.this, vessel_abbr+" �߰��߽��ϴ�.");
				txfSearch.setText("");
			}
			catch (AlreadyExistException e)
			{
				e.printStackTrace();
				JOptionPane.showMessageDialog(SearchVesselNameDialog.this, "���ڸ� �� �����մϴ�.");
			}
		} 

	}
	private void addVesselAction(final String te)
	{
		AddVesselDialog addVesselDialog = new AddVesselDialog(te);
		addVesselDialog.createAndUpdateUI();
	}


}