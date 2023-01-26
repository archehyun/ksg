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

import java.util.HashMap;

import com.ksg.common.util.KSGDateUtil;

import lombok.Data;

/**
 * ���̺� ���� �� ���� ����
 * @author archehyun
 *
 */
@Data
public class ShippersTable extends BaseInfo{
	
	
	public static final String GUBUN_CONSOLE 	= "Console";
	public static final String GUBUN_INLAND 	= "Inland";
	public static final String GUBUN_NNN 		= "NNN";
	public static final String GUBUN_NORMAL 	= "Normal";
	public static final String GUBUN_TS 		= "TS";
	
	private String agent;
	private String bookPage;			// ���� ������
	private int c_time=-1;
	private String common_shipping="";
	private String company_abbr="";
	private String company_name;
	private String console_cfs;
	private String console_page;
	private int d_time=-1;

	private String date_isusse;
	private String gubun; 				// ����	 
	HashMap<Object, Object> hachData;
	private String in_port; 			// inbound ����� �ε��� ����
	private String in_to_port; 			// inbound ������ �ε��� ����
	private String inland_indexs;
	private String operator; 			// ��ǥ ��ü
	private int othercell=-1;
	private String out_port; 			// outbound ����� �ε��� ����
	private String out_to_port; 		// outbound ������ �ε��� ����
	private int page=-1;
	private int port_col=-1;  			//�ױ���
	private String quark_format;
	private int r_port_col; 			//??
	protected String table_id;			// ���̺� ���̵�
	private int table_index=-1;
	private int tableCount;
	private String title="";
	private String TS;
	private String ts_port;
	private int tsIndex; 
	private int vsl_row=-1;
	
	
	
	public ShippersTable() {
		hachData = new HashMap<Object, Object>();
	}
	

	
	
//	public String getDate_isusse() {
//		try {
//			return KSGDateUtil.format(KSGDateUtil.toDate2(date_isusse));
//		} catch (Exception e) {
//			return date_isusse;
//		}
//	}
	
	
	/**
	 * outbound ����� �ε��� ����
	 * 1#2#3#4
	 * @return
	 */
	
	


	public String toString()
	{
		return "[table_id:"+table_id+",page:"+page+",date:"+date_isusse+","+this.getGubun()+"]";
	}
	@Override
	public String toInfoString() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
