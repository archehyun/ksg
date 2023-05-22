package com.dtp.api.schedule.joint.print.inbound;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.dtp.api.schedule.comparator.DateComparator;
import com.dtp.api.schedule.joint.print.SchedulePrintParam;
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
public class InboundSchedulePrintFile extends AbstractInboundSchedulePrint{
	
	protected DateComparator dateComparator 			= new DateComparator(new SimpleDateFormat("yyyy/MM/dd"));	
	
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
	public void initFile() throws IOException {
		
		fileName = fileLocation+"/"+INBOUND_PRINT_TXT;
		
		fw = new FileWriter(fileName);	
	}

	@Override
	public int execute() throws Exception {

		try {
			logger.info("인바운드 스케줄 프린트 시작");
			
			initFile();
			
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

	private ArrayList<String> createPrintList() {
		
		ArrayList<String> printList = new ArrayList<String>();
		
		lengthOfTask = resultMap.keySet().size();
		
		Object[] mapkey = resultMap.keySet().toArray();
		
		Arrays.sort(mapkey);

		for(Object fromPortName:mapkey)
		{	
			PortInfo portInfo = portMap.get(fromPortName);
			
			printList.add(FROM_PORT_TAG1+fromPortName+" , "+ portInfo.getPort_nationality());

			Map<String, List<ScheduleData>> vesselList= resultMap.get(fromPortName);

			List<InboundScheduleGroup> groupList = inboundScheduleRule.getInboundScheduleGroup(vesselList, true);
			
			List<String> strList = groupList.stream().sorted().map(o -> o.toPrintString()).collect(Collectors.toList());
			
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
