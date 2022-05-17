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
 * �������� Ŭ����
 * 
 * 
 * @author archehyun
 * 
 */

@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter @Setter
public class Company extends BaseInfo{
	
	private int table_id;			// ���̺� ID
	private int page; 				// ������ ��ȣ
	private int port_col; 			// �ױ� ��
	private int vsl_row;  			// ���� ��
	private int table_index;  		// ���̺� �ε���
	private String company_name;	// ����� �̸�
	private String company_abbr;	// ����� ���
	private String base_company_abbr;
	private String agent_name;		// ������Ʈ �̸�
	private String agent_abbr;		// ������Ʈ ���
	private String contents;		// ���

	

	@Override
	public String toInfoString() {
		
		return "["+company_name+","+company_abbr+"]";
	}

}
