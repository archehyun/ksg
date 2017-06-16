package com.ksg.view.adv;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ksg.commands.ImportTextCommand;
import com.ksg.commands.SearchPortCommand;
import com.ksg.commands.SearchSheetNameCommand;
import com.ksg.commands.xls.ImportXLSFileCommand;
import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Property;
import com.ksg.model.KSGModelManager;
import com.ksg.view.KSGViewParameter;
import com.ksg.view.adv.comp.PageCellRenderer;
import com.ksg.view.adv.comp.SheetModel;
import com.ksg.view.adv.comp.SimpleFileFilter;
import com.ksg.view.adv.dialog.SheetSelectDialog;
import com.ksg.view.adv.dialog.ViewXLSFileDialog;
import com.ksg.view.comp.FileInfo;
import com.ksg.view.comp.KSGCompboBox;
import com.ksg.view.comp.KSGTable2;
import com.ksg.view.comp.PageInfo;
import com.ksg.view.util.KSGPropertis;
import com.ksg.view.util.ViewUtil;
import com.ksg.xls.model.SheetInfo;

public class SearchPanel extends JPanel implements ActionListener{
	
	private boolean 		isPageSearch=true;
	
	private boolean 		isSamePageSelect=true;
	
	private JList 			companyLi;
	
	private JButton 		butAdjust,butCompanyAdd,butPre,butNext;
	
	private JList 			pageLi;
	public JList getPageLi() {
		return pageLi;
	}

	private TableService 	tableService;
	
	private Vector<ShippersTable> tableInfoList;
	
	private JPanel 			pnTableList,pnLeftMenu;
	
	private String			selectedInput="File";
	
	private int 			resultOK,currentPage,resultCancel,startTableIndex,totalTableCount;
	
	public JTextField		_txfXLSFile,_txfSearchedTableCount,txfCompany,_txfDate;
	
	private KSGModelManager manager = KSGModelManager.getInstance();
	
	private static int _tableViewCount = 10;
	
	private Vector<KSGXLSImportPanel> importTableList;
	public static final String SEARCH_TYPE_COMPANY = "선사";
	
	private JTextField  	_txfCPage,_txfPage,_txfPCompany,_txfPort;
	
	private static final String SEARCH_TYPE_PAGE = "페이지";
	
	
	private String selectedCompany;
	
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
	private JPanel pnSubSearch;
	private CardLayout selectLay2;
	private JPanel pnSubSelect;
	private CardLayout selectLay;
	private ButtonGroup bgKeyword;
	
	private JRadioButton butVesselOpt, butVoyageOpt;
	
	private JComboBox cbxSelectedInput;
	private KSGTable2 _tblError;
	private JTable tblPropertyTable;
	private JPanel pnTableInfo;
	private JTable _tblTable;
	private JList fileLi;
	
	private String 			searchType=SEARCH_TYPE_COMPANY;
	
	public String getSearchType() {
		return searchType;
	}

	ADVListPanel advListPanel;
	
	private DAOManager daoManager = DAOManager.getInstance();
	
	private String selectedPage;
	
