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
import com.ksg.domain.PortInfo;
import com.ksg.service.BaseServiceImpl;
import com.ksg.view.comp.table.model.KSGTableModel;

public class SearchPort_AbbrCommand extends BaseCommand {
	private String col_port_abbr[] = {"항구명","추가"};
	private String[] colums=null;
	private int table_type;
	
	public SearchPort_AbbrCommand(JTable tblTable, String quary, int search) {
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

		TableColumn portabbrcol0 = colmodel.getColumn(0);
		portabbrcol0.setPreferredWidth(400);
		TableColumn portabbrcol1 = colmodel.getColumn(1);
		portabbrcol1.setPreferredWidth(400);
		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	}
	private DefaultTableModel createModel(int table_type2) throws SQLException {

		DefaultTableModel model = new KSGTableModel();
		Iterator iter=null;
		model.setColumnCount(0);

		List li = null;


		colums = col_port_abbr;
		createPort_Abbr(model);
		



		return model;

	}
	public int getTotalSize() {
		// TODO Auto-generated method stub
		return totalSize;
	}
	private void createPort_Abbr(DefaultTableModel model) throws SQLException {
		Iterator iter;
		List li=null;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}
		switch (search_type) {
		case SearchBaseInfoCommand.ARRANGE:
			li =baseService.getArrangedPort_AbbrList(orderBy);
			break;
		case SearchBaseInfoCommand.SEARCH:
			li =baseService.getSearchedPort_AbbrList(orderBy);
			break;
		default:
			break;
		}
	
		searchTotalSize=li.size();
		totalSize = baseService.getPort_AbbrCount();
		iter = li.iterator();
		while(iter.hasNext())
		{
			PortInfo portInfo = (PortInfo) iter.next();
			model.addRow(new Object[]{portInfo.getPort_name(),
					portInfo.getPort_abbr()});
		}
	}


}
