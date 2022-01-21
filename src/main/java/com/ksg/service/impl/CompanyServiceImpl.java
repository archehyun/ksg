package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ksg.dao.impl.CompanyDAOImpl;
import com.ksg.service.CompanyService;

/**

  * @FileName : CompanyServiceImpl.java

  * @Project : KSG2

  * @Date : 2021. 11. 25. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :

  */
public class CompanyServiceImpl implements CompanyService{ 
	
	CompanyDAOImpl companyDAO;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public CompanyServiceImpl() {
		companyDAO = new CompanyDAOImpl();
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> selectList(Map<String, Object> commandMap) throws SQLException {
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", companyDAO.selectCompanyCount(commandMap));
		
		resultMap.put("master", companyDAO.selectCompanyList(commandMap));
		
		return resultMap;

	}

	public int update(HashMap<String, Object> param) throws SQLException{
		return companyDAO.updateCompany(param);
		
	}

	public int delete(HashMap<String, Object> param) throws SQLException{
		// TODO Auto-generated method stub
		return companyDAO.deleteCompany(param);
	}

	public void insert(HashMap<String, Object> param) throws SQLException{
		// TODO Auto-generated method stub
		
		companyDAO.insertCompany(param);
	}

	@Override
	public int getCompanyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
