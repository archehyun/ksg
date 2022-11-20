package com.ksg.schedule.logic.web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.exception.PortNullException;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.exception.VesselNullException;
import com.ksg.common.util.KSGDateUtil;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.schedule.logic.ScheduleJoint;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.schedule.logic.joint.DefaultScheduleJoint;
import com.ksg.service.ScheduleService;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;

/**
 * @author 박창현
 *
 */
public class DefaultWebScheduleV2 extends DefaultScheduleJoint {

	private int format_type;

	private IFWebScheduleFormat webScheduleFormat;

	private ScheduleManager scheduleManager = ScheduleManager.getInstance();

	public static final int FORMAT_NOMAL=1;

	public static final  int FORMAT_CONSOLE=2;

	public static final  int FORMAT_INLNAD=3;

	private SimpleDateFormat dateTypeYear = new SimpleDateFormat("yyyy");

	private SimpleDateFormat dateTypeMonth = new SimpleDateFormat("MM");

	private static final String BUSAN_PORT = "BUSAN";

	private static final String BUSAN_NEW_PORT = "BUSAN NEW";

	private String currentYear;

	private Vessel vesselInfo;// 선박 정보

	private int currentMonth;	

	private SAXBuilder builder;

	private List<ShippersTable> tableList; // db에서 조회된 테이블 정보 목록

	private ShippersTable parameter; // 파라미터

	private int scheduleID;// 웹 스케줄 생성 인덱스

	private ShippersTable shipperTableData; // 선사 테이블 정보

	private ADVData advData;// xml 형태 광고정보

	private PortInfo toPortInfo,fromPortInfo;

	private ByteArrayInputStream stream;

	private Document document;

	private String tableID;

	private String[][] arrayDatas;

	private Element root;

	private String fromDates[];
	private String toDates[];

	private String[][] vesselArrayDatas;

	private String vesselName;

	private String voyageNum;

	private List<PortInfo> portList,portAbbrList;

	// 지역별 저장을 위한 객첵
	private HashMap<String, Vector<ScheduleData>> areaKeyMap;

	private Vector<TablePort> portDataArray;

	private TablePort fromPortTableInfo;

	private TablePort toTablePortInfo;

	private String errorfileName;

	private String inlnadPortStr;//기항지 항구

	private PortInfo inlnadPortInfo;

	private String[] inlandDates;

	private int inlandPortIndexLists[];

	public DefaultWebScheduleV2(int type,ShippersTable parameter) throws SQLException {
		super();

		logger.info("전송용 웹스케줄 초기화 시작...");

		this.parameter = parameter;

		tableService = DAOManager.getInstance().createTableService();

		advService = DAOManager.getInstance().createADVService();

		builder = new SAXBuilder();		

		areaKeyMap = new HashMap<String, Vector<ScheduleData>>();

		for(int i=1;i<8;i++)
		{
			areaKeyMap.put("0"+i, new Vector<ScheduleData>());
			logger.debug("지역 키 생성:"+"0"+i);
		}		

		portList = baseService.getPortInfoList();

		logger.debug("Port 정보:"+portList.size());

		portAbbrList = baseService.getPort_AbbrList();

		logger.debug("PortAbbr 정보:"+portAbbrList.size());

		currentYear = dateTypeYear.format(new Date());

		currentMonth = Integer.valueOf(dateTypeMonth.format(new Date()));

		logger.debug("입력일 : "+parameter.getDate_isusse()+", 현재 일자:"+currentYear+"."+currentMonth);

		this.format_type = type;
		switch (this.format_type) {
		case FORMAT_NOMAL:
			webScheduleFormat = new NomalWebScheduleFormat(this);

			break;
		case FORMAT_CONSOLE:
			webScheduleFormat = new ConsloeWebScheduleFormat(this);
			logger.info("console format 생성");

			break;
		case FORMAT_INLNAD:
			webScheduleFormat = new InlandWebScheduleFormat(this);

			break;	

		default:
			break;
		}


		this.fileName = webScheduleFormat.getFileName();

		this.errorfileName = webScheduleFormat.getErrorFileName();

		logger.info("전송용 웹스케줄 초기화 종료...");

	}

	/**
	 * @설명 광고 정보가 있는지 확인
	 * @param tableData
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	protected boolean isTableDataValidation(ShippersTable tableData) throws SQLException, ParseException
	{

		advData = (ADVData) advService.getADVData(tableData.getTable_id());
		// 입력된 광고 정보가 없으며 통과
		if(advData==null||advData.getData()==null)
		{
			return true;
		}
		return false;
	}
	/**
	 * 항구 배열에서 항구 정보 추출
	 * 
	 * @param array
	 * @param index
	 * @return
	 */
	private TablePort getTablePort(Vector<TablePort> array,int index)
	{
		TablePort port1 = null;

		for(int i=0;i<array.size();i++)
		{

			TablePort port=(TablePort) array.get(i);
			if(port.getPort_index()==index)
				port1= port;
		}
		return port1;

	}
	/**
	 * @param portName
	 * @return
	 */
	public PortInfo getPortInfoByPortName(String portName)
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

