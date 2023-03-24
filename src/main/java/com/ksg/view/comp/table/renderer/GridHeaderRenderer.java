package com.ksg.view.comp.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

@SuppressWarnings("serial")
public class GridHeaderRenderer extends DefaultTableCellRenderer{
	Color gridHeaderForeground = new Color(47,47,47);
	
	Color gridHeaderBackground = new Color(249,250,250);
	
	Font gridHeaderFont = new Font("Malgun Gothic",Font.BOLD,12);
	
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		if (table != null) {
			JTableHeader header = table.getTableHeader();
			if (header != null) {
				setForeground(gridHeaderForeground);

				setBackground(gridHeaderBackground);
				
				setFont(gridHeaderFont);
			}
		}

		setText((value == null) ? "" : value.toString());

		setBorder(BorderFactory.createLineBorder(new Color(220,220,220),1));

		setHorizontalAlignment(JLabel.CENTER);
		
		this.setPreferredSize(new Dimension(this.getSize().width,30));


		return this;
	}

}
