package com.ksg.schedule.web;

import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;

/**
 * 콘솔 광고 정보를 웹용 스케줄로 변환
 * @author archehyun
 *
 */
public class ConsloeWebScheduleFormat extends WebScheduleFormat  implements IFWebScheduleFormat{

	/**
	 * 
	 */

	private String[][] arrayDatas;

	private String[][] vslDatas;
	DefaultWebSchedule webSchedule;
	public ConsloeWebScheduleFormat(DefaultWebSchedule webSchedule) {
		this.webSchedule = webSchedule;
		fileName="CONSOLE";
		errorFileName = "console";
	}
	

	public ConsloeWebScheduleFormat(DefaultWebScheduleV2 defaultWebScheduleV2) {
		fileName="CONSOLE";
		errorFileName = "console";
	}


	public String toWebScheduleString(ScheduleData data)
	{
		return	data.getFromPort()+"\t"+ 	//출발항
				data.getDateF()+"\t"+	 	//출발ETA
				data.getDateFBack()+"\t"+	//출발ETD
				data.getPort()+"\t"+		//도착항구
				data.getDateT()+"\t"+		//도착ETA
				data.getDateTBack()+"\t"+	//도착ETD
				data.getVessel()+"\t"+		//선박명
				data.getVoyage_num()+"\t"+	//항차번호
				data.getAgent()+"\t"+		//콘솔 업체명(에이전트 약어)
				data.getD_time()+"\t"+		//DCT
				data.getC_time()+"\t"+		//CCT
				data.getConsole_cfs()+"\t"+	//CFS
				data.getBookPage()+"\t"+	//지면 페이지
				data.getVessel_type()+"\t"+	//선종
				data.getOperator()+"\t"	+	//대표 선사
				data.getCompany_abbr()+"\t"	+	//Line(선사명 약어)
				data.getVessel_mmsi()+"\t"	//선박 MMSI 코드
				;
	}
	public ScheduleData createWebScheduleData(ShippersTable table,String inOutType,int vslIndex,
			Vessel vesselInfo,int fromPortIndex,int toPortIndex, String fromPort, String toPort)
	{
		arrayDatas = webSchedule.getArrayDatas();
		vslDatas = webSchedule.getVslDatas();
		String outToPortData 	= arrayDatas[vslIndex][toPortIndex-1];
		String outPortData		= arrayDatas[vslIndex][fromPortIndex-1];
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
		scheduledata.setVoyage_num(vslDatas[vslIndex][1]);// 항차 번호
		scheduledata.setCompany_abbr(table.getCompany_abbr());// Console 업체명
		scheduledata.setAgent(table.getAgent());		// 에이전트 약어
		scheduledata.setD_time(table.getD_time()==0?"":arrayDatas[vslIndex][table.getD_time()-1]); //DCT
		scheduledata.setC_time(table.getC_time()==0?"":arrayDatas[vslIndex][table.getC_time()-1]); //CCT		
		scheduledata.setConsole_cfs(getNewCFS(table.getConsole_cfs())); //CFS
		scheduledata.setConsole_page(table.getConsole_page());  // 콘솔 페이지
		scheduledata.setBookPage(table.getBookPage());	// 지면 페이지
		scheduledata.setVessel_type(vesselInfo.getVessel_type());				//선종
		scheduledata.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi());	
		scheduledata.setOperator(getOperator(vesselInfo.getVessel_company()));// 선박 대표업체
		
		return scheduledata;
	}
	private String getOperator(String vessel_company)
	{
		if(vessel_company.contains("/"))
		{
			return vessel_company.substring(0,vessel_company.indexOf("/"));
		}
		else
		{
			return vessel_company;
		}	
		
	}
	/**
	 * @since 2015-10-12
	 * @설명 입력된 CFS 정보 중 개행문자를 &로 치환하여 반환
	 * @param cfs 콘솔 CFS 정보
	 * @return
	 */
	private String getNewCFS(String cfs)
	{		
		return cfs.replaceAll("\n", "\\\\&");
	}



}
