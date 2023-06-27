package com.dtp.api.schedule.create;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.ListUtils;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleBuild;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.ScheduleServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
public class CreateScheduleDataGroup {

	protected ScheduleSubService 	scheduleService;

	private VesselServiceV2 vesselService;

	private List<ShippersTable> shipperTableList;
		
	List<CreateScheduleData> createScheduleDataList;
	

	public CreateScheduleDataGroup(List<ShippersTable> shipperTableList)
	{
		scheduleService = new ScheduleServiceImpl();
		
		this.shipperTableList = shipperTableList;
	}

	public int execute() throws Exception {

		try {

			log.info("Start");

			long startTime = System.currentTimeMillis();
			
			createScheduleDataList = shipperTableList.stream()
													.filter(item -> item.getData()!=null)
													.map(schedule -> {
														try {
															return new CreateScheduleData(schedule, schedule.getData());
														
														} catch (Exception e) {
															// TODO Auto-generated catch block
															e.printStackTrace();
														}
														return null;
													})
													.collect(Collectors.toList());

			
//
//
			for(CreateScheduleData tableData:createScheduleDataList)
			{	
				try
				{
					List<ScheduleData> scheduleList =tableData.getScheduleList();
					
					if (scheduleList.isEmpty()) continue;
					
					String table_id= tableData.getTableId();
					
					int result =scheduleService.deleteScheduleById(table_id);
					
					System.out.println("size:"+scheduleList.size());
					
					log.info("delete:({}}", result);
					
					insertSchedule(scheduleList);	
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					System.err.println("index error:"+e.getMessage());
				}
				
			}
		

			long endTime = System.currentTimeMillis();

			log.info("end({}ms)", (endTime-startTime));

			return ScheduleBuild.SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return ScheduleBuild.FAILURE;
	}

	public void insertSchedule( List<ScheduleData> insertList) throws SQLException
	{
		
		insertList.forEach(o -> log.debug(String.format("[%s, %s, %s, %s, %s, %s, %s, %s, %s, %s]", o.getTable_id(), o.getArea_code(), o.getArea_name(), o.getGubun(), o.getFromPort(), o.getFromDate(), o.getPort(), o.getToDate(), o.getAgent(), o.getInOutType()) ));
		
		List<List<ScheduleData>> partitions = ListUtils.partition(insertList, 60);
		
		try {
			for(List list:partitions)
			{
				scheduleService.insertScheduleBulkData(list);	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
			
		}
	}

}
