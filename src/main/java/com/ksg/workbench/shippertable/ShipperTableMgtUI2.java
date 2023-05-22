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
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

import com.dtp.api.control.ShipperTableController;
import com.ksg.commands.SearchSubTableCommand;
import com.ksg.common.model.CommandMap;
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
import com.ksg.view.comp.HintTextField;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.KSGRadioButton;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.view.comp.table.KSGTableSelectListner;
import com.ksg.view.comp.table.groupable.ColumnGroup;
import com.ksg.view.comp.table.groupable.GroupableTableHeader;
import com.ksg.workbench.common.comp.KSGDatePickerImpl;
import com.ksg.workbench.common.comp.button.KSGGradientButton;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.textfield.SearchTextField;
import com.ksg.workbench.common.comp.tree.CustomTree;
import com.ksg.workbench.common.comp.tree.KSGTreeDefault;
import com.ksg.workbench.shippertable.comp.KSGADVTablePanel;
import com.ksg.workbench.shippertable.comp.UpdateTablePanel;
import com.ksg.workbench.shippertable.dialog.AddTableInfoDialog;
import com.ksg.workbench.shippertable.dialog.AddTableInfoDialog_temp;
import com.ksg.workbench.shippertable.dialog.SearchCompanyDialog;
import com.ksg.workbench.shippertable.dialog.UpdateShipperTableDateDialog;

import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

/**

 * @FileName : ShipperTableMgtUI2.java

 * @Project : KSG2

 * @Date : 2022. 3. 15. 

 * @�ۼ��� : pch

 * @�����̷� :

 * @���α׷� ���� : ���� ���� ��ȸ ȭ��

 */
@SuppressWarnings("unchecked")
public class ShipperTableMgtUI2 extends ShipperTableAbstractMgtUI
{	
	private static final String ACTION_SEARCH = "��ȸ";

	private static final String ACTION_UPDATE_DATE = "�Է����� ����";

