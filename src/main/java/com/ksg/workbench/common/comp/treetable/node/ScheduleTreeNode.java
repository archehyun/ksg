package com.ksg.workbench.common.comp.treetable.node;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.schedule.execute.formater.JointFormatter;

@SuppressWarnings("serial")
public class ScheduleTreeNode extends DefaultMutableTreeNode{
	
	public ScheduleTreeNode(String string) {
		super(string);
	}

	public ScheduleTreeNode() {
		super();
	}

	protected JointFormatter formatter;

}
