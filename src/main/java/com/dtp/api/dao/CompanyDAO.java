package com.dtp.api.dao;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.Company;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @FileName : CompanyDAO.java
 * @Project : KSG2
 * @Date : 2023. 5. 16
 * @작성자 : ch.park
 * @변경이력 :
 * @프로그램 설명 :
 */
public class CompanyDAO extends AbstractDAO{
	
	public CompanyDAO() {
		super();
		this.namespace = "company";
	}
	
	public List<Company> selectListByCondition(Company param) throws SQLException {
		
		return selectList(namespace+".selectCompanyListByCondition", param);
	}

	public Object deleteCompany(String company_abbr) throws SQLException{
		return delete(namespace+".deleteCompany", company_abbr);
	}

	public Company selectById(String company_abbr) throws SQLException {
		
		return (Company) selectOne(namespace+".selectCompanyById", company_abbr);
	}

	public Object updateCompany(Company param) throws SQLException {
		return update(namespace+".updateCompanyInfo", param);
	}

	public Object insertCompany(Company param) throws SQLException {
		return insert(namespace+".insertCompany", param);
	}
}
