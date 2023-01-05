package com.ksg.workbench.common.comp.treetable.node;

import com.ksg.schedule.execute.formater.OutboundFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class OutbondScheduleTreeNode extends ScheduleTreeNode implements Comparable<Object>
{
	
	public String getVessel() {
		return vessel;
	}
	public String vessel;
	
	public String date;
	
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

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}
	

}