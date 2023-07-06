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
package com.ksg.workbench.print;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.ksg.commands.CreateXTGCommand;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ADVData;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.print.logic.quark.v1.XTGPage;
import com.ksg.service.ADVService;
import com.ksg.service.TableService;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.checkbox.KSGCheckBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.radiobutton.KSGRadioButton;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.view.comp.tree.KSGTree;
import com.ksg.view.comp.tree.KSGTreeDefault;
import com.ksg.view.comp.tree.KSGTreeImpl;
import com.ksg.workbench.common.comp.AbstractMgtUI;

public class PrintADVUI extends AbstractMgtUI implements ActionListener, KSGObserver{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextArea dataF;
	
	private JTextArea dataE;

	KSGModelManager manager = KSGModelManager.getInstance();
	
	private JTable tblTableList;
	
	private String selectDate;
	
	private JLabel lblDate;
	
	private JTable tblSubTableList;
	
	private String selectCompany;
	
	private JTextField _txfSearchByCompany;
	
	private static KSGTreeDefault tree2;
	
	private KSGPanel _pnADVTableList;
	
	private boolean 			isPageSearch=true;
	
	private JTree				_treeMenu;
	
	private static final int _LEFT_SIZE = 250;
	
	DAOManager daomanager = DAOManager.getInstance();

	private int inD_flag=1;
	
	private TableService tableService;
	
	private ADVService _advService;


	public PrintADVUI() {

		super();
		
		this.setName("PrintADVUI");
		
		manager.addObservers(this);
		
		selectDate = KSGDateUtil.format(new Date());
		
		_advService =daomanager.createADVService();
		
		tableService = new TableServiceImpl();

		this.title = "광고정보 출력";
		
		this.borderColor = new Color(107,138,15);
		
		createAndUpdateUI();
	}

	public void createAndUpdateUI() {
		
		this.setLayout(new BorderLayout(10,10));
		
		dataF = new JTextArea(15,15);
		
		dataE = new JTextArea();

		KSGPanel pnCenter = buildCenterPN();

		KSGPanel pnLeftMenu = buildLeftMenu();		

		KSGPanel pnInfo = buildNorthPn();

		this.add(pnInfo,BorderLayout.NORTH);
		
		this.add(pnCenter,BorderLayout.CENTER);
		
		this.add(pnLeftMenu,BorderLayout.WEST);
	}


	private KSGPanel buildCenterPN() {
		
		KSGPanel pnCenter = new KSGPanel();
		
		KSGPanel pnCenterControl = new KSGPanel(new BorderLayout());

		BorderLayout bl_pnCenter = new BorderLayout();
		pnCenter.setLayout(bl_pnCenter);

		cbxAgent = new KSGCheckBox("동일 Agent 선택");
		
		pnCenterControl.add(cbxAgent,BorderLayout.WEST);

		

		_pnADVTableList = new KSGPanel();
		
		_pnADVTableList.setLayout( new BorderLayout());

		lblMessage = new JLabel("광고정보가 없습니다.");
		
		lblCompany = new JLabel("");
		
		lblCompany.setIcon(new ImageIcon("images/trans.png"));
		
		_pnADVTableList.add(new JScrollPane(dataF));


		KSGPanel pnInfo = new KSGPanel();
		
		BorderLayout bl_pnInfo = new BorderLayout();
		
		pnInfo.setLayout( bl_pnInfo);
		
		pnInfo.add(lblCompany,BorderLayout.WEST);
		
		txfDate = new JTextField(10);
		
		txfDate.addKeyListener(new KeyAdapter(){


			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					JTextField f =(JTextField) e.getSource();
					String date = f.getText();
					if(date.length()==8)
					{
						String year=date.substring(0,4);
						String month=date.substring(4,6);
						String day=date.substring(6,8);
						String date2 = year+"-"+month+"-"+day;

						selectDate=date2;
						lblDate.setText(selectDate);
						viewUpdate();

						f.setText("");


					}
					if(date.length()==10)
					{
						lblDate.setText(date);
						viewUpdate();
						f.setText("");

					}
					else
					{
						JOptionPane.showMessageDialog(null, "입력 형식이 틀렸습니다.ex)yyyymmdd");
					}
				}
			}});
		KSGPanel pnSub = new KSGPanel();
		FlowLayout flowLayout_1 = (FlowLayout) pnSub.getLayout();
		
		pnInfo.add(pnSub,BorderLayout.EAST);
		JCheckBox box = new KSGCheckBox("월요일");
		box.setFont(new Font("굴림", Font.PLAIN, 12));
		box.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					txfDate.setText(KSGDateUtil.format(KSGDateUtil.nextMonday(new Date())));
				}
			}
		});
		

	
		//-----------------
		JCheckBox chckbxNewCheckBox = new KSGCheckBox("인디자인체크");

		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JCheckBox newbo =(JCheckBox) arg0.getSource();
				if(newbo.isSelected())
				{
					inD_flag=2;
					System.out.println ("InDesign 체크");

				}
				else
				{
					inD_flag=1;
					System.out.println("InDesign 체크 해제");
				}
			}
		}
				);
		
		
		lblDate = new JLabel(KSGDateUtil.format(new Date()));
		
		pnSub.add(new JLabel("출력날짜 : "));
		
		pnSub.add(lblDate);
		
		pnSub.add(txfDate);
		
		pnSub.add(box);
		
		pnSub.add(chckbxNewCheckBox);
		
		KSGGradientButton butExport = new KSGGradientButton("파일출력", "images/print.png",25,20);
		pnSub.add(butExport);

		butExport.setToolTipText("출력하기");
		butExport.setActionCommand("출력하기");
		butExport.addActionListener(this);