	public SearchPanel(ADVListPanel advListPanel) {
		
		tableService = daoManager.createTableService();
		
		this.advListPanel = advListPanel;
		
		advListPanel.setSearchPanel(this);
		
		initComp();
		
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

				currentPage=0;
				//updateTableListPN();
				butNext.setEnabled(false);
				butPre.setEnabled(false);

				updateUI();

			}});

		comp.setPreferredSize(new Dimension(100,25));
		
		setLayout(new BorderLayout());
		
		Box pnControl= new Box(BoxLayout.Y_AXIS);

		JLabel lblPage = new JLabel("페이지 : ");

		JPanel pnSubControl2 = new JPanel();
		
		pnSubControl2.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		txfTableCount.setText(_tableViewCount+"");

		JPanel pnSubFileSelect = buildFileSelectPn();
		
		JPanel pnSubTextSelect = buildTextSelectPn();


		JButton butImportFile = new JButton("\n불러오기(V)",new ImageIcon("images/importxls.gif"));
		
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
		butImportFile.setPreferredSize(new Dimension(150,45));
		JPanel pnSearchType = new JPanel();
		pnSearchType.setLayout(new FlowLayout(FlowLayout.LEFT));
		cbxSearchType = new JComboBox();
		cbxSearchType.addItem(SEARCH_TYPE_COMPANY);
		cbxSearchType.addItem(SEARCH_TYPE_PAGE);
		cbxSearchType.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				selectLay2.show(pnSubSearch, e.getItem().toString());
				searchType= e.getItem().toString();

			}
		});
		JLabel lblSearch= new JLabel();
		lblSearch.setText("검색 형식 : ");
		pnSearchType.add(lblSearch);
		pnSearchType.add(cbxSearchType);

		pnSubSearch = new JPanel();
		
		selectLay2 = new CardLayout();
		
		pnSubSearch.setLayout(selectLay2);
		
		pnSubSearch.add( buildCompanyInfoByCompany(),SEARCH_TYPE_COMPANY);
		pnSubSearch.add(buildCompanyInfoByPage(),SEARCH_TYPE_PAGE);

		pnControl.add(pnSearchType);
		pnControl.add(pnSubSearch);

		JPanel pnType = new JPanel();
		pnType.setLayout(new FlowLayout(FlowLayout.LEFT));

		pnSubSelect = new JPanel();
		selectLay = new CardLayout();
		pnSubSelect.setLayout(selectLay);
		pnSubSelect.add(pnSubFileSelect,"File");
		pnSubSelect.add(pnSubTextSelect,"Text");

		pnType.add(pnSubSelect);

		JPanel pnImportBut = new JPanel();
		pnImportBut.setLayout(new GridLayout(0,1));

		pnImportBut.add(butImportFile);
		JButton butSheetSelect = new JButton("Sheet 선택");		
		
		butSheetSelect.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				SheetSelectDialog dialog = new SheetSelectDialog(_tblSheetNameList);
				dialog.createAndUpdateUI();

			}});
		pnImportBut.add(butSheetSelect);
		JPanel pnKeyType = new JPanel(new GridLayout(0,1));


		bgKeyword = new ButtonGroup();
		butVesselOpt = new JRadioButton("Vessel",true);
		butVoyageOpt = new JRadioButton("Voyage");
		bgKeyword.add(butVesselOpt);
		bgKeyword.add(butVoyageOpt);
		pnKeyType.add(new JLabel("Key word 형식"));
		pnKeyType.add(butVesselOpt);
		pnKeyType.add(butVoyageOpt);

		pnType.add(pnKeyType);
		pnType.add(pnImportBut);


		JPanel pnSelectType = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
		JLabel  lbl = new JLabel("입력 형식 : ");

		pnSelectType.add(lbl);
		pnSelectType.add(cbxSelectedInput);
		pnControl.add(pnSelectType);
		pnControl.add(pnType);

		_tblError = new KSGTable2(KSGTable2.TABLE_TYPE_ERROR);		
		_tblError.setName("error");
		manager.addObservers(_tblError);

		_tblError.setComponentPopupMenu(createErrorPopupMenu());


		JTabbedPane tabbedPane = new JTabbedPane();

		JPanel pnPropety = new JPanel();
		pnPropety.setLayout(new BorderLayout());
		tblPropertyTable = new JTable();

		pnPropety.add(new JScrollPane(tblPropertyTable));

		pnTableInfo =new JPanel();
		_tblTable = new JTable();

		_tblTable.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {

				ShippersTable table = new ShippersTable();
				int row=_tblTable.getSelectedRow();

				int col= _tblTable.getSelectedColumn();

				if(row==-1)
					return;

				table.setTable_id((String) _tblTable.getValueAt(row, 0));

			}
		});
		_tblTable.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		_tblTable.addMouseListener(new MouseAdapter(){
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
		
		pnTableInfo.add(new JScrollPane(_tblTable),BorderLayout.CENTER);


		tabbedPane.addTab("테이블 정보",pnTableInfo);
		
		tabbedPane.addTab("History", pnPropety);

		add(pnControl,BorderLayout.NORTH);
		
		add(tabbedPane,BorderLayout.CENTER);
	}
	
	private JPanel buildCompanySearch()
	{
		JPanel pnMain = new JPanel();
		
		JLabel lblCompany = new JLabel("선사명 : ");
		
		pnMain.add(lblCompany);
		
		return pnMain;
	}
	private JPanel buildPageSearch()
	{
		return null;
	}
	
	
	
	

	/**
	 * @return
	 */
	private JPanel buildFileListPn() {
		JPanel pnSubControlInfo1 = new JPanel();
		pnSubControlInfo1.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel lblFileName = new JLabel("파일 명 : ");
		lblFileName.setIcon(new ImageIcon("images/xlslogo.png"));
		pnSubControlInfo1.add(lblFileName);
		this._txfXLSFile.setVisible(false);	

		pnSubControlInfo1.add(this._txfXLSFile);



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
		scrollPane.setPreferredSize(new Dimension(150,50));
		pnFile.add(scrollPane);

		pnFile.add(pnButList,BorderLayout.SOUTH);

		pnSubControlInfo1.add(pnFile);
		return pnSubControlInfo1;
	}

	private JPanel buildFileSelectPn() {
		JPanel pnSubControlInfo= new JPanel();
		pnSubControlInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel pnSubFileList = buildFileListPn();		
		pnSubControlInfo.add(pnSubFileList);
		return pnSubControlInfo;
	}
	/*private JComponent buildHistoryAndLegendPN() {

		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());
		JTabbedPane pnLegendAndHistory= new JTabbedPane();
		JPanel pnLegend = new JPanel();

		Box pnHistory = new Box(BoxLayout.Y_AXIS);

		JPanel pnCompany = new JPanel();
		pnCompany.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblCompany = new JLabel("선사 : ");

		lblCompany2 = new JLabel();
		JPanel pnPage = new JPanel();
		pnPage.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblPage = new JLabel("페이지 : ");
		lblPage2 = new JLabel("");
		pnPage.add(lblPage);
		pnPage.add(lblPage2);
		pnCompany.add(lblCompany);
		pnCompany.add(lblCompany2);

		pnHistory.add(pnCompany);
		pnHistory.add(pnPage);

		pnLegendAndHistory.addTab("입력결과(작업중)", pnHistory);		
		pnLegendAndHistory.addTab("범례(작업중)", pnLegend);

		JPanel pnControl = new JPanel();

		JButton butReload = new JButton(" 다시 불러오기");
		butReload.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				importTableList = new Vector<KSGXLSImportPn>();
				command = new ReloadXLSInfoCommand ();
				command.execute();
				int result=((ReloadXLSInfoCommand)command).result;

				if(result==1)
				{
					for(int i=0;i<manager.tableCount;i++)
					{
						KSGXLSImportPn table = new KSGXLSImportPn();
						table.setName("adv");	
						table.setTableIndex(i);

						table.addMouseListener(new MouseAdapter(){
							public void mouseClicked(MouseEvent e) {
								KSGXLSImportPn ta =(KSGXLSImportPn) e.getSource();
								int index=ta.getTableIndex();
								manager.selectTableIndex=index;
								manager.execute("error");
							}	
						});
						manager.addObservers(table);
						importTableList.add(table);


					}
					updateTableListPN();
					updateTableInfo();
					butAdjust.setEnabled(true);
					butNext.setEnabled(true);
					JOptionPane.showMessageDialog(ADVManageUI.this, manager.tableCount+"개의 광고테이블을 불러왔습니다.");
					manager.isWorkMoniter=false;
					manager.workProcessText="";
					manager.execute(KSGMainFrame.NAME);

					adjestADVListDialog = new AdjestADVListDialog(ADVManageUI.this, tableInfoList);
					adjestADVListDialog.createAndUpdateUI();

					XLSTableInfoMemento memento=manager.memento;
					cbxSelectedInput.setSelectedItem(memento.getSelectedInput());
					cbxSearchType.setSelectedItem(memento.getSearchType());
					lblCompany2.setText(memento.companyList.toString());

					for(int i=0;i<memento.pageList.size();i++)
					{

					}
					lblPage2.setText(memento.pageList.toString());
				}
			}});
		pnControl.add(butReload);
		JButton butDelHistory = new JButton("결과 지우기");
		pnControl.add(butDelHistory);
		pnHistory.add(pnControl);
		pnMain.setPreferredSize(new Dimension(_LEFT_SIZE,100));
		pnMain.add(pnLegendAndHistory,BorderLayout.CENTER);

		pnMain.setPreferredSize(new Dimension(0,250));
		return pnMain;
	}*/
	
	
	/**
	 * @return
	 */
	private Vector getSelectedSheetList(JTable _tblSheetNameList) {
		// 선택된 쉬트 파악
		logger.debug("start:"+_tblSheetNameList.getRowCount());
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
		pnMain.setBorder(fileInfoBorder);
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
/*	*//**
	 * @param index
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
	}
	/**
	 * 
	 
	private void initInfo() {
		manager.data=null;
		manager.setXLSTableInfoList(null);
		manager.vesselCount = 0;
		currentPage=0;
		advListPanel.updateTableListPN();

		//pnTableList.removeAll();

		manager.execute("vessel");
		manager.execute("error");
		_tblTable.setModel(new DefaultTableModel());
		for(int i=0;i<importTableList.size();i++)
		{
			manager.removeObserver(importTableList.get(i));
		}
		butAdjust.setEnabled(false);	
	}*/
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
		JPanel pnCompanyInfo = new JPanel();
		pnCompanyInfo.setLayout( new FlowLayout(FlowLayout.LEFT));
		JPanel pnSubPage = new JPanel(); 
		TitledBorder pageBoder = BorderFactory.createTitledBorder("페이지 선택(2)");
		pnSubPage.setBorder(pageBoder);
		pnSubPage.setLayout(new BorderLayout());


		JLabel lblSelectedpage = new JLabel("");

		Box pageContorl = new Box(BoxLayout.Y_AXIS);
		JButton butUP = new JButton("▲");
		butUP.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int v=pageLi.getSelectedIndex();
				if(v==0||v==-1)
					return;

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

				DefaultListModel model=(DefaultListModel) pageLi.getModel();

				Object d=model.remove(v);
				try{
					model.add(++v, d);
				}catch(ArrayIndexOutOfBoundsException ee){
					model.addElement(d);
				}

			}});
		pageContorl.add(butDown);

		JPanel pnPageInfo = new JPanel(new BorderLayout());
		pnPageInfo.add(lblSelectedpage,BorderLayout.SOUTH);
		JCheckBox cbx = new JCheckBox("동일 선사 추가 선택",isSamePageSelect);
		cbx.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box = (JCheckBox) e.getSource();

				isSamePageSelect=box.isSelected();
			}});

		pnPageInfo.add(cbx,BorderLayout.NORTH);



		_txfPage.setBorder(BorderFactory.createEmptyBorder());
		pnPageInfo.add(_txfPage);


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
		JScrollPane spPageList = new JScrollPane(pageLi);
		spPageList.setPreferredSize(new Dimension(100,75));

		pnSubPage.add(spPageList);
		pnSubPage.add(pnPageInfo,BorderLayout.WEST);
		pnSubPage.add(pageContorl,BorderLayout.EAST);

		JPanel pnSubControl1= new JPanel();
		pnSubControl1.setLayout(new FlowLayout(FlowLayout.LEADING));

		TitledBorder companyBoder = BorderFactory.createTitledBorder("선사 선택(1)");
		pnSubControl1.setBorder(companyBoder);
		JLabel lblCompany = new JLabel("선사명 : ");
		lblCompany.setIcon(new ImageIcon("images/table.png"));
		pnSubControl1.add(lblCompany);		

		pnSubControl1.add(txfCompany);

		pnCompanyInfo.add(pnSubControl1);
		pnCompanyInfo.add(pnSubPage);

		return pnCompanyInfo;
	}

	private Component buildCompanyInfoByPage() {
		JPanel pnCompanyInfo = new JPanel();
		pnCompanyInfo.setLayout( new FlowLayout(FlowLayout.LEFT));
		JPanel pnSubPage = new JPanel(); 
		TitledBorder pageBoder = BorderFactory.createTitledBorder("선사 선택");
		pnSubPage.setBorder(pageBoder);
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
		pnPageInfo.add(lblSelectedpage,BorderLayout.WEST);
		_txfPCompany.setBorder(BorderFactory.createEmptyBorder());

		pnPageInfo.add(_txfPCompany);


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
		spPageList.setPreferredSize(new Dimension(200,50));

		pnSubPage.add(spPageList);
		pnSubPage.add(pnPageInfo,BorderLayout.WEST);
		pnSubPage.add(pageContorl,BorderLayout.EAST);

		JPanel pnSubControl1= new JPanel();
		pnSubControl1.setLayout(new FlowLayout(FlowLayout.LEADING));

		TitledBorder companyBoder = BorderFactory.createTitledBorder(SEARCH_TYPE_PAGE);
		pnSubControl1.setBorder(companyBoder);
		JLabel lblCompany = new JLabel("페이지 : ");
		lblCompany.setIcon(new ImageIcon("images/table.png"));
		pnSubControl1.add(lblCompany);		

		pnSubControl1.add(_txfCPage);

		pnCompanyInfo.add(pnSubControl1);
		pnCompanyInfo.add(pnSubPage);

		return pnCompanyInfo;
	}
	public void updateTableInfo2(DefaultListModel listModel) 
	{
		try {


			DefaultTableModel model1 = new DefaultTableModel();
			String[]colName1 = {"테이블 아이디","페이지","타이틀", "항구수","선박수"};
			for(int i=0;i<colName1.length;i++)
				model1.addColumn(colName1[i]);
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
						ShippersTable p=(ShippersTable) inf.get(j);
						model1.addRow(new Object[]{p.getTable_id(),
								p.getPage(),p.getTitle(),p.getPort_col(),p.getVsl_row()});
					}
				}

			}

			_tblTable.setModel(model1);
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
		txfCompany = new JTextField(20);
		txfCompany.setEditable(false);
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
	public	void updateViewByTree(TreePath path) {
		
		logger.info("update by tree:"+path.getPathCount());

		String selectedCompany = path.getLastPathComponent().toString();

		switch (path.getPathCount()) {
		case 1: // root 선택시


			manager.selectedCompany=null;

			manager.selectedPage=-1;
			txfCompany.setText("");
			_txfPage.setText("");

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
			_txfPage.setText("");
			_txfCPage.setText("");
			_txfPCompany.setText("");
			DefaultListModel listModel2 = new DefaultListModel();
			pageLi.setModel(listModel2);
			companyLi.setModel(listModel2);

			//test
			butCompanyAdd.setEnabled(false);
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
			_txfPage.setText(page);
			_txfCPage.setText(page);
			_txfPCompany.setText(company);

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
	public void searchXLS()
	{
		try{
		logger.info("불러오기");
		
		DefaultListModel model = (DefaultListModel) pageLi.getModel();
		Vector<PageInfo> pageInfoList = new Vector<PageInfo>();
		for(int i=0;i<model.getSize();i++)
		{			
			pageInfoList.add((PageInfo) model.get(i));	
		}
		
		
		this.advListPanel.actionImportADVInfo(selectedCompany, 
				selectedPage,
				getSelectedSheetList(_tblSheetNameList), 
				butVesselOpt.isSelected()?ImportXLSFileCommand.VESSEL:ImportXLSFileCommand.VOYAGE,
				selectXLSFilePath,pageInfoList);
		
		
		}catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(SearchPanel.this, "error: "+e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}
