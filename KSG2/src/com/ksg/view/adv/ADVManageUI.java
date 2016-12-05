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
package com.ksg.view.adv;

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
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ksg.commands.ImportTextCommand;
import com.ksg.commands.InsertADVCommand;
import com.ksg.commands.KSGCommand;
import com.ksg.commands.SearchPortCommand;
import com.ksg.commands.SearchSheetNameCommand;
import com.ksg.commands.xls.ImportXLSFileCommand;
import com.ksg.commands.xls.ImportXLSFileCommandByXML;
import com.ksg.commands.xls.ImportXLSFileNewCommand;
import com.ksg.commands.xls.ReloadXLSInfoCommand;
import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Port;
import com.ksg.domain.Table_Property;
import com.ksg.model.KSGModelManager;
import com.ksg.model.KSGObserver;
import com.ksg.view.KSGMainFrame;
import com.ksg.view.KSGViewParameter;
import com.ksg.view.adv.PortTableComp.PortColorInfo;
import com.ksg.view.adv.comp.EditTableModel;
import com.ksg.view.adv.comp.PageCellRenderer;
import com.ksg.view.adv.comp.SheetModel;
import com.ksg.view.adv.comp.SimpleFileFilter;
import com.ksg.view.adv.dialog.AddAdvDialog;
import com.ksg.view.adv.dialog.AdjestADVListDialog;
import com.ksg.view.adv.dialog.SheetSelectDialog;
import com.ksg.view.adv.dialog.ViewXLSFileDialog;
import com.ksg.view.comp.CurvedBorder;
import com.ksg.view.comp.FileInfo;
import com.ksg.view.comp.KSGCompboBox;
import com.ksg.view.comp.KSGDialog;
import com.ksg.view.comp.KSGTable2;
import com.ksg.view.comp.KSGTree;
import com.ksg.view.comp.KSGTree1;
import com.ksg.view.comp.KSGTreeDefault;
import com.ksg.view.comp.PageInfo;
import com.ksg.view.search.dialog.AddTableInfoDialog;
import com.ksg.view.util.KSGDateUtil;
import com.ksg.view.util.KSGPropertis;
import com.ksg.view.util.ViewUtil;
import com.ksg.xls.XLSTableInfoMemento;
import com.ksg.xls.model.SheetInfo;

/**
 * @author 박창현
 *
 */
@SuppressWarnings("unchecked")
public class ADVManageUI extends JPanel implements ActionListener,KSGObserver
{	
	
	private static final int _LEFT_SIZE = 250;

	private static int _tableViewCount = 10;

	private static final int ADV_IMPORT_PANEL_ROW_SIZE = 250;
	
	private static final String SEARCH_TYPE_COMPANY = "선사";
	
	private static final String SEARCH_TYPE_PAGE = "페이지";
	
	private static final long serialVersionUID = 1L;
	
	private static KSGTreeDefault tree1;
	
	public KSGTable2 		_tblError;
	
	private JTable			_tblSheetNameList;
	
	private JTable			_tblSheetNameList2;
	
	private JTable 			_tblTable;
	
	private JTree			_treeMenu;
	
	private JTextField  	_txfCPage,_txfPage,_txfPCompany,_txfPort,
	_txfSearchByCompany,_txfVessel,
	txfImportDate,txfTableCount;
	public JTextField		_txfXLSFile,_txfSearchedTableCount,_txfCompany,_txfDate;

	private AdjestADVListDialog adjestADVListDialog;
	
	private ButtonGroup 	bgKeyword;
	
	private JButton 		butAdjust,butCompanyAdd,butPre;
	
	public JButton			butNext;
	
	private JRadioButton 	butVesselOpt,butVoyageOpt;
	
	private JComboBox 		cbxSearchType,cbxSelectedInput;
	private KSGCommand 		command;
	private KSGCompboBox 	comp;
	private JList 			companyLi;
	private Vector 			companyList;

	private DAOManager daoManager = DAOManager.getInstance();
	private JList 			fileLi,fileLi2;
	private Vector<KSGXLSImportPn> importTableList = new Vector<KSGXLSImportPn>();
	private boolean 		isPageSearch=true;
	private boolean 		isSamePageSelect=true;
	private JLabel 			lblCompany2,lblPage2,lblSelectedCompanyName,lblSelectedPage;

