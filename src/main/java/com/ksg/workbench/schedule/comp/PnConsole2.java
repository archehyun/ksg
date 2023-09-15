package com.ksg.workbench.schedule.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.dtp.api.control.ScheduleController;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.AreaInfo;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.view.comp.textfield.SearchTextField;
import com.ksg.workbench.schedule.dialog.SearchPortDialog;


/**

 * @FileName : PnConsole2.java

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

	private KSGComboBox cbxNormalInOut;

	private KSGComboBox cbxNormalSearch;

	private JTextField txfNoramlSearch;
	
	private SearchTextField txfFromPort;

	private SearchTextField txfToPort;

	private CommandMap searchParam;
	
	private KSGComboBox cbxArea;

	public PnConsole2() {

		super();
		
		this.setController(new ScheduleController());
		
		this.addComponentListener(this);

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
		tableH.addColumn(new KSGTableColumn("dateF", "출발일", 90));
		tableH.addColumn(new KSGTableColumn("dateT", "도착일", 90));
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

		cbxNormalInOut.setPreferredSize(new Dimension(100,23));

		cbxNormalInOut.setShowTotal(true);

		cbxNormalInOut.initComp();

		cbxNormalSearch = new KSGComboBox();

		cbxNormalSearch.setPreferredSize(new Dimension(150,25));

		cbxNormalSearch.addItem(new KSGTableColumn("", "전체"));

		cbxNormalSearch.addItem(new KSGTableColumn("table_id", "테이블 ID"));

		cbxNormalSearch.addItem(new KSGTableColumn("company_abbr", "선사명"));

		cbxNormalSearch.addItem(new KSGTableColumn("agent", "에이전트"));

		cbxNormalSearch.addItem(new KSGTableColumn("vessel", "선박명"));

		cbxNormalSearch.addItem(new KSGTableColumn("voyage_num", "항차명"));

		cbxNormalSearch.addItem(new KSGTableColumn("n_voyage_num", "항차번호"));

		cbxNormalSearch.addItem(new KSGTableColumn("fromPort", "출발항"));

		cbxNormalSearch.addItem(new KSGTableColumn("toPort", "도착항"));

		cbxNormalSearch.addItem(new KSGTableColumn("fromDate", "출발일"));

		cbxNormalSearch.addItem(new KSGTableColumn("toDate", "도착일"));	

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

		cbxArea = new KSGComboBox();

		cbxArea.setPreferredSize(new Dimension(250,23));

		JLabel lblFromPort = new JLabel("출발항");
		
		lblFromPort.setFont(labelFont);

		txfFromPort = new SearchTextField();

		txfFromPort.setPreferredSize(new Dimension(150,25));

		txfFromPort.setActionCommand("SEARCH_FROM_PORT");

		JLabel lblToPort = new JLabel("도착항");
		
		lblToPort.setFont(labelFont);

		txfToPort = new SearchTextField();

		txfToPort.setPreferredSize(new Dimension(150,25));

		txfToPort.setActionCommand("SEARCH_TO_PORT");

		txfFromPort.addActionListener(this);

		txfToPort.addActionListener(this);		

		KSGPanel pnPortSearch = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		pnPortSearch.add(lblFromPort);

		pnPortSearch.add(txfFromPort);

		pnPortSearch.add(lblToPort);

		pnPortSearch.add(txfToPort);

		KSGGradientButton butSearch = new KSGGradientButton("검색", "images/search3.png");

		butSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		KSGGradientButton butCancel = new KSGGradientButton("",  "images/init.png");

		butCancel.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butSearch.addActionListener(this);

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

		JLabel lbl1 = new JLabel("구분:");
		JLabel lbl2 = new JLabel("지역:");
		JLabel lbl3 = new JLabel("항목:");
		
		lbl1.setFont(labelFont);
		lbl2.setFont(labelFont);
		lbl3.setFont(labelFont);
		
		pnNormalSearchCenter.add(lbl1);

		pnNormalSearchCenter.add(cbxNormalInOut);

		pnNormalSearchCenter.add(lbl2);

		pnNormalSearchCenter.add(cbxArea);

		pnNormalSearchCenter.add(pnPortSearch);

		pnNormalSearchCenter.add(lbl3);

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

			searchParam.put("gubun", "console");			

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

			callApi("pnNormal2.fnSearch", searchParam);

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
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId = (String) result.get("serviceId");

		if("pnNormal2.fnSearch".equals(serviceId)) {

			List data = (List )result.get("data");

			tableH.setResultData(data);

		}
		else if("pnNormal2.init".equals(serviceId)) {

			List<AreaInfo> areaList=(List<AreaInfo>) result.get("areaList");

			cbxArea.removeAllItems();

			cbxArea.addItem(new KSGTableColumn("", "전체"));

			areaList.stream()	.sorted(Comparator.comparing(AreaInfo::getArea_name))
			.forEach(o->cbxArea.addItem(new KSGTableColumn(o.getArea_name(), o.getArea_name())));

		}
	}
	
	@Override
	public void componentShown(ComponentEvent e) {

		callApi("pnNormal2.init");
	}
}