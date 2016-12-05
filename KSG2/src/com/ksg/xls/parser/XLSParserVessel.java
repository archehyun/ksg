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
package com.ksg.xls.parser;

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

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.ADVService;
import com.ksg.dao.impl.BaseService;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ShippersTable;
import com.ksg.model.KSGModelManager;
import com.ksg.view.util.KSGPropertis;
import com.ksg.xls.ADVTableNotMatchException;
import com.ksg.xls.XLSManager;
import com.ksg.xls.XLSTableInfo;
import com.ksg.xls.model.SheetInfo;
import com.ksg.xls.model.TableLocation;

@SuppressWarnings("unchecked")
public class XLSParserVessel implements XLSManager{
	private String company;
	public String data="";
	private Vector datas = new Vector();
	private boolean emptyCheck;
	private Logger logger = Logger.getLogger(this.getClass());
	//private int other=0;
	private int page;
	private List preData;
	private KSGPropertis propertis = KSGPropertis.getIntance();
	private ADVService service;
	private Sheet sheet;
	public String vesselKeyWord[];
	public String bothKeyWord[];
	public Vector<TableLocation> tableLocationList;
	private TableService tableService;
	private String xlsFile;
	FormulaEvaluator evaluator;
	String isUnderPort="";
	boolean hasVoy=true;
//	private Boolean isDoubleKey;
	//private String upDown;
	BaseService baseService;
	DAOManager manager;
	public XLSParserVessel()
	{	
		try {
		manager = DAOManager.getInstance();
		baseService =manager.createBaseService();
		service = manager.createADVService();
		tableService = manager.createTableService();
		propertis.reLoad();
		isUnderPort = (String)propertis.getValues(KSGPropertis.PROPERTIES_UNDERPORT).toString();
		hasVoy = Boolean.parseBoolean(propertis.getValues(KSGPropertis.PROPERTIES_VOY).toString());
		this.emptyCheck=Boolean.parseBoolean((String)propertis.getValues("emptyCheck"));

		
			List vesselKeyList=baseService.getKeywordList("VESSEL");
			List voyageKeyList=baseService.getKeywordList("VOYAGE");
			List bothKeyList=baseService.getKeywordList("BOTH");

			vesselKeyWord = new String[vesselKeyList.size()];
			bothKeyWord = new String[bothKeyList.size()];

			for(int i=0;i<vesselKeyList.size();i++)
			{
				vesselKeyWord[i]= vesselKeyList.get(i).toString();
			}
			for(int i=0;i<bothKeyList.size();i++)
			{
				bothKeyWord[i]= bothKeyList.get(i).toString();
			}

		}
		catch (SQLException e) 
		{			
			e.printStackTrace();
		}

		String doubleKey=(String) propertis.getValues(KSGPropertis.PROPERTIES_DOUBLEKEY);
	

	}
	

	private Vector xlsTableInfoList;
	public Vector extractData(Sheet sheet,Vector<TableLocation> tableLocation)
	throws ADVTableNotMatchException 
	{
		logger.info("<===="+xlsFile+": data extract start ====>");
		xlsTableInfoList = new Vector();
		Vector datas2 = new Vector();		
		long start= System.currentTimeMillis();
		for(int i=0;i<tableLocation.size();i++)
		{
			// 기존 테이블 정보
			ShippersTable tableInfo = (ShippersTable) preData.get(i);
			// row의 위치 정보
			TableLocation location =(TableLocation) tableLocation.get(i);// 테이블의 시작 위치

			XLSTableInfo info = new XLSTableInfo(tableInfo, location);
			info.setTableInfo(tableInfo);
			info.setTable_id(tableInfo.getTable_id());
//			datas2.add(info.getTableStringInfo());
			xlsTableInfoList.add(info);
			KSGModelManager.getInstance().processBar.setValues(i);
		}
		long end= System.currentTimeMillis();
		logger.debug("search data time:"+(end-start));

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
		return datas;
	}


