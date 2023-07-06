package com.ksg.workbench.shippertable.comp.render;

import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;

import com.ksg.workbench.admin.KSGViewParameter;

@SuppressWarnings("serial")
public class VesselCellEditor  extends DefaultCellEditor
{
	public VesselCellEditor()
	{
		super(new JTextField());

	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {
		
		JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
				row, column);

		LookAndFeel.installColorsAndFont(editor, "TableHeader.background",
				"TableHeader.foreground", "TableHeader.font");

		Font defaultFont = new Font( editor.getFont().getName(), editor.getFont().getStyle(), KSGViewParameter.TABLE_CELL_SIZE);

		editor.setFont(defaultFont);

		editor.setText(value==null?"": String.valueOf(value));

		if(editor.isEditable()) editor.selectAll();

		return editor;
	}

}