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
package com.ksg.db;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
public class ConnectionManager 
{
	private String url, password, dbid, driver, dbtype;

	private Vector<Connection> buffer = new Vector<Connection>();

	static final int MSSQL=1;

	static final int MYSQL=2;

	private	final int maxConnetion =10;

	private static ConnectionManager manager = new ConnectionManager();

	private ConnectionManager()
	{		

	}

	public static ConnectionManager getInstance()
	{
		if(manager==null)
		{
			manager = new ConnectionManager();	
		}
		return manager;
	}

	public void initConPool()
	{
		try
		{
			initConnectionInfo();
			for(int i=0;i<this.maxConnetion;i++)
			{
				buffer.add(this.getConnectionInstance());
			}		
		}
		catch (FileNotFoundException e) 
		{
			throw new RuntimeException("DB 연결프러퍼티 파일을 찾을 수 없습니다.="+e.getMessage());
		}
		catch (SQLException e) {
			throw new RuntimeException("DB 연결에 문제가 생겼습니다.="+e.getMessage());
		} catch (ClassNotFoundException e) 
		{
			throw new RuntimeException("jdbc 파일을 찾을 수 없습니다.="+e.getMessage());
		}
	}


	public synchronized  Connection getConnPool()
	{
		while(buffer.size()==0)
		{
			try
			{
				wait();
			} catch (InterruptedException e)
			{	
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		Connection  con= this.buffer.remove(buffer.size()-1);

		return con;
	}
	public synchronized  void releaseConnPool(Connection conn)
	{
		this.buffer.addElement(conn);
		this.notifyAll();
	}

	public Connection getConnectionInstance() throws SQLException, ClassNotFoundException
	{

		Class.forName(driver);

		Connection con =DriverManager.getConnection(url,dbid,password);

		return con;
	}
	
	// 컨넥션 정보 초기화
	private void initConnectionInfo() throws FileNotFoundException
	{
		Properties properties = new Properties();
		try
		{
			properties .load(new FileInputStream("db.properties"));
			dbtype=properties.getProperty("dbtype");
			driver=properties.getProperty(dbtype+".driver");
			dbid=properties.getProperty(dbtype+".id");
			password=properties.getProperty(dbtype+".password");
			url=properties.getProperty(dbtype+".url");

		}		
		catch (IOException e) 
		{			
			e.printStackTrace();
		}
	}
}
