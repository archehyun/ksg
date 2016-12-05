package com.ksg.view.base.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.dao.impl.TableService;
import com.ksg.dao.impl.TableServiceImpl;
import com.ksg.view.adv.comp.SimpleFileFilter;
import com.ksg.view.util.ViewUtil;

public class ExportDialog extends JDialog implements ActionListener{



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TableService service;
	CreationHelper createHelper;
	private JTable tblExportTableList;
	public ExportDialog() {
		service = new TableServiceImpl();
	}
	Workbook wb;

	private JTable tblImportTableList;

	private JTextField txfLocation;

	private JPanel pnImportOption()
	{
		JPanel pnSouth = new JPanel(new GridLayout(1,2));
		BorderLayout optionLayout1 = new BorderLayout();
		optionLayout1.setHgap(5);
		JPanel pnOpton1 = new JPanel(optionLayout1);
		JLabel lblOption1 = new JLabel("저장 위치");
		lblOption1.setPreferredSize(new Dimension(100,15));
		pnOpton1.add(lblOption1,BorderLayout.WEST);
		txfLocation = new JTextField();
		JButton butLocation = new JButton("...");
		butLocation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser(".");
				fileChooser.setMultiSelectionEnabled(true);
				String[] pics = new String[] { ".xls"};

				fileChooser.addChoosableFileFilter(new SimpleFileFilter(pics,
						"Excel(*.xls)"));

				int errorrow = 0;
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					System.out.println(fileChooser.getSelectedFile());
					txfLocation.setText(fileChooser.getSelectedFile().getAbsolutePath());

					try {
						POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileChooser.getSelectedFile()));
						wb = (Workbook) new HSSFWorkbook(fs);
						int sheets=wb.getNumberOfSheets();
						ArrayList<String> sheetLists = new ArrayList<String>();
						for(int i=0;i<sheets;i++)
						{
							sheetLists.add(wb.getSheetName(i));
						}
						searchImportTable(sheetLists);
						
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}

			}
		});

		pnOpton1.add(txfLocation);
		pnOpton1.add(butLocation,BorderLayout.EAST);


		pnSouth.add(pnOpton1);

		pnSouth.setBorder(BorderFactory.createTitledBorder("Options"));

		return pnSouth;
	}

	private JPanel pnExport()
	{

		JPanel main = new JPanel(new BorderLayout());
		
		JPanel pnMain = new JPanel(new BorderLayout());
		pnMain.add(buildExportTableList());


		JPanel pnStart = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton butStart = new JButton("Start Export");
		butStart.addActionListener(this);

		pnStart.add(butStart);

		main.add(pnMain);
		main.add(pnStart,BorderLayout.SOUTH);
		return main;

	}
	public void createAndUpdateUI()
	{
		JTabbedPane pane = new JTabbedPane();

		pane.addTab("Export", pnExport());
		pane.addTab("Imports", pnImport());
		this.getContentPane().add(pane);

		this.setMinimumSize(new Dimension(514,384));
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		ViewUtil.center(this);
	}
	private Component pnImport() {
		JPanel pnMain = new JPanel(new BorderLayout());
		JPanel pnStart = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton butStart = new JButton("Start Import");
		butStart.addActionListener(this);
		pnStart.add(butStart);

		JPanel pnSoth = new JPanel(new BorderLayout());



		pnSoth.add(buildImportTableList());
		pnSoth.add(pnImportOption(),BorderLayout.SOUTH);

		pnMain.add(pnSoth);
		pnMain.add(pnStart,BorderLayout.SOUTH);
		return pnMain;
	}
	public static void main(String[] args) {
		ExportDialog dialog = new ExportDialog();
		dialog.createAndUpdateUI();


	}
	private void makeXLS(String sheetName, List columnList) throws IOException, SQLException
	{
		Sheet sheet = wb.createSheet(sheetName);
		int rowIndex=0;
		Row firstrow = sheet.createRow((short)rowIndex);
		for(int i=0;i<columnList.size();i++)
		{
			firstrow.createCell(i).setCellValue(
					createHelper.createRichTextString((String) columnList.get(i)));

		}
		List li = service.selectSystemDataList(sheetName);
		Iterator<Map> iter = li.iterator();
		System.out.println(li.size());
		for(rowIndex=1;iter.hasNext();rowIndex++)
		{
			Map item = iter.next();
			Row row = sheet.createRow(rowIndex);
			for(int i=0;i<columnList.size();i++)
			{

				// Create a cell and put a value in it.

				try{
					Object data = item.get(columnList.get(i));

					if(data instanceof String)
					{
						row.createCell(i).setCellValue(
								createHelper.createRichTextString((String) data));		
					}
					else if(data instanceof Short)
					{
						row.createCell(i).setCellValue(Short.toString((Short) data));
					}
					else if(data instanceof Integer)
					{						
						row.createCell(i).setCellValue(Integer.toString((Integer) data));

					}
					else if(data instanceof Date)
					{
						row.createCell(i).setCellValue(data.toString());
					}
					else
					{
						System.out.println(data);
					}


				}catch(ClassCastException e)
				{
					e.printStackTrace();
				}

			}
		}		
	}


	private JPanel buildExportTableList()
	{
		BorderLayout layout = new BorderLayout();
		layout.setVgap(15);
		JPanel pnMain = new JPanel(layout);
		tblExportTableList = new JTable();
		tblExportTableList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		searchExportTable();

		JPanel pnControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton butRefresh = new JButton("Refresh");
		butRefresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				searchExportTable();

			}
		});
		JButton butSelect = new JButton("Select Tables");
		butSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int row=tblExportTableList.getRowCount();
				for(int i=0;i<row;i++)
					tblExportTableList.setValueAt(Boolean.TRUE, i, 0);
			}
		});
		JButton butUnSelect = new JButton("Unselect All");
		butUnSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row=tblExportTableList.getRowCount();
				for(int i=0;i<row;i++)
					tblExportTableList.setValueAt(Boolean.FALSE, i, 0);
			}
		});
		pnControl.add(butRefresh);
		pnControl.add(butSelect);
		pnControl.add(butUnSelect);

		pnMain.add(new JScrollPane(tblExportTableList));
		pnMain.add(pnControl,BorderLayout.SOUTH);
		pnMain.setBorder(BorderFactory.createTitledBorder("Select Table objects to Export"));
		return pnMain;
	}
	private JPanel buildImportTableList()
	{
		BorderLayout layout = new BorderLayout();
		layout.setVgap(15);
		JPanel pnMain = new JPanel(layout);
		tblImportTableList = new JTable();
		tblImportTableList.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		searchExportTable();

		JPanel pnControl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton butRefresh = new JButton("Refresh");
		butRefresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				searchExportTable();

			}
		});
		JButton butSelect = new JButton("Select Tables");
		butSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int row=tblImportTableList.getRowCount();
				for(int i=0;i<row;i++)
					tblImportTableList.setValueAt(Boolean.TRUE, i, 0);
			}
		});
		JButton butUnSelect = new JButton("Unselect All");
		butUnSelect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row=tblImportTableList.getRowCount();
				for(int i=0;i<row;i++)
					tblImportTableList.setValueAt(Boolean.FALSE, i, 0);
			}
		});
		pnControl.add(butRefresh);
		pnControl.add(butSelect);
		pnControl.add(butUnSelect);




		pnMain.add(new JScrollPane(tblImportTableList));
		pnMain.add(pnControl,BorderLayout.SOUTH);
		pnMain.setBorder(BorderFactory.createTitledBorder("Select Table objects to Import"));
		return pnMain;
	}	
	private void searchImportTable(List xlsSheetList)
	{
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Export");
		model.addColumn("Schema Objects");
		tblImportTableList.setModel(model);   
		Enumeration ee = tblImportTableList.getColumnModel().getColumns();
		TableColumn col = (TableColumn) ee.nextElement();

		col.setCellRenderer(tblImportTableList.getDefaultRenderer(Boolean.class));
		col.setCellEditor(tblImportTableList.getDefaultEditor(Boolean.class));
		List list = xlsSheetList;
		for(int i=0;i<list.size();++i)
		{
			model.addRow(new Object[]{Boolean.FALSE,list.get(i)});	
		}


		TableColumnModel colmodel = tblImportTableList.getColumnModel();
		TableColumn namecol = colmodel.getColumn(0);
		namecol.setPreferredWidth(30);
		namecol = colmodel.getColumn(1);
		namecol.setPreferredWidth(450);


	}

	private void searchExportTable()
	{
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Export");
		model.addColumn("Schema Objects");
		tblExportTableList.setModel(model);   
		Enumeration ee = tblExportTableList.getColumnModel().getColumns();
		TableColumn col = (TableColumn) ee.nextElement();

		col.setCellRenderer(tblExportTableList.getDefaultRenderer(Boolean.class));
		col.setCellEditor(tblExportTableList.getDefaultEditor(Boolean.class));
		try {
			List list = (List) service.selectSystemTableList();
			for(int i=0;i<list.size();++i)
			{
				model.addRow(new Object[]{Boolean.FALSE,list.get(i)});	
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TableColumnModel colmodel = tblExportTableList.getColumnModel();
		TableColumn namecol = colmodel.getColumn(0);
		namecol.setPreferredWidth(30);
		namecol = colmodel.getColumn(1);
		namecol.setPreferredWidth(450);


	}
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals("Start Export"))			
		{
			wb = new HSSFWorkbook();
			createHelper = wb.getCreationHelper();
			try {
				int row=tblExportTableList.getRowCount();
				for(int i=0;i<row;i++)
				{
					boolean is = (Boolean) tblExportTableList.getValueAt(i, 0);
					if(is)
					{
						System.out.println("table:"+tblExportTableList.getValueAt(i, 1));


						String tableName = (String) tblExportTableList.getValueAt(i, 1);


						List columnList =service.selectTableColumnList(tableName);
						for(int j=0;j<columnList.size();j++)
						{
							System.out.print(columnList.get(j)+",");
						}
						System.out.println();
						makeXLS(tableName,columnList);


					}
				}
				FileOutputStream fileOut = new FileOutputStream("./tables.xls");
				wb.write(fileOut);
				fileOut.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		else if(command.equals("Start Import"))
		{
			int tableRow = tblImportTableList.getRowCount();
			if(tableRow<1)
				return;
			try{
			for(int i=0;i<tableRow;i++)
			{
				boolean is = (Boolean) tblImportTableList.getValueAt(i, 0);
				if(is)
				{
					System.out.println("table:"+tblImportTableList.getValueAt(i, 1));


					String tableName = (String) tblImportTableList.getValueAt(i, 1);
					System.out.println(wb);
					Sheet sh = wb.getSheet(tableName);
					
					int lastRowNum=sh.getLastRowNum();
					Row firstRow = sh.getRow(0);
					int columnNum = firstRow.getPhysicalNumberOfCells();
					
					for(int rowIndex=1;rowIndex<=lastRowNum;rowIndex++)
					{
						Row rows = sh.getRow(rowIndex);
						for(int columnIndex=0;columnIndex<columnNum;columnIndex++)
						{
							System.out.print(rows.getCell(columnIndex)+",");
						}
						System.out.println();
								
					}
					
							
				}
			}
			}catch(Exception ee)
			{
				ee.printStackTrace();
				JOptionPane.showMessageDialog(null, ee.getMessage());
			}
			
		}

	}



}
