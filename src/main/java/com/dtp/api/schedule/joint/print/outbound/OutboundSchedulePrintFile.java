package com.dtp.api.schedule.joint.print.outbound;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;
import com.ksg.commands.ScheduleExecute;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.ScheduleJointError;

import lombok.extern.slf4j.Slf4j;

/**
 * 

  * @FileName : OutboundSchedulePrintV3.java

  * @Project : KSG2

  * @Date : 2023. 4. 9. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 : 아웃 바운드 스케줄 출력 클래스
 */
@Slf4j
public class OutboundSchedulePrintFile extends AbstractSchedulePrint{

	public OutboundSchedulePrintFile() throws Exception {
		super();
	}
	
	protected OutboundScheduleRule outboundSchedule;

	protected static final String PORT_NAME 	= "outbound_port.txt";

//	protected static final String FILE_NAME 	= "outbound_new.txt";

	protected static final String ERROR_NAME 	= "outbound_error.txt";	

	protected String errorFileName;

	protected String portName;

	protected String[] fromPortArray;

	protected  Map<String, PortInfo> portMap;

//	protected  Map<String, Vessel> vesselMap;

	protected boolean isApplyTag=true;// 태그 적용 여부

	protected String 	BOLD_TAG_F="",
			BOLD_TAG_B="",
			TAG_VERSION0="",
			TAG_VERSION1="",
			TAG_VERSION2="",
			TAG_VERSION3="",
			TAG_VERSION4="",
			TAG_VERSION5="";

	private List<Code> fromPortList;


	public void init() throws Exception {
		
		log.info("태그정보 초기화");

		fromPortArray = new String[fromPortList.size()];

		for(int i=0;i<fromPortList.size();i++)
		{
			Code info = fromPortList.get(i);
			fromPortArray[i] =info.getCode_name();
		}

		if(isApplyTag)
		{
			BOLD_TAG_B="<ct:>";

			BOLD_TAG_F="<ct:Bold Condensed>";

			TAG_VERSION0="<KSC5601-WIN>";

			TAG_VERSION1="";

			TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<검정:COLOR:CMYK:Process:0,0,0,1>>";

			TAG_VERSION3="<dps:정규=<Nextstyle:정규><cc:검정><cs:8.000000><clig:0><cbs:-0.000000><phll:0><pli:53.858291><pfli:-53.858292><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";		

			TAG_VERSION4="<dps:Body Text=<BasedOn:정규><Nextstyle:Body Text><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

			TAG_VERSION5="<pstyle:Body Text><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cs:8.000000><cl:5.479995><cf:Helvetica LT Std><ct:Roman>\r\n";			
		}
		
		this.fileName  				= ksgPropertiey.getProperty("schedule.outbound.filename");

		fw 				= new FileWriter(fileLocation+"/"+String.format("%s.txt", fileName ));

		errorfw			= new FileWriter(fileLocation+"/"+ERROR_NAME);

		portfw 			= new FileWriter(fileLocation+"/"+PORT_NAME);
	}

	public OutboundSchedulePrintFile(CommandMap param) throws Exception {
		super();

		this.scheduleList 		= (List<ScheduleData>) param.get("outboundScheduleList");

		this.portMap 			= (Map<String, PortInfo>) param.get("portMap");

		this.vesselMap 			= (Map<String, Vessel>) param.get("vesselMap");
		
		this.fromPortList 		= (List<Code>) param.get("fromPortList");

		this.outboundSchedule 	= new OutboundScheduleRule(vesselMap);
	}

	@Override
	public int execute() throws Exception {

		try
		{	
			logger.info("아웃바운드 스케줄 프린트 시작");
			
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
								
								for(String fromPortKey:fromPortArray)
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
