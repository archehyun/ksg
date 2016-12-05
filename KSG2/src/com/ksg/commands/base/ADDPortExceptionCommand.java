package com.ksg.commands.base;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.ksg.commands.KSGCommand;
import com.ksg.dao.DAOManager;
import com.ksg.dao.impl.BaseService;
import com.ksg.domain.Code;
import com.ksg.model.KSGModelManager;

public class ADDPortExceptionCommand implements KSGCommand {
	
	
	String portName;
	private BaseService baseService;
	public ADDPortExceptionCommand(String portName) {
		baseService = DAOManager.getInstance().createBaseService();
		
		this.portName = portName;
	}

	public int execute() {
		String result=JOptionPane.showInputDialog(KSGModelManager.getInstance().frame, "예외 항구명을 를 입력하세요",portName);
		if(result!=null&&result.length()>0)
		{
			
			Code code_info = new Code();
			code_info.setCode_type("port_exception");
			code_info.setCode_field(result);
			code_info.setCode_name(result);
			code_info.setCode_name_kor(result);
			
			try {
				baseService.insertCode(code_info);
				
				return KSGCommand.RESULT_SUCCESS;

			} catch (SQLException e1) {
				if(e1.getErrorCode()==2627)
				{
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "존재하는 예외항구명  입니다.");
				}

				e1.printStackTrace();
			}
		}else
		{
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "예외항구명를 입력하세요");
		}
		
		return  KSGCommand.RESULT_FAILE;
	}

}
