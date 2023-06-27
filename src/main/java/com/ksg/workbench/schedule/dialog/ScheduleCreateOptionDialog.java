package com.ksg.workbench.schedule.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.dtp.api.schedule.create.CreateNormalSchdeduleCommandNew;
import com.ksg.commands.IFCommand;
import com.ksg.commands.schedule.BuildWebSchdeduleCommand;
import com.ksg.commands.schedule.create.CreateInlandScheduleCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.ScheduleServiceManager;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.textfield.LookAheadTextField;
import com.ksg.view.comp.textfield.StringArrayLookAhead;


/**

  * @FileName : ScheduleCreateOptionDialog.java

  * @Project : KSG2

  * @Date : 2022. 11. 22. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� : ������ ������ �Է� ��¥ �� �ɼ� �� ���� ȭ��

  */
public class ScheduleCreateOptionDialog extends KSGDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private int scheduleType;

	public int getScheduleType() {
		return scheduleType;
	}
	private JRadioButton rdoPage;

	private JTextField txfOptionInput;

	public static final int NOMAL=0;

	public static final int WEB=1;

	private LookAheadTextField txfDateInput;

	private KSGPanel pnOption;

	private boolean isOption=false;

	private JComboBox cbxType;

	private String inputDate;

	private ScheduleServiceManager scheduleServiceManager = ScheduleServiceManager.getInstance();
	
	public int getGubun()
	{
		return cbxType.getSelectedIndex();
	}
	public boolean isOption() {
		return isOption;
	}
	public void setOption(boolean isOption) {
		this.isOption = isOption;
	}

	public ScheduleCreateOptionDialog(int scheduleType) {

		logger.info("������ ����");
		this.scheduleType =scheduleType;
	}
	public ScheduleCreateOptionDialog(int scheduleType, String inputDate) {
		this(scheduleType);
		this.inputDate = inputDate;
	}

	public void close()
	{
		setVisible(false);
		dispose();
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
	
	@Override
	public void createAndUpdateUI()
	{	
		setModal(true);
		setTitle("������ ����");
		KSGPanel pnMain = new KSGPanel();

		KSGPanel pnInput = new KSGPanel();
		pnInput.setLayout( new FlowLayout(FlowLayout.LEADING));

		StringArrayLookAhead lookAhead = new StringArrayLookAhead(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
		txfDateInput = new LookAheadTextField("���� ��¥ �Է�",8,lookAhead);
		txfDateInput.setFocus_lost(false);
		if(inputDate !=null)
			txfDateInput.setText(inputDate);

		JCheckBox cbxMondya = new JCheckBox("������");
		cbxMondya.setBackground(Color.white);
		
		cbxMondya.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				
				JCheckBox bo =(JCheckBox) e.getSource();
				
				if(bo.isSelected())
				{
					txfDateInput.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
				}
			}});

		cbxType = new JComboBox();			
		cbxType.addItem(ShippersTable.GUBUN_NORMAL);
		cbxType.addItem(ShippersTable.GUBUN_CONSOLE);
		cbxType.addItem(ShippersTable.GUBUN_INLAND);
		pnInput.add(new JLabel("������ ����: "));
		pnInput.add(cbxType);
		pnInput.add(new JLabel("���� ����: "));
		pnInput.add(txfDateInput);
		pnInput.add(cbxMondya);

		pnOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		pnOption.setVisible(false);

		rdoPage = new JRadioButton("������",true);

		JRadioButton rdoCompany = new JRadioButton("����");

		ButtonGroup group = new ButtonGroup();

		group.add(rdoPage);

		group.add(rdoCompany);

		pnOption.add(rdoPage);

		pnOption.add(rdoCompany);

		txfOptionInput = new JTextField(10);

		txfOptionInput.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					buildSchedule();						
				}
			}

		});
		pnOption.add(txfOptionInput);

		KSGPanel pnControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton butOk = new KSGGradientButton("Ȯ��(D)");

		butOk.setActionCommand("Ȯ��");

		butOk.setMnemonic(KeyEvent.VK_D);
		

		JButton butCancel = new KSGGradientButton("���");	

		JButton butOption = new KSGGradientButton("�ɼ�>>");

		butOption.setActionCommand("�ɼ�");		

		butOk.addActionListener(this);

		butCancel.addActionListener(this);

		butOption.addActionListener(this);

		pnControl.add(butOk);

		pnControl.add(butCancel);

		pnControl.add(butOption);

		Box box = Box.createVerticalBox();
		
		KSGPanel pn1 = new KSGPanel();
		
		pn1.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		box.setBorder(BorderFactory.createTitledBorder("�������� ������ ��¥�� �Է� �ϼ���"));

		box.add(pn1);
		box.add(pnInput);
		box.add(pnOption);
		pnMain.add(box);

		getContentPane().add(pnMain);
		getContentPane().add(pnControl,BorderLayout.SOUTH);

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(KSGModelManager.getInstance().frame);
		setVisible(true);

	}
	/**
	 * 
	 */
	private void buildSchedule()
	{
		final String inputDate=txfDateInput.getText();
		
		String optionData = txfOptionInput.getText();
		
		boolean isPage = rdoPage.isSelected();

		/*
		 * ��ȿ�� üũ
		 */
		if(inputDate==null) return;

		String datePattern = "\\d{4}.\\d{1,2}.\\d{1,2}";

		boolean retval = inputDate.matches(datePattern);

		if(!retval)
		{
			JOptionPane.showMessageDialog(this, "�Է� ������ Ʋ�Ƚ��ϴ�. "+inputDate);
			return;
		}

		if(isOption())
		{
			if(optionData.length()<0)
			{
				JOptionPane.showMessageDialog(this, "�׸��� �Է��Ͻʽÿ�");
				return;
			}	
		}
		close();

		scheduleServiceManager.startScheduleMake(isOption(),isPage, optionData, inputDate,(String)cbxType.getSelectedItem(),getScheduleType());

	}
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();
		if(command.equals("Ȯ��"))
		{
			buildSchedule();

		}else if(command.equals("���"))
		{
			this.close();
		}
		else if(command.equals("�ɼ�"))
		{
			JButton but=(JButton) e.getSource();

			if(this.isOption())
			{
				but.setText("�ɼ�>>");					
				this.setOption(false);

			}else
			{
				but.setText("�ɼ�<<");
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

}
