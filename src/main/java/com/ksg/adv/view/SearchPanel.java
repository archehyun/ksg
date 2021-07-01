package com.ksg.adv.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ksg.adv.logic.model.SheetInfo;
import com.ksg.adv.view.comp.PageCellRenderer;
import com.ksg.adv.view.comp.SheetModel;
import com.ksg.adv.view.comp.SimpleFileFilter;
import com.ksg.adv.view.dialog.SearchCompanyAndPageDialog;
import com.ksg.adv.view.dialog.SheetSelectDialog;
import com.ksg.adv.view.dialog.ViewXLSFileDialog;
import com.ksg.commands.ImportTextCommand;
import com.ksg.commands.SearchPortCommand;
import com.ksg.commands.SearchSheetNameCommand;
import com.ksg.commands.xls.ImportXLSFileCommand;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.util.ViewUtil;
import com.ksg.common.view.comp.FileInfo;
import com.ksg.common.view.comp.KSGCompboBox;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.common.view.comp.KSGTable;
import com.ksg.common.view.comp.KSGTableImpl;
import com.ksg.common.view.comp.PageInfo;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Property;
import com.ksg.shippertable.service.TableService;
import com.ksg.shippertable.service.impl.TableServiceImpl;
import com.ksg.shippertable.view.comp.SearchTable;
import com.ksg.view.KSGViewParameter;

public class SearchPanel extends JPanel implements ActionListener{

	public boolean 		isPageSearch=true;

	private boolean 		isSamePageSelect=true;

	private JList 			companyLi; // 선사 목록

	private JList 			pageLi; // 페이지 목록

	private JButton 		butAdjust,butCompanyAdd,butPre,butNext;

	public JList getPageLi() {
		return pageLi;
	}

	private TableService 	tableService;

	private Vector<ShippersTable> tableInfoList;
	
	SearchCompanyAndPageDialog searchCompanyAndPageDialog;

	private String			selectedInput="File";

	public JTextField		_txfXLSFile,_txfSearchedTableCount,txfCompany,_txfDate;

	public KSGModelManager manager = KSGModelManager.getInstance();

	private static int _tableViewCount = 10;

	private Vector<KSGXLSImportPanel> importTableList;
	
	public static final String SEARCH_TYPE_COMPANY = "선사";

	private JTextField  	_txfCPage,txfPage,txfPCompany,_txfPort;

	private static final String SEARCH_TYPE_PAGE = "페이지";

	private String selectedCompany;
	
	private ShipperTableListTable listTable;
	
	private SheetSelectDialog dialog;

	public String getSelectedCompany() {
		return selectedCompany;
	}

	protected Logger 		logger = Logger.getLogger(this.getClass());
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private KSGPropertis 	propertis = KSGPropertis.getIntance();

	private String 			selectXLSFilePath;// 선택된 엑셀 파일 경로

	private JTable _tblSheetNameList;// 선택된 엑셀 파일의 쉬트 이름 목록

	private KSGCompboBox comp;

	private JTextField txfTableCount;

	private JComboBox cbxSearchType;

	private JPanel pnSubSearch, pnSubSelect, pnTableInfo;

	private CardLayout selectLay2;

	private CardLayout selectLay;

	private ButtonGroup bgKeyword;

	private JRadioButton butVesselOpt, butVoyageOpt;

	private JComboBox cbxSelectedInput;

	private KSGTableImpl tblError;

	private JTable tblPropertyTable;

	private JTable tblTable;

	private JList fileLi;

	private String 			searchType=SEARCH_TYPE_COMPANY;

	public String getSearchType() {
		return searchType;
	}

	private DAOManager daoManager = DAOManager.getInstance();

	private String selectedPage;

	private JComboBox cbxKeyWordType;
	
	private SearchTable searchTable;

	private JTabbedPane mainTab;

	private Font lblFont = new Font("맑은고딕",Font.BOLD,12);;
	
	ADVListPanel advListPanel;

