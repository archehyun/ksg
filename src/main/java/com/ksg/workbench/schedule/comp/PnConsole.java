package com.ksg.workbench.schedule.comp;

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

/**
 * @author 박창현
 * 콘솔 스케줄 생성 정보 조회
 */
public class PnConsole extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblConsoleCount;
	private ConsoleScheduleTable _tblConsoleScheduleList;

	public PnConsole() {
		this.setLayout(new BorderLayout());

		JPanel pnConsoleSearchMain = new JPanel(new BorderLayout());
		JPanel pnConsoleSearchCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel pnConsoleSearchEast = new JPanel(new FlowLayout(FlowLayout.RIGHT));


		JComboBox cbxConsoleSearch = new JComboBox();
		cbxConsoleSearch.addItem("테이블 아이디");
		cbxConsoleSearch.addItem("기항지");
		JTextField txfConsoleSearch = new JTextField(5);
		JButton butConsoleSearch= new JButton("검색");
		butConsoleSearch.setActionCommand("Console 검색");
		butConsoleSearch.addActionListener(this);

		pnConsoleSearchCenter.add(new JLabel("항목:"));
		pnConsoleSearchCenter.add(cbxConsoleSearch);
		pnConsoleSearchCenter.add(txfConsoleSearch);
		pnConsoleSearchCenter.add(butConsoleSearch);

		pnConsoleSearchMain.add(pnConsoleSearchCenter);
		pnConsoleSearchMain.add(pnConsoleSearchEast,BorderLayout.EAST);

		lblConsoleCount = new JLabel("0");
		pnConsoleSearchEast.add(lblConsoleCount);
		pnConsoleSearchEast.add(new JLabel("건"));

		_tblConsoleScheduleList = new ConsoleScheduleTable();
		add(pnConsoleSearchMain,BorderLayout.NORTH);
		add(new JScrollPane(_tblConsoleScheduleList));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			ScheduleData data = new ScheduleData();
			data.setGubun(ShippersTable.GUBUN_CONSOLE);

			lblConsoleCount.setText(String.valueOf(_tblConsoleScheduleList.updateTable(data)));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
