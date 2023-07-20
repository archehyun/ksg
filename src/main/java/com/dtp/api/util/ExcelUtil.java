package com.dtp.api.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.dtp.api.model.ExcelExport;
import com.ksg.common.util.KSGPropertis;

public class ExcelUtil {
	
	public static void exportData(String fileName, List<ExcelExport> dataList) throws IOException
	{
		Workbook wb = (Workbook) new HSSFWorkbook();
		
		for(ExcelExport data:dataList)
		{
			makeSheetData(wb, data.getSheetName(),data.getColumn(),data.getDataList());
		}
		
		writeFile(wb, fileName);
	}
	
	public static void exportData(String fileName, String column[], List dataList) throws IOException
	{
		Workbook wb = (Workbook) new HSSFWorkbook();
		
		String sheetName="data";

		makeSheetData(wb, sheetName, column, dataList);
		
		writeFile(wb, fileName);
	}
	
	private static void makeSheetData(Workbook wb, String sheetName, String column[], List dataList) throws IOException
	{
		Sheet sheet = wb.createSheet(sheetName);
		
		CreationHelper createHelper = wb.getCreationHelper();
		
		int rowIndex =0;
		
		Row firstrow = sheet.createRow((short)rowIndex);
		
		for(int index=0;index<column.length;index++)
		{
			firstrow.createCell(index).setCellValue(
					createHelper.createRichTextString(column[index]));
		}

		for(int i=0;i<dataList.size();i++)
		{
			Row row = sheet.createRow((short)++rowIndex);

			HashMap<String, Object> info=(HashMap<String, Object>) dataList.get(i);
			
			for(int index=0;index<column.length;index++)
			{
				row.createCell(index).setCellValue(
						createHelper.createRichTextString(getString(info.get(column[index]))));
			}
			
		}
	}
	
	private static void writeFile(Workbook wb, String fileName) throws IOException	
	{
		FileOutputStream fileOut = new FileOutputStream(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName);
		
		wb.write(fileOut);
		
		fileOut.close();
	}
	

	
	private static String getString(Object obj)
	{
		return  obj==null?"":String.valueOf(obj);
		
	}
}
