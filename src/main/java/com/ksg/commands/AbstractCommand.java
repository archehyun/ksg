package com.ksg.commands;

import javax.swing.JTable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.service.BaseService;

public abstract class AbstractCommand implements IFCommand{
	
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	
	protected int totalSize;
	protected int searchTotalSize;
	protected JTable _tblTable;
	protected String orderBy;
	protected int search_type;

	protected BaseService baseService;
	public int getTotalSize() {
		return totalSize;
	}
	public int getSearchTotalSize() {
		return searchTotalSize;
	}
	public void setSearchTotalSize(int searchTotalSize) {
		this.searchTotalSize = searchTotalSize;
	}
}
