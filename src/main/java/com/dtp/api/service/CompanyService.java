package com.dtp.api.service;

import java.util.List;

import com.ksg.domain.Company;

public interface CompanyService {
	
	public List<Company> selectListByCondition(Company param) throws Exception;

	public Company delete(String companyAbbr) throws Exception;

	public Company insert(Company company) throws Exception;

	public Company update(Company company)throws Exception;

	public Company selectById(String id)throws Exception;

}
