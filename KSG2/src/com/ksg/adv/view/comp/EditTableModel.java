package com.ksg.adv.view.comp;

import javax.swing.table.DefaultTableModel;

public class EditTableModel extends DefaultTableModel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EditTableModel(String[] columnames, int size) {
		super(columnames, size);
	}

	public boolean isCellEditable(int row, int column) {
		switch (column) {
		case 0:

			return false;
		case 1:

			return false;

		default:
			return true;
		}
	}
}
