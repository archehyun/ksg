package com.ksg.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class KSGTablePropertis  extends Properties {
	private static KSGTablePropertis instance = new KSGTablePropertis();
	public static KSGTablePropertis getIntance()
	{
		if (instance==null) {
			instance = new KSGTablePropertis();		
		}
		return instance;
	}
	private String KSG_TABLE_PROPERTIES_TXT="properties/tbale.properties";
	private KSGTablePropertis() 
	{
		load();
	}
	private void load() {
		try {
			this.load(new FileInputStream(KSG_TABLE_PROPERTIES_TXT));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Object getValues(Object key)
	{
		return this.get(key);
	}
	public void store()
	{
		try {
			this.store(new FileOutputStream(KSG_TABLE_PROPERTIES_TXT), "no commments");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
