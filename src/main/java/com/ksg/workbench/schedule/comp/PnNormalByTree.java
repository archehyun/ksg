package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.springframework.stereotype.Component;

import com.dtp.api.control.ScheduleController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ScheduleEnum;
import com.ksg.service.AreaService;
import com.ksg.service.impl.AreaServiceImpl;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.workbench.common.comp.View;
import com.ksg.workbench.common.comp.button.ImageButton;
import com.ksg.workbench.common.comp.label.BoldLabel;
import com.ksg.workbench.common.comp.panel.KSGPageTablePanel;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.common.comp.treetable.KSGTreeTable;
import com.ksg.workbench.common.comp.treetable.nodemager.TreeNodeManager;
import com.ksg.workbench.schedule.dialog.SearchPortDialog;


/**

 * @FileName : PnOutbound.java

 * @Project : KSG2

 * @Date : 2022. 3. 7. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 스케줄 조회 후 트리 형태로 표시 , 공동 배선 적용

 */

@Component("schedule")
public class PnNormalByTree extends PnSchedule implements View {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private KSGPageTablePanel tableH;	

	private KSGComboBox cbxNormalInOut;

	private KSGComboBox cbxArea;

	private JComboBox<KSGTableColumn> cbxNormalSearch;

	private JCheckBox chkRoute;

	private JTextField txfNoramlSearch;	

	private KSGTreeTable table;

	private ScheduleTreeTableModel treeTableModel;

	private JTextField txfToPort;

	private JTextField txfFromPort;
	
	private JCheckBox cbxIsAddValidate;

	private HashMap<String, Object> inboundCodeMap;

	private TreeNodeManager nodeManager = new TreeNodeManager();

	private AreaService areaService = new AreaServiceImpl();

	private ScheduleController control = new ScheduleController(); 

	protected ObjectMapper objectMapper;

	private KSGPanel pnRouteSerchOption;

	private JRadioButton rbtRouteDateSorted;

	private JRadioButton rbtRouteVesselSorted;
	
	private JLabel lblCount = new JLabel("0");

	private JLabel lblTotalCount;

	public PnNormalByTree() {

		super();

		codeService = new CodeServiceImpl();

		objectMapper = new ObjectMapper();

		this.setLayout(new BorderLayout());
		
		this.setController(new ScheduleController());

		this.addComponentListener(this);

		add(buildSearch(),BorderLayout.NORTH);

		add(buildCenter());
		
		

	}

	private java.awt.Component buildSouth() {
		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnMain.add(new JLabel("총"));
		pnMain.add(lblCount);
		return pnMain;
	}

