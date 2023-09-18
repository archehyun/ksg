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
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import com.dtp.api.control.ScheduleController;
import com.ksg.commands.schedule.route.RouteTaskNewVessel;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ScheduleType;
import com.ksg.domain.ShippersTable;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.checkbox.KSGCheckBox;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.radiobutton.KSGRadioButton;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.ui.ErrorLogManager;
import com.ksg.workbench.common.comp.AbstractMgtUI;
import com.ksg.workbench.common.comp.View;
import com.ksg.workbench.schedule.comp.PnConsole2;
import com.ksg.workbench.schedule.comp.PnInland2;
import com.ksg.workbench.schedule.comp.PnNormal2;
import com.ksg.workbench.schedule.comp.PnNormalByTree;

import mycomp.comp.MyTable;

/**

 * @FileName : ScheduleMgtUI.java

 * @Date : 2021. 4. 29. 

 * @�ۼ��� : ��â��

 * @�����̷� :

 * @���α׷� ���� : ������ ���� ��ȸ

 */
public class ScheduleMgtUI extends AbstractMgtUI implements ActionListener, ComponentListener, View {

	private static final String ACTION_CREATE = "������ ����";

	private static final String ACTION_DELETE = "������ ����";

	private ErrorLogManager errorLogManager = ErrorLogManager.getInstance();

	private static final long serialVersionUID = 1L;	

	private MyTable tblScheduleDateList;

	private JButton butDelete,butPrint;

	private JButton butBuild;

	private KSGPanel pnConsoleOption;

	private JRadioButton optPage;

	private JRadioButton optCFS;

	private KSGComboBox cbxTableDateList;

	private JTabbedPane tabPane;

	private KSGPanel pnNormalOption,pnOption;

	private JRadioButton optDate,optVessel;

	private CardLayout optionLayout;

	private KSGCheckBox cbxNew, cbxInboundSchedule, cbxOutboundSchedule, cbxRouteSchedule;

	private JComboBox cbxRouteLogic;

	private JCheckBox chxRouteLogic;

	private JCheckBox chxOutboundLogic;

	private JCheckBox chxInboundLogic;

	PrintAble printAble = new PrintAble();

	private JComboBox<String> cbxOutboundLogic;

	public ScheduleMgtUI() {

		this.setController(new ScheduleController());

		this.addComponentListener(this);

		this.title = "���������� ����";

		this.setName("������ Ȯ��");

		this.borderColor = new Color(255,100,100);

		createAndUpdateUI();

		callApi("scheduleViewUpdate");
	}

	/**
	 *@���� ȭ�� ���� �� ������Ʈ 
	 */
	public void createAndUpdateUI() 
	{
		initComp();

		this.setLayout(new BorderLayout(10,10));		

		this.add( buildLeftMenu(),BorderLayout.WEST);	

		this.add(buildCenter(),BorderLayout.CENTER);

		this.add(buildNorthPn(),BorderLayout.NORTH);
	}

