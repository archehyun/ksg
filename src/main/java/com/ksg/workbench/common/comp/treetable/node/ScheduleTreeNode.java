package com.ksg.workbench.common.comp.treetable.node;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.schedule.execute.formater.JointFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class ScheduleTreeNode extends DefaultMutableTreeNode{
	
	NodeType type;
	
	public ScheduleTreeNode() {};
	
	public ScheduleTreeNode(String string, NodeType type) {
		super(string);
		this.type = type;
	}

	

	public ScheduleTreeNode(TreeTableNode treeTableNode, NodeType type) {
		super(treeTableNode);
		this.type = type;
	}

	protected JointFormatter formatter;
	
	public String toString()
	{
		return formatter!=null? formatter.getFormattedString():super.toString();
	}
	public NodeType getType()
	{
		return type;
	}

}
