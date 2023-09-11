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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * 코드 정보 클래스
 * 
 * @author 박창현
 *
 */
@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class Code {
	
	
	private String cd_id;
	
	private String cd_nm;
	
	private String cd_eng;
	
	private String code_id;
	
	private String code_field;
	
	private String code_name;
	
	private String code_name_kor;
	
	private String code_type;
	
	public String toString(){
		return "["+this.getCode_name_kor()+"-"+this.getCode_field()+"]\n";
	}

}
