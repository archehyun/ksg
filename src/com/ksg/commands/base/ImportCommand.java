package com.ksg.commands.base;

import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.ksg.adv.service.AdvDAOImpl;
import com.ksg.commands.KSGCommand;
import com.ksg.commands.LongTask;
import com.ksg.dao.impl.BaseDAOManager;
import com.ksg.dao.impl.TableDAOImpl;

public abstract class ImportCommand implements KSGCommand, LongTask{
	
	protected SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
	protected Logger 			logger = Logger.getLogger(getClass());
	protected String message;
	protected int lengthOfTask;
	protected int current;	
	protected BaseDAOManager baseService;
	protected TableDAOImpl tableService; 
	protected boolean isdone=false;
	protected Workbook wb;
	protected Sheet sheet;
	protected AdvDAOImpl advDAOImpl;
	public ImportCommand() {
		baseService = new BaseDAOManager();
		tableService = new TableDAOImpl();
		advDAOImpl = new AdvDAOImpl();
	}
	@Override
	public int getLengthOfTask() {
		// TODO Auto-generated method stub
		return lengthOfTask;
	}

	@Override
	public int getCurrent() {
		// TODO Auto-generated method stub
		return current;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return isdone;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return message;
	}
	

}
