package com.ksg.workbench.base.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.dao.DAOImplManager;
import com.ksg.view.comp.panel.KSGPanel;
import com.ksg.view.comp.table.model.KSGTableModel;
import com.ksg.workbench.KSGViewParameter;
import com.ksg.workbench.base.BaseInfoUI;

/**
 * 마스터 화면 추상 클래스
 * 
 * @author 박창현
 *
 */
public abstract class PnBase extends KSGPanel implements ComponentListener{
	/**
	 * 
	 */
	protected HashMap<String, String> arrangeMap;
	
	protected boolean isShowData=false;	
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
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
	
	
	public PnBase(BaseInfoUI baseInfoUI) {
		this.baseInfoUI = baseInfoUI;
		
		//order by를 위한 칼럼 목록 생성		
		arrangeMap = new HashMap<String, String>();
		
		// 칼럼 순서 정보를 저장하기 위한 클래스 생성
		currentColumnNameList = new ArrayList<String>();		
		
		this.setLayout(new BorderLayout());		
		
		this.setBorder(BorderFactory.createLineBorder(Color.gray));
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


	
	public abstract void fnSearch();
	
	@Override
	public  void createAndUpdateUI() {};
	
	@Override
	public void componentResized(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	

}
