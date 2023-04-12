package com.ksg.schedule.logic.print.inbound;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.ScheduleJointError;
import com.ksg.schedule.logic.print.SchedulePrintParam;

public class InboundSchedulePrintFile extends AbstractInboundSchedulePrint{

	
	private List<ScheduleData> scheduleList;
	public InboundSchedulePrintFile() throws SQLException {
		super();
	}
	
	public InboundSchedulePrintFile(CommandMap param) throws SQLException {
		this();
		
		this.scheduleList 		= (List<ScheduleData>) param.get("inboundScheduleList");
		
		this.vesselMap 			= (Map<String, Vessel>) param.get("vesselMap");
	}
	
	public InboundSchedulePrintFile(SchedulePrintParam param) throws SQLException {
		this();
		
		this.scheduleList 		= param.getScheduleList();
		
		this.vesselMap 			= param.getVesselMap();
	}
	
	@Override
	public int execute() throws Exception {
		fileName = fileLocation+"/"+INBOUND_PRINT_TXT;
		
		try{
		fw = new FileWriter(fileName);
		
		Map<String, Map<String, Map<String, List<ScheduleData>>>> areaList =  scheduleList.stream().collect(
				Collectors.groupingBy(ScheduleData::getArea_name, // 지역
						Collectors.groupingBy(ScheduleData::getFromPort,
								Collectors.groupingBy(ScheduleData::getVessel
								))));// 출발항(외국항)
		
		for(String areaName:areaList.keySet())
		{
			System.out.println("area:"+areaName);
			
			fw.write(areaName+"\n");
			
			Map<String, Map<String, List<ScheduleData>>> portList= areaList.get(areaName);
			
			for(String portName:portList.keySet())
			{
				fw.write("\tport:"+portName+"\n");
				
				Map<String, List<ScheduleData>> vesselList=portList.get(portName);
				
				ArrayList<InboundScheduleGroup> groupList = new ArrayList<InboundSchedulePrintFile.InboundScheduleGroup>();
				vesselList.keySet()
				          .forEach(vesselName ->  groupList.add(new InboundScheduleGroup(vesselMap.get(vesselName), vesselList.get(vesselName))));	
				 
				 groupList.forEach(group -> {
					try {
						fw.write("\t\t:"+group+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
		}
		return 0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			throw new ScheduleJointError(e,data);
			
		}
		finally
		{
			// 파일 닫기
			close();
		}
	}
	class InboundScheduleGroup
	{
		private Vessel vessel;
		
		private List<ScheduleData>scheduleList;
		
		public InboundScheduleGroup(Vessel vessel, List<ScheduleData>scheduleList)
		{
			this.vessel = vessel;
			
			this.scheduleList = scheduleList;
		}
		public Object collect(Collector<Object, ?, List<Object>> list) {
			// TODO Auto-generated method stub
			return null;
		}
		public String toString()
		{
			StringBuffer jointedInboundPort = new StringBuffer();
			scheduleList.forEach(o -> jointedInboundPort.append(String.format("[%s]", o.getPort())));
			
			return String.format("%s %s\n", vessel.getVessel_name(), jointedInboundPort.toString());
		}
		
		
	}
	@Override
	public void close() throws IOException {
		fw.close();
	}

}
