package com.ksg.workbench.shippertable.comp.render;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.LookAndFeel;
import javax.swing.table.TableCellRenderer;

import com.ksg.view.comp.panel.KSGPanel;

public class MultiLineHeaderRenderer extends KSGPanel implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label;

		removeAll();

		String[] header = ((String) value).split("\n");

		if(header.length>1)
		{
			setLayout(new GridLayout(header.length, 1));
		}

		this.setPreferredSize(new Dimension(100,40));

		for(String s: header){

			label = new JLabel(s, JLabel.CENTER);

			label.setFont(new Font("±¼¸²",Font.PLAIN,12));

			LookAndFeel.installColorsAndFont(label, "TableHeader.background",
					"TableHeader.foreground", "TableHeader.font");
			add(label);
		}

		LookAndFeel.installBorder(this, "TableHeader.cellBorder");

		return this;
	}
}