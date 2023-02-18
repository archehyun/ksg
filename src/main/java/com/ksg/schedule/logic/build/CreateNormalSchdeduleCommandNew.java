/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.schedule.logic.build;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.JDOMException;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.commands.schedule.SwingWorker;
import com.ksg.common.exception.VesselNullException;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.logic.PortIndexNotMatchException;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.joint.ScheduleBuildUtil;
import com.ksg.service.ScheduleService;
import com.ksg.view.ui.ErrorLogManager;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * 코리아쉬핑가제트 항구정보 관리 다이어그램
 * <pre>
 * com.ksg.schedule.logic.build
 *     |_CreateNormalSchdeduleCommandNew.java     
 * </pre>
 * 
 * @date: 2020. 01. 15
 * @version: 1.0
 * @author: 박창현
 * @explanation 선박 스케줄 생성
 * 
 *   	
 * 
 */

@Slf4j
public class CreateNormalSchdeduleCommandNew extends CreateScheduleCommand
{
	/*
	 * 광고 스케줄에서 부산항과 부산 신항이 같이 나타날 경우 기존 부산항 스케줄은 무시
	 *  
	 */
	private static final String BUSAN = "BUSAN";

	private static final String BUSAN_NEW_PORT = "BUSAN NEW";

	private static final String SCHEDULE_BUILD_ERROR_TXT = "schedule_build_error.txt";

	protected Logger logger = LogManager.getLogger(this.getClass());

	private ErrorLogManager errorLogManager = ErrorLogManager.getInstance();

	private int[]	a_inport,a_intoport,a_outport,a_outtoport;

	private String[][] arrayDatas;

	private String outPortData,outToPortData;

	private Vector portDataArray;

	private XTGManager xtgmanager = new XTGManager();

	private String[][] vslDatas;

	private String currentTableID=null;

	private boolean isOutToNewBusanPortScheduleAdd=false;

	private boolean isOutNewBusanPortScheduleAdd=false;

	private int outBusanNewPortIndex;

	private int outToBusanNewPortIndex;

	private boolean isExitOutOldPort=false;

	private 	boolean isExitOutNewPort=false;

	private boolean isExitOutToOldPort=false;

	private boolean isExitOutToNewPort=false;

	SimpleDateFormat format = new SimpleDateFormat("M/d");


	public CreateNormalSchdeduleCommandNew() throws SQLException
	{
		super();
	}
	public CreateNormalSchdeduleCommandNew(ShippersTable op) throws SQLException 
	{
		this();
		searchOption =op;
	}

