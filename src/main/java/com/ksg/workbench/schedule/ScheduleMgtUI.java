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
package com.ksg.workbench.schedule;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.ksg.commands.schedule.SortAllCommand;
import com.ksg.commands.schedule.SortInlandCommnad;
import com.ksg.commands.schedule.route.RouteTaskNewVessel;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ScheduleType;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.ScheduleServiceManager;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.service.ScheduleService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.KSGCheckBox;
import com.ksg.view.comp.KSGRadioButton;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.ui.ErrorLogManager;
import com.ksg.workbench.common.comp.AbstractMgtUI;
import com.ksg.workbench.schedule.comp.PnConsole;
import com.ksg.workbench.schedule.comp.PnConsole2;
import com.ksg.workbench.schedule.comp.PnInbound;
import com.ksg.workbench.schedule.comp.PnInland;
import com.ksg.workbench.schedule.comp.PnInland2;
import com.ksg.workbench.schedule.comp.PnNormal;
import com.ksg.workbench.schedule.comp.PnNormal2;
import com.ksg.workbench.schedule.comp.PnNormalByTree;
import com.ksg.workbench.schedule.comp.PnOutbound;
import com.ksg.workbench.schedule.dialog.ScheduleResultDialog;

/**
 * ������ ���� ��ȸ
 * @author ��â��
 *
 */
/**

  * @FileName : ScheduleMgtUI.java

  * @Date : 2021. 4. 29. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� :

  */
public class ScheduleMgtUI extends AbstractMgtUI implements ActionListener, ComponentListener {


	private static final String ACTION_CREATE = "������ ����";

	private static final String ACTION_DELETE = "������ ����";

	ErrorLogManager errorLogManager = ErrorLogManager.getInstance();
	
	private ScheduleServiceManager serviceManager =ScheduleServiceManager.getInstance();
	
	private static final long serialVersionUID = 1L;	

	private KSGAbstractTable tblScheduleDateList;
	
	private JLabel lblNTop;
	
	private ScheduleResultDialog scheduleResultDialog;
	
	private ScheduleService scheduleService;
	
	private JDialog searchScheduleByCompanyDialog;	
	
	private JComboBox cbxOption;

	private JTextField txfOption;
	
	private JButton butDelete,butSort;
	
	private List tableDatelist;
	
	private JButton butBuild;
	
	SimpleDateFormat fromDateformat = new SimpleDateFormat("yy-mm-ss");
	
	SimpleDateFormat toDateformat = new SimpleDateFormat("yyyy.mm.ss");
	
	SimpleDateFormat optionformat = new SimpleDateFormat("yyyy/mm/ss");
	
	SimpleDateFormat consoleDateformat = new SimpleDateFormat("yyyy-mm-ss");

	private KSGPanel pnConsoleOption;

	private JRadioButton optPage;

	private JRadioButton optCFS;
	
	private JComboBox cbxTableDateList;

	private JTabbedPane tabPane;
	
	private KSGPanel pnNormalOption,pnOption;
	
	private JRadioButton optDate,optVessel;
	
	private CardLayout optionLayout;
	
	private KSGCheckBox cbxNew,cbxInboundSchedule,cbxOutboundSchedule,cbxRouteSchedule;

	private TableServiceImpl tableService;

	
	public ScheduleMgtUI() {

		scheduleService = new ScheduleServiceImpl();
		
		tableService = new TableServiceImpl();
		
		this.addComponentListener(this);
		
		this.title = "���������� ����";
		this.borderColor = new Color(255,100,100);
		createAndUpdateUI();
	}
	

	/**
	 *@���� ȭ�� ���� �� ������Ʈ 
	 */
	public void createAndUpdateUI() 
	{
		this.setName("SearchUI");
		
		this.setLayout(new BorderLayout(10,10));		

		KSGPanel pnNorth 		= buildNorthPn();		
		KSGPanel pnSouth		= buildSouthPn();
		KSGPanel pnLeftMenu		= buildLeftMenu();		
		KSGPanel pnCenter		= buildCenter();

		this.add(pnLeftMenu,BorderLayout.WEST);	

		this.add(pnCenter,BorderLayout.CENTER);
		
		this.add(pnNorth,BorderLayout.NORTH);		
		

		

	}

