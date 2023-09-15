package com.ksg.schedule.logic.print;

import java.sql.SQLException;

import com.dtp.api.schedule.joint.print.AbstractSchedulePrint;

public abstract class AbstractInboundSchedulePrint extends AbstractSchedulePrint{
	
	protected String 	BOLD_TAG_F;
	protected String 	BOLD_TAG_B;
	protected String 	TAG_VERSION0;	
	protected String 	TAG_VERSION2;
	protected String 	TAG_VERSION3;
	protected String 	TAG_VERSION6;
	
//	protected final String INBOUND_PRINT_TXT = "inbound_print.txt";

	public AbstractInboundSchedulePrint() throws SQLException {
		super();
		fileName  = "inbound_print.txt"; 
	}

	@Override
	public void init() {
		BOLD_TAG_B="<ct:>";

		BOLD_TAG_F="<ct:77 Bold Condensed>";

		TAG_VERSION0="<KSC5601-WIN>";

		TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<검정:COLOR:CMYK:Process:0,0,0,1>>";

		TAG_VERSION3="<dps:정규=<Nextstyle:정규><cc:검정><cs:7.500000><clig:0><cbs:-0.000000><phll:0><pli:21.543304><pfli:-21.543305><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:21.543304443359375\\,Left\\,.\\,0\\,\\;138.89764404296875\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

		TAG_VERSION6="<pstyle:정규><pli:21.543289><pfli:-21.543290><ptr:158\\,Char\\,\\[\\,0\\,\\;><cl:6.750000><chs:0.800003><cs:8.000000><cf:Helvetica LT Std><ct:Roman>";

		
	}

}
