package com.ksg.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ksg.common.dao.SqlMapManager;
import com.ksg.domain.Company;

public class CompanyDAOImpl {
	private SqlMapClient sqlMap;

	public CompanyDAOImpl() {
		try {
			sqlMap = SqlMapManager.getSqlMapInstance();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public int getCount() throws SQLException {
		// TODO Auto-generated method stub
		return (Integer) sqlMap.queryForObject("BASE_COMPANY.selectCompanyCount");
	}
	
	
	/**
	 * @param data
	 * @return
	 * @throws SQLException
	 */
	public int delete(String data) throws SQLException {
		return sqlMap.delete("BASE_COMPANY.deleteCompany",data);
		
	}
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public List getCompanyList() throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectCompanyList");
	}
	/**
	 * @param company
	 * @return
	 * @throws SQLException
	 */
	public List getCompanyList(Company company) throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectCompanyListOrderby",company);
	}
	/**
	 * @param searchKeyword
	 * @return
	 * @throws SQLException
	 */
	public List getSearchedCompanyList(String searchKeyword) throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectSearchedCompanyList",searchKeyword);
	}
	
	/**
	 * @param info
	 * @return
	 * @throws SQLException
	 */
	public Object insert(Company info) throws SQLException {
		return sqlMap.insert("BASE_COMPANY.insertCompany",info);
		
	}
	
	/**
	 * @param companyInfo
	 * @throws SQLException
	 */
	public int update(Company companyInfo) throws SQLException {
		return sqlMap.update("BASE_COMPANY.updateCompany",companyInfo);
		
	}
	
	/**
	 * @param orderBy
	 * @return
	 * @throws SQLException
	 */
	public List getArrangedCompanyList(Object orderBy) throws SQLException {
		return sqlMap.queryForList("BASE_COMPANY.selectArrangedCompanyList",orderBy);
	}





}
