package com.dtp.api.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.dtp.api.dao.CompanyDAO;
import com.dtp.api.exception.AlreadyExistException;
import com.dtp.api.service.CompanyService;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.domain.Company;

import lombok.extern.slf4j.Slf4j;

/**
 * 

  * @FileName : CompanyServiceImpl.java

  * @Project : KSG2

  * @Date : 2023. 5. 31. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :
 */
@Slf4j
public class CompanyServiceImpl implements CompanyService{

	private CompanyDAO dao;

	public CompanyServiceImpl()
	{
		dao = new CompanyDAO();
	}

	@Override
	public List<Company> selectCompanyListByCondition(Company param) throws Exception {
		
		log.info("param:{}", param);
		
		List<Company> result=dao.selectListByCondition(param);

		return result;
	}


	@Override
	public Company deleteCompany(String companyAbbr) throws Exception{
		log.info("param:{}", companyAbbr);
		
		Company selectOne = dao.selectById(companyAbbr);

		if(selectOne== null) throw new ResourceNotFoundException(companyAbbr+" is no exist");

		int result =(int) dao.deleteCompany(companyAbbr);
		
		return selectOne;
	}
	
	@Override
	public Company selectCompanyById(String company_abbr) throws Exception
	{
		log.info("param:{}",  company_abbr);
		
		return dao.selectById(company_abbr);
	}

	@Override
	public Company insertCompany(Company param) throws Exception {
		
		log.info("param:{}", param);
		
        Company selectOne= dao.selectById(param.getCompany_abbr());
        
        if(selectOne!=null) throw new AlreadyExistException("("+param.getCompany_name()+")존재하는 선사명입니다.");

        dao.insertCompany(param);

        return selectOne;
	}

	@Override
	public Company updateCompay(Company param) throws Exception {
		
		log.info("param:{}", param);
		
		Company selectOne= selectCompanyById(param.getCompany_abbr());
		
		if(selectOne==null) throw new ResourceNotFoundException("("+param.getCompany_name()+")선사명이 존재하지 않습니다.");

		int result = (int) dao.updateCompany(param);
		
		return selectOne;
	}
}
