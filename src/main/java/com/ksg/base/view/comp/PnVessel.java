package com.ksg.base.view.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

import com.ksg.adv.view.comp.SimpleFileFilter;
import com.ksg.base.service.VesselService;
import com.ksg.base.view.BaseInfoUI;
import com.ksg.base.view.dialog.InsertVesselAbbrInfoDialog;
import com.ksg.base.view.dialog.InsertVesselInfoDialog;
import com.ksg.base.view.dialog.UpdateVesselInfoDialog;
import com.ksg.base.view.dialog.VesselImportDialog;
import com.ksg.commands.base.VesselInfoExportCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.Code;
import com.ksg.domain.Vessel;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.label.BoldLabel;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableCellRenderer;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.view.comp.table.model.KSGTableModel;


/**

 * @FileName : PnVessel.java

 * @Date : 2021. 3. 2. 

 * @작성자 : 박창현

 * @변경이력 :

 * @프로그램 설명 : 선박 관리 화면

 */
public class PnVessel extends PnBase implements ActionListener, ComponentListener {

	private static final String STRING_ALL_DELETE 		= "전체 선박 데이터 삭제";
	private static final String STRING_VESSEL_TYPE 		= "선박타입";
	private static final String STRING_INPUTDATE 		= "등록일";
	private static final String STRING_VESSEL_COMPANY 	= "대표선사";
	private static final String STRING_VESSEL_MMSI 		= "MMSI";
	private static final String STRING_VESSEL_NAME 		= "선박명";
	private static final String STRING_CONTAINER_TYPE 	= "컨테이너 타입";
	private static final String STRING_ALL 				= "전체";
	private static final String STRING_SEARCH 			= "검색";
	private static final String STRING_IMPORT 			= "가져오기";
	private static final String STRING_EXPORT 			= "내보내기";
	private static final String STRING_INSERT 			= "신규";
	private static final String STRING_DELETE 			= "삭제";
	/**
	 * 
	 */
	private KSGPropertis 	propertis = KSGPropertis.getIntance();

	private SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");

	private String[] columName = {STRING_VESSEL_NAME, STRING_VESSEL_MMSI,STRING_VESSEL_TYPE, STRING_VESSEL_COMPANY,"사용 유무", STRING_INPUTDATE};

	private static final long serialVersionUID = 1L;

	private JTextField txfSearch;

	private JLabel lblTable,lblTotal;

	private JComboBox<KSGTableColumn> cbxField;

	private JComboBox cbxVesselType; // 선박 타입 선택

	private JComboBox cbxUse;// 사용 유무 선택

	KSGTablePanel tableH;
	
	SelectionListner selectionListner = new SelectionListner();

	private VesselService vesselService = new VesselService();

	private KSGAbstractTable tableD;
	private JLabel lblVesselName;
	private JLabel lblVesselMMSI;
	private JLabel lblVesselType;
	private JLabel lblVesselCompany;
	private JLabel lblVesselUse;
	private JLabel lblInputDate;

	public PnVessel(BaseInfoUI baseInfoUI) {

		super(baseInfoUI);
		this.addComponentListener(this);
		this.add(buildCenter());
	}

