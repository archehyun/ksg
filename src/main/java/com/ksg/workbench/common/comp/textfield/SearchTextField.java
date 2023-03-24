package com.ksg.workbench.common.comp.textfield;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;

import com.ksg.workbench.common.comp.button.ImageButton;
import com.ksg.workbench.common.comp.panel.KSGPanel;

public class SearchTextField extends KSGPanel
{
	JTextField txfFromPort; 
	
	JButton butSearchFromPort = new ImageButton("images/search1.png");
	
	public SearchTextField()
	{
		super(new BorderLayout());
		
		txfFromPort = new JTextField(8);		

		txfFromPort.setEditable(false);
		
		txfFromPort.setBackground(Color.white);
		txfFromPort.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
//		txfFromPort.setBorder(BorderFactory.createEmptyBorder());

		
		
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