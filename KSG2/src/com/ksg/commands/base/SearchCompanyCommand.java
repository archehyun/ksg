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
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.Company;
import com.ksg.view.comp.KSGTableModel;

public class SearchCompanyCommand extends BaseCommand{
	private int table_type;
	private String[] col_company={"Company", "Company_abbr", "Agent","Agent_abbr","Contents"};
	private String[] colums=null;
	/*public SearchCompanyCommand(JTable table, String orderBy) {
		baseService = new BaseServiceImpl();
		this._tblTable = table;
		this.orderBy=orderBy;
	}*/
	public SearchCompanyCommand(JTable tblTable, String quary, int search) {

		this.search_type=search;
		baseService = new BaseServiceImpl();
		this._tblTable = tblTable;
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
	private void createCompany(DefaultTableModel model) throws SQLException {
		Iterator iter;
		List li=null;
		for(int i=0;i<colums.length;i++)
		{
			model.addColumn(colums[i]);
		}

		switch (this.search_type) {
		case SearchBaseInfoCommand.ARRANGE:
			li =baseService.getArrangedCompanyList(this.orderBy);
			break;
		case SearchBaseInfoCommand.SEARCH:
			li =baseService.getSearchedCompanyList(this.orderBy);
			break;

		default:
			break;
		}

		searchTotalSize=li.size();
		totalSize = baseService.getCompanyCount();
		iter = li.iterator();
		while(iter.hasNext())
		{
			Company compamyInfo = (Company) iter.next();
			model.addRow(new Object[]{compamyInfo.getCompany_name(),		
					compamyInfo.getCompany_abbr(),
					compamyInfo.getAgent_name(),
					compamyInfo.getAgent_abbr(),
					compamyInfo.getContents()
					});
		}
	}
	private DefaultTableModel createModel(int table_type2) throws SQLException {

		DefaultTableModel model = new KSGTableModel();
		Iterator iter=null;
		model.setColumnCount(0);

		List li = null;
		colums = col_company;
		createCompany(model);
		return model;

	}
	private void updateColum(int table_type2) {

		_tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnModel colmodel = _tblTable.getColumnModel();
		TableColumn areacol;
	}

}
