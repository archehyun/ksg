package com.dtp.api.schedule.create;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ksg.commands.schedule.ErrorLog;
import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.exception.VesselNullException;
import com.ksg.domain.ADVData;
import com.ksg.domain.ADVDataParser;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ScheduleEnum;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.print.ScheduleBuildUtil;
import com.ksg.service.ScheduleService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CreateSchedule {
	
	public static final String BUSAN 			= "BUSAN";

	public static final String BUSAN_NEW_PORT 	= "BUSAN NEW";
	
	private boolean isFromBusanAndNewBusan;

	private boolean isToBusanAndNewBusan;

	protected ArrayList<ErrorLog> errorlist ;

	public static final int TYPE_INBOUND=1;

	public static final int TYPE_OUTBOUND=2;

	private int[]	index_inbound_fromport,index_inbound_toport,index_outbound_fromport,index_outbound_toport;

	private List<TablePort> tablePortList;

	private ShippersTable tableData;

	private String[][] vslDatas;

	private String[][] arrayDatas;

	private String gubun;

	public CreateSchedule() {

		errorlist= new ArrayList<ErrorLog>();
	}

	public void setShipperTable(ShippersTable tableData) throws Exception
	{
		log.info("init start:{}", tableData.getTable_id());
		
		this.tableData 						= tableData;

		if(tableData==null) throw new ResourceNotFoundException("tabledata is null");

		this.gubun 							= tableData.getGubun();

		String data 						= tableData.getData();

		this.tablePortList 					= tableData.getTablePortList();

		ADVDataParser parser 				= new ADVDataParser(new ADVData(data));

		// 날짜 정보 배열
		this.arrayDatas 					= parser.getDataArray();

		// 선박 정보 배열
		this.vslDatas 						= parser.getFullVesselArray(isTS(tableData));

		// 아웃바운드 출발항 인덱스
		this.index_outbound_fromport 		= ScheduleBuildUtil.makePortArraySub(tableData.getOut_port());

		// 인바운드 출발항 인덱스
		this.index_inbound_fromport 		= ScheduleBuildUtil.makePortArraySub(tableData.getIn_port());

		// 아웃바운드 도착항 인덱스
		this.index_outbound_toport			= ScheduleBuildUtil.makePortArraySub(tableData.getOut_to_port());

		// 인바운드 도착항 인덱스
		this.index_inbound_toport 			= ScheduleBuildUtil.makePortArraySub(tableData.getIn_to_port());
		
		log.info("init end:{}", tableData.getTable_id());
	}

	public List<TablePort> getPortList()
	{
		return this.tablePortList;
	}

	/**
	 * @param tableData
	 * @param vslIndex
	 * @param fromPorts
	 * @param toPorts
	 * @param InOutBoundType
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public List<ScheduleData> makeSchedule( String dataArray[], String vesselName, String voyageNum, int[]fromPorts, int[]toPorts,String InOutBoundType)
			throws ArrayIndexOutOfBoundsException,NullPointerException, ParseException,VesselNullException{

		ArrayList<ScheduleData> scheduleList = new ArrayList<ScheduleData>();

		if(fromPorts==null||toPorts==null)
		{			
			return scheduleList;
		}

		try{

			String scheduleDate[];

			this.isFromBusanAndNewBusan 	= this.hasNewBusanAndBusan(fromPorts);

			this.isToBusanAndNewBusan 		= this.hasNewBusanAndBusan(toPorts);			

			for(int fromPortCount=0;fromPortCount<fromPorts.length;fromPortCount++)
			{
				int outFromPortIndex =fromPorts[fromPortCount];

				// 외국항
				for(int toPortCount=0;toPortCount<toPorts.length;toPortCount++)
				{
					int outToPortIndex = toPorts[toPortCount];

					String outFromPortData 				= dataArray[outFromPortIndex-1];

					String outToPortData 				= dataArray[outToPortIndex-1];
					//

					if(!isScheduleDataValidation(outToPortData, outFromPortData)) continue;

					/*
					 * 부산항만 있을 경우
					 * 부산신항만 있을 경우
					 * 부산항과 신항이 같이 있을 경우
					 * 
					 */

					try 
					{
						// 출발일, 도착일 날짜 지정
						scheduleDate=					getScheduleDdate(InOutBoundType, dataArray, outFromPortIndex, outToPortIndex);

					} catch (NotSupportedDateTypeException e) 
					{

						// 날자 형태가 "-"라면 계속 속행
						if(!e.date.equals("-"))
						{
							//log.error("no match date:"+e.date+", id:"+tableData.getTable_id());
							errorlist.add(createError("날짜 오류", tableData.getTable_id(), e.date));	
						}
						continue;
					}

					TablePort _outFromport 				= getTablePort(outFromPortIndex);

					TablePort _outtoport 				= getTablePort( outToPortIndex);				

					scheduleList.addAll( makeScheduleBySubPort(dataArray, InOutBoundType,  
							scheduleDate, vesselName, voyageNum, 
							_outFromport, _outtoport));
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();

			throw new RuntimeException("error:"+e.getMessage()+",table id:"+tableData.getTable_id());
		}
		
		
		// 부산항, 부산 신항 처리
		if(InOutBoundType.equals(ScheduleEnum.OUTBOUND.getSymbol())&&this.isFromBusanAndNewBusan)
		{	
			Optional<ScheduleData> firstElement = scheduleList.stream()
																.filter(schedule -> schedule.getFromPort().equals(BUSAN_NEW_PORT))
																.findFirst();
			if(firstElement.isPresent())
			{
				scheduleList =(ArrayList<ScheduleData>) scheduleList.stream()
																	.filter(schedule -> !schedule.getFromPort().equals(BUSAN))
																	.collect(Collectors.toList());
			}
		}
		if(InOutBoundType.equals(ScheduleEnum.INBOUND.getSymbol())&&this.isToBusanAndNewBusan)
		{	
			Optional<ScheduleData> firstElement = scheduleList.stream()
																.filter(schedule -> schedule.getPort().equals(BUSAN_NEW_PORT))
																.findFirst();
			if(firstElement.isPresent())
			{
				scheduleList =(ArrayList<ScheduleData>) scheduleList.stream()
										.filter(schedule -> !schedule.getPort().equals(BUSAN))
										.collect(Collectors.toList());
			}
		}

		return scheduleList;
	}

	private List<ScheduleData> makeScheduleBySubPort(String dataArray[], String InOutBoundType, 
			String[] scheduleDate, String vesselName,
			String voyageNum, 
			TablePort subFromPort, TablePort subToPort) {

		ArrayList<ScheduleData> scheduleList 	= new ArrayList<ScheduleData>();

		TablePort[] subFromPortArray			= subFromPort.getSubPortArray();

		TablePort[] subToPortArray				= subToPort.getSubPortArray();	

		String area_code 	= null;

		String area_name 	= null;

		TablePort fromPort 	= null;

		TablePort toPort	= null;

		String dateF 		= scheduleDate[0];
		String dateT		= scheduleDate[1];

		for(int z =0;z<subFromPortArray.length;z++)
		{
			for(int c =0;c<subToPortArray.length;c++)
			{
				fromPort	= InOutBoundType.equals(ScheduleEnum.INBOUND.getSymbol())?subToPortArray[c]:subFromPortArray[z];

				toPort		= InOutBoundType.equals(ScheduleEnum.INBOUND.getSymbol())?subFromPortArray[z]:subToPortArray[c];

				area_code	= toPort.getArea_code();

				area_name	= toPort.getPort_area();

				ScheduleData insertData=	
						createScheduleData(this.tableData,
								voyageNum, 
								vesselName, 
								dateF,//dateF 
								dateT,//dateT 
								area_code, 
								area_name,								 
								fromPort.getPort_name(), 
								toPort.getPort_name(), 
								InOutBoundType
								);

				//구분이 콘솔일 경우
				if(gubun.equals(ShippersTable.GUBUN_CONSOLE))
				{
					insertData.setC_time(this.tableData.getC_time()==0?"":dataArray[this.tableData.getC_time()-1]);
					insertData.setD_time(this.tableData.getD_time()==0?"":dataArray[this.tableData.getD_time()-1]);
					insertData.setConsole_cfs(this.tableData.getConsole_cfs());
					insertData.setConsole_page(this.tableData.getConsole_page());
				}
				
				// 부산 신항 스케줄 제외

				scheduleList.add(insertData);
			}
		}

		return scheduleList;
	}
	
	public List<ScheduleData> getScheduleList(int fromPortIndex[], int toPortIndex[], String inOutType) throws Exception
	{
		
		ArrayList<ScheduleData> insertList = new ArrayList<ScheduleData>();

		for(int vslIndex=0;vslIndex<this.vslDatas.length;vslIndex++)
		{
			String vesselName		= this.vslDatas[vslIndex][0];

			String voyageNum		= this.vslDatas[vslIndex][1];

			try
			{
				//TODO 선박 조회 기능 변경
				Vessel vesselInfo 		= ScheduleManager.getInstance().searchVessel(vesselName);

				// 사용하지 않는 선박이면 스케줄에서 제외
				if(vesselInfo.getVessel_use()==Vessel.NON_USE) continue;

				// 인바운드 스케줄 생성
				List<ScheduleData> inboundList 	= makeSchedule( this.arrayDatas[vslIndex], vesselName, voyageNum, fromPortIndex,toPortIndex,inOutType);

				// 아웃바운드 스케줄 생성
				

				insertList.addAll(inboundList);

				
			}
			catch(ResourceNotFoundException e)
			{
				System.err.println("vessel null:"+e.getMessage());
				e.printStackTrace();
			}

		}
		return insertList;
	}

	public List<ScheduleData> getInboundScheduleList() throws Exception
	{
		return getScheduleList(this.index_inbound_fromport,this.index_inbound_toport,ScheduleEnum.INBOUND.getSymbol());
	}
	
	public List<ScheduleData> getOutboundScheduleList() throws Exception
	{		
		return getScheduleList(this.index_outbound_fromport,this.index_outbound_toport,ScheduleEnum.OUTBOUND.getSymbol());
	}

	private String[] getScheduleDdate(String InOutBoundType, String dataArray[],int outFromPortIndex, int outToPortIndex) throws NotSupportedDateTypeException
	{
		// 출발일, 도착일 날짜 지정
		if(InOutBoundType.equals(ScheduleEnum.INBOUND.getSymbol()))
		{
			return ScheduleDateUtil. adjestDateYear(dataArray[outToPortIndex-1], dataArray[outFromPortIndex-1]);
		}
		else
		{
			return ScheduleDateUtil.adjestDateYear(dataArray[outFromPortIndex-1],dataArray[outToPortIndex-1]);
		}
	}

	private TablePort getTablePort(List<TablePort> tablePortList, int index)
	{
		TablePort port1 = new TablePort();

		if(tablePortList.size()==1)
		{
			port1 = tablePortList.get(0);
		}
		else if(tablePortList.size()>1)
		{
			for(int i=0;i<tablePortList.size();i++)
			{
				TablePort port=tablePortList.get(i);

				if(port.getPort_index()==index)
				{
					port1.setPort_name(port.getPort_name());

					port1.addSubPort(port);
				}
			}
		}

		return port1;

	}

	private TablePort getTablePort(int index)
	{
		return getTablePort(this.tablePortList, index);
	}

	protected boolean isTS(ShippersTable tableData)
	{
		return tableData.getTS()!=null&&tableData.getTS().equals("TS");
	}
	
	protected ErrorLog createError(String type, String tableID, String content)
	{
		ErrorLog log = new ErrorLog();

		SimpleDateFormat formatter;

		String pattern = "yyyy년 M월 d일 a h시 m분";

		formatter = new SimpleDateFormat(pattern, new Locale("ko","KOREA"));

		log.setCurrentTime(formatter.format(new Date()));

		log.setType(type);

		log.setTableID(tableID);

		log.setDate(content);

		return log;
	}
	/**스케줄 날짜 타입 확인
	 * @return
	 */
	private boolean isScheduleDataValidation(String outToPortData, String outPortData ) {
		return !outToPortData.equals("-")&&!outPortData.equals("-")&&
				!outToPortData.equals("_")&&!outPortData.equals("_")&&
				!outToPortData.trim().equals("")&&!outPortData.trim().equals("");
	}

	private ScheduleData createScheduleData(ShippersTable table,

			String voyageNum,
			String vesselName,
			String dateF, 
			String dateT,
			String area_code, 
			String area_name, 
			String fromPort, 
			String toPort,
			String inOutType			
			)
	{
		ScheduleData scheduledata =ScheduleData.builder()
				.table_id(table.getTable_id())
				.vessel(vesselName)
				.agent(table.getAgent())
				.company_abbr(table.getCompany_abbr())
				.port(toPort)
				.fromPort(fromPort)
				.area_code(area_code)
				.area_name(area_name.toUpperCase())
				.InOutType(inOutType)
				.voyage_num(voyageNum)
				.n_voyage_num(ScheduleBuildUtil.getNumericVoyage(voyageNum))
				.gubun(table.getGubun())
				.common_shipping(table.getCommon_shipping() ==null?"":table.getCommon_shipping())
				.TS("")
				.ts_date("")
				.ts_port("")
				.ts_vessel("")
				.ts_voyage_num("")
				.inland_date("")
				.inland_port("")
				.DateF(dateF)
				.DateT(dateT)
				.fromDate(dateF)
				.toDate(dateT)
				.date_issue(table.getDate_isusse())
				.c_time("")
				.d_time("")
				.console_cfs("")
				.console_page("")
				.build();

		return scheduledata;
	}
	
	public boolean hasNewBusanAndBusan( int index[])
	{
		List strList = Arrays.stream(index)
							.boxed()       
							.collect(Collectors.toList());

		List list=this.tablePortList.stream()
				.distinct()
				.filter(o ->strList.contains(o.getPort_index()))
				.filter(o -> o.getPort_name().equals(BUSAN)||o.getPort_name().equals(BUSAN_NEW_PORT))
				.collect(Collectors.toList());

		return (list.size()>1);

	}
	class CreateOutboudSchedule
	{
		private int[]	index_outbound_fromport, index_outbound_toport;
		
		public CreateOutboudSchedule(int[] index_outbound_fromport, int [] index_outbound_toport)
		{
			this.index_outbound_fromport = index_outbound_fromport;
			this.index_outbound_toport =index_outbound_toport;
		}
		public String[] getScheduleDdate(String dataArray[], int outFromPortIndex, int outToPortIndex) throws NotSupportedDateTypeException
		{
			return ScheduleDateUtil.adjestDateYear(dataArray[outFromPortIndex-1],dataArray[outToPortIndex-1]);
		}
	}
	
	class CreateInboudSchedule
	{
		private int[]	index_outbound_fromport, index_outbound_toport;
		
		public CreateInboudSchedule(int[] index_outbound_fromport, int [] index_outbound_toport)
		{
			this.index_outbound_fromport = index_outbound_fromport;
			this.index_outbound_toport =index_outbound_toport;
		}
		public String[] getScheduleDdate(String dataArray[], int outFromPortIndex, int outToPortIndex) throws NotSupportedDateTypeException
		{
			return ScheduleDateUtil. adjestDateYear(dataArray[outToPortIndex-1], dataArray[outFromPortIndex-1]);
		}
	}

	
}
