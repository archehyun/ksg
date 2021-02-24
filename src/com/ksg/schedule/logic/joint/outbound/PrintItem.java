package com.ksg.schedule.logic.joint.outbound;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ksg.common.util.KSGDateUtil;

/**
 * @author ��â��
 *
 */
public class PrintItem implements Comparable<PrintItem>{
	
	private boolean isApplyTag=true;// �±� ���� ����
	
	public void initTag() {	


		if(isApplyTag)
		{
			BOLD_TAG_B="<ct:>";

			BOLD_TAG_F="<ct:Bold Condensed>";

			TAG_VERSION0="<KSC5601-WIN>";

			TAG_VERSION2="<vsn:8><fset:InDesign-Roman><ctable:=<����:COLOR:CMYK:Process:0,0,0,1>>";

			TAG_VERSION3="<dps:����=<Nextstyle:����><cc:����><cs:8.000000><clig:0><cbs:-0.000000><phll:0><pli:53.858291><pfli:-53.858292><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cf:Helvetica LT Std><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:����><prat:100.000000><prbc:����><prbt:100.000000><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";		

			TAG_VERSION4="<dps:Body Text=<BasedOn:����><Nextstyle:Body Text><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";

			TAG_VERSION5="<pstyle:Body Text><ptr:19.842498779296875\\,Left\\,.\\,0\\,\\;211.3332977294922\\,Right\\,.\\,0\\,\\;><cs:8.000000><cl:5.479995><cf:Helvetica LT Std><ct:Roman>\r\n";			
		}
	}
	
	
	private String 	BOLD_TAG_F="",
			BOLD_TAG_B="",
			TAG_VERSION0="",
			TAG_VERSION2="",
			TAG_VERSION3="",
			TAG_VERSION4="",
			TAG_VERSION5="";

	
	protected SimpleDateFormat inputDateFormat 	= KSGDateUtil.createInputDateFormat();

	protected SimpleDateFormat outputDateFormat = KSGDateUtil.createOutputDateFormat();
	
	private String vessel_name;//���ڸ�
	
	public String getVessel_name() {
		return vessel_name;
	}

	private String vessel_type;

	private String dateF;
	public String getDateF() {
		return dateF;
	}

	private String dateT;
	public String getDateT() {
		return dateT;
	}

	private String company;
	//private CompanyString companyString;

	public PrintItem(String dateF, String vessel_name,String vessel_type, String company,String dateT) {
		this.vessel_name = vessel_name;			
		this.vessel_type = vessel_type;
		this.dateF = dateF;
		this.dateT= dateT;
		this.company= company;
	}




	@Override
	public int compareTo(PrintItem o) {

		PrintItem table1 = (PrintItem) o;
		Date fromOne = new Date(table1.dateF);
		Date fromTwo = new Date(this.dateF);

		// ���� ���� : �����->������

		if(KSGDateUtil.isSame(table1.dateF, this.dateF))
		{
			//������� ���� ��� ������ �� 
			Date toOne = new Date(table1.dateT);
			Date toTwo = new Date(this.dateT);

			return KSGDateUtil.daysDiff( toOne,toTwo);
		}
		else
		{
			return KSGDateUtil.daysDiff( fromOne,fromTwo);	
		}

	}	


	public String toString()
	{
		// 
		try {

			String formatedDateF=outputDateFormat.format(inputDateFormat.parse(dateF));
			String formatedDateT=outputDateFormat.format(inputDateFormat.parse(dateT));
			return buildVesselXTG(formatedDateF, vessel_name, company, vessel_type, formatedDateT);
		} catch (ParseException e)
		{

			e.printStackTrace();
			return "date error";
		}

	}

	private String buildVesselXTG(String dateF, String vesselname, String company,	String vessel_type, String dateT) {
		if(isApplyTag)
		{
			return "<ct:><cs:><ct:Roman><cs:6.000000>"+dateF+"<ct:>"+BOLD_TAG_F+"\t"+vesselname+BOLD_TAG_B+"<ct:Roman>"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n";
		}
		else
		{
			return dateF+"\t"+vesselname+"\t"+(vessel_type.equals("")||vessel_type.equals(" ")?"   ":"   ["+vessel_type+"]   ")+"("+company+")\t"+dateT+"\r\n";


		}
	}

}
