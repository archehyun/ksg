/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.commands;

import java.awt.BorderLayout;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;

import com.ksg.adv.logic.xml.KSGXMLManager;
import com.ksg.adv.service.ADVService;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.view.comp.KSGTableCellRenderer;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.shippertable.service.TableService;
import com.ksg.shippertable.view.comp.KSGADVTablePanel;
import com.ksg.view.KSGViewParameter;

public class SearchADVCommand implements KSGCommand {

	private int ADV_ROW_H;
	protected Logger 			logger = Logger.getLogger(getClass());
	//private List searchedList;
	private ADVService	 		_advservice;
	private TableService tableService;
	private BaseService baseService;
	//private KSGModelManager 	manager = KSGModelManager.getInstance();
	private JTable _tblADVTable;
	public int result;

	public static final int NO_HAVE_ADV=-1;
	private KSGADVTablePanel _base;
	private ShippersTable shiptable;
	//private List portLi;
	private DAOManager daomanager;
	private String table_id;
	private int max;
	private int selectedrow=0,selectedcol=0;
	public SearchADVCommand(KSGADVTablePanel panel) 
	{
		daomanager = DAOManager.getInstance();
		ADV_ROW_H=KSGModelManager.ADV_ROW_H; 
		tableService = daomanager.createTableService();
		_advservice = daomanager.createADVService();
		baseService = daomanager.createBaseService();
		this._tblADVTable=panel.tblADVTable;
//		this._tblVesselTable=panel._tblVesselTable;
		_base=panel;
		shiptable=_base.getSelectedTable();
	}

	public SearchADVCommand(ShippersTable st,KSGADVTablePanel panel) {
		this(panel);
		this.shiptable=st;
		_base.setSelectedTable(st);
		this.table_id=shiptable.getTable_id();
	}
	public int execute() {

		logger.debug("execute SearchADVCommnad :");

		try {

			KSGModelManager.getInstance().selectedADVData=_advservice.getADVData(shiptable.getTable_id());

			if(KSGModelManager.getInstance().selectedADVData!=null)
			{

				nomalADV2();
				result= KSGCommand.RESULT_SUCCESS;
				return RESULT_SUCCESS;
			}
			else
			{
				logger.debug("adv data is null");
				nomalADV2();
				result= KSGCommand.RESULT_FAILE;
				return RESULT_FAILE;
			}


		}catch (Exception ee) 
		{
			result= KSGCommand.RESULT_FAILE;
			ee.printStackTrace();
			return RESULT_FAILE;
		}
	}

