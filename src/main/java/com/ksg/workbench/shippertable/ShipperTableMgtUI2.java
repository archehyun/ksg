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
package com.ksg.workbench.shippertable;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ksg.commands.SearchSubTableCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.service.ShipperTableService;
import com.ksg.service.TableService;
import com.ksg.service.impl.ADVServiceImpl;
import com.ksg.service.impl.ShipperTableServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.KSGRadioButton;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTableSelectListner;
import com.ksg.view.comp.table.model.TableModel;
import com.ksg.view.comp.tree.KSGTreeDefault;
import com.ksg.view.comp.tree.KSGTreeImpl;
import com.ksg.workbench.common.comp.KSGPageTablePanel;
import com.ksg.workbench.common.comp.button.PageAction;
import com.ksg.workbench.shippertable.comp.KSGADVTablePanel;
import com.ksg.workbench.shippertable.comp.UpdateTablePanel;
import com.ksg.workbench.shippertable.dialog.AddTableInfoDialog;
import com.ksg.workbench.shippertable.dialog.AddTableInfoDialog_temp;
import com.ksg.workbench.shippertable.dialog.UpdateShipperTableDateDialog;

/**

 * @FileName : ShipperTableMgtUI2.java

 * @Project : KSG2

 * @Date : 2022. 3. 15. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 광고 정보 조회 화면

 */
@SuppressWarnings("unchecked")
public class ShipperTableMgtUI2 extends ShipperTableAbstractMgtUI implements ActionListener
{	
	private static final String ACTION_SEARCH = "조회";

