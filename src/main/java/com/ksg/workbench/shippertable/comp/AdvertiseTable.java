package com.ksg.workbench.shippertable.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.ksg.adv.logic.xml.KSGXMLManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.renderer.DateCellRenderer;
import com.ksg.workbench.admin.KSGViewParameter;
import com.ksg.workbench.common.dialog.SearchVesselDialog;
import com.ksg.workbench.shippertable.comp.render.DateCellEditor;
import com.ksg.workbench.shippertable.comp.render.MultiLineHeaderRenderer;
import com.ksg.workbench.shippertable.comp.render.TableRowDeleteCellEditor;
import com.ksg.workbench.shippertable.comp.render.TableRowDeleteCellRenderer;
import com.ksg.workbench.shippertable.comp.render.VesselCellEditor;
import com.ksg.workbench.shippertable.comp.render.VesselRenderer;
import com.ksg.workbench.shippertable.dialog.SearchAndInsertVesselDialog;

import lombok.extern.slf4j.Slf4j;

/**���� ���� ���� �Է� ���̺�
 * @author archehyun
 *
 */
@SuppressWarnings("serial")
@Slf4j
public class AdvertiseTable extends JTable implements TableModelListener{

	private SelectionListener listener;

	private int prevKeyCode;

	private ShippersTable shippersTableInfo; // ���̺� ����

	// ��ϵ� ���� �Ǵ� ����
	private DefaultTableModel vesselModel = new DefaultTableModel();  // ���� ����

	private ADVData selectedADVData; // ��������

	private int selectedrow=0, selectedcol=0;

	private int ADV_ROW_H;

	private int max;
	
	private KSGXMLManager manager 				= new KSGXMLManager();

	private VesselServiceV2 vesselService;

	private int TABLE_VESSEL_VOYAGE_WIDTH[] = {150,100};
	
	private AdvtizeTableModeListner modelLister= new AdvtizeTableModeListner(this);

	private List<TablePort> portList;

	/**���� ���� �Ҵ�
	 * @param vesselmodel
	 */

	public DefaultTableModel getVesselModel() {
		return vesselModel;
	}

	/**
	 * �������� �Ҵ�
	 * @param shippersTableInfo
	 */
	public void setShippersTableInfo(ShippersTable shippersTableInfo) {
		this.shippersTableInfo = shippersTableInfo;
	}
	public void setTablePortList(List<TablePort> portList)
	{
		this.portList = portList;
	}
	public AdvertiseTable() {

		addKeyListener(new MyKeyAdapter(this));

		setName("advTable");

		listener = new SelectionListener(this);

		getSelectionModel().addListSelectionListener(listener);

		ADV_ROW_H=KSGModelManager.ADV_ROW_H; 

		vesselService = new VesselServiceImpl();

	}

