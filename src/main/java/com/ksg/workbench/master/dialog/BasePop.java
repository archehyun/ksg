package com.ksg.workbench.master.dialog;

import javax.swing.JDialog;

import com.ksg.common.util.ViewUtil;

@SuppressWarnings("serial")
public class BasePop extends JDialog{
	
	public static final int OK=1;
	
	public static final int CANCEL=2;
	
	public int result;
	
	public void showPop()
	{
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		ViewUtil.center(this, true);
	}
	
	public void close()
	{
		this.setVisible(false);
		this.dispose();
	}

}
