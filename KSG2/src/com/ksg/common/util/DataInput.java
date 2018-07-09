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
package com.ksg.common.util;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.BaseServiceImpl;
import com.ksg.domain.AreaInfo;
import com.ksg.domain.PortInfo;
import com.ksg.domain.ShippersTable;
import com.ksg.domain.Vessel;
import com.ksg.shippertable.service.TableServiceImpl;


public class DataInput {
	String filename;
	String id;
	BaseService baseService;
	private PortInfo t;
	protected Logger 			logger = Logger.getLogger(getClass());
	public DataInput(String filename) {
		this.filename=filename;
	}
	public void portStart() throws IOException, SQLException
	{
		int nullcount=0;
		BaseService baseService = new BaseServiceImpl();
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String s;

		while ((s = br.readLine()) != null) {

			Pattern p = Pattern.compile("\\t");

			StringTokenizer st = new StringTokenizer(s,"  \t+",false);
			String[] words = p.split(s);


			Vector ss = new Vector();
			int count=0;
			int pre=0;
			for(int i=0;i<s.length();i++)
			{
				if(s.charAt(i)=="\t".charAt(0)||i==s.length()-1)

				{
					if(i==s.length()-1)
						i++;
					ss.add(s.substring(pre, i));
					pre=i+1;

				}
			}
			
			try{
				t = initPortParameter(ss);

				baseService.insertPortInfo(t);
			}
			/*catch(Exception e)
			{
				System.err.println(id+":"+e.getMessage()+",t:"+t);
				
				
				
			}*/
			catch(NullPointerException e)
			{
				nullcount++;
			}catch(SQLException e3)
			{
				baseService.updatePortInfo(t);
			}


			System.out.println(ss.size()+","+ss+"\n"+s+"\n");
		}
		System.out.println(nullcount+"===");
		
	}
	private PortInfo initPortParameter(Vector ss) throws SQLException {
		PortInfo t = new PortInfo();
		t.setPort_name((String) ss.get(0));
		t.setPort_nationality((String) ss.get(1));
		t.setPort_area((String) ss.get(2));
		AreaInfo info=baseService.getAreaInfo(t.getPort_area());
		t.setArea_code(info.getArea_code());
		
		
		return t;
	}
	private ShippersTable initQuarkParameter(Vector ss) throws SQLException {
		ShippersTable shippersTable = new ShippersTable(); 
		shippersTable.setTable_id((String) ss.get(0));// id
		try{
		shippersTable.setQuark_format((String)ss.get(1) );
		}catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return shippersTable;
	}
	public void start() throws NumberFormatException, IOException
	{
		TableServiceImpl serviceImpl = new TableServiceImpl();
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String s;

		while ((s = br.readLine()) != null) {

			Pattern p = Pattern.compile("\\t");

			StringTokenizer st = new StringTokenizer(s,"  \t+",false);
			String[] words = p.split(s);


			Vector ss = new Vector();
			int count=0;
			int pre=0;
			for(int i=0;i<s.length();i++)
			{
				if(s.charAt(i)=="\t".charAt(0)||i==s.length()-1)

				{
					if(i==s.length()-1)
						i++;
					ss.add(s.substring(pre, i));
					pre=i+1;

				}


			}
			try{
				ShippersTable t = initParameter3(ss);

				serviceImpl.updateTable(t);
			}
			catch(Exception e)
			{
				System.err.println(id+":"+e.getMessage());
				e.printStackTrace();
				break;
			}


			System.out.println(ss.size()+","+ss+"\n"+s+"\n");
		}

	}
	private ShippersTable initParameter(Vector ss) {
		ShippersTable t = new ShippersTable();

		t.setTable_id((String) ss.get(0));// id
		t.setTable_index(Integer.parseInt(ss.get(1).toString()));//index

		int port=Integer.parseInt(ss.get(2).toString());
		if(port>0)
		{
			t.setPort_col(port);	
		}else
		{
			t.setPort_col(0);
		}
		int vessel=Integer.parseInt(ss.get(3).toString());
		if(vessel>0)
		{
			t.setVsl_row(vessel);	
		}else
		{
			t.setVsl_row(0);	
		}
		//			t.setVsl_row(Integer.parseInt(ss.get(3).toString()));
		t.setOut_port((String) ss.get(4));
		t.setOut_to_port((String) ss.get(5));
		t.setIn_port((String) ss.get(6));
		t.setIn_to_port((String) ss.get(7));
		t.setGubun("");

		if(ss.size()==8)
			t.setCommon_shipping((String) ss.get(8));
		return t;
	}
	private ShippersTable initParameter2(Vector ss) throws ArrayIndexOutOfBoundsException {
		ShippersTable t = new ShippersTable();



		t.setTable_id((String) ss.get(0));// id
		this.id = t.getTable_id();
		t.setPage(Integer.parseInt(ss.get(1).toString()));//page
		t.setTable_index(Integer.parseInt(ss.get(2).toString()));//index
		t.setCompany_abbr((String) ss.get(3));
		t.setAgent((String) ss.get(4));
		int port=Integer.parseInt(ss.get(5).toString());
		if(port>0)
		{
			t.setPort_col(port);	
		}else
		{
			t.setPort_col(0);
		}
		int vessel=Integer.parseInt(ss.get(6).toString().trim());
		if(vessel>0)
		{
			t.setVsl_row(vessel);	
		}else
		{
			t.setVsl_row(0);	
		}
		t.setTitle((String)ss.get(7));
		//			t.setVsl_row(Integer.parseInt(ss.get(3).toString()));
		/*	t.setOut_port((String) ss.get(4));
		t.setOut_to_port((String) ss.get(5));
		t.setIn_port((String) ss.get(6));
		t.setIn_to_port((String) ss.get(7));*/
		/*t.setGubun("");

		if(ss.size()==9)
			t.setCommon_shipping((String) ss.get(8));*/
		return t;
	}
	private ShippersTable initParameter3(Vector ss) throws ArrayIndexOutOfBoundsException {
		ShippersTable t = new ShippersTable();

		t.setTable_id((String) ss.get(0));// id
		t.setGubun((String) ss.get(1));
		t.setTS((String) ss.get(2));
		t.setIn_port((String) ss.get(3));
		t.setIn_to_port((String) ss.get(4));		
		t.setOut_port((String) ss.get(5));
		t.setOut_to_port((String) ss.get(6));
		if(ss.size()==8)
		{
			t.setCommon_shipping((String) ss.get(7));
		}
		else{
			t.setCommon_shipping("");
		}



		
		return t;
	}



