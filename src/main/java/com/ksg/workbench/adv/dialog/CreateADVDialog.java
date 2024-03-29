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
package com.ksg.workbench.adv.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ADVData;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.print.logic.quark.v1.XTGPage;
import com.ksg.print.logic.quark.v1.XTGParserImpl;
import com.ksg.service.ADVService;
import com.ksg.service.TableService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.KSGCompboBox2;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.print.PrintADVUI;

public class CreateADVDialog extends KSGDialog implements ActionListener{
	private ADVService service = new ADVServiceImpl();
	private TableService tableService = new TableServiceImpl();
	private XTGManager xtgmanager = new XTGManager();
	private KSGModelManager manager = KSGModelManager.getInstance();

	PrintADVUI ui;
	private JButton btnNext;
	public CreateADVDialog(Component ui) {


		this.ui = (PrintADVUI) ui;
		this.setModal(true);	


	}
	private Component buildButtom() {
		JPanel pnButtom = new JPanel();
		pnButtom.setLayout(new GridLayout(1,0));
		JPanel pnPass = new JPanel();
		GridLayout grid = new GridLayout(1,0);
		grid.setHgap(5);
		pnPass.setLayout(grid);

		btnNext = new JButton("Next");
		btnNext.addActionListener(this);

		pnPass.add(btnNext);


		pnButtom.add(new JPanel());
		pnButtom.add(pnPass);
		return pnButtom;
	}
	private JTextField txfDate;	
	private KSGCompboBox2 cbxCompany;
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private JPanel createPnZeroStep() {
		JPanel pnStepZero = new JPanel();
		pnStepZero.setLayout(new GridLayout(0,1));

		JPanel pnShipper = new JPanel();
		cbxCompany = new KSGCompboBox2("combo",KSGCompboBox2.TYPE2);
		manager.addObservers(cbxCompany);
		manager.execute(cbxCompany.getName());
		cbxCompany.setPreferredSize(new Dimension(200,25));

		pnShipper.add(new JLabel("선사 선택"));

		pnShipper.add(cbxCompany);


		JPanel pnDate = new JPanel();
		pnDate.add(new JLabel("날짜 선택"));
		pnStepZero.setBorder(BorderFactory.createTitledBorder("날짜선택"));
		txfDate =new JTextField(10);

		pnDate.add(txfDate);
		JCheckBox box = new JCheckBox("오늘 날짜",false);
		box.addChangeListener(new ChangeListener(){


			public void stateChanged(ChangeEvent e) {
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					txfDate.setText(dateFormat.format(new Date()));
				}
			}});
		pnDate.add(box);
		pnStepZero.add(pnShipper);
		pnStepZero.add(pnDate);

		return pnStepZero;
	}

	private void createXTGAction() throws SQLException
	{
		String advMessage = new String();
		// 테이블 정보 조회		
		String company = (String) cbxCompany.getSelectedItem();
		String date = txfDate.getText();
		List tableLi = tableService.getTableListByCompany(company, date);
		if(tableLi.size()==0)
		{
			this.setVisible(false);
			this.dispose();
			JOptionPane.showMessageDialog(null, date+"일자에 입력된 광고정보가 없습니다.");			
		}

		// 쿽 프레임 생성
		XTGPage page=xtgmanager.createDefaultPage();// 페이지 생성
		page.setPaser(new XTGParserImpl());		
		page.createTables(tableLi);


		// 새로운 데이터 할당
		List<ADVData> advLi = service.getADVDataList(company, date);

		page.setNewADVData(advLi);


		// 파일 생성
		xtgmanager.createXTGFile(page,company+date+".xtg");
		//ui.dataE.setText(advMessage);
		//ui.dataF.setText(page.parseDoc());

		// 광고정보 조회

	}


	public void actionPerformed(ActionEvent e) {
		String message="";
		try {

			this.createXTGAction();	

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.setVisible(true);
		this.dispose();

	}
	@Override
	public void createAndUpdateUI() {
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		this.getContentPane().add(createPnZeroStep(),BorderLayout.CENTER);
		this.getContentPane().add(buildButtom(),BorderLayout.SOUTH);
		Dimension screensize = ui.getSize();
		this.setSize(500,180);
		this.setLocation(((int)screensize.getWidth())/3, ((int)screensize.getHeight())/3);
		this.setTitle("외부데이터 불러오기");
		this.setVisible(true);

	}
}
