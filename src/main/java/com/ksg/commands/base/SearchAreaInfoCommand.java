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
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.AreaInfo;
import com.ksg.view.comp.KSGTableCellRenderer;
import com.ksg.view.comp.KSGTableModel;

public class SearchAreaInfoCommand extends BaseCommand{

	private String col_area[] = {"지역명","지역코드"};
	private String[] colums=null;
	private int table_type;
	/*public SearchAreaInfoCommand(JTable table, String orderBy) {
		baseService = new BaseServiceImpl();
		this._tblTable = table;
		this.orderBy=orderBy;
	}*/
	public SearchAreaInfoCommand(JTable table, String quary, int search) 
	{
		this.search_type=search;
		baseService = new BaseServiceImpl();
		this._tblTable = table;
		this.orderBy=quary;
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
	private void updateColum(int table_type2) {

		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnModel colmodel = _tblTable.getColumnModel();
		TableColumn areacol;

		for(int i=0;i<colmodel.getColumnCount();i++)
		{
			TableColumn namecol = colmodel.getColumn(i);

			DefaultTableCellRenderer renderer = new KSGTableCellRenderer();
			if(i==1)
			{
				renderer.setHorizontalAlignment(SwingConstants.CENTER);

			}

			namecol.setCellRenderer(renderer);	
		}
		areacol = colmodel.getColumn(0);
		areacol.setPreferredWidth(400);
		areacol = colmodel.getColumn(1);
		areacol.setPreferredWidth(100);
		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	private void createArea(DefaultTableModel model) throws SQLException {
		Iterator iter;
		List li=null;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		switch (this.search_type) {
		case SearchBaseInfoCommand.ARRANGE:
			li =baseService.getArrangedAreaList(orderBy);	
			break;
		case SearchBaseInfoCommand.SEARCH:
			li =baseService.getSearchedAreaList(orderBy);	
			break;

		default:
			break;
		}

		iter = li.iterator();
		searchTotalSize=li.size();
		totalSize = baseService.getAreaCount();
		while(iter.hasNext())
		{

			AreaInfo areaInfo = (AreaInfo) iter.next();
			model.addRow(new Object[]{areaInfo.getArea_name(),		

					areaInfo.getArea_code()});
		}



	}
	
	private DefaultTableModel createModel(int table_type2) throws SQLException {

		DefaultTableModel model = new KSGTableModel();
		Iterator iter=null;
		model.setColumnCount(0);

		List li = null;
		colums = col_area;
		createArea(model);

		return model;

	}

}
