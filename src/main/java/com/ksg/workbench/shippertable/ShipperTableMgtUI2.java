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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListCellRenderer;
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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

import org.jdom.JDOMException;

import com.dtp.api.control.ShipperTableController;
import com.dtp.api.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.Company;
import com.ksg.domain.ShippersTable;
import com.ksg.service.CompanyService;
import com.ksg.view.comp.KSGViewUtil;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.radiobutton.KSGRadioButton;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.view.comp.table.KSGTableSelectListner;
import com.ksg.view.comp.table.groupable.ColumnGroup;
import com.ksg.view.comp.table.groupable.GroupableTableHeader;
import com.ksg.view.comp.textfield.HintTextField;
import com.ksg.view.comp.textfield.SearchTextField;
import com.ksg.view.comp.tree.CustomTree;
import com.ksg.workbench.common.comp.KSGDatePickerImpl;
import com.ksg.workbench.shippertable.comp.KSGADVTablePanel;
import com.ksg.workbench.shippertable.comp.UpdateTablePanel;
import com.ksg.workbench.shippertable.dialog.AddTableInfoDialog;
import com.ksg.workbench.shippertable.dialog.AddTableInfoDialog_temp;
import com.ksg.workbench.shippertable.dialog.ManageTablePortPop;
import com.ksg.workbench.shippertable.dialog.SearchCompanyDialog;
import com.ksg.workbench.shippertable.dialog.UpdateShipperTableDateDialog;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

/**

 * @FileName : ShipperTableMgtUI2.java

 * @Project : KSG2

 * @Date : 2022. 3. 15. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 : 광고 정보 조회 화면

 */
@SuppressWarnings("unchecked")
@Slf4j
public class ShipperTableMgtUI2 extends ShipperTableAbstractMgtUI
{	
	private static final String ACTION_SEARCH = "조회";

	private static final String ACTION_UPDATE_DATE = "입력일자 수정";
	
	private KSGViewUtil propeties = KSGViewUtil.getInstance();

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

				CommandMap param = new CommandMap();

				param.put("table_id", table_id);

				callApi("shipperTableMgtUI2.selectOne", param);

