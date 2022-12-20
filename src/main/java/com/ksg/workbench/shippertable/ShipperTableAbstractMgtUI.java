package com.ksg.workbench.shippertable;

import java.sql.SQLException;

import com.ksg.workbench.common.comp.AbstractMgtUI;

@SuppressWarnings("serial")
public abstract class ShipperTableAbstractMgtUI extends AbstractMgtUI {
	
	
	protected boolean isShowData=true;
	
	public ShipperTableAbstractMgtUI()
	{
		super();
	}
	
	public abstract void showTableList() throws SQLException;
	
	public abstract void searchADVTable() throws SQLException;

	public abstract  void setPortCount(int count);
	
	

	@Deprecated
	public void updateSubTable() throws SQLException {}

	@Deprecated
	public void searchByOption() throws SQLException {}

	public void fnUpdate() {}

}
