package com.dtp.api.schedule.joint.tree.node;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.schedule.execute.formater.JointFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class ScheduleTreeNode extends DefaultMutableTreeNode{
	
	
	private String name;
	
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
	
	public ScheduleTreeNode(String name, TreeTableNode treeTableNode, NodeType type) {
		this(treeTableNode, type);
		this.name = name;
	}

	protected JointFormatter formatter;
	
	public String toString()
	{
		return formatter!=null? formatter.getFormattedString():this.name!=null?name:super.toString();
	}
	public NodeType getType()
	{
		return type;
	}

}
