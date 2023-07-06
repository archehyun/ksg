package com.ksg.workbench.shippertable.comp.render;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableRowDeleteCellRenderer extends DefaultTableCellRenderer
{
	@Override
	public Component getTableCellRendererComponent(JTable jtable, Object o, boolean isSeleted, boolean bln1, int row, int column) {
		Component com = super.getTableCellRendererComponent(jtable, o, isSeleted, bln1, row, column);
		DelActionPanel action = new DelActionPanel(jtable);

		Object obj= jtable.getValueAt(row, 0);

		action.setButVisible(obj!=null&&!String.valueOf(obj).isEmpty());
		
		

		if (isSeleted == false && row % 2 == 0) {
			action.setBackground(Color.WHITE);
		} else {
			action.setBackground(com.getBackground());
		}
		return action;
	}
}
