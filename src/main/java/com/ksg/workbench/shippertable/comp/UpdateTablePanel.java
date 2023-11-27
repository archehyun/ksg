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
package com.ksg.workbench.shippertable.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.dtp.api.service.CompanyService;
import com.dtp.api.service.impl.CompanyServiceImpl;
import com.dtp.api.util.PortIndexUitl;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Company;
import com.ksg.domain.ShippersTable;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.dialog.SearchCompanyDialog2;
import com.ksg.workbench.shippertable.ShipperTableAbstractMgtUI;

/**
 * @author 박창현
 * @설명 테이블 정보 관리 패널
 *
 */
@SuppressWarnings("serial")
public class UpdateTablePanel extends KSGPanel implements FocusListener{
	
	private JTextField 	txfTable_id;
	private JTextField 	txfAgent;
	private JTextField 	txfOutFromPort;
	private JTextField 	txfOutToPort;
	private JTextField 	txfInFromPort;
	private JTextField 	txfInToPort;
	private JTextField 	txfGubun;
	private JTextArea	txaCommon;
	private JTextArea 	txaQuark;
	private JTextField 	txfTitle;
	private JTextField 	txfPage;
	private JTextField 	txfBookPage;
	private JTextField 	txfCompany_Abbr;
	private JTextField 	txfCompany_name;

	private JTextField 	txfIndex;
	private JTextField  txfPortCount;
	private JTextField  txfVesselCount;
	private JTextField 	txfConsolePage;
	private ShippersTable shippersTable;
	private JTextField txfOhterCount;

	private ShipperTableAbstractMgtUI searchUI;

	private JLabel lblSaveInfo;
	private KSGComboBox cbxGubun;
	private JTextArea txaConsoleCFS;
	private JTextField txfDtime;
	private JTextField txfInland;
	private JTextField txfCtime;
	private KSGPanel pnClosingTime;
	private JRadioButton optPage;
	private JRadioButton optCFS;
	private JTabbedPane tabPaneInfo;
	private KSGPanel pnInland;
	
	private Border padding= BorderFactory.createEmptyBorder(5, 5, 5, 5);
	private KSGGradientButton butCompanySearch;
	
	public UpdateTablePanel(ShipperTableAbstractMgtUI searchUI) {
		super();
		
		this.searchUI =searchUI;
		
		initComp();
		
		createAndUpdteUI();

	}
	
