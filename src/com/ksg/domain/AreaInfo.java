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

/**
 * @설명 지역 정보
 * @author 박창현
 *
 */
public class AreaInfo extends BaseInfo{
	
	private String base_area_name;
	private String area_type;
	private String index;		// 
	private String area_code;  // 지역 코드
	private int area_book_code;// 책 코드
	public int getArea_book_code() {
		return area_book_code;
	}
	public void setArea_book_code(int areaBookCode) {
		area_book_code = areaBookCode;
	}
	private String area_name;
	private String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getArea_type() {
		return area_type;
	}
	public void setArea_type(String area_type) {
		this.area_type = area_type;
	}

	public String toString()
	{
		return this.getArea_name();
	}
	public String getBase_area_name() {
		return base_area_name;
	}
	public void setBase_area_name(String base_area_name) {
		this.base_area_name = base_area_name;
	}
	@Override
	public String toInfoString() {
		// TODO Auto-generated method stub
		return "["+area_name+","+area_code+"]";
	}

}
