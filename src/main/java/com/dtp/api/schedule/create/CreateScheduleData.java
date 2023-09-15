package com.dtp.api.schedule.create;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Vector;
import java.util.stream.Collectors;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.ADVDataParser;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;

import lombok.extern.slf4j.Slf4j;

@Deprecated
@Slf4j
public class CreateScheduleData {

	private String currentYear;

	private int currentMonth;

	private ShippersTable tableData;

	private SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

	private SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");

	public CreateScheduleData() throws Exception
	{
		this.currentYear 			= sdfYear.format(new Date());

		this.currentMonth 			= Integer.valueOf(sdfMonth.format(new Date()));
	}

	public CreateScheduleData(ShippersTable tableData) throws Exception
	{
		this();

		log.info("create:{}", tableData);

		this.tableData 				= tableData;

		this.portList 				= this.tableData.getTablePortList();

	}

	public CreateScheduleData(ShippersTable tableData, String data) throws Exception
	{
		this(tableData);

		this.setADVData(new ADVData(tableData.getData()));		
	}

	public CreateScheduleData(ShippersTable tableData, ADVData advData) throws Exception
	{
		this(tableData);

		this.setADVData(advData);		
	}

	public void setADVData(ADVData data) throws Exception
	{
		this.advDataParser 			= new ADVDataParser(data);

		this.arrayDatas 			= advDataParser.getDataArray();

		this.vslDatas 				= advDataParser.getFullVesselArray(isTS(tableData));

		this.outbound_from_indexs 	= ScheduleBuildUtil. makePortArraySub(tableData.getOut_port());

		this.outbound_to_indexs 	= ScheduleBuildUtil.makePortArraySub(tableData.getOut_to_port());

		this.inbound_from_indexs 	= ScheduleBuildUtil.makePortArraySub(tableData.getIn_port());

		this.inbound_to_indexs 		= ScheduleBuildUtil.makePortArraySub(tableData.getIn_to_port());
	}

	public List getScheduleList() throws Exception {

		ArrayList<ScheduleData> scheduleList = new ArrayList<ScheduleData>();

		List<ScheduleData> outboundList = makeScheduleGroupByTable( outbound_from_indexs, outbound_to_indexs, ScheduleData.OUTBOUND);

		List<ScheduleData> inboundList  = makeScheduleGroupByTable( inbound_from_indexs, inbound_to_indexs,  ScheduleData.INBOUND);

		scheduleList.addAll(outboundList);

		scheduleList.addAll(inboundList);

		log.info("inbound, outbound:{}, {}", inboundList.size(), outboundList.size());

		return scheduleList;
	}


	private List makeSchedule(ShippersTable tableData) throws Exception
	{
		// 아웃바운드 출발
		int[] outbound_from_index 		= ScheduleBuildUtil. makePortArraySub(tableData.getOut_port());

		// 아웃바운드 도착
		int[] outbound_to_index			= ScheduleBuildUtil.makePortArraySub(tableData.getOut_to_port());

		// 인바운드 출발
		int[] inbound_from_index 		= ScheduleBuildUtil.makePortArraySub(tableData.getIn_port());

		// 인바운드 도착
		int[] inbound_to_index 			= ScheduleBuildUtil.makePortArraySub(tableData.getIn_to_port());

		List<ScheduleData> outboundList = makeScheduleGroupByTable( outbound_from_index, outbound_to_index, ScheduleData.OUTBOUND);

		log.info("outbound:{}", outboundList.size());

		List<ScheduleData> inboundList  = makeScheduleGroupByTable( inbound_from_index, inbound_to_index,  ScheduleData.INBOUND);

		log.info("inbound:{}", inboundList.size());

		outboundList.addAll(inboundList);

		return outboundList;
	}

	private ArrayList<String> error = new ArrayList<String>();

	private List<TablePort> portList;

	private String[][] arrayDatas;

	private String[][] vslDatas;

	private ADVDataParser advDataParser;

	private int[] outbound_from_indexs;

	private int[] outbound_to_indexs;

	private int[] inbound_from_indexs;

	private int[] inbound_to_indexs;