	public int execute() {

		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				current = 0;
				done = false;
				canceled = false;
				statMessage = null;
				return new ActualTask();
			}
		};
		worker.start();
		return result;
	}

	ArrayList<ScheduleData> insertList = new ArrayList<ScheduleData>();
	/**
	 * @return 스케줄 생성 결과
	 */
	private int makeBildingSchedule() {
		log.debug("<==start build schedule ==>");

		try {
			/************ 전체 테이블 정보 조회  **********
			 * 1. 구분이 NULL 또는 NNN이 아닌 경우를 조회
			 * ********************************************			 
			 */
			log.info("search option:"+searchOption);

			long startTime = System.currentTimeMillis();


			List table_list = this.getTableListByOption();

			setMessageDialog();

			Iterator iter = table_list.iterator();


			while(iter.hasNext())
			{
				ShippersTable tableData = (ShippersTable) iter.next();

				currentTableID = tableData.getTable_id();

				// 테이블 데이터가 유효 한지 검증
				if(isTableDataValidation(tableData))
				{
					log.error("테이블 정보 오류:"+tableData.getTable_id());
					errorlist.add(createError("테이블 정보 오류", tableData.getTable_id(), ""));
					continue;
				}

				processMessageDialog.setMessage("");

				notifyMessage("Make:"+tableData.getCompany_abbr()+","+tableData.getTable_id());

				// 날짜 정보 배열
				arrayDatas = advData.getDataArray();

				// 선박 정보 배열
				vslDatas = advData.getFullVesselArray(isTS(tableData));

				//항구 정보 배열
				/**
				 * 각 항구 인덱스를 이용해서 
				 * 스케줄에서 불러와 배열을 생성함
				 */

				// 아웃바운드 국내항

				try {
					a_outport 	= ScheduleBuildUtil. makePortArraySub(tableData.getOut_port());
					// 인바운드 국내항
					a_inport 	= ScheduleBuildUtil.makePortArraySub(tableData.getIn_port());
					// 아웃바운드 외국항
					a_outtoport	= ScheduleBuildUtil.makePortArraySub(tableData.getOut_to_port());
					// 인바운드 외국항
					a_intoport 	= ScheduleBuildUtil.makePortArraySub(tableData.getIn_to_port());
				}catch(NumberFormatException e)
				{	
					log.error("ID({}) port index number foramt error:", currentTableID, e.getMessage());

					errorlist.add(createError("인덱스 오류", currentTableID, e.getMessage()));

					continue;
				}

				// 국내항
				portDataArray=getPortList(currentTableID);

				for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
				{
					try 
					{
						String vesselName=vslDatas[vslIndex][0];

						Vessel vesselInfo = ScheduleManager.getInstance().searchVessel(vesselName);

						// 사용하지 않는 선박이면 스케줄에서 제외
						if(vesselInfo.getVessel_use()==Vessel.NON_USE)
							continue;

						// 인바운드 스케줄 생성
						makeSchedule(tableData, vslIndex,a_inport,a_intoport,ScheduleService.INBOUND,advData);

						// 아웃바운드 스케줄 생성
						makeSchedule(tableData, vslIndex,a_outport,a_outtoport,ScheduleService.OUTBOUND,advData);
					}

					catch (PortIndexNotMatchException e) 
					{	
						String errorLog =e.table.getCompany_abbr()+"선사의 "+e.table.getPage()+" 페이지,"+e.table.getTable_index()+
								"번 테이블의 스케줄 정보 생성시 문제가 생겼습니다.\n\n항구정보, 항구 인덱스 정보,날짜 형식를 확인 하십시요.\n\n스케줄 생성을 종료 합니다.";
						errorLogManager.setLogger(errorLog);

						log.error(errorLog);
						JOptionPane.showMessageDialog(null, errorLog);

						e.printStackTrace();
						errorlist.add(this.createError("인덱스 오류", tableData.getTable_id(), ""));
						close();						
						return RESULT_FAILE;
					}
					catch(NotSupportedDateTypeException e)
					{
						log.error(e.getMessage());
						close();
						return RESULT_FAILE;
					}catch(VesselNullException e)
					{
						e.printStackTrace();
						close();
						ShippersTable errorTable = tableData;
						log.error(errorTable.toString());
						JOptionPane.showMessageDialog(null, "table id : "+errorTable.getTable_id()+", "+e.getMessage());
						return RESULT_FAILE;
					}
				}



				log.info("size:{}",insertList.size());

				if(!insertList.isEmpty())
				{	
					boolean flag = true;
					if(flag)
					{
						List newList=insertList.stream().filter(distinctByKey(m -> m.toKey())).collect(Collectors.toList());;

						Collection<List<ScheduleData>> partitions = partition(newList, 80);


						for(List items: partitions)
						{
							scheduleService.insertScheduleBulkData(items);	
						}
					}
					else
					{
						insertList.stream().forEach(schedule -> insertScheduleNew(schedule));
					}					
					insertList.clear();

				}

				process++;		
				current++;
			}

			done = true;

			current = lengthOfTask;			

			long endTime = System.currentTimeMillis();

			log.info("스케줄 생성 종료({})",getSecond((endTime-startTime)));

			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "스케줄 생성완료 ("+getSecond((endTime-startTime))+"s)");

			return RESULT_SUCCESS;

		} catch (SQLException e) {

			e.printStackTrace();
			done=false;
			JOptionPane.showMessageDialog(null, e);
			return RESULT_FAILE;
		}catch (Exception e) {
			e.printStackTrace();
			done=false;
			JOptionPane.showMessageDialog(null, "table-ID : "+currentTableID+", error message:"+e.getMessage());
			return RESULT_FAILE;
		}
		finally{
			printError();
			processMessageDialog.setVisible(false);
			processMessageDialog.dispose();
		}

		// 전체

	}

	private static <T> Collection<List<T>> partition(List<T> collection, int n) {
		AtomicInteger counter = new AtomicInteger();
		return collection.stream()
				.collect(Collectors.groupingBy(it -> counter.getAndIncrement() / n))
				.values();
	}
	public static <T> Predicate<T> distinctByKey( Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> map = new HashMap<>();
		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private void close() {
		done=false;
		processMessageDialog.setVisible(false);
		processMessageDialog.dispose();
	}

	private String getSecond(long millis)
	{
		int minutes = (int) ((millis / 1000) / 60 % 60);

		int seconds = (int) ((millis/1000) % 60);
		;
		return String.format("%02d:%02d", minutes, seconds);
	}


	private void printError() {
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<errorlist.size();i++)
		{
			buffer.append(errorlist.get(i).toString()+"\n");
		}
		xtgmanager.createXTGFile(buffer.toString(),SCHEDULE_BUILD_ERROR_TXT);
	}


	private TablePort getPort(Vector arry,int index)
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
	/**
	 * @param table
	 * @return
	 * @throws SQLException 
	 */
	private Vector getPortList(String table_id) throws SQLException 
	{
		portDataArray  = new Vector();


		TablePort tablePort = new TablePort();
		tablePort.setTable_id(table_id);
		tablePort.setPort_type(TablePort.TYPE_PARENT);

		List li=getTablePortList(tablePort);

		for(int i=0;i<li.size();i++)
		{
			TablePort port = (TablePort) li.get(i);

			portDataArray.add(port);
		}
		log.debug("portarray:"+table_id+","+portDataArray);

		return portDataArray;
	}


	/**
	 * @param table
	 * @param table_id
	 * @param voyageNum
	 * @param vesselName
	 * @param dateF
	 * @param dateT
	 * @param area_code
	 * @param area_name
	 * @param gubun
	 * @param company_abbr
	 * @param agent
	 * @param fromPort
	 * @param toPort
	 * @param inOutType
	 * @param common_shipping
	 * @param date_isusse
	 * @param vslIndex
	 */
	private void insertSchedule(ShippersTable table,String table_id,
			String voyageNum,String vesselName,
			String dateF, String dateT,
			String area_code, String area_name, 
			String gubun,String company_abbr,String agent,
			String fromPort, String toPort,String inOutType,
			String common_shipping,String date_isusse, int vslIndex)
	{

		ScheduleData scheduledata = new ScheduleData();
		scheduledata.setTable_id(table_id); // 테이블 아이디
		scheduledata.setVessel(vesselName); // 선박명
		scheduledata.setAgent(agent);		// 에이전트
		scheduledata.setPort(toPort);		// 도착항
		scheduledata.setFromPort(fromPort);		// 도착항
		scheduledata.setDateF(dateF);		// 출발일
		scheduledata.setDateT(dateT);		// 도착일
		scheduledata.setArea_code(area_code);// 지역 코드
		scheduledata.setArea_name(area_name);// 지역 명
		scheduledata.setCompany_abbr(company_abbr);// 선사명
		scheduledata.setVoyage_num(voyageNum);//	항차번호
		scheduledata.setN_voyage_num(ScheduleBuildUtil.getNumericVoyage(voyageNum));// 항차 번호(숫자)
		scheduledata.setInOutType(inOutType);	//in, out 타입
		scheduledata.setGubun(gubun);		//구분
		scheduledata.setCommon_shipping(common_shipping==null?"":common_shipping);//
		scheduledata.setDate_issue(date_isusse);// 입력일

		//구분이 콘솔일 경우
		if(searchOption.getGubun().equals(ShippersTable.GUBUN_CONSOLE))
		{
			scheduledata.setC_time(table.getC_time()==0?"":arrayDatas[vslIndex][table.getC_time()-1]);
			scheduledata.setD_time(table.getD_time()==0?"":arrayDatas[vslIndex][table.getD_time()-1]);
			scheduledata.setConsole_cfs(table.getConsole_cfs());
			scheduledata.setConsole_page(table.getConsole_page());
		}

		try
		{
			scheduleService.insertScheduleData(scheduledata);// DB 에 저장
		}
		catch(SQLException e)
		{

			switch (e.getErrorCode()) {
			case 1062:// 주키 동일
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				//					update++;
				break;
			case 2627:// 주키 동일
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				break;
			default:
				log.error("error:"+scheduledata+", id:"+scheduledata.getTable_id()+", fromPort:"+scheduledata.getFromPort());
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
				.build();
		//구분이 콘솔일 경우
		if(searchOption.getGubun().equals(ShippersTable.GUBUN_CONSOLE))
		{
			scheduledata.setC_time(table.getC_time()==0?"":arrayDatas[vslIndex][table.getC_time()-1]);
			scheduledata.setD_time(table.getD_time()==0?"":arrayDatas[vslIndex][table.getD_time()-1]);
			scheduledata.setConsole_cfs(table.getConsole_cfs());
			scheduledata.setConsole_page(table.getConsole_page());
		}
		else
		{
			scheduledata.setC_time("");
			scheduledata.setD_time("");
			scheduledata.setConsole_cfs("");
			scheduledata.setConsole_page("");
		}
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
	/**
	 * @deprecated
	 * @param table
	 * @param vslIndex
	 * @param InOutBoundType
	 * @param outPortIndex
	 * @param outToPortIndex
	 * @param outPort
	 * @param outToPort
	 * @param adv
	 * @throws SQLException
	 */
	private void insertSchedule(ShippersTable table, int vslIndex,
			String InOutBoundType, int outPortIndex, int outToPortIndex, String outPort, String outToPort,ADVData adv) throws SQLException,ArrayIndexOutOfBoundsException {

		outToPortData = arrayDatas[vslIndex][outToPortIndex-1];

		outPortData = arrayDatas[vslIndex][outPortIndex-1];

		ScheduleData scheduledata = new ScheduleData();

		scheduledata.setTable_id(table.getTable_id());

		if(isScheduleDataValidation())
		{
			// 선박명이 '-' 이면 스케줄 처리 안함

			Vessel op = new Vessel();
			op.setVessel_name(vslDatas[vslIndex][0]);

			for(int i=0;i<NO_VESSEL.size();i++)
			{
				Vessel opItem = (Vessel) NO_VESSEL.get(i);
				if(op.getVessel_name()==null)
				{
					errorlist.add(createError("Vessel Null", scheduledata.getTable_id(), scheduledata.getVessel()));
					return;	
				}

				if(op.getVessel_name().equals(opItem.getVessel_name()))
				{
					log.error("no vessel:"+op.getVessel_name());
					return ; 
				}
			}

			scheduledata.setVessel(vslDatas[vslIndex][0]);
			if(scheduledata.getVessel()==null)
			{
				errorlist.add(createError("Vessel Null", scheduledata.getTable_id(), scheduledata.getVessel()));
				return;
			}

			if(table.getGubun().equals("TS"))
			{
				String vsl[][] = adv.getFullVesselArray(true);
				scheduledata.setTs_vessel(vsl[vslIndex][0]);
				TablePort tablePort = new TablePort();
				// TS 확인
				tablePort.setTable_id(table.getTable_id());
				TablePort info = getTablePort(tablePort);
				scheduledata.setTS(info.getPort_name());


				String date[][]=null;
				try {
					date = adv.getDataArray();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				//scheduledata.setTs_date(date[vslIndex][table.getDirection()-1]);


			}
			scheduledata.setAgent(table.getAgent());

			// 인바운드
			if(InOutBoundType.equals(ScheduleService.INBOUND))
			{
				//inbound 스케줄 처리시 외국출항일이 7/29-7/30일때 늦은날짜인 7/30로 처리되어야 함.
				try 
				{
					scheduledata.setPort(outPort);
					scheduledata.setFromPort(outToPort);
					String dates[]=adjestDateYear(arrayDatas[vslIndex][outToPortIndex-1],arrayDatas[vslIndex][outPortIndex-1],InOutBoundType);

					scheduledata.setDateF(dates[0]);
					scheduledata.setDateT(dates[1]);
					checkDate(table, scheduledata);

				} catch (NotSupportedDateTypeException e) 
				{
					log.error("no match date:"+e.date+", id:"+scheduledata.getTable_id());
					errorlist.add(createError("No match date", scheduledata.getTable_id()+":"+scheduledata.getVessel(), e.date));
					return;
				}
			}
			else
			{
				try 
				{
					scheduledata.setPort(outToPort);
					scheduledata.setFromPort(outPort);

					String dates[]=adjestDateYear(arrayDatas[vslIndex][outPortIndex-1],arrayDatas[vslIndex][outToPortIndex-1],InOutBoundType);

					scheduledata.setDateF(dates[0]);
					scheduledata.setDateT(dates[1]);
					checkDate(table, scheduledata);
				} catch (NotSupportedDateTypeException e)
				{
					log.error("no match date:"+e.date+", id:"+scheduledata.getTable_id());
					errorlist.add(createError("날짜 오류", scheduledata.getTable_id(), e.date));
					return;
				}
			}


			if(scheduledata==null||scheduledata.getPort()==null)
			{
				log.error("schedule null:"+scheduledata);
				errorlist.add(createError("schedule null", scheduledata.getTable_id(), ""));
				return;
			}

			PortInfo  portInfo=getPortInfoByPortName(scheduledata.getPort() );
			if(portInfo!=null)
			{
				scheduledata.setArea_code(portInfo.getArea_code());
				scheduledata.setArea_name(portInfo.getPort_area());
			}else
			{
				PortInfo  portabbrInfo=getPortInfoAbbrByPortName(scheduledata.getPort() );
				if(portabbrInfo!=null)
				{
					scheduledata.setArea_code(portabbrInfo.getArea_code());
					scheduledata.setArea_name(portabbrInfo.getPort_area());
				}else
				{
					scheduledata.setArea_code("0");
				}
			}

		} 
		scheduledata.setCompany_abbr(table.getCompany_abbr());
		scheduledata.setVoyage_num(vslDatas[vslIndex][1]);
		scheduledata.setN_voyage_num(ScheduleBuildUtil.getNumericVoyage(vslDatas[vslIndex][1]));
		scheduledata.setInOutType(InOutBoundType);
		scheduledata.setGubun(table.getGubun());
		scheduledata.setCommon_shipping("");
		scheduledata.setDate_issue(table.getDate_isusse());
		scheduledata.setTable_id(table.getTable_id());

		if(searchOption.getGubun().equals(ShippersTable.GUBUN_CONSOLE))
		{
			scheduledata.setC_time(table.getC_time()==0?"":arrayDatas[vslIndex][table.getC_time()-1]);
			scheduledata.setD_time(table.getD_time()==0?"":arrayDatas[vslIndex][table.getD_time()-1]);
			scheduledata.setConsole_cfs(table.getConsole_cfs());
			scheduledata.setConsole_page(table.getConsole_page());
		}

		try
		{

			if(scheduledata.getFromPort()!=null)
			{
				scheduleService.insertScheduleData(scheduledata);// DB 에 저장
			}

		}
		catch(SQLException e)
		{

			switch (e.getErrorCode()) {
			case 1062:// 주키 동일
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				//					update++;
				break;
			case 2627:// 주키 동일
				try {
					scheduleService.updateScheduleData(scheduledata);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				//					update++;
				break;
			default:
				log.error("error:"+scheduledata+", id:"+scheduledata.getTable_id()+", fromPort:"+scheduledata.getFromPort());
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


	/**스케줄 날짜 타입 확인
	 * @return
	 */
	private boolean isScheduleDataValidation() {
		return !outToPortData.equals("-")&&!outPortData.equals("-")&&
				!outToPortData.equals("_")&&!outPortData.equals("_")&&
				!outToPortData.trim().equals("")&&!outPortData.trim().equals("");
	}

	/**@설명 항구이름을 기준으로 메모리 상에 있는 항구 상세 정보 검색
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	private PortInfo getPortInfoByPortName(String portName) 
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
	/**@설명 항구이름을 기준으로 메모리 상에 있는 항구 약어 정보 검색 
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	private PortInfo getPortInfoAbbrByPortName(String portName) 
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


	

	int oldPortIndex;
	int newPortIndex;
	boolean isNewPort=false;

	private boolean isExitNewPort(String[] array)
	{
		boolean isOldPort=false;
		boolean isNewPort=false;
		for(int i=0;i<array.length;i++)
		{
			if(array[i].equals(BUSAN_NEW_PORT))
			{
				isOldPort= true;
			}
			if(array[i].equals(BUSAN))
			{
				isNewPort= true;
			}
		}

		return isOldPort&&isNewPort;
	}


	private PortInfo getPortInfo(String toPort) throws SQLException
	{
		PortInfo  portInfo=getPortInfoByPortName(toPort);
		if(portInfo== null)
		{
			return getPortInfoAbbrByPortName(toPort);
		}
		else
		{
			return portInfo;
		}		

	}



	/**
	 * @param table
	 * @param vslIndex
	 * @param fromPorts
	 * @param toPorts
	 * @param InOutBoundType
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private void makeSchedule(ShippersTable table, int vslIndex, int[]fromPorts, int[]toPorts,String InOutBoundType,ADVData adv)
			throws ArrayIndexOutOfBoundsException,NullPointerException, ParseException,VesselNullException{
		if(fromPorts==null||toPorts==null)
		{			
			return;
		}

		try{
			String table_id= table.getTable_id();
			String company_abbr=table.getCompany_abbr();
			String agent = table.getAgent();
			String gubun= table.getGubun();
			String date_isusse = table.getDate_isusse();
			String common_shipping = table.getCommon_shipping();
			String dateF=null;
			String dateT=null;
			String area_code=null;
			String area_name=null;
			String vesselName=vslDatas[vslIndex][0];			
			String voyageNum=vslDatas[vslIndex][1];
			String fromPort=null;
			String toPort=null;
			String dates[]= null;



			log.debug("MakeSchedule "+InOutBoundType+ " start:"+table.getTable_id()+","+table.getPage());

			log.debug("table info:"+table.getTable_id()+", vessel name:"+vesselName+","+fromPorts.length+","+toPorts.length);

			boolean isOutFromBusanAndNewBusan=isBusanAndNewBusan(fromPorts, toPorts,TYPE_INBOUND);

			boolean isOutToBusanAndNewBusan=isBusanAndNewBusan(fromPorts, toPorts,TYPE_OUTBOUND);

			for(int fromPortCount=0;fromPortCount<fromPorts.length;fromPortCount++)
			{
				int outPortIndex =fromPorts[fromPortCount];

				// 외국항
				for(int toPortCount=0;toPortCount<toPorts.length;toPortCount++)
				{
					int outToPortIndex = toPorts[toPortCount];

					TablePort _outport = this.getPort(portDataArray, outPortIndex);

					TablePort _outtoport = this.getPort(portDataArray, outToPortIndex);

					String subFromPortArray[]=_outport.getPortArray();

					String subToPortArray[]=_outtoport.getPortArray();

					outToPortData = arrayDatas[vslIndex][outToPortIndex-1];

					outPortData = arrayDatas[vslIndex][outPortIndex-1];

					try{
						//부산 신항 스케줄 확인 아웃 바운드
						format.parse(arrayDatas[vslIndex][outToBusanNewPortIndex-1]);

						isOutToNewBusanPortScheduleAdd=true;
					}
					catch(Exception e)
					{
						isOutToNewBusanPortScheduleAdd=false;
					}

					try{
						//부산 신항 스케줄 확인 인 바운드
						format.parse(arrayDatas[vslIndex][outBusanNewPortIndex-1]);
						
						isOutNewBusanPortScheduleAdd=true;
					}
					catch(Exception e)
					{
						isOutNewBusanPortScheduleAdd=false;
					}

					/*
					 * 부산항만 있을 경우
					 * 부산신항만 있을 경우
					 * 부산항과 신항이 같이 있을 경우
					 * 
					 */

					try 
					{
						// 출발일, 도착일 날짜 지정
						if(InOutBoundType.equals(ScheduleService.INBOUND))
						{
							dates=adjestDateYear(arrayDatas[vslIndex][outToPortIndex-1],arrayDatas[vslIndex][outPortIndex-1],InOutBoundType);
						}
						else
						{
							dates=adjestDateYear(arrayDatas[vslIndex][outPortIndex-1],arrayDatas[vslIndex][outToPortIndex-1],InOutBoundType);
						}
						dateF=dates[0];
						dateT=dates[1];

					} catch (NotSupportedDateTypeException e) 
					{

						// 날자 형태가 "-"라면 계속 속행
						if(!e.date.equals("-"))
						{
							log.error("no match date:"+e.date+", id:"+table_id);
							errorlist.add(createError("날짜 오류", table_id, e.date));	
						}
						continue;
					}

					for(int z =0;z<subFromPortArray.length;z++)
					{
						for(int c =0;c<subToPortArray.length;c++)
						{
							try{

								// 출발항, 도착항 지정
								if(InOutBoundType.equals(ScheduleService.INBOUND))
								{										
									fromPort=subToPortArray[c];
									toPort=subFromPortArray[z];
								}
								else
								{
									fromPort=subFromPortArray[z];
									toPort=subToPortArray[c];									
								}
								PortInfo  fromPortInfo=getPortInfo(fromPort);

								PortInfo  toPortInfo=getPortInfo(toPort);

								if(fromPortInfo== null||toPortInfo== null)
								{
									logger.error("null port error- fromport:{}, toPort:{}, table_id:{}",fromPort, toPort,table_id);
									continue;
								}

								area_code=toPortInfo.getArea_code();
								area_name=toPortInfo.getPort_area();


								/* 부산항과 신항이 같이 있을 경우 부산항 스케줄은 추가 하지 않음
								 * 
								 */

								// outbound 스케줄:
								if(InOutBoundType.equals(ScheduleService.OUTBOUND)&&isOutToBusanAndNewBusan)
								{	
									/* 무시하기 위한 기준
									 * 1. 부산항, 부산신항이 동시에 존재하는 경우
									 * 2. 부산 신항 스케줄이 있는 경우
									 * 오류: 부산항, 부산 신항이 동시에 존재하는 경우 부산 신항 스케줄이 없을 경우에도 부산 스케줄 무시됨 
									 */

									if(fromPort.equals(BUSAN)&&isOutToNewBusanPortScheduleAdd)
									{
										log.info(InOutBoundType+" busan port skip:"+vesselName);
										log.info("new port index:"+outBusanNewPortIndex);
										continue;
									}
								}

								// inbound 스케줄
								if(InOutBoundType.equals(ScheduleService.INBOUND)&&isOutFromBusanAndNewBusan)
								{									
									if(toPort.equals(BUSAN)&&isOutNewBusanPortScheduleAdd)
									{
										log.info(InOutBoundType+" busan port skip:"+vesselName);
										log.info("new port index:"+outToBusanNewPortIndex);
										continue;
									}
								}

								// 날짜 형식이 맞지 않으면 처리 하지 않음
								if(!isScheduleDataValidation())
									continue;



								ScheduleData insertData=	
										createScheduleData(table, 
												table_id, 
												voyageNum, 
												vesselName, 
												dateF, 
												dateT, 
												area_code, 
												area_name,
												gubun,
												company_abbr, 
												agent, 
												fromPort, 
												toPort, InOutBoundType, common_shipping, date_isusse, vslIndex);


								insertList.add(insertData);

							}catch(Exception e)
							{
								e.printStackTrace();
								log.error("error:"+e.getMessage()+",table id:"+table.getTable_id());
								throw new NotSupportedDateTypeException("", 0);
							}
						}
					}

					/******************
					 *에러 발생 될 수 있음				*
					 **/				

				}

			}
			log.debug("MakeSchedule end");
		}	

		catch(Exception e)
		{
			e.printStackTrace();
			throw new PortIndexNotMatchException(table);
		}
	}


	/**
	 * @날짜 2015-10-01
	 * @설명 스케줄에 부산항과 부산신항이 동시에 존재하는지 판단, 부산항 : BUSAN, 부산신항 : BUSAN NEW
	 * @param fromPorts
	 * @param toPort
	 * @return
	 * @throws SQLException
	 * 
	 */
	private boolean isBusanAndNewBusan(int[] fromPorts, int[] toPort, int type)
			throws SQLException {

		isExitOutOldPort=false;
		isExitOutNewPort=false;
		isExitOutToOldPort=false;
		isExitOutToNewPort=false;
		// 항구확인
		for(int fromPortCount=0;fromPortCount<fromPorts.length;fromPortCount++)
		{
			int outPortIndex =fromPorts[fromPortCount];
			for(int toPortCount=0;toPortCount<toPort.length;toPortCount++)
			{
				int outToPortIndex = toPort[toPortCount];
				TablePort _outport = this.getPort(portDataArray, outPortIndex);
				TablePort _outtoport = this.getPort(portDataArray, outToPortIndex);


				PortInfo searchOutOldPort = this.getPortInfo(_outport.getPort_name());
				PortInfo searchOutNewPort = this.getPortInfo(_outport.getPort_name());

				PortInfo searchOutToOldPort = this.getPortInfo(_outtoport.getPort_name());
				PortInfo searchOutToNewPort = this.getPortInfo(_outtoport.getPort_name());				


				if(searchOutOldPort==null||searchOutNewPort==null)
					continue;

				if(searchOutOldPort.getPort_name().equals(BUSAN))
				{
					isExitOutOldPort = true;
				}
				if(searchOutNewPort.getPort_name().equals(BUSAN_NEW_PORT))
				{
					isExitOutNewPort = true;
					outBusanNewPortIndex=outPortIndex;
				}

				if(searchOutToOldPort==null||searchOutToNewPort==null)
					continue;

				if(searchOutOldPort.getPort_name().equals(BUSAN))
				{
					isExitOutToOldPort = true;

				}
				if(searchOutNewPort.getPort_name().equals(BUSAN_NEW_PORT))
				{
					isExitOutToNewPort = true;
					outToBusanNewPortIndex=outPortIndex;
				}
			}
		}


		boolean result=false;
		switch (type) {
		case TYPE_INBOUND:

			result= isExitOutOldPort&&isExitOutNewPort;
			break;

		case TYPE_OUTBOUND:

			result= isExitOutToOldPort&&isExitOutToNewPort;
			break;
		}
		return result;

	}
	private void insertScheduleNew(ScheduleData data)
	{
		try
		{
			scheduleService.insertScheduleData(data);// DB 에 저장
		}
		catch(SQLException e)
		{

			switch (e.getErrorCode()) {
			case 1062:// 주키 동일
				try {
					scheduleService.updateScheduleData(data);
				} catch (SQLException e2) {
					e2.printStackTrace();
				}
				//					update++;
				break;
			case 2627:// 주키 동일
				try {
					scheduleService.updateScheduleData(data);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				//					update++;
				break;
			default:
				log.error("error:"+data+", id:"+data.getTable_id()+", fromPort:"+data.getFromPort());
				e.printStackTrace();
				break;
			}
		}

	}
	class ActualTask {
		ActualTask() {

			result=makeBildingSchedule();
		}
	}




}



