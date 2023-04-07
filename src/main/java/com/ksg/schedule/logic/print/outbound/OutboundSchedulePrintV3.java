package com.ksg.schedule.logic.print.outbound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dtp.api.schedule.joint.outbound.OutboundSchedule;
import com.dtp.api.schedule.joint.outbound.OutboundScheduleGroup;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;

public class OutboundSchedulePrintV3 extends OutboundAbstractSchedulePrint{

	private OutboundSchedule outboundSchedule;

	private List<ScheduleData> scheduleList;
	
	private Map<String, PortInfo> portMap;
	
	private Map<String, Vessel> vesselMap;
	
	private ArrayList<String> printList = new ArrayList<String>();

	public OutboundSchedulePrintV3(List<ScheduleData> scheduleList) throws Exception {
		super();

		this.scheduleList = scheduleList;
	}

	public OutboundSchedulePrintV3(CommandMap param) throws Exception {
		super();
		
		this.scheduleList 	= (List<ScheduleData>) param.get("scheduleList");
		
		this.portMap 		= (Map<String, PortInfo>) param.get("portMap");
		
		this.vesselMap 		= (Map<String, Vessel>) param.get("vesselMap");
		
		outboundSchedule 	= new OutboundSchedule(vesselMap);
	}

	@Override
	public int execute() throws Exception {

		try
		{
			logger.info("스케줄 프린트 시작");
			Map<String, Map<String, List<ScheduleData>>> toPortList =  scheduleList.stream().collect(
					Collectors.groupingBy(ScheduleData::getPort, // 도착항
							Collectors.groupingBy(ScheduleData::getFromPort)));// 출발항

			toPortList.keySet().stream()
								.sorted()// 도착항 정렬
								.forEach(toPortKey->{
									//도착항 출력
									printList.add(buildToPortXTG(1,(String)toPortKey,portMap.get(toPortKey).getPort_nationality()));
									
									Map<String, List<ScheduleData>> fromPortItems=toPortList.get(toPortKey);
		
									fromPortItems.keySet().forEach(fromPortKey ->createFormPortPrintItem(fromPortKey, fromPortItems));
									
								});
			
			
			for(String print:printList)
			{
				fw.write(print);
			}
			
			logger.info("스케줄 프린트 종료");
			return 0;
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	private void createFormPortPrintItem(String fromPortKey, Map<String, List<ScheduleData>> fromPortItems) {
		
		//출발항 출력값
		printList.add(buildFromXTG(0, (String)fromPortKey));
		
		List<ScheduleData>  scheduleList=fromPortItems.get(fromPortKey);
		
		ArrayList<OutboundScheduleGroup> outboundScheduleGroupList = (ArrayList<OutboundScheduleGroup>) outboundSchedule. createFromPortOutboundScheduleGroup(scheduleList);

		// 출력 스트링 생성 후 리스트에 저장
		outboundScheduleGroupList.stream()
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

		String vessel = String.format("%s\t%s%s", BOLD_TAG_F, scheduleGroup.getVesselName(),BOLD_TAG_F);

		String company = scheduleGroup.getJointedCompanyName();

		String vessel_type = vesselMap.get(scheduleGroup.getVesselName()).getVessel_type();
		
		String formatedVesselType =  (vessel_type.equals("")||vessel_type.equals(" "))?"   ":"   ["+vessel_type+"]   ";   

		return String.format("<ct:><cs:><ct:Roman><cs:6.000000>%s<ct:> %s\t<ct:Roman>%s(%s)\t %s\r\n", dateF, vessel, formatedVesselType, company, dateT); 
	}
	
	@Override
	public void initTag() {
		BOLD_TAG_B="<ct:>";

		BOLD_TAG_F="<ct:Bold Condensed>";

		TAG_VERSION0="<KSC5601-WIN>";

		TAG_VERSION1="";

		TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<검정:COLOR:CMYK:Process:0,0,0,1>>";

		TAG_VERSION3="<dps:정규=<Nextstyle:정규><cc:검정><cs:8.000000><clig:0><cbs:-0.000000><phll:0><pli:53.858291><pfli:-53.858292><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";		

		TAG_VERSION4="<dps:Body Text=<BasedOn:정규><Nextstyle:Body Text><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

		TAG_VERSION5="<pstyle:Body Text><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cs:8.000000><cl:5.479995><cf:Helvetica LT Std><ct:Roman>\r\n";
	}

	/**
	 * @param i
	 * @param portName
	 * @param portNationality
	 * @return
	 */
	private String buildToPortXTG(int i, String portName,String portNationality) {

		if(isApplyTag)
		{
			return (i!=0?" \r\n<ct:><cs:><ct:Bold><cs:8.000000>":"<ct:><cs:><ct:Bold><cs:8.000000>")

					+portName +" , "+portNationality+" ";	
		}
		else
		{
			return (i!=0?" \r\n":"")+portName +" , "+portNationality+" ";	
		}

	}
	private String buildFromXTG(int j, String fromPort) {
		if(isApplyTag)
		{
			return (j==0?" \r\n \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ":" \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ")+fromPort+" -\r\n";
		}
		else
		{
			return (j==0?" \r\n \r\n- ":" \r\n- ")+fromPort+" -\r\n";
		}
	}



}
