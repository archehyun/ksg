package com.ksg.commands;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdom.JDOMException;

import com.ksg.common.dao.DAOManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.service.ADVService;
import com.ksg.service.TableService;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.table.model.KSGTableModel;

public class SearchADVViewCommand extends AbstractCommand{

	private int ADV_ROW_H=40;
	private List portLi;
	public int result;
	
	public static final int EXIST_ADV=1;
	public static final int NO_HAVE_ADV=-1;
	private String table_id;
	
	DAOManager daomanager=DAOManager.getInstance();
	private ADVService	 		_advservice;;
	private TableService tableService;
	ADVData data;
	private JTable _tblVesselList;
	private JTable _tblADVTable;
	public SearchADVViewCommand(String table_id,JTable tblVessel, JTable tblADV) {
		this.table_id=table_id;
		tableService = new TableServiceImpl();
		_advservice = daomanager.createADVService();
		this._tblVesselList = tblVessel;
		this._tblADVTable = tblADV;
		
	}
	public int execute() {
		logger.debug("execute SearchADVCommnad :");

		try {
			ShippersTable st = tableService.getTableById(table_id);

			data=_advservice.getADVData(st.getTable_id());

			if(data!=null)
			{
				if(st.getGubun()!=null&&st.getGubun().equals("TS"))
				{
					//TSADV();
				}else
				{
					nomalADV();
				}
				return RESULT_SUCCESS;
			}
			else
			{
				result= NO_HAVE_ADV;
				logger.debug("result is "+result);
				return RESULT_FAILE;
			}
		}catch (Exception ee) {
			result= NO_HAVE_ADV;
			ee.printStackTrace();
			return RESULT_SUCCESS;
		}

	}
	


	private void nomalADV() throws OutOfMemoryError, JDOMException, IOException {
		String[][] vesselList=data.getFullVesselArray(false);

		String [][]dataArray=data.getDataArray();

		try {
			
			portLi = tableService.getParentPortList(data.getTable_id());
		
			int max=tableService.getMaxPortIndex(data.getTable_id());
			
			logger.debug("max port index("+max+")");

			String table_id=data.getTable_id();
			KSGTableModel datamodel = new KSGTableModel(false);
			KSGTableModel vesselmodel = new KSGTableModel(false);
			datamodel.setColumnCount(0);

			for(int i=1;i<max+1;i++)
			{
				TablePort searchport = new TablePort();
				searchport.setTable_id(table_id);
				searchport.setPort_index(i);
				List portLi= tableService.getTablePortList(searchport);
				if(portLi.size()>1)
				{
					TablePort po=(TablePort) portLi.get(0);
					datamodel.addColumn(po.getPort_index()+"("+portLi.size()+")"+"\n"+po.getPort_name());
					
					
				}else if(portLi.size()==1)
				{
					TablePort po=(TablePort) portLi.get(0);
					datamodel.addColumn(po.getPort_index()+"\n"+po.getPort_name());
				}
				
				// 광고 박스에 번호 표기
				
			}

			vesselmodel.addColumn("Vessel");
			vesselmodel.addColumn("Voyage");
			int rowcount=15;
			
			if(vesselList.length>rowcount)
				rowcount=vesselList.length+1;
			vesselmodel.setRowCount(rowcount);
			datamodel.setRowCount(rowcount);
			
			
			
			for(int i=0;i<vesselList.length;i++)
			{
				vesselmodel.setValueAt(vesselList[i][0], i, 0);
				vesselmodel.setValueAt(vesselList[i][1], i, 1);
			}
			for(int i=0;i<dataArray.length;i++)
			{
				for(int j=0;j<dataArray[i].length;j++)
				{
					datamodel.setValueAt(dataArray[i][j], i, j);	
				}				
			}
			_tblVesselList.setModel(vesselmodel);
			_tblVesselList.setRowSelectionAllowed(false);
			_tblVesselList.setRowHeight(ADV_ROW_H);
			TableColumnModel vcolmodel =_tblVesselList.getColumnModel();

			for(int i=0;i<vcolmodel.getColumnCount();i++)
			{
				TableColumn namecol =vcolmodel.getColumn(i);
				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setCellRenderer(renderer);				
				namecol.setHeaderRenderer(new MultiLineHeaderRenderer());

			}
			TableColumn vnamecol = vcolmodel.getColumn(0);
			vnamecol.setPreferredWidth(160);//vessel

			_tblADVTable.setModel(datamodel);
			_tblADVTable.setRowHeight(ADV_ROW_H);
			_tblADVTable.setRowSelectionAllowed(false);


			ListSelectionModel smodel = _tblADVTable.getSelectionModel();
			_tblVesselList.setSelectionModel(smodel);


			TableColumnModel colmodel =_tblADVTable.getColumnModel();

			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);
//				if(i<colmodel.getColumnCount()-1)
				{
					DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
					renderer.setHorizontalAlignment(SwingConstants.CENTER);
					namecol.setCellRenderer(renderer);
					namecol.setCellEditor(new MyTableCellEditor());	
				}/*else
				{
					namecol.setCellEditor(new ADVCellRenderer());
					namecol.setCellRenderer(new ADVCellRenderer());

				}*/
				namecol.setHeaderRenderer(new MultiLineHeaderRenderer());
			}

