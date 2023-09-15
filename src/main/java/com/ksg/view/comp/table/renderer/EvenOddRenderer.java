/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.view.comp.table.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import org.jdom.Element;

import com.ksg.view.comp.ColorData;


public class EvenOddRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private boolean is=true;
	private static final long serialVersionUID = 1L;
	
	public boolean isCellEditable(int row, int column)
	{
		return (column ==0);
	}
	public void setHorizontalAlignment(int i)
	{
		super.setHorizontalAlignment(i);
	}
	public void setVisible(boolean is)
	{
		super.setVisible(is);
		this.is=is;
	}

	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		Component renderer=super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
		((JLabel) renderer).setOpaque(true);
		Color foreground, background;
		
		 if (table != null) {
		      JTableHeader header = table.getTableHeader();
		      if (header != null) {
		        setForeground(Color.BLUE);
		        setBackground(header.getBackground());
		        setFont(header.getFont());
		        
		      }
		    }

		
		
		if(isSelected)
		{
			foreground = Color.black;
			background=Color.YELLOW;
		}
		else
		{
			if(row %2==0)
			{
				foreground = Color.black;
				background=Color.WHITE;	
			}
			else
			{
				background= new Color(225,235,255);		
				foreground = Color.black;
			}
		}
		
		
		if(value instanceof ColorData)
		{
			ColorData v = (ColorData) value;
			renderer.setForeground(v.m_color);
			
		}else if(value instanceof Element)
		{	
			renderer.setForeground(foreground);
			Element v = (Element) value;

				((JLabel) renderer).setText(v.getAttributeValue("date"));
			((JLabel) renderer).setToolTipText(v.getAttributeValue("date2"));
			
				
		}else
		{
			renderer.setForeground(foreground);
		}
		renderer.setBackground(background);
	

		

		return renderer;
	}
	
	
}

