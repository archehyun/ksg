package com.ksg.view.comp.treetable;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.ksg.common.util.KSGDateUtil;

public class TreeTableNode {
	HashMap<String, Object> data;
	String vessel;
	String company;
	private String fromDate;
	private String toDate;
	private SimpleDateFormat inputDateFormat 	= KSGDateUtil.createInputDateFormat();

	private SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();
	public TreeTableNode(HashMap<String, Object> data) {
		this.data = data;
	}
	
	

	
	public Object get(Object key)
	{		
		return data.get(key);
	}

}
