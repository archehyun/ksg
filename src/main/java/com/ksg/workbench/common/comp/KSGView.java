package com.ksg.workbench.common.comp;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import com.dtp.api.control.AbstractController;
import com.ksg.common.model.CommandMap;
import com.ksg.view.comp.panel.KSGPanel;

public abstract class KSGView extends KSGPanel implements ActionListener, ComponentListener, View{
	
	
	protected Font labelFont = new Font("¸¼Àº°íµñ",Font.BOLD,12);
	protected AbstractController controller;
	
	protected CommandMap model;
	
	@Override
	public void componentResized(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
	

	@Override
	public void setModel(CommandMap model) {
		this. model = model;

	}
	public CommandMap getModel() {

		return model;
	}

	public void callApi(String serviceId, CommandMap param)
	{
		if(this.controller!=null)
			this.controller.call(serviceId, param, this);
	}
	
	public void callApi(String serviceId)
	{
		if(this.controller!=null)
			this.controller.call(serviceId, new CommandMap(),this);
	}
	
	public void updateView() {};
	
	public void setController(AbstractController constroller)
	{
		this.controller =constroller;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

}
