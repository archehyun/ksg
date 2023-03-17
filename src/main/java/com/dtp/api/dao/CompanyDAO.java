package com.dtp.api.dao;

import java.sql.SQLException;
import java.util.List;

import com.ksg.common.dao.AbstractDAO;
import com.ksg.domain.Company;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyDAO extends AbstractDAO{
	
	public CompanyDAO() {
		super();
		this.namespace = "company";
	}
	
	
	public List<Company> selectListByCondition(Company param) throws SQLException {
		
		log.debug("param:{}", param);
		return selectList(namespace+".selectCompanyListByCondition", param);

	}


	public int deleteCompany(String companyAbbr) {
		// TODO Auto-generated method stub
		return 0;
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
