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
import java.text.SimpleDateFormat;
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
import com.ksg.commands.base.VesselInfoExportCommand;
import com.ksg.common.model.CommandMap;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.Code;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.CodeServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.KSGComboBox;
import com.ksg.view.comp.table.KSGAbstractTable;
import com.ksg.view.comp.table.KSGTableColumn;
import com.ksg.view.comp.table.KSGTablePanel;
import com.ksg.workbench.adv.comp.SimpleFileFilter;
import com.ksg.workbench.common.comp.button.GradientButton;
import com.ksg.workbench.common.comp.dialog.KSGDialog;
import com.ksg.workbench.common.comp.label.BoldLabel;
import com.ksg.workbench.common.comp.panel.KSGPageTablePanel;
import com.ksg.workbench.common.comp.panel.KSGPanel;
import com.ksg.workbench.master.BaseInfoUI;
import com.ksg.workbench.master.dialog.InsertVesselAbbrInfoDialog;
import com.ksg.workbench.master.dialog.InsertVesselInfoDialog;
import com.ksg.workbench.master.dialog.UpdateVesselInfoDialog;
import com.ksg.workbench.master.dialog.VesselImportDialog;

import lombok.extern.slf4j.Slf4j;


/**

 * @FileName : PnVessel.java

 * @Date : 2021. 3. 2. 

 * @�ۼ��� : ��â��

 * @�����̷� :

 * @���α׷� ���� : ���� ���� ȭ��

 */
@Slf4j
public class PnVessel extends PnBase implements ActionListener {

	private static final String STRING_ALL_DELETE 		= "��ü ���� ������ ����";
	private static final String STRING_VESSEL_TYPE 		= "����Ÿ��";
	private static final String STRING_INPUTDATE 		= "�����";
	private static final String STRING_VESSEL_COMPANY 	= "��ǥ����";
	private static final String STRING_VESSEL_MMSI 		= "MMSI";
	private static final String STRING_VESSEL_NAME 		= "���ڸ�";
	private static final String STRING_VESSEL_ABBR 		= "���ڸ���";
	private static final String STRING_CONTAINER_TYPE 	= "�����̳� Ÿ��";
	private static final String STRING_ALL 				= "��ü";
	private static final String STRING_SEARCH 			= "�˻�";
	private static final String STRING_IMPORT 			= "��������";
	private static final String STRING_EXPORT 			= "��������";
	private static final String STRING_INSERT 			= "�ű�";
	private static final String STRING_DELETE 			= "����";
	/**
	 * 
	 */
	private KSGPropertis 	propertis = KSGPropertis.getIntance();



	private SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");

	private String[] columName = {STRING_VESSEL_NAME, STRING_VESSEL_MMSI,STRING_VESSEL_TYPE, STRING_VESSEL_COMPANY,"��� ����", STRING_INPUTDATE};

	private static final long serialVersionUID = 1L;

	private JTextField txfSearch;

	private JLabel lblTable;	

	private JComboBox<KSGTableColumn> cbxField;

	private KSGComboBox cbxVesselType; // ���� Ÿ�� ����

	private JComboBox cbxUse;// ��� ���� ����

	KSGTablePanel tableH;

	SelectionListner selectionListner = new SelectionListner();

	private VesselService vesselService = new VesselServiceImpl();

	private CodeServiceImpl codeService= new CodeServiceImpl();

	private KSGAbstractTable tableD;

	private JLabel lblVesselName;

	private JLabel lblVesselMMSI;

	private JLabel lblVesselType;

	private JLabel lblVesselCompany;

	private JLabel lblVesselUse;

	private JLabel lblInputDate;

	public PnVessel(BaseInfoUI baseInfoUI) {

		super(baseInfoUI);
		
		this.setController(new VesselController());
		
		this.addComponentListener(this);
		
		this.add(buildCenter());
		
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
	}

