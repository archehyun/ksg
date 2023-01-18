package com.ksg.workbench.common.comp;

import com.ksg.common.model.CommandMap;

public interface View {
	public CommandMap getModel();
	public void updateView();
	public void setModel(CommandMap model);


}
