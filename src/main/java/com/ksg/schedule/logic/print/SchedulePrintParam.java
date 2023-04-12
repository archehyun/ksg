package com.ksg.schedule.logic.print;

import java.util.List;
import java.util.Map;

import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class SchedulePrintParam {
	private String scheduleType;
	
	private List<ScheduleData> scheduleList;
	
	private Map<String, PortInfo> portMap;
	
	private Map<String, Vessel> vesselMap;
	
	private int orderBy;

}
