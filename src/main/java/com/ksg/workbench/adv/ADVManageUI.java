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
package com.ksg.workbench.adv;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.dtp.api.control.ShipperTableController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.TablePort;
import com.ksg.service.TableService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.FileInfo;
import com.ksg.view.comp.KSGCheckBox;
import com.ksg.view.comp.KSGRadioButton;
import com.ksg.view.comp.table.KSGTableImpl;
import com.ksg.workbench.adv.comp.SheetModel;
import com.ksg.workbench.adv.dialog.AdjestADVListDialog;
import com.ksg.workbench.adv.dialog.ViewXLSFileDialog;
import com.ksg.workbench.common.comp.AbstractMgtUI;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.tree.CustomTree;
import com.ksg.workbench.common.comp.tree.KSGTreeDefault;
import com.ksg.workbench.shippertable.dialog.AddTableInfoDialog_temp;

import lombok.extern.slf4j.Slf4j;


/**

 * @FileName : ADVManageUI.java

 * @Project : KSG2

 * @Date : 2021. 7. 7. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 광고정보 자동 입력 화면

 */
@SuppressWarnings("unchecked")
@Slf4j
public class ADVManageUI extends AbstractMgtUI  implements ActionListener
{	

	private static int _tableViewCount = 10;

	private static final String SEARCH_TYPE_COMPANY = "선사";

	private static final String SEARCH_TYPE_PAGE = "페이지";

	private static final long serialVersionUID = 1L;

	private static KSGTreeDefault tree1;

	private CustomTree tree;

	public KSGTableImpl 		_tblError;

	private JTable			_tblSheetNameList;

	private JTree			_treeMenu;

	private JTextField  	_txfCPage,
	_txfPage,
	_txfPCompany,
	_txfPort,
	_txfSearchByCompany,
	_txfVessel,
	txfImportDate;

	CommandMap viewModel = new CommandMap();

	public JTextField		_txfXLSFile,_txfSearchedTableCount,_txfCompany,_txfDate;

	private AdjestADVListDialog adjestADVListDialog;

	private ButtonGroup 	bgKeyword;

	private JButton 		butAdjust,butCompanyAdd,butPre;

	public JButton			butNext;

	private JList 			companyLi;

	private Vector 			companyList;

	private JList 			fileLi,fileLi2,pageLi;

	private Vector<KSGXLSImportPanel> importTableList = new Vector<KSGXLSImportPanel>();

	private boolean 		isPageSearch=true;

	private int 			pageCount;

	private Vector 			pageList;

	private KSGPanel 		pnSubSearch,pnSubSelect,pnTableInfo,pnTableList,pnLeftMenu;

	private JTabbedPane 	pnTab;

	private Vector 			resultA = new Vector();

	private int 			resultOK,currentPage,resultCancel,startTableIndex,totalTableCount;

	private String 			searchType=SEARCH_TYPE_COMPANY;

	private String			selectedInput="File";

	private ADVListPanel advListPanel;

	private TablePort tablePort;

	private TableService tableService;

	private SearchPanel searchPanel;

	private ADVServiceImpl _advService;