	public void readFile(String xlsFile,ShippersTable table)
	throws FileNotFoundException, ADVTableNotMatchException{

		try {
			// 파일을 불러옴
			logger.info("read XLS file=>"+xlsFile+",company=>"+table.getCompany_abbr()+" start");
			tableLocationList = new Vector();
			this.company=table.getCompany_abbr();
			this.xlsFile=xlsFile;
			this.page = table.getPage();
			
			
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);
			sheet = wb.getSheetAt(0);
			// 테이블 시작 위치 검색
			tableLocationList=searchTable(sheet);

			datas=extractData(sheet,tableLocationList);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public void readFile(String sheetName, String xlsFile, ShippersTable table)
	throws FileNotFoundException, ADVTableNotMatchException {

		try {
			// 파일을 불러옴
			logger.info("read XLS file=>"+xlsFile+",company=>"+table.getCompany_abbr()+" start");
			tableLocationList = new Vector();
			this.company=table.getCompany_abbr();
			this.xlsFile=xlsFile;
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);

			Sheet sheet=wb.getSheet(sheetName);

			// 테이블 시작 위치 검색
			tableLocationList=searchTable(sheet);

			datas=extractData(sheet,tableLocationList);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,"에러:"+ e.getMessage());
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public Vector<TableLocation> readFile(Vector sheetNameList, String xlsFile, ShippersTable table)
	throws ADVTableNotMatchException, IOException {

		Vector sheetList = new Vector();
		// 파일을 불러옴
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


		// 테이블 시작 위치 검색
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
		// 파일을 불러옴
		tableLocationList = new Vector();

		for(int i=0;i<sheetNameList.size();i++)
		{
			SheetInfo info = (SheetInfo) sheetNameList.get(i);
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(info.filePath));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);
			sheetList.add(wb.getSheet(info.sheetName));
			evaluator= wb.getCreationHelper().createFormulaEvaluator();
		}


		// 테이블 시작 위치 검색
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
		// 파일을 불러옴
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

		// 테이블 시작 위치 검색
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
		long starttime=System.currentTimeMillis();
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
					
					boolean vesselflag = false;
					boolean bothflag = false;

					// 키워드(Vessel )이 발견 되는지 확인 함
					for(int z=0;z<vesselKeyWord.length;z++)
					{
						if(temp.trim().toLowerCase().equals(vesselKeyWord[z].trim().toLowerCase()))
							vesselflag=true;
					}
					//					if(sheet.isColumnHidden(arg0))

					if(vesselflag)// 키워드가 발견되면
					{
						logger.info("table key(vessel) found at row:"+i+", col:"+j+",row h:"+row.getZeroHeight());
						TableLocation location = new TableLocation(sheet);// 위치정보를 저장할 클래스 생성
						location.row=i;
						location.col=j;
						location.setTableType(TableLocation.VESSEL);
						location.setKeyWordCell(cell);
						tableLocationList.add(location);
					}

					// 키워드(VESSEL&VOY )이 발견 되는지 확인 함
					for(int z=0;z<bothKeyWord.length;z++)
					{
						if(temp.trim().equals(bothKeyWord[z].trim()))
							
							bothflag=true;
					}

					if(bothflag)// 키워드가 발견되면
					{
						logger.info("table key(both) found at row:"+i+", col:"+j);
						

						TableLocation location = new TableLocation(sheet);// 위치정보를 저장할 클래스 생성
						location.row=i;
						location.col=j;
						location.setTableType(TableLocation.BOTH);
						location.setKeyWordCell(cell);

						tableLocationList.add(location);
					}
				}
			}
		}
		long endtime= System.currentTimeMillis();
		logger.debug("searching time:"+(endtime-starttime));	
		
		long starttime2= System.currentTimeMillis();
		
		
		//if(isDoubleKey)
		{
			Vector removeLocationList = new Vector();
			for(int i=0;i<tableLocationList.size();i++)
			{
				TableLocation t1=tableLocationList.get(i);
				for(int jj=0;jj<tableLocationList.size();jj++)
				{

					TableLocation t2=tableLocationList.get(jj);
					if(t1.col==t2.col&&t1.row==t2.row)
						continue;
					if(t1.col==t2.col&&Math.abs(t1.row-t2.row)<3)
					{

						if(t1.row>t2.row)
						{
							int tOneIndex = t1.sheet.getWorkbook().getSheetIndex(t1.sheet);
							int tTwoIndex = t2.sheet.getWorkbook().getSheetIndex(t2.sheet);


							if(t1.sheet.getWorkbook().getSheetName(tOneIndex).equals(t2.sheet.getWorkbook().getSheetName(tTwoIndex)))
							{
								logger.debug("remove "+t2);
								removeLocationList.add(t2);
							}

						}else
						{
							int tOneIndex = t1.sheet.getWorkbook().getSheetIndex(t1.sheet);
							int tTwoIndex = t2.sheet.getWorkbook().getSheetIndex(t2.sheet);
							if(t1.sheet.getWorkbook().getSheetName(tOneIndex).equals(t2.sheet.getWorkbook().getSheetName(tTwoIndex)))
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
		long endtime2= System.currentTimeMillis();
		logger.debug("init time:"+(endtime2-starttime2));
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
