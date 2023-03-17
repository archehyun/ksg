package com.ksg.view.comp.treetable;

import com.ksg.common.model.CommandMap;

public class TreeTableNode {
	
	
	private CommandMap param;

	public CommandMap getParam()
	{
		return param;
	}
	public TreeTableNode(CommandMap data) {
		this.param = data;
	}
	
	public Object get(Object key)
	{		
		return param.get(key);
	}
	public String toString()
	{
		return "";
	}

}
