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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.ParseException;
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
import javax.swing.JPanel;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ksg.commands.SearchPortCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.TablePort;
import com.ksg.service.ADVServiceImpl;
import com.ksg.service.TableService;
import com.ksg.service.TableServiceImpl;
import com.ksg.view.comp.CurvedBorder;
import com.ksg.view.comp.FileInfo;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableImpl;
import com.ksg.view.comp.tree.KSGTree;
import com.ksg.view.comp.tree.KSGTreeDefault;
import com.ksg.view.comp.tree.KSGTreeImpl;
import com.ksg.workbench.adv.comp.SheetModel;
import com.ksg.workbench.adv.dialog.AdjestADVListDialog;
import com.ksg.workbench.adv.dialog.ViewXLSFileDialog;
import com.ksg.workbench.print.PrintADVUI;
import com.ksg.workbench.shippertable.dialog.AddTableInfoDialog;


/**

  * @FileName : ADVManageUI.java

  * @Project : KSG2

  * @Date : 2021. 7. 7. 

  * @작성자 : 박창현

  * @변경이력 :

  * @프로그램 설명 : 광고정보 자동 입력 화면

  */
@SuppressWarnings("unchecked")
public class ADVManageUI extends KSGPanel implements ActionListener
{	
	
	private static final Logger logger = LoggerFactory.getLogger(ADVManageUI.class);
	private static int _tableViewCount = 10;
	
	private static final String SEARCH_TYPE_COMPANY = "선사";
	
	private static final String SEARCH_TYPE_PAGE = "페이지";
	
	private static final long serialVersionUID = 1L;
	
	private static KSGTreeDefault tree1;
	
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
	
	private JPanel 			pnSubSearch,pnSubSelect,pnTableInfo,pnTableList,pnLeftMenu;
	
	private JTabbedPane 	pnTab;
	
	private Vector 			resultA = new Vector();

	private int 			resultOK,currentPage,resultCancel,startTableIndex,totalTableCount;

	private String 			searchType=SEARCH_TYPE_COMPANY;
	
	private String			selectedInput="File";
	
	private ADVListPanel advListPanel;

	private TablePort tablePort;
	
	private TableService tableService;
	private SearchPanel searchPanel;
	
	public ADVManageUI() {

		Properties properties = new Properties();

		this.setName("ADVManageUI");
		
		tableService = new TableServiceImpl();
		
		_advService = new ADVServiceImpl();
		
		manager.addObservers(this);
		
		createAndUpdateUI();
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
			AddTableInfoDialog addTableInfoDialog = new AddTableInfoDialog(this,manager.selectedCompany);
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
		
		JPanel pnMain = new JPanel(new BorderLayout());
		
		pnMain.add(pnTab);
		pnMain.add(buildLeftMenu(),BorderLayout.WEST);

		return pnMain;
	}



	private JPanel buildHistoryCenter()
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		JPanel pnNorth = new JPanel(new BorderLayout());
		JLabel lblTableCountlbl =new JLabel("검색된 테이블 수 : ");


		butAdjust = new JButton("위치조정");
		butAdjust.addActionListener(this);
		butAdjust.setEnabled(false);
		
/*		JButton butReLoad = new JButton("다시 불러오기");
		butReLoad.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				//initInfo();
				if(selectedInput.equals("File"))
				{
				//	actionImportADVInfo();
				}else
				{
					//importADVTextInfoAction();
				}
			}});
*/

		JPanel pnNorthLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnNorthRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

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
		JPanel pnTableList = createPnTableList();