	public KSGPanel buildCenter()
	{

		treeTableModel = new ScheduleTreeTableModel();

		treeTableModel.addColumn(new KSGTableColumn("", "",2000));

		treeTableModel.addColumn(new KSGTableColumn("table_id", "테이블 ID",100));

		treeTableModel.addColumn(new KSGTableColumn("company_abbr", "선사명",100));

		treeTableModel.addColumn(new KSGTableColumn("agent", "에이전트",100));

		treeTableModel.addColumn(new KSGTableColumn("vessel", "선박명",200));

		treeTableModel.addColumn(new KSGTableColumn("voyage_num", "항차번호"));

		treeTableModel.addColumn(new KSGTableColumn("fromPort", "출발항",200));

		treeTableModel.addColumn(new KSGTableColumn("dateF", "출발일", 90));

		treeTableModel.addColumn(new KSGTableColumn("dateT", "도착일", 90));

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
		
		pnMain.add(createTitle(),BorderLayout.NORTH);
		
		return pnMain;
	}
	public JComponent createTitle() {

		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		

		String title = "스케줄 목록";
	

		lblTotalCount = new JLabel("0");
		lblTotalCount.setForeground(Color.red);



		pnTitle.add(new BoldLabel(title + " 총"));
		pnTitle.add(lblTotalCount);
		pnTitle.add(new JLabel("건"));


		pnMain.add(pnTitle,BorderLayout.LINE_START);
		

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


		cbxNormalInOut.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(cbxNormalInOut.getSelectedItem() ==null)return;

				String selectedValue = cbxNormalInOut.getSelectedItem().toString();

				chkRoute.setEnabled("OUTBOUND".equals(selectedValue));
				
				pnRouteSerchOption.setVisible("ROUTE".equals(selectedValue));			}
		});

		chkRoute = new JCheckBox("Route");
		chkRoute.setBackground(Color.white);
		
		cbxIsAddValidate = new JCheckBox("제외 항구 추가");
		
		cbxIsAddValidate.setBackground(Color.white);

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

		JButton butSearch = new JButton("검색");
		
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

		butSearch.addActionListener(this);

		pnNormalSearchCenter.add(new JLabel("구분:"));

		pnNormalSearchCenter.add(cbxNormalInOut);

		pnNormalSearchCenter.add(new JLabel("지역:"));

		pnNormalSearchCenter.add(cbxArea);

		pnNormalSearchCenter.add(pnPortSearch);

		pnNormalSearchCenter.add(new JLabel("항목:"));

		pnNormalSearchCenter.add(cbxNormalSearch);

		pnNormalSearchCenter.add(txfNoramlSearch);

		pnRouteSerchOption = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		
		pnRouteSerchOption.setVisible(false);

		pnRouteSerchOption.add(new JLabel("정렬:"));
		
		rbtRouteDateSorted = new JRadioButton("날짜");
		
		rbtRouteVesselSorted = new JRadioButton("선박");
		
		rbtRouteDateSorted.setBackground(Color.white);
		
		rbtRouteVesselSorted.setBackground(Color.white);
		
		 ButtonGroup group = new ButtonGroup();
		 
		 group.add(rbtRouteDateSorted);
		 
		 group.add(rbtRouteVesselSorted);
		 
		 pnRouteSerchOption.add(rbtRouteDateSorted);
		 
		 pnRouteSerchOption.add(rbtRouteVesselSorted);
		 
		 pnRouteSerchOption.add(cbxIsAddValidate);
		 
		 
		 rbtRouteDateSorted.setSelected(true);
		 
		 rbtRouteVesselSorted.setSelected(false);
		
		pnNormalSearchCenter.add(pnRouteSerchOption);
		

		KSGPanel pnNormalSeawrchEast = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		pnNormalSeawrchEast.add(butSearch);
		
		pnNormalSeawrchEast.add(butCancel);

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

			if(input_date!=null&&!input_date.equals(""))
			{
				param.put("date_issue", input_date);
			}
			else
			{
				JOptionPane.showMessageDialog(PnNormalByTree.this, "스케줄 생성 일자를 선택하십시요");
				return;
			}
			
			param.put("sortType", rbtRouteDateSorted.isSelected()?"date":"vessel");
			
			param.put("isAddValidate", cbxIsAddValidate.isSelected());

			logger.info("param:"+param);
			
			// inbound 호출시
			if(col.columnField.equals(ScheduleEnum.OUTBOUND.getSymbol()))
			{	
				param.put("depth", 5);
			}
			// outbound 호출시
			else
			{
				param.put("depth", 4);
			}
			
			

			callApi("pnNormalByTree.fnSearch", param);
			
			

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

		callApi("pnNormalByTree.init");

	}
	
	@Override
	public void updateView() {
		
		CommandMap result= this.getModel();

		boolean success = (boolean) result.get("success");
		
		if(success)
		{
			String serviceId = (String) result.get("serviceId");
			
			if("pnNormalByTree.init".equals(serviceId)) {
				
				cbxNormalInOut.initComp();	

				cbxNormalInOut.addItem(new KSGTableColumn("R", "ROUTE"));
				
				cbxArea.removeAllItems();
				
				cbxArea.addItem(new KSGTableColumn("", "전체"));
				
				List<String> areaList=(List<String>) result.get("areaList");
				
				
				areaList.stream()
						.forEach(areaName->cbxArea.addItem(new KSGTableColumn(areaName, areaName)));
				
				inboundCodeMap = (HashMap<String, Object>) result.get("inboundCodeMap");
			}
			else if("pnNormalByTree.fnSearch".equals(serviceId)) {
				
				Object treeNode = result.get("treeNode");
				
				int depth = (int) result.get("depth");
				
				int count = result.containsKey("count")?(int) result.get("count"):0;
				
				lblTotalCount.setText(String.valueOf(count));
				
				table.setShowPathCount(depth);
				
				treeTableModel.setRoot(treeNode);

				table.setTreeExpandedState(true);

				table.updateUI();
			
				
				
			}
			
			else if("deleteSchedule".equals(serviceId)) {
				
				int deleteCount = (int) result.get("deleteCount");
				
				JOptionPane.showMessageDialog(this, deleteCount+"건을 삭제 했습니다.");
				
				callApi("scheduleViewUpdate");
			}
		}
		else{  
			String error = (String) result.get("error");
			JOptionPane.showMessageDialog(this, error);
		}
	}

	

}
