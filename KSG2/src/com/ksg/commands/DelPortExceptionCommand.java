package com.ksg.commands;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.Code;

public class DelPortExceptionCommand implements KSGCommand {

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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return KSGCommand.RESULT_SUCCESS;	
		}else
		{
			return KSGCommand.RESULT_FAILE;
		}
		
	}

}
