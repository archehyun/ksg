package com.ksg.commands.xls;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.adv.logic.xml.KSGXMLManager;
import com.ksg.commands.IFCommand;

public class ImportXLSFileCommandByXML implements IFCommand{
	
	
	Vector tableInfoList;
	protected Logger logger = LogManager.getLogger(this.getClass());
	KSGXMLManager manager;
	public ImportXLSFileCommandByXML(Vector tableInfoList) {
		this.tableInfoList =tableInfoList;
	}

	public int execute() {
		
		logger.debug("xml create");
		manager = new KSGXMLManager();
		try 
		{
			manager.readFile(tableInfoList);
			return RESULT_SUCCESS;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return RESULT_FAILE;
		}
		
	}

}
