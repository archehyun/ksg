package com.ksg.workbench.adv.comp;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.view.comp.PageInfoCheckBox;

public class PageCellRenderer extends JCheckBox implements ActionListener,ListCellRenderer, ListSelectionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Font defaultFont;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	public PageCellRenderer() {
		setOpaque(true);
		defaultFont = KSGModelManager.getInstance().defaultFont;

	}
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		
		if(value instanceof PageInfoCheckBox)
		{
			PageInfoCheckBox info = (PageInfoCheckBox) value;
			info.setFont(defaultFont);
			this.setText(info.getText());

			if(isSelected)
			{
				info.setBackground(list.getSelectionBackground());
				info.setForeground(list.getSelectionForeground());
			}else
			{
				info.setBackground(list.getBackground());
				info.setForeground(list.getForeground());
			}
			if(cellHasFocus)
			{
				info.setFocusable(false);
			}
			return info;
		}else
		{
			return (Component) value;
		}
		



	}
	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting())
		{
			JList li=(JList) e.getSource();
			JCheckBox box=(JCheckBox) li.getSelectedValue();
			if(box.getModel().isSelected())
			{
				box.getModel().setSelected(false);
			}else
			{
				box.getModel()	.setSelected(true);
			}
			li.setSelectedIndex(-1);
			li.setSelectedValue(null, true);

		}
	}

}
