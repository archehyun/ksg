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

/** 항구 정보
 * @author 박창현
 *
 */
public class PortInfo extends BaseInfo {
	
	private String port_name;// 항구명
	private String port_nationality;//나라
	private String port_area;// 지역
	private String area_code;// 지역 코드
	
	
	private String base_port_name;
	private String base_port_abbr;	
	private String port_code;
	
	private int port_book_code;
	public int getPort_book_code() {
		return port_book_code;
	}
	public void setPort_book_code(int portBookCode) {
		port_book_code = portBookCode;
	}


	private String port_abbr;
	public String getPort_code() {
		return port_code;
	}
	public void setPort_code(String port_code) {
		this.port_code = port_code;
	}
	public String getPort_name() {
		return port_name;
	}
	public void setPort_name(String port_name) {
		this.port_name = port_name;
	}
	public String getPort_nationality() {
		return port_nationality;
	}
	public void setPort_nationality(String port_nationality) {
		this.port_nationality = port_nationality;
	}
	public String getPort_area() {
		return port_area;
	}
	public void setPort_area(String port_area) {
		this.port_area = port_area;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getPort_abbr() {
		return port_abbr;
	}
	public void setPort_abbr(String port_abbr) {
		this.port_abbr = port_abbr;
	}
	public String toString()
	{
		return "["+this.getPort_name()+"]";
	}
	public String getBase_port_name() {
		return base_port_name;
	}
	public void setBase_port_name(String base_port_name) {
		this.base_port_name = base_port_name;
	}
	public String getBase_port_abbr() {
		return base_port_abbr;
	}
	public void setBase_port_abbr(String base_port_abbr) {
		this.base_port_abbr = base_port_abbr;
	}
	public String toInfoString() {
		// TODO Auto-generated method stub
		return "["+this.getPort_name()+","+port_nationality+"]";
	}
	

}
