package com.ksg.base.view.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

import com.ksg.adv.view.comp.SimpleFileFilter;
import com.ksg.base.view.dialog.InsertVesselInfoDialog;
import com.ksg.base.view.dialog.UpdateVesselInfoDialog;
import com.ksg.base.view.dialog.VesselImportDialog;
import com.ksg.commands.base.VesselInfoExportCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.common.view.comp.KSGTableCellRenderer;
import com.ksg.common.view.comp.KSGTableModel;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;


/**
 * 선사 기초정보 관리 화면
 * @author archehyun
 *
 */
public class PnVessel extends PnBase implements ActionListener {

	private static final String STRING_ALL_DELETE 		= "전체 선박 데이터 삭제";
	private static final String STRING_VESSEL_TYPE 		= "선박타입";
	private static final String STRING_INPUTDATE 		= "등록일";
	private static final String STRING_VESSEL_COMPANY 	= "대표선사";
	private static final String STRING_VESSEL_MMSI 		= "MMSI";
	private static final String STRING_VESSEL_NAME 		= "선박명";
	private static final String STRING_CONTAINER_TYPE 	= "컨테이너 타입";
	private static final String STRING_ALL 				= "선택";
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

	private JComboBox cbxField;

	private JComboBox cbxVesselType; // 선박 타입 선택

	private JComboBox cbxUse;// 사용 유무 선택
	
	VesselTable vesselTable;


	public PnVessel() {

		super();

		this.add(buildCenter());

		this.initTable();

	}

	private JPanel buildCenter()
	{
		JPanel pnMain = new JPanel(new BorderLayout());

		JScrollPane jScrollPane =createTablePanel();

		tblTable.addMouseListener(new TableSelectListner());

		pnMain.add(jScrollPane);

		pnMain.add(jScrollPane);

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
		cbxField = new JComboBox();
		//cbxField.addItem(STRING_ALL);
		cbxField.addItem(STRING_VESSEL_NAME);
		cbxField.addItem(STRING_VESSEL_MMSI);
		cbxField.addItem(STRING_VESSEL_COMPANY);		
		cbxField.addItem(STRING_INPUTDATE);

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
			List li=	baseService.getSubCodeInfo(code);

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
		//cbxUse.addItem(STRING_ALL);
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
		JButton butExport = new JButton(STRING_EXPORT);
		JButton butImport = new JButton(STRING_IMPORT);
		JButton butVesselDel = new JButton(STRING_ALL_DELETE);
		pnButtomRight.setBorder(BorderFactory.createEtchedBorder());		
		butDel.addActionListener(this);
		butNew.addActionListener(this);
		butExport.addActionListener(this);
		butImport.addActionListener(this);
		butVesselDel.addActionListener(this);

		pnButtomRight.add(butNew);
		pnButtomRight.add(butDel);
		pnButtomRight.add(butExport);
		pnButtomRight.add(butImport);
		pnButtomRight.add(butVesselDel);
		pnButtom.add(pnButtomRight);
		return pnButtom;
	}

	private void createTable(List li)
	{
		model.clear();
		Iterator iter = li.iterator();

		while(iter.hasNext())
		{
			Vessel vesselInfo = (Vessel) iter.next();
			model.addRow(new Object[]{	vesselInfo.getVessel_name(),
					vesselInfo.getVessel_mmsi(),
					vesselInfo.getVessel_type(),
					vesselInfo.getVessel_company(),
					vesselInfo.getVessel_use()==1?"사용안함":"",
							vesselInfo.getInput_date()==null?vesselInfo.getInput_date():format.format(vesselInfo.getInput_date())					
			});
		}

		lblTotal.setText(searchTotalSize+"/"+totalSize);
	}

