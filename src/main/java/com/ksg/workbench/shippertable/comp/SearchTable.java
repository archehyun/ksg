package com.ksg.workbench.shippertable.comp;

import java.awt.Dimension;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.domain.ShippersTable;
import com.ksg.service.impl.TableServiceImpl;
import com.ksg.view.comp.table.KSGTable;
import com.ksg.view.comp.table.model.KSGTableModel;

/**
 * @author 박창현
 *
 */
public class SearchTable extends KSGTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected Logger logger = LogManager.getLogger(this.getClass());

	private ShippersTable searchParam;	

	public SearchTable() {
		
		super();

		tableService = new TableServiceImpl();	

		DefaultTableModel model = new KSGTableModel(){

			public boolean isCellEditable(int row, int column) 
			{
				return column==2?true:false;
			}
		};


		model.setColumnCount(0);

		for(int i=0;i<columNames.length;i++)
		{
			model.addColumn(columNames[i]);
		}
		setModel(model);

		updateColumModel();

		logger.debug("init table");

		updateUI();

		setPreferredScrollableViewportSize(new Dimension(1020, 400));

		setName("SubTotalTable");	

	}

	public ShippersTable getSearchParam() {
		return searchParam;
	}

	public void setSearchParam(ShippersTable searchParam) {
		this.searchParam = searchParam;
	}

	public void retrive() throws SQLException
	{
		if(searchParam==null)
			return;

		searchedList = tableService.getTableList(searchParam);

		DefaultTableModel model = new KSGTableModel(){

			public boolean isCellEditable(int row, int column) 
			{
				return column==2?true:false;
			}};

			model.setColumnCount(0);

			for(int i=0;i<columNames.length;i++)
			{
				model.addColumn(columNames[i]);
			}

			Iterator iter = searchedList.iterator();
			while(iter.hasNext())
			{
				ShippersTable cell= (ShippersTable) iter.next();
				model.addRow(new Object[]{
						cell.getPage(),
						cell.getTable_index(),
						cell.getDate_isusse(),
						cell.getTable_id(),
						cell.getPort_col(),						
						cell.getVsl_row(),
						cell.getCompany_abbr(),
						cell.getTitle(),
						cell.getGubun(),

						cell.getTs_port(),
						cell.getIn_port(),
						cell.getIn_to_port(),
						cell.getOut_port(),
						cell.getOut_to_port(),
						cell.getAgent()

				});

			}
			setModel(model);

			updateColumModel();

			logger.debug("retirve:"+this.getRowCount()+","+searchParam);

			updateUI();
	}

	public List getSearchedList() {
		return searchedList;
	}
	public Object getSelectedValue(int row)
	{
		return searchedList.get(row);
	}

	private void updateColumModel()
	{
		initColumModel();

		TableColumnModel colmodel =getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);
			DefaultTableCellRenderer renderer=	(DefaultTableCellRenderer) namecol.getCellRenderer();
			//renderer.setFont(defaultFont);
			if(i==0||i==1||i==2||i==4||i==5)
			{
				
				renderer.setHorizontalAlignment(SwingConstants.CENTER);
			}
			else
			{
				renderer.setHorizontalAlignment(SwingConstants.LEFT);
			}
		}

		TableColumnModel colmodel_width =getColumnModel();

		colmodel_width.getColumn(0).setMaxWidth(65); // 페이지		
		colmodel_width.getColumn(1).setMaxWidth(55); // 인덱스
		colmodel_width.getColumn(2).setMinWidth(125); // 입력일자
		colmodel_width.getColumn(2).setResizable(false);
		colmodel_width.getColumn(3).setMinWidth(100); // 테이블 아이디		
		colmodel_width.getColumn(4).setMaxWidth(60);  // 항구수
		colmodel_width.getColumn(5).setMaxWidth(60);  // 선박수
		colmodel_width.getColumn(6).setPreferredWidth(150); // 선사명
		colmodel_width.getColumn(7).setPreferredWidth(250); // 제목
		colmodel_width.getColumn(8).setWidth(65);		// 구분		
		colmodel_width.getColumn(8).setResizable(false);

		colmodel_width.getColumn(10).setPreferredWidth(100);		// InPorts
		colmodel_width.getColumn(11).setPreferredWidth(100);		// InToPorts
		colmodel_width.getColumn(12).setPreferredWidth(100);		// OutPorts
		colmodel_width.getColumn(13).setPreferredWidth(100);		// OutToPort
		colmodel_width.getColumn(14).setPreferredWidth(250);		// agent		
		
		
		//this.setFont(new Font(defaultFont.getName(), defaultFont.getStyle(), 25));

	}

	private String[] columNames  ={"페이지","인덱스","입력일자","테이블 ID","항구 수","선박 수","선사명","제목","구분",
			"TS 항구","InPorts","InToPorts","OutPorts","OutToPorts","Agent"};
	private List searchedList;


}

