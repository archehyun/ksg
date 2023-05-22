package com.dtp.api.schedule.joint.print.inbound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;

public class InboundScheduleRule {
	
	Map<String, Vessel> vesselMap;
	
	public InboundScheduleRule(Map<String, Vessel> vesselMap)
	{
		this.vesselMap = vesselMap;
	}
	
	public List<InboundScheduleGroup> getInboundScheduleGroup(Map<String,List<ScheduleData>> list, boolean printTag)
	{	
		ArrayList<InboundScheduleGroup> groupList = new ArrayList<InboundScheduleGroup>();
		
		for(Object vessel: list.keySet())		
		{
			List<ScheduleData> li =list.get(vessel);

					
			Map<Integer, Map<String, List<ScheduleData>>> vesselScheduleMap = li.stream().collect(
					Collectors.groupingBy(ScheduleData::getIntVoyage_num, // Áö¿ª
							Collectors.groupingBy(ScheduleData::getVessel)));

			
			for(Object key:vesselScheduleMap.keySet())
			{
				Map<String, List<ScheduleData>> scheduleList = vesselScheduleMap.get(key);
				
				groupList.addAll(
				scheduleList.entrySet().stream()
										.map(o -> new InboundScheduleGroup(vesselMap.get( o.getKey()), o.getValue(),printTag))
										.collect(Collectors.toList()));
				
			}
			
		}
		
		return groupList;
	}

}
