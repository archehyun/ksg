package com.ksg.commands.xls;

import com.ksg.commands.IFCommand;
import com.ksg.common.model.KSGModelManager;
import com.ksg.workbench.adv.xls.XLSTableInfoMemento;

public class ReloadXLSInfoCommand implements IFCommand {

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
