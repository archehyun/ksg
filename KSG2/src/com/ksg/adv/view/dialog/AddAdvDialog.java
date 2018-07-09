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
package com.ksg.adv.view.dialog;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.ksg.adv.service.ADVService;
import com.ksg.adv.service.ADVServiceImpl;
import com.ksg.adv.view.comp.ADVListPanel;
import com.ksg.adv.view.comp.XLSManagerImpl;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Company;

/**
 * @author archehyun
 *
 */
public class AddAdvDialog extends JDialog implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Logger logger = Logger.getLogger(getClass());
	private JTextField txfXLFile;
	private JTextField field2;
	private JPanel pnStepOne;
	private JPanel pnStepTwo;
	public String data; 
	public String selectXLSFile;
	public String selectXLSFilePath;
	public String selectDate;
	private CardLayout cardLayout = new CardLayout();
	private JPanel pnMain;
	private JTextField lblXLSFile;
	ADVListPanel manageUI;
	KSGModelManager manager = KSGModelManager.getInstance();
	ADVService service = new ADVServiceImpl();
	private JPanel pnStepZero;
	private int sequenceCount;
	private JButton btnBack;
	private JButton btnNext;
	private XLSManagerImpl xlsmanager;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	KSGPropertis viewPropertis= KSGPropertis.getIntance();
	String dataLocation;
	public AddAdvDialog(Container con, boolean modal,ADVListPanel manageUI) 
	{
		
		this.sequenceCount=0;
		this.manageUI= manageUI;

		this.setModal(modal);	

		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);
		this.getContentPane().add(buildButtom(),BorderLayout.SOUTH);

		Dimension screensize = manageUI.getSize();
		this.setSize(500,180);
		this.setLocation(((int)screensize.getWidth())/3, ((int)screensize.getHeight())/3);
		ViewUtil.center(this, false);
		this.lblXLSFile=manageUI.txfXLSFile;
		this.setTitle("외부데이터 불러오기");
		this.setVisible(true);
	}


	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ADD"))
		{
			dataLocation = viewPropertis.getProperty("dataLocation");
			JFileChooser fileChooser = new JFileChooser(dataLocation);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = fileChooser.getSelectedFile();
				txfXLFile.setText(selectedFile.getAbsolutePath());
				this.selectXLSFile=selectedFile.getName();
				this.selectXLSFilePath= selectedFile.getAbsolutePath();
				this.lblXLSFile.setText(this.selectXLSFile);
			}

		}
		else if(e.getActionCommand().equals("Cancel"))
		{
			this.dispose();
		}
		else if(e.getActionCommand().equals("Ok"))
		{
			
			try {

				xlsmanager = XLSManagerImpl.getInstance();
				String data = xlsmanager.getData();			

/*				manager.ADVStringData= xlsmanager.getXLSData();
				logger.debug("size;"+manager.ADVStringData.size());*/
				manager.data=data;

				manager.vesselCount = xlsmanager.getSearchedTableCount();

				manager.execute("adv");
				manager.execute("vessel");
				manager.execute("error");

				manageUI.updateTableListPN();
			//	manageUI.butNext.setEnabled(true);
				//manageUI.updateTableInfo();
				//manageUI._txfSearchedTableCount.setText(String.valueOf(manager.vesselCount));
			} catch (Exception e1) {

				JOptionPane.showMessageDialog(null,"광고정보생성에 실패했습니다.");
			}

			this.dispose();

		}
		else if(e.getActionCommand().equals("Back"))
		{
			if(sequenceCount>0)
			{
				cardLayout.previous(pnMain);
				if(sequenceCount==1)
					btnBack.setVisible(false);
				if(sequenceCount==2)
				{
					btnNext.setText("Next");
					btnNext.setActionCommand("Next");
				}
				sequenceCount--;
			}
		}
		else if(e.getActionCommand().equals("Next"))
		{	
			switch (sequenceCount) {
			case 0:
				selectDate=field2.getText();
				if(this.selectDate!=null&&selectDate.length()>0)
				{
					sequenceCount++;
					btnBack.setVisible(true);
					this.cardLayout.next(pnMain);


				}else
				{
					JOptionPane.showMessageDialog(null, "Error");
				}

				break;

			case 1:
				if(this.selectXLSFile!=null)
				{
					sequenceCount++;
					this.cardLayout.next(pnMain);
					btnNext.setText("Ok");
					btnNext.setActionCommand("Ok");


				}else
				{
					JOptionPane.showMessageDialog(null, "Error");
				}

				break;			
			}
		}
	}

	/**
	 * @return
	 */
	public Component buildButtom()
	{
		JPanel pnButtom = new JPanel();
		pnButtom.setLayout(new GridLayout(1,0));
		JPanel pnPass = new JPanel();
		GridLayout grid = new GridLayout(1,0);
		grid.setHgap(5);
		pnPass.setLayout(grid);

		btnBack = new JButton("Back");

		btnBack.setVisible(false);
		btnBack.addActionListener(this);
		pnPass.add(btnBack);
		btnNext = new JButton("Next");
		btnNext.addActionListener(this);

		pnPass.add(btnNext);
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(this);
		pnPass.add(btnCancel);

		pnButtom.add(new JPanel());
		pnButtom.add(pnPass);
		return pnButtom;
	}

	/**
	 * @return
	 */
	public Component buildCenter()
	{
		pnMain = new JPanel();
		pnMain.setLayout(cardLayout);

		pnStepZero	= 	createPnZeroStep();
		pnStepOne 	=	 createPnOneStep();
		pnStepTwo	=	createPnTwoStep();


		pnMain.add(pnStepZero,"StepZero");
		pnMain.add(pnStepOne,"StepOne");
		pnMain.add(pnStepTwo,"StepTwo");

		return pnMain;

	}

	/**
	 * @return
	 */
	public JPanel createPnOneStep()
	{
		JPanel pnStepOne = new JPanel();
		pnStepOne.setLayout(new GridLayout(0,1));
		pnStepOne.setBorder(BorderFactory.createTitledBorder("Select"));
		pnStepOne.add(new JLabel("엑셀 파일을 선택주세요"));

		JLabel label = new JLabel("File : ");
		label.setPreferredSize(new Dimension(50,25));
		JButton button = new JButton("ADD");
		button.addActionListener(this);
		txfXLFile = new JTextField();
		Box box2 =Box.createHorizontalBox();
		box2.setAlignmentX(Box.LEFT_ALIGNMENT);
		box2.add(label);
		box2.add(txfXLFile);
		box2.add(button);

		JLabel label1 = new JLabel("종류 : ");
		label1.setPreferredSize(new Dimension(50,25));

		JComboBox box = new JComboBox();
		box.addItem("모든 Excel 파일");
		box.addItem("모든 Word 파일");

		Box box1 =Box.createHorizontalBox();
		box1.setAlignmentX(Box.LEFT_ALIGNMENT);
		box1.add(label1);
		box1.add(box);
		box1.add(Box.createGlue());


		pnStepOne.add(box2);
		pnStepOne.add(box1);

		return pnStepOne;
	}

	/**
	 * @return
	 */
	private JPanel createPnTwoStep() {
		pnStepTwo = new JPanel();

		ButtonGroup buttonGroup = new ButtonGroup();
		JRadioButton rbt1 = new JRadioButton("일괄등록",true);
		JRadioButton rbt2 = new JRadioButton("순차등록");
		buttonGroup.add(rbt1);
		buttonGroup.add(rbt2);
		pnStepTwo.add(rbt1);
		pnStepTwo.add(rbt2);
		return pnStepTwo;
	}

	/**
	 * @return
	 */
	private JPanel createPnZeroStep() {
		JPanel pnStepZero = new JPanel();
		pnStepZero.setLayout(new GridLayout(0,1));

		JPanel pnShipper = new JPanel();
		JComboBox comboBox = new JComboBox();
		comboBox.setPreferredSize(new Dimension(200,25));
		pnShipper.add(new JLabel("선사 선택"));
		pnShipper.add(comboBox);
		try {
			List<Company> li = service.getCompanyList();
			logger.debug(li.size()+" company searched");
			for(int i=0;i<li.size();i++)
			{
				Company com = li.get(i);
				comboBox.addItem(com.getCompany_abbr());
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		comboBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JComboBox box =(JComboBox) e.getSource();
				String command=e.getActionCommand();
				//manageUI..setText(box.getSelectedItem().toString());

			}});

	
		JButton butSearchShipper = new JButton("검색");
		pnShipper.add(butSearchShipper);
		JPanel pnDate = new JPanel();
		pnDate.add(new JLabel("날짜 선택"));
		pnStepZero.setBorder(BorderFactory.createTitledBorder("날짜선택"));
		field2 =new JTextField(10);
		pnDate.add(field2);
		JCheckBox box = new JCheckBox("오늘 날짜",false);
		box.addChangeListener(new ChangeListener(){


			public void stateChanged(ChangeEvent e) {
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					field2.setText(dateFormat.format(new Date()));
					manageUI.txfDate.setText(dateFormat.format(new Date()));

				}else
				{
					field2.setText("");
					manageUI.txfDate.setText("");
				}
			}});
		pnDate.add(box);
		pnStepZero.add(pnShipper);
		pnStepZero.add(pnDate);

		return pnStepZero;
	}

	public void setSelectedXLSFilLabel(JTextField lblXLSFile) {
		this.lblXLSFile =lblXLSFile; 

	}


}


