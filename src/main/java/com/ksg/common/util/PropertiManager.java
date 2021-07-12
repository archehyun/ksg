package com.ksg.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import sun.security.util.Resources;

public class PropertiManager {
	
	private Properties properties;	
	private static PropertiManager instance;
	
	private PropertiManager()
	{
		try {
			load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static PropertiManager getInstance()
	{
		if(instance==null)
			instance = new PropertiManager();
		return instance;
	}
	private void load() throws IOException
	{
		properties = new Properties();
		Reader reader = com.ibatis.common.resources.Resources.getResourceAsReader("config/db.properties");
		properties.load(reader);
	}
	
	public static void main(String[] args) {
		PropertiManager manager = PropertiManager.getInstance();
		
		System.out.println(manager.getProperties());
	}
	public Properties getProperties()
	{
		return properties;
	}
	
}
