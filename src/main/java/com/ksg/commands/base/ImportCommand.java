package com.ksg.commands.base;

import java.text.SimpleDateFormat;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.commands.IFCommand;
import com.ksg.commands.LongTask;
import com.ksg.dao.impl.AdvDAOImpl;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.dao.impl.TableDAOImplTemp;

public abstract class ImportCommand implements IFCommand, LongTask{
	
	protected SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
	protected Logger logger = LogManager.getLogger(this.getClass());
	protected String message;
	protected int lengthOfTask;
	protected int current;	
	protected BaseDAOManager baseService;
	protected TableDAOImplTemp tableService; 
	protected boolean isdone=false;
	protected Workbook wb;
	protected Sheet sheet;
	protected AdvDAOImpl advDAOImpl;
	public ImportCommand() {
		baseService = new BaseDAOManager();
		tableService = new TableDAOImplTemp();
		advDAOImpl = new AdvDAOImpl();
	}
	@Override
	public int getLengthOfTask() {
		return lengthOfTask;
	}

	@Override
	public int getCurrent() {
		return current;
	}

	@Override
	public void stop() {
		
	}

	@Override
	public boolean isDone() {
		return isdone;
	}

	@Override
	public String getMessage() {
		return message;
	}
	

}
