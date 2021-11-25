package com.ksg.commands.base;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.commands.BaseCommand;
import com.ksg.domain.PortInfo;
import com.ksg.service.BaseServiceImpl;
import com.ksg.view.comp.table.model.KSGTableModel;

public class SearchPortInfoCommand extends BaseCommand {
	private String col_port[] = {"항구명","나라","지역","지역코드"};
	private String[] colums=null;
	private int table_type;
	
	public SearchPortInfoCommand(JTable tblTable, String quary, int search) {
		baseService = new BaseServiceImpl();
		this._tblTable = tblTable;
		this.orderBy=quary;
		this.search_type=search;
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
	private void createPort(DefaultTableModel model) throws SQLException {
		Iterator iter;
		List li=null;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		switch (search_type) {
		case SearchBaseInfoCommand.ARRANGE:
			li =baseService.getArrangedPortInfoList(this.orderBy);
			break;
		case SearchBaseInfoCommand.SEARCH:
			li =baseService.getSearchedPortList(this.orderBy);
			break;

		default:
			break;
		}
	
		iter = li.iterator();
		searchTotalSize=li.size();
		totalSize = baseService.getPortCount();
		while(iter.hasNext())
		{
			PortInfo portInfo = (PortInfo) iter.next();
			model.addRow(new Object[]{portInfo.getPort_name(),
					portInfo.getPort_nationality(),
					portInfo.getPort_area(),
					portInfo.getArea_code()});
		}
		
		
	}
	private void updateColum(int table_type2) {

		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnModel colmodel = _tblTable.getColumnModel();
		TableColumn areacol;

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			if(i==3)
			{

				renderer.setHorizontalAlignment(SwingConstants.CENTER);
				namecol.setMaxWidth(100);

			}
			namecol.setCellRenderer(renderer);	

		}
	}
	private DefaultTableModel createModel(int table_type2) throws SQLException {

		DefaultTableModel model = new KSGTableModel();
		Iterator iter=null;
		model.setColumnCount(0);

		List li = null;


		colums = col_port;
		createPort(model);



		return model;

	}
}
