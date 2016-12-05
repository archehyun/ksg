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
		cbxNormalInOut.addItem("전체");
		cbxNormalInOut.addItem("Inbound");
		cbxNormalInOut.addItem("Outbound");


		cbxNormalSearch = new JComboBox();
		cbxNormalSearch.addItem("전체");
		cbxNormalSearch.addItem("테이블 아이디");
		cbxNormalSearch.addItem("선사명");
		cbxNormalSearch.addItem("에이전트");
		cbxNormalSearch.addItem("선박명");
		cbxNormalSearch.addItem("Voyage번호");
		cbxNormalSearch.addItem("출발항");
		cbxNormalSearch.addItem("출발일");
		cbxNormalSearch.addItem("도착일");
		cbxNormalSearch.addItem("도착항");


		txfNoramlSearch = new JTextField(15);
		JButton butNormalSearch = new JButton("검색");
		butNormalSearch.addActionListener(this);
		butNormalSearch.setActionCommand("Normal 검색");


		pnNormalSearchCenter.add(new JLabel("구분:"));
		pnNormalSearchCenter.add(cbxNormalInOut);
		pnNormalSearchCenter.add(new JLabel("항목:"));
		pnNormalSearchCenter.add(cbxNormalSearch);
		pnNormalSearchCenter.add(txfNoramlSearch);
		pnNormalSearchCenter.add(butNormalSearch);



		JPanel pnNomalSearchEast =new JPanel(new FlowLayout(FlowLayout.RIGHT));
		lblNormalCount = new JLabel();
		lblNormalCount.setText("0");
		pnNomalSearchEast.add(lblNormalCount);
		pnNomalSearchEast.add(new JLabel("건"));


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
		// case 0: 전체
		case 1: // 테이블 아이디
			data.setTable_id(searchOption);	
			break;
		case 2: // 선사명
			data.setCompany_abbr(searchOption);	
			break;					
		case 3: // 에이전트
			data.setAgent(searchOption);	
			break;
		case 4: // 선박 명
			data.setVessel(searchOption);	
			break;					
		case 5: // Voyage번호
			data.setVoyage_num(searchOption);	
			break;
		case 6: // 출발항
			data.setFromPort(searchOption);	
			break;
		case 7: // 출발일
			data.setDateF(searchOption);	
			break;
		case 8: // 도착일
			data.setDateT(searchOption);	
			break;
		case 9: // 도착항
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