	/**
	 * 
	 * @param portList
	 * @param index
	 * @return
	 */
	private Optional<TablePort> getTablePort(List<TablePort> portList, int index)
	{
		// 부산, 부산 신항이 같이 있는지?

		return portList.stream().
				filter(s -> s.getPort_index()==index)
				.findFirst();
	}


	/**
	 * 
	 * @param portList
	 * @param index
	 * @return
	 */
	private Optional<TablePort> getTablePort(int index)
	{
		return getTablePort(this.portList,  index);
	}

	/**
	 * 
	 * @param portList
	 * @param index
	 * @return
	 */
	public boolean hasNewBusanAndBusan( int index[])
	{
		// TODO 부산 신항이 있는 지?

		//		portList.stream().랴

		// 부산, 부산 신항이 같이 있는지?

		List strList = Arrays.stream(index)        // IntStream
				.boxed()          // Stream<Integer>
				.collect(Collectors.toList());

		List list=this.portList.stream()
				.filter(o ->strList.contains(o.getPort_index()))
				.filter(o -> o.getPort_name().equals("BUSAN")||o.getPort_name().equals("BUSAN NEW"))
				.collect(Collectors.toList());

		return (list.size()>1);

	}

	private List makeScheduleGroupByTable(int[] from_index, int[] to_index, String inOut	) throws Exception
	{	
		if(from_index== null||to_index== null) return new ArrayList();

		ArrayList<ScheduleData> scheduleList 	= new ArrayList<ScheduleData>();

		for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
		{
			// TODO 사용하지 않는 선박 스케줄 제외

			try {

				String vesselName 		= vslDatas[vslIndex][0];

				String voyage_num		= vslDatas[vslIndex][1];

				String[] dateArray= this.arrayDatas[vslIndex];

				List list=extracted( from_index, to_index, inOut, dateArray,
						vesselName, voyage_num);

				scheduleList.addAll(list);

			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				throw new ArrayIndexOutOfBoundsException(e.getLocalizedMessage());
			}
		}
		log.info("make end");

		return scheduleList;
	}

	private String getFromDateValue(String dateArray[],  int fromPortIndex)
	{
		try {
			return adjestDateYear(KSGDateUtil.getETD(dateArray[fromPortIndex-1]));	
		}
		catch(Exception e)
		{
			return null;
		}

	}

	private String getToDateValue(int vslIndex,  int fromPortIndex) throws NotSupportedDateTypeException
	{
		return adjestDateYear(KSGDateUtil.getETA(this.arrayDatas[vslIndex][fromPortIndex-1]));
	}

	private int getPortIndex(int index[], String port_name)
	{
		List strList = Arrays.stream(index)        // IntStream
				.boxed()          // Stream<Integer>
				.collect(Collectors.toList());

		Optional<TablePort> port =portList.stream()
				.filter(o ->strList.contains(o.getPort_index()))
				.filter(s -> s.getPort_name().equals(port_name))
				.findFirst();

		return port.isPresent()?port.get().getPort_index():-1;
	}

