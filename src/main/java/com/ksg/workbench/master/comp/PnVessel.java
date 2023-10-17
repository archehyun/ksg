package com.ksg.workbench.master.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

import com.dtp.api.control.VesselController;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.button.KSGGradientButton;
import com.ksg.view.comp.combobox.KSGComboBox;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.label.BoldLabel;
import com.ksg.view.comp.notification.NotificationManager;
import com.ksg.view.comp.panel.KSGPageTablePanel;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.adv.comp.SimpleFileFilter;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.InsertVesselAbbrInfoDialog;
import com.ksg.workbench.master.dialog.InsertVesselInfoDialog;
import com.ksg.workbench.master.dialog.UpdateVesselInfoDialog;
import com.ksg.workbench.master.dialog.VesselImportDialog;

import lombok.extern.slf4j.Slf4j;
import mycomp.comp.MyTable;


/**

 * @FileName : PnVessel.java

 * @Date : 2021. 3. 2. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 선박 관리 화면

 */
@SuppressWarnings("serial")
@Slf4j
public class PnVessel extends PnBase implements ActionListener {

	private static final String STRING_ALL_DELETE 		= "전체 선박 데이터 삭제";
	private static final String STRING_VESSEL_TYPE 		= "선박타입";
	private static final String STRING_INPUTDATE 		= "등록일";
	private static final String STRING_VESSEL_COMPANY 	= "대표선사";
	private static final String STRING_VESSEL_MMSI 		= "MMSI";
	private static final String STRING_VESSEL_NAME 		= "선박명";
	private static final String STRING_VESSEL_ABBR 		= "선박명약어";
	private static final String STRING_CONTAINER_TYPE 	= "컨테이너 타입";
	private static final String STRING_ALL 				= "전체";
	private static final String STRING_SEARCH 			= "검색";
	private static final String STRING_IMPORT 			= "가져오기";
	private static final String STRING_EXPORT 			= "내보내기";
	/**
	 * 
	 */
	private KSGPropertis 	propertis = KSGPropertis.getIntance();

	private JTextField txfSearch;

	private JComboBox<KSGTableColumn> cbxField;

	private KSGComboBox cbxVesselType; // 선박 타입 선택

	private JComboBox cbxUse;// 사용 유무 선택

	private KSGTablePanel tableH;

	private SelectionListner selectionListner = new SelectionListner();

	private VesselService vesselService = new VesselServiceImpl();

//	private CodeServiceImpl codeService= new CodeServiceImpl();

	private MyTable tableD;

	private JLabel lblVesselName;

	private JLabel lblVesselMMSI;

	private JLabel lblVesselType;

	private JLabel lblVesselCompany;

	private JLabel lblVesselUse;

	private JLabel lblInputDate;
	private KSGGradientButton butUpSearch;
	private KSGGradientButton butCancel;

	public PnVessel(BaseInfoUI baseInfoUI) {

		super(baseInfoUI);
		
		this.initComp();

		this.setController(new VesselController());

		this.addComponentListener(this);

		this.add(buildCenter());

		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}
	public PnVessel() {

		super();
		
		this.initComp();

		this.setController(new VesselController());

		this.addComponentListener(this);

		this.add(buildCenter());

		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}

