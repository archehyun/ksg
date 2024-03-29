package com.ksg.workbench.shippertable;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.SQLException;

import com.ksg.workbench.common.comp.AbstractMgtUI;

@SuppressWarnings("serial")
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
	public void componentResized(ComponentEvent e) {}
	@Override
	public void componentMoved(ComponentEvent e) {}
	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}

	@Deprecated
	public void updateSubTable() throws SQLException {}

	@Deprecated
	public void searchByOption() throws SQLException {}

	public void fnUpdate() {}

}
