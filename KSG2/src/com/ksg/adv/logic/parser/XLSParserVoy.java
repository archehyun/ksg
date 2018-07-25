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
package com.ksg.adv.logic.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.adv.logic.model.SheetInfo;
import com.ksg.adv.logic.model.TableLocation;
import com.ksg.adv.service.ADVService;
import com.ksg.adv.view.comp.ADVTableNotMatchException;
import com.ksg.adv.view.comp.XLSManager;
import com.ksg.adv.view.comp.XLSTableInfo;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.ShippersTable;
import com.ksg.shippertable.service.TableService;

@SuppressWarnings("unchecked")
public class XLSParserVoy implements XLSManager{
	private String company;
	public String data="";
	private Vector datas = new Vector();
	private boolean emptyCheck;
	private Logger logger = Logger.getLogger(this.getClass());
	private int other=0;
	private int page;
	private List preData;
	private KSGPropertis propertis = KSGPropertis.getIntance();
	private ADVService service;
	private Sheet sheet;
	public String vesselKeyWord[];
	public String bothKeyWord[];
	public String voyageKeyWord[];
	public Vector<TableLocation> tableLocationList;
	private TableService tableService;
	private String xlsFile;
	FormulaEvaluator evaluator;
	String isUnderPort="";
	boolean hasVoy=true;
	private Boolean isDoubleKey;
	private String upDown;
	BaseService baseService;
	DAOManager manager;
	public XLSParserVoy()
	{	
		manager = DAOManager.getInstance();
		baseService =manager.createBaseService();
		service = manager.createADVService();
		tableService = manager.createTableService();
		propertis.reLoad();
		isUnderPort = (String)propertis.getValues(KSGPropertis.PROPERTIES_UNDERPORT).toString();
		hasVoy = Boolean.parseBoolean(propertis.getValues(KSGPropertis.PROPERTIES_VOY).toString());
		this.emptyCheck=Boolean.parseBoolean((String)propertis.getValues("emptyCheck"));

		try {
			//List vesselKeyList=baseService.getKeywordList("VESSEL");
			List voyageKeyList=baseService.getKeywordList("VOYAGE");
//			List bothKeyList=baseService.getKeywordList("BOTH");

			//vesselKeyWord = new String[vesselKeyList.size()];
			//bothKeyWord = new String[bothKeyList.size()];
			voyageKeyWord= new String[voyageKeyList.size()];
			
			
/*
			for(int i=0;i<vesselKeyList.size();i++)
			{
				vesselKeyWord[i]= vesselKeyList.get(i).toString();
			}
			for(int i=0;i<bothKeyList.size();i++)
			{
				bothKeyWord[i]= bothKeyList.get(i).toString();
			}*/
			for(int i=0;i<voyageKeyList.size();i++)
			{
				voyageKeyWord[i]= voyageKeyList.get(i).toString();
			}
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String doubleKey=(String) propertis.getValues(KSGPropertis.PROPERTIES_DOUBLEKEY);
		StringTokenizer st3 = new StringTokenizer(doubleKey,"|");
		isDoubleKey = Boolean.parseBoolean(st3.nextToken());
		upDown = st3.nextToken();


	}
	public XLSParserVoy(int other)
	{		
		this();

		this.other=other;
	}

	Vector xlsTableInfoList;
	public Vector extractData(Sheet sheet,Vector<TableLocation> tableLocation)
	throws ADVTableNotMatchException 
	{
		xlsTableInfoList = new Vector();
		Vector datas2 = new Vector();
		logger.info("<===="+xlsFile+": data extract start ====>");

		for(int i=0;i<tableLocation.size();i++)
		{
			// ���� ���̺� ����
			ShippersTable tableInfo = (ShippersTable) preData.get(i);
			// row�� ��ġ ����
			TableLocation location =(TableLocation) tableLocation.get(i);// ���̺��� ���� ��ġ

			XLSTableInfo info = new XLSTableInfo(tableInfo, location);
			info.setTableInfo(tableInfo);
			info.setTable_id(tableInfo.getTable_id());
			xlsTableInfoList.add(info);
		}

		return datas2;

	}

	public String getColumString(HSSFCell cell)
	{
		if(cell==null)
			return "";

		switch (cell.getCellType()) 
		{
		case HSSFCell.CELL_TYPE_STRING:

			return cell.getRichStringCellValue().toString();
		default:

			break;
		}
		return "";
	}

	public String getData() {
		return data;
	}

	public Vector getErrorList() {
		return null;
	}

	public int getSearchedTableCount() {

		if(tableLocationList!=null)
		{
			return tableLocationList.size();
		}else
		{
			return 0;
		}
	}


	public Vector getXLSData() {
		// TODO Auto-generated method stub
		return datas;
	}


	public void readFile(String xlsFile,ShippersTable table)
	throws FileNotFoundException, ADVTableNotMatchException{

		try {
			// ������ �ҷ���
			logger.info("read XLS file=>"+xlsFile+",company=>"+table.getCompany_abbr()+" start");
			tableLocationList = new Vector();
			this.company=table.getCompany_abbr();
			this.xlsFile=xlsFile;
			this.page = table.getPage();
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);
			sheet = wb.getSheetAt(0);
			// ���̺� ���� ��ġ �˻�
			tableLocationList=searchTable(sheet);

			datas=extractData(sheet,tableLocationList);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public void readFile(String sheetName, String xlsFile, ShippersTable table)
	throws FileNotFoundException, ADVTableNotMatchException {

		try {
			// ������ �ҷ���
			logger.info("read XLS file=>"+xlsFile+",company=>"+table.getCompany_abbr()+" start");
			tableLocationList = new Vector();
			this.company=table.getCompany_abbr();
			this.xlsFile=xlsFile;
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);

			Sheet sheet=wb.getSheet(sheetName);

			// ���̺� ���� ��ġ �˻�
			tableLocationList=searchTable(sheet);

			datas=extractData(sheet,tableLocationList);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,"����:"+ e.getMessage());
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public Vector<TableLocation> readFile(Vector sheetNameList, String xlsFile, ShippersTable table)
	throws ADVTableNotMatchException, IOException {

		Vector sheetList = new Vector();
		// ������ �ҷ���
		logger.info("read XLS file=>"+xlsFile+",company=>"+table.getCompany_abbr()+" start");
		tableLocationList = new Vector();
		this.company=table.getCompany_abbr();
		this.xlsFile=xlsFile;

		for(int i=0;i<sheetNameList.size();i++)
		{
			SheetInfo info = (SheetInfo) sheetNameList.get(i);
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(info.filePath));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);
			sheetList.add(wb.getSheet(info.sheetName));
			evaluator= wb.getCreationHelper().createFormulaEvaluator();
		}


		// ���̺� ���� ��ġ �˻�
		tableLocationList=searchTable(sheetList);
		if(preData.size()<tableLocationList.size())
		{
			throw new ADVTableNotMatchException(preData.size(),tableLocationList.size());
		}
		datas=extractData(sheet,tableLocationList);

		return tableLocationList;
	}
	public Vector<TableLocation> readFile(Vector sheetNameList)
	throws ADVTableNotMatchException, IOException {

		Vector sheetList = new Vector();
		// ������ �ҷ���
		tableLocationList = new Vector();

		for(int i=0;i<sheetNameList.size();i++)
		{
			SheetInfo info = (SheetInfo) sheetNameList.get(i);
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(info.filePath));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);
			sheetList.add(wb.getSheet(info.sheetName));
			evaluator= wb.getCreationHelper().createFormulaEvaluator();
		}


		// ���̺� ���� ��ġ �˻�
		tableLocationList=searchTable(sheetList);
		if(preData.size()<tableLocationList.size())
		{
			throw new ADVTableNotMatchException(preData.size(),tableLocationList.size());
		}
		datas=extractData(sheet,tableLocationList);

		return tableLocationList;
	}
	

	public Vector<TableLocation> readFile(Vector pageList, Vector sheetNameList, String xlsFile,
			ShippersTable table) throws FileNotFoundException,
			ADVTableNotMatchException, IOException {
		// ������ �ҷ���
		logger.info("read XLS file=>"+xlsFile+",company=>"+table.getCompany_abbr()+" start");
		tableLocationList = new Vector();
		this.company=table.getCompany_abbr();
		this.xlsFile=xlsFile;
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
		Workbook wb = (Workbook) new HSSFWorkbook(fs);

		evaluator= wb.getCreationHelper().createFormulaEvaluator();
		Vector sheetList = new Vector();
		for(int i=0;i<sheetNameList.size();i++)
		{
			sheetList.add(wb.getSheet(sheetNameList.get(i).toString()));
		}

		// ���̺� ���� ��ġ �˻�
		tableLocationList=searchTable(sheetList);
		if(preData.size()<tableLocationList.size())
		{
			throw new ADVTableNotMatchException(preData.size(),tableLocationList.size());
		}
		datas=extractData(sheet,tableLocationList);
		JOptionPane.showMessageDialog(null, datas);
		return tableLocationList;

	}

	/**
	 * @param sheet
	 * @return
	 */
	private Vector<TableLocation> searchTable(Sheet sheet) {
		logger.info("<===search table (0 TO "+sheet.getLastRowNum()+")start===>");
		Vector<TableLocation> tableLocationList = new Vector<TableLocation>();
		for(int i=0;i<sheet.getLastRowNum();i++)
		{
			HSSFRow row =(HSSFRow) sheet.getRow(i);

			if(row!=null&&!row.getZeroHeight())
			{
				for(int j=0;j<row.getLastCellNum();j++)
				{
					HSSFCell cell =row.getCell((short) j);

					if(sheet.isColumnHidden(j))
						continue;

					String temp=this.getColumString(cell);
					if(temp.length()==0)
						continue;
					
					boolean voyflag = false;

			/*		// Ű����(Vessel )�� �߰� �Ǵ��� Ȯ�� ��
					for(int z=0;z<vesselKeyWord.length;z++)
					{
						if(temp.trim().toLowerCase().equals(vesselKeyWord[z].trim().toLowerCase()))
							vesselflag=true;
					}
					//					if(sheet.isColumnHidden(arg0))

					if(vesselflag)// Ű���尡 �߰ߵǸ�
					{
						logger.info("table key(vessel) found at row:"+i+", col:"+j+",row h:"+row.getZeroHeight());
						TableLocation location = new TableLocation(sheet);// ��ġ������ ������ Ŭ���� ����
						location.row=i;
						location.col=j;
						location.setTableType(TableLocation.VESSEL);
						location.setKeyWordCell(cell);
						tableLocationList.add(location);
					}

					// Ű����(VESSEL&VOY )�� �߰� �Ǵ��� Ȯ�� ��
					for(int z=0;z<bothKeyWord.length;z++)
					{
						if(temp.trim().equals(bothKeyWord[z].trim()))
							
							bothflag=true;
					}

					if(bothflag)// Ű���尡 �߰ߵǸ�
					{
						logger.info("table key(both) found at row:"+i+", col:"+j);
						

						TableLocation location = new TableLocation(sheet);// ��ġ������ ������ Ŭ���� ����
						location.row=i;
						location.col=j;
						location.setTableType(TableLocation.BOTH);
						location.setKeyWordCell(cell);

						tableLocationList.add(location);
					}*/
					// Ű����(VOY )�� �߰� �Ǵ��� Ȯ�� ��
					for(int z=0;z<voyageKeyWord.length;z++)
					{
						if(temp.trim().equals(voyageKeyWord[z].trim()))
							
							voyflag=true;
					}
					if(voyflag)// Ű���尡 �߰ߵǸ�
					{
						logger.info("table key(voy) found at row:"+i+", col:"+j);
						

						TableLocation location = new TableLocation(sheet);// ��ġ������ ������ Ŭ���� ����
						
						location.setRow(i);
						location.setCol(j-1);
						
						location.setTableType(TableLocation.VOYAGE);
						location.setKeyWordCell(cell);

						tableLocationList.add(location);
					}
					
				}
			}
		}

		//if(isDoubleKey)
		{
			Vector removeLocationList = new Vector();
			for(int i=0;i<tableLocationList.size();i++)
			{
				TableLocation t1=tableLocationList.get(i);
				for(int jj=0;jj<tableLocationList.size();jj++)
				{

					TableLocation t2=tableLocationList.get(jj);
					if(t1.getCol()==t2.getCol()&&t1.getRow()==t2.getRow())
						continue;
					if(t1.getCol()==t2.getCol()&&Math.abs(t1.getRow()-t2.getRow())<3)
					{

						if(t1.getRow()>t2.getRow())
						{
							int tOneIndex = t1.getSheet().getWorkbook().getSheetIndex(t1.getSheet());
							int tTwoIndex = t2.getSheet().getWorkbook().getSheetIndex(t2.getSheet());


							if(t1.getSheet().getWorkbook().getSheetName(tOneIndex).equals(t2.getSheet().getWorkbook().getSheetName(tTwoIndex)))
							{
								logger.debug("remove "+t2);
								removeLocationList.add(t2);
							}

						}else
						{
							int tOneIndex = t1.getSheet().getWorkbook().getSheetIndex(t1.getSheet());
							int tTwoIndex = t2.getSheet().getWorkbook().getSheetIndex(t2.getSheet());
							if(t1.getSheet().getWorkbook().getSheetName(tOneIndex).equals(t2.getSheet().getWorkbook().getSheetName(tTwoIndex)))
							{
								removeLocationList.add(t1);
								logger.debug("remove "+t1);
							}
						}

					}
				}
			}

			for(int i=0;i<removeLocationList.size();i++)
			{
				tableLocationList.remove(removeLocationList.get(i));		
			}
		}
		logger.info("<====Searched Table Count :"+tableLocationList.size()+" ====>");
		return tableLocationList;
	}


	private Vector<TableLocation> searchTable(Vector sheetList) {
		for(int i=0;i<sheetList.size();i++)
		{
			Sheet sheet = (Sheet) sheetList.get(i);
			Vector<TableLocation> tableList = this.searchTable(sheet);

			for(int j=0;j<tableList.size();j++)
				tableLocationList.add(tableList.get(j));
		}
		return tableLocationList;

	}
	public void setEmptyCheck(boolean emptyCheck) {
		this.emptyCheck = emptyCheck;
	}
	public void setPreData(List li) {
		this.preData=li;

	}
	public Vector getXLSTableInfoList() {
		// TODO Auto-generated method stub
		return xlsTableInfoList;
	}
}