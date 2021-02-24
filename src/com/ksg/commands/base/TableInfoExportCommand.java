package com.ksg.commands.base;

import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.KSGCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ShippersTable;

public class TableInfoExportCommand extends ExportCommand{
	
	private List<ShippersTable> tableList;
	
	public TableInfoExportCommand(String fileName){
		super(fileName);
		
		wb = new HSSFWorkbook();
		
		sheetName = "table";
		
		
	}
	public TableInfoExportCommand(String sheetName, Workbook wb){
		super(sheetName,wb);
		logger.info(this.sheetName+" xls 파일 생성");
	}

	@Override
	public int execute() {
		
		// 선박 정보 조회
		try {
			
			tableList = tableService.selectTableList();
			
			logger.info("table size:"+tableList.size());
			
			Sheet sheet = wb.createSheet(sheetName);
			
			CreationHelper createHelper = wb.getCreationHelper();


			Row firstrow = sheet.createRow((short)0);
			firstrow.createCell(0).setCellValue(
					createHelper.createRichTextString("table_id"));
			firstrow.createCell(1).setCellValue(
					createHelper.createRichTextString("table_index"));
			firstrow.createCell(2).setCellValue(
					createHelper.createRichTextString("date_isusse"));
			firstrow.createCell(3).setCellValue(
					createHelper.createRichTextString("vsl_row"));
			firstrow.createCell(4).setCellValue(
					createHelper.createRichTextString("title"));
			firstrow.createCell(5).setCellValue(
					createHelper.createRichTextString("out_port"));
			firstrow.createCell(6).setCellValue(
					createHelper.createRichTextString("common_shipping"));
			firstrow.createCell(7).setCellValue(
					createHelper.createRichTextString("company_abbr"));
			firstrow.createCell(8).setCellValue(
					createHelper.createRichTextString("ts"));
			firstrow.createCell(9).setCellValue(
					createHelper.createRichTextString("company_name"));
			firstrow.createCell(10).setCellValue(
					createHelper.createRichTextString("gubun"));
			firstrow.createCell(11).setCellValue(
					createHelper.createRichTextString("page"));
			firstrow.createCell(12).setCellValue(
					createHelper.createRichTextString("agent"));
			firstrow.createCell(13).setCellValue(
					createHelper.createRichTextString("quark_format"));
			firstrow.createCell(14).setCellValue(
					createHelper.createRichTextString("port_col"));
			firstrow.createCell(15).setCellValue(
					createHelper.createRichTextString("out_to_port"));
			firstrow.createCell(16).setCellValue(
					createHelper.createRichTextString("in_to_port"));
			firstrow.createCell(17).setCellValue(
					createHelper.createRichTextString("in_port"));
			firstrow.createCell(18).setCellValue(
					createHelper.createRichTextString("othercell"));
			firstrow.createCell(19).setCellValue(
					createHelper.createRichTextString("console_cfs"));
			firstrow.createCell(20).setCellValue(
					createHelper.createRichTextString("d_time"));
			firstrow.createCell(21).setCellValue(
					createHelper.createRichTextString("c_time"));
			firstrow.createCell(22).setCellValue(
					createHelper.createRichTextString("console_pase"));
			firstrow.createCell(23).setCellValue(
					createHelper.createRichTextString("inland_indexs"));
			firstrow.createCell(24).setCellValue(
					createHelper.createRichTextString("bookPage"));
			
			
					

			for(int i=0;i<tableList.size();i++)
			{
				Row row = sheet.createRow((short)i+1);
				// Create a cell and put a value in it.

				ShippersTable info=tableList.get(i);
				
				row.createCell(0).setCellValue(createHelper.createRichTextString(info.getTable_id()));
				row.createCell(1).setCellValue(info.getTable_index());
				row.createCell(2).setCellValue(createHelper.createRichTextString(info.getDate_isusse()));
				row.createCell(3).setCellValue(info.getVsl_row());				
				row.createCell(4).setCellValue(createHelper.createRichTextString(info.getTitle()));
				row.createCell(5).setCellValue(createHelper.createRichTextString(info.getOut_port()));
				row.createCell(6).setCellValue(createHelper.createRichTextString(info.getCommon_shipping()));
				row.createCell(7).setCellValue(createHelper.createRichTextString(info.getCompany_abbr()));
				row.createCell(8).setCellValue(createHelper.createRichTextString(info.getTS()));
				row.createCell(9).setCellValue(createHelper.createRichTextString(info.getCompany_name()));
				row.createCell(10).setCellValue(createHelper.createRichTextString(info.getGubun()));
				row.createCell(11).setCellValue(info.getPage());
				row.createCell(12).setCellValue(createHelper.createRichTextString(info.getAgent()));
				row.createCell(13).setCellValue(createHelper.createRichTextString(info.getQuark_format()));
				row.createCell(14).setCellValue(info.getPort_col());
				row.createCell(15).setCellValue(createHelper.createRichTextString(info.getOut_to_port()));
				row.createCell(16).setCellValue(createHelper.createRichTextString(info.getIn_to_port()));
				row.createCell(17).setCellValue(createHelper.createRichTextString(info.getIn_port()));
				row.createCell(18).setCellValue(info.getOthercell());
				row.createCell(19).setCellValue(createHelper.createRichTextString(info.getConsole_cfs()));
				row.createCell(20).setCellValue(info.getD_time());
				row.createCell(21).setCellValue(info.getC_time());
				row.createCell(22).setCellValue(createHelper.createRichTextString(info.getConsole_page()));
				row.createCell(23).setCellValue(createHelper.createRichTextString(info.getInland_indexs()));
				row.createCell(24).setCellValue(createHelper.createRichTextString(info.getBookPage()));
						
			}

			fileWrite(wb);
			logger.info("성성 완료");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "파일 생성시 오류가 발생했습니다."+e.getMessage());
		} 
		return KSGCommand.RESULT_SUCCESS;
	}
	
	public static void main(String[] args) {
			TableInfoExportCommand command = new TableInfoExportCommand("port_table");
			command.execute();

	}

}
