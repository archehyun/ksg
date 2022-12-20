package com.ksg.schedule.execute.formater;

import com.ksg.common.model.CommandMap;

public class Routeformatter extends JointFormatter{
	
	
	String vesselName;
	
	String voyage;
	
	String fromPorts;
	
	String toPorts;
	
	@Override
	public void setParam(CommandMap param)
	{
		super.setParam(param);
		
		
		
//		this.fromDate = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(String.valueOf(param.get("dateF")));
		this.vesselName = String.valueOf(param.get("vessel"));
		this.voyage  = String.valueOf(param.get("voyage"));
		this.fromPorts =String.valueOf(param.get("fromPorts"));
		this.toPorts =String.valueOf(param.get("toPorts"));
//		this.company = String.valueOf(param.get("company_abbr"));
//		this.agent = String.valueOf(param.get("agent"));
//		this.toDate = KSGDateUtil.convertDateFormatYYYYMMDDToMMDD(String.valueOf(param.get("dateT")));
	}

	@Override
	public String getFormattedString() {
		fromPorts = "test";
		toPorts = "test1";
		
		return String.format("%s-%s \n%s\n%s", vesselName, voyage,fromPorts, toPorts);
	}

}
