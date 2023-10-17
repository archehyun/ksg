package com.dtp.api.schedule.joint.print.webschedule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.web.IFWebScheduleFormat;
import com.ksg.schedule.logic.web.NomalWebScheduleFormat;
import com.ksg.service.ScheduleSubService;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.ScheduleServiceImpl;
import com.ksg.service.impl.VesselServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSchedulePrint {

	private ScheduleSubService service;

	private ScheduleData searchOption;

	private IFWebScheduleFormat webScheduleFormat;

	private int globalScheduleID;

	private Map<String, Vessel> vesselMap;

	private VesselServiceV2 vesselService;

	private String fileName;

	private SimpleDateFormat dateTypeYear = new SimpleDateFormat("yyyy");

	private SimpleDateFormat dateTypeMonth = new SimpleDateFormat("MM");

	private int currentYear;

	private int currentMonth;

	public WebSchedulePrint(String eventDate)
	{

		service = new ScheduleServiceImpl();

		webScheduleFormat = new NomalWebScheduleFormat();

		vesselService = new VesselServiceImpl();				

	}
	public void execute()
	{
		try {

			globalScheduleID =0;

			// ���� ��¥ ���ϱ� (�ý��� �ð�, �ý��� Ÿ����)
			LocalDate now = LocalDate.now();

			
			currentYear = now.getYear();

			currentMonth = now.getMonthValue();

			List<ScheduleData> schedulelist = service.selecteScheduleListByCondition(new ScheduleData());

			vesselMap = extractedVesselMap(schedulelist );

			// �����ڵ�� �׷�ȭ
			Map<String, List<ScheduleData>> groupList =  schedulelist.stream()
					.collect(
							Collectors.groupingBy(ScheduleData::getArea_code)
							);


			Object obj[]=groupList.keySet().toArray();

			for(Object areaCode:obj)
			{
				String fileName = String.format("WW_SYBASE%s", String.valueOf(areaCode));

				List<ScheduleData> scheduleList = groupList.get(areaCode);

				checkFile(fileName);

				writeAreaSchedule(areaCode, fileName, scheduleList);
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void writeAreaSchedule(Object areaCode, String fileName, List<ScheduleData> scheduleList) throws Exception {

		FileWriter fw = new FileWriter(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName);

		log.info(areaCode+"����("+scheduleList.size()+"��)���� ��� ����...");

		try {
			for(ScheduleData data:scheduleList)
			{	

				// ���� ����

				Vessel vessel = vesselMap.get(data.getVessel());

				data.setOperator(vessel.getVessel_company() );

				data.setVessel_type(vessel.getVessel_type());

				data.setVessel_mmsi(vessel.getVessel_mmsi());

				//TODO : ��¥ ����
				
				String[] fromDates = KSGDateUtil.getDates(data.getDateF(),this.currentMonth,this.currentYear);

				String[] toDates   = KSGDateUtil.getDates(data.getDateT(),this.currentMonth,this.currentYear);
				
				data.setDateF(fromDates[0]);
				data.setDateFBack(fromDates[1]);
				
				data.setDateT(toDates[0]);
				data.setDateTBack(toDates[1]);

				fw.write(String.format("%s\t%d\t%s\n", data.getArea_code(), globalScheduleID++, webScheduleFormat.toWebScheduleString(data)));
			}
			log.info(areaCode+"���� ���� ��� ����");

		}catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		finally {
			fw.flush();

			fw.close();

		}
	}

	/**
	 * @param fileName
	 */
	private void checkFile(String fileName)
	{
		File fo = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION));
		if(!fo.exists())
		{
			fo.mkdir();
		}

		File file = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName);
		if(file.exists())
		{
			if(file.delete())
			{
				log.info("���� ���� ����");
			}
		}
	}

	private Map<String, Vessel> extractedVesselMap(List<ScheduleData> scheduleList) throws SQLException {
		List<String> vesselNames=scheduleList.stream().map(ScheduleData::getVessel)
				.distinct()
				.collect(Collectors.toList());


		if(vesselNames.isEmpty()) return new HashMap();

		CommandMap vesselParam = new CommandMap();

		vesselParam.put("vesselNameList", vesselNames);

		List<Vessel> vesselList = vesselService.selectVesselListByNameList(vesselNames);

		Map<String, Vessel> vesselMap = vesselList.stream().collect(Collectors.toMap(Vessel::getVessel_name, Function.identity()));

		return vesselMap;
	}

}
