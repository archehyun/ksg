package com.ksg.base.view.comp;

import java.awt.Color;
import java.awt.Font;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.ksg.common.dao.DAOImplManager;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.workbench.KSGViewParameter;

public abstract class BaseTable extends JTable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HashMap<String, String> arrangeMap;

	public ArrayList<String> currentColumnNameList;
	
	protected KSGTableModel model;
	
	protected String query;
	
	protected String orderby;
	
	protected int totalCount;

	public abstract void retrive() throws SQLException;
	
	protected BaseDAOManager baseService;
	
	protected Font defaultFont = new Font("돋음",0,14);
	
	public BaseTable() {
		
		baseService = DAOImplManager.getInstance().createBaseDAOImpl();
		//order by를 위한 칼럼 목록 생성		
		arrangeMap = new HashMap<String, String>();

		// 칼럼 순서 정보를 저장하기 위한 클래스 생성
		currentColumnNameList = new ArrayList<String>();
		
		initStyle();
	}
	public void initStyle()
	{
		this.setGridColor(Color.LIGHT_GRAY);

		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		this.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		
		this.setFont(defaultFont);
	}
	
	protected DefaultTableModel modelInit()
	{
		model = new KSGTableModel();

		for(int i=0;i<currentColumnNameList.size();i++)
		{
			model.addColumn(currentColumnNameList.get(i));
		}
		return model;
	}
	public void setQuery(String query) {
		this.query =query;
	}
	public int getToalCount()
	{
		return totalCount;
	}
	public int getRowCount()
	{
		return model.getRowCount();
	}

}
