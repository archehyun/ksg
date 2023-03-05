package com.dtp.api.schedule.create;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import com.dtp.api.service.ShipperTableService;
import com.dtp.api.service.impl.ShipperTableServiceImpl;
import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;
import com.ksg.service.ScheduleService;

public class CreateNomalSchedule {
	
	
	private int[]	a_inport,a_intoport,a_outport,a_outtoport;
	
	private ShipperTableService service = new ShipperTableServiceImpl();
	
	List<ShippersTable> shippersTables;
	
	public CreateNomalSchedule(String gubun, String date) throws SQLException
	{
		ShippersTable param = ShippersTable.builder()
										.gubun(gubun)
										.date_isusse(date)
										.build();
		
		shippersTables  = service.selectTableListAndPortListByCondition(param);
			
			
		
	}
	private List makeScheduleGroupByTable(ShippersTable tableData)
	{	
		ADVData advData = new ADVData(tableData.getData());
		
		// 날짜 정보 배열
		//arrayDatas = advData.getDataArray();

		// 선박 정보 배열
		//vslDatas = advData.getFullVesselArray(isTS(tableData));
		

		
		String[][] arrayDatas = advData.getDataArray();

		// 선박 정보 배열
		String[][] vslDatas = advData.getFullVesselArray(isTS(tableData));
		
		for(String vesselInfo[]:vslDatas)
		{
			 List li=makeScheduleByVesselInfo(tableData, vesselInfo);
		}
		
		return null;
	}
	private List  makeScheduleByVesselInfo(ShippersTable tableData, String vesselInfo[])
	{
		a_outport 	= ScheduleBuildUtil. makePortArraySub(tableData.getOut_port());
		// 인바운드 국내항
		a_inport 	= ScheduleBuildUtil.makePortArraySub(tableData.getIn_port());
		// 아웃바운드 외국항
		a_outtoport	= ScheduleBuildUtil.makePortArraySub(tableData.getOut_to_port());
		// 인바운드 외국항
		a_intoport 	= ScheduleBuildUtil.makePortArraySub(tableData.getIn_to_port());
		
		
		TablePort[] portArray = (TablePort[]) tableData.getTablePortList().toArray(new TablePort[tableData.getTablePortList().size()]);
		
		//arrayDatas = advData.getDataArray();
		
		for(int fromPortCount=0;fromPortCount<a_inport.length;fromPortCount++)
		{
			for(int toPortCount=0;toPortCount<a_intoport.length;toPortCount++)
			{
				String fromPort = portArray[fromPortCount].getPort_name();
				String toPort = portArray[toPortCount].getPort_name();
				String vesselName = vesselInfo[0];
				String voyage = vesselInfo[1];
//				String[] scheduleDate= getScheduleDdate(ScheduleService.INBOUND,0, fromPortCount, toPortCount);
			}
		}
		
		
		
		return null;
	}
	private void insertScheduleBulk(List scheduleList)
	{
		
	}
	protected boolean isTS(ShippersTable tableData)
	{
		return tableData.getTS()!=null&&tableData.getTS().equals("TS");
	}
	
	public ScheduleData createScheduleData(ShippersTable table,
			String table_id,
			String voyageNum,
			String vesselName,
			String dateF, 
			String dateT,
			String area_code, 
			String area_name, 
			String gubun,
			String company_abbr,
			String agent,
			String fromPort, 
			String toPort,
			String inOutType,
			String common_shipping,
			String date_isusse, 
			int vslIndex)

	{

		ScheduleData scheduledata =ScheduleData.builder()
				.table_id(table_id)
				.vessel(vesselName)
				.agent(agent)
				.company_abbr(company_abbr)
				.port(toPort)
				.fromPort(fromPort)
				.area_code(area_code)
				.area_name(area_name.toUpperCase())
				.InOutType(inOutType)
				.voyage_num(voyageNum)
				.n_voyage_num(ScheduleBuildUtil.getNumericVoyage(voyageNum))
				.gubun(gubun)
				.common_shipping(common_shipping==null?"":common_shipping)
				.TS("")
				.ts_date("")
				.ts_port("")
				.ts_vessel("")
				.ts_voyage_num("")
				//											.inland_date_back("")
				.inland_date("")
				.inland_port("")
				.DateF(dateF)
				.DateT(dateT)
				.date_issue(getDateIsusse(date_isusse))
				.c_time("")
				.d_time("")
				.console_cfs("")
				.console_page("")
				.build();
		
		
		
		return scheduledata;
	}
	private String getDateIsusse(String date_isusse)
	{
		try {
			return KSGDateUtil.format(KSGDateUtil.toDate2(date_isusse));
		} catch (ParseException e) {
			e.printStackTrace();
			return date_isusse;

		}
	}
	
	private String[] getScheduleDdate(String arrayDatas[][],String InOutBoundType, int vslIndex,int outPortIndex, int outToPortIndex) throws NotSupportedDateTypeException
	{
		// 출발일, 도착일 날짜 지정
		if(InOutBoundType.equals(ScheduleService.INBOUND))
		{
			return ScheduleBuildUtil.adjestDateYear(arrayDatas[vslIndex][outToPortIndex-1],arrayDatas[vslIndex][outPortIndex-1],InOutBoundType);
		}
		else
		{
			return ScheduleBuildUtil.adjestDateYear(arrayDatas[vslIndex][outPortIndex-1],arrayDatas[vslIndex][outToPortIndex-1],InOutBoundType);
		}
	}

	
	public int execute()
	{
		if(shippersTables.isEmpty()) return 0;
		
		for(ShippersTable item:shippersTables)
		{
			List scheduleList = makeScheduleGroupByTable(item);
			
			
			insertScheduleBulk(scheduleList);
		}
		return 0;
	}

}
