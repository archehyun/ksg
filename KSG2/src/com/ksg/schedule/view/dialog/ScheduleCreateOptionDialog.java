package com.ksg.schedule.view.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;

import com.ksg.commands.schedule.BuildWebSchdeduleCommand;
import com.ksg.commands.schedule.create.CreateInlandScheduleCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.view.comp.LookAheadTextField;
import com.ksg.common.view.comp.StringArrayLookAhead;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.build.CreateNormalSchdeduleCommandNew;

public class ScheduleCreateOptionDialog extends JDialog implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Logger 			logger = Logger.getLogger(getClass());

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
	
	private JPanel pnOption;
	
	private boolean isOption=false;
	
	private JComboBox cbxType;
	
	private String inputDate;
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
		super(KSGModelManager.getInstance().frame);
		logger.info("스케줄 생성");
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
	public void createAndUpdateUI()
	{	
		setModal(true);
		setTitle("스케줄 생성");
		JPanel pnMain = new JPanel();

		JPanel pnInput = new JPanel();
		pnInput.setLayout( new FlowLayout(FlowLayout.LEADING));

		StringArrayLookAhead lookAhead = new StringArrayLookAhead(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
		txfDateInput = new LookAheadTextField("생성 날짜 입력",8,lookAhead);
		txfDateInput.setFocus_lost(false);
		if(inputDate !=null)
		txfDateInput.setText(inputDate);

		JCheckBox cbxMondya = new JCheckBox("월요일");
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
		pnInput.add(new JLabel("스케줄 구분: "));
		pnInput.add(cbxType);
		pnInput.add(new JLabel("생성 일자: "));
		pnInput.add(txfDateInput);
		pnInput.add(cbxMondya);

		pnOption = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnOption.setVisible(false);


		rdoPage = new JRadioButton("페이지",true);
		
		JRadioButton rdoCompany = new JRadioButton("선사");
		
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

		JPanel pnControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butOk = new JButton("확인(D)");
		
		butOk.setActionCommand("확인");
		
		butOk.setMnemonic(KeyEvent.VK_D);
		
		JButton butCancel = new JButton("취소");	
		
		JButton butOption = new JButton("옵션>>");
		
		butOption.setActionCommand("옵션");		
		
		butOk.addActionListener(this);
		
		butCancel.addActionListener(this);
		
		butOption.addActionListener(this);


		pnControl.add(butOk);
		
		pnControl.add(butCancel);
		
		pnControl.add(butOption);


		Box box = Box.createVerticalBox();
		JPanel pn1 = new JPanel();
		pn1.setLayout(new FlowLayout(FlowLayout.LEADING));
		box.setBorder(BorderFactory.createTitledBorder("스케줄을 생성할 날짜를 입력 하세요"));
		
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
	private void buildSchedule()
	{
		final String inputDate=txfDateInput.getText();
		String optionData = txfOptionInput.getText();
		boolean isPage = rdoPage.isSelected();

		startScheduleMake(isPage, optionData, inputDate,(String)cbxType.getSelectedItem());

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
	private void startScheduleMake(final boolean isPage,
			final String optionData, final String inputDate,final String gubun) {
		if(inputDate==null)
			return;

		String datePattern = "\\d{4}.\\d{1,2}.\\d{1,2}";
		boolean retval = true;
		retval = inputDate.matches(datePattern);

		if(!retval)
		{
			JOptionPane.showMessageDialog(this, "입력 형식이 틀렸습니다. "+inputDate);
			return;
		}

		if(isOption())
		{
			if(optionData.length()<0)
				JOptionPane.showMessageDialog(this, "항목을 입력하십시요");
		}
		close();

		new Thread(){
			public void run()
			{
				try 
				{
					ShippersTable op = new ShippersTable();
					op.setDate_isusse(KSGDateUtil.toDate3(inputDate).toString());
					if(isOption())
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
					ScheduleManager.getInstance().init();
					switch (getScheduleType()) {
					case NOMAL:
						if(op.getGubun().equals(ShippersTable.GUBUN_INLAND))
						{
							CreateInlandScheduleCommand inlandcommand = new CreateInlandScheduleCommand(op);
							inlandcommand.execute();
						}else
						{
							CreateNormalSchdeduleCommandNew nomalCommand = new CreateNormalSchdeduleCommandNew(op);
							nomalCommand.execute();	
						}
						break;
					case WEB:
						ScheduleBuildMessageDialog di = new ScheduleBuildMessageDialog ();
						BuildWebSchdeduleCommand command = new BuildWebSchdeduleCommand(di,op);
						command.execute();
						break;
					default:
						break;
					}

				}
				catch (DateFormattException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
					return ;
				}catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
					return ;
				}
				catch (Exception e1) 
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
					return ;
				}

				close();
				
			}
		}.start();
	}

}
