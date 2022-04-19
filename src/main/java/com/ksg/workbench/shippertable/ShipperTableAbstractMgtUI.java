package com.ksg.workbench.shippertable;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.SQLException;

import com.ksg.workbench.common.comp.AbstractMgtUI;

public abstract class ShipperTableAbstractMgtUI extends AbstractMgtUI implements ComponentListener{
	
	
	protected boolean isShowData=true;
	
	public ShipperTableAbstractMgtUI()
	{
		super();
	}
	
	public abstract void showTableList() throws SQLException;
	
	public abstract void searchADVTable() throws SQLException;

	public abstract  void setPortCount(int count);
	
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
		//fnSearch();
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Deprecated
	public void updateSubTable() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Deprecated
	public void searchByOption() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void fnUpdate() {
		
		
	}

}
