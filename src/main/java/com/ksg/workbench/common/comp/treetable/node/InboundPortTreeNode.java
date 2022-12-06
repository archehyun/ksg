package com.ksg.workbench.common.comp.treetable.node;

public class InboundPortTreeNode extends ScheduleTreeNode{
	
	private String port_name;
	
	private String area;
	
	public InboundPortTreeNode(String port_name, String area)
	{
		this.port_name =port_name;
		
		this.area =area;
		
	}
	
	public InboundPortTreeNode(Object toPortKey) {
		
		this.port_name =(String) toPortKey;
	}

	public String toString()
	{
		return port_name+(area!=null?"  "+area:"");
	}

}
