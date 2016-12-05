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

public class Code {
	private String code_field;
	private String code_name;
	private String code_name_kor;
	private String code_type;
	public String getCode_field() {
		return code_field;
	}
	public void setCode_field(String code_field) {
		this.code_field = code_field;
	}
	public String getCode_name() {
		return code_name;
	}
	public void setCode_name(String code_name) {
		this.code_name = code_name;
	}
	public String getCode_type() {
		return code_type;
	}
	public void setCode_type(String code_type) {
		this.code_type = code_type;
	}
	public String getCode_name_kor() {
		return code_name_kor;
	}
	public void setCode_name_kor(String code_name_kor) {
		this.code_name_kor = code_name_kor;
	}
	public String toString(){
		return "["+this.getCode_name_kor()+"-"+this.getCode_field()+"]\n";
	}

}
