package com.ksg.commands;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Code;
import com.ksg.service.BaseService;

public class DelPortExceptionCommand extends AbstractCommand{

	String portName;
	private BaseService baseService;
	public DelPortExceptionCommand(String port_name) {
		this.portName = port_name;
		baseService = DAOManager.getInstance().createBaseService();
	}

	public int execute() {
		int result=JOptionPane.showConfirmDialog(KSGModelManager.getInstance().frame, portName+"를 예외 항구명에서 삭제하시겠습니까?");
		
		if(result==JOptionPane.OK_OPTION)
		{
			Code code_info = new Code();
			code_info.setCode_type("port_exception");
			code_info.setCode_field(portName);
			
			try {
				baseService.deleteCode(code_info);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return IFCommand.RESULT_SUCCESS;	
		}else
		{
			return IFCommand.RESULT_FAILE;
		}
		
	}

}
