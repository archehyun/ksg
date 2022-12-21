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
package com.ksg.domain;

import java.text.ParseException;
import java.util.StringTokenizer;

import com.ksg.common.util.KSGDateUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




/**
 * @author ��â��
 *
 */

@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class ScheduleData extends BaseInfo implements Comparable<Object>{
	private String agent; // ������Ʈ
	private String area_code; // ���� �ڵ�
	private String area_name; // ���� �̸�
	private String arrivalDate; // ��������
	private String c_time; // 
	private String common_shipping; // �����輱
	private String company_abbr; // ����� ���
	private String console_cfs;// �ܼ� CFS ����
	private String console_page; // �ܼ� ������
	private int console_print_type;
	private String d_time;
	private String data;
	private String date_issue;
	private String DateF;
	private String dateFBack;
	private String DateT;
	private String dateTBack;
	private String departDate;
	private String departure;
	private String desination;
	private int forSch;
	private String fromAreaCode;
	private String fromPort;
	private String gubun;
	private String inland_date;
	private String inland_date_back;
	private String vessel_mmsi; //���� MMSI �ڵ�

	private String inland_port;// �߰� ������
	private String InOutType;//����
	private int n_voyage_num;
	private int ntop;
	public int page; // ������
	private String port; // �ױ���
	private String table_id; // ���̺� ���̵�
	private String TS;
	private String ts_date;
	private String ts_port;
	private String ts_vessel;
	private String ts_voyage_num;
	private String vessel; // ���ڸ�
	private Vessel vesselInfo;
	private String vessel_type; // ����
	private String voyage_num; // ���� ��ȣ
	private String bookPage="";// ���� ������
	private String majorCompany="";// ���� ������
	private String orderby;

	public void setBookPage(String bookPage) {
		if(bookPage==null)
		{
			this.bookPage = "";
		}
		else
		{
			this.bookPage = bookPage;	
		}
		
	}
	public int compareTo(Object o) {
		ScheduleData table1 =(ScheduleData) o;

		try 
		{
			return KSGDateUtil.daysDiff( KSGDateUtil.toDate4(table1.getDateF()),KSGDateUtil.toDate4(this.getDateF()));
		} catch (ParseException e) 
		{

			return -1;
		}
	}
	
	public String getDateF2() {

		try {
			return KSGDateUtil.format2(KSGDateUtil.toDate(this.getDateF()));

		} catch (ParseException e) {
			return this.getDateF();

		}
	}

	public String getDateT2() {

		try 
		{
			return KSGDateUtil.format2(KSGDateUtil.toDate(this.getDateT()));

		}
		catch (ParseException e) 
		{
			return this.getDateT();

		}
	}
	
	public int getIntVoyage_num() {
		return this.getNumericVoyage(voyage_num);
	}
	private int getNumericVoyage(String voyage_num)
	{

		int result=0;

		String temp="";
		if(voyage_num==null)
			return 0;
		for(int i=0;i<voyage_num.length();i++)
		{
			try{
				temp+=Integer.parseInt(String.valueOf(voyage_num.charAt(i)));
			}catch(NumberFormatException e)
			{
				//				return 0;
			}
		}
		try{
			result=Integer.valueOf(temp);
		}catch(Exception e)
		{
			return 0;
		}

		return result;
	}
	
	

	public void setDateF(String dateF) 
	{
		
		StringTokenizer stringTokenizer = new StringTokenizer(dateF,"-");
		if(stringTokenizer.countTokens()==2)
		{
			stringTokenizer.nextToken();
			DateF = stringTokenizer.nextToken().trim();
			
			
		}else
		{
			DateF = dateF;	
		}
		
	}
	
	
	public void setScheduleID(int page) {
		this.page=page;
	}
	

	public String toInlandScheduleString()
	{
		return "["+this.getTable_id()+","+this.getDateF()+","+this.getFromPort()+","+this.getPort()+","+this.getDateT()+","+this.getInland_port()+"]";
	}
	
	public String toRouteDesc()
	{
		String dateT="";
		String dateF="";
		String dateTBack="";
		String dateFBack="";
		try {
			dateF=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getDateF()));
			dateT=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getDateT()));
			dateFBack=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getDateFBack()));
			dateTBack=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getDateTBack()));
		} catch (ParseException e) {
			dateF=this.getDateF();
			dateT=this.getDateT();
			e.printStackTrace();
		}
		return this.getFromPort()+dateF+dateFBack+this.getPort()+dateT+dateTBack+this.getVessel()+this.getVoyage_num()+(this.getTs_vessel()==null?"":this.getTs_vessel())+(this.getTs_voyage_num()==null?"":this.getTs_voyage_num())+this.getCompany_abbr();
	}
	public String toScheduleString()
	{

		String dateT="";
		String dateF="";
		try {
			dateF=KSGDateUtil.format2(KSGDateUtil.toDate(this.getDateF()));
			dateT=KSGDateUtil.format2(KSGDateUtil.toDate(this.getDateT()));
		} catch (ParseException e) {
			dateF=this.getDateF();
			dateT=this.getDateT();
			e.printStackTrace();
		}

		return this.getFromPort()+"\t"+this.getDateF()+"\t"+this.getVessel()+"\t"+this.getVoyage_num()+"\t"+this.getDateT()+"\t"+this.getPort();
	}
	
	public String toString()
	{
		return toTotalString();
	}
	public String  toTotalString()
	{
		String dateT="";
		String dateF="";
		try {
			dateF=KSGDateUtil.format2(KSGDateUtil.toDateBySearch(this.getDateF()));
			dateT=KSGDateUtil.format2(KSGDateUtil.toDateBySearch(this.getDateT()));
		} catch (ParseException e) {
			dateF=this.getDateF();
			dateT=this.getDateT();
			e.printStackTrace();
		}
		return "["+this.getTable_id()+"\t"+this.getFromPort()+"\t"+dateF+"\t"+
		this.getVessel()+"\t"+
		this.getCompany_abbr()+"\t"+
		dateT+"]";
	}
	public String toWebSchedule()
	{
		String dateT="";
		String dateF="";
		String dateTBack="";
		String dateFBack="";
		String dateTS="";
		try {
			dateF=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getDateF()));
			dateT=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getDateT()));
			dateFBack=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getDateFBack()));
			dateTBack=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getDateTBack()));
			
			if(this.getTs_date()!=null)
			{
			dateTS=KSGDateUtil.format5(KSGDateUtil.toDateBySearch(this.getTs_date()));
//			System.out.println(this.getTs_date()+","+dateTBack);
			}
		} catch (ParseException e) {
			dateF=this.getDateF();
			dateT=this.getDateT();
			System.err.println(e.getMessage());
		}
		return (this.getInOutType().equals("O")?"E":this.getInOutType())+"\t"
		+this.getFromPort()+"\t"
		+dateF+"\t"
		+dateFBack+"\t"
		+this.getPort()+"\t"
		+dateT+"\t"
		+dateTBack+"\t"
		+this.getVessel()+"\t"
		+this.getVoyage_num()+"\t"
		+(this.getTs_vessel()==null?"":this.getTs_vessel())+"\t"
		+(this.getTs_voyage_num()==null?"":this.getTs_voyage_num())+"\t"
		+this.getCompany_abbr()+"\t"
		+this.getAgent()+"\t"
		+(this.getTS()==null?"":this.getTS())+"\t"
		+this.toRouteDesc()+"\t\t"
		+(this.getTs_date()=="-"?"":dateTS);
	}
	public String toWebScheduleString()
	{
		// ����, E/I, ����ױ���, 

		String dateT="";
		String dateF="";
		try {
			dateF=KSGDateUtil.format2(KSGDateUtil.toDate(this.getDateF()));
			dateT=KSGDateUtil.format2(KSGDateUtil.toDate(this.getDateT()));
		} catch (ParseException e) {
			dateF=this.getDateF();
			dateT=this.getDateT();
			e.printStackTrace();
		}


		return "0\t"+this.getInOutType()+"\t"+this.getFromPort()+"\t"+this.getDateF()+"\t"+this.getDateF()+"\t"+
		this.getPort()+"\t"+this.getDateT()+"\t"+this.getDateT()+"\t"+this.getVessel()+"\t"+this.getVoyage_num()+"\t"+
		" \t \t \t"+this.getCompany_abbr()+"\t"+this.getAgent()+"\t "+
		this.getFromPort()+this.getDateF()+this.getDateF()+
		this.getPort()+this.getDateT()+this.getDateT()+this.getVessel()+this.getVoyage_num()+this.getCompany_abbr()
		;
	}
	private String operator;
	
	@Override
	public String toInfoString() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
