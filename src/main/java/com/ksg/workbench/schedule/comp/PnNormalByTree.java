package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.treetable.JTreeTable;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.KSGPageTablePanel;
import com.ksg.workbench.common.comp.button.PageAction;
import com.ksg.workbench.schedule.dialog.SearchPortDialog;


/**

 * @FileName : PnOutbound.java

 * @Project : KSG2

 * @Date : 2022. 3. 7. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :

 */
public class PnNormalByTree extends PnSchedule{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private KSGPageTablePanel tableH;	

	private List<HashMap<String, Object>> master;

	private KSGComboBox cbxNormalInOut;

	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JTextField txfNoramlSearch;	

	JTreeTable table;

	private ScheduleTreeTableModel treeTableModel;

	private SimpleDateFormat inputDateFormat 	= KSGDateUtil.createInputDateFormat();

	private SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();

	private JTextField txfToPort;

	private JTextField txfFromPort;

	public PnNormalByTree() {

		super();

		this.setLayout(new BorderLayout());

		add(buildSearch(),BorderLayout.NORTH);

		add(buildCenter());


	}

	public KSGPanel buildCenter()
	{
		tableH = new KSGPageTablePanel("스케줄 목록");		

		tableH.addColumn(new KSGTableColumn("gubun", "구분"));
		tableH.addColumn(new KSGTableColumn("table_id", "테이블 ID",100));
		tableH.addColumn(new KSGTableColumn("company_abbr", "선사명",100));
		tableH.addColumn(new KSGTableColumn("agent", "에이전트",100));
		tableH.addColumn(new KSGTableColumn("vessel", "선박명",200));
		tableH.addColumn(new KSGTableColumn("date_issue", "출력일자",100));
		tableH.addColumn(new KSGTableColumn("voyage_num", "항차번호"));
		tableH.addColumn(new KSGTableColumn("fromPort", "출발항",200));
		tableH.addColumn(new KSGTableColumn("DateF", "출발일", 90));
		tableH.addColumn(new KSGTableColumn("DateT", "도착일", 90));
		tableH.addColumn(new KSGTableColumn("port", "도착항",200));

		tableH.initComp();

		tableH.addActionListener(new PageAction(tableH,scheduleService));


		treeTableModel = new ScheduleTreeTableModel();
		
		treeTableModel.addColumn(new KSGTableColumn("", "",400));
		
		treeTableModel.addColumn(new KSGTableColumn("table_id", "테이블 ID",100));
		treeTableModel.addColumn(new KSGTableColumn("company_abbr", "선사명",100));
		treeTableModel.addColumn(new KSGTableColumn("agent", "에이전트",100));
		treeTableModel.addColumn(new KSGTableColumn("vessel", "선박명",200));
		
		treeTableModel.addColumn(new KSGTableColumn("voyage_num", "항차번호"));
		treeTableModel.addColumn(new KSGTableColumn("fromPort", "출발항",200));
		treeTableModel.addColumn(new KSGTableColumn("DateF", "출발일", 90));
		treeTableModel.addColumn(new KSGTableColumn("DateT", "도착일", 90));
		treeTableModel.addColumn(new KSGTableColumn("port", "도착항",200));

		table = new JTreeTable(treeTableModel );
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




	/**
	 * 
	 * 스케줄 리스트를 기준으로 트리 노드 생성
	 * 
	 * @param result
	 * @return
	 */
	private DefaultMutableTreeNode getOutboundTreeNode(HashMap<String, Object> areaList) {
		
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");

		Iterator areaListIter= areaList.keySet().iterator();

		while(areaListIter.hasNext())
		{
			String strArea = (String) areaListIter.next();
			HashMap<String, Object> toPortItems =  (HashMap<String, Object>) areaList.get(strArea);
			
			DefaultMutableTreeNode area = new DefaultMutableTreeNode(strArea);

			// 도착항 정렬
			Object[] mapkey = toPortItems.keySet().toArray();
			Arrays.sort(mapkey);


			for (Object toPortKey : mapkey)
			{
				// 요소조회
				HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) toPortItems.get(toPortKey);


				//tree 노드 생성
				DefaultMutableTreeNode toPort = new DefaultMutableTreeNode(toPortKey);

				// 출발항 정렬
				List<Entry<String, Object>> list_entries = new ArrayList<Entry<String, Object>>(fromPortitems.entrySet());


				// 출발항 목록
				Iterator fromPortIter = fromPortitems.keySet().iterator();

				while(fromPortIter.hasNext())
				{				  

					String fromPortKey = (String) fromPortIter.next();

					DefaultMutableTreeNode fromPort = new DefaultMutableTreeNode(fromPortKey);

					List<HashMap<String, Object>> schedule = (List) fromPortitems.get(fromPortKey);
					
					
					ArrayList<DefaultMutableTreeNode> jointSchedule = createJoinedScheduleNode(schedule);


					for(DefaultMutableTreeNode scheduleNode:jointSchedule)
					{
						fromPort.add(scheduleNode );
					}

					// 도착항 그룹에 추가
					toPort.add(fromPort);
				}

				area.add(toPort);				  
			}

			root.add(area);

		}

		return root;
	}
	