	/**
	 * @param portName
	 * @return
	 */
	public PortInfo getPortInfoAbbrByPortName(String portName)
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
	/**
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
	private PortInfo getPortInfo(String portName)
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
				return null;
			}
		}
	}	

	private int[] extractPortIndex(String strIndex)
	{


		int indexList[];
		try{
			StringTokenizer st = new StringTokenizer(strIndex.trim(),"#");

			ArrayList<Integer> list = new ArrayList<Integer>();

			while(st.hasMoreTokens())
			{
				int indexItem =Integer.parseInt(st.nextToken().trim());
				list.add(indexItem);
			}
			indexList = new int[list.size()];
			for(int i=0;i<list.size();i++)
			{
				indexList[i] = list.get(i);
			}
			logger.debug("extract Port Index:"+strIndex.trim() + ", size:"+indexList.length);
			return indexList;
		}catch(NumberFormatException e)
		{
			indexList = new int[0];
			logger.debug("extract Port Index:"+strIndex.trim() + ", size:"+indexList.length);
			return indexList;
		}

	}

	/** 
	 * 주어진 항구명이 있는지 판단해서 있는 항구면 항구 인덱스를, 아니면 -1을 반환
	 * @param array
	 * @param portName
	 * @return
	 */
	private int isExitPort(Vector<TablePort> array, int indexs[], String portName)
	{
		try{
			for(int i=0;i<indexs.length;i++)
			{
				int portIndex =indexs[i];


				TablePort port=this.getTablePort(array, portIndex);
				PortInfo searchOutOldPort = this.getPortInfo(port.getPort_name());

				if(searchOutOldPort==null)
					return -1;

				if(searchOutOldPort.getPort_name().equals(portName))
					return portIndex;
			}
		}catch(Exception e)
		{
			logger.error(e.getMessage()+",portName:"+portName);
			e.printStackTrace();
			return -1;
		}

		return -1;
	}