	/**
	 * 
	 */
	public void deleteAll()
	{
		log.info("delete");

		DefaultTableModel model=(DefaultTableModel) getModel();

		int row = model.getRowCount();

		int col = model.getColumnCount();

		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col-1;j++)
				model.setValueAt("", i, j);
		}

		updateUI();

	}

	public ADVData getADVData() throws Exception
	{
		ADVData insertParam = new ADVData();

		String xmlData 		= makeXMLData();

		insertParam.setTable_id(this.shippersTableInfo.getTable_id());

		insertParam.setCompany_abbr(this.shippersTableInfo.getCompany_abbr());

		insertParam.setData(xmlData);

		return insertParam;
	}

	private String makeXMLData() throws IllegalDataException{

		DefaultTableModel dmodel=(DefaultTableModel) getModel();

		Element rootElement = new Element("input");

		rootElement.setAttribute("type","xls");

		Element tableInfos = new Element("table");

		tableInfos.setAttribute("id",this.shippersTableInfo.getTable_id());	

		rootElement.addContent(tableInfos);

		int vesselModelCount = vesselModel.getRowCount();

		for(int i=0;i<vesselModelCount;i++)
		{
			String vesselName =(String)dmodel.getValueAt(i, 0);

			if(vesselName==null||vesselName.isEmpty()) continue;

			Element vesselInfo =new Element("vessel");

			vesselInfo.setAttribute("name", vesselName);

			vesselInfo.setAttribute("full-name",(String) vesselModel.getValueAt(i, 0));

			vesselInfo.setAttribute("voyage", this.getStringValue(dmodel.getValueAt(i, 1)));

			// TS �ΰ��
			if(isTS())
			{	
				vesselInfo.setAttribute("ts_name", this.getStringValue(dmodel.getValueAt(i, 2)));

				vesselInfo.setAttribute("ts_voyage",this.getStringValue(dmodel.getValueAt(i, 3)));
			}
			// TS �ΰ��:4, �ƴ� ��� 2

			for(int j = isTS()?4:2;j<dmodel.getColumnCount();j++)
			{
				Element inputInfo =new Element("input_date");

				inputInfo.setAttribute("index", String.valueOf(j));				

				inputInfo.setAttribute("date", this.getStringValue(dmodel.getValueAt(i, j)));

				vesselInfo.addContent(inputInfo);				
			}

			rootElement.addContent(vesselInfo);			
		}

		Document document = new Document(rootElement);

		Format format = Format.getPrettyFormat();

		format.setEncoding("EUC-KR");

		format.setIndent("\n");

		format.setIndent("\t");

		XMLOutputter outputter = new XMLOutputter(format);

		String data=outputter.outputString(document);

		log.debug("adv data:\n"+data);

		return data;
	}

	private boolean isTS() {
		return shippersTableInfo.getGubun()!=null&&shippersTableInfo.getGubun().equals("TS");
	}

	private String getStringValue(Object value)
	{
		return value==null?"": String.valueOf(value);
	}

	private void setValue( String obj, int row, int col) {

		DefaultTableModel model = (DefaultTableModel) getModel();
		
		model.removeTableModelListener(modelLister);		
				
		setValueAt(obj==null?null: obj.toUpperCase(), row, col);

		changeSelection(row, col, false, false);

		model.addTableModelListener(modelLister);
	}

	/**�÷����� �ʱ�ȭ
	 * @param defaultTableModel
	 * @throws SQLException
	 */
	private DefaultTableModel createTableModel() throws SQLException {

		DefaultTableModel defaultTableModel = new DefaultTableModel();

		if(isTS())
		{
			log.debug("TS Tabel Model Create");
			defaultTableModel.addColumn("FEEDER\nVESSEL");
			defaultTableModel.addColumn("FEEDER\nVOYAGE");
			defaultTableModel.addColumn("MOTHER\nVESSEL");
			defaultTableModel.addColumn("MOTHER\nVOYAGE");

		}else
		{
			log.debug("Nomal Tabel Model Create");
			defaultTableModel.addColumn("VESSEL");
			defaultTableModel.addColumn("VOYAGE");
		}

		Map<Integer, List<TablePort>> groupedPortMap =  portList.stream()
				.collect(Collectors.groupingBy(TablePort::getPort_index));

		Set<Integer> key = groupedPortMap.keySet();

		for(Integer index:key)
		{
			List<TablePort> portLi	= groupedPortMap.get(index);

			TablePort po			= (TablePort) portLi.get(0);

			int portIndex 			= po.getPort_index();

			String port_name 		= po.getPort_name();

			int size 				= portLi.size();

			defaultTableModel.addColumn( String.format("%s\n%s", portIndex+(size>1?"("+size+")":""), port_name ));
		}
		
		defaultTableModel.addColumn("����");

		return defaultTableModel;
	}

	public void updateTableView()
	{
		renderingColumModel(this.getColumnModel());

		updateUI();
	}

	private void renderingColumModel(TableColumnModel colmodel) {

		log.info("rendering");

		for(int columnIndex=0;columnIndex<colmodel.getColumnCount();columnIndex++)
		{
			TableColumn namecol = colmodel.getColumn(columnIndex);

			namecol.setHeaderRenderer(new MultiLineHeaderRenderer());

			if(columnIndex<2) // ����, ���� �� ������ ����
			{
				namecol.setCellRenderer(new VesselRenderer(this));

				namecol.setCellEditor(new VesselCellEditor());
			}
			else if(columnIndex>=2&&columnIndex<colmodel.getColumnCount()-1) 	// �⺻ �� ������ ����
			{
				namecol.setCellRenderer(new DateCellRenderer());

				namecol.setCellEditor(new DateCellEditor());

			}else 
			{
				namecol.setCellEditor(new TableRowDeleteCellEditor());

				namecol.setCellRenderer(new TableRowDeleteCellRenderer());
			}

			namecol.setPreferredWidth(columnIndex<2?TABLE_VESSEL_VOYAGE_WIDTH[columnIndex]:KSGViewParameter.TABLE_COLUM_WIDTH);//vessel
		}
	}
	

	private void initVesselModel() throws Exception
	{
		this.vesselModel 		= new DefaultTableModel();

		this.vesselModel.addColumn("���� ��");

		this.vesselModel.addColumn("���� �� ���");

		this.vesselModel = manager.createDBVesselNameModel(vesselModel ,this.shippersTableInfo.getAdvData());
		
		this.modelLister.setVesselModel(this.vesselModel);
	}

	private void makeTableModel() throws SQLException, NullPointerException, JDOMException, IOException
	{
		DefaultTableModel defaultTableModel = createTableModel();

		defaultTableModel 					= manager.setDBTableModelData(defaultTableModel,this.shippersTableInfo.getAdvData(), this.shippersTableInfo);

		setModel(defaultTableModel);

		renderingColumModel(getColumnModel());

		defaultTableModel.addTableModelListener(modelLister);
	}

	private void initStyle()
	{
		setCellSelectionEnabled(true);

		setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);

		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		setRowHeight(ADV_ROW_H);

		setGridColor(Color.lightGray);
	}

	public void retrive() throws Exception
	{	
		initVesselModel();

		makeTableModel();

		initStyle();
	}

	public void setSelectedADVData(ADVData selectedADVData)
	{
		this.selectedADVData = selectedADVData;
	}

	class SortedColumnHeaderRenderer implements TableCellRenderer
	{	
		TableCellRenderer textRenderer;

		public SortedColumnHeaderRenderer(TableCellRenderer tableCellRenderer) {
			this.textRenderer =tableCellRenderer;
		}
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component text;
			KSGPanel panel = new KSGPanel();
			panel.setLayout(new BorderLayout());
			if (textRenderer != null) {
				text = textRenderer.getTableCellRendererComponent(table, value,
						isSelected, hasFocus, row, column);
			} else {
				text = new JLabel((String) value, JLabel.CENTER);
				LookAndFeel.installColorsAndFont((JComponent) text,
						"TableHeader.background", "TableHeader.foreground",
						"TableHeader.font");
			}
			panel.add(text, BorderLayout.CENTER);

			return panel;
		}

	}

	public int getSelectedrow() {
		return selectedrow;
	}

	public void setSelectedrow(int selectedrow) {
		this.selectedrow = selectedrow;
	}

	public int getSelectedcol() {
		return selectedcol;
	}

	public void setSelectedcol(int selectedcol) {
		this.selectedcol = selectedcol;
	}

	/**
	 * @author archehyun
	 *
	 */
	class SelectionListener implements ListSelectionListener {

		private JTable table;

		private int selectedRows[];

		private int selectedColums[];


		public SelectionListener(JTable table) {

			this.table = table;
			log.info("create");
		}
		public String getSelectedValue()
		{
			StringBuffer buffer = new StringBuffer();

			for(int i=0;i<selectedRows.length;i++)
			{
				for(int j=0;j<selectedColums.length;j++)
				{
					buffer.append(table.getValueAt(selectedRows[i], selectedColums[j]));

					if(j<selectedColums.length) buffer.append("\t");
				}

				if(i<selectedRows.length) buffer.append("\n");

			}
			return buffer.toString();
		}
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()) {			

				selectedRows=table.getSelectedRows();
				selectedColums=table.getSelectedColumns();
				log.debug("valueChange");
			}

		}
	}
	public String getTableID() {
		return shippersTableInfo.getTable_id();
	}

	class MyKeyAdapter extends KeyAdapter implements ClipboardOwner
	{
		private JTable table;

		public MyKeyAdapter(JTable table)
		{
			this.table = table;
		}

		@Override
		public void keyPressed(KeyEvent e) {

			if(e.getKeyCode()==KeyEvent.VK_C)
			{
				if(prevKeyCode == KeyEvent.VK_CONTROL)
				{
					this.setClipboardContents(listener.getSelectedValue());
				}
			}

			else if(e.getKeyCode()==KeyEvent.VK_V)
			{
				if(prevKeyCode == KeyEvent.VK_CONTROL)
				{
					// �ٿ����� ���̺� ���õ� ���� �� ��ġ

					int selectedRow 	= this.table.getSelectedRow();

					int selectedColum 	= this.table.getSelectedColumn();

					String copyValue	= this.getClipboardContents();

					String rowField[]	= copyValue.split("\n");

					// ���� �������� ����� �� ���� ����
					for(int tableRowIndex=selectedRow,rowFieldCount=0;tableRowIndex<selectedRow+rowField.length;tableRowIndex++, rowFieldCount++)
					{
						// �ٿ����� ������ ��ü row ���� ũ�� ����
						if(tableRowIndex>=this.table.getRowCount()) break;

						String columField[] = rowField[rowFieldCount].split("\t");

						for(int tableColumIndex=selectedColum,columFieldCount=0;tableColumIndex<selectedColum+columField.length;tableColumIndex++,columFieldCount++)
						{
							// �ٿ����� ������ ��ü col ���� ũ�� ������ ���� ���ϰ� ���� row �̵�
							if(tableColumIndex>=this.table.getColumnCount()) continue;

							this.table.setValueAt(columField[columFieldCount], tableRowIndex, tableColumIndex);

						}
					}
					this.table.updateUI();

				}
			}
			else if(e.getKeyCode()==KeyEvent.VK_DELETE)
			{
				int rows[] = this.table.getSelectedRows();

				int cols[] = this.table.getSelectedColumns();

				for(int i=0;i<rows.length;i++)
				{
					for(int j=0;j<cols.length;j++)
					{
						this.table.setValueAt("", rows[i], cols[j]);
					}
				}			
			}
			else
			{
				prevKeyCode = e.getKeyCode();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

			JTable table = (JTable) e.getSource();

			int row		 = table.getSelectedRow();

			int col = table.getSelectedColumn();

			if(row==-1||col==-1) return;

			if(e.getKeyCode()==KeyEvent.VK_TAB)
			{
				int cl 			= table.getSelectedColumn();				

				int colCount	= table.getColumnCount();

				// ������ �÷�
				if(cl==colCount-1) table.changeSelection(row+1, 0, false, false);
			}

			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				row	= (row>0? row-1:0);

			}else
			{
				col	= (col>0? col-1:0);
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		// Ŭ�����忡 ���ڿ��� �ٿ��ְ� �� Ŭ������ Ŭ������ ������ �������ڰ� �ǵ��� �ϴ� �޼ҵ��.
		public void setClipboardContents( String aString ){

			// ������ ���ڿ�(aString)�� ������ �� �ֵ��� Transferable�� �����ؾ��Ѵ�. 
			StringSelection stringSelection = new StringSelection( aString );
			// �÷����� ���ؼ� �����Ǵ� Ŭ������ ��ɰ� ��ȣ �ۿ��ϴ� system Clipboard�� �ν��Ͻ� ��� �ȴ�.
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			// ������ transferable�� ��ü�� Ŭ�����忡 �����ϰ� �� ���ο� ���뿡 ���� �������ڷ� ����Ѵ�.
			//setContents(����, ��������)
			clipboard.setContents( stringSelection, this );
		}
		public String getClipboardContents() {
			String result = "";
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

			// ���� Ŭ�������� ������ ��Ÿ���� transferable�� ��ü�� ��ȯ�Ѵ�.
			//Ŭ������ �����͸� ��û�� ��ü�� ���ڷ� �����ϴ� ����� ����� ������ �ʴ´�.
			Transferable contents = clipboard.getContents(null);
			// Ŭ�����尡 ������� �ʰ� ���ڷ� ������ data flavor�� Transferable ��ü�� ���� ����
			// �Ǵ��� Ȯ���ϰ� Boolean ���� hasTransferableText ������ ����
			boolean hasTransferableText =
					(contents != null) &&
					contents.isDataFlavorSupported(DataFlavor.stringFlavor);
			// Java Unicode String class�� ��ǥ�ϴ� DataFlavor�� stringFlavor�� getTransferData() 
			// �޼ҵ��� ���ڷ� �����Ѵ�. getTransferData() �޼ҵ�� ���۵� �����͸� ��ǥ�ϴ� ��ü��
			// String ������ ����ȯ�� �� ��ȯ�Ѵ�.
			if ( hasTransferableText ) {
				try {
					result = (String)contents.getTransferData(DataFlavor.stringFlavor);
				}
				catch (UnsupportedFlavorException ex){
					// ǥ�� DataFlavor�� ����ϱ� ������ ���ɼ��� �ſ� ���������
					// ������ ���� ��� ����ó�����ݴϴ�.
					log.error(ex.getMessage());
					ex.printStackTrace();
				}
				catch (IOException ex) {
					System.out.println(ex);
					ex.printStackTrace();
				}
			}
			return result;
		}

		@Override
		public void lostOwnership(Clipboard clipboard, Transferable contents) {}

	}
	
	public Vessel selectVesselDetail(String vessel_abbr)
	{
		try {
			return vesselService.selectVesselDetail(vessel_abbr);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			return null;
		}
	}
	
	class AdvtizeTableModeListner implements TableModelListener
	{
		AdvertiseTable myTable;
		
		DefaultTableModel vesselModel;
		
		public void setVesselModel(DefaultTableModel vesselModel)
		{
			this.vesselModel = vesselModel;
		}
		
		public AdvtizeTableModeListner(AdvertiseTable myTable)
		{
			this.myTable = myTable;
		}
		
		/**
		 * @���� ���� �� �ڵ� ����
		 * @param selectedVesselrow
		 * @throws SQLException 
		 */
		public void autoVesselWrite(int selectedVesselrow) throws SQLException {

			this.autoVesselWrite(selectedVesselrow,0);
		}
		
		private List selectVesselList(Object value) throws SQLException
		{
			Vessel  op = new Vessel();

			op.setVessel_name(String.valueOf(value));

			HashMap<String, Object> param = new HashMap<String, Object>();

			param.put("vessel_name", value);

			HashMap<String, Object> resultMap= vesselService.selectDetailListByLike(param);

			List li = (List) resultMap.get("master");
			
			return li;
		}
		
		private void setVesselModelValueAt(int row, String vessel_name, String vessel_abbr)
		{
			this.vesselModel.setRowCount(row+1);

			this.vesselModel.setValueAt(vessel_name, row, 0);

			this.vesselModel.setValueAt(vessel_abbr, row, 1);
		}
		
		public void autoVesselWrite(int row, int col) throws SQLException {

			if(row==-1) return;		

			final Object value = getValueAt(row, col);

			log.debug("enter value:"+value+","+isEditing());

			if(String.valueOf(value).isEmpty()) return;
			
			List vesselDetailList = selectVesselList(value);
			
			String obj = null;
			
			if(vesselDetailList.size()==0)
			{
				int result=JOptionPane.showConfirmDialog(null, "�ش� ���ڸ��� �����ϴ�. �߰� �Ͻðڽ��ϱ�?", "���ڸ� �߰�", JOptionPane.YES_NO_OPTION);

				if(result == JOptionPane.YES_OPTION)
				{
					SearchAndInsertVesselDialog dialog = new SearchAndInsertVesselDialog(this.myTable, row,col,value.toString(),vesselModel );

					dialog .createAndUpdateUI();
				}
			}

			else if(vesselDetailList.size()==1)
			{
				HashMap<String, Object> vessel = (HashMap<String, Object>) vesselDetailList.get(0);

				obj 				= ((String)vessel.get("vessel_name")).toUpperCase();
				
				int vesselRow 		= this.vesselModel.getRowCount();
				
				String vessel_name = (String) vessel.get("vessel_name");
				
				String vessel_abbr = (String) vessel.get("vessel_abbr");

				setVesselModelValueAt(vesselRow, vessel_name, vessel_abbr);

			}else if(vesselDetailList.size()>1)
			{
				SearchVesselDialog searchVesselDialog = new SearchVesselDialog((String) value,vesselDetailList);

				searchVesselDialog.createAndUpdateUI();

				if(searchVesselDialog.result!=null)
				{
					String vessel_name = searchVesselDialog.result;
					
					String vessel_abbr = searchVesselDialog.resultAbbr;
					
					obj = searchVesselDialog.result;
					
					setVesselModelValueAt(row, vessel_name, vessel_abbr);	
				}
			}
			
			setValue(obj, row, col);
			
			log.info("set value:{}", obj);
			
			this.myTable. updateUI();

			log.debug("end");
		}
		
		public void tableChanged(TableModelEvent e) {

			try {
				if(e.getColumn()==0||e.getColumn()==2)
				{
					autoVesselWrite( e.getFirstRow(),(isTS()?2:0));
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
}