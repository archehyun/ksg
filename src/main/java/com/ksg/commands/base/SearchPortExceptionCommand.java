package com.ksg.commands.base;

import java.sql.SQLException;

import com.ksg.commands.KSGCommand;
import com.ksg.common.dao.DAOManager;
import com.ksg.domain.Code;
import com.ksg.service.BaseService;

public class SearchPortExceptionCommand implements KSGCommand {

	String portName;
	private BaseService baseService;
	public SearchPortExceptionCommand (String portName) {
		baseService = DAOManager.getInstance().createBaseService();

		this.portName = portName;
	}

	public int execute() {
		Code code_info = new Code();
		code_info.setCode_type("port_exception");
		code_info.setCode_field(portName);
		try 
		{
			Code f=baseService.getCodeInfo(code_info);
			
			if(f==null)
				return RESULT_FAILE;
			else
			{
				return RESULT_SUCCESS;		
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RESULT_FAILE;
		}
		
	}


}
