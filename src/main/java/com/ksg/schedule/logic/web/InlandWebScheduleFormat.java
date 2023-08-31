package com.ksg.schedule.logic.web;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;

/**
 * @author 박창현
 *
 */
public class InlandWebScheduleFormat extends WebScheduleFormat implements IFWebScheduleFormat{

	private String[][] arrayDatas;

	private String[][] vslDatas;
	
	public InlandWebScheduleFormat()
	{
		fileName="INLAND";
		errorFileName = "inlnad";
	}
	public InlandWebScheduleFormat(DefaultWebSchedule webSchedule) {
		this();
		this.webSchedule = webSchedule;

	}

	public InlandWebScheduleFormat(DefaultWebScheduleV2 defaultWebScheduleV2) {
		this();
	}
	public InlandWebScheduleFormat(DefaultWebScheduleV3 defaultWebScheduleV3) {
		this();
	}

	public String toWebScheduleString(ScheduleData data)
	{
		return	data.getInOutType()+"\t"+ 	//구분(In/Out)
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
				data.getInland_port()+"\t"+	//중간기착항구
				data.getInland_date()+"\t"+	//중간기작항ETA
				data.getInland_date_back()+"\t"+	//중간기작항ETD
				data.getBookPage()+"\t"+    //지면 페이지
				data.getVessel_type()+"\t"+	//선종
				data.getOperator()+"\t"+	//대표 선사
				data.getVessel_mmsi()+"\t"	//MMSI
				;
	}

	@Override
	public ScheduleData createWebScheduleData(ShippersTable table,
			String inOutType, int vslIndex, Vessel vesselInfo, int fromPortIndex,
			int toPortIndex, String fromPort, String toPort) {
		vslDatas = webSchedule.getVslDatas();

		String fromDates[]		= webSchedule.getFromDates();
		String toDates[]		= webSchedule.getToDates();		
		ScheduleData scheduledata = new ScheduleData();
		scheduledata.setFromPort(fromPort);				// 출발항
		scheduledata.setDateF(fromDates[0]);			// 출발일 ETA
		scheduledata.setDateFBack(fromDates[1]);	    // 출발일 ETD
		scheduledata.setPort(toPort);					// 도착항
		scheduledata.setDateT(toDates[0]);				// 도착일 ETA
		scheduledata.setDateTBack(toDates[1]);			// 도착일 ETD
		scheduledata.setVessel(vslDatas[vslIndex][0]);	// 선박명 할당
		scheduledata.setVoyage_num(vslDatas[vslIndex][1]);
		scheduledata.setCompany_abbr(table.getCompany_abbr());// Console 업체명
		scheduledata.setAgent(table.getAgent());		// 에이전트 할당
		scheduledata.setInland_port(table.getIn_to_port());		
		scheduledata.setVessel_type(vesselInfo.getVessel_type());				//선종 
		scheduledata.setOperator(vesselInfo.getVessel_company());// 대표업체
		return scheduledata;
	}



}
