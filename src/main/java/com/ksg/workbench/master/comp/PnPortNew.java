package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.dtp.api.control.PortController;
import com.dtp.api.schedule.joint.tree.node.AreaTreeNode;
import com.dtp.api.service.impl.CodeServiceImpl;
import com.ibatis.sqlmap.client.SqlMapException;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.Code;
import com.ksg.domain.ScheduleData;
import com.ksg.view.comp.IconData;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.label.BoldLabel;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.InsertPortAbbrInfoDialog;
import com.ksg.workbench.master.dialog.UpdatePortInfoDialog;

import lombok.extern.slf4j.Slf4j;
import mycomp.comp.MyTable;


/**

 * @FileName : PnPort.java

 * @Date : 2021. 2. 25. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 :항구 정보 관리 화면

 */
@Slf4j
public class PnPortNew extends PnBase implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String STRING_CODE_INFO 	= "코드정보";
	
	public static final String STRING_COMMONCODE_INFO 	= "공통코드";

	private JComboBox cbxAreaName,cbxAreaCode,cbxField;

	private JTextField txfSearch;
	
	private KSGTablePanel tableArea;

	private KSGTablePanel tableH;

	private MyTable tableD;

	private JLabel lblPortName;

	private JLabel lblArea;

	private JLabel lblAreaCode;

	private JLabel lblPationality;

	private final String ACTION_INSERT_ABBR="약어 등록";
	
	private final String ACTION_DELETE_ABBR="약어 삭제";

	private KSGGradientButton butUpSearch;

	private KSGGradientButton butCancel;

	private JButton butNewAbbr;

	private JButton butDelAbbr;
	
	private JTree tree;
	
	private CodeServiceImpl codeService;

	public PnPortNew(BaseInfoUI baseInfoUI) {

		super(baseInfoUI);
		
		this.initComp();

		this.addComponentListener(this);

		this.setController(new PortController());

		this.add(buildCenter());

		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		codeService = new CodeServiceImpl();
		
		callApi("pnPort.init");

	}
	private void initComp() {
		
		cbxField = new KSGComboBox();

		cbxField.setPreferredSize(new Dimension(150,23));

		cbxField.addItem("항구명");

		cbxField.addItem("나라");

		txfSearch = new JTextField(15);
		
		txfSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					fnSearch();
				}
			}
		});
		
		butUpSearch = new KSGGradientButton("검색", "images/search3.png");

		butUpSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butUpSearch.addActionListener(this);


		butCancel = new KSGGradientButton("",  "images/init.png");

		butCancel.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cbxAreaCode.setSelectedIndex(0);
				
				cbxAreaName.setSelectedIndex(0);
				
				cbxField.setSelectedIndex(0);
				
				txfSearch.setText("");
			}
		});
		
		cbxAreaName = new KSGComboBox();

		cbxAreaCode = new KSGComboBox();

		cbxAreaName.setPreferredSize(new Dimension(300,25));

		cbxAreaCode.setPreferredSize(new Dimension(80,25));
		
		lblPortName 	= new JLabel();
		
		lblPortName.setBackground(Color.WHITE);

		lblArea 		= new JLabel();

		lblAreaCode 	= new JLabel();

		lblPationality 	= new JLabel();
		
		butNewAbbr = new KSGGradientButton("추가");

		butNewAbbr.setActionCommand("약어 등록");

		butNewAbbr.addActionListener(this);
		
		butDelAbbr = new KSGGradientButton("삭제");

		butDelAbbr.setActionCommand("약어 삭제");

		butDelAbbr.addActionListener(this);
		
	}
	/**
	 * @return
	 */
	private JComponent buildSearchPanel() {

		KSGPanel pnSearch = new KSGPanel();

		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));

		pnSearch.add(new JLabel("지역:"));

		pnSearch.add(cbxAreaName);

		pnSearch.add(new JLabel("지역코드:"));

		pnSearch.add(cbxAreaCode);

		pnSearch.add(new JLabel("필드명 : "));

		pnSearch.add(cbxField);

		pnSearch.add(txfSearch);

		pnSearch.add(butUpSearch);

		pnSearch.add(butCancel);

		Box pnSearchAndCount = Box.createVerticalBox();

		pnSearchAndCount.add(pnSearch);

		KSGPanel pnMain= new KSGPanel(new BorderLayout());

		pnMain.add(buildLine(),BorderLayout.SOUTH);

		pnMain.add(pnSearchAndCount,BorderLayout.EAST);

		pnMain.add(buildTitleIcon("항구 정보"),BorderLayout.WEST);

		return pnMain;
	}
	
	private JTree createTreeMenu() {
		
		tree = new JTree();
		
		tree.setShowsRootHandles(true);
		
		tree.setExpandsSelectedPaths(true);
		tree.setRootVisible(true);

		tree.addTreeSelectionListener(new TreeSelectionListener(){

			private String _selectedTable;


			public void valueChanged(TreeSelectionEvent e) {

				TreePath path=e.getNewLeadSelectionPath();

				if(path!=null&&path.getLastPathComponent()!=null)
				{
					CommandMap param = new CommandMap();
					
					int pathCount =path.getPathCount();
					
					System.out.println("pathCount:"+pathCount);
					
					switch (pathCount) {

					case 2:
						param.put("area_code", path.getLastPathComponent().toString());
						break;	
						
					case 3:
						
						param.put("port_area", path.getLastPathComponent().toString());

						log.info("param:"+param);

						
						break;

					default:
						break;
					}
					
					callApi("selectPort", param);

					
//					param.put("port_area", port_area);
						

//					log.info("param:"+param);
//
////					callApi("selectPort", param);
//					try {
//
//						
//
//						showBaseInfo(_selectedTable);
//
//					}
//
//					catch(SqlMapException e2)
//					{
//						JOptionPane.showMessageDialog(PnPortNew.this, _selectedTable+"에 대한 쿼리가 없습니다.");
//					}
				}
			}
		});
		
		tree.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));



		return tree;
	}
	
	public void showBaseInfo(String port_area)
	{

//		if(panelList.containsKey(baseName))
//		{	
//			cardLayout.show(pnMain, baseName);
//		}
		

		
		CommandMap param = new CommandMap();

		
		param.put("port_area", port_area);
			

		log.info("param:"+param);

		callApi("selectPort", param);

	}