	private JPanel buildCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout());

		tableH = new KSGTablePanel("선박목록");

		KSGTableColumn columns[] = new KSGTableColumn[6];

		columns[0] = new KSGTableColumn();
		columns[0].columnField = "vessel_name";
		columns[0].columnName = STRING_VESSEL_NAME;
		columns[0].size = 200;

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
				if((Short)obj==0)
				{
					return "Y";
				}
				else {
					return "N";
					
				}				
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
		
		tableH.getSelectionModel().addListSelectionListener(selectionListner);

		KSGPanel pnMainCenter = new KSGPanel(new BorderLayout(5,5));

		pnMainCenter.add(tableH);

		pnMainCenter.add(createVesselDetail(),BorderLayout.EAST);	

		pnMain.add(pnMainCenter);

		pnMain.add(buildSearchPanel(),BorderLayout.NORTH);

		pnMain.add(buildButton(),BorderLayout.SOUTH);


		return pnMain;

	}
	/**
	 * 검색 항목 표시 패널
	 * @return
	 */
	private JPanel buildSearchPanel() {
		JPanel pnSearch = new JPanel();
		pnSearch.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblTotal = new JLabel();
		lblTable = new JLabel("선박 정보");
		lblTable.setSize(200, 25);
		lblTable.setFont(new Font("돋움",0,16));
		lblTable.setIcon(new ImageIcon("images/db_table.png"));

		JLabel lbl = new JLabel("필드명 : ");
		cbxField = new JComboBox<KSGTableColumn>();
		cbxField.addItem(new KSGTableColumn("vessel_name",STRING_VESSEL_NAME));
		cbxField.addItem(new KSGTableColumn("mmsi",STRING_VESSEL_MMSI));
		cbxField.addItem(new KSGTableColumn("vessel_company",STRING_VESSEL_COMPANY));		
		cbxField.addItem(new KSGTableColumn("input_date",STRING_INPUTDATE));

		txfSearch = new JTextField(15);

		txfSearch.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER)
				{
					searchData();
				}
			}
		});

		JLabel label = new JLabel("개 항목");
		JButton butUpSearch = new JButton(STRING_SEARCH);
		butUpSearch.addActionListener(this);

		cbxVesselType = new JComboBox();

		cbxVesselType.addItem(STRING_ALL);

		Code code = new Code();

		code.setCode_name_kor(STRING_CONTAINER_TYPE);
		try {
			List li=	baseDaoService.getSubCodeInfo(code);

			DefaultComboBoxModel boxModel = new DefaultComboBoxModel();

			Iterator iter = li.iterator();

			while(iter.hasNext())
			{
				Code code2=(Code) iter.next();
				cbxVesselType.addItem(code2.getCode_field());

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cbxUse = new JComboBox();
		cbxUse.addItem(STRING_ALL);
		cbxUse.addItem("사용함");
		cbxUse.addItem("사용안함");

		JLabel lblType = new JLabel("선박타입 : ");
		JLabel lblUse = new JLabel("사용유무 : ");

		cbxField.setPreferredSize(new Dimension(150,23));
		cbxVesselType.setPreferredSize(new Dimension(150,23));

		pnSearch.add(lblUse);
		pnSearch.add(cbxUse);		
		pnSearch.add(lblType);
		pnSearch.add(cbxVesselType);
		pnSearch.add(lbl);
		pnSearch.add(cbxField);
		pnSearch.add(txfSearch);
		pnSearch.add(butUpSearch);
		Box pnSearchAndCount = Box.createVerticalBox();
		pnSearchAndCount.add(pnSearch);

		JPanel pnCountInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnCountInfo.add(lblTotal);
		pnCountInfo.add(label);
		pnSearchAndCount.add(pnCountInfo);

		JPanel pnCount = new JPanel();
		pnCount.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnCount.add(lblTable);

		JPanel pnInfo= new JPanel(new BorderLayout());

		JPanel pnS = new JPanel();
		pnS.setBorder(BorderFactory.createLineBorder(Color.lightGray));
		pnS.setPreferredSize(new Dimension(0,1));
		JPanel pnS1 = new JPanel();
		pnS1.setPreferredSize(new Dimension(0,15));
		Box info = new Box(BoxLayout.Y_AXIS);
		info.add(pnS);
		info.add(pnS1);


		pnInfo.add(info,BorderLayout.SOUTH);
		pnInfo.add(pnSearchAndCount,BorderLayout.EAST);
		pnInfo.add(pnCount,BorderLayout.WEST);
		return pnInfo;
	}
	/**하단 버튼 목록 생성
	 * @return
	 */
	private JPanel buildButton()
	{
		JPanel pnButtom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel pnButtomRight = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton butDel = new JButton(STRING_DELETE);

		JButton butNew = new JButton(STRING_INSERT);
		JButton butNewAbbr = new JButton("약어등록");
		JButton butExport = new JButton(STRING_EXPORT);
		JButton butImport = new JButton(STRING_IMPORT);
		JButton butDelNewAbbr = new JButton("약어삭제");
		JButton butVesselDel = new JButton(STRING_ALL_DELETE);
		pnButtomRight.setBorder(BorderFactory.createEtchedBorder());		
		butDel.addActionListener(this);
		butNew.addActionListener(this);
		butDelNewAbbr.addActionListener(this);
		butNewAbbr.addActionListener(this);
		butExport.addActionListener(this);
		butImport.addActionListener(this);
		butVesselDel.addActionListener(this);

		pnButtomRight.add(butNew);
		pnButtomRight.add(butNewAbbr);
		pnButtomRight.add(butDel);
		pnButtomRight.add(butDelNewAbbr);
		pnButtomRight.add(butExport);
		pnButtomRight.add(butImport);
		pnButtomRight.add(butVesselDel);
		pnButtom.add(pnButtomRight);
		return pnButtom;
	}

	

	@Override
	public void updateTable(String query) {



	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals(STRING_SEARCH))
		{
			fnSearch();
		}
		else if(command.equals(STRING_DELETE))
		{
			deleteVesselAction();
		}
		else if(command.equals(STRING_INSERT))
		{
			insertAction();
		}
		else if(command.equals("약어삭제"))
		{
			int row = tableD.getSelectedRow();
			if(row<0)
				return;
			HashMap<String, Object> item=(HashMap<String, Object>) tableD.getValueAt(row);
			
			try {
			vesselService.deleteVesselAbbr(item);
			
			fnSearchDetail((String)item.get("vessel_name"));
			
			}catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, e1.getMessage());
				
			}
		}
		else if(command.equals("약어등록"))
		{
			int row=tableH.getSelectedRow();
			
			if(row<0)
				return;
			
			HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(tableH.getSelectedRow());
			KSGDialog dialog = new InsertVesselAbbrInfoDialog(item);
			dialog.createAndUpdateUI();
			fnSearchDetail((String)item.get("vessel_name"));
			
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

		String fileName=JOptionPane.showInputDialog(KSGModelManager.getInstance().frame, "파일명을 입력 하세요");

		// 파일 이름 생성
		if(fileName==null||fileName.equals(""))
		{
			//JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "파일명이 없습니다.");
			return;
		}
		VesselInfoExportCommand command = new VesselInfoExportCommand(fileName);
		command.execute();
	}


	/**
	 *엑셀 데이터 가져오기 
	 */
	private void importAction()
	{
		JFileChooser fileChooser = new JFileChooser(propertis.getProperty("dataLocation"));
		fileChooser.setMultiSelectionEnabled(true);
		String[] pics = new String[] { ".xls"};

		fileChooser.addChoosableFileFilter(new SimpleFileFilter(pics,
				"Excel(*.xls)"));

		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = fileChooser.getSelectedFile();
			VesselImportDialog dialog = new VesselImportDialog(selectedFile);
			dialog.createAndUpdateUI();
		}

	}
	/**선박 사용 항목  가져오기
	 * @param vesselUseCell
	 * @return
	 */
	private int getVesselUse(Cell vesselUseCell)
	{
		int vesselUse;
		try{
			switch (vesselUseCell.getCellType()) {
			case HSSFCell.CELL_TYPE_STRING:
				vesselUse= Integer.valueOf(vesselUseCell.getStringCellValue());

				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				vesselUse =(int) vesselUseCell.getNumericCellValue();
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				vesselUse =Vessel.USE;
			default:
				vesselUse =Vessel.USE;
				break;
			}
		}catch(Exception e)
		{
			return Vessel.USE;
		}

		return vesselUse;
	}

	/**
	 * 
	 */
	private void insertAction() {
		KSGDialog dialog = new InsertVesselInfoDialog();
		dialog.createAndUpdateUI();
		if(dialog.result==KSGDialog.SUCCESS)
		{
			searchData();
		}
	}
	private void deleteAllAction()
	{
		String result= JOptionPane.showInputDialog(KSGModelManager.getInstance().frame, "데이터를 삭제 하시려면 '삭제확인'을 입력 하십시요");
		if(result.equals("삭제확인"))
		{
			try {
				baseDaoService.deleteVesselAll();
				updateTable();
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
		int row=tableH.getSelectedRow();
		if(row<0)
			return;

		
		HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(row);
		
		String vessel_name = (String) item.get("vessel_name");
		int result=JOptionPane.showConfirmDialog(this,vessel_name+"를 삭제 하시겠습니까?", "선박 정보 삭제", JOptionPane.YES_NO_OPTION);
		if(result==JOptionPane.OK_OPTION)
		{	
			try {
				
				vesselService.deleteVessel(item);
				
				JOptionPane.showMessageDialog(this, "삭제되었습니다.");
				
				fnSearch();
				
				tableD.clearReslult();
				

			} catch (SQLException e1) {

				e1.printStackTrace();
				
				JOptionPane.showConfirmDialog(this, e1.getMessage());
			}
		}
	}

	public void fnSearch()
	{
		HashMap<String, Object> param = new HashMap<String, Object>();		


		if(cbxUse.getSelectedItem().equals("사용안함"))
		{	
			param.put("vessel_use", Vessel.NON_USE);
		}
		else
		{
			param.put("vessel_use", Vessel.USE);
		}


		if(!"".equals(txfSearch.getText()))
		{

			KSGTableColumn col =(KSGTableColumn) cbxField.getSelectedItem();

			param.put(col.columnField, txfSearch.getText());

		}

		try {
			HashMap<String, Object> result = (HashMap<String, Object>) vesselService.selectVesselList(param);

			tableH.setResultData(result);

			List master = (List) result.get("master");

			if(master.size()==0)
			{
				/*lblArea.setText("");
				lblAreaCode.setText("");
				lblPationality.setText("");
				lblPortName.setText("");
				tableD.clearReslult();*/
			}
			else
			{
				tableH.changeSelection(0,0,false,false);
			}



		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private void searchData() {

		String query=null;

		Vessel option = new Vessel();		

		if(cbxUse.getSelectedItem().equals("사용안함"))
		{
			option.setVessel_use(1);
		}
		else if(cbxUse.getSelectedItem().equals("사용함"))
		{
			option.setVessel_use(0);
		}

		if(cbxVesselType.getSelectedIndex()!=0)
		{
			option.setVessel_type((String)cbxVesselType.getSelectedItem());
		}

		String field=cbxField.getSelectedItem().toString();

		if(field.equals(STRING_VESSEL_NAME))
		{
			query="vessel_name";
		}else if(field.equals("선박명 약어"))
		{
			query="vessel_abbr";
		}
		else if(field.equals(STRING_VESSEL_MMSI))
		{
			query="vessel_mmsi";
		}
		else if(field.equals("대표 선사"))
		{
			query="vessel_company";
		}
		else if(field.equals("사용유무"))
		{
			query="vessel_use";
		}
		else if(field.equals(STRING_INPUTDATE))
		{
			query="CONVERT(varchar(10), input_date, 120)";
		}

		query+=" like '%"+txfSearch.getText()+"%'";
		option.setSearchKeyword(query);


		try {
			List li =baseDaoService.getSearchedVesselList(option);
			searchTotalSize=li.size();
			totalSize = baseDaoService.getVesselCount();			
		} catch (SQLException ee) {

			ee.printStackTrace();
			JOptionPane.showMessageDialog(PnVessel.this, ee.getMessage());
		}
	}
	
	public void fnCallBack()
	{
		
	}

	private JPanel createVesselDetail()
	{		
		lblVesselName = new JLabel();
		lblVesselMMSI = new JLabel();
		lblVesselType = new JLabel();
		lblVesselCompany = new JLabel();
		lblVesselUse = new JLabel();
		lblInputDate = new JLabel();

		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		pnMain.setPreferredSize(new Dimension(400, 0));

		KSGPanel pnTitle = new KSGPanel(new FlowLayout(FlowLayout.LEFT));

		pnTitle.setBackground(Color.WHITE);

		pnTitle.add(new BoldLabel("선박상세정보"));

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
		tableD.getParent().setBackground(Color.white);

		return pnMain;
	}
	private JPanel addComp(String name, JComponent comp)
	{
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setHgap(5);		
		JPanel pnMain = new JPanel(layout);

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

	public void updateTable() {

		searchData();

	}

	class TableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;
		public void mouseClicked(MouseEvent e) 
		{			

			if(e.getClickCount()>1)
			{
				int row=tableH.getSelectedRow();
				if(row<0)
					return;
				HashMap<String, Object> item = (HashMap<String, Object>) tableH.getValueAt(row);
				UpdateVesselInfoDialog dialog = new UpdateVesselInfoDialog(item);
				dialog.createAndUpdateUI();
				
			}
		}
	}

	@Override
	public String getOrderBy(TableColumnModel columnModel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initTable() {

		model = new KSGTableModel();

		for(int i=0;i<columName.length;i++)
		{
			model.addColumn(columName[i]);
		}

		RowSorter<TableModel> sorter
		= new TableRowSorter<TableModel>(model);

		tblTable.setRowSorter(sorter);

		tblTable.setModel(model);

		tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnModel colmodel = tblTable.getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new KSGTableCellRenderer();
			if(i==0||i==3)
			{
				renderer.setHorizontalAlignment(SwingConstants.LEFT);
			}
			else
			{
				renderer.setHorizontalAlignment(SwingConstants.CENTER);	
			}
			namecol.setCellRenderer(renderer);	
		}

		colmodel.getColumn(0).setPreferredWidth(400);
		colmodel.getColumn(1).setPreferredWidth(80);
		colmodel.getColumn(2).setPreferredWidth(80);
		colmodel.getColumn(3).setPreferredWidth(200);
		colmodel.getColumn(4).setPreferredWidth(80);
		colmodel.getColumn(5).setPreferredWidth(100);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		fnSearch();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class SelectionListner implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			if(!e.getValueIsAdjusting())
			{
				
				int row=tableH.getSelectedRow();
				if(row<0)
					return;
				
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

		try {
			HashMap<String, Object> commandMap = new HashMap<String, Object>();
			
			commandMap.put("vessel_name", vessel_name);
			
			HashMap<String, Object> resultMap =  (HashMap<String, Object>) vesselService.selectVesselAbbrList(commandMap);
			
			List result = (List) resultMap.get("master");
			
			tableD.setResultData(result);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