	/**
	 * @author ��â��
	 * @���̺����� ���콺 ����
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

	private static final int _LEFT_SIZE = 250;

	public static final int ADV_TYPE = 0;

	private static final long serialVersionUID = 1L;

	private static final String STRING_ERROR_NO_ADV_INFO = "���������� �����ϴ�. ���� ������ �߰� �Ͻðڽ��ϱ�?";

	public static final int TABLE_TYPE = 1;

	private static KSGTreeDefault tree2;

	private KSGPanel 			pnADVInfo, pnleftMenu, pnShipperInfo,pnTable;	

	private Box 				pnSearchInfoMain;

	private UpdateTablePanel 	pnUpdateTable;

	private JPopupMenu 			popupMenu;	

	private KSGTablePanel tableH;

	private JTable				currentTable;

	private JTree				_treeMenu;

	private JTextField 			txfInboundIn,txfInboundOut,txfOutboundIn,txfOutboundOut,txfSearchInput;

	private JTextField 			txfPageSearchInput;

	private JTextField 			txfPageIndexSearchInput;

	private AddTableInfoDialog addTableInfoDialog;

	private KSGADVTablePanel advTablePanel;// �������� ����ȭ��

	private JToggleButton butEdit; //

	private boolean 			close=true;	

	private SearchTextField stfCompanyAbbr;

	private JMenuItem delMenu;

	private int depth=1; //

	private final int DEPTH_PAGE=3; //

	private final int DEPTH_ROOT=1; //

	private final int DEPTH_SUB=2; //

	private UpdateShipperTableDateDialog updateAllDateDialog; //��¥ �ϰ� ���� â		

	public KSGTableSelectListner listner;

	public List 				_searchedList, selectedList;

	private JSplitPane spMain;	

	private CardLayout 			tableLayout;

	private JTextField txfCompany,txfCount,txfDate,txfDateSearch,txfPage,txfTable_id, txfImportDate;

	private KSGComboBox cbbOption;

	private KSGComboBox cbxGubun;

	private ShippersTable searchParam;

	private HashMap<String, Object> searchParamHash = new HashMap<String, Object>();

	SearchOptionKeyAdapter keyAdapter;

	private JLabel lblCompany,lblCount,lblDivision, lblPage, lblIndex, lblItem, lblDateSearch;

	private JMenuItem itemDateUpdate;

	private ShipperTableService shipperTableService;

	private ADVServiceImpl _advService;

	private TableService tableService;

	private CustomTree tree;

	UtilDateModel dateModel;

	private ButtonGroup tableGroup;

	private JRadioButton rbtPage;

	CommandMap viewModel = new CommandMap();

	public ShipperTableMgtUI2() 
	{	
		super();

		selectedList = new LinkedList();

		tableService = new TableServiceImpl();

		_advService= new ADVServiceImpl();

		shipperTableService = new ShipperTableServiceImpl();

		this.addComponentListener(this);

		this.setController(new ShipperTableController());

		this.title="�������� ����";

		this.borderColor=new Color(91,152,185);

		createAndUpdateUI(); 

		callApi("shipperTableMgtUI2.init");

	}

	private void insertAction()
	{
		int row=tableH.getSelectedRow();

		ShippersTable param = null;

		if(row>-1)
		{
			HashMap<String, Object> item = (HashMap<String, Object>) tableH.getValueAt(row);

			param = new ShippersTable();
			param.setTable_id((String) item.get("table_id"));
		}

		addTableInfoDialog = new AddTableInfoDialog(this,param);

		addTableInfoDialog.createAndUpdateUI();


	}
	public void actionPerformed(ActionEvent e) { 

		String command = e.getActionCommand();		

		if(command.equals("���̺�˻�:Page"))
		{
			JDialog dialog = new JDialog();

			KSGPanel pnMain = new KSGPanel();

			final JTextField txfSearchInput = new JTextField(15);

			JLabel lblSearchPage = new JLabel("Page : ");
			JButton butSubmit = new JButton("�˻�",new ImageIcon("images/buticon.gif"));
			butSubmit.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {

				}});

			pnMain.add(lblSearchPage);
			pnMain.add(txfSearchInput);
			pnMain.add(butSubmit);


			dialog.getContentPane().add(pnMain);
			dialog.pack();
			ViewUtil.center(dialog);
			dialog.setVisible(true);
		}

		else if(command.equals("�űԵ��"))
		{	

			if(addTableInfoDialog==null||!addTableInfoDialog.isVisible())
			{
				insertAction();
			}


		}else if(command.equals("���̺����"))
		{
			delAction();

		}
		else if(command.equals("�ױ�����"))
		{
			//updatePortAction();

		}
		else if(command.equals("���ں���"))
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

		else if(command.equals("�������"))
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
	 * ��ҽ� ���� �˻� ȭ�� ǥ��
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


		tableH = new KSGTablePanel("������");



		tableH.addColumn(new KSGTableColumn("page", "������"));
		tableH.addColumn(new KSGTableColumn("table_index", "�ε���"));
		tableH.addColumn(new KSGTableColumn("table_id", "���̺�ID", 125));
		tableH.addColumn(new KSGTableColumn("date_isusse", "�Է�����", 100));
		tableH.addColumn(new KSGTableColumn("port_col", "�ױ���",50));
		tableH.addColumn(new KSGTableColumn("vsl_row", "���ڼ�",50));		
		tableH.addColumn(new KSGTableColumn("company_abbr", "����",135,SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("agent", "������Ʈ",135, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("title", "����",250, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("gubun", "����"));
		tableH.addColumn(new KSGTableColumn("TS", "TS�ױ�"));
		tableH.addColumn(new KSGTableColumn("in_port", "�����",110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("in_to_port", "������",110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("out_port", "�����", 110, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("out_to_port", "������",110, SwingConstants.LEFT ));

		//TODO �÷� ��� ����

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


		tableH.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_ENTER)
				{

				}

			}

			public void keyReleased(KeyEvent arg0) {
				try {
					//JTable table = (JTable) arg0.getSource();

					if(arg0.getKeyCode()== KeyEvent.VK_ENTER)
					{



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

		butEdit = new JToggleButton("����(E)",new ImageIcon("images/editClose.gif"));

		butEdit.setMnemonic(KeyEvent.VK_E);

		butEdit.setSelectedIcon(new ImageIcon("images/editOpen.gif"));

		butEdit.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JToggleButton but =(JToggleButton) e.getSource();
				ButtonModel model =but.getModel();

				changeUpdatePNState(model.isSelected());

			}});


		JButton butCreateADV = new JButton("���� ���( + N)");

		butCreateADV.setActionCommand("���� ���");

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

		JRadioButton rbtCompany = new KSGRadioButton("���纰");

		rbtPage = new KSGRadioButton("��������",true);

		tableGroup.add(rbtCompany);

		tableGroup.add(rbtPage);

		panel.add(rbtCompany);

		panel.add(rbtPage);		

		ItemListener itemListener= new ItemListener(){

			public void itemStateChanged(ItemEvent e) {

				AbstractButton but = (AbstractButton) e.getSource();

				if(ItemEvent.SELECTED==e.getStateChange())
				{
					String te = but.getActionCommand();

					if(te.equals("���纰"))
					{
						tree.changeState(CustomTree.COMPAY_LIST);
					}
					else if(te.equals("��������"))
					{
						tree.changeState(CustomTree.PAGE_LIST);

					}

				}
			}};
			rbtCompany.addItemListener(itemListener);

			rbtPage.addItemListener(itemListener);

			panel.add(new JSeparator(JSeparator.HORIZONTAL));

			JButton butADDTable = new JButton(new ImageIcon("images/plus.gif"));


			butADDTable.setPreferredSize(new Dimension(35,25));

			butADDTable.setFocusPainted(false);

			butADDTable.setActionCommand("�űԵ��");

			butADDTable.setToolTipText("�ű� ���̺� ���");

			butADDTable.addActionListener(this);

			panel.add(butADDTable);

			JButton butDelTable = new JButton(new ImageIcon("images/minus.gif"));
			butDelTable.setPreferredSize(new Dimension(35,25));
			butDelTable.setFocusPainted(false);
			butDelTable.setActionCommand("���̺����");
			butDelTable.addActionListener(this);
			panel.add(butDelTable);


			KSGPanel pnTitle = new KSGPanel();
			pnTitle.setBackground(new Color(88,141,250));


			pnTitle.setLayout(new FlowLayout(FlowLayout.LEFT));

			JLabel label = new JLabel("���̺� ���");

			label.setForeground(Color.white);

			pnTitle.add(label);

			pnTitle.setPreferredSize( new Dimension(0,22));


			pnMain.add(pnSearch,BorderLayout.NORTH);

			pnMain.add(panel,BorderLayout.SOUTH);

			KSGPanel pnArrow = new KSGPanel()
					;
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
			butEdit.setText("Ȯ��(E)");

		}
		else
		{
			spMain.setDividerLocation(0);
			pnUpdateTable.setVisible(false);
			butEdit.setText("����(E)");

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
	 * ����:
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

		txfCompany = new JTextField(10);

		txfCompany.setEditable(false);

		txfDate = new HintTextField("2000.1.1",8);

		txfDate.setEditable(false);

		txfDate.setBorder(BorderFactory.createEmptyBorder());

		txfCount = new JTextField(2);

		txfCount.setEditable(false);

		lblCompany = new JLabel();

		lblCompany.setFont(new Font("����",0,15));

		Box bo = new Box(BoxLayout.Y_AXIS);

		KSGPanel pnMain1 = new KSGPanel();

		pnMain1.setLayout(new BorderLayout());

		KSGPanel pnLogo = new KSGPanel();

		pnLogo.setLayout( new FlowLayout(FlowLayout.LEFT));

		lblCompany.setIcon(new ImageIcon("images/table.png"));

		pnLogo.add(lblCompany);

		KSGPanel pnSearchMain = new KSGPanel();

		pnSearchMain.setLayout(new BorderLayout());

		lblDateSearch = new JLabel("  �Է�����: ");

		lblDateSearch.setFont(new Font("�������", Font.BOLD, 12));

		dateModel = new UtilDateModel();

		JDatePanelImpl datePanel = new JDatePanelImpl(dateModel);

		KSGDatePickerImpl jdatePicker = new KSGDatePickerImpl(datePanel);

		jdatePicker.setPreferredSize(new Dimension(150,23));

		KSGGradientButton butDateSearch = new KSGGradientButton("�˻�", "images/search3.png");

		butDateSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butDateSearch.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				searchParamHash = new HashMap<String, Object>();
				fnSearch();
			}
		});

		cbbOption = new KSGComboBox();
		cbbOption.setPreferredSize(new Dimension(150,23));
		cbbOption.addItem(new KSGTableColumn("page","������"));
		cbbOption.addItem(new KSGTableColumn("table_id","���̺�ID"));
		cbbOption.addItem(new KSGTableColumn("title","����"));
		cbbOption.addItem(new KSGTableColumn("agent","������Ʈ"));
		cbbOption.addItem(new KSGTableColumn("table_index","�ε���"));


		cbbOption.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JComboBox ss = (JComboBox) e.getSource();

				KSGTableColumn target=(KSGTableColumn) ss.getSelectedItem();

				if(target.columnName.equals("������")||target.columnName.equals("�ε���"))
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

		JCheckBox box = new JCheckBox("������",true);
		box.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box =(JCheckBox) e.getSource();


			}});

		cbxGubun = new KSGComboBox("tableType");
		cbxGubun.setShowTotal(true);
		cbxGubun.setPreferredSize(new Dimension(150,23));


		cbxGubun.initComp();

		cbxGubun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) 
			{
				fnSearch();

			}
		});		

		KSGPanel pnSearchMainDown = new KSGPanel();
		pnSearchMainDown.setLayout(new FlowLayout(FlowLayout.RIGHT));

		lblDivision = new JLabel("���̺� ����: ");
		lblPage = new JLabel("  ������: ");
		lblIndex = new JLabel("  �ε���: ");
		lblItem = new JLabel("  �׸�: ");

		JLabel lblCompany = new JLabel("����: ");

		Font font = new Font("�������",Font.BOLD,12);

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
		pnSearchMain.add(createSearchPanel(),BorderLayout.NORTH);
		pnSearchMain.add(pnSearchMainDown,BorderLayout.SOUTH);

		pnMain1.add(pnLogo,BorderLayout.WEST);
		pnMain1.add(pnSearchMain);

		bo.add(pnMain1);
		KSGPanel pnLeft = new KSGPanel();
		pnLeft.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("������ : ",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		label.setForeground(Color.blue.brighter());
		pnLeft.add(label);
		pnLeft.add(txfPage);		

		txfPage.setForeground(Color.blue);
		JLabel lbl = new JLabel("���̺� ID : ",new  ImageIcon("images/buticon.png"),JLabel.LEFT);
		lbl.setForeground(Color.blue.brighter());

		txfTable_id.setForeground(Color.blue.brighter());
		pnLeft.add(lbl);
		pnLeft.add(txfTable_id);

		pnTableInfo.add(pnLeft,BorderLayout.WEST);

		bo.add(pnTableInfo);

		JLabel lblOutboundIn = new JLabel("Outbound������:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnADVInfo.add(lblOutboundIn);
		pnADVInfo.add(txfOutboundIn);
		JLabel lblOutboundOut = new JLabel("Outbound�ܱ���:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnADVInfo.add(lblOutboundOut);
		pnADVInfo.add(txfOutboundOut);
		JLabel lblInboundIn = new JLabel("Inbound������:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnADVInfo.add(lblInboundIn);		
		pnADVInfo.add(txfInboundIn);
		JLabel lblInboundOut = new JLabel("Inbound�ܱ���:",new ImageIcon("images/buticon.png"),JLabel.LEFT);
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
	 * �˾� �޴� ����
	 * @return
	 */
	private JPopupMenu createPopupMenu() {
		JPopupMenu popupMenu = new JPopupMenu();

		JMenuItem itemUpdate = new JMenuItem("�ױ�����");
		itemUpdate.setActionCommand("�ױ�����");
		itemUpdate.addActionListener(this);

		itemDateUpdate = new JMenuItem("���ں���");
		itemDateUpdate.setActionCommand("���ں���");
		itemDateUpdate.addActionListener(this);		

		JMenuItem itemDel = new JMenuItem("���̺����");
		itemDel.addActionListener(this);

		JMenuItem itemADVDel = new JMenuItem("�������");
		itemADVDel.addActionListener(this);

		JCheckBoxMenuItem boxMenuItem = new JCheckBoxMenuItem("Scroll ǥ��",true);
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
	 * ���� Ʈ�� �޴� ����
	 * @return
	 */
	private JTree createTreeMenu() 
	{

		tree = new CustomTree();

		//		tree = new KSGTreeDefault("tree1");

		tree.setComponentPopupMenu(createTreePopupmenu());

		tree.setRowHeight(25);

		//		tree.update();

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


		JMenuItem itemTable = new JMenuItem("�� ���̺�...");
		itemTable.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				AddTableInfoDialog_temp addTableInfoDialog = new AddTableInfoDialog_temp(ShipperTableMgtUI2.this,manager.selectedCompany);
				addTableInfoDialog.createAndUpdateUI();
			}});
		delMenu = new JMenuItem("���̺� ����");

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
	 *  ���� �����̵� ��ư �ۼ�
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
				JOptionPane.showMessageDialog(this, param.get("table_id")+ "�� ���� �߽��ϴ�.");
				fnUpdate();
			}
		} catch (Exception e) {

			e.printStackTrace();
		}


	}

	/**
	 * �������� ����
	 */
	private void delADVAction() {



		int row = tableH.getSelectedRow();
		if(row<0)return;

		HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);

		String company_abbr = (String) param.get("company_abbr");
		String table_id =  (String) param.get("table_id");


		int j=JOptionPane.showConfirmDialog(this ,company_abbr+"��"+ table_id+
				" ���������� �����Ͻðڽ��ϱ�?","���� ���� ����",JOptionPane.OK_CANCEL_OPTION);
		if(j==JOptionPane.YES_OPTION)
		{
			try {
				logger.debug("option result "+j);
				int result=_advService.removeADVData(table_id, manager.selectedDate);
				logger.debug("del result "+result);
				if(result==1)
				{
					JOptionPane.showMessageDialog(this, "���������� �����߽��ϴ�.");
					ADVData data = new ADVData();
					data.setTable_id(manager.selectedTable_id);
					data.setCompany_abbr(company_abbr);
					tableService.updateTableDate(data);
					manager.execute(this.getName());
				}else if(result==0)
				{
					JOptionPane.showMessageDialog(null, "���������� �����ϴ�.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				if(e.getErrorCode()==1451)
					JOptionPane.showMessageDialog(null, e.getErrorCode()+","+"���� ������� �����Ͻðڽ��ϱ�?");
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
		lblCompany.setText("����� : "+com);

		txfCompany.setText(com);
		txfPage.setText(page);
		manager.selectedCompany=com;
		manager.selectedPage=Integer.parseInt(page);
	}
	/**
	 * �������� ��ȸ
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

	public void setPortCount(int count) {
		pnUpdateTable.setPortCount(count);

	}

	public void fnUpdate()
	{
		try {
			//			int page_size = tableH.getPageSize();

			searchParamHash.put("PAGE_SIZE", 0);

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



	private void selectGubun(ShippersTable searchParam, int gubunIndex )
	{
		switch (gubunIndex) {
		case 0:
			searchParam.setGubun(null);
			break;
		case 1:
			searchParam.setGubun(ShippersTable.GUBUN_NORMAL);// ���� �ʿ�
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

		case 0://������

			searchParam.setPage(Integer.parseInt(param));

			manager.selectedPage = Integer.parseInt(param);

			break;
		case 1:// ���̺� ���̵�
			searchParam.setTable_id(param);
			break;
		case 5://���̺� �ε���

			searchParam.setTable_index(Integer.parseInt(param));

			break;
		case 2://�����
			searchParam.setCompany_abbr(param);
			break;
		case 3:// ����
			searchParam.setTitle(param);
			break;
		case 4:// ������Ʈ
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

		case 0://������

			searchOption.put("page", Integer.parseInt(param));

			break;
		case 1:// ���̺� ���̵�

			searchOption.put("table_id", param);

			break;
		case 5://���̺� �ε���
			searchOption.put("table_index", Integer.parseInt(param));			

			break;
		case 2://�����
			searchOption.put("company_abbr", param);

			break;
		case 3:// ����
			searchOption.put("title", param);

			break;
		case 4:// ������Ʈ
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

			this.setTitle("��¥���� ����");

			lblDate = new JLabel(" �Է³�¥ : ");

			lblDate.setFont(KSGModelManager.getInstance().defaultFont);

			txfImportDate = new JTextField(8);

			JCheckBox cbxImportDate = new JCheckBox("������",false);

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
			JButton butOk = new JButton("Ȯ��");
			butOk.addActionListener(this);
			butOk.setFont(KSGModelManager.getInstance().defaultFont);
			JButton butCancel = new JButton("���");
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
			if(command.equals("���"))
			{				
				close();
			}
			else if(command.equals("Ȯ��"))
			{
				try {
					String date=txfImportDate.getText();

					tableService.updateTableDateByTableIDs(list,KSGDateUtil.toDate3(date).toString());

					result=1;

					close();

					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "��¥�� �����߽��ϴ�.");

				} catch (SQLException e1) {

					e1.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
				} catch (DateFormattException e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�Է� ����(2000.1.1)�� Ʋ�Ƚ��ϴ�. "+e2.getMessage());
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

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		boolean success = (boolean) result.get("success");

		if(success)
		{
			String serviceId = (String) result.get("serviceId");

			if("shipperTableMgtUI2.init".equals(serviceId)) {

				viewModel = (CommandMap) result.clone();

				tree.loadModel(viewModel);

			}
			else if("shipperTableMgtUI2.fnSearch".equals(serviceId)) {

				List data = (List )result.get("data");

				tableH.setResultData(data);

				//				tableH.setTotalCount(data.size());

				tableH.requestFocus();

				txfSearchInput.setText("");	

			}

			else if("deleteSchedule".equals(serviceId)) {

				int deleteCount = (int) result.get("deleteCount");

				JOptionPane.showMessageDialog(this, deleteCount+"���� ���� �߽��ϴ�.");

				callApi("scheduleViewUpdate");
			}
		}
		else{  
			String error = (String) result.get("error");
			JOptionPane.showMessageDialog(this, error);
		}
	}	
}



