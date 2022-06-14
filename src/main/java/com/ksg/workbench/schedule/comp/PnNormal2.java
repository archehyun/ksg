package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;


/**

 * @FileName : PnOutbound.java

 * @Project : KSG2

 * @Date : 2022. 3. 7. 

 * @�ۼ��� : ��â��

 * @�����̷� :

 * @���α׷� ���� :

 */
public class PnNormal2 extends PnSchedule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private KSGTablePanel tableH;

	private List<HashMap<String, Object>> master;

	private KSGComboBox cbxNormalInOut;

	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JTextField txfNoramlSearch;

	private HashMap<String, Object> searchParam;

//	private PageAction pageAction;	

	public PnNormal2() {

		super();		
		
		
		
		this.setLayout(new BorderLayout());

		add(buildSearch(),BorderLayout.NORTH);

		add(buildCenter());

	}

	public KSGPanel buildCenter()
	{
		tableH = new KSGTablePanel("������ ���");		
		tableH.addColumn(new KSGTableColumn("gubun", "����"));
		tableH.addColumn(new KSGTableColumn("table_id", "���̺� ID",100));
		tableH.addColumn(new KSGTableColumn("company_abbr", "�����",150));
		tableH.addColumn(new KSGTableColumn("agent", "������Ʈ",150));
		tableH.addColumn(new KSGTableColumn("vessel", "���ڸ�",200));
		tableH.addColumn(new KSGTableColumn("date_issue", "�������",100));
		tableH.addColumn(new KSGTableColumn("voyage_num", "������ȣ"));
		tableH.addColumn(new KSGTableColumn("fromPort", "�����",200));
		tableH.addColumn(new KSGTableColumn("DateF", "�����", 90));
		tableH.addColumn(new KSGTableColumn("DateT", "������", 90));
		tableH.addColumn(new KSGTableColumn("port", "������",200));

		tableH.initComp();

//		tableH.setPageCountIndex(6);
//
//		pageAction = new PageAction(tableH,scheduleService);
//		
//		
//		
//		tableH.addActionListener(pageAction);

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
		cbxNormalInOut.setShowTotal(true);		
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
		try {
			searchParam = new HashMap<String, Object>();
			searchParam.put("gubun", "Normal");			

			String searchOption  = txfNoramlSearch.getText();

			if(cbxNormalInOut.getSelectedIndex()>0) {
				KSGTableColumn col = (KSGTableColumn)cbxNormalInOut.getSelectedItem();
				searchParam.put("inOutType", col.columnField);

			}

			if(cbxNormalSearch.getSelectedIndex()>0) {

				KSGTableColumn item=(KSGTableColumn) cbxNormalSearch.getSelectedItem();
				if(!searchOption.equals(""))
					searchParam.put(item.columnField, searchOption);
			}

			if(input_date!=null||!input_date.equals(""))

			{
				searchParam.put("date_issue", input_date);
			}
			searchParam.put("TABLE_NAME", "TB_SCHEDULE_INFO");

//			int page_size = tableH.getPageSize();
//
//			searchParam.put("PAGE_SIZE", page_size);

			searchParam.put("PAGE_NO", 1);

			logger.info("param:"+searchParam);

			HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectListMap(searchParam);			

			result.put("PAGE_NO", 1);

			tableH.setResultData(result);

			master = (List) result.get("master");

			if(master.size()==0)
			{

			}
			else
			{
				
//				pageAction.setSearchPram(searchParam);
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
