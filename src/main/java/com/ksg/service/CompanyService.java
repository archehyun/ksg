package com.ksg.service;

import java.sql.SQLException;
import java.util.Map;

import com.ksg.common.model.CommandMap;
import com.ksg.domain.Company;

public interface CompanyService extends PageService{
	
	public Map<String, Object> selectListByCondition(Map<String, Object> commandMap) throws SQLException;
	
	public int update(CommandMap param) throws SQLException;	

	public int delete(CommandMap param) throws SQLException;

	public void insert(CommandMap param) throws SQLException;	
	
	public int getCompanyCount();

	public Company select(String company) throws SQLException;

}
