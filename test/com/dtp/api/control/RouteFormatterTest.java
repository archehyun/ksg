package com.dtp.api.control;

import org.junit.Test;

import com.ksg.common.model.CommandMap;
import com.ksg.schedule.execute.formater.JointFormatter;
import com.ksg.schedule.execute.formater.Routeformatter;

public class RouteFormatterTest {
	
	@Test
	public void testRouteformmater()
	{
		JointFormatter routeformatter = new Routeformatter();
		CommandMap param = new CommandMap();
		param.put("vessel", "test");
		param.put("voyage", "voy");
		routeformatter.setParam(param);
		System.out.println(routeformatter.getFormattedString());
		
		
	}
	

}
