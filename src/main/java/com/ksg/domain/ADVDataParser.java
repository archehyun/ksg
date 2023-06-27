package com.ksg.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ADVDataParser {
	
	private SAXBuilder builder = new SAXBuilder();

	private ADVData advData;

	private Document document = null;

	public static final String COL_DIVIDER = "\t";

	public static final String ROW_DIVIDER = "\n\n";
	
	private String data;
	
	private String[][] dataArray;
	
	private String[] portArray;
	
	public ADVDataParser(ADVData advData) throws NullPointerException, JDOMException, IOException
	{
		this.advData =advData ;
		
		this.data = advData.getData();
		
		if(data == null) throw new NullPointerException();
		
		document = builder.build(new ByteArrayInputStream(data.getBytes()));
		
		dataArray = this.getXMLDataArray(this.data);
		
		portArray = this.getPortArray(this.data);
		
	}

	public String[][] getXMLFullVesselArray(boolean ts) throws JDOMException, IOException
	{	
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
	
	public int getPortCount()
	{
		return this.portArray.length;
	}
	
	public String[] getPortArray(String data) {

		String words[] = data.split(ROW_DIVIDER);

		String[] b = words[0].split(COL_DIVIDER);

		String[]portArray = new String[b.length-2];

		for(int i=2;i<b.length;i++)
		{
			portArray[i-2]=b[i];
		}
		return portArray;
	}
	
	public String[] getPortArray()
	{
		return this.getPortArray(this.data);
	}


	private String[][] getXMLDataArray(String data) throws JDOMException, IOException, OutOfMemoryError
	{
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

	public String[][] getTSDataArray()
	{
		String words[] = data.split(ROW_DIVIDER);

		String[][] a = new String[words.length-1][];

		for(int i=1;i<words.length;i++)
		{
			StringTokenizer st2= new StringTokenizer(words[i],COL_DIVIDER);
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
	
	public String[][] getAbbrVesselArray(boolean ts)
	{
		try {
			return this.getXMLAbbrVesselArray(ts);
		} catch (JDOMException e) {

			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public ADVData getData()
	{
		return this.advData;
	}

	public String[][] getDataArray() 
	{	
		return dataArray;
	}

}
