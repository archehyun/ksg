package com.ksg.schedule.logic.joint;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.dtp.api.schedule.comparator.DateComparator;
import com.dtp.api.schedule.comparator.VesselComparator;
import com.dtp.api.schedule.joint.RouteJoint;
import com.dtp.api.schedule.joint.RouteJointSubject;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.joint.route.RouteScheduleGroup;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;

/**
 * 

  * @FileName : RouteScheduleJointV4.java

  * @Project : KSG2

  * @Date : 2023. 1. 29. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� : ���Ʈ ������ ���
 */
public class RouteScheduleJointV4 extends RouteAbstractScheduleJoint implements RouteJointSubject{
	
	private RouteJoint joint;
	
	private DateComparator dateComparator 			= new DateComparator(new SimpleDateFormat("yyyy/MM/dd"));

	private VesselComparator vesselComparator 		= new VesselComparator();
	
	private VesselServiceV2 vesselService 			= new VesselServiceImpl();
	
	private String date_isusse;
	
	private int orderBy;
	
	private List<Vessel>allVesselList;
	
	private List<ScheduleData> scheduleList;

	public RouteScheduleJointV4(ShippersTable op, int orderBy) throws Exception {
		super(op);
	}
	
	public RouteScheduleJointV4(String date_isusse, int orderBy) throws Exception {
		super();
		
		this.date_isusse = date_isusse;
		
		this.orderBy = orderBy;
		
		joint = new RouteJoint(this);
		
		CommandMap param = new CommandMap();
		
		param.put("date_issue", date_isusse);

		param.put("inOutType", OUTBOUND);
		
		// ������ ��� ��ȸ
		scheduleList= scheduleService.selecteScheduleListByCondition(param);
		
	}

	@Override
	public int execute() throws Exception {
		
		
		if(scheduleList== null || scheduleList.isEmpty()) return 0;
		
		long startTime = System.currentTimeMillis();
		
		message = "�׷κ� ������ ���..";		
		
		// vessel map ����
		allVesselList= vesselService.selectAllList();
		
		Map<String, Vessel> vesselNameMap = allVesselList.stream().distinct().collect(Collectors.toMap(Vessel::getVessel_name, Function.identity()));		
		
		logger.info("row schedule size:{}",scheduleList.size());		
		
		List<ScheduleData> schedulelist = scheduleList.stream().filter(schedule ->vesselNameMap.containsKey(schedule.getVessel())).collect(Collectors.toList());
		
		logger.info("schedule size:{}",scheduleList.size());

		schedulelist.forEach(item -> item.setArea_name(item.getArea_name().toUpperCase()));

		// ���������� �׷�ȭ
		Map<String, Map<String, List<ScheduleData>>> areaList =  schedulelist.stream().collect(
				Collectors.groupingBy(ScheduleData::getArea_name,
						// ����
						Collectors.groupingBy(ScheduleData::getVessel))
						
						); // ����
		
		
		executeJointSchedule(areaList);
		
		long endTime = System.currentTimeMillis();

		logger.info("�׷κ� ������ ���� ����({}s)",(endTime-startTime));
		//logger.info("�׷κ� ������ �� :({})",totalCount);

		return ScheduleJoint.SUCCESS;
	}
	
	

	private void printAreaList(Object[] areakeyList) {

		StringJoiner joiner = new StringJoiner("\n");
		
		// ���� ���� �α� ���
		for(Object area:areakeyList)
		{
			joiner.add((String)area);
		}

		logger.info("\n- �������- \n{}",joiner.toString());
	}



	public void writeArea(int index, String areaName) throws IOException
	{	
		fw.write((index>0?"\r\n\r\n\r\n\r\n":"")+areaName+"\r\n\r\n");
	}

