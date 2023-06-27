/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ksg.view.comp.panel;

import java.awt.Color;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.common.model.KSGModelManager;
import com.ksg.common.util.KSGPropertis;
import com.ksg.view.comp.KSGViewUtil;

/**
 * @author ¹ÚÃ¢Çö
 *
 */
public class KSGPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected KSGModelManager 	manager = KSGModelManager.getInstance();
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	protected KSGPropertis 	propertis = KSGPropertis.getIntance();
	
	protected KSGViewUtil viewPropeties = KSGViewUtil.getInstance();
	
	public void createAndUpdateUI()
	{
		
	}
	

	
	Color background=Color.white;
	
	
	public KSGPanel()
	{
		super();
		this.setDoubleBuffered(true);
	
		try {
		background=getColor(viewPropeties.getProperty("panel.background"));
		}catch(Exception e)
		
		{
			background = Color.white;
		}
		this.setBackground(background);
	}
	public KSGPanel(LayoutManager layout) {
		this();
		
		this.setLayout(layout);		
	}

    private Color getColor(String param)
	{
		String index[] = param.split(",");
		return new Color(Integer.parseInt(index[0].trim()),Integer.parseInt(index[1].trim()), Integer.parseInt(index[2].trim()));
	}
	
	
	
	
}
