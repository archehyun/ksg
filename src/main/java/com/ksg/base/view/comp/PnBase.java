package com.ksg.base.view.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import com.ksg.base.view.BaseInfoUI;
import com.ksg.common.dao.DAOImplManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.view.comp.dialog.KSGDialog;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.workbench.KSGViewParameter;

/**
 * 마스터 화면 추상 클래스
 * 
 * @author 박창현
 *
 */
public abstract class PnBase extends KSGPanel implements TableListener, ComponentListener{
	/**
	 * 
	 */
	protected HashMap<String, String> arrangeMap;
	
	protected ArrayList<String> currentColumnNameList;
	
	protected KSGTableModel model;
	
	protected JTable	tblTable;
	
	protected int searchTotalSize;
	
	protected int totalSize;
	
	private static final long serialVersionUID = 1L;
	
	DAOImplManager daoImplManager = DAOImplManager.getInstance();
	
	protected BaseInfoUI baseInfoUI;	
	
	public BaseInfoUI getBaseInfoUI() {
		return baseInfoUI;
	}
	protected BaseService service = new BaseServiceImpl();
	
	public PnBase(BaseInfoUI baseInfoUI) {
		this.baseInfoUI = baseInfoUI;
		
		baseDaoService = DAOImplManager.getInstance().createBaseDAOImpl();
		
		//order by를 위한 칼럼 목록 생성		
		arrangeMap = new HashMap<String, String>();
		
		// 칼럼 순서 정보를 저장하기 위한 클래스 생성
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
	
	public abstract void fnSearch();
	
	@Override
	public  void createAndUpdateUI() {};
	@Override
	public void update(KSGModelManager manager) {}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
