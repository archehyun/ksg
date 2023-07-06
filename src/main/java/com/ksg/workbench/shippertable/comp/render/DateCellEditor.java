package com.ksg.workbench.shippertable.comp.render;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class DateCellEditor extends DefaultCellEditor {

	private JTextField editor;

	public DateCellEditor() {
		super(new JTextField());

	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
			int row, int column) {
		
		
		editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
				row, column);

		LookAndFeel.installColorsAndFont(editor, "TableHeader.background",
				"TableHeader.foreground", "TableHeader.font");


		if (value != null)
		{
			editor.setForeground(Color.RED);
			editor.setText(value.toString());
			editor.selectAll();
		}

		editor.setHorizontalAlignment(SwingConstants.CENTER);
		//editor.setFont(new Font("Serif", Font.BOLD, 14));
		return editor;
	}
	public Object getCellEditorValue() {

		return editor.getText();
	}
}