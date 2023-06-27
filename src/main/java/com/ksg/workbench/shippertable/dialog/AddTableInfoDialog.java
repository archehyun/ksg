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
package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.dtp.api.control.ShipperTableController;
import com.dtp.api.util.PortIndexUitl;
import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Company;
import com.ksg.domain.ShippersTable;
import com.ksg.service.CompanyService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.master.dialog.BaseInfoDialog;
import com.ksg.workbench.shippertable.ShipperTableAbstractMgtUI;

import lombok.extern.slf4j.Slf4j;

/**

 * @FileName : AddTableInfoDialog.java

 * @Project : KSG2

 * @Date : 2022. 1. 23. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 신규 테이블 정보 추가

 */
@Slf4j
@SuppressWarnings("serial")
public class AddTableInfoDialog extends BaseInfoDialog implements FocusListener{

	private KSGModelManager manager = KSGModelManager.getInstance();

	private ShipperTableAbstractMgtUI searchUI;

	private String company_abbr;
	private ShippersTable searchOp;
	ShippersTable selectedTable;
	private JTextArea txaCommon;
	private JTextArea txaQuark;
	private JTextField txfAgent;
	private JTextField txfCompany;

	private JTextField txfIndex;
	private JTextField txfInFromPort;
	private JTextField txfInToPort;
	private JTextField txfOther;
	private JTextField txfOutFromPort;
	private JTextField txfOutToPort;
	private JTextField txfPage;
	private JTextField txfBookPage;
	private JTextField txfPortCount;
	private JTextField txfTableID;
	private JTextField txfTitle;
	private JTextField txfVesselCount;
	private JTextArea txaConsoleCFS;

	private JTextField txfConsolePage;
	private JTextField txfDtime;
	private JTextField txfCtime;
	private int vgap=31;
	private KSGComboBox cbxGubun;
	private KSGPanel pnClosingTime;
	private JRadioButton optPage;
	private JRadioButton optCFS;
	private KSGPanel pnInland;

	private JTextField txfInland;

//	private ShipperTableService service;

	private CompanyService companyService;

	private JLabel lblTitle;

	public AddTableInfoDialog() {

		super();

		advservice 		= new ADVServiceImpl();

		tableService 	= new TableServiceImpl();

		companyService 	= new CompanyServiceImpl();

		this.addComponentListener(this);

		this.setController(new ShipperTableController());
	}

	public AddTableInfoDialog(ShipperTableAbstractMgtUI parent) {

		this();

		this.setModal(true);

		this.searchUI = parent;		
	}
	public AddTableInfoDialog(ShipperTableAbstractMgtUI parent, ShippersTable selectedCompany) {

		this(parent);

		this.setSelectedCompany(selectedCompany);
	}
	public AddTableInfoDialog(ShipperTableAbstractMgtUI parent, String selectedCompany) {

		this(parent);

		this.company_abbr=selectedCompany;
	}

