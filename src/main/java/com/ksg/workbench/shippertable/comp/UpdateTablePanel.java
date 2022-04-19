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
package com.ksg.workbench.shippertable.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.Company;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.service.ADVService;
import com.ksg.service.BaseService;
import com.ksg.service.TableService;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.shippertable.ShipperTableAbstractMgtUI;
import com.ksg.workbench.shippertable.ShipperTableMgtUI;
import com.ksg.workbench.shippertable.dialog.UpdateTableInOutDialog;

/**
 * @author ��â��
 * @���� ���̺� ���� ���� �г�
 *
 */
public class UpdateTablePanel extends KSGPanel implements ActionListener,FocusListener{


	class EnterKeyListener extends KeyAdapter
	{
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				try 
				{
					int result = saveAction();
					if(result==1)
					{
						searchUI.searchByOption();
						searchUI.fnUpdate();
						lblSaveInfo.setText("���� �Ǿ����ϴ�.");

					}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		}		
	}
	class EnterKeyListener2 extends KeyAdapter
	{		
		JTextField field;
		public EnterKeyListener2(JTextField txf) {
			field = txf;
		}

		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				StringTokenizer stringTokenizer = new StringTokenizer(field.getText(),"#");
				if(stringTokenizer.countTokens()>0)
				{

					while(stringTokenizer.hasMoreTokens())
					{
						try
						{
							int in = Integer.parseInt(stringTokenizer.nextToken());

							// �����ϴ� �ױ����� Ȯ��
							TablePort port = new TablePort();
							port.setTable_id(txfTable_id.getText());
							port.setPort_index(in);
							try {
								TablePort searchPort=tableService.getTablePort(port);

								PortInfo info=baseService.getPortInfoByPortName(searchPort.getPort_name());
								if(info==null)
								{
									field.setText("");
									JOptionPane.showMessageDialog(null, "�ε��� "+in+"("+searchPort.getPort_name()+")�� �������� �ʽ��ϴ�.");
									return;

								}

							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}catch(NumberFormatException nume)
						{
							System.err.println(nume.getMessage());
						}
					}
				}
			}
		}
	}
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	private static final long serialVersionUID = 1L;
	
	private ADVService 	advService;
	private TableService tableService;
	private KSGModelManager modelManager = KSGModelManager.getInstance();
	private JTextField 	txfTable_id;
	private JTextField 	txfAgent;
	private JTextField 	txfOutPort;
	private JTextField 	txfOutToPort;
	private JTextField 	txfInPort;
	private JTextField 	txfInToPort;
	private JTextField 	txfGubun;
	private JTextArea	txaCommon;
	private JTextArea 	txaQuark;
	private JTextField 	txfTitle;
	private JTextField 	txfPage;
	private JTextField 	txfBookPage;
	private JTextField 	txfCompany_Abbr;
	
	private JTextField 	txfIndex;
	private JTextField  txfPortCount;
	private JTextField  txfVesselCount;
	private JTextField 	txfConsolePage;
	private ShippersTable shippersTable;
	private JTextField txfOhterCount;


	private ShipperTableAbstractMgtUI searchUI;

	private JLabel lblSaveInfo;
	private EnterKeyListener enterKeyListener;
	private BaseService baseService;

	private JComboBox cbxGubun;
	private JTextArea txaConsoleCFS;
	private JTextField txfDtime;
	private JTextField txfInland;
	private JTextField txfCtime;
	private KSGPanel pnClosingTime;
	private JRadioButton optPage;
	private JRadioButton optCFS;
	private JTabbedPane tabPaneInfo;
	private KSGPanel pnInland;
	public UpdateTablePanel() {
		enterKeyListener = new EnterKeyListener();
		this.advService = DAOManager.getInstance().createADVService();
		this.tableService= new TableServiceImpl();
		this.baseService = DAOManager.getInstance().createBaseService();
		createAndUpdteUI();
	}
	public UpdateTablePanel(ShippersTable table) {
		this();
		if(table!=null)
			this.setShipperTableData(table);
	}

	
	public UpdateTablePanel(ShipperTableAbstractMgtUI searchUI, ShippersTable table) {
		this();
		this.searchUI=searchUI;
		if(table!=null)
			this.setShipperTableData(table);
	}
	private void createAndUpdteUI()
	{
		this.setLayout( new BorderLayout());		

		this.txfTable_id		= new JTextField(15);	// ���̺� ���̵�
		this.txfCompany_Abbr 	= new JTextField(15);	// ���� ���
		this.txfAgent 			= new JTextField(15);	// ������Ʈ
		
		this.txfPage 			= new JTextField(3);	// ������
		this.txfBookPage 		= new JTextField(3);	// ���� ������
		this.txfIndex			= new JTextField(2);	// �ε���
		
		this.txfOutPort 		= new JTextField(15);	// ������ �ε���
		this.txfOutToPort		= new JTextField(15);
		this.txfInPort 			= new JTextField(15);	// ����
		this.txfGubun 			= new JTextField(3);
		this.txfInToPort 		= new JTextField(15);
		this.txfTitle 			= new JTextField(15);

		this.txfPortCount 		= new JTextField(2);
		this.txfVesselCount 	= new JTextField(2);
		this.txfOhterCount 		= new JTextField(2);

		this.txfDtime 			= new JTextField(2);
		this.txfCtime 			= new JTextField(2);
		this.txfInland			= new JTextField(2);
		this.txaCommon 			= new JTextArea();

		this.txaConsoleCFS 		= new JTextArea(0,15);
		this.txaQuark 			= new JTextArea(0,15);
		this.txfConsolePage 	= new JTextField(15);

		this.txfTable_id.setEditable(false);
		txfCompany_Abbr.setEditable(false);
		txfAgent.setEditable(false);
		
		this.txfTable_id.setHorizontalAlignment(JTextField.RIGHT);		
		this.txfPage.setHorizontalAlignment(JTextField.RIGHT);
		this.txfBookPage.setHorizontalAlignment(JTextField.RIGHT);
		this.txfIndex.setHorizontalAlignment(JTextField.RIGHT);
		
		this.txfPortCount.setHorizontalAlignment(JTextField.RIGHT);
		txfVesselCount.setHorizontalAlignment(JTextField.RIGHT);
		txfOhterCount.setHorizontalAlignment(JTextField.RIGHT);
		
		
/*		txfCompany_Abbr.addKeyListener(enterKeyListener);
		txfAgent.addKeyListener(enterKeyListener);
		txfOperator.addKeyListener(enterKeyListener);*/
		
		txfPage.addKeyListener(enterKeyListener);
		txfBookPage.addKeyListener(enterKeyListener);
		txfIndex.addKeyListener(enterKeyListener);
		
		txfOutToPort.addKeyListener(enterKeyListener);
		txfInToPort.addKeyListener(enterKeyListener);
		txfGubun.addKeyListener(enterKeyListener);
		txfTitle.addKeyListener(enterKeyListener);
				
		txaCommon.addKeyListener(enterKeyListener);
		txfPortCount.addKeyListener(enterKeyListener);
		txfVesselCount.addKeyListener(enterKeyListener);		
		txfOhterCount.addKeyListener(enterKeyListener);

		txfOutPort.addKeyListener(new EnterKeyListener2(txfOutPort));
		txfInPort.addKeyListener(new EnterKeyListener2(txfInPort));		

		txfCompany_Abbr.addFocusListener(this);
		txfAgent.addFocusListener(this);
		
		
		txfPage.addFocusListener(this);		
		txfBookPage.addFocusListener(this);
		txfIndex.addFocusListener(this);
		
		txfOutPort.addFocusListener(this);
		txfOutToPort.addFocusListener(this);		
		txfInPort.addFocusListener(this);		
		txfInToPort.addFocusListener(this);
		txfGubun.addFocusListener(this);
		txfTitle.addFocusListener(this);
		
		txaCommon.addFocusListener(this);
		txfPortCount.addFocusListener(this);
		txfVesselCount.addFocusListener(this);
		txfOhterCount.addFocusListener(this);	

		cbxGubun = new JComboBox();
		cbxGubun.addItem(ShippersTable.GUBUN_NORMAL);
		cbxGubun.addItem(ShippersTable.GUBUN_CONSOLE);
		cbxGubun.addItem(ShippersTable.GUBUN_INLAND);
		cbxGubun.addItem(ShippersTable.GUBUN_NNN);
		cbxGubun.addItem(ShippersTable.GUBUN_TS);

		cbxGubun.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg) {
				JComboBox cbx= (JComboBox) arg.getSource();
				String command=(String) cbx.getSelectedItem();


				if(command.equals(ShippersTable.GUBUN_CONSOLE))
				{
					pnClosingTime.setVisible(true);
				}
				else
				{
					pnClosingTime.setVisible(false);
				}

				if(command.equals(ShippersTable.GUBUN_INLAND))
				{
					pnInland.setVisible(true);

				}
				else
				{
					pnInland.setVisible(false);
				}


			}
		});


		Box pnMain = new Box(BoxLayout.Y_AXIS);

		KSGPanel pnTable = new KSGPanel();
		GridLayout gridLayout = new GridLayout(0,1);
		gridLayout.setVgap(10);
		pnTable.setLayout(gridLayout);
		TitledBorder boderTable = BorderFactory.createTitledBorder("���̺� ����");
		pnTable.setBorder(boderTable);
		
		
		KSGPanel pnCompanySearch = new KSGPanel(new BorderLayout());
		pnCompanySearch.add(txfCompany_Abbr);
		JButton button = new JButton("�˻�");
		pnCompanySearch.add(button,BorderLayout.EAST);
		
		
		button.addActionListener(new ActionListener(){

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				final JDialog dialog = new JDialog();

				try {
					List li=advService.getCompanyList();

					DefaultMutableTreeNode root = new DefaultMutableTreeNode("��ü����:"+li.size());
					Iterator iter =li.iterator();
					while(iter.hasNext())
					{
						Company company = (Company) iter.next();
						DefaultMutableTreeNode sub = new DefaultMutableTreeNode(company.getCompany_abbr());
						root.add(sub);						
					}

					dialog.setTitle("Company Selection");
					KSGPanel pnMain = new KSGPanel();
					pnMain.setLayout( new BorderLayout());
					final JTree tree = new JTree(root);
					tree.addMouseListener(new MouseAdapter() {

						public void mouseClicked(MouseEvent arg0) {
							if(arg0.getClickCount()>1)
							{
								TreePath path=tree.getSelectionPath();
								if(path.getPathCount()!=1)
								{
									String company=path.getLastPathComponent().toString();
									//setTableIndex(company);										
									txfCompany_Abbr.setText(company);
									

									dialog.setVisible(false);
									dialog.dispose();
								}
							}

						}
					});
					tree.addTreeSelectionListener(new TreeSelectionListener(){

						public void valueChanged(TreeSelectionEvent e) {
							TreePath path=e.getNewLeadSelectionPath();

							if(path.getPathCount()!=1)
								System.out.println(path.getLastPathComponent());	

						}});


					pnMain.add(new JScrollPane(tree),BorderLayout.CENTER);
					KSGPanel pnSubPnControl = new KSGPanel();
					pnSubPnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
					JButton butOK = new JButton("OK");

					butOK.addActionListener(new ActionListener(){

						public void actionPerformed(ActionEvent e) 
						{
							TreePath path=tree.getSelectionPath();
							if(path.getPathCount()!=1)
							{

								String company=path.getLastPathComponent().toString();
								try {
									
									Company companyInfo=baseService.getCompanyInfo(company);									
									txfCompany_Abbr.setText(companyInfo.getCompany_abbr());
									txfAgent.setText(companyInfo.getAgent_abbr());
									
									
								} catch (SQLException e1) {
									JOptionPane.showMessageDialog(null, "error:"+e1.getMessage());
									e1.printStackTrace();
								}
							}

							dialog.setVisible(false);
							dialog.dispose();							
						}});
					butOK.setPreferredSize(new Dimension(80,28));
					pnSubPnControl.add(butOK);
					JButton butCancel = new JButton("Cancel");

					butCancel.addActionListener(new ActionListener(){

						public void actionPerformed(ActionEvent e) {
							dialog.setVisible(false);
							dialog.dispose();

						}});
					pnSubPnControl.add(butCancel);
					butCancel.setPreferredSize(new Dimension(80,28));
					KSGPanel pnTitleInfo = new KSGPanel();
					pnTitleInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
					pnTitleInfo.add(new JLabel("Chose the Company"));
					pnMain.add(pnTitleInfo,BorderLayout.NORTH);
					pnMain.add(pnSubPnControl,BorderLayout.SOUTH);
					dialog.add(pnMain);					
					dialog.setSize(400, 400);
					ViewUtil.center(dialog, false);
					dialog.setVisible(true);

				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}});
		
		
		pnTable.add(createForm("���̺� ID: ", txfTable_id));
		pnTable.add(createForm("���̺� ����: ", cbxGubun));
		pnTable.add(createForm("����� ���: ", pnCompanySearch));
		pnTable.add(createForm("������Ʈ ���: ", 	txfAgent));		
		pnTable.add(createForm("����: ", 	txfTitle));


		Box pnPage = new Box(BoxLayout.X_AXIS);
		pnPage.add(createForm(" ������: ", txfPage,50));
		pnPage.add(createForm(" ����������: ", txfBookPage,75));
		pnPage.add(createForm(" �ε���: ", txfIndex,50));


		pnTable.add(pnPage);
		Box pnPage2 = new Box(BoxLayout.X_AXIS);
		pnPage2.add(createForm("�ױ���: ", txfPortCount,80));		
		pnPage2.add(createForm("���ڼ�: ", txfVesselCount,80));
		pnPage2.add(createForm("��Ÿ: ", txfOhterCount,80));
		pnTable.add(pnPage2);

		pnMain.add(pnTable);

		Box pnBound = Box.createVerticalBox();

		KSGPanel pnOutBound = createPnOutBound(gridLayout);

		KSGPanel pnInBound = createPnInBound(gridLayout);

		pnBound.add( pnOutBound);
		pnBound.add( pnInBound);

		pnMain.add(pnBound);

		pnClosingTime = new KSGPanel(new BorderLayout());		
		TitledBorder boderOther = BorderFactory.createTitledBorder("Closing Time Index");
		pnClosingTime.setBorder(boderOther);


		Box bxClosingTime = new Box(BoxLayout.X_AXIS);
		bxClosingTime.add(createForm("Document Closing: ", txfDtime,120));
		bxClosingTime.add(createForm(" Cargo Closing: ", txfCtime,100));
		pnClosingTime.add(bxClosingTime);



		pnInland = new KSGPanel(new BorderLayout());		
		TitledBorder boderInland = BorderFactory.createTitledBorder("���� ������ ����");
		pnInland.setBorder(boderInland);


		Box bxInland = new Box(BoxLayout.X_AXIS);
		bxInland.add(createForm("������: ", txfInland));

		pnInland.add(bxInland);

		tabPaneInfo = new JTabbedPane();
		KSGPanel pnS1 = new KSGPanel();

		pnS1.setLayout(new BorderLayout());
		KSGPanel pnS2 = new KSGPanel();	
		pnS2.setLayout(new BorderLayout());


		pnS1.add(new JScrollPane(txaCommon),BorderLayout.CENTER);
		pnS2.add(new JScrollPane(txaQuark),BorderLayout.CENTER);

		KSGPanel pnConsole = buildConsolePn();

		tabPaneInfo.addTab("�����輱", pnS1);
		tabPaneInfo.addTab("Format", pnS2);
		tabPaneInfo.addTab("Console ����", pnConsole);

		pnMain.add(pnClosingTime);
		pnMain.add(pnInland);
		pnMain.add(tabPaneInfo);
		pnClosingTime.setVisible(false);
		pnInland.setVisible(false);
		txfTable_id.setEditable(false);
		Box pnMain1 = new Box(BoxLayout.Y_AXIS);		
		pnMain1.add(pnMain);
		
		
		
		
		
		this.add(pnMain,BorderLayout.NORTH);
		this.add(tabPaneInfo,BorderLayout.CENTER);
		this.add(buildButtom(),BorderLayout.SOUTH);

	}
	/**@ �ܼ� ���� ���� ȭ��
	 * @return
	 */
	private KSGPanel buildConsolePn() {

		optPage = new JRadioButton("Page",true);
		optCFS = new JRadioButton("CFS");
		ButtonGroup bgConsole = new ButtonGroup();
		bgConsole.add(optPage);
		bgConsole.add(optCFS);

		KSGPanel pnConsole = new KSGPanel();	
		pnConsole.setLayout(new BorderLayout());

		KSGPanel pnConsolePage = new KSGPanel(new BorderLayout());
		pnConsolePage.add(new JLabel("Page ����"),BorderLayout.NORTH);
		pnConsolePage.add(txfConsolePage);

		KSGPanel pnConsoleCFS = new KSGPanel(new BorderLayout());
		pnConsoleCFS.add(new JLabel("CFS ����"), BorderLayout.NORTH);
		pnConsoleCFS.add(new JScrollPane(txaConsoleCFS));


		KSGPanel pnConsolePrintType = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		pnConsolePrintType.add(new JLabel("�ܼ� ���� ���� �� ���� ��ư�� ���� �ֽʽÿ�"));

		pnConsole.add(pnConsoleCFS);
		pnConsole.add(pnConsolePage,BorderLayout.NORTH);
		pnConsole.add(pnConsolePrintType,BorderLayout.SOUTH);
		return pnConsole;
	}
	private KSGPanel createPnInBound(GridLayout gridLayout) {
		KSGPanel pnInBound = new KSGPanel();
		pnInBound.setLayout(gridLayout);
		pnInBound.add(createForm("������ : ", txfInPort));
		pnInBound.add(createForm("�ܱ��� : ", txfInToPort));
		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("������ ���");
		pnInBound.setBorder(createTitledBorder);

		KSGPanel pnInBoundSub = new KSGPanel();
		pnInBoundSub.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butUpdateInbound = new JButton("���/����");
		butUpdateInbound.addActionListener(this);

		return pnInBound;
	}
	private KSGPanel createPnOutBound(GridLayout gridLayout) {
		KSGPanel pnOutBound = new KSGPanel();
		pnOutBound.setLayout(gridLayout);

		TitledBorder createTitledBorder = BorderFactory.createTitledBorder("������ ���");
		pnOutBound.setBorder(createTitledBorder);


		pnOutBound.add(createForm("������: ", txfOutPort));
		pnOutBound.add(createForm("�ܱ���: ", txfOutToPort));
		KSGPanel pnOutboundSub = new KSGPanel();
		pnOutboundSub.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butUpdate = new JButton("���/����");
		butUpdate.addActionListener(this);
		return pnOutBound;
	}
	private int saveAction() throws SQLException,NumberFormatException
	{
		logger.debug("save table info");
		ShippersTable table = new ShippersTable();

		table.setTable_id(txfTable_id.getText());
		
		table.setQuark_format(txaQuark.getText());
		
		table.setCompany_abbr(txfCompany_Abbr.getText());
		table.setAgent(txfAgent.getText());
		
		
		table.setPage(Integer.parseInt(txfPage.getText()));
		table.setBookPage(txfBookPage.getText());
		table.setTable_index(Integer.parseInt(txfIndex.getText()));
		
		table.setPort_col(Integer.parseInt(txfPortCount.getText()));
		table.setVsl_row(Integer.parseInt(txfVesselCount.getText()));
		table.setTitle(txfTitle.getText());			
		table.setOut_port(txfOutPort.getText());
		table.setIn_port(txfInPort.getText());
		table.setOut_to_port(txfOutToPort.getText());
		table.setIn_to_port(txfInToPort.getText());			
		table.setGubun((String)cbxGubun.getSelectedItem());
		table.setOthercell(Integer.parseInt(txfOhterCount.getText()));
		table.setConsole_cfs(txaConsoleCFS.getText());
		table.setConsole_page(txfConsolePage.getText());

		table.setD_time(Integer.parseInt(txfDtime.getText()));
		table.setC_time(Integer.parseInt(txfCtime.getText()));
		table.setCommon_shipping(txaCommon.getText());
		table.setInland_indexs(txfInland.getText());


		int result=tableService.update(table);
		logger.debug("save result:"+result);
		return result;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("�����ϱ�"))
		{
			try{

				int result=saveAction();

				if(result==1)
				{
					JOptionPane.showMessageDialog(UpdateTablePanel.this, "�ݿ� �Ǿ����ϴ�.");
					searchUI.updateSubTable();

				}
			}
			catch(NumberFormatException ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(UpdateTablePanel.this, "�Է������� �߸� �Ǿ����ϴ�.");
			}
			catch (SQLException e1) 
			{
				e1.printStackTrace();
				JOptionPane.showMessageDialog(UpdateTablePanel.this, e1.getMessage());
			}
		}
		else if(command.equals("���/����"))
		{
			if(shippersTable==null)
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "���õ� ���̺��� �����ϴ�.");
				return;
			}

			UpdateTableInOutDialog dialog = new UpdateTableInOutDialog(shippersTable);
			dialog.createAndUpdateUI();

			txfInPort.setText(dialog.inPortIndex);
			if(dialog.inPortIndex!=null)
				if(dialog.outPortIndex!=null)
					txfOutPort.setText(dialog.outPortIndex);
			if(dialog.inToPortIndex!=null)
				txfInToPort.setText(dialog.inToPortIndex);
			if(dialog.outToPortIndex!=null)
				txfOutToPort.setText(dialog.outToPortIndex);
		}

	}
	private KSGPanel createForm(String label,Component comp)
	{
		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new BorderLayout());
		JLabel label2 = new JLabel(label,JLabel.RIGHT);
		label2.setPreferredSize(new Dimension(90,20));
		pnMain.add(label2,BorderLayout.WEST);
		KSGPanel pnComp = new KSGPanel();
		pnComp.setLayout(new BorderLayout());
		pnComp.add(comp);
		pnMain.add(pnComp,BorderLayout.CENTER);
		return pnMain; 

	}
	private KSGPanel createForm(String label,Component comp,int size)
	{
		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new BorderLayout());
		JLabel lblTitle = new JLabel(label,JLabel.RIGHT);
		lblTitle.setPreferredSize(new Dimension(size,20));
		pnMain.add(lblTitle,BorderLayout.WEST);
		KSGPanel pnComp = new KSGPanel();
		pnComp.setLayout(new BorderLayout());
		pnComp.add(comp);
		pnMain.add(pnComp,BorderLayout.CENTER);
		return pnMain; 

	}

	public Component buildButtom()
	{
		KSGPanel pnButtom = new KSGPanel();
		pnButtom.setLayout(new FlowLayout(FlowLayout.RIGHT));


		JButton btnNext = new JButton("�����ϱ�");
		btnNext.setPreferredSize(new Dimension(90,23));
		btnNext.addActionListener(this);
		lblSaveInfo = new JLabel();  
		pnButtom.add(lblSaveInfo);
		pnButtom.add(btnNext);

		return pnButtom;
	}

	public void setShipperTableData(ShippersTable tableData) {


		try {
			shippersTable = tableService.getTableById(tableData.getTable_id());
			txfTable_id.setText(shippersTable.getTable_id());
			txaQuark.setText(shippersTable.getQuark_format());
			txfAgent.setText(shippersTable.getAgent());
			txfGubun.setText(shippersTable.getGubun());
			txfTitle.setText(shippersTable.getTitle());

			TablePort tablePort = new TablePort();
			tablePort.setTable_id(shippersTable.getTable_id());
			List li=tableService.getTablePortList(tablePort);


			txfPortCount.setText(String.valueOf(shippersTable.getPort_col()));

			if(li.size()!=shippersTable.getPort_col())
			{
				txfPortCount.setForeground(Color.red);
				
			}else
			{
				txfPortCount.setForeground(Color.black);
			}

			txfVesselCount.setText(String.valueOf(shippersTable.getVsl_row()));
			txfCompany_Abbr.setText(shippersTable.getCompany_abbr());
			
			
			txfPage.setText(String.valueOf(shippersTable.getPage()));
			txfBookPage.setText(shippersTable.getBookPage());
			txfIndex.setText(String.valueOf(shippersTable.getTable_index()));
			
			
			txaCommon.setText(shippersTable.getCommon_shipping());
			txfInPort.setText(shippersTable.getIn_port());
			txfInToPort.setText(shippersTable.getIn_to_port());
			txfOutPort.setText(shippersTable.getOut_port());
			txfOutToPort.setText(shippersTable.getOut_to_port());
			txfOhterCount.setText(String.valueOf(shippersTable.getOthercell()));
			txfCtime.setText(String.valueOf(shippersTable.getC_time()));
			txfDtime.setText(String.valueOf(shippersTable.getD_time()));
			txfInland.setText(shippersTable.getInland_indexs());
			cbxGubun.setSelectedItem(shippersTable.getGubun());

			if(shippersTable.getGubun().equals(ShippersTable.GUBUN_CONSOLE))
			{
				tabPaneInfo.setSelectedIndex(2);// �ܼ� �� ����	
			}else
			{
				tabPaneInfo.setSelectedIndex(0);//���� �輱 �� ����
			}


			txaConsoleCFS.setText(shippersTable.getConsole_cfs());
			txfConsolePage.setText(shippersTable.getConsole_page());



		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}
	
	public void setShipperTableData(HashMap<String, Object> param) {
		
		txfTable_id.setText(String.valueOf(param.get("table_id")));
		txaQuark.setText(String.valueOf(param.get("quark_format")));
		txfAgent.setText(String.valueOf(param.get("agent")));
		txfGubun.setText(String.valueOf(param.get("gubun")));
		txfTitle.setText(String.valueOf(param.get("title")));
		txfPortCount.setText(String.valueOf(param.get("port_col")));
		txfVesselCount.setText(String.valueOf(param.get("vsl_row")));
		txfCompany_Abbr.setText(String.valueOf(param.get("company_abbr")));	
		
		txfPage.setText(String.valueOf(param.get("page")));
		txfBookPage.setText(String.valueOf(param.get("bookPage")));
		txfIndex.setText(String.valueOf(param.get("index")));
		txaConsoleCFS.setText(String.valueOf(param.get("cfs")));
		txfConsolePage.setText(String.valueOf(param.get("console_page")));
		
		
	}
	
	
	public void setShipperTableData(String table_id) {


		try {
			shippersTable = tableService.getTableById(table_id);
			txfTable_id.setText(shippersTable.getTable_id());
			txaQuark.setText(shippersTable.getQuark_format());
			txfAgent.setText(shippersTable.getAgent());
			txfGubun.setText(shippersTable.getGubun());
			txfTitle.setText(shippersTable.getTitle());

			TablePort tablePort = new TablePort();
			tablePort.setTable_id(shippersTable.getTable_id());
			List li=tableService.getTablePortList(tablePort);


			txfPortCount.setText(String.valueOf(shippersTable.getPort_col()));

			if(li.size()!=shippersTable.getPort_col())
			{
				txfPortCount.setForeground(Color.red);
				
			}else
			{
				txfPortCount.setForeground(Color.black);
			}

			txfVesselCount.setText(String.valueOf(shippersTable.getVsl_row()));
			txfCompany_Abbr.setText(shippersTable.getCompany_abbr());
			
			
			txfPage.setText(String.valueOf(shippersTable.getPage()));
			txfBookPage.setText(shippersTable.getBookPage());
			txfIndex.setText(String.valueOf(shippersTable.getTable_index()));
			
			
			txaCommon.setText(shippersTable.getCommon_shipping());
			txfInPort.setText(shippersTable.getIn_port());
			txfInToPort.setText(shippersTable.getIn_to_port());
			txfOutPort.setText(shippersTable.getOut_port());
			txfOutToPort.setText(shippersTable.getOut_to_port());
			txfOhterCount.setText(String.valueOf(shippersTable.getOthercell()));
			txfCtime.setText(String.valueOf(shippersTable.getC_time()));
			txfDtime.setText(String.valueOf(shippersTable.getD_time()));
			txfInland.setText(shippersTable.getInland_indexs());
			cbxGubun.setSelectedItem(shippersTable.getGubun());

			if(shippersTable.getGubun().equals(ShippersTable.GUBUN_CONSOLE))
			{
				tabPaneInfo.setSelectedIndex(2);// �ܼ� �� ����	
			}else
			{
				tabPaneInfo.setSelectedIndex(0);//���� �輱 �� ����
			}


			txaConsoleCFS.setText(shippersTable.getConsole_cfs());
			txfConsolePage.setText(shippersTable.getConsole_page());



		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}
	public void focusGained(FocusEvent e) {
		Object f =e.getSource();

		lblSaveInfo.setText("");
		if(f instanceof JTextField)
		{
			((JTextField) f).selectAll();
			if(f.equals(txfOutToPort))
			{
				updateToPortIndex(txfOutPort,txfOutToPort);
			}
			if(f.equals(txfInToPort))
			{
				updateToPortIndex(txfInPort,txfInToPort);	
			}


		}else if (f instanceof JTextArea)
		{
			((JTextArea) f).selectAll();
		}

	}
	private void updateToPortIndex(JTextField txfOutPort,JTextField txfOutToPort) {


		StringTokenizer stringTokenizer = new StringTokenizer(txfOutPort.getText(),"#");
		if(stringTokenizer.countTokens()>0)
		{
			String portCount=txfPortCount.getText();
			Vector intCount = new Vector();
			while(stringTokenizer.hasMoreTokens())
			{
				try
				{
					int in = Integer.parseInt(stringTokenizer.nextToken());

					// �����ϴ� �ױ����� Ȯ��
					TablePort port = new TablePort();
					port.setTable_id(txfTable_id.getText());
					port.setPort_index(in);

					intCount.add(in);
				}catch(NumberFormatException nume)
				{
					System.err.println(nume.getMessage());
				}
			}

			int pCount = Integer.parseInt(portCount);

			pCount+=Integer.parseInt(txfOhterCount.getText());



			String dd="";
			for(int i=1;i<pCount+1;i++)
			{
				boolean flag=true;
				for(int j=0;j<intCount.size();j++)
				{
					int a = (Integer)intCount.get(j);
					if(a==i)
					{
						flag=false;
					}
				}

				if(flag)
				{

					// �����ϴ� �ױ����� Ȯ��
					TablePort port = new TablePort();
					port.setTable_id(txfTable_id.getText());
					port.setPort_index(i);
					try {
						TablePort searchPort=tableService.getTablePort(port);
						System.out.println(searchPort);
						try{

							if(searchPort!=null)
							{
								PortInfo info=baseService.getPortInfoByPortName(searchPort.getPort_name());
								if(info!=null)
								{
									dd+=String.valueOf(i);

									if(i<pCount)
									{
										dd+="#";
									}
								}
							}else
							{
								dd+=String.valueOf(i);

								if(i<pCount)
								{
									dd+="#";
								}
							}


						}catch(NullPointerException e)
						{
							e.printStackTrace();
						}

					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						System.err.println(e1.getErrorCode());
						if(e1.getErrorCode()==0)
						{
							dd+=String.valueOf(i);

							if(i<pCount)
							{
								dd+="#";
							}
						}
					}

				}

			}
			txfOutToPort.setText(dd);
		}


	}
	public void focusLost(FocusEvent e) {
		lblSaveInfo.setText("");
		Object f =e.getSource();

		lblSaveInfo.setText("");
		if(f.equals( txfOutPort))
		{
			updatePortIndexWhenFocusLost(txfOutPort,txfOutToPort);
		}
		if(f.equals( txfInPort))
		{
			updatePortIndexWhenFocusLost(txfInPort,txfInToPort);
		}
	}
	private void updatePortIndexWhenFocusLost(JTextField txfOutPort,JTextField txfOutToPort) {
		StringTokenizer stringTokenizer = new StringTokenizer(txfOutPort.getText(),"#");
		if(stringTokenizer.countTokens()>0)
		{
			String portCount=txfPortCount.getText();
			String oriOutPort = txfOutPort.getText();
			while(stringTokenizer.hasMoreTokens())
			{
				try
				{
					int in = Integer.parseInt(stringTokenizer.nextToken());

					// �����ϴ� �ױ����� Ȯ��
					TablePort port = new TablePort();
					port.setTable_id(txfTable_id.getText());
					port.setPort_index(in);
					try {
						TablePort searchPort=tableService.getTablePort(port);


						if(searchPort==null)
						{
							JOptionPane.showMessageDialog(null, "�ε��� "+in+"�� ��ϵǾ� ���� �ʽ���");
							return;
						}
						PortInfo info=baseService.getPortInfoByPortName(searchPort.getPort_name());
						if(info==null)
						{
							txfOutPort.setText("");
							txfOutToPort.setText("");
							JOptionPane.showMessageDialog(null, "�ε��� "+in+"("+searchPort.getPort_name()+")�� �������� �ʽ��ϴ�.");
							return;
						}

					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}catch(NumberFormatException nume)
				{
					System.err.println(nume.getMessage());
				}
			}
		}
	}
	public void setPortCount(int count) {
		txfPortCount.setText(String.valueOf(count));
	}

}

