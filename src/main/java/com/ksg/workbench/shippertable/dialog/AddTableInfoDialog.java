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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Company;
import com.ksg.domain.ShippersTable;
import com.ksg.service.CompanyService;
import com.ksg.service.ShipperTableService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.service.impl.CompanyServiceImpl;
import com.ksg.service.impl.ShipperTableServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
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
public class AddTableInfoDialog extends KSGDialog implements ActionListener,FocusListener{

	private String company_abbr;
	private KSGModelManager manager = KSGModelManager.getInstance();
	ShipperTableAbstractMgtUI searchUI;
	private ShippersTable searchOp;
	ShippersTable selectedTable;
	private JTextArea txaCommon;
	private JTextArea txaQuark;
	private JTextField txfAgent;
	private JTextField txfCompany;

	private JTextField txfIndex;
	private JTextField txfInPort;
	private JTextField txfInToPort;
	private JTextField txfOther;
	private JTextField txfOutPort;
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
	private JPanel pnClosingTime;
	private JRadioButton optPage;
	private JRadioButton optCFS;
	private JPanel pnInland;

	private JTextField txfInland;
	private ShipperTableService service;
	
	
	private CompanyService companyService;

	public AddTableInfoDialog() {
		super();
		
		advservice= new ADVServiceImpl();
		
		tableService = new TableServiceImpl();
		
		companyService = new CompanyServiceImpl();

		service = new ShipperTableServiceImpl();
	}

