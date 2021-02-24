package com.ksg.commands.xls;

import com.ksg.adv.view.xls.XLSTableInfoMemento;
import com.ksg.commands.KSGCommand;
import com.ksg.common.model.KSGModelManager;

public class ReloadXLSInfoCommand implements KSGCommand {

	KSGModelManager manager = KSGModelManager.getInstance();
	public int result;
	public int execute() {
		if(manager.memento!=null)
		{
			XLSTableInfoMemento infoMemento = manager.memento;
			manager.tableCount = infoMemento.tableList.size();
			manager.setXLSTableInfoList(infoMemento.getXLSTableInfo());
			result=1;
			return RESULT_SUCCESS;
		}else
		{
		
			result=0;
			return RESULT_FAILE;
		}
		
	}

}
