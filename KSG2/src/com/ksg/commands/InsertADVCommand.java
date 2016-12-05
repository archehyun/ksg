package com.ksg.commands;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.ADVService;
import com.ksg.dao.impl.TableService;
import com.ksg.domain.ADVData;

public class InsertADVCommand implements KSGCommand{
	private ADVService 		service;
	private DAOManager manager =DAOManager.getInstance();
	String table_id;
	protected Logger 			logger = Logger.getLogger(getClass());
	ADVData data;
	private TableService _tableService;
	public InsertADVCommand(ADVData data) {
		service = manager.createADVService();
		_tableService = manager.createTableService();
		this.data=data;
	}

	public int execute() {
		try {
			
			logger.debug("input date : "+data.getDate_isusse());
			
			service.removeADVData(data.getTable_id());
			ADVData d=service.insertADVData(data);
			logger.debug("execute:"+data.getTable_id()+","+d);
			_tableService.updateTableDate(data);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
		return 0;

	}

}
