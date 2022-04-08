package com.ksg.adv.logic.xls;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.domain.ShippersTable;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * @author 박창현
 *
 */
public class XLSReader {

	protected Logger logger = LogManager.getLogger(this.getClass());
	
	List<XLSTable> xlsTableList;// 엑셀 테이블 리스트
	
	List<ShippersTable> shippersTableList;// 엑셀 테이블 리스트

	String[] keywordTypeList;

	int keyWordType;

	private final int NONE=0; // 
	private final int VESSEL=1; // VESSEL
	private final int VOYAGE=2; //VOAYGE
	private final int BOTH=3; // VESSEL/VOYAGE


	/**
	 * @param cellContent
	 * @return
	 */
	private int checkKeyword(String cellContent)
	{		
		logger.debug("check keyword:"+cellContent);
		for(int z=0;z<keywordTypeList.length;z++)
		{
			if(cellContent.trim().toLowerCase().equals(keywordTypeList[z].trim().toLowerCase()))
			{
				return z+1;
			}
		}

		return NONE;
	}

	/**
	 * @설명 주어진 셀의 내용
	 * @param cell
	 * @return
	 */
	private String getColumString(HSSFCell cell)
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


	/**
	 * @설명 주어지 엑셀 쉬트에서 테이블을 키워드와 매칭되는 셀을 찾아해서 리스트하는 함수
	 * @param sheet
	 */
	private void searchXLSTable(Sheet sheet)
	{		
		boolean isSearched=false;		

		/**
		 * 쉬트의 0번 row 부터 마지막 row까지 검색
		 */
		
		
		for(int rowIndex=sheet.getFirstRowNum();rowIndex<=sheet.getLastRowNum();rowIndex++)
		{
			HSSFRow row =(HSSFRow) sheet.getRow(rowIndex);

			// 숨김처리 되어 있는 row는 패스 함
			if(row==null||row.getZeroHeight())
				continue;
			
			/*
			 * 쉬트의 n번째 row의 0번째 col부터 마지막 col까지 검색 
			 */
			for(int colIndex=row.getFirstCellNum();colIndex<row.getLastCellNum();colIndex++)
			{
				HSSFCell cell =row.getCell(colIndex);
				
				if(cell==null)
					continue;

				// 숨김처리 되어 있는 col은 패스함
				if(sheet.isColumnHidden(cell.getColumnIndex()))
					continue;

				// 추출된 셀 내용에 대해서 키워드와 동일한지 비교
				keyWordType = checkKeyword(this.getColumString(cell));

				switch (keyWordType) {
				case NONE:// 키워드가 검색되지
					isSearched=false;
					break;
				case VESSEL:
					isSearched=true;
					break;
				case VOYAGE:
					isSearched=true;
					break;
				case BOTH:
					isSearched=true;
					break;
				}
				//키워드가 검색되었을 경우 해당 row와 col을 저장하고 목록에 추가
				if(isSearched)
				{
					logger.debug("search keyword at ("+rowIndex+","+colIndex+")");
					XLSTable xlsTable = new XLSTable(sheet, rowIndex,colIndex);					
					xlsTable.setKeyWordType(keyWordType);					
					xlsTableList.add(xlsTable);
				}
			}

		}
	}
	
	/**
	 * @설명 DB에  저장된 테이블 정보와 엑셀에서 검색된 정보를 매칭
	 *      DB정보 기준으로 매칭하고 매칭 되지 않는 엑셀 정보는 무시
	 *      
	 * @param xlsTableList 엑셀 테이블
	 * @param shipperTables DB 테이블
	 */
	private void matchingDBTable(List<XLSTable> xlsTableList, List<ShippersTable> shipperTables)
	{
		logger.info("matching db table");
		// 검색되 엑셀 정보와 DB 테이블 정보가 매칭 되지 않을 경우
		if(xlsTableList.size()!=shipperTables.size())
		{
			
		}
		// 매칭 시작
		Iterator<ShippersTable> iter = shipperTables.iterator();
		
		int xlsCount=0;
		
		// 
		while(iter.hasNext())
		{
			ShippersTable shippersTable= iter.next();
			
			// 오류 발생 가능
			XLSTable xlsTable=xlsTableList.get(xlsCount);
			
			xlsTable.setShippersTable(shippersTable);
			
			xlsCount++;
			
		}
		
	}
	/**
	 * @param sheetNameList 엑셀 쉬트 목록
	 * @param fileName 엑셀 파일명
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void read(String[] sheetNameList, String fileName) throws FileNotFoundException, IOException
	{	
		logger.info("xls read...");

		updateKeyword();

		xlsTableList = new LinkedList<XLSTable>();

		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(fileName));

		Workbook wb = (Workbook) new HSSFWorkbook(fs);

		for(int i=0;i<sheetNameList.length;i++)
		{
			Sheet sheet=wb.getSheet(sheetNameList[i]);
			searchXLSTable(sheet);
		}
		
		logger.debug("searched table size:"+xlsTableList.size());
		
		matchingDBTable(xlsTableList, shippersTableList);
		
		logger.info("xls read end");
		

	}
	/**
	 * 검색 키워드 초기화 메소드
	 */
	private void updateKeyword()
	{
		keywordTypeList = new String[2];
		keywordTypeList[0]="VESSEL";
		keywordTypeList[1]="VESSEL/VOYAGE";
		logger.info("update keyword");
	}

}
