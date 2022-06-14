package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.Schedule;
import com.ksg.domain.ScheduleData;
import com.ksg.schedule.execute.formater.InboundJointedFormatter;
import com.ksg.schedule.execute.formater.JointFormatter;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.treetable.TreeTableNode;
import com.ksg.workbench.common.comp.KSGPageTablePanel;
import com.ksg.workbench.common.comp.treetable.KSGTreeTable;
import com.ksg.workbench.common.comp.treetable.node.PortTreeNode;
import com.ksg.workbench.common.comp.treetable.node.ScheduleTreeNode;
import com.ksg.workbench.schedule.dialog.SearchPortDialog;


/**

 * @FileName : PnOutbound.java

 * @Project : KSG2

 * @Date : 2022. 3. 7. 

 * @�ۼ��� : ��â��

 * @�����̷� :

 * @���α׷� ���� : ������ ��ȸ �� Ʈ�� ���·� ǥ�� , ���� �輱 ����

 */
public class PnNormalByTree extends PnSchedule {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private KSGPageTablePanel tableH;	

	private KSGComboBox cbxNormalInOut;

	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JTextField txfNoramlSearch;	

	private KSGTreeTable table;

	private ScheduleTreeTableModel treeTableModel;

	private SimpleDateFormat inputDateFormat 	= KSGDateUtil.createInputDateFormat();

	private SimpleDateFormat outputDateFormat 	= KSGDateUtil.createOutputDateFormat();

	private JTextField txfToPort;

	private JTextField txfFromPort;
	
	private HashMap<String, Object> inboundCodeMap;
	
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

		treeTableModel.addColumn(new KSGTableColumn("", "",400));

		treeTableModel.addColumn(new KSGTableColumn("table_id", "���̺� ID",100));
		treeTableModel.addColumn(new KSGTableColumn("company_abbr", "�����",100));
		treeTableModel.addColumn(new KSGTableColumn("agent", "������Ʈ",100));
		treeTableModel.addColumn(new KSGTableColumn("vessel", "���ڸ�",200));

		treeTableModel.addColumn(new KSGTableColumn("voyage_num", "������ȣ"));
		treeTableModel.addColumn(new KSGTableColumn("fromPort", "�����",200));
		treeTableModel.addColumn(new KSGTableColumn("DateF", "�����", 90));
		treeTableModel.addColumn(new KSGTableColumn("DateT", "������", 90));
		treeTableModel.addColumn(new KSGTableColumn("port", "������",200));

		table = new KSGTreeTable(treeTableModel );
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
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

	private DefaultMutableTreeNode getInboundTreeNode2(HashMap<String, Object> areaList) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");

		Iterator areaListIter= areaList.keySet().iterator();
		
		while(areaListIter.hasNext())
		{	
			String strArea = (String) areaListIter.next();

			HashMap<String, Object> fromPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			DefaultMutableTreeNode area = new DefaultMutableTreeNode(strArea);

			// ����� ����
			Object[] mapkey = fromPortItems.keySet().toArray();

			for (Object fromPortKey : mapkey)
			{
				// �����ȸ
				HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) fromPortItems.get(fromPortKey);

				//tree ��� ����
				DefaultMutableTreeNode fromPort = new DefaultMutableTreeNode(fromPortKey);

				// ������ ����
				List<Entry<String, Object>> list_entries = new ArrayList<Entry<String, Object>>(fromPortitems.entrySet());


				// ������ ���
				Iterator toPortIter = fromPortitems.keySet().iterator();

				while(toPortIter.hasNext())
				{
					String toPortKey = (String) toPortIter.next();

					DefaultMutableTreeNode toPort = new DefaultMutableTreeNode(toPortKey);

					List<ScheduleData> schedule = (List) fromPortitems.get(toPortKey);


					ArrayList<DefaultMutableTreeNode> jointSchedule = createOutboundJoinedScheduleNode(schedule);


					for(DefaultMutableTreeNode scheduleNode:jointSchedule)
					{
						fromPort.add(scheduleNode );
					}

					// ����� �׷쿡 �߰�
					fromPort.add(toPort);
				}

				area.add(fromPort);				  
			}