	// 광고 정보 조회
	private void nomalADV2() {

		KSGXMLManager manager = new KSGXMLManager();
		DefaultTableModel defaultTableModel = new DefaultTableModel();
		DefaultTableModel vesselmodel = new DefaultTableModel();
		_tblADVTable.setRowSelectionAllowed(false);
		_tblADVTable.setRowHeight(ADV_ROW_H);
		_tblADVTable.setGridColor(Color.darkGray);
		initColumn(defaultTableModel);	

		try 
		{
			if(KSGModelManager.getInstance().selectedADVData==null)
			{
				return;
			}

			if(KSGModelManager.getInstance().selectedADVData!=null)
			{
				max = tableService.getMaxPortIndex(KSGModelManager.getInstance().selectedADVData.getTable_id());
			}else
			{
				max=tableService.getMaxPortIndex(shiptable.getTable_id());
			}


			logger.debug("max port index("+max+")");
			for(int i=1;i<max+1;i++)
			{
				TablePort searchport = new TablePort();
				searchport.setTable_id(table_id);
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
				// 광고 박스에 번호 표기
			}

			defaultTableModel = manager.createDBTableModel(defaultTableModel,KSGModelManager.getInstance().selectedADVData);

			vesselmodel.addColumn("선박 명");
			vesselmodel.addColumn("선박 명 약어");

			vesselmodel = manager.createDBVesselNameModel(vesselmodel ,KSGModelManager.getInstance().selectedADVData);
			//_base.setVesseleModel(vesselmodel);

			if(defaultTableModel.getRowCount()<15)
				defaultTableModel.setRowCount(15);

			if(defaultTableModel.getRowCount()>=15)
				defaultTableModel.setRowCount(defaultTableModel.getRowCount()+2);

			defaultTableModel.addColumn("수정");



			_tblADVTable.setModel(defaultTableModel);
			renderingColumModel2(_tblADVTable.getColumnModel());
			defaultTableModel.addTableModelListener(new TableModelListener(){
				public void tableChanged(TableModelEvent e) 
				{
					if(e.getColumn()==0)
					{
						_base.autoVesselWrite( e.getFirstRow());
					}
					if(shiptable.getGubun()!=null&&shiptable.getGubun().equals("TS")&&e.getColumn()==2)
					{
						_base.autoVesselWrite( e.getFirstRow(),2);
					}					
					//					_base.saveAction();//
					//_base.searchADVTable();
				}});

		}


		catch (JDOMException e) 
		{
			try
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame,
				"입력된 광고정보가 없거나 지정된 양식을 따르지 않고 있습니다.\n\n광고정보를 다시 입력해 주십시요");
				logger.error("입력된 광고정보가 없거나 지정된 양식을 따르지 않고 있습니다.\n\n광고정보를 다시 입력해 주십시요");


			}catch (Exception ee) 
			{
				JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+ee.getMessage());
				ee.printStackTrace();
			}


		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e) 
		{
			e.printStackTrace();
		}
	}

	private void initColumn(DefaultTableModel defaultTableModel) {
		if(shiptable.getGubun()!=null&&shiptable.getGubun().equals("TS"))
		{
			logger.debug("TS Tabel Model Create");
			defaultTableModel.addColumn("FEEDER\nVESSEL");
			defaultTableModel.addColumn("FEEDER\nVOYAGE");
			defaultTableModel.addColumn("MOTHER\nVESSEL");
			defaultTableModel.addColumn("MOTHER\nVOYAGE");

		}else
		{
			logger.debug("Nomal Tabel Model Create");
			defaultTableModel.addColumn("VESSEL");
			defaultTableModel.addColumn("VOYAGE");
		}
	}



	private void renderingColumModel2(TableColumnModel colmodel) {


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
				// 색 지정 부분


			}
			else
			{
				namecol.setPreferredWidth(KSGViewParameter.TABLE_COLUM_WIDTH);//vessel
			}

			if(i>1&&i<colmodel.getColumnCount()-1) 	// 기본 셀 렌더러 지정
			{
				KSGTableCellRenderer renderer2 = new KSGTableCellRenderer();
				renderer2.setHorizontalAlignment(SwingConstants.CENTER);				
				namecol.setCellRenderer(renderer2);	
				namecol.setCellEditor(new MyTableCellEditor());


			}else if(i==colmodel.getColumnCount()-1)// 마지막셀 렌더러 지정
			{
				namecol.setCellEditor(new ADVButtonCellRenderer());
				namecol.setCellRenderer(new ADVButtonCellRenderer());
			}


			namecol.setHeaderRenderer(new MultiLineHeaderRenderer());
		}
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
			JPanel panel = new JPanel();
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
			}

			this.setPreferredSize(new Dimension(100,40));


			for(String s: header){
				label = new JLabel(s, JLabel.CENTER);
				label.setFont(new Font("굴림",Font.PLAIN,10));
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
		JPanel jPanel;
		int row;
		private JButton jButton;
		public ADVButtonCellRenderer(int row) {
			this();
			this.row = row;
		}
		public ADVButtonCellRenderer() {
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
							int col = model.getColumnCount();
							for(int i=0;i<col;i++)
							{
								model.setValueAt("", rowc, i);
							}
							_tblADVTable.setModel(model);
							_tblADVTable.updateUI();

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
				Object value, boolean isSelected, int row, int column) 
		{

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

	// 
	class MyTableCellEditor  extends DefaultCellEditor {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2577443687221331075L;
		private JTextField editor;
		public MyTableCellEditor() {
			super(new JTextField());
			// TODO Auto-generated constructor stub
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
		JTextField txfVessel = new JTextField();
		public VesselCellEditor(JTextField field)
		{
			super(field);


		}
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
					row, column);

			LookAndFeel.installColorsAndFont(editor, "TableHeader.background",
					"TableHeader.foreground", "TableHeader.font");




			if(value!=null)
			{
				editor.setText(String.valueOf(value));
			}else
			{
				editor.setText("");
			}



			if(editor.isEditable())
			{
				editor.selectAll();
			}



			return editor;
		}	
	}
	class VesselCellRenderer extends JTextField  implements TableCellRenderer 
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public VesselCellRenderer() {

		}
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {



			if(hasFocus)
			{
				if(value!=null)
				{
					this.setText(String.valueOf(value));
				}else
					this.setText("");	
			}

			if(isSelected)
			{

			}
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
		private static final long serialVersionUID = 1L;

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
				foreground = Color.black;
				background=Color.YELLOW;
			}
			else
			{
				if(row %2==0)
				{
					foreground = Color.black;
					background=Color.WHITE;	
				}
				else
				{
					background= new Color(225,235,255);		
					foreground = Color.black;
				}
			}

			try {
				// 오류 발생시 글자색 변경
				renderer.setBackground(background);
				if(value!=null)
				{	
					String vessel_abbr =String.valueOf(table.getValueAt(row,0));
					
					//선박약어 기준
					Vessel result=baseService.getVesselAbbrInfo(vessel_abbr);
					
					if(result==null)
						foreground = Color.RED;
				}
				renderer.setForeground(foreground);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			renderer.setForeground(foreground);
			renderer.setBackground(background);




			return renderer;
		}


	}

}


