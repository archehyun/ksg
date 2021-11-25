package com.ksg.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ksg.dao.impl.CompanyDAOImpl;

public class CompanyService {
	
	
	CompanyDAOImpl companyDAO;
	public CompanyService() {
		companyDAO = new CompanyDAOImpl();
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
