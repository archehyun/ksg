package com.ksg.common.model;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;

public class Configure extends Properties{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Configure instance;
		
	String db_resource = "config/db.properties";
	String ksg_resource = "config/ksg.properties";
	
	public Configure() {
		try {
            Reader reader1 = Resources.getResourceAsReader(db_resource);
            Reader reader2 = Resources.getResourceAsReader(ksg_resource);
            load(reader1);
            load(reader2);
     
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	public static Configure getInstance()
	{
		if(instance == null)

		{
			instance = new Configure();
		}

		return instance;
	}

}
