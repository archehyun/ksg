package com.ksg.schedule.view.comp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.service.ScheduleServiceLogic;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;

public class PnNormal extends JPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NormalScheduleTable 	_tblNormalScheduleList;

	private JLabel lblNormalCount;

	private JTextField txfNoramlSearch;
	
	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JComboBox cbxNormalInOut;
	
	private ScheduleServiceLogic scheduleService;
	
	private List<HashMap<String, Object>> master;
	
	KSGTablePanel tableH;
	

	
	@SuppressWarnings("rawtypes")
	public PnNormal() {
		
		this.setLayout(new BorderLayout());

		scheduleService = new ScheduleServiceLogic();
		JPanel pnNormalSearchMain = new JPanel(new BorderLayout());
		JPanel pnNormalSearchCenter = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cbxNormalInOut = new JComboBox();
		cbxNormalInOut.addItem("전체");
		cbxNormalInOut.addItem("Inbound");
		cbxNormalInOut.addItem("Outbound");


		cbxNormalSearch = new JComboBox<KSGTableColumn>();
		cbxNormalSearch.addItem(new KSGTableColumn("", "전체"));
		cbxNormalSearch.addItem(new KSGTableColumn("table_id", "테이블 ID"));
		cbxNormalSearch.addItem(new KSGTableColumn("company_abbr", "선사명"));
		cbxNormalSearch.addItem(new KSGTableColumn("agent", "에이전트"));
		cbxNormalSearch.addItem(new KSGTableColumn("vessel", "선박명"));
		cbxNormalSearch.addItem(new KSGTableColumn("voyage_num", "항차명"));
		cbxNormalSearch.addItem(new KSGTableColumn("fromPort", "출발항"));
		cbxNormalSearch.addItem(new KSGTableColumn("toPort", "도착항"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateF", "출발일"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateT", "도착일"));
		
		


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
		
		
		//	private String colums[] = {"","I/O","테이블 ID","선사명","에이전트","선박명","출력날짜","Voyage번호","출발항","DateF","DateT","도착항","구분","TS Port","TS Vessel","TS 일자","공동배선","지역코드"};
		tableH = new KSGTablePanel("스케줄 목록");
		
		tableH.addColumn(new KSGTableColumn("InOutType", "I/O"));
		tableH.addColumn(new KSGTableColumn("table_id", "테이블 ID"));
		tableH.addColumn(new KSGTableColumn("company_abbr", "선사명"));
		tableH.addColumn(new KSGTableColumn("agent", "에이전트"));
		tableH.addColumn(new KSGTableColumn("vessel", "선박명",200));
		tableH.addColumn(new KSGTableColumn("date_issue", "출력일자"));
		tableH.addColumn(new KSGTableColumn("voyage_num", "항차번호"));
		tableH.addColumn(new KSGTableColumn("fromPort", "출발항"));
		tableH.addColumn(new KSGTableColumn("DateF", "출발일"));
		tableH.addColumn(new KSGTableColumn("DateT", "도착일"));
		tableH.addColumn(new KSGTableColumn("port", "도착항"));
		tableH.addColumn(new KSGTableColumn("gubun", "구분"));
		
		
		tableH.initComp();
		
			
		_tblNormalScheduleList = new NormalScheduleTable();		

		add(pnNormalSearchMain,BorderLayout.NORTH);
		
		add(tableH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		try {
			
			switch (cbxNormalInOut.getSelectedIndex()) {
			case 1:
				param.put("inOutType", "I");	
				break;
			case 2:
				param.put("inOutType", "O");	
				break;
			default:
				break;
			}
			
			String searchOption  = txfNoramlSearch.getText();
			
			if(cbxNormalSearch.getSelectedIndex()>0) {
				
				KSGTableColumn item=(KSGTableColumn) cbxNormalSearch.getSelectedItem();
				param.put(item.columnField, searchOption);
				
				System.out.println("set");
			}
			
			
			HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectScheduleList(param);

			tableH.setResultData(result);

			master = (List) result.get("master");

			if(master.size()==0)
			{
				/*lblArea.setText("");
				lblAreaCode.setText("");
				lblPationality.setText("");
				lblPortName.setText("");
				tableD.clearReslult();*/
			}
			else
			{
				tableH.changeSelection(0,0,false,false);
			}

		} catch (SQLException ee) {
			// TODO Auto-generated catch block
			ee.printStackTrace();
		}
		
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


		
		/*switch (cbxNormalSearch.getSelectedIndex()) {
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
		}*/


		/*try {
			lblNormalCount.setText(String.valueOf(_tblNormalScheduleList.updateTable(data)));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
	}

}

