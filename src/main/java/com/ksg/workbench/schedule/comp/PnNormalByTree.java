package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.service.impl.AreaService;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.KSGPageTablePanel;
import com.ksg.workbench.common.comp.treetable.KSGTreeTable;
import com.ksg.workbench.schedule.comp.treenode.TreeNodeManager;
import com.ksg.workbench.schedule.dialog.SearchPortDialog;


/**

 * @FileName : PnOutbound.java

 * @Project : KSG2

 * @Date : 2022. 3. 7. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 스케줄 조회 후 트리 형태로 표시 , 공동 배선 적용

 */
public class PnNormalByTree extends PnSchedule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private KSGPageTablePanel tableH;	

	private KSGComboBox cbxNormalInOut;
	
	private KSGComboBox cbxArea;

	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JTextField txfNoramlSearch;	

	private KSGTreeTable table;

	private ScheduleTreeTableModel treeTableModel;

	private JTextField txfToPort;

	private JTextField txfFromPort;
	
	private HashMap<String, Object> inboundCodeMap;
	
	TreeNodeManager nodeManager = new TreeNodeManager();
	
	AreaService areaService = new AreaServiceImpl();
	
	protected ObjectMapper objectMapper;

	public PnNormalByTree() {

		super();
		
		codeService = new CodeServiceImpl();
		
		objectMapper = new ObjectMapper();

		this.setLayout(new BorderLayout());
		
		this.addComponentListener(this);

		add(buildSearch(),BorderLayout.NORTH);

		add(buildCenter());

	}

	public KSGPanel buildCenter()
	{

		treeTableModel = new ScheduleTreeTableModel();

		treeTableModel.addColumn(new KSGTableColumn("", "",600));

		treeTableModel.addColumn(new KSGTableColumn("table_id", "테이블 ID",100));
		
		treeTableModel.addColumn(new KSGTableColumn("company_abbr", "선사명",100));
		
		treeTableModel.addColumn(new KSGTableColumn("agent", "에이전트",100));
		
		treeTableModel.addColumn(new KSGTableColumn("vessel", "선박명",200));

		treeTableModel.addColumn(new KSGTableColumn("voyage_num", "항차번호"));
		
		treeTableModel.addColumn(new KSGTableColumn("fromPort", "출발항",200));
		
		treeTableModel.addColumn(new KSGTableColumn("DateF", "출발일", 90));
		
		treeTableModel.addColumn(new KSGTableColumn("DateT", "도착일", 90));
		
		treeTableModel.addColumn(new KSGTableColumn("port", "도착항",200));

		table = new KSGTreeTable(treeTableModel );
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		// 4번.
		JScrollPane treeTableScrollPane = new JScrollPane();
		
		treeTableScrollPane.setViewportView(table);

		// TreeTable top-level container 배경색 변경.
		table.getParent().setBackground(Color.white);

		KSGPanel pnMain = new KSGPanel(new BorderLayout());
		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));
		pnMain.add(treeTableScrollPane);
		return pnMain;
	}





	public KSGPanel buildSearch()
	{
		KSGPanel pnNormalSearchMain = new KSGPanel(new BorderLayout());
		
		KSGPanel pnNormalSearchCenter = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		 
		cbxNormalInOut = new KSGComboBox("inOutType");
		cbxNormalSearch = new KSGComboBox();
		cbxNormalSearch.addItem(new KSGTableColumn("", "전체"));
		cbxNormalSearch.addItem(new KSGTableColumn("table_id", "테이블 ID"));
		cbxNormalSearch.addItem(new KSGTableColumn("company_abbr", "선사명"));
		cbxNormalSearch.addItem(new KSGTableColumn("agent", "에이전트"));
		cbxNormalSearch.addItem(new KSGTableColumn("vessel", "선박명"));
		cbxNormalSearch.addItem(new KSGTableColumn("voyage_num", "항차명"));
		cbxNormalSearch.addItem(new KSGTableColumn("n_voyage_num", "항차번호"));		
		cbxNormalSearch.addItem(new KSGTableColumn("dateF", "출발일"));
		cbxNormalSearch.addItem(new KSGTableColumn("dateT", "도착일"));
		
		cbxArea = new KSGComboBox();

		JLabel lblFromPort = new JLabel("출발항");
		
		txfFromPort = new JTextField(10);
		
		txfFromPort.setEditable(false);
		
		JButton butSearchFromPort = new JButton("검색");		
		butSearchFromPort.setActionCommand("SEARCH_FROM_PORT");
		butSearchFromPort.addActionListener(this);
		JLabel lblToPort = new JLabel("도착항");
		txfToPort = new JTextField(10);
		txfToPort.setEditable(false);
		JButton butSearchToPort = new JButton("검색");
		butSearchToPort.setActionCommand("SEARCH_TO_PORT");
		butSearchToPort.addActionListener(this);
		KSGPanel pnPortSearch = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		pnPortSearch.add(lblFromPort);
		pnPortSearch.add(txfFromPort);
		pnPortSearch.add(butSearchFromPort);
		pnPortSearch.add(lblToPort);
		pnPortSearch.add(txfToPort);
		pnPortSearch.add(butSearchToPort);

		txfNoramlSearch = new JTextField(15);
		
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
		
		
		KSGPanel pnNormalSeawrchEast = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		
		pnNormalSeawrchEast.add(butSearch);		
		

		pnNormalSearchMain.add(pnNormalSearchCenter);
		
		pnNormalSearchMain.add(pnNormalSeawrchEast,BorderLayout.EAST);

		return pnNormalSearchMain;

	}
	public void fnSearch()
	{	

		try {
			
			CommandMap param = new CommandMap();
			
			param.put("gubun", gubun);

			String searchOption  = txfNoramlSearch.getText();

			KSGTableColumn col = (KSGTableColumn)cbxNormalInOut.getSelectedItem();

			param.put("inOutType", col.columnField);

			if(cbxArea.getSelectedIndex()>0)
			{
				KSGTableColumn item=(KSGTableColumn) cbxArea.getSelectedItem();
				param.put("area_name", item.columnField);
			}
			if(cbxNormalSearch.getSelectedIndex()>0) {

				KSGTableColumn item=(KSGTableColumn) cbxNormalSearch.getSelectedItem();
				if(!"".equals(searchOption))
				{

					param.put(item.columnField, searchOption);
				}
			}

			if(!"".equals(txfFromPort.getText())){
				param.put("fromPort", txfFromPort.getText());
			}

			if(!"".equals(txfToPort.getText())){
				param.put("port", txfToPort.getText());
			}

			if(input_date!=null||!input_date.equals(""))

			{
				param.put("date_issue", input_date);
			}

			logger.info("param:"+param);


			// inbound 호출시
			if(col.columnField.equals("I"))
			{	
				table.setShowPathCount(4);

				CommandMap result = (CommandMap) scheduleService.selectInboundScheduleGroupList(param);
				
				treeTableModel.setRoot(nodeManager.getInboundTreeNode(result));
			}
			// outbound 호출시
			else
			{
				table.setShowPathCount(5);		
				
				HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectOutboundScheduleGroupList(param);
				
				treeTableModel.setRoot(nodeManager.getOutboundTreeNode(result));
			}


			table.setTreeExpandedState(true);

			table.updateUI();


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

	
	/*
	 * 날짜 기준 정력
	 */
	class AscendingFromDate implements Comparator<HashMap<String,Object>> 
	{ 
		//출발일, 도착일 구분
		String dateType;
		//오름차순, 내림차순 구분
		int asc;
		
		public AscendingFromDate(String dateType, int asc)
		
		{
			this.dateType = dateType;
		}
		@Override 
		public int compare(HashMap<String,Object> one, HashMap<String,Object> two) {

			String fromDateOne = String.valueOf(one.get(dateType));
			String fromDateTwo = String.valueOf(two.get(dateType));

			return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

		} 
	}


	@Override
	public void componentShown(ComponentEvent e) {
		
		
		
		cbxNormalInOut.initComp();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("code_type", "inPort");
		try {
			List<CommandMap> areaList=areaService.selectAreaInfoList();
			cbxArea.removeAllItems();
			cbxArea.addItem(new KSGTableColumn("", "전체"));
			for(CommandMap item:areaList)
			{
				
				
				cbxArea.addItem(new KSGTableColumn(String.valueOf(item.get("area_name")), String.valueOf(item.get("area_name"))));
			}
			
			inboundCodeMap = (HashMap<String, Object>) codeService.selectInboundPortMap();
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
}
