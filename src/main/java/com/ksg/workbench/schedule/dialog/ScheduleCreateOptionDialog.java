package com.ksg.workbench.schedule.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.dtp.api.control.ScheduleController;
import com.dtp.api.schedule.create.CreateNormalSchdeduleCommandNew;
import com.ksg.commands.IFCommand;
import com.ksg.commands.schedule.BuildWebSchdeduleCommand;
import com.ksg.commands.schedule.create.CreateInlandScheduleCommand;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.ScheduleServiceManager;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.notification.Notification;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.textfield.StringArrayLookAhead;
import com.ksg.workbench.common.dialog.MainTypeDialog;


/**

  * @FileName : ScheduleCreateOptionDialog.java

  * @Project : KSG2

  * @Date : 2022. 11. 22. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 스케줄 생성시 입력 날짜 및 옵셥 값 설정 화면

  */
public class ScheduleCreateOptionDialog extends MainTypeDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private int scheduleType;


	private JRadioButton rdoPage;

	private JTextField txfOptionInput;

	public static final int NOMAL=0;

	public static final int WEB=1;

	private KSGPanel pnOption;

	private boolean isOption=false;

	private KSGComboBox cbxGubun;
	
	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");


	private ScheduleServiceManager scheduleServiceManager = ScheduleServiceManager.getInstance();

	private JRadioButton rdoCompany;

	private JCheckBox cbxMondya;
	
	private JCheckBox cbxOption;
	
	private KSGComboBox cbxTableDateList;
	
	public boolean isOption() {
		return isOption;
	}
	public void setOption(boolean isOption) {
		this.isOption = isOption;
	}

	public ScheduleCreateOptionDialog(int scheduleType) {

		logger.info("스케줄 생성");
		
		this.titleInfo = "스케줄 생성";
		
		this.setController(new ScheduleController());
		
		this.scheduleType =scheduleType;
		
		this.addComponentListener(this);
		
		initComp();
	}
	
	
	public int getScheduleType() {
		return scheduleType;
	}
	
	protected JRootPane createRootPane() {
		
		JRootPane rootPane = new JRootPane();
		
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		
		Action actionListener = new AbstractAction() {
			
			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		};
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		inputMap.put(stroke, "ESCAPE");
		
		rootPane.getActionMap().put("ESCAPE", actionListener);
		

		return rootPane;
	}
	
	private void initComp()
	{
		StringArrayLookAhead lookAhead = new StringArrayLookAhead(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
		

		
		cbxGubun = new KSGComboBox("tableType");
		
		cbxGubun.initComp();

		cbxGubun.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				fnSelectTableDate((String) cbxGubun.getSelectedItem());
				
			}});
		
		rdoPage = new JRadioButton("페이지",true);
		
		rdoPage.setBackground(Color.white);

		rdoCompany = new JRadioButton("선사");
		
		rdoCompany.setBackground(Color.white);

		ButtonGroup group = new ButtonGroup();

		group.add(rdoPage);

		group.add(rdoCompany);
		
		txfOptionInput = new JTextField(10);

		txfOptionInput.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					buildSchedule();						
				}
			}

		});
		
		cbxMondya = new JCheckBox("월요일");
		
		cbxMondya.setBackground(Color.white);
		
		cbxMondya.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				JCheckBox bo =(JCheckBox) e.getSource();
				
				if(bo.isSelected())
				{
					int count =cbxTableDateList.getItemCount();
					
					String monday =format.format(KSGDateUtil.nextMonday(new Date()));
					
					for(int i=0;i<count;i++)
					{
						KSGTableColumn col = cbxTableDateList.getItemAt(i);
						
						if(col.columnField.equals(monday))
						{	
							cbxTableDateList.setSelectedIndex(i);
							return;
						}
					}
					bo.setSelected(false);
					NotificationManager.showNotification(Notification.Type.WARNING, String.format("월요일(%s) 정보가 없습니다.", monday) );
				}
			}});
		
		cbxTableDateList = new KSGComboBox();
		
		cbxTableDateList.setPreferredSize(new Dimension(100, 25));
		
		cbxOption = new JCheckBox("기타",false);		
		
		cbxOption.setBackground(Color.white);
		
		cbxOption.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
			boolean selected =cbxOption.isSelected();
			
			rdoCompany.setVisible(selected);
			
			rdoPage.setVisible(selected);
			
			txfOptionInput.setVisible(selected);
			
			setOption(selected);
				
			}
		});
	}
	
	private KSGPanel buildCenter()
	{
		KSGPanel pnMain = new KSGPanel();
		
		KSGPanel pnGubun = new KSGPanel();
		
		pnGubun.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		pnGubun.add(new JLabel("스케줄 구분: "));
		
		pnGubun.add(cbxGubun);
		
		KSGPanel pnInputDate = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnInputDate.add(new JLabel("생성 일자: "));
		
		pnInputDate.add(cbxTableDateList);
		
		pnInputDate.add(cbxMondya);
		
		pnGubun.add(pnInputDate);
		
//		pnGubun.add(cbxOption);
		
//		pnInputDate.add(txfDateInput);
//		


		this.pnOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
//		pnOption.setVisible(false);

		this.pnOption.add(rdoPage);

		this.pnOption.add(rdoCompany);
		
		this.pnOption.add(txfOptionInput);
		
		this.pnOption.setVisible(false);
		
		Box box = Box.createVerticalBox();
		
//		box.setBorder(BorderFactory.createTitledBorder("스케줄 생성 정보"));
		
		box.add(pnGubun);
		
//		box.add(pnInputDate);
		
		box.add(pnOption);
		
		pnMain.add(box);
		
		return pnMain;
	}
	
	public KSGPanel buildControl()
	{
		KSGPanel pnControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton butOk = new KSGGradientButton("확인(D)");

		butOk.setActionCommand("확인");

		butOk.setMnemonic(KeyEvent.VK_D);

		JButton butCancel = new KSGGradientButton("취소");	

		JButton butOption = new KSGGradientButton("옵션>>");

		butOption.setActionCommand("옵션");		

		butOk.addActionListener(this);

		butCancel.addActionListener(this);

		butOption.addActionListener(this);

		pnControl.add(butOk);

		pnControl.add(butCancel);

		pnControl.add(butOption);
		
		return pnControl;
	}
	
	private void fnSelectTableDate(String gubun)
	{
		CommandMap param = new CommandMap();
		
		param.put("gubun", gubun);
		
		callApi("scheduleCreateOptionDialog.fnSelectTableDate",param);
	}
	
	@Override
	public void createAndUpdateUI()
	{	
		this.setModal(true);

		this.getContentPane().add(buildHeader(titleInfo),BorderLayout.NORTH);

		this.addComp(buildCenter(),BorderLayout.CENTER);

		this.addComp(buildControl(),BorderLayout.SOUTH);
		
//		this.setSize(400,300);

		ViewUtil.center(this, true);

		this.setResizable(false);

		this.setVisible(true);
	}
	
	/**
	 * 
	 */
	private void buildSchedule()
	{
		String optionData = txfOptionInput.getText();
		
		boolean isPage = rdoPage.isSelected();
		
		if(isOption())
		{
			if(optionData.isEmpty())
			{
				NotificationManager.showNotification(Notification.Type.WARNING, "항목을 입력하십시요");
				return;
			}	
		}
		
		KSGTableColumn  col = (KSGTableColumn) cbxTableDateList.getSelectedItem();
		
		String inputDate =col.columnField;
		
		close();

		KSGTableColumn typecol= (KSGTableColumn) cbxGubun.getSelectedItem();
		
		scheduleServiceManager.startScheduleMake(isOption(),isPage, optionData, inputDate,typecol.columnName ,getScheduleType());
	}
	
	@Override
	public void componentShown(ComponentEvent e) {
		
//		if(inputDate !=null) txfDateInput.setText(inputDate);
		
		fnSelectTableDate(ShippersTable.GUBUN_NORMAL);
		
	}
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		
		if(command.equals("확인"))
		{
			buildSchedule();

		}else if(command.equals("취소"))
		{
			this.close();
		}
		else if(command.equals("옵션"))
		{
			JButton but=(JButton) e.getSource();

			if(this.isOption())
			{
				but.setText("옵션>>");					
				this.setOption(false);

			}else
			{
				but.setText("옵션<<");
				this.setOption(true);	

			}
			pnOption.setVisible(this.isOption());
			this.pack();
		}

	}
	/**
	 * @param isOption
	 * @param isPage
	 * @param optionData
	 * @param inputDate
	 * @param gubun
	 * @param scheduleType
	 */
	private void startScheduleMake(final boolean isOption, final boolean isPage,
			final String optionData, final String inputDate,final String gubun, final int scheduleType) {

		new Thread(){
			public void run()
			{
				try 
				{
					ShippersTable op = new ShippersTable();

					op.setDate_isusse(KSGDateUtil.toDate3(inputDate).toString());

					if(isOption)
					{
						if(isPage)
						{
							op.setPage(Integer.parseInt(optionData));
						}else
						{
							op.setCompany_abbr(optionData);
						}
					}

					op.setGubun(gubun);

					ScheduleManager.getInstance().initMasterData();

					IFCommand command=null;

					switch (scheduleType) {
					case NOMAL:
						if(op.getGubun().equals(ShippersTable.GUBUN_INLAND))
						{
							command = new CreateInlandScheduleCommand(op);
						}else
						{
							command = new CreateNormalSchdeduleCommandNew(op);								
						}
						break;
					case WEB:
						ScheduleBuildMessageDialog di = new ScheduleBuildMessageDialog ();
						command = new BuildWebSchdeduleCommand(di,op);

						break;
					default:
						break;
					}
					command.execute();

				}
				catch (DateFormattException e1) {
					
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
					return ;
				}catch (NumberFormatException e1) {
					
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
					return ;
				}
				catch (Exception e1) 
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
					return ;
				}
			}
		}.start();
	}
	
	@Override
	public void updateView() {
		CommandMap resultMap= this.getModel();

		String serviceId=(String) resultMap.get("serviceId");

		if("scheduleCreateOptionDialog.fnSelectTableDate".equals(serviceId))
		{	
			List tableDatelist = (List) resultMap.get("tableDatelist");
			
			cbxTableDateList.removeAllItems();

			tableDatelist.stream().forEach(scheduleDate -> cbxTableDateList.addItem(new KSGTableColumn((String)scheduleDate, (String)scheduleDate) ));
		}
	}

}
