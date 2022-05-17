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
 * @���� ���� ����
 * @author ��â��
 *
 */
@Data
public class AreaInfo extends BaseInfo{
	
	private String base_area_name;
	
	private String area_type;
	private String index;		// 
	private String area_code;  // ���� �ڵ�
	private int area_book_code;// å �ڵ�
	
	private String area_name;
	
	
	

	public String toString()
	{
		return this.getArea_name();
	}
	
	@Override
	public String toInfoString() {
		// TODO Auto-generated method stub
		return "["+area_name+","+area_code+"]";
	}

}