	private void initComp() {
		
		cbxField = new KSGComboBox();

		cbxField.addItem(new KSGTableColumn("vessel_name",STRING_VESSEL_NAME));

		cbxField.addItem(new KSGTableColumn("vessel_abbr",STRING_VESSEL_ABBR));

		cbxField.addItem(new KSGTableColumn("vessel_mmsi",STRING_VESSEL_MMSI));

		cbxField.addItem(new KSGTableColumn("vessel_company",STRING_VESSEL_COMPANY));

		cbxField.addItem(new KSGTableColumn("input_date",STRING_INPUTDATE));
		
		cbxField.setPreferredSize(new Dimension(150,23));

		txfSearch = new JTextField(15);

		txfSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				if(e.getKeyChar()==KeyEvent.VK_ENTER)
				{
					fnSearch();
				}
			}
		});
		
		butUpSearch = new KSGGradientButton(STRING_SEARCH, "images/search3.png");

		butUpSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butUpSearch.addActionListener(this);

		butCancel = new KSGGradientButton("",  "images/init.png");

		butCancel.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));

		butCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				cbxUse.setSelectedIndex(0);
				cbxVesselType.setSelectedIndex(0);
				cbxField.setSelectedIndex(0);
				txfSearch.setText("");
			}
		});

		cbxVesselType = new KSGComboBox();

		cbxVesselType.addItem(new KSGTableColumn("", STRING_ALL));
		
		cbxVesselType.setPreferredSize(new Dimension(150,23));

		cbxUse = new KSGComboBox();

		cbxUse.addItem(STRING_ALL);

		cbxUse.addItem("사용함");

		cbxUse.addItem("사용안함");
		
		cbxUse.setPreferredSize(new Dimension(100,23));
		
		lblVesselName = new JLabel();

		lblVesselMMSI = new JLabel();

		lblVesselType = new JLabel();

		lblVesselCompany = new JLabel();

		lblVesselUse = new JLabel();

		lblInputDate = new JLabel();
	}

	private JComponent buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		tableH = new KSGTablePanel("선박목록");

		KSGTableColumn columns[] = new KSGTableColumn[6];

		columns[0] = new KSGTableColumn();
		columns[0].columnField = "vessel_name";
		columns[0].columnName = STRING_VESSEL_NAME;
		columns[0].size = 200;
		columns[0].ALIGNMENT = SwingConstants.LEFT;

		columns[1] = new KSGTableColumn();
		columns[1].columnField = "vessel_mmsi";
		columns[1].columnName = STRING_VESSEL_MMSI;
		columns[1].size = 80;

		columns[2] = new KSGTableColumn();
		columns[2].columnField = "vessel_type";
		columns[2].columnName = STRING_VESSEL_TYPE;
		columns[2].size = 80;

		columns[3] = new KSGTableColumn();
		columns[3].columnField = "vessel_company";
		columns[3].columnName = STRING_VESSEL_COMPANY;
		columns[3].size = 200;

		columns[4] = new KSGTableColumn()
		{
			public Object getValue(Object obj)
			{
				return (Integer)obj==0?"Y":"N";							
			}
		};
		columns[4].columnField = "vessel_use";
		columns[4].columnName = "사용유무";
		columns[4].size = 70;


		columns[5] = new KSGTableColumn();
		columns[5].columnField = "input_date";
		columns[5].columnName = STRING_INPUTDATE;
		columns[5].size = 100;

		tableH.setColumnName(columns);

		tableH.initComp();

		tableH.addMouseListener(new TableSelectListner());

		tableH.setShowControl(true);

		tableH.addContorlListener(this);

		tableH.getSelectionModel().addListSelectionListener(selectionListner);

		KSGPanel pnMainCenter = new KSGPanel(new BorderLayout(5,5));

		pnMainCenter.add(tableH);

		pnMainCenter.add(createVesselDetail(),BorderLayout.EAST);	

		pnMain.add(pnMainCenter);

		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);	

		pnMain.setBorder(BorderFactory.createEmptyBorder(0,7,5,7));


		return pnMain;

	}
	/**
	 * 검색 항목 표시 패널
	 * @return
	 */
	private JComponent buildSearchPanel() {

		KSGPanel pnSearch = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		pnSearch.add(new JLabel("사용유무 : "));
		pnSearch.add(cbxUse);		
		pnSearch.add(new JLabel("선박타입 : "));
		pnSearch.add(cbxVesselType);
		pnSearch.add(new JLabel("필드명 : "));
		pnSearch.add(cbxField);
		pnSearch.add(txfSearch);
		pnSearch.add(butUpSearch);
		pnSearch.add(butCancel);
		Box pnSearchAndCount = Box.createVerticalBox();
		
		pnSearchAndCount.add(pnSearch);

		KSGPanel pnCountInfo = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));	

		pnSearchAndCount.add(pnCountInfo);	

		KSGPanel pnInfo= new KSGPanel(new BorderLayout());

		pnInfo.add(buildLine(),BorderLayout.SOUTH);
		
		pnInfo.add(pnSearchAndCount,BorderLayout.EAST);
		
		pnInfo.add(buildTitleIcon("선박 정보"),BorderLayout.WEST);
		
		return pnInfo;
	}

	/**하단 버튼 목록 생성
	 * @return
	 */
	private JComponent buildButton()
	{
		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butExport 		= new KSGGradientButton(STRING_EXPORT);

		JButton butImport 		= new KSGGradientButton(STRING_IMPORT);

		JButton butVesselDel 	= new KSGGradientButton(STRING_ALL_DELETE);

		butExport.addActionListener(this);

		butImport.addActionListener(this);

		butVesselDel.addActionListener(this);

		pnMain.add(butExport);

		pnMain.add(butImport);

		pnMain.add(butVesselDel);

		return pnMain;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if(command.equals(STRING_SEARCH))
		{
			fnSearch();
		}
		else if(command.equals(KSGPageTablePanel.DELETE))
		{
			deleteVesselAction();
		}
		else if(command.equals(KSGPageTablePanel.INSERT))
		{
			insertAction();
		}
		else if(command.equals("약어삭제"))
		{
			int rowH = tableH.getSelectedRow();

			int rowD = tableD.getSelectedRow();

			if(rowH<0||rowD<0) return;

			HashMap<String, Object> itemH = (HashMap<String, Object>) tableH.getValueAt(rowH);

			HashMap<String, Object> itemD=(HashMap<String, Object>) tableD.getValueAt(rowD);

			CommandMap param = new CommandMap();

			param.put("vessel_name",itemH.get("vessel_name"));

			param.put("vessel_abbr",itemD.get("vessel_abbr"));

			callApi("deleteVesselDetail", param);

		}
		else if(command.equals("약어등록"))
		{
			int row=tableH.getSelectedRow();

			if(row<0) return;

			HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(tableH.getSelectedRow());

			String vessel_name = (String)item.get("vessel_name");

			KSGDialog dialog = new InsertVesselAbbrInfoDialog(vessel_name);

			dialog.createAndUpdateUI();

			fnSearchDetail(vessel_name);

		}
		else if(command.equals(STRING_EXPORT))
		{
			try {
				exportAction();
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(PnVessel.this, e1.getMessage());
			}
		}
		else if(command.equals(STRING_IMPORT))
		{
			importAction();
		}
		else if(command.equals(STRING_ALL_DELETE))
		{
			deleteAllAction();
		}
	}

	private void exportAction() throws SQLException
	{
//		String fileName=JOptionPane.showInputDialog(KSGModelManager.getInstance().frame, "파일명을 입력 하세요");
//
//		// 파일 이름 생성
//		if(fileName==null||fileName.equals("")) return;
//
//		// TODO VESSEL EXPORT 수정
//		VesselInfoExportCommand command = new VesselInfoExportCommand(fileName);
//
//		command.execute();
		
		callApi("pnVessel.export");
	}


	/**
	 *엑셀 데이터 가져오기 
	 */
	private void importAction()
	{
		JFileChooser fileChooser = new JFileChooser(propertis.getProperty("dataLocation"));

		fileChooser.setMultiSelectionEnabled(true);

		String[] pics = new String[] { ".xls"};

		fileChooser.addChoosableFileFilter(new SimpleFileFilter(pics, "Excel(*.xls)"));

		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

		File selectedFile = fileChooser.getSelectedFile();

		VesselImportDialog dialog = new VesselImportDialog(selectedFile);

		dialog.createAndUpdateUI();


	}
