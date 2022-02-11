package com.ksg.workbench.schedule.comp;

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
import javax.swing.JTextField;

import com.ksg.service.ScheduleService;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;

public class PnOutbound extends KSGPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private KSGTablePanel tableH;
	
	private ScheduleService scheduleService;
	
	private List<HashMap<String, Object>> master;

	private JComboBox cbxNormalInOut;

	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JTextField txfNoramlSearch;

	private JLabel lblNormalCount;
	
	public PnOutbound() {
		
		
		
		this.setLayout(new BorderLayout());
		
		add(buildSearch(),BorderLayout.NORTH);
		
		add(buildCenter());
		
	
	}
	
	public KSGPanel buildCenter()
	{
		tableH = new KSGTablePanel("������ ���");
		
		tableH.addColumn(new KSGTableColumn("InOutType", "I/O"));
		tableH.addColumn(new KSGTableColumn("table_id", "���̺� ID"));
		tableH.addColumn(new KSGTableColumn("company_abbr", "�����"));
		tableH.addColumn(new KSGTableColumn("agent", "������Ʈ"));
		tableH.addColumn(new KSGTableColumn("vessel", "���ڸ�",200));
		tableH.addColumn(new KSGTableColumn("date_issue", "�������",100));
		tableH.addColumn(new KSGTableColumn("voyage_num", "������ȣ"));
		tableH.addColumn(new KSGTableColumn("fromPort", "�����"));
		tableH.addColumn(new KSGTableColumn("DateF", "�����", 90));
		tableH.addColumn(new KSGTableColumn("DateT", "������", 90));
		tableH.addColumn(new KSGTableColumn("port", "������"));
		tableH.addColumn(new KSGTableColumn("gubun", "����"));
		
		
		tableH.initComp();
		return tableH;
	}
	
	
	
	public KSGPanel buildSearch()
	{
		KSGPanel pnNormalSearchMain = new KSGPanel(new BorderLayout());
		KSGPanel pnNormalSearchCenter = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		cbxNormalInOut = new JComboBox();
		cbxNormalInOut.addItem("��ü");
		cbxNormalInOut.addItem("Inbound");
		cbxNormalInOut.addItem("Outbound");


		cbxNormalSearch = new JComboBox<KSGTableColumn>();
		cbxNormalSearch.addItem(new KSGTableColumn("", "��ü"));
		cbxNormalSearch.addItem(new KSGTableColumn("table_id", "���̺� ID"));
		cbxNormalSearch.addItem(new KSGTableColumn("company_abbr", "�����"));
		cbxNormalSearch.addItem(new KSGTableColumn("agent", "������Ʈ"));
		cbxNormalSearch.addItem(new KSGTableColumn("vessel", "���ڸ�"));
		cbxNormalSearch.addItem(new KSGTableColumn("voyage_num", "������"));
		cbxNormalSearch.addItem(new KSGTableColumn("n_voyage_num", "������ȣ"));
		cbxNormalSearch.addItem(new KSGTableColumn("fromPort", "�����"));
		cbxNormalSearch.addItem(new KSGTableColumn("toPort", "������"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateF", "�����"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateT", "������"));
		
		


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



		KSGPanel pnNomalSearchEast =new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		lblNormalCount = new JLabel();
		lblNormalCount.setText("0");
		pnNomalSearchEast.add(lblNormalCount);
		pnNomalSearchEast.add(new JLabel("��"));


		pnNormalSearchMain.add(pnNormalSearchCenter);
		pnNormalSearchMain.add(pnNomalSearchEast,BorderLayout.EAST);
		return pnNormalSearchMain;
		
	}
	public void fnSearch()
	{
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		try {
			param.put("InOutType", "O");
			logger.info("param:"+param);
			
			HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectList(param);

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

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(e.equals("�˻�"))
		{
			fnSearch();
		}
		
	}
}