	@Override
	public void updateTable(String query) {



	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals(STRING_SEARCH))
		{
			searchData();
		}
		else if(command.equals(STRING_DELETE))
		{
			deleteVesselAction();
		}
		else if(command.equals(STRING_INSERT))
		{
			insertAction();
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
				baseService.deleteVesselAll();
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
		int row=tblTable.getSelectedRow();
		if(row<0)
			return;

		String data= (String) tblTable.getValueAt(row, 0);
		int result=JOptionPane.showConfirmDialog(this,data+"를 삭제 하시겠습니까?", "선박 정보 삭제", JOptionPane.YES_NO_OPTION);
		if(result==JOptionPane.OK_OPTION)
		{	
			try {
				int count=baseService.deleteVessel(data);
				if(count>0)
				{
					searchData();
					JOptionPane.showMessageDialog(this, "삭제되었습니다.");
				}

			} catch (SQLException e1) {

				e1.printStackTrace();
				JOptionPane.showConfirmDialog(this, e1.getMessage());
			}
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
		else	// 사용함
		{
			option.setVessel_use(0);
		}

		/*if(cbxUse.getSelectedIndex()!=0)
		{

			

		}*/
		if(cbxVesselType.getSelectedIndex()!=0)
		{
			option.setVessel_type((String)cbxVesselType.getSelectedItem());
		}
		
		String field=(String) cbxField.getSelectedItem();

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
		
		/*if(cbxField.getSelectedIndex()!=0)
		{
			
		}
		else
		{
			txfSearch.setText("");
		}*/

		try {
			List li =baseService.getSearchedVesselList(option);
			searchTotalSize=li.size();
			totalSize = baseService.getVesselCount();
			createTable(li);
		} catch (SQLException ee) {

			ee.printStackTrace();
			JOptionPane.showMessageDialog(PnVessel.this, ee.getMessage());
		}
	}

	@Override
	public void updateTable() {

		searchData();

	}

	class TableSelectListner extends MouseAdapter
	{
		KSGDialog dialog;
		public void mouseClicked(MouseEvent e) 
		{
			if(e.getClickCount()>=2)
			{
				JTable es = (JTable) e.getSource();
				int row=es.getSelectedRow();
				{
					String data=(String) tblTable.getValueAt(row, 0);
					Vessel option = new Vessel();
					option.setVessel_name(data);
					try {
						Vessel info = baseService.getVesselInfo(option);
						dialog = new UpdateVesselInfoDialog(info);
						dialog .createAndUpdateUI();
						if(dialog.result==KSGDialog.SUCCESS)
						{
							searchData();
						}

					} catch (SQLException e1) {

						e1.printStackTrace();
						JOptionPane.showMessageDialog(PnVessel.this, e1.getMessage());
					}
				}
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

	class VesselTable extends BaseTable
	{
		private String[] fieldName={"port_name","port_nationality","port_area","area_code"};
		private String[] columName = {STRING_VESSEL_NAME, STRING_VESSEL_MMSI,STRING_VESSEL_TYPE, STRING_VESSEL_COMPANY,"사용 유무", STRING_INPUTDATE};
		Vessel searchOP;
		public VesselTable() {
			super();
			// 칼럼 정보 초기화
			for(int i=0;i<columName.length;i++)
			{
				//arrangeMap.put(columName[i], fieldName[i]);
				this.currentColumnNameList.add(columName[i]);
			}
			this.setModel(modelInit());
			
			this.columInit();
		}
		private void columInit() {

			TableColumnModel colmodel = getColumnModel();

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
		public void retrive() throws SQLException {
			modelInit();
			List li =baseService.getSearchedVesselList(searchOP);
			Iterator iter = li.iterator();

			while(iter.hasNext())
			{
				Vessel vesselInfo = (Vessel) iter.next();
				model.addRow(new Object[]{	vesselInfo.getVessel_name(),
						vesselInfo.getVessel_mmsi(),
						vesselInfo.getVessel_type(),
						vesselInfo.getVessel_company(),
						vesselInfo.getVessel_use()==1?"사용안함":"",
								vesselInfo.getInput_date()==null?vesselInfo.getInput_date():format.format(vesselInfo.getInput_date())					
				});
			}
			RowSorter<TableModel> sorter
			= new TableRowSorter<TableModel>(model);

			setRowSorter(sorter);

			setModel(model);
			
			this.columInit();

			updateUI();
		}
		public void setOption(Vessel param)
		{
			this.searchOP = param;
		}
	
	}

	
}
