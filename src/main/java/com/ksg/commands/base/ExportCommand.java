package com.ksg.commands.base;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;

public abstract class ExportCommand extends ImportCommand{
	protected String fileName;
	protected String sheetName;
	protected Workbook wb;
	protected boolean isFileWrite=true;
	
	public ExportCommand(String fileName)
	{
		super();
		this.fileName = fileName;
		if(!this.fileName.endsWith(".xls"))
		{
			this.fileName+=".xls";
		}

	}
	public ExportCommand(String fileName, boolean isFileWrite)
	{
		this(fileName);
		this.isFileWrite= isFileWrite;
	}
	
	public ExportCommand(String sheetName, Workbook wb)
	{		
		this.sheetName = sheetName;
		this.wb = wb;
		this.isFileWrite=false;
	}
	public void fileWrite(Workbook wb) throws FileNotFoundException,IOException {
		
		if(!isFileWrite)
			return;
		
		
		logger.info("파일 출력");
		FileOutputStream fileOut = new FileOutputStream(KSGPropertis.getIntance().getProperty(KSGPropertis.SAVE_LOCATION)+"/"+fileName);
		wb.write(fileOut);
		fileOut.close();
		JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, fileName+" 파일을 생성했습니다.");
	}

}