			root.add(area);
		}

		return root;

	}

	/**
	 * ����
	 * ----�����(�ܱ���)
	 * --------����
	 * ------------������(������)
	 * 
	 * 
	 * @param areaList
	 * @return
	 */
	private DefaultMutableTreeNode getInboundTreeNode(HashMap<String, Object> areaList) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");
		Iterator areaListIter= areaList.keySet().iterator();

		while(areaListIter.hasNext())
		{
			String strArea = (String) areaListIter.next();

			DefaultMutableTreeNode area = new DefaultMutableTreeNode(strArea);

			//�����
			HashMap<String, Object> fromPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			// ����� ����
			Object[] mapkey = fromPortItems.keySet().toArray();

			for (Object fromPortKey : mapkey)
			{	
				HashMap<String, Object> vesselitems =  (HashMap<String, Object>) fromPortItems.get(fromPortKey);
				//tree ��� ����
				DefaultMutableTreeNode fromPort = new PortTreeNode((String)fromPortKey, "");

				// ���� ���
				Object[] vesselAndDatekey = vesselitems.keySet().toArray();
				
				
				//makesortedKey
				
				String[][] key = new String[vesselAndDatekey.length][2];
				
				for(int i =0;i<vesselAndDatekey.length;i++)
				{
					String str = (String) vesselAndDatekey[i];
					key[i][0] =str; //vesselAndDateF
					key[i][1] =str.substring(str.indexOf("$$")+2, str.length()); // dateF
					
				}
				
				// key ����� ���� ����
				Arrays.sort(key, new Comparator<String[]>() { 
					@Override 
					public int compare(String[] o1, String[] o2) {
						
						String fromDateOne = o1[1];
						String fromDateTwo = o2[1];;

						return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;
						
					}
				});

				
				
				for (int i=0;i<key.length;i++)
				{
					String str = (String) key[i][0];
					
					String vesselName =str.substring(0,str.indexOf("$$"));

					// ������ ���
					ArrayList<HashMap<String, Object>> scheduleList =  (ArrayList<HashMap<String, Object>>) vesselitems.get(str);


					fromPort.add(new GroupTreeNode(vesselName,new InboundJointedFormatter(), scheduleList ));
					
				}
				area.add(fromPort);

			}
			root.add(area);
		}


		return root;

	}

	/**
	 * 
	 * ������ ����Ʈ�� �������� inbound Ʈ�� ��� ����
	 * ����� -> ���ϸ� ���ڱ׷� -> ������ or ����� -> ���� -> ������   
	 * @param areaList
	 * @return
	 */
	private DefaultMutableTreeNode getInboundTreeNode1(HashMap<String, Object> areaList) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("AREA");

		Iterator areaListIter= areaList.keySet().iterator();

		while(areaListIter.hasNext())
		{	
			String strArea = (String) areaListIter.next();

			HashMap<String, Object> fromPortItems =  (HashMap<String, Object>) areaList.get(strArea);

			DefaultMutableTreeNode area = new DefaultMutableTreeNode(strArea);

			// ����� ����
			Object[] mapkey = fromPortItems.keySet().toArray();

			for (Object fromPortKey : mapkey)
			{
				// �����ȸ
				HashMap<String, Object> fromPortitems 	=  (HashMap<String, Object>) fromPortItems.get(fromPortKey);

				//tree ��� ����
				DefaultMutableTreeNode fromPort 		= new PortTreeNode((String)fromPortKey, "test");

				// ������ ����
				List<Entry<String, Object>> list_entries = new ArrayList<Entry<String, Object>>(fromPortitems.entrySet());

				// ������ ���
				Iterator toPortIter = fromPortitems.keySet().iterator();

				while(toPortIter.hasNext())
				{
					String toPortKey = (String) toPortIter.next();

					DefaultMutableTreeNode toPort = new DefaultMutableTreeNode(toPortKey);

					List<HashMap<String, Object>> schedule = (List) fromPortitems.get(toPortKey);

					ArrayList<DefaultMutableTreeNode> jointSchedule = createInboundJoinedScheduleNode(schedule);


					for(DefaultMutableTreeNode scheduleNode:jointSchedule)
					{
						fromPort.add(scheduleNode );
					}
				}

				area.add(fromPort);				  
			}

			root.add(area);
		}

		return root;

	}


	/**
	 * 
	 * ������ ����Ʈ�� �������� outbound Ʈ�� ��� ����
	 * ������ -> ����� -> �����ٱ׷� -> ������ or ������ -> ����� -> ������   
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

			// ������ ����
			Object[] mapkey = toPortItems.keySet().toArray();
			Arrays.sort(mapkey);


			for (Object toPortKey : mapkey)
			{
				// �����ȸ
				HashMap<String, Object> fromPortitems =  (HashMap<String, Object>) toPortItems.get(toPortKey);

				//tree ��� ����
				DefaultMutableTreeNode toPort = new PortTreeNode(toPortKey);

				// ����� ����
				List<Entry<String, Object>> list_entries = new ArrayList<Entry<String, Object>>(fromPortitems.entrySet());

				// ����� ���
				Iterator fromPortIter = fromPortitems.keySet().iterator();

				while(fromPortIter.hasNext())
				{				  

					String fromPortKey = (String) fromPortIter.next();

					DefaultMutableTreeNode fromPort = new DefaultMutableTreeNode(fromPortKey);

					List<ScheduleData> schedule = (List) fromPortitems.get(fromPortKey);

					ArrayList<DefaultMutableTreeNode> jointSchedule = createOutboundJoinedScheduleNode(schedule);


					for(DefaultMutableTreeNode scheduleNode:jointSchedule)
					{
						fromPort.add(scheduleNode );
					}

					// ������ �׷쿡 �߰�
					toPort.add(fromPort);
				}

				area.add(toPort);				  
			}

			root.add(area);

		}

		return root;
	}


	/**
	 * inbound�����輱 ���� 
	 * 
	 * @param schedule
	 * @return
	 */
	private ArrayList<DefaultMutableTreeNode> createInboundJoinedScheduleNode(List<HashMap<String, Object>> schedule)
	{
		HashMap<String, Object> scheduleList = new HashMap<String, Object>();
		// ���ڸ��� ���� ��� ��ȸ

		for(HashMap<String, Object> scheduleItem:schedule)
		{
			String vessel = String.valueOf( scheduleItem.get("vessel"));
			//int n_voyage = ScheduleBuildUtil.getNumericVoyage( String.valueOf(scheduleItem.get("voyage_num")));
			if(scheduleList.containsKey(vessel))
			{	
				ArrayList<HashMap<String, Object>> jointScheduleItemList = (ArrayList<HashMap<String, Object>>) scheduleList.get(vessel);
				jointScheduleItemList.add(scheduleItem);
			}
			else
			{
				ArrayList<HashMap<String, Object>> jointScheduleItemList = new ArrayList<HashMap<String, Object>>();
				jointScheduleItemList.add(scheduleItem);					
				scheduleList.put(vessel, jointScheduleItemList);
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

		// ������ �߰�
		// ����
		return nodeList;
	}

	/**
	 * outbound�����輱 ���� 
	 * @param schedule
	 * @return
	 */
	private ArrayList<DefaultMutableTreeNode> createOutboundJoinedScheduleNode(List<ScheduleData> schedule)
	{
		HashMap<String, Object> scheduleList = new HashMap<String, Object>();
		// ���� ��ȣ, ���ڸ��� ���� ��� ��ȸ

		for(ScheduleData scheduleItem:schedule)
		{
			
			//convert hashMap
			CommandMap scheduleMap=(CommandMap) objectMapper.convertValue(scheduleItem, CommandMap.class);
			
			String vessel = String.valueOf( scheduleMap.get("vessel"));
			int n_voyage = ScheduleBuildUtil.getNumericVoyage( String.valueOf(scheduleMap.get("voyage_num")));
			if(scheduleList.containsKey(vessel+"-"+n_voyage))
			{	
				ArrayList<HashMap<String, Object>> jointScheduleItemList = (ArrayList<HashMap<String, Object>>) scheduleList.get(vessel+"-"+n_voyage);
				jointScheduleItemList.add(scheduleMap);
			}
			else
			{
				ArrayList<HashMap<String, Object>> jointScheduleItemList = new ArrayList<HashMap<String, Object>>();
				jointScheduleItemList.add(scheduleMap);					
				scheduleList.put(vessel+"-"+n_voyage, jointScheduleItemList);
			}
		}

		ArrayList<DefaultMutableTreeNode> nodeList = new ArrayList<DefaultMutableTreeNode>();

		for(String strKey:scheduleList.keySet())
		{
			ArrayList<CommandMap> jointScheduleItemList=(ArrayList<CommandMap>) scheduleList.get(strKey);
			
			
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

		// ������ �߰�
		// ����
		return nodeList;
	}



	class InboundScheduleTreeNode extends DefaultMutableTreeNode
	{
		String vessel;

		String company;

		String fromDate, toDate;

		ArrayList scheduleList = new ArrayList();
		public InboundScheduleTreeNode() {
			super();
		}
		public InboundScheduleTreeNode(String string) {
			super(string);
		}

		public InboundScheduleTreeNode(TreeTableNode treeTableNode) {
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

			this.scheduleList.add(newChild);

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
		cbxNormalSearch = new KSGComboBox();
		cbxNormalSearch.addItem(new KSGTableColumn("", "��ü"));
		cbxNormalSearch.addItem(new KSGTableColumn("table_id", "���̺� ID"));
		cbxNormalSearch.addItem(new KSGTableColumn("company_abbr", "�����"));
		cbxNormalSearch.addItem(new KSGTableColumn("agent", "������Ʈ"));
		cbxNormalSearch.addItem(new KSGTableColumn("vessel", "���ڸ�"));
		cbxNormalSearch.addItem(new KSGTableColumn("voyage_num", "������"));
		cbxNormalSearch.addItem(new KSGTableColumn("n_voyage_num", "������ȣ"));		
		cbxNormalSearch.addItem(new KSGTableColumn("DateF", "�����"));
		cbxNormalSearch.addItem(new KSGTableColumn("DateT", "������"));


		JLabel lblFromPort = new JLabel("�����");		
		txfFromPort = new JTextField(10);
		txfFromPort.setEditable(false);
		JButton butSearchFromPort = new JButton("�˻�");		
		butSearchFromPort.setActionCommand("SEARCH_FROM_PORT");
		butSearchFromPort.addActionListener(this);
		JLabel lblToPort = new JLabel("������");
		txfToPort = new JTextField(10);
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
		CommandMap param = new CommandMap();

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


			// inbound ȣ���
			if(col.columnField.equals("I"))
			{	
				table.setShowPathCount(4);

				HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectInboundScheduleGroupList(param);
				
				treeTableModel.setRoot(getInboundTreeNode(result));
			}
			// outbound ȣ���
			else
			{
				table.setShowPathCount(5);		
				
				HashMap<String, Object> result = (HashMap<String, Object>) scheduleService.selectOutboundScheduleGroupList(param);
				
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

		if(command.equals("�˻�"))
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
	 * ��¥ ���� ����
	 */
	class AscendingFromDate implements Comparator<HashMap<String,Object>> 
	{ 
		//�����, ������ ����
		String dateType;
		//��������, �������� ����
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

	/**
	
	  * @ClassName : PnNormalByTree.java	  
	
	  * @Date : 2022. 4. 15. 
	
	  * @�ۼ��� : pch
	
	  * @�����̷� :
	
	  * @���α׷� ���� :
	
	  */
	class InboundScheduleNode extends DefaultMutableTreeNode
	{
		private SimpleDateFormat inputDateFormat 	= KSGDateUtil.createInputDateFormat();

		private SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();

		private String vesselName;
		
		private ArrayList<HashMap<String, Object>> scheduleList;
		
		public InboundScheduleNode(String vesselName,ArrayList<HashMap<String, Object>> scheduleList) {
			this.vesselName = vesselName;
			this.scheduleList =scheduleList;

			Iterator scheduleIter= scheduleList.iterator();

			while(scheduleIter.hasNext())
			{
				DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(new TreeTableNode((HashMap<String, Object>) scheduleIter.next()));
				add(subnode);
			}
			// ����� ���� ����
			Collections.sort(this.scheduleList, new AscendingFromDate("DateF",0) );
		}
		public String toString()
		{
			try {
				HashMap<String,Object> item = scheduleList.get(0);
				String	fromDate =outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("DateF"))));
				String toDate =outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("DateT"))));
				String company = String.valueOf(item.get("company_abbr"));
				String fromPort = String.valueOf(item.get("fromPort"));
				String toPort = String.valueOf(item.get("toPort"));
				return fromDate+"  "+vesselName + "  ("+company+")  "  +"  ["+inboundCodeMap.get(toPort)+"]"+ toDate;
			}catch(Exception e)
			{
				e.printStackTrace();
				return "error";
			}

		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		
		cbxNormalInOut.initComp();
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("code_type", "inPort");
		try {
			inboundCodeMap = (HashMap<String, Object>) codeService.selectInboundPortMap();
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	/*
	 * �����ٱ׷�node
	 * ---������node
	 * 
	 * �ιٿ��
	 * Area
	 * ---FromPort
	 * ------Vessel
	 * ---------Schedule
	 * 
	 * �ƿ��ٿ��
	 * Area
	 * ---FromPort
	 * ------ToPort
	 * ---------Vessel
	 * ------------Schedule
	 */
	
	class ScheduleTableTreeNode extends DefaultMutableTreeNode
	{
		public ScheduleTableTreeNode(TreeTableNode treeTableNode) {
			super(treeTableNode);
		}
	}
	

	
	/**
	
	  * @FileName : PnNormalByTree.java
	
	  * @Project : KSG2
	
	  * @Date : 2022. 5. 25. 
	
	  * @�ۼ��� : pch
	
	  * @�����̷� :
	
	  * @���α׷� ���� :
	
	  */
	class GroupTreeNode extends DefaultMutableTreeNode
	{
		
		private JointFormatter jointedFormatter;
		
		protected String nodeName;
		
		private ArrayList<HashMap<String, Object>> scheduleList;
		
		public GroupTreeNode(String nodeName) {
			
			this.nodeName = nodeName;
		}
		
		public GroupTreeNode (String nodeName,JointFormatter jointedFormatter, ArrayList<HashMap<String, Object>> scheduleList) {
			this(nodeName);
			this.jointedFormatter = jointedFormatter;			
			this.jointedFormatter.setNodeName(nodeName);
			this.jointedFormatter.setSchedule(scheduleList);
			
			Iterator scheduleIter= scheduleList.iterator();
			
			while(scheduleIter.hasNext())
			{
				DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(new TreeTableNode((HashMap<String, Object>) scheduleIter.next()));
				add(subnode);
			}
		}
		public String toString ()
		{
			return jointedFormatter==null?nodeName:jointedFormatter.getFormattedString();
		}
	}
}
