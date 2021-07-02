package com.ksg.common.view.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.ViewUtil;
import com.ksg.shippertable.service.TableService;
import com.ksg.view.comp.KSGDialog;

@SuppressWarnings("serial")
public class SearchADVCountDialog extends KSGDialog{

	private JLabel lblDate;
	private JTextField txfImportDate;
	private TableService _tableService;
	public SearchADVCountDialog() {
		this.setTitle("광고입력 조회");
		DAOManager manager = DAOManager.getInstance();
		_tableService = manager.createTableService();
	}
	public void createAndUpdateUI()
	{
		lblDate = new JLabel(" 입력날짜 : ");
		lblDate.setFont(KSGModelManager.getInstance().defaultFont);

		txfImportDate = new JTextField(8);
		JCheckBox cbxImportDate = new JCheckBox("월요일",false);
		cbxImportDate.setFont(KSGModelManager.getInstance().defaultFont);
		cbxImportDate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JCheckBox bo =(JCheckBox) e.getSource();
				if(bo.isSelected())
				{
					txfImportDate.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
				}
			}
		});

		JPanel pnMain = new JPanel();
		JPanel pnControl = new JPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOk = new JButton("확인");
		butOk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				String date=txfImportDate.getText();   

				if(!KSGDateUtil.isDashFomatt(date))
				{
					JOptionPane.showMessageDialog(null, "입력 형식(2000.1.1)이 틀렸습니다. "+date);
					return;
				}

				setVisible(false);
				dispose();

				try {
					int result=_tableService.getTableCount(KSGDateUtil.toDate3(date).toString());
					JOptionPane.showMessageDialog(null, date+" : "+result+"건");
				} catch (DateFormattException e1) {

					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}			
			}}
		);
		butOk.setFont(KSGModelManager.getInstance().defaultFont);
		JButton butCancel = new JButton("취소");
		butCancel.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
				dispose();
			}});
		butCancel.setFont(KSGModelManager.getInstance().defaultFont);
		pnControl.add(butOk);
		pnControl.add(butCancel);			
		pnMain.add(lblDate);
		pnMain.add(txfImportDate);
		pnMain.add(cbxImportDate);
		getContentPane().add(pnMain);
		getContentPane().add(pnControl,BorderLayout.SOUTH);
		setSize(250,100);
		ViewUtil.center(this);
		setVisible(true);
	}
}
