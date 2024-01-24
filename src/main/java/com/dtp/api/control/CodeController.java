package com.dtp.api.control;

import java.util.List;
import java.util.stream.Collectors;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.exception.ResourceNotFoundException;
import com.dtp.api.service.CodeService;
import com.dtp.api.service.impl.CodeServiceImpl;
import com.ksg.common.model.CommandMap;
import com.ksg.domain.Code;
import com.ksg.domain.KeyWordInfo;

import lombok.extern.slf4j.Slf4j;


/**
 * 

  * @FileName : CodeController.java

  * @Project : KSG2

  * @Date : 2023. 3. 17. 11

  * @�ۼ��� : pch

  * @�����̷� :

  * @���α׷� ���� : �ڵ� ���� ����
 */
@Slf4j
public class CodeController extends AbstractController{
	
	CodeService service;
	
	public CodeController(){
		
		service = new CodeServiceImpl();
	}
	
	@ControlMethod(serviceId = "selectCodeList")
    public CommandMap selectByCondtion(CommandMap param) throws Exception
    {
		log.info("start:{}", param);

		Code codeParam = Code.builder()
							.code_name((String) param.get("code_name"))
							.code_name_kor((String) param.get("code_name_kor"))
							.code_field((String) param.get("code_field"))							
							.build();
				
		List<Code> resultList = (List<Code>) service.selectCodeHListByCondition(codeParam);
		
		List<CommandMap> result=resultList.stream()
										.map(o -> objectMapper.convertValue(o, CommandMap.class))
										.collect(Collectors.toList());
								
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("data", result);
        
        log.info("end");
		
		return model;
    }
	
	@ControlMethod(serviceId = "selectCodeDetailList")
    public CommandMap selectDetailListByCondtion(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
		log.info("param:{}", param);

		Code codeParam = Code.builder()
							.code_name((String) param.get("code_name"))
							.code_type((String) param.get("code_type"))							
							.build();
				
		List<Code> resultList = (List<Code>) service.selectCodeDetailListByCondition(codeParam);
		
		List<CommandMap> result=resultList.stream()
										.map(o -> objectMapper.convertValue(o, CommandMap.class))
										.collect(Collectors.toList());
								
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("data", result);
        
        log.info("end");
		
		return model;
    }
	
	@ControlMethod(serviceId = "pnCheckPort.selectCodeDetailList")
    public CommandMap pnCheckPortSelectDetailListByCondtion(CommandMap param) throws Exception
    {
		log.info("start:{}", param);

		Code codeParam = Code.builder()
							.code_name((String) param.get("code_name"))
							.code_type((String) param.get("code_type"))							
							.build();
				
		List<Code> resultList = (List<Code>) service.selectCodeDetailListByCondition(codeParam);
		
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("data", resultList);
        
        log.info("end");
		
		return model;
    }
	
	@ControlMethod(serviceId = "pnKeyWord.selectKeyWordInfoListByCondition")
    public CommandMap pnKeyWordSelectKeyWordInfoListByCondition(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
		KeyWordInfo keyParam =KeyWordInfo.builder().key_type((String) param.get("key_type"))
										.build();

		List<KeyWordInfo> resultList = (List<KeyWordInfo>) service.selectKeyWordInfoListByCondition(keyParam);
		
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        model.put("data", resultList);
        
        log.info("end({})",resultList.size());
		
		return model;
    }
	
	@ControlMethod(serviceId = "pnKeyWord.insertKeyWord")
    public CommandMap pnKeyWordInsertKeyword(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
		KeyWordInfo keyParam =KeyWordInfo.builder()
										.key_type((String) param.get("key_type"))
										.key_name((String) param.get("key_name"))
										.build();

		List<KeyWordInfo> resultList = (List<KeyWordInfo>) service.insertKeyword(keyParam);
		
		CommandMap model = new CommandMap();
		
        log.info("end");
		
		return model;
    }
	
	@ControlMethod(serviceId = "pnKeyWord.deleteKeyWord")
    public CommandMap pnKeyWordDeleteKeyWord(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
		KeyWordInfo keyParam =KeyWordInfo.builder()
										.key_type((String) param.get("key_type"))
										.key_name((String) param.get("key_name"))
										.build();

		Object resultList =  service.deleteKeyword(keyParam);
		
		CommandMap model = new CommandMap();
		
        log.info("end");
		
		return model;
    }
	
	@ControlMethod(serviceId = "insertCode")
    public CommandMap insertCode(CommandMap param) throws Exception
    {	
		log.info("start:{}", param);
		
		Code codeParam = Code.builder()
							.cd_id((String) param.get("cd_id"))
							.cd_nm((String) param.get("cd_nm"))
							.cd_eng((String) param.get("cd_eng"))
							
							.build();
		
		service.insertCode(codeParam);
		
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        
        log.info("end");
		
		return model;
    }
	
	@ControlMethod(serviceId = "insertCodeDetail")
    public CommandMap insertCodeDetail(CommandMap param) throws Exception
    {	
		log.info("start:{}", param);
		
		Code codeParam = Code.builder()
							.code_name((String) param.get("code_name"))
							.code_type((String) param.get("code_type"))
							.code_field((String) param.get("code_field"))
							.code_name_kor((String) param.get("code_name_kor"))
							.build();
		
		service.insertCodeDetail(codeParam);
		
		CommandMap model = new CommandMap();
		
        model.put("success", true);
        model.put("data", codeParam);
        
        log.info("end");
		
		return model;
    }
    
	@ControlMethod(serviceId = "updateCode")
    public CommandMap updateCode(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
		String code_field = (String) param.get("code_field");
		
		CommandMap model = new CommandMap();
		
		model.put("code_field", code_field);
		
		log.info("end");
		
		return model;
    }
	
	@ControlMethod(serviceId = "deleteCode")
    public CommandMap deleteCode(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
		String code_field = (String) param.get("code_field");
		
		service.deleteCode(code_field);
								
		CommandMap model = new CommandMap();
		
		model.put("code_field", code_field);
		
		log.info("end");
		
		return model;
    }
	
	@ControlMethod(serviceId = "deleteCodeDetail")
    public CommandMap deleteCodeDetail(CommandMap param) throws Exception
    {
		log.info("start:{}", param);
		
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
		
		log.info("end");
		
		return model;
    }
}