//		pnInfo.add(info,BorderLayout.SOUTH);
		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout( new BorderLayout());
		pnMain.add(pnInfo,BorderLayout.NORTH);
		pnMain.add(_pnADVTableList,BorderLayout.CENTER);
		pnMain.add(pnCenterControl,BorderLayout.SOUTH);
		
		
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));

		pnCenter.add(pnMain,BorderLayout.CENTER);
		pnCenter.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));


		return pnCenter;
	}

	private void viewUpdate()
	{
		try {

			this.updateSubTableList(selectCompany, selectDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateSubTableList(String company_abbr,String date) throws SQLException
	{
		String[]colums={"Company","Date","Order"};
		List li2=_advService.getADVDataListAddOrder(company_abbr,date);

		DefaultTableModel model2 = new KSGTableModel(colums,li2.size());
		for(int i=0;i<li2.size();i++)
		{
			ADVData company = (ADVData) li2.get(i);

			model2.setValueAt(company.getCompany_abbr(), i, 0);

			model2.setValueAt(company.getDate_isusse(), i, 1);
			model2.setValueAt(company.getT_index(), i, 2);	

		}

	}

	private KSGPanel buildLeftMenu() 
	{
		KSGPanel pnLefMain = new KSGPanel();
		KSGPanel pnSearch =  new KSGPanel();
		pnSearch.setLayout( new BorderLayout());

		_treeMenu = createTreeMenu();		
		_txfSearchByCompany = new JTextField(8);

		KSGPanel pnSearchByCompany = new KSGPanel();
		JLabel lblCompany = new JLabel("선사 검색");
		
		pnSearchByCompany .add(lblCompany);
		pnSearchByCompany .add(_txfSearchByCompany);
		_txfSearchByCompany.addKeyListener(new KeyAdapter(){public void keyPressed(KeyEvent e) 
		{
			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				tree2.setSelectionRow(0);
				String text=_txfSearchByCompany.getText();
				if(!isPageSearch)

				{
					DefaultMutableTreeNode node = KSGTree.searchNodeByCompany(tree2,text);
					if(node!=null)
					{
						TreeNode[] nodes = ((DefaultTreeModel)tree2.getModel()).getPathToRoot(node);
						TreePath path = new TreePath(nodes);
						tree2.scrollPathToVisible(path);
						tree2.setSelectionPath(path);
					}else
					{
						JOptionPane.showMessageDialog(null, "해당선사가 없습니다.");
					}

				}else
				{
					try{
						int page= Integer.parseInt(text);
						DefaultMutableTreeNode node = KSGTree.searchNodeByPage(tree2,page);
						if(node!=null)
						{
							TreeNode[] nodes = ((DefaultTreeModel)tree2.getModel()).getPathToRoot(node);
							TreePath path = new TreePath(nodes);
							tree2.scrollPathToVisible(path);
							tree2.setSelectionPath(path);
						}else
						{
							JOptionPane.showMessageDialog(null, "해당 Page가 없습니다.");
						}
					}catch (NumberFormatException ee) {
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, text+":정확한 숫자를 입력하세요");
						ee.printStackTrace();
						logger.error(ee.getMessage());
					}
				}
				_txfSearchByCompany.setText("");
			}
		}
		});
		JCheckBox box = new KSGCheckBox("페이지", true);
		
		box.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				JCheckBox box =(JCheckBox) e.getSource();
				isPageSearch=box.isSelected();

			}});

		pnSearchByCompany.add(box);

		pnLefMain.setLayout(new BorderLayout());

		pnLefMain.add(new JScrollPane(_treeMenu),BorderLayout.CENTER);
		pnLefMain.setPreferredSize(new Dimension(_LEFT_SIZE,100));
		KSGPanel panel = new KSGPanel();
		ButtonGroup group = new ButtonGroup();


		JRadioButton butCompany = new KSGRadioButton("선사별");
		
		JRadioButton butPage = new KSGRadioButton("페이지별",true);
		
		group.add(butCompany);
		group.add(butPage);

		panel.add(butCompany);
		panel.add(butPage);

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
			butCompany.addItemListener(itemListener);
			butPage.addItemListener(itemListener);
			panel.add(new JSeparator(JSeparator.HORIZONTAL));
			JButton butADDTable = new KSGGradientButton("","images/plus.gif");

			butADDTable.setPreferredSize(new Dimension(35,25));
			butADDTable.setFocusPainted(false);
			butADDTable.setActionCommand("신규등록");
			butADDTable.setToolTipText("신규 테이블 등록");
			butADDTable.addActionListener(this);

			panel.add(butADDTable);
			JButton butDelTable = new KSGGradientButton("","images/minus.gif");
			butDelTable.setPreferredSize(new Dimension(35,25));
			butDelTable.setFocusPainted(false);
			butDelTable.setActionCommand("삭제");
			butDelTable.addActionListener(this);
			panel.add(butDelTable);

			pnSearch.add(pnSearchByCompany,BorderLayout.CENTER);
			
			
			pnLefMain.add(pnSearch,BorderLayout.NORTH);
			pnLefMain.add(panel,BorderLayout.SOUTH);
			
			pnLefMain.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			
			KSGPanel pnMain = new KSGPanel(new BorderLayout());
			pnMain.add(pnLefMain);
			pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

			return pnMain;
	}
	private JTree createTreeMenu() 
	{
		tree2 = new KSGTreeDefault("tree1");
		tree2.setRowHeight(25);
		tree2.update();


		tree2.addTreeSelectionListener(new TreeSelectionListener(){

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
			private void updateViewByTree(TreePath path) {
				String selectedCompany = path.getLastPathComponent().toString();
				
				logger.info("tree select:{},{}",selectedCompany, path.getPathCount());
				switch (path.getPathCount()) {
				case 1: // root 선택시
					lblCompany.setText("");
					manager.selectedCompany=null;
					selectedPage=-1;
					selectedCompany = null;
					dataF.setText("");
					break;
				case 2: //2번째 노드 선택 ex:0~9
					lblCompany.setText("");
					manager.selectedCompany=null;
					selectedPage=-1;
					selectedCompany = null;
					dataF.setText("");
					//						_pnADVTableList.removeAll();

					break;
				case 3: //3번재 노드 선택 ex) 11:OOCL
					dataF.setText("");
					StringTokenizer st = new StringTokenizer(selectedCompany,":");


					String com = new String();
					String page = new String();

					page=st.nextToken();
					com = st.nextToken();
					lblCompany.setText("선사명 : "+com);
					selectedPage=Integer.parseInt(page);
					selectCompany=com;
					selectDate=lblDate.getText();
					logger.info("select date:{}",selectDate);
					
					try{
						if(cbxAgent.isSelected())
						{
							String agent = tableService.getTableAgentByPage(selectedPage);
							if(inD_flag==1)
							{
								CreateXTGCommand command = new CreateXTGCommand(com,agent,lblDate.getText(),false,"",1);
								command.execute();
								dataF.setText( command.result);
								// inD_flag=1;
							}
							else
							{
								CreateXTGCommand command = new CreateXTGCommand(com,agent,lblDate.getText(),false,"",2);
								command.execute();
								dataF.setText( command.result);
								//	inD_flag=1;
							}						

							//logger.debug("command:"+command.result);
						}else
						{
							if(inD_flag==1)
							{
								CreateXTGCommand command = new CreateXTGCommand(com,selectedPage,lblDate.getText(),false,"",1);
								command.execute();
								//logger.debug("command:"+command.result);
								dataF.setText( command.result);
								// inD_flag=1;


							}
							else
							{	
								CreateXTGCommand command = new CreateXTGCommand(com,selectedPage,lblDate.getText(),false,"",2);
								command.execute();
								//logger.debug("command:"+command.result);
								dataF.setText( command.result);
								// inD_flag=1;

							}
						}
						
					}catch (Exception e) {
						e.printStackTrace();
					}

					manager.selectedCompany=com;
					manager.selectedPage=Integer.parseInt(page);
					break;
				default:
					break;
				}

			}});
		_pnADVTableList.updateUI();


		return tree2;
	}
	private KSGPanel createLeftMenuPn2() {
		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new BorderLayout());
		pnMain.setPreferredSize(new Dimension(500,0));
		tblTableList = new JTable();
		tblTableList.addMouseListener(new MouseAdapter(){

			public void mouseClicked(MouseEvent e) 
			{
				if(e.getClickCount()>=2)
				{
					JTable table=(JTable) e.getSource();
					int row=table.getSelectedRow();
					String company_abbr=(String) table.getValueAt(row, 0);
					String date=(String) table.getValueAt(row, 2);
					try {
						selectCompany=company_abbr;
						selectDate=date;
						updateSubTableList(company_abbr,date);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		tblSubTableList = new JTable();
		KSGPanel pnSubMain = new KSGPanel();
		pnSubMain.setLayout(new GridLayout(1,0));
		KSGPanel pnTableList = new KSGPanel();
		pnTableList.setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(tblTableList);
		pnTableList.add(scrollPane,BorderLayout.CENTER);
		pnTableList.add(new JLabel("입력정보"),BorderLayout.NORTH);	


		KSGPanel pnSub1 =new KSGPanel();
		pnSub1.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnSub1.add(new JLabel("날짜 입력"));

		pnTableList.add(pnSub1,BorderLayout.SOUTH);


		KSGPanel pnSubTableList = new KSGPanel();
		pnSubTableList.setLayout(new BorderLayout());
		JScrollPane scrollPane1 = new JScrollPane(tblSubTableList);
		pnSubTableList.add(scrollPane1,BorderLayout.CENTER);
		pnSubTableList.add(new JLabel("테이블정보"),BorderLayout.NORTH);
		KSGPanel pnSubTableListControl = new KSGPanel();
		pnSubTableListControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton button = new JButton("출력하기",new ImageIcon("images/textOutput.gif"));

		button.setToolTipText("출력하기");

		button.addActionListener(new ActionListener(){


			public void actionPerformed(ActionEvent e) {
				dataF.setText("");

			}});
		pnSubTableListControl.add(button);
		pnSubTableList.add(pnSubTableListControl,BorderLayout.SOUTH);

		pnSubMain.add(pnTableList);
		pnSubMain.add(pnSubTableList);


		pnMain.add(pnSubMain,BorderLayout.CENTER);

		return pnMain;
	}


	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("출력하기"))
		{
			System.out.println("출력하기 버튼");
			CreateXTGCommand commands = new CreateXTGCommand(selectCompany,selectedPage,selectDate,true,dataF.getText(),1);
			commands.execute();
		}
		else 
		{
			if(command.equals("인디자인출력"))
			{
				System.out.println("인디자인 출력하기 버튼");
				CreateXTGCommand commands = new CreateXTGCommand(selectCompany,selectedPage,selectDate,true,dataF.getText(),2);
				commands.execute();
			}
			else 
			{
				if(command.equals("선사선택"));
			}
		}
	}


	public void update(KSGModelManager manager) {
		this.viewUpdate();

	}
	
	XTGPage page;
	
	XTGManager xtgmanager = new XTGManager();
	
	private JLabel lblCompany;
	
	private JLabel lblMessage;
	
	private int selectedPage;
	
	private JTextField txfDate;
	
	private JCheckBox cbxAgent;
	
}

