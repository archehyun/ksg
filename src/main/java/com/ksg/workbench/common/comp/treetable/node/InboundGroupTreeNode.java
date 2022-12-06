package com.ksg.workbench.common.comp.treetable.node;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.schedule.execute.formater.InboundJointedFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

@SuppressWarnings("serial")
public class InboundGroupTreeNode extends ScheduleTreeNode
{
	
	protected String nodeName;
	
	private ArrayList<HashMap<String, Object>> scheduleList;
	
	public InboundGroupTreeNode(String nodeName) {
		
		this.nodeName = nodeName;
	}
	
	public InboundGroupTreeNode (String nodeName, ArrayList<HashMap<String, Object>> scheduleList) {
		this(nodeName);
		
		this.formatter = new InboundJointedFormatter();
		
		((InboundJointedFormatter)this.formatter).setNodeName(nodeName);
		
		((InboundJointedFormatter)this.formatter).setSchedule(scheduleList);
		
		scheduleList.stream().forEach(item -> add(new DefaultMutableTreeNode(new TreeTableNode(item))) );
		
		
	}
	public String toString ()
	{
		return formatter==null?nodeName:formatter.getFormattedString();
	}
}