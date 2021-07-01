package com.ksg.base.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ksg.base.dao.CompanyDAO;

public class CompanyService {
	
	
	CompanyDAO companyDAO;
	public CompanyService() {
		companyDAO = new CompanyDAO();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectCompanyList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", companyDAO.selectCompanyCount(commandMap));
		
		resultMap.put("master", companyDAO.selectCompanyList(commandMap));
		
		return resultMap;

	}

	public int updateComapny(HashMap<String, Object> param) throws SQLException{
		return companyDAO.updateCompany(param);
		
	}

	public int deleteCompany(HashMap<String, Object> param) throws SQLException{
		// TODO Auto-generated method stub
		return companyDAO.deleteCompany(param);
	}

	public void insertComapny(HashMap<String, Object> param) throws SQLException{
		// TODO Auto-generated method stub
		
		companyDAO.insertCompany(param);
	}

}
