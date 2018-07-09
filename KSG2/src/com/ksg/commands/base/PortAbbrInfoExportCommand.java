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
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.PortInfo;

public class PortAbbrInfoExportCommand extends ExportCommand{
	
	private List<PortInfo> portAbbrList;
	
	public PortAbbrInfoExportCommand(String fileName)  throws SQLException{
		super(fileName);
		
		wb = new HSSFWorkbook();
		
		sheetName = "portAbbr";
		
		portAbbrList = baseService.getPortAbbrList();
		
		
	}
	public PortAbbrInfoExportCommand(String sheetName, Workbook wb) throws SQLException {
		super(sheetName,wb);
		
		portAbbrList = baseService.getPortAbbrList();
		
	}

	@Override
	public int execute() {
		
		// 선박 정보 조회
		try {
			
			
			Sheet sheet = wb.createSheet(sheetName);
			
			CreationHelper createHelper = wb.getCreationHelper();


			Row firstrow = sheet.createRow((short)0);
			firstrow.createCell(0).setCellValue(
					createHelper.createRichTextString("port_name"));
			firstrow.createCell(1).setCellValue(
					createHelper.createRichTextString("port_abbr"));	
			
					

			for(int i=0;i<portAbbrList.size();i++)
			{
				Row row = sheet.createRow((short)i+1);
				// Create a cell and put a value in it.

				PortInfo info=portAbbrList.get(i);
				row.createCell(0).setCellValue(
						createHelper.createRichTextString(info.getPort_name()));
				row.createCell(1).setCellValue(
						createHelper.createRichTextString(info.getPort_abbr()));
				
						
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
		try {
			PortAbbrInfoExportCommand command = new PortAbbrInfoExportCommand("port_table");
			command.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
