package com.ksg.workbench.common.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.dtp.api.control.AbstractController;
import com.ksg.common.model.CommandMap;
import com.ksg.view.comp.border.CurvedBorder;
import com.ksg.view.comp.panel.KSGPanel;

/**

  * @FileName : AbstractMgtUI.java

  * @Project : KSG2

  * @Date : 2022. 3. 10. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public abstract class AbstractMgtUI extends KSGView implements View{
	
	protected String title;
	
	protected Color borderColor;
	
	private AbstractController controller;
	
	private CommandMap model;
	
	public AbstractMgtUI()
	{
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));	
	}	
	
	protected KSGPanel buildNorthPn() {
		
		KSGPanel pnNorth = new KSGPanel(new BorderLayout());
		
		pnNorth.setPreferredSize(new Dimension(0,35));

		KSGPanel pnTitleMain = new KSGPanel(new BorderLayout());
		
		KSGPanel pnTitle = new KSGPanel();
		
		pnTitle.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JLabel label = new JLabel(title);
		
		label.setForeground(new Color(61,86,113));

		Font titleFont = new Font("명조",Font.BOLD,18);
		
		label.setFont(titleFont);
		
		pnTitle.add(label);

		pnTitle.setBorder(new CurvedBorder(8,borderColor));
		
		pnTitleMain.add(pnTitle);
		
		KSGPanel pnTitleBouttom = new KSGPanel();
		
		pnTitleBouttom.setPreferredSize(new Dimension(0,15));
		
		pnTitleMain.add(pnTitleBouttom,BorderLayout.SOUTH);

		return pnTitleMain;
	}
	
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

}
