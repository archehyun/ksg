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
package com.ksg.adv.logic.poi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTextArea;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * @author Administrator
 *
 */
public class XLSFileReader implements IF_XLSManager
{
	/**
	 * 
	 */
	private JTextArea area;
	private int rows;
	private int cells;
	private String[][] xlsDatas;
	private Vector tableInfo;
	public XLSFileReader() {
		tableInfo = new Vector();
	}
	public XLSFileReader(String xlsFile) throws IOException 
	{
		this();
	}
	public XLSFileReader(String xlsFile, JTextArea xlsArea) throws Exception 
	{
		this();
		this.area=xlsArea;

		try {
			readIndex(xlsFile);
			readData(xlsFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getCellData2( HSSFCell cell) 
	{
		String returnData="";
		switch(cell.getCellType()) 
		{
		case HSSFCell.CELL_TYPE_STRING:
			returnData=cell.getRichStringCellValue().getString()+" ";
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:

			returnData=cell.getNumericCellValue()+" ";
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			returnData=cell.getBooleanCellValue()+" ";
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			break;
		default:
			System.out.println();
		}
		return returnData;
	}
	public String getData(int index)
	{
		Iterator iter=tableInfo.iterator();
		System.out.println(tableInfo.size());
		while(iter.hasNext())
		{
			XLSData data =(XLSData) iter.next();
			if(data.getIndex()==index)
				return data.getData();
		}
		return "";
	}
	/* (non-Javadoc)
	 * @see ac.poi.IF_XLSManager#getDataArray(int)
	 */
	public String[][] getDataArray(int index)
	{
		Iterator iter=tableInfo.iterator();
		while(iter.hasNext())
		{
			XLSData data =(XLSData) iter.next();
			if(data.getIndex()==index)
				return data.getDataArray();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see ac.poi.IF_XLSManager#getXLSData(java.lang.String)
	 */
	public String[][] getXLSData(String xlsFile) throws FileNotFoundException, IOException
	{
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet1 = wb.getSheetAt(0);
		rows=sheet1.getPhysicalNumberOfRows();
		xlsDatas = new String[rows][];

		for (int i=0;i<rows;i++) 
		{
			HSSFRow row = sheet1.getRow(i);

			if(row!=null)
			{
				cells = row.getPhysicalNumberOfCells();

				xlsDatas[i]=new String[cells];
				for (short j=0;j<cells;j++) 
				{
					HSSFCell cell= row.getCell(j);
					if(cell!=null)
					{
						switch(cell.getCellType()) {
						case HSSFCell.CELL_TYPE_STRING:
							xlsDatas[i][j]=cell.getRichStringCellValue().getString()+" ";
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:

							xlsDatas[i][j]=cell.getNumericCellValue()+" ";
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							xlsDatas[i][j]=cell.getBooleanCellValue()+" ";
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							break;
						}
					}
				}
			}

		}
		return xlsDatas;
	}
	/**
	 * @param xlsFile
	 * @throws Exception
	 * @throws IOException
	 */
	public void readData(String xlsFile) throws IOException
	{
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet1 = wb.getSheetAt(1);
		Iterator iter=tableInfo.iterator();

		while(iter.hasNext())
		{
			XLSData data =(XLSData) iter.next();
			this.area.append("Index : "+data.getIndex()+"\n");
			int startRow=data.getStart();
			int row = data.getRow();
			int col = data.getCol();
			String cellList="";
			String dataArray[][] = new String[row][col];
			int rowCount=0;
			for(int i=startRow;i<startRow+row;i++)
			{
				HSSFRow rows = sheet1.getRow(i);

				for (short j=0;j<col;j++) 
				{	
					HSSFCell cell= rows.getCell(j);

					data.setData(data.getData()+" "+getCellData2( cell));
					dataArray[rowCount][j]=getCellData2(cell); 
				}

				data.setData(data.getData()+((i<=startRow+row)?"\n":""));
				rowCount++;
			}
			data.setDataArray(dataArray);

			this.area.append(data.getData()+"\n");
		}

	}
	/**
	 * @param xlsFile
	 * @throws Exception
	 * @throws IOException
	 */
	private void readIndex(String xlsFile) throws Exception, IOException
	{		
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet1 = wb.getSheetAt(0);
		rows=sheet1.getPhysicalNumberOfRows();
		for (int i=1;i<rows;i++) {

			XLSData data = new XLSData();

			HSSFRow row = sheet1.getRow(i);
			HSSFCell pNum= row.getCell((short) 0);
			HSSFCell celStart= row.getCell((short) 1);
			HSSFCell celRow= row.getCell((short) 2);
			HSSFCell celCol= row.getCell((short) 3);
			data.setIndex((int) Double.parseDouble(this.getCellData2(pNum)));
			data.setStart((int) Double.parseDouble(this.getCellData2(celStart)));
			data.setRow((int) Double.parseDouble(this.getCellData2(celRow)));
			data.setCol((int) Double.parseDouble(this.getCellData2(celCol)));
			tableInfo.add(data);

		}
	}
	
	public boolean checkVersion2003(String xlsFile) {
		String xls=xlsFile.substring(xlsFile.lastIndexOf(".")+1, xlsFile.length());
		if(!xls.equals("xls"))
			return false;
		return true;		
	}
}
