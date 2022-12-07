package com.ksg.schedule.execute.joint.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.execute.joint.JointSchedule;
import com.ksg.service.VesselServiceV2;
import com.ksg.service.impl.VesselServiceImpl;

import lombok.extern.slf4j.Slf4j;

/**

  * @FileName : OutboundScheduleJoint.java

  * @Project : KSG2

  * @Date : 2022. 6. 8. 

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� :

  */
@Slf4j
public class OutboundScheduleJoint extends JointSchedule{

	

	

	public OutboundScheduleJoint(String date_isusse)	
	{
		super(date_isusse);

	}

	/**
	 * �������� ������, ����� ,���ڸ�, �������� �׷�ȭ
	 * 
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public Map<String, Map<String, Map<String, List<ScheduleData>>>> buildOutboundScheduleGroup(List<ScheduleData> result) throws Exception
	{

		Map<String, Map<String, Map<String, List<ScheduleData>>>> areaList =  result.stream().collect(
				Collectors.groupingBy(ScheduleData::getArea_name, // ����
						Collectors.groupingBy(ScheduleData::getFromPort, // �����
								Collectors.groupingBy(ScheduleData::getPort))));// ������


		return areaList;
	}



	/**�׷�ȭ�� �������� ����Ʈ�� ��ȯ, �����輱 ����
	 * 
	 * ���� ���� : 
	 * �����
	 * 		������
	 * 			����
	 * 
	 * @param areaMap
	 * @return
	 */
	public ScheduleList buildSortedAndJointedScheduleList2(Map map)  throws Exception	
	{
		ScheduleList areaLists = new ScheduleList(date);

		Iterator<String> areaIter= (Iterator<String>) map.keySet().iterator();

		while(areaIter.hasNext())
		{
			String area_name = areaIter.next();

			HashMap<String, Object> toitem= (HashMap<String, Object>) map.get(area_name);

			Iterator<String> toPortite= (Iterator<String>) toitem.keySet().iterator();

			ScheduleList toPortList = new ScheduleList(area_name);

			//TODO ���� : ����� ���� �߰�
			while(toPortite.hasNext())
			{
				String toPort = toPortite.next();

				HashMap<String, Object> Schjeduleitem= (HashMap<String, Object>) toitem.get(toPort);

				Iterator<String> fromPortIter= (Iterator<String>) Schjeduleitem.keySet().iterator();

				ScheduleList fromPortList = new ScheduleList(toPort);
				//TODO ���� : ������ ���� �߰�
				while(fromPortIter.hasNext())
				{
					String fromPort = fromPortIter.next();
					
					// ���� �輱�� ����� ������ ����Ʈ ����
					List<JointedSchehduleData> scheduleItems = getJointScheduleList((List) Schjeduleitem.get(fromPort));

					ScheduleList scheduleList = new ScheduleList(fromPort);


					for(JointedSchehduleData temp:scheduleItems)
					{	
						scheduleList.add(temp);
					}
					
					// ����� ���� �߰�
					Collections.sort(scheduleList, new AscendingFromDate() );

					fromPortList.add(scheduleList);
				}	
				toPortList.add(fromPortList);
			}
			areaLists.add(toPortList);
		}
		return areaLists;
	}


	



	@Override
	public int execute() {

		log.info("outbound joint start");
		CommandMap param = new CommandMap();

		param.put("inOutType", "O");
		param.put("date_issue", this.date);


		try {

			List<ScheduleData> result= scheduleService.selecteScheduleListByCondition(param);

			log.info("schedule size:{}",result.size());

			// ������ �׷�ȭ
			Map<String, Map<String, Map<String, List<ScheduleData>>>> areaMap = buildOutboundScheduleGroup(result);

			// ������ ���� �� ����Ʈȭ
			ScheduleList areaLists =  buildSortedAndJointedScheduleList2(areaMap);


			// ������ ���
			//new TagedPrintSchedule(areaLists).print();

		}catch(Exception e)
		{
			e.printStackTrace();
		}


		log.info("outbound joint end");
		return 0;
	}

	/**�����輱 ���� ����Ʈ
	 * ���ڸ�, �������� ���� ���
	 * ���輱���� ������ ��� ������� ���� ��¥, �������� ���� ��¥�� ����
	 * @param list
	 * @return
	 * @throws SQLException 
	 */
	private List getJointScheduleList(List<ScheduleData> list) throws SQLException
	{
		// ���ڸ����� group by
		Map<String, List<ScheduleData>> result= list.stream().collect(Collectors.groupingBy(ScheduleData::getVessel));

		Iterator<String> keys= result.keySet().iterator();

		ArrayList<JointedSchehduleData> list2 = new ArrayList<JointedSchehduleData>();

		while(keys.hasNext())
		{
			String vesselName = keys.next();
			try {
				//TODO NULL VESSEL ó�� ���
				Vessel vesselItem=getVesselInfo(vesselName);

				List<ScheduleData> items=result.get(vesselName);

				JointedSchehduleData group = new JointedSchehduleData();

				//group.vesselType = vesselItem.getVessel_type();

				for(ScheduleData sc:items)
				{
					group.addScheduleData(sc);
				}

				list2.add(group);

//				if(group.size()>1)
//
//					System.out.println("\t\t\tvesselName:"+vesselName+","+group.size());
			}catch(ResourceNotFoundException e)
			{
				System.err.println("null vessel:"+vesselName+", e:"+e.getMessage());
			}
		}

		return list2;
	}



	/** ��¥ ���� ����
	
	  * @FileName : OutboundScheduleJoint.java
	
	  * @Project : KSG2
	
	  * @Date : 2022. 6. 8. 
	
	  * @�ۼ��� : pch
	
	  * @�����̷� :
	
	  * @���α׷� ���� :
	
	  */
	class AscendingFromDate implements Comparator<JointedSchehduleData> 
	{ 
		@Override 
		public int compare(JointedSchehduleData one, JointedSchehduleData two) {

			String fromDateOne = one.getFromDate();
			String fromDateTwo = two.getFromDate();

			return KSGDateUtil.dayDiff(fromDateOne, fromDateTwo)>0?-1:1;

		} 
	}
	
	
	//TODO �±� ���� ���



	


	
}