	@Override
	public void initTag() {
		WORLD_F="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon���� �����100\\_TT>��<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		WORLD_B="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon���� �����100\\_TT>��<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		WORLD_VERSION1="<KSC5601-WIN>\r\n<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1><60.100.0.0.:COLOR:CMYK:Process:0.6,1,0,0><30.60.0.0.:COLOR:CMYK:Process:0.3,0.6,0,0>>";
		WORLD_INPORT="<cc:><ct:><cs:><cf:><cc:30.60.0.0.><ct:Roman><cs:6.000000><cbs:-1.000000><cf:Helvetica LT Std>";
		WORLD_OUTPORT="<cc:><ct:><cs:><cbs:><cf:><ct:Roman><cs:6.000000><cf:Helvetica LT Std>";
		WORLD_VERSION2="<pstyle:><ct:Bold><chs:0.900000><cl:8.000000><cf:Helvetica LT Std>";
		WORLD_VERSION3="<pstyle:����><pli:182.500000><pfli:-182.000000><psa:0.566894><ptr:96.37789916992188\\,Left\\,.\\,0\\,\\;201\\,Left\\,.\\,0\\,\\;><chs:0.800003><cl:20.000000><cs:18.000000><cf:Helvetica LT Std>\r\n<cl:><cl:20.099990>\r\n<cs:><ct:Bold><cs:18.000000>";
		WORLD_E="<ct:><cs:><cf:><ct:Bold><cf:Helvetica LT Std>";

	}
	private void executeJointSchedule(Map<String, Map<String, List<ScheduleData>>> areaList) throws IOException
	{	
		
		Object[] areakeyList = areaList.keySet().toArray();

		lengthOfTask = areakeyList.length;

		// ������ ����
		Arrays.sort(areakeyList);
		
		// ������ ���
		printAreaList(areakeyList);		

		int firstIndex=0;
		
		int totalCount = 0;

		fw.write(WORLD_VERSION1+"\r\n"+WORLD_VERSION2);

		
		// ���� ��ȸ
		for(Object strArea:areakeyList)
		{	
			firstIndex++;
			
			writeArea(firstIndex, (String) strArea);

			Map<String, List<ScheduleData>> vesselList = areaList.get(strArea);
			
			logger.info("Area: "+strArea+ ", ������ �׷� ������:"+vesselList.keySet().size());

			// ���ڸ����� �׷�ȭ
		
			Object[] vesselArray = vesselList.keySet().toArray();
			
			List<RouteScheduleGroup> scheduleGroupList = new ArrayList<RouteScheduleGroup>();			
			
			for (Object vesselKey : vesselArray)
			{	
				List<ScheduleData> scheduleList = (List<ScheduleData>) vesselList.get(vesselKey);
				
				// ������ȣ(����)�� �׷�ȭ
				Map<Integer, List<ScheduleData>> voyageList =  scheduleList.stream().collect(
						Collectors.groupingBy(o -> ScheduleBuildUtil. getNumericVoyage(o.getVoyage_num()) ));// ����

				Object[] voyageArray = voyageList.keySet().toArray();

				//������ ����
				Arrays.sort(voyageArray);
				
				for(Object voyagekey:voyageArray)
				{
					List<ScheduleData> subscheduleList = voyageList.get(voyagekey);
					
					joint.createScheduleAndAddGroup(scheduleGroupList, subscheduleList, (String)strArea, (String)vesselKey);
					
				}
			}
			
			// �����, ���ڸ� �������� ����
			Collections.sort(scheduleGroupList, orderBy==RouteAbstractScheduleJoint.ORDER_BY_DATE ?dateComparator:vesselComparator);
			
			// ���
			for(RouteScheduleGroup group:scheduleGroupList ){
				
				String strCompanys 	= group.toCompanyString();
				
				String strVoyage 	= group.getVoyage();
				
				String vesselName 	= group.getVessel();
				
				String strFromPorts = group.toFromPortString();
				
				String strToPorts 	= group.toToPortString();
				
				fw.write(String.format("%s%s - %s (%s)\r\n%s%s\r\n%s%s\r\n\r\n",WORLD_F,vesselName, strVoyage, strCompanys,WORLD_INPORT, strFromPorts,WORLD_OUTPORT, strToPorts));
				
			}
			
			current++;
			
			firstIndex++;
		}
		
		close();
		
	}

	@Override
	public void createScheduleAndAddGroup(List scheduleGroupList, List scheduleList, String areaName, String vesselName) {
		
		List<RouteScheduleGroup> validScheduleGroupList = joint.getValidatedScheduleGroupList(areaName,vesselName, scheduleList);
		
		scheduleGroupList.addAll(validScheduleGroupList);
	}

	
	

}