//	private KSGPanel buildButton()
//	{
//		KSGPanel pnButtom = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
//
//		KSGPanel pnButtomRight = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
//
//		JButton butNewAbbr = new JButton("추가");
//
//		butNewAbbr.setActionCommand("약어 등록");
//
//		JButton butDelAbbr = new JButton("삭제");
//
//		butDelAbbr.setActionCommand("약어 삭제");
//
//		pnButtomRight.setBorder(BorderFactory.createEtchedBorder());		
//
//		butNewAbbr.addActionListener(this);
//
//		butDelAbbr.addActionListener(this);
//
//		pnButtom.add(pnButtomRight);
//
//		return pnButtom;
//	}

	private KSGPanel addComp(String name, JComponent comp)
	{
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);

		layout.setHgap(5);		

		KSGPanel pnMain = new KSGPanel(layout);

		pnMain.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		JLabel lblName = new JLabel(name);

		Font font = lblName.getFont();

		Font newFont = new Font(font.getFontName(),Font.BOLD,font.getSize());

		lblName.setFont(newFont);

		Dimension siz = lblName.getPreferredSize();

		lblName.setPreferredSize(new Dimension(75, (int) siz.getHeight()));

		pnMain.add(lblName);

		pnMain.add(comp);

		return pnMain;

	}
	private KSGPanel createPortDetail()
	{		
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));		

		pnMain.setPreferredSize(new Dimension(400, 0));

		KSGPanel pnTitle = new KSGPanel(new BorderLayout());

		pnTitle.setBackground(Color.WHITE);

		pnTitle.add(new BoldLabel("항구상세정보"),BorderLayout.WEST);

		KSGPanel pnControl = new KSGPanel(new FlowLayout());

		pnControl.add(butNewAbbr);

		pnControl.add(butDelAbbr);

		pnTitle.add(pnControl,BorderLayout.EAST);

		KSGPanel pnSubMain = new KSGPanel(new BorderLayout(5,5));

		KSGPanel pnPortInfo = new KSGPanel(new GridLayout(4,1,2,2));

		pnPortInfo.add(addComp("항구명",lblPortName));

		pnPortInfo.add(addComp("나라",lblPationality));

		pnPortInfo.add(addComp("지역",lblArea));

		pnPortInfo.add(addComp("지역코드",lblAreaCode));

		tableD = new KSGAbstractTable();

		tableD.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		KSGTableColumn dcolumns = new KSGTableColumn();

		dcolumns.columnField = "port_abbr";

		dcolumns.columnName = "항구명 약어";

		tableD.addColumn(dcolumns);

		tableD.initComp();

		pnSubMain.add(pnPortInfo,BorderLayout.NORTH);

		pnSubMain.add(new JScrollPane(tableD));

		pnMain.add(pnTitle,BorderLayout.NORTH);

		pnMain.add(pnSubMain);

		tableD.getParent().setBackground(Color.white);

		return pnMain;
	}

	private JComponent buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		tableH = new KSGTablePanel("항구목록");

		tableH.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				if(!e.getValueIsAdjusting())
				{
					if(e.getFirstIndex()<0) return;

					String portName=(String) tableH.getValueAt(e.getFirstIndex(), 0);

					String pationality = (String) tableH.getValueAt(e.getFirstIndex(), 1);

					lblPortName.setText(portName);

					lblPationality.setText(pationality);

					CommandMap commandMap = new CommandMap();

					commandMap.put("port_name", portName);

					callApi("selectPortDetailList", commandMap);					

				}
			}
		});
		
		tableArea = new KSGTablePanel("지역 정보");;
		
		tableArea.addMouseListener(new AreaTableSelectListner());
		
		KSGTableColumn columns[] = new KSGTableColumn[3];

		columns[0] = new KSGTableColumn();
		columns[0].columnField = "area_code";
		columns[0].columnName = "지역코드";
		columns[0].size = 75;

		columns[1] = new KSGTableColumn();
		columns[1].columnField = "area_name";
		columns[1].columnName = "지역명";
		columns[1].size = 300;
		columns[1].ALIGNMENT = SwingConstants.LEFT;

		columns[2] = new KSGTableColumn();
		columns[2].columnField = "area_book_code";
		columns[2].columnName = "북코드";
		columns[2].size = 75;

		tableArea.setColumnName(columns);
		
		tableArea.initComp();
				
		
		tableH.addColumn(new KSGTableColumn("port_name", "항구명",300, SwingConstants.LEFT ));
		tableH.addColumn(new KSGTableColumn("port_nationality", "나라",300, SwingConstants.LEFT ));
