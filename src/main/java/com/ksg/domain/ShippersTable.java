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

import java.util.HashMap;

import com.ksg.common.util.KSGDateUtil;

/**
 * 테이블에 대한 상세 정보 정의
 * @author archehyun
 *
 */
public class ShippersTable extends BaseInfo{
	
	public static final int CONSOLE_CFS=1;	
	public static final int CONSOLE_PAGE=0;	
	public static final String GUBUN_CONSOLE 	= "Console";
	public static final String GUBUN_INLAND 	= "Inland";
	public static final String GUBUN_NNN 		= "NNN";
	public static final String GUBUN_NORMAL 	= "Normal";
	public static final String GUBUN_TS 		= "TS";
	
	private String agent;
	private String bookPage;			// 지면 페이지
	private int c_time=-1;
	private String common_shipping="";
	private String company_abbr="";
	private String company_name;
	private String console_cfs;
	private String console_page;
	private int d_time=-1;

	private String date_isusse;
	private String gubun; 				// 구분	 
	HashMap<Object, Object> hachData;
	private String in_port; 			// inbound 출발항 인덱스 문자
	private String in_to_port; 			// inbound 도착항 인덱스 문자
	private String inland_indexs;
	private String operator; 			// 대표 업체
	private int othercell=-1;
	private String out_port; 			// outbound 출발항 인덱스 문자
	private String out_to_port; 		// outbound 도착항 인덱스 문자
	private int page=-1;
	private int port_col=-1;  			//항구수
	private String quark_format;
	private int r_port_col; 			//??
	protected String table_id;			// 테이블 아이디
	private int table_index=-1;
	private int tableCount;
	private String title="";
	private String TS;
	private String ts_port;
	private int tsIndex; 
	private int vsl_row=-1;
	
	
	
	public ShippersTable() {
		hachData = new HashMap<Object, Object>();
	}
	public String getAgent() {
		return agent;
	}
	public String getBookPage() {
		return bookPage;
	}
	public int getC_time() {
		return c_time;
	}
	public String getCommon_shipping() {
		return common_shipping;
	}
	public String getCompany_abbr() {
		return company_abbr;
	}
	public String getCompany_name() {
		return company_name;
	}
	public String getConsole_cfs() {
		return console_cfs;
	}

	public String getConsole_page() {
		return console_page;
	}
	public int getD_time() {
		return d_time;
	}
	public String getDate_isusse() {
		try {
			return KSGDateUtil.format(KSGDateUtil.toDate2(date_isusse));
		} catch (Exception e) {
			return date_isusse;
		}
	}
	public String getGubun() {
		return gubun;
	}
	public Object getHashData(Object key)
	{
		return hachData.get(key);
	}
	public String getIn_port() {
		return in_port;
	}

	public String getIn_to_port() {
		return in_to_port;
	}
	public String getInland_indexs() {
		return inland_indexs;
	}
	public String getOperator() {
		return operator;
	}
	public int getOthercell() {
		return othercell;
	}
	
	/**
	 * outbound 출발항 인덱스 문자
	 * 1#2#3#4
	 * @return
	 */
	public String getOut_port() {
		return out_port;
	}
	public String getOut_to_port() {
		return out_to_port;
	}
	public int getPage() {
		return page;
	}
	
	public int getPort_col() {
		return port_col;
	}
	public String getQuark_format() {
		return quark_format;
	}
	
	public int getR_port_col() {
		return r_port_col;
	}
	public String getTable_id() {
		return table_id;
	}
	public int getTable_index() {
		return table_index;
	}
	public int getTableCount() {
		return tableCount;
	}
	public String getTitle() {
		return title;
	}
	public String getTS() {
		return TS;
	}
	public String getTs_port() {
		return ts_port;
	}
	public int getTsIndex() {
		return tsIndex;
	}
	public int getVsl_row() {
		return vsl_row;
	}
	public void setAgent(String agent) {
		this.agent = agent;
		this.hachData.put("agent", agent);
	}
	public void setBookPage(String bookPage) {
		this.bookPage = bookPage;
	}
	public void setC_time(int c_time) {
		this.c_time = c_time;
	}
	public void setCommon_shipping(String common_shipping) {
		this.common_shipping = common_shipping;
		this.hachData.put("common_shipping", common_shipping);
	}
	public void setCompany_abbr(String company_abbr) {
		this.company_abbr = company_abbr;
		this.hachData.put("company_abbr", company_abbr);
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
		this.hachData.put("company_name", company_name);
	}
	public void setConsole_cfs(String console_cfs) {
		this.console_cfs = console_cfs;
	}

	public void setConsole_page(String console_page) {
		this.console_page = console_page;
	}
	public void setD_time(int d_time) {
		this.d_time = d_time;
	}
	
	public void setDate_isusse(String date_isusse) {
		this.date_isusse = date_isusse;
		this.hachData.put("date_isusse", date_isusse);
	}

	public void setGubun(String gubun) {
		this.gubun = gubun;
		this.hachData.put("gubun", gubun);
	}
	public void setIn_port(String in_port) {
		this.in_port = in_port;
		this.hachData.put("in_port", in_port);
	}
	
	
	public void setIn_to_port(String in_to_port) {
		this.in_to_port = in_to_port;
		this.hachData.put("in_to_port", in_to_port);
	}


	public void setInland_indexs(String inland_indexs) {
		this.inland_indexs = inland_indexs;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public void setOthercell(int othercell) {
		this.othercell = othercell;
	}
	public void setOut_port(String out_port) {
		this.out_port = out_port;
		this.hachData.put("out_port", out_port);
	}
	public void setOut_to_port(String out_to_port) {
		this.out_to_port = out_to_port;
		this.hachData.put("out_to_port", out_to_port);
	}
	public void setPage(int page) {
		this.page = page;
		this.hachData.put("page", page);
	}
	public void setPort_col(int port_col) {
		this.port_col = port_col;
		this.hachData.put("port_col", port_col);
	}
	public void setQuark_format(String quark_format) {
		this.quark_format = quark_format;
		this.hachData.put("quark_format", quark_format);
	}
	public void setR_port_col(int r_port_col) {
		this.r_port_col = r_port_col;
	}
	public void setTable_id(String table_id) {
		this.table_id = table_id;
		this.hachData.put("table_id", table_id);
	}
	public void setTable_index(int table_index) {
		this.table_index = table_index;
		this.hachData.put("table_index", table_index);
	}
	public void setTableCount(int tableCount) {
		this.tableCount = tableCount;
		this.hachData.put("tableCount", tableCount);
	}
	public void setTitle(String title) {
		this.title = title;
		this.hachData.put("title", title);
	}
	public void setTS(String ts) {
		TS = ts;
		this.hachData.put("TS", tableCount);
	}
	public void setTs_index(int tsIndex) {
		this.tsIndex = tsIndex;
	}
	public void setTs_port(String ts_port) {
		this.ts_port = ts_port;
		this.hachData.put("ts_port", ts_port);
	}

	public void setTsIndex(int tsIndex) {
		this.tsIndex = tsIndex;
	}
	public void setVsl_row(int vsl_row) {
		this.vsl_row = vsl_row;
		this.hachData.put("vsl_row", vsl_row);
	}

	public String toString()
	{
		return "[table_id:"+table_id+",page:"+page+",date:"+date_isusse+","+this.getGubun()+"]";
	}
	@Override
	public String toInfoString() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
