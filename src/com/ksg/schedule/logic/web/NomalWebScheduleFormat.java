package com.ksg.schedule.logic.web;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;

/**
 * @author 박창현
 *
 */
public class NomalWebScheduleFormat extends WebScheduleFormat implements IFWebScheduleFormat{
	
	private DefaultWebSchedule webSchedule;
	private DefaultWebScheduleV2 webSchedulev2;
	private String[][] arrayDatas;
	private String[][] vslDatas;
	
	public NomalWebScheduleFormat(DefaultWebSchedule webSchedule) {
		this.webSchedule = webSchedule;
		fileName="WW_SYBASE";
		errorFileName = "ww_sybase";
	}

	public NomalWebScheduleFormat(DefaultWebScheduleV2 defaultWebScheduleV2) {
		webSchedulev2 = defaultWebScheduleV2;
		fileName="WW_SYBASE";
		errorFileName = "ww_sybase";
	}

	public String toWebScheduleString(ScheduleData data)
	{
		return	data.getInOutType()+"\t"+ 	//구분(Inbound/Outbound)
				data.getFromPort()+"\t"+ 	//출발항
				data.getDateF()+"\t"+	 	//출발ETA
				data.getDateFBack()+"\t"+	//출발ETD
				data.getPort()+"\t"+		//도착항구
				data.getDateT()+"\t"+		//도착ETA
				data.getDateTBack()+"\t"+	//도착ETD
				data.getVessel()+"\t"+		//선박명
				data.getVoyage_num()+"\t"+	//항차번호
				data.getCompany_abbr()+"\t"+//Line
				data.getAgent()+"\t"+		//Agent
				data.getBookPage()+"\t"+	//지면페이지				
				data.getVessel_type()+"\t"+	//선종
				data.getOperator()+"\t"+//대표 선사
				data.getVessel_mmsi()+"\t"	//선박 MMSI 코드 
				;
	}

	public ScheduleData createWebScheduleData(ShippersTable table,
			String inOutType, int vslIndex, Vessel vesselInfo, int fromPortIndex,
			int toPortIndex, String fromPort, String toPort) {

		arrayDatas = webSchedule.getArrayDatas();
		vslDatas = webSchedule.getVslDatas();
		String outToPortData 	= arrayDatas[vslIndex][toPortIndex-1];
		String outPortData		= arrayDatas[vslIndex][fromPortIndex-1];
		String fromDates[]		= webSchedule.getFromDates();
		String toDates[]		= webSchedule.getToDates();		
		
		ScheduleData scheduledata = new ScheduleData();
		scheduledata.setInOutType(inOutType);
		scheduledata.setFromPort(fromPort);				// 출발항
		scheduledata.setDateF(fromDates[0]);			// 출발일 ETA
		scheduledata.setDateFBack(fromDates[1]);	    // 출발일 ETD
		scheduledata.setPort(toPort);					// 도착항
		scheduledata.setDateT(toDates[0]);				// 도착일 ETA
		scheduledata.setDateTBack(toDates[1]);			// 도착일 ETD
		scheduledata.setVessel(vesselInfo.getVessel_name());	// 선박명 할당
		scheduledata.setVoyage_num(vslDatas[vslIndex][1]); // 항차 번호
		scheduledata.setCompany_abbr(table.getCompany_abbr());// Line
		scheduledata.setAgent(table.getAgent());		// 에이전트 
		scheduledata.setBookPage(table.getBookPage());
		scheduledata.setVessel_type(vesselInfo.getVessel_type());				//선종
		scheduledata.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi());
		
		String vessel_compmay="";
		if(vessel_compmay.contains("/"))
		{
			vessel_compmay=vesselInfo.getVessel_company().substring(0,vesselInfo.getVessel_company().indexOf("/"));
		}
		else
		{
			vessel_compmay= vesselInfo.getVessel_company();
		}
		scheduledata.setOperator(vessel_compmay);// 대표업체
		return scheduledata;
	}
}
