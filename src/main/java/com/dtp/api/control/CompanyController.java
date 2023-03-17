package com.dtp.api.control;

import java.util.List;
import java.util.stream.Collectors;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.service.CompanyService;
import com.dtp.api.service.impl.CompanyServiceImpl;
import com.ksg.common.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Company;
import com.ksg.workbench.common.comp.View;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyController extends AbstractController{
	
	CompanyService service;
	
	public CompanyController(){
		
		service = new CompanyServiceImpl();
	}
	
	public void setView(View view)
    {
        this.view = view; 
    }
	
	@ControlMethod(serviceId = "selectCompany")
	
    public CommandMap selectByCondtion(CommandMap param) throws Exception
    {
		log.debug("param:{}", param);
		
		
		
		Company companyParam = Company.builder()
				.company_name((String)(param.get("company_name")))
				.company_abbr((String)(param.get("company_abbr")))
				.agent_name((String)(param.get("agent_name")))
				.agent_abbr((String)(param.get("agent_abbr")))
				.build();
		
		
		List<Company> resultList = (List<Company>) service.selectListByCondition(companyParam);
		
		
		List<CommandMap> result=resultList.stream()
										.map(o -> objectMapper.convertValue(o, CommandMap.class))
										.collect(Collectors.toList());
								
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("data", result);
		
		
		return model;
    }
	
	 @ControlMethod(serviceId = "insertCompany")
	    public CommandMap insertCompany(CommandMap param) throws Exception{

	        String company_name = (String) param.get("company_name");
	        String company_abbr = (String) param.get("company_abbr");
	        String agent_name = (String) param.get("agent_name");
	        String agent_abbr = (String) param.get("agent_abbr");
	        String contents = (String) param.get("contents");

	        Company company = Company.builder()
	                                .company_name(company_name)
	                                .company_abbr(company_abbr)
	                                .agent_name(agent_name)
	                                .agent_abbr(agent_abbr)
	                                .contents(contents)
	                                .build();

	        Company result = service.insert(company);                                
	        CommandMap returnMap = new CommandMap();
	        returnMap.put("success", true);
	        returnMap.put("data", result);
	        return returnMap;
	    }
	
	@ControlMethod(serviceId = "deleteCompany")
    public CommandMap deleteCompany(CommandMap param) throws Exception {
		
		
		String companyAbbr = (String) param.get("companyAbbr");
		
		if(companyAbbr== null) throw new ResourceNotFoundException("companyAbbr is null");
		
        Company result=service.delete(companyAbbr);
        
        CommandMap returnMap = new CommandMap();
        
        returnMap.put("success", true);
        
        returnMap.put("data", result);

        return returnMap;
    }
	
	@ControlMethod(serviceId = "updateCompany")
    public CommandMap updateCompany(CommandMap param) throws Exception{
        
        String company_name = (String) param.get("company_name");
        String company_abbr = (String) param.get("company_abbr");
        String agent_name = (String) param.get("agent_name");
        String agent_abbr = (String) param.get("agent_abbr");
        String base_company_abbr = (String) param.get("base_company_abbr");
        String contents = (String) param.get("contents");

        Company company = Company.builder()
                                
                                .company_name(company_name)
                                .base_company_abbr(base_company_abbr)
                                .company_abbr(company_abbr)
                                .agent_name(agent_name)
                                .agent_abbr(agent_abbr)
                                .contents(contents)
                                .build();

        Company result = service.update(company);
        CommandMap returnMap = new CommandMap();
        returnMap.put("success", true);
        returnMap.put("data", result);
        return returnMap;
        
    }
	

}