	private JComponent buildCenter()
	{
		KSGPanel pnMain = new KSGPanel(new BorderLayout());

		tableH = new KSGTablePanel("���ڸ��");

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
				if((Integer)obj==0)
				{
					return "Y";
				}
				else {
					return "N";

				}				
			}
		};
		columns[4].columnField = "vessel_use";
		columns[4].columnName = "�������";
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
	 * �˻� �׸� ǥ�� �г�
	 * @return
	 */
	private JComponent buildSearchPanel() {

		KSGPanel pnSearch = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));	

		JLabel lbl = new JLabel("�ʵ�� : ");
		
		cbxField = new KSGComboBox();
		
		cbxField.addItem(new KSGTableColumn("vessel_name",STRING_VESSEL_NAME));
		
		cbxField.addItem(new KSGTableColumn("vessel_abbr",STRING_VESSEL_ABBR));
		
		cbxField.addItem(new KSGTableColumn("vessel_mmsi",STRING_VESSEL_MMSI));
		
		cbxField.addItem(new KSGTableColumn("vessel_company",STRING_VESSEL_COMPANY));
		
		cbxField.addItem(new KSGTableColumn("input_date",STRING_INPUTDATE));

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

		GradientButton butUpSearch = new GradientButton(STRING_SEARCH, "images/search3.png");
		butUpSearch.setGradientColor(Color.decode("#215f00"), Color.decode("#3cac00"));
		
		butUpSearch.addActionListener(this);

		cbxVesselType = new KSGComboBox();

		cbxVesselType.addItem(new KSGTableColumn("", STRING_ALL));

		cbxUse = new KSGComboBox();
		
		cbxUse.addItem(STRING_ALL);
		
		cbxUse.addItem("�����");
		
		cbxUse.addItem("������");

		JLabel lblType = new JLabel("����Ÿ�� : ");
		
		JLabel lblUse = new JLabel("������� : ");
		
		cbxUse.setPreferredSize(new Dimension(100,23));

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

		KSGPanel pnCountInfo = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));	

		pnSearchAndCount.add(pnCountInfo);	

		KSGPanel pnInfo= new KSGPanel(new BorderLayout());

		pnInfo.add(buildLine(),BorderLayout.SOUTH);
		pnInfo.add(pnSearchAndCount,BorderLayout.EAST);
		pnInfo.add(buildTitleIcon("���� ����"),BorderLayout.WEST);
		return pnInfo;
	}

	/**�ϴ� ��ư ��� ����
	 * @return
	 */
	private JComponent buildButton()
	{
		KSGPanel pnMain = new KSGPanel(new FlowLayout(FlowLayout.RIGHT));
		KSGPanel pnButtomRight = new KSGPanel(new FlowLayout(FlowLayout.LEFT));
		JButton butDel = new JButton(STRING_DELETE);


		JButton butNewAbbr = new JButton("�����");
		JButton butExport = new JButton(STRING_EXPORT);
		JButton butImport = new JButton(STRING_IMPORT);
		JButton butDelNewAbbr = new JButton("������");
		JButton butVesselDel = new JButton(STRING_ALL_DELETE);

		butDel.addActionListener(this);


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
		else if(command.equals("������"))
		{
			int row = tableD.getSelectedRow();
			if(row<0)
				return;
			HashMap<String, Object> item=(HashMap<String, Object>) tableD.getValueAt(row);

			try {
				vesselService.deleteDetail(item);

				fnSearchDetail((String)item.get("vessel_name"));

			}catch (Exception e1) {

				e1.printStackTrace();

				JOptionPane.showMessageDialog(PnVessel.this, e1.getMessage());

			}
		}
		else if(command.equals("�����"))
		{
			int row=tableH.getSelectedRow();

			if(row<0) return;

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

		String fileName=JOptionPane.showInputDialog(KSGModelManager.getInstance().frame, "���ϸ��� �Է� �ϼ���");

		// ���� �̸� ����
		if(fileName==null||fileName.equals(""))
		{
			//JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "���ϸ��� �����ϴ�.");
			return;
		}


		// TODO VESSEL EXPORT ����
		VesselInfoExportCommand command = new VesselInfoExportCommand(fileName);
		command.execute();







	}


	/**
	 *���� ������ �������� 
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
	/**���� ��� �׸�  ��������
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
		
		log.debug("insert");
		
		KSGDialog dialog = new InsertVesselInfoDialog(this);
		
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
	private void deleteAllAction()
	{
		String result= JOptionPane.showInputDialog(KSGModelManager.getInstance().frame, "�����͸� ���� �Ͻ÷��� '����Ȯ��'�� �Է� �Ͻʽÿ�");
		if(result.equals("����Ȯ��"))
		{
			try {
				
				vesselService.delete(new HashMap<String, Object>());
				//				baseDaoService.deleteVesselAll();

				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�����͸� ���� �߽��ϴ�.");
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
		
		if(row<0) return;

		HashMap<String, Object> item=(HashMap<String, Object>) tableH.getValueAt(row);

		String vessel_name = (String) item.get("vessel_name");

		
		int result=JOptionPane.showConfirmDialog(this,vessel_name+"�� ���� �Ͻðڽ��ϱ�?", "���� ���� ����", JOptionPane.YES_NO_OPTION);
		
		if(result==JOptionPane.OK_OPTION)
		{	
//			try {
				CommandMap param = new CommandMap();
				
				param.put("vessel_name", vessel_name);

				callApi("deleteVessel", param);
				
//				vesselService.delete(param);
//
//				JOptionPane.showMessageDialog(this, "�����Ǿ����ϴ�.");
//
//				fnSearch();
//
//				tableD.clearReslult();

//
//			} catch (SQLException e1) {
//
//				e1.printStackTrace();
//
//				JOptionPane.showConfirmDialog(this, e1.getMessage());
//			}
		}
	}

	public void fnSearch()
	{
		CommandMap param = new CommandMap();	



		if(cbxVesselType.getSelectedIndex()>0)
		{
			KSGTableColumn comboItem =(KSGTableColumn) cbxVesselType.getSelectedItem();
			param.put("vessel_type", comboItem.columnField);

		}

		if(cbxUse.getSelectedIndex()>0)
		{
			if(cbxUse.getSelectedItem().equals("������"))
			{	
				param.put("vessel_use", Vessel.NON_USE);
			}
			else
			{
				param.put("vessel_use", Vessel.USE);
			}
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
		lblVesselName = new JLabel();
		
		lblVesselMMSI = new JLabel();
		
		lblVesselType = new JLabel();
		
		lblVesselCompany = new JLabel();
		
		lblVesselUse = new JLabel();
		
		lblInputDate = new JLabel();

		KSGPanel pnMain = new KSGPanel(new BorderLayout(5,5));

		pnMain.setPreferredSize(new Dimension(400, 0));

		KSGPanel pnTitle = new KSGPanel(new BorderLayout());

		pnTitle.setBackground(Color.WHITE);

		pnTitle.add(new BoldLabel("���ڻ�����"),BorderLayout.WEST);

		KSGPanel pnControl = new KSGPanel(new FlowLayout());

		JButton butNewAbbr = new GradientButton("�߰�");
		butNewAbbr.setActionCommand("�����");
		JButton butDelAbbr = new GradientButton("����");
		butDelAbbr.setActionCommand("������");

		pnControl.add(butNewAbbr);
		pnControl.add(butDelAbbr);

		butNewAbbr.addActionListener(this);
		butDelAbbr.addActionListener(this);

		pnTitle.add(pnControl,BorderLayout.EAST);

		KSGPanel pnSubMain = new KSGPanel(new BorderLayout(5,5));

		KSGPanel pnPortInfo = new KSGPanel(new GridLayout(5,1,2,2));

		pnPortInfo.add(addComp("���ڸ�",lblVesselName));
		pnPortInfo.add(addComp("MMSI",lblVesselMMSI));
		pnPortInfo.add(addComp("����Ÿ��",lblVesselType));
		pnPortInfo.add(addComp("��ǥ����",lblVesselCompany));
		pnPortInfo.add(addComp("�����",lblInputDate));

		tableD = new KSGAbstractTable();

		tableD.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		KSGTableColumn dcolumns = new KSGTableColumn();

		dcolumns.columnField = "vessel_abbr";

		dcolumns.columnName = "���ڸ� ���";

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
		KSGDialog dialog;
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

		Code code = new Code();

		code.setCode_name_kor(STRING_CONTAINER_TYPE);	

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
			CommandMap param = new CommandMap();

			param.put("vessel_name", vessel_name);
			
			callApi("selectVesselDetailList", param);
	}

	@Override
	public void updateView() {
		CommandMap result= this.getModel();

		boolean success = (boolean) result.get("success");

		if(success)
		{
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
				JOptionPane.showMessageDialog(this, "�����Ǿ����ϴ�.");

				fnSearch();

				tableD.clearReslult();
			}
			
			
			
			else if("pnVessel.init".equals(serviceId))
			{
				List data = (List )result.get("data");		

				DefaultComboBoxModel boxModel = new DefaultComboBoxModel();

				Iterator iter =data.iterator();
				
				cbxVesselType.removeAllItems();
				
				cbxVesselType.addItem(new KSGTableColumn("","��ü"));

				while(iter.hasNext())
				{
					HashMap<String, Object> item = (HashMap<String, Object>) iter.next();

					cbxVesselType.addItem(new KSGTableColumn( (String)item.get("code_field"), (String)item.get("code_name")));

				}
				
				if(isShowData)
					fnSearch();

			}

		}
		else{  
			String error = (String) result.get("error");
			JOptionPane.showMessageDialog(this, error);
		}

	}
}
