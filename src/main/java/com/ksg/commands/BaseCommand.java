package com.ksg.commands;

import javax.swing.JTable;

import com.ksg.service.BaseService;

public abstract class BaseCommand implements KSGCommand{
	protected int totalSize;
	protected int searchTotalSize;
	protected JTable _tblTable;
	protected String orderBy;
	protected int search_type;

	protected BaseService baseService;
	public int getTotalSize() {
		// TODO Auto-generated method stub
		return totalSize;
	}
	public int getSearchTotalSize() {
		return searchTotalSize;
	}
	public void setSearchTotalSize(int searchTotalSize) {
		this.searchTotalSize = searchTotalSize;
	}
}
