package com.ksg.view.comp.treetable;

import java.util.HashMap;

public class TreeTableNode {
	
	
	private HashMap<String, Object> data;

	
	public TreeTableNode(HashMap<String, Object> data) {
		this.data = data;
	}
	
	public Object get(Object key)
	{		
		return data.get(key);
	}

}