	/**
	 * 공동배선 적용 
	 * @param schedule
	 * @return
	 */
	private ArrayList<DefaultMutableTreeNode> createJoinedScheduleNode(List<HashMap<String, Object>> schedule)
	{
		HashMap<String, Object> scheduleList = new HashMap<String, Object>();
		// 항차 번호, 선박명이 같은 목록 조회
		
		for(HashMap<String, Object> scheduleItem:schedule)
		{
			String vessel = String.valueOf( scheduleItem.get("vessel"));
			int n_voyage = ScheduleBuildUtil.getNumericVoyage( String.valueOf(scheduleItem.get("voyage_num")));
			if(scheduleList.containsKey(vessel+"-"+n_voyage))
			{	
				ArrayList<HashMap<String, Object>> jointScheduleItemList = (ArrayList<HashMap<String, Object>>) scheduleList.get(vessel+"-"+n_voyage);
				jointScheduleItemList.add(scheduleItem);
			}
			else
			{
				ArrayList<HashMap<String, Object>> jointScheduleItemList = new ArrayList<HashMap<String, Object>>();
				jointScheduleItemList.add(scheduleItem);					
				scheduleList.put(vessel+"-"+n_voyage, jointScheduleItemList);
			}
		}
		
		ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();
				
		for(String strKey:scheduleList.keySet())
		{
			ArrayList<HashMap<String, Object>> jointScheduleItemList=(ArrayList<HashMap<String, Object>>) scheduleList.get(strKey);
			if(jointScheduleItemList.size()==1)
			{
				DefaultMutableTreeNode node = new ScheduleTreeNode(new TreeTableNode(jointScheduleItemList.get(0)));
				nodeList.add(node);
			}
			else if(jointScheduleItemList.size()>1)
			{
				DefaultMutableTreeNode node = new ScheduleTreeNode("joint");
				for(HashMap<String, Object> item: jointScheduleItemList)
				{
					DefaultMutableTreeNode subnode = new ScheduleTreeNode(new TreeTableNode(item));
					node.add(subnode);
				}
				nodeList.add(node);
			}
			
		}
		
		// 스케줄 추가
		// 정렬
		return nodeList;
	}
	class ScheduleTreeNode extends DefaultMutableTreeNode
	{
		String vessel;
		String company;
		String fromDate, toDate;
		public ScheduleTreeNode() {
			super();
		}
		public ScheduleTreeNode(String string) {
			super(string);
		}

		public ScheduleTreeNode(TreeTableNode treeTableNode) {
			super(treeTableNode);
			setInfo(treeTableNode);

		}
		public String toString()
		{
			return fromDate+"  "+vessel+"("+company +")"+toDate;
		}
		@Override
		public void add(MutableTreeNode newChild) {
			super.add(newChild);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) newChild;

			TreeTableNode item= (TreeTableNode) node.getUserObject();
			setInfo(item);

		}
		public void setInfo(TreeTableNode item)		

		{
			vessel=String.valueOf(item.get("vessel"));
			company =String.valueOf(item.get("company_abbr"));


			try {

				fromDate =outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("DateF"))));
				toDate =outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("DateT"))));
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}





	public KSGPanel buildSearch()
	{
		KSGPanel pnNormalSearchMain = new KSGPanel(new BorderLayout());
		KSGPanel pnNormalSearchCenter = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		cbxNormalInOut = new KSGComboBox("inOutType");
		cbxNormalInOut.initComp();

		cbxNormalSearch = new KSGComboBox();
		cbxNormalSearch.addItem(new KSGTableColumn("", "전체"));
		cbxNormalSearch.addItem(new KSGTableColumn("table_id", "테이블 ID"));
		cbxNormalSearch.addItem(new KSGTableColumn("company_abbr", "선사명"));
		cbxNormalSearch.addItem(new KSGTableColumn("agent", "에이전트"));
		cbxNormalSearch.addItem(new KSGTableColumn("vessel", "선박명"));
		cbxNormalSearch.addItem(new KSGTableColumn("voyage_num", "항차명"));
		cbxNormalSearch.addItem(new KSGTableColumn("n_voyage_num", "항차번호"));		
		cbxNormalSearch.addItem(new KSGTableColumn("DateF", "출발일"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateT", "도착일"));


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
		//butSearch.setActionCommand("Normal 검색");



		pnNormalSearchCenter.add(new JLabel("구분:"));
		pnNormalSearchCenter.add(cbxNormalInOut);

		pnNormalSearchCenter.add(pnPortSearch);



		pnNormalSearchCenter.add(new JLabel("항목:"));
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
			param.put("gubun", gubun);			

			String searchOption  = txfNoramlSearch.getText();

			KSGTableColumn col = (KSGTableColumn)cbxNormalInOut.getSelectedItem();
			
			param.put("inOutType", col.columnField);


			if(cbxNormalSearch.getSelectedIndex()>0) {

				KSGTableColumn item=(KSGTableColumn) cbxNormalSearch.getSelectedItem();
				if(!searchOption.equals(""))

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

			HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectScheduleGroupList(param);

			if(col.columnField.equals("I"))
			{	
				
				treeTableModel.setRoot(getOutboundTreeNode(result));
			}
			else
			{
				treeTableModel.setRoot(getOutboundTreeNode(result));
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

	class AscendingFromDate implements Comparator<HashMap<String,Object>> 
	{ 
		@Override 
		public int compare(HashMap<String,Object> one, HashMap<String,Object> two) {

			String fromDateOne = String.valueOf(one.get("DateF"));
			String fromDateTwo = String.valueOf(two.get("DateF"));
			
			return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

		} 
	}



}
