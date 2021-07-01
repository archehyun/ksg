package com.ksg.commands.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.KSGCommand;
import com.ksg.common.dao.DAOImplManager;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.domain.PortInfo;

public class PortInfoImportCommand extends ImportCommand{
	
	private PortInfo insertParameter;


	public PortInfoImportCommand(File xlsfile) throws FileNotFoundException, IOException {
		super();
		POIFSFileSystem fs= new POIFSFileSystem(new FileInputStream(xlsfile));
		wb = (Workbook) new HSSFWorkbook(fs);
		sheet = wb.getSheet("port");
		lengthOfTask = sheet.getLastRowNum();
		current=1;
		isdone=false;
	}
	
	public PortInfoImportCommand(HSSFWorkbook wb) throws FileNotFoundException, IOException {
		super();		
		this.wb = (Workbook) wb;
		sheet = wb.getSheet("port");
		lengthOfTask = sheet.getLastRowNum();
		current=1;
		isdone=false;
	}
	

	@Override
	public int execute() {
		try{

			this.message = sheet.getLastRowNum()+"개 선박정보 가져오는중";


			for(int i=1;i<=sheet.getLastRowNum();i++)
			{
				HSSFRow row =(HSSFRow) sheet.getRow(i);
				Cell cell0 =row.getCell(0, HSSFRow.RETURN_BLANK_AS_NULL);//port_name
				Cell cell1 =row.getCell(1, HSSFRow.RETURN_BLANK_AS_NULL);//port_nationality
				Cell cell2 =row.getCell(2, HSSFRow.RETURN_BLANK_AS_NULL);//port_area
				Cell cell3 =row.getCell(3, HSSFRow.RETURN_BLANK_AS_NULL);//area_code				
				insertParameter = new PortInfo();

				insertParameter.setPort_name(cell0.getStringCellValue());
				insertParameter.setPort_nationality(cell1.getStringCellValue());
				insertParameter.setPort_area(cell2.getStringCellValue());
				insertParameter.setArea_code(cell2.getStringCellValue());					
				
				logger.info("xls insert:"+insertParameter.toInfoString());
				baseService.insertPort(insertParameter);
				current++;
			}
		}
		catch (SQLException e1) 
		{
			e1.printStackTrace();

			// 동일한 항목이 있을 경우
			if(e1.getErrorCode()==2627)
			{
				try 
				{
					baseService.update(insertParameter);
				} catch (SQLException e2) 
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			else
			{
				logger.error(e1.getErrorCode()+":"+e1.getMessage()+" : "+insertParameter.toInfoString());
			}
		}
		finally
		{
			isdone=true;
		}

		return KSGCommand.RESULT_SUCCESS;
	}
	
	public static void main(String[] args) {
		
		BaseDAOManager baseService=DAOImplManager.getInstance().createBaseDAOImpl();
/*		try {
			int delcount=baseService.deletePortAll();
			
			System.out.println("delete "+delcount);
			 PortInfoImportCommand command = new PortInfoImportCommand("all_test");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		 
	}
	

}
