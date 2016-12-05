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

/**@선사정보
 * @author archehyun
 *
 */
public class Company extends BaseInfo{
	
	private int table_id;
	private int page;
	private int port_col;
	private int vsl_row;
	private int table_index;
	private String company_name;// 선사명 이름
	private String company_abbr;// 선사명 약어
	private String base_company_abbr;
	private String agent_name;// 에이전트 이름
	private String agent_abbr;// 에이전트 약어
	private String contents;// 비고

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public String getCompany_abbr() {
		return company_abbr;
	}

	public void setCompany_abbr(String company_abbr) {
		this.company_abbr = company_abbr;
	}

	public int getTable_id() {
		return table_id;
	}

	public void setTable_id(int table_id) {
		this.table_id = table_id;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPort_col() {
		return port_col;
	}

	public void setPort_col(int port_col) {
		this.port_col = port_col;
	}

	public int getVsl_row() {
		return vsl_row;
	}

	public void setVsl_row(int vsl_row) {
		this.vsl_row = vsl_row;
	}

	public int getTable_index() {
		return table_index;
	}

	public void setTable_index(int table_index) {
		this.table_index = table_index;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company) {
		this.company_name = company;
	}

	

	public String getAgent_abbr() {
		return agent_abbr;
	}

	public void setAgent_abbr(String agent_abbr) {
		this.agent_abbr = agent_abbr;
	}

	public String getAgent_name() {
		return agent_name;
	}

	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}

	public String getBase_company_abbr() {
		return base_company_abbr;
	}

	public void setBase_company_abbr(String base_company_abbr) {
		this.base_company_abbr = base_company_abbr;
	}

	@Override
	public String toInfoString() {
		// TODO Auto-generated method stub
		return "["+company_name+","+company_abbr+"]";
	}

}
