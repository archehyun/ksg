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
package com.ksg.commands.base;

import java.awt.Component;
import java.awt.Font;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.commands.IFCommand;
import com.ksg.domain.ADVData;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.Company;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;
import com.ksg.service.BaseService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.view.comp.table.renderer.DateCellRenderer;

public class SearchBaseInfoCommand implements IFCommand {

	
	SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
	public static final int ARRANGE=1;
	public static final int SEARCH=2;


	public static final int TABLE_PORT=1;
	public static final int TABLE_COMPANY=2;
	public static final int TABLE_AREA=3;	
	public static final int TABLE_PAGE=4;	
	public static final	int TABLE_PORT_ABBR = 5;
	public static final int TABLE_VESSEL=6;	
	public static final int TABLE_ADV = 7;
	

	public static final int CODE_ERROR=8;
	public static final int CODE_TABLE=9;
	
	public static final int CODE_SUBTABLE_IN_PORT_LIST=10;
	public static final int CODE_SUBTABLE_CON_TYPE=11;
	public static final int CODE_SUBTABLE_EXCEPTION_PORT=12;
	public static final int CODE_SUBTABLE_DB_TABLE=13;
	public static final int CODE_SUBTABLE_IN_START_PORT=14;
	public static final int CODE_SUBTABLE_TABLE_INDEX=15;
	public static final  int TABLE_VESSEL_ABBR=16;
	


	private JTable _tblTable;
	private String[] col_area	 	= {"코드","지역명","지역코드"};
	private String[] col_company	= {"선사명", "선사명 약어", "에이전트","에이전트 약어","비고"};
	private String[] col_page		= {"page","company_abbr","table_count"};
	private String[] col_port		= {"항구명","나라","지역","지역코드"};
	private String[] col_port_abbr 	= {"항구명","항구명 약어"};
	private String[] col_vessel		= {"선박명","MMSI","선박 타입","대표선사","사용 유무","등록일"};
	private String[] col_vessel_abbr= {"선박명","선박명 약자"};
	private String[] col_adv		= {"table_id","company_abbr","page","date_isusse","data"};
	private String[] colums=null;
	private BaseService service;
	private int table_type;
	private int totalSize;
	
