package com.ksg.workbench.shippertable.comp.render;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

public class TableRowDeleteCellEditor extends DefaultCellEditor
{
	public TableRowDeleteCellEditor() {
		super(new JTextField());
	}

	@Override
	public Component getTableCellEditorComponent(JTable jtable, Object o, boolean bln, int row, int column) {
		DelActionPanel action = new DelActionPanel(jtable);
		action.initEvent(row);
		action.setBackground(jtable.getSelectionBackground());
		return action;
	}
	public Object getCellEditorValue() {
		return null;
	}

}