package com.ksg.commands.base;

import java.sql.SQLException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ksg.commands.KSGCommand;
import com.ksg.common.dao.DAOManager;
import com.ksg.common.model.KSGModelManager;
import com.ksg.domain.Code;
import com.ksg.service.BaseService;

public class ADDPortExceptionCommand implements KSGCommand {
	
	
	String portName;
	private BaseService baseService;
	protected Logger 		logger = Logger.getLogger(this.getClass());
	public ADDPortExceptionCommand(String portName) {
		baseService = DAOManager.getInstance().createBaseService();
		
		this.portName = portName;
	}

	public int execute() {
		
		logger.debug("port_name:"+portName);
		String result=JOptionPane.showInputDialog(KSGModelManager.getInstance().frame, "���� �ױ����� �� �Է��ϼ���",portName);
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
					JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�����ϴ� �����ױ���  �Դϴ�.");
				}

				e1.printStackTrace();
			}
		}else
		{
			JOptionPane.showMessageDialog(KSGModelManager.getInstance().frame, "�����ױ��� �Է��ϼ���");
		}
		
		return  KSGCommand.RESULT_FAILE;
	}

}