	private KSGPanel buildLeftMenu() {
		// ���� ��¥ ���====================
		KSGPanel pnLeftMenu = new KSGPanel(new BorderLayout(5,5));	

		butSort = new JButton("���� ��� (P)");
		butSort.setActionCommand("���� ���");
		butSort.setMnemonic(KeyEvent.VK_P);
		butBuild = new JButton("������ ����(N)");
		butBuild.setMnemonic(KeyEvent.VK_N);
		butBuild.setActionCommand(ACTION_CREATE);
		butDelete = new JButton("������ ����(D)");
		butDelete.setMnemonic(KeyEvent.VK_D);
		butDelete.setActionCommand(ACTION_DELETE);

		butSort.addActionListener(new SchedulePrintAction());
		butBuild.addActionListener(this);		
		butDelete.addActionListener(this);		


		// ���̺� ������Ʈ ���� �׸� ����======================		
		KSGPanel pnTableDateModel = new KSGPanel(new BorderLayout());

		KSGPanel pnTableDateModelSouth = new KSGPanel(new BorderLayout());
		KSGPanel pnTableDateModelSouthPadding = new KSGPanel();
		pnTableDateModelSouthPadding.setPreferredSize(new Dimension(15,15));

		pnTableDateModelSouth.add(pnTableDateModelSouthPadding,BorderLayout.SOUTH);

		cbxTableDateList = new JComboBox();

		JButton butUpdate = new JButton("����");
		butUpdate.addActionListener(this);

		KSGPanel pnDateMain = new KSGPanel(new BorderLayout());
		
		KSGPanel pnDate = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnDate.add(new JLabel("���� ����"));
		
		pnDate.add(cbxTableDateList);
		
		pnDate.add(butUpdate);

		GridLayout butlayout = new GridLayout(0,1);
		
		butlayout.setVgap(5);
		
		KSGPanel pnDateButton = new KSGPanel(butlayout);
		
		pnDateButton.setPreferredSize(new Dimension(190,100));
		
		pnDateButton.add(butBuild);
		
		pnDateButton.add(butDelete);

		pnDateMain.add(pnDate);
		
		pnDateMain.add(pnDateButton,BorderLayout.SOUTH);
		
		pnTableDateModel.add(pnDateMain);
		
		pnTableDateModel.add(pnTableDateModelSouth,BorderLayout.SOUTH);		


		// ������ �������� ���===============================
		KSGPanel pnTblScheduleDateList = new KSGPanel(new BorderLayout());
		tblScheduleDateList=  new KSGAbstractTable();
		
		tblScheduleDateList.addColumn(new KSGTableColumn("gubun","����",110));
		
		tblScheduleDateList.addColumn(new KSGTableColumn("date_issue","��������",120));
		
		tblScheduleDateList.addColumn(new KSGTableColumn("cnt","�Ǽ�"));
		
		tblScheduleDateList.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		
		tblScheduleDateList.initComp();
		

		tblScheduleDateList.setShowGrid(false);
		tblScheduleDateList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseClicked(arg0);
				int row=tblScheduleDateList.getSelectedRow();
				if(row==-1)
					return;
				String gubun = (String) tblScheduleDateList.getValueAt(row, 0);
				String inputdate = (String) tblScheduleDateList.getValueAt(row, 1);
				
				pnNormal2.setInput_date(inputdate);
				pnConsole2.setInput_date(inputdate);
				pnInland2.setInput_date(inputdate);
				pnNomalByTree.setInput_date(inputdate);
				pnNomalByTree.setGubun(gubun);
				
				if(gubun.equals(ShippersTable.GUBUN_CONSOLE)||gubun.equals(ShippersTable.GUBUN_NORMAL))
				{
					pnOption.setVisible(true);
					optionLayout.show(pnOption,gubun);

				}else
				{
					pnOption.setVisible(false);
				}
				
				// pnNomalByTree
				if(tabPane.getSelectedIndex()==3)
				{
					pnNomalByTree.fnSearch();
				}

//				if(gubun.equals(ShippersTable.GUBUN_NORMAL))
//				{
//					tabPane.setSelectedIndex(0);
//
//				}else if(gubun.equals(ShippersTable.GUBUN_CONSOLE))
//				{
//					tabPane.setSelectedIndex(1);
//				}
//				else if(gubun.equals(ShippersTable.GUBUN_INLAND))
//				{
//					tabPane.setSelectedIndex(2);
//				}
			}
		});
		pnTblScheduleDateList.add(new JLabel("������ ���� ����"),BorderLayout.NORTH);
		pnTblScheduleDateList.add(new JScrollPane(tblScheduleDateList));
		tblScheduleDateList.getParent().setBackground(Color.white);
		pnConsoleOption = new KSGPanel(new FlowLayout(FlowLayout.LEADING));
		pnConsoleOption.setBorder(BorderFactory.createTitledBorder("��� �׸�"));
		optPage = new JRadioButton("Page",true);
		optCFS = new JRadioButton("CFS");
		ButtonGroup bg = new ButtonGroup();
		bg.add(optPage);
		bg.add(optCFS);		


		pnConsoleOption.add(new JLabel("�ܼ� ���:"));
		pnConsoleOption.add(optPage);
		pnConsoleOption.add(optCFS);		

		pnNormalOption = new KSGPanel(new GridLayout(2,1));

		KSGPanel pnNormalSelectionOption = new KSGPanel(new GridLayout(3,1));
		pnNormalSelectionOption.setBorder(BorderFactory.createTitledBorder("������ ���� ����"));
		cbxInboundSchedule = new KSGCheckBox("Inbound",true);
		cbxOutboundSchedule = new KSGCheckBox("Outbound",true);
		cbxRouteSchedule = new KSGCheckBox("�׷κ�",true);
		cbxRouteSchedule.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pnNormalRouteOption.setVisible(cbxRouteSchedule.isSelected());
				
			}
		});
		
		cbxInboundSchedule.setBackground(Color.white);
		
		cbxOutboundSchedule.setBackground(Color.white);
		
		cbxRouteSchedule.setBackground(Color.white);
		
		pnNormalSelectionOption.add(cbxInboundSchedule);
		
		pnNormalSelectionOption.add(cbxOutboundSchedule);
		
		pnNormalSelectionOption.add(cbxRouteSchedule);

		pnNormalRouteOption = new KSGPanel(new FlowLayout());
		
		pnNormalRouteOption.setBorder(BorderFactory.createTitledBorder("�׷κ� ���� ����"));

		optDate = new KSGRadioButton("��¥",true);
		
		optVessel = new KSGRadioButton("����");
		
		cbxNew = new KSGCheckBox("�ű� ���");		


		ButtonGroup bg2 = new ButtonGroup();
		
		bg2.add(optDate);bg2.add(optVessel);

		pnNormalRouteOption.add(optDate);
		
		pnNormalRouteOption.add(optVessel);
		
		pnNormalRouteOption.add(cbxNew);		

		pnNormalOption.add(pnNormalSelectionOption);
		
		pnNormalOption.add(pnNormalRouteOption);		

		optionLayout = new CardLayout();
		pnOption = new KSGPanel(optionLayout);

		pnOption.add(pnConsoleOption,ShippersTable.GUBUN_CONSOLE);
		
		pnOption.add(pnNormalOption,ShippersTable.GUBUN_NORMAL);
		
		pnOption.setVisible(false);


		pnTblScheduleDateList.add(pnOption,BorderLayout.SOUTH);

		//=============================================

		// ��ư ���=============================================

		GridLayout gridLayout = butlayout;
		gridLayout.setVgap(5);
		KSGPanel pnLeftMenuButtonList = new KSGPanel(gridLayout);
		pnLeftMenuButtonList.setPreferredSize(new Dimension(250,50));

		pnLeftMenuButtonList.add(butSort);		

		//====================================================

		pnLeftMenu.add(pnLeftMenuButtonList,BorderLayout.SOUTH);
		pnLeftMenu.add(pnTableDateModel,BorderLayout.NORTH);
		pnLeftMenu.add(pnTblScheduleDateList);
		
		pnLeftMenu.setPreferredSize(new Dimension(230,0));
		
		pnLeftMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		
		pnMain.add(pnLeftMenu);
		pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		//pnMain.setBorder(BorderFactory.createTitledBorder("������ ���� ����"));
		return pnMain;
	}

	private void updateTableDateList() throws SQLException {
		tableDatelist = tableService.getTableDateList();

		cbxTableDateList.removeAllItems();

		for(int i=0;i<tableDatelist.size();i++)
		{
			ShippersTable date = (ShippersTable) tableDatelist.get(i);
			cbxTableDateList.addItem(date.getDate_isusse());
		}
	}

	private KSGPanel buildCenter() {
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		PnNormal pnNormal = new PnNormal();

		PnConsole pnConsole = new PnConsole();

		PnInland pnInland = new PnInland();
		
		PnOutbound pnOutbound = new PnOutbound();
		
		PnInbound pnInbound = new PnInbound();

		tabPane = new JTabbedPane();

//		tabPane.add(pnNormal,ShippersTable.GUBUN_NORMAL);
//		tabPane.add(pnConsole,ShippersTable.GUBUN_CONSOLE);
//		tabPane.add(pnInland,ShippersTable.GUBUN_INLAND);
		
		
		//tabPane.add(pnOutbound, "OUTBOUND");
		//tabPane.add(pnInbound, "INBOUND");
		
		
		pnNormal2 = new PnNormal2();
		tabPane.add(pnNormal2, "NORMAL");
		pnConsole2 = new PnConsole2();
		tabPane.add(pnConsole2, "CONSOLE");		
		pnInland2 = new PnInland2();
		tabPane.add(pnInland2, "INLAND");
		pnNomalByTree = new PnNormalByTree();
		tabPane.add(pnNomalByTree, "TreeTable");
		
		
		pnMain.add(tabPane);
		

		return pnMain;
	}

	/**
	 * @���� ������ ���� ���� ���̺� ����
	 * @throws SQLException 
	 * 
	 */
	private void updateScheduleDateList() throws SQLException {
		DefaultTableModel defaultTableModel = new DefaultTableModel()
		{
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
		}
		;
		defaultTableModel.addColumn("����");
		defaultTableModel.addColumn("��������");
		List scheduleDateList = scheduleService.selectScheduleDateList();
		List inlandScheduleDateList = scheduleService.getInlandScheduleDateList();
		if(scheduleDateList.size()==0&&inlandScheduleDateList.size()==0)
		{
			butDelete.setEnabled(false);
			butSort.setEnabled(false);
		}

		else
		{
			butDelete.setEnabled(true);
			butSort.setEnabled(true);
		}


//		Iterator<ScheduleData> iter = scheduleDateList.iterator();
//		while(iter.hasNext())
//		{
//			ScheduleData item = iter.next();
//			defaultTableModel.addRow(new Object[]{item.getGubun(),item.getDate_issue()});
//		}
//		Iterator<ScheduleData> inlnadIter = inlandScheduleDateList.iterator();
//
//		while(inlnadIter.hasNext())
//		{
//			ScheduleData item = inlnadIter.next();
//			defaultTableModel.addRow(new Object[]{item.getGubun(),item.getDate_issue()});
//		}
		
		List li = scheduleService.selectScheduleDateList();
		tblScheduleDateList.setResultData(li);
		
		
		
		
		
	}


	
	/**
	 * @throws SQLException 
	 * 
	 */
	private void searchScheduleAction() throws SQLException {

		int row = tblScheduleDateList.getSelectedRow();
		if(row==-1)
			return;

		// ����
		String gubun = (String) tblScheduleDateList.getValueAt(row, 0);
		
		//��������
		String date_isuss = (String) tblScheduleDateList.getValueAt(row, 1);
		
		ScheduleData data = new ScheduleData();
		data.setDate_issue(date_isuss);
		
		data.setGubun(gubun);
		
		if(!txfOption.getText().equals(""))
		{
			String op = txfOption.getText();
			
			int inoutOptionIndex=cbxOption.getSelectedIndex();
			
			switch (inoutOptionIndex) {

			case 0: // ���̺� ID
				data.setTable_id(op);
				break;
			case 1: // �����
				data.setCompany_abbr(op);
				break;
			case 2: // ������Ʈ ��
				data.setAgent(op);
				break;	
			case 3: // ���ڸ�
				data.setVessel(op);
				break;
			case 4: // �����
				data.setFromPort(op);
				break;
			case 5: // ������
				data.setPort(op);
				break;
			default:
				break;
			}
		}


		txfOption.setText("");


	}	

	/**
	 * 
	 */
	private void scheduleDeleteAction() {
		try 
		{
			int result=scheduleService.deleteSchedule();
			int b=scheduleService.deleteInlnadSchedule();

			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, (result+b)+"���� ���� �߽��ϴ�.");
		
			updateScheduleDateList();
			updateTableDateList();

		} catch (SQLException e1) 
		{
			e1.printStackTrace();
			JOptionPane.showMessageDialog(ScheduleMgtUI.this, "error:"+e1.getMessage());
		}
	}

	/** @���� ȭ�� ����
	 * @return
	 */
	private KSGPanel buildSouthPn() {
		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new BorderLayout());

		KSGPanel pnRight = new KSGPanel();
		pnRight.setLayout(new FlowLayout(FlowLayout.RIGHT));	

		JButton butArrange = new JButton("����");
		butArrange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new JDialog(KSGModelManager.getInstance().frame);
				dialog.setModal(true);
				KSGPanel pnMain = new KSGPanel();
				pnMain.add(new JLabel("�ۼ� ���Դϴ�."));
				dialog.getContentPane().add(pnMain,BorderLayout.CENTER);
				dialog.setSize(400, 400);
				ViewUtil.center(dialog, false);
				dialog.setVisible(true);

			}
		});
		butArrange.setPreferredSize(new Dimension(80,25));


		JButton butCompanySearch = new JButton("���纰 �˻�");
		butCompanySearch.setMnemonic(KeyEvent.VK_C);
		butCompanySearch.setFont(KSGModelManager.getInstance().defaultFont);
		butCompanySearch.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {


				String searchDate=JOptionPane.showInputDialog("��¥�� �Է��ϼ���","ex)2010.1.1");


				if(searchDate==null)
					return;
				String datePattern = "\\d{4}.\\d{1,2}.\\d{1,2}";
				boolean retval = true;
				retval = searchDate.matches(datePattern);

				if(!retval)
				{
					JOptionPane.showMessageDialog(null, "�Է� ������ Ʋ�Ƚ��ϴ�. "+searchDate);
					return;
				}

				searchScheduleByCompanyDialog = new JDialog();				
				searchScheduleByCompanyDialog.setModal(true);
				searchScheduleByCompanyDialog.setTitle("���纰 ������ �˻�");
				KSGPanel pnMain = new KSGPanel();

				JTable tblCompany = new JTable();
				DefaultTableModel model = new DefaultTableModel();

				model.addColumn("�����");

				try {
					List li=	scheduleService.getScheduleListGroupByCompany(KSGDateUtil.format(KSGDateUtil.toDate3(searchDate)));


					for(int i=0;i<li.size();i++)
					{

						model.addRow(new Object[]{li.get(i)});
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DateFormattException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				tblCompany.setModel(model);

				pnMain.setLayout(new BorderLayout());

				pnMain.add(new JScrollPane(tblCompany));

				KSGPanel pncontrol = new KSGPanel();
				pncontrol.setLayout(new FlowLayout(FlowLayout.RIGHT));
				JButton butOk = new JButton("Ȯ��");
				butOk.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						searchScheduleByCompanyDialog.setVisible(true);
						searchScheduleByCompanyDialog.dispose();

					}});
				JButton butCancel = new JButton("���");
				butCancel.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) 
					{
						searchScheduleByCompanyDialog.setVisible(true);
						searchScheduleByCompanyDialog.dispose();

					}});

				pncontrol.add(butOk);
				pncontrol.add(butCancel);
				pnMain.add(pncontrol,BorderLayout.SOUTH);

				searchScheduleByCompanyDialog.getContentPane().add(pnMain);
				searchScheduleByCompanyDialog.setSize(500, 500);
				ViewUtil.center(searchScheduleByCompanyDialog);

				searchScheduleByCompanyDialog.setVisible(true);


			}
		});

		JButton butScheduleResult = new JButton("SCHEDULE RESULT");
		butScheduleResult.setMnemonic(KeyEvent.VK_U);
		butScheduleResult.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {


				if(scheduleResultDialog==null||!scheduleResultDialog.isVisible())
				{
					scheduleResultDialog = new ScheduleResultDialog();
					scheduleResultDialog.createAndUpdateUI();
				}

			}});
		JButton butErrorLog = new JButton("ERROR LOG����");
		butErrorLog.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(ScheduleMgtUI.this, errorLogManager.getLogger());

			}
		});

		KSGPanel pnLeft = new KSGPanel();


		lblNTop = new JLabel("0/0");
		pnLeft.add(lblNTop);

		pnMain.add(pnRight,BorderLayout.EAST);


		return pnMain;
	}


	ScheduleManager scheduleManager = ScheduleManager.getInstance();

	private KSGPanel pnNormalRouteOption;

	private PnNormal2 pnNormal2;

	private PnConsole2 pnConsole2;

	private PnInland2 pnInland2;

	private PnNormalByTree pnNomalByTree;

	class SchedulePrintAction implements ActionListener
	{

		private static final String ACTION_PRINT_FLIE = "���� ���";

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			try{
				if(command.equals(ACTION_PRINT_FLIE))
				{
					int selectedRow=tblScheduleDateList.getSelectedRow();
					if(selectedRow<0)
					{					
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "���� ����� ���ڸ� �����Ͻʽÿ�");
						return;
					}
					
					//����
					String gubun = (String) tblScheduleDateList.getValueAt(selectedRow, 0);
					
					//������ ������
					String selectedDate = (String) tblScheduleDateList.getValueAt(selectedRow, 1);

					
					java.util.Date d = fromDateformat.parse(selectedDate);

					ScheduleData op = new ScheduleData();
					
					// �ܼ� ������ ����
					if(gubun.equals(ShippersTable.GUBUN_CONSOLE))
					{
						op.setDate_issue(selectedDate);

						op.setConsole_print_type(optPage.isSelected()?ScheduleType.CONSOLE_PAGE:ScheduleType.CONSOLE_CFS);

						ScheduleJoint console=scheduleManager.getConsoleSchedudle(op);
						
						scheduleManager.addBulid(console);

						scheduleManager.startBuild();

					}
					
					// �η��� ������
					else if(gubun.equals(ShippersTable.GUBUN_INLAND))
					{
						op.setDate_issue(selectedDate);
						
						new SortInlandCommnad(op).execute();
					}
					
					else //normal: �ƿ��ٿ��, �ιٿ��, �׷κ�
					{
						ShippersTable op2 = new ShippersTable();
						op2.setDate_isusse(optionformat.format(d));
						new SortAllCommand(op2,optDate.isSelected()?RouteTaskNewVessel.ORDER_BY_DATE:RouteTaskNewVessel.ORDER_BY_VESSEL,
											cbxNew.isSelected(),
											cbxInboundSchedule.isSelected(),
											cbxOutboundSchedule.isSelected(),
											cbxRouteSchedule.isSelected()).execute();

					}
				}
			}catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(ScheduleMgtUI.this, ee.getMessage());
			}
		}
	}


	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		try {
			if(command.equals("������ ��ȸ"))
			{
				searchScheduleAction();
			}
			else if(command.equals("�˻�"))
			{
				searchScheduleAction();
			}
			else if(command.equals(ACTION_DELETE))
			{
				scheduleDeleteAction();
			}
			else if(command.equals("����"))
			{
				updateScheduleDateList();
				updateTableDateList();

			}
			else if(command.equals(ACTION_CREATE))
			{		
				// ������ �������� ����

				try {

					java.util.Date d = consoleDateformat.parse((String)cbxTableDateList.getSelectedItem());
					String inputDate = toDateformat.format(d);
					
					/* �ɼ� ���� �׸�
					 * 1. 
					 * 2. 
					 */
					
					// �Է°� ��ȿ�� üũ
					
					
					// ������ ����
					serviceManager.buildSchedule(inputDate);
					
					//
				
					updateScheduleDateList();
					
					updateTableDateList();
					
				} catch (ParseException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(ScheduleMgtUI.this, e.getMessage());
				}


			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(ScheduleMgtUI.this, e.getMessage());
		}

	}


	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentShown(ComponentEvent e) {
		try {
			updateTableDateList();
			updateScheduleDateList();
		} catch (SQLException ee) {
			JOptionPane.showMessageDialog(ScheduleMgtUI.this, ee.getMessage());
			ee.printStackTrace();
		}
		
	}


	@Override
	public void componentHidden(ComponentEvent e) {
		
		
	}




}