	public static void main(String[] args) throws IOException, SQLException 
	{
		DataInput dataInput = new DataInput("C:\\Documents and Settings\\Administrator\\바탕 화면\\DTP 프로그램 관련\\Vessel");
		dataInput.vesselStart();
	}
	private void vesselStart() throws IOException {
		BaseService baseService = new BaseServiceImpl();
		
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String s;
		while ((s = br.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(s,"\t");
			
			Vessel info = new Vessel();
			try{
			info.setVessel_name(st.nextToken());
			info.setVessel_abbr(st.nextToken());
			}catch(Exception e)
			{
				logger.error(e.getMessage()+":"+s);
			}
			
			try{
				info.setVessel_type(st.nextToken());
			}catch(Exception e)
			{
				info.setVessel_type("");
			}
			
			try {
				baseService.insertVessel(info);
			} catch (SQLException e1) {
				if(e1.getErrorCode()==2627)
				{
					logger.error("중복키 오류:"+s);
					
				}else
				{
					logger.error(e1.getMessage()+":"+s);
					e1.printStackTrace();
				}
				
			}
			/*System.out.print("[");
			System.out.print(st.nextToken());
			System.out.print(",");
			System.out.print(st.nextToken());
			System.out.print(",");
			try{
			System.out.print(st.nextToken());
			}catch(Exception e)
			{
				System.out.print("null");	
			}
			System.out.print("]");
			System.out.println();*/
			
		/*	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
//		baseService.insertVessel(vessel);
		
	}
	private void quarkStart() throws IOException {
		TableServiceImpl serviceImpl = new TableServiceImpl();
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		String s;

		while ((s = br.readLine()) != null) {

			Pattern p = Pattern.compile("\\t");

			StringTokenizer st = new StringTokenizer(s,"  \t+",false);
			String[] words = p.split(s);


			Vector ss = new Vector();
			int count=0;
			int pre=0;
			for(int i=0;i<s.length();i++)
			{
				if(s.charAt(i)=="\t".charAt(0)||i==s.length()-1)

				{
					if(i==s.length()-1)
						i++;
					ss.add(s.substring(pre, i));
					pre=i+1;

				}


			}
			try{
				ShippersTable t = initQuarkParameter(ss);

				serviceImpl.updateTable(t);
			}
			catch(Exception e)
			{
				System.err.println(id+":"+e.getMessage());
				e.printStackTrace();
				break;
			}


			System.out.println(ss.size()+","+ss+"\n"+s+"\n");
		}
		
	}


}
