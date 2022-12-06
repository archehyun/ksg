package com.ksg.workbench.common.comp.treetable.node;

import com.ksg.common.model.CommandMap;
import com.ksg.schedule.execute.formater.OutboundFormatter;

@SuppressWarnings("serial")
public class OutbondScheduleTreeNode extends ScheduleTreeNode
{
	public OutbondScheduleTreeNode() {
		super();
	}
	
	public OutbondScheduleTreeNode(String string) {
		super(string);
	}

	public OutbondScheduleTreeNode(CommandMap param) {
		super();
		formatter = new OutboundFormatter();
		formatter.setParam(param);
	}
	
	public String toString()
	{
		return formatter!=null? formatter.getFormattedString():super.toString();
	}
	

}