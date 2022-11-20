package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.exception.AlreadyExistException;
import com.ksg.common.exception.UnhandledException;
import com.ksg.common.model.CommandMap;
import com.ksg.dao.impl.CompanyDAOImpl;
import com.ksg.domain.Company;
import com.ksg.service.CompanyService;

import lombok.extern.slf4j.Slf4j;

/**

 * @FileName : CompanyServiceImpl.java

 * @Project : KSG2

 * @Date : 2021. 11. 25. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 :

 */
@Slf4j
public class CompanyServiceImpl extends AbstractServiceImpl implements CompanyService{

	protected ObjectMapper objectMapper;

	private CompanyDAOImpl companyDAO;

	public CompanyServiceImpl() {

		super();
		objectMapper = new ObjectMapper();
		companyDAO = new CompanyDAOImpl();

	}
	


	@SuppressWarnings("unchecked")
	public CommandMap selectListByCondition(Map<String, Object> param) throws SQLException {

		log.info("param:{}", param);

		Company searchParam =Company.builder()
				.company_name(param.get("company_name")!=null?String.valueOf(param.get("company_name")):null)
				.agent_name(param.get("agent_name")!=null?String.valueOf(param.get("agent_name")):null)
				.company_abbr(param.get("company_abbr")!=null?String.valueOf(param.get("company_abbr")):null)
				.agent_abbr(param.get("agent_abbr")!=null?String.valueOf(param.get("agent_abbr")):null)
				.build();

		List<Company> li=companyDAO.selectList(searchParam);

		ArrayList<CommandMap> map = new ArrayList<CommandMap>();

		log.info(objectMapper.toString());

		for(Company item:li)
		{	
			map.add((CommandMap) objectMapper.convertValue(item, CommandMap.class));
		}

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", companyDAO.selectCount(param));

		resultMap.put("master", map);

		return resultMap;

	}
	
	@SuppressWarnings("unchecked")
	public CommandMap selectListByLike(Map<String, Object> param) throws SQLException {

		log.info("param:{}", param);

		Company searchParam =Company.builder()
				.company_name(param.get("company_name")!=null?String.valueOf(param.get("company_name")):null)
				.agent_name(param.get("agent_name")!=null?String.valueOf(param.get("agent_name")):null)
				.company_abbr(param.get("company_abbr")!=null?String.valueOf(param.get("company_abbr")):null)
				.agent_abbr(param.get("agent_abbr")!=null?String.valueOf(param.get("agent_abbr")):null)
				.build();

		List<Company> li=companyDAO.selectByLike(searchParam);

		ArrayList<CommandMap> map = new ArrayList<CommandMap>();

		log.info(objectMapper.toString());

		for(Company item:li)
		{	
			map.add((CommandMap) objectMapper.convertValue(item, CommandMap.class));
		}

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", companyDAO.selectCount(param));

		resultMap.put("master", map);

		return resultMap;

	}


	public int update(CommandMap param) throws SQLException{
		log.debug("param:{}", param);
		return companyDAO.update(param);

	}

	public int delete(CommandMap param) throws SQLException{
		log.debug("param:{}", param);
		return companyDAO.deleteCompany(param);
	}

	public void insert(CommandMap param) throws RuntimeException{
		log.debug("param:{}", param);

		try
		{	
			Company searchParam =Company.builder()
					.company_name(param.get("company_name")!=null?String.valueOf(param.get("company_name")):null)
					.agent_name(param.get("agent_name")!=null?String.valueOf(param.get("agent_name")):null)
					.company_abbr(param.get("company_abbr")!=null?String.valueOf(param.get("company_abbr")):null)
					.agent_abbr(param.get("agent_abbr")!=null?String.valueOf(param.get("agent_abbr")):null)
					.contents(param.get("contents")!=null?String.valueOf(param.get("contents")):null)

					.build();



			Company exist = companyDAO.select(searchParam);

			if(exist != null) throw new AlreadyExistException("exist ");


			companyDAO.insert(searchParam) ;


		} catch (SQLException e1) {

			throw new UnhandledException(e1.getMessage());
		}
	}

	@Override
	public int getCompanyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CommandMap selectListByPage(HashMap<String, Object> param) throws SQLException {

		log.debug("param:{}", param);

		CommandMap resultMap = new CommandMap();

		resultMap.put("total", companyDAO.selectCount(param));

		resultMap.put("master", companyDAO.selectListByPage(param));

		resultMap.put("PAGE_NO", 1);

		return resultMap;
	}

	@Override
	public Company select(String company) throws SQLException {

		log.debug("param:{}",company);

		Company param  = Company.builder().company_name(company).build();
		return companyDAO.select(param);
	}

}
