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

  * @�ۼ��� : ��â��

  * @�����̷� :

  * @���α׷� ���� :

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
		
		tableH.initComp();
		
		tableH.addActionListener(new PageAction(tableH,scheduleService));
		
		
		treeTableModel = new ScheduleTreeTableModel();
		treeTableModel.addColumn(new KSGTableColumn("", ""));
		//treeTableModel.addColumn(new KSGTableColumn("gubun", "����"));
		treeTableModel.addColumn(new KSGTableColumn("table_id", "���̺� ID",100));
		treeTableModel.addColumn(new KSGTableColumn("company_abbr", "�����",100));
		treeTableModel.addColumn(new KSGTableColumn("agent", "������Ʈ",100));
		treeTableModel.addColumn(new KSGTableColumn("vessel", "���ڸ�",200));
		//treeTableModel.addColumn(new KSGTableColumn("date_issue", "�������",100));
		treeTableModel.addColumn(new KSGTableColumn("voyage_num", "������ȣ"));
		treeTableModel.addColumn(new KSGTableColumn("fromPort", "�����",200));
		treeTableModel.addColumn(new KSGTableColumn("DateF", "�����", 90));
		treeTableModel.addColumn(new KSGTableColumn("DateT", "������", 90));
		treeTableModel.addColumn(new KSGTableColumn("port", "������",200));
		
		table = new JTreeTable(treeTableModel );
		
		
		// 4��.
		JScrollPane treeTableScrollPane = new JScrollPane();
		treeTableScrollPane.setViewportView(table);
		 
		// TreeTable top-level container ���� ����.
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
//					  // �ű� ����� ����Ʈ ����
//					  ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
//					  scheduleList.add(item);
//					  
//					  // �ű� ����� ���� ����
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
				  
				  //�ش� ������ ������ ���
				  HashMap<String, Object> toPorts =(HashMap<String, Object>) areaList.get(area_name);
				  
				//����� ���� ���
				  if(toPorts.containsKey(toPort))					  
				  {  
					  
					  //������ ���
					  HashMap<String, Object> fromPorts =(HashMap<String, Object>) toPorts.get(toPort);
					  
					  // ������ ���� ���
					  if(fromPorts.containsKey(fromPort))					  
					  {
						  List list =(List) fromPorts.get(fromPort); 
						  list.add(item);
					  }
					  // ������ ���� ���
					  else
					  {
						  ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
						  scheduleList.add(item);						  
						  
						  fromPorts.put(fromPort, scheduleList);
					  }
					  // ������ ���

					  
					  // �ش� �������� ���� ���
					  
					  
				  }
				  // ����� ���� ���
				  else
				  {
					  // �ű� ������ ����Ʈ ����
					  ArrayList<HashMap<String, Object>> scheduleList = new ArrayList<HashMap<String,Object>>();
					  scheduleList.add(item);
					  
					  HashMap<String, Object> newFromPorts = new HashMap<String, Object>();
					  newFromPorts.put(fromPort, scheduleList);
					  
					  
					  // �ű� ������ ���� ����
					  
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
				  
				  // �����ȸ
				  String toPortKey = (String) toPortIter.next();
				  
				  HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) toPortItems.get(toPortKey);
				  
				  
				  //tree ��� ����
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
		cbxNormalSearch.addItem(new KSGTableColumn("", "��ü"));
		cbxNormalSearch.addItem(new KSGTableColumn("table_id", "���̺� ID"));
		cbxNormalSearch.addItem(new KSGTableColumn("company_abbr", "�����"));
		cbxNormalSearch.addItem(new KSGTableColumn("agent", "������Ʈ"));
		cbxNormalSearch.addItem(new KSGTableColumn("vessel", "���ڸ�"));
		cbxNormalSearch.addItem(new KSGTableColumn("voyage_num", "������"));
		cbxNormalSearch.addItem(new KSGTableColumn("n_voyage_num", "������ȣ"));
		cbxNormalSearch.addItem(new KSGTableColumn("fromPort", "�����"));
		cbxNormalSearch.addItem(new KSGTableColumn("port", "������"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateF", "�����"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateT", "������"));
		
		
		JLabel lblFromPort = new JLabel("�����");		
		JTextField txfFromPort = new JTextField(5);
		txfFromPort.setEditable(false);
		JButton butSearchFromPort = new JButton("�˻�");		
		butSearchFromPort.setActionCommand("SEARCH_FROM_PORT");
		butSearchFromPort.addActionListener(this);
		JLabel lblToPort = new JLabel("������");
		JTextField txfToPort = new JTextField(5);
		txfToPort.setEditable(false);
		JButton butSearchToPort = new JButton("�˻�");
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
		JButton butSearch = new JButton("�˻�");
		butSearch.addActionListener(this);
		//butSearch.setActionCommand("Normal �˻�");


		
		pnNormalSearchCenter.add(new JLabel("����:"));
		pnNormalSearchCenter.add(cbxNormalInOut);
		
		pnNormalSearchCenter.add(pnPortSearch);
		
		
		
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
		
		if(command.equals("�˻�"))
		{
			fnSearch();
		}
		
	}

}
