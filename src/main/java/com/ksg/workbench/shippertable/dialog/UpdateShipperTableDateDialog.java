package com.ksg.workbench.shippertable.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dtp.api.control.ShipperTableController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.ViewUtil;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.workbench.master.dialog.BaseInfoDialog;

/**

 * @FileName : UpdateShipperTableDateDialog.java

 * @Project : KSG2

 * @Date : 2022. 3. 14. 

 * @�ۼ��� : pch

 * @�����̷� :

 * @���α׷� ���� : ���̺� ��¥ ���� ����

 */
public class UpdateShipperTableDateDialog extends BaseInfoDialog implements ActionListener{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel lblDate;

	private JTextField txfImportDate;	

	private List tableIDList;

	private JComponent parent;

	public UpdateShipperTableDateDialog(List list, JComponent parent)
	{
		this.tableIDList = list;

		this.setController(new ShipperTableController());

		this.parent =parent;

		setLocationRelativeTo(parent);
	}

	@Override
	public void createAndUpdateUI() {

		setModal(true);

		this.setTitle("��¥���� ����");

		getContentPane().add(buildCenter());

		getContentPane().add(buildControl(),BorderLayout.SOUTH);
		
		ViewUtil.center(this, true);

		setVisible(true);
	}

	private KSGPanel buildCenter() {

		lblDate = new JLabel(" �Է³�¥ : ");

		lblDate.setFont(KSGModelManager.getInstance().defaultFont);

		txfImportDate = new JTextField(8);

		JCheckBox cbxImportDate = new JCheckBox("������",false);
		
		cbxImportDate.setBackground(Color.white);

		cbxImportDate.setFont(KSGModelManager.getInstance().defaultFont);

		cbxImportDate.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {

				JCheckBox bo =(JCheckBox) e.getSource();

				if(bo.isSelected()) txfImportDate.setText(KSGDateUtil.dashformat(KSGDateUtil.nextMonday(new Date())));
			}
		});

		KSGPanel pnMain = new KSGPanel();

		pnMain.add(lblDate);

		pnMain.add(txfImportDate);

		pnMain.add(cbxImportDate);

		return pnMain;
	}

	private KSGPanel buildControlPn() {

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

		return pnControl;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("���"))
		{				
			close();
		}
		else if(command.equals("����"))
		{
			CommandMap map = new CommandMap();
			
			try {

				String date_isusse 	= KSGDateUtil.toDate3(txfImportDate.getText()).toString();

				map.put("date_isusse", date_isusse);

				map.put("tableIDList", tableIDList);
					
			}
			catch(Exception e2)
			{
				
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�Է� ����(2000.1.1)�� Ʋ�Ƚ��ϴ�. "+e2.getMessage());
			}
			callApi("updateShipperTableDateDialog.updateDate", map);
		}
	}

	@Override
	public void updateView() {

		CommandMap resultMap= this.getModel();

		String serviceId=(String) resultMap.get("serviceId");

		if("updateShipperTableDateDialog.updateDate".equals(serviceId))
		{	
			int result=(int) resultMap.get("result");

			JOptionPane.showMessageDialog(UpdateShipperTableDateDialog.this, result+" �� �����߽��ϴ�.");
			
			result = KSGDialog.SUCCESS;

			close();
		}
	}

}
