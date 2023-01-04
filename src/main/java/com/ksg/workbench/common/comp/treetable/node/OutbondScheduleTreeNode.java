package com.ksg.workbench.common.comp.treetable.node;

import com.ksg.schedule.execute.formater.OutboundFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class OutbondScheduleTreeNode extends ScheduleTreeNode
{
	
	
	public OutbondScheduleTreeNode(String string) {
		super(string, NodeType.SCHEDULE);
	}
	
	public OutbondScheduleTreeNode(String string, NodeType nodeType) {
		super(string, nodeType);
	}
	

	public OutbondScheduleTreeNode(TreeTableNode treeTableNode) {
		super(treeTableNode,  NodeType.SCHEDULE);
		formatter = new OutboundFormatter();
		formatter.setParam(treeTableNode.getParam());
	}
	
	public OutbondScheduleTreeNode(TreeTableNode treeTableNode, NodeType nodeType) {
		super(treeTableNode,  nodeType);
		formatter = new OutboundFormatter();
		formatter.setParam(treeTableNode.getParam());
	}
	

}