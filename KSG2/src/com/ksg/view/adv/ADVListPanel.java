package com.ksg.view.adv;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.ksg.commands.InsertADVCommand;
import com.ksg.commands.KSGCommand;
import com.ksg.commands.xls.ImportXLSFileCommand;
import com.ksg.commands.xls.ImportXLSFileCommandByXML;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.model.KSGModelManager;
import com.ksg.view.KSGMainFrame;
import com.ksg.view.adv.PortTableComp.PortColorInfo;
import com.ksg.view.adv.comp.SheetModel;
import com.ksg.view.adv.dialog.AddAdvDialog;
import com.ksg.view.comp.PageInfo;
import com.ksg.view.util.KSGDateUtil;

public class ADVListPanel extends JPanel implements ActionListener, MouseWheelListener{
	private static final int UNIT_INCREMENT = 15;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	/**
	 * 
	 */
	private JTable			_tblSheetNameList;
	private static final int ADV_IMPORT_PANEL_ROW_SIZE = 250;
	private KSGModelManager manager = KSGModelManager.getInstance();
	private static final String SEARCH_TYPE_COMPANY = "선사";
	private static int _tableViewCount = 10;
	private static final String SEARCH_TYPE_PAGE = "페이지";
	private static final long serialVersionUID = 1L;
	private String 			searchType=SEARCH_TYPE_COMPANY;
	private JButton butAdjust;
	public JTextField		_txfXLSFile,txfSearchedTableCount,_txfDate,txfImportDate,_txfCPage;
	private JList 			pageLi;
	private JList 			fileLi;
	private JList 			companyLi;
	private TableService 	tableService;
	private Vector 			pageList;
	private Vector<KSGXLSImportPn> importTableList = new Vector<KSGXLSImportPn>();
	private Vector companyList;
	private Vector<ShippersTable> tableInfoList;
	private ImportXLSFileCommand command;
	int totalTableCount;
	JPanel pnTableList;
	private String			selectedInput="File";
	private int 			pageCount;
	private int resultCancel;
	private ImportXLSFileCommandByXML command2;
	private JButton 		butCompanyAdd,butPre;
	public JButton			butNext;
	private Vector 			resultA = new Vector();	
	private JTable 			_tblTable;
	Scrollbar scrollbar;
	JTextField _txfPage;
	private String selectedCompany;
	public ADVListPanel() {
		
		setLayout(new BorderLayout());

		JPanel pnNorth = new JPanel(new BorderLayout());
		JLabel lblTableCountlbl =new JLabel("검색된 테이블 수 : ");
		

		butAdjust = new JButton("위치조정");
		butAdjust.addActionListener(this);
		butAdjust.setEnabled(false);
		
		JButton butReLoad = new JButton("다시 불러오기");
		butReLoad.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				/*initInfo();
				if(selectedInput.equals("File"))
				{
					actionImportADVInfo();
				}else
				{
					importADVTextInfoAction();
				}*/
			}});


		JPanel pnNorthLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JPanel pnNorthRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		txfSearchedTableCount = new JTextField(15);

		pnNorthLeft.add(lblTableCountlbl);
		pnNorthLeft.add(txfSearchedTableCount);


		pnNorthRight.add(butAdjust);
		
		pnNorthRight.add(butReLoad);

		pnNorth.add(pnNorthLeft,BorderLayout.WEST);
		
		pnNorth.add(pnNorthRight,BorderLayout.EAST);



		JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		//TODO 섦정 파일 오류 확인
		/* 2014.11.3 오류 발생 주석 처리
		 *
		*/
		//pane.setDividerLocation(Integer.parseInt((propertis.getProperty("errorDividerLoction"))));
		JPanel pnTableList = createPnTableList();
		

		pane.setTopComponent(pnTableList);
		add(pane);
		add(pnNorth,BorderLayout.NORTH);
		add(buildSouthPn(),BorderLayout.SOUTH);
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
		mainScrollPane = new JScrollPane(pnTableList);
		mainScrollPane.getVerticalScrollBar().setUnitIncrement(UNIT_INCREMENT);
		
		
		//jScrollPane.setAutoscrolls(true);
		//jScrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		pnTableListMain.add(mainScrollPane,BorderLayout.CENTER);
		
		return pnTableListMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
