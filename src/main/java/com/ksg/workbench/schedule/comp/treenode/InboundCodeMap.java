package com.ksg.workbench.schedule.comp.treenode;

import java.util.HashMap;

import com.dtp.api.service.impl.CodeServiceImpl;

public class InboundCodeMap {
	
	private static InboundCodeMap instance;
	
	protected CodeServiceImpl codeService;
	
	private static HashMap<String, Object> inboundCodeMap;
	
	private InboundCodeMap()
	{
		codeService = new CodeServiceImpl();
		try {
			inboundCodeMap = (HashMap<String, Object>) codeService.selectInboundPortMap();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static InboundCodeMap getInstance()
	{
		if(instance == null)
			return instance = new InboundCodeMap();
		return instance;
	}
	
	public String get(Object object)
	{
		return (String) inboundCodeMap.get(object);
	}
	
	

}
