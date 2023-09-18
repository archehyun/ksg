package com.dtp.api.schedule.joint.print.inbound;

import java.io.FileWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;
import com.ksg.commands.ScheduleExecute;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.ScheduleJointError;
import com.ksg.workbench.schedule.comp.treenode.InboundCodeMap;
/**
 * 

 * @FileName : InboundSchedulePrintFile.java

 * @Project : KSG2

 * @Date : 2023. 5. 16. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 :
 */
public class InboundSchedulePrintFile extends AbstractSchedulePrint{
	
	protected String 	BOLD_TAG_F;
	protected String 	BOLD_TAG_B;
	protected String 	TAG_VERSION0;	
	protected String 	TAG_VERSION2;
	protected String 	TAG_VERSION3;
	protected String 	TAG_VERSION6;
	
	private InboundCodeMap inboundCodeMap 				= InboundCodeMap.getInstance();

	private Map<String, Map<String, List<ScheduleData>>> resultMap;
	
	private String FROM_PORT_TAG1 = "<ct:><cf:><cs:><cs:8.000000><cf:Helvetica LT Std><ct:Bold>";
	
	private String FROM_DATE_TAG1 = "<ct:><cf:><cs:><cs:6.500000><cf:Helvetica LT Std><ct:Roman>";
	
	private String VESSEL_TAG ="\t<ct:><cf:><cf:Helvetica Neue LT Std>";
	
	private InboundScheduleRule inboundScheduleRule;

	public InboundSchedulePrintFile() throws SQLException {
		super();
	}

	public InboundSchedulePrintFile(CommandMap param) throws SQLException {
		this();

		this.scheduleList 		= (List<ScheduleData>) param.get("inboundScheduleList");
		
		this.resultMap 			= (Map<String, Map<String, List<ScheduleData>>>) param.get("inboundScheduleMap");
		
		this.portMap 			= (HashMap<String, PortInfo>) param.get("portMap");
		
		this.vesselMap 			= (Map<String, Vessel>) param.get("vesselMap");
		
		inboundScheduleRule 	= new InboundScheduleRule(vesselMap);

	}

	
	@Override
	public void init() throws Exception {
		
		BOLD_TAG_B="<ct:>";

		BOLD_TAG_F="<ct:77 Bold Condensed>";

		TAG_VERSION0="<KSC5601-WIN>";

		TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<검정:COLOR:CMYK:Process:0,0,0,1>>";

		TAG_VERSION3="<dps:정규=<Nextstyle:정규><cc:검정><cs:7.500000><clig:0><cbs:-0.000000><phll:0><pli:21.543304><pfli:-21.543305><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:21.543304443359375\\,Left\\,.\\,0\\,\\;138.89764404296875\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

		TAG_VERSION6="<pstyle:정규><pli:21.543289><pfli:-21.543290><ptr:158\\,Char\\,\\[\\,0\\,\\;><cl:6.750000><chs:0.800003><cs:8.000000><cf:Helvetica LT Std><ct:Roman>";
		
		this.fileName  	= ksgPropertiey.getProperty("schedule.inbound.filename");
		
		fw 				= new FileWriter(fileLocation+"/"+String.format("%s.txt", fileName ));
	}

	@Override
	public int execute() throws Exception {

		try {
			logger.info("인바운드 스케줄 프린트 시작");
			
			ArrayList<String> printList =createPrintList();
			
			fw.write(getHeader());

			fw.write(getBody(printList));
			
			this.message = "인바운드 생성완료";
			
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
	/**
	 * 
	 * @return
	 */
	private ArrayList<String> createPrintList() {
		
		ArrayList<String> printList = new ArrayList<String>();
		
		lengthOfTask 				= resultMap.keySet().size();
		
		Object[] mapkey 			= resultMap.keySet().toArray();
		
		Arrays.sort(mapkey);

		for(Object fromPortName:mapkey)
		{	
			PortInfo portInfo = portMap.get(fromPortName);
			
			printList.add(FROM_PORT_TAG1+fromPortName+" , "+ portInfo.getPort_nationality());

			Map<String, List<ScheduleData>> vesselList= resultMap.get(fromPortName);
			
			// inbound 스케줄 그룹 생성
			List<InboundScheduleGroup> groupList = inboundScheduleRule.createScheduleGroup(vesselList, true);
			
			List<String> strList = groupList.stream()
											// 날짜 필터링
											.filter(o -> o.isValidateDate())
											.sorted().map(o -> o.toPrintString()).collect(Collectors.toList());
			
			printList.add(StringUtils.join(strList,"\r\n"));
			
			current++;
		}
		return printList;
	}

	private String getHeader() {
		return String.format("%s%s%s%s%s%s%s%s", TAG_VERSION0,"\r\n",TAG_VERSION2,"\r\n",TAG_VERSION3,"\r\n",TAG_VERSION6,"\r\n");
	}

	private String getBody(ArrayList<String> printList) {
		return StringUtils.join(printList,"\r\n\r\n");
	}


	@Override
	public void writeFile(ArrayList<String> printList) throws Exception {}

}
