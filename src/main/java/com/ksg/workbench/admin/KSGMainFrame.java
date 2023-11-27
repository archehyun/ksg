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
package com.ksg.workbench.admin;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.dtp.api.control.AbstractController;
import com.dtp.api.control.MainController;
import com.ksg.commands.IFCommand;
import com.ksg.commands.schedule.BuildXMLInboundCommand;
import com.ksg.commands.schedule.BuildXMLOutboundCommand;
import com.ksg.commands.schedule.BuildXMLRouteScheduleCommand;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.Configure;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.model.KSGObserver;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.ScheduleServiceManager;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.textfield.LookAheadTextField;
import com.ksg.view.comp.textfield.StringArrayLookAhead;
import com.ksg.workbench.KSGLogin;
import com.ksg.workbench.adv.ADVManageUI;
import com.ksg.workbench.common.comp.View;
import com.ksg.workbench.common.dialog.SearchADVCountDialog;
import com.ksg.workbench.common.dialog.WebScheduleCreateDialog;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.preference.PreferenceDialog;

import lombok.extern.slf4j.Slf4j;


/**

 * @FileName : KSGMainFrame.java

 * @Date : 2021. 4. 17. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
@SuppressWarnings("serial")
@Slf4j
public class KSGMainFrame extends JFrame implements ActionListener,KSGObserver, View{

	private AbstractController controller;

	public void setController(AbstractController constroller)
	{
		this.controller =constroller;
	}
	private CommandMap model;

	private static final String MONDAY = "월요일";
	
	private static final String SCHEDULE_WORLDWIDE	= "항로별 스케줄 생성";
	private static final String SCHEDULE_INBOUND	= "Inbound 스케줄 생성";
	private static final String SCHEDULE_OUTBOUND	= "Outbound 스케줄 생성";
	private static final String ADV_INPUT_SEARCH	= "광고입력 조회";
	private static final String SCHEDULE_DELETE 	= "Schedule 삭제";
	private static final String SCHEDULE_SEARCH 	= "Schedule 확인";
	private static final String ADV_PRINT 			= "광고정보 출력";
	private static final String ADV_INPUT 			= "광고정보 입력";
	private static final String PREFERENCE 			= "환경설정";
	private static final String ADV_SEARCH 			= "광고정보 조회";
	private static final String BASE_MAIN 			= "기초정보관리";
	private static final String BASE_CODE 			= "코드정보";
	private static final String BASE_AREA 			= "지역정보";
	private static final String BASE_COMPANY 		= "선사정보";
	private static final String BASE_PORT 			= "항구정보";
	private static final String BASE_PORT_ABBR 		= "항구약어정보";
	private static final String BASE_VESSEL 		= "선박정보";
	private static final String BASE_VESSEL_ABBR 	= "선박약어정보";
	
	private String[] adv_menu = {"광고정보관리", ADV_SEARCH, ADV_INPUT,"-", ADV_PRINT, ADV_INPUT_SEARCH};
	
	private String[] master_menu = {"기초정보관리", BASE_CODE,"-", BASE_AREA, BASE_COMPANY, BASE_PORT, BASE_VESSEL};

	private LookAheadTextField txfImportDate;

	private static String TITLE="KSG DTP System";

	private KSGModelManager modelManager = KSGModelManager.getInstance();

	private CardLayout cardLayout= new CardLayout();

	ScheduleServiceManager serviceManager =ScheduleServiceManager.getInstance();

	private IFCommand scheduleCommand;

	protected JDialog optionDialog;

	private LookAheadTextField txfDate;

	private BaseInfoUI pnBaseInfo;

	private ADVManageUI pnAdvAdd;

	private KSGPanel pnCenter,pnSearch,pnPrintADV;

	private KSGPanel pnSchedule;

	private KSGModelManager manager;

	private JMenuBar menuBar;

	private JToolBar toolbar;

	private KSGLogin login;

	private JProgressBar workprocess;

	private JLabel lblWorkProcessText;

	public static String NAME="ksgframe";

	private AdvertisementActionListener advActionListener = new AdvertisementActionListener();

	private ScheduleActionListener scheduleActionListener = new ScheduleActionListener();

	private SortActionListenr sortActionListenr = new SortActionListenr();

	private BuildActionListenr buildActionListenr = new BuildActionListenr();

	private Configure config = Configure .getInstance();

	private PnMainTab pnMainView = new PnMainTab();

	public KSGMainFrame(KSGLogin login) 
	{	
		this.setName(NAME);

		NotificationManager.getInstance().setFrame(this);

		this.setController(new MainController());

		manager = KSGModelManager.getInstance();

		manager.addObservers(this);

		this.login = login;
	}

	public void completeCardLayout()
	{
		new Thread(){
			public void run()
			{
				log.debug("init frame start");

				KSGMainFrame.this.setVisible(false);

				KSGMainFrame.this.setResizable(true);

				KSGMainFrame.this.setDefaultLookAndFeelDecorated(true);		

				KSGMainFrame.this.setMinimumSize(new Dimension(1300,800));

				menuBar.setVisible(true);

				ViewUtil.center(KSGMainFrame.this, true);

				pnMainView.showPanel(ADV_SEARCH);

				login.setVisible(false);

				login.dispose();

				KSGMainFrame.this.setVisible(true);

				KSGMainFrame.this.setExtendedState(JFrame.MAXIMIZED_BOTH);

				log.debug("init frame end");
			}

		}.start();
	}

	private KSGPanel buildCenter() 
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout(0,10));

		pnCenter = new KSGPanel();

		pnCenter.setLayout(cardLayout);

		pnMain.add(this.pnMainView);

		pnMain.add(buildButtom(),BorderLayout.SOUTH);

		return pnMain;
	}

	/**
	 * 화면 초기화 
	 */
	public void createAndUpdateUI()
	{
		log.debug("create frame start");

		Toolkit toolkit = Toolkit.getDefaultToolkit();

		Image img = toolkit.getImage("images/img_logo.png");

		this.setIconImage(img);		

		this.setJMenuBar(crateMenuBar());

		this.setLayout(new BorderLayout(0,5 ));

		this.getContentPane().add(buildToolBar(),BorderLayout.NORTH);

		this.getContentPane().add(buildCenter(),BorderLayout.CENTER);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setTitle(TITLE);

		this.setVisible(false);

		log.debug("create frame end");

	}

	private KSGPanel buildButtom() {

		int inst = 10;

		KSGPanel pnButtom = new KSGPanel();

		pnButtom.setLayout(new FlowLayout(FlowLayout.RIGHT));

		pnButtom.setPreferredSize(new Dimension(0,inst+15));

		workprocess = new JProgressBar();

		workprocess.setVisible(manager.isWorkMoniter);

		lblWorkProcessText = new JLabel();

		lblWorkProcessText.setVisible(manager.isWorkMoniter);

		pnButtom.add(lblWorkProcessText);

		pnButtom.add(workprocess);

		KSGPanel pnMain = new KSGPanel();

		pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		pnMain.setLayout(new BorderLayout());

		KSGPanel pnLeft = new KSGPanel();

		pnLeft.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel label = new JLabel("DB : "+config.getProperty("mssql.db"));

		label.setFont(KSGModelManager.getInstance().defaultFont);

		pnLeft.add(label);

		pnMain.add(pnButtom,BorderLayout.EAST);

		pnMain.add(pnLeft,BorderLayout.WEST);

		return pnMain;
	}

	private void addToolBarButton(String butName, String img, String action)
	{
		JButton butSearch = new JButton(butName,new ImageIcon(img));

		butSearch.setPreferredSize(new Dimension(20,20));
		
		butSearch.setActionCommand(action);

		butSearch.setBorderPainted(false);

		butSearch.setVerticalAlignment(0);

		butSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				pnMainView.showPanel(e.getActionCommand());
			}
		});
		toolbar.add(butSearch);
	}

	/** 툴바 생성
	 * @return
	 */
	private Component buildToolBar() 
	{
		toolbar = new JToolBar();

		toolbar.setPreferredSize(new Dimension(-1,45));

		toolbar.setBorderPainted(true);

		addToolBarButton("광고 조회","images/menu_search.gif",ADV_SEARCH);

		addToolBarButton("광고 입력","images/importxls8.gif",ADV_INPUT);

		addToolBarButton("광고 출력","images/export.gif",ADV_PRINT);

		addToolBarButton("스케줄 관리","images/menu_schedule.gif",SCHEDULE_SEARCH);

		addToolBarButton(BASE_MAIN,"images/db_table8.gif",BASE_MAIN);

		StringArrayLookAhead lookAhead = new StringArrayLookAhead(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
		txfImportDate = new LookAheadTextField("광고입력수 조회",10,lookAhead);

		txfImportDate.setMaximumSize(new Dimension(100,25));

		txfImportDate.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e) {

				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					String date=txfImportDate.getText();

					if(!KSGDateUtil.isDashFomatt(date))
					{
						JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "입력 형식(2000.1.1)이 틀렸습니다. "+date);

						toolbar.requestFocus();

						return;
					}

					try {
						String searchDate = KSGDateUtil.toDate3(date).toString();
						
						CommandMap param = new CommandMap();
						
						param.put("searchDate", searchDate);
						
						callApi("searchTableCount", param);

					} 
					catch (DateFormattException e2) {
						JOptionPane.showMessageDialog(null, "error:"+e2.getMessage());
						e2.printStackTrace();
					}
				}
			}
		});

		JCheckBox cbxImportDate = new JCheckBox(MONDAY,false);

		cbxImportDate.setFont(KSGModelManager.getInstance().defaultFont);

		cbxImportDate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				JCheckBox bo =(JCheckBox) e.getSource();
				
				if(bo.isSelected()) txfImportDate.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
			}
		});

		toolbar.addSeparator();
		
		toolbar.add(txfImportDate);

		return toolbar;
	}


	private JMenuBar crateMenuBar() 
	{
		menuBar = new JMenuBar();
		
		BiggerMenu fileMenu = new BiggerMenu("File");
		
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem ExitMenu = new JMenuItem("Exit", KeyEvent.VK_X);
		
		ExitMenu.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				System.exit(0);

			}});
		fileMenu.add(ExitMenu);

		menuBar.add(fileMenu);


		//광고정보관리 메뉴 ================================

		BiggerMenu AdvMenu = new BiggerMenu(adv_menu[0]);
		
		for(int i =1;i<adv_menu.length;i++)
		{
			if("-".equals(adv_menu[i]))
			{
				AdvMenu.addSeparator();
			}
			else
			{
				this.addMenuItem(AdvMenu, adv_menu[i], KeyEvent.VK_X,advActionListener);
			}
		}

		//기초정보관리 메뉴================================

		BiggerMenu BaseInfoMenu = new BiggerMenu(master_menu[0]);
		
		for(int i =1;i<master_menu.length;i++)
		{
			if("-".equals(master_menu[i]))
			{
				BaseInfoMenu.addSeparator();
			}
			else
			{
				this.addMenuItem(BaseInfoMenu, master_menu[i], this);
			}
		}
		//===============================================

		BiggerMenu schduleMenu = new BiggerMenu("Schedule정보 관리");

		addMenuItem(schduleMenu, SCHEDULE_DELETE,scheduleActionListener);

		schduleMenu.addSeparator();

		JMenu menuScheduleBuild=(JMenu) addMenu(schduleMenu, "Schedule Build");	

		//====================================================================	
		JMenuItem subMenuBuildTotal =new JMenuItem("Out/Inbound");

		JMenuItem subMenuBuildConsole =new JMenuItem("Console");

		subMenuBuildTotal.addActionListener(buildActionListenr);

		subMenuBuildConsole.addActionListener(buildActionListenr);

		menuScheduleBuild.add(subMenuBuildTotal);

		menuScheduleBuild.add(subMenuBuildConsole);	

		//====================================================================	
		JMenu menuScheduleSort=(JMenu) addMenu(schduleMenu, "Schedule Sort");

		JMenuItem subMenuSortOutbound =new JMenuItem("Outbound");

		JMenuItem subMenuSortInbound =new JMenuItem("Inbound");

		JMenuItem subMenuSortWorld =new JMenuItem("항로별");		

		subMenuSortOutbound.addActionListener(sortActionListenr);

		subMenuSortInbound.addActionListener(sortActionListenr);

		subMenuSortWorld.addActionListener(sortActionListenr);

		menuScheduleSort.add(subMenuSortOutbound);

		menuScheduleSort.add(subMenuSortInbound);

		menuScheduleSort.add(subMenuSortWorld);		

		schduleMenu.addSeparator();

		schduleMenu.addSeparator();

		JMenuItem menuItem1=addMenuItem(schduleMenu, "일괄작업(Build_Sort)", this);

		menuItem1.setEnabled(false);

		addMenuItem(schduleMenu, "일괄작업(Sort)",scheduleActionListener);		

		addMenuItem(schduleMenu, "일괄작업(전송용)",scheduleActionListener);

		addMenuItem(schduleMenu, "일괄작업(전송용New)",scheduleActionListener);

		schduleMenu.addSeparator();

		BiggerMenu optionMenu = new BiggerMenu("옵션",KeyEvent.VK_O);

		this.addMenuItem(optionMenu, PREFERENCE, this);

		menuBar.add(AdvMenu);

		menuBar.add(schduleMenu);

		menuBar.add(BaseInfoMenu);	

		menuBar.add(optionMenu);

		menuBar.setVisible(false);

		return menuBar;
	}

	private JMenu addMenu(JMenu menu,String label)
	{
		JMenu item = new JMenu(label);
		
		item.addActionListener(this);
		
		menu.add(item);
		
		return item;
	}
	private JMenuItem addMenuItem(JMenu menu,String label, ActionListener ac)
	{
		JMenuItem item = new JMenuItem(label);
		
		item.addActionListener(ac);
		
		menu.add(item);
		
		return item;
	}

	private JMenuItem addMenuItem(JMenu menu,String label,int numeric,ActionListener ac)
	{	
		JMenuItem item = this.addMenuItem(menu, label, ac);	
		
		item.setMnemonic(numeric);

		return item;
	}

	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();

		if(command.equals(PREFERENCE))
		{
			PreferenceDialog preferenceDialog = new PreferenceDialog(PREFERENCE,true);
			
			preferenceDialog.createAndUpdateUI();
		}
		
		pnMainView.showPanel(command);
		
	}
	private void sort()
	{
		optionDialog = new JDialog(KSGModelManager.getInstance().frame);

		optionDialog.setModal(true);

		optionDialog.setTitle("Sorting");

		KSGPanel pnMain = new KSGPanel();

		KSGPanel pnInput = new KSGPanel();

		pnInput.setLayout( new FlowLayout(FlowLayout.LEADING));

		StringArrayLookAhead lookAhead = new StringArrayLookAhead(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));

		txfDate = new LookAheadTextField("생성 날짜 입력",15,lookAhead);

		txfDate.setFocus_lost(false);

		JCheckBox cbxMondya = new JCheckBox(MONDAY);

		cbxMondya.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					txfDate.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
				}

			}});

		pnInput.add(txfDate);

		pnInput.add(cbxMondya);

		KSGPanel pnOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		pnOption.setVisible(false);

		KSGPanel pnControl = new KSGPanel();

		JButton butOk = new JButton("확인");

		butOk.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				try {
					optionDialog.setVisible(false);
					optionDialog.dispose();
					start( txfDate);
				} catch (NoSuchElementException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}


			}});
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				optionDialog.setVisible(false);
				
				optionDialog.dispose();
				
			}});

		pnControl.add(butOk);

		pnControl.add(butCancel);

		Box box = Box.createVerticalBox();

		KSGPanel pn1 = new KSGPanel();

		pn1.setLayout(new FlowLayout(FlowLayout.LEADING));

		pn1.add(new JLabel("Sort 할 날짜를 입력 하세요"));

		box.add(pn1);

		box.add(pnInput);

		pnMain.add(box);

		optionDialog.getContentPane().add(pnMain);

		optionDialog.getContentPane().add(pnControl,BorderLayout.SOUTH);

		ViewUtil.center(optionDialog, true);

		optionDialog.setVisible(true);
	}
	
	private void start(JTextField txfDate) throws NoSuchElementException, ParseException
	{
		final String result=txfDate.getText();
		
		if(result==null) return;

		String datePattern = "\\d{4}.\\d{1,2}.\\d{1,2}";
		
		boolean retval = true;
		
		retval = result.matches(datePattern);

		if(!retval)
		{
			JOptionPane.showMessageDialog(null, "입력 형식이 틀렸습니다. "+result);
			return;
		}
		SimpleDateFormat fromDateformat = new SimpleDateFormat("yyyy.mm.ss");
		SimpleDateFormat optionformat = new SimpleDateFormat("yyyy/mm/ss");
		java.util.Date d = fromDateformat.parse(result);

		ShippersTable op = new ShippersTable();
		op.setDate_isusse(optionformat.format(d));
		new BuildXMLRouteScheduleCommand(op).execute();
		optionDialog.setVisible(false);
		optionDialog.dispose();
	}
	class BiggerMenu extends JMenu {
		BiggerMenu(String label) {
			super(label);
		}

		public BiggerMenu(String string, int vkO) {

			this(string);
			this.setMnemonic(vkO);
		}

		public Dimension getMinimumSize() {
			Dimension d =getPreferredSize(); 
			d.width+=20;
			return d;
		}

		public Dimension getMaximumSize() {
			Dimension d =getPreferredSize();
			d.width+=20;
			return d;
		}
	}
	public void update(KSGModelManager manager) {

		workprocess.setVisible(manager.isWorkMoniter);
		
		lblWorkProcessText.setVisible(manager.isWorkMoniter);
		
		workprocess.setIndeterminate(manager.isWorkMoniter);
		
		if(manager.isWorkMoniter)
		{
			lblWorkProcessText.setText(manager.workProcessText);
			
			workprocess.setString(manager.workProcessText);
		}

	}

	class BuildActionListenr implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Out/Inbound"))
			{
				try {

					cardLayout.show(KSGMainFrame.this.pnCenter, SCHEDULE_SEARCH);
					serviceManager.buildSchedule();
					modelManager.execute(pnSchedule.getName());
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
	}

	class SortActionListenr implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {

			if(e.getActionCommand().equals("Inbound"))
			{
				cardLayout.show(KSGMainFrame.this.pnCenter, SCHEDULE_SEARCH);

				scheduleCommand = new BuildXMLInboundCommand();

				scheduleCommand.execute();

			}
			else if(e.getActionCommand().equals("Outbound"))
			{

				cardLayout.show(KSGMainFrame.this.pnCenter, SCHEDULE_SEARCH);

				scheduleCommand = new BuildXMLOutboundCommand();

				scheduleCommand.execute();

			}
			else if(e.getActionCommand().equals("항로별"))
			{
				cardLayout.show(KSGMainFrame.this.pnCenter, SCHEDULE_SEARCH);

				optionDialog = new JDialog(KSGModelManager.getInstance().frame);

				optionDialog.setModal(true);

				optionDialog.setTitle("스케줄 생성");

				KSGPanel pnMain = new KSGPanel();

				KSGPanel pnInput = new KSGPanel();

				pnInput.setLayout( new FlowLayout(FlowLayout.LEADING));

				StringArrayLookAhead lookAhead = new StringArrayLookAhead(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
				
				txfDate = new LookAheadTextField("생성 날짜 입력",15,lookAhead);
				
				txfDate.setFocus_lost(false);

				JCheckBox cbxMondya = new JCheckBox(MONDAY);
				
				cbxMondya.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						JCheckBox bo =(JCheckBox) e.getSource();
						if(bo.isSelected())
						{
							txfDate.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
						}

					}});

				pnInput.add(txfDate);

				pnInput.add(cbxMondya);

				KSGPanel pnOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

				pnOption.setVisible(false);

				KSGPanel pnControl = new KSGPanel();

				JButton butOk = new JButton("확인");
				butOk.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {

						try {
							optionDialog.setVisible(false);
							optionDialog.dispose();
							start( txfDate);
						} catch (NoSuchElementException e1) {
							e1.printStackTrace();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}});
				JButton butCancel = new JButton("취소");
				butCancel.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {
						optionDialog.setVisible(false);
						optionDialog.dispose();
					}});
				pnControl.add(butOk);
				pnControl.add(butCancel);

				Box box = Box.createVerticalBox();
				KSGPanel pn1 = new KSGPanel();
				pn1.setLayout(new FlowLayout(FlowLayout.LEADING));
				pn1.add(new JLabel("Sort 할 날짜를 입력 하세요"));
				box.add(pn1);
				box.add(pnInput);
				pnMain.add(box);

				optionDialog.getContentPane().add(pnMain);
				optionDialog.getContentPane().add(pnControl,BorderLayout.SOUTH);
				ViewUtil.center(optionDialog, true);

				optionDialog.setVisible(true);
			}

			else if(e.getActionCommand().equals("Schedule Build"))
			{
				try {

					cardLayout.show(KSGMainFrame.this.pnCenter, SCHEDULE_SEARCH);
					serviceManager.buildSchedule();
					modelManager.execute(pnSchedule.getName());
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}
			}
		}
	}

	// 광고정보 관련 액션
	class AdvertisementActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {

			cardLayout.show(pnCenter,  e.getActionCommand());

			if(e.getActionCommand().equals(ADV_INPUT_SEARCH))
			{
				KSGDialog countDialog = new SearchADVCountDialog();

				countDialog.createAndUpdateUI();
			}
			else
			{
				pnMainView.showPanel(e.getActionCommand());
			}
		}
	}

	// 스케줄정보 관련 액션
	class ScheduleActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {

			pnMainView.showPanel(SCHEDULE_SEARCH);

			try {

				if(e.getActionCommand().equals(SCHEDULE_INBOUND))
				{
					CommandMap param = new CommandMap();
					
					callApi("schedule.inbound",param);
				}
				else if(e.getActionCommand().equals(SCHEDULE_OUTBOUND))
				{
					CommandMap param = new CommandMap();
					
					callApi("schedule.outbound",param);
				}
				else if(e.getActionCommand().equals(SCHEDULE_WORLDWIDE))
				{
					WorldWideDialog dialog = new WorldWideDialog();

					dialog.createAndUpdateUI();
				}

				else if(e.getActionCommand().equals("Schedule Build"))
				{
					CommandMap param = new CommandMap();
					
					callApi("schedule.build",param);

				}
				else if(e.getActionCommand().equals(SCHEDULE_DELETE))
				{
					callApi("schedule.delete");
				}				

				else if(e.getActionCommand().equals("일괄작업(Sort)"))
				{
					new Thread(){
						public void run()
						{
							JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "Sorting을 시작 합니다.");

							sort();
						}

					}.start();
				}
				else if(e.getActionCommand().equals("일괄작업(전송용)"))
				{
					serviceManager.buildWebSchedule();

				}
				else if(e.getActionCommand().equals("일괄작업(전송용New)"))
				{
					WebScheduleCreateDialog createDialog = new WebScheduleCreateDialog();

					createDialog.createAndUpdateUI(KSGMainFrame.this);
				}

			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage());
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("showMenu".equals(serviceId)) {

			String menuId=(String) result.get("menuId");

			cardLayout.show(pnCenter,  menuId);			
		}
		
		if("schedule.delete".equals(serviceId)) {

			int deleteCount=(int) result.get("deleteCount");
			
			JOptionPane.showMessageDialog(KSGMainFrame.this, deleteCount==0?"삭제 할 스케줄 정보가 없습니다.":String.format("%d건을 삭제 했습니다.", deleteCount));

		}
		else if("searchTableCount".equals(serviceId)) {

			String  date=(String) result.get("searchDate");

			int count=(int) result.get("count");

			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, date+" : "+count+"건");

			toolbar.requestFocus();
		}
	}

	@Override
	public void setModel(CommandMap model) {
		
		this. model = model;
	}
	public CommandMap getModel() {

		return model;
	}

	public void callApi(String serviceId, CommandMap param)
	{
		if(this.controller!=null)
			this.controller.call(serviceId, param, this);
	}

	public void callApi(String serviceId)
	{
		if(this.controller!=null)
			this.controller.call(serviceId, new CommandMap(),this);
	}
	
	class WorldWideDialog extends JDialog implements ActionListener
	{
		public WorldWideDialog() {}

		private Component buildControl()
		{
			KSGPanel pnControl = new KSGPanel();

			JButton butOk = new JButton("확인");

			butOk.addActionListener(this);
			JButton butCancel = new JButton("취소");
			butCancel.addActionListener(this);
			pnControl.add(butOk);
			pnControl.add(butCancel);
			return pnControl;
		}

		private Component buildCenter()
		{
			KSGPanel pnMain = new KSGPanel();

			KSGPanel pnInput = new KSGPanel();

			pnInput.setLayout( new FlowLayout(FlowLayout.LEADING));

			StringArrayLookAhead lookAhead = new StringArrayLookAhead(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));

			txfDate = new LookAheadTextField("생성 날짜 입력",15,lookAhead);

			txfDate.setFocus_lost(false);

			JCheckBox cbxMondya = new JCheckBox(MONDAY);

			cbxMondya.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					JCheckBox bo =(JCheckBox) e.getSource();
					if(bo.isSelected())
					{
						txfDate.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
					}

				}});

			pnInput.add(txfDate);

			pnInput.add(cbxMondya);

			KSGPanel pnOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

			pnOption.setVisible(false);

			Box box = Box.createVerticalBox();

			KSGPanel pn1 = new KSGPanel();

			pn1.setLayout(new FlowLayout(FlowLayout.LEADING));

			pn1.add(new JLabel("Sort 할 날짜를 입력 하세요"));

			box.add(pn1);

			box.add(pnInput);

			pnMain.add(box);

			return pnMain;
		}

		public void createAndUpdateUI()
		{
			setModal(true);

			setTitle("스케줄 생성");

			getContentPane().add(buildCenter());

			getContentPane().add(buildControl(),BorderLayout.SOUTH);

			ViewUtil.center(this, true);

			setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			if("확인".equals(command))
			{
				try {
					setVisible(false);
					dispose();
					start( txfDate);
				} catch (NoSuchElementException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			else if("취소".equals(command))
			{
				setVisible(false);
				dispose();
			}
		}
	}
}