package com.ksg.workbench.common.comp.treetable.node;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.schedule.execute.formater.JointFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class ScheduleTreeNode extends DefaultMutableTreeNode{
	
	public ScheduleTreeNode(String string) {
		super(string);
	}

	public ScheduleTreeNode() {
		super();
	}

	public ScheduleTreeNode(TreeTableNode treeTableNode) {
		super(treeTableNode);
	}

	protected JointFormatter formatter;
	
	public String toString()
	{
		return formatter!=null? formatter.getFormattedString():super.toString();
	}

}