		pane.setTopComponent(pnTableList);
		pnMain.add(pane);
		pnMain.add(pnNorth,BorderLayout.NORTH);
		pnMain.add(buildSouthPn(),BorderLayout.SOUTH);
		return pnMain;
	}
	private JPanel buildLeftMenu() 
	{
		pnLeftMenu = new JPanel();
		JPanel pnSearch =  new JPanel();
		pnSearch.setLayout(new BorderLayout());

		_treeMenu = createTreeMenu();		
		_txfSearchByCompany = new JTextField(8);


		JPanel pnSearchByCompany = new JPanel();
		JLabel lblCompany = new JLabel("선사 검색");
		lblCompany.setPreferredSize( new Dimension(60,15));
		pnSearchByCompany .add(lblCompany);
		pnSearchByCompany .add(_txfSearchByCompany);
		_txfSearchByCompany.addKeyListener(new KeyAdapter(){
		public void keyPressed(KeyEvent e) 
		{
			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				String text=_txfSearchByCompany.getText();
				if(!isPageSearch)

				{
					DefaultMutableTreeNode node = KSGTree.searchNodeByCompany(tree1,text);
					if(node!=null)
					{
						TreeNode[] nodes = ((DefaultTreeModel)tree1.getModel()).getPathToRoot(node);
						TreePath path = new TreePath(nodes);
						tree1.scrollPathToVisible(path);
						tree1.setSelectionPath(path);
					}else
					{
						JOptionPane.showMessageDialog(null, "해당선사가 없습니다.");
						_txfSearchByCompany.setText("");
					}
					_txfSearchByCompany.setText("");
				}else
				{
					try{
						int page= Integer.parseInt(text);
						DefaultMutableTreeNode node = KSGTree.searchNodeByPage(tree1,page);
						if(node!=null)
						{
							TreeNode[] nodes = ((DefaultTreeModel)tree1.getModel()).getPathToRoot(node);
							TreePath path = new TreePath(nodes);
							tree1.scrollPathToVisible(path);
							tree1.setSelectionPath(path);
							_txfSearchByCompany.setText("");
						}else
						{
							JOptionPane.showMessageDialog(null, "해당 Page가 없습니다.");
							_txfSearchByCompany.setText("");
						}
					}catch (NumberFormatException ee) {
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, text+" <== 정확한 숫자를 입력하세요");
//						ee.printStackTrace();
						logger.error(ee.getMessage());
						_txfSearchByCompany.setText("");
					}
				}

			}
		}
		});
		JCheckBox box = new JCheckBox(SEARCH_TYPE_PAGE,true);
		box.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box =(JCheckBox) e.getSource();
				isPageSearch=box.isSelected();

			}});

		pnSearchByCompany.add(box);

		pnSearch.add(pnSearchByCompany);

		pnLeftMenu.setLayout(new BorderLayout());


		JPanel pnContorl = new JPanel();
		ButtonGroup group = new ButtonGroup();


		JRadioButton button = new JRadioButton("선사별");
		JRadioButton button1 = new JRadioButton("페이지별",true);
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
					logger.debug("selected "+te);
					if(te.equals("선사별"))
					{
						tree1.setGroupBy(KSGTreeImpl.GroupByCompany);
					}
					else if(te.equals("페이지별"))
					{
						tree1.setGroupBy(KSGTreeImpl.GroupByPage);
					}
					manager.execute(tree1.getName());
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

			JPanel pnTitle = new JPanel();
			pnTitle.setBackground(new Color(88,141,250));
			pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));
			JLabel label = new JLabel("테이블 목록");
			label.setForeground(Color.white);
			pnTitle.add(label);
			pnTitle.setPreferredSize( new Dimension(0,22));

			pnSearch.add(pnSearchByCompany,BorderLayout.NORTH);
			pnSearch.add(new JScrollPane(_treeMenu),BorderLayout.CENTER);
			pnSearch.add(pnContorl,BorderLayout.SOUTH);


			JPanel pnMain = new JPanel(new BorderLayout());

			pnMain.add(pnSearch,BorderLayout.CENTER);

			pnLeftMenu.add(pnMain);
			pnLeftMenu.add(KSGDialog.createMargin(10, 0),BorderLayout.WEST);

			return pnLeftMenu;
	}





	/**
	 * @return
	 */
	private Component buildSouthPn() {

		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		JPanel paRight = new JPanel();
		paRight.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton butADD = new JButton("광고불러오기",new ImageIcon("images/importxls.gif"));
		butADD.setToolTipText("광고정보추가");
		butADD.setPreferredSize(new Dimension(100,20));
		butADD.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {


			}});
		/*JButton butSave = new JButton("광고정보저장",new ImageIcon("images/save.gif"));

		butSave.setToolTipText("광고정보저장");
		butSave.setActionCommand("광고정보저장");
		butSave.setPreferredSize(new Dimension(150,25));

		butSave.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) 
			{
				try{
					if(txfImportDate.getText().length()<=0)
					{
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "날짜입력:");	
						return;
					}

					List tableli = new LinkedList();

					if(searchType.equals(SEARCH_TYPE_COMPANY)){
						
						int pageRow = pageLi.getModel().getSize();
						
						DefaultListModel model = (DefaultListModel)pageLi.getModel();
						
						for(int i=0;i<pageRow;i++)
						{
							PageInfo info=(PageInfo) model.get(i);
							
							if(info.isSelected())
							{
								ShippersTable stable = new ShippersTable();
								
								stable.setPage(Integer.parseInt(info.chekInfo.toString()));
								
								stable.setCompany_abbr(_txfCompany.getText());
								
								List templi=tableService.selectTableInfoList(stable);
								
								for(int j=0;j<templi.size();j++)
								{
									ShippersTable table = (ShippersTable) templi.get(j);
									tableli.add(table);
								}
							}

						}
					}else
					{
						int pageRow = companyLi.getModel().getSize();
						for(int i=0;i<pageRow;i++)
						{
							DefaultListModel model = (DefaultListModel) companyLi.getModel();

							PageInfo info=(PageInfo) model.get(i);
							if(info.isSelected())
							{
								ShippersTable stable = new ShippersTable();
								stable.setPage(Integer.parseInt(_txfCPage.getText()));
								stable.setCompany_abbr(info.chekInfo.toString());
								List templi=tableService.selectTableInfoList(stable);
								for(int j=0;j<templi.size();j++)
								{
									ShippersTable table = (ShippersTable) templi.get(j);
									tableli.add(table);
								}
							}
						}
					}

					//saveAction(tableli);


				}catch(Exception es)
				{
					JOptionPane.showMessageDialog(null, "등록실패:"+es.getMessage());
					es.printStackTrace();
				}

			}});
*/
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

		JPanel pnLeft =new JPanel();
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
		JLabel lblTitle = new JLabel("광고정보 입력");
		
		lblTitle.setForeground(new Color(61,86,113));
		
		Font titleFont = new Font("명조",Font.BOLD,18);
		
		lblTitle.setFont(titleFont);

		JPanel pnTitleMain =new JPanel(new BorderLayout());
		
		JPanel pnTitle =new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		pnTitle.setBorder(new CurvedBorder(8,new Color(179,195,207)));
		
		pnTitle.add(lblTitle);

		pnTitleMain.add(pnTitle);
		
		JPanel pnTitleBouttom = new JPanel();
		
		pnTitleBouttom.setPreferredSize(new Dimension(0,15));
		
		pnTitleMain.add(pnTitleBouttom,BorderLayout.SOUTH);


		this.setLayout(new BorderLayout());

		JPanel pnMain = new JPanel();
		
		pnMain.setLayout(new BorderLayout());

		JPanel pnLeft = new JPanel();
		
		pnLeft.setPreferredSize(new Dimension(15,0));
		
		JPanel pnRight = new JPanel();
		
		pnRight.setPreferredSize(new Dimension(15,0));


		pnMain.add(pnRight,BorderLayout.EAST);
		
		pnMain.add(pnLeft,BorderLayout.WEST);
		
		pnMain.add(buildCenter(),BorderLayout.CENTER);
		
		this.add(pnTitleMain,BorderLayout.NORTH);
		
		this.add(pnMain,BorderLayout.CENTER);
	}

	private JPopupMenu createErrorPopupMenu() {
		JPopupMenu errorPopupMenu = new JPopupMenu();
		JMenuItem menu1 = new JMenuItem("포트검색");
		menu1.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) 
			{
				SearchPortCommand  portCommand = new SearchPortCommand();
				portCommand.execute();

			}});
		errorPopupMenu.add(menu1);
		return errorPopupMenu;
	}

	/**
	 * @return
	 */
	private JPanel createPnTableList() {
		JPanel pnTableListMain = new JPanel();
		pnTableListMain.setLayout(new BorderLayout());

		pnTableList = new JPanel();
		pnTableList.setLayout(new BoxLayout(pnTableList, BoxLayout.Y_AXIS));


		JPanel pnTableListControl = new JPanel();
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
		try {

			tree1.addTreeSelectionListener(new TreeSelectionListener(){

				public void valueChanged(TreeSelectionEvent e) {

					TreePath path=e.getNewLeadSelectionPath();
					if(path!=null)
					{

						try{
							searchPanel.updateViewByTree(path);
						}catch(Exception e2)
						{
							e2.printStackTrace();
							return;
						}
					}
				}
			});

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return tree1;
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
				AddTableInfoDialog addTableInfoDialog = new AddTableInfoDialog(ADVManageUI.this,manager.selectedCompany);
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



/*	*//**
	 * @param li
	 * @throws SQLException
	 * @throws ParseException
	 *//* 
	private void saveAction(List li) throws ParseException, SQLException {

		logger.info("start");
		resultA.removeAllElements();

		for(int i=0;i<importTableList.size();i++)
		{
			KSGXLSImportPanel xlsPn = importTableList.get(i);
			Vector portList=xlsPn.getPortList();
			for(int j=0;j<portList.size();j++)
			{
				PortColorInfo port=(PortColorInfo) portList.get(j);
				
				if(port.getArea_code()==null)
					continue;
				if(port.getArea_code().equals("-"))
				{
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, (xlsPn.getTableIndex()+1)+ "번 테이블의 ("+port.getPort_name()+") 항구 정보가 없음");
					return;
				}
			}
			Vector t=xlsPn.getNullVesselList();
			if(t.size()>0)
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, (xlsPn.getTableIndex()+1)+ "번 테이블에 ("+t.size()+")개의 등록 되지 않은 선박 명이 있습니다. ");
				return;
			}			
		}

		for(int i=0;i<importTableList.size();i++)
		{	
			KSGXLSImportPanel xlsPn = importTableList.get(i);
			Vector portList=xlsPn.getPortList();
			savePortList(xlsPn, portList);
			String insertData = new String();


			// 수정 필요

			insertData =xlsPn.getTableXMLInfo();

			logger.info("table info : \n"+insertData);

			ShippersTable tableinfo=(ShippersTable) li.get(i);

			ADVData data = new ADVData();

			data.setData(insertData);
			data.setDate_isusse(KSGDateUtil.toDate2(txfImportDate.getText()));
			//TODO 아이디 입력 부분 수정 
			data.setTable_id(tableinfo.getTable_id());
			data.setCompany_abbr(_txfCompany.getText());
			data.setPage(manager.selectedPage);

			data.setTable_id(tableinfo.getTable_id());
			
			advService.removeADVData(data.getTable_id());
			ADVData d=advService.insertADVData(data);
			if(d!=null)
			{
				tableService.updateTableDate(data);
				logger.info("update table");
			}
			else
			{
				System.out.println("");
			}
			
			KSGCommand command = new InsertADVCommand(data);
			command.execute();
		}
		JOptionPane.showMessageDialog(null, "광고정보를 저장했습니다.");
		logger.info("save");

	}
	*/

	/*private void savePortList(KSGXLSImportPanel xlsPn, Vector portList) {
		try {
			TablePort delPort = new TablePort();
			delPort.setTable_id(xlsPn.getTable_id());
			tableService.deleteTablePort(delPort);

			for(int j=0;j<portList.size();j++)
			{
				PortColorInfo port=(PortColorInfo) portList.get(j);
				if(port.getArea_code()==null)
					continue;
				tablePort = new TablePort();
				tablePort.setTable_id(xlsPn.getTable_id());
				tablePort.setPort_type("P");
				tablePort.setPort_index(port.getIndex());
				tablePort.setPort_name(port.getPort_name());
				tablePort.setParent_port(port.getPort_name());
				tableService.insertPortList(tablePort);
			}
		}catch(SQLException e)
		{

			if(e.getErrorCode()==2627)
			{
				e.printStackTrace();
			}else
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e.getMessage());
				e.printStackTrace();
			}
		}
	}*/

	public void setTabIndex(int i) {
		pnTab.getModel().setSelectedIndex(i);

	}
	public void update(KSGModelManager manager) 
	{
		
	}



/*	private void updatePropertyTable(String company_abbr, int page) {
		try {
			List li=tableService.getTableProperty(company_abbr,page);

			DefaultTableModel model = new DefaultTableModel();
			model.setRowCount(li.size());

			String[]colName = {"선사명","페이지","하위항구","Voyage추가여부","구분자","구분자 위치"};

			for(int i=0;i<colName.length;i++)
				model.addColumn(colName[i]);
			logger.debug("property :"+li.size());
			for(int i=0;i<li.size();i++)
			{
				Table_Property p=(Table_Property) li.get(i);
				model.setValueAt(p.getCompany_abbr(), i, 0);
				model.setValueAt(p.getPage(), i, 1);
				model.setValueAt(p.getUnder_port(), i, 2);

				if(p.getVoyage()==1)
				{
					model.setValueAt("추가", i, 3);
				}else
				{
					model.setValueAt("-", i, 3);
				}
				model.setValueAt(p.getVoyage(), i, 3);
				model.setValueAt(p.getVesselvoydivider(), i, 4);
				model.setValueAt(p.getVesselvoycount(), i, 5);
			}
			tblPropertyTable.setModel(model);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

/*	
	*//**
	 * 
	 *//*
	public void updateTableInfo()
	{
		try {
			List li = new LinkedList();
			if(tableInfoList==null)
				return;
			for(int i=0;i<tableInfoList.size();i++)
			{
				ShippersTable t = (ShippersTable) tableInfoList.get(i);
				List  tempLI=tableService.getTableListByCompany(_txfCompany.getText(), t.getPage());
				for(int j=0;j<tempLI.size();j++)
				{
					li.add(tempLI.get(j));
				}
			}

			String columnames[]={"Tabel ID",SEARCH_TYPE_PAGE,"Title","Table Index","Index","항구 수","선박 수","기타"};
			DefaultTableModel model = new EditTableModel(columnames,li.size());

			int index=1;

			for(int i=0;i<li.size();i++)
			{
				int col=0;
				ShippersTable table=(ShippersTable) li.get(i);
				model.setValueAt(table.getTable_id(), i, col++);
				model.setValueAt(table.getPage(), i, col++);
				model.setValueAt(table.getTitle(), i, col++);
				model.setValueAt(table.getTable_index(), i, col++);
				model.setValueAt(index, i, col++);
				model.setValueAt(table.getPort_col(), i, col++);
				model.setValueAt(table.getVsl_row(), i, col++);
				model.setValueAt(table.getOthercell(), i, col++);
				index++;
			}
			_tblTable.setModel(model);
			TableColumnModel colmodel_width =_tblTable.getColumnModel();

			colmodel_width.getColumn(2).setPreferredWidth(200);

			model.addTableModelListener(new TableModelListener(){

				public void tableChanged(TableModelEvent e) {
					if(e.getType()==TableModelEvent.UPDATE)
					{
						int row = _tblTable.getSelectedRow();
						ShippersTable table = new ShippersTable();
						table.setTable_id((String) _tblTable.getValueAt(row, 0));
						table.setPage(Integer.parseInt(_tblTable.getValueAt(row, 1).toString()));

						table.setTable_index(Integer.parseInt(_tblTable.getValueAt(row, 3).toString()));
						table.setPort_col(Integer.parseInt(_tblTable.getValueAt(row, 5).toString()));

						table.setVsl_row(Integer.parseInt(_tblTable.getValueAt(row, 6).toString()));


						table.setOthercell(Integer.parseInt(_tblTable.getValueAt(row, 7).toString()));
						try {
							tableService.updateTable(table);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}});

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/


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

/*	private void updateViewByTree(TreePath path) {

		String selectedCompany = path.getLastPathComponent().toString();

		switch (path.getPathCount()) {
		case 1: // root 선택시


			manager.selectedCompany=null;

			manager.selectedPage=-1;
			_txfCompany.setText("");
			_txfPage.setText("");

			lblSelectedCompanyName.setText("");
			lblSelectedPage.setText("");
			DefaultListModel listModel1 = new DefaultListModel();
			pageLi.setModel(listModel1);
			companyLi.setModel(listModel1);

			//test
			butCompanyAdd.setEnabled(false);
			lblSelectedCompanyName.setText("");
			lblSelectedPage.setText("");

			break;
		case 2: // 0~9 선택시
			manager.selectedCompany=null;

			manager.selectedPage=-1;
			_txfCompany.setText("");
			_txfPage.setText("");
			_txfCPage.setText("");
			_txfPCompany.setText("");
			DefaultListModel listModel2 = new DefaultListModel();
			pageLi.setModel(listModel2);
			companyLi.setModel(listModel2);

			//test
			butCompanyAdd.setEnabled(false);
			lblSelectedCompanyName.setText("");
			lblSelectedPage.setText("");
			break;
		case 3: // 0~9 선택시
			StringTokenizer st = new StringTokenizer(selectedCompany,":");

			String com = new String();
			String page = new String();

			page=st.nextToken();
			com = st.nextToken();

			_txfCompany.setText(com);
			_txfPage.setText(page);
			_txfCPage.setText(page);
			_txfPCompany.setText(com);

			try {
				if(searchType.equals(SEARCH_TYPE_COMPANY))
				{
					List li=tableService.getTablePageListByCompany(com);
					DefaultListModel listModel = new DefaultListModel();
					int row=0;
					for(int i=0;i<li.size();i++)
					{
						ShippersTable tableInfo = (ShippersTable) li.get(i);

						PageInfo info = new PageInfo(tableInfo.getPage());

						if(Integer.parseInt(page)==tableInfo.getPage())
						{
							info.setSelected(true);
							row=i;
						}
						if(isSamePageSelect)
						{
							info.setSelected(true);
						}

						listModel.addElement(info);
					}
					pageLi.setModel(listModel);
					pageLi.setVisibleRowCount(row);
					searchPanel.updateTableInfo2(listModel);
					logger.debug("searched Page size:"+listModel.size());

				}else
				{
					List li=tableService.getTableCompanyListByPage(Integer.parseInt(page));
					DefaultListModel listModel = new DefaultListModel();
					for(int i=0;i<li.size();i++)
					{
						ShippersTable tableInfo = (ShippersTable) li.get(i);

						PageInfo info = new PageInfo(tableInfo.getCompany_abbr());

						if(com.equals(info.getText()))
						{
							info.setSelected(true);
						}
						listModel.addElement(info);
					}

					companyLi.setModel(listModel);
					searchPanel.updateTableInfo2(listModel);

				}


				updatePropertyTable(com,Integer.parseInt(page));


				//test
				butCompanyAdd.setEnabled(true);
				lblSelectedPage.setText(page);
				lblSelectedCompanyName.setText(com);

			} catch (SQLException e) {
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e.getMessage());
				e.printStackTrace();
			}
			manager.selectedCompany=com;

			manager.selectedPage=Integer.parseInt(page);
			break;
		default:
			break;
		}
	}*/
}


