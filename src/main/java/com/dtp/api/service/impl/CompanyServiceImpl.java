package com.dtp.api.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.dtp.api.dao.CompanyDAO;
import com.dtp.api.exception.AlreadyExistException;
import com.dtp.api.service.CompanyService;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.domain.Company;

public class CompanyServiceImpl implements CompanyService{


	CompanyDAO dao;

	public CompanyServiceImpl()
	{
		dao = new CompanyDAO();
	}

	@Override
	public List<Company> selectListByCondition(Company param) throws SQLException {


		List<Company> result=dao.selectListByCondition(param);

		return result;
	}



	@Override
	public Company delete(String companyAbbr) throws Exception{
		Company selectOne = dao.selectById(companyAbbr);

		if(selectOne== null) throw new ResourceNotFoundException(companyAbbr+" is no exist");

		dao.deleteCompany(companyAbbr);

		return selectOne;
	}
	
	
	@Override
	public Company selectById(String company_abbr) throws Exception
	{
		return dao.selectById(company_abbr);
	}

	@Override
	public Company insert(Company param) throws Exception {
        Company selectOne= dao.selectById(param.getCompany_abbr());
        
        if(selectOne!=null)
            throw new AlreadyExistException("("+param.getCompany_name()+")존재하는 선사명입니다.");

        dao.insertCompany(param);

        return selectOne;
	}

	@Override
	public Company update(Company param) throws Exception {
		Company selectOne= selectById(param.getCompany_abbr());
		
		if(selectOne==null)
			throw new ResourceNotFoundException("("+param.getCompany_name()+")선사명이 존재하지 않습니다.");

		int result = (int) dao.updateCompany(param);
		
		return selectOne;
	}

}