	public AddTableInfoDialog(ShipperTableAbstractMgtUI parent) {

		this();
		this.searchUI=parent;		
	}
	public AddTableInfoDialog(ShipperTableAbstractMgtUI prent, ShippersTable selectedCompany) {

		this();
		this.searchUI=prent;	
		this.setModal(true);
		this.setSelectedCompany(selectedCompany);

	}
	public AddTableInfoDialog(ShipperTableAbstractMgtUI parent, String selectedCompany) {

		this();
		this.searchUI=parent;		
		this.setModal(true);
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
		String in_port			= txfInPort.getText();
		String in_to_port		= txfInToPort.getText();
		String out_port			= txfOutPort.getText();
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


		HashMap<String, Object> param = new HashMap<String, Object>();
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


		service.insert(param);

	}

//	private void saveAction_temp() throws SQLException
//	{
//		ShippersTable shippersTable = new ShippersTable();
//		shippersTable.setTable_id(txfTableID.getText());
//		shippersTable.setPage(Integer.parseInt(txfPage.getText()));
//		shippersTable.setBookPage(txfBookPage.getText());
//		shippersTable.setTable_index(Integer.parseInt(txfIndex.getText()));
//		shippersTable.setCompany_abbr(txfCompany.getText());				
//		shippersTable.setAgent(txfAgent.getText());
//		shippersTable.setCommon_shipping(txaCommon.getText());
//		shippersTable.setQuark_format(txaQuark.getText());
//		shippersTable.setIn_port(txfInPort.getText());
//		shippersTable.setIn_to_port(txfInToPort.getText());
//		shippersTable.setOut_port(txfOutPort.getText());
//		shippersTable.setOut_to_port(txfOutToPort.getText());
//		shippersTable.setPort_col(Integer.parseInt(txfPortCount.getText()));
//		shippersTable.setVsl_row(Integer.parseInt(txfVesselCount.getText()));
//		shippersTable.setTitle(txfTitle.getText());
//		shippersTable.setOthercell(Integer.parseInt(txfOther.getText()));
//		shippersTable.setConsole_cfs(txaConsoleCFS.getText());
//		shippersTable.setConsole_page(txfConsolePage.getText());
//		shippersTable.setGubun((String) cbxGubun.getSelectedItem());
//		shippersTable.setC_time(Integer.parseInt(txfCtime.getText()));
//		shippersTable.setD_time(Integer.parseInt(txfDtime.getText()));
//		shippersTable.setInland_indexs(txfInland.getText());
//		tableService.insert(shippersTable);
//		manager.execute(searchUI.getName());
//
//	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("저장"))
		{

			//try{

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

	}
	public Component buildButtom()
	{
		JPanel pnButtom = new JPanel();
		pnButtom.setLayout(new GridLayout(1,0));
		JPanel pnPass = new JPanel(new FlowLayout(FlowLayout.RIGHT));



		JButton btnNext = new JButton("저장");
		btnNext.addActionListener(this);

		pnPass.add(btnNext);
		JButton btnCancel = new JButton("취소");
		btnCancel.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) {

				AddTableInfoDialog.this.setVisible(false);
				AddTableInfoDialog.this.dispose();

			}});

		pnPass.add(btnCancel);

		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		Box bb = new Box(BoxLayout.Y_AXIS);
		bb.add(pnS);
		bb.add(pnS1);
		bb.add(pnPass);
		pnButtom.add(bb);
		return pnButtom;
	}
	private Box buildPnCenter() {

		txfTableID=createTextField( 20);

		txfPage=createTextField( 2);
		txfBookPage=createTextField( 5);
		txfIndex=createTextField( 2);


		txfCompany=createTextField( 13);
		txfAgent=createTextField( 20);	


		txfAgent.setEditable(false);
		txfCompany.setEditable(false);



		txfPortCount=createTextField( 3);
		txfVesselCount=createTextField( 3);		
		txfOutPort=createTextField( 13);		
		txfOther =createTextField( 3);
		txfOutToPort =createTextField( 13);
		txfInPort =createTextField( 13);
		txfInToPort =createTextField( 13);
		txfInland =createTextField( 13);
		txaCommon=createTextArea( 20);
		txaQuark=createTextArea( 20,false);
		txfTitle =createTextField(20);
		txaConsoleCFS = createTextArea(20);
		txfConsolePage = createTextField(5);
		txfDtime =createTextField(3);
		txfCtime =createTextField(3);
		txfDtime.setText("0");
		txfCtime.setText("0");
		txfIndex.setText("0");
		txfOther.setText("0");
		txfIndex.setHorizontalAlignment(JTextField.RIGHT);
		txfPage.setHorizontalAlignment(JTextField.RIGHT);


		JPanel pnFromCompany = new JPanel();
		pnFromCompany.setLayout(new FlowLayout(FlowLayout.LEADING));
		pnFromCompany.add(createForm("선사명 약어: ", txfCompany));
		JButton button = new JButton("검색");
		button.addActionListener(new ActionListener(){

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
//				final JDialog dialog = new JDialog(AddTableInfoDialog.this,true);
//
//				try 
//				{
//					CommandMap param = new CommandMap();
//					CommandMap result= (CommandMap) companyService.selectListByCondition(param);
//					List<CommandMap> data = (List<CommandMap>) result.get("master");
//					DefaultMutableTreeNode root = new DefaultMutableTreeNode("전체선사:"+data.size());
//					Iterator<CommandMap> iter =data.iterator();
//					while(iter.hasNext())
//					{
//						CommandMap company = (CommandMap) iter.next();
//						DefaultMutableTreeNode sub = new DefaultMutableTreeNode(company.get("company_name"));
//						root.add(sub);						
//					}
//
//					dialog.setTitle("선사 명 선택");
//					KSGPanel pnMain = new KSGPanel();
//					pnMain.setLayout( new BorderLayout());
//					final JTree tree = new JTree(root);
//					tree.addMouseListener(new MouseAdapter() {
//
//						public void mouseClicked(MouseEvent arg0) {
//							if(arg0.getClickCount()>1)
//							{
//								TreePath path=tree.getSelectionPath();
//								if(path.getPathCount()!=1)
//								{
//									String company=path.getLastPathComponent().toString();
//									setTableIndex(company);										
//									txfCompany.setText(company);
//									txaCommon.setText(company);
//
//									dialog.setVisible(false);
//									dialog.dispose();
//								}
//							}
//
//						}
//					});
//					tree.addTreeSelectionListener(new TreeSelectionListener(){
//
//						public void valueChanged(TreeSelectionEvent e) {
//							TreePath path=e.getNewLeadSelectionPath();
//
//							if(path.getPathCount()!=1)
//								System.out.println(path.getLastPathComponent());	
//
//						}});
//
//
//					pnMain.add(new JScrollPane(tree),BorderLayout.CENTER);
//					JPanel pnSubPnControl = new JPanel();
//					pnSubPnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
//					JButton butOK = new JButton("OK");
//
//					butOK.addActionListener(new ActionListener(){
//
//						public void actionPerformed(ActionEvent e) 
//						{
//							TreePath path=tree.getSelectionPath();
//							if(path.getPathCount()!=1)
//							{
//								String company=path.getLastPathComponent().toString();
//								try {
//
//									log.debug("company:{}",company);
//									
//									
//									Company companyInfo=companyService.select(company);
//									setTableIndex(companyInfo.getCompany_abbr());
//									txfCompany.setText(companyInfo.getCompany_abbr());
//									txfAgent.setText(companyInfo.getAgent_abbr());
//									txaCommon.setText(companyInfo.getCompany_abbr());
//								} catch (Exception e1) {
//									JOptionPane.showMessageDialog(null, "error:"+e1.getMessage());
//									e1.printStackTrace();
//								}
//							}
//
//							dialog.setVisible(false);
//							dialog.dispose();							
//						}});
//					butOK.setPreferredSize(new Dimension(80,28));
//					pnSubPnControl.add(butOK);
//					JButton butCancel = new JButton("Cancel");
//
//					butCancel.addActionListener(new ActionListener(){
//
//						public void actionPerformed(ActionEvent e) {
//							dialog.setVisible(false);
//							dialog.dispose();
//
//						}});
//					pnSubPnControl.add(butCancel);
//					butCancel.setPreferredSize(new Dimension(80,28));
//					JPanel pnTitleInfo = new JPanel();
//					pnTitleInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
//					pnTitleInfo.add(new JLabel("Chose the Company"));
//					pnMain.add(pnTitleInfo,BorderLayout.NORTH);
//					pnMain.add(pnSubPnControl,BorderLayout.SOUTH);
//					dialog.add(pnMain);					
//					dialog.setSize(400, 400);
//					ViewUtil.center(dialog, false);
//					dialog.setVisible(true);
//
//				} catch (SQLException e1) {
//					e1.printStackTrace();
//				}
				
				SearchCompanyDialog searchCompanyDialog = new SearchCompanyDialog(AddTableInfoDialog.this);
				
				searchCompanyDialog.createAndUpdateUI();
				
				
				
				String company_name = searchCompanyDialog.result;
				if(company_name == null)return;
				try {

					log.debug("company_name:{}",company_name);
					
					
					Company companyInfo=companyService.select(company_name);
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
		JPanel pnFromPortVslCount = new JPanel();
		pnFromPortVslCount.setBorder(BorderFactory.createEmptyBorder());
		pnFromPortVslCount.setLayout(new  FlowLayout(FlowLayout.LEADING));
		pnFromPortVslCount.add(txfPortCount);
		pnFromPortVslCount.add(createForm("선박수 : ", txfVesselCount,50));
		pnFromPortVslCount.add(createForm("기타 : ", txfOther,40));		


		JPanel pnTableInfo = new JPanel();
		pnTableInfo.setBorder(BorderFactory.createEmptyBorder());
		pnTableInfo.setLayout(new  FlowLayout(FlowLayout.LEADING));
		pnTableInfo.add(createForm("페이지 : ", txfPage));
		pnTableInfo.add(createForm("지면페이지 : ", txfBookPage,75));	
		pnTableInfo.add(createForm("인덱스 : ", txfIndex,50));


		cbxGubun = new KSGComboBox("tableType");		


		cbxGubun.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {

				KSGTableColumn column = (KSGTableColumn) cbxGubun.getSelectedItem();
				String command = column.columnField;

				if(command.equals(ShippersTable.GUBUN_CONSOLE))
				{
					pnClosingTime.setVisible(true);
				}
				else
				{
					pnClosingTime.setVisible(false);
				}

				if(command.equals(ShippersTable.GUBUN_INLAND))
				{
					pnInland.setVisible(true);
				}
				else
				{
					pnInland.setVisible(false);
				}


				AddTableInfoDialog.this.pack();

			}
		});


		Box boxTableInfo = Box.createVerticalBox();
		boxTableInfo.setBorder(BorderFactory.createTitledBorder("테이블 정보"));
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

		JPanel pnOutBound = createPnOutBound(gridLayout);


		JPanel pnInBound = createPnInBound(gridLayout);

		pnBound.add( pnOutBound);
		pnBound.add( pnInBound);


		pnClosingTime = new JPanel(new BorderLayout());
		Box bxClosingTime = new Box(BoxLayout.X_AXIS);

		bxClosingTime.add(createForm("Document Closing: ", txfDtime,120));
		bxClosingTime.add(createForm(" Cargo Closing: ", txfCtime,100));
		pnClosingTime.add(bxClosingTime);		
		pnClosingTime.setBorder(BorderFactory.createTitledBorder("Closing Time Index"));


		pnInland = new JPanel(new BorderLayout());		
		TitledBorder boderInland = BorderFactory.createTitledBorder("내륙 기항지 지정");
		pnInland.setBorder(boderInland);


		Box bxInland = new Box(BoxLayout.X_AXIS);
		bxInland.add(createForm("기항지: ", txfInland));

		pnInland.add(bxInland);

		Box pnMain = new Box(BoxLayout.Y_AXIS);
		pnMain.add(boxTableInfo);
		pnMain.add(pnBound);
		pnMain.add(pnClosingTime);
		pnMain.add(pnInland);

		pnClosingTime.setVisible(false);
		pnInland.setVisible(false);

		JPanel pnConsole = new JPanel(new BorderLayout());

		JPanel pnConsolePage = new JPanel(new BorderLayout());
		JPanel pnConsoleCFS = new JPanel(new BorderLayout());

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

		pnMain.add(pn);

		setTableIndex(company_abbr);


		pnMain.add(buildButtom());
		return pnMain;
	}
	private JPanel createPnInBound(GridLayout gridLayout) {
		JPanel pnInBound = new JPanel();
		pnInBound.setLayout(gridLayout);
		pnInBound.add(createForm("국내항 : ", txfInPort));
		pnInBound.add(createForm("외국항 : ", txfInToPort));
		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("수입항 등록");
		pnInBound.setBorder(createTitledBorder);

		JPanel pnInBoundSub = new JPanel();
		pnInBoundSub.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butUpdateInbound = new JButton("등록/수정");
		butUpdateInbound.addActionListener(this);

		return pnInBound;
	}
	private JPanel createPnOutBound(GridLayout gridLayout) {
		JPanel pnOutBound = new JPanel();
		pnOutBound.setLayout(gridLayout);

		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("수출항 등록");
		pnOutBound.setBorder(createTitledBorder);


		pnOutBound.add(createForm("국내항 : ", txfOutPort));
		pnOutBound.add(createForm("외국항 : ", txfOutToPort));
		JPanel pnOutboundSub = new JPanel();
		pnOutboundSub.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butUpdate = new JButton("등록/수정");
		butUpdate.addActionListener(this);
		return pnOutBound;
	}

	public void createAndUpdateUI() {
		this.setTitle("테이블 정보 추가");

		this.addComponentListener(this);
		JPanel pnTitleInfo = new JPanel();
		pnTitleInfo.setLayout(new BorderLayout());
		pnTitleInfo.setBorder(BorderFactory.createEtchedBorder());
		pnTitleInfo.setPreferredSize(new Dimension(0,45));

		JLabel label = new JLabel("테이블 정보 생성",JLabel.LEFT);

		label.setFont(new Font("aria", Font.BOLD, 16));
		pnTitleInfo.add(label,BorderLayout.WEST);
		pnTitleInfo.setBackground(Color.white);


		JPanel leftPadding = new JPanel();
		leftPadding.setPreferredSize(new Dimension(15,0));
		JPanel rightPadding = new JPanel();
		rightPadding.setPreferredSize(new Dimension(15,0));

		this.getContentPane().add(pnTitleInfo,BorderLayout.NORTH);
		this.getContentPane().add(buildPnCenter(), BorderLayout.CENTER);
		this.getContentPane().add(leftPadding, BorderLayout.WEST);
		this.getContentPane().add(rightPadding, BorderLayout.EAST);



		ViewUtil.center(this, true);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);

	}


	private Component createForm(JPanel comp, int i) {
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEADING));
		pnMain.setPreferredSize(new Dimension(0,i));

		pnMain.add(comp);
		return pnMain; 
	}
	private JPanel createForm(String label,Component comp)
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JLabel label2 = new JLabel(label,JLabel.RIGHT);
		label2.setPreferredSize(new Dimension(100,20));
		pnMain.add(label2,BorderLayout.WEST);
		JPanel pnComp = new JPanel();
		pnComp.setLayout(new BorderLayout());
		pnComp.add(comp);
		pnMain.add(pnComp,BorderLayout.CENTER);
		return pnMain; 

	}
	private JPanel createForm(String label,Component comp, int width)
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JLabel label2 = new JLabel(label,JLabel.RIGHT);
		label2.setVerticalTextPosition(JLabel.CENTER);
		label2.setPreferredSize(new Dimension(	width,20));
		pnMain.add(label2,BorderLayout.WEST);
		JPanel pnComp = new JPanel();
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
		if(add)
			comp.addFocusListener(this);

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
			((JTextField) f).selectAll();
			if(f.equals(txfOutToPort))
			{
				StringTokenizer stringTokenizer = new StringTokenizer(txfOutPort.getText(),"#");
				if(stringTokenizer.countTokens()>0)
				{
					String portCount=txfPortCount.getText();
					Vector intCount = new Vector();
					while(stringTokenizer.hasMoreTokens())
					{
						try
						{
							int in = Integer.parseInt(stringTokenizer.nextToken());
							intCount.add(in);
						}catch(NumberFormatException nume)
						{
							System.err.println(nume.getMessage());
						}
					}



					int pCount = Integer.parseInt(portCount);
					String dd="";
					for(int i=1;i<pCount+1;i++)
					{
						boolean flag=true;
						for(int j=0;j<intCount.size();j++)
						{
							int a = (Integer)intCount.get(j);
							if(a==i)
							{
								flag=false;
							}
						}

						if(flag)
						{
							dd+=String.valueOf(i);
							if(i<pCount)
								dd+="#";
						}

					}
					txfOutToPort.setText(dd);
				}

			}
			if(f.equals(txfInToPort))
			{
				StringTokenizer stringTokenizer = new StringTokenizer(txfInPort.getText(),"#");
				if(stringTokenizer.countTokens()>0)
				{
					String portCount=txfPortCount.getText();
					Vector intCount = new Vector();
					while(stringTokenizer.hasMoreTokens())
					{
						try
						{
							int in = Integer.parseInt(stringTokenizer.nextToken());
							intCount.add(in);
						}catch(NumberFormatException nume)
						{
							System.err.println(nume.getMessage());
						}
					}



					int pCount = Integer.parseInt(portCount);
					String dd="";
					for(int i=1;i<pCount+1;i++)
					{
						boolean flag=true;
						for(int j=0;j<intCount.size();j++)
						{
							int a = (Integer)intCount.get(j);
							if(a==i)
							{
								flag=false;
							}
						}

						if(flag)
						{
							dd+=String.valueOf(i);
							if(i<pCount)
								dd+="#";
						}

					}
					txfInToPort.setText(dd);
				}
			}
		}else if (f instanceof JTextArea)
		{
			((JTextArea) f).selectAll();
		}

	}
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}
	private void setTableIndex(String company_abbr) {
		if(company_abbr!=null)
		{
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
		
		initSearchOp();

	}

	private void initSearchOp() {
		if(searchOp!=null)
		{	
			try {
				selectedTable = tableService.getTableById(searchOp.getTable_id());

				txfTitle.setText(selectedTable.getTitle());
				txfCompany.setText(selectedTable.getCompany_abbr());
				txfPage.setText(String.valueOf(selectedTable.getPage()));
				txfBookPage.setText(String.valueOf(selectedTable.getBookPage()));
				txfPortCount.setText(String.valueOf(selectedTable.getPort_col()));
				txfVesselCount.setText((String.valueOf( selectedTable.getPort_col())));
				txaQuark.setText(selectedTable.getQuark_format());
				txaCommon.setText(selectedTable.getCommon_shipping());
				txfInPort.setText(selectedTable.getIn_port());
				txfOutPort.setText(selectedTable.getOut_port());
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

			} catch (SQLException ee) {
				JOptionPane.showMessageDialog(AddTableInfoDialog.this, ee.getMessage());
				ee.printStackTrace();
			}
		}
	}

}
