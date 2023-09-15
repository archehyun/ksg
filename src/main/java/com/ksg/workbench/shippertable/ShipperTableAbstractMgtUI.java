package com.ksg.workbench.shippertable;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;

import com.ksg.common.model.CommandMap;
import com.ksg.workbench.common.comp.AbstractMgtUI;

@SuppressWarnings("serial")
public abstract class ShipperTableAbstractMgtUI extends AbstractMgtUI implements ActionListener, KeyListener{
	
	public final String ADV_CANCEL 		= "ADV_CANCEL";

	public final String SAVE_ADV_DATA 	= "SAVE_ADV_DATA";
	
	protected boolean isShowData=true;
	
	protected CommandMap model;
	
	public ShipperTableAbstractMgtUI()
	{
		super();
	}
	
	public abstract void showTableList() throws SQLException;
	
	public abstract void searchADVTable() throws SQLException;

	public abstract  void setPortCount(int count);

	public void fnUpdate() {}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}	
}