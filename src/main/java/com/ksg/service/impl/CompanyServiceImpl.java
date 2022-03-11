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
	public Map<String, Object> selectList(Map<String, Object> param) throws SQLException {
		
		
		
		logger.info("param:{}", param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", companyDAO.selectCount(param));
		
		resultMap.put("master", companyDAO.selectCompanyList(param));
		
		return resultMap;

	}

	public int update(HashMap<String, Object> param) throws SQLException{
		logger.info("param:{}", param);
		return companyDAO.update(param);
		
	}

	public int delete(HashMap<String, Object> param) throws SQLException{
		logger.info("param:{}", param);
		return companyDAO.deleteCompany(param);
	}

	public void insert(HashMap<String, Object> param) throws SQLException{
		logger.info("param:{}", param);
		
		companyDAO.insert(param);
	}

	@Override
	public int getCompanyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HashMap<String, Object> selectListByPage(HashMap<String, Object> param) throws SQLException {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		resultMap.put("total", companyDAO.selectCount(param));
		
		resultMap.put("master", companyDAO.selectListByPage(param));
		
		resultMap.put("PAGE_NO", 1);
		
		return resultMap;
	}

}
