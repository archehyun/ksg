package com.ksg.schedule.execute.joint;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.Vessel;
import com.ksg.schedule.execute.Executeable;
import com.ksg.service.ScheduleServiceV2;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;

public abstract class JointSchedule implements Executeable{
	
	SimpleDateFormat inputDateFormat 	= KSGDateUtil.inputDateFormat;

	SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();
	
	private VesselServiceV2 vesselService;
	
	protected ScheduleServiceV2 	scheduleService;
	protected String date;
	
	protected HashMap<String, Object> vesselMap;
	public JointSchedule(String date)
	{
		scheduleService 	= new ScheduleServiceImpl();
		this.date = date;
//		shipperTableService = new ShipperTableServiceImpl();
//		advService 			= new ADVServiceImpl();
		vesselService = new VesselServiceImpl();
		try {
			vesselMap =vesselService.selectTotalList();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getFormatedDate(String date)
	{
		return date;
//		try {
//			
//			return outputDateFormat.format(inputDateFormat.parse(date));
//		} catch (ParseException e) {
//			
//			return "error";
//		}
	}
	/**
	 * ���� ���� ��ȸ
	 * 	���� ��� �������� ���� ���� ��ȸ
	 * @param vesselAbbrName
	 * @return
	 * @throws ResourceNotFoundException
	 */
	public Vessel getVesselInfo(String vesselAbbrName) throws ResourceNotFoundException {

		Vessel item = (Vessel) vesselMap.get(vesselAbbrName);
		
		//���� ���� ���� �ÿ� ���� ����
		if(item == null) throw new ResourceNotFoundException(vesselAbbrName); 

		return item;
	}


}
