package com.ksg.schedule.joint;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractGroup extends HashMap<String,Object>	
{
	String strKey;
	
	public AbstractGroup() {
	}
	public AbstractGroup(String keyName) {
		strKey=keyName;
	}
	public List addSchedule(Map<String, Object> obj)
	{
		List li = null;
		String key=((String) obj.get(strKey)).toUpperCase();
		
		if(this.containsKey(key))
		{
			li = (List) this.get(key);
			li.add(obj);
			
		}else
		{
			li = new LinkedList<HashMap<String, Object>>();
			li.add(obj);
			this.put(key, li);
		}
		return li;
	}
}
