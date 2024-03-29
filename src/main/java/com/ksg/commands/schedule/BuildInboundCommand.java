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
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ksg.commands.IFCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.ViewUtil;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.print.logic.quark.v1.XTGManager;
import com.ksg.service.BaseService;
import com.ksg.service.ScheduleService;
import com.ksg.service.impl.BaseServiceImpl;
import com.ksg.service.impl.ScheduleServiceImpl;

public class BuildInboundCommand implements IFCommand {
	private static final String TAG_SIZE = "<$z7f$>";
	private ScheduleService scheduleService;
	private BaseService baseService;
	private JTextArea area2;
	StringBuffer buffer;
	private JProgressBar bar;
	private String VERSION;
	private String TABLE;
	private String BOLD;
	private String TABLE_SUB;
	public BuildInboundCommand() {

		scheduleService = new ScheduleServiceImpl();
		baseService = new BaseServiceImpl();
		
		try {
			VERSION=readTag("InboundSchedule_Version");
			BOLD=readTag("InboundSchedule_BOLD");
			xtgManager = new XTGManager();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	XTGManager xtgManager = new XTGManager();

/*	String TAG_0=" ";
	String TAG_2=" ";
	String TAG_1="  ";*/

	@SuppressWarnings("unchecked")
	public int execute() 
	{
		try {
			buffer = new StringBuffer();

			List portli = scheduleService.getInboundPortList();

			Iterator portiter = portli.iterator();


			while(portiter.hasNext())
			{
				String port = (String) portiter.next();

				PortInfo portInfo=baseService.getPortInfoByPortName(port);

				
				
				//항구 및 지역 표시(볼드체)
				if(portInfo!=null)
				{
					buffer.append("\n<Bz&>"+port+" , "+portInfo.getPort_nationality()+"\n"+"<$z7>"+"\n");
				}else
				{
					PortInfo port_abbr=scheduleService.getPortInfoByPortAbbr(port);
					if(port_abbr==null)
						continue;

					if(!scheduleService.isInPort(port_abbr.getPort_name()))
					{
						PortInfo portInfo2=baseService.getPortInfoByPortName(port_abbr.getPort_name());
						buffer.append("\n<Bz&>"+port_abbr.getPort_name()+", "+portInfo2.getPort_nationality()+"\n<$z7>\n");
					}else
					{
						continue;
					}
				}
				
				
				
				// 항구이름으로 Inbound 스케줄 조회 
				List inScheduleList = scheduleService.getInboundScheduleList(port);
				
				
				
				Iterator sIter = inScheduleList.iterator();

				while(sIter.hasNext())
				{
					ScheduleData data = (ScheduleData) sIter.next();

					// 항구이름과 선박으로 스케줄 조회
					List li=scheduleService.getInboundScheduleListByVessel(port,data.getVessel());
					Vector portlitemp = new Vector();
					for(Iterator iter=li.iterator();iter.hasNext();)
					{
						portlitemp.add(iter.next());
					}
					Vector portListtemp = new Vector();

					//1. 도착일 같고 출발이 다를때 선박명이 같을 때,
					for(Iterator iter=li.iterator();iter.hasNext();)
					{
						ScheduleData fdata = (ScheduleData) iter.next();
						String company = fdata.getCompany_abbr();
						for(Iterator tempiter=portlitemp.iterator();tempiter.hasNext();)
						{
							ScheduleData tdata = (ScheduleData)tempiter.next();
							if(!fdata.getCompany_abbr().equals(tdata.getCompany_abbr())&&// 선사명이 다름
									fdata.getPort().equals(tdata.getPort())&&// 도착 항구가 같음
									fdata.getDateT().equals(tdata.getDateT())&&// 도착 일자가 같음
									!fdata.getDateF().equals(tdata.getDateF())// 출발 일자가 다름
							)
							{
								company+=","+tdata.getCompany_abbr();
								StringTokenizer st1 = new StringTokenizer(fdata.getDateF(),"/");
								int monthF = Integer.parseInt(st1.nextToken());
								int dayF =Integer.parseInt(st1.nextToken());
								StringTokenizer st2 = new StringTokenizer(tdata.getDateF(),"/");
								int monthT = Integer.parseInt(st2.nextToken());
								int dayT =Integer.parseInt(st2.nextToken());


								if(monthF>monthT)
								{
									fdata.setDateF(fdata.getDateF());
								}else if(monthF==monthT)
								{									
									fdata.setDateF(dayF>dayT?fdata.getDateF():tdata.getDateF());
									
								}else
								{
									fdata.setDateF(tdata.getDateF());
								}
								portlitemp.remove(tdata);
							}  

						}
						fdata.setCompany_abbr(company);
						portListtemp.add(fdata);

					}

					StringBuffer sdata = new StringBuffer();


					Vector dateFtemp =  new Vector();
					for(int i=0;i<portListtemp.size();i++)
					{
						boolean flag = true;
						ScheduleData data1 = (ScheduleData) portListtemp.get(i);
						for(int j=0;j<dateFtemp.size();j++)
						{

							ScheduleData d = (ScheduleData) dateFtemp.get(j);
							if(data1.getDateF().equals(d.getDateF())&&
									data1.getVessel().equals(d.getVessel())&&
									data1.getCompany_abbr().equals(d.getCompany_abbr()))
							{
								flag=false;
							}
						}
						if(flag)
							dateFtemp.add(data1);

					}
					for(int i=0;i<dateFtemp.size();i++)
					{
						ScheduleData data2 = (ScheduleData) dateFtemp.get(i);
						String dateF =data2.getDateF();
						String vessel = data2.getVessel();
						String company_abbr= data2.getCompany_abbr();
						String common = new String();
						for(Iterator tempIter2=portListtemp.iterator();tempIter2.hasNext();)
						{
							ScheduleData fdata = (ScheduleData) tempIter2.next();


							// 선박 명과 도착일이 같고 출항일이 다를때

							if(data.getVessel().equals(fdata.getVessel())&&
									data.getVoyage_num().equals(fdata.getVoyage_num()))
								common+="<Bz&>"+this.getPortCode(fdata.getPort())+" "+fdata.getDateT();
						}
						
						if(data.getDateF().equals(data2.getDateF())&&data.getVessel().equals(data2.getVessel()))
							sdata.append("<$>"+dateF+"\t"+BOLD+vessel+TAG_SIZE+"  ("+company_abbr+")\t"+common+"\n");

					}
					buffer.append(sdata.toString());
				}		

			}
			createDialog();
			area2.setText(VERSION+"\n"+buffer.toString());
			xtgManager.createXTGFile(VERSION+"\n"+buffer.toString(), "ImportSchedule_");
			return RESULT_SUCCESS;

		} catch (SQLException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "error:"+e.getMessage());
			return RESULT_FAILE;
		}// Inbound 항구 조회


	}

	private String getPortCode(String port)
	{
		String fport="";
		try 
		{

			PortInfo portInfo=baseService.getPortInfoByPortName(port);
			if(portInfo==null)
			{
				PortInfo port_abbr=scheduleService.getPortInfoByPortAbbr(port);
				if(port_abbr!=null)
				{	
					fport= port_abbr.getPort_name();
				}
			}
			else
			{
				fport=portInfo.getPort_name();
			}

		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
		
		if(fport.equals("PUSAN"))
		{	
			return "["+BOLD+"P"+TAG_SIZE+"]";
		}else if(fport.equals("INCHON"))
		{
			return "["+BOLD+"I"+TAG_SIZE+"]";	
		}
		else if(fport.equals("KWANGYANG"))
		{
			return "["+BOLD+"K"+TAG_SIZE+"]";	
		}
		else if(fport.equals("ULSAN"))
		{
			return "["+BOLD+"U"+TAG_SIZE+"]";	
		}
		else
		{
			return "["+BOLD+port+TAG_SIZE+"]";	
		}

	}
	private void createDialog() {
		JDialog dialog = new JDialog(KSGModelManager.getInstance().frame);
		//		dialog.setModal(true);
		dialog.setTitle("Inbound 스케줄 생성 테스트");
		area2 = new JTextArea();
		dialog.getContentPane().add(new JScrollPane(area2),BorderLayout.CENTER);
		dialog.setSize(800, 500);
		ViewUtil.center(dialog, false);
		dialog.setVisible(true);
	}
	BufferedReader bufferedReader=null;
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

}