	private void saveAction() throws Exception
	{
		String table_id 		= txfTableID.getText();

		int page 				= Integer.parseInt(txfPage.getText());

		String bookPage 		= txfBookPage.getText();

		int table_index			= Integer.parseInt(txfIndex.getText());

		String company_abbr 	= txfCompany.getText();

		String agent 			= txfAgent.getText();

		String common_shipping 	= txaCommon.getText();

		String quark_format		= txaQuark.getText();

		String in_port			= txfInFromPort.getText();

		String in_to_port		= txfInToPort.getText();

		String out_port			= txfOutFromPort.getText();

		String out_to_port		= txfOutToPort.getText();

		int port_col			= Integer.parseInt(txfPortCount.getText());

		int vsl_row				= Integer.parseInt(txfVesselCount.getText());

		String title 			= txfTitle.getText();

		int othercell			= Integer.parseInt(txfOther.getText());

		String console_cfs		= txaConsoleCFS.getText();

		String console_page		= txfConsolePage.getText();

		String gubun			= ((KSGTableColumn) cbxGubun.getSelectedItem()).columnName;

		int c_time				= Integer.parseInt(txfCtime.getText());

		int d_time				= Integer.parseInt(txfDtime.getText());

		String inland_indexs	= txfInland.getText();


		CommandMap param = new CommandMap();
		param.put("table_id", table_id);
		param.put("page", page);
		param.put("bookPage", bookPage);
		param.put("table_index", table_index);
		param.put("company_abbr", company_abbr);
		param.put("agent", agent);
		param.put("common_shipping", common_shipping);
		param.put("quark_format",  quark_format);
		param.put("in_port",  in_port);
		param.put("in_to_port",  in_to_port);
		param.put("out_port",  out_port);
		param.put("out_to_port",  out_to_port);
		param.put("port_col",  port_col);
		param.put("vsl_row",  vsl_row);
		param.put("title",  title);
		param.put("othercell",  othercell);
		param.put("console_cfs",  console_cfs);
		param.put("console_page",  console_page);
		param.put("gubun",  gubun);
		param.put("c_time",  c_time);
		param.put("d_time",  d_time);
		param.put("inland_indexs",  inland_indexs);

		callApi("addTableInfoDialog.insert",param);

//		service.insert(param);

	}

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("저장"))
		{
			if(txfTableID.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(null, "테이블 ID를 지정하세요");
				return;
			}

			if(txfPage.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(null, "Page를 지정하세요");
				return;
			}
			
			if(txfIndex.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(null, "Index를 지정하세요");
				return;
			}
			if(txfCompany.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(null, "선사를 지정하세요");
				return;
			}

			if(txfPortCount.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(null, "항구수를 지정하세요");
				return;
			}
			
			if(txfVesselCount.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(null, "선박수를 지정하세요");
				return;
			}
			if(txfCompany.getText().length()<=0)
			{
				JOptionPane.showMessageDialog(null, "선사를 지정하세요");
				return;
			}

			try {
				
				saveAction();

				this.setVisible(false);

				this.dispose();

				searchUI.fnUpdate();
			}

			catch (AlreadyExistException e1) {
				JOptionPane.showMessageDialog(null, "동일한 테이블 ID가 존재합니다.");	
			}

			catch(Exception e1)
			{
				JOptionPane.showMessageDialog(this, e1.getMessage());
				e1.printStackTrace();

			}

		}
		else if(command.equals("취소"))
		{
			this.setVisible(false);

			this.dispose();
		}

	}

	private JComponent buildPnCenter() {

		txfTableID 		= createTextField( 20);

		txfPage 		= createTextField( 2);

		txfBookPage 	= createTextField( 5);

		txfIndex		= createTextField( 2);

		txfCompany		= createTextField( 13);

		txfAgent 		= createTextField( 20);

		txfAgent.setEditable(false);

		txfCompany.setEditable(false);

		txfPortCount	= createTextField( 3);

		txfVesselCount	= createTextField( 3);

		txfOutFromPort		= createTextField( 13);

		txfOther		= createTextField( 3);

		txfOutToPort 	= createTextField( 13);

		txfInFromPort 		= createTextField( 13);

		txfInToPort 	= createTextField( 13);

		txfInland  		= createTextField( 13);

		txaCommon		= createTextArea( 20);

		txaQuark		= createTextArea( 20,false);

		txfTitle 		= createTextField(20);

		txaConsoleCFS 	= createTextArea(20);

		txfConsolePage 	= createTextField(5);

		txfDtime 		= createTextField(3);

		txfCtime 		= createTextField(3);

		txfDtime.setText("0");

		txfCtime.setText("0");

		txfIndex.setText("0");

		txfOther.setText("0");

		txfIndex.setHorizontalAlignment(JTextField.RIGHT);

		txfPage.setHorizontalAlignment(JTextField.RIGHT);

		KSGPanel pnFromCompany = new KSGPanel();

		pnFromCompany.setLayout(new FlowLayout(FlowLayout.LEADING));

		pnFromCompany.add(createForm("선사명 약어: ", txfCompany));

		JButton button = new KSGGradientButton("검색");

		button.addActionListener(new ActionListener(){

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {

				SearchCompanyDialog searchCompanyDialog = new SearchCompanyDialog(AddTableInfoDialog.this);

				searchCompanyDialog.createAndUpdateUI();

				String company_abbr = searchCompanyDialog.result;

				if(company_abbr == null)return;
				try {

					log.debug("company_name:{}",company_abbr);

					CommandMap param = new CommandMap();

					param.put("company_abbr", company_abbr);

					Company companyInfo=companyService.select(param );

					setTableIndex(companyInfo.getCompany_abbr());

					txfCompany.setText(companyInfo.getCompany_abbr());

					txfAgent.setText(companyInfo.getAgent_abbr());

					txaCommon.setText(companyInfo.getCompany_abbr());

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "error:"+e1.getMessage());
					e1.printStackTrace();
				}

			}});

		pnFromCompany.add(button);

		KSGPanel pnFromPortVslCount = new KSGPanel();

		pnFromPortVslCount.setBorder(BorderFactory.createEmptyBorder());

		pnFromPortVslCount.setLayout(new  FlowLayout(FlowLayout.LEADING));

		pnFromPortVslCount.add(txfPortCount);

		pnFromPortVslCount.add(createForm("선박수 : ", txfVesselCount,50));

		pnFromPortVslCount.add(createForm("기타 : ", txfOther,40));		

		KSGPanel pnTableInfo = new KSGPanel();

		pnTableInfo.setBorder(BorderFactory.createEmptyBorder());

		pnTableInfo.setLayout(new  FlowLayout(FlowLayout.LEADING));

		pnTableInfo.add(createForm("페이지 : ", txfPage));

		pnTableInfo.add(createForm("지면페이지 : ", txfBookPage,75));

		pnTableInfo.add(createForm("인덱스 : ", txfIndex,50));

		cbxGubun = new KSGComboBox("tableType");

		cbxGubun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				KSGTableColumn column = (KSGTableColumn) cbxGubun.getSelectedItem();

				if(column== null) return;

				String command = column.columnField;
				
				pnClosingTime.setVisible(command.equals(ShippersTable.GUBUN_CONSOLE));

				pnInland.setVisible(command.equals(ShippersTable.GUBUN_INLAND));

				AddTableInfoDialog.this.pack();
			}
		});

		Box boxTableInfo = Box.createVerticalBox();

		boxTableInfo.setBackground(Color.white);

		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("테이블 정보");

		createTitledBorder.setTitleFont(getFont().deriveFont(Font.BOLD));

		boxTableInfo.setBorder(createTitledBorder);

		boxTableInfo.add(createForm(createForm("테이블 ID : ", txfTableID),vgap));

		boxTableInfo.add(createForm(createForm("테이블 구분 : ", cbxGubun),vgap));

		boxTableInfo.add(pnFromCompany);

		boxTableInfo.add(createForm(createForm("에이전트 약어 : ", txfAgent),vgap));

		boxTableInfo.add( pnTableInfo);		

		boxTableInfo.add(createForm("항구수 : ",pnFromPortVslCount));

		boxTableInfo.add(createForm(createForm("제목 : ", txfTitle),vgap));

		Box pnBound = Box.createVerticalBox();

		GridLayout gridLayout = new GridLayout(0,1);

		gridLayout.setVgap(10);

		pnBound.add( createPnOutBound(gridLayout));

		pnBound.add( createPnInBound(gridLayout));

		pnClosingTime = new KSGPanel(new BorderLayout());

		Box bxClosingTime = new Box(BoxLayout.X_AXIS);

		bxClosingTime.add(createForm("Document Closing: ", txfDtime,120));

		bxClosingTime.add(createForm(" Cargo Closing: ", txfCtime,100));

		pnClosingTime.add(bxClosingTime);	

		pnClosingTime.setBorder(BorderFactory.createTitledBorder("Closing Time Index"));

		pnInland = new KSGPanel(new BorderLayout());

		TitledBorder boderInland = BorderFactory.createTitledBorder("내륙 기항지 지정");

		pnInland.setBorder(boderInland);

		Box bxInland = new Box(BoxLayout.X_AXIS);

		bxInland.add(createForm("기항지: ", txfInland));

		pnInland.add(bxInland);

		Box pnBox = new Box(BoxLayout.Y_AXIS);
		pnBox.add(boxTableInfo);
		pnBox.add(pnBound);
		pnBox.add(pnClosingTime);
		pnBox.add(pnInland);

		pnClosingTime.setVisible(false);
		pnInland.setVisible(false);

		KSGPanel pnConsole = new KSGPanel(new BorderLayout());

		KSGPanel pnConsolePage = new KSGPanel(new BorderLayout());
		
		KSGPanel pnConsoleCFS = new KSGPanel(new BorderLayout());

		pnConsoleCFS.add(new JScrollPane(txaConsoleCFS));
		
		pnConsolePage.add(txfConsolePage);
		
		optPage = new JRadioButton("Page",true);
		
		optCFS = new JRadioButton("CFS");



		pnConsoleCFS.add(new JLabel("CFS 정보"),BorderLayout.NORTH);
		
		pnConsolePage.add(new JLabel("Page 정보"),BorderLayout.NORTH);
		
		pnConsole.add(pnConsoleCFS);
		
		pnConsole.add(pnConsolePage,BorderLayout.NORTH);


		JTabbedPane pn = new JTabbedPane();

		pn.addTab("공동배선", new JScrollPane(txaCommon));
		
		pn.addTab("QuarkFormat", new JScrollPane(txaQuark));
		
		pn.addTab("Console", pnConsole);

		pnBox.add(pn);

		setTableIndex(company_abbr);

		KSGPanel pnMain = new KSGPanel();

		pnMain.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		pnMain.add(pnBox);

		return pnMain;
	}
	private KSGPanel createPnInBound(GridLayout gridLayout) {

		KSGPanel pnInBound = new KSGPanel();

		pnInBound.setLayout(gridLayout);

		pnInBound.add(createForm("국내항 : ", txfInFromPort));

		pnInBound.add(createForm("외국항 : ", txfInToPort));

		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("수입항 등록");

		createTitledBorder.setTitleFont(getFont().deriveFont(Font.BOLD));

		pnInBound.setBorder(createTitledBorder);

		KSGPanel pnInBoundSub = new KSGPanel();

		pnInBoundSub.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton butUpdateInbound = new JButton("등록/수정");

		butUpdateInbound.addActionListener(this);

		return pnInBound;
	}
	private KSGPanel createPnOutBound(GridLayout gridLayout) {

		KSGPanel pnOutBound = new KSGPanel();

		pnOutBound.setLayout(gridLayout);

		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("수출항 등록");

		createTitledBorder.setTitleFont(getFont().deriveFont(Font.BOLD));

		pnOutBound.setBorder(createTitledBorder);

		pnOutBound.add(createForm("국내항 : ", txfOutFromPort));

		pnOutBound.add(createForm("외국항 : ", txfOutToPort));

		KSGPanel pnOutboundSub = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butUpdate = new JButton("등록/수정");

		butUpdate.addActionListener(this);

		return pnOutBound;
	}

	public void createAndUpdateUI() {

		this.setTitle("테이블 정보 추가");

		this.setBackground(Color.white);

		this.addComponentListener(this);

		this.getContentPane().add(buildTitle(),BorderLayout.NORTH);

		this.getContentPane().add(buildPnCenter());

		this.getContentPane().add(buildControl(),BorderLayout.SOUTH);

		ViewUtil.center(this, true);

		this.setResizable(false);

		this.setVisible(true);
	}

	private JComponent buildTitle()
	{
		KSGPanel pnTitle = new KSGPanel();

		pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));

		lblTitle = new JLabel("테이블 정보 생성");

		lblTitle.setFont(new Font("area",Font.BOLD,16));

		pnTitle.add(lblTitle);

		return pnTitle;
	}

	private Component createForm(JComponent comp, int i) {

		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.LEADING));

		pnMain.setPreferredSize(new Dimension(0,i));

		pnMain.add(comp);

		return pnMain; 
	}
	private KSGPanel createForm(String label,Component comp)
	{
		KSGPanel pnMain = new KSGPanel();

		pnMain.setLayout(new BorderLayout());

		JLabel label2 = new JLabel(label,JLabel.RIGHT);

		label2.setPreferredSize(new Dimension(100,20));

		pnMain.add(label2,BorderLayout.WEST);

		KSGPanel pnComp = new KSGPanel();

		pnComp.setLayout(new BorderLayout());

		pnComp.add(comp);

		pnMain.add(pnComp,BorderLayout.CENTER);

		return pnMain; 

	}
	private KSGPanel createForm(String label,Component comp, int width)
	{
		KSGPanel pnMain = new KSGPanel();

		pnMain.setLayout(new BorderLayout());

		JLabel label2 = new JLabel(label,JLabel.RIGHT);

		label2.setVerticalTextPosition(JLabel.CENTER);

		label2.setPreferredSize(new Dimension(	width,20));

		pnMain.add(label2,BorderLayout.WEST);

		KSGPanel pnComp = new KSGPanel();

		pnComp.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnComp.add(comp);

		pnMain.add(pnComp,BorderLayout.CENTER);

		return pnMain; 
	}
	public JTextArea createTextArea(int i)
	{
		return this.createTextArea(i, true);
	}
	public JTextArea createTextArea(int i,boolean add)
	{
		JTextArea comp =  new JTextArea(5,i);

		if(add) comp.addFocusListener(this);

		return comp;
	}

	public JTextField createTextField(int i)
	{
		JTextField comp =  new JTextField(i);

		comp.addFocusListener(this);

		return comp;
	}
	
	public void focusGained(FocusEvent e) {
		
		Object f =e.getSource();
		
		if(f instanceof JTextField)
		{
			JTextField txfToIndex = (JTextField) f;

			JTextField txfFromIndex = null;

			txfToIndex.selectAll();

			/**
			 * toIndex 생성
			 */
			if(f.equals(txfOutToPort)||f.equals(txfInToPort))
			{
				try {
					txfFromIndex 		= f.equals(txfOutToPort)?txfOutFromPort:txfInFromPort;

					List intCountArray 	= PortIndexUitl.extractIntArray(txfFromIndex);

					if(intCountArray.isEmpty()) return;

					int portCount 		= Integer.parseInt(txfPortCount.getText());

					String strToIndex 	= PortIndexUitl.creatIndexString(intCountArray, portCount);

					txfToIndex.setText(strToIndex);

				}catch(Exception ee)
				{
					txfToIndex.setText("");
					txfFromIndex.setText("");
				}
			}

		}else if (f instanceof JTextArea)
		{
			((JTextArea) f).selectAll();
		}
	}

	
	public void focusLost(FocusEvent arg0) {

	}
	private void setTableIndex(String company_abbr) {

		if(company_abbr==null) return;

		try 
		{
			int last=tableService.getLastTableIndex(company_abbr);

			txfIndex.setText(String.valueOf((last+1)));

		} catch (SQLException e) {

			e.printStackTrace();

		}catch (NullPointerException e1) 
		{
			txfIndex.setText("0");
		}
		txfCompany.setText(company_abbr);
	}

	public ShippersTable getSelectedCompany() {
		return searchOp;
	}

	public void setSelectedCompany(ShippersTable selectedCompany) {
		this.searchOp = selectedCompany;
	}

	@Override
	public void componentShown(ComponentEvent e) {

		cbxGubun.initComp();

		CommandMap param = new CommandMap();
		
		if(searchOp!=null) param.put("table_id", searchOp.getTable_id());

		callApi("addTableInfoDialog.init", param);
	}

	@Override
	public void updateView() {
		
		CommandMap resultMap= this.getModel();

		boolean success = (boolean) resultMap.get("success");

		if(success)
		{
			String serviceId=(String) resultMap.get("serviceId");
			
			if("addTableInfoDialog.insert".equals(serviceId))
			{	
				result = KSGDialog.SUCCESS;

				JOptionPane.showMessageDialog(this, "추가했습니다.");	

				close();
			}

			if("addTableInfoDialog.init".equals(serviceId))
			{	
				ShippersTable selectedTable = (ShippersTable) resultMap.get("selectedTable");
				
				if(selectedTable==null) return;
				
				txfTitle.setText(selectedTable.getTitle());
				txfCompany.setText(selectedTable.getCompany_abbr());
				txfPage.setText(String.valueOf(selectedTable.getPage()));
				txfBookPage.setText(String.valueOf(selectedTable.getBookPage()));
				txfPortCount.setText(String.valueOf(selectedTable.getPort_col()));
				txfVesselCount.setText((String.valueOf( selectedTable.getPort_col())));
				txaQuark.setText(selectedTable.getQuark_format());
				txaCommon.setText(selectedTable.getCommon_shipping());
				txfInFromPort.setText(selectedTable.getIn_port());
				txfOutFromPort.setText(selectedTable.getOut_port());
				txfInToPort.setText(selectedTable.getIn_to_port());
				txfOutToPort.setText(selectedTable.getOut_to_port());
				txfOther.setText(String.valueOf(selectedTable.getOthercell()));
				txfAgent.setText(selectedTable.getAgent());
				cbxGubun.setSelectedItem(selectedTable.getGubun());				
				txfCtime.setText(String.valueOf(selectedTable.getC_time()));
				txfDtime.setText(String.valueOf(selectedTable.getD_time()));
				txaConsoleCFS.setText(selectedTable.getConsole_cfs());
				txfConsolePage.setText(selectedTable.getConsole_page());
				txfInland.setText(selectedTable.getInland_indexs());
				
			}
		}
		else{  
			String error = (String) resultMap.get("error");

			JOptionPane.showMessageDialog(this, error);
		}
	}
}
