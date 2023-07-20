package com.dtp.api.control;

import java.util.List;
import java.util.stream.Collectors;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.service.CompanyService;
import com.dtp.api.service.impl.CompanyServiceImpl;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Company;

import lombok.extern.slf4j.Slf4j;

/**
 * 

  * @FileName : CompanyController.java

  * @Project : KSG2

  * @Date : 2023. 5. 31. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :
 */
@Slf4j
public class CompanyController extends AbstractController{
	
	private CompanyService service;
	
	public CompanyController(){
		
		service = new CompanyServiceImpl();
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ControlMethod(serviceId = "selectCompany")	
    public CommandMap selectByCondtion(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
		Company companyParam = Company	.builder()
										.company_name((String)(param.get("company_name")))
										.company_abbr((String)(param.get("company_abbr")))
										.agent_name((String)(param.get("agent_name")))
										.agent_abbr((String)(param.get("agent_abbr")))
										.build();
		
		List<Company> resultList = (List<Company>) service.selectCompanyListByCondition(companyParam);
		
		List<CommandMap> result=resultList.stream()
										.map(o -> objectMapper.convertValue(o, CommandMap.class))
										.collect(Collectors.toList());
								
		CommandMap model = new CommandMap();
		
        model.put("data", result);
        
        log.info("end");
		
		return model;
    }
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ControlMethod(serviceId = "selectCompanyListByCondition")	
    public CommandMap selectCompanyListByCondition(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
		Company companyParam = Company	.builder()
										.company_name((String)(param.get("company_name")))
										.company_abbr((String)(param.get("company_abbr")))
										.agent_name((String)(param.get("agent_name")))
										.agent_abbr((String)(param.get("agent_abbr")))
										.build();
		
		List<Company> resultList = (List<Company>) service.selectCompanyListByCondition(companyParam);
		
		List<CommandMap> result=resultList.stream()
										.map(o -> objectMapper.convertValue(o, CommandMap.class))
										.collect(Collectors.toList());
								
		CommandMap model = new CommandMap();
		
        model.put("data", result);
        
        log.info("end");
		
		return model;
    }
	
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ControlMethod(serviceId = "insertCompany")
	public CommandMap insertCompany(CommandMap param) throws Exception{
		
		log.info("start:{}", param);

        String company_name = (String) param.get("company_name");
        
        String company_abbr = (String) param.get("company_abbr");
        
        String agent_name 	= (String) param.get("agent_name");
        
        String agent_abbr 	= (String) param.get("agent_abbr");
        
        String contents 	= (String) param.get("contents");

        Company company = Company.builder()
                                .company_name(company_name)
                                .company_abbr(company_abbr)
                                .agent_name(agent_name)
                                .agent_abbr(agent_abbr)
                                .contents(contents)
                                .build();

        Company result = service.insertCompany(company);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception, ResourceNotFoundException
	 */
	@ControlMethod(serviceId = "deleteCompany")
    public CommandMap deleteCompany(CommandMap param) throws Exception {
		
		log.info("start:{}", param);
		
		String company_abbr = (String) param.get("company_abbr");
		
		if(company_abbr== null) throw new ResourceNotFoundException("company_abbr is null");
		
        Company result=service.deleteCompany(company_abbr);
        
        System.out.println(result);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");

        return returnMap;
    }
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@ControlMethod(serviceId = "updateCompany")
    public CommandMap updateCompany(CommandMap param) throws Exception{
		
		log.info("start:{}", param);
        
        String company_name 		= (String) param.get("company_name");
        
        String company_abbr 		= (String) param.get("company_abbr");
        
        String agent_name 			= (String) param.get("agent_name");
        
        String agent_abbr 			= (String) param.get("agent_abbr");
        
        String base_company_abbr 	= (String) param.get("base_company_abbr");
        
        String contents 			= (String) param.get("contents");

        Company company = Company	.builder()                                
	                                .company_name(company_name)
	                                .base_company_abbr(base_company_abbr)
	                                .company_abbr(company_abbr)
	                                .agent_name(agent_name)
	                                .agent_abbr(agent_abbr)
	                                .contents(contents)
	                                .build();

        Company result = service.updateCompay(company);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("data", result);
        
        log.info("end");
        
        return returnMap;
    }
}
