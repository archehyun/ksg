package com.ksg.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ksg.domain.Company;

public interface CompanyDAO {
	
	// -- select List -- //
	
	public List<Company> selectList(Company commandMap) throws SQLException;

	public Company select(Company param) throws SQLException;
	
	
	// -- update -- //
	public int update(HashMap<String, Object> param) throws SQLException;
	
	// -- delete -- //
	
	public int delete(Map<String, Object> commandMap) throws SQLException;
	
	// -- insert -- //
	
	public Object insert(Company param) throws SQLException;
	
	
	public Object selectListByPage(HashMap<String, Object> param) throws SQLException;
	public int selectCount(Map<String, Object> commandMap) throws SQLException;
	
	

}
