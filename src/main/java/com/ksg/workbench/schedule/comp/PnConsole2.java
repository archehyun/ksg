package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.service.ScheduleService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.view.comp.KSGAutoComboBox;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.KSGPageTablePanel;
import com.ksg.workbench.common.comp.button.PageAction;


/**

  * @FileName : PnOutbound.java

  * @Project : KSG2

  * @Date : 2022. 3. 7. 

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� :

  */
public class PnConsole2 extends PnSchedule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private KSGPageTablePanel tableH;	
	
	private List<HashMap<String, Object>> master;

	private KSGComboBox cbxNormalInOut;

	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JTextField txfNoramlSearch;	
	
	public PnConsole2() {
		
		super();		
		
		this.setLayout(new BorderLayout());
		
		add(buildSearch(),BorderLayout.NORTH);
		
		add(buildCenter());
		
	
	}
	
	public KSGPanel buildCenter()
	{
		tableH = new KSGPageTablePanel("������ ���");
		
		
		tableH.addColumn(new KSGTableColumn("gubun", "����"));
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
		tableH.addColumn(new KSGTableColumn("area_code", "�����ڵ�",200));
		tableH.addColumn(new KSGTableColumn("console_cfs", "CFS",200));
		tableH.addColumn(new KSGTableColumn("consore_page", "Consoe Page",200));
		tableH.addColumn(new KSGTableColumn("c_time", "C Time",200));
		tableH.addColumn(new KSGTableColumn("d_time", "D Time",200));
		
		tableH.initComp();
		
		tableH.setPageCountIndex(6);
		
		tableH.addActionListener(new PageAction(tableH,scheduleService));
		
		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));
		pnMain.add(tableH);
		
		return pnMain;
	}
	
	
	
	public KSGPanel buildSearch()
	{
		KSGPanel pnNormalSearchMain = new KSGPanel(new BorderLayout());
		KSGPanel pnNormalSearchCenter = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		cbxNormalInOut = new KSGComboBox("inOutType");
		cbxNormalInOut.initComp();

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
			param.put("gubun", "console");
			
			
			String searchOption  = txfNoramlSearch.getText();
			
			if(cbxNormalInOut.getSelectedIndex()>0) {
				param.put("inOutType", cbxNormalInOut.getSelectedItem());
				
			}
			
			if(cbxNormalSearch.getSelectedIndex()>0) {
				
				KSGTableColumn item=(KSGTableColumn) cbxNormalSearch.getSelectedItem();
				if(!searchOption.equals(""))
				param.put(item.columnField, searchOption);
			}
			
			if(input_date!=null||!input_date.equals(""))
				
			{
				param.put("date_issue", input_date);
			}
			
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

}
