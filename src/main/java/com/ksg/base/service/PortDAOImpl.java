package com.ksg.base.service;

import java.io.IOException;
import java.sql.SQLException;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;

public class PortDAOImpl {
	
	private SqlMapClient sqlMap;
	public PortDAOImpl() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int deletePortAbbr(String data) throws SQLException {
		return sqlMap.delete("BASE_PORT.deletePort_Abbr",data);
		
	}
	public int deletePort(String port) throws SQLException {
		
		return sqlMap.delete("BASE_PORT.deletePort",port);
	}
	public int deletePortAll() throws SQLException {
		
		return sqlMap.delete("BASE_PORT.deletePortAll");
	}
	

}