	public SearchBaseInfoCommand(String tableName,int table_type,JTable table) {
		service = new BaseServiceImpl();
		this.table_type= table_type;
		this._tblTable = table;
	}
	private void createArea(DefaultTableModel model) throws SQLException {
		Iterator iter;
		List li;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		li =service.getAreaInfoList();
		iter = li.iterator();
		totalSize=li.size();
		while(iter.hasNext())
		{

			AreaInfo areaInfo = (AreaInfo) iter.next();
			model.addRow(new Object[]{areaInfo.getArea_book_code(),areaInfo.getArea_name(),		

					areaInfo.getArea_code()});
		}
	}
	private void createCompany(DefaultTableModel model) throws SQLException {
		Iterator iter;
		List li;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		li =service.getCompanyList();
		totalSize=li.size();
		iter = li.iterator();
		while(iter.hasNext())
		{
			Company compamyInfo = (Company) iter.next();
			model.addRow(new Object[]{compamyInfo.getCompany_name(),		
					compamyInfo.getCompany_abbr(),
					compamyInfo.getAgent_name(),
					compamyInfo.getAgent_abbr(),compamyInfo.getContents()});
		}
	}
	private DefaultTableModel createModel(int table_type2) throws SQLException {

		DefaultTableModel model = new KSGTableModel();
		Iterator iter=null;
		model.setColumnCount(0);

		List li = null;
		switch (table_type2) {
		case TABLE_COMPANY:

			colums = col_company;
			createCompany(model);
			break;
		case TABLE_AREA:

			colums = col_area;
			createArea(model);
			break;
		case TABLE_PORT:
			colums = col_port;
			createPort(model);
			break;
		case TABLE_PORT_ABBR:
			colums = col_port_abbr;
			createPort_Abbr(model);
			break;
		case TABLE_VESSEL:
			colums = col_vessel;
			createVessel(model);
			break;
		case TABLE_VESSEL_ABBR:
			colums = col_vessel_abbr;
			createVessel_Abbr(model);
			break;
		case TABLE_ADV:
			colums = col_adv;
			createADV(model);
			break;	
		case TABLE_PAGE:
			colums = col_page;
			break;			

		default:
			break;
		}


		return model;

	}
	private void createADV(DefaultTableModel model) {
		Iterator iter;
		List li;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		try {
			li =service.getADVInfoList();

			iter = li.iterator();
			totalSize=li.size();

			while(iter.hasNext())
			{
				ADVData advInfo = (ADVData) iter.next();
				model.addRow(new Object[]{advInfo.getTable_id(),
						advInfo.getCompany_abbr(),
						advInfo.getPage(),
						advInfo.getDate_isusse(),
						advInfo.getData()});

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void createPort(DefaultTableModel model) throws SQLException {
		Iterator iter;
		List li;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		li =service.getPortInfoList();
		iter = li.iterator();
		totalSize=li.size();
		while(iter.hasNext())
		{
			PortInfo portInfo = (PortInfo) iter.next();
			model.addRow(new Object[]{portInfo.getPort_name(),
					portInfo.getPort_nationality(),
					portInfo.getPort_area(),
					portInfo.getArea_code()});
		}
	}
	private void createPort_Abbr(DefaultTableModel model) throws SQLException {
		Iterator iter;
		List li;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		li =service.getPort_AbbrList();
		totalSize=li.size();
		iter = li.iterator();
		while(iter.hasNext())
		{
			PortInfo portInfo = (PortInfo) iter.next();
			model.addRow(new Object[]{portInfo.getPort_name(),
					portInfo.getPort_abbr()});
		}
	}
	private void createVessel(DefaultTableModel model) throws SQLException{
		Iterator iter;
		List li;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		
		Vessel op = new Vessel();
		op.setOption("vessel_name=vessel_abbr order by vessel_name");
		li =service.getVesselList(op);
		iter = li.iterator();
		totalSize=li.size();
		while(iter.hasNext())
		{
			Vessel vesselInfo = (Vessel) iter.next();
			model.addRow(new Object[]{vesselInfo.getVessel_name(),vesselInfo.getVessel_mmsi(),vesselInfo.getVessel_type(),vesselInfo.getVessel_company(),vesselInfo.getVessel_use()==1?"사용안함":"",vesselInfo.getInput_date()==null?vesselInfo.getInput_date():format.format(vesselInfo.getInput_date())});
		}
	}
	private void createVessel_Abbr(DefaultTableModel model) throws SQLException{
		Iterator iter;
		List li;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		li =service.getVesselAbbrList(new Vessel());
		iter = li.iterator();
		totalSize=li.size();
		while(iter.hasNext())
		{
			Vessel vesselInfo = (Vessel) iter.next();
			model.addRow(new Object[]{vesselInfo.getVessel_name(),vesselInfo.getVessel_abbr(),vesselInfo.getVessel_type()});
		}
	}
	public int execute() {

		try{

			_tblTable.setModel(createModel(this.table_type));

			updateColum(this.table_type);
			return RESULT_SUCCESS;


		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, "error:"+e.getMessage());
			e.printStackTrace();
			return RESULT_FAILE;
		}

	}
	public int getTotalSize() {
		return totalSize;
	}
	
	
	/** 칼럼 뷰 조절
	 * @param table_type2
	 */
	private void updateColum(int table_type2) {

		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_tblTable.setColumnSelectionAllowed(false);
		TableColumnModel colmodel = _tblTable.getColumnModel();
		TableColumn areacol;
		DefaultTableCellRenderer cell_left_renderer = new DateCellRenderer();
		DefaultTableCellRenderer cell_center_renderer = new DateCellRenderer();
		cell_center_renderer.setHorizontalAlignment(JLabel.CENTER);
		MyTableHeaderRenderer head_renderer =new MyTableHeaderRenderer();
		
		
		switch (table_type2) {
		case TABLE_COMPANY:


			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);
				namecol.setHeaderRenderer(head_renderer);
				namecol.setCellRenderer(cell_left_renderer);
				namecol.setPreferredWidth(300);
			}
			
			break;
		case TABLE_AREA:

			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				cell_left_renderer = new DateCellRenderer();
				namecol.setHeaderRenderer(head_renderer);
				if(i==2||i==0)
				{
					namecol.setCellRenderer(cell_center_renderer);	
				}
				else
				{
					namecol.setCellRenderer(cell_left_renderer);
				}

					
			}
			areacol = colmodel.getColumn(1);
			areacol.setPreferredWidth(400);
			areacol = colmodel.getColumn(2);
			areacol.setPreferredWidth(80);

			break;
		case TABLE_PORT:

			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);
				
