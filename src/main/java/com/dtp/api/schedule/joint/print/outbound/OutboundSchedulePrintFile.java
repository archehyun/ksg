package com.dtp.api.schedule.joint.print.outbound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.ksg.commands.ScheduleExecute;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.ScheduleJointError;

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

	public OutboundSchedulePrintFile() throws Exception {
		super();

	}

	public OutboundSchedulePrintFile(CommandMap param) throws Exception {
		super();

		this.scheduleList 		= (List<ScheduleData>) param.get("outboundScheduleList");

		this.portMap 			= (Map<String, PortInfo>) param.get("portMap");

		this.vesselMap 			= (Map<String, Vessel>) param.get("vesselMap");

		this.outboundSchedule 	= new OutboundScheduleRule(vesselMap);
	}

	@Override
	public int execute() throws Exception {

		try
		{	
			logger.info("아웃바운드 스케줄 프린트 시작");
			
			initFile();
			
			ArrayList<String> printList =createPrintList();
			
			fw.write(getHeader());
			
			fw.write(getBoday(printList));
			
			this.message = "아웃바운드 생성종료";

			logger.info("스케줄 프린트 종료");
			
			return ScheduleExecute.SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			throw new ScheduleJointError(e,data);
		}
		
		finally
		{
			close();
		}	
	}


	private ArrayList<String> createPrintList() {
		
		ArrayList<String> printList = new ArrayList<String>();
		
		Map<String, Map<String, List<ScheduleData>>> scheduleGroupMap = outboundSchedule.groupedOutboundSchedule(this.scheduleList);
		
		lengthOfTask = scheduleGroupMap.keySet().size();

		scheduleGroupMap.keySet().stream()
							.sorted()// 도착항 정렬
							.forEach(toPortKey->{
								
								StringBuffer buffer = new StringBuffer();
								
								buffer.append(buildToPortXTG((String)toPortKey,portMap.get(toPortKey).getPort_nationality()));
				
								Map<String, List<ScheduleData>> fromPortItems=scheduleGroupMap.get(toPortKey);
								
								for(String fromPortKey:fromPort)
								{
									if(fromPortItems.containsKey(fromPortKey)) {
										buffer.append( createFormPortPrintItem(fromPortKey, fromPortItems.get(fromPortKey)));
									}
								}
								
								printList.add(buffer.toString());
								
								current++;
							});
		return printList;
	}	
	

	/**
	 * @param fromPortKey
	 * @param fromPortItems
	 */
	private String createFormPortPrintItem(String fromPortKey, List<ScheduleData> scheduleList) {
		
		StringBuffer buffer = new StringBuffer();
		//출발항 출력값
		buffer.append(buildFromXTG((String)fromPortKey));

		ArrayList<OutboundScheduleGroup> outboundScheduleGroupList = (ArrayList<OutboundScheduleGroup>) outboundSchedule. createScheduleGroup(scheduleList);
		
		outboundScheduleGroupList.forEach(o -> o.joinnted());

		// 출력 스트링 생성 후 리스트에 저장
		outboundScheduleGroupList.stream()
								.filter(o->!o.isDateValidate())
								.sorted()
								.map(schedule -> makeScheduleStr(schedule))
								.collect(Collectors.toList())
								.forEach(item ->buffer.append(item));
		return buffer.toString();
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

		return (isApplyTag?"<ct:><cs:><ct:Bold><cs:8.000000>":"") +portName +" , "+portNationality +(isApplyTag?" \r\n":" ");	

	}
	private String buildFromXTG(String fromPort) {

		return (isApplyTag?" \r\n<ct:><cs:><ct:Bold><cs:7.000000>- ":"")
				+fromPort+" -\r\n";

	}
	private String getHeader() {
		String buffer = TAG_VERSION0+"\r\n"+
				TAG_VERSION2+"\r\n"+
				TAG_VERSION3+"\r\n"+
				TAG_VERSION4+"\r\n"+
				TAG_VERSION5;
		return buffer;
	}
	
	private String getBoday(ArrayList<String> printList) {
		return StringUtils.join(printList,"\r\n");
	}


	@Override
	public void writeFile(ArrayList<String> printList) throws Exception {
		// TODO Auto-generated method stub
		
	}



}
