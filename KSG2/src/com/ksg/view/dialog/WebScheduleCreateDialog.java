package com.ksg.view.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ksg.domain.ShippersTable;
import com.ksg.schedule.web.DefaultWebSchedule;
import com.ksg.schedule.web.DefaultWebScheduleV2;
import com.ksg.view.comp.LookAheadTextField;
import com.ksg.view.comp.StringArrayLookAhead;
import com.ksg.view.util.KSGDateUtil;

/**
 * @author archehyun
 *
 */
public class WebScheduleCreateDialog extends JDialog implements ActionListener{
	/**
	 * 
	 */
	/**
	 * 
	 */
	DefaultWebSchedule defaultWebSchedule;
	private static final long serialVersionUID = 1L;
	private JComboBox cbxWebType;
	private LookAheadTextField txfDateInput;
	private String inputDate;
	public WebScheduleCreateDialog() {
		this.setModal(true);
		
	}
	public void createAndUpdateUI(Component component)
	{
		this.setTitle("전송용 스케줄 생성");
		this.getContentPane().add(buildMain());
		this.getContentPane().add(buildContorl(),BorderLayout.SOUTH);
		this.pack();
		this.setLocationRelativeTo(component);
		this.setResizable(false);
		this.setVisible(true);
		
		
	}
	private JPanel buildContorl() {
		JPanel pnMain= new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnMain.setBorder(BorderFactory.createEtchedBorder());
		JButton pnOk = new JButton("생성");
		
		JButton pnCancel = new JButton("취소");
		pnOk.addActionListener(this);
		pnCancel.addActionListener(this);
		pnMain.add(pnOk);
		pnMain.add(pnCancel);
		return pnMain;
	}
	private JPanel buildMain() {
		JPanel pnMain = new JPanel();
		cbxWebType = new JComboBox();
		cbxWebType.addItem(ShippersTable.GUBUN_NORMAL);
		cbxWebType.addItem(ShippersTable.GUBUN_CONSOLE);
		cbxWebType.addItem(ShippersTable.GUBUN_INLAND);
		
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
		pnMain.add(new JLabel("스케줄 종류:"));
		pnMain.add(cbxWebType);
		pnMain.add(new JLabel("입력일자:"));
		pnMain.add(txfDateInput);
		
		return pnMain;
	}
	int format_type;
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("생성"))
		{
			String inputDate=txfDateInput.getText();
			format_type=DefaultWebSchedule.FORMAT_NOMAL;
			if(cbxWebType.getSelectedItem().equals(ShippersTable.GUBUN_NORMAL))
			{
				format_type = DefaultWebSchedule.FORMAT_NOMAL;
			}
			else if(cbxWebType.getSelectedItem().equals(ShippersTable.GUBUN_CONSOLE))
			{
				
				format_type = DefaultWebSchedule.FORMAT_CONSOLE;
			}
			else if(cbxWebType.getSelectedItem().equals(ShippersTable.GUBUN_INLAND))
			{
				
				format_type = DefaultWebSchedule.FORMAT_INLNAD;
			}
			
			try {
				final ShippersTable op = new ShippersTable();
				op.setDate_isusse(KSGDateUtil.toDate3(inputDate).toString());
				op.setGubun(cbxWebType.getSelectedItem().toString());
				
				
				this.setVisible(false);
				this.dispose();
				
				new Thread()
				{
					public void run()
					{
						try {
							defaultWebSchedule = new DefaultWebSchedule(format_type,op);
							defaultWebSchedule.execute();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
					}
				}.start();
				
				
				
			}  catch (NoSuchElementException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else if(command.equals("취소"))
		{
			this.setVisible(false);
			this.dispose();
		}
		
	}

}
