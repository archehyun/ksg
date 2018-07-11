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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
@SuppressWarnings("unchecked")
public class ADVData {
	public static final String COL_DIVIDER = "\t";
	protected Logger 		logger = Logger.getLogger(this.getClass());
	public int test;
	public String data;
	public String table_id;
	public Date date_isusse;
	public String company_abbr;
	public int t_index;
	private int page;
	private String ts;
	private Document document = null;
	private SAXBuilder builder = new SAXBuilder();
	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}
	public static final String ROW_DIVIDER = "\n\n";

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
		/*try{
		String words[] = data.split(ADVData.ROW_DIVIDER);
		String[][] a = new String[words.length-1][];

		dataList =  new Vector();
		portList = new Vector();
		vesselvoy= new Vector();

		// port
		StringTokenizer portst = new StringTokenizer(words[0],ADVData.COL_DIVIDER);
		portst.nextToken();
		portst.nextToken();
		while( portst.hasMoreTokens())
		{
			portList.add(portst.nextToken());
		}

		// data
		for(int i=1;i<words.length;i++)
		{
			StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);

			Vector subDataList = new Vector();

			//a[i-1]=new String[st2.countTokens()-2];

			VesselVoyInfo info = new VesselVoyInfo();
			info.vessel_name=st2.nextToken();
			info.voyage=st2.nextToken();

			vesselvoy.add(info);
			while(st2.hasMoreTokens())
			{
				subDataList.add(st2.nextToken());
			}
			dataList.add(subDataList);
		}
		}catch(Exception e){
			e.printStackTrace();
			return;
		}*/



	}
	public String getTable_id() {
		return table_id;
	}
	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}
	public Date getDate_isusse() {
		return date_isusse;
	}
	public void setDate_isusse(Date date_isusse) {
		this.date_isusse = date_isusse;
	}
	public String getCompany_abbr() {
		return company_abbr;
	}
	public void setCompany_abbr(String company_abbr) {
		this.company_abbr = company_abbr;
	}
	public int getT_index() {
		return t_index;
	}
	public void setT_index(int t_index) {
		this.t_index = t_index;
	}
	public String toString()
	{
		return "[company:"+company_abbr+",date:"+date_isusse+"]\n"+data;
	}

	public void setDataArray(String dataA[][])
	{
		/*if(this.getData()==null)
		{
			return ;
		}else
		{
			StringTokenizer st = new StringTokenizer(data,ROW_DIVIDER);
			String portList=st.nextToken();
			String temp =portList+ROW_DIVIDER;
			String vesselArray[][] =this.getVesselArray(); 
			for(int i=0;i<dataA.length;i++)
			{
				for(int j=0;j<vesselArray.length;j++)
				{
					temp+=vesselArray[i][j]+COL_DIVIDER;
				}
				for(int j =0;j<dataA[i].length;j++)
				{
					temp+=dataA[i][j];
					if(j<dataA[i].length-1)
						temp+=COL_DIVIDER;
				}
				if(i<dataA.length-1)
					temp+=ROW_DIVIDER;
			}
			this.setData(temp);
		}*/
	}
	public String[][] getDataArray3()
	{
		if(this.getData()==null)
		{	
			return null;
		}
		else
		{
			String words[] = data.split(ADVData.ROW_DIVIDER);
			String[][] a = new String[words.length-1][];
			for(int i=1;i<words.length;i++)
			{
				StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);
				a[i-1]=new String[st2.countTokens()];

				for(int j=0;j<a[i-1].length;j++)
				{
					a[i-1][j]=st2.nextToken();
				}
			}

			return a;
		}

	}

	public String[][] getDataArray() throws JDOMException, IOException,OutOfMemoryError
	{	
			try{
			return this.getXMLDataArray();
			}catch(OutOfMemoryError e)
			{
				e.printStackTrace();
				throw new OutOfMemoryError();
			}
	}
	
	//2014. 9. 12 메모리 오류 때문에 수정
	/*public String[][] getDataArray()
	{
		if(this.getData()==null)
		{	
			return null;
		}
		else
		{
			String words[] = data.split(ADVData.ROW_DIVIDER);
			String[][] a = new String[words.length-1][];
			for(int i=1;i<words.length;i++)
			{
				StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);
				a[i-1]=new String[st2.countTokens()-2];
				st2.nextToken();
				st2.nextToken();
				for(int j=0;j<a[i-1].length;j++)
				{
					a[i-1][j]=st2.nextToken();
				}
			}

			return a;
		}
		try {
			return this.getXMLDataArray();
		} catch (JDOMException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}*/
	/*public String[][] getXMLVesselArray() throws JDOMException, IOException
	{
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new ByteArrayInputStream(data.getBytes()));
		Element root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");
		String returndata[][] = new String[vessel_list.size()][];
		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);
			String vessel_name = vessel_info.getAttributeValue("name");
			String voyage  = vessel_info.getAttributeValue("voyage");
			returndata[i] = new String[2];
			returndata[i][0] =vessel_name;
			returndata[i][1] =voyage;

		}
		return returndata;
	}*/
	public String[][] getXMLFullVesselArray(boolean ts) throws JDOMException, IOException
	{	
		document = builder.build(new ByteArrayInputStream(data.getBytes()));
		Element root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");
		String returndata[][] = new String[vessel_list.size()][];
		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);
			if(ts){
				String vessel_name = vessel_info.getAttributeValue("ts_name");
				String voyage  = vessel_info.getAttributeValue("ts_voyage");
				returndata[i] = new String[2];
				returndata[i][0] =vessel_name;
				returndata[i][1] =voyage;
			}else
			{
				String vessel_name = vessel_info.getAttributeValue("full-name");

				String voyage  = vessel_info.getAttributeValue("voyage");
				returndata[i] = new String[2];
				returndata[i][0] =vessel_name;
				returndata[i][1] =voyage;
			}
		}
		return returndata;
	}
	public String[][] getXMLAbbrVesselArray(boolean ts) throws JDOMException, IOException
	{
		document = builder.build(new ByteArrayInputStream(data.getBytes()));
		Element root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");
		String returndata[][] = new String[vessel_list.size()][];
		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);
			if(ts){
				String vessel_name = vessel_info.getAttributeValue("ts_name");
				String voyage  = vessel_info.getAttributeValue("ts_voyage");
				returndata[i] = new String[2];
				returndata[i][0] =vessel_name;
				returndata[i][1] =voyage;
			}else
			{
				String vessel_name = vessel_info.getAttributeValue("name");				
				String voyage  = vessel_info.getAttributeValue("voyage");
				returndata[i] = new String[2];
				returndata[i][0] =vessel_name;
				returndata[i][1] =voyage;
			}

		}
		return returndata;
	}
	

	public String[][] getXMLDataArray() throws JDOMException, IOException, OutOfMemoryError
	{
		
		
		// error Java heap space
		
		document= builder.build(new ByteArrayInputStream(data.getBytes()));
		Element root = document.getRootElement();
		List vessel_list=root.getChildren("vessel");
		String returndata[][] = new String[vessel_list.size()][];
		for(int i=0;i<vessel_list.size();i++)
		{
			Element vessel_info = (Element) vessel_list.get(i);			

			List li=vessel_info.getChildren("input_date");

			returndata[i] = new String[li.size()];
			for(int j=0;j<li.size();j++)
			{
				Element input_date=(Element) li.get(j);
				try{

					returndata[i][j]=input_date.getAttributeValue("date").trim();
				}catch(Exception e)
				{
					throw new RuntimeException();
				}
			}

		}
		return returndata;
	}
	public String[][] getDataArray2()
	{
		if(this.getData()==null)
		{	
			return null;
		}
		else
		{
			String words[] = data.split(ADVData.ROW_DIVIDER);
			String[][] a = new String[words.length-1][];
			for(int i=1;i<words.length;i++)
			{
				StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);
				a[i-1]=new String[st2.countTokens()-2];
				st2.nextToken();
				st2.nextToken();
				for(int j=0;j<a[i-1].length;j++)
				{
					a[i-1][j]=st2.nextToken();
				}
			}

			return a;
		}
	}
	public String[][] getTSDataArray()
	{
		if(this.getData()==null)
		{	
			return null;
		}
		else
		{
			String words[] = data.split(ADVData.ROW_DIVIDER);
			String[][] a = new String[words.length-1][];
			for(int i=1;i<words.length;i++)
			{
				StringTokenizer st2= new StringTokenizer(words[i],ADVData.COL_DIVIDER);
				a[i-1]=new String[st2.countTokens()-4];
				st2.nextToken();
				st2.nextToken();
				st2.nextToken();
				st2.nextToken();
				for(int j=0;j<a[i-1].length;j++)
				{
					a[i-1][j]=st2.nextToken();
				}
			}

			return a;
		}
	}
	public int getPortCount()
	{
		return this.getPortArray().length;
	}

	public String[][] getFullVesselArray(boolean ts)
	{
		try {
			return this.getXMLFullVesselArray(ts);
		} catch (JDOMException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public String[][] getAbbrVesselArray(boolean ts)
	{
		try {
			return this.getXMLAbbrVesselArray(ts);
		} catch (JDOMException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String[] getPortArray() {
		if(this.getData()==null)
		{
			return null;
		}else
		{

			String words[] = data.split(ADVData.ROW_DIVIDER);
			String[] b = words[0].split(ADVData.COL_DIVIDER);
			String[]portArray = new String[b.length-2];
			for(int i=2;i<b.length;i++)
			{
				portArray[i-2]=b[i];
			}
			return portArray;
		}
	}
	public String[] getTSPortArray() {
		// TODO Auto-generated method stub
		if(this.getData()==null)
		{
			return null;
		}else
		{

			String words[] = data.split(ADVData.ROW_DIVIDER);
			String[] b = words[0].split(ADVData.COL_DIVIDER);
			String[]a = new String[b.length-4];
			for(int i=4;i<b.length;i++)
			{
				a[i-4]=b[i];
			}

			return a;
		}
	}
	public void setPage(int page) {
		this.page=page;
	}
	public int getPage()
	{
		return page;
	}
	public void addDataRow() {
		String a[] = new String[this.getPortCount()];
		data+=ROW_DIVIDER;
		for(int i=0;i<a.length;i++)
		{
			data+="-";
			if(i<a.length)
				data+=COL_DIVIDER;
		}
	}
	class VesselVoyInfo
	{
		public String vessel_name;
		public String voyage;
	}
	
}
