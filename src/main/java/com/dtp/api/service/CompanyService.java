package com.dtp.api.service;

import java.sql.SQLException;
import java.util.List;

import com.ksg.domain.Company;

public interface CompanyService {
	
	public List<Company> selectCompanyListByCondition(Company param) throws Exception;

	public Company deleteCompany(String companyAbbr) throws Exception;

	public Company insertCompany(Company company) throws Exception;

	public Company updateCompay(Company company)throws Exception;

	public Company selectCompanyById(String id)throws Exception;


}