/*	private Component buildSouthPn() {

		JPanel pnMain = new JPanel();
		pnMain.setLayout(new BorderLayout());

		JPanel paRight = new JPanel();
		paRight.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton butADD = new JButton("광고불러오기",new ImageIcon("images/importxls.gif"));
		butADD.setToolTipText("광고정보추가");
		butADD.setPreferredSize(new Dimension(100,20));
		butADD.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				//AddAdvDialog dialog = new AddAdvDialog(null,true,ADVManageUI.this);

			}});
		JButton butSave = new JButton("광고정보저장",new ImageIcon("images/save.gif"));

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

					saveAction(tableli);


				}catch(Exception es)
				{
					JOptionPane.showMessageDialog(null, "등록실패:"+es.getMessage());
					es.printStackTrace();
				}

			}});

		JButton butCancel = new JButton(new ImageIcon("images/cancel.gif"));
		butCancel.setPreferredSize(new Dimension(35,25));
		butCancel.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) {
				initInfo();
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
		paRight.add(butSave);
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
	}*/
/*	private void initInfo() {
		manager.data=null;
		manager.setXLSTableInfoList(null);
		manager.vesselCount = 0;
		currentPage=0;
		updateTableListPN();

		pnTableList.removeAll();

		manager.execute("vessel");
		manager.execute("error");
		_tblTable.setModel(new DefaultTableModel());
		for(int i=0;i<importTableList.size();i++)
		{
			manager.removeObserver(importTableList.get(i));
		}
		butAdjust.setEnabled(false);	
	}

	*//**
	 * @param li
	 * @throws SQLException
	 * @throws ParseException
	 *//* 
	private void saveAction(List li) throws ParseException {

		logger.debug("start");
		resultA.removeAllElements();

		for(int i=0;i<importTableList.size();i++)
		{
			KSGXLSImportPn xlsPn = importTableList.get(i);
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
			KSGXLSImportPn xlsPn = importTableList.get(i);
			Vector portList=xlsPn.getPortList();
			savePortList(xlsPn, portList);
			String insertData = new String();


			// 수정 필요

			insertData =xlsPn.getTableXMLInfo();

			logger.debug("table info : \n"+insertData);

			ShippersTable tableinfo=(ShippersTable) li.get(i);

			ADVData data = new ADVData();

			data.setData(insertData);
			data.setDate_isusse(KSGDateUtil.toDate2(txfImportDate.getText()));
			//TODO 아이디 입력 부분 수정 
			data.setTable_id(tableinfo.getTable_id());
			data.setCompany_abbr(_txfCompany.getText());
			data.setPage(manager.selectedPage);

			data.setTable_id(tableinfo.getTable_id());
			KSGCommand command = new InsertADVCommand(data);
			command.execute();
		}
		JOptionPane.showMessageDialog(null, "광고정보를 저장했습니다.");

	}*/
	public void updateTableListPN() 
	{

		totalTableCount = manager.tableCount-1;
		
		pageCount =totalTableCount/_tableViewCount;
		
		pnTableList.removeAll();
		
		logger.debug("adv execute:"+importTableList.size());
		
		manager.execute("adv");
		
		
		// 인덱스를 조회 하여 지정 하는 부분 필요
		
		
		for(int i=0;i<importTableList.size();i++)
		{
			KSGXLSImportPn table=importTableList.get(i);
			table.addMouseWheelListener(this);
			
			JScrollPane scrollPane = new JScrollPane(table);
			
			scrollPane.addMouseWheelListener(this);
			
			TitledBorder createTitledBorder = BorderFactory.createTitledBorder(table.getTitle());
			createTitledBorder.setTitleFont(new Font("돋음",0,12));
			scrollPane.setBorder(createTitledBorder);			
			pnTableList.add(scrollPane);
		}

		this.updateUI();
	}
	public void actionImportADVInfo(String company, String page,Vector sheetList, int keyType, String 			selectXLSFilePath, Vector<PageInfo> pageInfoList) {
		try {

			logger.debug("<=====start=====>");

			manager.isWorkMoniter=true;
			manager.workProcessText="XLS 정보를 가져오는 중...";
			
			
			manager.execute(KSGMainFrame.NAME);
			
			this.selectedCompany = company;
			int result=actionImportADVInfoSub(company, page, sheetList,keyType, selectXLSFilePath,pageInfoList);
			if(result==1)
			{
				for(int i=0;i<manager.tableCount;i++)
				{
					KSGXLSImportPn table = new KSGXLSImportPn();
					table.setPreferredSize(new Dimension(0,ADV_IMPORT_PANEL_ROW_SIZE));

					table.setName("adv");	
					table.setTableIndex(i);

					table.addMouseListener(myMouseListener);
					manager.addObservers(table);
					importTableList.add(table);
				}

				updateTableListPN();
				//updateTableInfo();
				//pnTab.setSelectedIndex(1);
				butAdjust.setEnabled(true);
				butNext.setEnabled(true);
				
				//KSGModelManager.getInstance().processBar.close();
			
				//JOptionPane.showMessageDialog(ADVManageUI.this, manager.tableCount+"개의 광고테이블을 불러왔습니다.");
				manager.isWorkMoniter=false;
				manager.workProcessText="";
				manager.execute(KSGMainFrame.NAME);
				logger.debug("<=====end=====>");
			}


		} catch (Exception e1) {

			JOptionPane.showMessageDialog(null,"광고정보생성에 실패했습니다.\nerror: "+e1.getMessage());
			manager.isWorkMoniter=false;
			e1.printStackTrace();
			return;
		}
	}
	public int actionImportADVInfoSub(String company, String page,Vector sheetList, int keyType, String selectXLSFilePath, Vector<PageInfo> pageInfoList) {
		try{
			logger.debug("start");
			importTableList = new Vector<KSGXLSImportPn>();				
			
			int resultOK = 1;
			int resultCancel = 0;
			
			//입력된 데이터 확인
			if(company.equals(""))
			{
				JOptionPane.showMessageDialog(null, "선사를 선택하십시요");
				manager.isWorkMoniter=false;
				return  resultCancel;
			}

			if(sheetList.size()==0)
			{
				JOptionPane.showMessageDialog(null, "선택된 sheet가 없습니다.");	
				manager.isWorkMoniter=false;
				return  resultCancel;
			}

			companyList = new Vector();
			pageList 	= new Vector();
			if(searchType.equals(SEARCH_TYPE_COMPANY))
			{
				tableInfoList = new Vector<ShippersTable>();
				
				for(int i=0;i<pageInfoList.size();i++)
				{
					PageInfo info=(PageInfo) pageInfoList.get(i);
					if(info.isSelected())
					{
						ShippersTable table = new ShippersTable();
						table.setCompany_abbr(company);
						table.setPage((Integer)info.chekInfo);
						tableInfoList.add(table);
						pageList.add((Integer)info.chekInfo);
					}
				}
			}else
			{
				tableInfoList = new Vector();
				int pageRow = companyLi.getModel().getSize();
				for(int i=0;i<pageRow;i++)
				{
					DefaultListModel model = (DefaultListModel) companyLi.getModel();

					PageInfo info=(PageInfo) model.get(i);
					if(info.isSelected())
					{
						ShippersTable table = new ShippersTable();
						table.setCompany_abbr((String)info.chekInfo);
						table.setPage(Integer.parseInt(_txfCPage.getText()));
						tableInfoList.add(table);
						companyList.add(info.chekInfo);
					}
				}
			}

			logger.debug("execute importxlsfilecommand");
			command = new ImportXLSFileCommand(tableInfoList,
					sheetList,
					selectXLSFilePath,
					company,
					Integer.parseInt(page),
					searchType,
					selectedInput,keyType);
			command.execute();

			command2 = new ImportXLSFileCommandByXML(tableInfoList);
			command2.execute();

			//_txfSearchedTableCount.setText(String.valueOf(manager.tableCount));
			logger.debug("end:resultOK:"+manager.tableCount);
			return resultOK;
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.debug("end:resultCancel,errorCode:"+e.getMessage());
			return resultCancel;
		}

	}
	class MyMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) {
			KSGXLSImportPn ta =(KSGXLSImportPn) e.getSource();
			int index=ta.getTableIndex();
			manager.selectTableIndex=index;
			manager.execute("error");
		}
	}
	private MyMouseListener myMouseListener = new MyMouseListener();
	private int currentPage;
	private TablePort tablePort;
	private JScrollPane mainScrollPane;
	
	public void initInfo() {
		manager.data=null;
		manager.setXLSTableInfoList(null);
		manager.vesselCount = 0;
		currentPage=0;
		updateTableListPN();

		pnTableList.removeAll();

		manager.execute("vessel");
		manager.execute("error");
		for(int i=0;i<importTableList.size();i++)
		{
			manager.removeObserver(importTableList.get(i));
		}
		butAdjust.setEnabled(false);	
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

				AddAdvDialog dialog = new AddAdvDialog(null,true,ADVListPanel.this);

			}});
		JButton butSave = new JButton("광고정보저장",new ImageIcon("images/save.gif"));

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
								
								stable.setCompany_abbr(selectedCompany);
								
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

					saveAction(tableli);


				}catch(Exception es)
				{
					JOptionPane.showMessageDialog(null, "등록실패:"+es.getMessage());
					es.printStackTrace();
				}

			}});

		JButton butCancel = new JButton(new ImageIcon("images/cancel.gif"));
		butCancel.setPreferredSize(new Dimension(35,25));
		butCancel.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) {
				//initInfo();
				//_txfCompany.setText("");
				//_txfPage.setText("");
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
		paRight.add(butSave);
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
					String c = selectedCompany+"_";
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
	 * @param li
	 * @throws SQLException
	 * @throws ParseException
	 */ 
	private void saveAction(List li) throws ParseException {

		logger.debug("start");
		resultA.removeAllElements();

		for(int i=0;i<importTableList.size();i++)
		{
			KSGXLSImportPn xlsPn = importTableList.get(i);
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
			KSGXLSImportPn xlsPn = importTableList.get(i);
			Vector portList=xlsPn.getPortList();
			savePortList(xlsPn, portList);
			String insertData = new String();


			// 수정 필요

			insertData =xlsPn.getTableXMLInfo();

			logger.debug("table info : \n"+insertData);

			ShippersTable tableinfo=(ShippersTable) li.get(i);

			ADVData data = new ADVData();

			data.setData(insertData);
			data.setDate_isusse(KSGDateUtil.toDate2(txfImportDate.getText()));
			//TODO 아이디 입력 부분 수정 
			data.setTable_id(tableinfo.getTable_id());
			data.setCompany_abbr(selectedCompany);
			data.setPage(manager.selectedPage);

			data.setTable_id(tableinfo.getTable_id());
			KSGCommand command = new InsertADVCommand(data);
			command.execute();
		}
		JOptionPane.showMessageDialog(null, "광고정보를 저장했습니다.");

	}
	private void savePortList(KSGXLSImportPn xlsPn, Vector portList) {
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
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		
		int currentValue = mainScrollPane.getVerticalScrollBar().getValue();
		int unitIncrement = mainScrollPane.getVerticalScrollBar().getUnitIncrement();
		
		System.out.println(currentValue+","+unitIncrement);
		
		mainScrollPane.getVerticalScrollBar().setValue(currentValue+unitIncrement);
		
	}



}
