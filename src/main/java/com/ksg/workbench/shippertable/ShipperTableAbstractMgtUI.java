package com.ksg.workbench.shippertable;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.SQLException;

import com.ksg.view.comp.panel.KSGPanel;

public abstract class ShipperTableAbstractMgtUI extends KSGPanel implements ComponentListener{
	
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

}
