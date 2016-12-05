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
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ksg.commands.KSGCommand;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.dao.impl.ScheduleService;
import com.ksg.dao.impl.ScheduleServiceImpl;
import com.ksg.domain.Company;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ScheduleData;
import com.ksg.model.KSGModelManager;
import com.ksg.quark.XTGManager;
import com.ksg.view.util.KSGDateUtil;
import com.ksg.view.util.ViewUtil;

public class BuildOutboundCommand implements KSGCommand {
	private JTextArea area2;
	private ScheduleService scheduleService;
	private BaseService baseService;
	private String TAG_0=" ";
	private String TAG_1=" ";
	private String TAG_2=" ";
	private String TAG_3=" ";
	private XTGManager xtgManager;
	public BuildOutboundCommand() {
		scheduleService = new ScheduleServiceImpl();
		baseService = new BaseServiceImpl();
		xtgManager = new XTGManager();
	}
	public int execute() {

		try {

			List portli = scheduleService.getOutboundPortList();
			//Outbound µµÂøÇ× Á¶È¸

			Iterator portiter = portli.iterator();

			StringBuffer total = new StringBuffer();
			while(portiter.hasNext())
			{
				String port = (String) portiter.next();


				//  Áö¿ª¸í ¼³Á¤
				PortInfo portInfo=baseService.getPortInfoByPortName(port);
				if(portInfo!=null)
				{
					total.append("\n"+TAG_0+port+","+portInfo.getPort_name()+"\n");
				}else
				{
					total.append("\n"+TAG_0+port+"\n\n");
				}

				// Ãâ¹ßÇ× Á¶È¸
				List fromPortList = scheduleService.getOutboundFromPortList(port);

				//List a = this.getScheduleListByPort(port);

				Iterator fromPortiter = fromPortList.iterator();
				while(fromPortiter.hasNext())
				{
					String fromPort = (String) fromPortiter.next();
					//Ãâ¹ßÇ× , µµÂøÇ× ±âÁØÀ¸·Î ½ºÄÉÁÙ Á¶È¸
					List schedueList_1 = scheduleService.getOutboundScheduleList(port, fromPort);

					if(schedueList_1.size()>0)
						total.append("- "+TAG_1+fromPort+" -"+TAG_2+"\n");	

					Iterator sIterator = schedueList_1.iterator();
					String fromPortListString=new String();
					Vector usedSchedule = new Vector();
					//
					while(sIterator.hasNext())
					{
						ScheduleData oneData = (ScheduleData) sIterator.next();


						// È®ÀÎ Çß´Â Áö È®ÀÎ
						if(isUse(usedSchedule,oneData))
							continue;


						String dateF=oneData.getDateF();
						String dateT=oneData.getDateT();
						Company agent=baseService.getCompanyInfo(oneData.getCompany_abbr());
						List schedueList_2 = scheduleService.getOutboundScheduleList(port, fromPort);
						String company_abbr=oneData.getCompany_abbr();
						for(int i=0;i<schedueList_2.size();i++)
						{
							// case1 Ãâ¹ßÇ×,µµÂøÇ× µ¿ÀÏ, ÃâÇ×ÀÏ µµÂøÀÏ µ¿ÀÏ, Ç×Â÷ ¹øÈ£ µ¿ÀÏ,´Ù¸¥ ¼±»ç 
							ScheduleData twoData=(ScheduleData) schedueList_2.get(i);
							if( !oneData.getCompany_abbr().equals(twoData.getCompany_abbr())&&// ¼±»ç ´Ù¸§
									oneData.getVessel().equals(twoData.getVessel())&&
									oneData.getDateF().equals(twoData.getDateF())&&// Ãâ¹ßÀÏ °°À½
									oneData.getDateT().equals(twoData.getDateT())&&// µµÂøÀÏ °°À½
									!oneData.getVoyage_num().equals(twoData.getVoyage_num())// Ç×Â÷ ¹øÈ£ °°À½
							)
							{
								dateF = twoData.getDateF();
								dateT= twoData.getDateT();
								company_abbr = "   ("+agent.getCompany_abbr()+","+twoData.getCompany_abbr()+")\t";
								usedSchedule.add(twoData);
								break;
							}


							// case Ãâ¹ßÇ×,µµÂøÇ× °°À½, ´Ù¸¥ ¼±»ç, ÃâÇ×ÀÏ µµÂøÀÏ °¢ 3ÀÏ ÀÌ³»
							if( !oneData.getCompany_abbr().equals(twoData.getCompany_abbr())&&
									oneData.getVessel().equals(twoData.getVessel()))
								// ¼±»ç´Â ´Ù¸§
							{
								if(isThreeDayUnder(oneData.getDateF(), twoData.getDateF())&&isThreeDayUnder(oneData.getDateT(), twoData.getDateT()))
								{
									dateT = rowerDate(oneData.getDateT(),twoData.getDateT());
									dateF =biggerDate(oneData.getDateF(),twoData.getDateF());
									company_abbr = "   ("+agent.getCompany_abbr()+","+twoData.getCompany_abbr()+")\t";

									usedSchedule.add(twoData);
									break;
								}
							}

							// Ãâ¹ßÇ×,µµÂøÇ× °°À½

						}
						// ¼±»çÀÇ ¿äÃ»ÀÌ ÀÖ¾ú´ÂÁö È®ÀÎ
						
						//Vessel vslInfo=baseService.getVesselInfo(oneData.getVessel());
						/*if(vslInfo!=null&&vslInfo.getRequest_company()!=null)
						{
							oneData.setVessel(oneData.getVessel()+".");
							company_abbr=vslInfo.getRequest_company();
						}*/

						total.append(dateF+"\t"+TAG_3+oneData.getVessel()+TAG_2+company_abbr+"\t"+dateT);

						total.append("\n");	
						/*if(schedueList_1.size()==1)
						{

						}*/
					}
				}
			}
			createDialog();
			area2.append(total.toString());
			xtgManager.createXTGFile(total.toString(), "ExportSchedule_");
			return RESULT_SUCCESS;
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "½ºÄÉÁÙ »ý¼ºÀ» ½ÇÆÐÇß½À´Ï´Ù. "+e.getErrorCode()+","+e.getMessage());
			return RESULT_FAILE;
		}catch (NullPointerException e) {
			JOptionPane.showMessageDialog(null, "½ºÄÉÁÙ »ý¼ºÀ» ½ÇÆÐÇß½À´Ï´Ù. "+e.getMessage());
			return RESULT_FAILE;
		} finally {
		}

	}
	private String rowerDate(String onedate, String twodate) {
		int oneTMonth =this.getMonth(onedate);
		int twoTMonth =this.getMonth(twodate);
		int oneTDay =this.getDay(onedate);
		int twoTDay =this.getDay(twodate);
		if(oneTMonth>twoTMonth)
		{
			return twodate;
		}
		else if(oneTMonth==twoTMonth)
		{
			if(oneTDay>twoTDay)
			{
				return twodate;

			}else
			{
				return onedate;
			}
		}else
		{
			return onedate;
		}
	}
	private String biggerDate(String onedate, String twodate) {
		
		try {
			if(KSGDateUtil.daysDiff(KSGDateUtil.toDate(onedate), KSGDateUtil.toDate(twodate))<=0)
			{
				return twodate;
			}else
			{
				return onedate;
			}
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"oneDate:"+onedate+", twoDate:"+twodate);
		}
		
		
	}
	private boolean isThreeDayUnder(String onedate, String twodate) {
		
		try {
			return KSGDateUtil.daysDiffABS(KSGDateUtil.toDate(onedate), KSGDateUtil.toDate(twodate))<=3;
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage()+"oneDate:"+onedate+", twoDate:"+twodate);
		}

	}
	private boolean isUse(Vector usedSchedule,ScheduleData data) {
		boolean flag = false;
		for(int i=0;i<usedSchedule.size();i++)
		{
			ScheduleData usededData = (ScheduleData) usedSchedule.get(i);
			if(data.getCompany_abbr().equals(usededData.getCompany_abbr())&&
					data.getPort().equals(usededData.getPort())&&
					data.getFromPort().equals(usededData.getFromPort())&&
					data.getDateF().equals(usededData.getDateF())&&
					data.getDateT().equals(usededData.getDateT())
			)
				flag= true;
			//JOptionPane.showMessageDialog(null, "test");
		}
		return flag;
	}
	private int getDay(String dateF) {
		StringTokenizer st = new StringTokenizer(dateF,"/");
		st.nextToken();
		return Integer.parseInt(st.nextToken());
	}
	private int getMonth(String dateF) {
		StringTokenizer st = new StringTokenizer(dateF,"/");
		return Integer.parseInt(st.nextToken());
	}
	private void createDialog() {
		JDialog dialog = new JDialog(KSGModelManager.getInstance().frame);
		dialog.setTitle("Outbound ½ºÄÉÁÙ »ý¼º Å×½ºÆ®");
		area2 = new JTextArea();
		dialog.getContentPane().add(new JScrollPane(area2),BorderLayout.CENTER);
		dialog.setSize(800, 500);
		ViewUtil.center(dialog, false);
		dialog.setVisible(true);
	}

}
