package com.ksg.view.base;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import com.ksg.dao.DAOImplManager;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.view.comp.KSGDialog;

public abstract class PnBase extends JPanel implements TableListener{
	/**
	 * 
	 */
	protected HashMap<String, String> arrangeMap;
	
	protected ArrayList<String> currentColumnNameList;
	
	protected Logger logger = Logger.getLogger(getClass());
	
	protected DefaultTableModel model;
	
	protected JTable	_tblTable;
	
	protected int searchTotalSize;
	
	protected int totalSize;
	
	private static final long serialVersionUID = 1L;
	
	protected BaseDAOManager baseService;
	
	DAOImplManager daoImplManager = DAOImplManager.getInstance();
	
	public PnBase() {
		baseService = DAOImplManager.getInstance().createBaseDAOImpl();
		
		//order by를 위한 칼럼 목록 생성		
		arrangeMap = new HashMap<String, String>();
		
		// 칼럼 순서 정보를 저장하기 위한 클래스 생성
		currentColumnNameList = new ArrayList<String>();
		
		
		this.setLayout(new BorderLayout());
		
		add(KSGDialog.createMargin(),BorderLayout.EAST);
		
		add(KSGDialog.createMargin(),BorderLayout.WEST);
	}
	protected String query;
	
	public abstract void updateTable(String query);
	
	public abstract String getOrderBy(TableColumnModel columnModel);	
	

}
