package com.ksg.view.search;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ShippersTable;
import com.ksg.view.KSGViewParameter;
import com.ksg.view.comp.KSGTableCellRenderer;
import com.ksg.view.comp.KSGTableModel;

public class SearchTable extends JTable{
	protected Logger 			logger = Logger.getLogger(getClass());
	private TableService _tableService;
	private Font defaultFont = new Font("돋음",0,9);
	private ShippersTable searchParam;
	public SearchTable() {

		_tableService = DAOManager.getInstance().createTableService();
		this.setFont(defaultFont);

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
		logger.info("init table");

		updateUI();

		this.setGridColor(Color.LIGHT_GRAY);

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setPreferredScrollableViewportSize(new Dimension(1020, 400));
		setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);

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

		searchedList = _tableService.getTableList(searchParam);

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
						//						cell.getPort_col()==portli.size()?cell.getPort_col():new ColorData(ColorData.RED,cell.getPort_col()),
						cell.getPort_col(),
						/*cell.getPort_col()== cell.getR_port_col()?cell.getPort_col():
							cell.getPort_col()+cell.getOthercell()==cell.getR_port_col()?
									new ColorData(ColorData.Black,cell.getPort_col()+"("+cell.getR_port_col()+")"):
										new ColorData(ColorData.RED,cell.getPort_col()+"("+cell.getR_port_col()+")"),*/
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

			logger.info("retirve:"+this.getRowCount()+","+searchParam);
			updateUI();
	}

	public List getSearchedList() {
		return searchedList;
	}



	private void updateColumModel()
	{
		TableColumnModel colmodel =getColumnModel();

		for(int i=0;i<colmodel.getColumnCount();i++)
		{

			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new KSGTableCellRenderer();
			if(i==0||i==1||i==2||i==4||i==5)
				renderer.setHorizontalAlignment(SwingConstants.CENTER);

			namecol.setCellRenderer(renderer);
			namecol.setHeaderRenderer(new IconHeaderRenderer());

		}

		TableColumnModel colmodel_width =getColumnModel();

		colmodel_width.getColumn(0).setMaxWidth(65); // 페이지		
		colmodel_width.getColumn(1).setMaxWidth(55); // 인덱스
		colmodel_width.getColumn(2).setWidth(100); // 입력일자
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




	}

	private String[] columNames  ={"페이지","인덱스","입력일자","테이블 ID","항구 수","선박 수","선사명","제목","구분",
			"TS 항구","InPorts","InToPorts","OutPorts","OutToPorts","Agent"};
	private List searchedList;

	class IconHeaderRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(new Color(61,86,113));

					setBackground(new Color(214,226,242));
					Font f = header.getFont();
					Font font = new Font(f.getFontName(),Font.BOLD,f.getSize());

					setFont(font);


				}
			}

			setText((value == null) ? "" : value.toString());

			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			setHorizontalAlignment(JLabel.CENTER);
			this.setPreferredSize(new Dimension(this.getSize().width,30));


			return this;
		}
	}


}
