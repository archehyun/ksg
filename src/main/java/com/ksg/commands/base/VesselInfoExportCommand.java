package com.ksg.commands.base;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.KSGCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;


@Deprecated
public class VesselInfoExportCommand extends ExportCommand{
	
	
	

	private List<Vessel> vesselList;
	
	private VesselService service = new VesselServiceImpl();

	public VesselInfoExportCommand(String fileName)  {
		
		super(fileName);
		sheetName = "vessel";
	}
	
	public VesselInfoExportCommand(String sheetName,Workbook wb)  {		
		super(sheetName, wb);
		
	}
	
	

	@Override
	public int execute() {


		// 선박 정보 조회
		try {
			
			wb = (Workbook) new HSSFWorkbook();
			
			HashMap<String,Object> result = service.selectList(new HashMap<String, Object>());
			
			List vesselList = (List) result.get("master");
			//vesselList = baseService.selectList(new Vessel());		
			
			
			Sheet sheet = wb.createSheet(sheetName);
			
			CreationHelper createHelper = wb.getCreationHelper();

			Row firstrow = sheet.createRow((short)0);
			firstrow.createCell(0).setCellValue(
					createHelper.createRichTextString("vessel_name"));
			firstrow.createCell(1).setCellValue(
					createHelper.createRichTextString("vessel_abbr"));
			firstrow.createCell(2).setCellValue(
					createHelper.createRichTextString("vessel_type"));
			firstrow.createCell(3).setCellValue(
					createHelper.createRichTextString("vessel_use"));
			firstrow.createCell(4).setCellValue(
					createHelper.createRichTextString("vessel_company"));
			firstrow.createCell(5).setCellValue(
					createHelper.createRichTextString("vessel_mmsi"));
			firstrow.createCell(6).setCellValue(
					createHelper.createRichTextString("input_date"));


			for(int i=0;i<vesselList.size();i++)
			{
				Row row = sheet.createRow((short)i+1);
				// Create a cell and put a value in it.

				HashMap<String, Object> info=(HashMap<String, Object>) vesselList.get(i);
				row.createCell(0).setCellValue(
						createHelper.createRichTextString((String) info.get("vessel_name")));
				row.createCell(1).setCellValue(
						createHelper.createRichTextString((String) info.get("vessel_abbr")));
				row.createCell(2).setCellValue(
						createHelper.createRichTextString((String) info.get("vessel_type")));
				row.createCell(3).setCellValue(
						createHelper.createRichTextString(String.valueOf(info.get("vessel_use"))));
				row.createCell(4).setCellValue(
						createHelper.createRichTextString((String) info.get("vessel_company")));
				row.createCell(5).setCellValue(
						createHelper.createRichTextString((String) info.get("vessel_mmsi")));
				
				String varInput_date = (String)info.get("input_date");
				logger.debug("date info:"+varInput_date);
				row.createCell(6).setCellValue(
						createHelper.createRichTextString(								
								varInput_date==null?"":varInput_date));

			}
			
			fileWrite(wb);
			logger.info("vessel table 생성 완료");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "파일 생성시 오류가 발생했습니다."+e.getMessage());
		} 
		return KSGCommand.RESULT_SUCCESS;
	}


	

}
