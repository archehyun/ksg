package com.ksg.base.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.Vessel;
import com.ksg.schedule.execute.joint.impl.OutboundScheduleJoint;
import com.ksg.service.VesselService;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;

public class VesselServiceTeset {
	
	VesselService service;
	@Before
	public void setUp()
	{
		service = new VesselServiceImpl();
		
	}
	
	@Test
	public void test()
	{
		try {
			
			List<String> vesselName = new ArrayList<String>();
			vesselName.add("test");
			CommandMap param = new CommandMap();
			param.put("vessel_use", 0);
			param.put("vesselNameList", vesselName.toArray(new String[vesselName.size()]));
			param.put("vessel_type", "A");
			
		List<Vessel> li=	service.selectListByCondition(param);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
