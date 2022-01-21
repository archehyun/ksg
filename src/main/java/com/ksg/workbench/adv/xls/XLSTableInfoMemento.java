package com.ksg.workbench.adv.xls;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.domain.ShippersTable;
@SuppressWarnings("unchecked")
public class XLSTableInfoMemento {
	public Vector companyList;
	public Vector<ShippersTable> pageList;	
	public Vector tableList;
	protected Logger logger = LogManager.getLogger(this.getClass());
	private String searchType;
	private String selectedInput;
	
	public XLSTableInfoMemento() 
	{
		logger.debug("create xlstableMemento");
		this.companyList = new Vector();
		this.pageList = new Vector();
		tableList = new Vector();
		
	}
	// xls ������ �߰��Ѵ�.
	public void addXLSTableInfo(XLSTableInfo info)
	{
//		logger.debug("add to memento xlstableinfo:"+info.toString());
		tableList.add(info);
	}
	public Vector getXLSTableInfo()
	{
		return tableList;
	}
	public void setPageList(Vector<ShippersTable> pageList) 
	{
		this.pageList = pageList;
		logger.debug("memento pageList:"+this.pageList);
		
	}
	public void setSearchType(String searchType) {
		this.searchType=searchType;
		
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSelectedInput(String selectedInput) {
		this.selectedInput = selectedInput;
	}
	public String getSelectedInput() {
		return selectedInput;
	}

}
