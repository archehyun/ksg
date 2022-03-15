package com.ksg.view.comp.treetable;

import java.util.HashMap;

public class TreeTableNode {
	HashMap<String, Object> data;
	public TreeTableNode(HashMap<String, Object> data) {
		this.data = data;
	}
	
	
	public String toString()
	{
		return "";
	}
	
	public Object get(Object key)
	{
		//System.out.println("key:"+key+",data:"+data);
		return data.get(key);
	}

}
