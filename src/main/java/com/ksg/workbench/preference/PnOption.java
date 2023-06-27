package com.ksg.workbench.preference;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.view.comp.panel.KSGPanel;

/**

  * @FileName : PnOption.java

  * @Project : KSG2

  * @Date : 2022. 3. 9. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public abstract class PnOption extends KSGPanel implements ActionListener, ComponentListener{
	
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
	
	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
