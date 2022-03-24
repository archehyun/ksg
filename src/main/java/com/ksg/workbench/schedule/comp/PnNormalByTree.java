package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.treetable.JTreeTable;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.KSGPageTablePanel;
import com.ksg.workbench.common.comp.button.PageAction;


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
		treeTableModel.addColumn(new KSGTableColumn("", ""));
		//treeTableModel.addColumn(new KSGTableColumn("gubun", "구분"));
		treeTableModel.addColumn(new KSGTableColumn("table_id", "테이블 ID",100));
		treeTableModel.addColumn(new KSGTableColumn("company_abbr", "선사명",100));
		treeTableModel.addColumn(new KSGTableColumn("agent", "에이전트",100));
		treeTableModel.addColumn(new KSGTableColumn("vessel", "선박명",200));
		//treeTableModel.addColumn(new KSGTableColumn("date_issue", "출력일자",100));
		treeTableModel.addColumn(new KSGTableColumn("voyage_num", "항차번호"));
		treeTableModel.addColumn(new KSGTableColumn("fromPort", "출발항",200));
		treeTableModel.addColumn(new KSGTableColumn("DateF", "출발일", 90));
		treeTableModel.addColumn(new KSGTableColumn("DateT", "도착일", 90));
		treeTableModel.addColumn(new KSGTableColumn("port", "도착항",200));
		
		table = new JTreeTable(treeTableModel );
		
		
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
	
