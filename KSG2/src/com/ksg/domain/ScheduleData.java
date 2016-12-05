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

import com.ksg.view.util.KSGDateUtil;



public class ScheduleData implements Comparable<Object>{
	private String agent;
	private String area_code;
	private String area_name;
	private String arrivalDate;
	private String c_time;
	private String common_shipping;
	private String company_abbr;
	private String console_cfs;// 콘솔 CFS 정보
	private String console_page; // 콘솔 페이지
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
	private String vessel_mmsi; //선박 MMSI 코드

	private String inland_port;// 중간 기항지
	private String InOutType;//구분
	private int n_voyage_num;
	private int ntop;
	public int page;
	private String port;
	private String table_id;
	private String TS;
	private String ts_date;
	private String ts_port;
	private String ts_vessel;
	private String ts_voyage_num;
	private String vessel;
	private String vessel_type; // 선종
	private String voyage_num;
	private String bookPage="";// 지면 페이지
	private String majorCompany="";// 지면 페이지
	public String getBookPage() {
		return bookPage;
	}
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
	public String getAgent() {
		return agent;
	}
	public String getArea_code() {
		return area_code;
	}
	public String getArea_name() {
		return area_name;
	}
	public String getArrivalDate() {
		return arrivalDate;
	}
	public String getC_time() {
		return c_time;
	}
	public String getCommon_shipping() {
		return common_shipping;
	}
	public String getCompany_abbr() {
		return company_abbr;
	}
	public String getConsole_cfs() {
		return console_cfs;
	}
	public String getConsole_page() {
		return console_page;
	}
	public int getConsole_print_type() {
		return console_print_type;
	}
	public String getD_time() {
		return d_time;
	}
	public String getData() {
		return data;
	}
	public String getDate_issue() {
		return date_issue;
	}
	public String getDateF() {

		return DateF;
	}
	public String getDateF2() {

		try {
			return KSGDateUtil.format2(KSGDateUtil.toDate(this.getDateF()));

		} catch (ParseException e) {
			return this.getDateF();

		}
	}
	public String getDateFBack()
	{
		return dateFBack;
	}
	public String getDateT() {

		return DateT;
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
	
	public String getDateTBack()
	{
		return dateTBack;
	}
	public String getDepartDate() {
		return departDate;
	}
	public String getDeparture() {
		return departure;
	}
	public String getDesination() {
		return desination;
	}
	public int getForSch() {
		return forSch;
	}
	public String getFromAreaCode() {
		return fromAreaCode;
	}
	public String getFromPort() {
		return fromPort;
	}
	public String getGubun() {
		return gubun;
	}
	public String getInland_date() {
		return inland_date;
	}

	public String getInland_port() {
		return inland_port;
	}
	public String getInOutType() {
		return InOutType;
	}
	public int getN_voyage_num() {
		return n_voyage_num;
	}
	public int getNtop() {
		return ntop;
	}
	public String getPort() {
		return port;
	}
	public String getTable_id() {
		return table_id;
	}
	public String getTs() {
		return TS;
	}
	public String getTS() {
		return TS;
	}
	public String getTs_date() {
		return ts_date;
	}
	public String getTs_port() {
		return ts_port;
	}
	public String getTs_vessel() {
		return ts_vessel;
	}
	public String getTs_voyage_num() {
		return ts_voyage_num;
	}
	public String getVessel() {
		return vessel;
	}
	public String getVessel_type() {
		return vessel_type;
	}
	public String getVoyage_num() {
		return voyage_num;
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
	public void setAgent(String agent) {
		this.agent = agent;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public void setArea_name(String areaName) {
		area_name = areaName;
	}
	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public void setC_time(String c_time) {
		this.c_time = c_time;
	}

	public void setCommon_shipping(String common_shipping) {
		this.common_shipping = common_shipping;
	}
	public void setCompany_abbr(String company_abbr) {
		this.company_abbr = company_abbr;
	}
	public void setConsole_cfs(String console_cfs) {
		this.console_cfs = console_cfs;
	}
	public void setConsole_page(String console_page) {
		this.console_page = console_page;
	}
	public void setConsole_print_type(int console_print_type) {
		this.console_print_type = console_print_type;
	}
	public void setD_time(String d_time) {
		this.d_time = d_time;
	}

	public void setData(String data) {
		this.data = data;
	}


	public void setDate_issue(String dateIssue) {
		date_issue = dateIssue;
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
	public void setDateFBack(String dateF)
	{
		this.dateFBack=dateF;
	}
	public void setDateT(String dateT) {
		DateT = dateT;
	}
	public void setDateTBack(String dateT)
	{
		this.dateTBack=dateT;
	}
	public void setDepartDate(String departDate) {
		this.departDate = departDate;
	}
	public void setDesination(String desination) {
		this.desination = desination;
	}

	public void setForSch(int forSch) {
		this.forSch = forSch;
	}
	public void setFromAreaCode(String toAreaCode) {
		this.fromAreaCode = toAreaCode;
	}
public void setFromPort(String fromPort) {
		this.fromPort = fromPort;
	}
	public void setGubun(String gubun) {
		this.gubun = gubun;
	}
	public void setInland_date(String inland_date) {
		this.inland_date = inland_date;
	}
	public void setInland_port(String inland_port) {
		this.inland_port = inland_port;
	}
	public void setInOutType(String InOutType) {
		this.InOutType = InOutType;
	}
	public void setN_voyage_num(int nVoyageNum) {
		n_voyage_num = nVoyageNum;
	}
	public void setNtop(int ntop) {
		this.ntop = ntop;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public void setScheduleID(int page) {
		this.page=page;
	}
	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}
	public void setTs(String ts) {
		this.TS = ts;
	}
	public void setTS(String ts) {
		TS = ts;
	}
	public void setTs_date(String tsDate) {
		ts_date = tsDate;
	}
	public void setTs_port(String tsPort) {
		ts_port = tsPort;
	}
	public void setTs_vessel(String tsVessel) {
		ts_vessel = tsVessel;
	}
	public void setTs_voyage_num(String tsVoyageNum) {
		ts_voyage_num = tsVoyageNum;
	}
	public void setVessel(String vessel) {
		this.vessel = vessel;
	}

	public void setVessel_type(String vesselType) {
		vessel_type = vesselType;
	}
	public void setVoyage_num(String voyage_num) {
		this.voyage_num = voyage_num;
	}

	public String toInlandScheduleString()
	{
		return "["+this.getTable_id()+","+this.getDateF()+","+this.getFromPort()+","+this.getPort()+","+this.getDateT()+","+this.getInland_port()+"]";
	}
	public String getVessel_mmsi() {
		return vessel_mmsi;
	}
	public void setVessel_mmsi(String vessel_mmsi) {
		this.vessel_mmsi = vessel_mmsi;
	}
	public String getInland_date_back() {
		return inland_date_back;
	}
	public void setInland_date_back(String inland_date_back) {
		this.inland_date_back = inland_date_back;
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
		+(this.getTs()==null?"":this.getTs())+"\t"
		+this.toRouteDesc()+"\t\t"
		+(this.getTs_date()=="-"?"":dateTS);
	}
	public String toWebScheduleString()
	{
		// 순번, E/I, 출발항구명, 

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
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperator() {
		// TODO Auto-generated method stub
		return operator;
	}

	
	
}
