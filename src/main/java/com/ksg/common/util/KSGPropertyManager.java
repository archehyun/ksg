package com.ksg.common.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.domain.Table_Property;

public class KSGPropertyManager {

	List<Table_Property> propertyList;
	
	private static KSGPropertyManager manager;

	private KSGPropertyManager()
	{
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
			this.propertyList=this.getKSGTableProperty();
			System.out.println("test");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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
		/*Table_Property param = new Table_Property();
		param.setTable_id(table_id);*/
		
		//return (Table_Property) sqlMap.queryForObject("TABLEProperty.selectTABLEProperty",param);
		Iterator<Table_Property> iter =propertyList.iterator();
		
		while(iter.hasNext())
		{
			Table_Property item = iter.next();
			if(item.getTable_id().equals(table_id))
				return item;
		}
		return null;
	}
	
	//TO-DO ¼öÁ¤
	private List getKSGTableProperty() throws SQLException{ 
		return (List) sqlMap.queryForList("TABLEProperty.selectTABLEProperty");
	}
	
	SqlMapClient sqlMap;
	
	public void update(Table_Property property) throws SQLException
	{
		sqlMap.update("TABLEProperty.updateTableProperty",property);
	}

	public void insert(Table_Property property) throws SQLException {
		
			sqlMap.insert("TABLEProperty.insertTableProperty",property);
	}
	public void init()
	{
		try {
			this.propertyList=this.getKSGTableProperty();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