			result= EXIST_ADV;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class MultiLineHeaderRenderer extends JPanel implements TableCellRenderer {

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
				this.setPreferredSize(new Dimension(100,40));
			}else
			{
				this.setPreferredSize(new Dimension(100,40));
			}

			for(String s: header){
				label = new JLabel(s, JLabel.CENTER);
				LookAndFeel.installColorsAndFont(label, "TableHeader.background",
						"TableHeader.foreground", "TableHeader.font");
				add(label);
			}
			LookAndFeel.installBorder(this, "TableHeader.cellBorder");
			return this;
		}

	}

	class ADVCellRenderer 
	extends AbstractCellEditor
	implements TableCellEditor, TableCellRenderer
	{
		JPanel jPanel;
		int row;
		private JButton jButton;
		public ADVCellRenderer(int row) {
			this();
			this.row = row;
		}
		public ADVCellRenderer() {
			jPanel = new JPanel();
			jPanel.setBorder(BorderFactory.createEmptyBorder());
			jPanel.setBackground(Color.white);
			jPanel.setPreferredSize(new Dimension(100,45));
			jButton = new JButton("C");
			jButton.setPreferredSize(new Dimension(40,15));


			jButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					int rowc=_tblADVTable.getSelectedRow();
					if(rowc==-1)
						return;
					try{

						if(rowc<_tblADVTable.getRowCount())
						{

							DefaultTableModel model=(DefaultTableModel) _tblADVTable.getModel();
							int rowcount = model.getRowCount();
							int col = model.getColumnCount();
							for(int i=0;i<col;i++)
							{
								model.setValueAt("", rowc, i);
							}
							
							
							
							DefaultTableModel vmodel=(DefaultTableModel) _tblVesselList.getModel();
							col = vmodel.getColumnCount();
							for(int i=0;i<col;i++)
							{
								vmodel.setValueAt("", rowc, i);
							}

							/*DefaultTableModel cmodel=(DefaultTableModel) _tblMotherVesselTable.getModel();
							if(cmodel!=null&&cmodel.getRowCount()>=1)
							{
								cmodel.removeRow(rowc);
								_tblMotherVesselTable.setModel(cmodel);

							}*/
							_tblADVTable.setModel(model);
							_tblVesselList.setModel(vmodel);


						}
					}catch(Exception ee)
					{
						JOptionPane.showMessageDialog(null, "error:"+ee.getMessage());
						ee.printStackTrace();
					}
				}
			});
			jPanel.add(jButton);
		}

		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {

			return jPanel;
		}

		public Object getCellEditorValue() {
			return null;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			if(isSelected)
			{
				jPanel.setBackground(table.getSelectionBackground());
				jPanel.setForeground(table.getSelectionForeground());
			}else
			{
				jPanel.setBackground(table.getBackground());
				jPanel.setForeground(table.getForeground());
			}
			return jPanel;
		}

	}
	class IndexedColum
	{
		public int index;
		public String port_name;
	}
	class MyTableCellEditor  extends DefaultCellEditor {

		private JTextField editor;
		public MyTableCellEditor() {
			super(new JTextField());
			// TODO Auto-generated constructor stub
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
				int row, int column) {
			editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
					row, column);
			if (value != null)
			{
				editor.setText(value.toString());
				editor.selectAll();
				
			}
			//if (column == 0)
			{
				editor.setHorizontalAlignment(SwingConstants.CENTER);
				editor.setFont(new Font("Serif", Font.BOLD, 14));
			} /*else {
				editor.setHorizontalAlignment(SwingConstants.RIGHT);
				editor.setFont(new Font("Serif", Font.ITALIC, 12));
			}
*/			return editor;
		}
		public Object getCellEditorValue() {

			return editor.getText();
		}
	}

}