				namecol.setHeaderRenderer(head_renderer);
				if(i==3)
				{
					namecol.setCellRenderer(cell_center_renderer);
					namecol.setPreferredWidth(80);

				}else
				{
					namecol.setCellRenderer(cell_left_renderer);
					namecol.setPreferredWidth(300);	
				}
			}
			

			break;
		case TABLE_PORT_ABBR:
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				namecol.setCellRenderer(cell_left_renderer);
				namecol.setHeaderRenderer(head_renderer);
			}
			TableColumn portabbrcol0 = colmodel.getColumn(0);
			portabbrcol0.setPreferredWidth(300);
			TableColumn portabbrcol1 = colmodel.getColumn(1);
			portabbrcol1.setPreferredWidth(300);
			break;
		case TABLE_VESSEL:
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);
				namecol.setCellRenderer(cell_left_renderer);
				namecol.setHeaderRenderer(head_renderer);
				if(i==0 || i==3)
				{
					namecol.setCellRenderer(cell_left_renderer);
					namecol.setPreferredWidth(250);
				}else
				{
					namecol.setCellRenderer(cell_center_renderer);
					namecol.setPreferredWidth(80);
				}
			}
			break;
		case TABLE_VESSEL_ABBR:
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);
				namecol.setCellRenderer(cell_left_renderer);
			    namecol.setHeaderRenderer(head_renderer);
			}
			
			TableColumn namecolVESSEL_Abbr = colmodel.getColumn(0);
			namecolVESSEL_Abbr.setPreferredWidth(250);
			namecolVESSEL_Abbr = colmodel.getColumn(1);
			namecolVESSEL_Abbr.setPreferredWidth(250);
			break;
		case TABLE_PAGE:
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);

				namecol.setCellRenderer(cell_left_renderer);	
			}
			break;		
		case TABLE_ADV:
			for(int i=0;i<colmodel.getColumnCount();i++)
			{
				TableColumn namecol = colmodel.getColumn(i);
				namecol.setCellRenderer(cell_left_renderer);	
			}
			areacol = colmodel.getColumn(0);
			areacol.setPreferredWidth(100);
			areacol = colmodel.getColumn(1);
			areacol.setPreferredWidth(200);
			areacol = colmodel.getColumn(2);
			areacol.setPreferredWidth(45);
			areacol = colmodel.getColumn(3);
			areacol.setPreferredWidth(150);
			areacol = colmodel.getColumn(4);
			areacol.setPreferredWidth(400);
			break;		

		default:
			break;
		}
	}
	class MyTableHeaderRenderer extends JLabel implements TableCellRenderer {
		  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		      boolean hasFocus, int rowIndex, int vColIndex) {
			  
  
		    setText(value.toString());
		    setHorizontalAlignment(JLabel.CENTER);
		    setToolTipText((String) value);
		    setBorder(BorderFactory.createEtchedBorder());
		    	Font tt=table.getFont();
		    	Font new_font = new Font(tt.getFontName(),0,tt.getSize()+2);
		    	
		    setFont(new_font);
		    
		    return this;
		  }

		}
}

