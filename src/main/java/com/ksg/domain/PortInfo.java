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

import lombok.Data;

/** 항구 정보
 * @author 박창현
 *
 */
@Data
public class PortInfo extends BaseInfo {
	
	private String port_name;			// 항구명
	
	private String port_nationality;	//나라
	
	private String port_area;			// 지역
	
	private String area_code;			// 지역 코드	
	
	private String base_port_name;
	
	private String base_port_abbr;	
	
	private String port_code;
	
	private int port_book_code;
	
	private String port_abbr;
	
	public String toString()
	{
		return "["+this.getPort_name()+"]";
	}
	
	public String toInfoString() {
		// TODO Auto-generated method stub
		return "["+this.getPort_name()+","+getPort_nationality()+"]";
	}
	

}