	@SuppressWarnings("finally")
	@Override
	public int execute() {

		try {
			logger.info("전송용 스케줄 생성 시작...");

			tableList = tableService.getTableListByDate(parameter);

			lengthOfTask =tableList.size();

			current=0;

			scheduleID =0;

			logger.info("테이블 수: "+lengthOfTask);

			processDialog = new ScheduleBuildMessageDialog(this);

			processDialog.setMessage(fileName+"("+lengthOfTask+"건)");

			processDialog.createAndUpdateUI();

			processDialog.setTask(this);			

			Iterator<ShippersTable> iter = tableList.iterator();

			this.message ="웹 전송용 스케줄 생성 중...";

			while(iter.hasNext())
			{					
				try {
					shipperTableData = iter.next();

					logger.debug("tableID:"+shipperTableData.getTable_id()+", page:"+shipperTableData.getPage()+" 처리");
					// 각 테이블에 대한 정보가 이상이 있을 경우 처리 하지 않음
					if(isTableDataValidation(shipperTableData))
					{
						continue;
					}

					// 테이블에 해당하는 광고 정보
					this.arrayDatas = this.getXMLDataArray(advData.getData());

					// TS 여부
					boolean isTS =shipperTableData.getTS()!=null&&shipperTableData.getTS().equals("TS");

					// 테이블에 해당하는 선박 정보					
					vesselArrayDatas = this.getXMLFullVesselArray(isTS, advData.getData());

					// 각 항구에 해당하는 인덱스 추출
					//HashMap<Integer, Integer> portIndexMap=makePortArrayWebIndexMap(shipperTableData);

					portDataArray=getPortList(shipperTableData);

					tableID = shipperTableData.getTable_id();

					int outboundFromPortIndexList[]=extractPortIndex(shipperTableData.getOut_port());

					int outboundToPortIndexList[]=extractPortIndex(shipperTableData.getOut_to_port());


					int inboundFromPortIndexList[]=extractPortIndex(shipperTableData.getIn_to_port());

					int inboundToPortIndexList[]=extractPortIndex(shipperTableData.getIn_port());
					
					if(format_type==FORMAT_INLNAD)
					{
						inlandPortIndexLists = extractPortIndex(shipperTableData.getInland_indexs());
					}

					// outbound 부산신항이 있는지 판단						
					int outbundBusanNewPortIndex=isExitPort(portDataArray, outboundFromPortIndexList, BUSAN_NEW_PORT);

					// outbound 부산항이 있는지 판단
					int outboundBusanPortIndex=isExitPort(portDataArray, outboundFromPortIndexList, BUSAN_PORT);

					// inbound 부산신항이 있는지 판단						
					int inbundBusanNewPortIndex=isExitPort(portDataArray, inboundToPortIndexList, BUSAN_NEW_PORT);

					// inbound 부산항이 있는지 판단
					int inboundBusanPortIndex=isExitPort(portDataArray, inboundToPortIndexList, BUSAN_PORT);


					logger.debug("BUSAN PORT:"+outboundBusanPortIndex+", BUSAN NEW PORT:"+outbundBusanNewPortIndex);

					for(int vslIndex=0;vslIndex<vesselArrayDatas.length;vslIndex++)
					{	
						try {
							vesselName = vesselArrayDatas[vslIndex][0];


							vesselInfo=scheduleManager.searchVessel(vesselName);
							
							// 사용하지 않는 선박이면 스케줄에서 제외
							if(vesselInfo.getVessel_use()==Vessel.NON_USE)
								continue;

							voyageNum = vesselArrayDatas[vslIndex][1];

							logger.debug("vessel index:"+vslIndex+",선박명:"+vesselName+", voyage:"+voyageNum+" 웹 스케줄 생성" );
							
							
							if(format_type==FORMAT_INLNAD)
							{
								logger.debug("<outbound>");
								makeInlandWebSchedule(ScheduleService.OUTBOUND,outboundFromPortIndexList, outboundToPortIndexList,inlandPortIndexLists,outboundBusanPortIndex, outbundBusanNewPortIndex,
										vslIndex);

								logger.debug("<inbound>");
								makeInlandWebSchedule(ScheduleService.INBOUND,inboundFromPortIndexList, inboundToPortIndexList,inlandPortIndexLists,inboundBusanPortIndex, inbundBusanNewPortIndex,
										vslIndex);
							}
							else
							{
								logger.debug("<outbound>");
								makeWebSchedule(ScheduleService.OUTBOUND,outboundFromPortIndexList, outboundToPortIndexList,outboundBusanPortIndex, outbundBusanNewPortIndex,
										vslIndex);

								logger.debug("<inbound>");
								makeWebSchedule(ScheduleService.INBOUND,inboundFromPortIndexList, inboundToPortIndexList,inboundBusanPortIndex, inbundBusanNewPortIndex,
										vslIndex);
							}

						} catch (ResourceNotFoundException e1) {
							logger.error("vessel name is null:"+vesselName);
							continue;
						}

					}
					logger.debug("tableID:"+shipperTableData.getTable_id()+",page:"+shipperTableData.getPage()+" 처리 종료\n");
					// 오류 발생시 오류 처리 후 다음 테이블 진행
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
				} catch (JDOMException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch(Exception e)
				{
					e.printStackTrace();
					logger.info("tableID:"+shipperTableData.getTable_id()+", error:"+e.getMessage());
				}

				current++;
			}
			try {
				printWebSchedule();
				JOptionPane.showMessageDialog(processDialog, "웹 스케줄 생성 완료");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (SQLException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
			return ScheduleJoint.FAILURE; 

		}
		finally{
			processDialog.setVisible(false);
			done=true;
			if(this.processDialog!=null){
				this.processDialog.setVisible(false);
				this.processDialog.dispose();
			}
		}

		logger.info("전송용 스케줄 생성 종료...");
		return ScheduleJoint.SUCCESS;
	}

	private void makeInlandWebSchedule(String inOutType,int[] fromPortIndexList, int[] toPortIndexList,int[] inlandIndexList,
			int busanPortIndex,int busanNewPortIndex, int vslIndex) throws SQLException
	{
		// 출발항 순회
		for(int fromPortIndex=0;fromPortIndex<fromPortIndexList.length;fromPortIndex++)
		{						
			int fromPortIndexA=fromPortIndexList[fromPortIndex];
			// 출발항 설정
			try{
				// 아웃바운드, 부산항, 부산 신항 둘다 있는 경우 수정
				if(inOutType.equals(ScheduleService.OUTBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
				{	
					// 부산항 인덱스를 부산 신항으로 적용
					/* skip하기 위한 기준
					 * 1. 부산항, 부산신항이 동시에 존재하는 경우
					 * 2. 부산 신항 스케줄이 있는 경우
					 */
					if(fromPortIndexA==busanPortIndex)
					{
						try{

							KSGDateUtil.getDates(arrayDatas[vslIndex][busanNewPortIndex-1],currentMonth,currentYear);
							logger.info("부산신항 스케줄로 인한 부산항 스케줄 skip:"+shipperTableData.getTable_id()+","+busanNewPortIndex+","+arrayDatas[vslIndex][busanNewPortIndex-1]);
							continue;	
						}catch(Exception e)
						{
							// 부산항, 부산신항 둘다 존재 하지만 부산신항의 날짜 정보가 없는 경우 부산항 정보 표시
						}
					}
				}
				
				fromPortTableInfo = this.getTablePort(portDataArray, fromPortIndexA);
				
				// 아웃바운드, 부산항, 부산 신항 둘다 있는 경우 수정
				if(inOutType.equals(ScheduleService.OUTBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
				{
					
					//부산신항 항구명을 부산항으로 변경 
					if(fromPortIndexA==busanNewPortIndex)
					{	
						fromPortTableInfo = this.getTablePort(portDataArray, busanPortIndex);
					}
				}
				
				//항구명이 부산 신항일 경우 부산항으로 변경				
				if(fromPortTableInfo.getPort_name().equals(BUSAN_NEW_PORT))
				{
					fromPortTableInfo.setPort_name(BUSAN_PORT);
				}
				

				if(fromPortTableInfo== null)
				{
					logger.error(shipperTableData.getTable_id()+" 등록되지 않은 항구명 존재, 테이블 정보 및 항구 인덱스 확인, index: "+fromPortIndexA);										
					continue;

				}

				fromPortInfo = scheduleManager.searchPort(fromPortTableInfo.getPort_name());

				logger.debug("\tFrom Port Index:"+fromPortIndexA+" from port name:"+fromPortTableInfo.getPort_name()+", from dates:"+arrayDatas[vslIndex][fromPortIndexA-1]);
			}
			catch(PortNullException e)
			{
				logger.error("tableID: "+shipperTableData.getTable_id()+", from port name null:"+fromPortTableInfo.getPort_name());
				
				continue;
			}
			
			// 출발일 설정
			try{

				/* 사람이 입력한 값은 1부터시작
				 * 프로그램에서는 0부터 시작
				 */
				fromDates = KSGDateUtil.getDates(arrayDatas[vslIndex][fromPortIndexA-1],currentMonth,currentYear);


			}
			catch(VesselDateNotMatchException e)
			{
				//날짜 형식이 다르다면 패스
				//logger.error("outbound 출발일 날짜 형식 오류:"+arrayDatas[vslIndex][outboundFromPort[outboundFromPortIndex]-1]);
				continue;
			}


			//도착항 순회
			for(int toPortIndex=0;toPortIndex<toPortIndexList.length;toPortIndex++)
			{								
				int toPortIndexA=toPortIndexList[toPortIndex];
				// 도착항 설정
				try{

					// 인바운드, 부산항, 부산 신항 둘다 있는 경우
					if(inOutType.equals(ScheduleService.INBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
					{	
						// 부산항 인덱스를 부산 신항으로 적용
						/* skip하기 위한 기준
						 * 1. 부산항, 부산신항이 동시에 존재하는 경우
						 * 2. 부산 신항 스케줄이 있는 경우
						 */
						if(toPortIndexA==busanPortIndex)
						{						
							try{
								KSGDateUtil.getDates(arrayDatas[vslIndex][busanNewPortIndex-1],currentMonth,currentYear);
								logger.info("부산신항 스케줄로 인한 부산항 스케줄 skip:"+shipperTableData.getTable_id()+","+busanNewPortIndex);
								continue;	
							}catch(Exception e)
							{
								// 부산항, 부산신항 둘다 존재 하지만 부산신항의 날짜 정보가 없는 경우 기존 유지

							}
						}
					}

					toTablePortInfo = this.getTablePort(portDataArray, toPortIndexA);
					
					// 인바운드, 부산항, 부산 신항 둘다 있는 경우
					if(inOutType.equals(ScheduleService.INBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
					{
						//부산 신항 항구명을 부산항으로 지정
						if(toPortIndexA==busanNewPortIndex)
						{
							toTablePortInfo = this.getTablePort(portDataArray, busanPortIndex);
						}
					}

					if(toTablePortInfo== null)
					{
						logger.error(shipperTableData.getTable_id()+" 등록되지 않은 항구명 존재, 테이블 정보 확인");										
						continue;
					}
					
					//항구명이 부산 신항일 경우 부산항으로 변경
					if(toTablePortInfo.getPort_name().equals(BUSAN_NEW_PORT))
					{
						toTablePortInfo.setPort_name(BUSAN_PORT);
					}

					toPortInfo = scheduleManager.searchPort(toTablePortInfo.getPort_name());
					
					
				}catch(PortNullException e)
				{
					logger.error("to port name is null:"+toTablePortInfo.getPort_name());
					continue;
				}

				// 도착일 설정
				try {

					logger.debug("\t\tTo Port Index:"+(toPortIndexA)+", port name:"+toTablePortInfo.getPort_name()+", to dates:"+arrayDatas[vslIndex][toPortIndexA-1]);

					toDates = KSGDateUtil.getDates(arrayDatas[vslIndex][toPortIndexA-1],currentMonth,currentYear);

				} catch (VesselDateNotMatchException e) {
					//logger.error("outbound 도착일 날짜 형식 오류:"+arrayDatas[vslIndex][outboundToPort[outboundToPortIndex]-1]);
					//e.printStackTrace();
					continue;
				}				


				// 기항지 순회
				for(int inlnadIndex=0;inlnadIndex<inlandIndexList.length;inlnadIndex++)
				{
					int inlandIndexA=inlandIndexList[inlnadIndex];
					//기항지 항구 설정
					TablePort inlandTablePortInfo = this.getTablePort(portDataArray, inlandIndexA);


					if(inlandTablePortInfo== null)
					{
						logger.error(shipperTableData.getTable_id()+" 등록되지 않은 기항지 항구명 존재, 테이블 정보 확인");										
						continue;
					}

					try{
						inlnadPortInfo = scheduleManager.searchPort(inlandTablePortInfo.getPort_name());
					}catch(PortNullException e)
					{
						logger.error("to port name is null:"+inlandTablePortInfo.getPort_name());
						continue;
					}
					
					// 기항일 설정
					try {

						logger.debug("\t\t\tInland Port Index:"+(inlandIndexA)+", port name:"+inlnadPortInfo.getPort_name()+", to dates:"+arrayDatas[vslIndex][inlandIndexA-1]);

						inlandDates = KSGDateUtil.getDates(arrayDatas[vslIndex][inlandIndexA-1],currentMonth,currentYear);
					} catch (VesselDateNotMatchException e) {
						//logger.error("outbound 도착일 날짜 형식 오류:"+arrayDatas[vslIndex][outboundToPort[outboundToPortIndex]-1]);
						//e.printStackTrace();
						continue;
					}
					insertInlandWebSchedule(inOutType, fromPortInfo, fromDates, vesselInfo, toPortInfo, toDates, voyageNum, inlnadPortInfo.getPort_name(), inlandDates, shipperTableData);
					
				}
			}

		}
	}

	private void makeWebSchedule(String inOutType,int[] fromPortIndexList, int[] toPortIndexList,
			int busanPortIndex,int busanNewPortIndex, int vslIndex) throws SQLException {

		// 출발항 순회
		for(int fromPortIndex=0;fromPortIndex<fromPortIndexList.length;fromPortIndex++)
		{						
			int fromPortIndexA=fromPortIndexList[fromPortIndex];

			// 출발항 설정
			try{
				// 아웃바운드, 부산항, 부산 신항 둘다 있는 경우 수정
				if(inOutType.equals(ScheduleService.OUTBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
				{	
					// 부산항 인덱스를 부산 신항으로 적용
					/* skip하기 위한 기준
					 * 1. 부산항, 부산신항이 동시에 존재하는 경우
					 * 2. 부산 신항 스케줄이 있는 경우
					 */
					if(fromPortIndexA==busanPortIndex)
					{
						try{

							KSGDateUtil.getDates(arrayDatas[vslIndex][busanNewPortIndex-1],currentMonth,currentYear);
							logger.info("부산신항 스케줄로 인한 부산항 스케줄 skip:"+shipperTableData.getTable_id()+","+busanNewPortIndex+","+arrayDatas[vslIndex][busanNewPortIndex-1]);
							continue;	
						}catch(Exception e)
						{
							// 부산항, 부산신항 둘다 존재 하지만 부산신항의 날짜 정보가 없는 경우 부산항 정보 표시
						}
					}
				}

				fromPortTableInfo = this.getTablePort(portDataArray, fromPortIndexA);
				
				// 아웃바운드, 부산항, 부산 신항 둘다 있는 경우
				if(inOutType.equals(ScheduleService.OUTBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
				{
					// 부산 신항 항구 명을 부산항으로 지정
					if(fromPortIndexA==busanNewPortIndex)
					{
						fromPortTableInfo = this.getTablePort(portDataArray, busanPortIndex);
					}
				}

				if(fromPortTableInfo== null)
				{
					logger.error(shipperTableData.getTable_id()+" 등록되지 않은 항구명 존재, 테이블 정보 및 항구 인덱스 확인, index: "+fromPortIndexA);										
					continue;
				}

				fromPortInfo = scheduleManager.searchPort(fromPortTableInfo.getPort_name());

				logger.debug("\tFrom Port Index:"+fromPortIndexA+" from port name:"+fromPortTableInfo.getPort_name()+", from dates:"+arrayDatas[vslIndex][fromPortIndexA-1]);
			}
			catch(PortNullException e)
			{
				logger.error("tableID: "+shipperTableData.getTable_id()+", from port name null:"+fromPortTableInfo.getPort_name());
				System.err.println("from port name null:"+fromPortTableInfo.getPort_name());
				continue;
			}

			// 출발일 설정
			try{

				/* 사람이 입력한 값은 1부터시작
				 * 프로그램에서는 0부터 시작
				 */
				fromDates = KSGDateUtil.getDates(arrayDatas[vslIndex][fromPortIndexA-1],currentMonth,currentYear);


			}
			catch(VesselDateNotMatchException e)
			{
				//날짜 형식이 다르다면 패스
				//logger.error("outbound 출발일 날짜 형식 오류:"+arrayDatas[vslIndex][outboundFromPort[outboundFromPortIndex]-1]);
				continue;
			}



			//도착항 순회
			for(int toPortIndex=0;toPortIndex<toPortIndexList.length;toPortIndex++)
			{	

				// 도착항 인덱스
				int toPortIndexA=toPortIndexList[toPortIndex];

				// 도착항 설정
				try{

					// 인바운드, 부산항, 부산 신항 둘다 있는 경우 수정
					if(inOutType.equals(ScheduleService.INBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
					{	
						// 부산항 인덱스를 부산 신항으로 적용
						/* skip하기 위한 기준
						 * 1. 부산항, 부산신항이 동시에 존재하는 경우
						 * 2. 부산 신항 스케줄이 있는 경우
						 */
						if(toPortIndexA==busanPortIndex)
						{						
							try{
								KSGDateUtil.getDates(arrayDatas[vslIndex][busanNewPortIndex-1],currentMonth,currentYear);
								logger.info("부산신항 스케줄로 인한 부산항 스케줄 skip:"+shipperTableData.getTable_id()+","+busanNewPortIndex);
								continue;	
							}catch(Exception e)
							{
								// 부산항, 부산신항 둘다 존재 하지만 부산신항의 날짜 정보가 없는 경우 기존 유지

							}
						}
					}

					toTablePortInfo = this.getTablePort(portDataArray, toPortIndexA);
					
					// 인바운드, 부산항, 부산 신항 둘다 있는 경우 수정
					if(inOutType.equals(ScheduleService.INBOUND)&&busanPortIndex!=-1&&busanNewPortIndex!=-1)
					{
						// 부산 신항 항구 명을 부산항으로 지정
						if(toPortIndexA==busanNewPortIndex)
						{
						toTablePortInfo = this.getTablePort(portDataArray, busanPortIndex);
						}
					}


					if(toTablePortInfo== null)
					{
						logger.error(shipperTableData.getTable_id()+" 등록되지 않은 항구명 존재, 테이블 정보 확인");										
						continue;
					}

					toPortInfo = scheduleManager.searchPort(toTablePortInfo.getPort_name());
					
				}catch(PortNullException e)
				{
					logger.error("to port name is null:"+toTablePortInfo.getPort_name());
					continue;
				}

				// 도착일 설정
				try {

					logger.debug("\t\tTo Port Index:"+(toPortIndexA)+", port name:"+toTablePortInfo.getPort_name()+", to dates:"+arrayDatas[vslIndex][toPortIndexA-1]);

					toDates = KSGDateUtil.getDates(arrayDatas[vslIndex][toPortIndexA-1],currentMonth,currentYear);
				} catch (VesselDateNotMatchException e) {
					//logger.error("outbound 도착일 날짜 형식 오류:"+arrayDatas[vslIndex][outboundToPort[outboundToPortIndex]-1]);
					//e.printStackTrace();
					continue;
				}

				switch (format_type) {
				case FORMAT_NOMAL:
					insertNomalWebSchedule(inOutType, fromPortInfo, fromDates, vesselInfo, toPortInfo, toDates, voyageNum, shipperTableData);
					break;
				case FORMAT_CONSOLE:

					String closingTimes[] = new String[2];
					closingTimes[0] = shipperTableData.getD_time()==0?"":arrayDatas[vslIndex][shipperTableData.getD_time()-1];
					closingTimes[1] = shipperTableData.getC_time()==0?"":arrayDatas[vslIndex][shipperTableData.getC_time()-1];
					insertConsoleWebSchedule(inOutType, fromPortInfo, fromDates, vesselInfo, toPortInfo, toDates, voyageNum, closingTimes, shipperTableData);
					break;

				default:
					break;
				}
			}
		}
	}
	
	/**콘솔 타입 웹 스케줄 정보 생성
	 * @param inOutType 인바우드/아웃바운드 타입
	 * @param fromPort 출발항 정보
	 * @param fromDates 출발일 정보 (ETA, ETD)
	 * @param vesselInfo 선박 정보
	 * @param toPort 도착항 정보
	 * @param toDates 도착일 정보(ETA, ETD)
	 * @param voyageNum 항차 번호
	 * @param closingTimes Closing Time(DCT,CCT)
	 * @param shipperTableData 테이블 정보
	 */
	private void insertInlandWebSchedule(String inOutType, PortInfo fromPort, String fromDates[], Vessel vesselInfo, PortInfo toPort, String toDates[],String voyageNum,String inlandPort, String[] inlandDates, ShippersTable shipperTableData)
	{
		ScheduleData webScheduleData =  new ScheduleData();

		webScheduleData.setInOutType(inOutType); 								// in, out 타입
		webScheduleData.setFromPort(fromPort.getPort_name());					//출발항
		webScheduleData.setDateF(fromDates[0]);									//출발일 ETA
		webScheduleData.setDateFBack(fromDates[1]);								//출발일 ETD
		webScheduleData.setVessel(vesselInfo.getVessel_name());					//선박명
		webScheduleData.setPort(toPort.getPort_name());							//도착항
		webScheduleData.setDateT(toDates[0]);									//도착일 ETA
		webScheduleData.setDateTBack(toDates[1]);								//도착일 ETD
		webScheduleData.setVoyage_num(voyageNum);								//항차번호
		webScheduleData.setCompany_abbr(shipperTableData.getCompany_abbr());	//Line
		webScheduleData.setAgent(shipperTableData.getAgent());					//에이전트
		webScheduleData.setBookPage(shipperTableData.getBookPage());			//책 페이지
		webScheduleData.setVessel_type(vesselInfo.getVessel_type());			//선박타입
		webScheduleData.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi()); //MMSI		
		webScheduleData.setFromAreaCode(fromPort.getArea_code());				//출발항 지역코드
		webScheduleData.setArea_code(toPort.getArea_code());					//도착항 지역코드
		webScheduleData.setOperator(getOperator(vesselInfo.getVessel_company()));// 선박 대표업체
		webScheduleData.setInland_port(inlandPort);
		webScheduleData.setInland_date(inlandDates[0]);							//기항일 ETA
		webScheduleData.setInland_date_back(inlandDates[1]);					//기항일 ETD
		

		addWebData(webScheduleData);
	}
	

	/**
	 * @param inOutType 인바우드/아웃바운드 타입
	 * @param fromPort 출발항 정보
	 * @param fromDates 출발일 정보 (ETA, ETD)
	 * @param vesselInfo 선박 정보
	 * @param toPort 도착항 정보
	 * @param toDates 도착일 정보(ETA, ETD)
	 * @param voyageNum 항차 번호
	 * @param shipperTableData 테이블 정보
	 */
	private void insertNomalWebSchedule(String inOutType, PortInfo fromPort, String fromDates[], Vessel vesselInfo, PortInfo toPort, String toDates[],String voyageNum, ShippersTable shipperTableData)
	{
		ScheduleData webScheduleData =  new ScheduleData();

		webScheduleData.setInOutType(inOutType); 								// in, out 타입
		webScheduleData.setFromPort(fromPort.getPort_name());					//출발항
		webScheduleData.setDateF(fromDates[0]);									//출발일 ETA
		webScheduleData.setDateFBack(fromDates[1]);								//출발일 ETD
		webScheduleData.setVessel(vesselInfo.getVessel_name());					//선박명
		webScheduleData.setPort(toPort.getPort_name());							//도착항
		webScheduleData.setDateT(toDates[0]);									//도착일 ETA
		webScheduleData.setDateTBack(toDates[1]);								//도착일 ETD
		webScheduleData.setVoyage_num(voyageNum);								//항차번호
		webScheduleData.setCompany_abbr(shipperTableData.getCompany_abbr());	//Line
		webScheduleData.setAgent(shipperTableData.getAgent());					//에이전트
		webScheduleData.setBookPage(shipperTableData.getBookPage());			//책 페이지
		webScheduleData.setVessel_type(vesselInfo.getVessel_type());			//선박타입
		webScheduleData.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi()); //MMSI
		webScheduleData.setOperator(vesselInfo.getVessel_company().contains("/")?vesselInfo.getVessel_company().substring(0,vesselInfo.getVessel_company().indexOf("/")):vesselInfo.getVessel_company());
		webScheduleData.setFromAreaCode(fromPort.getArea_code());
		webScheduleData.setArea_code(toPort.getArea_code());		

		addWebData(webScheduleData);
	}

	/**콘솔 타입 웹 스케줄 정보 생성
	 * @param inOutType 인바우드/아웃바운드 타입
	 * @param fromPort 출발항 정보
	 * @param fromDates 출발일 정보 (ETA, ETD)
	 * @param vesselInfo 선박 정보
	 * @param toPort 도착항 정보
	 * @param toDates 도착일 정보(ETA, ETD)
	 * @param voyageNum 항차 번호
	 * @param closingTimes Closing Time(DCT,CCT)
	 * @param shipperTableData 테이블 정보
	 */
	private void insertConsoleWebSchedule(String inOutType, PortInfo fromPort, String fromDates[], Vessel vesselInfo, PortInfo toPort, String toDates[],String voyageNum,String closingTimes[], ShippersTable shipperTableData)
	{
		ScheduleData webScheduleData =  new ScheduleData();

		webScheduleData.setInOutType(inOutType); 								// in, out 타입
		webScheduleData.setFromPort(fromPort.getPort_name());					//출발항
		webScheduleData.setDateF(fromDates[0]);									//출발일 ETA
		webScheduleData.setDateFBack(fromDates[1]);								//출발일 ETD
		webScheduleData.setVessel(vesselInfo.getVessel_name());					//선박명
		webScheduleData.setPort(toPort.getPort_name());							//도착항
		webScheduleData.setDateT(toDates[0]);									//도착일 ETA
		webScheduleData.setDateTBack(toDates[1]);								//도착일 ETD
		webScheduleData.setVoyage_num(voyageNum);								//항차번호
		webScheduleData.setCompany_abbr(shipperTableData.getCompany_abbr());	//Line
		webScheduleData.setAgent(shipperTableData.getAgent());					//에이전트
		webScheduleData.setBookPage(shipperTableData.getBookPage());			//책 페이지
		webScheduleData.setVessel_type(vesselInfo.getVessel_type());			//선박타입
		webScheduleData.setVessel_mmsi(vesselInfo.getVessel_mmsi()==null?"":vesselInfo.getVessel_mmsi()); //MMSI		
		webScheduleData.setFromAreaCode(fromPort.getArea_code());				//출발항 지역코드
		webScheduleData.setArea_code(toPort.getArea_code());					//도착항 지역코드

		webScheduleData.setOperator(getOperator(vesselInfo.getVessel_company()));// 선박 대표업체
		webScheduleData.setD_time(closingTimes[0]); //DCT
		webScheduleData.setC_time(closingTimes[1]); //CCT		
		webScheduleData.setConsole_cfs(getNewCFS(shipperTableData.getConsole_cfs())); //CFS
		webScheduleData.setConsole_page(shipperTableData.getConsole_page());  // 콘솔 페이지

		addWebData(webScheduleData);
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
	 * @설명 입력된 CFS 정보 중 개행문자를 $로 치환하여 반환
	 * @param cfs 콘솔 CFS 정보
	 * @return
	 */
	private String getNewCFS(String cfs)
	{		
		return cfs.replaceAll("\n", "\\\\&");
	}




	/**
	 * @설명 전체 지역에 대한 웹스케줄 출력
	 * @throws IOException
	 */
	private void printWebSchedule() throws IOException
	{
		logger.info("파일 출력 시작...");

		Set<String> keyList = areaKeyMap.keySet();

		Iterator<String> iter = keyList.iterator();

		while(iter.hasNext())
		{
			String keyArea = iter.next();
			writeWebSchedule(keyArea, areaKeyMap.get(keyArea));
		}

		areaKeyMap.clear();

		logger.info("파일 출력 종료");
	}
	/**
	 * @설명 개별 지역에 대한 출력
	 * @param areaName
	 * @param vesselList
	 * @throws IOException
	 */
	private void writeWebSchedule(String areaName,Vector<ScheduleData> vesselList) throws IOException
	{
		logger.info(areaName+"지역("+vesselList.size()+"건)파일 출력 시작...");

		makeFile(areaName);

		FileWriter fw = new FileWriter(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName+areaName);
		logger.info("file name:"+fileName);


		for(int i=0;i<vesselList.size();i++)
		{
			ScheduleData data = vesselList.get(i);
			//logger.debug(webScheduleFormat.toWebScheduleString(data));

			fw.write(data.getArea_code()+"\t"+scheduleID+"\t"+webScheduleFormat.toWebScheduleString(data)+"\n");

			scheduleID++;
		}

		fw.flush();

		fw.close();

		logger.info(areaName+"지역 파일 출력 종료");
	}


	/**
	 * @param data
	 */
	private void addWebData(ScheduleData data)
	{
		String key =data.getFromAreaCode();
		logger.debug("area code:"+key);
		if(areaKeyMap.containsKey(key))
		{	
			Vector<ScheduleData> area=areaKeyMap.get(key);
			area.add(data);
		}
	}

	/**
	 * @param fileName
	 */
	private void makeFile(String fileName)
	{
		File fo = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION));
		if(!fo.exists())
		{
			fo.mkdir();
		}

		File file = new File(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName);
		if(file.exists())
		{
			if(file.delete())
			{
				logger.info("이전 파일 삭제");
			}
		}

	}
	/**
	 * @param ports
	 * @return
	 */
	private String getPortIndexInfo(String ports)
	{
		if(ports==null)
		{
			return "";
		}
		else
		{
			return ports.trim();
		}
	}
	/**
	 * @param portName
	 * @return
	 */
	public PortInfo getPortAreaCode(String portName){
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
				return null;
			}
		}
	}

	/**
	 *  테이블 아이디를 기준으로 포트 정보 추출
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	public Vector<TablePort> getPortList(ShippersTable table) throws SQLException 
	{
		Vector<TablePort> portDataArray  = new Vector<TablePort>();
		TablePort tablePort = new TablePort();
		tablePort.setTable_id(table.getTable_id());
		tablePort.setPort_type(TablePort.TYPE_PARENT);

		List<TablePort> li=tableService.getTablePortList(tablePort);

		for(int i=0;i<li.size();i++)
		{
			TablePort port = li.get(i);

			portDataArray.add(port);
		}

		return portDataArray;
	}


	/**
	 * @설명 테이블 정보에서 각 항구목록에 대한 인덱스 정보 추출
	 * @param outPortIndex 아웃 바운드 출발항
	 * @param outToPortIndex 아웃바운드 도착항
	 * @param inPortIndex 인바운드 출발항
	 * @param inToPortIndex 인바운드 도착항
	 * @return
	 * @throws NumberFormatException
	 */
	public HashMap<Integer, Integer> makePortArrayWebIndexMap(String outPortIndex, String outToPortIndex,String inPortIndex, String inToPortIndex) throws NumberFormatException{
		String delimiters = "#";
		HashMap<Integer, Integer> indexlist = new HashMap<Integer, Integer>();
		StringTokenizer st = new StringTokenizer(outPortIndex,delimiters);

		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(outToPortIndex,delimiters);
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(inPortIndex,delimiters);
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}

		st = new StringTokenizer(inToPortIndex,delimiters);
		while(st.hasMoreTokens())
		{
			int indexItem =Integer.parseInt(st.nextToken().trim());
			if(!indexlist.containsKey(indexItem))
			{
				indexlist.put(indexItem,indexItem);
			}
		}
		return indexlist;
	}


	/**
	 * @param tableData
	 * @return
	 * @throws NumberFormatException
	 */
	private HashMap<Integer, Integer> makePortArrayWebIndexMap(ShippersTable tableData) throws NumberFormatException{


		String outPortIndex 	= getPortIndexInfo(tableData.getOut_port());
		String outToPortIndex	= getPortIndexInfo(tableData.getOut_to_port());
		String inPortIndex 		= getPortIndexInfo(tableData.getIn_port());
		String inToPortIndex 	= getPortIndexInfo(tableData.getIn_to_port());

		HashMap<Integer, Integer> indexlist = this.makePortArrayWebIndexMap(outPortIndex, outToPortIndex, inPortIndex, inToPortIndex);


		//TS 항구 인덱스  추가
		if(tableData.getGubun().equals("TS"))
		{
			int ts_index=tableData.getTsIndex();

			if(!indexlist.containsKey(ts_index))
			{
				indexlist.put(ts_index, ts_index);
			}
		}

		return indexlist;
	}

	private String[][] getXMLDataArray(String data) throws JDOMException, IOException, OutOfMemoryError
	{
		// error Java heap space

		stream =new ByteArrayInputStream(data.getBytes());

		document= builder.build(stream);

		Element root = document.getRootElement();

		List vessel_list=root.getChildren("vessel");

		String[][] returndata = new String[vessel_list.size()][];

		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);			

			List li=vessel_info.getChildren("input_date");

			returndata[i] = new String[li.size()];

			for(int j=0;j<li.size();j++)
			{
				Element input_date=(Element) li.get(j);

				returndata[i][j]=input_date.getAttributeValue("date").trim();
			}
		}
		return returndata;
	}

	/**
	 * @param ts
	 * @param data
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public String[][] getXMLFullVesselArray(boolean ts,String data) throws JDOMException, IOException
	{	
		stream =new ByteArrayInputStream(data.getBytes());
		document= builder.build(stream);
		root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");
		String[][] returndata = new String[vessel_list.size()][];
		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);
			if(ts){
				String vessel_name = vessel_info.getAttributeValue("ts_name");
				String voyage  = vessel_info.getAttributeValue("ts_voyage");
				returndata[i] = new String[2];
				returndata[i][0] =vessel_name;
				returndata[i][1] =voyage;
			}else
			{
				String vessel_name = vessel_info.getAttributeValue("full-name");

				String voyage  = vessel_info.getAttributeValue("voyage");
				returndata[i] = new String[2];
				returndata[i][0] =vessel_name;
				returndata[i][1] =voyage;
			}
		}
		return returndata;
	}

	@Override
	public void initTag() {
		// TODO Auto-generated method stub
		
	}

}