	private void initComp() {
		
		butPrint = new JButton("���� ��� (P)");

		butPrint.setActionCommand("���� ���");

		butPrint.setMnemonic(KeyEvent.VK_P);

		butBuild = new JButton("������ ����(N)");

		butBuild.setMnemonic(KeyEvent.VK_N);

		butBuild.setActionCommand(ACTION_CREATE);		

		butDelete = new JButton("������ ����(D)");

		butDelete.setMnemonic(KeyEvent.VK_D);

		butDelete.setActionCommand(ACTION_DELETE);

		butPrint.addActionListener(new SchedulePrintAction());

		butBuild.addActionListener(this);	

		butDelete.addActionListener(this);

		cbxTableDateList = new KSGComboBox();

		cbxRouteSchedule 	= new KSGCheckBox("�׷κ�",true);

		cbxInboundSchedule 	= new KSGCheckBox("Inbound",true);

		cbxOutboundSchedule = new KSGCheckBox("Outbound",true);

		cbxRouteSchedule.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pnNormalRouteOption.setVisible(cbxRouteSchedule.isSelected());
				chxRouteLogic.setVisible(cbxRouteSchedule.isSelected());
			}
		});

		cbxOutboundSchedule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chxOutboundLogic.setVisible(cbxOutboundSchedule.isSelected());
			}
		});

		cbxInboundSchedule.addActionListener(printAble);

		cbxInboundSchedule.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				chxInboundLogic.setVisible(cbxInboundSchedule.isSelected());
			}
		});

		cbxRouteSchedule.addActionListener(printAble);

		cbxOutboundSchedule.addActionListener(printAble);

		cbxOutboundLogic = new JComboBox<String>();

		chxOutboundLogic = new JCheckBox("�ű�",true);

		chxOutboundLogic.setBackground(Color.white);

		cbxOutboundLogic.addItem("����");

		cbxOutboundLogic.addItem("�ű�");

		cbxRouteLogic = new JComboBox<String>();

		chxRouteLogic = new JCheckBox("�ű�",true);

		chxRouteLogic.setBackground(Color.white);

		cbxRouteLogic.addItem("����");

		cbxRouteLogic.addItem("�ű�");

		chxInboundLogic = new JCheckBox("�ű�",true);

		chxInboundLogic.setBackground(Color.white);

		butUpdate = new KSGGradientButton("����");

		butUpdate.addActionListener(this);
		
		optPage 		= new KSGRadioButton("Page",true);

		optCFS 			= new KSGRadioButton("CFS");

		ButtonGroup bg 	= new ButtonGroup();

		bg.add(optPage);

		bg.add(optCFS);
		
		optDate = new KSGRadioButton("��¥",true);

		optVessel = new KSGRadioButton("����");

		cbxNew = new KSGCheckBox("�ű� ���");		

		ButtonGroup bg2 = new ButtonGroup();

		bg2.add(optDate);
		
		bg2.add(optVessel);

	}

	private KSGPanel buildLeftMenu() {
		// ���� ��¥ ���====================
		KSGPanel pnLeftMenu = new KSGPanel(new BorderLayout(5,5));	


		// ���̺� ������Ʈ ���� �׸� ����======================		
		KSGPanel pnTableDateModel = new KSGPanel(new BorderLayout());

		KSGPanel pnTableDateModelSouth = new KSGPanel(new BorderLayout());

		KSGPanel pnTableDateModelSouthPadding = new KSGPanel();

		pnTableDateModelSouthPadding.setPreferredSize(new Dimension(15,15));

		pnTableDateModelSouth.add(pnTableDateModelSouthPadding,BorderLayout.SOUTH);

		KSGPanel pnDateMain = new KSGPanel(new BorderLayout());

		GridLayout butlayout = new GridLayout(0,1);

		butlayout.setVgap(5);

		KSGPanel pnDateButton = new KSGPanel(butlayout);
		

		pnDateButton.setPreferredSize(new Dimension(190,80));

		pnDateButton.add(butBuild);

		pnDateMain.add(pnDateButton,BorderLayout.SOUTH);
		

		pnTableDateModel.add(pnDateMain);

		pnTableDateModel.add(pnTableDateModelSouth,BorderLayout.SOUTH);		


		// ������ �������� ���===============================
		KSGPanel pnTblScheduleDateList = new KSGPanel(new BorderLayout(5,5));

		tblScheduleDateList=  new KSGAbstractTable();

		tblScheduleDateList.addColumn(new KSGTableColumn("gubun","����",110));

		tblScheduleDateList.addColumn(new KSGTableColumn("date_issue","��������",120));

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

		KSGPanel TblScheduleDateListNorth = new KSGPanel(new BorderLayout());

		TblScheduleDateListNorth.add(new JLabel("������ ���� ���"),BorderLayout.WEST);

		TblScheduleDateListNorth.add(butUpdate,BorderLayout.EAST);

		pnTblScheduleDateList.add(TblScheduleDateListNorth,BorderLayout.NORTH);

		pnTblScheduleDateList.add(new JScrollPane(tblScheduleDateList));

		tblScheduleDateList.getParent().setBackground(Color.white);


		pnTblScheduleDateList.add(createOptionPn(),BorderLayout.SOUTH);

		//=============================================

		// ��ư ���=============================================

		GridLayout gridLayout = butlayout;

		gridLayout.setVgap(5);

		KSGPanel pnLeftMenuButtonList = new KSGPanel(gridLayout);

		pnLeftMenuButtonList.setPreferredSize(new Dimension(250,150));

		pnLeftMenuButtonList.add(butDelete);

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

		return pnMain;
	}

	private KSGPanel createOptionPn()
	{
		// �ܼ� �ɼ�
		pnConsoleOption = new KSGPanel(new FlowLayout(FlowLayout.LEADING));

		pnConsoleOption.setBorder(BorderFactory.createTitledBorder("��� �׸�"));

		pnConsoleOption.add(new JLabel("�ܼ� ���:"));

		pnConsoleOption.add(optPage);

		pnConsoleOption.add(optCFS);		

		GridLayout layout = new GridLayout(2,1);

		layout.setVgap(5);


		// ��� �ɼ�

		pnNormalOption = new KSGPanel(layout);

		KSGPanel pnNormalSelectionOption = new KSGPanel(new GridLayout(3,1));

		pnNormalSelectionOption.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		pnNormalSelectionOption.setBorder(BorderFactory.createTitledBorder("������ ���� ����"));


		KSGPanel pnOutboundScheduleOption 	= new KSGPanel(new GridLayout(1,0));

		KSGPanel pnRouteScheduleOption 		= new KSGPanel(new GridLayout(1,0));

		KSGPanel pnInboundScheduleOption 	= new KSGPanel(new GridLayout(1,0));


		pnRouteScheduleOption.add(cbxRouteSchedule);

		pnRouteScheduleOption.add(chxRouteLogic);


		pnOutboundScheduleOption.add(cbxOutboundSchedule);

		pnOutboundScheduleOption.add(chxOutboundLogic);


		pnInboundScheduleOption.add(cbxInboundSchedule);

		pnInboundScheduleOption.add(chxInboundLogic);

		pnNormalSelectionOption.add(pnOutboundScheduleOption);

		pnNormalSelectionOption.add(pnInboundScheduleOption);

		pnNormalSelectionOption.add(pnRouteScheduleOption);
		

		pnNormalRouteOption = new KSGPanel(new FlowLayout());

		pnNormalRouteOption.setBorder(BorderFactory.createTitledBorder("�׷κ� ���� ����"));

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

		return pnOption;
	}

	private KSGPanel buildCenter() {

		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		tabPane = new JTabbedPane();

		pnNormal2 = new PnNormal2();

		pnConsole2 = new PnConsole2();

		pnInland2 = new PnInland2();

		pnNomalByTree = new PnNormalByTree();

		tabPane.add(pnNomalByTree, "TreeTable");

		tabPane.add(pnNormal2, "NORMAL");

		tabPane.add(pnConsole2, "CONSOLE");

		tabPane.add(pnInland2, "INLAND");

		pnMain.add(tabPane);

		return pnMain;
	}

	private KSGPanel pnNormalRouteOption;

	private PnNormal2 pnNormal2;

	private PnConsole2 pnConsole2;

	private PnInland2 pnInland2;

	private PnNormalByTree pnNomalByTree;

	private KSGGradientButton butUpdate;

	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();

		if(command.equals(ACTION_DELETE))
		{
			CommandMap param = new CommandMap();

			int row=tblScheduleDateList.getSelectedRow();

			if(row<0) return;
			// ����
			String gubun 		= (String) tblScheduleDateList.getValueAt(row, 0);

			//��������
			String date_isuss 	= (String) tblScheduleDateList.getValueAt(row, 1);

			param.put("gubun", gubun);

			param.put("date_isuss", date_isuss);

			callApi("deleteSchedule");
		}
		else if(command.equals("����"))
		{	
			callApi("scheduleViewUpdate");
		}
		else if(command.equals(ACTION_CREATE))
		{		
			callApi("createSchedule");
		}
	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("scheduleViewUpdate".equals(serviceId)) {

			List tableDatelist = (List) result.get("tableDatelist");

			tableDatelist.stream().forEach(scheduleDate -> cbxTableDateList.addItem(new KSGTableColumn((String)scheduleDate, (String)scheduleDate) ));

			List scheduleDateLists = (List) result.get("scheduleDateLists");

			cbxTableDateList.removeAllItems();

			tblScheduleDateList.setResultData(scheduleDateLists);
		}
		else if("deleteSchedule".equals(serviceId)) {

			int deleteCount = (int) result.get("deleteCount");

			JOptionPane.showMessageDialog(this, deleteCount+"���� ���� �߽��ϴ�.");

			callApi("scheduleViewUpdate");
		}

		else if("createSchedule".equals(serviceId)) {

		}
	}

	class PrintAble implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			// ��� ���õ��� �ʾҴٸ� ��Ȱ��ȭ
			butPrint.setEnabled(!(!cbxRouteSchedule.isSelected()&&!cbxOutboundSchedule.isSelected()&&!cbxInboundSchedule.isSelected()));
		}
	}
	
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
					String gubun 		= (String) tblScheduleDateList.getValueAt(selectedRow, 0);

					//������ ������
					String selectedDate = (String) tblScheduleDateList.getValueAt(selectedRow, 1);				

					ScheduleData op = new ScheduleData();

					op.setDate_issue(selectedDate);

					CommandMap param = new CommandMap();

					param.put("gubun", gubun);

					param.put("op", op);


					// �ܼ� ������ ����
					if(gubun.equals(ShippersTable.GUBUN_CONSOLE))
					{	
						op.setConsole_print_type(optPage.isSelected()?ScheduleType.CONSOLE_PAGE:ScheduleType.CONSOLE_CFS);						
					}

					else //normal: �ƿ��ٿ��, �ιٿ��, �׷κ�
					{
						param.put("date_issue", selectedDate);

						param.put("isPrintOutbound", cbxOutboundSchedule.isSelected());

						param.put("isPrintInbound", cbxInboundSchedule.isSelected());

						param.put("isPrintRoute", cbxRouteSchedule.isSelected());

						param.put("isNew", cbxNew.isSelected());

						param.put("isPrintNewRoute", chxRouteLogic.isSelected());

						param.put("isPrintNewOutbound", chxOutboundLogic.isSelected());

						param.put("isPrintNewInbound", chxInboundLogic.isSelected());

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
}
