/*******************************************************************************
f * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.view.search;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.jdom.Element;

import com.ksg.commands.DelTableCommand;
import com.ksg.commands.SearchSubTableCommand;
import com.ksg.dao.DAOManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.model.KSGModelManager;
import com.ksg.view.base.dialog.UpdateTableInfoDialog;
import com.ksg.view.comp.CurvedBorder;
import com.ksg.view.comp.KSGPanel;
import com.ksg.view.comp.KSGTableSelectListner;
import com.ksg.view.comp.KSGTree1;
import com.ksg.view.comp.KSGTreeDefault;
import com.ksg.view.comp.UpdateTablePanel;
import com.ksg.view.search.dialog.AddTableInfoDialog;
import com.ksg.view.search.dialog.ManagePortDialog;
import com.ksg.view.util.KSGDateUtil;
import com.ksg.view.util.ViewUtil;
/**
 * @author archehyun
 * @설명 광고 정보 조회 화면
 */
@SuppressWarnings("unchecked")
public class SearchUI extends KSGPanel implements ActionListener
{	
	/**
	 * @author archehyun
	 * @테이블에서의 마우스 동작
	 */
	class UpdateMouseAdapter extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) 
		{
			selectedshippersTable = getShipperTableBySelectedTable();
			if(selectedshippersTable==null)
				return;
			try {
				ShippersTable shippersTable= _tableService.getTableById(selectedshippersTable.getTable_id());

				manager.selectedTable=selectedshippersTable;
				manager.selectedTable_id=selectedshippersTable.getTable_id();
				manager.selectedPage = shippersTable.getPage();

				_txfInboundIn.setText(shippersTable.getIn_port());

				_txfInboundOut.setText(shippersTable.getIn_to_port());

				_txfOutboundIn.setText(shippersTable.getOut_port());

				_txfOutboundOut.setText(shippersTable.getOut_to_port());

				_txfPage.setText(String.valueOf(shippersTable.getPage()));

				_txfTable_id.setText(shippersTable.getTable_id());

				if(selectedshippersTable!=null)
				{
					_pnUpdateTable.setShipperTableData(shippersTable);
					if(dialog!=null)
						dialog.setShipperTableData(shippersTable);
				}

			} catch (SQLException e1) {

				e1.printStackTrace();
			}


			if(e.getClickCount()>1)
			{				
				updateADVTable(e);
				logger.debug("click:"+e.getClickCount()+",selectedPage:"+manager.selectedPage);
			}
		}
	}
	//TODO 테이블 스타일 정리
	private static final int _LEFT_SIZE = 250;

	public static final int ADV_TYPE = 0;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String STRING_ERROR_NO_ADV_INFO = "광고정보가 없습니다. 광고 정보를 추가 하시겠습니까?";

	public static final int TABLE_TYPE = 1;

	private static KSGTreeDefault tree2;

	private JPanel 				_pnADVInfo, _pnleftMenu, _pnShipperInfo,_pnTable;	

	private Box 				_pnSearchInfoMain;

	private UpdateTablePanel 	_pnUpdateTable;

	private JPopupMenu 			_popupMenu;	

	private SearchTable tblSearchTable;

	private JTable				_currentTable;

	private JTree				_treeMenu;

	private JTextField 			_txfInboundIn,_txfInboundOut,_txfOutboundIn,_txfOutboundOut,txfSearchInput;

	private JTextField 			txfPageSearchInput;

	private JTextField 			txfPageIndexSearchInput;

	private AddTableInfoDialog addTableInfoDialog;

	private UpdateTableInfoDialog 	dialog = null;

	private KSGADVTablePanel advTablePanel;// 광고정보 편집화면

	private JToggleButton butEdit; //

	private boolean 			close=true;

	private String orderParam;

	private Font defaultFont = new Font("돋음",0,10);

	private JMenuItem delMenu;

	private int depth=1; //

	private final int DEPTH_PAGE=3; //

	private final int DEPTH_ROOT=1; //

	private final int DEPTH_SUB=2; //

	private UpdateDateDialog updateAllDateDialog; //날짜 일괄 수정 창

	private int endpage;

	private boolean 			isPageSearch=true;

	private JLabel lblCompany,lblCount;

	public KSGTableSelectListner listner;

	private Element rootElement;

	public List 				_searchedList;

	private int selectedRow;

	private ShippersTable 		selectedshippersTable;

	private JSplitPane spMain;

	private int startpage;

	private CardLayout 			tableLayout;

	private JTextField txfCompany,txfCount,_txfDate,txfDateSearch,_txfPage,_txfTable_id, txfImportDate;

	private JComboBox cbbOption,cbbGubun;

	private ShippersTable searchParam;

	DAOManager daoManager = DAOManager.getInstance();

	public SearchUI() 
	{
		_tableService = daoManager.createTableService();
		_advService= daoManager.createADVService();

		createAndUpdateUI();
	}
	public void actionPerformed(ActionEvent e) { 
		String command = e.getActionCommand();

		logger.debug("acction command:"+command);
		if(command.equals("테이블검색:Page"))
		{
			JDialog dialog = new JDialog();

			JPanel pnMain = new JPanel();

			final JTextField txfSearchInput = new JTextField(15);			

			JLabel lblSearchPage = new JLabel("Page : ");
			JButton butSubmit = new JButton("검색",new ImageIcon("images/buticon.gif"));
			butSubmit.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					try {

						ShippersTable shippersTable = new ShippersTable();						
						shippersTable.setPage(Integer.parseInt(txfSearchInput.getText()));
						tblSearchTable.setSearchParam(shippersTable);
						tblSearchTable.retrive();
						_searchedList = tblSearchTable.getSearchedList();
						logger.info(_searchedList.size());
					} catch (SQLException e1) {
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e1.getMessage());
						e1.printStackTrace();
					}
					catch (NumberFormatException e2) {
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e2.getMessage());
						e2.printStackTrace();
					}
				}});

			pnMain.add(lblSearchPage);
			pnMain.add(txfSearchInput);
			pnMain.add(butSubmit);


			dialog.getContentPane().add(pnMain);
			dialog.pack();
			ViewUtil.center(dialog);
			dialog.setVisible(true);
		}

		else if(command.equals("신규등록"))
		{
			selectedshippersTable = getShipperTableBySelectedTable();

			if(addTableInfoDialog==null||!addTableInfoDialog.isVisible())
			{
				addTableInfoDialog = new AddTableInfoDialog(this,selectedshippersTable);
				addTableInfoDialog.createAndUpdateUI();
			}


		}else if(command.equals("테이블삭제"))
		{
			delAction();

		}
		else if(command.equals("항구관리"))
		{
			updatePortAction();

		}
		else if(command.equals("일자변경"))
		{
			JLabel lblDate = new JLabel(" 입력날짜 : ");
			lblDate.setFont(defaultFont);

			final JTextField txfImportDate = new JTextField(8);

			JCheckBox cbxImportDate = new JCheckBox("월요일",false);
			cbxImportDate.setFont(defaultFont);
			cbxImportDate.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					JCheckBox bo =(JCheckBox) e.getSource();
					if(bo.isSelected())
					{
						txfImportDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
					}
				}
			});

			JDialog dialog  = new JDialog();
			dialog.setTitle("일자 변경(작업중)");
			JPanel pnMain = new JPanel();
			JPanel pnControl = new JPanel();
			pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
			JButton butOk = new JButton("확인");
			butOk.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) 
				{

				}});
			butOk.setFont(defaultFont);
			JButton butCancel = new JButton("취소");

			pnControl.add(butOk);
			pnControl.add(butCancel);			
			pnMain.add(lblDate);
			pnMain.add(txfImportDate);
			pnMain.add(cbxImportDate);
			dialog.getContentPane().add(pnMain);
			dialog.getContentPane().add(pnControl,BorderLayout.SOUTH);
			dialog.setSize(250,100);
			ViewUtil.center(dialog);
			dialog.setVisible(true);

		}

		else if(command.equals("광고삭제"))
		{
			delADVAction();
		}

		else if(command.equals("날짜일괄수정"))
		{
			updateAllDateDialog = new UpdateDateDialog(_searchedList);		
		}
	}

	/**
	 * 취소시 원래 검색 화면 표시
	 * @throws SQLException
	 */
	public void showTableList() throws SQLException {

		logger.info("seleted page:"+searchParam);

		_pnADVInfo.setVisible(false);		
		/*
		//_searchedList= _tableService.getTableList(_searchParam);

		_tblSubTotalTable.setSearchParam(searchParam);

		_tblSubTotalTable.retrive();

		_currentTable=_tblSubTotalTable;*/

		//searchSubTable();

		tableLayout.show(_pnTable, tblSearchTable.getName());
	}

	/**
	 * @return
	 */
	private JPanel buildCenter() 
	{
		_pnShipperInfo = new JPanel();
		_pnShipperInfo.setLayout(new BorderLayout());
		_pnTable = new JPanel();
		tableLayout = new CardLayout();
		_pnTable.setLayout(tableLayout);


		tblSearchTable = new SearchTable();

		tblSearchTable.addKeyListener(new KeyAdapter() {


			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_ENTER)
				{
					final JTable table = (JTable) e.getSource();
					int row = table.getSelectedRow();
					if(row==-1)
						return;
					final int page =(Integer)table.getValueAt(row, 0);					

					searchADVTable();
					_pnADVInfo.setVisible(true);
					tableLayout.show(_pnTable, advTablePanel.getName());
				}

			}

			public void keyReleased(KeyEvent arg0) {

				JTable table = (JTable) arg0.getSource();

				if(arg0.getKeyCode()== KeyEvent.VK_ENTER)
				{

					logger.debug("releas " +arg0.getKeyCode()+table.getSelectedColumn());


					int row = table.getSelectedRow();
					//int col = table.getSelectedColumn();

					if(row==-1)
						return;

					try {
						KSGDateUtil.toDate2(String.valueOf(tblSearchTable.getValueAt(row, 2)));
					} catch (ParseException e1) 
					{
						JOptionPane.showMessageDialog(null, "정확한 날짜를 입력하십시요");
						try {


							searchSubTable();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return;
					}
					try {
						String table_id = String.valueOf(table.getValueAt(row, 3));
						String date = String.valueOf(table.getValueAt(row, 2));

						_tableService.updateTableDate(table_id,date);

						_advService.updateDateADVData(table_id, date);

						switch (depth) {
						case DEPTH_SUB:
							_searchedList= _tableService.getTableListByPage(startpage,endpage);	
							searchSubTable();
							break;
						case DEPTH_PAGE:
							updateSubTable();	
							break;

						default:
							break;
						}

					} catch (SQLException e) {
						e.printStackTrace();
					} 
				}
			}
		});


		tblSearchTable.setComponentPopupMenu(createPopupMenu());
		tblSearchTable.setName("SearchTable");
		tblSearchTable.addMouseListener( new UpdateMouseAdapter());
		JScrollPane jScrollPane = new JScrollPane(tblSearchTable);
		jScrollPane.getViewport().setBackground(Color.white);

		_pnTable.add(jScrollPane,tblSearchTable.getName());

		advTablePanel = new KSGADVTablePanel(this);


		_pnTable.add(advTablePanel,advTablePanel.getName());

		JPanel pnSlideShow = cteateSlideShowPn();

		JPanel pnSouth = new JPanel(new BorderLayout());

		butEdit = new JToggleButton("편집(E)",new ImageIcon("images/editClose.gif"));
		butEdit.setMnemonic(KeyEvent.VK_E);
		butEdit.setFont(defaultFont);
		butEdit.setSelectedIcon(new ImageIcon("images/editOpen.gif"));

		butEdit.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JToggleButton but =(JToggleButton) e.getSource();
				ButtonModel model =but.getModel();

				changeUpdatePNState(model.isSelected());

			}});


		JButton butCreateADV = new JButton("광고 등록( + N)");
		butCreateADV.setFont(defaultFont);
		butCreateADV.setActionCommand("광고 등록");
		butCreateADV.addActionListener(this);


		JPanel pnRightControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		pnRightControl.add(butEdit);

		JPanel pnLeftControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butUpdateDate = new JButton("날짜일괄수정");
		butUpdateDate.addActionListener(this);

		pnLeftControl.add(butUpdateDate);
		pnSouth.add(pnRightControl, BorderLayout.EAST);
		pnSouth.add(pnLeftControl, BorderLayout.WEST);

		_pnUpdateTable = new UpdateTablePanel(this,null);
		_pnUpdateTable.setBorder(BorderFactory.createEtchedBorder());
		_pnUpdateTable.setVisible(false);
		_pnUpdateTable.setMinimumSize(new Dimension(200,0));
		_pnUpdateTable.setMaximumSize(new Dimension(300,0));


		JPanel pnTableMain = new JPanel();
		pnTableMain.setLayout(new BorderLayout());

		JPanel pnTableMainLeft = new JPanel();
		pnTableMainLeft.setPreferredSize(new Dimension(15,0));
		JPanel pnTableMainRight = new JPanel();
		pnTableMainRight.setPreferredSize(new Dimension(15,0));
		pnTableMain.add(pnTableMainLeft,BorderLayout.WEST);
		pnTableMain.add(pnTableMainRight,BorderLayout.EAST);

		pnTableMain.add(_pnTable,BorderLayout.CENTER);

		spMain = new JSplitPane();

		spMain.setLeftComponent(_pnUpdateTable);
		spMain.setRightComponent(pnTableMain);

		_pnShipperInfo.add(spMain,BorderLayout.CENTER);	
		_pnShipperInfo.add(pnSlideShow,BorderLayout.WEST);
		_pnShipperInfo.add(pnSouth,BorderLayout.SOUTH);


		_pnShipperInfo.add(createPnSearchInfo(),BorderLayout.NORTH);

		JPanel pnMain = new JPanel();

		pnMain.setLayout( new BorderLayout());
		pnMain.add(_pnShipperInfo,BorderLayout.CENTER);
		_pnleftMenu = buildLeftMenu();
		_pnleftMenu.setVisible(false);

		pnMain.add(_pnleftMenu,BorderLayout.WEST);
		pnMain.add(_pnShipperInfo,BorderLayout.CENTER);

		return pnMain;
	}

	/**
	 * @return
	 */
	private JPanel buildLeftMenu() 
	{
		JPanel pnMain = new JPanel();
		JPanel pnSearch =  new JPanel();
		pnSearch.setLayout( new BorderLayout());

		_treeMenu = createTreeMenu();

		pnMain.setLayout(new BorderLayout());

		pnMain.add(new JScrollPane(_treeMenu),BorderLayout.CENTER);
		pnMain.setPreferredSize(new Dimension(_LEFT_SIZE,100));
		JPanel panel = new JPanel();
		ButtonGroup group = new ButtonGroup();


		JRadioButton rbtCompany = new JRadioButton("선사별");
		rbtCompany.setFont(defaultFont);
		JRadioButton rbtPage = new JRadioButton("페이지별",true);
		rbtPage.setFont(defaultFont);
		group.add(rbtCompany);
		group.add(rbtPage);

		panel.add(rbtCompany);
		panel.add(rbtPage);

		ItemListener itemListener= new ItemListener(){

			public void itemStateChanged(ItemEvent e) {
				AbstractButton but = (AbstractButton) e.getSource();
				if(ItemEvent.SELECTED==e.getStateChange())
				{
					String te = but.getActionCommand();
					logger.debug("selected "+te);
					if(te.equals("선사별"))
					{
						tree2.setGroupBy(KSGTree1.GroupByCompany);
					}
					else if(te.equals("페이지별"))
					{
						tree2.setGroupBy(KSGTree1.GroupByPage);
					}
					manager.execute(tree2.getName());
				}
			}};
			rbtCompany.addItemListener(itemListener);
			rbtPage.addItemListener(itemListener);
			panel.add(new JSeparator(JSeparator.HORIZONTAL));
			JButton butADDTable = new JButton(new ImageIcon("images/plus.gif"));

			butADDTable.setPreferredSize(new Dimension(35,25));
			butADDTable.setFocusPainted(false);
			butADDTable.setActionCommand("신규등록");
			butADDTable.setToolTipText("신규 테이블 등록");
			butADDTable.addActionListener(this);

			panel.add(butADDTable);
			JButton butDelTable = new JButton(new ImageIcon("images/minus.gif"));
			butDelTable.setPreferredSize(new Dimension(35,25));
			butDelTable.setFocusPainted(false);
			butDelTable.setActionCommand("테이블삭제");
			butDelTable.addActionListener(this);
			panel.add(butDelTable);


			JPanel pnTitle = new JPanel();
			pnTitle.setBackground(new Color(88,141,250));


			pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));

			JLabel label = new JLabel("테이블 목록");

			label.setForeground(Color.white);

			pnTitle.add(label);
			pnTitle.setPreferredSize( new Dimension(0,22));


			pnMain.add(pnSearch,BorderLayout.NORTH);
			pnMain.add(panel,BorderLayout.SOUTH);

			JPanel pnArrow = new JPanel();
			JButton butA = new JButton("");
			pnArrow.setPreferredSize(new Dimension(15,0));
			pnArrow.add(butA);
			pnMain.setPreferredSize(new Dimension(220,0));


			return pnMain;
	}
	/**
	 * @param isSelected
	 */
	private void changeUpdatePNState(boolean isSelected)
	{
		if(isSelected)
		{
			spMain.setDividerLocation(340);

			_pnUpdateTable.setVisible(true);
			butEdit.setText("확인(E)");

		}
		else
		{
			spMain.setDividerLocation(0);
			_pnUpdateTable.setVisible(false);
			butEdit.setText("편집(E)");

		}
	}


	public void createAndUpdateUI() {

		logger.info("init searchUI start");

		this.setName("SearchUI");

		this.manager.addObservers(this);
		_popupMenu = createPopupMenu();

		JPanel pnTitleMain = new JPanel(new BorderLayout());
		JPanel pnTitle = new JPanel();
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEADING));
		JLabel label = new JLabel("광고정보 관리");
		label.setForeground(new Color(61,86,113));

		Font titleFont = new Font("명조",Font.BOLD,18);
		label.setFont(titleFont);
		pnTitle.add(label);


		pnTitle.setBorder(new CurvedBorder(8,new Color(91,152,185)));
		pnTitleMain.add(pnTitle);
		JPanel pnTitleBouttom = new JPanel();
		pnTitleBouttom.setPreferredSize(new Dimension(0,15));
		pnTitleMain.add(pnTitleBouttom,BorderLayout.SOUTH);

		this.setLayout(new BorderLayout());
		this.add(buildCenter(),BorderLayout.CENTER);	
		this.add(pnTitleMain,BorderLayout.NORTH);
		this.add(this.createMargin(10),BorderLayout.WEST);
		logger.info("init searchUI end");


	}



	/**
	 * 설명:
	 * @returnSD
	 */
	private JPanel createPnSearchInfo() {
		_pnSearchInfoMain = new Box(BoxLayout.Y_AXIS);

		_pnADVInfo = new JPanel();
		_pnADVInfo.setLayout(new FlowLayout(FlowLayout.LEADING));

		_txfOutboundIn = new JTextField(8);
		_txfInboundIn = new JTextField(8);
		_txfOutboundOut = new JTextField(8);		
		_txfInboundOut = new JTextField(8);

		_txfOutboundIn.setEditable(false);
		_txfOutboundOut.setEditable(false);
		_txfInboundIn.setEditable(false);
		_txfInboundOut.setEditable(false);

		JPanel pnTableInfo = new JPanel();
		pnTableInfo.setLayout(new BorderLayout());

		_txfPage = new JTextField(10);
		_txfPage.setEditable(false);
		_txfPage.setBorder(BorderFactory.createEtchedBorder());


		txfPageSearchInput = new JTextField(4);
		txfPageSearchInput.addKeyListener(new KeyAdapter() {

			public void keyTyped(KeyEvent e)
			{
				if(e.getKeyChar()==KeyEvent.VK_ENTER)
				{
					
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

				txfPageSearchInput.setForeground(Color.black);
				String str = txfPageSearchInput.getText();
				try{
					Integer.parseInt(str);

				}catch(NumberFormatException ee)
				{
					txfPageSearchInput.setForeground(Color.red);
				}

			}

		});
		txfPageIndexSearchInput = new JTextField(4);

		txfPageIndexSearchInput.addKeyListener(new KeyAdapter() {



			@Override
			public void keyReleased(KeyEvent e) {

				txfPageIndexSearchInput.setForeground(Color.black);
				String str = txfPageIndexSearchInput.getText();
				try{
					Integer.parseInt(str);

				}catch(NumberFormatException ee)
				{
					txfPageIndexSearchInput.setForeground(Color.red);
				}

			}


		});

		_txfTable_id = new JTextField(14);
		_txfTable_id.setEditable(false);
		_txfTable_id.setBorder(BorderFactory.createEtchedBorder());

		txfCompany = new JTextField(10);
		txfCompany.setEditable(false);

		_txfDate = new JTextField(8);
		_txfDate.setEditable(false);
		_txfDate.setBorder(BorderFactory.createEmptyBorder());

		txfCount = new JTextField(2);
		txfCount.setEditable(false);

		lblCompany = new JLabel();
		lblCompany.setFont(new Font("돋움",0,15));

		Box bo = new Box(BoxLayout.Y_AXIS);


		JPanel pnMain1 = new JPanel();		
		pnMain1.setLayout(new BorderLayout());

		JPanel pnLogo = new JPanel();
		pnLogo.setLayout( new FlowLayout(FlowLayout.LEFT));

		lblCompany.setIcon(new ImageIcon("images/table.png"));
		pnLogo.add(lblCompany);

		JPanel pnSearchMain = new JPanel();
		pnSearchMain.setLayout(new BorderLayout());
		JLabel lblDateSearch = new JLabel("  입력일자: ");
		txfDateSearch = new JTextField(10);
		txfDateSearch.addKeyListener(new KeyAdapter(){

			public void keyReleased(KeyEvent e) 
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					String date=txfDateSearch.getText();

					try {

						if(KSGDateUtil.isDashFomatt(date))
						{
							ShippersTable OP = new ShippersTable();
							OP.setDate_isusse(KSGDateUtil.toDate3(date).toString());
							_searchedList=_tableService.getTableListByDate(OP);

						}else
						{
							JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "입력 형식(2000.1.1) 이 틀렸습니다.");
							txfDateSearch.setText("");
							return;
						}

						_currentTable= tblSearchTable;
						searchSubTable();
						lblCount.setText(String.valueOf(_searchedList.size()));
						tableLayout.show(_pnTable, tblSearchTable.getName());
						txfDateSearch.setText("");
					} catch (SQLException e1) {
						e1.printStackTrace();
					} catch (ParseException e2) {
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "입력 형식(2000.1.1) 이 틀렸습니다.");
						txfDateSearch.setText("");
						return;
					}
				}
			}

		});
		Icon warnIcon = new ImageIcon("images/search.png");

		JButton butDateSearch = new JButton(warnIcon);
		butDateSearch.setText("조회");
		butDateSearch.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e) 
			{
				String param=txfSearchInput.getText();
				logger.debug("input parm:"+param);
				searchByOption(param);
			}

		});


		cbbOption = new JComboBox();
		//cbbOption.addItem("페이지");
		cbbOption.addItem("테이블ID");

		//cbbOption.addItem("인덱스");
		cbbOption.addItem("선사명");
		cbbOption.addItem("제목");
		cbbOption.addItem("에이전트");
		cbbOption.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JComboBox ss = (JComboBox) e.getSource();

				String target=(String) ss.getSelectedItem();
				logger.debug(target+","+ss.getSelectedIndex());

			}});
		txfSearchInput = new JTextField(10);
		txfSearchInput.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					String param=txfSearchInput.getText();
					logger.debug("key input:"+param);
					searchByOption(param);
				}
			}
		});

		JCheckBox box = new JCheckBox("페이지",true);
		box.setFont(defaultFont);
		box.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box =(JCheckBox) e.getSource();
				isPageSearch=box.isSelected();

			}});

		cbbGubun = new JComboBox();
		cbbGubun.addItem("전체");
		cbbGubun.addItem(ShippersTable.GUBUN_NORMAL);
		cbbGubun.addItem(ShippersTable.GUBUN_NNN);
		cbbGubun.addItem(ShippersTable.GUBUN_TS);
		cbbGubun.addItem(ShippersTable.GUBUN_CONSOLE);
		cbbGubun.addItem(ShippersTable.GUBUN_INLAND);
		cbbGubun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) 
			{

				try {
					searchByGubun();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});		

		JPanel pnSearchMainDown = new JPanel();
		pnSearchMainDown.setLayout(new FlowLayout(FlowLayout.RIGHT));

		pnSearchMainDown.add(new JLabel("테이블 구분: "));
		pnSearchMainDown.add(cbbGubun);		
		pnSearchMainDown.add(new JLabel("  페이지: "));
		pnSearchMainDown.add(txfPageSearchInput);
		pnSearchMainDown.add(new JLabel("  인덱스: "));
		pnSearchMainDown.add(txfPageIndexSearchInput);

		pnSearchMainDown.add(new JLabel("  항목: "));
		pnSearchMainDown.add(cbbOption);
		pnSearchMainDown.add(txfSearchInput);
		pnSearchMainDown.add(lblDateSearch);
		pnSearchMainDown.add(txfDateSearch);
		pnSearchMainDown.add(butDateSearch);


		pnSearchMain.add(createSearchPanel(),BorderLayout.NORTH);
		pnSearchMain.add(pnSearchMainDown,BorderLayout.SOUTH);


		pnMain1.add(pnLogo,BorderLayout.WEST);
		pnMain1.add(pnSearchMain);


		bo.add(pnMain1);
		JPanel pnLeft = new JPanel();
		pnLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("페이지 : ",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		label.setForeground(Color.blue.brighter());
		label.setFont(defaultFont);
		pnLeft.add(label);
		pnLeft.add(_txfPage);		

		_txfPage.setForeground(Color.blue);
		JLabel lbl = new JLabel("테이블 ID : ",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		lbl.setForeground(Color.blue.brighter());
		lbl.setFont(defaultFont);

		_txfTable_id.setForeground(Color.blue.brighter());
		pnLeft.add(lbl);
		pnLeft.add(_txfTable_id);

		JPanel pnright = new JPanel();
		pnright.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblCount = new JLabel();
		lblCount.setForeground(new Color(171,126,111));
		pnright.add(lblCount);
		JLabel label2 = new JLabel("개 항목");
		pnright.add(label2);

		pnTableInfo.add(pnLeft,BorderLayout.WEST);
		pnTableInfo.add(pnright,BorderLayout.EAST);
		bo.add(pnTableInfo);

		JLabel lblOutboundIn = new JLabel("Outbound국내항:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		lblOutboundIn.setFont(defaultFont);
		_pnADVInfo.add(lblOutboundIn);
		_pnADVInfo.add(_txfOutboundIn);
		JLabel lblOutboundOut = new JLabel("Outbound외국항:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		lblOutboundOut.setFont(defaultFont);
		_pnADVInfo.add(lblOutboundOut);
		_pnADVInfo.add(_txfOutboundOut);
		JLabel lblInboundIn = new JLabel("Inbound국내항:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		lblInboundIn.setFont(defaultFont);
		_pnADVInfo.add(lblInboundIn);		
		_pnADVInfo.add(_txfInboundIn);
		JLabel lblInboundOut = new JLabel("Inbound외국항:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		lblInboundOut.setFont(defaultFont);
		_pnADVInfo.add(lblInboundOut);
		_pnADVInfo.add(_txfInboundOut);

		_pnSearchInfoMain.add(bo);

		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		_pnSearchInfoMain.add(pnS);
		_pnSearchInfoMain.add(pnS1);
		_pnSearchInfoMain.add(_pnADVInfo);

		_pnADVInfo.setVisible(false);

		JPanel pnMain = new JPanel();
		pnMain.setLayout( new BorderLayout());
		pnMain.add(this.createMargin(),BorderLayout.WEST);
		pnMain.add(this.createMargin(),BorderLayout.EAST);
		pnMain.add(_pnSearchInfoMain,BorderLayout.CENTER);
		return pnMain;
	}

	/**
	 * 팝업 메뉴 생성
	 * @return
	 */
	private JPopupMenu createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem itemUpdate = new JMenuItem("항구관리");
		itemUpdate.setActionCommand("항구관리");
		itemUpdate.addActionListener(this);

		JMenuItem itemDateUpdate = new JMenuItem("일자변경");
		itemDateUpdate.setActionCommand("일자변경");
		itemDateUpdate.addActionListener(this);

		JMenuItem itemDel = new JMenuItem("테이블삭제");
		itemDel.addActionListener(this);

		JMenuItem itemADVDel = new JMenuItem("광고삭제");
		itemADVDel.addActionListener(this);


		JCheckBoxMenuItem boxMenuItem = new JCheckBoxMenuItem("Scroll 표시",true);
		boxMenuItem.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
				JCheckBoxMenuItem m =(JCheckBoxMenuItem) arg0.getSource();
				if(m.isSelected())
				{
					tblSearchTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					//_tblTotalTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				}else
				{
					tblSearchTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
					//_tblTotalTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				}
			}
		});

		popupMenu.add(itemUpdate);

		popupMenu.add(itemDateUpdate);
		popupMenu.addSeparator();
		popupMenu.add(itemDel);
		popupMenu.add(itemADVDel);
		popupMenu.addSeparator();
		popupMenu.add(boxMenuItem);
		popupMenu.addSeparator();
		return popupMenu;
	}

	/**
	 * @return
	 */
	private JPanel createSearchPanel() {
		JPanel pnSearchByCompany = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		/*JLabel lblSearchCompany = new JLabel("테이블 구분");

		cbbGubun = new JComboBox();
		cbbGubun.addItem("전체");
		cbbGubun.addItem(ShippersTable.GUBUN_NORMAL);
		cbbGubun.addItem(ShippersTable.GUBUN_NNN);
		cbbGubun.addItem(ShippersTable.GUBUN_TS);
		cbbGubun.addItem(ShippersTable.GUBUN_CONSOLE);
		cbbGubun.addItem(ShippersTable.GUBUN_INLAND);
		cbbGubun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) 
			{

				try {
					searchByGubun();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});


		pnSearchByCompany .add(lblSearchCompany);
		pnSearchByCompany .add(cbbGubun);*/

		return pnSearchByCompany;
	}

	/**
	 * 좌측 트리 메뉴 생성
	 * @return
	 */
	private JTree createTreeMenu() 
	{
		tree2 = new KSGTreeDefault("tree1");
		tree2 .setFont(defaultFont);
		tree2.setComponentPopupMenu(createTreePopupmenu());
		try {

			tree2.addTreeSelectionListener(new TreeSelectionListener(){

				public void valueChanged(TreeSelectionEvent e) {

					TreePath path=e.getNewLeadSelectionPath();
					if(path!=null)
					{

						try{
							updateViewByTree(path);

						}catch(Exception e2)
						{
							e2.printStackTrace();
							return;
						}
					}

				}});


		} catch (Exception e1) {
			e1.printStackTrace();			

		}
		return tree2;
	}

	/**
	 * @return
	 */
	private JPopupMenu createTreePopupmenu() {
		JPopupMenu menu = new JPopupMenu();


		JMenuItem itemTable = new JMenuItem("새 테이블...");
		itemTable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				AddTableInfoDialog addTableInfoDialog = new AddTableInfoDialog(SearchUI.this,manager.selectedCompany);
				addTableInfoDialog.createAndUpdateUI();
			}});
		delMenu = new JMenuItem("테이블 삭제");

		delMenu.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				delAction();
			}});	

		menu.add(itemTable);
		menu.add(delMenu);

		return menu;
	}

	/**
	 *  좌측 슬라이드 버튼 작성
	 * @return
	 */
	private JPanel cteateSlideShowPn() {
		JPanel pn = new JPanel();
		pn.setLayout(new BorderLayout());
		pn.setPreferredSize(new Dimension(25,0));
		final JButton button = new JButton(new ImageIcon("images/right_arrow_.gif"));

		button.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) {
				if(close)
				{
					_pnleftMenu.setPreferredSize(new Dimension(_LEFT_SIZE,0));
					_pnleftMenu.setVisible(true);
					close = false;
					button.setIcon(new ImageIcon("images/left_arrow_.gif"));
				}else
				{
					_pnleftMenu.setPreferredSize(new Dimension(0,0));
					_pnleftMenu.setVisible(false);
					close = true;
					button.setIcon(new ImageIcon("images/right_arrow_.gif"));
				}

			}});
		pn.add(button,BorderLayout.CENTER);
		return pn;
	}

	/**
	 * 
	 */
	private void delAction() {

		ShippersTable table= getShipperTableBySelectedTable();

		DelTableCommand delTableCommand = new DelTableCommand(table,this);

		int result=delTableCommand.execute();



		KSGModelManager.getInstance().execute(this.getName());

	}

	/**
	 * 광고정보 삭제
	 */
	private void delADVAction() {

		ShippersTable table= getShipperTableBySelectedTable();

		if(table==null)
		{
			JOptionPane.showMessageDialog(null, "테이블 정보를 선택하십시요");
			return ;
		}


		int j=JOptionPane.showConfirmDialog(this ,table.getCompany_abbr()+"의"+ table.getTable_id()+
				" 광고정보를 삭제하시겠습니까?","광고 정보 삭제",JOptionPane.OK_CANCEL_OPTION);
		if(j==JOptionPane.YES_OPTION)
		{
			try {
				logger.debug("option result "+j);
				int result=_advService.removeADVData(table.getTable_id(), manager.selectedDate);
				logger.debug("del result "+result);
				if(result==1)
				{
					JOptionPane.showMessageDialog(this, "광고정보를 삭제했습니다.");
					ADVData data = new ADVData();
					data.setTable_id(manager.selectedTable_id);
					data.setCompany_abbr(table.getCompany_abbr());
					_tableService.updateTableDate(data);
					manager.execute(this.getName());
				}else if(result==0)
				{
					JOptionPane.showMessageDialog(null, "광고정보가 없습니다.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				if(e.getErrorCode()==1451)
					JOptionPane.showMessageDialog(null, e.getErrorCode()+","+"기존 광고까지 삭제하시겠습니까?");
			}
		}else
		{
			logger.debug("option result1 "+j);
		}
	}
	//TODO 수정 예정..전체 선택시 오류 발생
	/**
	 * @return
	 */
	private ShippersTable getShipperTableBySelectedTable() {
		int row=_currentTable.getSelectedRow();
		try{
			if(row!=-1)
			{
				ShippersTable shippersTable = new ShippersTable();

				shippersTable.setTable_id((String) _currentTable.getValueAt(row, 3));//
				manager.selectedDate = (String) _currentTable.getValueAt(row, 2);
				shippersTable.setCompany_abbr((String) _currentTable.getValueAt(row, 6));

				return shippersTable;
			}else
			{
				return null;
			}
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "error:"+_currentTable.getName()+", "+e.getMessage());
			return null;
		}
	}
	/**
	 * @param selectedCompany
	 */
	private void initNotify(String selectedCompany) {
		StringTokenizer st = new StringTokenizer(selectedCompany,":");

		String com = new String();
		String page = new String();

		page=st.nextToken();
		com = st.nextToken();
		lblCompany.setText("선사명 : "+com);

		txfCompany.setText(com);
		_txfPage.setText(page);
		manager.selectedCompany=com;
		manager.selectedPage=Integer.parseInt(page);
	}
	/**
	 * 광고정보 조회
	 * @param row
	 * @return
	 */
	public void searchADVTable() {

		try{			
			advTablePanel.retrive();

		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(SearchUI.this, "error : "+e.getMessage());
		}

		
	}
	/**
	 * @param colum
	 * @throws SQLException
	 */
	public void searchSubTable() throws SQLException {

		if(_searchedList!=null)
		{
			logger.debug("searchsubtable:"+_searchedList.size());

			SearchSubTableCommand searchSubTableCommand = new SearchSubTableCommand(_currentTable,_searchedList);

			searchSubTableCommand.execute();
		}
	}

	/**
	 * @param selectedCompany
	 * @throws SQLException
	 */
	private void selectPage(String selectedCompany) throws SQLException {
		depth =DEPTH_PAGE; 

		//_currentTable =_tblSubTotalTable;

		initNotify(selectedCompany);

		manager.selectedDate=null;

		updateSubTable();
		delMenu.setEnabled(true);
		tableLayout.show(_pnTable, tblSearchTable.getName());

		_txfDate.setText("");
		_txfTable_id.setText("");
	}

	/**
	 * @param selectedCompany
	 */
	private void selectRoot(String selectedCompany) {
		depth =DEPTH_ROOT; 
		_txfDate.setText("");
		txfCompany.setText("");
		_txfPage.setText("");
		_txfTable_id.setText("");

		manager.selectedCompany=null;
		manager.selectedDate=null;
		manager.orderBy="page";
		lblCompany.setText(selectedCompany);


		//KSGModelManager.getInstance().execute(_tblTotalTable.getName());


		delMenu.setEnabled(false);
		//_currentTable =_tblTotalTable;

		lblCount.setText(String.valueOf(tblSearchTable.getRowCount()));
		tableLayout.show(_pnTable, tblSearchTable.getName());
	}
	/**
	 * @param selectedCompany
	 * @throws SQLException
	 */
	private void selectSub(String selectedCompany) throws SQLException {
		depth =DEPTH_SUB; 
		_txfDate.setText("");
		txfCompany.setText("");
		_txfPage.setText("");
		_txfTable_id.setText("");
		manager.selectedCompany=null;
		manager.selectedDate=null;

		StringTokenizer st = new StringTokenizer(selectedCompany,"~");
		if(st.countTokens()==2)
		{

			startpage = Integer.parseInt(st.nextToken());
			endpage = Integer.parseInt(st.nextToken());
			_searchedList= _tableService.getTableListByPage(startpage,endpage);

			txfCount.setText(String.valueOf(_searchedList.size()));
			lblCount.setText(String.valueOf(_searchedList.size()));
			logger.debug("searchedList:"+_searchedList.size());

		}else
		{
			txfCount.setText(String.valueOf(0));				
		}
		lblCompany.setText("선사번호 : "+selectedCompany);
		searchSubTable();
		tableLayout.show(_pnTable, tblSearchTable.getName());
		delMenu.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see com.ksg.model.KSGObserver#update(com.ksg.model.KSGModelManager)
	 */
	public void update(KSGModelManager manager) {

		logger.debug("update Table");
		try {
			manager.execute(tree2.getName());
			if(manager.selectedCompany==null)			
			{
				tree2.setSelectionPath(tree2.getPathForRow(0));
			}
			manager.execute(tblSearchTable.getName());
			updateSubTable();

		} catch (SQLException e) {

			e.printStackTrace();
			logger.error(e.getErrorCode()+","+e.getMessage());
		}

	}
	/**
	 * @param e
	 */
	private void updateADVTable(MouseEvent e) {
		logger.info("start");
		final JTable table = (JTable) e.getSource();
		selectedRow = table.getSelectedRow();
		if(selectedRow==-1)
			return;

		final String table_id = (String) table.getValueAt(selectedRow, 3);
		final String company_abbr = (String) table.getValueAt(selectedRow, 6);

		logger.info("update ADVTable : "+table_id);


		ShippersTable st = new ShippersTable();
		st.setTable_id(table_id);


		advTablePanel.setSelectedTable(st);
		advTablePanel.retrive();

		/*	SearchADVCommand searchADVCommand = new SearchADVCommand(st,advTablePanel);
		searchADVCommand.execute();
		_selectedADVData = KSGModelManager.getInstance().selectedADVData;



		if(searchADVCommand.result!=SearchADVCommand.RESULT_SUCCESS)// 광고정보가 있다면
		{
			logger.info("no adv and insert adv");
			ADVData data = new ADVData();
			data.setCompany_abbr(company_abbr);
			data.setTable_id(table_id);
			data.setDate_isusse(null);
			rootElement = new Element("input");
			rootElement.setAttribute("type","xls");
			Element tableInfos = new Element("table");
			tableInfos.setAttribute("id","");
			rootElement.addContent(tableInfos);
			Document document = new Document(rootElement);
			Format format = Format.getPrettyFormat();

			format.setEncoding("EUC-KR");
			format.setIndent("\n");
			format.setIndent("\t");

			XMLOutputter outputter = new XMLOutputter(format);


			String strXml=outputter.outputString(document);


			data.setData(strXml);

			try {
				data.setDate_isusse(KSGDateUtil.toDate2(KSGDateUtil.getDate()));
			} catch (ParseException e2) {
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e2.getMessage());
			}
			KSGCommand  command = new InsertADVCommand(data);
			command.execute();
			KSGModelManager.getInstance().selectedADVData=_selectedADVData = data;

		}*/
		_pnADVInfo.setVisible(true);
		tableLayout.show(_pnTable, advTablePanel.getName());
		logger.debug("end");


	}
	/**
	 * 
	 */
	private void updatePortAction()
	{
		selectedshippersTable = this.getShipperTableBySelectedTable();
		if(selectedshippersTable!=null)
		{
			ManagePortDialog dialog = new ManagePortDialog(selectedshippersTable.getTable_id(),this);
			dialog.createAndUpdateUI();
		}
	}
	/**
	 * @throws SQLException
	 */
	public void updateSubTable( ) throws SQLException {


		if(manager.selectedCompany==null)
			return;

		logger.info("updateSubTable:"+manager.selectedCompany+","+manager.selectedPage);

		searchByOption(orderParam);


	}
	/**
	 * @param selectedCompany
	 * @param selectedPage
	 * @throws SQLException
	 */
	public void updateSubTable(String selectedCompany, int selectedPage) throws SQLException {


		ShippersTable shippersTable = new ShippersTable();

		shippersTable.setPage(selectedPage);

		_searchedList = _tableService.getTableListByPage(shippersTable);

		lblCount.setText(String.valueOf(_searchedList.size()));

		searchSubTable();
	}
	/**
	 * @param path
	 */
	private void updateViewByTree(TreePath path) {
		logger.debug("select tree "+path.getLastPathComponent());


		String selectedCompany = path.getLastPathComponent().toString();
		manager.selectedTable_id=null;
		_pnADVInfo.setVisible(false);
		try {
			logger.debug("selected path:"+path.getPathCount());
			switch (path.getPathCount()) {
			case DEPTH_ROOT: // root 선택시
				selectRoot(selectedCompany);

				break;

			case DEPTH_SUB: //2번째 노드 선택 ex:0~9
				selectSub(selectedCompany);

				break;
			case DEPTH_PAGE: //3번재 노드 선택 ex) 11:OOCL
				selectPage(selectedCompany);
				break;

			default:
				break;
			}

		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage());
		}
		txfCount.setText(String.valueOf(manager.tableCount));
	}
	/**
	 * @param count
	 */
	public void setPortCount(int count) {
		_pnUpdateTable.setPortCount(count);

	}


	public void searchByOption() {
		searchByOption(orderParam);
	}




	/**
	 * 구분에 의한 조회
	 * @throws SQLException
	 */
	private void searchByGubun() throws SQLException
	{
		int gubunIndex = cbbGubun.getSelectedIndex();
		this.searchParam = new ShippersTable();
		switch (gubunIndex) {
		case 0:
			searchParam.setGubun(null);
			break;
		case 1:
			searchParam.setGubun(ShippersTable.GUBUN_NORMAL);// 수정 필요
			break;
		case 2:
			searchParam.setGubun(ShippersTable.GUBUN_NNN);
			break;
		case 3:
			searchParam.setGubun(ShippersTable.GUBUN_TS);
			break;
		case 4:
			searchParam.setGubun(ShippersTable.GUBUN_CONSOLE);
			break;	
		case 5:
			searchParam.setGubun(ShippersTable.GUBUN_INLAND);
			break;	
		default:
			break;
		}

		tblSearchTable.setSearchParam(searchParam);

		_currentTable= tblSearchTable;

		tableLayout.show(_pnTable, tblSearchTable.getName());

		lblCount.setText(String.valueOf(tblSearchTable.getRowCount()));
	}


	private void searchByOption(String param) 
	{
		int index=cbbOption.getSelectedIndex();

		logger.debug("search param:"+param);

		orderParam = param;

		searchParam = new ShippersTable();

		if(!txfPageSearchInput.getText().equals(""))
		{
			String strPage =txfPageSearchInput.getText();

			try{
				searchParam.setPage(Integer.parseInt(strPage));
				manager.selectedPage = Integer.parseInt(strPage);
			}
			catch(NumberFormatException number_e)
			{			
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, strPage+":정확한 숫자를 입력하세요");
				txfPageSearchInput.setText("");
				return;
			}
		}

		if(!txfPageIndexSearchInput.getText().equals(""))
		{
			String strPageIndex =txfPageIndexSearchInput.getText();

			try{

				searchParam.setTable_index(Integer.parseInt(strPageIndex));
			}
			catch(NumberFormatException number_e)
			{			
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, strPageIndex+":정확한 숫자를 입력하세요");
				txfPageIndexSearchInput.setText("");
				return;
			}
		}


		switch (index) {

		/*	case 0://페이지

				_searchParam.setPage(Integer.parseInt(param));

				manager.selectedPage = Integer.parseInt(param);

				break;*/
		case 0:// 테이블 아이디
			searchParam.setTable_id(param);
			break;
			/*		case 1://테이블 인덱스

			_searchParam.setTable_index(Integer.parseInt(param));

			break;*/
		case 1://선사명
			searchParam.setCompany_abbr(param);
			break;
		case 2:// 제목
			searchParam.setTitle(param);
			break;
		case 3:// 에이전트
			searchParam.setAgent(param);
			break;

		default:
			break;
		}


		int gubunIndex = cbbGubun.getSelectedIndex();
		switch (gubunIndex) {
		case 0:
			searchParam.setGubun(null);
			break;
		case 1:
			searchParam.setGubun("Normal");
			break;
		case 2:
			searchParam.setGubun("NNN");
			break;
		case 3:
			searchParam.setGubun("TS");
			break;
		case 4:
			searchParam.setGubun("console");
			break;	

		default:
			break;
		}


		try {

			String date=txfDateSearch.getText();

			if(!date.equals(""))
			{
				if(KSGDateUtil.isDashFomatt(date))
				{
					try{
						searchParam.setDate_isusse(KSGDateUtil.toDate3(date).toString());
					}catch(Exception e)
					{
						e.printStackTrace();
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error: "+e.getMessage());
						txfDateSearch.setText("");
					}

				}else
				{
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "입력 형식(2000.1.1) 이 틀렸습니다.");
					txfDateSearch.setText("");
					return;
				}
			}


			tblSearchTable.setSearchParam(searchParam);
			tblSearchTable.retrive();


			_currentTable= tblSearchTable;


			lblCount.setText(String.valueOf(tblSearchTable.getRowCount()));

			txfDateSearch.setText("");

			txfSearchInput.setText("");

			txfPageSearchInput.setText("");

			txfPageIndexSearchInput.setText("");

			tableLayout.show(_pnTable, tblSearchTable.getName());

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	class UpdateDateDialog extends JDialog implements ActionListener
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel lblDate;
		private JTextField txfImportDate;

		List searchedList;
		public UpdateDateDialog(List searchedList) {
			this();
			this.searchedList = searchedList;

		}
		public UpdateDateDialog() {
			setModal(true);

			this.setTitle("날짜정보 수정");

			lblDate = new JLabel(" 입력날짜 : ");

			lblDate.setFont(KSGModelManager.getInstance().defaultFont);

			txfImportDate = new JTextField(8);

			JCheckBox cbxImportDate = new JCheckBox("월요일",false);

			cbxImportDate.setFont(KSGModelManager.getInstance().defaultFont);

			cbxImportDate.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					JCheckBox bo =(JCheckBox) e.getSource();
					if(bo.isSelected())
					{
						txfImportDate.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
					}
				}
			});

			JPanel pnMain = new JPanel();
			JPanel pnControl = new JPanel();
			pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
			JButton butOk = new JButton("확인");
			butOk.addActionListener(this);
			butOk.setFont(KSGModelManager.getInstance().defaultFont);
			JButton butCancel = new JButton("취소");
			butCancel.addActionListener(this);
			butCancel.setFont(KSGModelManager.getInstance().defaultFont);
			pnControl.add(butOk);
			pnControl.add(butCancel);			
			pnMain.add(lblDate);
			pnMain.add(txfImportDate);
			pnMain.add(cbxImportDate);
			getContentPane().add(pnMain);
			getContentPane().add(pnControl,BorderLayout.SOUTH);
			this.pack();
			setLocationRelativeTo(SearchUI.this);
			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals("취소"))
			{
				setVisible(false);
				dispose();
			}
			else if(command.equals("확인"))
			{
				String date=txfImportDate.getText();   

				if(!KSGDateUtil.isDashFomatt(date))
				{
					JOptionPane.showMessageDialog(null, "입력 형식(2000.1.1)이 틀렸습니다. "+date);
					return;
				}

				setVisible(false);
				dispose();

				try {

					ShippersTable table = new ShippersTable();

					table.setDate_isusse(KSGDateUtil.toDate3(date).toString());

					//_tableService.updateTableDateAll(table);

					_tableService.updateTableDateByTableIDs(tblSearchTable.getSearchedList(),KSGDateUtil.toDate3(date).toString());

					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "날짜를 수정했습니다.");


				} catch (SQLException e1) {

					e1.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
				} catch (ParseException e2) {
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e2.getMessage());
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e3.getMessage());
				}
			}
		}
	}

}



