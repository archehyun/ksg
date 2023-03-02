package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dtp.api.control.ScheduleController;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.AreaInfo;
import com.ksg.service.AreaService;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.common.comp.button.ImageButton;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.schedule.dialog.SearchPortDialog;


/**

 * @FileName : PnOutbound.java

 * @Project : KSG2

 * @Date : 2022. 3. 7. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
public class PnNormal2 extends PnSchedule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private KSGTablePanel tableH;

	private List<HashMap<String, Object>> master;

	private KSGComboBox cbxNormalInOut;
	
	private KSGComboBox cbxArea;
	
	private AreaService areaService = new AreaServiceImpl();

	private JComboBox<KSGTableColumn> cbxNormalSearch;
	
	private ScheduleController control = new ScheduleController(); 

	private JTextField txfNoramlSearch;

	private JTextField txfToPort;

	private JTextField txfFromPort;


	private CommandMap searchParam;
	
	public PnNormal2() {

		super();
		
		this.setLayout(new BorderLayout());
		
		this.addComponentListener(this);

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
		tableH.addColumn(new KSGTableColumn("area_name", "지역명"));
		tableH.addColumn(new KSGTableColumn("fromPort", "출발항",200));
		tableH.addColumn(new KSGTableColumn("dateF", "출발일", 90));
		tableH.addColumn(new KSGTableColumn("dateT", "도착일", 90));
		tableH.addColumn(new KSGTableColumn("port", "도착항",200));

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
		cbxNormalInOut.setShowTotal(true);		
		cbxNormalInOut.initComp();

		cbxNormalSearch = new JComboBox<KSGTableColumn>();
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
		txfNoramlSearch.addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyChar()== KeyEvent.VK_ENTER)
				{
					fnSearch();
				}
			}
			
		});
		
		JButton butCancel = new JButton("초기화");
		butCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				txfFromPort.setText("");
				txfToPort.setText("");
				txfNoramlSearch.setText("");
				cbxArea.setSelectedIndex(0);
				cbxNormalSearch.setSelectedIndex(0);
				
			}
		});
		
		cbxArea = new KSGComboBox();
		
		JLabel lblFromPort = new JLabel("출발항");

		txfFromPort = new JTextField(10);

		txfFromPort.setEditable(false);

		JButton butSearchFromPort = new ImageButton("images/search1.png");	
		
		butSearchFromPort.setActionCommand("SEARCH_FROM_PORT");
		
		butSearchFromPort.addActionListener(this);
		
		JLabel lblToPort = new JLabel("도착항");
		
		txfToPort = new JTextField(10);
		
		txfToPort.setEditable(false);
		
		JButton butSearchToPort = new ImageButton("images/search1.png");
		
		butSearchToPort.setActionCommand("SEARCH_TO_PORT");
		
		butSearchToPort.addActionListener(this);
		
		KSGPanel pnPortSearch = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnPortSearch.add(lblFromPort);
		pnPortSearch.add(txfFromPort);
		pnPortSearch.add(butSearchFromPort);
		pnPortSearch.add(lblToPort);
		pnPortSearch.add(txfToPort);
		pnPortSearch.add(butSearchToPort);
		
		JButton butSearch = new JButton("검색");
		butSearch.addActionListener(this);

		pnNormalSearchCenter.add(new JLabel("구분:"));
		pnNormalSearchCenter.add(cbxNormalInOut);
		pnNormalSearchCenter.add(new JLabel("지역:"));
		pnNormalSearchCenter.add(cbxArea);
		pnNormalSearchCenter.add(pnPortSearch);
		pnNormalSearchCenter.add(new JLabel("항목:"));
		pnNormalSearchCenter.add(cbxNormalSearch);
		pnNormalSearchCenter.add(txfNoramlSearch);
		pnNormalSearchCenter.add(butSearch);
		pnNormalSearchCenter.add(butCancel);
		
		KSGPanel pnNormalSeawrchEast = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		pnNormalSeawrchEast.add(butSearch);
		
		pnNormalSeawrchEast.add(butCancel);

		pnNormalSearchMain.add(pnNormalSearchCenter);

		pnNormalSearchMain.add(pnNormalSeawrchEast,BorderLayout.EAST);
		

		pnNormalSearchMain.add(pnNormalSearchCenter);

		return pnNormalSearchMain;

	}
	public void fnSearch()
	{
		try {
			searchParam = new CommandMap();
			searchParam.put("gubun", "Normal");			

			String searchOption  = txfNoramlSearch.getText();

			if(cbxNormalInOut.getSelectedIndex()>0) {
				KSGTableColumn col = (KSGTableColumn)cbxNormalInOut.getSelectedItem();
				searchParam.put("inOutType", col.columnField);

			}
			if(cbxArea.getSelectedIndex()>0)
			{
				KSGTableColumn item=(KSGTableColumn) cbxArea.getSelectedItem();
				searchParam.put("area_name", item.columnField);
			}

			if(cbxNormalSearch.getSelectedIndex()>0) {

				KSGTableColumn item=(KSGTableColumn) cbxNormalSearch.getSelectedItem();
				if(!searchOption.equals(""))
					searchParam.put(item.columnField, searchOption);
			}
			
			if(!"".equals(txfFromPort.getText())){
				searchParam.put("fromPort", txfFromPort.getText());
			}

			if(!"".equals(txfToPort.getText())){
				searchParam.put("port", txfToPort.getText());
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

			
			CommandMap result =control.selectScheduleMapList(searchParam);
//			CommandMap result = (CommandMap) scheduleService.selectListMap(searchParam);			

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

		if(command.equals("검색"))
		{
			fnSearch();
		}
		else if(command.equals("SEARCH_FROM_PORT"))
		{
			SearchPortDialog portDialog = new SearchPortDialog();

			portDialog.createAndUpdateUI();

			txfFromPort.setText(portDialog.result);
		}
		else if(command.equals("SEARCH_TO_PORT"))
		{
			SearchPortDialog portDialog = new SearchPortDialog();

			portDialog.createAndUpdateUI();

			txfToPort.setText(portDialog.result);
		}		

	}
	@Override
	public void componentShown(ComponentEvent e) {

		try {
			
			cbxArea.removeAllItems();
			
			cbxArea.addItem(new KSGTableColumn("", "전체"));
			
			List<AreaInfo> areaList=areaService.selectAll();
			
			areaList.stream()	.sorted(Comparator.comparing(AreaInfo::getArea_name))
								.forEach(o->cbxArea.addItem(new KSGTableColumn(o.getArea_name(), o.getArea_name())));
			

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