	private static final String ACTION_UPDATE_DATE = "입력일자 수정";
	/**
	 * @author 박창현
	 * @테이블에서의 마우스 동작
	 */
	class UpdateMouseAdapter extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) 
		{			
			try {


				int row = tableH.getSelectedRow();
				if(row<0) return;


				HashMap<String , Object> item = (HashMap<String, Object>) tableH.getValueAt(row);

				String table_id = String.valueOf(item.get("table_id"));

				txfInboundIn.setText(String.valueOf(item.get("in_port")));

				txfInboundOut.setText(String.valueOf(item.get("in_to_port")));

				txfOutboundIn.setText(String.valueOf(item.get("out_port")));

				txfOutboundOut.setText(String.valueOf(item.get("out_to_port")));

				txfPage.setText(String.valueOf(item.get("page")));

				txfTable_id.setText(table_id);

				pnUpdateTable.setShipperTableData(table_id);

				if(e.getClickCount()>1)
				{				
					updateADVTable(table_id);
					logger.info("click:"+e.getClickCount()+",selectedPage:"+manager.selectedPage);
				}
			}catch(Exception E)

			{
				E.printStackTrace();
				JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, E.getMessage());
			}
		}

	}
	//TODO 테이블 스타일 정리
	private static final int _LEFT_SIZE = 250;

	public static final int ADV_TYPE = 0;

	private static final long serialVersionUID = 1L;

	private static final String STRING_ERROR_NO_ADV_INFO = "광고정보가 없습니다. 광고 정보를 추가 하시겠습니까?";

	public static final int TABLE_TYPE = 1;

	private static KSGTreeDefault tree2;

	private KSGPanel 			pnADVInfo, pnleftMenu, pnShipperInfo,pnTable;	

	private Box 				pnSearchInfoMain;

	private UpdateTablePanel 	pnUpdateTable;

	private JPopupMenu 			popupMenu;	

	private KSGPageTablePanel tableH;

	private JTable				currentTable;

	private JTree				_treeMenu;

	private JTextField 			txfInboundIn,txfInboundOut,txfOutboundIn,txfOutboundOut,txfSearchInput;

	private JTextField 			txfPageSearchInput;

	private JTextField 			txfPageIndexSearchInput;

	private AddTableInfoDialog addTableInfoDialog;

	private KSGADVTablePanel advTablePanel;// 광고정보 편집화면

	private JToggleButton butEdit; //

	private boolean 			close=true;	

	private JMenuItem delMenu;

	private int depth=1; //

	private final int DEPTH_PAGE=3; //

	private final int DEPTH_ROOT=1; //

	private final int DEPTH_SUB=2; //

	private UpdateShipperTableDateDialog updateAllDateDialog; //날짜 일괄 수정 창		

	public KSGTableSelectListner listner;

	public List 				_searchedList, selectedList;

	private JSplitPane spMain;	

	private CardLayout 			tableLayout;

	private JTextField txfCompany,txfCount,txfDate,txfDateSearch,txfPage,txfTable_id, txfImportDate;

	private JComboBox cbbOption,cbbGubun;

	private KSGComboBox cbxGubun;

	private ShippersTable searchParam;

	private HashMap<String, Object> searchParamHash = new HashMap<String, Object>();

	SearchOptionKeyAdapter keyAdapter;

	private JLabel lblCompany,lblCount,lblDivision, lblPage, lblIndex, lblItem, lblDateSearch;

	private JMenuItem itemDateUpdate;

	private ShipperTableService shipperTableService;

	private ADVServiceImpl _advService;

	private TableService tableService;	

	public ShipperTableMgtUI2() 
	{		

		super();

		selectedList = new LinkedList();

		tableService = new TableServiceImpl();

		_advService= new ADVServiceImpl();
		
		shipperTableService = new ShipperTableServiceImpl();

		this.addComponentListener(this);

		this.title="광고정보 관리";

		this.borderColor=new Color(91,152,185);

		createAndUpdateUI();

	}

	private void insertAction()
	{
		int row=tableH.getSelectedRow();
		if(row<0) return;

		HashMap<String, Object> param= (HashMap<String, Object>) tableH.getValueAt(row);

		addTableInfoDialog = new AddTableInfoDialog(this,param);
		addTableInfoDialog.createAndUpdateUI();


	}
	public void actionPerformed(ActionEvent e) { 

		String command = e.getActionCommand();		

		if(command.equals("테이블검색:Page"))
		{
			JDialog dialog = new JDialog();

			KSGPanel pnMain = new KSGPanel();

			final JTextField txfSearchInput = new JTextField(15);			

			JLabel lblSearchPage = new JLabel("Page : ");
			JButton butSubmit = new JButton("검색",new ImageIcon("images/buticon.gif"));
			butSubmit.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					//					try {
					//						ShippersTable shippersTable = new ShippersTable();						
					//						shippersTable.setPage(Integer.parseInt(txfSearchInput.getText()));
					////						tblSearchTable.setSearchParam(shippersTable);
					////						tblSearchTable.retrive();
					////						_searchedList = tblSearchTable.getSearchedList();
					//
					//					} catch (SQLException e1) {
					//						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e1.getMessage());
					//						e1.printStackTrace();
					//					}
					//					catch (NumberFormatException e2) {
					//						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e2.getMessage());
					//						e2.printStackTrace();
					//					}
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

			if(addTableInfoDialog==null||!addTableInfoDialog.isVisible())
			{
				insertAction();
			}


		}else if(command.equals("테이블삭제"))
		{
			delAction();

		}
		else if(command.equals("항구관리"))
		{
			//updatePortAction();

		}
		else if(command.equals("일자변경"))
		{	

			int selectRow[]=tableH.getSelectedRows();
			ArrayList list = new ArrayList();

			for(int i=0;i<selectRow.length;i++)
			{
				list.add(tableH.getValueAt(i));
			}
			if(list.size()==0)return;


			updateAllDateDialog = new UpdateShipperTableDateDialog(list, this);
			updateAllDateDialog.createAndUpdateUI();



		}

		else if(command.equals("광고삭제"))
		{
			delADVAction();
		}

		else if(command.equals(ACTION_UPDATE_DATE))
		{
			updateAllDateDialog = new UpdateShipperTableDateDialog(selectedList, this);
			if(updateAllDateDialog.result==1)
			{
				
			}
		}
	}

	/**
	 * 취소시 원래 검색 화면 표시
	 * @throws SQLException
	 */
	public void showTableList() throws SQLException {

		logger.info("seleted page:"+searchParamHash);

		
		fnSearch();

		pnADVInfo.setVisible(false);		

		tableLayout.show(pnTable, tableH.getName());
	}

	/**
	 * @return
	 */
	private KSGPanel buildCenter() 
	{
		pnShipperInfo = new KSGPanel();

		pnShipperInfo.setLayout(new BorderLayout());

		pnTable = new KSGPanel();

		tableLayout = new CardLayout();

		pnTable.setLayout(tableLayout);


		tableH = new KSGPageTablePanel("광고목록");
		tableH.addColumn(new KSGTableColumn("page", "페이지"));
		tableH.addColumn(new KSGTableColumn("table_index", "인덱스"));
		tableH.addColumn(new KSGTableColumn("table_id", "테이블ID", 125));
		tableH.addColumn(new KSGTableColumn("date_isusse", "입력일자", 100));
		tableH.addColumn(new KSGTableColumn("port_col", "항구수",50));
		tableH.addColumn(new KSGTableColumn("vsl_row", "선박수",50));		
		tableH.addColumn(new KSGTableColumn("company_abbr", "선사",135,SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("agent", "에이전트",135, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("title", "제목",250, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("gubun", "구분"));
		tableH.addColumn(new KSGTableColumn("TS", "TS항구"));
		tableH.addColumn(new KSGTableColumn("in_port", "Inbound 출발항",110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("in_to_port", "Inbound 도착항",110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("out_port", "Outbound 출발항", 110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("out_to_port", "Outbound 도착항",110, SwingConstants.LEFT ));
		
		//TODO 컬럼 가운데 정렬

		tableH.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		tableH.initComp();

		tableH.setPageCountIndex(6);

		tableH.addPageActionListener(new PageAction(tableH, shipperTableService));

		tableH.setName("tableH");
		//tableH.setShowControl(true);


		//tblSearchTable = new SearchTable();

		tableH.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_ENTER)
				{
					//					final JTable table = (JTable) e.getSource();
					//					int row = table.getSelectedRow();
					//					if(row==-1)
					//						return;
					//					final int page =(Integer)table.getValueAt(row, 0);					
					//
					//					searchADVTable();
					//
					//					pnADVInfo.setVisible(true);
					//
					//					tableLayout.show(pnTable, advTablePanel.getName());
				}

			}

			public void keyReleased(KeyEvent arg0) {
				try {
					//JTable table = (JTable) arg0.getSource();

					if(arg0.getKeyCode()== KeyEvent.VK_ENTER)
					{

						//						logger.debug("releas " +arg0.getKeyCode()+table.getSelectedColumn());
						//
						//
						//						int row = table.getSelectedRow();
						//
						//						if(row==-1)
						//							return;

						//					try {
						//						KSGDateUtil.toDate2(String.valueOf(tblSearchTable.getValueAt(row, 2)));
						//					} catch (ParseException e1) 
						//					{
						//						JOptionPane.showMessageDialog(null, "정확한 날짜를 입력하십시요");
						//						try {
						//
						//
						//							searchSubTable();
						//						} catch (SQLException e) {
						//							// TODO Auto-generated catch block
						//							e.printStackTrace();
						//						}
						//						return;
						//					}

						//						String table_id = String.valueOf(table.getValueAt(row, 3));
						//						String date = String.valueOf(table.getValueAt(row, 2));
						//
						//						HashMap<String, Object> param = new HashMap<String, Object>();
						//
						//						param.put("table_id", table_id);
						//
						//						param.put("date_isusse", date);
						//
						//						shipperTableService.update(param);
						//
						//						//tableService.updateTableDate(table_id,date);
						//
						//						_advService.updateDateADVData(table_id, date);
						//
						//						switch (depth) {
						//						case DEPTH_SUB:
						//							_searchedList= tableService.getTableListByPage(startpage,endpage);	
						//							searchSubTable();
						//							break;
						//						case DEPTH_PAGE:
						//							//	updateSubTable();	
						//							break;
						//
						//						default:
						//							break;
						//						}


					}
				}
				catch (Exception e) {


					e.printStackTrace();

					JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, "error:"+e.getMessage());
				} 
			}
		});

		MyPopupMenuListener listener = new MyPopupMenuListener();

		JPopupMenu popMenu = createPopupMenu();

		popMenu.addPopupMenuListener(listener);		

		tableH.addMouseListener(new UpdateMouseAdapter());

		tableH.setComponentPopupMenu(popMenu);

		pnTable.add(tableH,tableH.getName());

		advTablePanel = new KSGADVTablePanel(this);
		advTablePanel.setName("adv");

		pnTable.add(advTablePanel,advTablePanel.getName());

		KSGPanel pnSlideShow = cteateSlideShowPn();

		KSGPanel pnSouth = new KSGPanel(new BorderLayout());

		butEdit = new JToggleButton("편집(E)",new ImageIcon("images/editClose.gif"));
		butEdit.setMnemonic(KeyEvent.VK_E);
		butEdit.setSelectedIcon(new ImageIcon("images/editOpen.gif"));

		butEdit.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JToggleButton but =(JToggleButton) e.getSource();
				ButtonModel model =but.getModel();

				changeUpdatePNState(model.isSelected());

			}});


		JButton butCreateADV = new JButton("광고 등록( + N)");
		butCreateADV.setActionCommand("광고 등록");
		butCreateADV.addActionListener(this);


		KSGPanel pnRightControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		pnRightControl.add(butEdit);

		KSGPanel pnLeftControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butUpdateDate = new JButton(ACTION_UPDATE_DATE);
		butUpdateDate.setVisible(false);
		butUpdateDate.addActionListener(this);

		pnLeftControl.add(butUpdateDate);
		pnSouth.add(pnRightControl, BorderLayout.EAST);
		pnSouth.add(pnLeftControl, BorderLayout.WEST);

		pnUpdateTable = new UpdateTablePanel(this);
		pnUpdateTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnUpdateTable.setVisible(false);
		pnUpdateTable.setMinimumSize(new Dimension(200,0));
		pnUpdateTable.setMaximumSize(new Dimension(300,0));


		KSGPanel pnTableMain = new KSGPanel();
		pnTableMain.setLayout(new BorderLayout());

		KSGPanel pnTableMainLeft = new KSGPanel();
		pnTableMainLeft.setPreferredSize(new Dimension(15,0));
		KSGPanel pnTableMainRight = new KSGPanel();
		pnTableMainRight.setPreferredSize(new Dimension(15,0));
		pnTableMain.add(pnTableMainLeft,BorderLayout.WEST);
		pnTableMain.add(pnTableMainRight,BorderLayout.EAST);

		pnTableMain.add(pnTable,BorderLayout.CENTER);

		spMain = new JSplitPane();

		spMain.setLeftComponent(pnUpdateTable);
		spMain.setRightComponent(pnTableMain);

		pnShipperInfo.add(spMain,BorderLayout.CENTER);	

		pnShipperInfo.add(pnSlideShow,BorderLayout.WEST);

		pnShipperInfo.add(pnSouth,BorderLayout.SOUTH);

		pnShipperInfo.add(createPnSearchInfo(),BorderLayout.NORTH);

		KSGPanel pnMain = new KSGPanel(new BorderLayout());		

		pnMain.add(pnShipperInfo,BorderLayout.CENTER);

		pnleftMenu = buildLeftMenu();

		pnleftMenu.setVisible(false);

		pnMain.add(pnleftMenu,BorderLayout.WEST);

		pnMain.add(pnShipperInfo,BorderLayout.CENTER);

		return pnMain;
	}

	/**
	 * @return
	 */
	private KSGPanel buildLeftMenu() 
	{
		KSGPanel pnMain = new KSGPanel();

		KSGPanel pnSearch =  new KSGPanel();

		pnSearch.setLayout( new BorderLayout());

		_treeMenu = createTreeMenu();

		pnMain.setLayout(new BorderLayout());

		pnMain.add(new JScrollPane(_treeMenu),BorderLayout.CENTER);
		pnMain.setPreferredSize(new Dimension(_LEFT_SIZE,100));
		KSGPanel panel = new KSGPanel();
		ButtonGroup group = new ButtonGroup();


		JRadioButton rbtCompany = new KSGRadioButton("선사별");
		JRadioButton rbtPage = new KSGRadioButton("페이지별",true);
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
						tree2.setGroupBy(KSGTreeImpl.GroupByCompany);
					}
					else if(te.equals("페이지별"))
					{
						tree2.setGroupBy(KSGTreeImpl.GroupByPage);
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


			KSGPanel pnTitle = new KSGPanel();
			pnTitle.setBackground(new Color(88,141,250));


			pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));

			JLabel label = new JLabel("테이블 목록");

			label.setForeground(Color.white);

			pnTitle.add(label);
			pnTitle.setPreferredSize( new Dimension(0,22));


			pnMain.add(pnSearch,BorderLayout.NORTH);
			pnMain.add(panel,BorderLayout.SOUTH);

			KSGPanel pnArrow = new KSGPanel();
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

			pnUpdateTable.setVisible(true);
			butEdit.setText("확인(E)");

		}
		else
		{
			spMain.setDividerLocation(0);
			pnUpdateTable.setVisible(false);
			butEdit.setText("편집(E)");

		}
	}


	public void createAndUpdateUI() {

		logger.debug("init searchUI start");

		this.setName("SearchUI");

		keyAdapter = new SearchOptionKeyAdapter();

		popupMenu = createPopupMenu();

		this.setLayout(new BorderLayout());

		this.add(buildCenter(),BorderLayout.CENTER);

		this.add(buildNorthPn(),BorderLayout.NORTH);

		this.fnSearch();


		logger.debug("init searchUI end");


	}



	/**
	 * 설명:
	 * @returnSD
	 */
	private KSGPanel createPnSearchInfo() {
		pnSearchInfoMain = new Box(BoxLayout.Y_AXIS);

		pnADVInfo = new KSGPanel();
		pnADVInfo.setLayout(new FlowLayout(FlowLayout.LEADING));

		txfOutboundIn = new JTextField(8);
		txfInboundIn = new JTextField(8);
		txfOutboundOut = new JTextField(8);		
		txfInboundOut = new JTextField(8);

		txfOutboundIn.setEditable(false);
		txfOutboundOut.setEditable(false);
		txfInboundIn.setEditable(false);
		txfInboundOut.setEditable(false);

		txfOutboundIn.setBackground(Color.white);
		txfOutboundOut.setBackground(Color.white);
		txfInboundIn.setBackground(Color.white);
		txfInboundOut.setBackground(Color.white);



		KSGPanel pnTableInfo = new KSGPanel();
		pnTableInfo.setLayout(new BorderLayout());

		txfPage = new JTextField(10);
		txfPage.setEditable(false);
		txfPage.setBorder(BorderFactory.createEtchedBorder());
		txfPage.setBackground(Color.white);


		txfPageSearchInput = new JTextField(4);

		txfPageSearchInput.addKeyListener(keyAdapter);

		txfPageSearchInput.setBackground(Color.white);

		txfPageIndexSearchInput = new JTextField(4);

		txfPageIndexSearchInput.addKeyListener(keyAdapter);
		txfPageIndexSearchInput.setBackground(Color.white);

		txfTable_id = new JTextField(14);
		txfTable_id.setEditable(false);
		txfTable_id.setBorder(BorderFactory.createEtchedBorder());
		txfTable_id.setBackground(Color.white);

		txfCompany = new JTextField(10);

		txfCompany.setEditable(false);

		txfDate = new HintTextField("2000.1.1",8);

		txfDate.setEditable(false);

		txfDate.setBorder(BorderFactory.createEmptyBorder());

		txfCount = new JTextField(2);

		txfCount.setEditable(false);

		lblCompany = new JLabel();

		lblCompany.setFont(new Font("돋움",0,15));

		Box bo = new Box(BoxLayout.Y_AXIS);

		KSGPanel pnMain1 = new KSGPanel();

		pnMain1.setLayout(new BorderLayout());

		KSGPanel pnLogo = new KSGPanel();

		pnLogo.setLayout( new FlowLayout(FlowLayout.LEFT));

		lblCompany.setIcon(new ImageIcon("images/table.png"));

		pnLogo.add(lblCompany);

		KSGPanel pnSearchMain = new KSGPanel();

		pnSearchMain.setLayout(new BorderLayout());

		lblDateSearch = new JLabel("  입력일자: ");

		lblDateSearch.setFont(new Font("맑은고딕", Font.BOLD, 12));

		txfDateSearch = new HintTextField("2000.1.1",10);

		txfDateSearch.addKeyListener(new KeyAdapter(){

			public void keyReleased(KeyEvent e) 
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{	
					fnSearch();
				}
			}
		});
		Icon warnIcon = new ImageIcon("images/search.png");

		JButton butDateSearch = new JButton(warnIcon);

		butDateSearch.setText(ACTION_SEARCH);

		butDateSearch.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				searchParamHash = new HashMap<String, Object>();
				fnSearch();
			}
		});

		cbbOption = new JComboBox();




		cbbOption.setBackground(Color.white);

		cbbOption.addItem("페이지");
		cbbOption.addItem("테이블ID");
		cbbOption.addItem("선사명");
		cbbOption.addItem("제목");
		cbbOption.addItem("에이전트");		
		cbbOption.addItem("인덱스");

		cbbOption.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JComboBox ss = (JComboBox) e.getSource();

				String target=(String) ss.getSelectedItem();

				if(target.equals("페이지")||target.equals("인덱스"))
				{
					txfSearchInput.addKeyListener(keyAdapter);
				}
				else
				{
					txfSearchInput.removeKeyListener(keyAdapter);
				}
			}});
		txfSearchInput = new JTextField(10);
		txfSearchInput.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{

					fnSearch();
					String param=txfSearchInput.getText();
					//					try {
					//						searchByOption(param);
					//					} catch (SQLException e1) {
					//						JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, e1.getMessage());
					//						e1.printStackTrace();
					//					}
				}
			}
		});

		JCheckBox box = new JCheckBox("페이지",true);
		box.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box =(JCheckBox) e.getSource();


			}});


		cbxGubun = new KSGComboBox("tableType");
		cbxGubun.setShowTotal(true);


		cbxGubun.initComp();

		cbxGubun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) 
			{


				fnSearch();

			}
		});		

		KSGPanel pnSearchMainDown = new KSGPanel();
		pnSearchMainDown.setLayout(new FlowLayout(FlowLayout.RIGHT));

		lblDivision = new JLabel("테이블 구분: ");
		lblPage = new JLabel("  페이지: ");
		lblIndex = new JLabel("  인덱스: ");
		lblItem = new JLabel("  항목: ");
		Font font = new Font("맑은고딕",Font.BOLD,12);

		lblDivision.setFont(font);

		lblPage.setFont(font);
		lblIndex.setFont(font);
		lblItem.setFont(font);

		pnSearchMainDown.add(lblDivision);
		pnSearchMainDown.add(cbxGubun);

		pnSearchMainDown.add(lblItem);
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
		KSGPanel pnLeft = new KSGPanel();
		pnLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("페이지 : ",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		label.setForeground(Color.blue.brighter());
		pnLeft.add(label);
		pnLeft.add(txfPage);		

		txfPage.setForeground(Color.blue);
		JLabel lbl = new JLabel("테이블 ID : ",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		lbl.setForeground(Color.blue.brighter());

		txfTable_id.setForeground(Color.blue.brighter());
		pnLeft.add(lbl);
		pnLeft.add(txfTable_id);

		pnTableInfo.add(pnLeft,BorderLayout.WEST);

		bo.add(pnTableInfo);

		JLabel lblOutboundIn = new JLabel("Outbound국내항:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnADVInfo.add(lblOutboundIn);
		pnADVInfo.add(txfOutboundIn);
		JLabel lblOutboundOut = new JLabel("Outbound외국항:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnADVInfo.add(lblOutboundOut);
		pnADVInfo.add(txfOutboundOut);
		JLabel lblInboundIn = new JLabel("Inbound국내항:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnADVInfo.add(lblInboundIn);		
		pnADVInfo.add(txfInboundIn);
		JLabel lblInboundOut = new JLabel("Inbound외국항:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnADVInfo.add(lblInboundOut);
		pnADVInfo.add(txfInboundOut);

		pnSearchInfoMain.add(bo);

		KSGPanel pnS = new KSGPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		KSGPanel pnS1 = new KSGPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		pnSearchInfoMain.add(pnS);
		pnSearchInfoMain.add(pnS1);
		pnSearchInfoMain.add(pnADVInfo);

		pnADVInfo.setVisible(false);

		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout( new BorderLayout());

		pnMain.add(pnSearchInfoMain,BorderLayout.CENTER);
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

		itemDateUpdate = new JMenuItem("일자변경");
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
					tableH.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				}else
				{
					tableH.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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
	private KSGPanel createSearchPanel() {
		KSGPanel pnSearchByCompany = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		return pnSearchByCompany;
	}

	/**
	 * 좌측 트리 메뉴 생성
	 * @return
	 */
	private JTree createTreeMenu() 
	{
		tree2 = new KSGTreeDefault("tree1");
		tree2.setComponentPopupMenu(createTreePopupmenu());
		tree2.setRowHeight(25);
		tree2.update();

		tree2.addTreeSelectionListener(new TreeSelectionListener(){

			public void valueChanged(TreeSelectionEvent e) {

				TreePath path=e.getNewLeadSelectionPath();
				if(path!=null)
				{

					try{
						//updateViewByTree(path);

					}catch(Exception e2)
					{
						e2.printStackTrace();
						return;
					}
				}

			}});



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
				AddTableInfoDialog_temp addTableInfoDialog = new AddTableInfoDialog_temp(ShipperTableMgtUI2.this,manager.selectedCompany);
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
	private KSGPanel cteateSlideShowPn() {
		KSGPanel pn = new KSGPanel();
		pn.setLayout(new BorderLayout());
		pn.setPreferredSize(new Dimension(25,0));
		final JButton button = new JButton(new ImageIcon("images/right_arrow_.gif"));

		button.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) {
				if(close)
				{
					pnleftMenu.setPreferredSize(new Dimension(_LEFT_SIZE,0));
					pnleftMenu.setVisible(true);
					close = false;
					button.setIcon(new ImageIcon("images/left_arrow_.gif"));
				}else
				{
					pnleftMenu.setPreferredSize(new Dimension(0,0));
					pnleftMenu.setVisible(false);
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


		int row = tableH.getSelectedRow();
		if(row<0)return;

		HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);


		try {
			int result = shipperTableService.delete(param);
			if(result>0)
			{
				JOptionPane.showMessageDialog(this, param.get("table_id")+ "를 삭제 했습니다.");
				fnUpdate();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * 광고정보 삭제
	 */
	private void delADVAction() {



		int row = tableH.getSelectedRow();
		if(row<0)return;

		HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);

		String company_abbr = (String) param.get("company_abbr");
		String table_id =  (String) param.get("table_id");


		int j=JOptionPane.showConfirmDialog(this ,company_abbr+"의"+ table_id+
				" 광고정보를 삭제하시겠습니까?","광고 정보 삭제",JOptionPane.OK_CANCEL_OPTION);
		if(j==JOptionPane.YES_OPTION)
		{
			try {
				logger.debug("option result "+j);
				int result=_advService.removeADVData(table_id, manager.selectedDate);
				logger.debug("del result "+result);
				if(result==1)
				{
					JOptionPane.showMessageDialog(this, "광고정보를 삭제했습니다.");
					ADVData data = new ADVData();
					data.setTable_id(manager.selectedTable_id);
					data.setCompany_abbr(company_abbr);
					tableService.updateTableDate(data);
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
		txfPage.setText(page);
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
			JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, "error : "+e.getMessage());
		}
	}
	/**
	 * @param colum
	 * @throws SQLException
	 */
	private void searchSubTable() throws SQLException {

		if(_searchedList!=null)
		{
			logger.debug("searchsubtable:"+_searchedList.size());

			SearchSubTableCommand searchSubTableCommand = new SearchSubTableCommand(currentTable,_searchedList);

			searchSubTableCommand.execute();
		}
	}

	

	/**
	 * @param selectedCompany
	 */
	private void selectRoot(String selectedCompany) {
		depth =DEPTH_ROOT; 
		txfDate.setText("");
		txfCompany.setText("");
		txfPage.setText("");
		txfTable_id.setText("");

		manager.selectedCompany=null;
		manager.selectedDate=null;
		manager.orderBy="page";
		lblCompany.setText(selectedCompany);

		delMenu.setEnabled(false);

		//lblCount.setText(String.valueOf(tblSearchTable.getRowCount()));
		tableLayout.show(pnTable, tableH.getName());
	}
	


	private void updateADVTable(String table_id) {
		logger.info("start");		

		ShippersTable st = new ShippersTable();

		st.setTable_id(table_id);

		advTablePanel.setSelectedTable(st);

		advTablePanel.retrive();

		pnADVInfo.setVisible(true);

		tableLayout.show(pnTable, advTablePanel.getName());

		logger.debug("end");


	}
	
	/**
	 * @param selectedCompany
	 * @param selectedPage
	 * @throws SQLException
	 */
	public void updateSubTable(String selectedCompany, int selectedPage) throws SQLException {


		ShippersTable shippersTable = new ShippersTable();

		shippersTable.setPage(selectedPage);

		_searchedList = tableService.getTableListByPage(shippersTable);

		lblCount.setText(String.valueOf(_searchedList.size()));

		searchSubTable();
	}
	//	/**
	//	 * @param path
	//	 */
	//	private void updateViewByTree(TreePath path) {
	//
	//		logger.debug("select tree "+path.getLastPathComponent());
	//
	//		String selectedCompany = path.getLastPathComponent().toString();
	//		manager.selectedTable_id=null;
	//		pnADVInfo.setVisible(false);
	//		try {
	//			logger.debug("selected path:"+path.getPathCount());
	//			switch (path.getPathCount()) {
	//			case DEPTH_ROOT: // root 선택시
	//				selectRoot(selectedCompany);
	//
	//				break;
	//
	//			case DEPTH_SUB: //2번째 노드 선택 ex:0~9
	//				selectSub(selectedCompany);
	//
	//				break;
	//			case DEPTH_PAGE: //3번재 노드 선택 ex) 11:OOCL
	//
	//				selectPage(selectedCompany);
	//
	//				break;
	//
	//			default:
	//				break;
	//			}
	//
	//		} catch (SQLException e1) {
	//			JOptionPane.showMessageDialog(null, e1.getMessage());
	//		}
	//		txfCount.setText(String.valueOf(manager.tableCount));
	//	}
	/**
	 * @param count
	 */
	public void setPortCount(int count) {
		pnUpdateTable.setPortCount(count);

	}

	public void fnUpdate()
	{
		try {
			int page_size = tableH.getPageSize();

			searchParamHash.put("PAGE_SIZE", page_size);

			searchParamHash.put("PAGE_NO", 1);

			logger.info("param:"+searchParamHash);

			HashMap<String, Object> result = (HashMap<String, Object>) shipperTableService.selectListByPage(searchParamHash);

			result.put("PAGE_NO", 1);

			tableH.setResultData(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void fnSearch()
	{
		logger.info("START");

		String param=txfSearchInput.getText();
		
		if(searchParamHash ==null)
		searchParamHash = new HashMap<String, Object>();

		if(cbxGubun.getSelectedIndex()>0)
		{
			KSGTableColumn item=(KSGTableColumn) cbxGubun.getSelectedItem();
			searchParamHash.put("gubun", item.columnName);

		}

		if(!param.equals("")) {
			selectOptionHash(searchParamHash, param, cbbOption.getSelectedIndex());
		}


		String date=txfDateSearch.getText();

		if(!date.equals("2000.1.1"))
		{

			try {
				String	fomattedDate = KSGDateUtil.toDate3(date).toString();


				searchParamHash.put("date_isusse",fomattedDate);
			} catch (DateFormattException e) {
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e.getMessage()+ ": 입력 형식(2000.1.1) 이 틀렸습니다.");
			}

		}
		try {

			int page_size = tableH.getPageSize();

			searchParamHash.put("PAGE_SIZE", page_size);

			searchParamHash.put("PAGE_NO", 1);

			logger.info("param:"+searchParamHash);

			HashMap<String, Object> result = (HashMap<String, Object>) shipperTableService.selectListByPage(searchParamHash);

			result.put("PAGE_NO", 1);

			tableH.setResultData(result);


			List master = (List) result.get("master");

			txfDateSearch.setText("2000.1.1");

			tableH.requestFocus();

			txfSearchInput.setText("");

			if(master.size()==0)
			{
				/*lblArea.setText("");
				lblAreaCode.setText("");
				lblPationality.setText("");
				lblPortName.setText("");
				tableD.clearReslult();*/
			}
			else
			{
				//tableH.changeSelection(0,0,false,false);
				txfSearchInput.requestFocus();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	private void selectGubun(ShippersTable searchParam, int gubunIndex )
	{
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
	}



	/**
	 * @param param
	 * @param index
	 */
	private void selectOption(String param, int index) {

		if(param== null||param.equals("")) return;

		switch (index) {

		case 0://페이지

			searchParam.setPage(Integer.parseInt(param));

			manager.selectedPage = Integer.parseInt(param);

			break;
		case 1:// 테이블 아이디
			searchParam.setTable_id(param);
			break;
		case 5://테이블 인덱스

			searchParam.setTable_index(Integer.parseInt(param));

			break;
		case 2://선사명
			searchParam.setCompany_abbr(param);
			break;
		case 3:// 제목
			searchParam.setTitle(param);
			break;
		case 4:// 에이전트
			searchParam.setAgent(param);
			break;

		default:
			break;
		}
	}

	/**
	 * @param param
	 * @param index
	 */
	private void selectOptionHash(HashMap<String, Object> searchOption,String param, int index) {

		if(param== null||param.equals("")) return;

		switch (index) {

		case 0://페이지

			searchOption.put("page", Integer.parseInt(param));

			break;
		case 1:// 테이블 아이디

			searchOption.put("table_id", param);

			break;
		case 5://테이블 인덱스
			searchOption.put("table_index", param);			

			break;
		case 2://선사명
			searchOption.put("company_abbr", param);

			break;
		case 3:// 제목
			searchOption.put("title", param);

			break;
		case 4:// 에이전트
			searchOption.put("agent", param);

			break;

		default:
			break;
		}
	}

	class UpdateDateDialog extends JDialog implements ActionListener
	{
		public int result=0;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel lblDate;
		private JTextField txfImportDate;

		private List list;

		public UpdateDateDialog(List searchedList) {
			this.list = searchedList;			

			tableService = new TableServiceImpl();
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

			KSGPanel pnMain = new KSGPanel();
			KSGPanel pnControl = new KSGPanel();
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
			setLocationRelativeTo(ShipperTableMgtUI2.this);
			setVisible(true);
		}
		public void close()
		{
			setVisible(false);
			dispose();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if(command.equals("취소"))
			{				
				close();
			}
			else if(command.equals("확인"))
			{

				try {
					String date=txfImportDate.getText();

					tableService.updateTableDateByTableIDs(list,KSGDateUtil.toDate3(date).toString());

					result=1;

					close();

					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "날짜를 수정했습니다.");

				} catch (SQLException e1) {

					e1.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
				} catch (DateFormattException e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "입력 형식(2000.1.1)이 틀렸습니다. "+e2.getMessage());
				}
				catch(Exception e3)
				{
					e3.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e3.getMessage());
				}
			}
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {

		//if(isShowData)fnSearch();
	}
	class SearchOptionKeyAdapter extends KeyAdapter
	{		

		@Override
		public void keyReleased(KeyEvent e) {
			JTextField txf =(JTextField) e.getSource();
			txf.setForeground(Color.black);
			String str = txf.getText();
			if(str== null||str.equals(""))
				return;
			try{
				Integer.parseInt(str);

			}catch(NumberFormatException ee)
			{
				txf.setForeground(Color.red);
			}
		}
	}
	class MyPopupMenuListener implements PopupMenuListener {
		public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
		}

		public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {


		}
	}
	class ColorComboBoxListCellRenderer implements ListCellRenderer
	{
		protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel renderer = (JLabel) defaultRenderer
					.getListCellRendererComponent(list, value, index,
							isSelected, cellHasFocus);

			renderer.setBackground(Color.white);
			return renderer;
		}

	}

	class SearchTableModel extends TableModel{

		public SearchTableModel()
		{
			super();
		}

		public boolean isCellEditable(int row, int column) 
		{
			return false;
		}
	}

	public class HintTextField extends JTextField {  



		Font gainFont = new Font("Tahoma", Font.PLAIN, 11);  

		Font lostFont = new Font("Tahoma", Font.ITALIC, 11);  



		public HintTextField(final String hint, int count) {  
			super(count);


			setText(hint);  

			setFont(lostFont);  

			setForeground(Color.GRAY);

			this.addFocusListener(new FocusAdapter() {  



				@Override  

				public void focusGained(FocusEvent e) {  

					if (getText().equals(hint)) {  

						setText("");  

						setFont(gainFont);  

					} else {  

						setText(getText());  

						setFont(gainFont);  

					}  

				}  



				@Override  

				public void focusLost(FocusEvent e) {  

					if (getText().equals(hint)|| getText().length()==0) {  

						setText(hint);  

						setFont(lostFont);  

						setForeground(Color.GRAY);  

					} else {  

						setText(getText());  

						setFont(gainFont);  

						setForeground(Color.BLACK);  

					}
				}
			});
		}
	}  
}



