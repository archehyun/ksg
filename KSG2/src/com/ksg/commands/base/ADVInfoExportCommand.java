package com.ksg.commands.base;

import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.KSGCommand;
import com.ksg.domain.ADVData;
import com.ksg.model.KSGModelManager;

public class ADVInfoExportCommand extends ExportCommand{
	
	private List<ADVData> advList;
	public ADVInfoExportCommand(String fileName){
		super(fileName);
		
		wb = new HSSFWorkbook();
		
		sheetName = "adv";
		
		
	}
	public ADVInfoExportCommand(String sheetName, Workbook wb){
		super(sheetName,wb);
		
	}

	@Override
	public int execute() {
		
		// 선박 정보 조회
		try {
			
			advList = advDAOImpl.getADVDataList();
			
			Sheet sheet = wb.createSheet(sheetName);
			
			CreationHelper createHelper = wb.getCreationHelper();


			Row firstrow = sheet.createRow((short)0);
			firstrow.createCell(0).setCellValue(
					createHelper.createRichTextString("table_id"));
			firstrow.createCell(1).setCellValue(
					createHelper.createRichTextString("data"));
			firstrow.createCell(2).setCellValue(
					createHelper.createRichTextString("data_isusse"));
			firstrow.createCell(3).setCellValue(
					createHelper.createRichTextString("company_abbr"));
			firstrow.createCell(3).setCellValue(
					createHelper.createRichTextString("page"));
			
					

			for(int i=0;i<advList.size();i++)
			{
				Row row = sheet.createRow((short)i+1);
				// Create a cell and put a value in it.

				ADVData info=advList.get(i);
				row.createCell(0).setCellValue(
						createHelper.createRichTextString(info.getTable_id()));
				row.createCell(1).setCellValue(
						createHelper.createRichTextString(info.getData()));
				row.createCell(2).setCellValue(
						createHelper.createRichTextString(info.getDate_isusse().toString()));
				row.createCell(3).setCellValue(info.getPage());
						
			}

			fileWrite(wb);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "파일 생성시 오류가 발생했습니다."+e.getMessage());
		} 
		return KSGCommand.RESULT_SUCCESS;
	}
	
	public static void main(String[] args) {
			ADVInfoExportCommand command = new ADVInfoExportCommand("port_table");
			command.execute();

	}

}
