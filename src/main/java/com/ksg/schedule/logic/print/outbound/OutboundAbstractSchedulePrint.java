package com.ksg.schedule.logic.print.outbound;

import java.io.FileWriter;

import com.ksg.schedule.logic.print.DefaultSchedulePrint;

public abstract class OutboundAbstractSchedulePrint extends DefaultSchedulePrint{
	
	
	protected static final String PORT_NAME = "outbound_port.txt";

	protected static final String FILE_NAME = "outbound_new.txt";

	protected static final String ERROR_NAME = "outbound_error.txt";	
	
	protected FileWriter fw;

	protected FileWriter errorfw;

	protected FileWriter portfw;
	protected String errorFileName;
	protected String portName;
	
	protected boolean isApplyTag=true;// 태그 적용 여부
	
	protected String 	BOLD_TAG_F="",
						BOLD_TAG_B="",
						TAG_VERSION0="",
						TAG_VERSION1="",
						TAG_VERSION2="",
						TAG_VERSION3="",
						TAG_VERSION4="",
						TAG_VERSION5="";

	public OutboundAbstractSchedulePrint() throws Exception {
		super();
		fileName = fileLocation+"/"+FILE_NAME;

		errorFileName = fileLocation+"/"+ERROR_NAME;

		portName = fileLocation+"/"+PORT_NAME;

		fw = new FileWriter(fileName);

		errorfw = new FileWriter(errorFileName);

		portfw = new FileWriter(portName);

		
	}
	public void initTag() {
		logger.info("태그정보 초기화");


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
	}

}
