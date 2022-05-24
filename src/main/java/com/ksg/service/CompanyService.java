package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ksg.domain.Company;

public interface CompanyService extends PageService{
	
	public Map<String, Object> selectList(Map<String, Object> commandMap) throws SQLException;
	
	public int update(HashMap<String, Object> param) throws SQLException;	

	public int delete(HashMap<String, Object> param) throws SQLException;

	public void insert(HashMap<String, Object> param) throws SQLException;	
	
	public int getCompanyCount();

	public Company select(String company) throws SQLException;

}
