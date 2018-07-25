package com.ksg.base.view.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.ksg.common.dao.DAOImplManager;
import com.ksg.common.view.comp.KSGDialog;
import com.ksg.common.view.comp.KSGTableModel;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.view.KSGViewParameter;

public abstract class PnBase extends JPanel implements TableListener{
	/**
	 * 
	 */
	protected HashMap<String, String> arrangeMap;
	
	protected ArrayList<String> currentColumnNameList;
	
	protected Logger logger = Logger.getLogger(getClass());
	
	protected KSGTableModel model;
	
	protected JTable	tblTable;
	
	protected int searchTotalSize;
	
	protected int totalSize;
	
	private static final long serialVersionUID = 1L;
	
	protected BaseDAOManager baseService;
	
	DAOImplManager daoImplManager = DAOImplManager.getInstance();
	
	protected BaseService service = new BaseServiceImpl();
	
	public PnBase() {
		
		baseService = DAOImplManager.getInstance().createBaseDAOImpl();
		
		//order by�� ���� Į�� ��� ����		
		arrangeMap = new HashMap<String, String>();
		
		// Į�� ���� ������ �����ϱ� ���� Ŭ���� ����
		currentColumnNameList = new ArrayList<String>();		
		
		this.setLayout(new BorderLayout());
		
		add(KSGDialog.createMargin(),BorderLayout.EAST);
		
		add(KSGDialog.createMargin(),BorderLayout.WEST);
	}
	
	public JScrollPane createTablePanel()
	{
		
		tblTable = new JTable();
		
		JScrollPane jScrollPane = new JScrollPane(tblTable);
		
		jScrollPane.getViewport().setBackground(Color.white);
		
		tblTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tblTable.setRowHeight(KSGViewParameter.TABLE_ROW_HEIGHT);
		
		tblTable.setGridColor(Color.lightGray);	
	
		
		return jScrollPane;
	}
	protected String query;
	
	public abstract void updateTable(String query);
	
	public abstract String getOrderBy(TableColumnModel columnModel);
	
	public abstract void initTable();
	
	

}