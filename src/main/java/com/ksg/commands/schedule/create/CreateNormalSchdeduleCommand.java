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
package com.ksg.commands.schedule.create;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.JDOMException;

import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.commands.schedule.SwingWorker;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ADVData;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.TablePort;
import com.ksg.domain.Vessel;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.logic.PortIndexNotMatchException;
import com.ksg.schedule.logic.build.CreateScheduleCommand;
import com.ksg.service.ScheduleService;
import com.ksg.view.ui.ErrorLogManager;
/**
 * @deprecated
 * @author archehyun
 *
 */
public class CreateNormalSchdeduleCommand extends CreateScheduleCommand
{
	private static final String SCHEDULE_BUILD_ERROR_TXT = "schedule_build_error.txt";

	private ErrorLogManager errorLogManager = ErrorLogManager.getInstance();

	private int[]	a_inport,a_intoport,a_outport,a_outtoport;

	private String[][] arrayDatas;

	private String outPortData,outToPortData;

	private Vector portDataArray;

	private XTGManager xtgmanager = new XTGManager();

	private String[][] vslDatas;

	public CreateNormalSchdeduleCommand(ShippersTable op) throws SQLException 
	{
		super();
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


	private int makeBildingSchedule() {
		logger.debug("<==start build schedule ==>");

		try {
			/************ 전체 테이블 정보 조회  **********
			 * 1. 구분이 NULL 또는 NNN이 아닌 경우를 조회
			 * ********************************************			 
			 */
			logger.info("search option:"+searchOption);
			this.setTime(startTime);
			List table_list = this.getTableListByOption();
			setMessageDialog();
			Iterator iter = table_list.iterator();

			while(iter.hasNext())
			{
				ShippersTable tableData = (ShippersTable) iter.next();

				// 테이블 데이터가 유효 한지 검증
				if(isTableDataValidation(tableData))
				{
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
				 * 스케줄에서 불러오 배열을 생성함
				 */
				
				// 아웃바운드 국내항
				a_outport=makePortArraySub(tableData.getOut_port(),tableData);
				// 인바운드 국내항
				a_inport =makePortArraySub(tableData.getIn_port(),tableData);
				// 아웃바운드 외국항
				a_outtoport=makePortArraySub(tableData.getOut_to_port(),tableData);
				// 인바운드 외국항
				a_intoport =makePortArraySub(tableData.getIn_to_port(),tableData);				

				for(int vslIndex=0;vslIndex<vslDatas.length;vslIndex++)
				{
					try 
					{
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
						JOptionPane.showMessageDialog(null, errorLog);

						e.printStackTrace();
						errorlist.add(this.createError("인덱스 오류", tableData.getTable_id(), ""));
						close();						
						return RESULT_FAILE;
					}
					catch(NotSupportedDateTypeException e)
					{
						close();
						return RESULT_FAILE;
					}finally
					{
						printError();
					}
				}
				process++;		
				current++;
			}

			done = true;
			current = lengthOfTask;	

			logger.info("<==build schedule end==>");
			this.setTime(endtime);
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "스케줄 생성완료 ("+(endtime-startTime)+"ms)");
			return RESULT_SUCCESS;

		} catch (SQLException e) {

			e.printStackTrace();
			done=false;
			return RESULT_FAILE;
		}catch (Exception e) {
			e.printStackTrace();

			done=false;
			JOptionPane.showMessageDialog(null, "error:"+e.getMessage());
			return RESULT_FAILE;
		}
		finally{
			printError();
			processMessageDialog.setVisible(false);
			processMessageDialog.dispose();
		}

		// 전체
	}


	private void close() {
		done=false;
		processMessageDialog.setVisible(false);
		processMessageDialog.dispose();
	}






	private void printError() {
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<errorlist.size();i++)
		{
			buffer.append(errorlist.get(i).toString()+"\n");
		}
		xtgmanager.createXTGFile(buffer.toString(),SCHEDULE_BUILD_ERROR_TXT);
	}
	/**
	 * 
	 * 
	 * outbound 스케줄 처리시 국내출항일이 8/4-8/5일때 늦은날짜인 8/5로 처리되어야 함
	 * @param dateF
	 * @param inOutBoundType 
	 * @return
	 * @throws NotSupportedDateTypeException 
	 * @throws ParseException
	 */

