package com.dtp.api.control;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.exception.ResourceNotFoundException;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Code;
import com.ksg.service.impl.CodeServiceImpl;

import lombok.extern.slf4j.Slf4j;


/**
 * 

  * @FileName : CodeController.java

  * @Project : KSG2

  * @Date : 2023. 3. 17. 

  * @작성자 : pch

  * @변경이력 :

  * @프로그램 설명 :
 */
@Slf4j
public class CodeController extends AbstractController{
	
	CodeServiceImpl service;
	
	protected Logger logger = LogManager.getLogger(this.getClass());
	
	public CodeController(){
		
		service = new CodeServiceImpl();
	}
	
	@ControlMethod(serviceId = "selectCodeList")
    public CommandMap selectByCondtion(CommandMap param) throws Exception
    {
		log.debug("param:{}", param);

		Code codeParam = Code.builder()
							.code_name((String) param.get("code_name"))
							.code_name_kor((String) param.get("code_name_kor"))
							.code_field((String) param.get("code_field"))							
							.build();
				
		List<Code> resultList = (List<Code>) service.selectCodeHList(codeParam);
		
		
		List<CommandMap> result=resultList.stream()
										.map(o -> objectMapper.convertValue(o, CommandMap.class))
										.collect(Collectors.toList());
								
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("data", result);
		
		
		return model;
    }
	
	@ControlMethod(serviceId = "selectCodeDetailList")
    public CommandMap selectDetailListByCondtion(CommandMap param) throws Exception
    {
		log.debug("param:{}", param);
		
		logger.info("param:{}", param);

		Code codeParam = Code.builder()
							.code_name((String) param.get("code_name"))
							.code_type((String) param.get("code_type"))							
							.build();
				
		List<Code> resultList = (List<Code>) service.selectCodeDetailList(codeParam);
		
		
		List<CommandMap> result=resultList.stream()
										.map(o -> objectMapper.convertValue(o, CommandMap.class))
										.collect(Collectors.toList());
								
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("data", result);
		
		
		return model;
    }
	
	
	
	@ControlMethod(serviceId = "insertCode")
    public CommandMap insertCode(CommandMap param) throws Exception
    {	
		Code codeParam = Code.builder()
							.code_name((String) param.get("code_name"))
							.code_type((String) param.get("code_type"))
							.code_field((String) param.get("code_field"))
							.code_name_kor((String) param.get("code_name_kor"))
							.build();
		
		service.insertCode(codeParam);
		
		CommandMap model = new CommandMap();
		
        model.put("success", true);
		
		
		return model;
    }
	
	@ControlMethod(serviceId = "insertCodeDetail")
    public CommandMap insertCodeDetail(CommandMap param) throws Exception
    {	
		
		Code codeParam = Code.builder()
							.code_name((String) param.get("code_name"))
							.code_type((String) param.get("code_type"))
							.code_field((String) param.get("code_field"))
							.code_name_kor((String) param.get("code_name_kor"))
							.build();
		
		service.insertCodeDetail(codeParam);
		
		CommandMap model = new CommandMap();
		
        model.put("success", true);
		
		
		return model;
    }
    
	
	@ControlMethod(serviceId = "deleteCode")
    public CommandMap deleteCode(CommandMap param) throws Exception
    {
		log.debug("param:{}", param);
		
		logger.info("param:{}", param);
		
		String code_field = (String) param.get("code_field");
		
		service.deleteCode(code_field);
		
								
		CommandMap model = new CommandMap();
		
		model.put("code_field", code_field);
		
        model.put("success", true);
		
		
		return model;
    }
	
	@ControlMethod(serviceId = "deleteCodeDetail")
    public CommandMap deleteCodeDetail(CommandMap param) throws Exception
    {
		log.debug("param:{}", param);
		
		logger.info("param:{}", param);
		
		String code_name= (String) param.get("code_name");
		
		String code_field = (String) param.get("code_field");
		
		if(code_name==null|| code_field==null) throw new ResourceNotFoundException("code_name:"+code_name+",code_field:"+code_field);
		
		Code codeParam = Code.builder()
								.code_name(code_name)
								.code_field(code_field)
								.build();
		
		service.deleteCodeDetail(codeParam);
		
								
		CommandMap model = new CommandMap();
		
		model.put("code_name", code_name);
		model.put("code_field", code_field);
		
        model.put("success", true);
		
		
		return model;
    }
	
	
	


}
