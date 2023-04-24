package com.ksg.schedule.logic.print.outbound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dtp.api.schedule.joint.outbound.OutboundScheduleRule;
import com.dtp.api.schedule.joint.outbound.OutboundScheduleGroup;
import com.ksg.commands.ScheduleExecute;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.SchedulePrintParam;

/**
 * 

  * @FileName : OutboundSchedulePrintV3.java

  * @Project : KSG2

  * @Date : 2023. 4. 9. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 아웃 바운드 스케줄 출력 클래스
 */
public class OutboundSchedulePrintFile extends AbstractOutboundSchedulePrint{

	private List<ScheduleData> scheduleList;

	private ArrayList<String> printList = new ArrayList<String>();
	
	public OutboundSchedulePrintFile() throws Exception {
		super();

	}

	public OutboundSchedulePrintFile(List<ScheduleData> scheduleList) throws Exception {
		super();

		this.scheduleList = scheduleList;
	}

	public OutboundSchedulePrintFile(CommandMap param) throws Exception {
		super();

		this.scheduleList 		= (List<ScheduleData>) param.get("outboundScheduleList");

		this.portMap 			= (Map<String, PortInfo>) param.get("portMap");

		this.vesselMap 			= (Map<String, Vessel>) param.get("vesselMap");

		this.outboundSchedule 	= new OutboundScheduleRule(vesselMap);
	}
	
	public OutboundSchedulePrintFile(SchedulePrintParam param) throws Exception {
		super();

		this.scheduleList 		= param.getScheduleList();

		this.portMap 			= param.getPortMap();

		this.vesselMap 			= param.getVesselMap();

		this.outboundSchedule 	= new OutboundScheduleRule(vesselMap);
		
		
	}

	@Override
	public int execute() throws Exception {

		try
		{
			
			logger.info("스케줄 프린트 시작");
			
			Map<String, Map<String, List<ScheduleData>>> scheduleGroupMap = outboundSchedule.groupedOutboundSchedule(this.scheduleList);
			
			lengthOfTask = scheduleGroupMap.keySet().size();

			scheduleGroupMap.keySet().stream()
								.sorted()// 도착항 정렬
								.forEach(toPortKey->{
									//도착항 출력
									printList.add((printList.size()>0?"\r\n":"")+ buildToPortXTG((String)toPortKey,portMap.get(toPortKey).getPort_nationality()));
					
									Map<String, List<ScheduleData>> fromPortItems=scheduleGroupMap.get(toPortKey);
									
									for(String fromPortKey:fromPort)
									{
										if(fromPortItems.containsKey(fromPortKey)) createFormPortPrintItem(fromPortKey, fromPortItems.get(fromPortKey));	
									}
									current++;
								});
			
			writeFile(printList);
			
			this.message = "아웃바운드 생성완료";

			logger.info("스케줄 프린트 종료");
			
			return ScheduleExecute.SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(e);
		}
		
	}

	private void writeFile(ArrayList<String> printList) throws Exception  {
		
		try {
			initFile();
			
			fw.write(buildVersionXTG());

			for(String print:printList)
			{
				fw.write(print);
			}
		} catch (IOException e) {
			throw new Exception(e);
		}
		finally
		{
			fw.close();
			errorfw.close();
			portfw.close();
		}		
	}

	

	/**
	 * @param fromPortKey
	 * @param fromPortItems
	 */
	private void createFormPortPrintItem(String fromPortKey, List<ScheduleData> scheduleList) {

		//출발항 출력값
		printList.add(buildFromXTG((String)fromPortKey));

		ArrayList<OutboundScheduleGroup> outboundScheduleGroupList = (ArrayList<OutboundScheduleGroup>) outboundSchedule. createFromPortOutboundScheduleGroup(scheduleList);
		
		outboundScheduleGroupList.forEach(o -> o.joinnted());

		// 출력 스트링 생성 후 리스트에 저장
		outboundScheduleGroupList.stream()
								.filter(o->!o.isDateValidate())
								.sorted()
								.map(schedule -> makeScheduleStr(schedule))
								
								.collect(Collectors.toList())
								.forEach(item ->printList.add(item));
	}

	/**
	 * @param scheduleGroup
	 * @return
	 */
	private String makeScheduleStr(OutboundScheduleGroup scheduleGroup) 
	{
		String dateF = "<ct:><cs:><ct:Roman><cs:6.000000>"+scheduleGroup.getJointedDateF()+"<ct:>";

		String dateT = scheduleGroup.getJointedDateT();

		String vessel =scheduleGroup.getVesselName();

		String company = scheduleGroup.getJointedCompanyName();

		String vessel_type = vesselMap.get(scheduleGroup.getVesselName()).getVessel_type();

		String formatedVesselType =  (vessel_type.equals("")||vessel_type.equals(" "))?"   ":"   ["+vessel_type+"]   ";   

		return String.format("%s\t%s<ct:Roman>%s(%s)\t%s\r\n", dateF, vessel, formatedVesselType, company, dateT); 
	}

	/**
	 * @param portName
	 * @param portNationality
	 * @return
	 */
	private String buildToPortXTG( String portName,String portNationality) {

		return (isApplyTag?"<ct:><cs:><ct:Bold><cs:8.000000>":"")
				+portName +" , "+portNationality
				+(isApplyTag?" \r\n":" ");	

	}
	private String buildFromXTG(String fromPort) {

		return (isApplyTag?" \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ":"")
				+fromPort+" -\r\n";

	}
	private String buildVersionXTG() {
		String buffer = TAG_VERSION0+"\r\n"+
				TAG_VERSION2+"\r\n"+
				TAG_VERSION3+"\r\n"+
				TAG_VERSION4+"\r\n"+
				TAG_VERSION5;
		return buffer;
	}



}