	private TablePort getPort(Vector arry,int index)
	{
		TablePort port1 = new TablePort();
		for(int i=0;i<arry.size();i++)
		{
			TablePort port=(TablePort) arry.get(i);
			if(port.getPort_index()==index)
				port1.addSubPort(port);
		}
		return port1;

	}
	/**
	 * @param table
	 * @return
	 */
	private Vector getPortList(ShippersTable table) 
	{
		portDataArray  = new Vector();
		try {

			TablePort tablePort = new TablePort();
			tablePort.setTable_id(table.getTable_id());
			tablePort.setPort_type(TablePort.TYPE_PARENT);

			List li=getTablePortList(tablePort);

			for(int i=0;i<li.size();i++)
			{
				TablePort port = (TablePort) li.get(i);

				portDataArray.add(port);
			}
			logger.debug("portarray:"+table.getTable_id()+","+portDataArray);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return portDataArray;
	}
	
	
	private void insertSchedule(String table_id,String voyageNum,String vesselName,String dateF, String dateT,String area_code, String area_name, String gubun)
	{
		
	}
	/**
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
					logger.error("no vessel:"+op.getVessel_name());
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
				//tablePort.setPort_index(table.getDirection());
				tablePort.setTable_id(table.getTable_id());
				TablePort info = getTablePort(tablePort);
				scheduledata.setTs(info.getPort_name());


				String date[][]=null;
				try {
					date = adv.getDataArray();
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JDOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
					logger.error("no match date:"+e.date+", id:"+scheduledata.getTable_id());
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
					logger.error("no match date:"+e.date+", id:"+scheduledata.getTable_id());
					errorlist.add(createError("날짜 오류", scheduledata.getTable_id(), e.date));
					return;
				}
			}


			if(scheduledata==null||scheduledata.getPort()==null)
			{
				logger.error("schedule null:"+scheduledata);
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
		scheduledata.setN_voyage_num(getNumericVoyage(vslDatas[vslIndex][1]));
		//scheduledata.setE_I(InOutBoundType);
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
	/**@설명 항구이름을 기준으로 메모리 상에 있는 항구 약어 정보 검색 
	 * @param portName
	 * @return
	 * @throws SQLException
	 */
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
	

	/**
	 * @param field
	 * @param table
	 * @return
	 */
	private int[] makePortArraySub(String field,ShippersTable table) {
		if(field==null||field.equals("")||field.equals(" "))
			return null;

		logger.debug("table id:"+table.getTable_id()+" field:"+field);
		field=field.trim();

		// #을 기준으로 항구 인덱스를 구분
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
				logger.error("number foramt error:"+field+",id:"+table.getTable_id());
				errorlist.add(createError("인덱스 오류", table.getTable_id(), field));

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

	int oldPortIndex;
	int newPortIndex;
	boolean isNewPort=false;
	
	private int isExitNewPort(String[] array)
	{
		for(int i=0;i<array.length;i++)
		{
			if(array[i].equals("BUSAN"))
			{
				return i;
			}
		}
		
		return -1;
	}
	private int isExitOldPort(String[] array)
	{
		for(int i=0;i<array.length;i++)
		{
			if(array[i].equals("BUSAN"))
			{
				return i;
			}
		}
		
		return -1;
	}

	/**
	 * @param table
	 * @param vslIndex
	 * @param ports
	 * @param outPort
	 * @param InOutBoundType
	 * @throws ArrayIndexOutOfBoundsException
	 */
	private void makeSchedule(ShippersTable table, int vslIndex, int[]ports, int[]outPort,String InOutBoundType,ADVData adv) throws ArrayIndexOutOfBoundsException,NullPointerException, ParseException{
		if(ports==null||outPort==null)
		{			
			return;
		}

		try{
			// 국내항
			portDataArray=getPortList(table);

			logger.info("MakeSchedule start:"+table);
			for(int fromPortCount=0;fromPortCount<ports.length;fromPortCount++)
			{
				int outPortIndex =ports[fromPortCount];

				// 외국항
				for(int toPortCount=0;toPortCount<outPort.length;toPortCount++)
				{
					int outToPortIndex = outPort[toPortCount];

					TablePort _outport = this.getPort(portDataArray, outPortIndex);
					TablePort _outtoport = this.getPort(portDataArray, outToPortIndex);

					String fromPortArray[]=_outport.getPortArray();					
					String toPortArray[]=_outtoport.getPortArray();
					
					oldPortIndex = isExitOldPort(fromPortArray);
					newPortIndex = isExitOldPort(fromPortArray);
					
					
					
					for(int z =0;z<fromPortArray.length;z++)
					{
						for(int c =0;c<toPortArray.length;c++)
						{
							try{
								insertSchedule(table, vslIndex,
										InOutBoundType, outPortIndex, outToPortIndex, fromPortArray[z], toPortArray[c],adv);
							}catch(Exception e)
							{
								e.printStackTrace();
								logger.error("error:"+e.getMessage()+",table id:"+table.getTable_id());
								throw new NotSupportedDateTypeException("", 0);
							}
						}
					}

					/******************
					 *에러 발생 될 수 있음				*
					 **/				

				}
				logger.info("MakeSchedule end");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new PortIndexNotMatchException(table);
		}
	}

	class ActualTask {
		ActualTask() {

			result=makeBildingSchedule();
		}
	}





	


}
