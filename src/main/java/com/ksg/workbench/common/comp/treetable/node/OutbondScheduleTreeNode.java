package com.ksg.workbench.common.comp.treetable.node;

import com.ksg.schedule.execute.formater.OutboundFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class OutbondScheduleTreeNode extends ScheduleTreeNode
{
	
	
	public OutbondScheduleTreeNode(String string) {
		super(string, NodeType.SCHEDULE);
	}
	

	public OutbondScheduleTreeNode(TreeTableNode treeTableNode) {
		super(treeTableNode,  NodeType.SCHEDULE);
		formatter = new OutboundFormatter();
		formatter.setParam(treeTableNode.getParam());
	}
	

}