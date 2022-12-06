package com.ksg.workbench.common.comp.treetable.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ksg.schedule.execute.formater.JointFormatter;
import com.ksg.view.comp.treetable.TreeTableNode;

public class InboundGroupTreeNode extends DefaultMutableTreeNode
{
	
	private JointFormatter jointedFormatter;
	
	protected String nodeName;
	
	private ArrayList<HashMap<String, Object>> scheduleList;
	
	public InboundGroupTreeNode(String nodeName) {
		
		this.nodeName = nodeName;
	}
	
	public InboundGroupTreeNode (String nodeName,JointFormatter jointedFormatter, ArrayList<HashMap<String, Object>> scheduleList) {
		this(nodeName);
		this.jointedFormatter = jointedFormatter;			
		this.jointedFormatter.setNodeName(nodeName);
		this.jointedFormatter.setSchedule(scheduleList);
		
		Iterator scheduleIter= scheduleList.iterator();
		
		while(scheduleIter.hasNext())
		{
			DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(new TreeTableNode((HashMap<String, Object>) scheduleIter.next()));
			add(subnode);
		}
	}
	public String toString ()
	{
		return jointedFormatter==null?nodeName:jointedFormatter.getFormattedString();
	}
}