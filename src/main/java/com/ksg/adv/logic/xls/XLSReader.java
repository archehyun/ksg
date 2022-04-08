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
 * @author ��â��
 *
 */
public class XLSReader {

	protected Logger logger = LogManager.getLogger(this.getClass());
	
	List<XLSTable> xlsTableList;// ���� ���̺� ����Ʈ
	
	List<ShippersTable> shippersTableList;// ���� ���̺� ����Ʈ

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
	 * @���� �־��� ���� ����
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
	 * @���� �־��� ���� ��Ʈ���� ���̺��� Ű����� ��Ī�Ǵ� ���� ã���ؼ� ����Ʈ�ϴ� �Լ�
	 * @param sheet
	 */
	private void searchXLSTable(Sheet sheet)
	{		
		boolean isSearched=false;		

		/**
		 * ��Ʈ�� 0�� row ���� ������ row���� �˻�
		 */
		
		
		for(int rowIndex=sheet.getFirstRowNum();rowIndex<=sheet.getLastRowNum();rowIndex++)
		{
			HSSFRow row =(HSSFRow) sheet.getRow(rowIndex);

			// ����ó�� �Ǿ� �ִ� row�� �н� ��
			if(row==null||row.getZeroHeight())
				continue;
			
			/*
			 * ��Ʈ�� n��° row�� 0��° col���� ������ col���� �˻� 
			 */
			for(int colIndex=row.getFirstCellNum();colIndex<row.getLastCellNum();colIndex++)
			{
				HSSFCell cell =row.getCell(colIndex);
				
				if(cell==null)
					continue;

				// ����ó�� �Ǿ� �ִ� col�� �н���
				if(sheet.isColumnHidden(cell.getColumnIndex()))
					continue;

				// ����� �� ���뿡 ���ؼ� Ű����� �������� ��
				keyWordType = checkKeyword(this.getColumString(cell));

				switch (keyWordType) {
				case NONE:// Ű���尡 �˻�����
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
				//Ű���尡 �˻��Ǿ��� ��� �ش� row�� col�� �����ϰ� ��Ͽ� �߰�
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
	 * @���� DB��  ����� ���̺� ������ �������� �˻��� ������ ��Ī
	 *      DB���� �������� ��Ī�ϰ� ��Ī ���� �ʴ� ���� ������ ����
	 *      
	 * @param xlsTableList ���� ���̺�
	 * @param shipperTables DB ���̺�
	 */
	private void matchingDBTable(List<XLSTable> xlsTableList, List<ShippersTable> shipperTables)
	{
		logger.info("matching db table");
		// �˻��� ���� ������ DB ���̺� ������ ��Ī ���� ���� ���
		if(xlsTableList.size()!=shipperTables.size())
		{
			
		}
		// ��Ī ����
		Iterator<ShippersTable> iter = shipperTables.iterator();
		
		int xlsCount=0;
		
		// 
		while(iter.hasNext())
		{
			ShippersTable shippersTable= iter.next();
			
			// ���� �߻� ����
			XLSTable xlsTable=xlsTableList.get(xlsCount);
			
			xlsTable.setShippersTable(shippersTable);
			
			xlsCount++;
			
		}
		
	}
	/**
	 * @param sheetNameList ���� ��Ʈ ���
	 * @param fileName ���� ���ϸ�
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
	 * �˻� Ű���� �ʱ�ȭ �޼ҵ�
	 */
	private void updateKeyword()
	{
		keywordTypeList = new String[2];
		keywordTypeList[0]="VESSEL";
		keywordTypeList[1]="VESSEL/VOYAGE";
		logger.info("update keyword");
	}

}
