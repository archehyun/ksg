package com.ksg.view.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DBPropertis extends Properties{
	
	private String PROPERTY_URL="mssql.url";
	private String PROPERTY_ID= "mssql.id";
	private String PROPERTY_PASSWORD="mssql.password";
	private String PROPERTY_DRIVER="mssql.driver";
	private static final String BIN_COM_KSG_DAO_DB_PROPERTIES = "bin/com/ksg/dao/db.properties";

	public static void main(String[] args) {
		DBPropertis dbPropertis = new DBPropertis();
	}
	public DBPropertis() {
		load();
	}
	
	private void load() {
		try {
			this.load(new FileInputStream(BIN_COM_KSG_DAO_DB_PROPERTIES));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getURL()
	{
		return this.getProperty(this.PROPERTY_URL);
	}
	public String getID()
	{
		return this.getProperty(this.PROPERTY_ID);
	}
	public String getDriver()
	
	{
		return this.getProperty(this.PROPERTY_DRIVER);
	}
	public String getPassword()
	{
		return this.getProperty(this.PROPERTY_PASSWORD);
	}

}
