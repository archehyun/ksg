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
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.adv.logic.model.SheetInfo;
import com.ksg.adv.logic.model.TableLocation;
import com.ksg.common.util.KSGPropertis;
import com.ksg.domain.ShippersTable;
import com.ksg.workbench.adv.comp.ADVTableNotMatchException;
import com.ksg.workbench.adv.comp.KeyWordManager;

@SuppressWarnings("unchecked")
public class XLSReaderVessel extends XLSReader{


	private boolean emptyCheck;

	private int page;

	String isUnderPort="";

	boolean hasVoy=true;

	KeyWordManager keyWordManager = KeyWordManager.getInstance();



	public XLSReaderVessel()
	{	
		super();

		propertis.reLoad();
		
		isUnderPort = (String)propertis.getValues(KSGPropertis.PROPERTIES_UNDERPORT).toString();
		
		hasVoy = Boolean.parseBoolean(propertis.getValues(KSGPropertis.PROPERTIES_VOY).toString());
		
		this.emptyCheck=Boolean.parseBoolean((String)propertis.getValues("emptyCheck"));
		
		vesselKeyWord = keyWordManager.getVesselKeyWord();
		
		voyKeyWord = keyWordManager.getVoyageKeyWord();
		
		bothKeyWord = keyWordManager.getBothKeyWord();

		String doubleKey=(String) propertis.getValues(KSGPropertis.PROPERTIES_DOUBLEKEY);

	}








	/* (non-Javadoc)
	 * @see com.ksg.adv.logic.parser.XLSReader#getSearchedTableCount()
	 */
	public int getSearchedTableCount() {

		if(tableLocationList!=null)
		{
			return tableLocationList.size();
		}else
		{
			return 0;
		}
	}



	/* (non-Javadoc)
	 * @see com.ksg.adv.logic.parser.XLSReader#readFile(java.lang.String, com.ksg.domain.ShippersTable)
	 */
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


	/* (non-Javadoc)
	 * @see com.ksg.adv.logic.parser.XLSReader#readFile(java.util.Vector, java.util.Vector, java.lang.String, com.ksg.domain.ShippersTable)
	 */
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
	 * 쉬트를 전체 검색하여 테이블 키워드 검색
	 * 키워드
	 *   1. vessel
	 *   2. voy
	 *   3. vessel/voy
	 * @param sheet
	 * @return
	 */
	private Vector<TableLocation> searchTable(Sheet sheet) {
		logger.info("\n<===search table (0 TO "+sheet.getLastRowNum()+")start===>");

		Vector<TableLocation> tableLocationList = new Vector<TableLocation>();

		long starttime=System.currentTimeMillis();

		for(int rowIndex=0;rowIndex<sheet.getLastRowNum();rowIndex++)
		{
			HSSFRow row =(HSSFRow) sheet.getRow(rowIndex);

			if(row==null)
				continue;

			if(row.getZeroHeight())
				continue;


			for(int colIndex=0;colIndex<row.getLastCellNum();colIndex++)
			{
				HSSFCell cell =row.getCell((short) colIndex);

				if(sheet.isColumnHidden(colIndex))
					continue;

				String cellContent=this.getColumString(cell);
				
				if(cellContent.length()==0)
					continue;


				// 키워드(Vessel )이 발견 되는지 확인 함
				int keywordType=checkKeyword(cellContent);
				
				
				if(keywordType!=TableLocation.NONE)
				{
					TableLocation location = new TableLocation(sheet);// 위치정보를 저장할 클래스 생성

					location.setRow(rowIndex);
					location.setCol(colIndex);
					location.setKeyWordCell(cell);
					tableLocationList.add(location);
					
					switch (keywordType) {
					case TableLocation.VESSEL:
						location.setTableType(TableLocation.VESSEL);
						break;
					case TableLocation.VOYAGE:
						location.setTableType(TableLocation.VOYAGE);
						break;	
					case TableLocation.BOTH:
						location.setTableType(TableLocation.BOTH);
						break;	

					default:
						break;
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
		long endtime2= System.currentTimeMillis();
		logger.debug("init time:"+(endtime2-starttime2));
		logger.info("<====Searched Table Count :"+tableLocationList.size()+" ====>");
		return tableLocationList;
	}




	private int checkKeyword(String temp) {
		int keywordType=TableLocation.NONE;
		for(int z=0;z<vesselKeyWord.length;z++)
		{
			if(temp.trim().toLowerCase().equals(vesselKeyWord[z].trim().toLowerCase()))
				return TableLocation.VESSEL;
		}
/*		for(int z=0;z<voyKeyWord.length;z++)
		{
			if(temp.trim().toLowerCase().equals(voyKeyWord[z].trim().toLowerCase()))
				return TableLocation.VOYAGE;
		}*/
		for(int z=0;z<bothKeyWord.length;z++)
		{
			if(temp.trim().toLowerCase().equals(bothKeyWord[z].trim().toLowerCase()))
				return TableLocation.BOTH;
		}
		
		return keywordType;
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
		return xlsTableInfoList;
	}
}
