package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.ksg.common.model.CommandMap;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.button.PageAction;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;


/**

 * @FileName : PnOutbound.java

 * @Project : KSG2

 * @Date : 2022. 3. 7. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
public class PnConsole2 extends PnSchedule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private KSGTablePanel tableH;	

	private List<HashMap<String, Object>> master;

	private KSGComboBox cbxNormalInOut;

	private KSGComboBox cbxNormalSearch;

	private JTextField txfNoramlSearch;

	private CommandMap searchParam;

	private PageAction pageAction;	

	public PnConsole2() {

		super();

		this.setLayout(new BorderLayout());

		add(buildSearch(),BorderLayout.NORTH);

		add(buildCenter());

	}

	public KSGPanel buildCenter()
	{
		tableH = new KSGTablePanel("스케줄 목록");


		tableH.addColumn(new KSGTableColumn("gubun", "구분"));
		tableH.addColumn(new KSGTableColumn("table_id", "테이블 ID",100));
		tableH.addColumn(new KSGTableColumn("company_abbr", "선사명",150));
		tableH.addColumn(new KSGTableColumn("agent", "에이전트",150));
		tableH.addColumn(new KSGTableColumn("vessel", "선박명",200));
		tableH.addColumn(new KSGTableColumn("date_issue", "출력일자",100));
		tableH.addColumn(new KSGTableColumn("voyage_num", "항차번호"));
		tableH.addColumn(new KSGTableColumn("fromPort", "출발항",200));
		tableH.addColumn(new KSGTableColumn("DateF", "출발일", 90));
		tableH.addColumn(new KSGTableColumn("DateT", "도착일", 90));
		tableH.addColumn(new KSGTableColumn("port", "도착항",200));
		tableH.addColumn(new KSGTableColumn("area_code", "지역코드",80));
		tableH.addColumn(new KSGTableColumn("console_cfs", "CFS",200));
		tableH.addColumn(new KSGTableColumn("console_page", "Consoe Page",100));
		tableH.addColumn(new KSGTableColumn("c_time", "C Time",100));
		tableH.addColumn(new KSGTableColumn("d_time", "D Time",100));

		tableH.initComp();

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
		cbxNormalInOut.addItem(new KSGTableColumn("","전체"));
		cbxNormalInOut.setShowTotal(true);
		cbxNormalInOut.initComp();

		cbxNormalSearch = new KSGComboBox();
		cbxNormalSearch.addItem(new KSGTableColumn("", "전체"));
		cbxNormalSearch.addItem(new KSGTableColumn("table_id", "테이블 ID"));
		cbxNormalSearch.addItem(new KSGTableColumn("company_abbr", "선사명"));
		cbxNormalSearch.addItem(new KSGTableColumn("agent", "에이전트"));
		cbxNormalSearch.addItem(new KSGTableColumn("vessel", "선박명"));
		cbxNormalSearch.addItem(new KSGTableColumn("voyage_num", "항차명"));
		cbxNormalSearch.addItem(new KSGTableColumn("n_voyage_num", "항차번호"));
		cbxNormalSearch.addItem(new KSGTableColumn("fromPort", "출발항"));
		cbxNormalSearch.addItem(new KSGTableColumn("toPort", "도착항"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateF", "출발일"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateT", "도착일"));	


		txfNoramlSearch = new JTextField(15);
		
		
		KSGGradientButton butSearch = new KSGGradientButton("검색", "images/search3.png");
		butSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		butSearch.addActionListener(this);
		


		pnNormalSearchCenter.add(new JLabel("구분:"));
		pnNormalSearchCenter.add(cbxNormalInOut);
		pnNormalSearchCenter.add(new JLabel("항목:"));
		pnNormalSearchCenter.add(cbxNormalSearch);
		pnNormalSearchCenter.add(txfNoramlSearch);
//		pnNormalSearchCenter.add(butSearch);
		
		
		KSGPanel pnNormalSeawrchEast = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		pnNormalSeawrchEast.add(butSearch);

//		pnNormalSeawrchEast.add(butCancel);

		pnNormalSearchMain.add(pnNormalSearchCenter);

		pnNormalSearchMain.add(pnNormalSeawrchEast,BorderLayout.EAST);

//		pnNormalSearchMain.add(pnNormalSearchCenter);

		return pnNormalSearchMain;

	}
	public void fnSearch()
	{

		try {

			searchParam = new CommandMap();

			searchParam.put("gubun", "console");


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

			//int page_size = tableH.getPageSize();

			//searchParam.put("PAGE_SIZE", page_size);

			searchParam.put("PAGE_NO", 1);

			logger.info("param:"+searchParam);

			CommandMap result = (CommandMap) scheduleService.selectListMap(searchParam);			

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
				//pageAction.setSearchPram(searchParam);
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

		if(command.equals("검색"))
		{
			fnSearch();
		}

	}

}
