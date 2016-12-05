package com.ksg.commands.schedule.create;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.dao.impl.ScheduleService;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Table_Port;
import com.ksg.domain.Vessel;
import com.ksg.schedule.build.PortIndexNotMatchException;
import com.ksg.schedule.build.ScheduleManager;
import com.ksg.schedule.build.VesselNullException;

/**인랜드 스케줄 생성
 * @author archehyun
 *
 */
public class CreateInlandScheduleCommand extends CreateScheduelCommand{

	ScheduleManager scheduleManager = ScheduleManager.getInstance();
	private String[][] arrayDatas; 	// 날짜 정보 배열
	private String[][] vslDatas;	// 선박 정보 배열
	private int[] a_outbound_port_index; 		// 
	private int[] a_inbound_port_index; 		//
	private int[] a_outbound_toport_index; 		//
	private int[] a_inbound_toport_index; 		//
	private int[] a_inland_port_index; 		//
	private Vector portDataArray;
	private String scheduleDates[];
	private String scheduleDates2[];
	private String toPortStr;
	private String fromPortStr;
	private String inlnadPortStr;
	private String inlnad_date;
	private Vessel vesselInfo;
	private ShippersTable tableData;
	public CreateInlandScheduleCommand(ShippersTable searchOption) throws SQLException {
		super();
		this.searchOption = searchOption;
	}

	
	public int execute() {
		try {
			logger.debug("search option:"+searchOption);
			List table_list = this.getTableListByOption();
			logger.debug("table Size:"+table_list.size());
			Iterator tableIter = table_list.iterator();
			while(tableIter.hasNext())
			{
				tableData = (ShippersTable) tableIter.next();
				logger.debug("스케줄 처리:"+tableData);
				// 해당 테이블에 대한 광고정보 조회

				if(isTableDataValidation(tableData))
				{
					errorlist.add(createError("테이블 정보 오류", tableData.getTable_id(), ""));
					continue;
				}

				// 날짜 정보 배열 생성
				arrayDatas 				= advData.getDataArray();

				// 선박 정보 배열 생성
				vslDatas 				= advData.getFullVesselArray(isTS(tableData));

				// 아웃바운드 국내항 인덱스 배열 생성
				a_outbound_port_index	= makePortArraySub(tableData.getOut_port());

				// 인바운드 국내항 인덱스 배열 생성
				a_inbound_port_index 	= makePortArraySub(tableData.getIn_port());

				// 아웃바운드 외국항 인덱스 배열 생성
				a_outbound_toport_index = makePortArraySub(tableData.getOut_to_port());

				// 인바운드 외국항 인덱스 배열 생성
				a_inbound_toport_index 	= makePortArraySub(tableData.getIn_to_port());

				// 기항지 인덱스 배열 생성
				a_inland_port_index= makePortArraySub(tableData.getInland_indexs());

				// 스케줄 조합
				for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
				{					
					vesselInfo = scheduleManager.searchVessel(vslDatas[vslIndex][0]);

					// 인바운드 스케줄 조합
					makeSchedule(tableData, vslIndex,a_inbound_port_index,a_inbound_toport_index,a_inland_port_index,ScheduleService.INBOUND,advData);

					// 아웃바운드 스케줄 조합
					makeSchedule(tableData, vslIndex,a_outbound_port_index,a_outbound_toport_index,a_inland_port_index,ScheduleService.OUTBOUND,advData);
				}
			}
			JOptionPane.showMessageDialog(null, "스케줄 생성 완료");

		}
		catch (VesselNullException e) {
			JOptionPane.showMessageDialog(null, "스케줄 생성 실패:선박명이 없습니다.테이블 id:"+tableData.getTable_id()+", 선박명:"+e.getVesselName() );
			e.printStackTrace();
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(null, "스케줄 생성 실패");
			e.printStackTrace();
		}
		
		return 0;
	}
	/**
	 * @param array
	 * @param index
	 * @return
	 */
	private Table_Port getPort(Vector array,int index)
	{
		Table_Port port1 = new Table_Port();
		for(int i=0;i<array.size();i++)
		{
			Table_Port port=(Table_Port) array.get(i);
			// 포트 인덱스가 같으면
			if(port.getPort_index()==index)
				port1.addSubPort(port);
		}
		return port1;

	}	
	/**
	 * @param table 테이블 정보
	 * @param vslIndex 선박 인덱스
	 * @param ports
	 * @param outPort
	 * @param InOutBoundType
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private void makeSchedule(ShippersTable table, int vslIndex, int[]ports, int[]outPort, int[] inlnadPortIndex,String InOutBoundType,ADVData adv) throws ArrayIndexOutOfBoundsException,NullPointerException, ParseException{
		if(ports==null||outPort==null)
		{			
			return;
		}
		logger.debug("vslIndex:"+vslIndex);

		try{
			// 국내항
			portDataArray=getPortList(table);


			for(int outPortIndex=0;outPortIndex<ports.length;outPortIndex++)
			{
				logger.debug("outPortIndex:"+outPortIndex);
				//  국내항 출발 항 인덱스 조회

				// 외국항
				for(int outToPortIndex=0;outToPortIndex<outPort.length;outToPortIndex++)
				{

					Table_Port fromPort = this.getPort(portDataArray, ports[outPortIndex]);
					Table_Port toPort = this.getPort(portDataArray, outPort[outToPortIndex]);


					String fromPortArray[]	= fromPort.getPortArray();
					String toPortArray[]	= toPort.getPortArray();


					for(int fromPortIndex =0;fromPortIndex<fromPortArray.length;fromPortIndex++)
					{
						for(int toPortIndex =0;toPortIndex<toPortArray.length;toPortIndex++)
						{
							try{


								// 기항지 순회
								for(int inlnadIndex=0;inlnadIndex<inlnadPortIndex.length;inlnadIndex++)
								{
									Table_Port inlnadPorts = this.getPort(portDataArray, inlnadPortIndex[inlnadIndex]);
									String inlnadPortArray[]	= inlnadPorts.getPortArray();

									for(int inlnadPortNum=0;inlnadPortNum<inlnadPortArray.length;inlnadPortNum++)
									{	
										//출발항, 도착항 지정
										if(InOutBoundType.equals(ScheduleService.INBOUND))
										{
											if(arrayDatas[vslIndex][outPort[outPortIndex]-1].equals("-")||arrayDatas[vslIndex][ports[outToPortIndex]-1].equals("-"))
												continue;

											toPortStr =fromPortArray[fromPortIndex];// 출발항
											fromPortStr =toPortArray[toPortIndex];// 도착항											
											scheduleDates = adjestDateYear(arrayDatas[vslIndex][outPort[outPortIndex]-1],arrayDatas[vslIndex][ports[outToPortIndex]-1],InOutBoundType);
											scheduleDates = adjestDateYear2(arrayDatas[vslIndex][outPort[outPortIndex]-1],arrayDatas[vslIndex][ports[outToPortIndex]-1],InOutBoundType);
										}
										else if(InOutBoundType.equals(ScheduleService.OUTBOUND))
										{
											//											하이픈 처리된 스케줄은 패스
											if(arrayDatas[vslIndex][ports[outPortIndex]-1].equals("-")||arrayDatas[vslIndex][outPort[outToPortIndex]-1].equals("-"))
												continue;
											fromPortStr =fromPortArray[fromPortIndex];	// 출발항
											toPortStr=toPortArray[toPortIndex];			// 도착항											
											scheduleDates =adjestDateYear(arrayDatas[vslIndex][ports[outPortIndex]-1],arrayDatas[vslIndex][outPort[outToPortIndex]-1],InOutBoundType);
										}

										//하이픈 처리된 스케줄은 패스
										if(arrayDatas[vslIndex][inlnadPortIndex[inlnadIndex]-1].equals("-"))
											continue;
										// 기항지 설정
										inlnadPortStr 			= inlnadPortArray[inlnadPortNum];
										// 기항 일자 설정
										inlnad_date				= arrayDatas[vslIndex][inlnadPortIndex[inlnadIndex]-1];
										int inland_MonthAndDay[]=getETD(inlnad_date);
										int inland_year = selectYear(currentMonth, inland_MonthAndDay[0], Integer.valueOf(currentYear));

										// 항차번호(문자)
										String voyage_num_str 	= vslDatas[vslIndex][1];
										// 항차번호(숫자)
										int voyage_num 			= getNumericVoyage(vslDatas[vslIndex][1]);
										// 지역 정보 생성
										PortInfo  portInfo=getPortInfo(toPortStr);

										ScheduleData scheduledata = new ScheduleData();
										scheduledata.setTable_id(table.getTable_id());  		// 테이블 아이디
										scheduledata.setGubun(table.getGubun());				// 구분
										scheduledata.setVessel(vslDatas[vslIndex][0]);			// 선박 명
										scheduledata.setVessel_type(vesselInfo.getVessel_type());
										scheduledata.setCompany_abbr(table.getCompany_abbr());	// 선사 약어
										scheduledata.setVoyage_num(voyage_num_str); 			// 항차 번호(문자)
										scheduledata.setN_voyage_num(voyage_num);				// 항차 번호(숫자)
										scheduledata.setInOutType(InOutBoundType); 				// in, out 타입			 							
										scheduledata.setCommon_shipping("");					// 공동배선 정보
										scheduledata.setDate_issue(table.getDate_isusse());		// 스케줄 생성 일
										scheduledata.setAgent(table.getAgent());				// 에이전트 정보									
										scheduledata.setArea_code(portInfo.getArea_code()); 	// 지역 코드
										scheduledata.setArea_name(portInfo.getPort_area());		// 지역 이름										
										scheduledata.setFromPort(fromPortStr); 					// 출발항
										scheduledata.setInland_port(inlnadPortStr); 			// 기항지 항구명
										scheduledata.setPort(toPortStr);						// 도착항
										scheduledata.setInland_date(inland_year+"/"+inland_MonthAndDay[0]+"/"+inland_MonthAndDay[1]);				// 기항일자
										scheduledata.setDateF(scheduleDates[0]); 				// 출발일
										scheduledata.setDateT(scheduleDates[1]); 				// 도착일
										
										//scheduledata.setDateFBack(scheduleDates2[0]);
										//scheduledata.setDateTBack(scheduleDates2[1]);
										try
										{
											logger.info("insert Schedule:"+scheduledata.toInlandScheduleString());
											scheduleService.insertInlandScheduleData(scheduledata);// DB 에 저장

										}
										catch(SQLException e)
										{
											switch (e.getErrorCode()) {
											case 1062:// 주키 동일
												try {
													scheduleService.updateScheduleData(scheduledata);
												} catch (SQLException e2) {
													// TODO Auto-generated catch block
													e2.printStackTrace();
												}
												//					update++;
												break;
											case 2627:// 주키 동일
												try {
													scheduleService.updateScheduleData(scheduledata);
												} catch (SQLException e1) {
													// TODO Auto-generated catch block
													e1.printStackTrace();
												}
												//					update++;
												break;
											default:
												logger.error("error:"+scheduledata+", id:"+scheduledata.getTable_id()+", fromPort:"+scheduledata.getFromPort());
												e.printStackTrace();
												break;
											}
										}
										catch(Exception e)
										{
											e.printStackTrace();
											errorlist.add(createError(e.getMessage(), scheduledata.getTable_id(), ""));
										}
									}
								}
							}
							// SKIP 날짜 오류
							catch(NotSupportedDateTypeException e)
							{
								e.printStackTrace();
								continue;
							}
							catch(Exception e)
							{
								e.printStackTrace();
								throw new NotSupportedDateTypeException("", 0);
							}
						}
					}

					/******************
					 *에러 발생 될 수 있음				*
					 **/				

				}
				logger.debug("MakeSchedule end");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new PortIndexNotMatchException(table);
		}
	}


	/**
	 * @param table
	 * @return
	 */
	private Vector getPortList(ShippersTable table) 
	{
		portDataArray  = new Vector();
		try {

			Table_Port tablePort = new Table_Port();
			tablePort.setTable_id(table.getTable_id());
			tablePort.setPort_type(Table_Port.TYPE_PARENT);

			List li=getTablePortList(tablePort);

			for(int i=0;i<li.size();i++)
			{
				portDataArray.add((Table_Port) li.get(i));
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return portDataArray;
	}	
	/**
	 * @param field
	 * @param table
	 * @return
	 */
	private int[] makePortArraySub(String field) {
		if(field==null||field.equals("")||field.equals(" "))
			return null;

		field=field.trim();
		StringTokenizer st = new StringTokenizer(field,"#");

		Vector<Integer> indexList = new Vector<Integer>();
		while(st.hasMoreTokens())
		{
			try
			{
				indexList.add(Integer.parseInt(st.nextToken().trim()));
			}
			catch (NumberFormatException e) 
			{
				/*logger.error("number foramt error:"+field+",id:"+table.getTable_id());
				errorlist.add(createError("인덱스 오류", table.getTable_id(), field));*/

				continue;
			}
		}

		int array[] =new int[indexList.size()];
		for(int i=0;i<indexList.size();i++)
		{
			array[i]=indexList.get(i);
		}		

		return array;

	}


	/**@설명 항구이름을 기준으로 메모리 상에 있는 항구 상세 정보 검색
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	private PortInfo getPortInfoByPortName(String portName) throws SQLException
	{
		Iterator<PortInfo> iter = portList.iterator();
		while(iter.hasNext())
		{
			PortInfo info = iter.next();
			if(info.getPort_name().equals(portName))
				return info;
		}
		return null;
	}
	private PortInfo getPortInfo(String portName) throws SQLException
	{
		PortInfo  portInfo=getPortInfoByPortName(portName );
		if(portInfo!=null)
		{
			return portInfo;

		}else
		{
			PortInfo  portabbrInfo=getPortInfoAbbrByPortName(portName );
			if(portabbrInfo!=null)
			{
				return portabbrInfo;

			}else
			{
				PortInfo temp = new PortInfo();
				temp.setArea_code("0");
				return temp;
			}
		}
	}
	private PortInfo getPortInfoAbbrByPortName(String portName) throws SQLException
	{
		Iterator<PortInfo> iter = portAbbrList.iterator();
		while(iter.hasNext())
		{
			PortInfo info = iter.next();
			if(info.getPort_abbr().equals(portName))
				return info;
		}
		return null;
	}


}
