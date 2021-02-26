package com.ksg.common.comp;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class KSGPanel extends JPanel{
	
	
	public KSGPanel()
	{
		super();
		this.setBackground(Color.white);
	}
	public KSGPanel(LayoutManager layout) {
		super(layout);
		this.setBackground(Color.white);
	}

}
