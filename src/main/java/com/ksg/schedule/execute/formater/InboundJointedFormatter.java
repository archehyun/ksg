package com.ksg.schedule.execute.formater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.ksg.common.util.KSGDateUtil;
import com.ksg.workbench.schedule.comp.treenode.InboundCodeMap;


public class InboundJointedFormatter extends JointFormatter
{
	
	
	InboundCodeMap inboundCodeMap;
	
	private SimpleDateFormat inputDateFormat 	= KSGDateUtil.inputDateFormat;

	private SimpleDateFormat outputDateFormat 	= KSGDateUtil.createOutputDateFormat();
	
	public InboundJointedFormatter() {
		
		inboundCodeMap = InboundCodeMap.getInstance();
		
	}

	@Override
	public String getFormattedString() {
		try {
			HashMap<String,Object> item = scheduleList.get(0);
			String fromDate 	= outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("DateF"))));
			String toDate 		= outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("DateT"))));
			String company 		= String.valueOf(item.get("company_abbr"));
			String fromPort 	= String.valueOf(item.get("fromPort"));
			String toPort 		= String.valueOf(item.get("port"));
			
			return String.format("%-8s%s  (%s)  %s", fromDate, getNodeName(), company, getFormattedToPorts());
			
		}catch(Exception e)
		{
			e.printStackTrace();
			return "error";
		}
	}

	private String getFormattedToPorts() throws ParseException
	{
		StringBuffer strPots= new StringBuffer();

		for(HashMap<String, Object>item: scheduleList)
		{
			String portCode = (String) inboundCodeMap.get(item.get("port"));
			String toDate =outputDateFormat.format(inputDateFormat.parse(String.valueOf(item.get("DateT"))));
			strPots.append("["+portCode+"]"+toDate);
		}
		return strPots.toString();
	}

}
