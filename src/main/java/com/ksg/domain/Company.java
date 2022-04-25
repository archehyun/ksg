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

/**
 * 
 * 선사정보 클래스
 * 
 * 
 * @author archehyun
 * 
 */
@Data
public class Company extends BaseInfo{
	
	private int table_id;			// 테이블 ID
	private int page; 				// 페이지 번호
	private int port_col; 			// 항구 수
	private int vsl_row;  			// 선박 수
	private int table_index;  		// 테이블 인덱스
	private String company_name;	// 선사명 이름
	private String company_abbr;	// 선사명 약어
	private String base_company_abbr;
	private String agent_name;		// 에이전트 이름
	private String agent_abbr;		// 에이전트 약어
	private String contents;		// 비고

	

	@Override
	public String toInfoString() {
		
		return "["+company_name+","+company_abbr+"]";
	}

}
