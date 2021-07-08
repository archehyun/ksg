package com.ksg.base.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ksg.base.dao.CompanyDAO;

public class CompanyService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);
	
	
	CompanyDAO companyDAO;
	public CompanyService() {
		companyDAO = new CompanyDAO();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectCompanyList(Map<String, Object> param) throws SQLException {
		
		
		logger.info("param:{}",param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", companyDAO.selectCompanyCount(param));
		
		resultMap.put("master", companyDAO.selectCompanyList(param));
		
		return resultMap;

	}

	public int updateComapny(HashMap<String, Object> param) throws SQLException{
		logger.info("param:{}",param);
		return companyDAO.updateCompany(param);
		
	}

	public int deleteCompany(HashMap<String, Object> param) throws SQLException{
		logger.info("param:{}",param);
		return companyDAO.deleteCompany(param);
	}

	public void insertComapny(HashMap<String, Object> param) throws SQLException{
		logger.info("param:{}",param);
		
		companyDAO.insertCompany(param);
	}

}
