package com.ksg.base.view.dialog;

import com.ksg.base.view.BaseInfoUI;
import com.ksg.view.comp.dialog.KSGDialog;

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
