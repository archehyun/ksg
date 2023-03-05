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

import com.dtp.api.control.ScheduleController;
import com.ksg.commands.schedule.route.RouteTaskNewVessel;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ScheduleType;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.ScheduleServiceManager;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.KSGCheckBox;
import com.ksg.view.comp.KSGRadioButton;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.ui.ErrorLogManager;
import com.ksg.workbench.common.comp.AbstractMgtUI;
import com.ksg.workbench.common.comp.View;
import com.ksg.workbench.common.comp.panel.KSGPanel;
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
 * 스케줄 정보 조회
 * @author 박창현
 *
 */
/**

 * @FileName : ScheduleMgtUI.java

 * @Date : 2021. 4. 29. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
public class ScheduleMgtUI extends AbstractMgtUI implements ActionListener, ComponentListener, View {


	private static final String ACTION_CREATE = "스케줄 생성";

	private static final String ACTION_DELETE = "스케줄 삭제";

	ErrorLogManager errorLogManager = ErrorLogManager.getInstance();

	private ScheduleServiceManager serviceManager =ScheduleServiceManager.getInstance();

	private static final long serialVersionUID = 1L;	

	private KSGAbstractTable tblScheduleDateList;

	private JLabel lblNTop;

	private ScheduleResultDialog scheduleResultDialog;

	private ScheduleSubService scheduleService;

	private JDialog searchScheduleByCompanyDialog;	

	private JComboBox cbxOption;

	private JTextField txfOption;

	private JButton butDelete,butPrint;

	private List tableDatelist;

	private JButton butBuild;

	private SimpleDateFormat fromDateformat = new SimpleDateFormat("yy-mm-ss");

	private SimpleDateFormat toDateformat = new SimpleDateFormat("yyyy.mm.ss");

	private SimpleDateFormat optionformat = new SimpleDateFormat("yyyy/mm/ss");

	private SimpleDateFormat consoleDateformat = new SimpleDateFormat("yyyy-mm-ss");

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

	JComboBox cbxRouteLogic;	

	public ScheduleMgtUI() {

		scheduleService = new ScheduleServiceImpl();

		tableService = new TableServiceImpl();
		
		this.setController(new ScheduleController());

		this.addComponentListener(this);

		this.title = "스케줄정보 관리";

		this.borderColor = new Color(255,100,100);

		createAndUpdateUI();
	}


	/**
	 *@설명 화면 생성 및 업데이트 
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
		// 왼쪽 일짜 목록====================
		KSGPanel pnLeftMenu = new KSGPanel(new BorderLayout(5,5));	

		butPrint = new JButton("파일 출력 (P)");
		
		butPrint.setActionCommand("파일 출력");
		
		butPrint.setMnemonic(KeyEvent.VK_P);
		
		butBuild = new JButton("스케줄 생성(N)");
		
		butBuild.setMnemonic(KeyEvent.VK_N);
		
		butBuild.setActionCommand(ACTION_CREATE);		
		
		butDelete = new JButton("스케줄 삭제(D)");
		
		butDelete.setMnemonic(KeyEvent.VK_D);
		
		butDelete.setActionCommand(ACTION_DELETE);

		butPrint.addActionListener(new SchedulePrintAction());
		
		
		butBuild.addActionListener(this);	
		
		butDelete.addActionListener(this);		


		// 테이블 업데이트 일자 항목 생성======================		
		KSGPanel pnTableDateModel = new KSGPanel(new BorderLayout());

		KSGPanel pnTableDateModelSouth = new KSGPanel(new BorderLayout());

		KSGPanel pnTableDateModelSouthPadding = new KSGPanel();

		pnTableDateModelSouthPadding.setPreferredSize(new Dimension(15,15));

		pnTableDateModelSouth.add(pnTableDateModelSouthPadding,BorderLayout.SOUTH);

		cbxTableDateList = new JComboBox();
		
		cbxTableDateList.setPreferredSize(new Dimension(120,25));

		JButton butUpdate = new JButton("갱신");
		
		butUpdate.addActionListener(this);

		KSGPanel pnDateMain = new KSGPanel(new BorderLayout());

		KSGPanel pnDate = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		pnDate.add(new JLabel("기준 일자"));

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


		// 스케줄 생성일자 목록===============================
		KSGPanel pnTblScheduleDateList = new KSGPanel(new BorderLayout());

		tblScheduleDateList=  new KSGAbstractTable();

		tblScheduleDateList.addColumn(new KSGTableColumn("gubun","구분",110));

		tblScheduleDateList.addColumn(new KSGTableColumn("date_issue","생성일자",120));

		tblScheduleDateList.addColumn(new KSGTableColumn("cnt_i","I"));

		tblScheduleDateList.addColumn(new KSGTableColumn("cnt_o","O"));

		tblScheduleDateList.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

		tblScheduleDateList.initComp();


		tblScheduleDateList.setShowGrid(false);

		tblScheduleDateList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				super.mouseClicked(arg0);

				int row=tblScheduleDateList.getSelectedRow();

				if(row==-1) return;

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
				
				if(tabPane.getSelectedIndex()==3)
				{
					pnNomalByTree.fnSearch();
				}
			}
		});
		pnTblScheduleDateList.add(new JLabel("스케줄 생성 일자"),BorderLayout.NORTH);
		
		pnTblScheduleDateList.add(new JScrollPane(tblScheduleDateList));
		
		tblScheduleDateList.getParent().setBackground(Color.white);
		
		pnConsoleOption = new KSGPanel(new FlowLayout(FlowLayout.LEADING));
		
		pnConsoleOption.setBorder(BorderFactory.createTitledBorder("출력 항목"));
		
		optPage = new KSGRadioButton("Page",true);
		
		optCFS = new KSGRadioButton("CFS");
		
		ButtonGroup bg = new ButtonGroup();
		
		bg.add(optPage);
		bg.add(optCFS);		


		pnConsoleOption.add(new JLabel("콘솔 출력:"));
		pnConsoleOption.add(optPage);
		pnConsoleOption.add(optCFS);		

		pnNormalOption = new KSGPanel(new GridLayout(2,1));

		KSGPanel pnNormalSelectionOption = new KSGPanel(new GridLayout(3,1));
		pnNormalSelectionOption.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		pnNormalSelectionOption.setBorder(BorderFactory.createTitledBorder("스케줄 생성 여부"));
		cbxInboundSchedule = new KSGCheckBox("Inbound",true);
		cbxOutboundSchedule = new KSGCheckBox("Outbound",true);
		cbxRouteSchedule = new KSGCheckBox("항로별",true);

		cbxRouteSchedule.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pnNormalRouteOption.setVisible(cbxRouteSchedule.isSelected());
				cbxRouteLogic.setVisible(cbxRouteSchedule.isSelected());
			}
		});

		cbxInboundSchedule.setBackground(Color.white);

		cbxOutboundSchedule.setBackground(Color.white);

		cbxRouteSchedule.setBackground(Color.white);

		cbxRouteLogic = new JComboBox<String>();
		cbxRouteLogic.addItem("기존");
		cbxRouteLogic.addItem("신규");

		KSGPanel pnOutboundScheduleOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		KSGPanel pnRouteScheduleOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		KSGPanel pnInboundScheduleOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		pnRouteScheduleOption.add(cbxRouteSchedule);
		pnRouteScheduleOption.add(cbxRouteLogic);

		pnOutboundScheduleOption.add(cbxOutboundSchedule);
		
		pnInboundScheduleOption.add(cbxInboundSchedule);

		pnNormalSelectionOption.add(pnOutboundScheduleOption);

		pnNormalSelectionOption.add(pnInboundScheduleOption);

		pnNormalSelectionOption.add(pnRouteScheduleOption);




		pnNormalRouteOption = new KSGPanel(new FlowLayout());

		pnNormalRouteOption.setBorder(BorderFactory.createTitledBorder("항로별 정렬 기준"));

		optDate = new KSGRadioButton("날짜",true);

		optVessel = new KSGRadioButton("선박");

		cbxNew = new KSGCheckBox("신규 방식");		


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

		// 버튼 목록=============================================

		GridLayout gridLayout = butlayout;
		gridLayout.setVgap(5);
		KSGPanel pnLeftMenuButtonList = new KSGPanel(gridLayout);
		pnLeftMenuButtonList.setPreferredSize(new Dimension(250,50));

		pnLeftMenuButtonList.add(butPrint);		

		//====================================================

		pnLeftMenu.add(pnLeftMenuButtonList,BorderLayout.SOUTH);
		pnLeftMenu.add(pnTableDateModel,BorderLayout.NORTH);
		pnLeftMenu.add(pnTblScheduleDateList);

		pnLeftMenu.setPreferredSize(new Dimension(280,0));

		pnLeftMenu.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		pnMain.add(pnLeftMenu);
		pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		//pnMain.setBorder(BorderFactory.createTitledBorder("스케줄 정보 관리"));
		return pnMain;
	}

	private void updateTableDateList(List<String> tableDatelist)  {		

				
	}

	private KSGPanel buildCenter() {

		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		PnNormal pnNormal = new PnNormal();

		PnConsole pnConsole = new PnConsole();

		PnInland pnInland = new PnInland();

		PnOutbound pnOutbound = new PnOutbound();

		PnInbound pnInbound = new PnInbound();

		tabPane = new JTabbedPane();

		pnNormal2 = new PnNormal2();

		pnConsole2 = new PnConsole2();

		pnInland2 = new PnInland2();

		pnNomalByTree = new PnNormalByTree();

		tabPane.add(pnNormal2, "NORMAL");
		tabPane.add(pnConsole2, "CONSOLE");
		tabPane.add(pnInland2, "INLAND");
		tabPane.add(pnNomalByTree, "TreeTable");



		pnMain.add(tabPane);


		return pnMain;
	}

	List scheduleDateLists;

	/**
	 * @설명 스케줄 생성 일자 테이블 갱신
	 * @throws SQLException 
	 * 
	 */
	private void updateScheduleDateList(List scheduleDateLists) throws SQLException {


		List scheduleDateList = scheduleService.selectScheduleDateList();

		List inlandScheduleDateList = scheduleService.getInlandScheduleDateList();

		if(scheduleDateList.size()==0&&inlandScheduleDateList.size()==0)
		{
			butDelete.setEnabled(false);
			butPrint.setEnabled(false);
		}

		else
		{
			butDelete.setEnabled(true);
			butPrint.setEnabled(true);
		}		

		tblScheduleDateList.setResultData(scheduleDateLists);


	}



	/**
	 * @throws SQLException 
	 * 
	 */
	private void searchScheduleAction() throws SQLException {

		int row = tblScheduleDateList.getSelectedRow();
		if(row==-1)
			return;

		// 구분
		String gubun = (String) tblScheduleDateList.getValueAt(row, 0);

		//생성일자
		String date_isuss = (String) tblScheduleDateList.getValueAt(row, 1);

		ScheduleData data = new ScheduleData();
		data.setDate_issue(date_isuss);

		data.setGubun(gubun);

		if(!txfOption.getText().equals(""))
		{
			String op = txfOption.getText();

			int inoutOptionIndex=cbxOption.getSelectedIndex();

			switch (inoutOptionIndex) {

			case 0: // 테이블 ID
				data.setTable_id(op);
				break;
			case 1: // 선사명
				data.setCompany_abbr(op);
				break;
			case 2: // 에이전트 명
				data.setAgent(op);
				break;	
			case 3: // 선박명
				data.setVessel(op);
				break;
			case 4: // 출발항
				data.setFromPort(op);
				break;
			case 5: // 도착항
				data.setPort(op);
				break;
			default:
				break;
			}
		}


		txfOption.setText("");


	}

	/** @설명 화면 생성
	 * @return
	 */
	private KSGPanel buildSouthPn() {
		KSGPanel pnMain = new KSGPanel();
		pnMain.setLayout(new BorderLayout());

		KSGPanel pnRight = new KSGPanel();
		pnRight.setLayout(new FlowLayout(FlowLayout.RIGHT));	

		JButton butArrange = new JButton("정렬");
		butArrange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JDialog dialog = new JDialog(KSGModelManager.getInstance().frame);
				dialog.setModal(true);
				KSGPanel pnMain = new KSGPanel();
				pnMain.add(new JLabel("작성 중입니다."));
				dialog.getContentPane().add(pnMain,BorderLayout.CENTER);
				dialog.setSize(400, 400);
				ViewUtil.center(dialog, false);
				dialog.setVisible(true);

			}
		});
		butArrange.setPreferredSize(new Dimension(80,25));


		JButton butCompanySearch = new JButton("선사별 검색");
		butCompanySearch.setMnemonic(KeyEvent.VK_C);
		butCompanySearch.setFont(KSGModelManager.getInstance().defaultFont);
		butCompanySearch.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {


				String searchDate=JOptionPane.showInputDialog("날짜를 입력하세요","ex)2010.1.1");


				if(searchDate==null)
					return;
				String datePattern = "\\d{4}.\\d{1,2}.\\d{1,2}";
				boolean retval = true;
				retval = searchDate.matches(datePattern);

				if(!retval)
				{
					JOptionPane.showMessageDialog(null, "입력 형식이 틀렸습니다. "+searchDate);
					return;
				}

				searchScheduleByCompanyDialog = new JDialog();				
				searchScheduleByCompanyDialog.setModal(true);
				searchScheduleByCompanyDialog.setTitle("선사별 스케줄 검색");
				KSGPanel pnMain = new KSGPanel();

				JTable tblCompany = new JTable();
				DefaultTableModel model = new DefaultTableModel();

				model.addColumn("선사명");

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
				JButton butOk = new JButton("확인");
				butOk.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						searchScheduleByCompanyDialog.setVisible(true);
						searchScheduleByCompanyDialog.dispose();

					}});
				JButton butCancel = new JButton("취소");
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
		JButton butErrorLog = new JButton("ERROR LOG보기");
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

		private static final String ACTION_PRINT_FLIE = "파일 출력";

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			try{
				if(command.equals(ACTION_PRINT_FLIE))
				{
					int selectedRow=tblScheduleDateList.getSelectedRow();
					
					if(selectedRow<0)
					{					
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "파일 출력할 일자를 선택하십시요");
						return;
					}

					//구분
					String gubun 		= (String) tblScheduleDateList.getValueAt(selectedRow, 0);

					//스케줄 생성일
					String selectedDate = (String) tblScheduleDateList.getValueAt(selectedRow, 1);				


					ScheduleData op = new ScheduleData();
					
					op.setDate_issue(selectedDate);
					
					CommandMap param = new CommandMap();
					
					param.put("gubun", gubun);
					
					param.put("op", op);
					

					// 콘솔 스케줄 생성
					if(gubun.equals(ShippersTable.GUBUN_CONSOLE))
					{	
						op.setConsole_print_type(optPage.isSelected()?ScheduleType.CONSOLE_PAGE:ScheduleType.CONSOLE_CFS);						
					}

					else //normal: 아웃바운드, 인바운드, 항로별
					{
						ShippersTable op2 = new ShippersTable();

						op2.setDate_isusse(selectedDate);						

						param.put("date_isusse", selectedDate);

						param.put("isPrintOutbound", cbxOutboundSchedule.isSelected());

						param.put("isPrintInbound", cbxInboundSchedule.isSelected());

						param.put("isPrintRoute", cbxRouteSchedule.isSelected());

						param.put("isNew", cbxNew.isSelected());

						param.put("isPrintNewRoute", cbxRouteLogic.getSelectedIndex()!=0);

						param.put("orderBy", optDate.isSelected()?RouteTaskNewVessel.ORDER_BY_DATE:RouteTaskNewVessel.ORDER_BY_VESSEL);
					}
					
					callApi("schedulePrint", param);
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
			if(command.equals("스케줄 조회"))
			{
				searchScheduleAction();
			}
			else if(command.equals("검색"))
			{
				searchScheduleAction();
			}
			else if(command.equals(ACTION_DELETE))
			{
				callApi("deleteSchedule");
			}
			else if(command.equals("갱신"))
			{
				
				callApi("scheduleViewUpdate");
			}
			else if(command.equals(ACTION_CREATE))
			{		
				// 스케줄 생성일자 선택

				try {

					java.util.Date d = consoleDateformat.parse((String)cbxTableDateList.getSelectedItem());
					String inputDate = toDateformat.format(d);

					/* 옵션 선택 항목
					 * 1. 
					 * 2. 
					 */

					// 입력값 유효성 체크


					// 스케줄 생성
					serviceManager.buildSchedule(inputDate);

					//
					updateView();

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
	public void componentShown(ComponentEvent e) {
		
		callApi("scheduleViewUpdate");
	}

	@Override
	public void updateView() {
		
		CommandMap result= this.getModel();

		boolean success = (boolean) result.get("success");

		if(success)
		{
			String serviceId = (String) result.get("serviceId");
			
			if("scheduleViewUpdate".equals(serviceId)) {
				
				List tableDatelist = (List) result.get("tableDatelist");

				List scheduleDateLists = (List) result.get("scheduleDateLists");

				cbxTableDateList.removeAllItems();
				
				tblScheduleDateList.setResultData(scheduleDateLists);
				
				tableDatelist.stream().forEach(scheduleDate -> cbxTableDateList.addItem(scheduleDate));
	
			}
			else if("deleteSchedule".equals(serviceId)) {
				
				int deleteCount = (int) result.get("deleteCount");
				
				JOptionPane.showMessageDialog(this, deleteCount+"건을 삭제 했습니다.");
				
				callApi("scheduleViewUpdate");
			}
		}
		else{  
			String error = (String) result.get("error");
			JOptionPane.showMessageDialog(this, error);
		}
		
	}

}