				if(e.getClickCount()>1)
				{				
					showADVTable(table_id);

					log.info("click:"+e.getClickCount()+",selectedPage:"+manager.selectedPage);
				}
			}catch(Exception E)

			{
				E.printStackTrace();
				JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, E.getMessage());
			}
		}
	}

	private static final int _LEFT_SIZE = 250;

	public static final int ADV_TYPE = 0;

	private static final long serialVersionUID = 1L;

	private static final String STRING_ERROR_NO_ADV_INFO = "광고정보가 없습니다. 광고 정보를 추가 하시겠습니까?";

	public static final int TABLE_TYPE = 1;

	private KSGPanel 			pnADVInfo, pnleftMenu, pnShipperInfo,pnTable;	

	private Box 				pnSearchInfoMain;

	private UpdateTablePanel 	pnUpdateTable;

	private JPopupMenu 			popupMenu;	

	private KSGTablePanel 		tableH;

	private JTree				_treeMenu;

	private JTextField 			txfInboundIn,txfInboundOut,txfOutboundIn,txfOutboundOut,txfSearchInput;

	private JTextField 			txfPageSearchInput;

	private JTextField 			txfPageIndexSearchInput;

	private AddTableInfoDialog 	addTableInfoDialog;

	private KSGADVTablePanel 	advTablePanel;// 광고정보 편집화면

	private JToggleButton 		butEdit; //

	private boolean 			close=true;	

	private SearchTextField 	stfCompanyAbbr;

	private JMenuItem 			delMenu;

	private int 				depth=1; //

	private final int 			DEPTH_PAGE=3; //

	private final int 			DEPTH_ROOT=1; //

	private final int 			DEPTH_SUB=2; //

	private UpdateShipperTableDateDialog updateAllDateDialog; //날짜 일괄 수정 창		

	public KSGTableSelectListner listner;

	public List 				_searchedList, selectedList;

	private JSplitPane spMain;	

	private CardLayout 			tableLayout;

	private JTextField txfCount,txfDate,txfPage,txfTable_id;

	private KSGComboBox cbbOption, cbxGubun;

	private ShippersTable searchParam;

	private HashMap<String, Object> searchParamHash = new HashMap<String, Object>();

	SearchOptionKeyAdapter keyAdapter;

	private JLabel lblCompany,lblCount,lblDivision, lblPage, lblIndex, lblItem, lblDateSearch;

	private JMenuItem itemDateUpdate;

	private CustomTree tree;

	UtilDateModel dateModel;

	private ButtonGroup tableGroup;

	private JRadioButton rbtPage;

	private CompanyService companyService;

	private KSGDatePickerImpl jdatePicker;

	public ShipperTableMgtUI2() 
	{	
		super();

		selectedList = new LinkedList();

		this.addComponentListener(this);

		this.setController(new ShipperTableController());

		this.title="광고정보 관리";

		this.borderColor=new Color(91,152,185);

		createAndUpdateUI(); 

		callApi("shipperTableMgtUI2.init");

	}

	private void insertAction()
	{
		int row=tableH.getSelectedRow();

		if(row<0) return;

		ShippersTable param = null;

		HashMap<String, Object> item = (HashMap<String, Object>) tableH.getValueAt(row);

		param = new ShippersTable();

		param.setTable_id((String) item.get("table_id"));

		addTableInfoDialog = new AddTableInfoDialog(this,param);

		addTableInfoDialog.createAndUpdateUI();

	}
	public void actionPerformed(ActionEvent e) { 

		String command = e.getActionCommand();

		try 
		{

			if(command.equals("테이블검색:Page"))
			{
				JDialog dialog = new JDialog();

				KSGPanel pnMain = new KSGPanel();

				final JTextField txfSearchInput = new JTextField(15);

				JLabel lblSearchPage = new JLabel("Page : ");

				JButton butSubmit = new JButton("검색",new ImageIcon("images/buticon.gif"));

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
				fnDeleteTable();

			}
			else if(command.equals("항구 관리"))
			{
				ManageTablePortPop pop = new ManageTablePortPop(advTablePanel.getTable_id());

				pop.showPop();

				if(pop.RESLULT!=ManageTablePortPop.OK) return;

				searchADVTable();

			}
			else if(command.equals(SAVE_ADV_DATA))
			{
				ADVData insertParam= advTablePanel.getADVData();

				CommandMap param = new CommandMap();

				param.put("insertParam", insertParam);

				callApi("KSGADVTablePanel.save",param);

			}

			else if(command.equals(ADV_CANCEL))
			{
				pnADVInfo.setVisible(false);		

				tableLayout.show(pnTable, tableH.getName());
			}
			else if(command.equals("일자변경"))
			{	
				int selectRow[]=tableH.getSelectedRows();

				ArrayList<String> list = new ArrayList<String>();

				for(int i=0;i<selectRow.length;i++)
				{
					CommandMap param = (CommandMap) tableH.getValueAt(selectRow[i]);

					list.add((String) param.get("table_id"));
				}

				if(list.size()==0)return;

				updateAllDateDialog = new UpdateShipperTableDateDialog(list, this);

				updateAllDateDialog.createAndUpdateUI();

				if(updateAllDateDialog.result == KSGDialog.SUCCESS) fnSearch();
			}

			else if(command.equals("검색"))
			{
				fnSearch();
			}

			else if(command.equals("저장하기"))
			{
				updateAndSaveShipperTableInfo();
			}
			else if(command.equals("SEARCH_COMPANY"))
			{
				SearchCompanyDialog companyDialog = new SearchCompanyDialog();

				companyDialog.createAndUpdateUI();

				String company_abbr = companyDialog.result;

				if(company_abbr == null)return;

				CommandMap param = new CommandMap();

				param.put("company_abbr", company_abbr);

				Company companyInfo=companyService.select(param );

				pnUpdateTable. setCompanyInfo(companyInfo);	

			}

		} catch (Exception e1)
		{
			JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, e1.getMessage());
		}
	}

	private void updateAndSaveShipperTableInfo() {

		ShippersTable table = pnUpdateTable.getSelectShipperTable();

		CommandMap param = new CommandMap();

		param.put("updateParam", table);

		callApi("shipperTableMgtUI2.update",param);
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

		tableH = new KSGTablePanel("광고목록");

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
		tableH.addColumn(new KSGTableColumn("in_port", "출발항",110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("in_to_port", "도착항",110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("out_port", "출발항", 110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("out_to_port", "도착항",110, SwingConstants.LEFT ));

		//TODO 컬럼 가운데 정렬

		tableH.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		tableH.initComp();

		TableColumnModel cm = tableH.getTable().getColumnModel();

		ColumnGroup inbound = new ColumnGroup("Inbound");

		inbound.add(cm.getColumn(11));

		inbound.add(cm.getColumn(12));

		ColumnGroup outbound = new ColumnGroup("Outbound");

		outbound.add(cm.getColumn(13));

		outbound.add(cm.getColumn(14));

		GroupableTableHeader header = (GroupableTableHeader)tableH.getTable().getTableHeader();

		header.addColumnGroup(inbound);

		header.addColumnGroup(outbound);

		tableH.setName("tableH");

		JPopupMenu popMenu = createPopupMenu();

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

				boolean isSelected = model.isSelected();

				pnUpdateTable.setVisible(isSelected);

				spMain.setDividerLocation(isSelected?340:0);

				butEdit.setText(isSelected?"확인(E)":"편집(E)");

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

		tableGroup = new ButtonGroup();

		JRadioButton rbtCompany = new KSGRadioButton("선사별");

		rbtPage = new KSGRadioButton("페이지별",true);

		tableGroup.add(rbtCompany);

		tableGroup.add(rbtPage);

		panel.add(rbtCompany);

		panel.add(rbtPage);		

		ItemListener itemListener= new ItemListener(){

			public void itemStateChanged(ItemEvent e) {

				AbstractButton but = (AbstractButton) e.getSource();

				if(ItemEvent.SELECTED!=e.getStateChange()) return;

				String te = but.getActionCommand();

				tree.changeState(te.equals("선사별")?CustomTree.COMPAY_LIST:CustomTree.PAGE_LIST);

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

	public void createAndUpdateUI() {

		logger.debug("init searchUI start");

		this.setName("SearchUI");

		keyAdapter = new SearchOptionKeyAdapter();

		popupMenu = createPopupMenu();

		this.setLayout(new BorderLayout());

		this.add(buildCenter(),BorderLayout.CENTER);

		this.add(buildNorthPn(),BorderLayout.NORTH);

		//this.fnSearch();

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

		stfCompanyAbbr = new SearchTextField();

		stfCompanyAbbr.setPreferredSize(new Dimension(150,23));

		stfCompanyAbbr.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				SearchCompanyDialog dialog = new SearchCompanyDialog();

				dialog.createAndUpdateUI();

				stfCompanyAbbr.setText(dialog.result);
			}
		});

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

		dateModel = new UtilDateModel();

		JDatePanelImpl datePanel = new JDatePanelImpl(dateModel);

		jdatePicker = new KSGDatePickerImpl(datePanel);

		jdatePicker.setPreferredSize(new Dimension(150,23));

		KSGGradientButton butDateSearch = new KSGGradientButton("검색", "images/search3.png");

		butDateSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butDateSearch.addActionListener(this);
		
		KSGGradientButton butCancel = new KSGGradientButton("",  "images/init.png");
		
		butCancel.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		
		butCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				
				txfSearchInput.setText("");
				
				stfCompanyAbbr.setText("");
				
				cbxGubun.setSelectedIndex(0);
				
				cbbOption.setSelectedIndex(0);
				
//				
			}
		});
		

		cbbOption = new KSGComboBox();

		cbbOption.setPreferredSize(new Dimension(150,23));

		cbbOption.addItem(new KSGTableColumn("page","페이지"));

		cbbOption.addItem(new KSGTableColumn("table_id","테이블ID"));

		cbbOption.addItem(new KSGTableColumn("title","제목"));

		cbbOption.addItem(new KSGTableColumn("agent","에이전트"));

		cbbOption.addItem(new KSGTableColumn("table_index","인덱스"));


		cbbOption.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				JComboBox ss = (JComboBox) e.getSource();

				KSGTableColumn target=(KSGTableColumn) ss.getSelectedItem();

				if(target.columnName.equals("페이지")||target.columnName.equals("인덱스"))
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
				}
			}
		});

		JCheckBox box = new JCheckBox("페이지",true);

		cbxGubun = new KSGComboBox("tableType");

		cbxGubun.setShowTotal(true);

		cbxGubun.setPreferredSize(new Dimension(150,23));

		cbxGubun.initComp();

