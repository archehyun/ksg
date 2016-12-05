package com.ksg.view.util;

import java.io.IOException;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.dao.SqlMapManager;
import com.ksg.domain.Table_Property;

public class KSGPropertyManager {


	private static KSGPropertyManager manager;

	private KSGPropertyManager()
	{
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static KSGPropertyManager getInstance() 
	{
		if(manager==null)
			manager = new KSGPropertyManager();
		return manager;
	}
	public Table_Property getKSGTableProperty(String table_id) throws SQLException{ 
		return (Table_Property) sqlMap.queryForObject("TABLEProperty.selectTABLEProperty",table_id);
	}
	SqlMapClient sqlMap;
	public void update(Table_Property property) throws SQLException
	{
		sqlMap.update("TABLEProperty.updateTableProperty",property);
	}

	public void insert(Table_Property property) throws SQLException {
		
			sqlMap.insert("TABLEProperty.insertTableProperty",property);


		
		
	}


}