	public void setAdvListPanel(ADVListPanel advListPanel) {
		this.advListPanel = advListPanel;
	}
	ADVManageUI manageUI;
	public SearchPanel(ADVManageUI manageUI) {
		
		this.manageUI = manageUI;

		initComp();

		tableService = new TableServiceImpl();



		_tblSheetNameList = new JTable();

		comp = new KSGCompboBox("vessel",KSGCompboBox.TYPE1);

		txfTableCount = new JTextField(2);

		JComboBox box = new JComboBox();

		for(int i=1;i<10;i++)
		{
			box.addItem(i);
		}
		box.setSelectedIndex(0);
		box.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JComboBox b =(JComboBox) e.getSource();
				_tableViewCount=(Integer)b.getSelectedItem();

				butNext.setEnabled(false);
				butPre.setEnabled(false);

				updateUI();

			}});

		comp.setPreferredSize(new Dimension(100,25));

		setLayout(new BorderLayout());

		Box pnControl= new Box(BoxLayout.X_AXIS);

		JLabel lblPage = new JLabel("페이지 : ");

		JPanel pnSubControl2 = new JPanel();

		pnSubControl2.setLayout(new FlowLayout(FlowLayout.LEADING));

		txfTableCount.setText(_tableViewCount+"");



		tblError = new KSGTableImpl(KSGTableImpl.TABLE_TYPE_ERROR);

		tblError.setName("error");

		manager.addObservers(tblError);

		tblError.setComponentPopupMenu(createErrorPopupMenu());

		mainTab = new JTabbedPane();

		JPanel pnPropety = new JPanel();
		
		pnPropety.setLayout(new BorderLayout());
		
		tblPropertyTable = new JTable();

		pnPropety.add(new JScrollPane(tblPropertyTable));

		pnTableInfo =new JPanel();
		
		listTable = new ShipperTableListTable();
		
		tblTable = new JTable();

		tblTable.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {

				ShippersTable table = new ShippersTable();
				int row=tblTable.getSelectedRow();

				int col= tblTable.getSelectedColumn();

				if(row==-1)
					return;

				table.setTable_id((String) tblTable.getValueAt(row, 0));

			}
		});
		
		tblTable.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		
		tblTable.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()>=1)
				{
					JTable table = (JTable) e.getSource();

					int row=table.getSelectedRow();

					String company_abbr=(String) table.getValueAt(row, 0);

					logger.debug("selected Company:"+company_abbr);
				}
			}
		});

		pnTableInfo.setLayout(new BorderLayout());

		JScrollPane jScrollPane = new JScrollPane(listTable);
		jScrollPane.getViewport().setBackground(Color.white);
		pnTableInfo.add(jScrollPane,BorderLayout.CENTER);

		//tabbedPane.addTab("테이블 목록", buildTableInfo());
		
		mainTab.addTab("테이블 정보",pnTableInfo);
		
		//mainTab.addTab("결과", advListPanel);

		

		//tabbedPane.addTab("History", pnPropety);

		add(buildSearchOption(),BorderLayout.NORTH);

		add(mainTab,BorderLayout.CENTER);
	}
	
	private JPanel buildTableInfo()	
	{
		JPanel pnMain = new JPanel(new BorderLayout());
		searchTable = new SearchTable();
		pnMain.add(new JScrollPane(searchTable));
		
		return pnMain;
		
	}


	/**
	 * @return
	 */
	private JPanel buildFileListPn() {
		JPanel pnMain = new JPanel(new BorderLayout());
		
		JLabel lblFileName = new JLabel("파일 명: ");

		this._txfXLSFile.setVisible(false);	

		JButton butFile = new JButton("추가(A)");
		butFile.setMnemonic(KeyEvent.VK_A);
		butFile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				fileAddAction(fileLi,_tblSheetNameList);
			}

		});

		JPanel pnButList = new JPanel();
		pnButList.setPreferredSize(new Dimension(275,25));
		pnButList.setLayout(new GridLayout(1,0));

		pnButList.add(butFile);
		JButton butDel = new JButton("삭제(D)");
		butDel.setMnemonic(KeyEvent.VK_D);
		butDel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				fileDelAction(fileLi,_tblSheetNameList);

			}});
		pnButList.add(butDel);
		JButton butUp = new JButton("위로");
		butUp.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				fileUPAction(fileLi);
			}

		});
		pnButList.add(butUp);

		JButton butDown = new JButton("아래로");
		butDown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				fileDownAction(fileLi);

			}
		});
		pnButList.add(butDown);

		JPanel pnFile = new JPanel();
		pnFile.setLayout(new BorderLayout());
		fileLi = new JList();
		fileLi.setComponentPopupMenu(createXLSListPopup());

		fileLi.setModel(new DefaultListModel());
		JScrollPane scrollPane = new JScrollPane(fileLi);

		scrollPane.setPreferredSize(new Dimension(160,100));
		
		JButton butImportFile = new JButton("\n불러오기(V)");
		butImportFile.setMnemonic(KeyEvent.VK_V);
		butImportFile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				SearchPanel.this.advListPanel.initInfo();
				logger.debug("import xls");
				if(selectedInput.equals("File"))
				{
					searchXLS();
					//actionImportADVInfo();

				}else
				{
					//importADVTextInfoAction();
				}
			}});

		pnFile.add(scrollPane);
		pnFile.add(pnButList,BorderLayout.SOUTH);

		pnMain.add(pnFile);
		JPanel pnBut = new JPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createTitledBorder("파일 목록"));
		return pnMain;
	}


	/**
	 * @return
	 */
	private JPanel buildPageList()
	{
		return new PageListPanel();
	}

	/**
	 * @return
	 */
	private JPanel buildFileSelectPn() {
		JPanel pnMain = new JPanel(new FlowLayout(FlowLayout.LEFT));

		pnSubSelect = new JPanel();		
		selectLay = new CardLayout();		
		pnSubSelect.setLayout(selectLay);		
		pnSubSelect.add(buildFileListPn(),"File");		
		pnSubSelect.add(buildTextSelectPn(),"Text");
		
		pnMain.add(buildPageList());
		pnMain.add(pnSubSelect);		

		return pnMain;
	}

	/**
	 * @return
	 */
	private JPanel buildKeyType()	
	{
		JPanel pnKeyTypeMain = new JPanel(new BorderLayout());

		pnKeyTypeMain.setAlignmentY(JPanel.TOP_ALIGNMENT);
		JPanel pnKeyType = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lblKeyWord = new JLabel("키워드 형식: ");
		lblKeyWord.setHorizontalAlignment(JLabel.RIGHT);
		lblKeyWord.setPreferredSize(new Dimension(80, 20));
		lblKeyWord.setFont(lblFont);


		//	JPanel pnSelectType = new JPanel(new FlowLayout(FlowLayout.LEFT));


		JPanel pnInputType = new JPanel();
		pnInputType.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnKeyType.add(lblKeyWord);
		pnKeyType.add(cbxKeyWordType);


		pnKeyTypeMain.add(pnKeyType,BorderLayout.WEST);
		
		return pnKeyTypeMain;
	}
	

	/**
	 * @return
	 */
	private JComponent buildSearchOption()
	{
		JPanel pnMain= new JPanel(new BorderLayout());

		// 검색 형식 : 페이지, 선사
		JPanel pnSearchType = new JPanel(new FlowLayout(FlowLayout.LEFT));

		pnSubSearch = new JPanel();		
		selectLay2 = new CardLayout();		
		pnSubSearch.setLayout(selectLay2);
		pnSubSearch.add( buildCompanyInfoByCompany(),SEARCH_TYPE_COMPANY);
		//pnSubSearch.add(buildCompanyInfoByPage(),SEARCH_TYPE_PAGE);	

		JLabel lblSearch= new JLabel("검색 형식: ");
		
		lblSearch.setFont(lblFont);

		lblSearch.setPreferredSize(new Dimension(75, 20));
		lblSearch.setHorizontalAlignment(JLabel.RIGHT);

		//pnSearchType.add(lblSearch);
		//pnSearchType.add(cbxSearchType);
		pnSearchType.add(pnSubSearch);

		GridLayout gridLayout = new GridLayout(0,1);

		JPanel pnSearchTypeMain = new JPanel(gridLayout);

		//	pnSearchType.setBorder(BorderFactory.createEtchedBorder());
		
		JLabel  lblInputType = new JLabel("입력 형식: ");
		lblInputType.setPreferredSize(new Dimension(80, 20));
		lblInputType.setHorizontalAlignment(JLabel.RIGHT);
		lblInputType.setFont(lblFont);
		
		JCheckBox cbkCheck = new JCheckBox("동일 선사 추가 선택",isSamePageSelect);
		cbkCheck.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();

				isSamePageSelect=box.isSelected();
			}});
		JPanel pnType2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnType2.add(lblInputType);		
		pnType2.add(cbxSelectedInput);
		pnType2.add(cbkCheck);
		
		
		pnSearchTypeMain.add(pnSearchType);
		pnSearchTypeMain.add(buildKeyType());
		pnSearchTypeMain.add(pnType2);


		//입력 형식 : 파일, 텍스트		



		// 조회 형식 : Vessel, Voyage

		/*bgKeyword = new ButtonGroup();		
		butVesselOpt = new JRadioButton("Vessel",true);		
		butVoyageOpt = new JRadioButton("Voyage");		
		bgKeyword.add(butVesselOpt);		
		bgKeyword.add(butVoyageOpt);		*/



		JPanel pnImportBut = new JPanel( new BorderLayout());

		JButton butSheetSelect = new JButton("Sheet 선택");
		butSheetSelect.setVisible(false);

		butSheetSelect.addActionListener(this);
		
		JButton butCancel = new JButton("취소하기");
		butCancel.setVisible(false);
		butCancel.addActionListener(this);
		
		JPanel pnSeachs =  new JPanel(new FlowLayout(FlowLayout.LEFT));
			
		
		JButton butImportFile = new JButton("\n불러오기(V)");
		
		butImportFile.setActionCommand("불러오기");

		butImportFile.setMnemonic(KeyEvent.VK_V);

		butImportFile.addActionListener(this);
		
		pnImportBut.setPreferredSize(new Dimension(100,130));
		
		//pnImportBut.add(butSheetSelect);
		pnImportBut.add(butImportFile, BorderLayout.SOUTH);
		//pnImportBut.add(butCancel);
		
		pnSeachs.add(pnSearchTypeMain);
		pnSeachs.add(buildFileSelectPn());
		pnSeachs.add(pnImportBut);
		
		

		pnMain.add(pnSeachs);	

		//pnMain.add(pnImportBut,BorderLayout.EAST);

		return pnMain;
	}



	/**
	 * @return
	 */
	private Vector getSelectedSheetList(JTable _tblSheetNameList) {
		// 선택된 쉬트 파악
		int row =_tblSheetNameList.getRowCount();
		Vector sheetList = new Vector();
		for(int i=0;i<row;i++)
		{
			Boolean use=(Boolean) _tblSheetNameList.getValueAt(i, 3);
			if(use.booleanValue())
			{
				SheetInfo sheetInfo=new SheetInfo();
				sheetInfo.filePath = (String) _tblSheetNameList.getValueAt(i, 0);
				sheetInfo.sheetName = (String) _tblSheetNameList.getValueAt(i, 2);
				sheetList.add(sheetInfo);
			}
		}
		logger.debug("end:"+sheetList.size());
		return sheetList;

	}
	private void fileAddAction(JList fileLi,JTable table) {

		logger.debug("file add:");
		JFileChooser fileChooser = new JFileChooser(propertis.getProperty("dataLocation"));
		fileChooser.setMultiSelectionEnabled(true);
		String[] pics = new String[] { ".xls"};

		fileChooser.addChoosableFileFilter(new SimpleFileFilter(pics,
				"Excel(*.xls)"));


		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File[] selectedFiles = fileChooser.getSelectedFiles();


			for(int i=0;i<selectedFiles.length;i++)
			{
				DefaultListModel filemodel=(DefaultListModel) fileLi.getModel();

				for(int j=0;j<filemodel.size();j++)
				{
					FileInfo t = (FileInfo) filemodel.get(j);
					if(t.file.equals(selectedFiles[i].getName()))
					{
						JOptionPane.showMessageDialog(null, "동일 항목이 존재합니다.");
						return;
					}
				}
				FileInfo fileInfo= new FileInfo();
				fileInfo.file = selectedFiles[i].getName();
				fileInfo.filePath = selectedFiles[i].getAbsolutePath();
				KSGPropertis pp=KSGPropertis.getIntance();
				// 위치 저장
				pp.setProperty(KSGPropertis.DATA_LOCATION, selectedFiles[i].getParent());
				pp.store();


				filemodel.addElement(fileInfo);

				selectXLSFilePath = selectedFiles[i].getAbsolutePath();


				updateSheetNameList(fileLi,table);
			}
		}
	}


	private void fileDelAction(JList fileLi,JTable table) {
		Object s[]=fileLi.getSelectedValues();

		if(s==null||s.length<1)
			return;

		DefaultListModel model = (DefaultListModel) fileLi.getModel();
		for(int i=0;i<s.length;i++)		
			model.removeElement(s[i]);

		updateSheetNameList(fileLi,table);
	}

	private void fileDownAction(JList fileLi) {
		DefaultListModel filemodel=(DefaultListModel) fileLi.getModel();
		int selectedIndex=fileLi.getSelectedIndex();
		if(selectedIndex>filemodel.getSize()-2)
			return;

		Object tempobj=filemodel.getElementAt(selectedIndex+1);
		Object obj = filemodel.getElementAt(selectedIndex);
		filemodel.setElementAt(obj, selectedIndex+1);
		filemodel.setElementAt(tempobj, selectedIndex);
		fileLi.setSelectedIndex( selectedIndex+1);
		updateSheetNameList(fileLi, _tblSheetNameList);
	}

	/**
	 * @param fileLi
	 */
	private void fileUPAction(JList fileLi) {
		DefaultListModel filemodel=(DefaultListModel) fileLi.getModel();
		int selectedIndex=fileLi.getSelectedIndex();
		if(selectedIndex<1)
			return;

		Object tempobj=filemodel.getElementAt(selectedIndex-1);
		Object obj = filemodel.getElementAt(selectedIndex);
		filemodel.setElementAt(obj, selectedIndex-1);
		filemodel.setElementAt(tempobj, selectedIndex);
		fileLi.setSelectedIndex( selectedIndex-1);
		updateSheetNameList(fileLi, _tblSheetNameList);
	}
	/**
	 * @param colums
	 * @throws SQLException
	 */

	private void updateSheetNameList(JList fileLi, JTable _tblSheetNameList) {


		DefaultListModel filemodel = (DefaultListModel) fileLi.getModel();
		SearchSheetNameCommand comm = new SearchSheetNameCommand(filemodel);
		comm.execute();

		List temp = comm.sheetNameList;


		if(temp.size()>=0)
		{
			Object data[][] =new Object[temp.size()][];
			for(int j=0;j<temp.size();j++)
			{
				SheetInfo info=(SheetInfo) temp.get(j);
				data[j]=new Object[]{info.filePath,info.file,info.sheetName,Boolean.FALSE};
			}
			TableModel sheelModel = new SheetModel(data);
			_tblSheetNameList.setModel(sheelModel);
		}
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
	private JPanel buildTextSelectPn() {
		JPanel  pnMain = new JPanel();
		pnMain.setLayout(new FlowLayout(FlowLayout.LEFT));
		TitledBorder fileInfoBorder = BorderFactory.createTitledBorder("Text 입력");
		//pnMain.setBorder(fileInfoBorder);
		JLabel lbl = new JLabel("파일 형식 : ");
		JButton butView = new JButton("입력창 표시");
		butView.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				final JDialog inputTextdialog = new JDialog(KSGModelManager.getInstance().frame);
				inputTextdialog.setTitle("텍스트 입력");
				final JTextArea area = new JTextArea();
				JPanel pnControl = new JPanel();
				pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
				JButton butOK = new JButton("확인");
				butOK.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {

						SearchPanel.this.advListPanel.initInfo();
						importTableList = new Vector<KSGXLSImportPanel>();
						if(searchType.equals(SEARCH_TYPE_COMPANY))
						{
							tableInfoList = new Vector<ShippersTable>();
							int pageRow = pageLi.getModel().getSize();
							DefaultListModel model = (DefaultListModel) pageLi.getModel();
							for(int i=0;i<pageRow;i++)
							{

								PageInfo info=(PageInfo) model.get(i);
								if(info.isSelected())
								{
									ShippersTable table = new ShippersTable();
									table.setCompany_abbr(txfCompany.getText());
									table.setPage((Integer)info.chekInfo);
									tableInfoList.add(table);
								}
							}
						}

						ImportTextCommand command = new ImportTextCommand(area.getText());

						command.execute();

						inputTextdialog.setVisible(false);

						inputTextdialog.dispose();

						_txfSearchedTableCount.setText(String.valueOf(manager.tableCount));
						
						for(int i=0;i<manager.tableCount;i++)
						{
							KSGXLSImportPanel table = new KSGXLSImportPanel();
							table.setName("adv");	
							table.setTableIndex(i);

							table.addMouseListener(new MouseAdapter(){
								public void mouseClicked(MouseEvent e) {
									KSGXLSImportPanel ta =(KSGXLSImportPanel) e.getSource();
									int index=ta.getTableIndex();
									logger.debug("selected table: "+index	);
									manager.selectTableIndex=index;
									manager.execute("error");
								}
							});
							manager.addObservers(table);
							importTableList.add(table);
						}

						advListPanel.updateTableListPN();
						butNext.setEnabled(true);
						//updateTableInfo();
						//JOptionPane.showMessageDialog(ADVManageUI.this, manager.tableCount+"개의 광고테이블을 불러왔습니다.");

					}
				});

				JButton butCancel = new JButton("취소");
				butCancel.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						inputTextdialog.setVisible(false);
						inputTextdialog.dispose();

					}
				});
				pnControl.add(butOK);
				pnControl.add(butCancel);
				inputTextdialog.setModal(true);

				inputTextdialog.add(new JScrollPane(area));
				inputTextdialog.add(pnControl,BorderLayout.SOUTH);

				inputTextdialog.setSize(500,400);
				ViewUtil.center(inputTextdialog, false);
				inputTextdialog.setVisible(true);


			}
		});

		JComboBox box = new JComboBox();


		box.addItem("한글(.hwp)");
		box.addItem("워드(.doc)");
		box.setSelectedIndex(1);

		pnMain.add(lbl);
		pnMain.add(box);
		pnMain.add(butView);

		return pnMain;
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

	private Component buildCompanyInfoByCompany()
	{
		JPanel pnMain = new JPanel();
		pnMain.setLayout( new FlowLayout(FlowLayout.LEFT));
		//JPanel pnSubPage = new JPanel(); 
		TitledBorder pageBoder = BorderFactory.createTitledBorder("페이지 선택(2)");
		//pnSubPage.setBorder(pageBoder);
		//pnSubPage.setLayout(new BorderLayout());


		JLabel lblSelectedpage = new JLabel("");


		JPanel pnPageInfo = new JPanel(new BorderLayout());
		pnPageInfo.add(lblSelectedpage,BorderLayout.SOUTH);
		JCheckBox cbx = new JCheckBox("동일 선사 추가 선택",isSamePageSelect);
		cbx.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();

				isSamePageSelect=box.isSelected();
			}});

		pnPageInfo.add(cbx,BorderLayout.NORTH);


		txfPage.setBorder(BorderFactory.createEmptyBorder());

		pnPageInfo.add(txfPage);

		JButton butPageControl = new JButton("검색");
		butPageControl.setVisible(false);
		butPageControl.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				JButton but = (JButton) e.getSource();

				PageListDialog dialog = new PageListDialog();
				dialog.setLocation(but.getLocationOnScreen());
				dialog.createAndUpdateUI();

			}
		});

		//	pnSubPage.add(pnPageInfo,BorderLayout.WEST);
		//	pnSubPage.add(butPageControl,BorderLayout.EAST);


		//TitledBorder companyBoder = BorderFactory.createTitledBorder("선사 선택(1)");
		//pnSubControl1.setBorder(companyBoder);
		JLabel lblCompany = new JLabel("선사명: ");
		lblCompany.setPreferredSize(new Dimension(75, 20));
		lblCompany.setHorizontalAlignment(JLabel.RIGHT);
		lblCompany.setFont(lblFont);

		JButton butSearch = new JButton("검색");
		butSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SearchCompanyAndPageDialog andPageDialog = new SearchCompanyAndPageDialog(SearchPanel.this);
				andPageDialog.createAndUpdateUI();
				andPageDialog.setLocationRelativeTo(SearchPanel.this);

			}
		});


		pnMain.add(lblCompany);
		pnMain.add(txfCompany);		
		//pnMain.add(butSearch);
		//pnMain.add(pnPageInfo);		
		//pnMain.add(butPageControl);

		return pnMain;
	}

	private Component buildCompanyInfoByPage() {
		JPanel pnMain = new JPanel();
		pnMain.setLayout( new FlowLayout(FlowLayout.LEFT));
		JPanel pnSubPage = new JPanel(); 
		TitledBorder pageBoder = BorderFactory.createTitledBorder("선사 선택");
		//pnSubPage.setBorder(pageBoder);
		pnSubPage.setLayout(new BorderLayout());
		JLabel lblSelectedpage = new JLabel("선사명 : ");

		Box pageContorl = new Box(BoxLayout.Y_AXIS);
		JButton butUP = new JButton("▲");
		butUP.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int v=pageLi.getSelectedIndex();
				if(v==0||v==-1)
					return;

				logger.debug("update:"+v);
				DefaultListModel model=(DefaultListModel) pageLi.getModel();

				Object d=model.remove(v);
				model.add(--v, d);
			}});


		pageContorl.add(butUP);
		JButton butDown = new JButton("▼");
		butDown.addActionListener(new  ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int v=pageLi.getSelectedIndex();
				if(v>pageLi.getModel().getSize()-1||v==-1)
					return;

				logger.debug("update:"+v);
				DefaultListModel model=(DefaultListModel) pageLi.getModel();

				Object d=model.remove(v);
				try{
					model.add(++v, d);
				}catch(ArrayIndexOutOfBoundsException ee){
					model.addElement(d);
				}


			}});
		pageContorl.add(butDown);

		JPanel pnPageInfo = new JPanel();

		txfPCompany.setBorder(BorderFactory.createEmptyBorder());

		pnPageInfo.add(lblSelectedpage,BorderLayout.WEST);

		pnPageInfo.add(txfPCompany);

		//pnPageInfo.add(new JButton("검색"),BorderLayout.EAST);


		companyLi = new JList();
		PageCellRenderer pageCellRenderer = new PageCellRenderer();
		companyLi.setCellRenderer(pageCellRenderer);
		companyLi.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				JList li=(JList) e.getSource();
				JCheckBox box=(JCheckBox) li.getSelectedValue();
				logger.debug(box.getModel().isSelected());
				if(box.getModel().isSelected())
				{
					logger.debug(box.getModel().isSelected()+",");
					box.getModel().setSelected(false);
				}else
				{
					logger.debug(box.getModel().isSelected()+",,");
					box.getModel()	.setSelected(true);
				}
				companyLi.updateUI();

			}
		});
		JScrollPane spPageList = new JScrollPane(companyLi);
		//spPageList.setPreferredSize(new Dimension(200,50));

		//pnSubPage.add(spPageList);
		pnSubPage.add(pnPageInfo,BorderLayout.WEST);
		//pnSubPage.add(pageContorl,BorderLayout.EAST);

		JPanel pnSubControl1= new JPanel();
		pnSubControl1.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel lblCompany = new JLabel("페이지 : ");

		pnSubControl1.add(lblCompany);		

		pnSubControl1.add(_txfCPage);

		pnMain.add(pnSubControl1);
		pnMain.add(pnSubPage);

		return pnMain;
	}
	
	public void updateTableInfo2(DefaultListModel listModel) 
	{
		try {
			
			listTable.setParam(listModel);
			listTable.retrive();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	private void initComp()
	{
		_txfXLSFile = new JTextField(20);
		_txfXLSFile.setEditable(false);
		_txfDate = new JTextField(8);
		_txfDate.setEditable(false);
		_txfPort = new JTextField(2);
		_txfPort.setEditable(false);
		//_txfVessel = new JTextField(2);
		//_txfVessel.setEditable(false);
		txfCompany = new JTextField(15);
		txfCompany.setEditable(false);
		txfPage = new JTextField(5);
		txfPage.setEditable(false);
		txfPage.setVisible(false);
		_txfCPage = new JTextField(5);
		_txfCPage.setEditable(false);
		txfPCompany = new JTextField(20);
		txfPCompany.setEditable(false);
		_txfSearchedTableCount = new JTextField(2);
		_txfSearchedTableCount.setEditable(false);

		cbxSelectedInput = new JComboBox();
		cbxSelectedInput.addItem("File");
		cbxSelectedInput.addItem("Text");
		cbxSelectedInput.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				selectLay.show(pnSubSelect, e.getItem().toString());
				selectedInput = e.getItem().toString();

				manager.selectedInput=selectedInput ;
			}
		});
		cbxKeyWordType = new JComboBox();
		cbxKeyWordType.setPreferredSize(new Dimension(125, 20));
		cbxKeyWordType.addItem("Vessel");
		cbxKeyWordType.addItem("Voyage");

		cbxSearchType = new JComboBox();
		cbxSearchType.setPreferredSize(new Dimension(125, 20));
		cbxSearchType.addItem(SEARCH_TYPE_COMPANY);
		cbxSearchType.addItem(SEARCH_TYPE_PAGE);
		cbxSearchType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				selectLay2.show(pnSubSearch, e.getItem().toString());
				searchType= e.getItem().toString();
			}
		});

		pageLi = new JList();

		DefaultListModel defaultListModel = new DefaultListModel();
		pageLi.setModel(defaultListModel);
		PageCellRenderer pageCellRenderer = new PageCellRenderer();
		pageLi.setCellRenderer(pageCellRenderer);
		pageLi.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				JList li3=(JList) e.getSource();
				JCheckBox box=(JCheckBox) li3.getSelectedValue();
				if(box==null)
					return;
				logger.debug(box.getModel().isSelected());
				if(box.getModel().isSelected())
				{
					box.getModel().setSelected(false);
				}else
				{
					box.getModel()	.setSelected(true);
				}
				DefaultListModel m = (DefaultListModel) pageLi.getModel();
				Vector c=new Vector();
				for(int i=0;i<m.getSize();i++)
				{

					PageInfo info=(PageInfo) m.get(i);
					if(info.isSelected())
					{						
						c.add((Integer)info.chekInfo);
					}
				}
				try {
					DefaultTableModel model = new DefaultTableModel();

					String[]colName = {"선사명","페이지","하위항구","Voyage추가여부","구분자","구분자 위치"};

					for(int i=0;i<colName.length;i++)
						model.addColumn(colName[i]);

					for(int j=0;j<c.size();j++)
					{
						List li=tableService.getTableProperty(txfCompany.getText(),(Integer)c.get(j));

						logger.debug("property :"+li.size());
						for(int i=0;i<li.size();i++)
						{
							Table_Property p=(Table_Property) li.get(i);

							model.addRow(new Object[]{p.getCompany_abbr(),
									p.getPage(),
									p.getUnder_port(),
									(p.getVoyage()==1)?"추가":"-",
											p.getVesselvoydivider(),
											p.getVesselvoycount()
							});

						}
						tblPropertyTable.setModel(model);
						updateTableInfo2((DefaultListModel) pageLi.getModel());
					}

				} catch (SQLException ee) 
				{
					ee.printStackTrace();
				}

				pageLi.updateUI();
			}
		});


	}
	

	public	void updateViewByTree(TreePath path) {

		logger.info("update by tree:"+path.getPathCount());

		String selectedCompany = path.getLastPathComponent().toString();

		switch (path.getPathCount()) {
		case 1: // root 선택시


			manager.selectedCompany=null;

			manager.selectedPage=-1;
			
			txfCompany.setText("");
			
			txfPage.setText("");

			//	lblSelectedCompanyName.setText("");
			//	lblSelectedPage.setText("");
			DefaultListModel listModel1 = new DefaultListModel();
			pageLi.setModel(listModel1);
			companyLi.setModel(listModel1);

			//test
			butCompanyAdd.setEnabled(false);
			//	lblSelectedCompanyName.setText("");
			//	lblSelectedPage.setText("");

			break;
		case 2: // 0~9 선택시
			manager.selectedCompany=null;

			manager.selectedPage=-1;
			txfCompany.setText("");
			txfPage.setText("");
			_txfCPage.setText("");
			txfPCompany.setText("");
			DefaultListModel listModel2 = new DefaultListModel();
			pageLi.setModel(listModel2);
			companyLi.setModel(listModel2);

			//test
			//lblSelectedCompanyName.setText("");
			//lblSelectedPage.setText("");
			break;
		case 3: // 0~9 선택시
			StringTokenizer st = new StringTokenizer(selectedCompany,":");

			String company = new String();
			String page = new String();

			page=st.nextToken();
			company = st.nextToken();

			txfCompany.setText(company);

			this.selectedCompany =company;
			this.selectedPage = page;
			txfPage.setText(page);
			_txfCPage.setText(page);
			txfPCompany.setText(company);

			try {
				if(searchType.equals(SEARCH_TYPE_COMPANY))
				{
					List li=tableService.getTablePageListByCompany(company);
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
					
					updateTableInfo2(listModel);
					
					logger.info("searched Page size:"+listModel.size());

				}else
				{
					List li=tableService.getTableCompanyListByPage(Integer.parseInt(page));
					DefaultListModel listModel = new DefaultListModel();
					for(int i=0;i<li.size();i++)
					{
						ShippersTable tableInfo = (ShippersTable) li.get(i);

						PageInfo info = new PageInfo(tableInfo.getCompany_abbr());

						if(company.equals(info.getText()))
						{
							info.setSelected(true);
						}
						listModel.addElement(info);
					}
					logger.info("searched Page size:"+listModel.size());
					companyLi.setModel(listModel);
					updateTableInfo2(listModel);

				}


				updatePropertyTable(company,Integer.parseInt(page));


				//test
				//butCompanyAdd.setEnabled(true);
				//lblSelectedPage.setText(page);
				//lblSelectedCompanyName.setText(com);

			} catch (SQLException e) {
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e.getMessage());
				e.printStackTrace();
			}
			manager.selectedCompany=company;

			manager.selectedPage=Integer.parseInt(page);
			break;
		default:
			break;
		}
	}
	

	/**
	 * @param company_abbr
	 * @param page
	 */
	private void updatePropertyTable(String company_abbr, int page) {
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
	}
	
	public void cancel()
	{
		
	}
	
	/**
	 * 
	 */
	public void searchXLS()
	{
		try{
			logger.info("엑셀 정보 불러오기");

			if(selectedCompany==null)
			{
				return;
			}

			DefaultListModel model = (DefaultListModel) pageLi.getModel();
			
			Vector<PageInfo> pageInfoList = new Vector<PageInfo>();
			
			for(int i=0;i<model.getSize();i++)
			{			
				pageInfoList.add((PageInfo) model.get(i));	
			}				
			
			//쉬트 선택
			manageUI.setSelectedIndex(1);
			
			dialog = new SheetSelectDialog(_tblSheetNameList);
			
			dialog.createAndUpdateUI();
			
			/*
			 * 자동 입력 결과 생성
			 */
			
			if(dialog.result==1)
			{
				this.advListPanel.actionImportADVInfo(selectedCompany, 
						selectedPage,
						getSelectedSheetList(_tblSheetNameList), 
						cbxKeyWordType.getSelectedItem().equals("Vessel")?ImportXLSFileCommand.VESSEL:ImportXLSFileCommand.VOYAGE,
								selectXLSFilePath,pageInfoList);	
			}

			


		}catch(Exception e)
		{
			e.printStackTrace();
			manageUI.setSelectedIndex(0);
			JOptionPane.showMessageDialog(SearchPanel.this, "error: "+e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		if(command.equals("불러오기"))
		{
			logger.debug("import xls");
			this.advListPanel.initInfo();			
			if(selectedInput.equals("File"))
			{
				searchXLS();
				//actionImportADVInfo();

			}else
			{
				//importADVTextInfoAction();
			}
		}
		else if(command.equals("Sheet 선택"))
		{
			dialog = new SheetSelectDialog(_tblSheetNameList);
			dialog.createAndUpdateUI();
		}
		else if(command.equals("취소하기"))
		{
			//initInfo();
			//_txfCompany.setText("");
			//_txfPage.setText("");
			DefaultListModel model = new DefaultListModel();
			
			fileLi.setModel(model);
			
			pageLi.setModel(model);
			
			advListPanel.initTable();
			
			
			//butAdjust.setEnabled(false);
		}
			

	}

	/**
	 * @author archehyun
	 *
	 */
	class PageListPanel extends JPanel implements ActionListener
	{
		public PageListPanel() {
			this.setLayout(new BorderLayout());
			Box pnPageControl = new Box(BoxLayout.Y_AXIS);

			JButton butUP = new JButton("▲");
			butUP.addActionListener(this);

			JButton butDown = new JButton("▼");
			butDown.addActionListener(this);

			pnPageControl.add(butUP);
			pnPageControl.add(butDown);	

			add(new JScrollPane(pageLi));
			add(pnPageControl,BorderLayout.EAST);
			this.setBorder(BorderFactory.createTitledBorder("페이지 목록"));
			this.setPreferredSize(new Dimension(200, 150));
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			int v=pageLi.getSelectedIndex();
			if(command.equals("▲"))
			{
				if(v==0||v==-1)
					return;

				DefaultListModel model=(DefaultListModel) pageLi.getModel();

				Object d=model.remove(v);
				model.add(--v, d);
			}
			else
			{
				if(v>pageLi.getModel().getSize()-1||v==-1)
					return;

				DefaultListModel model=(DefaultListModel) pageLi.getModel();

				Object d=model.remove(v);
				try{
					model.add(++v, d);
				}catch(ArrayIndexOutOfBoundsException ee){
					model.addElement(d);
				}
			}

		}

	}


	class PageListDialog extends KSGDialog implements ActionListener
	{
		@Override
		public void createAndUpdateUI() {

			Box pnPageControl = new Box(BoxLayout.Y_AXIS);

			JButton butUP = new JButton("▲");
			butUP.addActionListener(this);

			JButton butDown = new JButton("▼");
			butDown.addActionListener(this);

			pnPageControl.add(butUP);
			pnPageControl.add(butDown);	

			this.getContentPane().add(new JScrollPane(pageLi));
			this.getContentPane().add(pnPageControl,BorderLayout.EAST);

			this.setSize(300,300);
			this.setVisible(true);


		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			int v=pageLi.getSelectedIndex();
			if(command.equals("▲"))
			{
				if(v==0||v==-1)
					return;

				DefaultListModel model=(DefaultListModel) pageLi.getModel();

				Object d=model.remove(v);
				model.add(--v, d);
			}
			else
			{
				if(v>pageLi.getModel().getSize()-1||v==-1)
					return;

				DefaultListModel model=(DefaultListModel) pageLi.getModel();

				Object d=model.remove(v);
				try{
					model.add(++v, d);
				}catch(ArrayIndexOutOfBoundsException ee){
					model.addElement(d);
				}
			}

		}

	}
	
	class ShipperTableListTable extends KSGTable
	{
		String[] columNames = {"순서","테이블 아이디","페이지","선사명","타이틀", "항구수","선박수"};
		DefaultListModel listModel;
		
		public void setParam(DefaultListModel listModel)
		{
			this.listModel = listModel;
		}
		public ShipperTableListTable() {
			
			initStyle();
			
			tableService = DAOManager.getInstance().createTableService();	
			
			DefaultTableModel model = new DefaultTableModel();

			model.setColumnCount(0);

			for(int i=0;i<columNames.length;i++)
			{
				model.addColumn(columNames[i]);
			}		
			
			
			setModel(model);
			
			updateColumModel();
		}
		
		private void updateColumModel()
		{
			initColumModel();

			TableColumnModel colmodel =getColumnModel();

			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);
				DefaultTableCellRenderer renderer=	(DefaultTableCellRenderer) namecol.getCellRenderer();
				if(i<5)
				{
					
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
				}
				else
				{
					renderer.setHorizontalAlignment(SwingConstants.RIGHT);
				}
			}

			TableColumnModel colmodel_width =getColumnModel();

			colmodel_width.getColumn(0).setPreferredWidth(60); // 순서
			colmodel_width.getColumn(1).setPreferredWidth(325); // 테이블 아이디		
			colmodel_width.getColumn(2).setPreferredWidth(60); // 페이지
			colmodel_width.getColumn(3).setPreferredWidth(325); // 선사명
			colmodel_width.getColumn(4).setPreferredWidth(525); // 타이틀
			
			colmodel_width.getColumn(5).setPreferredWidth(60);
			colmodel_width.getColumn(5).setResizable(false);
			colmodel_width.getColumn(6).setPreferredWidth(60);
			colmodel_width.getColumn(6).setResizable(false);

		}
		

		@Override
		public void retrive() throws SQLException {
			
			DefaultTableModel model1 = new DefaultTableModel();

			for(int i=0;i<columNames.length;i++)
			{
				model1.addColumn(columNames[i]);
			}
			int count=0;
			for(int i=0;i<listModel.size();i++)
			{
				PageInfo info=(PageInfo) listModel.get(i);
				if(info.isSelected())
				{
					ShippersTable shippersTable  = new ShippersTable();
					
					shippersTable.setPage(Integer.parseInt(info.getText()));
					
					List inf = tableService.getTableListByPage(shippersTable);
					
					for(int j=0;j<inf.size();j++)
					{
						ShippersTable shipper=(ShippersTable) inf.get(j);
						model1.addRow(new Object[]{++count,shipper.getTable_id(),
								shipper.getPage(),shipper.getCompany_abbr(),shipper.getTitle(),shipper.getPort_col(),shipper.getVsl_row()});
					}
				}

			}

			setModel(model1);
			
			updateColumModel();
			
		}
		
	}
	

}
