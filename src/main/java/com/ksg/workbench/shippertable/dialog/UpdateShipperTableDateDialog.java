package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.DateFormattException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.service.ShipperTableService;
import com.ksg.service.impl.ShipperTableServiceImpl;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.shippertable.ShipperTableMgtUI2;

/**

  * @FileName : UpdateShipperTableDateDialog.java

  * @Project : KSG2

  * @Date : 2022. 3. 14. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� : ���̺� ��¥ ���� ����

  */
public class UpdateShipperTableDateDialog extends KSGDialog implements ActionListener{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel lblDate;
	
	private JTextField txfImportDate;	

	private List tableIDlist;
	
	private ShipperTableService service;
	
	JComponent parent;
	
	public UpdateShipperTableDateDialog(List list, JComponent parent)
	{
		this.tableIDlist = list;
		service = new ShipperTableServiceImpl();
		this.parent =parent; 
		setLocationRelativeTo(parent);
	}

	@Override
	public void createAndUpdateUI() {
				

		tableService = new TableServiceImpl();
		setModal(true);

		this.setTitle("��¥���� ����");

		lblDate = new JLabel(" �Է³�¥ : ");

		lblDate.setFont(KSGModelManager.getInstance().defaultFont);

		txfImportDate = new JTextField(8);

		JCheckBox cbxImportDate = new JCheckBox("������",false);

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

		KSGPanel pnMain = new KSGPanel();
		KSGPanel pnControl = new KSGPanel();
		pnControl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton butOk = new JButton("Ȯ��");
		butOk.addActionListener(this);
		butOk.setFont(KSGModelManager.getInstance().defaultFont);
		JButton butCancel = new JButton("���");
		butCancel.addActionListener(this);
		butCancel.setFont(KSGModelManager.getInstance().defaultFont);
		pnControl.add(butOk);
		pnControl.add(butCancel);			
		pnMain.add(lblDate);
		pnMain.add(txfImportDate);
		pnMain.add(cbxImportDate);
		getContentPane().add(pnMain);
		getContentPane().add(pnControl,BorderLayout.SOUTH);
		this.pack();
		
		setVisible(true);
		
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("���"))
		{				
			close();
		}
		else if(command.equals("Ȯ��"))
		{

			try {
				String date=txfImportDate.getText();				
				
				String inputDate = KSGDateUtil.toDate3(date).toString();

				service.updateTableDateByTableIDs(tableIDlist,inputDate);

				result=1;

				close();
				((ShipperTableMgtUI2)parent).fnSearch();
				

				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "��¥�� �����߽��ϴ�.");

			} catch (SQLException e1) {

				e1.printStackTrace();
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
			} catch (DateFormattException e2) {
				e2.printStackTrace();
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�Է� ����(2000.1.1)�� Ʋ�Ƚ��ϴ�. "+e2.getMessage());
			}
			catch(Exception e3)
			{
				e3.printStackTrace();
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e3.getMessage());
			}
		}
	}

}
