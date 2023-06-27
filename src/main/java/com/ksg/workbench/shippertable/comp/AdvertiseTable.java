package com.ksg.workbench.shippertable.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
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
import com.ksg.service.TableService;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.renderer.KSGTableCellRenderer;
import com.ksg.workbench.admin.KSGViewParameter;
import com.ksg.workbench.shippertable.dialog.SearchAndInsertVesselDialog;
import com.ksg.workbench.shippertable.dialog.SearchVesselDialog;

import lombok.extern.slf4j.Slf4j;

/**���� ���� ���� �Է� ���̺�
 * @author archehyun
 *
 */
@SuppressWarnings("serial")
@Slf4j
public class AdvertiseTable extends JTable implements KeyListener, ClipboardOwner{

	private SelectionListener listener;

	private int prevKeyCode;

	private ShippersTable shippersTableInfo; // ���̺� ����

	private DefaultTableModel vesselModel;  // ���� ����

	private ADVData selectedADVData; // ��������

	private int selectedrow=0,selectedcol=0;

	private int ADV_ROW_H;

	private int max;

	private TableService tableService;

	private VesselServiceV2 vesselService;

	/**���� ���� �Ҵ�
	 * @param vesselmodel
	 */

	public DefaultTableModel getVesselModel() {
		return vesselModel;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {}

	/**
	 * �������� �Ҵ�
	 * @param shippersTableInfo
	 */
	public void setShippersTableInfo(ShippersTable shippersTableInfo) {
		this.shippersTableInfo = shippersTableInfo;
	}

	public AdvertiseTable() {

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		addKeyListener(this);

		setName("advTable");

		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		setCellSelectionEnabled(true);

		listener = new SelectionListener(this);

		getSelectionModel().addListSelectionListener(listener);

		ADV_ROW_H=KSGModelManager.ADV_ROW_H; 

		setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);

		tableService = new TableServiceImpl();

		vesselService = new VesselServiceImpl();

	}
	public void setVesselModel(DefaultTableModel vesselModel) {
		
		this.vesselModel = vesselModel;
	}

