package com.ksg.adv.view.comp;

import java.awt.Color;

import javax.swing.Icon;



public class VesselInfo {
	DiamondIcon icon;
	private boolean isExist;
	private boolean isMulti;
	public String vesselName;
	public Icon getIcon() 
	{
		return icon;
	}

	public boolean isExist() {
		return isExist;
	}

	public void setExist(boolean isExist) {
		this.isExist = isExist;
		if(!isExist)
		{
			icon=new DiamondIcon(Color.red); 
		}else
		{

		}
	}
	public String toString()
	{
		return vesselName;
	}
	public void setMulti(boolean flag)
	{
		this.isMulti = flag;
	}
	public boolean isMulti() {
		// TODO Auto-generated method stub
		return isMulti;
	}
}