	protected Logger 		logger = Logger.getLogger(this.getClass());
	private KSGModelManager manager = KSGModelManager.getInstance();
	private int 			pageCount;
	private JList 			pageLi;
	private Vector 			pageList;
	private JPanel 			pnSubSearch,pnSubSelect,pnTableInfo,pnTableList,pnLeftMenu;
	private JTabbedPane 	pnTab;
	private KSGPropertis 	propertis = KSGPropertis.getIntance();
	private Vector 			resultA = new Vector();

	private int 			resultOK,currentPage,resultCancel,startTableIndex,totalTableCount;

	private String 			searchType=SEARCH_TYPE_COMPANY;
	private String			selectedInput="File";
	private CardLayout 		selectLay,selectLay2;
	private String 			selectXLSFilePath;

	private Vector<ShippersTable> tableInfoList;
	private Table_Port tablePort;

	private TableService 	tableService;

	private JTable 			tblPropertyTable,tblSelectedCompany;


	public ADVManageUI() {

		Properties properties = new Properties();

		this.setName("ADVManageUI");
		tableService = daoManager.createTableService();
		manager.addObservers(this);
		
		createAndUpdateUI();		
	}
	private void actionImportADVInfo() {
		try {

			logger.debug("<=====start=====>");

			manager.isWorkMoniter=true;
			manager.workProcessText="XLS 정보를 가져오는 중...";
			manager.execute(KSGMainFrame.NAME);
			int result=actionImportADVInfoSub();
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
				updateTableInfo();
				pnTab.setSelectedIndex(1);
				butAdjust.setEnabled(true);
				butNext.setEnabled(true);
				KSGModelManager.getInstance().processBar.close();
			
				JOptionPane.showMessageDialog(ADVManageUI.this, manager.tableCount+"개의 광고테이블을 불러왔습니다.");
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

	private void actionImportADVInfo2() 
	{
		try {

			manager.isWorkMoniter=true;
			manager.workProcessText="XLS 정보를 가져오는 중...";
			manager.execute(KSGMainFrame.NAME);
			int result=actionImportADVInfoSub2();
			if(result==1)
			{
				for(int i=0;i<manager.tableCount;i++)
				{
					KSGXLSImportPn table = new KSGXLSImportPn();
					table.setPreferredSize(new Dimension(0,150));
					table.setName("adv");	
					table.setTableIndex(i);

					table.addMouseListener(myMouseListener);
					manager.addObservers(table);
					importTableList.add(table);
				}

				updateTableListPN();
				updateTableInfo();
				
				
				pnTab.setSelectedIndex(1);
				butAdjust.setEnabled(true);
				butNext.setEnabled(true);
				JOptionPane.showMessageDialog(ADVManageUI.this, manager.tableCount+"개의 광고테이블을 불러왔습니다.");
				manager.isWorkMoniter=false;
				manager.workProcessText="";
				manager.execute(KSGMainFrame.NAME);

				this.adjestADVListDialog = new AdjestADVListDialog(this, tableInfoList);
				adjestADVListDialog.createAndUpdateUI();
			}


		} catch (Exception e1) {

			JOptionPane.showMessageDialog(null,"광고정보생성에 실패했습니다.\nerror: "+e1.getMessage());
			manager.isWorkMoniter=false;
			e1.printStackTrace();
			return;
		}
	}

	private int actionImportADVInfoSub() {
		try{
			logger.debug("start");
			importTableList = new Vector<KSGXLSImportPn>();				
			String company=_txfCompany.getText();
			String page = _txfPage.getText();
			resultOK = 1;
			resultCancel = 0;
			
			//입력된 데이터 확인
			if(company.equals(""))
			{
				JOptionPane.showMessageDialog(null, "선사를 선택하십시요");
				manager.isWorkMoniter=false;
				return  resultCancel;
			}

			DefaultListModel fileModel = (DefaultListModel) fileLi.getModel();
			if(fileModel.size()==0)
			{
				JOptionPane.showMessageDialog(null, "엑셀 파일을 선택하십시요");
				manager.isWorkMoniter=false;
				return  resultCancel;
			}

			Vector sheetList = getSelectedSheetList(_tblSheetNameList);

			if(sheetList.size()==0)
			{
				SheetSelectDialog dialog  = new SheetSelectDialog(_tblSheetNameList);
				dialog.createAndUpdateUI();
			}

			sheetList = getSelectedSheetList(_tblSheetNameList);

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
				int pageRow = pageLi.getModel().getSize();
				DefaultListModel model = (DefaultListModel) pageLi.getModel();
				for(int i=0;i<pageRow;i++)
				{
					PageInfo info=(PageInfo) model.get(i);
					if(info.isSelected())
					{
						ShippersTable table = new ShippersTable();
						table.setCompany_abbr(_txfCompany.getText());
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

			int keyType=0;

			keyType=  butVesselOpt.isSelected()?ImportXLSFileCommand.VESSEL:ImportXLSFileCommand.VOYAGE;

			logger.debug("execute importxlsfilecommand");
			command = new ImportXLSFileCommand(tableInfoList,
					sheetList,
					selectXLSFilePath,
					company,
					Integer.parseInt(page),
					searchType,
					selectedInput,keyType);
			command.execute();

			command = new ImportXLSFileCommandByXML(tableInfoList);
			command.execute();

			_txfSearchedTableCount.setText(String.valueOf(manager.tableCount));
			logger.debug("end:resultOK");
			return resultOK;
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.debug("end:resultCancel,errorCode:"+e.getMessage());
			return resultCancel;
		}

	}


	private int actionImportADVInfoSub2()

	{
		try
		{
			resultOK = 1;
			resultCancel = 0;
			importTableList = new Vector<KSGXLSImportPn>();			
			DefaultTableModel companyModel =(DefaultTableModel) tblSelectedCompany.getModel();
			if(companyModel.getRowCount()==0)
			{
				JOptionPane.showMessageDialog(null, "선사 정보를 추가하십시요");
				manager.isWorkMoniter=false;
				return  resultCancel;
			}
			DefaultListModel fileModel = (DefaultListModel) fileLi2.getModel();
			if(fileModel.size()==0)
			{
				JOptionPane.showMessageDialog(null, "엑셀 파일을 선택하십시요");
				manager.isWorkMoniter=false;
				return  resultCancel;
			}
			Vector sheetList = getSelectedSheetList(_tblSheetNameList2);
			if(sheetList.size()==0)
			{
				JOptionPane.showMessageDialog(null, "선택된 sheet가 없습니다.");	
				manager.isWorkMoniter=false;
				return  resultCancel;
			}
			companyList = new Vector();
			tableInfoList = new Vector<ShippersTable>();
			for(int i=0;i<companyModel.getRowCount();i++)
			{
				ShippersTable shippersTable = new ShippersTable();
				shippersTable.setCompany_abbr(companyModel.getValueAt(i, 2).toString());
				shippersTable.setPage((Integer)companyModel.getValueAt(i, 3));
				shippersTable.setTable_index((Integer)companyModel.getValueAt(i, 4));
				shippersTable.setTable_id(companyModel.getValueAt(i, 5).toString());

				companyList.add(shippersTable);
				tableInfoList.add(shippersTable);
			}


			ImportXLSFileNewCommand fileNewCommand = new ImportXLSFileNewCommand(companyList, getSelectedSheetList(_tblSheetNameList2));
			fileNewCommand.execute();

			_txfSearchedTableCount.setText(String.valueOf(manager.tableCount));



			return resultOK;
		}catch(Exception e)
		{
			e.printStackTrace();
			return resultCancel;
		}
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
		else if(command.equals("위치조정"))
		{

			if(adjestADVListDialog==null)
			{
				this.adjestADVListDialog = new AdjestADVListDialog(this, tableInfoList);
				adjestADVListDialog .setShipper(_txfCompany.getText());
				adjestADVListDialog.createAndUpdateUI();
			}else
			{
				adjestADVListDialog .setShipper(_txfCompany.getText());
			}
			adjestADVListDialog.setVisible(true);
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
		buildSearchOptioinPN();
		pnTab.addTab("입력정보", buildSearchOptioinPN());

		pnTab.addTab("결과", buildHistoryCenter());

		return pnTab;
	}

	private Component buildSearchOptioinPN2() 
	{
		XLSSearchOptionPn optionPn = new XLSSearchOptionPn();
		return optionPn;
	}
	private Component buildSearchOptioinPN3() 
	{
		NewSearchOptionPn optionPn = new NewSearchOptionPn();
		return optionPn;
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
						List li=tableService.getTableProperty(_txfCompany.getText(),(Integer)c.get(j));

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

		pnSubControl1.add(_txfCompany);

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
	private JComponent buildHistoryAndLegendPN() {

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

		JButton butReLoad = new JButton("다시 불러오기");
		butReLoad.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				initInfo();
				if(selectedInput.equals("File"))
				{
					actionImportADVInfo();
				}else
				{
					importADVTextInfoAction();
				}
			}});


		JPanel pnNorthLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnNorthRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		pnNorthLeft.add(lblTableCountlbl);
		pnNorthLeft.add(_txfSearchedTableCount);


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
		_txfSearchByCompany.addKeyListener(new KeyAdapter(){public void keyPressed(KeyEvent e) 
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
						tree1.setGroupBy(KSGTree1.GroupByCompany);
					}
					else if(te.equals("페이지별"))
					{
						tree1.setGroupBy(KSGTree1.GroupByPage);
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

	private Component buildProgress() {
		JPanel pnMain = new JPanel();
		pnMain.setPreferredSize(new Dimension(0,15));
		pnMain.add(new JLabel("엑셀 정보를 가져오는 중..."));
		return pnMain;
	}

	private JPanel buildSearchOptioinPN() {
		_tblSheetNameList = new JTable();
		JPanel pnMain = new JPanel();
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
				updateTableListPN();
				butNext.setEnabled(false);
				butPre.setEnabled(false);

				updateUI();

			}});

		comp.setPreferredSize(new Dimension(100,25));
		pnMain.setLayout(new BorderLayout());
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
				initInfo();
				if(selectedInput.equals("File"))
				{
					actionImportADVInfo();

				}else
				{
					importADVTextInfoAction();
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

		pnMain.add(pnControl,BorderLayout.NORTH);
		
		pnMain.add(tabbedPane,BorderLayout.CENTER);
		return pnMain;
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

				AddAdvDialog dialog = new AddAdvDialog(null,true,ADVManageUI.this);

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
	}

	private Component buildTest() {
		JPanel pnMain = new JPanel(new BorderLayout());

		JPanel pnSearchOp = new JPanel(new BorderLayout());
		JPanel pnSearchOpLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lblCompany = new JLabel("선사 정보",new ImageIcon("images/buticon.png"),JLabel.LEFT);
		pnSearchOpLeft.add(lblCompany);

		JPanel pnSearchButtomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton butImportFile = new JButton("\n불러오기",new ImageIcon("images/importxls.gif"));
		butImportFile.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				actionImportADVInfo2();

			}});
		pnSearchButtomRight.add(butImportFile);

		JPanel pnSearchButtom = new JPanel(new BorderLayout());

		JPanel pnSearchButtomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lblCompanyName = new JLabel("선사명 : ");

		lblSelectedCompanyName = new JLabel();
		lblSelectedCompanyName.setPreferredSize(new Dimension(100,22));
		lblSelectedCompanyName.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		JLabel lblPage = new JLabel("페이지 : ");
		lblSelectedPage = new JLabel();
		lblSelectedPage.setPreferredSize(new Dimension(45,22));
		lblSelectedPage.setBorder(BorderFactory.createLineBorder(Color.lightGray));

		pnSearchButtomLeft.add(lblCompanyName);


		butCompanyAdd = new JButton("추가");
		butCompanyAdd.setEnabled(false);
		butCompanyAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				try {
					logger.debug("company:"+lblSelectedCompanyName.getText()+",page:"+lblSelectedPage.getText());
					List li=	tableService.getTableListByCompany(lblSelectedCompanyName.getText(), Integer.parseInt(lblSelectedPage.getText()));

					DefaultTableModel model =(DefaultTableModel) tblSelectedCompany.getModel();

					for(int i=0;i<li.size();i++)
					{

						ShippersTable p=(ShippersTable) li.get(i);

						boolean isAdd = true;
						for(int j=0;j<model.getRowCount();j++)
						{
							if(p.getCompany_abbr().equals(model.getValueAt(j, 2))&&
									p.getPage()==(Integer)model.getValueAt(j, 3)&&
									p.getTable_index()==(Integer)model.getValueAt(j, 4))
							{
								isAdd=false;
							}
						}
						if(isAdd)
						{
							model.addRow(new Object[]{new Boolean(false),

									p.getTitle(),
									p.getCompany_abbr(),
									p.getPage(),
									p.getTable_index(),
									p.getTable_id()});
						}


					}
					TableColumnModel colmodel_width =tblSelectedCompany.getColumnModel();

					colmodel_width.getColumn(0).setMaxWidth(25);
					colmodel_width.getColumn(3).setMaxWidth(55);
					colmodel_width.getColumn(4).setMaxWidth(55);
					tblSelectedCompany.setModel(model);

					lblSelectedCompanyName.setText("");
					lblSelectedPage.setText("");

				} catch (SQLException e1) {
					e1.printStackTrace();
				}

			}});
		JButton butDel = new JButton("선택삭제");
		butDel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				DefaultTableModel oldModel =(DefaultTableModel) tblSelectedCompany.getModel();
				DefaultTableModel newModel = new DefaultTableModel(){
					public Class getColumnClass(int c) {
						return getValueAt(0, c).getClass();
					}
				};
				int row= oldModel.getRowCount();
				String[]colName = {"","제목","선사명","페이지","인덱스","테이블 ID"};

				for(int i=0;i<colName.length;i++)
					newModel.addColumn(colName[i]);

				logger.debug("row:"+row);
				for(int i=0;i<row;i++)
				{
					Boolean use=(Boolean)oldModel.getValueAt(i, 0);
					logger.debug("boolean:"+use+","+oldModel.getColumnCount());
					if(!use.booleanValue())
					{
						Object[] newObj =new Object[oldModel.getColumnCount()];

						for(int j=0;j<oldModel.getColumnCount();j++)
						{
							newObj[j]=oldModel.getValueAt(i, j);							
						}
						newModel.addRow(newObj);
					}
				}

				tblSelectedCompany.setModel(newModel);
				
				TableColumnModel colmodel_width =tblSelectedCompany.getColumnModel();

				colmodel_width.getColumn(0).setMaxWidth(25);
				
				colmodel_width.getColumn(3).setMaxWidth(55);
				
				colmodel_width.getColumn(4).setMaxWidth(55);

				tblSelectedCompany.updateUI();

			}});

		pnSearchButtomLeft.add(lblCompanyName);
		
		pnSearchButtomLeft.add(lblSelectedCompanyName);
		
		pnSearchButtomLeft.add(lblPage);
		
		pnSearchButtomLeft.add(lblSelectedPage);
		
		pnSearchButtomLeft.add(butCompanyAdd);
		
		pnSearchButtomLeft.add(butDel);


		pnSearchButtom.add(pnSearchButtomLeft,BorderLayout.WEST);
		pnSearchButtom.add(pnSearchButtomRight,BorderLayout.EAST);


		pnSearchOp.add(pnSearchOpLeft,BorderLayout.WEST);		
		pnSearchOp.add(pnSearchButtom,BorderLayout.SOUTH);



		tblSelectedCompany = new JTable(new DefaultTableModel(){
			public Class getColumnClass(int c) {

				return getValueAt(0, c).getClass();
			}
		});
		tblSelectedCompany.setRowHeight(20);
		DefaultTableModel model =(DefaultTableModel) tblSelectedCompany.getModel();

		String[]colName = {"","제목","선사명","페이지","인덱스","테이블 ID"};

		for(int i=0;i<colName.length;i++)
			model.addColumn(colName[i]);

		TableColumnModel colmodel_width =tblSelectedCompany.getColumnModel();

		colmodel_width.getColumn(0).setMaxWidth(25);
		colmodel_width.getColumn(3).setMaxWidth(55);
		colmodel_width.getColumn(4).setMaxWidth(55);
		tblSelectedCompany.setModel(model);

		JPanel box =new JPanel();
		GridLayout gridLayout = new GridLayout(1,0);
		gridLayout.setHgap(10);
		box.setLayout(gridLayout);
		box.setPreferredSize(new Dimension(0,300));

		JPanel pnFile = new JPanel(new BorderLayout());
		JPanel pnFileTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel lblFileTitle = new JLabel("XLS 파일 정보",new ImageIcon("images/buticon.png"),JLabel.LEFT);		
		JPanel pnFileControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butPuls = new JButton("+");

		butPuls.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				fileAddAction(fileLi2,_tblSheetNameList2);


			}});
		JButton butMinus = new JButton("-");
		butMinus.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				fileDelAction(fileLi2,_tblSheetNameList2);

			}});

		JButton butUp1 = new JButton("▲");

		butUp1.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				fileUPAction(fileLi2);
			}});

		JButton butDown1 = new JButton("▼");
		butDown1.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				fileDownAction(fileLi2);


			}});



		pnFileControl.add(butPuls);
		pnFileControl.add(butMinus);
		pnFileControl.add(butUp1);
		pnFileControl.add(butDown1);

		fileLi2 = new JList();
		fileLi2.setComponentPopupMenu(createXLSListPopup());

		fileLi2.setModel(new DefaultListModel());
		JScrollPane scrollPane = new JScrollPane(fileLi2);

		pnFileTitle.add(lblFileTitle);
		pnFile.add(pnFileTitle,BorderLayout.NORTH);
		pnFile.add(pnFileControl,BorderLayout.SOUTH);
		pnFile.add(scrollPane);

		JPanel pnSheet = new JPanel(new BorderLayout());
		JTable tblSheet = new JTable();
		pnSheet.add(new JScrollPane(tblSheet));

		JPanel pnSheetTitle = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnSheetControl = new JPanel(new BorderLayout());


		JCheckBox cbxc = new JCheckBox();
		cbxc.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.DESELECTED:
					int row =_tblSheetNameList2.getRowCount();
					for(int i=0;i<row;i++)
					{
						Boolean use=(Boolean) _tblSheetNameList2.getValueAt(i, 3);

						_tblSheetNameList2.setValueAt(false, i, 3);
					}
					break;
				case ItemEvent.SELECTED:
					int row2 =_tblSheetNameList2.getRowCount();
					for(int i=0;i<row2;i++)
					{
						Boolean use=(Boolean) _tblSheetNameList2.getValueAt(i, 3);

						_tblSheetNameList2.setValueAt(true, i, 3);
					}
					break;
				default:
					break;
				}
				_tblSheetNameList2.updateUI();

			}

		});

		JButton butUp = new JButton("▲");
		butUp.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int row=_tblSheetNameList2.getSelectedRow();
				int col=_tblSheetNameList2.getSelectedColumn();
				if(row==-1)
					return ;
				SheetModel model = (SheetModel) _tblSheetNameList2.getModel();
				model.upRow(row);
				if(row!=0)
					_tblSheetNameList2.changeSelection(row-1, col, false, false);
				_tblSheetNameList2.updateUI();

			}});


		JButton butDown = new JButton("▼");
		butDown.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int row=_tblSheetNameList2.getSelectedRow();
				int col=_tblSheetNameList2.getSelectedColumn();
				if(row==-1)
					return ;
				SheetModel model = (SheetModel) _tblSheetNameList2.getModel();
				model.downRow(row);

				if(row<_tblSheetNameList2.getRowCount()-1)
					_tblSheetNameList2.changeSelection(row+1, col, false, false);
				_tblSheetNameList2.updateUI();

			}});


		JPanel pnSheetControlLeft = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnSheetControlLeft.add(new JLabel("전체선택"));
		pnSheetControlLeft.add(cbxc);



		JPanel pnSheetControlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnSheetControlRight.add(butUp);
		pnSheetControlRight.add(butDown);


		pnSheetControl.add(pnSheetControlRight,BorderLayout.EAST);
		pnSheetControl.add(pnSheetControlLeft,BorderLayout.WEST);


		JLabel lblSheetTitle = new JLabel("Sheet 정보",new ImageIcon("images/buticon.png"),JLabel.LEFT);

		pnSheetTitle.add(lblSheetTitle);
		_tblSheetNameList2 = new JTable();

		pnSheet.add(pnSheetTitle,BorderLayout.NORTH);
		pnSheet.add(pnSheetControl,BorderLayout.SOUTH);
		pnSheet.add(new JScrollPane(_tblSheetNameList2));


		box.add(pnFile);
		box.add(pnSheet);

		JPanel pnSelectedCompanyInfo = new JPanel(new BorderLayout());
		pnSelectedCompanyInfo.add(new JScrollPane(tblSelectedCompany));
		JPanel pnSelectedCompanyInfoButtom = new JPanel(new BorderLayout());

		JPanel pnSelectedCompanyInfoButtomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnSelectedCompanyInfoButtomLeft.add(new JLabel("전체선택"));
		JCheckBox cbxSelectAll = new JCheckBox();

		cbxSelectAll.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange()) {
				case ItemEvent.DESELECTED:
					int row =tblSelectedCompany.getRowCount();
					for(int i=0;i<row;i++)
					{
						Boolean use=(Boolean) tblSelectedCompany.getValueAt(i, 0);

						tblSelectedCompany.setValueAt(false, i, 0);
					}
					break;
				case ItemEvent.SELECTED:
					int row2 =tblSelectedCompany.getRowCount();
					for(int i=0;i<row2;i++)
					{
						Boolean use=(Boolean) tblSelectedCompany.getValueAt(i, 0);

						tblSelectedCompany.setValueAt(true, i, 0);
					}
					break;
				default:
					break;
				}
				tblSelectedCompany.updateUI();

			}

		});

		pnSelectedCompanyInfoButtomLeft.add(cbxSelectAll);
		JPanel pnSelectedCompanyInfoButtomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butUp2 = new JButton("▲");

		butUp2.setEnabled(false);


		JButton butDown2 = new JButton("▼");
		
		butDown2.setEnabled(false);

		pnSelectedCompanyInfoButtomRight.add(butUp2);
		
		pnSelectedCompanyInfoButtomRight.add(butDown2);

		pnSelectedCompanyInfoButtom.add(pnSelectedCompanyInfoButtomLeft,BorderLayout.WEST);
		
		pnSelectedCompanyInfoButtom.add(pnSelectedCompanyInfoButtomRight,BorderLayout.EAST);
		
		pnSelectedCompanyInfo.add(pnSelectedCompanyInfoButtom,BorderLayout.SOUTH);

		pnMain.add(pnSelectedCompanyInfo);
		
		pnMain.add(pnSearchOp,BorderLayout.NORTH);
		
		pnMain.add(box,BorderLayout.SOUTH);

		return pnMain;
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

						initInfo();
						importTableList = new Vector<KSGXLSImportPn>();
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
									table.setCompany_abbr(_txfCompany.getText());
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
							KSGXLSImportPn table = new KSGXLSImportPn();
							table.setName("adv");	
							table.setTableIndex(i);

							table.addMouseListener(new MouseAdapter(){
								public void mouseClicked(MouseEvent e) {
									KSGXLSImportPn ta =(KSGXLSImportPn) e.getSource();
									int index=ta.getTableIndex();
									logger.debug("selected table: "+index	);
									manager.selectTableIndex=index;
									manager.execute("error");
								}
							});
							manager.addObservers(table);
							importTableList.add(table);
						}

						updateTableListPN();
						butNext.setEnabled(true);
						updateTableInfo();
						JOptionPane.showMessageDialog(ADVManageUI.this, manager.tableCount+"개의 광고테이블을 불러왔습니다.");

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
	/**
	 * 
	 */
	private void createAndUpdateUI() 
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
		this.add(buildLeftMenu(),BorderLayout.WEST);
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
		tree1.setComponentPopupMenu(createTreePopuomenu());
		try {

			tree1.addTreeSelectionListener(new TreeSelectionListener(){

				public void valueChanged(TreeSelectionEvent e) {

					TreePath path=e.getNewLeadSelectionPath();
					if(path!=null)
					{

						try{
							updateViewByTree(path);
						}catch(Exception e2)
						{
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


	private void fileAddAction(JList fileLi,JTable table) {
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
		updateSheetNameList(fileLi, _tblSheetNameList2);
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
		updateSheetNameList(fileLi, _tblSheetNameList2);
	}

	/**
	 * @return
	 */
	private Vector getSelectedSheetList(JTable _tblSheetNameList) {
		// 선택된 쉬트 파악
		logger.debug("start");
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
	/**
	 * 
	 */
	private void importADVTextInfoAction() {
		logger.debug("start");
//		manager.ADVStringData=null;
		importTableList = new Vector<KSGXLSImportPn>();				
		String company=_txfCompany.getText();

		if(company.equals(""))
		{
			JOptionPane.showMessageDialog(null, "선사를 선택하십시요");

			return;
		}
		if(searchType.equals(SEARCH_TYPE_COMPANY))
		{
			tableInfoList = new Vector();
			int pageRow = pageLi.getModel().getSize();
			for(int i=0;i<pageRow;i++)
			{
				DefaultListModel model = (DefaultListModel) pageLi.getModel();

				PageInfo info=(PageInfo) model.get(i);
				if(info.isSelected())
				{
					ShippersTable table = new ShippersTable();
					table.setCompany_abbr(_txfCompany.getText());
					table.setPage((Integer)info.chekInfo);
					tableInfoList.add(table);
				}
			}
			JOptionPane.showMessageDialog(null, manager.tableCount);
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
				}
			}
		}
		for(int i=0;i<manager.tableCount;i++)
		{
			KSGXLSImportPn table = new KSGXLSImportPn();
			table.setPreferredSize(new Dimension(0,300));
			table.setName("adv");	
			table.setTableIndex(i);

			table.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e) {
					KSGXLSImportPn ta =(KSGXLSImportPn) e.getSource();
					int index=ta.getTableIndex();
					logger.debug("selected table: "+index	);
					manager.selectTableIndex=index;
					manager.execute("error");
				}
			});
			manager.addObservers(table);
			importTableList.add(table);
		}

		updateTableListPN();
		butNext.setEnabled(true);
		updateTableInfo();
		JOptionPane.showMessageDialog(ADVManageUI.this, manager.tableCount+"개의 광고테이블을 불러왔습니다.");
		logger.debug("start");
	}

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

	/**
	 * 
	 */
	private void initInfo() {
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
			data.setCompany_abbr(_txfCompany.getText());
			data.setPage(manager.selectedPage);

			data.setTable_id(tableinfo.getTable_id());
			KSGCommand command = new InsertADVCommand(data);
			command.execute();
		}
		JOptionPane.showMessageDialog(null, "광고정보를 저장했습니다.");

	}
	private void savePortList(KSGXLSImportPn xlsPn, Vector portList) {
		try {
			Table_Port delPort = new Table_Port();
			delPort.setTable_id(xlsPn.getTable_id());
			tableService.deleteTablePort(delPort);

			for(int j=0;j<portList.size();j++)
			{
				PortColorInfo port=(PortColorInfo) portList.get(j);
				if(port.getArea_code()==null)
					continue;
				tablePort = new Table_Port();
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

	public void setTabIndex(int i) {
		pnTab.getModel().setSelectedIndex(i);

	}
	public void update(KSGModelManager manager) 
	{
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

	/**
	 * 
	 */
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

	private void updateTableInfo2(DefaultListModel listModel) 
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
			KSGXLSImportPn table=importTableList.get(i);
			JScrollPane scrollPane = new JScrollPane(table);
			TitledBorder createTitledBorder = BorderFactory.createTitledBorder(table.getTitle());
			createTitledBorder.setTitleFont(new Font("돋음",0,12));

			scrollPane.setBorder(createTitledBorder);
			pnTableList.add(scrollPane);
		}

		this.updateUI();
	}

	private void updateViewByTree(TreePath path) {

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
					updateTableInfo2(listModel);
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
					updateTableInfo2(listModel);

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
	}
}