	/**
	 * 
	 */
	public void delete()
	{
		log.info("delete");

		DefaultTableModel model=(DefaultTableModel) getModel();

		int row = model.getRowCount();

		int col = model.getColumnCount();

		for(int i=0;i<row;i++)
		{
			for(int j=0;j<col;j++)
				model.setValueAt("", i, j);
		}
		setModel(model);

		updateUI();

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

				int selectedRow 	= this.getSelectedRow();

				int selectedColum 	= this.getSelectedColumn();

				String copyValue	= this.getClipboardContents();

				String rowField[]=copyValue.split("\n");

				// ���� �������� ����� �� ���� ����
				for(int tableRowIndex=selectedRow,rowFieldCount=0;tableRowIndex<selectedRow+rowField.length;tableRowIndex++, rowFieldCount++)
				{
					// �ٿ����� ������ ��ü row ���� ũ�� ����
					if(tableRowIndex>=this.getRowCount()) break;

					String columField[] = rowField[rowFieldCount].split("\t");
					
					for(int tableColumIndex=selectedColum,columFieldCount=0;tableColumIndex<selectedColum+columField.length;tableColumIndex++,columFieldCount++)
					{
						// �ٿ����� ������ ��ü col ���� ũ�� ������ ���� ���ϰ� ���� row �̵�
						if(tableColumIndex>=this.getColumnCount()) continue;

						this.setValueAt(columField[columFieldCount], tableRowIndex, tableColumIndex);

					}
				}
				this.updateUI();

			}
		}
		else if(e.getKeyCode()==KeyEvent.VK_DELETE)
		{
			int rows[] = this.getSelectedRows();

			int cols[] = this.getSelectedColumns();
			
			for(int i=0;i<rows.length;i++)
			{
				for(int j=0;j<cols.length;j++)
				{
					this.setValueAt("", rows[i], cols[j]);
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
		{
			int row=table.getSelectedRow();
			
			int col = table.getSelectedColumn();
			
			if(row==-1||col==-1) return;
			
			if(e.getKeyCode()==KeyEvent.VK_TAB)
			{
				int cl=table.getSelectedColumn();
				int ro = table.getSelectedRow();
				int colCount=table.getColumnCount();
				if(cl==colCount-1)
				{
					table.changeSelection(ro+1, 0, false, false);
				}
			}

			if(e.getKeyCode()==KeyEvent.VK_ENTER)
			{
				row=row-1;
				if(row==-1)
					row=0;
			}else
			{
				col=col-1;
				if(col==-1)
					col=0;
			}
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
	
	public ADVData getADVData()
	{
		ADVData insertParam = new ADVData();
		
		String data 		= makeData();
		
		insertParam.setTable_id(this.shippersTableInfo.getTable_id());
		
		insertParam.setCompany_abbr(this.shippersTableInfo.getCompany_abbr());
		
		insertParam.setData(data);
		
		return insertParam;
	}

	
	private String makeData() throws IllegalDataException{
		
		DefaultTableModel dmodel=(DefaultTableModel) getModel();

		Element rootElement = new Element("input");

		rootElement.setAttribute("type","xls");

		Element tableInfos = new Element("table");

		tableInfos.setAttribute("id",this.shippersTableInfo.getTable_id());	

		rootElement.addContent(tableInfos);

		for(int i=0;i<dmodel.getRowCount();i++)
		{
			String vesselName =String.valueOf(dmodel.getValueAt(i, 0));

			if(vesselName==null||vesselName.equals("null")||vesselName.equals("")) continue;

			Element vesselInfo =new Element("vessel");

			vesselInfo.setAttribute("name", vesselName);
			
			vesselInfo.setAttribute("full-name",(String) vesselModel.getValueAt(i, 0));


			vesselInfo.setAttribute("voyage", this.getStringValue(String.valueOf(dmodel.getValueAt(i, 1))));


			// TS �ΰ��
			if(shippersTableInfo.getGubun()!=null&&shippersTableInfo.getGubun().equals("TS"))
			{	
				vesselInfo.setAttribute("ts_name", this.getStringValue(dmodel.getValueAt(i, 2)));
				
				vesselInfo.setAttribute("ts_voyage",this.getStringValue(dmodel.getValueAt(i, 3)));
			}
			// TS �ΰ��:4, �ƴ� ��� 2

			for(int j = shippersTableInfo.getGubun()!=null&&shippersTableInfo.getGubun().equals("TS")?4:2;j<dmodel.getColumnCount();j++)
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
	public String getStringValue(Object value)
	{
		if(value==null||value.equals("null"))		
			return "";
		else			
			return String.valueOf(value);
	}
	// 
	/**
	 * @���� ���� �� �ڵ� ����
	 * @param selectedVesselrow
	 */
	public void autoVesselWrite(int selectedVesselrow) {
		this.autoVesselWrite(selectedVesselrow,0);
	}
	public void setValue( String obj, int selectedVesselrow,int col) {
		DefaultTableModel model=(DefaultTableModel) getModel();
		TableModelListener s[]=model.getTableModelListeners();

		for(int i=0;i<s.length;i++)
		{
			model.removeTableModelListener(s[i]);
		}
		if(obj==null)
		{
			setValueAt(obj, selectedVesselrow, col);	
		}
		else
		{
			setValueAt(obj.toUpperCase(), selectedVesselrow, col);
		}

		changeSelection(selectedVesselrow, col, false, false);

		model.addTableModelListener(new TableModelListener(){

			public void tableChanged(TableModelEvent e) {
				
				if(e.getColumn()==0)
				{
					autoVesselWrite( e.getFirstRow());
				}
				if(shippersTableInfo.getGubun()!=null&&shippersTableInfo.getGubun().equals("TS")&&e.getColumn()==2)
				{
					autoVesselWrite(e.getFirstRow(),2);
				}					
			}});
	}

	public void autoVesselWrite(int selectedVesselrow, int col) {

		if(selectedVesselrow==-1) return;		

		final Object value = getValueAt(selectedVesselrow, col);

		log.debug("enter value:"+value+","+isEditing());
		try {

			if(value==null) return;

			if(String.valueOf(value).length()<=0) return;

			Vessel  op = new Vessel();

			op.setVessel_name(String.valueOf(value));

			HashMap<String, Object> param = new HashMap<String, Object>();

			param.put("vessel_name", value);

			HashMap<String, Object> resultMap= vesselService.selectDetailListByLike(param);

			List li = (List) resultMap.get("master");


			if(li.size()==1)
			{
				HashMap<String, Object> vessel = (HashMap<String, Object>) li.get(0);

				String obj = ((String)vessel.get("vessel_name")).toUpperCase();

				setValue(obj, selectedVesselrow, col);
				
				vesselModel.setRowCount(vesselModel.getRowCount()+1);
				
				vesselModel.setValueAt(vessel.get("vessel_name"), selectedVesselrow, 0);
				
				vesselModel.setValueAt(vessel.get("vessel_abbr"), selectedVesselrow, 1);
				
				return;


			}else if(li.size()>1)
			{
				SearchVesselDialog searchVesselDialog = new SearchVesselDialog((String) value,li);
				
				searchVesselDialog.createAndUpdateUI();

				if(searchVesselDialog.result!=null)
				{
					setValue(searchVesselDialog.result, selectedVesselrow, col);

					vesselModel.setRowCount(vesselModel.getRowCount()+1);
					
					vesselModel.setValueAt(searchVesselDialog.result, selectedVesselrow, 0);
					
					vesselModel.setValueAt(searchVesselDialog.resultAbbr, selectedVesselrow, 1);
					
					return;

				}else
				{
					setValue(null, selectedVesselrow, col);
					
					return;
				}

			}else
			{
				int result=JOptionPane.showConfirmDialog(null, "�ش� ���ڸ��� �����ϴ�. �߰� �Ͻðڽ��ϱ�?", "���ڸ� �߰�", JOptionPane.YES_NO_OPTION);

				if(result == JOptionPane.YES_OPTION)
				{
					SearchAndInsertVesselDialog dialog = new SearchAndInsertVesselDialog(this,selectedVesselrow,col,value.toString(),vesselModel );
					
					dialog .createAndUpdateUI();

				}else
				{
					setValue(null, selectedVesselrow, col);
					log.debug("select no option:"+selectedVesselrow);
					return;
				}
			}
		} catch (SQLException e1) {
			JOptionPane.showMessageDialog(null, "error:"+e1.getMessage());
			e1.printStackTrace();
		}
	}

	/**�÷����� �ʱ�ȭ
	 * @param defaultTableModel
	 * @throws SQLException
	 */
	private void initColumn(DefaultTableModel defaultTableModel) throws SQLException {
		if(shippersTableInfo.getGubun()!=null&&shippersTableInfo.getGubun().equals("TS"))
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

		if(selectedADVData!=null)
		{
			max = tableService.getMaxPortIndex(selectedADVData.getTable_id());
		}else
		{
			max=tableService.getMaxPortIndex(shippersTableInfo.getTable_id());
		}

		log.debug("max port index("+max+")");

		/*
		 * ������������ �ױ�(Į�� ���� ǥ��)
		 */

		for(int i=1;i<max+1;i++)
		{
			TablePort searchport = new TablePort();
			
			searchport.setTable_id(shippersTableInfo.getTable_id());
			
			searchport.setPort_index(i);
			
			List portLi= tableService.getTablePortList(searchport);
			
			if(portLi.size()>1)
			{
				TablePort po=(TablePort) portLi.get(0);
				defaultTableModel.addColumn(po.getPort_index()+"("+portLi.size()+")"+"\n"+po.getPort_name());


			}else if(portLi.size()==1)
			{
				TablePort po=(TablePort) portLi.get(0);
				defaultTableModel.addColumn(po.getPort_index()+"\n"+po.getPort_name());
			}
			// ���� �ڽ��� ��ȣ ǥ��
		}
	}

	public void updateTableView()
	{
		renderingColumModel(this.getColumnModel());
		updateUI();
	}

	private void renderingColumModel(TableColumnModel colmodel) {

		log.info("rendering");

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);

			if(i==0||i==1)
			{
				VesselRenderer renderer = new VesselRenderer();

				namecol.setCellRenderer(renderer);	

				namecol.setCellEditor(new VesselCellEditor( new JTextField()));

				if(i==0)
					namecol.setPreferredWidth(KSGViewParameter.TABLE_VESSEL_COLUM_WIDTH);//vessel
				if(i==1)
					namecol.setPreferredWidth(KSGViewParameter.TABLE_VOYAGE_COLUM_WIDTH);//vessel
				// �� ���� �κ�
			}
			else
			{
				namecol.setPreferredWidth(KSGViewParameter.TABLE_COLUM_WIDTH);//vessel
			}

			if(i>1&&i<colmodel.getColumnCount()-1) 	// �⺻ �� ������ ����
			{
				KSGTableCellRenderer renderer2 = new KSGTableCellRenderer();
				renderer2.setHorizontalAlignment(SwingConstants.CENTER);				
				namecol.setCellRenderer(renderer2);	
				namecol.setCellEditor(new MyTableCellEditor());


			}else if(i==colmodel.getColumnCount()-1)// �������� ������ ����
			{
				namecol.setCellEditor(new ADVButtonCellRenderer());
				namecol.setCellRenderer(new ADVButtonCellRenderer());
			}


			namecol.setHeaderRenderer(new MultiLineHeaderRenderer());
		}
	}
	
	private void makeTableModel(ADVData advData) throws SQLException, NullPointerException, JDOMException, IOException
	{
		KSGXMLManager manager = new KSGXMLManager();

		DefaultTableModel defaultTableModel = new DefaultTableModel();

		DefaultTableModel vesselmodel 		= new DefaultTableModel();

		setRowHeight(ADV_ROW_H);

		setGridColor(Color.lightGray);

		initColumn(defaultTableModel);

		defaultTableModel = manager.createDBTableModel(defaultTableModel,advData);

		vesselmodel.addColumn("���� ��");
		
		vesselmodel.addColumn("���� �� ���");

		vesselmodel = manager.createDBVesselNameModel(vesselmodel ,advData);

		setVesselModel(vesselmodel);

		if(defaultTableModel.getRowCount()<15)
			defaultTableModel.setRowCount(15);

		if(defaultTableModel.getRowCount()>=15)
			defaultTableModel.setRowCount(defaultTableModel.getRowCount()+2);

		defaultTableModel.addColumn("����");

		this.setVesselModel(vesselmodel);

		setModel(defaultTableModel);

		renderingColumModel(getColumnModel());

		defaultTableModel.addTableModelListener(new TableModelListener(){
			public void tableChanged(TableModelEvent e) 
			{
				if(e.getColumn()==0)
				{
					autoVesselWrite( e.getFirstRow());
				}
				if(shippersTableInfo.getGubun()!=null&&shippersTableInfo.getGubun().equals("TS")&&e.getColumn()==2)
				{
					autoVesselWrite( e.getFirstRow(),2);
				}					

			}});
	}

	public void retrive() throws Exception
	{
		
		makeTableModel(this.shippersTableInfo.getAdvData());

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
	/**
	 * @author archehyun
	 *
	 */
	class MultiLineHeaderRenderer extends KSGPanel implements TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label;
			removeAll();
			String[] header = ((String) value).split("\n");
			if(header.length>1)
			{
				setLayout(new GridLayout(header.length, 1));
			}

			this.setPreferredSize(new Dimension(100,40));


			for(String s: header){
				label = new JLabel(s, JLabel.CENTER);
				label.setFont(new Font("����",Font.PLAIN,12));
				LookAndFeel.installColorsAndFont(label, "TableHeader.background",
						"TableHeader.foreground", "TableHeader.font");
				add(label);
			}
			LookAndFeel.installBorder(this, "TableHeader.cellBorder");
			return this;
		}

	}

	class ADVButtonCellRenderer	extends AbstractCellEditor	implements TableCellEditor, TableCellRenderer
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		KSGPanel delCellPanel;
		int row;
		private JButton jButton;
		public ADVButtonCellRenderer(int row) {
			this();
			this.row = row;
		}
		public ADVButtonCellRenderer() {
			delCellPanel = new KSGPanel();
			delCellPanel.setBorder(BorderFactory.createEmptyBorder());
			delCellPanel.setBackground(Color.white);
			delCellPanel.setPreferredSize(new Dimension(100,45));
			jButton = new JButton("Del");


			jButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int rowc=AdvertiseTable.this.getSelectedRow();
					
					if(rowc==-1) return;
					
					try{

						if(rowc<AdvertiseTable.this.getRowCount())
						{
							DefaultTableModel model=(DefaultTableModel) AdvertiseTable.this.getModel();
							int col = model.getColumnCount();
							for(int i=0;i<col;i++)
							{
								model.setValueAt("", rowc, i);
							}
							
							AdvertiseTable.this.setModel(model);
							
							AdvertiseTable.this.updateUI();

						}
					}catch(Exception ee)
					{
						JOptionPane.showMessageDialog(null, "error:"+ee.getMessage());
						ee.printStackTrace();
					}
				}
			});
			delCellPanel.add(jButton);
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) 
		{

			return delCellPanel;
		}

		public Object getCellEditorValue() {
			return null;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if(isSelected)
			{
				delCellPanel.setBackground(table.getSelectionBackground());
				delCellPanel.setForeground(table.getSelectionForeground());
			}else
			{
				delCellPanel.setBackground(table.getBackground());
				delCellPanel.setForeground(table.getForeground());
			}
			return delCellPanel;
		}
	}
	class IndexedColum
	{
		public int index;
		public String port_name;
	}

	// 
	class MyTableCellEditor  extends DefaultCellEditor {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2577443687221331075L;
		private JTextField editor;
		public MyTableCellEditor() {
			super(new JTextField());

		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
				int row, int column) {
			editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
					row, column);

			LookAndFeel.installColorsAndFont(editor, "TableHeader.background",
					"TableHeader.foreground", "TableHeader.font");


			if (value != null)
			{
				editor.setForeground(Color.RED);
				editor.setText(value.toString());
				editor.selectAll();
			}

			editor.setHorizontalAlignment(SwingConstants.CENTER);
			//editor.setFont(new Font("Serif", Font.BOLD, 14));
			return editor;
		}
		public Object getCellEditorValue() {

			return editor.getText();
		}
	}

	class VesselCellEditor extends DefaultCellEditor
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private Font defaultFont;

		public VesselCellEditor(JTextField field)
		{
			super(field);

			defaultFont = new Font(field.getFont().getName(),field.getFont().getStyle(), KSGViewParameter.TABLE_CELL_SIZE);
		}
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
					row, column);

			LookAndFeel.installColorsAndFont(editor, "TableHeader.background",
					"TableHeader.foreground", "TableHeader.font");

			editor.setFont(defaultFont);
			
			editor.setText(value==null?"": String.valueOf(value));
			
			if(editor.isEditable()) editor.selectAll();
			

			return editor;
		}	
	}
	/**
	 * 
	 * �������̺� �� ���� ���� ǥ�� ���� ���� ���� ����
	 * @author ��â��
	 *
	 */
	class VesselCellRenderer extends JTextField  implements TableCellRenderer 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		Font defaultFont;
		public VesselCellRenderer() {
			defaultFont = new Font(this.getFont().getName(),this.getFont().getStyle(), KSGViewParameter.TABLE_CELL_SIZE);

		}
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if(hasFocus)
			{	
				this.setText(value ==null?"":String.valueOf(value));
					
			}

			this.setFont(defaultFont);

			return this;
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
	class VesselRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private boolean is=true;

		private Font defaultFont;

		private static final long serialVersionUID = 1L;


		public VesselRenderer() {
			defaultFont = new Font(this.getFont().getName(),this.getFont().getStyle(), KSGViewParameter.TABLE_CELL_SIZE);
		}

		public boolean isCellEditable(int row, int column)
		{
			return (column ==0);
		}
		public void setHorizontalAlignment(int i)
		{
			super.setHorizontalAlignment(i);
		}
		public void setVisible(boolean is)
		{
			super.setVisible(is);
			this.is=is;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Component renderer=super.getTableCellRendererComponent(
					table, value, isSelected, hasFocus, row, column);
			((JLabel) renderer).setOpaque(true);

			Color foreground, background;

			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(Color.BLUE);
					setBackground(header.getBackground());
					setFont(header.getFont());

				}
			}

			if(isSelected)
			{
				foreground = Color.white;
				background=new Color(51,153,255);				
			}
			else
			{

				foreground = Color.black;
				background=Color.WHITE;	

			}

			try {
				// ���� �߻��� ���ڻ� ����

				if(value!=null)
				{	
					String vessel_abbr =String.valueOf(table.getValueAt(row,0));

					//���ھ�� ����
					Vessel result = vesselService.selectDetail(vessel_abbr);

					if(result==null) foreground = Color.RED;
				}

				renderer.setBackground(background);
				renderer.setForeground(foreground);

			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.setFont(defaultFont);

			return renderer;
		}
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
					if(j<selectedColums.length)
						buffer.append("\t");

				}
				if(i<selectedRows.length)
					buffer.append("\n");

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
		// TODO Auto-generated method stub
		return shippersTableInfo.getTable_id();
	}
}
