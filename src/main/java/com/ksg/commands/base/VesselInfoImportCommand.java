package com.ksg.commands.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.IFCommand;
import com.ksg.domain.Vessel;
import com.ksg.service.VesselService;
import com.ksg.service.impl.VesselServiceImpl;

public class VesselInfoImportCommand extends ImportCommand {


	Vessel insertParameter=null;
	
	HashMap<String, Object> param;
	
	VesselService service = new VesselServiceImpl();

	public VesselInfoImportCommand(File xlsfile) throws FileNotFoundException, IOException {
		super();
		POIFSFileSystem fs= new POIFSFileSystem(new FileInputStream(xlsfile));
		wb = (Workbook) new HSSFWorkbook(fs);
		sheet = wb.getSheet("vessel");
		lengthOfTask = sheet.getLastRowNum();
		current=1;
		isdone=false;
	}
	
	public VesselInfoImportCommand(HSSFWorkbook wb) throws FileNotFoundException, IOException {
		super();		
		this.wb = (Workbook) wb;
		sheet = wb.getSheet("vessel");
		lengthOfTask = sheet.getLastRowNum();
		current=1;
		isdone=false;
	}

	public int execute()
	{
		
		
		try{

			this.message = sheet.getLastRowNum()+"개 선박정보 가져오는중";
			

			for(int i=1;i<=sheet.getLastRowNum();i++)
			{
				HSSFRow row =(HSSFRow) sheet.getRow(i);
				Cell cell0 =row.getCell(0, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_name
				Cell cell1 =row.getCell(1, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_abbr
				Cell cell2 =row.getCell(2, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_type
				Cell cell3 =row.getCell(3, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_use
				Cell cell4 =row.getCell(4, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_company
				Cell cell5 =row.getCell(5, HSSFRow.RETURN_BLANK_AS_NULL);//vessel_mmsi
				Cell cell6 =row.getCell(6, HSSFRow.RETURN_BLANK_AS_NULL);//input_date
				insertParameter = new Vessel();

				insertParameter.setVessel_name(cell0.getStringCellValue());
				insertParameter.setVessel_abbr(cell1.getStringCellValue());
				insertParameter.setVessel_type(cell2.getStringCellValue());
				insertParameter.setVessel_use(this.getVesselUse(cell3));					
				insertParameter.setVessel_company(cell4.getStringCellValue());
				insertParameter.setVessel_mmsi(cell5.getStringCellValue());
			//	insertParameter.setInput_date(cell6.getStringCellValue().equals("")?null:format.parse(cell6.getStringCellValue()));

				logger.info("xls insert:"+insertParameter.toInfoString());
				
				param = new HashMap<String, Object>();
				
				param.put("vessel_name", cell0.getStringCellValue());
				param.put("vessel_abbr", cell1.getStringCellValue());
				param.put("vessel_type", cell2.getStringCellValue());
				param.put("vessel_use", this.getVesselUse(cell3));
				param.put("vessel_company", cell4.getStringCellValue());
				param.put("vessel_mmsi", cell5.getStringCellValue());
				param.put("input_date", cell6.getStringCellValue().equals("")?null:format.parse(cell6.getStringCellValue()));
				
				
				//service.insert(param);
				//baseService.insertVessel(insertParameter);
				current++;
			}
		}
		catch (RuntimeException e1) 
		{
			e1.printStackTrace();
//
//			// 동일한 항목이 있을 경우
//			if(e1.getErrorCode()==2627)
//			{
//				try 
//				{
//					service.update(param);
//					//baseService.update(insertParameter);
//				} catch (SQLException e2) 
//				{
//					// TODO Auto-generated catch block
//					e2.printStackTrace();
//				}
//			}
//			else
//			{
//				logger.error(e1.getErrorCode()+":"+e1.getMessage()+" : "+insertParameter.toInfoString());
//			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			isdone=true;
		}

		return IFCommand.RESULT_SUCCESS;
	}
	private int getVesselUse(Cell vesselUseCell)
	{
		int vesselUse;
		try{
			switch (vesselUseCell.getCellType()) {
			case HSSFCell.CELL_TYPE_STRING:
				vesselUse= Integer.valueOf(vesselUseCell.getStringCellValue());

				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				vesselUse =(int) vesselUseCell.getNumericCellValue();
				break;
			case HSSFCell.CELL_TYPE_BLANK:
				vesselUse =Vessel.USE;
			default:
				vesselUse =Vessel.USE;
				break;
			}
		}catch(Exception e)
		{
			return Vessel.USE;
		}

		return vesselUse;
	}


}
