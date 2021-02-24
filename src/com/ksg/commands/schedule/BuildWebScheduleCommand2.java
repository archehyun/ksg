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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ksg.commands.KSGCommand;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.print.logic.quark.XTGManager;
import com.ksg.schedule.ScheduleService;
import com.ksg.schedule.ScheduleServiceImpl;

public class BuildWebScheduleCommand2 implements KSGCommand {
	private BaseService baseService;
	private ScheduleService scheduleService;
	Vector webSchedule;
	protected Logger 			logger = Logger.getLogger(getClass());
	private XTGManager xtgManager;
	public BuildWebScheduleCommand2() {
		baseService = new BaseServiceImpl();
		scheduleService= new ScheduleServiceImpl();
		webSchedule = new Vector();
		xtgManager = new XTGManager();
	}
	public int execute() {
		try {
			logger.debug("execute");
			List li=baseService.getAreaGroupList();
			List li2=baseService.getAreaGroupList();
			Iterator iter1 = li.iterator();
			logger.debug("size:"+li.size());
			while(iter1.hasNext())
			{
				logger.debug("test");
				AreaInfo  info1 = (AreaInfo) iter1.next();
				Iterator iter2 = li2.iterator();
				while(iter2.hasNext())
				{
					AreaInfo  info2 = (AreaInfo) iter2.next();
					
					WebAreaInfo info = new WebAreaInfo();
					info.fInfo=info1;
					info.tInfo=info2;
					webSchedule.add(info);					
				}				
			}
			List scheduleList = scheduleService.getScheduleList();
			Iterator scheduleliter = scheduleList.iterator();
			
			while(scheduleliter.hasNext())
			{
				logger.debug("logg");
				ScheduleData data=(ScheduleData) scheduleliter.next();
				Iterator wetIter = webSchedule.iterator();
				while(wetIter.hasNext())
				{
					WebAreaInfo info = (WebAreaInfo) wetIter.next();
					
					
					
					/**
					 * 오류 발생 가능 성 있음
					 * 항구가 존재하지 않을 경우 오류 발생
					 * 추후 항구가 없을시 발생할수 있는 예외처리를 해야 함!!!!
					 */
					// 스케줄 출발항
					PortInfo fport=baseService.getPortInfo(data.getFromPort());
					if(fport==null)
					{
						JOptionPane.showMessageDialog(null, data.getFromPort()+"가 등록되어있지 않습니다.");
						return RESULT_FAILE;
					}
					AreaInfo from = baseService.getAreaInfo(fport.getPort_area());
					// 스케줄 도착항
					
					PortInfo tport=baseService.getPortInfo(data.getPort());
					
					if(tport==null)
					{
						JOptionPane.showMessageDialog(null, data.getPort()+"가 등록되어있지 않습니다.");
						return RESULT_FAILE;
					}
					AreaInfo to = baseService.getAreaInfo(tport.getPort_area());
					
					int tcode = Integer.parseInt(info.tInfo.getArea_code())/100;
					int fcode = Integer.parseInt(info.fInfo.getArea_code())/100;
					
					int tcode1 = Integer.parseInt(to.getArea_code())/100;
					int fcode1 = Integer.parseInt(from.getArea_code())/100;
					
					if(tcode==tcode1&&fcode==fcode1)
					{
						info.scheduleDataList.add(data);
							
					}
				}
			}
			
			for(int i=0;i<webSchedule.size();i++)
			{
				WebAreaInfo in = (WebAreaInfo) webSchedule.get(i);
				int f = Integer.parseInt(in.fInfo.getArea_code());
				int t = Integer.parseInt(in.tInfo.getArea_code());
				xtgManager.createXTGFile(in.toString(), "web/WW_SYBASE"+f/100+""+t/100);
			}
			
			return RESULT_SUCCESS;
			
		} catch (SQLException e) {

			e.printStackTrace();
			return RESULT_FAILE;
		}
	}
	class WebAreaInfo
	{
		public AreaInfo fInfo;
		public AreaInfo tInfo;
		public Vector scheduleDataList;
		public WebAreaInfo() {
			scheduleDataList = new Vector();
		}
		public String toString()
		{
			StringBuffer buffer = new StringBuffer();
			for(int i=0;i<scheduleDataList.size();i++)
			{
				ScheduleData data=(ScheduleData) scheduleDataList.get(i);
				buffer.append(data.toWebScheduleString()+"\n");
				
			}
				
			return buffer.toString();
		}
	}
	public static void main(String[] args) {
		BuildWebScheduleCommand2  b= new BuildWebScheduleCommand2();
		b.execute();
	}
}
