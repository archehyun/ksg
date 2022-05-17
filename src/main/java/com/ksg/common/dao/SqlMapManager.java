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
package com.ksg.common.dao;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

public class SqlMapManager 
{
	
	private static String resource = "config/sql-map-config.xml";
	
	private static String resource2 = "config/sql-map-config2.xml";
	
	private static SqlMapClient sqlMap;
	 
	protected static SqlSession _session=null;
	
	private SqlMapManager(){}
	
	  
	   static {
	        try {
	            
	            Reader reader = Resources.getResourceAsReader(resource2);
	            SqlSessionFactory sqlMapper = new SqlSessionFactoryBuilder().build(reader);
	            
	            _session = sqlMapper.openSession();
	         } catch(IOException e) {
	             e.printStackTrace();
	        }
	    }
	   
	   
	public static SqlMapClient getSqlMapInstance() throws IOException 
	{
		if(sqlMap==null)
		{
			Reader reader = Resources.getResourceAsReader(resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		}
		
		return sqlMap;
	}
	public static SqlMapClient getSqlMapInstance(boolean flag) throws IOException 
	{
		if(flag)
		{
			Reader reader = Resources.getResourceAsReader(resource);
			sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
		}
		
		return sqlMap;
	}
	
	 public static SqlSession getSqlSession() {
	        return _session;
	    }
	
	

}
