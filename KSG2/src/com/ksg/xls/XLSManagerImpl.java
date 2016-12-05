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
package com.ksg.xls;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.xls.ImportXLSFileCommand;
import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ShippersTable;
import com.ksg.xls.model.TableLocation;
import com.ksg.xls.parser.XLSParserVessel;
import com.ksg.xls.parser.XLSParserVoy;

/**
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class XLSManagerImpl{

	private static XLSManagerImpl managerImpl;
	public static XLSManagerImpl getInstance() {
		if(managerImpl==null)
			managerImpl = new XLSManagerImpl();
		return managerImpl;
	}
	private XLSErrorHandler errorHandler;
	private Logger logger = Logger.getLogger(this.getClass());
	private DAOManager manager = DAOManager.getInstance();
	public int other=0;

	private XLSManager parser;

	public Vector<TableLocation> tableLocation;
	private TableService tableService;
	private XLSManagerImpl() {
		tableService = manager.createTableService();

		errorHandler = new XLSErrorHandler();
	}

	public XLSTableInfoMemento createXLSTableInfoMemento()
	{

		XLSTableInfoMemento m = new XLSTableInfoMemento();
		for(int i=0;i<this.getXLSTableInfoList().size();i++)
		{
			m.addXLSTableInfo(this.getXLSTableInfoList().get(i));
		}
		return m;
	}

	public Vector extractData(Sheet sheet, Vector<TableLocation> tableLocation)
	throws ADVTableNotMatchException {
		return parser.extractData(sheet, tableLocation);
	}
	public String getData() {
		return parser.getData();
	}
	public Vector getErrorList()
	{
		return errorHandler.getErrorList();
	}

	public int getSearchedTableCount() {
		return parser.getSearchedTableCount();
	}

	public List getSheetNameList(String xlsFile)
	{
		try
		{
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);
			int sheetCount=wb.getNumberOfSheets();
			List sheetList =new LinkedList();
			for(int i=0;i<sheetCount;i++)
			{	
				sheetList.add(wb.getSheetName(i));
			}
			return sheetList;

		}catch(FileNotFoundException fe)
		{
			JOptionPane.showMessageDialog(null, fe.getMessage());
			fe.printStackTrace();
			return null;
		}catch(IOException ie)
		{
			JOptionPane.showMessageDialog(null, ie.getMessage());
			ie.printStackTrace();
			return null;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public Sheet getXLSSheet(String filePath, String sheetName) throws FileNotFoundException, IOException 
	{
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filePath));
		Workbook wb = (Workbook) new HSSFWorkbook(fs);
		return wb.getSheet(sheetName);

	}
	public Vector<XLSTableInfo> getXLSTableInfoList()
	{
		return parser.getXLSTableInfoList();
	}

	public void readFile(String sheetName, String xlsFile, ShippersTable table)
	throws FileNotFoundException, ADVTableNotMatchException {
		try {
			readInit(table);
			parser.readFile(sheetName,xlsFile,table);
		} 
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,xlsFile+" 파일이 없습니다.");
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}

	}


	public void readFile(Vector tableList, Vector sheetList) throws ADVTableNotMatchException, IOException
	{
		parser		 = new XLSParserVessel();
		List li = new LinkedList();
		for(int i=0;i<tableList.size();i++)
		{
			li.add(tableList.get(i));
		}
		parser.setPreData(li);
		parser.readFile(sheetList);
	}
	

	public Vector<TableLocation> readFile(Vector pageList, Vector sheetList, String xlsFile,
			ShippersTable table,int keyType) throws FileNotFoundException,
			ADVTableNotMatchException, IOException {

		try {
			logger.debug("read xls file start");

			switch (keyType) {
			case ImportXLSFileCommand.VESSEL:
				parser		 = new XLSParserVessel();
				break;
			case ImportXLSFileCommand.VOYAGE:
				parser		 = new XLSParserVoy();	
				break;

			default:
				break;
			}			

			List li = new LinkedList();

			for(int i=0;i<pageList.size();i++)
			{
				ShippersTable t=(ShippersTable) pageList.get(i);
				List templi = tableService.getTableListByCompany(t.getCompany_abbr(),t.getPage());

				for(int j=0;j<templi.size();j++)
				{
					li.add(templi.get(j));
				}
			}

			if(li.size()==0)
			{
				JOptionPane.showMessageDialog(null,"테이블 정보가 없습니다. 테이블을 생성하십시요");
				return null;
			}


			parser.setPreData(li);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parser.readFile(sheetList,xlsFile,table);
	}

	/**
	 * @param table
	 * @throws SQLException
	 */
	public void readInit(ShippersTable table) throws SQLException
	{
		logger.debug("start");
		parser		 = new XLSParserVessel();

		List li=tableService.getTableListByCompany(table.getCompany_abbr(),table.getPage());
		if(li.size()==0)
		{
			JOptionPane.showMessageDialog(null,"테이블 정보가 없습니다. 테이블을 생성하십시요");
			return;
		}
		logger.debug("end");
		parser.setPreData(li);
	}
}
