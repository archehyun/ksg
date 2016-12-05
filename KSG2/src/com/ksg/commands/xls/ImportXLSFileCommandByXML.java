package com.ksg.commands.xls;

import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ksg.commands.KSGCommand;
import com.ksg.xls.xml.KSGXMLManager;

public class ImportXLSFileCommandByXML implements KSGCommand{
	
	
	Vector tableInfoList;
	Logger logger = Logger.getLogger(this.getClass());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RESULT_FAILE;
		}
		
	}

}