//		tableH.addColumn(new KSGTableColumn("port_area", "지역",300, SwingConstants.LEFT ));
//		tableH.addColumn(new KSGTableColumn("area_code", "지역코드",100));
		tableH.addColumn(new KSGTableColumn("abbr_count", "상세수",50, SwingConstants.RIGHT));

		tableH.initComp();

		tableH.setShowControl(true);

		tableH.addMouseListener(new TableSelectListner());

		tableH.addContorlListener(this);
		
		KSGPanel pnMainCenter = new KSGPanel(new BorderLayout(5,5));

		pnMainCenter.add(tableH);
		
		pnMainCenter.add(buildLeft(),BorderLayout.WEST);
		
//		pnMainCenter.add(tableArea,BorderLayout.WEST);

		pnMainCenter.add(createPortDetail(),BorderLayout.EAST);


		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);

		pnMain.add(pnMainCenter);

		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));

		return pnMain;

	}
	
	public KSGPanel buildLeft()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));		

		pnMain.setPreferredSize(new Dimension(400, 0));

		KSGPanel pnTitle = new KSGPanel(new BorderLayout());

		pnTitle.setBackground(Color.WHITE);

		KSGPanel pnControl = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		pnControl.add(new JButton("추가"));

		pnControl.add(new JButton("삭제"));
		pnControl.setVisible(false);
		
		pnTitle.add(new BoldLabel("지역 목록"),BorderLayout.WEST);

		pnTitle.add(pnControl,BorderLayout.EAST);
		
		pnTitle.setPreferredSize(new Dimension(0, 30));
	
		pnMain.add(pnTitle,BorderLayout.NORTH);

		pnMain.add(createTreeMenu());
		
		
		
		return pnMain;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals("검색"))
		{
			this.fnSearch();
		}
		else if(command.equals(KSGTablePanel.INSERT))
		{
			KSGDialog dialog = new UpdatePortInfoDialog(UpdatePortInfoDialog.INSERT);

			dialog.createAndUpdateUI();

			if(dialog.result==KSGDialog.SUCCESS)
			{
				this.fnSearch();
			}
		}
		else if(command.equals("약어 등록"))
		{
			int row=tableH.getSelectedRow();

			if(row<0) return;

			String port_name=(String) tableH.getValueAt(row, 0);

			KSGDialog dialog = new InsertPortAbbrInfoDialog(getBaseInfoUI(),port_name);

			dialog.createAndUpdateUI();

			if(dialog.result==KSGDialog.SUCCESS)
			{
				CommandMap param = new CommandMap();

				param.put("port_name", port_name);

				callApi("selectPortDetailList", param);
			}
		}
		else if(command.equals(KSGTablePanel.DELETE))
		{
			int row=tableH.getSelectedRow();

			if(row<0) return;

			String data = (String) tableH.getValueAt(row, 0);

			int result=JOptionPane.showConfirmDialog(PnPortNew.this, data+"를 삭제 하시겠습니까?", "항구 정보 삭제", JOptionPane.YES_NO_OPTION);

			if(result==JOptionPane.CANCEL_OPTION)  return;

			CommandMap param = new CommandMap();

			param.put("port_name", data);

			callApi("deletePort", param);
		}
		else if(command.equals("약어 삭제"))
		{
			int row=tableD.getSelectedRow();

			if(row<0) return;

			HashMap<String, Object> data = (HashMap<String, Object>) tableD.getValueAt(row);

			String port_name = (String)data.get("port_name");

			String port_abbr = (String)data.get("port_abbr");

			int result=JOptionPane.showConfirmDialog(this, port_abbr+"를 삭제 하시겠습니까?", "항구 약어 정보 삭제", JOptionPane.YES_NO_OPTION);

			if(result==JOptionPane.CANCEL_OPTION)  return;
			
			CommandMap param = new CommandMap();

			param.put("port_name", port_name);

			param.put("port_abbr", port_abbr);

			callApi("deletePortDetail", param);
		}
	}
	
	private void treeInit(List<AreaInfo> data) throws Exception
	{
		logger.debug("update tree model");
		
		DefaultMutableTreeNode root 	= new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16.png"),null,"전체"));
		
		
		
		Map<String, List<AreaInfo>> areaList =  data.stream().collect(
				Collectors.groupingBy(AreaInfo::getArea_code ));
		
		
		
		
		Set<String> key = areaList.keySet();
		
		for(String keyItem: key)
		{
			DefaultMutableTreeNode item = new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16_2.png"),null,keyItem));
			
			List<AreaInfo> area= areaList.get(keyItem);
			
			for(AreaInfo areaItem :area)
			{
				
//				DefaultMutableTreeNode itemSub = new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16_2.png"),null,areaItem.getArea_name()));
				DefaultMutableTreeNode itemSub = new AreaTreeNode(areaItem.getArea_name());
				item.add(itemSub);
			}
			
			root.add(item);
		}		
		
		
		tree.setModel(new DefaultTreeModel(root));
		
		tree.putClientProperty("JTree.lineStyle", "Angled");
		
		TreeCellRenderer renderer = new IconCellRenderer();
		
		tree.setCellRenderer(renderer);
		
		tree.setRowHeight(25);
		
		expandAll(tree, true);

		
	}
	
	private void viewInit() throws Exception
	{
		logger.debug("update tree model");

		HashMap<String, Object> param = new HashMap<String, Object>();

		param.put("code_type", "table");
		
		Code codeParam = Code.builder().code_type("table").build();

		List<Code>result= codeService.selectCodeDetailListByCondition(codeParam);		

		DefaultMutableTreeNode root 	= new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16.png"),null,"기초정보"));

		DefaultMutableTreeNode code 	= new DefaultMutableTreeNode(STRING_CODE_INFO);

		DefaultMutableTreeNode table 	= new DefaultMutableTreeNode("기초 정보");


		DefaultMutableTreeNode commonCode = new DefaultMutableTreeNode(STRING_COMMONCODE_INFO);

		code.add(commonCode);
		
		for(Code codeItem:result)
		{
			table.add(new DefaultMutableTreeNode(new IconData(new ImageIcon("images/db_table16_2.png"),null,codeItem.getCode_name_kor())));
		}


		root.add(code);
		root.add(table);
		tree.setModel(new DefaultTreeModel(root));
		tree.putClientProperty("JTree.lineStyle", "Angled");
		TreeCellRenderer renderer = new IconCellRenderer();
		tree.setCellRenderer(renderer);
		tree.setRowHeight(25);
		expandAll(tree, true);

		tree.setRootVisible(false);
	}
	public static void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}
	
	class AreaTableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;

		String portName;

		String pationality;

		String area;

		String areaCode;

		public void mouseClicked(MouseEvent e) 
		{	
			try
			{
				JTable es = (JTable) e.getSource();

				int row=es.getSelectedRow();
				
				if(row<0) return;

				if(e.getClickCount()>0)
				{
					
					HashMap tableparam = (HashMap) tableArea.getValueAt(row);
					
					String port_area=(String) tableparam.get("area_name");
					
					String area_code=(String) tableparam.get("area_code");
					
					
					CommandMap param = new CommandMap();

					param.put("area_code", area_code);
					param.put("port_area", port_area);
						

					log.info("param:"+param);

					callApi("selectPort", param);

				}

				if(e.getClickCount()>1)
				{				
//					HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);
//
//					dialog = new UpdatePortInfoDialog(UpdatePortInfoDialog.UPDATE,param);
//
//					dialog.createAndUpdateUI();
//
//					if(dialog.result==KSGDialog.SUCCESS)
//					{
//						fnSearch();
//					}
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(PnPortNew.this, ee.getMessage());
			}
		}
	}
	
	
	class TableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;

		String portName;

		String pationality;

		String area;

		String areaCode;

		public void mouseClicked(MouseEvent e) 
		{	
			try
			{
				if(e.getSource() instanceof JTable)
				{
					JTable es = (JTable) e.getSource();

					int row=es.getSelectedRow();
					
					if(row<0) return;

					if(e.getClickCount()>0)
					{
						CommandMap param = (CommandMap) tableH.getValueAt(row);

						lblPortName.setText((String) param.get("port_name"));

						lblPationality.setText((String) param.get("port_nationality"));

						lblArea.setText((String) param.get("port_area"));

						lblAreaCode.setText((String) param.get("area_code"));

						callApi("selectPortDetailList", param);
					}

					if(e.getClickCount()>1)
					{				
						HashMap<String, Object> param = (HashMap<String, Object>) tableH.getValueAt(row);

						dialog = new UpdatePortInfoDialog(UpdatePortInfoDialog.UPDATE,param);

						dialog.createAndUpdateUI();

						if(dialog.result==KSGDialog.SUCCESS)
						{
							fnSearch();
						}
					}	
				}
					 
				
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(PnPortNew.this, ee.getMessage());
			}
		}
	}

	@Override
	public void fnSearch() {

		CommandMap param = new CommandMap();

		if(cbxAreaCode.getSelectedIndex()>0)
		{
			param.put("area_code", cbxAreaCode.getSelectedItem());
		}

		if(cbxAreaName.getSelectedIndex()>0)
		{
			param.put("port_area", cbxAreaName.getSelectedItem());
		}

		String field = (String) cbxField.getSelectedItem();

		String searchParam = txfSearch.getText();

		if(!"".equals(searchParam))
		{
			if(field.equals("항구명"))
			{
				param.put("port_name", searchParam);

			}else if(field.equals("나라"))
			{
				param.put("port_nationality", searchParam);
			}	
		}			

		log.info("param:"+param);

		callApi("selectPort", param);

	}

	@Override
	public void componentShown(ComponentEvent e) {

		if(isShowData) fnSearch();

	}
	private void initComboBox(JComboBox combox, List list)	
	{
		combox.addItem("전체");

		list.stream().forEach(item -> combox.addItem(item));
	}
	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId=(String) result.get("serviceId");

		if("selectPort".equals(serviceId))
		{	
			try {
				List data = (List )result.get("data");

				tableH.setResultData(data);

				tableH.setTotalCount(String.valueOf(data.size()));

				if(data.size()==0)tableH.changeSelection(0,0,false,false);

				if(data.size()==0)
				{
					lblArea.setText("");
					lblAreaCode.setText("");
					lblPationality.setText("");
					lblPortName.setText("");
					tableD.clearReslult();
				}
				else
				{
					tableH.changeSelection(0,0,false,false);
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
		else if("selectPortDetailList".equals(serviceId))
		{
			List data = (List )result.get("data");
			
			tableD.setResultData(data);

		}
		else if("pnPort.init".equals(serviceId))
		{
			List<String> araaCode =(List<String>) result.get("areaCode");
			
			List<String> araaName =(List<String>) result.get("areaName");
			
			List araaList =(List<String>) result.get("areaList");
			
			
			
			
			initComboBox(cbxAreaName, araaName);
			
			initComboBox(cbxAreaCode, araaCode);
			
//			tableArea.setResultData(araaList);
			
			
			try {
				treeInit(araaList);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		else if("deletePort".equals(serviceId))
		{
			fnSearch();

		}
		else if("deletePortDetail".equals(serviceId))
		{
			int row=tableH.getSelectedRow();

			String port_name=(String) tableH.getValueAt(row, 0);

			CommandMap param = new CommandMap();

			param.put("port_name", port_name);

			NotificationManager.showNotification("삭제되었습니다.");

			callApi("selectPortDetailList", param);
		}
	}
	class IconCellRenderer 
	extends    JLabel 
	implements TreeCellRenderer
	{
		protected Color m_bkNonSelectionColor;
		protected Color m_bkSelectionColor;
		protected Color m_borderSelectionColor;
		protected boolean m_selected;
		protected Color m_textNonSelectionColor;

		protected Color m_textSelectionColor;

		public IconCellRenderer()
		{
			super();
			m_textSelectionColor = UIManager.getColor(
					"Tree.selectionForeground");
			m_textNonSelectionColor = UIManager.getColor(
					"Tree.textForeground");
			m_bkSelectionColor = UIManager.getColor(
					"Tree.selectionBackground");
			m_bkNonSelectionColor = UIManager.getColor(
					"Tree.textBackground");
			m_borderSelectionColor = UIManager.getColor(
					"Tree.selectionBorderColor");
			setOpaque(false);
		}

		public Component getTreeCellRendererComponent(JTree tree, 
				Object value, boolean sel, boolean expanded, boolean leaf, 
				int row, boolean hasFocus) 
		{
			DefaultMutableTreeNode node = 
					(DefaultMutableTreeNode)value;
			Object obj = node.getUserObject();
			setText(obj.toString());

			if (obj instanceof Boolean)
				setText("Retrieving data...");

			if (obj instanceof IconData)
			{
				IconData idata = (IconData)obj;
				if (expanded)
					setIcon(idata.getExpandedIcon());
				else
					setIcon(idata.getIcon());
			}
			else
				setIcon(null);

			setFont(tree.getFont());
			setForeground(sel ? m_textSelectionColor : 
				m_textNonSelectionColor);
			setBackground(sel ? m_bkSelectionColor : 
				m_bkNonSelectionColor);
			m_selected = sel;
			return this;
		}

		public void paintComponent(Graphics g) 
		{
			Color bColor = getBackground();
			Icon icon = getIcon();

			g.setColor(bColor);
			int offset = 0;
			if(icon != null && getText() != null) 
				offset = (icon.getIconWidth() + getIconTextGap());
			g.fillRect(offset, 0, getWidth() - 1 - offset,
					getHeight() - 1);

			if (m_selected) 
			{
				g.setColor(m_borderSelectionColor);
				g.drawRect(offset, 0, getWidth()-1-offset, getHeight()-1);
			}
			super.paintComponent(g);
		}
	}
	public static void expandAll(JTree tree, TreePath parent, boolean expand) {

		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}
	
	
}