	private void initComp()
	{
		this.txfTable_id		= new JTextField(15);	// 테이블 아이디
		this.txfCompany_Abbr 	= new JTextField(15);	// 선사 약어
		this.txfCompany_name 	= new JTextField(15);	// 선사 명
		this.txfAgent 			= new JTextField(15);	// 에이전트

		this.txfPage 			= new JTextField(3);	// 페이지
		this.txfBookPage 		= new JTextField(3);	// 지면 페이지
		this.txfIndex			= new JTextField(2);	// 인덱스

		this.txfOutFromPort 	= new JTextField(15);	// 국내항 인덱스
		this.txfOutToPort		= new JTextField(15);
		this.txfInFromPort 		= new JTextField(15);	// 구분
		this.txfGubun 			= new JTextField(3);
		this.txfInToPort 		= new JTextField(15);
		this.txfTitle 			= new JTextField(15);

		this.txfPortCount 		= new JTextField(2);
		this.txfVesselCount 	= new JTextField(2);
		this.txfOhterCount 		= new JTextField(2);

		this.txfDtime 			= new JTextField(2);
		this.txfCtime 			= new JTextField(2);
		this.txfInland			= new JTextField(2);
		this.txaCommon 			= new JTextArea();

		this.txaConsoleCFS 		= new JTextArea(0,15);
		this.txaQuark 			= new JTextArea(0,15);
		this.txfConsolePage 	= new JTextField(15);

		this.txfTable_id.setEditable(false);
		
		this.txfCompany_Abbr.setEditable(false);
		
		this.txfAgent.setEditable(false);

		this.txfTable_id.setHorizontalAlignment(JTextField.RIGHT);
		
		this.txfPage.setHorizontalAlignment(JTextField.RIGHT);
		
		this.txfBookPage.setHorizontalAlignment(JTextField.RIGHT);
		
		this.txfIndex.setHorizontalAlignment(JTextField.RIGHT);

		this.txfPortCount.setHorizontalAlignment(JTextField.RIGHT);
		
		this.txfVesselCount.setHorizontalAlignment(JTextField.RIGHT);
		
		this.txfOhterCount.setHorizontalAlignment(JTextField.RIGHT);

		this.txfPage.addKeyListener(searchUI);
		
		this.txfBookPage.addKeyListener(searchUI);
		
		this.txfIndex.addKeyListener(searchUI);

		this.txfOutToPort.addKeyListener(searchUI);
		
		this.txfInToPort.addKeyListener(searchUI);
		
		this.txfGubun.addKeyListener(searchUI);
		
		this.txfTitle.addKeyListener(searchUI);

		this.txaCommon.addKeyListener(searchUI);
		
		this.txfPortCount.addKeyListener(searchUI);
		
		this.txfVesselCount.addKeyListener(searchUI);	
		
		this.txfOhterCount.addKeyListener(searchUI);

		this.txfCompany_Abbr.addFocusListener(this);
		
		this.txfAgent.addFocusListener(this);

		this.txfPage.addFocusListener(this);
		
		this.txfBookPage.addFocusListener(this);
		
		this.txfIndex.addFocusListener(this);

		this.txfOutFromPort.addFocusListener(this);
		
		this.txfOutToPort.addFocusListener(this);
		
		this.txfInFromPort.addFocusListener(this);
		
		this.txfInToPort.addFocusListener(this);
		
		this.txfGubun.addFocusListener(this);
		
		this.txfTitle.addFocusListener(this);

		this.txaCommon.addFocusListener(this);
		
		this.txfPortCount.addFocusListener(this);
		
		this.txfVesselCount.addFocusListener(this);
		
		this.txfOhterCount.addFocusListener(this);	

		this.cbxGubun = new KSGComboBox("tableType");
		
		this.cbxGubun.initComp();

		this.cbxGubun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg) {
				
				JComboBox cbx= (JComboBox) arg.getSource();
				
				String command=(String) cbx.getSelectedItem();

				pnClosingTime.setVisible(command.equals(ShippersTable.GUBUN_CONSOLE));

				pnInland.setVisible(command.equals(ShippersTable.GUBUN_INLAND));
			}
		});
		
		
		butCompanySearch = new KSGGradientButton("검색");
		
		butCompanySearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		
		butCompanySearch.addActionListener(new ActionListener(){

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				
				boolean flag = true;
				
				if(flag)
				{
					SearchCompanyDialog2 searchCompanyDialog = new SearchCompanyDialog2();

					searchCompanyDialog.createAndUpdateUI();

					CommandMap result  = (CommandMap) searchCompanyDialog.resultObj;

					if(result  == null)return;
					
					String company_name = (String) result.get("company_name");
					String company_abbr = (String) result.get("company_abbr");
					String agent_name = (String) result.get("agent_name");
					
					txfCompany_Abbr.setText(company_abbr);
					txfCompany_name.setText(company_name);
					txfAgent.setText(agent_name);
				}
				else
				{
					
					//TODO : 삭제
					final JDialog dialog = new JDialog();

					try {
						
						CompanyService advService = new CompanyServiceImpl();
						List li=advService.selectCompanyListByCondition(new Company());

						DefaultMutableTreeNode root = new DefaultMutableTreeNode("전체선사:"+li.size());
						Iterator iter =li.iterator();
						while(iter.hasNext())
						{
							Company company = (Company) iter.next();
							DefaultMutableTreeNode sub = new DefaultMutableTreeNode(company.getCompany_abbr());
							root.add(sub);						
						}

						dialog.setTitle("Company Selection");
						KSGPanel pnMain = new KSGPanel();
						pnMain.setLayout( new BorderLayout());
						final JTree tree = new JTree(root);
						tree.addMouseListener(new MouseAdapter() {

							public void mouseClicked(MouseEvent arg0) {
								if(arg0.getClickCount()>1)
								{
									TreePath path=tree.getSelectionPath();
									if(path.getPathCount()!=1)
									{
										String company=path.getLastPathComponent().toString();
										//setTableIndex(company);										
										txfCompany_Abbr.setText(company);
										
										
										dialog.setVisible(false);
										dialog.dispose();
									}
								}

							}
						});
						tree.addTreeSelectionListener(new TreeSelectionListener(){

							public void valueChanged(TreeSelectionEvent e) {
								TreePath path=e.getNewLeadSelectionPath();

								if(path.getPathCount()!=1)
									System.out.println(path.getLastPathComponent());	

							}});


						pnMain.add(new JScrollPane(tree),BorderLayout.CENTER);
						KSGPanel pnSubPnControl = new KSGPanel();
						pnSubPnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
						JButton butOK = new JButton("OK");

						butOK.addActionListener(new ActionListener(){

							public void actionPerformed(ActionEvent e) 
							{
								TreePath path=tree.getSelectionPath();
								if(path.getPathCount()!=1)
								{

									String company=path.getLastPathComponent().toString();
									try {
										
										Company companyInfo=advService.selectCompanyById(company);									
										txfCompany_Abbr.setText(companyInfo.getCompany_abbr());
										txfAgent.setText(companyInfo.getAgent_abbr());
										txfCompany_name.setText(companyInfo.getCompany_name());
										
										
										
									} catch (SQLException e1) {
										JOptionPane.showMessageDialog(null, "error:"+e1.getMessage());
										e1.printStackTrace();
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}

								dialog.setVisible(false);
								dialog.dispose();							
							}});
						butOK.setPreferredSize(new Dimension(80,28));
						pnSubPnControl.add(butOK);
						JButton butCancel = new JButton("Cancel");

						butCancel.addActionListener(new ActionListener(){

							public void actionPerformed(ActionEvent e) {
								dialog.setVisible(false);
								dialog.dispose();

							}});
						pnSubPnControl.add(butCancel);
						butCancel.setPreferredSize(new Dimension(80,28));
						KSGPanel pnTitleInfo = new KSGPanel();
						pnTitleInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
						pnTitleInfo.add(new JLabel("Chose the Company"));
						pnMain.add(pnTitleInfo,BorderLayout.NORTH);
						pnMain.add(pnSubPnControl,BorderLayout.SOUTH);
						dialog.add(pnMain);					
						dialog.setSize(400, 400);
						ViewUtil.center(dialog, false);
						dialog.setVisible(true);

					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
						
						

				
			}});

		butCompanySearch.setActionCommand("SEARCH_COMPANY");
	}
	private void createAndUpdteUI()
	{
		this.setLayout( new BorderLayout());		

		Box pnMain = new Box(BoxLayout.Y_AXIS);

		KSGPanel pnTable = new KSGPanel();

		GridLayout gridLayout = new GridLayout(0,1);

		gridLayout.setVgap(10);

		pnTable.setLayout(gridLayout);

		TitledBorder boderTable = BorderFactory.createTitledBorder("테이블 정보");

		boderTable.setTitleFont(getFont().deriveFont(Font.BOLD));
		
		pnTable.setBorder(BorderFactory.createCompoundBorder(boderTable, padding) );

		KSGPanel pnCompanySearch = new KSGPanel(new BorderLayout(5,0));

		pnCompanySearch.add(txfCompany_Abbr);

		pnCompanySearch.add(butCompanySearch,BorderLayout.EAST);

		butCompanySearch.addActionListener(searchUI);

		pnTable.add(createForm("테이블 ID: ", txfTable_id));

		pnTable.add(createForm("테이블 구분: ", cbxGubun));

		pnTable.add(createForm("선사명 약어: ", pnCompanySearch));

		pnTable.add(createForm("에이전트 약어: ", 	txfAgent));	

		pnTable.add(createForm("제목: ", 	txfTitle));


		Box pnPage = new Box(BoxLayout.X_AXIS);

		pnPage.add(createForm(" 페이지: ", txfPage,50));

		pnPage.add(createForm(" 지면페이지: ", txfBookPage,75));

		pnPage.add(createForm(" 인덱스: ", txfIndex,50));


		pnTable.add(pnPage);

		Box pnPage2 = new Box(BoxLayout.X_AXIS);

		pnPage2.add(createForm("항구수: ", txfPortCount,80));

		pnPage2.add(createForm("선박수: ", txfVesselCount,80));

		pnPage2.add(createForm("기타: ", txfOhterCount,80));

		pnTable.add(pnPage2);

		pnMain.add(pnTable);

		Box pnBound = Box.createVerticalBox();

		KSGPanel pnOutBound = createPnOutBound(gridLayout);

		KSGPanel pnInBound = createPnInBound(gridLayout);

		pnBound.add( pnOutBound);

		pnBound.add( pnInBound);

		pnMain.add(pnBound);

		pnClosingTime = new KSGPanel(new BorderLayout());

		TitledBorder boderOther = BorderFactory.createTitledBorder("Closing Time Index");

		pnClosingTime.setBorder(boderOther);


		Box bxClosingTime = new Box(BoxLayout.X_AXIS);

		bxClosingTime.add(createForm("Document Closing: ", txfDtime,120));

		bxClosingTime.add(createForm(" Cargo Closing: ", txfCtime,100));

		pnClosingTime.add(bxClosingTime);

		pnInland = new KSGPanel(new BorderLayout());	

		TitledBorder boderInland = BorderFactory.createTitledBorder("내륙 기항지 지정");

		pnInland.setBorder(boderInland);


		Box bxInland = new Box(BoxLayout.X_AXIS);

		bxInland.add(createForm("기항지: ", txfInland));

		pnInland.add(bxInland);

		tabPaneInfo = new JTabbedPane();

		KSGPanel pnS1 = new KSGPanel();

		pnS1.setLayout(new BorderLayout());

		KSGPanel pnS2 = new KSGPanel();	

		pnS2.setLayout(new BorderLayout());


		pnS1.add(new JScrollPane(txaCommon),BorderLayout.CENTER);

		pnS2.add(new JScrollPane(txaQuark),BorderLayout.CENTER);
		
		KSGPanel pnConsole = buildConsolePn();
		
		
		Border padding2 = BorderFactory.createEmptyBorder(5, 7, 5, 7);
		
		pnS1.setBorder(padding2);
		
		pnS2.setBorder(padding2);
		
		pnConsole.setBorder(padding2);


		tabPaneInfo.addTab("공동배선", pnS1);

		tabPaneInfo.addTab("Format", pnS2);

		tabPaneInfo.addTab("Console 정보", pnConsole);

		pnMain.add(pnClosingTime);

		pnMain.add(pnInland);

		pnMain.add(tabPaneInfo);

		pnClosingTime.setVisible(false);

		pnInland.setVisible(false);

		txfTable_id.setEditable(false);

		Box pnMain1 = new Box(BoxLayout.Y_AXIS);

		pnMain1.add(pnMain);

		this.add(pnMain,BorderLayout.NORTH);

		this.add(tabPaneInfo,BorderLayout.CENTER);

		this.add(buildButtom(),BorderLayout.SOUTH);

	}

	

	/**@ 콘솔 정보 저장 화면
	 * @return
	 */
	private KSGPanel buildConsolePn() {

		optPage = new JRadioButton("Page",true);

		optCFS = new JRadioButton("CFS");

		ButtonGroup bgConsole = new ButtonGroup();

		bgConsole.add(optPage);

		bgConsole.add(optCFS);

		KSGPanel pnConsole = new KSGPanel();	

		pnConsole.setLayout(new BorderLayout());

		KSGPanel pnConsolePage = new KSGPanel(new BorderLayout());

		pnConsolePage.add(new JLabel("Page 정보"),BorderLayout.NORTH);

		pnConsolePage.add(txfConsolePage);

		KSGPanel pnConsoleCFS = new KSGPanel(new BorderLayout());

		pnConsoleCFS.add(new JLabel("CFS 정보"), BorderLayout.NORTH);

		pnConsoleCFS.add(new JScrollPane(txaConsoleCFS));

		KSGPanel pnConsolePrintType = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		pnConsolePrintType.add(new JLabel("콘솔 정보 저장 시 저장 버튼을 눌러 주십시요"));

		pnConsole.add(pnConsoleCFS);

		pnConsole.add(pnConsolePage,BorderLayout.NORTH);

		pnConsole.add(pnConsolePrintType,BorderLayout.SOUTH);

		return pnConsole;
	}
	private KSGPanel createPnInBound(GridLayout gridLayout) {
		
		KSGPanel pnInBound = new KSGPanel();

		pnInBound.setLayout(gridLayout);

		pnInBound.add(createForm("국내항 : ", txfInFromPort));

		pnInBound.add(createForm("외국항 : ", txfInToPort));

		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("수입항 Index");

		createTitledBorder.setTitleFont(getFont().deriveFont(Font.BOLD));

		pnInBound.setBorder(BorderFactory.createCompoundBorder(createTitledBorder, padding));

		KSGPanel pnInBoundSub = new KSGPanel();

		pnInBoundSub.setLayout(new FlowLayout(FlowLayout.RIGHT));

		return pnInBound;
	}
	private KSGPanel createPnOutBound(GridLayout gridLayout) {

		KSGPanel pnOutBound = new KSGPanel();

		pnOutBound.setLayout(gridLayout);

		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("수출항 Index");

		createTitledBorder.setTitleFont(getFont().deriveFont(Font.BOLD));

		pnOutBound.setBorder(BorderFactory.createCompoundBorder(createTitledBorder, padding));

		pnOutBound.add(createForm("국내항: ", txfOutFromPort));

		pnOutBound.add(createForm("외국항: ", txfOutToPort));

		KSGPanel pnOutboundSub = new KSGPanel();

		pnOutboundSub.setLayout(new FlowLayout(FlowLayout.RIGHT));

		return pnOutBound;
	}
	
	public ShippersTable getSelectShipperTable() 
	{
		ShippersTable table = new ShippersTable();

		table.setTable_id(txfTable_id.getText());

		table.setQuark_format(txaQuark.getText());

		table.setCompany_abbr(txfCompany_Abbr.getText());
		
		table.setCompany_name(txfCompany_name.getText());

		table.setAgent(txfAgent.getText());

		table.setPage(Integer.parseInt(txfPage.getText()));

		table.setBookPage(txfBookPage.getText());

		table.setTable_index(Integer.parseInt(txfIndex.getText()));

		table.setPort_col(Integer.parseInt(txfPortCount.getText()));

		table.setVsl_row(Integer.parseInt(txfVesselCount.getText()));

		table.setTitle(txfTitle.getText());

		table.setOut_port(txfOutFromPort.getText());

		table.setIn_port(txfInFromPort.getText());

		table.setOut_to_port(txfOutToPort.getText());

		table.setIn_to_port(txfInToPort.getText());

		table.setGubun((String)cbxGubun.getSelectedItem());

		table.setOthercell(Integer.parseInt(txfOhterCount.getText()));

		table.setConsole_cfs(txaConsoleCFS.getText());

		table.setConsole_page(txfConsolePage.getText());

		table.setD_time(Integer.parseInt(txfDtime.getText()));

		table.setC_time(Integer.parseInt(txfCtime.getText()));

		table.setCommon_shipping(txaCommon.getText());

		table.setInland_indexs(txfInland.getText());
		
		table.setDate_isusse(shippersTable.getDate_isusse());
		
		table.setTable_date(shippersTable.getTable_date());

		return table;
	}

	
	private KSGPanel createForm(String label,Component comp)
	{
		return createForm(label,comp, 90);

	}
	private KSGPanel createForm(String label,Component comp,int size)
	{
		KSGPanel pnMain = new KSGPanel();

		pnMain.setLayout(new BorderLayout());

		JLabel lblTitle = new JLabel(label,JLabel.RIGHT);

		lblTitle.setPreferredSize(new Dimension(size,20));

		pnMain.add(lblTitle,BorderLayout.WEST);

		KSGPanel pnComp = new KSGPanel();

		pnComp.setLayout(new BorderLayout());

		pnComp.add(comp);

		pnMain.add(pnComp,BorderLayout.CENTER);

		return pnMain; 
	}

	public Component buildButtom()
	{
		KSGPanel pnButtom = new KSGPanel();

		pnButtom.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton btnNext = new KSGGradientButton("저장하기");

		btnNext.setPreferredSize(new Dimension(90,23));

		btnNext.addActionListener(searchUI);

		lblSaveInfo = new JLabel();

		pnButtom.add(lblSaveInfo);

		pnButtom.add(btnNext);

		return pnButtom;
	}

	public void setShipperTableData(ShippersTable shippersTable) throws Exception {
		
		this.shippersTable = shippersTable;

		txfTable_id.setText(shippersTable.getTable_id());

		txaQuark.setText(shippersTable.getQuark_format());

		txfAgent.setText(shippersTable.getAgent());

		txfGubun.setText(shippersTable.getGubun());

		txfTitle.setText(shippersTable.getTitle());

		txfPortCount.setText(String.valueOf(shippersTable.getPort_col()));

		txfPortCount.setForeground(shippersTable.getTablePortList().size()==shippersTable.getPort_col()?Color.black:Color.red);

		txfVesselCount.setText(String.valueOf(shippersTable.getVsl_row()));

		txfCompany_Abbr.setText(shippersTable.getCompany_abbr());

		txfPage.setText(String.valueOf(shippersTable.getPage()));

		txfBookPage.setText(shippersTable.getBookPage());

		txfIndex.setText(String.valueOf(shippersTable.getTable_index()));

		txaCommon.setText(shippersTable.getCommon_shipping());

		txfInFromPort.setText(shippersTable.getIn_port());

		txfInToPort.setText(shippersTable.getIn_to_port());

		txfOutFromPort.setText(shippersTable.getOut_port());

		txfOutToPort.setText(shippersTable.getOut_to_port());

		txfOhterCount.setText(String.valueOf(shippersTable.getOthercell()));

		txfCtime.setText(String.valueOf(shippersTable.getC_time()));

		txfDtime.setText(String.valueOf(shippersTable.getD_time()));

		txfInland.setText(shippersTable.getInland_indexs());

		cbxGubun.setSelectedItem(shippersTable.getGubun());

		txaConsoleCFS.setText(shippersTable.getConsole_cfs());

		txfConsolePage.setText(shippersTable.getConsole_page());

		tabPaneInfo.setSelectedIndex(shippersTable.getGubun().equals(ShippersTable.GUBUN_CONSOLE)?2:0);// 0: 콘솔 탭 선택, 2:	공동 배선 탭
	}

	public void focusGained(FocusEvent e) {
		Object f =e.getSource();

		lblSaveInfo.setText("");
		
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
	
	public void focusLost(FocusEvent e) {

		lblSaveInfo.setText("");

		Object f =e.getSource();

		lblSaveInfo.setText("");
	}

	public void setPortCount(int count) {
		txfPortCount.setText(String.valueOf(count));
	}
	
	public void setSaveInfo(String info)
	{
		lblSaveInfo.setText(info);
	}
	public void setCompanyInfo(Company companyInfo) {

		txfCompany_Abbr.setText(companyInfo.getCompany_abbr());

		txfAgent.setText(companyInfo.getAgent_abbr());

		txaCommon.setText(companyInfo.getCompany_abbr());
	}
}