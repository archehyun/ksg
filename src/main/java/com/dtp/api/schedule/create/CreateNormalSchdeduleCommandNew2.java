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
package com.dtp.api.schedule.create;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.dtp.api.util.KSGListUtil;
import com.ksg.commands.schedule.NotSupportedDateTypeException;
import com.ksg.commands.schedule.SwingWorker;
import com.ksg.common.exception.VesselNullException;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ScheduleData;
import com.ksg.domain.ShippersTable;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.logic.PortIndexNotMatchException;
import com.ksg.view.ui.ErrorLogManager;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import lombok.extern.slf4j.Slf4j;
/**
 * 
 * 
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
public class CreateNormalSchdeduleCommandNew2 extends CreateScheduleCommand
{
	/*
	 * 광고 스케줄에서 부산항과 부산 신항이 같이 나타날 경우 기존 부산항 스케줄은 무시
	 *  
	 */

	
	
	private static final String SCHEDULE_BUILD_ERROR_TXT = "schedule_build_error.txt";

	private ErrorLogManager errorLogManager = ErrorLogManager.getInstance();

	private XTGManager xtgmanager = new XTGManager();

	public CreateNormalSchdeduleCommandNew2() throws SQLException
	{
		super();
	}
	public CreateNormalSchdeduleCommandNew2(ShippersTable op) throws SQLException 
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

	/**
	 * @return 스케줄 생성 결과
	 */
	private int makeBuildingSchedule() {
		log.debug("<==start build schedule ==>");

		try {
			/************ 전체 테이블 정보 조회  **********
			 * 1. 구분이 NULL 또는 NNN이 아닌 경우를 조회
			 * ********************************************			 
			 */
			log.info("search option:"+searchOption);

			long startTime = System.currentTimeMillis();

			List<ShippersTable> table_list = this.selectShipperTableListAllByCondition();

			setMessageDialog();

			for(ShippersTable tableDataItem: table_list)
			{
				// 스케줄 생성 초기화
				CreateSchedule createSchedule = new CreateSchedule(); 

				try {

					createSchedule.setShipperTable(tableDataItem);

					processMessageDialog.setMessage("");

					notifyMessage("Make:"+tableDataItem.getCompany_abbr()+","+tableDataItem.getTable_id());

				}catch(NumberFormatException e)
				{
					log.error("ID({}) port index number foramt error:", tableDataItem.getTable_id(), e.getMessage());

					errorlist.add(createError("인덱스 오류", tableDataItem.getTable_id(), e.getMessage()));

					continue;

				}catch(Exception e)				
				{
					e.printStackTrace();
					log.error("error:{} - {}", tableDataItem.getTable_id(), e.getMessage());

					errorlist.add(createError("error", tableDataItem.getTable_id(), e.getMessage()));

					continue;
				}

				// 스케줄 생성
				try 
				{
					List<ScheduleData> inboundList= (ArrayList<ScheduleData>) createSchedule.getInboundScheduleList();
					
					List<ScheduleData> outboundList= (ArrayList<ScheduleData>) createSchedule.getOutboundScheduleList();
					
					inboundList.addAll(outboundList);

					if(inboundList.isEmpty()) continue;

					insertList(tableDataItem.getTable_id(),inboundList);

					log.info("make schedule({}):{}", tableDataItem.getTable_id(), inboundList.size());

					process++;		

					current++;

				}

				catch (PortIndexNotMatchException e) 
				{	
					String errorLog =e.table.getCompany_abbr()+"선사의 "+e.table.getPage()+" 페이지,"+e.table.getTable_index()+
							"번 테이블의 스케줄 정보 생성시 문제가 생겼습니다.\n\n항구정보, 항구 인덱스 정보,날짜 형식를 확인 하십시요.\n\n스케줄 생성을 종료 합니다.";
					errorLogManager.setLogger(errorLog);

					errorlist.add(this.createError("인덱스 오류", tableDataItem.getTable_id(), ""));

					log.error(errorLog);

					throw new RuntimeException(e.getMessage());

				}
				catch (Exception e)
				{
					System.err.println(tableDataItem.getTable_id()+", "+e.getMessage());

					log.error(e.getMessage());

					throw new RuntimeException("table id : "+tableDataItem.getTable_id()+", "+e.getMessage());
				}
			}

			done = true;

			current = lengthOfTask;			

			long endTime = System.currentTimeMillis();

			log.info("스케줄 생성 종료({})",getSecond((endTime-startTime)));

			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "스케줄 생성완료 ("+getSecond((endTime-startTime))+"s)");

			return RESULT_SUCCESS;

		} catch (SQLException e) {

			throw new RuntimeException(e.getMessage());


		}catch (Exception e) {
			e.printStackTrace();
			done=false;
			JOptionPane.showMessageDialog(null, "error message:"+e.getMessage());
			return RESULT_FAILE;
		}

		// 전체

	}
	private void insertList(String table_id, List<ScheduleData> insertList) throws SQLException {

		scheduleService.deleteScheduleById(table_id);

		List<ScheduleData> newList=insertList.stream().filter(KSGListUtil.distinctByKey(m -> m.toKey())).collect(Collectors.toList());;

		Collection<List<ScheduleData>> partitions = KSGListUtil.partition(newList, 50);

		for(List<ScheduleData> items: partitions)
		{
			scheduleService.insertScheduleBulkData(items);
		}
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

		return String.format("%02d:%02d", minutes, seconds);
	}

	private void printError() {

		StringBuffer buffer = new StringBuffer();

		errorlist.forEach(o -> buffer.append(o.toString()+"\n"));

		xtgmanager.createXTGFile(buffer.toString(),SCHEDULE_BUILD_ERROR_TXT);
	}

	class ActualTask {
		ActualTask() {
			try {
				result=makeBuildingSchedule();
			}
			catch(Exception e)
			{	
				JOptionPane.showMessageDialog(null, e.getMessage());
				
				close();
				done=false;
				result= RESULT_FAILE;
			}
			finally {
				printError();
				processMessageDialog.setVisible(false);
				processMessageDialog.dispose();
			}
		}
	}
}