//	private DefaultMutableTreeNode getInboundTreeNode(List<HashMap<String, Object>> master2) {
//		 DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");
//		  
//		  HashMap<String, Object> areaList = new HashMap<String, Object>();
//		  
//		  Iterator<HashMap<String, Object>>iter = result.iterator();
//		  while(iter.hasNext())
//		  {
//			  HashMap<String, Object> item = iter.next();
//			  
//			  String area_name=(String) item.get("area_name");
//			  
//			  String fromPort = (String) item.get("fromPort");
//			  
//			  if(areaList.containsKey(area_name))
//			  {
//				  HashMap<String, Object> fromPorts =(HashMap<String, Object>) areaList.get(area_name);
//				  
//				  if(fromPorts.containsKey(fromPort))					  
//				  {
//					  List list =(List) fromPorts.get(fromPort);
//					  
//					  list.add(item);  
//				  }
//				  else
//				  {
//					  // 신규 출발항 리스트 생성
//					  ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
//					  scheduleList.add(item);
//					  
//					  // 신규 출발항 정보 생성
//					  
//					  fromPorts.put(fromPort, scheduleList);
//					  
//					  					  
//					  
//				  }
//				  
//			  }
//			  else
//			  {
//				  ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
//				  scheduleList.add(item);
//				  HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
//				  newFromPorts.put(fromPort, scheduleList);
//				  
//	
//				  areaList.put(area_name, newFromPorts);
//			  }
//		  }
//		  
//		  Iterator keyList= areaList.keySet().iterator();
//		  
//		  while(keyList.hasNext())
//		  {
//			  String key = (String) keyList.next();
//			  HashMap<String, Object> items =  (HashMap<String, Object>) areaList.get(key);
//			  DefaultMutableTreeNode area = new DefaultMutableTreeNode(key);
//			  
//			  Iterator fromPortIter = items.keySet().iterator();
//			  while(fromPortIter.hasNext())
//			  {
//				  String fromPortKey = (String) fromPortIter.next();
//				  
//				  DefaultMutableTreeNode fromPort = new DefaultMutableTreeNode(fromPortKey);
//				  
//				  area.add(fromPort);
//				  
//				  List<HashMap<String, Object>> schedule = (List) items.get(fromPortKey);
//				  for(HashMap<String, Object> item:schedule)
//				  {
//					  fromPort.add( new DefaultMutableTreeNode(new TreeTableNode(item)));
//				  }
//				  
//			  }
//			  
//			 
//			  
//			  root.add(area);
//			  
//		  }
//		  
//		  return root;
//	}

	
	private DefaultMutableTreeNode getOutboundTreeNode(List result) {
		  DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");
		  
		  HashMap<String, Object> areaList = new HashMap<String, Object>();
		  
		  Iterator<HashMap<String, Object>>iter = result.iterator();
		  while(iter.hasNext())
		  {
			  HashMap<String, Object> item = iter.next();
			  
			  String area_name=(String) item.get("area_name");
			  
			  String toPort = (String) item.get("port");
			  
			  String fromPort = (String) item.get("fromPort");
			  
			  String vesselName = (String) item.get("vessel_name");
			  
			  if(areaList.containsKey(area_name))
			  {
				  
				  //해당 지역의 도착항 목록
				  HashMap<String, Object> toPorts =(HashMap<String, Object>) areaList.get(area_name);
				  
				//출발항 있을 경우
				  if(toPorts.containsKey(toPort))					  
				  {  
					  
					  //도착항 목록
					  HashMap<String, Object> fromPorts =(HashMap<String, Object>) toPorts.get(toPort);
					  
					  // 도착항 있을 경우
					  if(fromPorts.containsKey(fromPort))					  
					  {
						  List list =(List) fromPorts.get(fromPort); 
						  list.add(item);
					  }
					  // 도착항 없을 경우
					  else
					  {
						  ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
						  scheduleList.add(item);						  
						  
						  fromPorts.put(fromPort, scheduleList);
					  }
					  // 스케줄 목록

					  
					  // 해당 도착항의 선박 목록
					  
					  
				  }
				  // 출발항 없을 경우
				  else
				  {
					  // 신규 스케줄 리스트 생성
					  ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
					  scheduleList.add(item);
					  
					  HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
					  newFromPorts.put(fromPort, scheduleList);
					  
					  
					  // 신규 도착항 정보 생성
					  
					  toPorts.put(toPort, newFromPorts);
					  
					  					  
					  
				  }
				  
			  }
			  else
			  {
				  ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
				  scheduleList.add(item);
				  
				  
				  HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
				  newFromPorts.put(fromPort, scheduleList);
				  
				  HashMap<String, Object> newToPorts = new HashMap<String, Object>();
				  newToPorts.put(toPort, newFromPorts);
				  
				  
				  
	
				  areaList.put(area_name, newToPorts);
			  }
		  }
		  
		  Iterator areaListIter= areaList.keySet().iterator();
		  
		  while(areaListIter.hasNext())
		  {
			  String strArea = (String) areaListIter.next();
			  HashMap<String, Object> toPortItems =  (HashMap<String, Object>) areaList.get(strArea);
			  DefaultMutableTreeNode area = new DefaultMutableTreeNode(strArea);
			  
			  Iterator toPortIter = toPortItems.keySet().iterator();
			  while(toPortIter.hasNext())
			  {
				  
				  // 요소조회
				  String toPortKey = (String) toPortIter.next();
				  
				  HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) toPortItems.get(toPortKey);
				  
				  
				  //tree 노드 생성
				  DefaultMutableTreeNode toPort = new DefaultMutableTreeNode(toPortKey);
				  
				  
				  
				  Iterator fromPortIter = fromPortitems.keySet().iterator();
				  while(fromPortIter.hasNext())
				  {
					  
					  
					  String fromPortKey = (String) fromPortIter.next();
					  DefaultMutableTreeNode fromPort = new DefaultMutableTreeNode(fromPortKey);
					  List<HashMap<String, Object>> schedule = (List) fromPortitems.get(fromPortKey);
					  
					  
					  
					  for(HashMap<String, Object> item:schedule)
					  {
						  fromPort.add( new DefaultMutableTreeNode(new TreeTableNode(item)));
					  }
					  toPort.add(fromPort);
				  }
				  
				  area.add(toPort);
				  
				 
				  
			  }
			  
			 
			  
			  root.add(area);
			  
		  }
		  
		  return root;
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
		cbxNormalSearch.addItem(new KSGTableColumn("fromPort", "출발항"));
		cbxNormalSearch.addItem(new KSGTableColumn("port", "도착항"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateF", "출발일"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateT", "도착일"));
		
		
		JLabel lblFromPort = new JLabel("출발항");		
		JTextField txfFromPort = new JTextField(5);
		txfFromPort.setEditable(false);
		JButton butSearchFromPort = new JButton("검색");		
		butSearchFromPort.setActionCommand("SEARCH_FROM_PORT");
		butSearchFromPort.addActionListener(this);
		JLabel lblToPort = new JLabel("도착항");
		JTextField txfToPort = new JTextField(5);
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
			param.put("gubun", "Normal");			
			
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
			
			if(input_date!=null||!input_date.equals(""))
				
			{
				param.put("date_issue", input_date);
			}
			int page_size = tableH.getPageSize();
			
			param.put("PAGE_SIZE", page_size);
			
			param.put("PAGE_NO", 1);
			
			logger.info("param:"+param);
			
			HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectList(param);			
			
			result.put("PAGE_NO", 1);
			
			tableH.setResultData(result);

			master = (List) result.get("master");
			
			
			
			if(col.columnField.equals("I"))
			{
				treeTableModel.setRoot(getOutboundTreeNode(master));
			}
			else
			{
				treeTableModel.setRoot(getOutboundTreeNode(master));	
			}
			
			
			table.setTreeExpandedState(true);
			table.updateUI();

			if(master.size()==0)
			{
				
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
		
		if(command.equals("검색"))
		{
			fnSearch();
		}
		
	}

}