//		cbxGubun.addActionListener(new ActionListener() {
//
//			public void actionPerformed(ActionEvent arg0) 
//			{
//				fnSearch();
//			}
//		});		

		KSGPanel pnSearchMainDown = new KSGPanel();

		pnSearchMainDown.setLayout(new FlowLayout(FlowLayout.RIGHT));

		lblDivision = new JLabel("테이블 구분: ");

		lblPage = new JLabel("  페이지: ");

		lblIndex = new JLabel("  인덱스: ");

		lblItem = new JLabel("  항목: ");

		JLabel lblCompany = new JLabel("선사: ");

		Font font = new Font("맑은고딕",Font.BOLD,12);

		lblDivision.setFont(font);

		lblPage.setFont(font);

		lblIndex.setFont(font);

		lblItem.setFont(font);

		lblCompany.setFont(font);

		pnSearchMainDown.add(lblDivision);

		pnSearchMainDown.add(cbxGubun);

		pnSearchMainDown.add(lblCompany);

		pnSearchMainDown.add(stfCompanyAbbr);

		pnSearchMainDown.add(lblItem);

		pnSearchMainDown.add(cbbOption);

		pnSearchMainDown.add(txfSearchInput);

		pnSearchMainDown.add(lblDateSearch);

		//		pnSearchMainDown.add(txfDateSearch);

		pnSearchMainDown.add(jdatePicker);

		pnSearchMainDown.add(butDateSearch);
		pnSearchMainDown.add(butCancel);

		pnSearchMain.add(createSearchPanel(),BorderLayout.NORTH);

		pnSearchMain.add(pnSearchMainDown,BorderLayout.SOUTH);

		pnMain1.add(pnLogo,BorderLayout.WEST);

		pnMain1.add(pnSearchMain);

		bo.add(pnMain1);
		KSGPanel pnLeft = new KSGPanel();
		pnLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("페이지 : ",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		label.setBackground(propeties.getColor("searchtextfield.readonly") );
		label.setForeground(Color.blue.brighter());
		pnLeft.add(label);
		pnLeft.add(txfPage);		

		txfPage.setForeground(Color.blue);
		JLabel lbl = new JLabel("테이블 ID : ",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		lbl.setBackground(propeties.getColor("searchtextfield.readonly") );
		lbl.setForeground(Color.blue.brighter());

		txfTable_id.setForeground(Color.blue.brighter());
		pnLeft.add(lbl);
		pnLeft.add(txfTable_id);

		pnTableInfo.add(pnLeft,BorderLayout.WEST);

		bo.add(pnTableInfo);

		pnADVInfo.add(new JLabel("Outbound국내항:",new ImageIcon("images/buticon.png"),JLabel.LEFT));
		pnADVInfo.add(txfOutboundIn);
		pnADVInfo.add(new JLabel("Outbound외국항:",new ImageIcon("images/buticon.png"),JLabel.LEFT));
		pnADVInfo.add(txfOutboundOut);
		pnADVInfo.add(new JLabel("Inbound국내항:",new ImageIcon("images/buticon.png"),JLabel.LEFT));		
		pnADVInfo.add(txfInboundIn);
		pnADVInfo.add(new JLabel("Inbound외국항:",new ImageIcon("images/buticon.png"),JLabel.LEFT));
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

		JCheckBoxMenuItem boxMenuItem = new JCheckBoxMenuItem("Scroll 표시",true);

		boxMenuItem.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {

				JCheckBoxMenuItem m =(JCheckBoxMenuItem) arg0.getSource();

				tableH.setAutoResizeMode(m.isSelected()?JTable.AUTO_RESIZE_OFF:JTable.AUTO_RESIZE_ALL_COLUMNS);
			}
		});

		popupMenu.add(itemUpdate);

		popupMenu.add(itemDateUpdate);

		popupMenu.addSeparator();

		popupMenu.add(itemDel);

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
		tree = new CustomTree();

		tree.setComponentPopupMenu(createTreePopupmenu());

		tree.setRowHeight(25);

		tree.addTreeSelectionListener(new TreeSelectionListener(){

			public void valueChanged(TreeSelectionEvent e) {

				TreePath path=e.getNewLeadSelectionPath();

				if(path!=null)
				{
					int pathCount = path.getPathCount();
					CommandMap param = new CommandMap();

					switch (pathCount) {

					case 2:

						if(rbtPage.isSelected())
						{
							String pa = path.getLastPathComponent().toString();

							String pa1[] = pa.split("~");

							String startPage= pa1[0];

							String endPage= pa1[1];

							param.put("startPage", Integer.parseInt(startPage));

							param.put("endPage", Integer.parseInt(endPage));
						}
						else
						{
							String alphabet = path.getLastPathComponent().toString();

							param.put("alphabet",alphabet);
						}
						break;
					case 3:

						if(rbtPage.isSelected())
						{
							String ca = path.getLastPathComponent().toString();

							String ca1[] = ca.split(":");

							param.put("page", Integer.parseInt(ca1[0]));	
						}
						else
						{
							String ca = path.getLastPathComponent().toString();

							param.put("company_abbr", ca);
						}
						break;

					default:
						break;
					}

					callApi("shipperTableMgtUI2.fnSearch", param);
				}

			}});
		return tree;
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
				fnDeleteTable();
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

				pnleftMenu.setPreferredSize(new Dimension(close?_LEFT_SIZE:0,0));

				pnleftMenu.setVisible(close);

				button.setIcon(new ImageIcon("images/"+(close?"left_arrow_.gif":"right_arrow_.gif")));

				close = !close;

			}});

		pn.add(button,BorderLayout.CENTER);

		return pn;
	}

	/**
	 * 
	 */
	private void fnDeleteTable() {

		int row = tableH.getSelectedRow();

		if(row<0)return;

		HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);

		CommandMap pa = new CommandMap();

		pa.put("table_id", param.get("table_id"));

		callApi("shipperTableMgtUI2.delete", pa);

	}

	/**
	 * 광고정보 조회
	 * @param row
	 * @return
	 */
	public void searchADVTable() {

		try {

			advTablePanel.retrive();

		}catch(JDOMException | ResourceNotFoundException e)
		{	
			JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, "입력된 광고정보가 없거나 지정된 양식을 따르지 않고 있습니다.\n\n광고정보를 다시 생성해 주십시요");
		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, "error : "+e.getMessage());
		}		
	}

	private void showADVTable(String table_id) {

		logger.info("start");		

		CommandMap param = new CommandMap();

		param.put("table_id", table_id);

		callApi("showADVTable",param);

	}

	public void setPortCount(int count) {
		pnUpdateTable.setPortCount(count);

	}

	public void fnSearch()
	{
		logger.info("START");

		CommandMap param = new CommandMap();		

		if(cbxGubun.getSelectedIndex()>0)
		{
			KSGTableColumn item=(KSGTableColumn) cbxGubun.getSelectedItem();

			param.put("gubun", item.columnName);
		}

		String strParam=txfSearchInput.getText();

		if(!strParam.equals(""))
		{
			KSGTableColumn col = (KSGTableColumn) cbbOption.getSelectedItem();
			param.put(col.columnField, strParam);
		}

		String company_abbr= stfCompanyAbbr.getText();

		if(!company_abbr.equals("")) {
			param.put("company_abbr", company_abbr);
		}

		if(dateModel.isSelected()) 
		{
			param.put("date_isusse", String.format("%tF", dateModel.getValue()));
		}

		callApi("shipperTableMgtUI2.fnSearch", param);			
	}

	@Override
	public void componentShown(ComponentEvent e) {
//		fnSearch();
		//if(isShowData)
	}
	class SearchOptionKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyReleased(KeyEvent e) {

			JTextField txf =(JTextField) e.getSource();

			txf.setForeground(Color.black);

			String str = txf.getText();
			
			if(str.isEmpty() ) return;
			
			try{
				
				Integer.parseInt(str);

			}catch(NumberFormatException ee)
			{
				txf.setForeground(Color.red);
			}
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

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		boolean success = (boolean) result.get("success");

		if(success)
		{
			String serviceId = (String) result.get("serviceId");

			try 
			{

				if("shipperTableMgtUI2.init".equals(serviceId)) {

					CommandMap viewModel = (CommandMap) result.clone();

					tree.loadModel(viewModel);
					
					fnSearch();

				}
				else if("shipperTableMgtUI2.fnSearch".equals(serviceId)) {

					List data = (List )result.get("data");

					tableH.setResultData(data);

					tableH.requestFocus();

//					txfSearchInput.setText("");	
				}

				else if("shipperTableMgtUI2.update".equals(serviceId)) {

					pnUpdateTable.setSaveInfo("저장 되었습니다.");

				}

				else if("shipperTableMgtUI2.delete".equals(serviceId)) {

					fnSearch();

					JOptionPane.showMessageDialog(this, "삭제 했습니다.");

				}
				else if("shipperTableMgtUI2.selectOne".equals(serviceId)) {

					CommandMap viewModel = (CommandMap) result.clone();

					ShippersTable shippersTable= (ShippersTable) viewModel.get("selectedTable");

					if(shippersTable != null) pnUpdateTable.setShipperTableData(shippersTable);

				}

				else if("KSGADVTablePanel.save".equals(serviceId)) {

					fnSearch();

					pnADVInfo.setVisible(false);		

					tableLayout.show(pnTable, tableH.getName());
				}

				else if("deleteSchedule".equals(serviceId)) {

					int deleteCount = (int) result.get("deleteCount");

					JOptionPane.showMessageDialog(this, deleteCount+"건을 삭제 했습니다.");

					callApi("scheduleViewUpdate");
				}

				else if("showADVTable".equals(serviceId)) {

					ShippersTable shipperTable 	= (ShippersTable) result.get("shpperTable");				

					advTablePanel.setSelectedTable(shipperTable);

					advTablePanel.retrive();

					pnADVInfo.setVisible(true);

					tableLayout.show(pnTable, advTablePanel.getName());


				}
			}catch(Exception e)
			{	
				e.printStackTrace();
				JOptionPane.showMessageDialog(ShipperTableMgtUI2.this, "error : "+e.getMessage());
			}
		}
		else{  
			String error = (String) result.get("error");

			JOptionPane.showMessageDialog(this, error);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if(e.getKeyCode()==KeyEvent.VK_ENTER)
		{
			updateAndSaveShipperTableInfo();
		}
	}
}