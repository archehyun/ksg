package com.ksg.workbench.base.dialog;

import com.ksg.workbench.base.BaseInfoUI;
import com.ksg.workbench.common.comp.dialog.KSGDialog;

public abstract class BaseInfoDialog extends KSGDialog{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BaseInfoDialog(BaseInfoUI baseInfoUI) {
		super();
		this.baseInfoUI = baseInfoUI;
	}
	
	public BaseInfoDialog() {
		super();
		
	}

	protected BaseInfoUI baseInfoUI;

}
