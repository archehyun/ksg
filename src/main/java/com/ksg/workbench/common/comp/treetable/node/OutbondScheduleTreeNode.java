package com.ksg.workbench.common.comp.treetable.node;

import java.util.List;

import com.ksg.schedule.execute.formater.OutboundFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class OutbondScheduleTreeNode extends ScheduleTreeNode
{
	public OutbondScheduleTreeNode() {
		super();
	}
	
	public OutbondScheduleTreeNode(String string) {
		super(string);
	}
	public OutbondScheduleTreeNode(String string, List scheduleList) {
		super(string);
	}

	public OutbondScheduleTreeNode(TreeTableNode treeTableNode) {
		super(treeTableNode);
		formatter = new OutboundFormatter();
		formatter.setParam(treeTableNode.getParam());
	}
	

}