	public ADVManageUI() {

		super();

		this.title="광고정보 입력";

		this.borderColor = new Color(179,195,207);

		Properties properties = new Properties();

		this.setName("ADVManageUI");

		this.setController(new ShipperTableController());

		tableService = new TableServiceImpl();

		_advService = new ADVServiceImpl();

		this.addComponentListener(this);

		createAndUpdateUI();

		callApi("aDVManageUI.init");
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("이전"))
		{
			if(currentPage<=pageCount&&currentPage>0)
			{
				currentPage--;

				this.updateTableListPN();
			}
		}
		else if(command.equals("다음"))
		{
			logger.debug("다음 c:"+currentPage+", t:"+pageCount);

			if(currentPage<pageCount)
			{
				currentPage++;
				this.updateTableListPN();
			}

		}else if(command.equals("신규등록"))
		{
			AddTableInfoDialog_temp addTableInfoDialog = new AddTableInfoDialog_temp(this,manager.selectedCompany);
			addTableInfoDialog.createAndUpdateUI();
		}
	}

	/**
	 * @return
	 */
	private Component buildCenter() {


		pnTab = new JTabbedPane();

		pnTab.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JTabbedPane tp=(JTabbedPane) e.getSource();
				if(tp.getSelectedIndex()==0)
				{
					if(pnLeftMenu!=null)
						pnLeftMenu.setVisible(true);
				}else
				{
					if(pnLeftMenu!=null)
						pnLeftMenu.setVisible(false);	
				}

			}});

		initComp();


		//검색 화면

		searchPanel = new SearchPanel(this);

		// 결과 봐면

		advListPanel = new ADVListPanel();

		advListPanel.setSearchPanel(searchPanel);

		searchPanel.setAdvListPanel(advListPanel);


		pnTab.addTab("입력정보", searchPanel);
		pnTab.addTab("결과", advListPanel);

		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		pnMain.add(pnTab);


		return pnMain;
	}



	private KSGPanel buildHistoryCenter()
	{
		KSGPanel pnMain = new KSGPanel();

		pnMain.setLayout(new BorderLayout());

		KSGPanel pnNorth = new KSGPanel(new BorderLayout());

		JLabel lblTableCountlbl =new JLabel("검색된 테이블 수 : ");


		butAdjust = new JButton("위치조정");

		butAdjust.addActionListener(this);

		butAdjust.setEnabled(false);

		KSGPanel pnNorthLeft = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		KSGPanel pnNorthRight = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		pnNorthLeft.add(lblTableCountlbl);

		pnNorthLeft.add(_txfSearchedTableCount);


		pnNorthRight.add(butAdjust);

		//pnNorthRight.add(butReLoad);//

		pnNorth.add(pnNorthLeft,BorderLayout.WEST);

		pnNorth.add(pnNorthRight,BorderLayout.EAST);



		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		//TODO 설정 파일 오류 확인
		/* 2014.11.3 오류 발생 주석 처리
		 *
		 */
		//pane.setDividerLocation(Integer.parseInt((propertis.getProperty("errorDividerLoction"))));
		KSGPanel pnTableList = createPnTableList();

		pane.setTopComponent(pnTableList);
		pnMain.add(pane);
		pnMain.add(pnNorth,BorderLayout.NORTH);
		pnMain.add(buildSouthPn(),BorderLayout.SOUTH);
		return pnMain;
	}
	private KSGPanel buildLeftMenu() 
	{
		pnLeftMenu = new KSGPanel();
		KSGPanel pnSearch =  new KSGPanel();
		pnSearch.setLayout(new BorderLayout());

		_treeMenu = createTreeMenu();		
		_txfSearchByCompany = new JTextField(8);


		KSGPanel pnSearchByCompany = new KSGPanel();

		JLabel lblCompany = new JLabel("선사 검색");

		lblCompany.setPreferredSize( new Dimension(60,15));

		pnSearchByCompany .add(lblCompany);

		pnSearchByCompany .add(_txfSearchByCompany);

		_txfSearchByCompany.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;

				String text=_txfSearchByCompany.getText();


				try{

					DefaultMutableTreeNode node = searchTreeNode(isPageSearch, text);


					if(node==null)
					{
						JOptionPane.showMessageDialog(ADVManageUI.this, "검색된 정보가 없습니다.");
						_txfSearchByCompany.setText("");
						return;
					}

					TreeNode[] nodes = ((DefaultTreeModel)tree.getModel()).getPathToRoot(node);

					TreePath path = new TreePath(nodes);

					tree.scrollPathToVisible(path);

					tree.setSelectionPath(path);


				}catch (NumberFormatException ee) {

					JOptionPane.showMessageDialog(ADVManageUI.this, text+" <== 정확한 숫자를 입력하세요");
					logger.error(ee.getMessage());

				}
				catch (Exception ee)
				{
					JOptionPane.showMessageDialog(ADVManageUI.this, "error:"+ee.getMessage());
					ee.printStackTrace();
				}
				finally {
					_txfSearchByCompany.setText("");
				}


			}
		});
		JCheckBox box = new KSGCheckBox(SEARCH_TYPE_PAGE,true);
		box.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box =(JCheckBox) e.getSource();
				isPageSearch=box.isSelected();

			}});

		pnSearchByCompany.add(box);

		pnSearch.add(pnSearchByCompany);

		pnLeftMenu.setLayout(new BorderLayout());


		KSGPanel pnContorl = new KSGPanel();
		ButtonGroup group = new ButtonGroup();


		JRadioButton button = new KSGRadioButton("선사별");

		JRadioButton button1 = new KSGRadioButton("페이지별",true);
		group.add(button);
		group.add(button1);

		pnContorl.add(button);
		pnContorl.add(button1);

		ItemListener itemListener= new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				AbstractButton but = (AbstractButton) e.getSource();
				if(ItemEvent.SELECTED==e.getStateChange())
				{
					String te = but.getActionCommand();

					if(te.equals("선사별"))
					{
						tree.changeState(CustomTree.COMPAY_LIST);

					}
					else if(te.equals("페이지별"))
					{
						tree.changeState(CustomTree.PAGE_LIST);
					}
				}
			}};

			button.addItemListener(itemListener);
			button1.addItemListener(itemListener);
			pnContorl.add(new JSeparator(JSeparator.HORIZONTAL));
			JButton butADDTable = new JButton(new ImageIcon("images/plus.gif"));

			butADDTable.setPreferredSize(new Dimension(35,25));
			butADDTable.setFocusPainted(false);
			butADDTable.setActionCommand("신규등록");
			butADDTable.setToolTipText("신규 테이블 등록");
			butADDTable.addActionListener(this);

			pnContorl.add(butADDTable);
			JButton butDelTable = new JButton(new ImageIcon("images/minus.gif"));
			butDelTable.setPreferredSize(new Dimension(35,25));
			butDelTable.setFocusPainted(false);
			butDelTable.setActionCommand("삭제");
			butDelTable.addActionListener(this);
			pnContorl.add(butDelTable);

			KSGPanel pnTitle = new KSGPanel();
			pnTitle.setBackground(new Color(88,141,250));
			pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel("테이블 목록");
			label.setForeground(Color.white);
			pnTitle.add(label);
			pnTitle.setPreferredSize( new Dimension(0,22));

			pnSearch.add(pnSearchByCompany,BorderLayout.NORTH);

			pnSearch.add(new JScrollPane(_treeMenu),BorderLayout.CENTER);

			pnSearch.add(pnContorl,BorderLayout.SOUTH);

			pnSearch.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));			

			pnLeftMenu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			pnLeftMenu.add(pnSearch);


			return pnLeftMenu;
	}

	public DefaultMutableTreeNode searchTreeNode(boolean isPage, Object param)
	{
		return tree.searchNode( isPage? Integer.parseInt((String) param):(String) param);

	}


	/**
	 * @return
	 */
	private Component buildSouthPn() {

		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new BorderLayout());

		KSGPanel paRight = new KSGPanel();
		paRight.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton butADD = new JButton("광고불러오기",new ImageIcon("images/importxls.gif"));
		butADD.setToolTipText("광고정보추가");
		butADD.setPreferredSize(new Dimension(100,20));
		butADD.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {


			}});

		JButton butCancel = new JButton(new ImageIcon("images/cancel.gif"));
		butCancel.setPreferredSize(new Dimension(35,25));
		butCancel.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) {
				//initInfo();
				_txfCompany.setText("");
				_txfPage.setText("");
				DefaultListModel model = new DefaultListModel();
				fileLi.setModel(model);
				pageLi.setModel(model);
				Object data[][] = new Object[0][];
				SheetModel mo = new SheetModel(data);
				_tblSheetNameList.setModel(mo);
				butAdjust.setEnabled(false);

			}});

		JLabel lblDate = new JLabel(" 입력날짜 : ");

		txfImportDate = new JTextField(8);

		JCheckBox cbxImportDate = new JCheckBox("월요일",false);
		cbxImportDate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					txfImportDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
				}
			}});
		paRight.add(lblDate);
		paRight.add(txfImportDate);
		paRight.add(cbxImportDate);
		//paRight.add(butSave);
		paRight.add(butCancel);

		KSGPanel pnLeft =new KSGPanel();
		pnLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton button = new JButton("설정 보기");
		button.setVisible(false);
		button.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String page_l ="";

				if(searchType.equals(SEARCH_TYPE_COMPANY))
				{
					String c = _txfCompany.getText()+"_";
					for(int i=0;i<pageList.size();i++)
					{
						c+=pageList.get(i);
						if(i<pageList.size()-1)
							c+="_";
					}
					page_l=c;
				}else
				{
					String p = _txfPage.getText()+"_";
					for(int i=0;i<companyList.size();i++)
					{
						p+=companyList.get(i);
						if(i<companyList.size()-1)
							p+="_";
					}
					page_l=p;
				}
				JOptionPane.showMessageDialog(null, page_l);

			}});
		pnLeft.add(button);

		JButton jButton = new JButton("설정 저장");
		jButton.setVisible(false);
		pnLeft.add(jButton);
		pnMain.add(pnLeft,BorderLayout.WEST);
		pnMain.add(paRight,BorderLayout.EAST);

		return pnMain;
	}


	/**
	 * 
	 */
	public void createAndUpdateUI() 
	{	

		this.setLayout(new BorderLayout(10,10));

		this.add(buildNorthPn(),BorderLayout.NORTH);

		this.add(buildCenter(),BorderLayout.CENTER);

		this.add(buildLeftMenu(),BorderLayout.WEST);
	}


	/**
	 * @return
	 */
	private KSGPanel createPnTableList() {
		KSGPanel pnTableListMain = new KSGPanel();
		pnTableListMain.setLayout(new BorderLayout());

		pnTableList = new KSGPanel();
		pnTableList.setLayout(new BoxLayout(pnTableList, BoxLayout.Y_AXIS));


		KSGPanel pnTableListControl = new KSGPanel();
		butPre = new JButton("이전");
		butPre.setEnabled(false);
		butPre.addActionListener(this);
		pnTableListControl.add(butPre);
		butNext = new JButton("다음");
		butNext.addActionListener(this);
		butNext.setEnabled(false);
		pnTableListControl.add(butNext);
		pnTableListMain.add(new JScrollPane(pnTableList),BorderLayout.CENTER);
		return pnTableListMain;
	}

	/**
	 * @return
	 */
	private JTree createTreeMenu() 
	{
		tree1 = new KSGTreeDefault("tree1");


		tree1.setRowHeight(25);

		tree1.setComponentPopupMenu(createTreePopuomenu());

		tree = new CustomTree();

		tree.setRowHeight(25);

		tree.setComponentPopupMenu(createTreePopuomenu());

		tree.addTreeSelectionListener(new TreeSelectionListener(){

			public void valueChanged(TreeSelectionEvent e) {

				TreePath path=e.getNewLeadSelectionPath();
				
				if(path!=null)
				{

					try{
						searchPanel.updateViewByTree(path);
					}catch(Exception e2)
					{
						JOptionPane.showMessageDialog(ADVManageUI.this, e2.getMessage());
						e2.printStackTrace();
						//return;
					}
				}
			}
		});

		return tree;
	}

	/**
	 * @return
	 */
	private JPopupMenu createTreePopuomenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenu newMenu = new JMenu("새로 만들기");
		JMenuItem itemCompany = new JMenuItem(SEARCH_TYPE_COMPANY);

		JMenuItem itemTable = new JMenuItem("테이블");
		itemTable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				AddTableInfoDialog_temp addTableInfoDialog = new AddTableInfoDialog_temp(ADVManageUI.this,manager.selectedCompany);
				addTableInfoDialog.createAndUpdateUI();
			}});
		JMenuItem xlsMenu = new JMenuItem("파일 불러오기");

		newMenu.add(itemCompany);

		newMenu.add(itemTable);

		menu.add(newMenu);

		menu.add(xlsMenu);
		return menu;
	}
	private JPopupMenu createXLSListPopup() {
		JPopupMenu menu =  new JPopupMenu();
		JMenuItem viewMenu = new JMenuItem("보기");
		viewMenu.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				FileInfo info = (FileInfo) fileLi.getSelectedValue();

				if(info==null)
					return;
				ViewXLSFileDialog dialog = new ViewXLSFileDialog(info);
				dialog.createAndUpdateUI();

			}});

		menu.add(viewMenu);
		return menu;
	}


	/**
	 *컴포넌트 초기화 
	 */
	private void initComp()
	{
		_txfXLSFile = new JTextField(20);
		_txfXLSFile.setEditable(false);
		_txfDate = new JTextField(8);
		_txfDate.setEditable(false);
		_txfPort = new JTextField(2);
		_txfPort.setEditable(false);
		_txfVessel = new JTextField(2);
		_txfVessel.setEditable(false);
		_txfCompany = new JTextField(20);
		_txfCompany.setEditable(false);
		_txfPage = new JTextField(5);
		_txfPage.setEditable(false);
		_txfPage.setVisible(false);
		_txfCPage = new JTextField(5);
		_txfCPage.setEditable(false);
		_txfPCompany = new JTextField(20);
		_txfPCompany.setEditable(false);
		_txfSearchedTableCount = new JTextField(2);
		_txfSearchedTableCount.setEditable(false);
	}

	public void setTabIndex(int i) {
		pnTab.getModel().setSelectedIndex(i);

	}


	/**
	 * @param index
	 */
	public void updateTableListPN() 
	{


		totalTableCount = manager.tableCount-1;
		pageCount =totalTableCount/_tableViewCount;
		pnTableList.removeAll();

		logger.debug("adv execute");
		manager.execute("adv");


		// 인덱스를 조회 하여 지정 하는 부분 필요
		for(int i=0;i<importTableList.size();i++)
		{
			KSGXLSImportPanel table=importTableList.get(i);
			JScrollPane scrollPane = new JScrollPane(table);
			TitledBorder createTitledBorder = BorderFactory.createTitledBorder(table.getTitle());
			createTitledBorder.setTitleFont(new Font("돋음",0,12));

			scrollPane.setBorder(createTitledBorder);
			pnTableList.add(scrollPane);
		}

		this.updateUI();
	}

	public void setSelectedIndex(int index) {
		pnTab.setSelectedIndex(index);

	}

	@Override
	public void componentShown(ComponentEvent e) {

		//		tree1.update();
		callApi("aDVManageUI.init");
	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		boolean success = (boolean) result.get("success");

		if(success)
		{
			String serviceId = (String) result.get("serviceId");

			if("aDVManageUI.init".equals(serviceId)) {				


				viewModel = (CommandMap) result.clone();

				tree.loadModel(viewModel);


			}
			else if("shipperTableMgtUI2.fnSearch".equals(serviceId)) {



			}

			else if("deleteSchedule".equals(serviceId)) {


			}
		}
		else{  
			String error = (String) result.get("error");
			JOptionPane.showMessageDialog(this, error);
		}
	}



}


