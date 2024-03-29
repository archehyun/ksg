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
 * 코드 정보 클래스
 * 
 * @author 박창현
 *
 */
@Data
public class Code {
	
	private String code_field;
	
	private String code_name;
	
	private String code_name_kor;
	
	private String code_type;
	
	public String toString(){
		return "["+this.getCode_name_kor()+"-"+this.getCode_field()+"]\n";
	}

}
