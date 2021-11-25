package com.ksg.commands;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.ADVData;
import com.ksg.service.ADVService;
import com.ksg.service.TableService;

public class InsertADVCommand implements KSGCommand{
	private ADVService 		service;
	private DAOManager manager =DAOManager.getInstance();
	String table_id;
	protected Logger 			logger = Logger.getLogger(getClass());
	ADVData data;
	private TableService tableService;
	public InsertADVCommand(ADVData data) {
		service = manager.createADVService();
		tableService = manager.createTableService();
		this.data=data;
	}

	public int execute() {
		try {
			
			//logger.debug("input date : "+data.getDate_isusse());
			
			service.removeADVData(data.getTable_id());
			ADVData d=service.insertADVData(data);
			tableService.updateTableDate(data);
			//JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "광고 정보를 저장 했습니다.");
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, e.getMessage());
			e.printStackTrace();
		}
		return 0;

	}

}
