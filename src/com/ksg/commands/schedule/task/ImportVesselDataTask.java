package com.ksg.commands.schedule.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.LongTask;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.Vessel;
import com.ksg.schedule.view.dialog.ScheduleBuildMessageDialog;

public class ImportVesselDataTask implements LongTask{


	private int lengthOfTask;
	private boolean done = false;
	private boolean canceled = false;
	private String statMessage;
	final int FORWARD =0;
	final int BACK =1;
	private int current = 0;

	private BaseService service;
	protected Logger 			logger = Logger.getLogger(getClass());
	private ScheduleBuildMessageDialog di;
	public ImportVesselDataTask(File selectedFile) {
		try {

			current = 0;
			done = false;
			canceled = false;
			statMessage = null;

			service =DAOManager.getInstance().createBaseService();
			POIFSFileSystem fs= new POIFSFileSystem(new FileInputStream(selectedFile));
			Workbook wb = (Workbook) new HSSFWorkbook(fs);
			Sheet sheet = wb.getSheetAt(0);
			lengthOfTask =sheet.getLastRowNum();
			di = new ScheduleBuildMessageDialog (this);
			di.setMessage("선박 정보 추가 중...");
			di.createAndUpdateUI();
			logger.info("데이터 가져오기 시작:"+lengthOfTask);
			
			for(int i=1;i<sheet.getLastRowNum();i++)
			{
				logger.debug("insert "+i);
				HSSFRow row =(HSSFRow) sheet.getRow(i);
				Cell cell0 =row.getCell(0, HSSFRow.RETURN_BLANK_AS_NULL);
				Cell cell1 =row.getCell(1, HSSFRow.RETURN_BLANK_AS_NULL);
				Cell cell2 =row.getCell(2, HSSFRow.RETURN_BLANK_AS_NULL);
				Cell cell3 =row.getCell(3, HSSFRow.RETURN_BLANK_AS_NULL);
				Cell cell4 =row.getCell(4, HSSFRow.RETURN_BLANK_AS_NULL);
				Vessel op = new Vessel();
				try{

					op.setVessel_name(cell0.getStringCellValue());
					op.setVessel_abbr(cell1.getStringCellValue());
					op.setVessel_type(cell2.getStringCellValue());
					
					op.setVessel_company(cell4.getStringCellValue());
					
				}catch(Exception e)
				{
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e.getMessage()+",index:"+i);
					return;
				}
				try 
				{

					switch (cell3.getCellType()) {
					case HSSFCell.CELL_TYPE_STRING:
						String result = cell3.getStringCellValue();
						op.setVessel_use( Integer.valueOf(result));
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						op.setVessel_use((int) cell3.getNumericCellValue());		
						break;
					case HSSFCell.CELL_TYPE_BLANK:
						op.setVessel_use(Vessel.USE);
					default:
						break;
					}

				}catch(NullPointerException e)
				{
					op.setVessel_use(Vessel.USE);
				}catch(Exception e)
				{
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "error:"+e.getMessage());
					e.printStackTrace();
				}
				try 
				{					
					service.insertVessel(op);
				} catch (SQLException e1) 
				{

					if(e1.getErrorCode()==2627)
					{
						try 
						{
							logger.debug("update:"+i);
							service.updateVessel(op);
						} catch (SQLException e2) 
						{
							e2.printStackTrace();
						}
					}
					else
					{
						logger.error(e1.getErrorCode()+":"+e1.getMessage());
					}

				}
				current=i;
			}
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "데이터를 가져 왔습니다.");

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		catch (NumberFormatException e3) {
			e3.printStackTrace();
		}
		finally
		{
			done=true;
			if(this.di!=null){
				this.di.setVisible(false);
				this.di.dispose();
			}
		}

	}

	public int getCurrent() {
		// TODO Auto-generated method stub
		return current;
	}

	public int getLengthOfTask() {
		// TODO Auto-generated method stub
		return lengthOfTask;
	}

	public String getMessage() {
		// TODO Auto-generated method stub
		return statMessage;
	}

	public boolean isDone() {
		// TODO Auto-generated method stub
		return done;
	}

	public void stop() {
		canceled = true;
		statMessage = null;

	}

}
