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
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.service.ScheduleService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;


public class PnOutbound extends KSGPanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private KSGPageTablePanel tableH;
	
	private ScheduleService scheduleService;
	
	private List<HashMap<String, Object>> master;

	private JComboBox cbxNormalInOut;

	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JTextField txfNoramlSearch;

	
	
	public PnOutbound() {
		
		super();
		
		scheduleService = new ScheduleServiceImpl();
		
		this.setLayout(new BorderLayout());
		
		add(buildSearch(),BorderLayout.NORTH);
		
		add(buildCenter());
		
	
	}
	
	public KSGPanel buildCenter()
	{
		tableH = new KSGPageTablePanel("������ ���");
		
		
		tableH.addColumn(new KSGTableColumn("table_id", "���̺� ID",100));
		tableH.addColumn(new KSGTableColumn("company_abbr", "�����",100));
		tableH.addColumn(new KSGTableColumn("agent", "������Ʈ",100));
		tableH.addColumn(new KSGTableColumn("vessel", "���ڸ�",200));
		tableH.addColumn(new KSGTableColumn("date_issue", "�������",100));
		tableH.addColumn(new KSGTableColumn("voyage_num", "������ȣ"));
		tableH.addColumn(new KSGTableColumn("fromPort", "�����",200));
		tableH.addColumn(new KSGTableColumn("DateF", "�����", 90));
		tableH.addColumn(new KSGTableColumn("DateT", "������", 90));
		tableH.addColumn(new KSGTableColumn("port", "������",200));
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
		JButton butSearch = new JButton("�˻�");
		butSearch.addActionListener(this);
		//butSearch.setActionCommand("Normal �˻�");


		pnNormalSearchCenter.add(new JLabel("����:"));
		pnNormalSearchCenter.add(cbxNormalInOut);
		pnNormalSearchCenter.add(new JLabel("�׸�:"));
		pnNormalSearchCenter.add(cbxNormalSearch);
		pnNormalSearchCenter.add(txfNoramlSearch);
		pnNormalSearchCenter.add(butSearch);

		pnNormalSearchMain.add(pnNormalSearchCenter);
		
		return pnNormalSearchMain;
		
	}
	public void fnSearch()
	{
		HashMap<String, Object> param = new HashMap<String, Object>();
		
		try {
			param.put("inOutType", "O");
			
			int page_size = tableH.getPageSize();
			param.put("PAGE_SIZE", page_size);
			param.put("PAGE_NO", 1);
			
			logger.info("param:"+param);
			
			HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectListByPage(param);

			
			
			result.put("PAGE_NO", 1);
			
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

		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "error:"+e.getMessage());
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if(command.equals("�˻�"))
		{
			fnSearch();
		}
		
	}
	class PageAction implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			int page = tableH.getPage();
			
			int endPage = tableH.getTotalPage();

			int page_size = tableH.getPageSize();

			HashMap<String, Object> commandMap = new HashMap<String, Object>();
			commandMap.put("PAGE_SIZE", page_size);

			if (command.equals("Next")) {

				if (page < endPage) {
					commandMap.put("PAGE_NO", page + 1);

				} else {
					return;
				}

			} else if (command.equals("Forword")) {
				if (page > 1) {
					commandMap.put("PAGE_NO", page - 1);

				} else {
					return;
				}
			} else if (command.equals("GoPage")) {
				//				commandMap.put("PAGE_NO", page;
			}

			else if (command.equals("First")) {
				commandMap.put("PAGE_NO", 1);
			}

			else if (command.equals("Last")) {
				commandMap.put("PAGE_NO", endPage);
			}

			try {
//				commandMap.put("selectID", selectID);
				HashMap<String, Object> resultMap = (HashMap<String, Object>) scheduleService.selectListByPage(commandMap);
				resultMap.put("PAGE_NO", commandMap.get("PAGE_NO"));

				tableH.setResultData(resultMap);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		
	}
}
