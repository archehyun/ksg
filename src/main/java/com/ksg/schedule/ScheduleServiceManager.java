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
package com.ksg.schedule;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

import com.dtp.api.schedule.create.CreateNormalSchdeduleCommandNew2;
import com.ksg.commands.IFCommand;
import com.ksg.commands.schedule.BuildInboundCommand;
import com.ksg.commands.schedule.BuildWebSchdeduleCommand;
import com.ksg.commands.schedule.BuildXMLOutboundCommand;
import com.ksg.commands.schedule.create.CreateInlandScheduleCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.ShippersTable;
import com.ksg.schedule.logic.ScheduleManager;
import com.ksg.workbench.schedule.dialog.ScheduleBuildMessageDialog;
import com.ksg.workbench.schedule.dialog.ScheduleCreateOptionDialog;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 박창현
 *
 */
@Slf4j
public class ScheduleServiceManager {
	
	
	public static final int NOMAL=0;

	public static final int WEB=1;
	
	
	private static ScheduleServiceManager serviceManager;
	
	private Timer timer;
	
	private JProgressBar bar =new JProgressBar();
	
	public static ScheduleServiceManager getInstance() {
		
		if(serviceManager==null) serviceManager = new ScheduleServiceManager();
		
		return serviceManager;
	}
	private ScheduleCreateOptionDialog optionDialog;

	public void buildInboundSchedule()throws SQLException
	{
		new Thread(){

			public void run()
			{
				timer = new Timer(1000,new ActionListener(){


					public void actionPerformed(ActionEvent e) 
					{
						//TODO 로긴시 해야할 일 작성

					}});
				bar.setIndeterminate(true);
				timer.start();
				
				
				JDialog dialog = new JDialog();
				JPanel panel = new JPanel();
				panel.add(new JLabel("Inbound스케줄 생성 중"));
				panel.add(bar);
				dialog.setSize(400, 200);
				ViewUtil.center(dialog, false);
				dialog.setVisible(true);
				dialog.getContentPane().add(panel,BorderLayout.CENTER);
				BuildInboundCommand buildInboundCommand = new BuildInboundCommand();
				buildInboundCommand.execute();
				dialog.setVisible(false);
				dialog.dispose();
				timer.stop();
			}
		}.start();

	}
	/**
	 * @throws SQLException
	 */
	public void buildOutboundSchedule() throws SQLException
	{
		new Thread(){
			public void run()
			{
				timer = new Timer(1000,new ActionListener(){


					public void actionPerformed(ActionEvent e) 
					{
						//TODO 로긴시 해야할 일 작성

					}});
				bar.setIndeterminate(true);
				timer.start();
				JDialog dialog = new JDialog(KSGModelManager.getInstance().frame);
				JPanel panel = new JPanel();
				panel.add(new JLabel("Outbound스케줄 생성 중"));
				panel.add(bar);
				dialog.setSize(400, 200);
				ViewUtil.center(dialog, false);
				dialog.setVisible(true);
				dialog.getContentPane().add(panel,BorderLayout.CENTER);
				BuildXMLOutboundCommand buildOutboundCommand = new BuildXMLOutboundCommand();
				buildOutboundCommand.execute();
				dialog.setVisible(false);
				dialog.dispose();
				timer.stop();
			}
		}.start();
	}

	public String getDate(String da)
	{
		if(da.length()!=8) JOptionPane.showMessageDialog(null, "입력자리수가 다릅니다.");
		
		String year		= da.substring(0,4);
		
		String month	= da.substring(4,6);
		
		String day		= da.substring(6,8);
		
		return year+"-"+month+"-"+day;
	}
	
	/**
	 * @throws SQLException
	 */
	public void buildSchedule() throws SQLException
	{
		optionDialog = new ScheduleCreateOptionDialog(ScheduleCreateOptionDialog.NOMAL);
		optionDialog.createAndUpdateUI();
	}

	
	/**
	 * @throws SQLException
	 */
	public void buildWebSchedule() throws SQLException
	{
		optionDialog = new ScheduleCreateOptionDialog(ScheduleCreateOptionDialog.WEB);
		optionDialog.createAndUpdateUI();
	}
	
	
	/**
	 * @param isOption 옵션 여부
	 * @param isPage 옵션 페이지
	 * @param optionData 옵션 데이터
	 * @param inputDate 입력일자
	 * @param gubun 구분
	 * @param scheduleType 스케줄 타입
	 */
	public void startScheduleMake(final boolean isOption, final boolean isPage,
			final String optionData, final String inputDate,final String gubun, final int scheduleType) {
		

		new Thread(){
			public void run()
			{
				try 
				{
					ShippersTable op = new ShippersTable();
					
					op.setDate_isusse(inputDate);
					
					if(isOption)
					{
						if(isPage)
						{
							op.setPage(Integer.parseInt(optionData));
						}else
						{
							op.setCompany_abbr(optionData);
						}
					}
					
					op.setGubun(gubun);
					
					ScheduleManager.getInstance().initMasterData();
					
					IFCommand command=null;
					
					switch (scheduleType) {
					case NOMAL:
						if(op.getGubun().equals(ShippersTable.GUBUN_INLAND))
						{
							command = new CreateInlandScheduleCommand(op);
						}else
						{
							command = new CreateNormalSchdeduleCommandNew2(op);								
						}
						break;
					case WEB:
						
						ScheduleBuildMessageDialog di = new ScheduleBuildMessageDialog ();
						
						command = new BuildWebSchdeduleCommand(di,op);
						
						break;
					default:
						break;
					}
					command.execute();

				}
				
				catch (Exception e1) 
				{
					e1.printStackTrace();
					
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e1.getMessage());
					
					return ;
				}
			}
		}.start();
	}
}
