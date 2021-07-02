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
package com.ksg.commands.schedule;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ksg.commands.KSGCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.schedule.ScheduleService;
import com.ksg.schedule.ScheduleServiceImpl;

public class BuildRouteScheduleCommand implements KSGCommand {
	private static final String WORLD_SCHEDULE_ = "WorldSchedule_";
	private ScheduleService scheduleService;
	private BaseService baseService;
	private JTextArea area2;
	BufferedReader bufferedReader=null;
	private String VERSION;
	private String TABLE;
	private String END;
	private String TABLE_SUB;
	private XTGManager xtgManager;
	
	public BuildRouteScheduleCommand() {
		scheduleService = new ScheduleServiceImpl();
		baseService = new BaseServiceImpl();
		try {
			TABLE=readTag("RouteSchedule_Table");
			VERSION=readTag("RouteSchedule_Version");
			END=readTag("RouteSchedule_END");
			TABLE_SUB=readTag("RouteSchedule_TableSub");
			xtgManager = new XTGManager();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private String readTag(String fileName) throws IOException {
		
		String tag=new String();
		try
		{
			bufferedReader = 
				new BufferedReader(new FileReader("./tag/"+fileName));
			String temp;
			tag=bufferedReader.readLine();
			while((temp=bufferedReader.readLine())!=null)
			{
				tag+="\n"+temp;
				
			}
		}
		finally
		{
			if(bufferedReader!=null)
			{
				bufferedReader.close();
			}
				
		}
		return tag;
	}
	public int execute() {
		
		// Search Port Info
		try {
			List li=baseService.getAreaInfoList();
			StringBuffer buffer = new StringBuffer();
			buffer.append(VERSION+"\n\n");
			Iterator iter = li.iterator();
			while(iter.hasNext())
			{
				AreaInfo portInfo=(AreaInfo) iter.next();
				buffer.append(portInfo.getArea_name()+"\n\n\n\n");
				
				// 스케줄(항차 번호 검색)(지역 코드,Outbound)				
				List scheduleList=null;//=scheduleService.getScheduleListByArea(portInfo.getArea_code(),"O",1);
				Iterator scheIter = scheduleList.iterator();
				if(scheduleList.size()>0)
					buffer.append("<B>");
				while(scheIter.hasNext())
				{
					ScheduleData scheduleData = (ScheduleData) scheIter.next();
					//각 항차 번호에 해당하는 선박 검색
					
					String xVessel = scheduleData.getVessel();
					String xVoy = scheduleData.getVoyage_num();
					
					//
					List scheduletempList=scheduleService.getScheduleListByVesselVoy(xVessel,xVoy);
					Iterator tempIter = scheduletempList.iterator();
					
					// 
					buffer.append(TABLE+xVessel+TABLE_SUB+" - "+xVoy+"\t("+scheduleData.getCompany_abbr()+")\t");
					

					Vector inPortList = new Vector();
					Vector outPortList = new Vector();
					
					while(tempIter.hasNext())
					{
						ScheduleData tempData=(ScheduleData) tempIter.next();
						
						addFromPort(inPortList, tempData);						
						addToPort(outPortList, tempData);
						
					}
					for(int i=0;i<inPortList.size();i++)
					{
						ScheduleData data=(ScheduleData) inPortList.get(i);
						buffer.append(data.getFromPort()+" "+data.getDateF());
					}

					for(int i=0;i<outPortList.size();i++)
					{
						ScheduleData data=(ScheduleData) outPortList.get(i);
						buffer.append("  *  "+data.getPort()+"  "+data.getDateT());
					}
					
					buffer.append("\n");

				}
				if(scheduleList.size()>0)
				buffer.append(END+"\n\n");
			}
			createDialog();
			area2.setText(buffer.toString());
			xtgManager.createXTGFile(buffer.toString(), WORLD_SCHEDULE_);
			return RESULT_SUCCESS;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RESULT_FAILE;

		}
	}
	private void addFromPort(Vector inPortList, ScheduleData tempData) {
		boolean isInPort = true;
		for(int i=0;i<inPortList.size();i++)
		{
			ScheduleData data =(ScheduleData) inPortList.get(i);
			if(data.getFromPort().equals(tempData.getFromPort()))
			{
				isInPort=false;
			}
		}
		
		if(isInPort)
			inPortList.add(tempData);
	}
	private void addToPort(Vector inPortList, ScheduleData tempData) {
		boolean isInPort = true;
		for(int i=0;i<inPortList.size();i++)
		{
			ScheduleData data =(ScheduleData) inPortList.get(i);
			if(data.getPort().equals(tempData.getPort()))
			{
				isInPort=false;
			}
		}
		
		if(isInPort)
			inPortList.add(tempData);
	}
	private void createDialog() {
		JDialog dialog = new JDialog(KSGModelManager.getInstance().frame);
		area2 = new JTextArea();
		dialog.setTitle("항로별 스케줄 생성 테스트");
		dialog.getContentPane().add(new JScrollPane(area2),BorderLayout.CENTER);
		dialog.setSize(800, 500);
		ViewUtil.center(dialog, false);
		dialog.setVisible(true);
	}
	


}
