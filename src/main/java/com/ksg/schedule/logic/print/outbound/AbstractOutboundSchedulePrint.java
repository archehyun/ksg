package com.ksg.schedule.logic.print.outbound;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.dtp.api.schedule.joint.outbound.OutboundScheduleRule;
import com.ksg.commands.schedule.XML_INFO;
import com.ksg.common.dao.DAOManager;
import com.ksg.domain.Code;
import com.ksg.domain.PortInfo;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.print.AbstractSchedulePrint;
import com.ksg.service.BaseService;

public abstract class AbstractOutboundSchedulePrint extends AbstractSchedulePrint{

	protected OutboundScheduleRule outboundSchedule;

	protected static final String PORT_NAME 	= "outbound_port.txt";

	protected static final String FILE_NAME 	= "outbound_new.txt";

	protected static final String ERROR_NAME 	= "outbound_error.txt";	

	protected String errorFileName;

	protected BaseService baseService;

	protected String portName;

	protected String[] fromPort;

	protected  Map<String, PortInfo> portMap;

	protected  Map<String, Vessel> vesselMap;

	protected boolean isApplyTag=true;// 태그 적용 여부

	protected String 	BOLD_TAG_F="",
			BOLD_TAG_B="",
			TAG_VERSION0="",
			TAG_VERSION1="",
			TAG_VERSION2="",
			TAG_VERSION3="",
			TAG_VERSION4="",
			TAG_VERSION5="";

	public AbstractOutboundSchedulePrint() throws Exception {
		super();
		baseService 	= DAOManager.getInstance().createBaseService();
	}
	public void initFile() throws IOException {
		fileName = fileLocation+"/"+FILE_NAME;

		errorFileName = fileLocation+"/"+ERROR_NAME;

		portName = fileLocation+"/"+PORT_NAME;

		fw = new FileWriter(fileName);

		errorfw = new FileWriter(errorFileName);

		portfw = new FileWriter(portName);
	}
	public void init() throws Exception {
		logger.info("태그정보 초기화");

		Code param = new Code();

		param.setCode_type(XML_INFO.XML_TAG_FROM_PORT);

		List<Code> li = baseService.getCodeInfoList(param);
		
		fromPort = new String[li.size()];

		for(int i=0;i<li.size();i++)
		{
			Code info = li.get(i);
			fromPort[i] =info.getCode_name();
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

		initFile();
	}
	public void close()
	{

	}

}
