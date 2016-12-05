package com.ksg.commands.base;

import java.sql.SQLException;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.KSGCommand;
import com.ksg.domain.Company;
import com.ksg.model.KSGModelManager;

public class CompanyExportCommand extends ExportCommand{

	
	private List<Company> companyList;
	
	public CompanyExportCommand(String fileName) {
		super(fileName);
		
		
		wb = new HSSFWorkbook();
		
		sheetName = "company";
	}
	
	public CompanyExportCommand(String sheetName,Workbook wb) {
		super(sheetName,wb); 
		
		
		
	}

	@Override
	public int execute() {
		
		// 선박 정보 조회
		try {			
			companyList = baseService.getCompanyList();
			Sheet sheet = wb.createSheet(sheetName);
			
			CreationHelper createHelper = wb.getCreationHelper();


			Row firstrow = sheet.createRow((short)0);
			firstrow.createCell(0).setCellValue(
					createHelper.createRichTextString("company_name"));
			firstrow.createCell(1).setCellValue(
					createHelper.createRichTextString("company_abbr"));
			firstrow.createCell(2).setCellValue(
					createHelper.createRichTextString("agent_abbr"));
			firstrow.createCell(3).setCellValue(
					createHelper.createRichTextString("agent_name"));
			firstrow.createCell(4).setCellValue(
					createHelper.createRichTextString("contents"));
			
					

			for(int i=0;i<companyList.size();i++)
			{
				Row row = sheet.createRow((short)i+1);
				// Create a cell and put a value in it.

				Company info=companyList.get(i);
				row.createCell(0).setCellValue(
						createHelper.createRichTextString(info.getCompany_name()));
				row.createCell(1).setCellValue(
						createHelper.createRichTextString(info.getCompany_abbr()));
				row.createCell(2).setCellValue(
						createHelper.createRichTextString(info.getAgent_abbr()));
				row.createCell(3).setCellValue(
						createHelper.createRichTextString(info.getAgent_name()));
				row.createCell(4).setCellValue(
						createHelper.createRichTextString(info.getContents()));
						
			}

			fileWrite(wb);
			logger.info("company table 생성 완료");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "파일 생성시 오류가 발생했습니다."+e.getMessage());
		} 
		return KSGCommand.RESULT_SUCCESS;
	}
	public static void main(String[] args) throws SQLException {
		CompanyExportCommand command = new CompanyExportCommand("company_table");
		command.execute();
	}

}
