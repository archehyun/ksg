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
package com.ksg.common.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import com.ibatis.common.resources.Resources;
import com.ksg.view.comp.notification.NotificationManager;


import com.ksg.view.comp.notification.Notification;

public class KSGPropertis extends Properties{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static boolean DOUBLE_LINE=false;
	public static boolean ETA_ETD=false;
	public static Boolean PROPERTIES_DIVIDER=true;
	public static final int PROPERTIES_DIVIDER_NOMAL=0;
	public static final int PROPERTIES_DIVIDER_SLASH=1;
	public static final int PROPERTIES_DIVIDER_DOT = 2;
	public static final int PROPERTIES_DIVIDER_BRACKETS=3;
	
	public static Boolean PROPERTIES_DIVIDER2=true;
	public static final String PROPERTIES_VOY = "hasVoy";
	public static final String PROPERTIES_DOUBLEKEY = "doubleKey";
	public static String PROPERTIES_EMPTYCHECK="emptyCheck";
	public static String PROPERTIES_XLSKEY_VESSEL="xlskey.vessel";
	public static String PROPERTIES_XLSKEY_VOYAGE="xlskey.voyage";
	public static String PROPERTIES_XLSKEY_BOTH="xlskey.both";
	public static String PROPERTIES_SELECCTEDKEYWORD="selectedkeyword";
	public static String PROPERTIES_INCODE_LIST="INCODE_LIST";
	public static String PROPERTIES_SELECTED_INCODE="selected_incode";
	public static String PROPERTIES_UNDERPORT="underPort";
	public static String PROPERTIES_VESSEL_VOY_DIVIDER="vesselvoydivider";
	public static String PROPERTIES_VESSEL_VOY_COUNT="vesselvoycount";
	public static final String DATA_LOCATION = "dataLocation";
	public static final String SAVE_LOCATION = "saveLocation";

	String[] stringp ={
			PROPERTIES_VOY,
			PROPERTIES_DOUBLEKEY,
			PROPERTIES_EMPTYCHECK,
			
			PROPERTIES_XLSKEY_VESSEL,
			PROPERTIES_XLSKEY_VOYAGE,
			PROPERTIES_XLSKEY_BOTH,
			
			PROPERTIES_INCODE_LIST,
			PROPERTIES_SELECTED_INCODE,
			PROPERTIES_UNDERPORT,
			PROPERTIES_SELECCTEDKEYWORD,
			
			PROPERTIES_VESSEL_VOY_COUNT,
			PROPERTIES_VESSEL_VOY_DIVIDER,
			
			DATA_LOCATION,
			SAVE_LOCATION
	};
	
	
	private  String KSG_PROPERTIES_TXT = "./config/ksg.properties";
	
	private static KSGPropertis instance = new KSGPropertis();
	public static KSGPropertis getIntance()
	{
		if (instance==null) {
			instance = new KSGPropertis();
			
		}
		return instance;
	}
	private KSGPropertis() 
	{
		load();
	
	}
	private void load() {
		try {
			Reader re =Resources.getResourceAsReader("config/ksg.properties");
			
			this.load(re);
		} catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Object getValues(Object key)
	{
		return this.get(key);
	}
	public void reLoad()
	{
		load();
	}
	public void setPropertisFile(String fileName)
	{
		this.KSG_PROPERTIES_TXT=fileName;
	}
	public void store()  
	{	
			
		try {
			this.store(new FileOutputStream(KSG_PROPERTIES_TXT), "no commments");
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			NotificationManager.showNotification(Notification.Type.WARNING, e.getMessage());
		}
		
	}

}
