package com.ksg.commands.schedule.route;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jdom.Element;

import com.ksg.commands.schedule.task.SimpleTask;
import com.ksg.common.dao.DAOManager;
import com.ksg.domain.Code;
import com.ksg.domain.ShippersTable;
import com.ksg.print.logic.quark.XTGManager;
import com.ksg.schedule.view.dialog.ScheduleBuildMessageDialog;

public abstract class DefaultRouteTask extends SimpleTask{
	public static final int ORDER_BY_DATE=1;
	public static final int ORDER_BY_VESSEL=2;
	protected String INCODE_KEY;
	protected String WORLD_OUTPORT;
	protected String WORLD_F;
	protected String WORLD_B;
	protected String WORLD_E;
	protected String WORLD_VERSION1;
	protected String WORLD_VERSION2;
	protected String WORLD_VERSION3;
	protected int UP_SIZE,DOWN_SIZE;

	protected String WORLD_INPORT;
	protected final int FORWARD =0;
	protected final int BACK =1;
	protected XTGManager xtgmanager = new XTGManager();
	protected  static final String WORLD_SOURCE_XML = "world_source.xml";
	protected ShippersTable op;
	protected DAOManager manager =DAOManager.getInstance();
	protected ScheduleBuildMessageDialog di;
	protected SimpleDateFormat inputDateType_yyyy_MM_dd = new SimpleDateFormat("yyyy/MM/dd");
	protected SimpleDateFormat outputDateType = new SimpleDateFormat("M/d");
	protected void initTag(){
		logger.debug("start");
		try {
		Code code_info = new Code();
		code_info.setCode_name("WORLD_F");
		
			Code b=baseService.getCodeInfo(code_info);


//		WORLD_F=b.getCode_field();
//		WORLD_F="◆11◆";
		WORLD_F="<ct:><cf:><cs:><cf:Helvetica Neue LT Std><ct:77 Bold Condensed><cs:7.000000>";
		WORLD_F="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon가변 윤고딕100\\_TT>▲<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		code_info.setCode_name("WORLD_B");

		Code f=baseService.getCodeInfo(code_info);

//		WORLD_B=f.getCode_field();
//		WORLD_B="◆22◆";
		WORLD_B="<ct:><cf:><cf:Helvetica LT Std><ct:Roman><cs:><cs:6.000000>";
		WORLD_B="<cc:><ct:><cs:><cf:><cc:60.100.0.0.><ct:30><cs:7.500000><cf:Yoon가변 윤고딕100\\_TT>▲<ct:><cf:><ct:Bold><cf:Helvetica LT Std>";
		code_info.setCode_name("WORLD_VERSION1");
		Code f0=baseService.getCodeInfo(code_info);
//		WORLD_VERSION1=f0.getCode_field();
//		WORLD_VERSION1="◆33◆";
		WORLD_VERSION1="<KSC5601-WIN>\r\n<vsn:8><fset:InDesign-Roman><ctable:=<Black:COLOR:CMYK:Process:0,0,0,1><60.100.0.0.:COLOR:CMYK:Process:0.6,1,0,0><30.60.0.0.:COLOR:CMYK:Process:0.3,0.6,0,0>>";
		WORLD_INPORT="<cc:><ct:><cs:><cf:><cc:30.60.0.0.><ct:Roman><cs:6.000000><cbs:-1.000000><cf:Helvetica LT Std>";
		WORLD_OUTPORT="<cc:><ct:><cs:><cbs:><cf:><ct:Roman><cs:6.000000><cf:Helvetica LT Std>";
		code_info.setCode_name("WORLD_VERSION2");
		Code f2=baseService.getCodeInfo(code_info);
//		WORLD_VERSION2=f2.getCode_field();
//		WORLD_VERSION2="◆44◆";
		WORLD_VERSION2="<dps:정규=<Nextstyle:정규><cc:검정><clig:0><cbs:-0.000000><phll:0><palp:1.199996><clang:Neutral><ph:0><pmcbh:3><phc:0><pswh:6><phz:0.000000><cf:JCsm><pmaws:1.500000><pmiws:1.000000><pmaxl:0.149993><prac:검정><prat:100.000000><prbc:검정><prbt:100.000000><pta:JustifyLeft><blf:\\<TextFont\\>><bltf:\\<TextStyle\\>>>";
		WORLD_VERSION2="<pstyle:><ct:Bold><chs:0.900000><cl:8.000000><cf:Helvetica LT Std>";

		code_info.setCode_name("WORLD_VERSION3");
		Code f3=baseService.getCodeInfo(code_info);
//		WORLD_VERSION3=f3.getCode_field();
//		WORLD_VERSION3="◆55◆";
		WORLD_VERSION3="<pstyle:정규><pli:182.500000><pfli:-182.000000><psa:0.566894><ptr:96.37789916992188\\,Left\\,.\\,0\\,\\;201\\,Left\\,.\\,0\\,\\;><chs:0.800003><cl:20.000000><cs:18.000000><cf:Helvetica LT Std>\r\n<cl:><cl:20.099990>\r\n<cs:><ct:Bold><cs:18.000000>";

		code_info.setCode_name("WORLD_E");
		Code f4=baseService.getCodeInfo(code_info);
//		WORLD_E=f4.getCode_field();
//		WORLD_E="◆66◆";
		WORLD_E=" \r\n<cl:><cl:20.099990><cs:><ct:><cf:><cf:Helvetica LT Std><ct:Bold><cs:18.000000>";
		WORLD_E="<ct:><cs:><cf:><ct:Bold><cf:Helvetica LT Std>";

		code_info.setCode_name("WORLD_U_SIZE");
		Code USIZE=baseService.getCodeInfo(code_info);
		UP_SIZE=Integer.parseInt(USIZE.getCode_field());

		code_info.setCode_name("WORLD_L_SIZE");
		Code LSIZE=baseService.getCodeInfo(code_info);
		DOWN_SIZE=Integer.parseInt(LSIZE.getCode_field());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.debug("end");

	}
	/**
	 * @작성일 2015-05-06
	 * @설명 항로별 스케줄 생성 여부 결정
	 * 		- 일본, 중국 : 2개 미만 스케줄 제외
	 * 		- 러시아	  : 1개 이상 스케줄 포함
	 *		- 기타	  : 3개 미만 스케줄 제외
	 * 
	 * @param code
	 * @param outport_list
	 * @return
	 */
	protected boolean skipCheck(Element area, List outport_list) {
		boolean flag = false;

		String area_code = area.getAttributeValue("area_code");
		int area_book_code = Integer.parseInt(area.getAttributeValue("area_book_code"));

		if(area_code.equals("02"))
		{
			switch (area_book_code) {
			case 1: // 아시아(Asia)
				if(outport_list.size()<3)
				{
					flag = true;
				}
				break;
			
			case 16: //  (CIS)
				if(outport_list.size()<3)
				{
					flag = true;
				}
				break;
			case 9: // PERSIAN GULF
				if(outport_list.size()<3)
				{
					flag = true;
				}
				break;
				
			case 4: // China
				if(outport_list.size()<2)
				{
					flag = true;
				}
				break;
			case 7: // Japan
				if(outport_list.size()<2)
				{
					flag = true;
				}
				break;	

			default:// 러시아는 1개라도 있으면 출력
				break;
			}
		}
		else // 다른 지역은 3개 미만인 스케줄은 제외
		{
			if(outport_list.size()<3)
				flag = true;
		}

		return flag;
	}
	public int getNumericVoyage(String voyage_num)
	{

		int result=0;

		String temp="";
		if(voyage_num==null)
			return 0;
		for(int i=0;i<voyage_num.length();i++)
		{
			try{
				temp+=Integer.parseInt(String.valueOf(voyage_num.charAt(i)));
			}catch(NumberFormatException e)
			{
				//				return 0;
			}
		}
		try{
			result=Integer.valueOf(temp);
		}catch(Exception e)
		{
			return 0;
		}

		return result;
	}
}
