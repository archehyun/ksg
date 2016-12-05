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
package com.ksg.dao.impl;

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

import org.apache.log4j.Logger;

import com.ksg.commands.schedule.BuildInboundCommand;
import com.ksg.commands.schedule.BuildRouteScheduleCommand;
import com.ksg.commands.schedule.BuildXMLOutboundCommand;
import com.ksg.model.KSGModelManager;
import com.ksg.view.schedule.dialog.ScheduleCreateOptionDialog;
import com.ksg.view.util.ViewUtil;

public class ScheduleServiceManager {
	
	protected Logger 		logger = Logger.getLogger(this.getClass());
	private static ScheduleServiceManager serviceManager;
	private Timer timer;
	private JProgressBar bar =new JProgressBar();
	public static ScheduleServiceManager getInstance() {
		if(serviceManager==null)
			serviceManager = new ScheduleServiceManager();
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
/*	public void buildRouteShedule()
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
				panel.add(new JLabel("스케줄 생성 중"));
				panel.add(bar);
				dialog.setSize(400, 200);
				ViewUtil.center(dialog, false);
				dialog.setVisible(true);
				dialog.getContentPane().add(panel,BorderLayout.CENTER);
				BuildRouteScheduleCommand buildRouteScheduleCommand = new BuildRouteScheduleCommand();
				buildRouteScheduleCommand.execute();
				dialog.setVisible(false);
				dialog.dispose();
				timer.stop();
			}
		}.start();
	}*/
	public String getDate(String da)
	{
		if(da.length()!=8)
			JOptionPane.showMessageDialog(null, "입력자리수가 다릅니다.");
		String year=da.substring(0,4);
		String month=da.substring(4,6);
		String day=da.substring(6,8);
		return year+"-"+month+"-"+day;
	}
	
	public void buildSchedule() throws SQLException
	{
		optionDialog = new ScheduleCreateOptionDialog(ScheduleCreateOptionDialog.NOMAL);
		optionDialog.createAndUpdateUI();
	}

	public void buildSchedule(String inputDate) throws SQLException
	{
		optionDialog = new ScheduleCreateOptionDialog(ScheduleCreateOptionDialog.NOMAL,inputDate);
		optionDialog.createAndUpdateUI();
	}
	public void buildWebSchedule() throws SQLException
	{
		optionDialog = new ScheduleCreateOptionDialog(ScheduleCreateOptionDialog.WEB);
		optionDialog.createAndUpdateUI();
	}
	public void buildWebSchedule(String inputDate) throws SQLException
	{
		optionDialog = new ScheduleCreateOptionDialog(ScheduleCreateOptionDialog.WEB,inputDate);
		optionDialog.createAndUpdateUI();
	}
}