//	/**선박 사용 항목  가져오기
//	 * @param vesselUseCell
//	 * @return
//	 */
//	private int getVesselUse(Cell vesselUseCell)
//	{
//		int vesselUse;
//		try{
//			switch (vesselUseCell.getCellType()) {
//			case HSSFCell.CELL_TYPE_STRING:
//				vesselUse= Integer.valueOf(vesselUseCell.getStringCellValue());
//
//				break;
//			case HSSFCell.CELL_TYPE_NUMERIC:
//				vesselUse =(int) vesselUseCell.getNumericCellValue();
//				break;
//			case HSSFCell.CELL_TYPE_BLANK:
//				vesselUse =Vessel.USE;
//			default:
//				vesselUse =Vessel.USE;
//				break;
//			}
//		}catch(Exception e)
//		{
//			return Vessel.USE;
//		}
//
//		return vesselUse;
//	}

	/**
	 * 
	 */
	private void insertAction() {

		log.debug("insert");

		KSGDialog dialog = new InsertVesselInfoDialog();

		dialog.createAndUpdateUI();

		switch (dialog.result) {

		case KSGDialog.SUCCESS:

			fnSearch();

			break;
		case KSGDialog.FAILE:

			break;	

		default:
			//fnSearch();
			break;
		}

	}
	// 컨트롤러로 이관
	private void deleteAllAction()
	{
		String result= JOptionPane.showInputDialog(KSGModelManager.getInstance().frame, "데이터를 삭제 하시려면 '삭제확인'을 입력 하십시요");

		if(result.equals("삭제확인"))
		{
			try {

				vesselService.delete(new HashMap<String, Object>());

				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "데이터를 삭제 했습니다.");

			} catch (SQLException e1) {

				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, e1.getMessage());
			}
		}
	}

	/**
	 * 
	 */
	private void deleteVesselAction() {
		
		log.info("delete");

		int row = tableH.getSelectedRow();

		if(row<0) return;

		HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(row);

		String vessel_name = (String) item.get("vessel_name");

		int result=JOptionPane.showConfirmDialog(this,vessel_name+"를 삭제 하시겠습니까?", "선박 정보 삭제", JOptionPane.YES_NO_OPTION);

		if(result==JOptionPane.OK_OPTION)
		{	
			CommandMap param = new CommandMap();

			param.put("vessel_name", vessel_name);

			callApi("deleteVessel", param);
		}
	}

	public void fnSearch()
	{
		log.info("search");
		
		CommandMap param = new CommandMap();	

		if(cbxVesselType.getSelectedIndex()>0)
		{
			KSGTableColumn comboItem =(KSGTableColumn) cbxVesselType.getSelectedItem();
			
			param.put("vessel_type", comboItem.columnField);
		}

		if(cbxUse.getSelectedIndex()>0)
		{
			param.put("vessel_use", cbxUse.getSelectedItem().equals("사용안함")?Vessel.NON_USE:Vessel.USE);
		}

		if(!"".equals(txfSearch.getText()))
		{

			KSGTableColumn col =(KSGTableColumn) cbxField.getSelectedItem();

			param.put(col.columnField, txfSearch.getText());
		}
		
		callApi("selectVessel", param);
	}

	private JComponent createVesselDetail()
	{		

		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		pnMain.setPreferredSize(new Dimension(400, 0));

		KSGPanel pnTitle = new KSGPanel(new BorderLayout());

		pnTitle.setBackground(Color.WHITE);

		pnTitle.add(new BoldLabel("선박상세정보"),BorderLayout.WEST);

		KSGPanel pnControl = new KSGPanel(new FlowLayout());

		JButton butNewAbbr = new KSGGradientButton("추가");

		butNewAbbr.setActionCommand("약어등록");

		JButton butDelAbbr = new KSGGradientButton("삭제");

		butDelAbbr.setActionCommand("약어삭제");

		pnControl.add(butNewAbbr);

		pnControl.add(butDelAbbr);

		butNewAbbr.addActionListener(this);

		butDelAbbr.addActionListener(this);

		pnTitle.add(pnControl,BorderLayout.EAST);

		KSGPanel pnSubMain = new KSGPanel(new BorderLayout(5,5));

		KSGPanel pnPortInfo = new KSGPanel(new GridLayout(5,1,2,2));

		pnPortInfo.add(addComp("선박명",lblVesselName));

		pnPortInfo.add(addComp("MMSI",lblVesselMMSI));

		pnPortInfo.add(addComp("선박타입",lblVesselType));

		pnPortInfo.add(addComp("대표선사",lblVesselCompany));

		pnPortInfo.add(addComp("등록일",lblInputDate));

		tableD = new KSGAbstractTable();

		tableD.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		KSGTableColumn dcolumns = new KSGTableColumn();

		dcolumns.columnField = "vessel_abbr";

		dcolumns.columnName = "선박명 약어";

		tableD.addColumn(dcolumns);

		tableD.initComp();

		pnSubMain.add(pnPortInfo,BorderLayout.NORTH);

		pnSubMain.add(new JScrollPane(tableD));

		pnMain.add(pnTitle,BorderLayout.NORTH);

		pnMain.add(pnSubMain);

		pnMain.add(buildButton(),BorderLayout.SOUTH);

		tableD.getParent().setBackground(Color.white);

		return pnMain;
	}
	
	private JComponent addComp(String name, JComponent comp)
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

	class TableSelectListner extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) 
		{			
			if(e.getClickCount()>1)
			{
				int row=tableH.getSelectedRow();

				if(row<0) return;

				HashMap<String, Object> item = (HashMap<String, Object>) tableH.getValueAt(row);

				UpdateVesselInfoDialog dialog = new UpdateVesselInfoDialog(item);

				dialog.createAndUpdateUI();

				if(dialog.result==KSGDialog.SUCCESS) fnSearch();

			}
		}
	}


	@Override
	public void componentShown(ComponentEvent e) {

		CommandMap param = new CommandMap();

		param.put("code_type", "conType");

		callApi("pnVessel.init", param);
	}

	class SelectionListner implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e) {

			if(!e.getValueIsAdjusting())
			{
				int row=tableH.getSelectedRow();

				if(row<0) return;

				String vessel_name=(String) tableH.getValueAt(row, 0);

				String vessel_mmsi=(String) tableH.getValueAt(row, 1);

				String vessel_type=(String) tableH.getValueAt(row, 2);

				String vessel_company=(String) tableH.getValueAt(row, 3);

				String vessel_use=String.valueOf(tableH.getValueAt(row, 4));

				String input_date=(String) tableH.getValueAt(row, 5);

				lblVesselName.setText(vessel_name);

				lblVesselCompany.setText(vessel_company);

				lblVesselType.setText(vessel_type);

				lblVesselMMSI.setText(vessel_mmsi);

				lblVesselUse.setText(vessel_use);

				lblInputDate.setText(input_date);

				fnSearchDetail(vessel_name);
			}
		}
	}
	public void fnSearchDetail(String vessel_name)
	{	
		CommandMap param = new CommandMap();

		param.put("vessel_name", vessel_name);

		callApi("selectVesselDetailList", param);
	}

	@Override
	public void updateView() {

		CommandMap result= this.getModel();

		String serviceId=(String) result.get("serviceId");

		if("selectVessel".equals(serviceId))
		{	
			List data = (List )result.get("data");

			tableH.setResultData(data);

			tableH.setTotalCount(String.valueOf(data.size()));

			if(data.size()==0)tableH.changeSelection(0,0,false,false);

			if(data.size()==0)
			{
				tableD.clearReslult();
			}
			else
			{
				tableH.changeSelection(0,0,false,false);
			}
		}
		else if("selectVesselDetailList".equals(serviceId))
		{
			List data = (List )result.get("data");

			tableD.setResultData(data);
		}

		else if("deleteVessel".equals(serviceId))
		{
			NotificationManager.showNotification("삭제되었습니다.");

			fnSearch();

			tableD.clearReslult();
		}
		else if("deleteVesselDetail".equals(serviceId))
		{
			NotificationManager.showNotification("삭제되었습니다.");

			int row=tableH.getSelectedRow();

			if(row<0) return;

			String vessel_name=(String) tableH.getValueAt(row, 0);

			fnSearchDetail(vessel_name);
		}
		else if("pnVessel.init".equals(serviceId))
		{
			List data = (List )result.get("data");		

			DefaultComboBoxModel boxModel = new DefaultComboBoxModel();

			Iterator iter =data.iterator();

			cbxVesselType.removeAllItems();

			cbxVesselType.addItem(new KSGTableColumn("","전체"));

			while(iter.hasNext())
			{
				HashMap<String, Object> item = (HashMap<String, Object>) iter.next();

				cbxVesselType.addItem(new KSGTableColumn( (String)item.get("code_field"), (String)item.get("code_name")));
			}

			if(isShowData) fnSearch();
		}
		else if("pnVessel.export".equals(serviceId))
		{
			NotificationManager.showNotification("파일을 생성했습니다.");
		}
	}
}