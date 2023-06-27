package com.ksg.view.comp.textfield;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;

import com.ksg.view.comp.KSGViewUtil;
import com.ksg.view.comp.button.ImageButton;
import com.ksg.view.comp.panel.KSGPanel;

public class SearchTextField extends KSGPanel
{
	JTextField txfFromPort; 
	
	JButton butSearchFromPort = new ImageButton("images/search1.png");
	
	private KSGViewUtil propeties = KSGViewUtil.getInstance();
	
	public SearchTextField()
	{
		super(new BorderLayout());
		
		txfFromPort = new JTextField(8);		

		txfFromPort.setEditable(false);
		
		txfFromPort.setBackground(propeties.getColor("searchtextfield.readonly") );
		
		txfFromPort.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
		
		butSearchFromPort.setBackground(Color.white);

		butSearchFromPort.setActionCommand("SEARCH_FROM_PORT");
		
		butSearchFromPort.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));			
		
		add(txfFromPort);
		
		add(butSearchFromPort,BorderLayout.EAST);
		
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
	}
	
	public void addActionListener(ActionListener listener)
	{
		butSearchFromPort.addActionListener(listener);
	}
	
	public String getText()
	{
		return txfFromPort.getText();
	}
	
	public void setActionCommand(String command)
	{
		butSearchFromPort.setActionCommand(command);	
	}
	
	public void setText(String text)
	{
		txfFromPort.setText(text);
	}
	
	
}