package com.ksg.view.schedule;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.view.ui.NormalScheduleTable;

public class PnNormal extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NormalScheduleTable 	_tblNormalScheduleList;

	private JLabel lblNormalCount;

	private JTextField txfNoramlSearch;
	
	private JComboBox cbxNormalSearch;

	private JComboBox cbxNormalInOut;
	
	public PnNormal() {
		
		this.setLayout(new BorderLayout());


		JPanel pnNormalSearchMain = new JPanel(new BorderLayout());
		JPanel pnNormalSearchCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cbxNormalInOut = new JComboBox();
		cbxNormalInOut.addItem("��ü");
		cbxNormalInOut.addItem("Inbound");
		cbxNormalInOut.addItem("Outbound");


		cbxNormalSearch = new JComboBox();
		cbxNormalSearch.addItem("��ü");
		cbxNormalSearch.addItem("���̺� ���̵�");
		cbxNormalSearch.addItem("�����");
		cbxNormalSearch.addItem("������Ʈ");
		cbxNormalSearch.addItem("���ڸ�");
		cbxNormalSearch.addItem("Voyage��ȣ");
		cbxNormalSearch.addItem("�����");
		cbxNormalSearch.addItem("�����");
		cbxNormalSearch.addItem("������");
		cbxNormalSearch.addItem("������");


		txfNoramlSearch = new JTextField(15);
		JButton butNormalSearch = new JButton("�˻�");
		butNormalSearch.addActionListener(this);
		butNormalSearch.setActionCommand("Normal �˻�");


		pnNormalSearchCenter.add(new JLabel("����:"));
		pnNormalSearchCenter.add(cbxNormalInOut);
		pnNormalSearchCenter.add(new JLabel("�׸�:"));
		pnNormalSearchCenter.add(cbxNormalSearch);
		pnNormalSearchCenter.add(txfNoramlSearch);
		pnNormalSearchCenter.add(butNormalSearch);



		JPanel pnNomalSearchEast =new JPanel(new FlowLayout(FlowLayout.RIGHT));
		lblNormalCount = new JLabel();
		lblNormalCount.setText("0");
		pnNomalSearchEast.add(lblNormalCount);
		pnNomalSearchEast.add(new JLabel("��"));


		pnNormalSearchMain.add(pnNormalSearchCenter);
		pnNormalSearchMain.add(pnNomalSearchEast,BorderLayout.EAST);

		_tblNormalScheduleList = new NormalScheduleTable();		


		add(pnNormalSearchMain,BorderLayout.NORTH);
		add(new JScrollPane(_tblNormalScheduleList));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ScheduleData data = new ScheduleData();
		data.setGubun(ShippersTable.GUBUN_NORMAL);

		switch (cbxNormalInOut.getSelectedIndex()) {
		case 1:
			data.setInOutType("I");	
			break;
		case 2:
			data.setInOutType("O");	
			break;
		default:
			break;
		}


		String searchOption  = txfNoramlSearch.getText();
		switch (cbxNormalSearch.getSelectedIndex()) {
		// case 0: ��ü
		case 1: // ���̺� ���̵�
			data.setTable_id(searchOption);	
			break;
		case 2: // �����
			data.setCompany_abbr(searchOption);	
			break;					
		case 3: // ������Ʈ
			data.setAgent(searchOption);	
			break;
		case 4: // ���� ��
			data.setVessel(searchOption);	
			break;					
		case 5: // Voyage��ȣ
			data.setVoyage_num(searchOption);	
			break;
		case 6: // �����
			data.setFromPort(searchOption);	
			break;
		case 7: // �����
			data.setDateF(searchOption);	
			break;
		case 8: // ������
			data.setDateT(searchOption);	
			break;
		case 9: // ������
			data.setPort(searchOption);	
			break;					
		default:
			break;
		}


		try {
			lblNormalCount.setText(String.valueOf(_tblNormalScheduleList.updateTable(data)));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