	private List extracted(int[] from_index, int[] to_index, String inOut,
			String[] dateArray, String vesselName, String voyage_num) throws Exception {

		ArrayList<ScheduleData> subScheduleList 	= new ArrayList<ScheduleData>();

		boolean bothNewBusanAndBusan 				= hasNewBusanAndBusan( from_index);

		for(int fromPortCount=0;fromPortCount<from_index.length;fromPortCount++)
		{	
			Optional<TablePort> fromPort 	= getTablePort(from_index[fromPortCount]);

			// busan check
			if(bothNewBusanAndBusan
					&&"BUSAN".equals(fromPort.get().getPort_name()))
			{	

				String date =getFromDateValue(dateArray, getPortIndex(from_index, "BUSAN NEW"));

				if(date !=null) continue;

			}	
			try
			{
				for(int toPortCount=0;toPortCount<to_index.length;toPortCount++)
				{

					int outFromPortIndex 			= from_index[fromPortCount];

					int outToPortIndex 				= to_index[toPortCount];
					
					String fromDate 				= adjestDateYear("%04d%02d%02d",KSGDateUtil.getETD(dateArray[outFromPortIndex-1]));

					String dateF 					= adjestDateYear("%d/%d/%d",KSGDateUtil.getETD(dateArray[outFromPortIndex-1]));


					String toDate 					= adjestDateYear(KSGDateUtil.getETA(dateArray[outToPortIndex-1]));

					String dateT 					= adjestDateYear("%d/%d/%d", KSGDateUtil.getETA(dateArray[outToPortIndex-1]));

					Optional<TablePort> toPort 		= getTablePort(outToPortIndex);

					if(!fromPort.isPresent() ||!toPort.isPresent())
					{
						log.error("port not exist error:"+outToPortIndex);

						throw new RuntimeException("port not exist error:"+outToPortIndex);						
					}

					String area_code = fromPort.get().getArea_code();

					String area_name = fromPort.get().getPort_area();
					
					String fromPortSubArray[]	= fromPort.get().getSubPortNameArray();
					
					String toPortSubArray[]		= toPort.get().getSubPortNameArray();
					
					if(fromPortSubArray.length>1||toPortSubArray.length>1)
					{
						System.out.println();
					}

					subScheduleList.add(
							ScheduleData.builder()	
							.table_id(this.tableData.getTable_id())
							.vessel(vesselName)
							.voyage_num(voyage_num)
							.InOutType(inOut)
							.company_abbr(this.tableData.getCompany_abbr())
							.voyage_num(voyage_num)

							.fromDate(inOut.equals(ScheduleData.OUTBOUND)? fromDate:toDate)
							.toDate(inOut.equals(ScheduleData.OUTBOUND)? toDate:fromDate)
							.fromPort(inOut.equals(ScheduleData.OUTBOUND)? fromPort.get().getPort_name():toPort.get().getPort_name())
							.port(inOut.equals(ScheduleData.OUTBOUND)? toPort.get().getPort_name():fromPort.get().getPort_name())


							.DateF(inOut.equals(ScheduleData.OUTBOUND)? dateF:dateT)
							.DateT(inOut.equals(ScheduleData.OUTBOUND)? dateT:dateF)

							.agent(tableData.getAgent())
							.common_shipping(this.tableData.getCommon_shipping())
							.area_code(area_code)
							.area_name(area_name)
							.gubun(this.tableData.getGubun())
							.date_issue(this.tableData.getDate_isusse())
							.table_date(this.tableData.getTable_date())
							.build());

				}
			}
			catch(NotSupportedDateTypeException e)
			{
				
			}			
		}
		return subScheduleList;
	}
	
	private TablePort getSubTablePort(List arry,int index)
	{
		TablePort port1 = new TablePort();

		if(arry.size()==1)
		{
			port1 = (TablePort) arry.get(0);
		}
		else if(arry.size()>1)
		{
			for(int i=0;i<arry.size();i++)
			{
				TablePort port=(TablePort) arry.get(i);
				
				if(port.getPort_index()==index)
				{
					port1.setPort_name(port.getPort_name());
					port1.addSubPort(port);
				}
			}
		}

		return port1;

	}

	/** TS 인지 확인
	 * @param tableData
	 * @return
	 */
	protected boolean isTS(ShippersTable tableData)
	{
		return tableData.getTS()!=null&&tableData.getTS().equals("TS");
	}

	protected String adjestDateYear(int[] monthAndDay) throws NotSupportedDateTypeException{

		if(monthAndDay[0]==0||monthAndDay[1]==0) throw new NotSupportedDateTypeException("error date", 0);

		int year = KSGDateUtil. selectYear(currentMonth, monthAndDay[0], Integer.valueOf(currentYear));

		return String.format("%04d%02d%02d",   year, monthAndDay[0], +monthAndDay[1]);
	}

	protected String adjestDateYear(String format, int[] monthAndDay) throws NotSupportedDateTypeException{

		if(monthAndDay[0]==0||monthAndDay[1]==0) throw new NotSupportedDateTypeException("error date", 0);

		int year = KSGDateUtil. selectYear(currentMonth, monthAndDay[0], Integer.valueOf(currentYear));

		return String.format(format,   year, monthAndDay[0], +monthAndDay[1]);
	}

	public String getTableId() {
		return tableData.getTable_id();
	}
}
