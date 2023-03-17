package com.ksg.workbench.shippertable;

import java.sql.SQLException;

import com.dtp.api.control.AbstractController;
import com.ksg.common.model.CommandMap;
import com.ksg.workbench.common.comp.AbstractMgtUI;
import com.ksg.workbench.common.comp.View;

@SuppressWarnings("serial")
public abstract class ShipperTableAbstractMgtUI extends AbstractMgtUI implements View{
	
	
	protected boolean isShowData=true;
	
	protected CommandMap model;
	
	
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
