package com.ksg.commands.base;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ksg.commands.BaseCommand;
import com.ksg.common.view.comp.KSGTableModel;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Vessel;

public class SearchVesselAbbrCommand extends BaseCommand {

	private String[] col_vessel={"선박명","선박명 약어"};
	private String[] colums=null;
	private int table_type;
	public SearchVesselAbbrCommand(JTable table,String orderby) {
		baseService = new BaseServiceImpl();
		this._tblTable = table;
		this.orderBy=orderby;
	}
	public SearchVesselAbbrCommand(JTable tblTable, String quary, int search) {
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
	private void updateColum(int table_type2) {

		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnModel colmodel = _tblTable.getColumnModel();
		TableColumn areacol;

		TableColumn namecolVESSEL = colmodel.getColumn(0);
		namecolVESSEL.setPreferredWidth(300);
		namecolVESSEL = colmodel.getColumn(1);
		namecolVESSEL.setPreferredWidth(300);
		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	private DefaultTableModel createModel(int table_type2) throws SQLException {

		DefaultTableModel model = new KSGTableModel();
		Iterator iter=null;
		model.setColumnCount(0);

		List li = null;


		colums = col_vessel;
		createVesselAbbr(model);


		return model;

	}
	
	private void createVesselAbbr(DefaultTableModel model) throws SQLException{
		Iterator iter;
		List li = null;
		
		this.totalSize=baseService.getVesselAbbrCount();
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		switch (search_type) {
		case SearchBaseInfoCommand.ARRANGE:
			li =baseService.getArrangedVesselAbbrList(orderBy);
			break;
		case SearchBaseInfoCommand.SEARCH:
			li =baseService.getSearchedVesselAbbrList(orderBy);
			break;
		default:
			break;
		}
		
		iter = li.iterator();
		searchTotalSize=li.size();
		while(iter.hasNext())
		{
			Vessel vesselInfo = (Vessel) iter.next();
			model.addRow(new Object[]{vesselInfo.getVessel_name(),vesselInfo.getVessel_abbr(),vesselInfo.getVessel_type()});
		}
	}

}
