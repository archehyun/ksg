package com.ksg.workbench.common.comp.treetable;

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

import com.ksg.view.comp.treetable.JTreeTable;
import com.ksg.view.comp.treetable.TreeTableModel;
import com.ksg.workbench.common.comp.treetable.node.InboundGroupTreeNode;
import com.ksg.workbench.common.comp.treetable.node.InboundPortTreeNode;
import com.ksg.workbench.common.comp.treetable.node.JointOutboundScheduleTreeNode;
import com.ksg.workbench.common.comp.treetable.node.OutbondScheduleTreeNode;

@SuppressWarnings("serial")
public class KSGTreeTable extends JTreeTable{

	Image changePortImg;
	Image changeShipImg;
	Image changeShipRedImg;

	public KSGTreeTable(TreeTableModel treeTableModel) {
		super(treeTableModel);
		Image img = new ImageIcon("images/port.png").getImage();
		changePortImg = img.getScaledInstance(15, 15, Image.SCALE_SMOOTH);

		Image img2 = new ImageIcon("images/ship_group.png").getImage();
		changeShipImg = img2.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		
		Image img3 = new ImageIcon("images/ship_group_red.png").getImage();
		changeShipRedImg = img3.getScaledInstance(15, 15, Image.SCALE_SMOOTH);

		this.setCellRenderer(new ScheduleCellRenderer());

	}

	class ScheduleCellRenderer extends JLabel implements TreeCellRenderer
	{

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {

			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

			TreeNode t = node.getParent();
			
			if(t== null) return this;
			
			if(node instanceof InboundPortTreeNode)
			{
				setIcon(new ImageIcon(changePortImg));
			}
			else if(node instanceof InboundGroupTreeNode)
			{
				setIcon(new ImageIcon(changeShipImg)); 
			}
			else if(node instanceof OutbondScheduleTreeNode)
			{
				setIcon(new ImageIcon(changeShipImg)); 
			} 
			
			else if(node instanceof JointOutboundScheduleTreeNode)
			{
				setIcon(new ImageIcon(changeShipRedImg)); 
			} 

			setText(String.valueOf(value));
			
			return this;
		}

	}

}
