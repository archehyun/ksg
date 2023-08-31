package com.ksg.workbench.preference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.workbench.common.comp.KSGView;

/**

  * @FileName : PnOption.java

  * @Project : KSG2

  * @Date : 2022. 3. 9. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public abstract class PnOption extends KSGView {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected PreferenceDialog preferenceDialog;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public PnOption(PreferenceDialog preferenceDialog)
	{
		this.preferenceDialog= preferenceDialog;
	}
	
	public abstract void saveAction();

}
