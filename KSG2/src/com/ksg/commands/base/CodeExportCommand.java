package com.ksg.commands.base;

import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.KSGCommand;
import com.ksg.domain.PortInfo;
import com.ksg.model.KSGModelManager;

public class CodeExportCommand extends ExportCommand{
	
	private List<PortInfo> portList;
	public CodeExportCommand(String fileName){
		super(fileName);
		
		wb = new HSSFWorkbook();
		
		sheetName = "code";
		
		
	}
	public CodeExportCommand(String sheetName, Workbook wb){
		super(sheetName,wb);
		
	}

	@Override
	public int execute() {
		
		// 선박 정보 조회
		try {
			
			portList = baseService.getPortInfoList();
			
			Sheet sheet = wb.createSheet(sheetName);
			
			CreationHelper createHelper = wb.getCreationHelper();


			Row firstrow = sheet.createRow((short)0);
			firstrow.createCell(0).setCellValue(
					createHelper.createRichTextString("code_field"));
			firstrow.createCell(1).setCellValue(
					createHelper.createRichTextString("code_name"));
			firstrow.createCell(2).setCellValue(
					createHelper.createRichTextString("code_type"));
			firstrow.createCell(3).setCellValue(
					createHelper.createRichTextString("code_name_kor"));
			
					

			for(int i=0;i<portList.size();i++)
			{
				Row row = sheet.createRow((short)i+1);
				// Create a cell and put a value in it.

				PortInfo info=portList.get(i);
				row.createCell(0).setCellValue(
						createHelper.createRichTextString(info.getPort_name()));
				row.createCell(1).setCellValue(
						createHelper.createRichTextString(info.getPort_nationality()));
				row.createCell(2).setCellValue(
						createHelper.createRichTextString(info.getPort_area()));
				row.createCell(3).setCellValue(
						createHelper.createRichTextString(info.getArea_code()));
						
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
			CodeExportCommand command = new CodeExportCommand("port_table");
			command.execute();

	}

}
