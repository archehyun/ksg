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

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * 광고정보 클랙스
 * @author 박창현
 *
 */
@Getter @Setter
@SuppressWarnings("unchecked")
public class ADVData {

//	public static final String COL_DIVIDER = "\t";
//
//	public static final String ROW_DIVIDER = "\n\n";

//	protected Logger logger = LogManager.getLogger(this.getClass());

	public int test;

	private String data;

	private String table_id;

	private Date date_isusse;

	private String table_date;

	private String company_abbr;

	private int t_index;

	private int page;

	private String ts;

//	private Document document = null;
//
//	private SAXBuilder builder = new SAXBuilder();

	public ADVData(){}

	public ADVData(String data){
		this.data = data;
	}

	public String toString()
	{
		return "[company:"+company_abbr+",date:"+date_isusse+"]\n"+data;
	}

//	public String[][] getDataArray3()
//	{
//		if(this.getData()==null)
//		{	
//			return null;
//		}
//		else
//		{
//			String words[] = data.split(ADVData.ROW_DIVIDER);
//			String[][] a = new String[words.length-1][];
//			for(int i=1;i<words.length;i++)
//			{
//				StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);
//				a[i-1]=new String[st2.countTokens()];
//
//				for(int j=0;j<a[i-1].length;j++)
//				{
//					a[i-1][j]=st2.nextToken();
//				}
//			}
//
//			return a;
//		}
//	}

//	public String[][] getDataArray() 
//	{	
//
//		try {
//			return this.getXMLDataArray();
//		} catch (OutOfMemoryError | JDOMException | IOException e) {
//			return new String[0][];
//		}
//
//	}

//	public String[][] getXMLFullVesselArray(boolean ts) throws JDOMException, IOException
//	{	
//		document = builder.build(new ByteArrayInputStream(data.getBytes()));
//		Element root = document.getRootElement();
//		List vessel_list=root.getChildren("vessel");
//		String returndata[][] = new String[vessel_list.size()][];
//		for(int i=0;i<vessel_list.size();i++)
//		{
//			Element vessel_info = (Element) vessel_list.get(i);
//			if(ts){
//				String vessel_name = vessel_info.getAttributeValue("ts_name");
//				String voyage  = vessel_info.getAttributeValue("ts_voyage");
//				returndata[i] = new String[2];
//				returndata[i][0] =vessel_name;
//				returndata[i][1] =voyage;
//			}else
//			{
//				String vessel_name = vessel_info.getAttributeValue("full-name");
//
//				String voyage  = vessel_info.getAttributeValue("voyage");
//				returndata[i] = new String[2];
//				returndata[i][0] =vessel_name;
//				returndata[i][1] =voyage;
//			}
//		}
//		return returndata;
//	}
//	public String[][] getXMLAbbrVesselArray(boolean ts) throws JDOMException, IOException
//	{
//		document = builder.build(new ByteArrayInputStream(data.getBytes()));
//		Element root = document.getRootElement();
//		List vessel_list=root.getChildren("vessel");
//		String returndata[][] = new String[vessel_list.size()][];
//		for(int i=0;i<vessel_list.size();i++)
//		{
//			Element vessel_info = (Element) vessel_list.get(i);
//			if(ts){
//				String vessel_name = vessel_info.getAttributeValue("ts_name");
//				String voyage  = vessel_info.getAttributeValue("ts_voyage");
//				returndata[i] = new String[2];
//				returndata[i][0] =vessel_name;
//				returndata[i][1] =voyage;
//			}else
//			{
//				String vessel_name = vessel_info.getAttributeValue("name");				
//				String voyage  = vessel_info.getAttributeValue("voyage");
//				returndata[i] = new String[2];
//				returndata[i][0] =vessel_name;
//				returndata[i][1] =voyage;
//			}
//
//		}
//		return returndata;
//	}


//	public String[][] getXMLDataArray() throws JDOMException, IOException, OutOfMemoryError
//	{
//		document= builder.build(new ByteArrayInputStream(data.getBytes()));
//
//		Element root = document.getRootElement();
//
//		List vessel_list=root.getChildren("vessel");
//
//		String returndata[][] = new String[vessel_list.size()][];
//
//		for(int i=0;i<vessel_list.size();i++)
//		{
//			Element vessel_info = (Element) vessel_list.get(i);			
//
//			List li=vessel_info.getChildren("input_date");
//
//			returndata[i] = new String[li.size()];
//			for(int j=0;j<li.size();j++)
//			{
//				Element input_date=(Element) li.get(j);
//				try{
//
//					returndata[i][j]=input_date.getAttributeValue("date").trim();
//				}catch(Exception e)
//				{
//					throw new RuntimeException();
//				}
//			}
//
//		}
//		return returndata;
//	}
//	public String[][] getDataArray2()
//	{
//		if(this.getData()==null) return null;
//
//		String words[] = data.split(ADVData.ROW_DIVIDER);
//
//		String[][] a = new String[words.length-1][];
//
//		for(int i=1;i<words.length;i++)
//		{
//			StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);
//
//			a[i-1]=new String[st2.countTokens()-2];
//
//			st2.nextToken();
//
//			st2.nextToken();
//
//			for(int j=0;j<a[i-1].length;j++)
//			{
//				a[i-1][j]=st2.nextToken();
//			}
//		}
//
//		return a;
//	}
//	public String[][] getTSDataArray()
//	{
//		if(this.getData()==null) return null;
//
//		String words[] = data.split(ADVData.ROW_DIVIDER);
//
//		String[][] a = new String[words.length-1][];
//
//		for(int i=1;i<words.length;i++)
//		{
//			StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);
//			a[i-1]=new String[st2.countTokens()-4];
//			st2.nextToken();
//			st2.nextToken();
//			st2.nextToken();
//			st2.nextToken();
//			for(int j=0;j<a[i-1].length;j++)
//			{
//				a[i-1][j]=st2.nextToken();
//			}
//		}
//
//		return a;
//	}
//	public int getPortCount()
//	{
//		return this.getPortArray().length;
//	}

//	public String[][] getFullVesselArray(boolean ts)
//	{
//		try {
//			return this.getXMLFullVesselArray(ts);
//		} catch (JDOMException e) {
//
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
//	public String[][] getAbbrVesselArray(boolean ts)
//	{
//		try {
//			return this.getXMLAbbrVesselArray(ts);
//		} catch (JDOMException e) {
//
//			e.printStackTrace();
//			return null;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	public String[] getPortArray() {
//
//		if(this.getData()==null) return null;
//
//		String words[] = data.split(ADVData.ROW_DIVIDER);
//
//		String[] b = words[0].split(ADVData.COL_DIVIDER);
//
//		String[]portArray = new String[b.length-2];
//
//		for(int i=2;i<b.length;i++)
//		{
//			portArray[i-2]=b[i];
//		}
//		return portArray;
//	}
//	public String[] getTSPortArray() {
//
//		if(this.getData()==null) return null;
//
//		String words[] = data.split(ADVData.ROW_DIVIDER);
//
//		String[] b = words[0].split(ADVData.COL_DIVIDER);
//
//		String[]a = new String[b.length-4];
//
//		for(int i=4;i<b.length;i++)
//		{
//			a[i-4]=b[i];
//		}
//
//		return a;
//
//	}

//	public void addDataRow() {
//		String a[] = new String[this.getPortCount()];
//		data+=ROW_DIVIDER;
//		for(int i=0;i<a.length;i++)
//		{
//			data+="-";
//			if(i<a.length)
//				data+=COL_DIVIDER;
//		}
//	}
}
