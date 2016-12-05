package com.ksg.view.adv.dialog;

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

import com.ksg.dao.DAOManager;
import com.ksg.domain.Vessel;
import com.ksg.view.adv.comp.VesselInfo;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.util.ViewUtil;

public class SearchVesselDialog extends KSGDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txfSearch;
	private JList liVessel;
	String vesselName;
	public VesselInfo info;

	public static final int OK_OPTION=1;
	public static final int CANCEL_OPTION=2;
	public int OPTION;
	private JPanel pnCenter;
	private boolean isSelected=false;
	private JButton butAdd;
	private JButton pnAddVesselAbbr;
	private JDialog addDialog;


	public SearchVesselDialog(String vesselName) {
		super();
		this.vesselName=vesselName;
		baseService = DAOManager.getInstance().createBaseService();
	}
	public void createAndUpdateUI() {
		setTitle("���ڸ� �˻�");
		setModal(true);

		JPanel pnTitle = new JPanel(new BorderLayout());

		JLabel lblTitle = new JLabel("�˻��� ���ڸ�: "+vesselName);

		JPanel pnMargin = new JPanel();
		pnMargin.setPreferredSize(new Dimension(15,0));
		pnTitle.add(pnMargin,BorderLayout.WEST);
		pnTitle.add(lblTitle);

		JPanel pnSearchRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

		pnSearchRight.add(butAdd);
		pnSearchRight.add(butSearch);

		pnTitle.add(pnSearchRight,BorderLayout.EAST);

		pnCenter = new JPanel();
		pnCenter.setVisible(false);
		pnCenter.setLayout(new BorderLayout());
		liVessel = new JList();


		JPanel pnSearch = new JPanel();
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
		JButton butCancel = new JButton("�ݱ�");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				setVisible(false);
				dispose();

			}});




		JPanel pnRightControl = new JPanel();
		pnRightControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnRightControl.add(butOK);
		pnRightControl.add(butCancel);
		JPanel pnLeftContorl = new JPanel();
		pnAddVesselAbbr = new JButton("���ڸ� ����߰�");
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
		int result=JOptionPane.showConfirmDialog(null,vessel_name+":"+vessel_abbr+"�� �߰��Ͻðڽ��ϱ�?","���ڸ� ��� �߰�",JOptionPane.OK_CANCEL_OPTION);
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
				JOptionPane.showMessageDialog(null, vessel_abbr+" �߰��߽��ϴ�.");
				txfSearch.setText("");
			} catch (SQLException e1) 
			{
				if(e1.getErrorCode()==2627)// �ߺ�Ű
				{
					JOptionPane.showMessageDialog(null, "���ڸ��� �����մϴ�.");
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
	private void addVesselAction2(final String te) {
		
		JFrame frame = new JFrame();
	    JOptionPane optionPane = new JOptionPane();
	    optionPane.setMessage("���ڸ� �߰�");
	    optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
	    JButton butUse = new JButton("�����");
	    butUse.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				
				Vessel vessel = new Vessel();
				vessel.setVessel_name(te);
				vessel.setVessel_abbr(te);
//				vessel.setVessel_use(Vessel.USE);
				try {
					baseService.insertVessel(vessel);
//					_baseService.insertVessel_Abbr(vessel);
					JOptionPane.showMessageDialog(null, te+" �߰��߽��ϴ�.");
					txfSearch.setText("");
				} catch (SQLException e1) 
				{
					if(e1.getErrorCode()==2627)// �ߺ�Ű
					{
						JOptionPane.showMessageDialog(null, "���ڸ��� �����մϴ�.");
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

	    JButton butNonUse = new JButton("������");
	    butNonUse.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {Vessel vessel = new Vessel();
			vessel.setVessel_name(te);
			vessel.setVessel_abbr(te);
//			vessel.setVessel_use(Vessel.NON_USE);
			try {
				baseService.insertVessel(vessel);
				baseService.insertVessel_Abbr(vessel);
				JOptionPane.showMessageDialog(null, te+" �߰��߽��ϴ�.");
				txfSearch.setText("");
			} catch (SQLException e1) 
			{
				if(e1.getErrorCode()==2627)// �ߺ�Ű
				{
					JOptionPane.showMessageDialog(null, "���ڸ��� �����մϴ�.");
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

	    JButton butCancel = new JButton("���");
	    butCancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				addDialog.setVisible(false);
				addDialog.dispose();
			}
		});
	    optionPane.setOptions(new Object[] {butUse,butNonUse,butCancel  });
	    addDialog = optionPane.createDialog(frame, "���ڸ�");
	    addDialog.setVisible(true);


		/*int result=JOptionPane.showConfirmDialog(null,te+"�� �߰��Ͻðڽ��ϱ�?","���ڸ� �߰�",JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.YES_OPTION)
		{
			Vessel vessel = new Vessel();
			vessel.setVessel_name(te);
			vessel.setVessel_abbr(te);
			try {
				_baseService.insertVessel(vessel);
				_baseService.insertVessel_Abbr(vessel);
				JOptionPane.showMessageDialog(null, te+" �߰��߽��ϴ�.");
				txfSearch.setText("");
			} catch (SQLException e1) 
			{
				if(e1.getErrorCode()==2627)// �ߺ�Ű
				{
					JOptionPane.showMessageDialog(null, "���ڸ��� �����մϴ�.");
				}else
				{
					JOptionPane.showMessageDialog(null, e1.getMessage());	
				}
				e1.printStackTrace();
			}
		}*/
	}

}
