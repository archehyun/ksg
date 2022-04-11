package com.ksg.workbench.common.comp.treetable.node;

import javax.swing.tree.DefaultMutableTreeNode;

public class PortTreeNode extends DefaultMutableTreeNode{
	
	String port_name;
	
	String area;
	
	public PortTreeNode(String port_name, String area)
	{
		this.port_name =port_name;
		
		this.area =area;
		
	}
	
	public PortTreeNode(Object toPortKey) {
		
		this.port_name =(String) toPortKey;
	}

	public String toString()
	{
		return port_name+(area!=null?"  "+area:"");
	}

}
