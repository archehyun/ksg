package com.ksg.workbench.common.comp.treetable.node;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.common.model.CommandMap;
import com.ksg.schedule.execute.formater.InboundJointedFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class InboundGroupTreeNode extends ScheduleTreeNode
{
	
	protected String nodeName;
	
	private ArrayList<CommandMap> scheduleList;
	
	public InboundGroupTreeNode(String nodeName) {
		
		this.nodeName = nodeName;
	}
	
	public InboundGroupTreeNode (String nodeName, ArrayList<CommandMap> scheduleList) {
		this(nodeName);
		
		this.formatter = new InboundJointedFormatter();
		
		((InboundJointedFormatter)this.formatter).setNodeName(nodeName);
		
		((InboundJointedFormatter)this.formatter).setSchedule(scheduleList);
		
		scheduleList.stream().forEach(item -> add(new DefaultMutableTreeNode(new TreeTableNode(item))) );
		
		
	}

}